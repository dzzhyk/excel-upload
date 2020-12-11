package com.yankaizhang.excel.util;

import java.io.Serializable;

public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_CODE = 1;
    private static final Integer FAILED_CODE = -1;

    // 响应码
    private Integer code;
    // 数据
    private Object data;
    // 描述
    private String msg;

    public Result() {
    }

    public Result(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    // 成功
    public static Result buildSuccess() {
        return new Result(SUCCESS_CODE, null, null);
    }

    // 成功，传入数据
    public static Result buildSuccess(Object data) {
        return new Result(SUCCESS_CODE, data, null);
    }

    // 成功，传入描述
    public static Result buildSuccess(String msg) {
        return new Result(SUCCESS_CODE, null, msg);
    }

    // 成功，传入描述信息，数据
    public static Result buildSuccess(String msg, Object data) {
        return new Result(SUCCESS_CODE, data, msg);
    }

    // 失败
    public static Result buildError() {
        return new Result(FAILED_CODE, null, null);
    }

    // 失败，传入描述信息
    public static Result buildError(String msg) {
        return new Result(FAILED_CODE, null, msg);
    }

    // 失败，传入描述信息，数据
    public static Result buildError(String msg, Object data) {
        return new Result(FAILED_CODE, data, msg);
    }

    // 根据返回码判断是否成功，返回json
    // 多用于dao操作返回值
    public static Result judge(int code, int target, String succMsg, String failMsg){
        Result result = null;
        if (code==target){
            result = Result.buildSuccess(succMsg);
        }else{
            result = Result.buildError(failMsg);
        }
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}