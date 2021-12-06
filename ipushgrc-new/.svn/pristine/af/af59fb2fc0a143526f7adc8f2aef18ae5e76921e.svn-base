package com.pushworld.ipushgrc.ui.wfrisk.p050;

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
 * 以Bom方式查看体系文件与风险! 是重要的亮点界面!
 * A.有两种图,一个是体系视图,一个是机构视图
 * B.由于机构改动比较大，增删改操作都比较频繁，所以机构采取机构名称匹配，比如，查询 法律合规部 应包括 总部法律合规部及各分行法律合规部的数据
 * C.内控五要素可能改动比较小，一般是改个名称，所以根据id匹配
 * @author xch
 *
 */
public class WFAndRiskBomWKPanel extends AbstractWorkPanel implements ChangeListener {
	private JTabbedPane tab;
	private JPanel jPanel2;
	private boolean ifclick2 = false;
	private boolean isSelfCorp = false;//是否查询本机构，默认为查询全部

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		tab = new JTabbedPane();
		jPanel2 = new JPanel();
		BillBomPanel bomPanel = new BillBomPanel("smeom");
		if (!ClientEnvironment.isAdmin()) {
			bomPanel.setEditable(false);//不可编辑,后来觉得管理身份登录还是能编辑方便些!【xch/2012-03-08】
		}
		bomPanel.putClientProperty("BOMTYPE", "PROCESS");
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
			bomPanel.setRiskVO(new WFRiskUIUtil().getHashtableRiskVO("PROCESS", "BLCORPNAME", alldepts, isSelfCorp));
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
		tab.addTab("流程机构视图", bomPanel);
		String str_isShow = this.getMenuConfMapValueAsStr("是否显示体系视图", "Y");//机场财务汪扬要求去掉内控体系，现改为菜单参数【李春娟/2014-10-10】
		if ("Y".equalsIgnoreCase(str_isShow)) {
			tab.addTab("流程体系视图", jPanel2);
			tab.addChangeListener(this);
		}
		this.add(tab);
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			BillBomPanel bomPanel = new BillBomPanel("icmap");
			if (!ClientEnvironment.isAdmin()) {
				bomPanel.setEditable(false);//不可编辑,后来觉得管理身份登录还是能编辑方便些!【xch/2012-03-08】
			}
			bomPanel.putClientProperty("BOMTYPE", "PROCESS");
			try {
				String[] classname = UIUtil.getStringArrayFirstColByDS(null, "select bindclassname from pub_bom_b where bomid=(select id from pub_bom where code ='icmap')");
				if (classname == null || classname.length == 0) {
					MessageBox.show(this, "请检查Bom图的设置条件!");
					return;
				}
				bomPanel.setRiskVO(new WFRiskUIUtil().getHashtableRiskVO("PROCESS", "ICTYPENAME", null, isSelfCorp));
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
			jPanel2.add(bomPanel); //
			ifclick2 = true;
		}
	}
}
