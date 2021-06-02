package com.newbiegroup.hermes.example.hermesJunitTest.consumer;

import com.newbiegroup.rpc.remoting.client.RpcClient;
import com.newbiegroup.rpc.remoting.client.RpcFuture;
import com.newbiegroup.rpc.remoting.client.proxy.RpcAsyncProxy;

import java.util.concurrent.ExecutionException;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/12 1:28
 */
public class ConsumerStarter {

    public static void sync() {
        RpcClient client = new RpcClient();
        client.initClient("127.0.0.1:8765", 3000);
        HelloService helloService = client.invokeSync(HelloService.class);
        String result = helloService.hello("what the black!");
        System.out.println(result);
    }

    public static void async() throws ExecutionException, InterruptedException {
        RpcClient client = new RpcClient();
        client.initClient("127.0.0.1:8765", 3000);
        RpcAsyncProxy proxy = client.invokeAsync(HelloService.class);
        RpcFuture future1 = proxy.call("hello", "张勇");
        RpcFuture future2 = proxy.call("sayHello", new User(1, "233"));
        Object result = future1.get();
        Object result2 = future2.get();
        System.err.println("result: " + result);
        System.err.println("result2: " + result2);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        async();
    }
}
