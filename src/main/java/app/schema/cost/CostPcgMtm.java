package app.schema.cost;

import app.factory.SchemaInfo;
import joptsimple.internal.Strings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author OAK
 *
 */
@Service
public class CostPcgMtm implements SchemaInfo {

    @Override
    public String getTableName() {
        return "CFE_PCG_MTMSalesOrg_201906";
//        return "CFE_PCG_MTMSalesOrg_201907";
    }

    @Override
    public String [] getColumns() {
        String [] columns = {
                "MTM_NO",
                "SALES_ORG",
                "SALES_OFFICE",
                "COST_TYPE",
                "CYCLE",
                "PRODUCT_GROUP",
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
                "Cycle_Product_Group",
                "Sys_Created_Date",
                "UpdateDate"
        };
        return columns;
    }

    @Override
    public Map<String, String> columnMaps() {
        return Collections.emptyMap();
    }

    @Override
    public List<String> mapping() {
        return Arrays.asList("MTM_NO", "SALES_ORG", "SALES_OFFICE", "COST_TYPE");
    }

    @Override
    public String sql() {
        return "select ID," + Strings.join(getColumns(), ",") + " from " + getTableName() ;
    }

}
