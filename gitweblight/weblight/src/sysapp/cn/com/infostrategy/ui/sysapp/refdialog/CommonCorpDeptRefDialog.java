package cn.com.infostrategy.ui.sysapp.refdialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.sysapp.corpdept.CorpDeptBillTreePanel;

/**
 * ͨ�õĻ�������,����ͨ��ʱ�����������Ҫ��������ѯ����!!�������߶������������õ�!
 * ���������ѯ��������ͨ��������ʱ����й��˵�!!!����������Ҫ!!!
 * @author xch
 *
 */
public class CommonCorpDeptRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private HashMap parMaps = new HashMap(); //����ת�Ͳ���!!

	public CommonCorpDeptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	public CommonCorpDeptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _parDefineStr) {
		super(_parent, _title, refItemVO, panel); //
		parMaps.putAll(new TBUtil().parseStrAsMap(_parDefineStr)); //
	}

	private final String STR_SIMPLE = "ֱ��ѡ��";
	private final String STR_COMPLEX = "����ѡ��";

	private WLTRadioPane radioPanel = null;

	private WLTButton btn_allarea, btn_confirm, btn_cancel; //ȷ����ȡ����ť
	private CorpDeptBillTreePanel billTreePanel_Corp_simple = null; //
	private CorpDeptBillTreePanel billTreePanel_Corp = null;

	private JCheckBox rb_zhonghbm, rb_fengh, rb_shiyb, rb_fenghbm, rb_zhih, rb_shiybfb; //���в���,����,��ҵ��,���в���,֧��,��ҵ���ֲ�.
	private JCheckBox rb_zhonghbm_sub, rb_zhongh, rb_shiyb_sub, rb_fenghbm_sub, rb_zhih_sub, rb_shiybfb_sub; //���в���,����,��ҵ��,���в���,֧��,��ҵ���ֲ�.

	private WLTButton btn_selAllType, btn_selAllRecord; //ȫѡ,ȫ��
	private BillListPanel billListPanel_Corp = null; //ѡ�еĻ���

	private RefItemVO returnRefItemVO = null; //

	@Override
	public void initialize() {
		//
		radioPanel = new WLTRadioPane();
		billTreePanel_Corp_simple = getBillTreeWithChecked(true);
		radioPanel.addTab(STR_SIMPLE, billTreePanel_Corp_simple);
		radioPanel.addTab(STR_COMPLEX, getSplitPanel());

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(radioPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

		if (!billTreePanel_Corp_simple.isCustConditionIsNull()) { //�����Ϊ��,��Ҫ�������з�Χ�İ�ť
			btn_allarea.setVisible(false); //
		}
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	/**
	 * ����һ�û�����
	 * @param canCheck �趨���ڵ�ǰ�Ƿ����ѡ��
	 * @return
	 */
	private CorpDeptBillTreePanel getBillTreeWithChecked(boolean canCheck) {
		CorpDeptBillTreePanel billTreePanel = new CorpDeptBillTreePanel(3, true); //������...
		billTreePanel.getJTree().setRootVisible(true); //
		billTreePanel.queryDataByCondition(null); //��ѯ��������!!!
		billTreePanel.setSelected(billTreePanel.getRootNode()); //ѡ�и����
		billTreePanel.addBillTreeSelectListener(this); //�����ѡ��仯�¼�!!
		billTreePanel.reSetTreeChecked(canCheck);//������Ϊ��ѡ
		return billTreePanel;
	}

	private WLTSplitPane getSplitPanel() {
		//������
		billTreePanel_Corp = getBillTreeWithChecked(false);

		//�ұߵİ�ť����(��һ��)
		JPanel panel_btn_1 = new JPanel(); //�������е����񲼾�
		panel_btn_1.setLayout(null); //
		rb_zhonghbm = new JCheckBox("���в���"); //
		rb_fengh = new JCheckBox("����"); //
		rb_shiyb = new JCheckBox("��ҵ��"); //
		rb_fenghbm = new JCheckBox("���в���"); //
		rb_zhih = new JCheckBox("֧��"); //
		rb_shiybfb = new JCheckBox("��ҵ���ֲ�"); //

		btn_selAllType = new WLTButton("ѡ�����л���"); //
		btn_selAllType.putClientProperty("selected", Boolean.FALSE); //
		btn_selAllType.setFocusable(false); //

		rb_zhonghbm_sub = new JCheckBox("���в���_����"); //
		rb_zhongh = new JCheckBox("����"); //
		rb_shiyb_sub = new JCheckBox("��ҵ��_����"); //
		rb_fenghbm_sub = new JCheckBox("���в���_����"); //
		rb_zhih_sub = new JCheckBox("֧��_����"); //
		rb_shiybfb_sub = new JCheckBox("��ҵ���ֲ�_����"); //

		btn_selAllRecord = new WLTButton("ѡ�б����������"); //
		btn_selAllRecord.putClientProperty("selected", Boolean.FALSE); //
		btn_selAllRecord.setFocusable(false); //

		int li_x = 0; //

		int li_width = 105; ////
		rb_zhonghbm.setBounds(li_x, 0, li_width, 20); ////..
		rb_zhonghbm_sub.setBounds(li_x, 20, li_width, 20); ////
		li_x = li_x + li_width; ////

		li_width = 55; //
		rb_fengh.setBounds(li_x, 0, li_width, 20); //
		rb_zhongh.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 100; //
		rb_shiyb.setBounds(li_x, 0, li_width, 20); //
		rb_shiyb_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 105; //
		rb_fenghbm.setBounds(li_x, 0, li_width, 20); //
		rb_fenghbm_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 90; //
		rb_zhih.setBounds(li_x, 0, li_width, 20); //
		rb_zhih_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 120; //
		rb_shiybfb.setBounds(li_x, 0, li_width, 20); //
		rb_shiybfb_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 125; //
		btn_selAllType.setBounds(li_x, 0, li_width, 20); //
		//li_x = li_x + li_width; //

		li_width = 125; //
		btn_selAllRecord.setBounds(li_x, 22, li_width, 20); //
		li_x = li_x + li_width; //

		rb_zhonghbm.addActionListener(this); //
		rb_fengh.addActionListener(this); //
		rb_shiyb.addActionListener(this); //
		rb_fenghbm.addActionListener(this); //
		rb_zhih.addActionListener(this); //
		rb_shiybfb.addActionListener(this); //
		btn_selAllType.addActionListener(this); //

		rb_zhonghbm_sub.addActionListener(this); //
		rb_zhongh.addActionListener(this); //
		rb_shiyb_sub.addActionListener(this); //
		rb_fenghbm_sub.addActionListener(this); //
		rb_zhih_sub.addActionListener(this); //
		rb_shiybfb_sub.addActionListener(this); //
		btn_selAllRecord.addActionListener(this); //

		panel_btn_1.add(rb_zhonghbm); //
		panel_btn_1.add(rb_fengh); //
		panel_btn_1.add(rb_shiyb); //
		panel_btn_1.add(rb_fenghbm); //
		panel_btn_1.add(rb_zhih); //
		panel_btn_1.add(rb_shiybfb); //
		panel_btn_1.add(btn_selAllType); //

		panel_btn_1.add(rb_zhonghbm_sub); //
		panel_btn_1.add(rb_zhongh); //
		panel_btn_1.add(rb_shiyb_sub); //
		panel_btn_1.add(rb_fenghbm_sub); //
		panel_btn_1.add(rb_zhih_sub); //
		panel_btn_1.add(rb_shiybfb_sub); //
		panel_btn_1.add(btn_selAllRecord); //
		panel_btn_1.setPreferredSize(new Dimension(li_x, 45)); //

		JPanel panel_btn_list = new JPanel(new BorderLayout()); //
		panel_btn_list.add(panel_btn_1, BorderLayout.NORTH); //

		billListPanel_Corp = new BillListPanel(new DefaultTMO(getListParnetTMO(), getListChildTMO())); //
		billListPanel_Corp.setToolbarVisiable(false); //
		panel_btn_list.add(billListPanel_Corp, BorderLayout.CENTER); //

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Corp, panel_btn_list); //
		splitPanel.setDividerLocation(225);

		return splitPanel;
	}

	/**
	 * ��ť���
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_allarea = new WLTButton("���з�Χ"); ////
		btn_cancel = new WLTButton("ȡ��");

		btn_cancel.addActionListener(this); //
		btn_allarea.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_allarea); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * �����ť����!!!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_allarea) {
			onChooseAllArea(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == rb_zhonghbm || //
				e.getSource() == rb_fengh || //
				e.getSource() == rb_shiyb || //
				e.getSource() == rb_fenghbm || //
				e.getSource() == rb_zhih || //
				e.getSource() == rb_shiybfb || //
				e.getSource() == rb_zhonghbm_sub || //
				e.getSource() == rb_zhongh || //
				e.getSource() == rb_shiyb_sub || //
				e.getSource() == rb_fenghbm_sub || //
				e.getSource() == rb_zhih_sub || //
				e.getSource() == rb_shiybfb_sub) {
			onSelectCorpChanged(); //
		} else if (e.getSource() == btn_selAllType) {
			onSelectAllCorpType((WLTButton) e.getSource()); //�Ƿ�ѡ������!
		} else if (e.getSource() == btn_selAllRecord) {
			onSelectAllRecord((WLTButton) e.getSource()); //�Ƿ�ѡ������!
		}
	}

	private void onSelectAllCorpType(WLTButton _button) {
		Boolean bo_selected = (Boolean) _button.getClientProperty("selected"); //
		if (bo_selected) {
			setAllCorpTypeChecked(false); //
			_button.setText("ѡ�����л���"); //
			_button.putClientProperty("selected", Boolean.FALSE); //
		} else {
			setAllCorpTypeChecked(true); //
			_button.setText("ȡ��ѡ�����л���"); //
			_button.putClientProperty("selected", Boolean.TRUE); //
		}
		onSelectCorpChanged(); //
	}

	private void setAllCorpTypeChecked(boolean _checked) {
		rb_zhonghbm.setSelected(_checked);
		rb_fengh.setSelected(_checked);
		rb_shiyb.setSelected(_checked);
		rb_fenghbm.setSelected(_checked);
		rb_zhih.setSelected(_checked);
		rb_shiybfb.setSelected(_checked);
		rb_zhonghbm_sub.setSelected(_checked);
		rb_zhongh.setSelected(_checked);
		rb_shiyb_sub.setSelected(_checked);
		rb_fenghbm_sub.setSelected(_checked);
		rb_zhih_sub.setSelected(_checked);
		rb_shiybfb_sub.setSelected(_checked);
	}

	private void onSelectAllRecord(WLTButton _button) {
		Boolean bo_selected = (Boolean) _button.getClientProperty("selected"); //
		if (bo_selected) {
			billListPanel_Corp.clearSelection(); //
			_button.setText("ѡ�б����������"); //
			_button.putClientProperty("selected", Boolean.FALSE); //
		} else {
			billListPanel_Corp.selectAll();
			_button.setText("ȡ��ѡ�б������"); //
			_button.putClientProperty("selected", Boolean.TRUE); //
		}
	}

	/**
	 * ���ȷ��ʱ������
	 */
	private void onConfirm() {
		BillVO[] billVOs = null;
		if (radioPanel.getSelectTitle().equals(this.STR_SIMPLE)) {
			billVOs = billTreePanel_Corp_simple.getCheckedVOs();
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "��ѡ��������ϵ�һ������,�ɹ�ѡҪѡ��Ļ����ڵ�ǰ�ĸ�ѡ����ѡ��û���!"); //
				return;
			}
		} else if (radioPanel.getSelectTitle().equals(this.STR_COMPLEX)) {
			billVOs = billListPanel_Corp.getSelectedBillVOs(); //
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "��ѡ�����е�һ������,�ɵ�����Ϸ���\"ѡ�б����������\"��ť����ѡ������������!"); //
				return;
			}
		}

		//����ѡ�е����л���..
		StringBuilder sb_ids = new StringBuilder(); //
		StringBuilder sb_names = new StringBuilder(); //
		for (int i = 0; i < billVOs.length; i++) {
			sb_ids.append(billVOs[i].getStringValue("id") + ";"); //
			sb_names.append(billVOs[i].getStringValue("name") + ";"); //
		}

		returnRefItemVO = new RefItemVO(sb_ids.toString(), null, sb_names.toString()); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	/**
	 * ѡ�����з�Χ
	 */
	private void onChooseAllArea() {
		returnRefItemVO = new RefItemVO("���з�Χ", null, "���з�Χ"); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	/**
	 * ���ȡ��ʱ������!!
	 */
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(2); //
		this.dispose(); //
	}

	/**
	 * 
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		onSelectCorpChanged();
	}

	private void onSelectCorpChanged() {
		billListPanel_Corp.clearTable(); //��ձ������!!!
		DefaultMutableTreeNode node = billTreePanel_Corp.getSelectedNode(); //
		if (node == null) {
			return;
		}

		ArrayList al_corpTypes = getChooseCorpType(); //ѡ�еĻ�������!!
		ArrayList al_allCorpNodes = new ArrayList(); //
		DefaultMutableTreeNode[] selectetNodes = billTreePanel_Corp.getSelectedNodes(); //
		for (int i = 0; i < selectetNodes.length; i++) {
			DefaultMutableTreeNode[] selNode_allNodes = billTreePanel_Corp.getOneNodeChildPathNodes(selectetNodes[i]); //ȡ�����л���
			for (int j = 0; j < selNode_allNodes.length; j++) {
				if (!al_allCorpNodes.contains(selNode_allNodes[j])) { //���������
					al_allCorpNodes.add(selNode_allNodes[j]); //
				}
			}
		}

		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) al_allCorpNodes.toArray(new DefaultMutableTreeNode[0]); //
		for (int i = 0; i < allNodes.length; i++) {
			String str_corpType = billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "corptype"); //
			if (al_corpTypes.contains(str_corpType)) { //����������������͵�!!!
				int li_row = billListPanel_Corp.addEmptyRow(false); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "id")), li_row, "id"); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "code")), li_row, "code"); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "name")), li_row, "name"); //
				billListPanel_Corp.setValueAt(new StringItemVO(str_corpType), li_row, "corptype"); //CORPTYPE
			}

		}
	}

	/**
	 * ��ȡ��ǰѡ�е���ʲô����!!!!
	 * @return
	 */
	private ArrayList getChooseCorpType() {
		ArrayList al_type = new ArrayList(); //
		if (rb_zhonghbm.isSelected()) {
			al_type.add("���в���"); //
		}
		if (rb_fengh.isSelected()) {
			al_type.add("����"); //
		}
		if (rb_shiyb.isSelected()) {
			al_type.add("��ҵ��"); //
		}
		if (rb_fenghbm.isSelected()) {
			al_type.add("���в���"); //
		}
		if (rb_zhih.isSelected()) {
			al_type.add("֧��"); //
		}
		if (rb_shiybfb.isSelected()) {
			al_type.add("��ҵ���ֲ�"); //
		}

		if (rb_zhonghbm_sub.isSelected()) {
			al_type.add("���в���_��������"); //
		}
		if (rb_zhongh.isSelected()) {
			al_type.add("����"); //
		}
		if (rb_shiyb_sub.isSelected()) {
			al_type.add("��ҵ��_��������"); //
		}
		if (rb_fenghbm_sub.isSelected()) {
			al_type.add("���в���_��������"); //
		}
		if (rb_zhih_sub.isSelected()) {
			al_type.add("֧��_��������"); //
		}
		if (rb_shiybfb_sub.isSelected()) {
			al_type.add("��ҵ���ֲ�_��������"); //
		}
		return al_type;
	}

	private HashVO getListParnetTMO() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "������"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "������"); //ģ������
		vo.setAttributeValue("templetname_e", "������"); //ģ������
		return vo;
	}

	@Override
	public int getInitWidth() {
		return 950; //
	}

	/**
	 * ȡ���б���ӱ���VO.
	 * @return
	 */
	private HashVO[] getListChildTMO() {
		ArrayList al_hvo = new ArrayList(); //

		HashVO itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Id"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CODE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "NAME"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "225"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CORPTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CORPTYPE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		return (HashVO[]) al_hvo.toArray(new HashVO[0]);
	}

}
