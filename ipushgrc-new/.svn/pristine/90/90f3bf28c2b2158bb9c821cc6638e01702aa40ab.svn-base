package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditPanel;

public class CmpfileAndWFGraphFrame extends BillFrame implements ChangeListener {
	private String cmpfile_id;//当前流程文件id
	private JTabbedPane tab;//流程文件和流程页签
	private JPanel jPanel2;//流程页签
	private JPanel jPanel3;//word浏览页签
	private BillOfficePanel officePanel;
	private boolean ifclick2 = false;//是否点击了流程页签
	private boolean ifclick3 = false;//是否点击了word页签
	private BillCardPanel cardpanel_cmpfile;//流程文件卡片面板
	private WFGraphEditPanel wfpanel;
	private String showprocessid;//直接显示风险所在的流程或某个流程
	private String tabcount = "0";//文件查看页签数，如果代码设置为1、2、3，分别表示显示文件、显示文件和流程、显示文件和流程和word，除此之外的数值则根据平台参数判断【李春娟/2015-02-11】

	public CmpfileAndWFGraphFrame(Container _parentContainer, String _title, String _cmpfileid) {
		this(_parentContainer, _title, _cmpfileid, 1000, 700);
	}

	public CmpfileAndWFGraphFrame(Container _parentContainer, String _title, String _cmpfileid, String _tabcount) {
		super(_parentContainer, _title, 1000, 700);
		this.cmpfile_id = _cmpfileid;
		this.tabcount = _tabcount;
		initialize();
	}

	public CmpfileAndWFGraphFrame(Container _parentContainer, String _title, String _cmpfileid, int _width, int _height) {
		super(_parentContainer, _title, _width, _height);
		this.cmpfile_id = _cmpfileid;
		initialize();
	}

	public void setShowprocessid(String showprocessid) {
		this.showprocessid = showprocessid;
	}

	public BillCardPanel getCardpanel_cmpfile() {//或得流程文件卡片面板【李春娟/2014-03-03】
		return cardpanel_cmpfile;
	}

