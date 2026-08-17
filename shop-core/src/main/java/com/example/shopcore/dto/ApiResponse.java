package com.example.shopcore.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
    boolean success;
    String message;
    T data;



    public ApiResponse<T> success(T data){
        return new ApiResponse<T>(true,"OK",data);
    }
    public static ApiResponse<?> error(String message){
        return new ApiResponse<>(false,message,null);
    }
}
