package app.vo;

import com.lenovo.cpm.elasticsearch.rest.annotations.Field;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * ui - cfe-cto-bmc
 * </p>
 *
 * @author yanlm
 * @date 2019/04/10
 */
public class OutputUiCtoBmcVO implements Serializable {
    private static final long serialVersionUID = 8106006450158153844L;

    @Field(name = "Variant")
    private String variant;
    @Field(name = "SALES_ORG")
    private String salesOrg;
    @Field(name = "SALES_OFFICE")
    private String salesOffice;
    @Field(name = "COST_TYPE")
    private String costType;
    @Field(name = "phcode")
    private String phCode;
    @Field(name = "CYCLE")
    private String cycle;
    @Field(name = "M1")
    private BigDecimal m1;
    @Field(name = "M2")
    private BigDecimal m2;
    @Field(name = "M3")
    private BigDecimal m3;
    @Field(name = "M4")
    private BigDecimal m4;
    @Field(name = "M5")
    private BigDecimal m5;
    @Field(name = "M6")
    private BigDecimal m6;
    @Field(name = "M7")
    private BigDecimal m7;
    @Field(name = "M8")
    private BigDecimal m8;
    @Field(name = "M9")
    private BigDecimal m9;
    @Field(name = "M10")
    private BigDecimal m10;
    @Field(name = "M11")
    private BigDecimal m11;
    @Field(name = "M12")
    private BigDecimal m12;

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
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

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getPhCode() {
        return phCode;
    }

    public void setPhCode(String phCode) {
        this.phCode = phCode;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public BigDecimal getM1() {
        return m1;
    }

    public void setM1(BigDecimal m1) {
        this.m1 = m1;
    }

    public BigDecimal getM2() {
        return m2;
    }

    public void setM2(BigDecimal m2) {
        this.m2 = m2;
    }

    public BigDecimal getM3() {
        return m3;
    }

    public void setM3(BigDecimal m3) {
        this.m3 = m3;
    }

    public BigDecimal getM4() {
        return m4;
    }

    public void setM4(BigDecimal m4) {
        this.m4 = m4;
    }

    public BigDecimal getM5() {
        return m5;
    }

    public void setM5(BigDecimal m5) {
        this.m5 = m5;
    }

    public BigDecimal getM6() {
        return m6;
    }

    public void setM6(BigDecimal m6) {
        this.m6 = m6;
    }

    public BigDecimal getM7() {
        return m7;
    }

    public void setM7(BigDecimal m7) {
        this.m7 = m7;
    }

    public BigDecimal getM8() {
        return m8;
    }

    public void setM8(BigDecimal m8) {
        this.m8 = m8;
    }

    public BigDecimal getM9() {
        return m9;
    }

    public void setM9(BigDecimal m9) {
        this.m9 = m9;
    }

    public BigDecimal getM10() {
        return m10;
    }

    public void setM10(BigDecimal m10) {
        this.m10 = m10;
    }

    public BigDecimal getM11() {
        return m11;
    }

    public void setM11(BigDecimal m11) {
        this.m11 = m11;
    }

    public BigDecimal getM12() {
        return m12;
    }

    public void setM12(BigDecimal m12) {
        this.m12 = m12;
    }

}
