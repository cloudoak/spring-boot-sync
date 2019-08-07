package app.schema.cost;

import app.factory.SchemaInfo;
import joptsimple.internal.Strings;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @author OAK
 *
 */
@Service
public class CostPcgCto implements SchemaInfo {

    @Override
    public String getTableName() {
        return "CFE_PCG_CTOVKSalesOrg_201906";
    }

    String [] columns = {
        "Variant",
        "COST_TYPE",
        "PRODUCT_GROUP",
        "Sales_Org",
        "Sales_Office",
        "CYCLE",
        "CYCLE_PRODUCT_GROUP",
        "Currency_Name",
        "Price_Unit",
        "Pre_M1",
        "Pre_M2",
        "Pre_M3",
        "Pre_M4",
        "Pre_M5",
        "Pre_M6",
        "Pre_M7",
        "Pre_M8",
        "Pre_M9",
        "Pre_M10",
        "Pre_M11",
        "Pre_M12",
        "M1",
        "M2",
        "M3",
        "M4",
        "M5",
        "M6",
        "M7",
        "M8",
        "M9",
        "M10",
        "M11",
        "M12",
        "Sys_Created_date",
        "UpdateDate"
    };



    @Override
    public String [] getColumns() {
        String [] c = new String[columns.length + 1];
        System.arraycopy(columns, 0, c, 0, columns.length - 1);
        c[c.length - 1] = "phcode";
        return c;
    }

    @Override
    public Map<String, String> columnMaps() {
        Map<String, String> maps = new HashMap<>(2);
        maps.put("sales_org", "SALES_ORG");
        maps.put("sales_office", "SALES_OFFICE");
        return maps;
    }

    @Override
    public List<String> mapping() {
        return Arrays.asList("Variant", "COST_TYPE", "PRODUCT_GROUP", "Sales_Org", "Sales_Office", "phcode");
    }

    @Override
    public String sql() {
        return "select " + Strings.join(columns, ",") +
                ", CONCAT(PH1,PH2,PH3,PH4) as phcode " + " from " + getTableName();
    }

}
