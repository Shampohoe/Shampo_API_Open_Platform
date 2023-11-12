package com.shampo.shampo_interface;


import com.shampo.shampoclisdk.client.ShampoClient;
import com.shampo.shampoclisdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ShampoInterfaceApplicationTests {

	@Resource
	private ShampoClient shampoClient;

	@Test
	void contextLoads() {
		/*String result1=shampoClient.getNameByGet("shampo");
		User user=new User();
		user.setUsername("kkli");
		String result3=shampoClient.getUsernameByPost(user);
		System.out.println(result1);
		System.out.println(result3);*/
	}

}
