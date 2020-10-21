package cn.com.pushworld.wn.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.UIUtil;

public class HistoryPersonRecord implements WLTJobIFC {
	
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		String historyTime=getSYYCMonth();
		String[] existsDate = dmo.getStringArrayFirstColByDS(null, "SELECT 1 FROM historyryb WHERE CREATETIME='"+historyTime+"'");
		if(existsDate.length>0){
			dmo.executeUpdateByDS(null,
					"delete from historyryb where createtime='"
							+ historyTime + "'");
		}else{
			InsertSQLBuilder insert = new InsertSQLBuilder("historyryb");
			HashVO[] staffInfos = dmo.getHashVoArrayByDS(null,
					"select * from v_sal_personinfo where 1=1");
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < staffInfos.length;i++ ){
				insert.putFieldValue("CODE", staffInfos[i].getStringValue("CODE"));
				insert.putFieldValue("NAME", staffInfos[i].getStringValue("NAME"));
				insert.putFieldValue("STATIONRATIO", staffInfos[i].getStringValue("STATIONRATIO"));
				insert.putFieldValue("ISUNCHECK", staffInfos[i].getStringValue("ISUNCHECK"));
				insert.putFieldValue("DEPTNAME", staffInfos[i].getStringValue("DEPTNAME"));
				insert.putFieldValue("MAINSTATION", staffInfos[i].getStringValue("MAINSTATION"));
				insert.putFieldValue("DEPTCODE", staffInfos[i].getStringValue("DEPTCODE"));
				insert.putFieldValue("CREATETIME", historyTime);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
		}
		return "计算成功！";
	}
	
	/**
	 * 获取上月时间
	 * @return fj 2020年5月19日18:11:14
	 */
	private String getSYYCMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(otherDate);
	}

}
