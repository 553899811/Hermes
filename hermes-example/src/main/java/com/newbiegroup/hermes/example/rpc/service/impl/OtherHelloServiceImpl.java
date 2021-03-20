package com.newbiegroup.hermes.example.rpc.service.impl;

import com.newbiegroup.hermes.example.rpc.service.HelloService;

/**
 * <p>Description: </p>
 * <p>Company: https://www.yuque.com/newbiegroup</p>
 *
 * @author newbiegroup
 * @version 1.0.0
 * @date 2021/1/13 17:39
 */
public class OtherHelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "another say hello！";
    }

    @Override
    public String test() {
        return null;
    }
}
