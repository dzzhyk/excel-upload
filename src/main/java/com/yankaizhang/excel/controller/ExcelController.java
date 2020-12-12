package com.yankaizhang.excel.controller;

import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.service.MongoService;
import com.yankaizhang.excel.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Controller
//@Scope("prototype")
@Slf4j
public class ExcelController {

    @Autowired
    FileService fileService;

    @Autowired
    MongoService mongoService;

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @RequestMapping("/preEx")
    @ResponseBody
    public synchronized Result insertExcelFile(@RequestParam("f") String filename){
        FileInfo fileInfo = fileService.selectBySaveName(filename);
        if (fileInfo == null){
            return Result.buildError("待导入文件记录不存在：" + filename);
        }

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            return Result.buildError("文件存储路径不存在：" + filename);
        }

        File target = new File(dir.getAbsolutePath() + File.separator + filename);
        if (!target.exists()){
            return Result.buildError("待导入文件实际不存在：" + filename);
        }

        Boolean result;

        if (filename.endsWith("xls")){

            fileService.updateFileStatus(filename, FileStatusConstant.DOING_INSERT);
            result = mongoService.parseExcel(target, ExcelConstant.XLS);

        }else if (filename.endsWith("xlsx")){
            fileService.updateFileStatus(filename, FileStatusConstant.DOING_INSERT);
            result = mongoService.parseExcel(target, ExcelConstant.XLSX);

        }else{
            return Result.buildError("不支持的文件类型");
        }

        if (result){
            fileService.updateFileStatus(filename, FileStatusConstant.SUCCESS_INSERT);
            return Result.buildSuccess("文件导入成功：" + filename);
        }else {
            fileService.updateFileStatus(filename, FileStatusConstant.FAILED_INSERT);
            log.debug("失败文件: " + filename);
            return Result.buildError("文件导入失败：" + filename);
        }

    }


}
