package com.shampo.shampogateway;

import com.shampo.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class})
@EnableDubbo
@Service
public class ShampoGatewayApplication {

	@DubboReference
	private DemoService demoService;
	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(ShampoGatewayApplication.class, args);
		ShampoGatewayApplication application = context.getBean(ShampoGatewayApplication.class);
		String result = application.doSayHello("world");
		String result2 = application.doSayHello2("world");
		System.out.println("result: " + result);
		System.out.println("result: " + result2);
	}

	public String doSayHello(String name) {
		return demoService.sayHello(name);
	}

	public String doSayHello2(String name) {
		return demoService.sayHello2(name);
	}




}
