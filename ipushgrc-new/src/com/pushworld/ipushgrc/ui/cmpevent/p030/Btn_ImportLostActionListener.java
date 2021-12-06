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
 * 自动导入风险损失按钮逻辑
 * 
 * @author hm
 * 
 */
public class Btn_ImportLostActionListener implements WLTActionListener {
	BillCardPanel cardPanel = null;
	private HashMap map; // 存放字典中code。可以决定大小。
	private String maxpossible = ""; // 最大可能性
	private String maxfinalost = ""; // 最大财务损失
	private String maxcmplost = ""; // 最大合规损失
	private String maxhonorlost = ""; // 最大声誉损失

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "浏览状态不允许执行此操作！");
			return;
		}
		onAutoImport();
	}

	/*
	 * 自动导入风险点所对应的最大损失。
	 */
	public void onAutoImport() {
		String refrisks = cardPanel.getBillVO().getStringValue("refrisks");
		if (refrisks == null || refrisks.equals("")) {
			MessageBox.show(cardPanel, "请选择[风险分析-" + cardPanel.getTempletItemVO("refrisks").getItemname() + "]");
			return;
		}
		TBUtil tbutil = new TBUtil();
		map = new HashMap();
		HashMap valuemap = new HashMap();
		String refrisk = tbutil.getInCondition(refrisks);
		try { // 可能性 财务损失 合规损失 声誉损失
			HashVO[] refriskItems = UIUtil.getHashVoArrayByDS(null, " select possible, finalost,cmplost,honorlost from cmp_risk where id in(" + refrisk + ")");
			HashVO[] comboboxdict = UIUtil.getHashVoArrayByDS(null, "select id,code,name,seq,type from pub_comboboxdict where type='风险可能性' or type='风险损失_财务损失'" + " or type='风险损失_合规因素'  or type='风险损失_声誉影响' order by type,seq");
			for (int i = 0; i < comboboxdict.length; i++) {
				if("风险可能性".equals(comboboxdict[i].getStringValue("type"))){
					map.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getIntegerValue("code"));
					valuemap.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getStringValue("id"));
				}else{
					map.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getIntegerValue("id"));
					valuemap.put(comboboxdict[i].getStringValue("id") + "_" + comboboxdict[i].getStringValue("type"), comboboxdict[i].getStringValue("code"));
				}
			}
			max(refriskItems);
			cardPanel.setValueAt("finalost", new RefItemVO(maxfinalost, "", (String) valuemap.get(maxfinalost+"_风险损失_财务损失")));
			cardPanel.setValueAt("serious", new RefItemVO(maxcmplost, "", (String) valuemap.get(maxcmplost+"_风险损失_合规因素")));
			cardPanel.setValueAt("honorlost_type", new RefItemVO(maxhonorlost, "",(String) valuemap.get(maxhonorlost+"_风险损失_声誉影响")));

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 求最大值。
	 */
	public void max(HashVO[] vo) {
		for (int i = 0; i < vo.length; i++) {
			maxpossible = getMax(vo, i, "风险可能性", "possible", maxpossible);
			maxfinalost = getMax(vo, i, "风险损失_财务损失", "finalost", maxfinalost);
			maxcmplost = getMax(vo, i, "风险损失_合规因素", "cmplost", maxcmplost);
			maxhonorlost = getMax(vo, i, "风险损失_声誉影响", "honorlost", maxhonorlost);
		}
	}

	/**
	 * 比较前后两个值的大小
	 * 
	 * @param vo
	 *            风险点VO
	 * @param index
	 *            目前第几个风险点
	 * @param type
	 *            元素类型
	 * @param dbitem
	 *            数据库中的字段名称
	 * @param value
	 *            目前最大值
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
