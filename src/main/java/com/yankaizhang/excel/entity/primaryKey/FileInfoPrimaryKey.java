package com.yankaizhang.excel.entity.primaryKey;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileInfoPrimaryKey implements Serializable {

    private Long id;
    private String fileMd5;

}
