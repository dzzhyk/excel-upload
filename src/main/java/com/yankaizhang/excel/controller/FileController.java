package com.yankaizhang.excel.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.response.LayuiResult;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.util.FileUploadUtil;
import com.yankaizhang.excel.response.Result;
import com.yankaizhang.excel.vo.FileInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 文件上传controller
 * @author dzzhyk
 */
@Controller
@Scope("prototype")
@Slf4j
public class FileController {

    @Autowired
    FileService fileService;

    @RequestMapping({"/index", "/"})
    public String index(){
        return "index";
    }

    @RequestMapping("/uploadlist")
    public String uploadList(){
        return "uploadlist";
    }

    @PostMapping("/list")
    @ResponseBody
    public LayuiResult<FileInfoVO> list(
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "limit", defaultValue = "20") Long limit
    ){
        Page<FileInfoVO> voPage = new Page<>();
        voPage.setCurrent(page);
        voPage.setSize(limit);
        IPage<FileInfoVO> fileInfoVOIPage = fileService.selectFileInfoPage(voPage);
        List<FileInfoVO> pageRecords = fileInfoVOIPage.getRecords();
        Long count = fileService.selectCount();
        if (null != pageRecords){
            return LayuiResult.success(count, pageRecords);
        }
        return LayuiResult.failed("获取服务器文件信息失败");
    }


    /**
     * 分块文件上传
     */
    @RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("fileMd5") String fileMd5,
                         @RequestParam("chunk") Integer chunk){

        boolean result =
                FileUploadUtil.uploadChunk(file, fileService.getChunkPath(), fileMd5, chunk);
        if (result){
            return Result.buildSuccess("分块文件上传成功");
        }
        return Result.buildError("分块文件上传失败");
    }

    /**
     * 检查文件是否存在
     */
    @RequestMapping(value = "/checkFile", method = RequestMethod.POST)
    @ResponseBody
    public Result checkFile(@RequestParam("fileMd5") String fileMd5){
        FileInfo fileInfo = fileService.selectByMd5(fileMd5);
        if (null != fileInfo){
            // 如果文件存在，把删除位改为-1
            fileService.updateFileDelFlag(fileInfo.getSaveName(), -1);
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
    public Result checkChunk(@RequestParam("fileMd5") String fileMd5,
                             @RequestParam("chunk") Integer chunk,
                             @RequestParam("fileSize") Long fileSize){

        String chunkFilePath = FileUploadUtil.genChunkFilePath(fileService.getChunkPath(), fileMd5);

        // 尝试获取块文件
        File chunkFile = new File(chunkFilePath + chunk);

        if (chunkFile.exists() && fileSize != null && FileUtil.size(chunkFile) == fileSize){
            return Result.buildSuccess("分块文件校验成功");
        }else{
            // 如果校验不通过就删除分块
            if (chunkFile.exists()){
                FileUtil.del(chunkFile);
            }
            return Result.buildError("分块文件校验失败");
        }

    }

    /**
     * 合并分块
     */
    @RequestMapping(value = "/mergeChunk", method = RequestMethod.POST)
    @ResponseBody
    public Result mergeChunk(@RequestParam("fileMd5") String fileMd5,
                             @RequestParam("fileName") String fileName,
                             @RequestParam("fileSize") Long fileSize){

        // 获取分块文件目录
        String chunkFileFolderPath = FileUploadUtil.genChunkFilePath(fileService.getChunkPath(), fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);

        // 获取分块文件列表
        File[] files = chunkFileFolder.listFiles();
        if (files == null){
            return Result.buildError("分块文件不存在");
        }
        List<File> fileList = Arrays.asList(files);

        // 创建目标合并文件
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        String saveName = IdUtil.simpleUUID() + "." + fileType;
        String filePath = fileService.getUploadPath() + File.separator + saveName;
        File mergeFile = new File(filePath);

        //执行合并
        File dir = new File(fileService.getUploadPath());
        if (!dir.exists()){
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs){
                return Result.buildError("创建文件夹失败");
            }
        }

        mergeFile = FileUploadUtil.mergeFile(fileList, mergeFile);

        if(mergeFile == null){
            //合并文件失败
            return Result.buildError("合并文件失败");
        }

        // 校验文件的md5值是否和前端传入的md5一致
        boolean checkFileMd5 = FileUploadUtil.checkFileMd5(mergeFile, fileName, fileMd5);

        if(!checkFileMd5){
            //校验文件失败
            return Result.buildError("合并后文件校验MD5失败");
        }

        // 保存文件上传信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setTrueName(fileName);
        fileInfo.setSaveName(saveName);
        fileInfo.setFileMd5(fileMd5);
        fileInfo.setDelFlag(-1);
        fileInfo.setStatus(-1);
        fileInfo.setPath(mergeFile.getAbsolutePath());
        fileInfo.setUrl(filePath);
        fileInfo.setSize(fileSize);
        fileInfo.setType(fileType);
        fileInfo.setCreated(new Date());
        fileService.insertFile(fileInfo);

        return Result.buildSuccess("合并文件成功", saveName);
    }

    @GetMapping("/download")
    @ResponseBody
    public void download(
            HttpServletResponse response,
            @RequestParam("f") String filename){
        fileService.downloadFile(filename, response);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestParam("f") String filename){
        Integer integer = fileService.updateFileDelFlag(filename, 1);
        if (integer > 0){
            return Result.buildSuccess("删除成功");
        }
        return Result.buildError("删除失败");
    }
}
