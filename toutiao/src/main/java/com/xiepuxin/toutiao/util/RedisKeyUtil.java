package com.xiepuxin.toutiao.util;

public class RedisKeyUtil {
    public static final String SPLIT = ":";
    public static final String BIZ_LIKE = "LIKE";
    public static final String BIZ_DISLIKE = "DISLIKE";
    public static final String BIZ_EVENT = "EVENT";

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDislikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }
}
