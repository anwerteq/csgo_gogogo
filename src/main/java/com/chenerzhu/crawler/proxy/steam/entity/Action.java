package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

// Inner class for Action
    @Entity
    @Data
    public  class Action {

        @javax.persistence.Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;  // 操作的ID，用于唯一标识该操作

        private String link;  // 操作的链接，通常是一个Steam协议链接

        private String name;  // 操作的名称，例如“在游戏中检视”


    }