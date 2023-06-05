package ua.gaponov.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
@AllArgsConstructor
public class DatabaseRequest {
    private String sql;
    private StatementParameters<Object> param;
}
