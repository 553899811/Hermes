package com.newbiegroup.rpc.remoting.client;

import com.newbiegroup.rpc.remoting.codec.RpcDecoder;
import com.newbiegroup.rpc.remoting.codec.RpcEncoder;
import com.newbiegroup.rpc.remoting.codec.RpcRequest;
import com.newbiegroup.rpc.remoting.codec.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/17 10:36
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();

        //编解码的handler
        cp.addLast(new RpcEncoder(RpcRequest.class));

        /**
         * 包头+包体：最大 1<<16
         * 包头 范围[0,4] 前四个字节;
         */
        cp.addLast(new LengthFieldBasedFrameDecoder(1 << 16, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        //实际的业务处理器RpcClientHandler
        cp.addLast(new RpcClientHandler());
    }
}
