package com.yankaizhang.excel.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.ExcelLine;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.response.LayuiResult;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.service.MongoService;
import com.yankaizhang.excel.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

@Controller
@Scope("prototype")
@Slf4j
public class ExcelController {

    @Autowired
    FileService fileService;

    @Autowired
    MongoService mongoService;

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @PostMapping("/preEx")
    @ResponseBody
    public Result insertExcelFile(@RequestParam("f") String filename) {
        FileInfo fileInfo = fileService.selectBySaveName(filename);
        if (fileInfo == null) {
            return Result.buildError("待导入文件记录不存在：" + filename);
        }

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            return Result.buildError("文件存储路径不存在：" + filename);
        }

        File target = new File(dir.getAbsolutePath() + File.separator + filename);
        if (!target.exists()) {
            return Result.buildError("待导入文件实际不存在：" + filename);
        }

        // 线程池
        Future<Result> resultFuture = ThreadUtil.execAsync(new Callable<Result>() {
            @Override
            public Result call() {
                Boolean result;
                if (filename.endsWith("xls")) {

                    fileService.updateFileStatus(filename, FileStatusConstant.DOING_INSERT);
                    result = mongoService.parseExcel(target, ExcelConstant.XLS);

                } else if (filename.endsWith("xlsx")) {
                    fileService.updateFileStatus(filename, FileStatusConstant.DOING_INSERT);
                    result = mongoService.parseExcel(target, ExcelConstant.XLSX);

                } else {
                    return Result.buildError("不支持的文件类型");
                }

                if (result) {
                    fileService.updateFileStatus(filename, FileStatusConstant.SUCCESS_INSERT);
                    return Result.buildSuccess("文件导入成功：" + filename);
                } else {
                    fileService.updateFileStatus(filename, FileStatusConstant.FAILED_INSERT);
                    log.debug("失败文件: " + filename);
                    return Result.buildError("文件导入失败：" + filename);
                }
            }
        });

        // 等待结果返回，60秒超时
        try {
            return resultFuture.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return Result.buildError("处理操作超时");
        }
    }


    /**
     * Excel预览
     */
    @GetMapping("/preview")
    public String preview(@RequestParam("f") String filename,
                          @RequestParam(value = "sheet", required = false) Integer sheet,
                          Model model) {
        if (null == sheet){
            sheet = 0;
        }
        FileInfo fileInfo = fileService.selectBySaveName(filename);

        if (fileInfo == null){
            return "index";
        }

        String collectionName = FileUtil.getPrefix(filename);
        Long lines = mongoService.getExcelLineCount(sheet, collectionName);
        if (lines == null){
            return "index";
        }

        model.addAttribute("filename", filename);
        model.addAttribute("lines", lines);
        model.addAttribute("collectionName", collectionName);
        model.addAttribute("fileInfo", fileInfo);
        return "show";
    }

    @PostMapping("/getEx/{coll}/{sheet}/{curr}/{size}")
    @ResponseBody
    public Result getLinesByPage(@PathVariable(value = "coll") String collectionName,
                                 @PathVariable(value = "sheet") Integer sheet,
                                 @PathVariable(value = "curr") Integer curr,
                                 @PathVariable(value = "size") Integer size){
        if (sheet == null){
            // 默认读取第一个sheet
            sheet = 0;
        }
        List<ExcelLine> lineList = mongoService.getExcelLinesPage(collectionName, sheet, curr, size);
        if (lineList != null){
            return Result.buildSuccess(lineList);
        }else{
            return Result.buildError("获取分页信息失败");
        }
    }
}