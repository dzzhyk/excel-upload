package com.yankaizhang.excel.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.ExcelLine;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.service.MongoService;
import com.yankaizhang.excel.response.Result;
import com.yankaizhang.excel.vo.ExcelPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

    private static final ExecutorService executor = ThreadUtil.newExecutor(10);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/preEx")
    @ResponseBody
    public Result insertExcelFile(@RequestParam("f") String filename) {
        log.info("收到写入请求 : {}", filename);
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
        Future<Result> resultFuture = executor.submit(new Callable<Result>() {
            @Override
            public Result call() {
                Boolean result;
                log.info("开始写入 : {}", filename);
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

        // 等待结果返回，300秒超时
        try {
            return resultFuture.get(300, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fileService.updateFileStatus(filename, FileStatusConstant.FAILED_INSERT);
            e.printStackTrace();
            return Result.buildError("处理操作超时");
        }
    }


    /**
     * Excel预览
     */
    @GetMapping("/preview")
    public String preview(@RequestParam("f") String filename, Model model) {

        FileInfo fileInfo = fileService.selectBySaveName(filename);

        if (fileInfo == null){
            return "index";
        }

        String collectionName = FileUtil.getPrefix(filename);

        // 获取总的sheet数量
        Integer sheetCount = mongoService.getExcelSheetCount(collectionName);

        model.addAttribute("filename", filename);
        model.addAttribute("sheetCount", sheetCount);
        model.addAttribute("collectionName", collectionName);
        model.addAttribute("fileInfo", fileInfo);

        return "show";
    }


    @PostMapping("/getLines/{coll}/{sheet}")
    @ResponseBody
    public Result getSheetLines(@PathVariable(value = "coll") String collectionName,
                                @PathVariable(value = "sheet") Integer sheet){
        Long lines = mongoService.getExcelLineCountBySheet(collectionName, sheet);
        if (lines != null){
            return Result.buildSuccess("获取成功", lines);
        }else{
            return Result.buildError("获取失败");
        }
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

        if (lineList == null){
            return Result.buildError("获取分页信息失败");
        }

        // 获取该表格页面必要的信息
        ExcelPageVO pageVO = new ExcelPageVO();
        Map<Long, ExcelLine> lineMap = lineList.stream().collect(Collectors.toMap(ExcelLine::getRow, ExcelLine -> ExcelLine));
        pageVO.setLineMap(lineMap);
        pageVO.setCount(lineList.size());
        pageVO.setRowSet(lineMap.keySet());
//        log.info("Excel响应体 : {}", pageVO);
        return Result.buildSuccess("查询成功", pageVO);
    }
}