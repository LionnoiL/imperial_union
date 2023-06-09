package ua.gaponov.entity.product1c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.utils.FilesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Product1CService {

    public static List<Product1C> getProductsFromFile(String fileName, int shopId) {
        List<Product1C> product1CList = new ArrayList<>();

        List<String> fileLines = FilesUtils.getFilesLines(fileName);
        for (String fileLine : fileLines) {
            try {
                Product1C product1C = new Product1C();
                product1C.setShopId(shopId);
                String[] strings = fileLine.split(";");
                product1C.setCode(strings[0]);
                product1C.setName(strings[1]);
                if (Objects.equals(strings[2], "1")) {
                    product1C.setWeight(Objects.equals(strings[2], "1"));
                }
                product1C.setBarcode(null);
                if (strings.length == 4) {
                    if ('2'!=strings[3].charAt(0)){
                        product1C.setBarcode(strings[3]);
                    }
                }
                product1CList.add(product1C);
            } catch (Exception e) {
                log.error("Failed parse csv line {}", fileLine);
            }
        }

        return product1CList;
    }
}
