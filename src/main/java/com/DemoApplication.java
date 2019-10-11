package com;

import com.yangframe.config.applicationEvent.ApplicationListenerStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//DruidDataSourceAutoConfigure.class,
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
		application.addListeners(new ApplicationListenerStart());
		application.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		SpringApplicationBuilder builder1 = builder.sources(DemoApplication.class);
		SpringApplication application = builder1.application();
		application.addListeners(new ApplicationListenerStart());
		return builder1;
	}
}
