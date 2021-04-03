package com.newbiegroup.hermes.example.rpc.Singletons;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/20 14:44
 */
public class Singleton implements Serializable {

    // volatile 禁止指令重排序
    private static volatile Singleton instance;

    /**
     * 私有化构造器,这样该类就不会被实例化;
     */
    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                    return instance;
                }
            }
        }
        return instance;
    }

    /**
     * 提供readResolve()方法
     * 当JVM反序列化恢复一个新对象时，系统会自动调用readResolve()方法返回指定好的对象
     * 从而保证系统通过反序列化机制不会产生多的Java对象
     *
     * @return 单例对象
     * @throws ObjectStreamException 异常
     */
    private Object readResolve() throws ObjectStreamException {
        return instance;
    }
}
