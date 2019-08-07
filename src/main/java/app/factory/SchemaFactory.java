package app.factory;

import app.schema.cost.*;
import app.schema.price.ChannelPrice;
import app.schema.price.ListPriceCto;
import app.schema.price.ListPriceMtm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author OAK
 *
 */
@Service
public class SchemaFactory {

    @Autowired
    private ChannelPrice channelPrice;

    @Autowired
    private ListPriceCto listPriceCto;

    @Autowired
    private ListPriceMtm listPriceMtm;

    @Autowired
    private CostPcgMtm costPcgMtm;

    @Autowired
    private CostMbgMtm costMbgMtm;

    @Autowired
    private CostDcgCto costDcgCto;

    @Autowired
    private CostPcgCto costPcgCto;

    @Autowired
    private CostDcgMtm costDcgMtm;

    public SchemaInfo getSchemaInfo(String key) {
        switch (key) {
            case "channelprice":
                return channelPrice;
            case "listprice_cto_office":
                return listPriceCto;
            case "listprice_mtm_office":
                return listPriceMtm;
            case "cfe_pcg_mtmsalesorg_2": //"CFE_PCG_MTMSalesOrg_201907":
                return costPcgMtm;
            case "CFE_DCG_MTMSalesOrg_201906":
                return costDcgMtm;
            case "CFE_MBG_MTMSalesOrg_201906":
                return costMbgMtm;
            case "CFE_PCG_CTOVKSalesOrg_201906":
                return costPcgCto;
            case "CFE_DCG_CTOVKSalesOrg_201906":
                return costDcgCto;
            default:
                return null;
        }
    }
}
