package com.project.parksystem.config;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Самописный пул соединений с возвратом соединения через close().
 */
public class ConnectionPool {
    private static final String URL = "jdbc:postgresql://localhost:5432/MyPark";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1234";
    private static final int MAX_CONNECTIONS = 10;

    private static final List<Connection> availableConnections = new ArrayList<>();

    static {
        try {
            Class.forName("org.postgresql.Driver");
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                Connection realConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                availableConnections.add(realConnection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инициализации пула соединений", e);
        }
    }

    public static synchronized Connection getConnection() {
        //System.out.println("Доступные соединения: " + availableConnections.size());
        while (availableConnections.isEmpty()) {
            try {
                ConnectionPool.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ожидание соединения было прервано", e);
            }
        }

        Connection realConnection = availableConnections.remove(0);

        // Оборачиваем в Proxy: вызов connection.close() будет возвращать соединение обратно
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        returnConnection(realConnection);
                        return null;
                    }
                    return method.invoke(realConnection, args);
                }
        );
    }

    public static synchronized void returnConnection(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) return;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при проверке соединения", e);
        }
        availableConnections.add(connection);
        ConnectionPool.class.notifyAll();
    }
}
