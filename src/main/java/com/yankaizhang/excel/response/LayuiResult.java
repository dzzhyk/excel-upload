package com.yankaizhang.excel.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * layui-table响应体
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LayuiResult <T> {

    private static final int SUCCESS = 0;
    private static final int FAILED = -1;

    private int code;
    private String msg;
    private long count;
    private List<T> data;

    public static <T> LayuiResult<T>  success(long count, List<T> data){
        return new LayuiResult<T>(SUCCESS, "", count, data);
    }

    public static <T> LayuiResult <T> failed(String msg){
        return new LayuiResult<T>(FAILED, msg, 0, null);
    }

}