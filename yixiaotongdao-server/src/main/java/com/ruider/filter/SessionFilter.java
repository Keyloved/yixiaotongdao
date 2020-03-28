package com.ruider.filter;
import com.ruider.config.webConfig.SpringContextUtil;
import com.ruider.controller.RedisController;
import com.ruider.utils.RedisUtils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class SessionFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    //标示符：表示当前用户未登录(可根据自己项目需要改为json样式)
    String NO_LOGIN = "您还未登录";

    //不需要登录就可以访问的路径(比如:注册登录等)
    String[] includeUrls = new String[]{
            "/userInfo/IfAuthorizationEd",
            "/userInfo/addUserInfoIfNoSaved",
            "/redisC/redisAdd","/redisC/redisGet",
            "/needsManagement/getNeedInfoByNeedTypeId",
            "/needsManagement/getNeedAndJoinUsers",
            "/MessageManage/getNeedMessageDetails",
            "/userInfo/getUserDetails",
            "/needsManagement/getNeedsByUserId",
            "/needsManagement/getKeyWordsContent",
            "/userInfo/weixin/session",
            "/userInfo/weixin/getUserInfoByOpenId",
            "/userInfo/test"
    };


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            //HttpSession session = request.getSession();
            String uri = request.getRequestURI();
            System.out.println("filter url:"+uri);
            logger.info("【url:" + uri + "请求session验证】");
            //是否需要过滤
            boolean needFilter = isNeedFilter(uri);
            filterChain.doFilter(servletRequest, servletResponse);


            if (!needFilter) { //不需要过滤直接传给下一个过滤器
                logger.info("【url:" + uri + "不需要session验证】");
                filterChain.doFilter(servletRequest, servletResponse);
            } else { //需要过滤器
                logger.info("【用户请求 uri：" + uri + "session验证】");

                RedisController redisController = (RedisController)SpringContextUtil.getBean("redisController");
                System.out.println("request.getHeader key:"+request.getHeader("token"));
                System.out.println("getRedis:" + redisController.getRedis(request.getHeader("token")));
                // session中包含user对象,则是登录状态
                if( redisController.getRedis(request.getHeader("token")) != null){
                    //System.out.println("userId:"+session.getAttribute("userId"));
                    logger.info("【用户请求uri：" + uri + "通过验证】");
                    logger.info("【当前用户已登陆，UserId：" + redisController.getRedis(request.getHeader("token")) + "】");
                    filterChain.doFilter(request, response);
                }else{
                    logger.info("【用户请求uri：" + uri + "未通过验证】");
                    logger.info("【用户未登录】");
                    String requestType = request.getHeader("X-Requested-With");
                    //判断是否是ajax请求
                    if(requestType!=null && "XMLHttpRequest".equals(requestType)){
                        response.getWriter().write("../userPage/userPage");
                    }
                    return;
                }
            }

        }
        catch (Exception e) {
            logger.error("【用户请求验证异常，filter failed】",e);
        }
    }

    /**
     * @Author: xxxxx
     * @Description: 是否需要过滤
     * @Date: 2018-03-12 13:20:54
     * @param uri
     */
    public boolean isNeedFilter(String uri) {

        for (String includeUrl : includeUrls) {
            if(includeUrl.equals(uri)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}