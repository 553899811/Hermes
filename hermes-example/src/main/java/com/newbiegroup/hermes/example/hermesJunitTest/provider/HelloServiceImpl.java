package com.newbiegroup.hermes.example.hermesJunitTest.provider;

import com.newbiegroup.hermes.example.hermesJunitTest.consumer.HelloService;
import com.newbiegroup.hermes.example.hermesJunitTest.consumer.User;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/12 0:38
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(User user) {
        return user.getName() + " is " + user.getAge() + " years old,he say Hello to World!";
    }

    @Override
    public String hello(String name) {
        System.err.println("---------服务调用-------------");
        return "hello! " + name;
    }
}
