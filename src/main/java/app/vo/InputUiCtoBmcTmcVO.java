package app.vo;

import com.lenovo.cpm.elasticsearch.rest.annotations.Field;

import java.io.Serializable;

/**
 * <p>
 * Cfe-Mtm-Bmc vo
 * </p>
 *
 * @author yanlm
 * @date 2019/04/10
 */
public class InputUiCtoBmcTmcVO implements Serializable {
    private static final long serialVersionUID = 2558526234423036003L;

    @Field(name = "phcode")
    private String phCode;
    @Field(name = "Sales_Org")
    private String salesOrg;
    @Field(name = "Sales_Office")
    private String salesOffice;
    /** PCG/DCG/MBG **/
    private String productGroup;
    /** bmc/tmc **/
    @Field(name = "COST_TYPE")
    private String costType;
    /** 版本 **/
    private String cycle;

    /** 当前页*/
    private Integer page;

    /** 每页显示条数*/
    private Integer pageSize;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPhCode() {
        return phCode;
    }

    public void setPhCode(String phCode) {
        this.phCode = phCode;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }


    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public String getSalesOffice() {
        return salesOffice;
    }

    public void setSalesOffice(String salesOffice) {
        this.salesOffice = salesOffice;
    }
}
