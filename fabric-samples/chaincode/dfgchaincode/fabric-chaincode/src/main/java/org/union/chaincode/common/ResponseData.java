package org.union.chaincode.common;

/**
 * @description
 * @date
 */
public class ResponseData<T> {

    private Boolean success;

    private String message;

    private T data;

    public static <T> ResponseData<T> ok(T data) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setData(data);
        return responseData;
    }

    public static <T> ResponseData<T> fail(String message) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setSuccess(false);
        responseData.setMessage(message);
        return responseData;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
