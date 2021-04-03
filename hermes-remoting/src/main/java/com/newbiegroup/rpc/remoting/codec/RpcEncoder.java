package com.newbiegroup.rpc.remoting.codec;

import com.newbiegroup.serialization.ProtostuffSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>ClassName:  编码器 </p>
 *
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/3 18:08
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {


    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    /**
     * 编码器要做的事情
     * 1.把对应的java对象进行编码
     * 2.之后把内容填充到buffer中
     * 3.发送到server端;
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] data = ProtostuffSerialization.serialize(msg);

            //消息分为：1.包头(数据包长度) 2.包体(数据包内容)
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
