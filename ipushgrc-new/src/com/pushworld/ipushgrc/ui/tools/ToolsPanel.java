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
 * ���������
 * @author Gwang
 *
 */
public class ToolsPanel extends AbstractWorkPanel implements ActionListener {

	WLTButton seq_btn = new WLTButton("Seqence ����[��������������Ч]");
	WLTButton rule_btn = new WLTButton("�޸��ƶ�");
	WLTButton getAllCmpFile_btn = new WLTButton("�����ļ�����");
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
		seq_lab.setText("����[tables.xml]����������Sequence. [weblight.xml]�е�[INSTALLAPPS]����tables.xml�ĸ���.");//����tables.xml�б���������pub_sequence�ж�Ӧ��sequence�����/2014-02-21��
		this.add(seq_lab);
		this.add(seq_btn);

		WLTLabel seq_lab2 = new WLTLabel();
		seq_lab2.setText("���ƶ������еĸ����Ƶ��ӱ���,��Ҫ���ӱ��д�����¼,����ʹ�������,ֻ��ִ��һ��.");
		this.add(seq_lab2);
		this.add(rule_btn);
		
		WLTLabel lab3 = new WLTLabel();
		lab3.setText("���ط��������������ļ�                          	                                                    ");
		this.add(lab3);
		this.add(getAllCmpFile_btn);

		seq_btn.addActionListener(this);
		rule_btn.addActionListener(this);
		getAllCmpFile_btn.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == seq_btn) {
			resetSeq(); //Seqence ����
		} else if (e.getSource() == rule_btn) {
			updateRule();
		} else if (e.getSource() == getAllCmpFile_btn) {
			getAllCmpFile();
		}
	}

	private void updateRule() {
		try {
			boolean hasruleitem = TBUtil.getTBUtil().getSysOptionBooleanValue("�ƶ��Ƿ����Ŀ", true);
			if (!hasruleitem) {
				MessageBox.show(this, "�ƶȲ�����Ŀά�����ʲ���Ҫִ�иò�����");
				return;
			}
			getGRCService().removeRuleAttachfileToRuleitem();
			MessageBox.show(this, "�����ɹ�!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "����ʧ��,��鿴����̨!");
		}
	}

	/**
	 * ȡ�úϹ��Ʒ����
	 * @return
	 */
	private IPushGRCServiceIfc getGRCService() throws Exception {
		return (IPushGRCServiceIfc) RemoteServiceFactory.getInstance().lookUpService(IPushGRCServiceIfc.class); //����Զ�̷���
	}

	/**
	 * ȡ��SysAppService
	 * @return
	 */
	private SysAppServiceIfc getSysAppService() throws Exception {
		return (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //����Զ�̷���
	}

	/***
	 * ��ձ�[pub_sequence], Ȼ�����[tables.xml]����������Sequence. 
	 * [weblight.xml]�е�[INSTALLAPPS]����tables.xml�ĸ���, Ҳ����˵����[INSTALLAPPS]�ж���İ������tables.xml�ļ�
	 * �������ֱ�Ӵ����Ĳ�û����tables.xml�ļ��ж���, �Ͳ�������Seq!
	 */
	private void resetSeq() {
		try {
			int n = sysAppServiceIfc.resetAllSequence();
			if (n > 0) {
				MessageBox.show("�ɹ�����Sequence" + n + "��, �������������!");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAllCmpFile() {
		try {			
			getGRCService().outputAllCmpFileByHisWord();
			MessageBox.show(this, "�����ɹ�!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}