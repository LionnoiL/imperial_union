package ua.gaponov.database;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor
@Slf4j
public class SqlHelper<T> {

    public static void execSql(String sql) {
        try (Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            //NOP
        }
    }

    public void execSql(String sql, StatementParameters parameters) throws SQLException {
        try (Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            parameters.fillStatement(statement);
            statement.executeUpdate();
        }
    }

    public void execSql(List<DatabaseRequest> requests)
            throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (DatabaseRequest request : requests) {
                    PreparedStatement statement = connection.prepareStatement(request.getSql());
                    StatementParameters param = request.getParam();
                    param.fillStatement(statement);
                    statement.executeUpdate();
                    statement.close();
                }
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                log.error("Database request execute failed. {}", ex);
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<T> getAll(String sql, StatementParameters parameters, Mapper mapper) {
        List<T> result = new ArrayList<T>();
        try (Connection connection = Database.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(sql);
            parameters.fillStatement(statement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add((T) mapper.map(resultSet));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<T> getAll(String sql, Mapper<T> mapper) {
        List<T> result = new ArrayList<T>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(mapper.map(resultSet));
            }
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public T getOne(String sql, StatementParameters parameters, Mapper<T> mapper) {
        List<T> list = getAll(sql, parameters, mapper);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public int getCount(String sql) {
        int result = 0;
        try (Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public double getSum(String sql) {
        double result = 0;
        try (Connection connection = Database.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                BigDecimal sumResult = (BigDecimal) resultSet.getObject(1);
                if (Objects.nonNull(sumResult)) {
                    result = result + sumResult.doubleValue();
                }
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public T getLast(String tableName, String resultFieldName, String idFieldName, String where) {
        String sql = "select " + resultFieldName + " from " + tableName + " " + where + " order by " + idFieldName + " desc limit 1;";
        T result = null;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T sumResult = (T) resultSet.getObject(1);
                if (Objects.nonNull(sumResult)) {
                    result = sumResult;
                }
            }
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public T getFirst(String tableName, String resultFieldName, String idFieldName, String where) {
        String sql = "select " + resultFieldName + " from " + tableName + " " + where + " order by " + idFieldName + " limit 1;";
        T result = null;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T sumResult = (T) resultSet.getObject(1);
                if (Objects.nonNull(sumResult)) {
                    result = sumResult;
                }
            }
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
