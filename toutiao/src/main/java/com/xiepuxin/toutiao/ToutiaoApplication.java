package com.xiepuxin.toutiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ToutiaoApplication extends SpringBootServletInitializer /*implements EmbeddedServletContainerCustomizer*/ {

	public static void main(String[] args) {
		SpringApplication.run(ToutiaoApplication.class, args);
	}
//    @Override
//    public void customize(ConfigurableEmbeddedServletContainer container) {
//        container.setPort(8080);
//    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ToutiaoApplication.class);
    }
}
