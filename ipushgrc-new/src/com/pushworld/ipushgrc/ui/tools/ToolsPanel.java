package com.pushworld.ipushgrc.ui.tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 工具箱面版
 * @author Gwang
 *
 */
public class ToolsPanel extends AbstractWorkPanel implements ActionListener {

	WLTButton seq_btn = new WLTButton("Seqence 重置[必须重启服务生效]");
	WLTButton rule_btn = new WLTButton("修复制度");
	WLTButton getAllCmpFile_btn = new WLTButton("流程文件发布");
	private SysAppServiceIfc sysAppServiceIfc = null;

	@Override
	public void initialize() {

		try {
			sysAppServiceIfc = this.getSysAppService();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		WLTLabel seq_lab = new WLTLabel();
		seq_lab.setText("遍历[tables.xml]并反向生成Sequence. [weblight.xml]中的[INSTALLAPPS]决定tables.xml的个数.");//根据tables.xml中表名来更新pub_sequence中对应的sequence【李春娟/2014-02-21】
		this.add(seq_lab);
		this.add(seq_btn);

		WLTLabel seq_lab2 = new WLTLabel();
		seq_lab2.setText("将制度主表中的附件移到子表中,需要在子表中创建记录,正常使用情况下,只能执行一次.");
		this.add(seq_lab2);
		this.add(rule_btn);
		
		WLTLabel lab3 = new WLTLabel();
		lab3.setText("下载发布的所有流程文件                          	                                                    ");
		this.add(lab3);
		this.add(getAllCmpFile_btn);

		seq_btn.addActionListener(this);
		rule_btn.addActionListener(this);
		getAllCmpFile_btn.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == seq_btn) {
			resetSeq(); //Seqence 重置
		} else if (e.getSource() == rule_btn) {
			updateRule();
		} else if (e.getSource() == getAllCmpFile_btn) {
			getAllCmpFile();
		}
	}

	private void updateRule() {
		try {
			boolean hasruleitem = TBUtil.getTBUtil().getSysOptionBooleanValue("制度是否分条目", true);
			if (!hasruleitem) {
				MessageBox.show(this, "制度不分条目维护，故不需要执行该操作！");
				return;
			}
			getGRCService().removeRuleAttachfileToRuleitem();
			MessageBox.show(this, "操作成功!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "操作失败,请查看控制台!");
		}
	}

	/**
	 * 取得合规产品服务
	 * @return
	 */
	private IPushGRCServiceIfc getGRCService() throws Exception {
		return (IPushGRCServiceIfc) RemoteServiceFactory.getInstance().lookUpService(IPushGRCServiceIfc.class); //定义远程服务
	}

	/**
	 * 取得SysAppService
	 * @return
	 */
	private SysAppServiceIfc getSysAppService() throws Exception {
		return (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //定义远程服务
	}

	/***
	 * 清空表[pub_sequence], 然后遍历[tables.xml]并反向生成Sequence. 
	 * [weblight.xml]中的[INSTALLAPPS]决定tables.xml的个数, 也就是说会找[INSTALLAPPS]中定义的包下面的tables.xml文件
	 * 如果表是直接创建的并没有在tables.xml文件中定义, 就不能生成Seq!
	 */
	private void resetSeq() {
		try {
			int n = sysAppServiceIfc.resetAllSequence();
			if (n > 0) {
				MessageBox.show("成功生成Sequence" + n + "个, 请立即重起服务!");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAllCmpFile() {
		try {			
			getGRCService().outputAllCmpFileByHisWord();
			MessageBox.show(this, "操作成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
