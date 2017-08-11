package com.xiepuxin.toutiao.model;

import com.alibaba.fastjson.JSON;
import com.xiepuxin.toutiao.util.ToutiaoUtil;

public class User {
    private int id;
    private String name;
    private String password;
    private String salt;
    private String headUrl;

    public User(){}

    public User(String name){
        this.name = name;
        this.password = "";
        this.salt = "";
        this.headUrl = "";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getId() {

        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
