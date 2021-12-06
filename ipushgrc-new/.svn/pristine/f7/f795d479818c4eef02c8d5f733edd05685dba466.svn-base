package com.pushworld.ipushgrc.ui.risk.p050;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * 风险地图!!!
 * @author xch
 *
 */
public class RiskMapWKPanel extends AbstractWorkPanel implements ChangeListener {

	private JTabbedPane tab; //
	private JPanel jPanel2; //
	private boolean ifclick2 = false;
	private boolean isSelfCorp = false;//是否查询本机构，默认为查询全部

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		tab = new JTabbedPane(); //
		jPanel2 = new JPanel(); //

		BillBomPanel bomPanel = new BillBomPanel("smeom");
		if (!ClientEnvironment.isAdmin()) {
			bomPanel.setEditable(false);//不可编辑
		}
		bomPanel.putClientProperty("BOMTYPE", "RISK");//设置bomtype为风险bom图
		try {
			String[] classname = UIUtil.getStringArrayFirstColByDS(null, "select bindclassname from pub_bom_b where bomid=(select id from pub_bom where code ='smeom')");
			if (classname == null || classname.length == 0) {
				MessageBox.show(this, "请检查Bom图的设置条件!");
				return;
			}
			ArrayList alldepts = new ArrayList();
			for (int i = 0; i < classname.length; i++) {
				String blcorpname = classname[i].substring(classname[i].indexOf("\"") + 1, classname[i].lastIndexOf("\""));
				if (alldepts.contains(blcorpname)) {
					continue;
				}
				alldepts.add(blcorpname);
			}
			String str_isSelfCorp = this.getMenuConfMapValueAsStr("是否查询本机构", "N");
			if ("Y".equalsIgnoreCase(str_isSelfCorp)) {
				isSelfCorp = true;
			}
			bomPanel.setRiskVO(new WFRiskUIUtil().getHashtableRiskVO("RISK", "BLCORPNAME", alldepts, isSelfCorp));
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}

		tab.addTab("风险机构视图", bomPanel); //
		String str_isShow = this.getMenuConfMapValueAsStr("是否显示体系视图", "Y");//机场财务汪扬要求去掉内控体系，现改为菜单参数【李春娟/2014-10-10】
		if ("Y".equalsIgnoreCase(str_isShow)) {
			tab.addTab("风险体系视图", jPanel2); //
			tab.addChangeListener(this); //
		}
		this.add(tab);
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			BillBomPanel bomPanel = new BillBomPanel("icmap");
			if (!ClientEnvironment.isAdmin()) {
				bomPanel.setEditable(false);//不可编辑
			}
			bomPanel.putClientProperty("BOMTYPE", "RISK");//设置bomtype为风险bom图
			try {
				String[] classname = UIUtil.getStringArrayFirstColByDS(null, "select bindclassname from pub_bom_b where bomid=(select id from pub_bom where code ='icmap')");
				if (classname == null || classname.length == 0) {
					MessageBox.show(this, "请检查Bom图的设置条件!");
					return;
				}
				bomPanel.setRiskVO(new WFRiskUIUtil().getHashtableRiskVO("RISK", "ICTYPENAME", null, isSelfCorp));
				jPanel2.add(bomPanel); //
				ifclick2 = true;
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}
}
