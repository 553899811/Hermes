package com.newbiegroup.hermes.example.service.impl;

import com.newbiegroup.hermes.example.service.HelloService;

/**
 * <p>Description: </p>
 * @version 1.0.0
 * @date 2020/10/11 17:05
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "I love you,JDK!";
    }

    @Override
    public String test() {
        return "I love you,test!";
    }
}
