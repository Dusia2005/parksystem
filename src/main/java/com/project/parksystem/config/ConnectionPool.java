package com.project.parksystem.config;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Самописный пул соединений.
 * Использует Proxy для возврата соединений обратно в пул при вызове close().
 */
public final class ConnectionPool {

    private static final String URL = "jdbc:postgresql://localhost:5432/MyPark";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1234";
    private static final int MAX_CONNECTIONS = 10;

    private static final List<Connection> availableConnections = new ArrayList<>();

    // Запрещаем создание экземпляров класса
    private ConnectionPool() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
            initializePool();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL драйвер не найден", e);
        }
    }

    /**
     * Инициализирует пул соединений.
     */
    private static void initializePool() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                Connection realConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                availableConnections.add(realConnection);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при создании соединения", e);
            }
        }
    }

    /**
     * Получает соединение из пула.
     *
     * @return Proxy-соединение, возвращающееся в пул при close()
     * @throws RuntimeException если поток был прерван во время ожидания
     */
    public static synchronized Connection getConnection() {
        while (availableConnections.isEmpty()) {
            try {
                ConnectionPool.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ожидание соединения было прервано", e);
            }
        }

        Connection realConnection = availableConnections.remove(0);

        return createConnectionProxy(realConnection);
    }

    /**
     * Создает прокси для соединения, которое возвращается в пул при вызове close().
     *
     * @param realConnection реальное соединение с БД
     * @return прокси-соединение
     */
    private static Connection createConnectionProxy(Connection realConnection) {
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

    /**
     * Возвращает соединение обратно в пул.
     *
     * @param connection соединение для возврата
     * @throws RuntimeException если произошла ошибка при проверке соединения
     */
    public static synchronized void returnConnection(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при проверке соединения", e);
        }

        availableConnections.add(connection);
        ConnectionPool.class.notifyAll();
    }
}