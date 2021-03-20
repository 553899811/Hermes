package com.newbiegroup.hermes.example.rpc.RemoteProxyDemo.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RpcServer {

    private static final Map<String, Class<?>> serviceRegistry = new ConcurrentHashMap<>();
    private int port;

    public RpcServer(int port) {
        this.port = port;
    }

    public RpcServer register(Class serviceInterface, Class impClass) {
        //getCanonicalName()是获取所传类从java语言规范定义的格式输出
        serviceRegistry.put(serviceInterface.getCanonicalName(), impClass);
        return this;
    }

    public void run() throws IOException {
        //1.启动服务
        ServerSocket serverSocket = new ServerSocket(port,3);
//        serverSocket.bind(new InetSocketAddress());
        System.out.println("start server!");
        //ois 和 oos 用于序列化;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        Socket socket = null;
        //server端开启后 ，开启线程 循环等待 客户端链接
        try {
            while (true) {
                //构建通讯管道，循环等待客户端链接;
                socket = serverSocket.accept();
                ois = new ObjectInputStream(socket.getInputStream());
                String serviceName = ois.readUTF();//客户端传过来的接口名字
                String methodName = ois.readUTF();//客户端传过来的方法名字
                Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();//客户端传过来的方法参数类型
                Object[] args = (Object[]) ois.readObject();//客户端传过来的参数值;
                System.out.println("接收到客户端的请求: serviceName: " + serviceName + ", methodName:" + methodName);
                //4. 根据客户端穿过来的信息获取注册的服务端接口,并调用
                Class<?> serviceClass = serviceRegistry.get(serviceName);
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + " not found!");
                }
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object invokeResult = method.invoke(serviceClass.newInstance(), args);
                //5. 返回调用的方法
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(invokeResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void close(ObjectInputStream ois, ObjectOutputStream oos, Socket socket) {
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
