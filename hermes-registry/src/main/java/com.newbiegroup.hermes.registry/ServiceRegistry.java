package com.newbiegroup.hermes.registry;

import java.net.InetSocketAddress;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/27 12:43
 */
public interface ServiceRegistry {
    void serviceRegister(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
