package com.newbiegroup.hermes.example.hermesJunitTest.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/12 0:30
 */
public class Main {
    public static void main(String[] args) {
        Map map = new HashMap<>(6);
        int cap = 6;
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        System.out.println(n + 1);
    }
}
