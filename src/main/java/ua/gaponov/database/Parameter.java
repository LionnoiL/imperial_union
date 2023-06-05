package ua.gaponov.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Andriy Gaponov
 */
@Getter
@AllArgsConstructor
public class Parameter<T> {
    private T value;
}
