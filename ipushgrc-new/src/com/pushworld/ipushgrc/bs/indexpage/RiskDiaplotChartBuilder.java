package com.pushworld.ipushgrc.bs.indexpage;

import java.text.DecimalFormat;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * �����Ǳ���ͳ��!!!
 * ��Ҫȡ������ʵ���߼�!!!
 * @author xch
 *
 */
public class RiskDiaplotChartBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String userCode) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO[] hvs = new HashVO[2]; //

		hvs[0] = new HashVO(); //
		HashVO[] eventVO = dmo.getHashVoArrayByDS(null, "select id,finddate,happendate from cmp_event where happendate like '" + new TBUtil().getCurrDate().substring(0, 4) + "%'");//ȡ���귢�����¼�
		CommonDate finddate = null;
		CommonDate happendate = null;
		int days = 0;
		for (int i = 0; i < eventVO.length; i++) {
			finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
			happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
			days += CommonDate.getDaysBetween(happendate, finddate);
		}
		DecimalFormat df = new DecimalFormat("0");
		String eventDate = "0";
		if (eventVO.length != 0) {
			eventDate = df.format((double) days / eventVO.length);// ʱ���� ʵ��ֵ��ƽ��ֵ��
		}

		hvs[0].setAttributeValue("����", "Υ���¼����ּ�ʱ��"); //Υ���¼����ּ�ʱ�ʡ����/2012-03-30��
		hvs[0].setAttributeValue("X��", "����"); //
		hvs[0].setAttributeValue("ʵ��ֵ", eventDate); //
		hvs[0].setAttributeValue("��Сֵ", 0); //
		hvs[0].setAttributeValue("����ֵ", 30); //
		hvs[0].setAttributeValue("����ֵ", 90); //
		hvs[0].setAttributeValue("���ֵ", 150); //
		hvs[0].setAttributeValue("����ɫ", "FFBD9D");
		hvs[0].setAttributeValue("��ʾ", "����ʱ���뷢��ʱ�����������ƽ����"); //

		hvs[1] = new HashVO(); //
		String[] str_counts = dmo.getStringArrayFirstColByDS(null, "select count(id) from v_risk_process_file where filestate='3' and ctrlfneffect like '%��Ч%' union all select count(id) from v_risk_process_file where filestate='3'");
		String percent = "0";
		if (str_counts.length != 0) {
			df = new DecimalFormat("0.0");
			percent = df.format(100 - Double.parseDouble(str_counts[0]) * 100 / Double.parseDouble(str_counts[1]));//���տ�����	
		}
		hvs[1].setAttributeValue("����", "����ʧ����"); //���ҳ����з��յ�����!! ���ҳ���������Ϊ���ƻ�����Ч�Ϳ�����Ч������!! �����������,���ɰٷֱ�!!
		hvs[1].setAttributeValue("X��", "�ٷֱ�"); //
		hvs[1].setAttributeValue("ʵ��ֵ", percent); //
		hvs[1].setAttributeValue("��Сֵ", 0); //
		hvs[1].setAttributeValue("����ֵ", 50); //
		hvs[1].setAttributeValue("����ֵ", 80); //
		hvs[1].setAttributeValue("���ֵ", 100); //
		hvs[1].setAttributeValue("����ɫ", "62FFFF"); //
		hvs[1].setAttributeValue("��ʾ", "�����ʵ��ķ��յ����������з��յ��е�ռ��"); //

		return hvs;
	}
}
