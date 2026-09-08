package com.bank.common.response;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data){
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }

    public T getData(){
        return data;
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true,"Success",data);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false,message,null);
    }


}
