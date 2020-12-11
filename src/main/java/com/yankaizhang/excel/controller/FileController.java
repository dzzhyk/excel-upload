package com.yankaizhang.excel.controller;

import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 文件上传controller
 * @author dzzhyk
 */
@Controller
@Scope("prototype")
public class FileController {

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @Autowired
    FileService fileService;

    @RequestMapping({"/index", "/"})
    public String index(Model model){
        return "index";
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Result upload(MultipartFile file){
        System.out.println(file.getOriginalFilename());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查文件是否存在
     */
    @RequestMapping(value = "/checkFile", method = RequestMethod.POST)
    @ResponseBody
    public Result checkFile(@RequestParam("fileMd5") String fileMd5){
        System.out.println("获取到MD5: " + fileMd5);
        boolean exist = fileService.exist(fileMd5);
        System.out.println("文件是否存在：" + exist);
        if (exist){
            return Result.buildSuccess();
        }else{
            return Result.buildError();
        }
    }

    /**
     * 检查分片是否已经存在
     */
    @RequestMapping(value = "/checkChunk", method = RequestMethod.POST)
    @ResponseBody
    public Result checkChunk(){
        return null;
    }

    /**
     * 合并分片
     */
    @RequestMapping(value = "/mergeChunk", method = RequestMethod.POST)
    @ResponseBody
    public Result mergeChunk(){
        return null;
    }


    @RequestMapping("/download")
    @ResponseBody
    public void download(
            HttpServletResponse response,
            @RequestParam("f") String filename){
        fileService.downloadFile(filename, response);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public void delete(@RequestParam("f") String filename){
        fileService.deleteFile(filename);
    }
}
