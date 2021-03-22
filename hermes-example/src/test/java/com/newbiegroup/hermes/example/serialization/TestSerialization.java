package com.newbiegroup.hermes.example.serialization;

import com.newbiegroup.hermes.example.rpc.Serialization.Student;
import com.newbiegroup.hermes.example.rpc.Singletons.Singleton;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/20 12:36
 */
public class TestSerialization {

    @Test
    public void testWrite() throws Exception {
        Student stu1 = new Student(1001, "jack");
        Student stu2 = new Student(1002, "tom");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("d:\\stu.dat"));
        oos.writeObject(stu1);
        oos.writeObject(stu2);
        oos.close();
    }

    @Test
    public void testSingeton() throws Exception {
        //获取instance 对象
        Singleton instance = Singleton.getInstance();
        // 获取文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\Test.txt");
        // 获取对象输出流
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        // 输出对象
        objectOutputStream.writeObject(instance);

        // 关闭资源
        objectOutputStream.close();
        fileOutputStream.close();

        // 获取对象输入流
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("d:\\Test.txt"));

        // 读取对象
        Object object = objectInputStream.readObject();

        // 判断两个对象是否相等，返回true/false
        System.out.println(instance == object);
    }

}
