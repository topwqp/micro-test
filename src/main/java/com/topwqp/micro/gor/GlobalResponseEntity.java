package com.topwqp.micro.gor;

/**
 * @author wangqiupeng
 * @date 2019年12月13日14:53:35
 * @desc 响应实体
 */
public class GlobalResponseEntity<T> extends BaseResponse {

    private static final long serialVersionUID = 8573928125672925890L;

    private T data;

    public GlobalResponseEntity() {

    }

    public GlobalResponseEntity(int code, String msg, T data) {
        super(code, msg);
        this.data = data;
    }

    /**
     * 响应成功
     *
     * @return
     */
    public static GlobalResponseEntity success() {
        return new GlobalResponseEntity(200, "ok", null);
    }

    /**
     * 响应成功，并返回说明文字以及数据
     *
     * @param msg
     * @param data
     * @return
     */
    public static GlobalResponseEntity success(String msg, Object data) {
        return new GlobalResponseEntity(200, msg, data);
    }

    /**
     * 响应失败
     *
     * @return
     */
    public static GlobalResponseEntity failure(String msg) {
        return failure(500, msg);
    }

    /**
     * 响应失败，并返回说明文字以及数据
     *
     * @param msg
     * @param data
     * @return
     */
    public static GlobalResponseEntity failure(String msg, Object data) {
        return new GlobalResponseEntity(500, msg, data);
    }

    public static GlobalResponseEntity failure(Integer code, String msg) {
        return new GlobalResponseEntity(code, msg, null);
    }

    /**
     * 处理中
     *
     * @param msg
     * @return
     */
    public static GlobalResponseEntity processing(String msg) {
        return failure(500, msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