	public void initialize() {
		tab = new JTabbedPane();
		tab.addChangeListener(this);
		jPanel2 = new JPanel();
		jPanel3 = new JPanel();
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "文件/流程查看", "CMP_CMPFILE_CODE5");//只能维护本部门的流程文件【李春娟/2012-07-13】
		cardpanel_cmpfile = new BillCardPanel(templetcode);//修改了模板，因第二个页签流程图需要将本窗口大小设置大些比较好，第一个页流程文件为了居中显示，新增了这个模板【李春娟/2012-03-28】
		tab.addTab("文件", cardpanel_cmpfile);//文件是必须有的
		cardpanel_cmpfile.queryDataByCondition("id=" + cmpfile_id);
		if ("1".equals(this.tabcount)) {//因为默认有文件，故配置为显示1个页签，则啥也不做
		} else if ("2".equals(this.tabcount)) {
			tab.addTab("流程", jPanel2);
			boolean isShowFile = new TBUtil().getSysOptionBooleanValue("查看文件和流程窗口是否默认显示文件", true);//点击流程文件名称链接，打开的查看文件和流程窗口，默认直接显示文件，但一汽中要求直接显示流程，故增加此参数。【李春娟/2012-05-21】
			if (!isShowFile) {
				tab.setSelectedIndex(1);//	
			}
		} else if ("3".equals(this.tabcount)) {
			tab.addTab("流程", jPanel2);
			tab.addTab("word浏览", jPanel3);
			int openIndex = TBUtil.getTBUtil().getSysOptionIntegerValue("流程文件名称链接默认打开页签", 0);//【李春娟/2015-02-11】
			if (openIndex > 0 && openIndex < 4) {//只能设置为1、2、3
				tab.setSelectedIndex(openIndex - 1);//	
			} else {
				boolean isShowFile = new TBUtil().getSysOptionBooleanValue("查看文件和流程窗口是否默认显示文件", true);//点击流程文件名称链接，打开的查看文件和流程窗口，默认直接显示文件，但一汽中要求直接显示流程，故增加此参数。【李春娟/2012-05-21】
				if (!isShowFile) {
					tab.setSelectedIndex(1);//	
				}
			}
		} else {
			tab.addTab("流程", jPanel2);
			int openIndex = TBUtil.getTBUtil().getSysOptionIntegerValue("流程文件名称链接默认打开页签", 0);//【李春娟/2015-02-11】
			//如果设置了“流程文件名称链接默认打开页签”，并且参数正确（只能为1、2、3，分别表示第几个页签），则“查看文件和流程窗口是否默认显示文件”参数失效，
			//反之，则不显示“word浏览”页签，“查看文件和流程窗口是否默认显示文件”参数生效【李春娟/2015-02-11】
			if (openIndex > 0 && openIndex < 4) {//只能设置为1、2、3
				tab.addTab("word浏览", jPanel3);
				tab.setSelectedIndex(openIndex - 1);//	
			} else {
				boolean isShowFile = new TBUtil().getSysOptionBooleanValue("查看文件和流程窗口是否默认显示文件", true);//点击流程文件名称链接，打开的查看文件和流程窗口，默认直接显示文件，但一汽中要求直接显示流程，故增加此参数。【李春娟/2012-05-21】
				if (!isShowFile) {
					tab.setSelectedIndex(1);//	
				}
			}
		}
		this.getContentPane().add(tab);
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			String[][] processes = null;
			try {
				processes = UIUtil.getStringArrayByDS(null, "select process.id,process.code,process.name,cmpfile.cmpfilename from pub_wf_process process ,cmp_cmpfile cmpfile where cmpfile.id=process.cmpfileid and cmpfile.id =" + cmpfile_id + " order by process.userdef04, process.id");
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
			jPanel2.removeAll();
			if (processes == null || processes.length == 0) {// 如果风险点直接关联流程文件，而该文件又没有流程
				JLabel label = new JLabel(" 该文件无流程!");//【李春娟/2015-02-11】
				label.setForeground(Color.RED);
				jPanel2.add(label);
				return;
			} else {
				wfpanel = new WFGraphEditPanel(this, cmpfile_id, processes[0][3], processes, false);//不需要传入主管部门了，在新增或编辑时直接查询比较好【李春娟/2012-03-19】
				if (this.showprocessid != null) {
					wfpanel.showLevel(showprocessid);
				} else {
					wfpanel.showLevel(processes[0][0]);
				}
				jPanel2.add(wfpanel);
			}
			ifclick2 = true;
		} else if (tab.getSelectedIndex() == 2 && !ifclick3) {
			jPanel3.setLayout(new BorderLayout());
			String filename;
			try {
				IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				filename = server.createCmpfileByHistWord(cmpfile_id);
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
				return;
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			if (filename == null) {
				JLabel label = new JLabel(" 该文件无版本,需发布才能查看!");
				label.setForeground(Color.RED);
				jPanel3.add(label);
			} else {
				OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //
				officeVO.setIfshowsave(false);
				officeVO.setIfshowprint_all(false);
				officeVO.setIfshowprint_fen(false);
				officeVO.setIfshowprint_tao(false);
				officeVO.setIfshowedit(false);
				officeVO.setToolbar(false);
				officeVO.setIfshowclose(false);
				officeVO.setPrintable(false);
				officeVO.setMenubar(false);
				officeVO.setMenutoolbar(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setTitlebar(false);
				officeVO.setIfshowprint(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setIfshowshowcomment(false);
				officeVO.setIfshowacceptedit(false);
				officeVO.setIfshowshowedit(false);
				officeVO.setIfshowhideedit(false);
				officeVO.setIfshowwater(false);
				officeVO.setIfShowResult(false); //不显示结果区域显示。
				officeVO.setIfselfdesc(true); //关键
				officeVO.setSubdir("officecompfile/word");
				officeVO.setAbsoluteSeverDir(false);
				officePanel = new BillOfficePanel(filename, officeVO); //使用Office面板打开,因为后来遇到需
				jPanel3.add(officePanel);
			}
			ifclick3 = true;
		}
	}

	public boolean beforeWindowClosed() {
		if (officePanel != null && officePanel.getWebBrowser() != null) {
			officePanel.getWebBrowser().executeScript("swingCall('closedoc');");
		}
		return true;
	}
}
