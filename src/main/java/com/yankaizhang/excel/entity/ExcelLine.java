package com.yankaizhang.excel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Excel文件行对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcelLine {

    private int sheet;
    private long row;
    private List<Object> rowList;

}
