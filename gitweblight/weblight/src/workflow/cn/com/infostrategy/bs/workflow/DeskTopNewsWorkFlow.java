package cn.com.infostrategy.bs.workflow;

import java.awt.Color;
import java.awt.Font;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;

/**
 * ��������ʾ������,����������!!
 * @author xch
 *
 */
public class DeskTopNewsWorkFlow implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String userCode) throws Exception {
		CommDMO dmo = new CommDMO(); //
		String str_currtime = new TBUtil().getCurrTime(); //��ǰʱ��
		String str_userid = dmo.getCurrSession().getLoginUserId(); //
		String str_sql_1 = "select id,prdealpoolid,prinstanceid,parentinstanceid,rootinstanceid,templetcode,tabname,pkvalue,msg,createtime,islookat,prioritylevel,dealtimelimit,isccto from pub_task_deal where dealuser='" + str_userid + "' order by createtime desc"; //

		HashVO[] hvs = dmo.getHashVoArrayByDS(null, str_sql_1); //
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("msg"); //
			if (hvs[i].getStringValue("prioritylevel") != null) {
				if (hvs[i].getStringValue("prioritylevel").equals("����")) {
					hvs[i].setUserObject("foreground", new Color(255, 128, 64)); //
					hvs[i].setUserObject("icon", "office_090.gif"); //
				} else if (hvs[i].getStringValue("prioritylevel").equals("�ǳ�����")) {
					hvs[i].setUserObject("foreground", Color.RED); //
					hvs[i].setUserObject("icon", "office_090.gif"); //
				} else {
					hvs[i].setUserObject("foreground", Color.BLACK); //
				}
			} else {
				hvs[i].setUserObject("foreground", Color.BLACK); //
			}

			if (hvs[i].getStringValue("islookat") == null || hvs[i].getStringValue("islookat").equals("N")) {
				hvs[i].setUserObject("font", new Font(LookAndFeel.font.getName(), Font.BOLD, LookAndFeel.font.getSize())); //�ѹ���
			} else {
				if (hvs[i].getStringValue("dealtimelimit") != null) {
					if (hvs[i].getStringValue("dealtimelimit").compareTo(str_currtime) < 0) { //�����ǰʱ���ѳ����涨ʱ��
						hvs[i].setUserObject("font", new Font(LookAndFeel.font.getName(), Font.ITALIC, LookAndFeel.font.getSize())); //�ѹ���
					} else {
						hvs[i].setUserObject("font", LookAndFeel.font); //
					}
				}
			}
		}
		return hvs;
	}
}
