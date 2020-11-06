package com.github.zuihou.xss.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.zuihou.xss.wrapper.XssRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 跨站工具 过滤器
 *
 * @author zuihou
 * @date 2019-06-28 17:05
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 可放行的请求路径
     */

    public static final String IGNORE_PATH = "ignorePath";
    /**
     * 可放行的参数值
     */
    public static final String IGNORE_PARAM_VALUE = "ignoreParamValue";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 可放行的请求路径列表
     */
    private List<String> ignorePathList;
    /**
     * 可放行的参数值列表
     */
    private List<String> ignoreParamValueList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("XSS fiter [XSSFilter] init start ...");
        String ignorePaths = filterConfig.getInitParameter(IGNORE_PATH);
        String ignoreParamValues = filterConfig.getInitParameter(IGNORE_PARAM_VALUE);
        ignorePathList = StrUtil.split(ignorePaths, ',');
        ignoreParamValueList = StrUtil.split(ignoreParamValues, ',');
        log.debug("XSS fiter [XSSFilter] init end");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 判断uri是否包含项目名称
        String uriPath = ((HttpServletRequest) request).getRequestURI();
        if (isIgnorePath(uriPath)) {
            log.debug("ignore xssfilter,path[" + uriPath + "] pass through XssFilter, go ahead...");
            chain.doFilter(request, response);
            return;
        } else {
            log.debug("has xssfiter path[" + uriPath + "] need XssFilter, go to XssRequestWrapper");
            chain.doFilter(new XssRequestWrapper((HttpServletRequest) request, ignoreParamValueList), response);
        }
    }

    @Override
    public void destroy() {
        log.debug("XSS fiter [XSSFilter] destroy");
    }

    private boolean isIgnorePath(String uriPath) {
        if (StrUtil.isBlank(uriPath)) {
            return true;
        }
        if (CollectionUtil.isEmpty(ignorePathList)) {
            return false;
        } else {
            return ignorePathList.stream().anyMatch((url) -> uriPath.startsWith(url) || ANT_PATH_MATCHER.match(url, uriPath));
        }
    }
}
