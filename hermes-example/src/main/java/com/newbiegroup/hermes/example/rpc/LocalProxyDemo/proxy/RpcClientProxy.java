package com.newbiegroup.hermes.example.rpc.LocalProxyDemo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>Description: 本地动态代理 </p>
 *
 * @author yong.zhang
 * @version 1.0.0
 * @date 2020/10/22 19:57
 */
public class RpcClientProxy<T> implements InvocationHandler {

    /**
     * 服务端代理接口
     */
    private Class<T> serverInstance;

    // 目标实现类 Class
    private Object target;

    public RpcClientProxy(Object target) {
        this.target = target;
    }

    public T getProxy() {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("Do something before");
        Object result = method.invoke(target, args);
        System.out.println("Do something after");
        return result;
    }

}
