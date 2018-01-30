package com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableCaching
@EnableScheduling
//@EnableAspectJAutoProxy
@EnableSwagger2
public class ApiApplication {

	public static void main(String[] args) {


        SpringApplication.run(new Object[]{ApiApplication.class}, args);

		
	}
}
