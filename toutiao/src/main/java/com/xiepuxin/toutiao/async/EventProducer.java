package com.xiepuxin.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import com.xiepuxin.toutiao.util.JedisAdapter;
import com.xiepuxin.toutiao.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            logger.info("产生一个事件，事件类型是："+model.getEventType());
            String json = JSONObject.toJSONString(model);
            String key = RedisKeyUtil.getEventQueueKey();
            long length = jedisAdapter.lpush(key, json);
            logger.info("lpush after length : " + length);
            return true;
        }catch(Exception e){
            logger.error("产生事件异常："+e.getMessage());
            return false;
        }
    }



}
