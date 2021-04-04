package com.newbiegroup.rpc.remoting.client;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 22:48
 */
public interface RpcCallBack {

    void success(Object result);

    void failure(Throwable cause);
}
