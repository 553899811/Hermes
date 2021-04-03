package com.newbiegroup.rpc.remoting.codec;

import com.newbiegroup.serialization.ProtostuffSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>ClassName: 解码器 </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/3 23:25
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //如果请求数据包 不足4bytes 直接返回,如果大于4 bytes,那么程序继续执行
        if (in.readableBytes() < 4) {
            return;
        }
        // 首先记录一下当前的位置
        in.markReaderIndex();

        // 当前请求包 包体的大小
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();//继续读
            return;
        }

        //真正读取需要长度的数据包内容
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //解码操作 返回指定的对象
        Object obj = ProtostuffSerialization.deserialize(data, genericClass);
        //填充到buffer中，传播给下游handler做实际的处理;
        out.add(obj);
    }
}
