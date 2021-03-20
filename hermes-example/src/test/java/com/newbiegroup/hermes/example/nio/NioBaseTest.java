package com.newbiegroup.hermes.example.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/19 17:35
 */
public class NioBaseTest {

    @Test
    public void test01() {
        // 分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("初始化");
        System.out.println("position：" + buf.position());
        System.out.println("limit：" + buf.limit());
        System.out.println("capacity：" + buf.capacity());


        // 存入数据到缓冲区
        String str = "hermes rpc";
        buf.put(str.getBytes());

        System.out.println("存入数据");
        System.out.println("position：" + buf.position());
        System.out.println("limit：" + buf.limit());
        System.out.println("capacity：" + buf.capacity());
        System.out.println("mark：" + buf.mark());
    }
}
