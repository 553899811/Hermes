package com.newbiegroup.rpc.remoting.client;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>ClassName: RPC连接管理器 </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/17 0:08
 */
public class RpcConnectManager {

    private static final Logger log = LoggerFactory.getLogger(RpcConnectManager.class);
    private static volatile RpcConnectManager RPC_CONNECT_MANAGER = new RpcConnectManager();

    /**
     * 一个连接的地址，对应一个世纪的业务处理器(client)
     */
    private Map<InetSocketAddress, RpcClientHandler> connectedHandlerMap = new ConcurrentHashMap<>();

    /**
     * 所有连接成功的地址 所对应的任务执行器列表;
     */
    private CopyOnWriteArrayList<RpcClientHandler> connectedHandleList = new CopyOnWriteArrayList<>();
    /**
     * 用于异步的提交连接请求的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1 << 16));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private RpcConnectManager() {

    }

    public static RpcConnectManager getInstance() {
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

    /**
     * @param allServerAddress
     * @desc 更新地址缓存信息，并异步发起链接
     */
    public void updateConnectedServer(List<String> allServerAddress) {
        if (CollectionUtils.isEmpty(allServerAddress)) {
            log.error("no available ServerAddress");
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

    }

    private void connectAsync(InetSocketAddress remotePeer) {
        threadPoolExecutor.execute(new Runnable() {
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

                }
            }
        });
    }

    /**
     * 连接失败时，及时的释放资源，清空缓存;
     * 先删除所有的connectedHandlerMap中的数据
     * 然后再清空connectedHandleList中的数据;
     */
    private void clearConnected() {
        for (RpcClientHandler rpcClientHandler : connectedHandleList) {
            //通过RpcClientHandler找到具体的远程地址，从connectedHandlerMap中移除;
            SocketAddress remotePeer = rpcClientHandler.getRemotePeer();
            RpcClientHandler handler = connectedHandlerMap.get(remotePeer);
            if (handler != null) {
                handler.close();
                connectedHandlerMap.remove(remotePeer);
            }
        }
        connectedHandleList.clear();
    }

    public static void main(String[] args) {
    }
}
