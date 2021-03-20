package com.newbiegroup.hermes.example.rpc.LocalProxyDemo.client;

import com.newbiegroup.hermes.example.rpc.LocalProxyDemo.proxy.RpcClientProxy;
import com.newbiegroup.hermes.example.rpc.service.HelloService;
import com.newbiegroup.hermes.example.rpc.service.impl.HelloServiceImpl;

import java.io.Serializable;

/**
 * <p>Description: </p>
 * <p>Company: https://www.yuque.com/newbiegroup</p>
 *
 * @author newbiegroup
 * @version 1.0.0
 * @date 2020/10/22 19:56
 */
public class ClientBoot implements Serializable {
    //https://www.jianshu.com/p/bb9beca7f7bc
    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        HelloService proxy = (HelloService) new RpcClientProxy(new HelloServiceImpl()).getProxy();
        System.out.println(proxy.test());
    }

}
