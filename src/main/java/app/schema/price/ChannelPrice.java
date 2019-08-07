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
public class ChannelPrice implements SchemaInfo {

    @Override
    public String getTableName() {
        return "channelprice";
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
            "distribution_channel",
            "customer",
            "customer_price_group",
            "country",
            "incoterm",
            "shipping_condition",
            "currency",
            "price",
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
        Map<String, String> maps = new HashMap<>(9);
        maps.put("sales_org", "sales_org");
        maps.put("sales_office", "sales_office");
        maps.put("material", "material");
        maps.put("distribution_channel", "distribution_channel");
        maps.put("customer", "customer");
        maps.put("customer_price_group", "customer_price_group");
        maps.put("country", "country");
        maps.put("incoterm", "incoterm");
        maps.put("shipping_condition", "shipping_condition");
        return maps;
    }

    @Override
    public List<String> mapping() {
        return Arrays.asList("sales_org", "sales_office", "material", "distribution_channel",
                "customer", "customer_price_group", "country", "incoterm", "shipping_condition");
    }

    @Override
    public String sql() {
        return "SELECT "+ Strings.join(getColumns(), ",") + " FROM " + getTableName() + " WHERE is_effective='1' ";
    }
}
