package com.topwqp.micro.gor;

import java.io.Serializable;

/**
 * @author wangqiupeng
 * @date 2019年12月13日15:10:38
 * @desc 响应实体定义
 */
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 6890358143162623490L;

    private int code;

    private String msg;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean ok() {
        return this.code == 200;
    }

    public boolean processing() {
        return this.code == 500;
    }

    public boolean failed() {
        return !ok() && !processing();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
