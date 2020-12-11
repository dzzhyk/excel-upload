package com.yankaizhang.excel.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 分片信息
 */

@Data
@Entity
@Table(name = "chunk_info")
public class ChunkInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "file_md5")
    private String fileMd5;

    @Column
    private int chunks;

    @Column
    private int chunk;

}
