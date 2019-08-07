package app.vo;

import com.lenovo.cpm.elasticsearch.rest.annotations.Field;
import com.lenovo.cpm.elasticsearch.rest.vo.BaseVo;
import lombok.Data;

import java.util.Date;

@Data
public class ListPriceCtoVo extends BaseVo {

    @Field(name = "module")
    private String module;

    @Field(name = "condition_type")
    private String conditionType;

    @Field(name = "condition_table")
    private String conditionTable;

    @Field(name = "sales_org")
    private String salesOrg;

    @Field(name = "sales_office")
    private String salesOffice;

    @Field(name = "material")
    private String material;

    @Field(name = "price")
    private Double price;

    @Field(name = "currency")
    private String currency;

    @Field(name = "valid_from")
    private Date validFrom;

    @Field(name = "valid_to")
    private String validTo;

    @Field(name = "create_by")
    private String  createBy;

    @Field(name = "create_time")
    private Date createTime;

    @Field(name = "is_expand")
    private String  isExpand;


}
