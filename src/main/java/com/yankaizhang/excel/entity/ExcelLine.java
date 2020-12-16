package com.yankaizhang.excel.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.util.List;

/**
 * Excel文件行对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcelLine {

    @JSONField(name = "sheet")
    private int sheet;

    @JSONField(name = "row")
    private long row;

    @JSONField(name = "rowList")
    private List<Object> rowList;

}
