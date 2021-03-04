package cn.com.infostrategy.ui.mdata.formatcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * һ��ͨ����ע��������ת����һ���Զ�����յĻ���!!
 * ��һ��һ���Զ�����ն�����ͨ��һ��ע�����������ҳ��,Ȼ������������һ��ȷ����ȡ����Ť�Ӷ��γ�һ������!!!
 * @author xch
 *
 */
public abstract class AbstractRegFormatRefDialog extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillFormatPanel billFormatPanel = null;
	private BillListPanel selectedDataListPanel = null; //ѡ�����ݵ��б�
	private WLTButton btn_add, btn_addall, btn_remove, btn_removeall; //�ĸ���ť
	private WLTButton btn_moveup, btn_movedown, btn_addregbtn; //����,����!!

	public AbstractRegFormatRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, "[ע]" + _title, refItemVO, panel);
	}

	public abstract String getRegFormatCode(); //ע�����ı���!!!����Ҫ��

	public int getRefStyleType() { //���շ��
		return 0; //
	}

	public abstract RefItemVO checkBeforeClose() throws Exception; //ע�����ı���!!!����Ҫ��

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billFormatPanel = BillFormatPanel.getRegisterFormatPanel(getRegFormatCode()); //ͨ��ע��������һ����ʽ���
		JPanel panel_refcontent = new JPanel(new BorderLayout()); //
		int li_seltype = getRefStyleType(); //ѡ������,0-��ѡ;1-��ѡ;2-��ѡ(���ҷָ�);3-��ѡ(���·ָ�)4;-��ѡ(����ҳǩ)
		if (li_seltype == 0) { //��ѡ,��ֻ��ѡһ��..
			panel_refcontent.add(billFormatPanel); //

			BillPanel returnPanel = this.getBillFormatPanel().getReturnRefItemVOFrom();
			if (returnPanel != null) {
				if (returnPanel instanceof BillListPanel) {
					BillListPanel billList = (BillListPanel) returnPanel;
					billList.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
				} else if (returnPanel instanceof BillTreePanel) {
					BillTreePanel billTree = (BillTreePanel) returnPanel;
					billTree.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //ֻ�ܵ�ѡ
				}
			}

			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 1) { //��ѡ,������ѡ���
			panel_refcontent.add(billFormatPanel); //
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 2) //���ҷָ�
		{
			WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT); //
			splitPane.setDividerLocation(billFormatPanel.getSuggestWidth() - 400); // 
			splitPane.setLeftComponent(billFormatPanel); //

			btn_add = new WLTButton("     >    "); //
			btn_addall = new WLTButton("   >>    "); //
			btn_remove = new WLTButton("     <    "); //
			btn_removeall = new WLTButton("   <<    "); //
			//���Ӽ����¼�!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			JPanel panel_btn = new JPanel(new BorderLayout()); //
			panel_btn.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
			Box bntBox = Box.createVerticalBox();
			bntBox.add(Box.createGlue());
			bntBox.add(btn_add); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_addall); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_remove); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_removeall); //
			bntBox.add(Box.createGlue());
			panel_btn.add(bntBox); //
			panel_btn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //

			JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_btn, getSelectedDataListPanel()); //
			splitPane2.setDividerLocation(55); //
			splitPane2.setDividerSize(0); //
			splitPane2.setEnabled(false); //
			splitPane.setRightComponent(splitPane2); //

			panel_refcontent.add(splitPane); //
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 3) {
			WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT); //
			splitPane.setDividerLocation(billFormatPanel.getSuggestHeight() - 300); // 
			splitPane.setLeftComponent(billFormatPanel); //

			btn_add = new WLTButton("��"); //
			btn_addall = new WLTButton("ȫ��"); //
			btn_remove = new WLTButton("��"); //
			btn_removeall = new WLTButton("ȫ��"); //

			//���Ӽ����¼�!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			btn_add.setPreferredSize(new Dimension(65, 20)); //
			btn_addall.setPreferredSize(new Dimension(65, 20)); //
			btn_remove.setPreferredSize(new Dimension(65, 20)); //
			btn_removeall.setPreferredSize(new Dimension(65, 20)); //

			JPanel panel_btn = new JPanel(new FlowLayout()); //
			panel_btn.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
			panel_btn.add(btn_add); //
			panel_btn.add(btn_addall); //
			panel_btn.add(btn_remove); //
			panel_btn.add(btn_removeall); //

			JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel_btn, getSelectedDataListPanel()); //
			splitPane2.setDividerLocation(30); //
			splitPane2.setDividerSize(0); //
			splitPane2.setEnabled(false); //
			splitPane.setRightComponent(splitPane2); //

			panel_refcontent.add(splitPane);
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 4) {
			btn_add = new WLTButton(">"); //
			btn_addall = new WLTButton(">>"); //
			btn_remove = new WLTButton("<"); //
			btn_removeall = new WLTButton("<<"); //

			//���Ӽ����¼�!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			btn_add.setPreferredSize(new Dimension(65, 20)); //
			btn_addall.setPreferredSize(new Dimension(65, 20)); //
			btn_remove.setPreferredSize(new Dimension(65, 20)); //
			btn_removeall.setPreferredSize(new Dimension(65, 20)); //

			JPanel panel_btn_1 = new JPanel(new FlowLayout()); //
			panel_btn_1.add(btn_add); //
			panel_btn_1.add(btn_addall); //

			JPanel panel_btn_2 = new JPanel(new FlowLayout()); //
			panel_btn_2.add(btn_remove); //
			panel_btn_2.add(btn_removeall); //
			btn_confirm = new WLTButton("ȷ��");
			btn_cancel = new WLTButton("ȡ��");
			btn_cancel.addActionListener(this); //
			btn_confirm.addActionListener(this); //

			panel_btn_2.add(btn_remove); //
			panel_btn_2.add(btn_removeall); //
			panel_btn_2.add(btn_confirm); //
			panel_btn_2.add(btn_cancel); //

			JPanel panel_1 = new JPanel(new BorderLayout()); //
			panel_1.add(billFormatPanel, BorderLayout.CENTER); //
			panel_1.add(panel_btn_1, BorderLayout.SOUTH); //

			JPanel panel_2 = new JPanel(new BorderLayout()); //
			panel_2.add(getSelectedDataListPanel(), BorderLayout.CENTER); //
			panel_2.add(panel_btn_2, BorderLayout.SOUTH); //

			JTabbedPane tabbedPane = new JTabbedPane(); //
			tabbedPane.addTab("����ѡ��", panel_1); //
			tabbedPane.addTab("��ѡ������", panel_2); //
			tabbedPane.setSelectedIndex(1); //
			panel_refcontent.add(tabbedPane); //

			this.add(panel_refcontent, BorderLayout.CENTER); //
		}

		if (getRefStyleType() == 1) { //����Ƕ�ѡ,�����ָ�!
			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				String[] str_ids = getInitRefItemVO().getId().split(";"); //
				setInitSelectRow(getBillFormatPanel().getReturnRefItemVOFrom(), str_ids, getBillFormatPanel().getReturnRefItemVOIDFieldName()); //
			}
		} else if (getRefStyleType() == 2 || getRefStyleType() == 3 || getRefStyleType() == 4) {
			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				String[] str_ids = getInitRefItemVO().getId().split(";"); //
				String[] str_names = getInitRefItemVO().getName().split(";"); //
				for (int i = 0; i < str_ids.length; i++) {
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_ids[i]), li_row, "RefID"); //��������
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_names[i]), li_row, "RefName"); //
				}
			}
		}

		//�����ڴ�С�����λ��
		this.setSize(billFormatPanel.getSuggestWidth(), billFormatPanel.getSuggestHeight()); //���ý�������߶�
		int li_screenwidth = (int) UIUtil.getScreenMaxDimension().getWidth(); //
		int li_screenheight = (int) UIUtil.getScreenMaxDimension().getHeight(); //
		int li_x = (li_screenwidth - billFormatPanel.getSuggestWidth()) / 2;
		int li_y = (li_screenheight - billFormatPanel.getSuggestHeight()) / 2;
		li_x = (li_x < 0 ? 0 : li_x);
		li_y = (li_y < 0 ? 0 : li_y);
		this.setLocation(li_x, li_y); //

		if (getRefStyleType() != 0 && billFormatPanel.getReturnRefItemVOFrom() == null) {
			this.setTitle(this.getTitle() + "<������:����Ϊ��ѡ����,��û�ж��巵������>"); //
		}
	}

	private void setInitSelectRow(BillPanel _billPanel, String[] str_ids, String _returnRefItemVOIDFieldName) {
		if (_billPanel != null) {
			if (_billPanel instanceof BillListPanel) {
				BillListPanel billList = (BillListPanel) _billPanel; //
				int li_rowcount = billList.getRowCount(); //
				for (int i = 0; i < li_rowcount; i++) {
					String str_idvalue = billList.getRealValueAtModel(i, _returnRefItemVOIDFieldName); //
					if (str_idvalue != null) {
						for (int j = 0; j < str_ids.length; j++) {
							if (str_idvalue.equals(str_ids[j])) {
								billList.getTable().getSelectionModel().addSelectionInterval(i, i); //
								break; //
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showInfo(); //
				}
			}
		});

		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	protected void showInfo() {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_regformatpanel where code = '" + getRegFormatCode() + "'"); //
			String str_info = "";//
			str_info = str_info + "ʵ����:" + this.getClass().getName() + "\r\n";
			str_info = str_info + "���õ�ע�����Code:" + hvs[0].getStringValue("code") + "\r\n";
			str_info = str_info + "\r\n-------------- ���幫ʽ ----------------\r\n";
			str_info = str_info + hvs[0].getStringValue("formatformula") + "\r\n"; //

			str_info = str_info + "\r\n-------------- �¼���ʽ ----------------\r\n";
			str_info = str_info + hvs[0].getStringValue("eventbindformula") + "\r\n"; //
			MessageBox.showTextArea(this, "�Զ���������õ�ע�������Ϣ", str_info); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //�����ȷ���򷵻�����
			try {
				returnRefItemVO = checkBeforeClose(); // ����ǰ������һ��У��,����ɹ������򷵻�����,�������쳣,�˳�!
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return; //
			}
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		} else if (e.getSource() == btn_add) {
			onAdd(); //
		} else if (e.getSource() == btn_addall) {
			onAddAll(); //
		} else if (e.getSource() == btn_remove) {
			onRemove(); //
		} else if (e.getSource() == btn_removeall) {
			onRemoveAll(); //
		} else if (e.getSource() == btn_addregbtn) {
			onAddSysRegButton(); //����ϵͳע���Ͱ�ť!!
		}
	}

	/**
	 * ����һ��
	 */
	public void onAdd() {
	}

	public void onAddAll() {
	}

	public void onRemove() {
	}

	public void onRemoveAll() {
	}

	public void onAddSysRegButton() {
		String[][] str_allbtns = WLTButton.getSysRegButtonType(); ////
		JComboBox comBox = new JComboBox(); //
		for (int i = 1; i < str_allbtns.length; i++) {
			comBox.addItem(str_allbtns[i][0]); //
		}

		JPanel panel = new JPanel(null); //
		comBox.setBounds(5, 5, 165, 20); //
		panel.add(comBox); //
		panel.setPreferredSize(new Dimension(180, 25)); //
		if (JOptionPane.showConfirmDialog(this, panel, "��ʾ", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			String str_seltext = (String) comBox.getSelectedItem(); //
			int li_newrow = selectedDataListPanel.addEmptyRow(); //
			selectedDataListPanel.setValueAt(new StringItemVO("$" + str_seltext), li_newrow, "RefID"); //
			selectedDataListPanel.setValueAt(new StringItemVO("$" + str_seltext), li_newrow, "RefName"); //
		}
	}

	public BillFormatPanel getBillFormatPanel() {
		return billFormatPanel;
	}

	public BillListPanel getSelectedDataListPanel() {
		if (selectedDataListPanel == null) {
			selectedDataListPanel = new BillListPanel(new TMO_SelectedData()); //
			btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //����!!
			btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //����!!
			btn_addregbtn = new WLTButton("ϵͳע����"); //
			btn_addregbtn.addActionListener(this); //
			selectedDataListPanel.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown, btn_addregbtn }); //
			selectedDataListPanel.repaintBillListButton(); //
		}
		return selectedDataListPanel; // 
	}

	class TMO_SelectedData extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "selecteddata"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "��ѡ������"); //ģ������
			vo.setAttributeValue("templetname_e", "selecteddata"); //ģ������
			vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", "ID"); //������
			vo.setAttributeValue("pksequencename", null); //������
			vo.setAttributeValue("savedtablename", null); //�������ݵı���
			vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
			vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
			vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("listcustpanel", null); //�б��Զ������
			vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

			vo.setAttributeValue("ISSHOWLISTCUSTBTN", "Y"); //�б��Ƿ���ʾ�Զ��尴ť
			vo.setAttributeValue("Listcustbtndesc", null); //�б��Զ��尴ť

			vo.setAttributeValue("TREEPK", "id"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeviewfield", "name"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeisshowtoolbar", "Y"); //������ʾ������
			vo.setAttributeValue("Treeisonlyone", "N"); //������ʾ������
			vo.setAttributeValue("Treeseqfield", "seq"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeisshowroot", "Y"); //�б��Ƿ���ʾ������ť��
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "RefID"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "���մ洢ֵ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "RefID"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "RefName"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "������ʾֵ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "RefName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "155"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
