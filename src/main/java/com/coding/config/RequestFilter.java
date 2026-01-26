package com.coding.config;

import com.coding.http.HeaderRequestWrapper;
import com.coding.starter.jwt.config.IJwtService;
import com.coding.utils.HttpKit;
import com.coding.utils.R;
import com.coding.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestFilter implements Filter {

    private final Environment environment;

    private final IJwtService jwtService;

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final AppProperties appProperties;

    public static final String TOKEN_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String env = environment.getProperty("spring.profiles.active");

        // 处理CORS预检请求（OPTIONS请求）
        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String uri = httpServletRequest.getRequestURI();
        String token = httpServletRequest.getHeader(TOKEN_HEADER);
        String contentType = request.getContentType();
        log.trace("content-type : {}", contentType);
        if (StringUtils.isNotBlank(contentType) && contentType.contains("multipart/form-data")) {
            chain.doFilter(request, response);
            return;
        }
        HeaderRequestWrapper httpServletRequestWrapper = new HeaderRequestWrapper(httpServletRequest);
        log.trace("local env : {} , uri : {} ,token : {}", env, uri, token);

        boolean inWhiteList = checkInWhiteList(appProperties.getWhiteList(), uri);
        log.info("请求过滤 - uri: {}, inWhiteList: {}, hasToken: {}", uri, inWhiteList, StringUtils.isNotBlank(token));
        
        if (inWhiteList) {
            // 如果接口在白名单里
            if (isTokenValid(token)) {
                // 如果有token,将用户信息写入上下文
                boolean writeUserInfo = writeUserInfo(httpServletRequestWrapper, token);
                if (!writeUserInfo) {
                    log.info("env : {} , uri : {} ,接口在白名单里,上传了token,但是token无效,继续放行", env, uri);
                    chain.doFilter(httpServletRequest, response);
                } else {
                    // 如果成功写入用户信息，使用 wrapper 传递
                    log.info("env : {} , uri : {} ,接口在白名单里,已写入用户信息,使用wrapper", env, uri);
                    chain.doFilter(httpServletRequestWrapper, response);
                }
            } else {
                // 如果没有token,直接放行
                log.info("env : {} , uri : {} ,接口在白名单里,无token,直接放行", env, uri);
                chain.doFilter(httpServletRequest, response);
            }
        } else {
            // 如果接口不再白名单里,
            if (!isTokenValid(token)) {
                // 如果没登录,返回需要登录
                log.trace("env : {} , uri : {} ,接口不在白名单里,未登录,拒绝放行", env, uri);
                ResponseUtil.write(response, R.createByNeedLogin());
                return;
            }
            // 如果已经登录,将用户信息写入上下文
            boolean writeUserInfo = writeUserInfo(httpServletRequestWrapper, token);
            if (!writeUserInfo) {
                ResponseUtil.write(response, R.createByNeedLogin());
                log.trace("env : {} , uri : {} ,接口不在白名单里,上传了token,但是token无效,拒绝放行", env, uri);
                return;
            }
            log.trace("env : {} , uri : {} ,接口不在白名单里,授权成功,放行", env, uri);
            chain.doFilter(httpServletRequestWrapper, response);
        }
        long end = System.currentTimeMillis();
        log.trace("useTime: {} ms {}", (end - start), ((HttpServletRequest) request).getRequestURI());
    }

    /**
     * 将用户信息写入上下文
     *
     * @param httpServletRequestWrapper
     * @param token
     * @return 是否成功
     */
    private boolean writeUserInfo(HeaderRequestWrapper httpServletRequestWrapper, String token) {
        try {
            String userId = jwtService.getSub(token);
            Map<String, Object> claims = jwtService.getClaims(token);
            log.info("写入用户信息 - userId: {}, claims: {}", userId, claims);
            if (userId != null && !userId.isEmpty()) {
                httpServletRequestWrapper.addHeader("userId", userId);
                log.info("成功写入用户ID到请求头 - userId: {}", userId);
                return true;
            } else {
                log.warn("userId为空，无法写入请求头");
                return false;
            }
        } catch (Exception e) {
            log.warn("传了token但是token错误,提示需要重新登录", e);
            return false;
        }
    }

    private boolean isTokenValid(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        try {
            log.info("token: [{}]", token);
            Map<String, Object> claims = jwtService.getClaims(token);
            if (claims == null) {
                return false;
            }
            Object iss = claims.get("iss");
            return iss != null;
        } catch (Exception e) {
            log.warn("token不合法,不是有效token", e);
            return false;
        }
    }

    private boolean checkInWhiteList(Set<String> whiteList, String uri) {
        if (CollectionUtils.isEmpty(whiteList)) {
            log.debug("白名单为空");
            return false;
        }
        if (StringUtils.isBlank(uri)) {
            log.debug("URI为空");
            return false;
        }
        for (String pattern : whiteList) {
            boolean matched = antPathMatcher.match(pattern, uri);
            if (matched) {
                log.debug("URI匹配白名单模式 - uri: {}, pattern: {}", uri, pattern);
                return true;
            }
        }
        log.debug("URI不在白名单中 - uri: {}", uri);
        return false;
    }
}
