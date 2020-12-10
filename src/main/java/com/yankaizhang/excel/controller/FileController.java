package com.yankaizhang.excel.controller;

import com.yankaizhang.excel.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传controller
 * @author dzzhyk
 */
@Controller
public class FileController {

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @PostMapping("/upload")
    @ResponseBody
    public Result upload(MultipartFile file){

        String trueName = file.getOriginalFilename();
        if (null == trueName){
            return Result.buildError("文件名为空");
        }

        // 尝试创建保存路径
        File dir = new File(uploadPath);
        if (!dir.exists()){
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs){
                return Result.buildError("创建文件目录失败");
            }
        }

        String saveName = UUID.randomUUID().toString();
        String saveSuffix = trueName.substring(trueName.lastIndexOf("."));
        File newFile = new File(dir.getAbsolutePath() + File.separator + saveName + saveSuffix);

        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.buildError("转储文件失败");
        }

        return Result.buildSuccess("提交上传请求完成");
    }

}
