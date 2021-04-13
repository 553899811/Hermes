package com.newbiegroup.rpc.remoting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>ClassName: RPC连接管理器
 * 核心作用：服务于RpcClient;
 * 核心功能:
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/17 0:08
 */
@Slf4j
public class RpcConnectManager {

    /**
     * 单例
     * volatile 禁止指令重排序
     */
    private static volatile RpcConnectManager RPC_CONNECT_MANAGER;

    /**
     * 一个连接的地址，对应一个世纪的业务处理器(client)
     */
    private Map<InetSocketAddress, RpcClientHandler> connectedHandlerMap = new ConcurrentHashMap<>();

    /**
     * 所有连接成功的地址 所对应的任务执行器列表;
     */
    private CopyOnWriteArrayList<RpcClientHandler> connectedHandlerList = new CopyOnWriteArrayList<>();
    /**
     * 用于异步的提交连接请求的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1 << 16));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private ReentrantLock connectedLock = new ReentrantLock();

    private Condition connectedCondition = connectedLock.newCondition();

    private long connectedTimeOutMills = 6000;


    private AtomicInteger handlerIdx = new AtomicInteger(0);

    @Getter
    @Setter
    private volatile boolean isRunning = true;

    public RpcConnectManager() {

    }

    /**
     * DCL 双重校验
     *
     * @return
     */
    public static RpcConnectManager getInstance() {
        if (RPC_CONNECT_MANAGER == null) {
            synchronized (RpcConnectManager.class) {
                if (RPC_CONNECT_MANAGER == null) {
                    RPC_CONNECT_MANAGER = new RpcConnectManager();
                    return RPC_CONNECT_MANAGER;
                }
            }
        }
        return RPC_CONNECT_MANAGER;
    }
    //1 .异步连接 线程池 真正的发起连接，连接失败监听，连接成功监听
    //2 . 对于连接进来的资源做一个缓存管理，updateConnectedServer

    public void connect(final String serverAddress) {
        if (StringUtils.isBlank(serverAddress)) {
            throw new RuntimeException("remoting serverAddress can't be blank");
        }
        // 按照逗号解析
        List<String> allServerAddress = Arrays.asList(serverAddress.split(","));
        updateConnectedServer(allServerAddress);
    }

    public void connect(List<String> serverAddressList) {
        updateConnectedServer(serverAddressList);
    }

    /**
     * @param allServerAddress
     * @desc 更新地址缓存信息，并异步发起链接
     * 192.168.11.111:8765,192.168.11.112:8765,192.168.11.113:8765
     */
    public void updateConnectedServer(List<String> allServerAddress) {
        if (CollectionUtils.isEmpty(allServerAddress)) {
            //清楚所有的缓存信息;
            clearConnected();
            log.error("no available server address");
            return;
        }

        // 1.解析 allServerAddress地址，并且临时存储到我们的newAllInetSocketAddress 缓存中;
        HashSet<InetSocketAddress> newAllInetSocketAddress = new HashSet<>();
        for (int i = 0; i < allServerAddress.size(); i++) {
            String[] array = allServerAddress.get(i).split(":");
            if (array.length == 2) {
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
                newAllInetSocketAddress.add(remotePeer);
            }
        }
        // 2. 调用建立连接方法，发起远程连接操作
        for (InetSocketAddress serverNodeAddress : newAllInetSocketAddress) {
            if (!connectedHandlerMap.keySet().contains(serverNodeAddress)) {
                connectAsync(serverNodeAddress);
            }
        }
        // 3. 如果allServerAddress 列表里不存在的链接地址，那么我们需要从缓存中移除掉
        for (int i = 0; i < connectedHandlerList.size(); i++) {
            RpcClientHandler rpcClientHandler = connectedHandlerList.get(i);
            SocketAddress remotePeer = rpcClientHandler.getRemotePeer();
            if (!newAllInetSocketAddress.contains(remotePeer)) {
                RpcClientHandler handler = connectedHandlerMap.get(remotePeer);
                if (handler != null) {
                    handler.close();//释放资源
                    connectedHandlerMap.remove(remotePeer);
                }
                connectedHandlerList.remove(rpcClientHandler);
            }
        }
    }

    private void connectAsync(InetSocketAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new RpcClientInitializer());

