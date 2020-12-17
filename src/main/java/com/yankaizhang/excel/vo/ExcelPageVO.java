package com.yankaizhang.excel.vo;

import com.yankaizhang.excel.entity.ExcelLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

/**
 * Excel分页视图类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcelPageVO {

    private Map<Long, ExcelLine> lineMap;
    private Set<Long> rowSet;
    private Integer count;

}
