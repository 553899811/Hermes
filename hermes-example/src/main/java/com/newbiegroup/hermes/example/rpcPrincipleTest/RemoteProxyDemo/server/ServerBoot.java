package com.newbiegroup.hermes.example.rpcPrincipleTest.RemoteProxyDemo.server;

import com.newbiegroup.hermes.example.rpcPrincipleTest.service.HelloService;
import com.newbiegroup.hermes.example.rpcPrincipleTest.service.impl.HelloServiceImpl;

import java.io.IOException;

/**
 * <p>Description: </p>
 * <p>Company: https://www.yuque.com/newbiegroup</p>
 *
 * @author newbiegroup
 * @version 1.0.0
 * @date 2020/10/22 19:56
 */
public class ServerBoot {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(8888);
        rpcServer.register(HelloService.class, HelloServiceImpl.class);
        try {
            rpcServer.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
