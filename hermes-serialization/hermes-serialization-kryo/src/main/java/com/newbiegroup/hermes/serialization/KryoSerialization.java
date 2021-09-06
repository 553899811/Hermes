package com.newbiegroup.hermes.serialization;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/14 10:39
 */
public class KryoSerialization<T> implements Serialization<T> {

    @Override
    public <T1> byte[] serialize(T1 obj) {
        return new byte[0];
    }

    @Override
    public <T1> T1 deserialize(byte[] data, Class<T1> cls) {
        return null;
    }
}
