package com.yankaizhang.excel.vo;

import com.yankaizhang.excel.constant.FileStatusConstant;
import com.yankaizhang.excel.entity.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 文件信息视图对象
 * @author dzzhyk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVO {

    private String trueName;
    private String saveName;
    private Long fileSize;
    private Date created;
    private String fileMd5;

    /**
     * 文件处理状态
     */
    private int status = FileStatusConstant.WAITING_INSERT;

    /**
     * 从{@link FileInfo}对象创建
     */
    public FileInfoVO(FileInfo fileInfo){
        this.trueName = fileInfo.getTrueName();
        this.saveName = fileInfo.getSaveName();
        this.created = fileInfo.getCreated();
        this.fileSize = fileInfo.getSize();
        this.status = fileInfo.getStatus();
        this.fileMd5 = fileInfo.getFileMd5();
    }
}
