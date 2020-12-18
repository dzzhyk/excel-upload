package com.yankaizhang.excel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yankaizhang.excel.entity.FileInfo;
import com.yankaizhang.excel.vo.FileInfoVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dzzhyk
 */
public interface FileService {

    /**
     * 加入文件记录
     */
    Integer insertFile(FileInfo fileInfo);

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
    Integer updateFileDelFlag(String filename, int flag);

    /**
     * 更新文件状态
     * @param filename 服务器文件名称
     * @param status 更新状态
     */
    Integer updateFileStatus(String filename, int status);

    String getUploadPath();

    String getChunkPath();

    IPage<FileInfoVO> selectFileInfoPage(Page<FileInfoVO> page);

    FileInfo selectBySaveName(String filename);

    FileInfo selectByMd5(String fileMd5);

    Long selectCount();

}
