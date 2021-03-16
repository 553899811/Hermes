package com.newbiegroup.rpc.remoting.client;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/17 0:08
 */

public class RpcConnectManager {

    private static volatile RpcConnectManager RPC_CONNECT_MANAGER = new RpcConnectManager();


    public static RpcConnectManager getInstance() {
        return RPC_CONNECT_MANAGER;
    }
}
