package app.schema.price;

import app.factory.SchemaInfo;
import joptsimple.internal.Strings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author OAK
 *
 */
@Service
public class ListPriceMtm implements SchemaInfo {

    @Override
    public String getTableName() {
        return "listprice_mtm_office";
    }

    @Override
    public String [] getColumns() {
        String [] columns = {
                "module",
                "condition_type",
                "condition_table",
                "sales_org",
                "sales_office",
                "material",
                "price",
                "currency",
                "valid_from",
                "valid_to",
                "create_by",
                "create_time",
                "is_expand"
        };
        return columns;
    }

    @Override
    public Map<String, String> columnMaps() {
        Map<String, String> maps = new HashMap<>(3);
        maps.put("sales_org", "sales_org");
        maps.put("sales_office", "sales_office");
        maps.put("material", "material");
        return maps;
    }

    @Override
    public List<String> mapping() {
        return Arrays.asList("material", "sales_org", "sales_office");
    }

    @Override
    public String sql() {
        return "SELECT " + Strings.join(getColumns(), ",") + " FROM " + getTableName() + " WHERE is_effective='1' ";
    }

}
