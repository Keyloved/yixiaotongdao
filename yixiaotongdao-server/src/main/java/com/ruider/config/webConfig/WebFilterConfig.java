package com.ruider.config.webConfig;

import com.ruider.filter.SessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterConfig {
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        //新建过滤器注册类
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 添加我们写好的过滤器
        registration.setFilter( new SessionFilter());
        // 设置过滤器的URL模式
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * session时长设置
     * @return
     */
    /*
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer Container) {

                Container.setSessionTimeout(12*60*60);//单位为S
            }
        };
    }
    */
}