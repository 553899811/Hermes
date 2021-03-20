package com.newbiegroup.hermes.example.rpc.RemoteProxyDemo.client;

import com.newbiegroup.hermes.example.rpc.RemoteProxyDemo.proxy.RpcClientProxy;
import com.newbiegroup.hermes.example.rpc.service.HelloService;

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
        new Task().run();
    }

    static class Task implements Runnable {
        RpcClientProxy<HelloService> rpcClientProxy = new RpcClientProxy<>(HelloService.class, "127.0.0.1", 8888);
        HelloService helloService = rpcClientProxy.getClientInstance();

        @Override
        public void run() {
            String result = helloService.sayHello("中国");
            System.out.println(result);
        }
    }
}
