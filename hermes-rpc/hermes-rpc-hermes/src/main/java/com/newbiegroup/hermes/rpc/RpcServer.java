package com.newbiegroup.hermes.rpc;

import com.newbiegroup.hermes.common.config.provider.ProviderConfig;
import com.newbiegroup.rpc.remoting.codec.RpcDecoder;
import com.newbiegroup.rpc.remoting.codec.RpcEncoder;
import com.newbiegroup.rpc.remoting.codec.RpcRequest;
import com.newbiegroup.rpc.remoting.codec.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 10:00
 */
@Slf4j
public class RpcServer {
    private String serverAddress;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private volatile Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress) throws InterruptedException {
        this.serverAddress = serverAddress;
        this.start();
    }

    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(new LengthFieldBasedFrameDecoder(1 << 16, 0, 4, 0, 0));
                        cp.addLast(new RpcDecoder(RpcRequest.class));
                        cp.addLast(new RpcEncoder(RpcResponse.class));
                        cp.addLast(new RpcServerHandler(handlerMap));
                    }
                });
        String[] array = serverAddress.split(";");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("server success bind to " + serverAddress);
                } else {
                    log.info("server fail bind to" + serverAddress);
                    throw new Exception("server start fail,cause:" + future.cause());
                }
            }
        });

        try {
            channelFuture.await(5000, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                log.info("start hermes rpc channel success!");
            }
        } catch (InterruptedException e) {
            log.error("start hermes rpc occur interrupted,ex:" + e);
        }
    }

    /**
     * 程序注册器
     * 将服务以<K,V>形式注册到本地缓存Map中
     *
     * @param providerConfig
     */
    public void registerProcessor(ProviderConfig providerConfig) {
        //key: providerConfig.interface(userService接口权限命名)
        //value: providerConfig.ref(userService接口下具体的实现类 userServiceImpl实例对象)
        handlerMap.put(providerConfig.getInterface(), providerConfig.getRef());
    }

    /**
     * 关闭服务端server
     * 优雅关闭线程资源
     */
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
