package ua.gaponov.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
public class StatementParameters<T> {

    private final List<Parameter> items = new ArrayList<>();

    public StatementParameters() {
    }

    public StatementParameters(T value) {
        items.add(new Parameter<T>(value));
    }

    public static <T> StatementParameters<T> build(T... elements) {
        StatementParameters<T> result = new StatementParameters<T>();
        result.addAll(elements);
        return result;
    }

    private void add(Parameter parameter) {
        items.add(parameter);
    }

    public List<Parameter> getItems() {
        return items;
    }

    public void fillStatement(PreparedStatement statement) throws SQLException {
        List<Parameter> params = getItems();
        int index = 1;
        for (Parameter p : params) {
            statement.setObject(index++, p.getValue());
        }
    }

    public <T> void addAll(T... elements) {
        for (T element : elements) {
            add(new Parameter(element));
        }
    }

    public void addNull() {
        add(new Parameter(null));
    }
}
