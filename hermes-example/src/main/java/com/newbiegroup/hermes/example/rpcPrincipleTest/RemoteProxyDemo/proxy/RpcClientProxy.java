package com.newbiegroup.hermes.example.rpcPrincipleTest.RemoteProxyDemo.proxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <p>Description: 远程动态代理 </p>
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

    /**
     * 服务端地址
     */
    private InetSocketAddress address;

    public RpcClientProxy(Class<T> serverInstance, String ip, Integer port) {
        this.address = new InetSocketAddress(ip, port);
        this.serverInstance = serverInstance;
    }

    public T getClientInstance() {
        return (T) Proxy.newProxyInstance(serverInstance.getClassLoader(), new Class<?>[]{serverInstance}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            //1. 创建Socket客户端，根据指定地址连接远程服务提供者
            socket = new Socket();
            socket.connect(address);
            //2. 将远程服务调用所需的接口类，方法名，参数列表等编码后发送给服务提供者
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeUTF(serverInstance.getName());//接口名
            oos.writeUTF(method.getName());//方法名
            oos.writeObject(method.getParameterTypes());//方法参数类型,数组
            oos.writeObject(args);//具体方法参数值;
            //3. 同步阻塞等待服务器返回应答，获取应答后返回
            ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
    }
}
