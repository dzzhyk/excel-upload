package com.yankaizhang.excel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChunkInfo {

    private String filename;
    private String fileMd5;
    private Integer chunk;
    private Long chunkSize;

}
