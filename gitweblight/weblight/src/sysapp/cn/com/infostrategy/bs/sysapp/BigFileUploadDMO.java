package cn.com.infostrategy.bs.sysapp;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.RemoteCallServlet;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.sysapp.other.BIgFileUploadIfc;
import org.apache.log4j.Logger;

import java.util.List;

public class BigFileUploadDMO implements BIgFileUploadIfc {
    private CommDMO commDMO = null; //
    private TBUtil tbUtil = null; //
    private Logger logger = WLTLogger.getLogger(BigFileUploadDMO.class);
    @Override
    public int upLoad(List list) {
        int count=0;
        try {
            getCommDMO().executeBatchByDS(null,list);
            count++;
            logger.info("<<<<<<<<<<<<<count="+count+">>>>>>"+Thread.currentThread().getId());
        } catch (Exception e) {
            count=0;
            e.printStackTrace();
        }
        return count;
    }
    private CommDMO getCommDMO() {
        if (commDMO != null) {
            return commDMO; //
        }
        commDMO = new CommDMO(); //
        return commDMO;
    }

    private TBUtil getTBUtil() {
        if (tbUtil != null) {
            return tbUtil;
        }
        tbUtil = new TBUtil(); //
        return tbUtil;
    }
}
