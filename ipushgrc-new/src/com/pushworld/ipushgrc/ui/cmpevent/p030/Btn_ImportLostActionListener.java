package com.pushworld.ipushgrc.ui.cmpevent.p030;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * �Զ����������ʧ��ť�߼�
 * 
 * @author hm
 * 
 */
public class Btn_ImportLostActionListener implements WLTActionListener {
	BillCardPanel cardPanel = null;
	private HashMap map; // ����ֵ���code�����Ծ�����С��
	private String maxpossible = ""; // ��������
	private String maxfinalost = ""; // ��������ʧ
	private String maxcmplost = ""; // ���Ϲ���ʧ
	private String maxhonorlost = ""; // ���������ʧ

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "���״̬������ִ�д˲�����");
			return;
		}
		onAutoImport();
	}

	/*
	 * �Զ�������յ�����Ӧ�������ʧ��
	 */
	public void onAutoImport() {
		String refrisks = cardPanel.getBillVO().getStringValue("refrisks");
		if (refrisks == null || refrisks.equals("")) {
			MessageBox.show(cardPanel, "��ѡ��[���շ���-" + cardPanel.getTempletItemVO("refrisks").getItemname() + "]");
			return;
		}
		TBUtil tbutil = new TBUtil();
		map = new HashMap();
		HashMap valuemap = new HashMap();
		String refrisk = tbutil.getInCondition(refrisks);
		try { // ������ ������ʧ �Ϲ���ʧ ������ʧ
			HashVO[] refriskItems = UIUtil.getHashVoArrayByDS(null, " select possible, finalost,cmplost,honorlost from cmp_risk where id in(" + refrisk + ")");
			HashVO[] comboboxdict = UIUtil.getHashVoArrayByDS(null, "select id,code,name,seq,type from pub_comboboxdict where type='���տ�����' or type='������ʧ_������ʧ'" + " or type='������ʧ_�Ϲ�����'  or type='������ʧ_����Ӱ��' order by type,seq");
			for (int i = 0; i < comboboxdict.length; i++) {
				if("���տ�����".equals(comboboxdict[i].getStringValue("type"))){
					map.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getIntegerValue("code"));
					valuemap.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getStringValue("id"));
				}else{
					map.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getIntegerValue("id"));
					valuemap.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getStringValue("code"));
				}
			}
			max(refriskItems);
			cardPanel.setValueAt("finalost", new RefItemVO(maxfinalost, "", (String) valuemap.get(maxfinalost+"_������ʧ_������ʧ")));
			cardPanel.setValueAt("serious", new RefItemVO(maxcmplost, "", (String) valuemap.get(maxcmplost+"_������ʧ_�Ϲ�����")));
			cardPanel.setValueAt("honorlost_type", new RefItemVO(maxhonorlost, "",(String) valuemap.get(maxhonorlost+"_������ʧ_����Ӱ��")));

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * �����ֵ��
	 */
	public void max(HashVO[] vo) {
		for (int i = 0; i < vo.length; i++) {
			maxpossible = getMax(vo, i, "���տ�����", "possible", maxpossible);
			maxfinalost = getMax(vo, i, "������ʧ_������ʧ", "finalost", maxfinalost);
			maxcmplost = getMax(vo, i, "������ʧ_�Ϲ�����", "cmplost", maxcmplost);
			maxhonorlost = getMax(vo, i, "������ʧ_����Ӱ��", "honorlost", maxhonorlost);
		}
	}

	/**
	 * �Ƚ�ǰ������ֵ�Ĵ�С
	 * 
	 * @param vo
	 *            ���յ�VO
	 * @param index
	 *            Ŀǰ�ڼ������յ�
	 * @param type
	 *            Ԫ������
	 * @param dbitem
	 *            ���ݿ��е��ֶ�����
	 * @param value
	 *            Ŀǰ���ֵ
	 * @return
	 */
	public String getMax(HashVO[] vo, int index, String type, String dbitem, String value) {
		if (value == null || value.equals("")) {
			value = vo[index].getStringValue(dbitem);
		} else {
			Integer p_1 = (Integer) map.get(vo[index].getStringValue(dbitem) + "_" + type);
			Integer p_2 = (Integer) map.get(value + "_" + type);
			if (p_1 != null && p_2 != null) {
				if (p_1 > p_2) {
					value = vo[index].getStringValue(dbitem);
				}
			}
		}
		return value;
	}
}
