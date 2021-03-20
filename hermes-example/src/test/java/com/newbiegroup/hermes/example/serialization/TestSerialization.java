package com.newbiegroup.hermes.example.serialization;

import com.newbiegroup.hermes.example.rpc.Serialization.Student;
import org.junit.Test;

import java.io.*;
import java.util.List;

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
    public void testRead() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:\\stu.dat"));
         List<Student> list=(List<Student>) ois.readObject();
        for (Student student : list) {
            System.out.println(student.getName());
        }
    }
}
