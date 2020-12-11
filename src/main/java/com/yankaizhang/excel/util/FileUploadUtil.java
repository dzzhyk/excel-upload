package com.yankaizhang.excel.util;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    /**
     * 上传单个文件
     */
    public static FileInfo uploadSingleFile(MultipartFile file, HttpServletRequest request, String uploadUrl) throws Exception {

        // 获取绝对路径
        String path = request.getSession().getServletContext().getRealPath(uploadUrl);
        File dir = new File(path);
        if(!dir.exists()){
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs){
                throw new Exception("创建存放文件夹失败");
            }
        }
        //原始文件名
        String trueName = file.getOriginalFilename();
        //文件类型
        String fileType = getFileExt(trueName);

        if(StringUtils.isBlank(fileType)){
            throw new Exception("文件后缀不能为空！");
        }

        String fileName = UUID.randomUUID().toString();
        String filePath = path + File.separator + fileName + "." + fileType;
        File newFile = new File(filePath);
        file.transferTo(newFile);


        FileInfo fileInfo = new FileInfo();

        fileInfo.setTrueName(trueName);
        fileInfo.setSaveName(fileName);
        fileInfo.setPath(filePath);
        fileInfo.setSize(file.getSize());
        fileInfo.setType(fileType);
        fileInfo.setCreated(new Date());
        fileInfo.setUrl(uploadUrl + "/" + fileName + "." + fileType);
        fileInfo.setStatus(FileStatusConstant.WAITING_INSERT);
        fileInfo.setDelFlag(-1);

        // 获取文件MD5
        String md5 = SecureUtil.md5(newFile);
        fileInfo.setFileMd5(md5);

        return  fileInfo;
    }

    private static String getFileExt(String trueName) {
        if(StringUtils.isBlank(trueName)){
            return "";
        }
        return trueName.substring(trueName.lastIndexOf(".") + 1).toLowerCase().trim();
    }

    /**
     * 上传分片
     */
    public static FileInfo uploadChunk(MultipartFile file, HttpServletRequest request, String uploadUrl, String fileMd5, int chunk){
        return new FileInfo();
    }

    /**
     * 合并分片
     */
    public static FileInfo mergeChunk(){
        return new FileInfo();
    }

}
