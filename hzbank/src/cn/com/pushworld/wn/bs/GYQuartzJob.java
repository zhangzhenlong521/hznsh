package cn.com.pushworld.wn.bs;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.bs.common.WLTJobTimer;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.GYServerKHPlan;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class GYQuartzJob implements WLTJobIFC {

	private String str;
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd");
			int currentDay = Integer.parseInt(format.format(new Date()));
			GYQuartzJob gy = new GYQuartzJob();
			switch (currentDay) {
			case 1:
			case 8:
			case 9:
			case 15:
			case 22:
				gy.gradeEnd();
				gy.gradeScore();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "�������";
	}

	public void gradeEnd() {
		try {// �������:1.�޸�״̬;2.�����ܷ�
			System.out.println("��Ա����������ֳɹ�");
			HashVO[] vo = dmo
					.getHashVoArrayByDS(
							null,
							"SELECT distinct(USERCODE) AS USERCODE,STATE,PFTIME FROM WN_GYPF_TABLE WHERE STATE='������'");
			if (vo == null || vo.length <= 0) {
				return;
			}
			for (int i = 0; i < vo.length; i++) {
				HashVO v = vo[i];
				gradeEveryOne(v.getStringValue("USERCODE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����ÿ���˵�����
	/**
	 * ������ǰ��Ա������������,�����ǰ����������δ���ֻ�����δ�������ֱ�ӽ�����
	 * 
	 * @param usercode
	 *            :��Ա��
	 */
	public void gradeEveryOne(String usercode) {
		try {
			Double KOUOFEN = 0.0;
			Double result = 0.0;
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"wnSalaryDb.WN_GYPF_TABLE");
			HashVO[] vo = dmo.getHashVoArrayByDS(null,
					"select * from wnSalaryDb.WN_GYPF_TABLE where usercode='"
							+ usercode + "' and state='������' Order by ID");
			List list = new ArrayList<String>();
			for (int i = 0; i < vo.length - 1; i++) {
				String koufenValue = vo[i].getStringValue("KOUOFEN");
				String defenValue = vo[i].getStringValue("FENZHI");
				KOUOFEN = Double.parseDouble(defenValue);
				result = result + KOUOFEN;
				update.setWhereCondition("id='" + vo[i].getStringValue("id")
						+ "'");
				update.putFieldValue("KOUOFEN", KOUOFEN);
				update.putFieldValue("state", "���ֽ���");
				list.add(update.getSQL());
			}
			double sum = Double.parseDouble(vo[vo.length - 1]
					.getStringValue("KOUOFEN"));
			double sumfen = sum - result;
			String sumSQL = "update wnSalaryDb.WN_GYPF_TABLE set KOUOFEN='"
					+ sumfen + "',state='���ֽ���' where usercode='" + usercode
					+ "' and xiangmu='�ܷ�'";
			System.out.println(sumSQL);
			list.add(sumSQL);
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeScore() {
		try {
			WnSalaryServiceImpl service = new WnSalaryServiceImpl();
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			System.out.println("��ʼ���ɴ�ּƻ�");
			str = service.getSqlInsert(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}