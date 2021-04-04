package com.newbiegroup.rpc.remoting.client;

import com.newbiegroup.rpc.remoting.codec.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * <p>ClassName: 实际的业务处理器 </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/17 10:24
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel;

    private SocketAddress remotePeer;

    /**
     * 通道激活的时候触发此方法
     * @param ctx
     * @throws Exception
     */
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();

    }

    /**
     * 通道激活的时候触发此方法
     *
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse o) throws Exception {

    }

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    /**
     * Netty提供了一种主动关闭连接的方法
     * <p>
     * 发送一个Unpooled.EMPTY_BUFFER
     * 这样我们的ChannelFutureListener的close事件就会监听到并关闭通道;
     */
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
