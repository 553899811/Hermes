package com.newbiegroup.hermes.serialization;

/**
 * <p>ClassName: 序列化通用接口 </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/12 0:48
 */
public interface Serialization<T> {

     <T> byte[] serialize(T obj);

     <T> T deserialize(byte[] data, Class<T> cls);
}
