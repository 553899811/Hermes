package com.newbiegroup.rpc.remoting.client;

import com.newbiegroup.rpc.remoting.client.proxy.RpcProxyImpl;

import java.lang.reflect.Proxy;

/**
 * <p>ClassName:RPC客户端  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/3 10:32
 */
public class RpcClient {

    private String serverAddress;

    private long timeout;

    public void initClient(String serverAddress, long timeout) {
        this.serverAddress = serverAddress;
        this.timeout = timeout;
        connect();
    }

    private void connect() {
        RpcConnectManager.getInstance().connect(serverAddress);
    }

    public void stop() {
        RpcConnectManager.getInstance().stop();
    }

    /**
     * 同步调用;
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> T invokeSync(Class<T> interfaceClass) {
        Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxyImpl(interfaceClass, timeout));
        return (T) proxy;
    }
}
