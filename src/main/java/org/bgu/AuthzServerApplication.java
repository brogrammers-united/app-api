package org.bgu;

import org.bgu.config.annotation.TheAppStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@TheAppStarter(isGateway = true)
@SpringBootApplication
@EnableEurekaClient
public class AuthzServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthzServerApplication.class, args);
	}

}
