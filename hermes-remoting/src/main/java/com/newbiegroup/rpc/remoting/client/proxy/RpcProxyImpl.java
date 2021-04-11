package com.newbiegroup.rpc.remoting.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/11 18:03
 */
public class RpcProxyImpl<T> implements InvocationHandler {

    private Class<?> clazz;

    private long timeout;

    public RpcProxyImpl(Class<T> interfaceClass, long timeout) {
        this.clazz = interfaceClass;
        this.timeout = timeout;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
