package com.yankaizhang.excel.vo;

import com.yankaizhang.excel.constant.FileStatusConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 文件信息视图对象
 * @author dzzhyk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVO {

    private String trueFileName;
    private String serverFileName;
    private long fileSize;
    private LocalDateTime created;

    /**
     * 文件处理状态
     */
    private int status = FileStatusConstant.WAITING_INSERT;

    public FileInfoVO(String trueFileName, String serverFileName, long fileSize, LocalDateTime created) {
        this.trueFileName = trueFileName;
        this.serverFileName = serverFileName;
        this.fileSize = fileSize;
        this.created = created;
    }
}
