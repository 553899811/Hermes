package com.newbiegroup.hermes.example.RemoteProxyDemo.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/3/5 23:23
 */
public class FastJsonSerialization implements Serialization {
    @Override
    public <T> String serialize(T object) {
        String bytes = JSONObject.toJSONString(object);
        return bytes;
    }

    @Override
    public <T> T deSerialize(String data, Class<T> clz) {
        T o = JSONObject.parseObject(data, clz);
        return o;
    }

    static class Student implements Serializable {
        String name;
        int score;
        Student(){

        }
        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        FastJsonSerialization fastJsonSerialization = new FastJsonSerialization();
        Student stu = new Student("张三丰",99);
        String data = fastJsonSerialization.serialize(stu);
        System.out.println(data);
        Student student = fastJsonSerialization.deSerialize(data, Student.class);
        System.out.println(student.name);
    }
}
