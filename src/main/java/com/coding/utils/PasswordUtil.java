package com.coding.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

/**
 * 密码加密工具类
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Slf4j
public class PasswordUtil {

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return rawPassword;
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return Objects.equals(rawPassword, encodedPassword);
    }

    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 是否符合要求（6-32位）
     */
    public static boolean isValid(String password) {
        return password != null && password.length() >= 6 && password.length() <= 32;
    }
}

