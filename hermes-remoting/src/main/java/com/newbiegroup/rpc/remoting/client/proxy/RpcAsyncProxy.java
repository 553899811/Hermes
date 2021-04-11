package com.newbiegroup.rpc.remoting.client.proxy;

import com.newbiegroup.rpc.remoting.client.RpcFuture;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/11 13:31
 */
public interface RpcAsyncProxy {
    RpcFuture call(String funcName, Object... args);
}
