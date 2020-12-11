package com.yankaizhang.excel.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 文件信息对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "file_info")
public class FileInfo {

    @Id
    private Long id;

    @Column(name = "true_name")
    private String trueName;

    @Column(name = "save_name")
    private String saveName;

    @Column
    private Long size;

    /**
     * 文件类型
     */
    @Column
    private String type;

    @Column
    private Date created;

    /**
     * 文件保存路径
     */
    @Column
    private String path;

    /**
     * 文件访问url
     */
    @Column
    private String url;

    /**
     * 文件MD5信息
     */
    @Column(name = "file_md5")
    private String fileMd5;

    /**
     * 文件处理状态
     */
    @Column
    private Integer status;

    /**
     * 删除标志位
     */
    @Column(name = "del_flag")
    private Integer delFlag;
}
