package com.yankaizhang.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.mapper.FileInfoMapper;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.vo.FileInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author dzzhyk
 */
@Service
@Scope("prototype")
public class FileServiceImpl implements FileService {

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @Value(value = "${excel.chunk-path}")
    private String chunkPath;

    @Override
    public Integer insertFile(FileInfo fileInfo) {
        return fileInfoMapper.insert(fileInfo);
    }

    @Override
    public void downloadFile(String filename, HttpServletResponse response) {

        File dir = new File(uploadPath);
        if (!dir.exists()){
            return;
        }

        String filePath = dir.getAbsolutePath() + File.separator + filename;
        Path path = Paths.get(filePath);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);

        try (OutputStream os = response.getOutputStream()) {
            Files.copy(path, os);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Integer deleteFile(String filename) {
        return fileInfoMapper.deleteBySaveName(filename);
    }

    @Override
    public Integer updateFileStatus(String filename, int status) {
        return fileInfoMapper.updateFileStatus(filename, status);
    }

    @Override
    public String getUploadPath() {
        return uploadPath;
    }

    @Override
    public String getChunkPath() {
        return chunkPath;
    }

    @Override
    public IPage<FileInfoVO> selectFileInfoPage(Page<FileInfoVO> page) {
        return fileInfoMapper.selectFileInfoVo(page);
    }

    @Override
    public FileInfo selectBySaveName(String filename) {
        return fileInfoMapper.selectBySaveName(filename);
    }

    @Override
    public FileInfo selectByMd5(String fileMd5) {
        return fileInfoMapper.selectByFileMd5(fileMd5);
    }

    @Override
    public Long selectCount() {
        return Long.valueOf(fileInfoMapper.selectCount(new QueryWrapper<>()));
    }

}
