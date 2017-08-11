package com.xiepuxin.toutiao;

import com.xiepuxin.toutiao.model.News;
import com.xiepuxin.toutiao.model.User;
import com.xiepuxin.toutiao.util.JedisAdapter;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ToutiaoApplication.class)
//@Sql("/init-schema.sql")
public class JedisTests {
    private static final Logger logger = LoggerFactory.getLogger(JedisTests.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testObject(){
        User user = new User();
        user.setHeadUrl(ToutiaoUtil.ALIYUN_DOMAIN_PREFIX+"images/1.jpg");
        user.setName("user1");
        user.setPassword("pwd");
        user.setSalt("salt");

        jedisAdapter.setObject("user1xx",user);

        User user1 = jedisAdapter.getObject("user1xx",User.class);

        logger.info("xiepuxin , " + user1.toString().equals(user.toString()));
    }
}
