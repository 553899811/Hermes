package com.newbiegroup.rpc.remoting.client.proxy;

import com.newbiegroup.rpc.remoting.client.RpcClientHandler;
import com.newbiegroup.rpc.remoting.client.RpcConnectManager;
import com.newbiegroup.rpc.remoting.client.RpcFuture;
import com.newbiegroup.rpc.remoting.codec.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/11 13:32
 */
public class RpcProxyImpl<T> implements RpcAsyncProxy, InvocationHandler {

    private Class<T> clazz;

    private long timeout;

    private RpcConnectManager rpcConnectManager;

    public RpcProxyImpl(RpcConnectManager rpcConnectManager, Class<T> clazz, long timeout) {
        this.clazz = clazz;
        this.timeout = timeout;
        this.rpcConnectManager = rpcConnectManager;
    }


    /**
     * 异步的代理接口实现,真正的抛出去RpcFuture给业务方法做实际的回调等待结果
     *
     * @param funcName
     * @param args
     * @return
     */
    @Override
    public RpcFuture call(String funcName, Object... args) {
        //1.设置请求对象
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(this.clazz.getName());
        request.setMethodName(funcName);
        request.setParameters(args);
        //	TODO: 对应的方法参数类型应该通过 类类型 + 方法名称 通过反射得到parameterTypes
        Class<?>[] parameterTypes = new Class[args.length];
        for(int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        //2.选择一个合适的Client任务处理器
//		RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();
        RpcClientHandler handler = this.rpcConnectManager.chooseHandler();
        RpcFuture future = handler.sendRequest(request);
        return future;
    }

    private Class<?> getClassType(Object arg) {
        Class<?> classType = arg.getClass();
        String typeName = classType.getName();
        if (typeName.equals("java.lang.Integer")) {
            return Integer.TYPE;
        } else if (typeName.equals("java.lang.Long")) {
            return Long.TYPE;
        } else if (typeName.equals("java.lang.Float")) {
            return Float.TYPE;
        } else if (typeName.equals("java.lang.Double")) {
            return Double.TYPE;
        } else if (typeName.equals("java.lang.Character")) {
            return Character.TYPE;
        } else if (typeName.equals("java.lang.Boolean")) {
            return Boolean.TYPE;
        } else if (typeName.equals("java.lang.Short")) {
            return Short.TYPE;
        } else if (typeName.equals("java.lang.Byte")) {
            return Byte.TYPE;
        }
        return classType;
    }

    /**
     * invoke代理接口调用方式
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.设置请求对象
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        //2.选择一个合适的client任务管理器
        RpcClientHandler handler = this.rpcConnectManager.chooseHandler();
        //3.发送真正的客户端请求,返回结果
        RpcFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get(timeout, TimeUnit.SECONDS);
    }


}
