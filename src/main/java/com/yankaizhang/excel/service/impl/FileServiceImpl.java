package com.yankaizhang.excel.service.impl;

import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.mapper.FileInfoMapper;
import com.yankaizhang.excel.service.FileService;
import com.yankaizhang.excel.util.Result;
import com.yankaizhang.excel.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

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

    @Override
    public void downloadFile(String filename, HttpServletResponse response) {

        File dir = new File(uploadPath);
        if (!dir.exists()){
            return;
        }

        File file = new File(dir.getAbsolutePath() + File.separator + filename);

        if (!file.exists()) {
            return;
        }

        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);

        byte[] buffer = new byte[1024];

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             OutputStream os = response.getOutputStream()
        ) {
            // 文件下载
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            os.flush();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void deleteFile(String filename) {
        fileInfoMapper.deleteBySaveName(filename);
    }

    @Override
    public void updateFileStatus(String filename, int status) {

    }

    @Override
    public boolean exist(String md5) {
        FileInfo fileInfo = fileInfoMapper.selectByFileMd5(md5);
        return fileInfo != null;
    }
}
