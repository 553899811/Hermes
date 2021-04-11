package com.newbiegroup.hermes.example.rpcPrincipleTest.RemoteProxyDemo.serialize;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/5 22:43
 */
public interface Serialization {

    <T> String serialize(T object);

    <T> T deSerialize(String data, Class<T> clz);
}
