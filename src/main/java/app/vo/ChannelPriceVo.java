package app.vo;

import com.lenovo.cpm.elasticsearch.rest.annotations.Document;
import com.lenovo.cpm.elasticsearch.rest.annotations.Field;
import com.lenovo.cpm.elasticsearch.rest.vo.BaseVo;
import lombok.Data;
import org.elasticsearch.index.VersionType;

import java.util.Date;

@Data
@Document(indexName = "channelprice", type = "channelprice", shards = 5, replicas = 1, refreshInterval = "1s", versionType = VersionType.EXTERNAL)
public class ChannelPriceVo extends BaseVo {

    @Field(name = "module")
    private String module;

    @Field(name = "condition_type")
    private  String conditionType;

    @Field(name = "condition_table")
    private  String conditionTable;

    @Field(name = "sales_org")
    private  String salesOrg;

    @Field(name = "sales_office")
    private  String salesOffice;

    @Field(name = "distribution_channel")
    private  String distributionChannel;

    @Field(name = "customer")
    private  String customer;

    @Field(name = "customer_price_group")
    private  String  customerPriceGroup;

    @Field(name = "country")
    private  String country;

    @Field(name = "material")
    private  String material;

    @Field(name = "price")
    private  Double price;

    @Field(name = "currency")
    private String currency;

    @Field(name = "valid_from")
    private  String validFrom;

    @Field(name = "valid_to")
    private  String validTo;

    @Field(name = "create_by")
    private  String createBy;

    @Field(name = "create_time")
    private  Date createTime;

    @Field(name = "split_key")
    private  String splitKey;

    @Field(name = "is_expand")
    private  String isExpand;

}
