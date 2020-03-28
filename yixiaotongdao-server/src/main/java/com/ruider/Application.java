package com.ruider;

import com.ruider.config.webConfig.SpringContextUtil;
import com.ruider.controller.NeedsManagementController;
import com.ruider.controller.UserController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan("com.ruider.mapper")
@ComponentScan("com.ruider.service")
@ComponentScan(basePackageClasses = UserController.class)
@ComponentScan(basePackageClasses = NeedsManagementController.class)
@ServletComponentScan
@Configuration
@EnableCaching
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		SpringContextUtil.setApplicationContext(context);
	}



}
