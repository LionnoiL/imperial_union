package ua.gaponov.entity.product;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class ProductDatabaseMapper implements Mapper<Product> {

    @Override
    public Product map(ResultSet rs) {
        try {
            Product product = new Product();
            product.setId(rs.getString("id"));
            product.setName(rs.getString("product_name"));
            product.setWeight(rs.getBoolean("weight"));

            return product;
        } catch (SQLException ex) {
            log.error("Error map product", ex);
        }
        return null;
    }
}
