package com.newbiegroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>Description: </p>
 * <p>Company: https://www.yuque.com/newbiegroup</p>
 *
 * @author newbiegroup
 * @version 1.0.0
 * @date 2021/1/19 15:12
 */
public class Test {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/franchisee_settlement","root","123456");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Hello World!");
        }
        System.out.println(connection.getClass().getClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader());
        System.out.println(Connection.class.getClassLoader());
    }
}
