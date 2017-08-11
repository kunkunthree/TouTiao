package com.xiepuxin.toutiao.util;

import com.alibaba.fastjson.JSON;
import com.xiepuxin.toutiao.service.AliyunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    public static void print(int index,Object object){
        System.out.println(String.format("%d,%s",index,object.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.setex("hello",15,"world");

        jedis.set("pv","100");
        jedis.incr("pv");
        print(2,jedis.get("pv"));
        jedis.incrBy("pv",5);
        print(2,jedis.get("pv"));

        //列表操作
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName,"a"+i);
        }
        print(3,jedis.lrange(listName,0,12));
        print(4,jedis.llen(listName));
        print(5,jedis.lpop(listName));
        print(6,jedis.llen(listName));
        print(7,jedis.lindex(listName,3));
        print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));
        print(9,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","yy"));
        print(10,jedis.lrange(listName,0,12));


        String userKey = "userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","123");

        print(11,jedis.hget(userKey,"name"));
        print(12,jedis.hgetAll(userKey));
        print(13,jedis.hkeys(userKey));
        print(14,jedis.hvals(userKey));
        print(15,jedis.hexists(userKey,"email"));
        print(16,jedis.hexists(userKey,"name"));
        jedis.hsetnx(userKey,"school","zju");
        jedis.hsetnx(userKey,"name","xyx");
        print(17,jedis.hgetAll(userKey));

        //集合set
        String likeKey1 = "newsLike1";
        String likeKey2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1,i+"");
            jedis.sadd(likeKey2,i*2+"");
        }
        print(18,jedis.smembers(likeKey1));
        print(19,jedis.smembers(likeKey2));
        print(20,jedis.sinter(likeKey1,likeKey2));
        print(21,jedis.sunion(likeKey1,likeKey2));
        print(22,jedis.sdiff(likeKey1,likeKey2));
        print(23,jedis.sismember(likeKey1,"5"));
        jedis.srem(likeKey1,"5");
        print(24,jedis.sismember(likeKey1,"5"));
        print(25,jedis.scard(likeKey1));
        jedis.smove(likeKey2,likeKey1,"14");
        print(26,jedis.smembers(likeKey1));
        print(27,jedis.smembers(likeKey2));

        //
        String rankKey = "rankKey";
        jedis.zadd(rankKey,200,"jim");
        jedis.zadd(rankKey,151,"ben");
        jedis.zadd(rankKey,141,"lee");
        jedis.zadd(rankKey,235,"tom");
        jedis.zadd(rankKey,32,"sam");
        print(28,jedis.zcard(rankKey));
        print(29,jedis.zcount(rankKey,61,200));
        print(30,jedis.zscore(rankKey,"lee"));
        jedis.zincrby(rankKey,2,"lee");
        print(31,jedis.zscore(rankKey,"lee"));
        jedis.zincrby(rankKey,2,"sola");
        print(32,jedis.zscore(rankKey,"sola"));
        print(33,jedis.zcount(rankKey,0,100));
        print(34,jedis.zrange(rankKey,1,3));
        print(35,jedis.zrevrange(rankKey,1,3));

        for (Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,0,200)) {
            print(36,tuple.getElement()+" : " +tuple.getScore());
        }

        print(37,jedis.zrank(rankKey,"tom"));
        print(38,jedis.zrevrank(rankKey,"tom"));

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL"+i);
            j.close();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("jedis.get发生异常:"+e.getMessage());
            return null;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.set(key,value);
        }catch (Exception e){
            logger.error("jedis.set发生异常:"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("jedis.sadd发生异常:"+e.getMessage());
            return 0;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("jedis.srem发生异常:"+e.getMessage());
            return 0;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismenber(String key,String member){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,member);
        }catch (Exception e){
            logger.error("jedis.sismenber发生异常:"+e.getMessage());
            return false;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("jedis.scard发生异常:"+e.getMessage());
            return 0;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void setex(String key,int seconds,String value){
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.setex(key,seconds,value);
        }catch (Exception e){
            logger.error("jedis.setex发生异常:"+e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("jedis.lpush发生异常:"+e.getMessage());
            return 0;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeOut,String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeOut,key);
        }catch (Exception e){
            logger.error("jedis.brpop发生异常:"+e.getMessage());
            return null;
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object object){
        set(key, JSON.toJSONString(object));
    }

    public <T> T getObject(String key, Class<T> clazz){
        String value = get(key);
        return value == null ? null : JSON.parseObject(value,clazz);
    }


}
