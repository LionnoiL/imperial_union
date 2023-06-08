package ua.gaponov.database;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.similarity.SimilarityProduct;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class StringDatabaseMapper implements Mapper<String> {

    @Override
    public String map(ResultSet rs) {
        try {
            String result;
            result = rs.getString(1);
            return result;
        } catch (SQLException ex) {
            log.error("Error map string", ex);
        }
        return null;
    }
}
