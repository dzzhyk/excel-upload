package com.yankaizhang.excel.constant;

import org.springframework.stereotype.Component;

/**
 * 文件状态常量
 */
@Component
public class FileStatusConstant {

    public static final int WAITING_INSERT = -1;
    public static final int DOING_INSERT = 0;
    public static final int SUCCESS_INSERT = 1;
    public static final int FAILED_INSERT = 2;

}
