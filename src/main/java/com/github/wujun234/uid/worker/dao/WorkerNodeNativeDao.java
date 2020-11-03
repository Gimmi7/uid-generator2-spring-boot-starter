package com.github.wujun234.uid.worker.dao;

import com.github.wujun234.uid.impl.UidDatasourceProperties;
import com.github.wujun234.uid.worker.entity.WorkerNodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;

public class WorkerNodeNativeDao {
    private static final Logger log = LoggerFactory.getLogger(WorkerNodeNativeDao.class);

    @Autowired
    private UidDatasourceProperties datasourceProperties;

    public void addWorkerNode(WorkerNodeEntity entity) {
        Connection connection = this.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO WORKER_NODE" +
                    "(HOST_NAME," +
                    "PORT," +
                    "TYPE," +
                    "LAUNCH_DATE," +
                    "MODIFIED," +
                    "CREATED)" +
                    "VALUES (" +
                    "?," +
                    "?," +
                    "?," +
                    "?," +
                    "NOW()," +
                    "NOW())";
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getHostName());
            statement.setString(2, entity.getPort());
            statement.setInt(3, entity.getType());
            statement.setDate(4, new Date(entity.getLaunchDate().getTime()));

            statement.executeUpdate();
            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                entity.setId(id);
                System.out.println("***************************************");
                System.out.println("***************************************");
                System.out.println(entity);
                System.out.println("***************************************");
                System.out.println("***************************************");
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private Connection getConnection() {
        try {
            Class.forName(datasourceProperties.getDriverClassName());
            return DriverManager.getConnection(datasourceProperties.getJdbcUrl(), datasourceProperties.getUsername(), datasourceProperties.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
