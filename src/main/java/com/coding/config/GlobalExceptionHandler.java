package com.coding.config;

import com.coding.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理（统一返回 R 格式，避免前端拿到默认 400/500）
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<String> handleValidateException(Exception e) {
        log.warn("参数校验失败", e);
        return R.createByErrorMessage("参数校验失败，请检查输入");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败", e);
        return R.createByErrorMessage("请求体解析失败，请检查JSON格式");
    }

    @ExceptionHandler(DataAccessException.class)
    public R<String> handleDataAccessException(DataAccessException e) {
        log.error("数据库异常", e);
        return R.createByErrorMessage("数据库异常，请检查数据库连接或先执行 doc/init.sql 初始化表结构");
    }

    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception e) {
        log.error("系统异常", e);
        return R.createByErrorMessage("系统异常，请查看后端日志");
    }
}

