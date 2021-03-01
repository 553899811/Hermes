package com.newbiegroup.hermes.example.LocalProxyDemo.client;

import com.newbiegroup.hermes.example.LocalProxyDemo.proxy.RpcClientProxy;
import com.newbiegroup.hermes.example.service.HelloService;
import com.newbiegroup.hermes.example.service.impl.HelloServiceImpl;

/**
 * <p>Description: </p>
 * <p>Company: https://www.yuque.com/newbiegroup</p>
 *
 * @author newbiegroup
 * @version 1.0.0
 * @date 2020/10/22 19:56
 */
public class ClientBoot {
    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        HelloService proxy = (HelloService) new RpcClientProxy(new HelloServiceImpl()).getProxy();
        System.out.println(proxy.test());
    }

}
