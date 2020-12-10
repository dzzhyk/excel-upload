package com.yankaizhang.excel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件视图类
 * @author dzzhyk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {

    private String trueFileName;
    private String serverFileName;
    private int fileSize;
    private LocalDateTime created;

}
