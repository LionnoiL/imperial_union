package ua.gaponov.entity.barcodes;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Mapper;
import ua.gaponov.database.MapperException;
import ua.gaponov.entity.product.ProductService;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class BarcodeDatabaseMapper implements Mapper<Barcode> {

    @Override
    public Barcode map(ResultSet rs) {
        try {
            Barcode barcode = new Barcode();
            barcode.setValue(rs.getString("barcode"));
            barcode.setProduct(ProductService.getProductById(rs.getString("product_id")));
            return barcode;
        } catch (SQLException ex) {
            log.error("Error map barcode", ex);
            new MapperException("Error map barcode");
        }
        return null;
    }
}
