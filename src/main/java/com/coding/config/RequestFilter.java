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

        if (checkInWhiteList(appProperties.getWhiteList(), uri)) {
            // 如果接口在白名单里
            if (isTokenValid(token)) {
                // 如果有token,将用户信息写入上下文
                boolean writeUserInfo = writeUserInfo(httpServletRequestWrapper, token);
                if (!writeUserInfo) {
                    log.trace("env : {} , uri : {} ,接口在白名单里,上传了token,但是token无效,继续放行", env, uri);
                }
                chain.doFilter(httpServletRequest, response);
            } else {
                // 如果没有token,直接放行
                chain.doFilter(httpServletRequest, response);
                log.trace("env : {} , uri : {} ,接口在白名单里,跳过授权认定", env, uri);
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
            log.info("claims:{}", claims);
            if ("system".equals(claims.get("iss"))) {
                httpServletRequestWrapper.addHeader("userId", userId);
                return true;
            } else {
                httpServletRequestWrapper.addHeader("userId", userId);
                return true;
            }

        } catch (Exception e) {
            log.info("传了token但是token错误,提示需要重新登录", e);
            String env = environment.getProperty("spring.profiles.active");
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
            return false;
        }
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        for (String pattern : whiteList) {
            if (antPathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
