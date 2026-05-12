package com.echt.task_management_system.common.web;

import com.echt.task_management_system.common.response.ApiErrorResponse;
import com.echt.task_management_system.common.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.echt.task_management_system.controller")
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {

        // Prevent double wrapping
        if (body instanceof ApiResponse<?>
                || body instanceof ApiErrorResponse
                || body instanceof com.echt.task_management_system.dto.response.ApiResponse<?>) {
            return body;
        }

        int status = response instanceof ServletServerHttpResponse servletResponse
                ? servletResponse.getServletResponse().getStatus()
                : HttpStatus.OK.value();

        return ApiResponse.success(
                status,
                successMessage(request.getMethod()),
                body
        );
    }

    private String successMessage(HttpMethod method) {

        if (HttpMethod.POST.equals(method)) {
            return "Request created successfully";
        }

        if (HttpMethod.PUT.equals(method)
                || HttpMethod.PATCH.equals(method)) {

            return "Request updated successfully";
        }

        if (HttpMethod.DELETE.equals(method)) {
            return "Request deleted successfully";
        }

        return "Request fetched successfully";
    }
}