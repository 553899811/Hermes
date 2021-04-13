package com.newbiegroup.rpc.remoting.client;

import com.newbiegroup.rpc.remoting.client.proxy.RpcAsyncProxy;
import com.newbiegroup.rpc.remoting.client.proxy.RpcProxyImpl;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private List<String> serverAddressList;

    private long timeout;

    /**
     * 本地缓存对象 - 同步
     */
    private final Map<Class<?>, Object> syncProxyInstanceMap = new ConcurrentHashMap<>();

    /**
     * 本地缓存对象 - 异步
     */
    private final Map<Class<?>, Object> asyncProxyInstanceMap = new ConcurrentHashMap<>();

    private RpcConnectManager rpcConnectManager;

    @SuppressWarnings("unchecked")
    public void initClient(String serverAddress, long timeout) {
        this.serverAddress = serverAddress;
        this.timeout = timeout;
        this.rpcConnectManager = new RpcConnectManager();
        connect();
    }

    public <T> T initClient(List<String> serverAddressList, long timeout, Class<T> interfaceClass) {
        this.serverAddressList = serverAddressList;
        this.timeout = timeout;
        this.rpcConnectManager = new RpcConnectManager();
        this.rpcConnectManager.connect(serverAddressList);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxyImpl<>(rpcConnectManager, interfaceClass, timeout));
    }

    private void connect() {
        this.rpcConnectManager.connect(this.serverAddress);
    }

    public void stop() {
        this.rpcConnectManager.stop();
    }

    /**
     * 同步调用方法
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Class<T> interfaceClass) {
        if (syncProxyInstanceMap.containsKey(interfaceClass)) {
            return (T) syncProxyInstanceMap.get(interfaceClass);
        } else {
            Object proxy = Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new RpcProxyImpl(rpcConnectManager, interfaceClass, timeout));
            syncProxyInstanceMap.put(interfaceClass, proxy);
            return (T) proxy;
        }
    }

    /**
     * 异步调用
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> RpcAsyncProxy invokeAsync(Class<T> interfaceClass) {

        if (asyncProxyInstanceMap.containsKey(interfaceClass)) {
            return (RpcProxyImpl<T>) asyncProxyInstanceMap.get(interfaceClass);
        } else {
            RpcProxyImpl<T> rpcProxyImpl = new RpcProxyImpl<T>(
                    rpcConnectManager, interfaceClass, timeout);
            asyncProxyInstanceMap.put(interfaceClass, rpcProxyImpl);
            return rpcProxyImpl;
        }
    }
}
