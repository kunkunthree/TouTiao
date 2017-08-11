package com.xiepuxin.toutiao;

import com.xiepuxin.toutiao.dao.UserDAO;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.service.ToutiaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class ToutiaoApplicationTests {

	@Autowired
	private UserDAO userDAO;

	@Test
	public void contextLoads() {
		Random rand = new Random();
		for(int i = 0 ; i < 10 ; i++){
			User user = new User();
			user.setHeadUrl("Http://images.xiepuxin.com/head/" + rand.nextInt(1000) + "t.png");
			user.setName("USER"+i);
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);
		}
	}

}
