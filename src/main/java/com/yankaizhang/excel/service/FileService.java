package com.yankaizhang.excel.service;

import com.yankaizhang.excel.util.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dzzhyk
 */
public interface FileService {

    /**
     * 下载文件
     * @param filename 服务器文件名称
     * @param response 相应
     */
    void downloadFile(String filename, HttpServletResponse response);

    /**
     * 删除文件
     * @param filename 服务器文件名称
     */
    void deleteFile(String filename);

    /**
     * 更新文件状态
     * @param filename 服务器文件名称
     * @param status 更新状态
     */
    void updateFileStatus(String filename, int status);

    /**
     * 根据md5查找是否有文件记录
     */
    boolean exist(String md5);
}
