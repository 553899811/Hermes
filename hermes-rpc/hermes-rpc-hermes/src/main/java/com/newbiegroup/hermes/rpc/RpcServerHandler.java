package com.newbiegroup.hermes.rpc;

import com.newbiegroup.rpc.remoting.codec.RpcRequest;
import com.newbiegroup.rpc.remoting.codec.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 10:18
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String, Object> handlerMap;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1 << 16));

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    //1.解析rpcRequest
    //2.从handlerMap中找到具体的接口(key)所绑定的具体实现
    //3.通过反射cglib调用具体方法传递相关执行参数执行逻辑
    //4.返回响应信息给client;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        // submit 和 execute 区别;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                RpcResponse rpcResponse = new RpcResponse();
                rpcResponse.setRequestId(rpcRequest.getRequestId());
                try {
                    Object result = handle(rpcRequest);
                    rpcResponse.setResult(result);
                } catch (Throwable t) {
                    log.error("rpc server handle request throwable:" + t);
                }

                ctx.writeAndFlush(rpcResponse).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            //add afterRpcHook;
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析RpcRequest请求并且通过反射获取具体的本地服务实例后执行具体的方法
     *
     * @param rpcRequest
     * @return
     */
    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceRef = handlerMap.get(className);
        Class<?> serviceRefClass = serviceRef.getClass();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();
        //[1] JDK 动态代理
        //[2] cglib代理
        FastClass serviceFastClass = FastClass.create(serviceRefClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

        return serviceFastMethod.invoke(serviceRef, parameters);
    }

    /**
     * 异常处理关闭连接
     *
     * @param ctx
     * @param cause
     */
    @Override
    @SuppressWarnings("deprecation")
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught throwable:" + cause);
        ctx.close();
    }

}