                connect(b, remotePeer);
            }
        });
    }

    private void connect(final Bootstrap b, InetSocketAddress remotePeer) {
        //1 .建立连接
        final ChannelFuture channelFuture = b.connect(remotePeer);
        //2.连接失败的时候添加监听，清除资源后进行释放发起重连操作
        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        log.warn(" connect fail, to reconnect! ");
                        clearConnected();
                        //资源释放后 重新连接;
                        connect(b, remotePeer);
                    }
                }, 3, TimeUnit.SECONDS);
            }
        });
        //3.连接成功的时候添加监听，把我们的新连接放入到缓存中;
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("successfully connect to remote server, remote peer = " + remotePeer);
                    RpcClientHandler handler = future.channel().pipeline().get(RpcClientHandler.class);
                    addHandler(handler);
                }
            }
        });
    }

    /**
     * 添加RpcClientHandler到指定的缓存中
     * connectedHandlerMap && connectedHandlerList
     *
     * @param handler
     */
    private void addHandler(RpcClientHandler handler) {
        connectedHandlerList.add(handler);
        InetSocketAddress remotePeer = (InetSocketAddress) handler.getChannel().remoteAddress();
        connectedHandlerMap.put(remotePeer, handler);
        //signalAvailableHandler 唤醒可用的业务执行器
        signalAvailableHandler();
    }

    /**
     * 唤醒另外一端的线程(阻塞的状态中) 告知有新连接接入
     */
    private void signalAvailableHandler() {
        connectedLock.lock();
        try {
            connectedCondition.signalAll();
        } finally {
            connectedLock.unlock();
        }
    }

    /**
     * 等待新连接接入通知方法
     *
     * @return
     */
    private boolean waitingForAvailableHandler() throws InterruptedException {
        connectedLock.lock();
        try {
            return connectedCondition.await(this.connectedTimeOutMills, TimeUnit.MICROSECONDS);
        } finally {
            connectedLock.unlock();
        }
    }

    /**
     * 选择一个实际的业务处理器
     *
     * @return
     */
    public RpcClientHandler chooseHandler() {
        List<RpcClientHandler> hanlders = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlerList.clone();
        int size = hanlders.size();
        /**
         * 说明缓存中没有任何新的连接;
         */
        while (isRunning && size <= 0) {
            try {
                boolean available = waitingForAvailableHandler();
                if (available) {
                    //重新获取新值;
                    hanlders = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlerList.clone();
                    size = hanlders.size();
                }
            } catch (InterruptedException e) {
                //等待可用handler 期间被打断
                log.error(" waiting for available node is interrupted");
                throw new RuntimeException("no connect any services", e);
            }
        }
        if (!isRunning) {
            log.info("rpchandler has been closed");
            return null;
        }
        int dataIndex = (handlerIdx.getAndAdd(1) + size) % size;
        return hanlders.get(dataIndex);
    }

    /**
     * 连接失败时，及时的释放资源，清空缓存;
     * 先删除所有的connectedHandlerMap中的数据
     * 然后再清空connectedHandleList中的数据;
     */
    private void clearConnected() {
        for (RpcClientHandler rpcClientHandler : connectedHandlerList) {
            //通过RpcClientHandler找到具体的远程地址，从connectedHandlerMap中移除;
            SocketAddress remotePeer = rpcClientHandler.getRemotePeer();
            RpcClientHandler handler = connectedHandlerMap.get(remotePeer);
            if (handler != null) {
                //关闭连接;
                handler.close();
                connectedHandlerMap.remove(remotePeer);
            }
        }
        connectedHandlerList.clear();
    }


    /**
     * 将线程资源优雅关闭
     */
    public void stop() {
        isRunning = false;
        for (int i = 0; i < connectedHandlerList.size(); i++) {
            RpcClientHandler rpcClientHandler = connectedHandlerList.get(i);
            rpcClientHandler.close();
        }
        //唤醒正在阻塞的请求获取handler的线程，并通过isRunning 返回null 将其释放;
        signalAvailableHandler();
        //关闭线程池;
        threadPoolExecutor.shutdown();
        //eventLoop安全关闭
        eventLoopGroup.shutdownGracefully();
    }


    /**
     * 发起重连
     * 需要将对应的资源释放
     *
     * @param handler
     * @param remotePeer
     */
    public void reconnect(final RpcClientHandler handler, final InetSocketAddress remotePeer) {
        if (handler != null) {
            handler.close();
            connectedHandlerList.remove(handler);
            connectedHandlerMap.remove(remotePeer);
        }
        connectAsync(remotePeer);
    }
}
