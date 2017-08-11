package com.xiepuxin.toutiao.service;

import com.xiepuxin.toutiao.util.JedisAdapter;
import com.xiepuxin.toutiao.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 如果喜欢就返回1，不喜欢就返回-1，否则返回0
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        if(jedisAdapter.sismenber(likeKey,userId+"")){
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        if(jedisAdapter.sismenber(dislikeKey,userId+"")){
            return -1;
        }
        return 0;
    }

    public long like(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,userId+"");
        logger.info("like userId: " + userId + " , likeKey: " + likeKey);

        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.srem(dislikeKey,userId+"");

        return jedisAdapter.scard(likeKey);
    }

    public long dislike(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,userId+"");

        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.sadd(dislikeKey,userId+"");

        logger.info("dislike userId: " + userId + " , dislikeKey: " + dislikeKey);

        return jedisAdapter.scard(dislikeKey);
    }



}
