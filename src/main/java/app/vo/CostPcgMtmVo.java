package app.vo;

import com.lenovo.cpm.elasticsearch.rest.vo.BaseVo;
import lombok.Data;

import java.util.Date;

@Data
public class CostPcgMtmVo extends BaseVo {


    private String mtm_no;

    private String sales_org;

    private String sales_office;

    private String cost_type;

    private String cycle;

    private String product_group;

    private String currency_name;

    private String price_unit;
    private Double m1;
    private Double m2;
    private Double m3;
    private Double m4;
    private Double m5;
    private Double m6;
    private Double m7;
    private Double m8;
    private Double m9;
    private Double m10;
    private Double m11;
    private Double m12;
    private Date sys_created_date;

//    private String MTM_NO;
//
//    private String SALES_ORG;
//
//    private String SALES_OFFICE;
//
//    private String COST_TYPE;
//
//    private String CYCLE;
//
//    private String PRODUCT_GROUP;
//
//    private String Currency_Name;
//
//    private String Price_Unit;
//    private Double M1;
//    private Double M2;
//    private Double M3;
//    private Double M4;
//    private Double M5;
//    private Double M6;
//    private Double M7;
//    private Double M8;
//    private Double M9;
//    private Double M10;
//    private Double M11;
//    private Double M12;
//    private Date Sys_Created_Date;

}
