/**************************************************************************
 * $RCSfile: AbstractWLTCompentPanel.java,v $  $Revision: 1.45 $  $Date: 2013/02/28 06:14:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.report.WordFileUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.MetaDataUIUtil;
import cn.com.infostrategy.ui.mdata.UIRefPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.WLTHyperLinkLabel;

/**
 * ���пؼ�������
 * @author xch
 *
 */
public abstract class AbstractWLTCompentPanel extends JPanel {

	public abstract String getItemKey();

	public abstract String getItemName();

	public abstract String getValue();

	public abstract Object getObject(); //

	public abstract void setValue(String _value);

	public abstract void setObject(Object _obj);

	public abstract void reset(); //

	public abstract void setItemEditable(boolean _bo); //

	public abstract void setItemVisiable(boolean _bo); //�����Ƿ����ʾ

	public abstract boolean isItemEditable(); //�Ƿ�ɱ༭

	public abstract JLabel getLabel();

	public abstract void focus();

	public abstract int getAllWidth(); //

	public TBUtil tBUtil = null; //

	private Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = null;

	protected BillPanel billPanel = null; //

	private JPopupMenu popmenu_header = null;
	private JMenuItem item_table_templetmodify_1, item_table_templetmodify_2, menu_db_add, menu_db_del, menu_db_updatevalue, menu_db_flow, menu_db_flow_database, menu_lookhelp, menu_lookaonfig, menu_showuitruedata, menu_showdbtruedata, menu_export_pic, menu_export_html, menu_export_reporthtml,
			menu_export_reportword, menu_export_designDoc, menu_item_state; //
	private ActionListener actionListener_popMenuItem; //

	public AbstractWLTCompentPanel() {
		this.setOpaque(false);
	}

	public JLabel createLabel(Pub_Templet_1_ItemVO _templetItemVO) {
		return createLabel(_templetItemVO, true); //Ĭ������ʾ��ʷ�ۼ���,�������еĿ�Ƭ�ؼ�����Ҫ��!!
	}

	/**
	 * ����Label
	 * @return
	 * @param  _isShowKeepTraceFeel �Ƿ���ʾ��ʷ����ۼ�!!
	 */
	public JLabel createLabel(Pub_Templet_1_ItemVO _templetItemVO, boolean _isShowKeepTraceFeel) {
		this.pub_Templet_1_ItemVO = _templetItemVO;
		final WLTLabel label = new WLTLabel();

		label.setFont(LookAndFeel.font); //
		label.setHorizontalAlignment(SwingConstants.RIGHT); //����
		label.setVerticalAlignment(SwingConstants.TOP); //

		String str_name = "";
		if (ClientEnvironment.getInstance().isEngligh()) {
			str_name = _templetItemVO.getItemname_e();
		} else {
			str_name = _templetItemVO.getItemname();
		}

		if (this.billPanel != null && this.billPanel instanceof BillQueryPanel) {
			if (WLTConstants.COMP_DATE.equals(_templetItemVO.getItemtype())) {
				str_name = str_name + "(����)";
				label.addStrItemColor("(����)", Color.RED);
			}
		}

		String str_tiptext = _templetItemVO.getItemtiptext(); //
		if (str_tiptext == null || str_tiptext.trim().equals("")) {
			str_tiptext = str_name; //
		}

		label.setOpaque(false); //
		boolean _ismustinput = false;
		if ("Y".equalsIgnoreCase(_templetItemVO.getIsmustinput2()) || "W".equalsIgnoreCase(_templetItemVO.getIsmustinput2())) { //����Ǳ�����򾯸���,�򶼱�ʾ����! ��ҵ�ڷ���ģ��ǿ��Ҫ������Ǿ�����Ҳ���Ǻ�!
			_ismustinput = true;
		}
		if (_isShowKeepTraceFeel) { //�����Ҫ��ʾ��ʷ�ۼ�
			if (pub_Templet_1_ItemVO != null && pub_Templet_1_ItemVO.getIskeeptrace()) {
				label.setToolTipText("<html>" + str_tiptext + "<br>&nbsp;<font color=\"#FF8040\">�����ɲ鿴��ʷ�ۼ�</font>&nbsp;</html>"); ////
				label.setForeground(new Color(255, 128, 64)); //���ýۺ�ɫ!!
			} else {
				label.setToolTipText(str_tiptext); //
			}
		} else {
			label.setToolTipText(str_tiptext); //
		}

		//������뱣��,���ֲ��ܱ���,����ʾ��ɫ!!!�澯
		if (_templetItemVO.isNeedSave() && !_templetItemVO.isCanSave()) {
			label.setForeground(java.awt.Color.RED);
		} else {
			String fg_bgcolor = pub_Templet_1_ItemVO.getShowbgcolor();
			if (fg_bgcolor == null || fg_bgcolor.trim().equals("")) {
				label.setForeground(LookAndFeel.systemLabelFontcolor); //ͳһ���ʹ��ϵͳ�����ֵ���ɫ�����/2012-03-19��
			} else {
				String[] fg_bgcolors = fg_bgcolor.split(",");
				if (fg_bgcolors.length == 1) { //ֻ����ǰ��ɫ������ɫĬ��
					label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
					label.setBackground(LookAndFeel.tableheaderbgcolor); //����Ĭ�ϱ���ɫ
				} else if (fg_bgcolors.length > 1) {
					label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
					label.setBackground(getTBUtil().getColor(fg_bgcolors[1])); //���ñ���ɫ
				}
			}

		}
		String str_realText = getTBUtil().getLableRealText(str_name, ClientEnvironment.getInstance().isAdmin(), _ismustinput, _templetItemVO.isViewColumn(), _templetItemVO.isNeedSave()); //
		label.setText(str_realText); //�������ı�
		if (_ismustinput) {
			label.addStrItemColor("*", Color.RED); //
		}
		//����߿�
		if (_templetItemVO.getPub_Templet_1VO().getIsshowcardborder().booleanValue()) {
			label.setBorder(BorderFactory.createLineBorder(new Color(255, 128, 128), 1)); //�ۺ�ɫ
		}

		//���ô�С
		int li_width = 120;
		int li_height = 20;

		if (pub_Templet_1_ItemVO.getLabelwidth() != null) {
			li_width = pub_Templet_1_ItemVO.getLabelwidth().intValue();
		}
		if (pub_Templet_1_ItemVO.getCardHeight() != null) {
			li_height = pub_Templet_1_ItemVO.getCardHeight().intValue();
		}

		label.setPreferredSize(new Dimension(li_width, li_height)); //���Ŀǰ��д����,��120
		label.addMouseListener(new MouseAdapter() { //
					public void mousePressed(MouseEvent evt) {
						if (evt.getButton() == MouseEvent.BUTTON3) {
							//if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { //����ǹ�����ݵ�¼,����ʾ�Ҽ�
							showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); ////�����˵�
							//}
						} else {
							if (pub_Templet_1_ItemVO.getItemtype().equals(WLTConstants.COMP_EXCEL)) {
								label.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
								callRefButtonClicked(evt.getX(), evt.getY()); //
								label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
							} else {
								showKeepTrace(); ////
							}
						}
					}

					/**
					 * ������ʱ
					 */
					public void mouseEntered(MouseEvent e) {
						if (pub_Templet_1_ItemVO.getItemtype().equals(WLTConstants.COMP_EXCEL) || pub_Templet_1_ItemVO.getIskeeptrace()) { //������Ҫ��,���Ե����ֱ�Ӵ�Excel����,�����������ȥʱҪ�ı���
							label.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
							label.setFont(LookAndFeel.font_b); //
						}
					}

					/**
					 * ����뿪ʱ
					 */
					public void mouseExited(MouseEvent e) {
						if (pub_Templet_1_ItemVO.getItemtype().equals(WLTConstants.COMP_EXCEL) || pub_Templet_1_ItemVO.getIskeeptrace()) { //������Ҫ��,���Ե����ֱ�Ӵ�Excel����,�����������ȥʱҪ�ı���
							label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
							label.setFont(LookAndFeel.font); //
						}
					}
				});

		return label;
	}

	/**
	 * ���������ò��յİ�ť���÷���
	 */
	private void callRefButtonClicked(int _x, int _y) {
		((UIRefPanel) AbstractWLTCompentPanel.this).onButtonClicked(); //
	}

	/**
	 * ��ʾ��ʷ�ۼ�
	 */
	public void showKeepTrace() {
		if (pub_Templet_1_ItemVO != null) {
			if (pub_Templet_1_ItemVO.getIskeeptrace()) { //�����Ҫ�����ۼ�����ŵ�����ʾ�޸ĺۼ���..
				String str_tablename = this.pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename(); //
				if (str_tablename != null) {
					String str_pkname = pub_Templet_1_ItemVO.getPub_Templet_1VO().getPkname(); //
					if (str_pkname != null) {
						BillPanel contentBillPanel = getBillPanel(); //
						if (contentBillPanel instanceof BillCardPanel) {
							BillCardPanel cardPanel = (BillCardPanel) contentBillPanel; //
							String str_pkvalue = cardPanel.getRealValueAt(str_pkname); //
							if (str_pkvalue != null) {
								ShowBillTraceDialog dialog = new ShowBillTraceDialog(this, str_tablename, str_pkname, str_pkvalue, this.getItemKey()); //
								dialog.setVisible(true); //
							}
						}
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "ģ��Ϊ��!"); //
		}
	}

	public WLTHyperLinkLabel[] createHyperLinkLabel(String _itemKey, String _itemName, String _hyperLinkDesc, BillPanel _billPanel) {
		if (_hyperLinkDesc == null || _hyperLinkDesc.trim().equals("")) {
			return null;
		} else {
			String[] str_hyperItems = _hyperLinkDesc.split(","); //
			WLTHyperLinkLabel[] hyperlinks = new WLTHyperLinkLabel[str_hyperItems.length]; //
			for (int i = 0; i < str_hyperItems.length; i++) {
				int li_pos_1 = str_hyperItems[i].lastIndexOf("<");
				int li_pos_2 = str_hyperItems[i].lastIndexOf(">");
				String str_labelName = "";
				String str_dialogname = "";
				if (li_pos_1 > 0) {
					str_labelName = str_hyperItems[i].substring(0, li_pos_1);
					str_dialogname = str_hyperItems[i].substring(li_pos_1 + 1, li_pos_2);
				} else {
					str_labelName = str_hyperItems[i];
				}
				hyperlinks[i] = new WLTHyperLinkLabel(_itemKey, _itemName, str_labelName, str_dialogname, _billPanel); //�������������!!
			}
			return hyperlinks;
		}

	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		if (popmenu_header != null) {
			popmenu_header.show(_compent, _x, _y); //
			return; //
		}

		final JLabel label = (JLabel) _compent;
		popmenu_header = new JPopupMenu();

		JMenu menu_table_templetmodify = new JMenu("����ģ��༭");
		menu_table_templetmodify.setIcon(UIUtil.getImage("office_127.gif")); //�Ǹ�����ͼ��,����ӡ�����!!
		item_table_templetmodify_1 = new JMenuItem("�༭ѡ����");
		item_table_templetmodify_2 = new JMenuItem("�༭����ģ��");

		JMenu menu_db = new JMenu("�������ݿ��ٴ���");
		menu_db.setIcon(UIUtil.getImage("office_147.gif")); //
		menu_db_add = new JMenuItem("�ڿ������Ӹ���");
		menu_db_del = new JMenuItem("�ڿ���ɾ������");
		menu_db_updatevalue = new JMenuItem("�п��ٸ�ֵ");
		menu_db_flow = new JMenuItem("��ģ�������ӹ�������3���ֶ�");
		menu_db_flow_database = new JMenuItem("�ڿ������ӹ�������3���ֶ�");

		menu_lookhelp = new JMenuItem("�鿴����", UIUtil.getImage("office_156.gif")); ////
		menu_lookaonfig = new JMenuItem("�鿴��������", UIUtil.getImage("office_162.gif"));
		menu_item_state = new JMenuItem("�鿴����״̬", UIUtil.getImage("office_011.gif"));
		menu_showuitruedata = new JMenuItem("�鿴UI��ʵ������", UIUtil.getImage("office_086.gif"));
		menu_showdbtruedata = new JMenuItem("�鿴DB��ʵ������", UIUtil.getImage("office_009.gif")); //

		JMenu menu_exportpage = new JMenu("����ҳ��"); ////
		menu_exportpage.setIcon(UIUtil.getImage("office_170.gif")); //
		menu_export_pic = new JMenuItem("������ͼƬ"); ////
		menu_export_html = new JMenuItem("������Html"); ////
		menu_export_reporthtml = new JMenuItem("����Html����"); ////
		menu_export_reportword = new JMenuItem("����Word����"); ////
		menu_export_designDoc = new JMenuItem("����������ĵ�"); //

		menu_exportpage.add(menu_export_pic); //
		menu_exportpage.add(menu_export_html); //
		menu_exportpage.add(menu_export_reporthtml); //
		menu_exportpage.add(menu_export_reportword); //
		menu_exportpage.add(menu_export_designDoc); //

		Dimension menuItemSize = new Dimension(95, 19); //
		menu_table_templetmodify.setPreferredSize(menuItemSize);
		item_table_templetmodify_1.setPreferredSize(menuItemSize);
		item_table_templetmodify_2.setPreferredSize(menuItemSize);

		menu_showuitruedata.setPreferredSize(menuItemSize);
		menu_showdbtruedata.setPreferredSize(menuItemSize);

		menu_export_pic.setPreferredSize(menuItemSize);
		menu_export_html.setPreferredSize(menuItemSize);
		menu_export_reporthtml.setPreferredSize(menuItemSize);
		menu_export_reportword.setPreferredSize(menuItemSize);

		menu_table_templetmodify.add(item_table_templetmodify_1); //
		menu_table_templetmodify.add(item_table_templetmodify_2); //

		menu_db.add(menu_db_add);
		menu_db.add(menu_db_del);
		menu_db.add(menu_db_flow);
		menu_db.add(menu_db_flow_database);
		menu_db.add(menu_db_updatevalue);

		actionListener_popMenuItem = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == item_table_templetmodify_1) {
					modifyTemplet1();
				} else if (e.getSource() == item_table_templetmodify_2) {
					modifyTemplet2();
				} else if (e.getSource() == menu_lookhelp) {
					onLookHelp(); //
				} else if (e.getSource() == menu_lookaonfig) {
					lookconfig(label);
				} else if (e.getSource() == menu_db_add) {
					addDb();
				} else if (e.getSource() == menu_db_del) {
					delDb();
				} else if (e.getSource() == menu_db_updatevalue) {
					updateDb();
				} else if (e.getSource() == menu_db_flow_database) {
					flowDb();
				} else if (e.getSource() == menu_db_flow) {
					flowTemplet();
				} else if (e.getSource() == menu_showuitruedata) {
					showUITrueData();
				} else if (e.getSource() == menu_showdbtruedata) {
					showDBTrueData();
				} else if (e.getSource() == menu_export_pic) {
					onExportToPic();
				} else if (e.getSource() == menu_export_html) {
					onExportToHtml();
				} else if (e.getSource() == menu_export_reporthtml) {
					onExportToReportHtml();
				} else if (e.getSource() == menu_export_reportword) {
					onExportToReportWord();
				} else if (e.getSource() == menu_export_designDoc) {
					onExportToDesignDoc();
				} else if (e.getSource() == menu_item_state) {
					showItemState();
				} else {
					MessageBox.show(label, "�����δ֪�ؼ�[" + e.getSource() + "]"); //
				}
			}
		};

		item_table_templetmodify_1.addActionListener(actionListener_popMenuItem);
		item_table_templetmodify_2.addActionListener(actionListener_popMenuItem);
		menu_lookhelp.addActionListener(actionListener_popMenuItem);
		menu_lookaonfig.addActionListener(actionListener_popMenuItem);
		menu_db_add.addActionListener(actionListener_popMenuItem);
		menu_db_del.addActionListener(actionListener_popMenuItem);
		menu_db_updatevalue.addActionListener(actionListener_popMenuItem);
		menu_db_flow_database.addActionListener(actionListener_popMenuItem);
		menu_db_flow.addActionListener(actionListener_popMenuItem);
		menu_showuitruedata.addActionListener(actionListener_popMenuItem);
		menu_showdbtruedata.addActionListener(actionListener_popMenuItem);
		menu_export_pic.addActionListener(actionListener_popMenuItem);
		menu_export_html.addActionListener(actionListener_popMenuItem);
		menu_export_reporthtml.addActionListener(actionListener_popMenuItem);
		menu_export_reportword.addActionListener(actionListener_popMenuItem);
		menu_export_designDoc.addActionListener(actionListener_popMenuItem);
		menu_item_state.addActionListener(actionListener_popMenuItem);
		if (ClientEnvironment.getInstance().isAdmin()) {
			popmenu_header.add(menu_table_templetmodify); // ����ģ��༭
			popmenu_header.add(menu_db); // ����ģ��༭
		}
		popmenu_header.add(menu_lookaonfig); // ����ģ��༭
		if (ClientEnvironment.getInstance().isAdmin()) {
			popmenu_header.add(menu_showuitruedata); // ����ģ��༭
			popmenu_header.add(menu_showdbtruedata); // ����ģ��༭
			popmenu_header.add(menu_item_state);
		}

		popmenu_header.add(menu_exportpage); // ����ģ��༭
		popmenu_header.add(menu_lookhelp); // ����ģ��༭
		popmenu_header.show(_compent, _x, _y); //
	}

	private void modifyTemplet1() {
		String str_itemKey = this.pub_Templet_1_ItemVO.getItemkey();
		try {
			if (this.billPanel != null) {
				if (billPanel instanceof BillCardPanel) {
					BillCardPanel cardPanel = (BillCardPanel) billPanel;
					new MetaDataUIUtil().modifyTemplet(cardPanel, cardPanel.getTempletVO().getBuildFromType(), cardPanel.getTempletVO().getBuildFromInfo(), cardPanel.getTempletVO().getTempletcode(), cardPanel.getTempletVO().getTempletname(), false, str_itemKey);
				} else if (billPanel instanceof BillQueryPanel) {
					BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
					new MetaDataUIUtil().modifyTemplet(queryPanel, queryPanel.getTempletVO().getBuildFromType(), queryPanel.getTempletVO().getBuildFromInfo(), queryPanel.getTempletVO().getTempletcode(), queryPanel.getTempletVO().getTempletname(), false, str_itemKey);
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(billPanel, ex); //
			return; //
		}

	}

	private void modifyTemplet2() {
		try {
			if (this.billPanel != null) {
				if (billPanel instanceof BillCardPanel) {
					BillCardPanel cardPanel = (BillCardPanel) billPanel;
					new MetaDataUIUtil().modifyTemplet2(cardPanel, cardPanel.getTempletVO().getBuildFromType(), cardPanel.getTempletVO().getBuildFromInfo(), cardPanel.getTempletVO().getTempletcode(), cardPanel.getTempletVO().getTempletname(), false, null);
				} else if (billPanel instanceof BillQueryPanel) {
					BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
					new MetaDataUIUtil().modifyTemplet2(queryPanel, queryPanel.getTempletVO().getBuildFromType(), queryPanel.getTempletVO().getBuildFromInfo(), queryPanel.getTempletVO().getTempletcode(), queryPanel.getTempletVO().getTempletname(), false, null);
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(billPanel, ex); //
			return; //
		}
	}

	private void addDb() {//��sunfujun/20120508/��ģ������ɾ���ֶε��Ż�_xch��
		if (this.pub_Templet_1_ItemVO.getPub_Templet_1VO() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "ֱ���ɴ��봴��,���ɱ༭!"); //
			return;
		}//��sunfujun/20120509/����Ӧ������δ��롿
		String str_tablename = pub_Templet_1_ItemVO.getPub_Templet_1VO().getSavedtablename(); //
		String str_itemkey = pub_Templet_1_ItemVO.getItemkey(); //
		String str_itemname = pub_Templet_1_ItemVO.getItemname(); //
		String str_itemtype = pub_Templet_1_ItemVO.getItemtype(); //
		new MetaDataUIUtil().showAddColumnPanel(this, str_tablename, str_itemkey, str_itemname, str_itemtype);
	}

	private void delDb() {//��sunfujun/20120508/��ģ������ɾ���ֶε��Ż�_xch��
		if (this.pub_Templet_1_ItemVO.getPub_Templet_1VO() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "ֱ���ɴ��봴��,���ɱ༭!"); //
			return;
		}
		String str_tablename = pub_Templet_1_ItemVO.getPub_Templet_1VO().getSavedtablename(); //
		String str_itemkey = pub_Templet_1_ItemVO.getItemkey(); //
		String str_itemname = pub_Templet_1_ItemVO.getItemname(); //
		new MetaDataUIUtil().showDropColumnPanel(this, str_tablename, str_itemkey, str_itemname);
	}

	private void updateDb() {
		try {

			if (this.pub_Templet_1_ItemVO.getPub_Templet_1VO() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename().trim().equals("")) {
				JOptionPane.showMessageDialog(this, "ֱ���ɴ��봴��,���ɱ༭!"); //
				return;
			}

			String str_tablename = pub_Templet_1_ItemVO.getPub_Templet_1VO().getSavedtablename(); //
			String str_itemkey = pub_Templet_1_ItemVO.getItemkey(); //
			String str_itemname = pub_Templet_1_ItemVO.getItemname(); //

			JPanel jPanel_1 = new JPanel();
			JLabel jLabel1 = new JLabel(str_itemname);
			JTextField jTextField1 = new JTextField();
			jPanel_1.setLayout(null);
			jLabel1.setBounds(10, 10, 60, 20);
			jTextField1.setBounds(80, 10, 300, 20);
			jPanel_1.add(jLabel1);
			jPanel_1.add(jTextField1);

			jPanel_1.setPreferredSize(new Dimension(500, 100));
			if (JOptionPane.showConfirmDialog(this, jPanel_1, "�п��ٸ�ֵ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			UIUtil.executeUpdateByDS(null, "update " + str_tablename + " set " + str_itemkey + "=" + jTextField1.getText());

			MessageBox.show("�и�ֵ�ɹ�!");

		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	private void flowDb() {
		try {

			if (this.pub_Templet_1_ItemVO.getPub_Templet_1VO() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename().trim().equals("")) {
				JOptionPane.showMessageDialog(this, "ֱ���ɴ��봴��,���ɱ༭!"); //
				return;
			}
			String str_tablename = pub_Templet_1_ItemVO.getPub_Templet_1VO().getSavedtablename(); //
			UIUtil.executeUpdateByDS(null, "alter table " + str_tablename + " add wfprinstanceid varchar(100)");
			UIUtil.executeUpdateByDS(null, "alter table " + str_tablename + " add billtype varchar(100)");
			UIUtil.executeUpdateByDS(null, "alter table " + str_tablename + " add busitype varchar(100)");
			MessageBox.show("�ڱ�[" + str_tablename + "]�����ӹ������гɹ�!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	private void flowTemplet() {
		try {

			if (this.pub_Templet_1_ItemVO.getPub_Templet_1VO() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename() == null || pub_Templet_1_ItemVO.getPub_Templet_1VO().getTablename().trim().equals("")) {
				JOptionPane.showMessageDialog(this, "ֱ���ɴ��봴��,���ɱ༭!"); //
				return;
			}
			String str_id = pub_Templet_1_ItemVO.getId(); //
			HashVO[] haid = UIUtil.getHashVoArrayByDS(null, "select pk_pub_templet_1 from pub_templet_1_item where pk_pub_templet_1_item='" + str_id + "'");
			String pkid = haid[0].getStringValue("pk_pub_templet_1");
			String seq_number1 = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1_item");
			HashVO[] hasovo = UIUtil.getHashVoArrayByDS(null, "select showorder from pub_templet_1_item where pk_pub_templet_1='" + pkid + "' order by showorder desc");
			int i = hasovo[0].getIntegerValue("showorder");
			StringBuffer sb_2 = new StringBuffer();
			sb_2.append("insert into pub_templet_1_item ");
			sb_2.append("( ");
			sb_2.append("pk_pub_templet_1_item, ");
			sb_2.append("pk_pub_templet_1, ");

			sb_2.append("itemkey, ");
			sb_2.append("itemname, ");
			sb_2.append("itemname_e, ");
			sb_2.append("itemtype, ");

			//	sb_2.append("comboxdesc, ");
			//	sb_2.append("refdesc, ");
			sb_2.append("issave, ");
			sb_2.append("isdefaultquery, ");

			sb_2.append("ismustinput, ");
			//	sb_2.append("loadformula, ");
			//	sb_2.append("editformula, ");
			sb_2.append("showorder, ");

			//	sb_2.append("showbgcolor, ");
			sb_2.append("listwidth, ");
			sb_2.append("cardwidth, ");
			sb_2.append("listisshowable, ");

			sb_2.append("listiseditable, ");
			sb_2.append("cardisshowable, ");
			sb_2.append("cardiseditable, ");
			//	sb_2.append("defaultvalueformula, ");
			sb_2.append("iswrap");
			//	sb_2.append("grouptitle");

			sb_2.append(") ");
			sb_2.append(" values");
			sb_2.append("( ");

			sb_2.append(seq_number1 + ",'");
			sb_2.append(pkid + "',");
			sb_2.append("'wfprinstanceid', ");
			sb_2.append("'wfprinstanceid', ");
			sb_2.append("'wfprinstanceid', ");

			sb_2.append("'�ı���', ");

			sb_2.append("'Y',");
			sb_2.append("'2',");
			sb_2.append("'N',");

			//sb_2.append(""+null+",");
			//sb_2.append(""+null+",");
			sb_2.append(i + 1 + ",");
			//sb_2.append(""+null+",");
			sb_2.append("145,");
			sb_2.append("'138',");
			sb_2.append("'Y',");

			sb_2.append("'1',");
			sb_2.append("'Y',");
			sb_2.append("'1',");

			//sb_2.append(""+null+",");
			//sb_2.append(""+null+",");
			sb_2.append("'N'");
			//sb_2.append(""+null+",");

			sb_2.append(")");

			UIUtil.executeUpdateByDS(null, sb_2.toString());

			String seq_number2 = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1_item");
			HashVO[] hasovo1 = UIUtil.getHashVoArrayByDS(null, "select showorder from pub_templet_1_item where pk_pub_templet_1 ='" + pkid + "' order by showorder desc");
			int j = hasovo1[0].getIntegerValue("showorder");
			StringBuffer sb_21 = new StringBuffer();
			sb_21.append("insert into pub_templet_1_item ");
			sb_21.append("( ");
			sb_21.append("pk_pub_templet_1_item, ");
			sb_21.append("pk_pub_templet_1, ");

			sb_21.append("itemkey, ");
			sb_21.append("itemname, ");
			sb_21.append("itemname_e, ");
			sb_21.append("itemtype, ");
			sb_21.append("issave, ");
			sb_21.append("isdefaultquery, ");
			sb_21.append("ismustinput, ");
			sb_21.append("showorder, ");
			sb_21.append("listwidth, ");
			sb_21.append("cardwidth, ");
			sb_21.append("listisshowable, ");
			sb_21.append("listiseditable, ");
			sb_21.append("cardisshowable, ");
			sb_21.append("cardiseditable, ");
			sb_21.append("iswrap");
			sb_21.append(") ");
			sb_21.append(" values");
			sb_21.append("( ");
			sb_21.append(seq_number2 + ",'");
			sb_21.append(pkid + "',");
			sb_21.append("'billtype', ");
			sb_21.append("'billtype', ");
			sb_21.append("'billtype', ");
			sb_21.append("'�ı���', ");
			sb_21.append("'Y',");
			sb_21.append("'2',");
			sb_21.append("'N',");
			sb_21.append(j + 1 + ",");
			sb_21.append("145,");
			sb_21.append("'138',");
			sb_21.append("'Y',");
			sb_21.append("'1',");
			sb_21.append("'Y',");
			sb_21.append("'1',");
			sb_21.append("'N'");
			sb_21.append(")");

			UIUtil.executeUpdateByDS(null, sb_21.toString());

			String seq_number3 = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1_item");
			String seq_number31 = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1");
			HashVO[] hasovo2 = UIUtil.getHashVoArrayByDS(null, "select showorder from pub_templet_1_item where pk_pub_templet_1='" + pkid + "' order by showorder desc");
			int k = hasovo2[0].getIntegerValue("showorder");
			StringBuffer sb_31 = new StringBuffer();
			sb_31.append("insert into pub_templet_1_item ");
			sb_31.append("( ");
			sb_31.append("pk_pub_templet_1_item, ");
			sb_31.append("pk_pub_templet_1, ");

			sb_31.append("itemkey, ");
			sb_31.append("itemname, ");
			sb_31.append("itemname_e, ");
			sb_31.append("itemtype, ");
			sb_31.append("issave, ");
			sb_31.append("isdefaultquery, ");
			sb_31.append("ismustinput, ");
			sb_31.append("showorder, ");
			sb_31.append("listwidth, ");
			sb_31.append("cardwidth, ");
			sb_31.append("listisshowable, ");
			sb_31.append("listiseditable, ");
			sb_31.append("cardisshowable, ");
			sb_31.append("cardiseditable, ");
			sb_31.append("iswrap");
			sb_31.append(") ");
			sb_31.append(" values");
			sb_31.append("( ");
			sb_31.append(seq_number3 + ",'");
			sb_31.append(pkid + "',");
			sb_31.append("'busitype', ");
			sb_31.append("'busitype', ");
			sb_31.append("'busitype', ");
			sb_31.append("'�ı���', ");
			sb_31.append("'Y',");
			sb_31.append("'2',");
			sb_31.append("'N',");
			sb_31.append(j + 1 + ",");
			sb_31.append("145,");
			sb_31.append("'138',");
			sb_31.append("'Y',");
			sb_31.append("'1',");
			sb_31.append("'Y',");
			sb_31.append("'1',");
			sb_31.append("'N'");
			sb_31.append(")");

			UIUtil.executeUpdateByDS(null, sb_31.toString());

			MessageBox.show("��ģ���Ϲ������гɹ�!");

		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	private void showUITrueData() {
		if (this.billPanel != null) {
			if (billPanel instanceof BillCardPanel) {
				BillCardPanel billCardPanel = (BillCardPanel) billPanel; //
				billCardPanel.showUIRecord(); //
			}
		}
	}

	private void showDBTrueData() {
		if (this.billPanel != null) {
			if (billPanel instanceof BillCardPanel) {
				BillCardPanel billCardPanel = (BillCardPanel) billPanel; //
				billCardPanel.showDBRecordData(); //
			}
		}
	}

	private void onLookHelp() {
		String str_tiptext = pub_Templet_1_ItemVO.getItemtiptext(); //
		if (str_tiptext == null || str_tiptext.trim().equals("")) {
			str_tiptext = pub_Templet_1_ItemVO.getItemname(); //

		}
		if (str_tiptext == null) {
			str_tiptext = "";
		}
		JEditorPane editPanel = new JEditorPane("text/html", str_tiptext); //
		editPanel.setFont(LookAndFeel.font); //
		editPanel.setEditable(false); //
		editPanel.setBackground(LookAndFeel.systembgcolor); //
		BillDialog dialog = new BillDialog(this, "�鿴��" + pub_Templet_1_ItemVO.getItemname() + "������", 800, 200); //���ڸ��㡾xch/2012-03-07��
		dialog.getContentPane().add(new JScrollPane(editPanel)); ////
		dialog.setVisible(true); //	
	}

	private void lookconfig(JLabel _label) {
		MessageBox.showTextArea(this, getConfigStr(_label)); //
	}

	private String getConfigStr(JLabel _label) {
		StringBuffer sb_config = new StringBuffer();
		//sb_config.append("<html><body>");
		sb_config.append("��ǩ�ı�:[" + _label.getText() + "]\r\n"); //
		sb_config.append("ģ�����:[" + this.pub_Templet_1_ItemVO.getPub_Templet_1VO().getTempletcode() + "]\r\n"); //templetCode
		sb_config.append("ģ������:[" + (this.pub_Templet_1_ItemVO.getPub_Templet_1VO().getTempletname() == null ? "" : this.pub_Templet_1_ItemVO.getPub_Templet_1VO().getTempletname()) + "]\r\n"); //templetName

		sb_config.append("\r\n"); //

		sb_config.append("ItemKey:[" + this.pub_Templet_1_ItemVO.getItemkey() + "]\r\n"); //
		sb_config.append("ItemName:[" + this.pub_Templet_1_ItemVO.getItemname() + "]\r\n"); //
		sb_config.append("ItemName_e:[" + this.pub_Templet_1_ItemVO.getItemname_e() + "]\r\n"); //
		sb_config.append("ItemTipText:[" + this.pub_Templet_1_ItemVO.getItemtiptext() + "]\r\n"); //
		sb_config.append("ItemType:" + this.pub_Templet_1_ItemVO.getItemtype() + "\r\n"); //

		sb_config.append("��������:" + (this.pub_Templet_1_ItemVO.getComboxdesc() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getComboxdesc()) + "\r\n"); //
		sb_config.append("���ն���:" + (this.pub_Templet_1_ItemVO.getRefdesc() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getRefdesc()) + "\r\n"); //
		sb_config.append("�����Ӷ���:" + (this.pub_Templet_1_ItemVO.getHyperlinkdesc() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getHyperlinkdesc()) + "\r\n"); //
		sb_config.append("���ع�ʽ:" + (this.pub_Templet_1_ItemVO.getLoadformula() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getLoadformula()) + "\r\n"); //
		sb_config.append("�༭��ʽ:" + (this.pub_Templet_1_ItemVO.getEditformula() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getEditformula()) + "\r\n"); //
		sb_config.append("Ĭ��ֵ��ʽ:" + (this.pub_Templet_1_ItemVO.getDefaultvalueformula() == null ? "" : "\r\n" + this.pub_Templet_1_ItemVO.getDefaultvalueformula()) + "\r\n"); //

		sb_config.append("\r\n******************* ��ʽ�������CommUCDefineVO����ֵ *************************\r\n"); //
		CommUCDefineVO ucDfVO = pub_Templet_1_ItemVO.getUCDfVO(); //
		if (ucDfVO != null) {
			sb_config.append("CommUCDefineVO������=[" + ucDfVO.getTypeName() + "]\r\n"); //
			String[] str_keys = ucDfVO.getAllConfKeys(); //
			if (str_keys.length <= 0) {
				sb_config.append("CommUCDefineVOû��һ������!!\r\n"); //
			} else {
				for (int i = 0; i < str_keys.length; i++) {
					sb_config.append("[" + str_keys[i] + "]=[" + ucDfVO.getConfValue(str_keys[i]) + "]\r\n");
				}
			}
		} else {
			sb_config.append("CommUCDefineVOֵΪ��!\r\n"); //
		}

		sb_config.append("\r\n******************* ��ʽ�������QueryCommUCDefineVO����ֵ *************************\r\n"); //
		CommUCDefineVO queryUcDfVO = pub_Templet_1_ItemVO.getQueryUCDfVO(); //
		if (queryUcDfVO != null) {
			sb_config.append("QueryCommUCDefineVO������=[" + queryUcDfVO.getTypeName() + "]\r\n"); //
			String[] str_keys = queryUcDfVO.getAllConfKeys(); //
			if (str_keys.length <= 0) {
				sb_config.append("QueryCommUCDefineVOû��һ������!!\r\n"); //
			} else {
				for (int i = 0; i < str_keys.length; i++) {
					sb_config.append("[" + str_keys[i] + "]=[" + queryUcDfVO.getConfValue(str_keys[i]) + "]\r\n");
				}
			}
		} else {
			sb_config.append("QueryCommUCDefineVOֵΪ��!\r\n"); //
		}

		//sb_config.append("</body></html>");
		return sb_config.toString();
	}

	/**
	 * ȡ�ù�����
	 * @return
	 */
	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	//������ͼƬ!!
	private void onExportToPic() {
		try {
			String str_fileName = System.getProperty("ClientCodeCache") + "\\BillCardPanel_" + System.currentTimeMillis() + ".jpg"; //
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			VFlowLayoutPanel vFlowPanel = (VFlowLayoutPanel) cardPanel.getMainPanel(); //
			JPanel printPanel = (JPanel) vFlowPanel.getContentPanel(); ////
			printPanel.setOpaque(true); //��͸��....
			getTBUtil().saveCompentAsPicture(printPanel, str_fileName, true); //���BMP��
			printPanel.setOpaque(false); //��͸��....
			Desktop.open(new File(str_fileName)); //���ò���ϵͳ�Ļ�ͼ�����ͼƬ,Ȼ�����û�ͼ���ߵĹ��ܽ��д�ӡ..
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	//����������ĵ�!!�������������������,����ģ������ú�,ϣ��ֱ�ӽ���ͼ��ģ���񵼳��������Ա��Ҫ���ĵ�!!!
	//�������������ƽ׶���,�������Ч��,ֱ������Word�ĵ�!!!��xch/2012-05-07��
	//�����ʴ�Ŀǰ��Ҫ���޸� [2012-05-14]
	//�����ʴ������ְѵ�������ĵ���ʱ�޸����¡��Ժ��ٸ�ɿ����õġ�[����2012-06-26]
	private void onExportToDesignDoc() {
		try {
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			VFlowLayoutPanel vFlowPanel = (VFlowLayoutPanel) cardPanel.getMainPanel(); //
			JPanel printPanel = (JPanel) vFlowPanel.getContentPanel(); ////
			printPanel.setOpaque(true); //��͸��....
			Rectangle rec = printPanel.getBounds(); //
			String str_imgXml = getTBUtil().getCompent64Code(printPanel, rec.width, rec.height); //ȡ��ͼƬ����
			printPanel.setOpaque(false); //��͸��....

			//ȡ��ģ���е�����!!!
			ArrayList al_datas = new ArrayList(); //
			//			al_datas.add(new String[] { "�ֶ�����","Ӣ������","����","�ֶ�����" ,"�ֶγ���", "��������", "�Ƿ����", "��ע" }); // "�ؼ�����" 
			al_datas.add(new String[] { "���", "��������", "Ӣ������", "����", "����", "�ǿ�", "��ע" }); // "�ؼ�����"
			Pub_Templet_1VO templetVO = pub_Templet_1_ItemVO.getPub_Templet_1VO(); //
			Pub_Templet_1_ItemVO[] itemVOs = templetVO.getItemVos(); //

			TableDataStruct tabledata = UIUtil.getTableDataStructByDS(null, " select * from " + templetVO.getTablename() + " where 1=2");
			Map table_item_row = new HashMap();
			String[] str_name = tabledata.getHeaderName();
			String[] str_type = tabledata.getHeaderTypeName();
			int[] li_length = tabledata.getHeaderLength(); //
			int[] li_precision = tabledata.getPrecision();
			int[] li_scale = tabledata.getScale();
			for (int i = 0; i < str_name.length; i++) {
				table_item_row.put(str_name[i].toUpperCase(), i);
			}
			for (int i = 0; i < itemVOs.length; i++) { //comboxdesc,refdesc,ismustinput
				String str_itemKey = itemVOs[i].getItemkey(); //
				String str_itemName = itemVOs[i].getItemname(); //
				String str_itemType = itemVOs[i].getItemtype(); //
				String item_database_type = "";
				String str_comboxDesc = itemVOs[i].getComboxdesc(); //��������!
				String str_refDesc = itemVOs[i].getRefdesc(); //���ն���!
				String str_isMustInput = itemVOs[i].getIsmustinput2(); //����������!
				str_isMustInput = "Y".equals(str_isMustInput) ? "��" : "";
				String str_specs = ""; //�ʴ��ͻ�Ҫ �������ƣ���ʵ����ϵͳ�Զ����ɣ����ݵ��룬�ֹ�¼��
				String li_itemWidth = itemVOs[i].getSaveLimit() + "";
				boolean isShowable = itemVOs[i].getCardisshowable(); //�Ƿ���ʾ??
				if (str_itemKey.equalsIgnoreCase("ID") || itemVOs[i].isPrimaryKey() || !isShowable) {
					str_specs = "�Զ�����";
				} else {
					if (str_itemType == null || str_itemType.equals("")) {
						str_specs = "�ֹ�¼��";
					} else if ("��������ֿ��ı���".contains(str_itemType) || str_itemType.contains("�ı���") || str_itemType.equals("�ļ�ѡ���") || str_itemType.equals(WLTConstants.COMP_OFFICE) || str_itemType.equals(WLTConstants.COMP_EXCEL)) {
						str_specs = "�ֹ���д";
					} else if (str_itemType.contains("����") || str_itemType.contains("������") || str_itemType.contains("�ӱ�") || "����ʱ��".contains(str_itemType) || str_itemType.equals(WLTConstants.COMP_CHECKBOX)) {
						str_specs = "���ѡ��";
					}
				}
				String str_desc = (str_comboxDesc == null ? "" : str_comboxDesc) + (str_refDesc == null ? "" : str_refDesc); //
				if (!str_itemType.equals("Label") && !str_itemType.equals("��ť")) { //
					if (table_item_row.containsKey(str_itemKey.toUpperCase())) {
						int num = (Integer) table_item_row.get(str_itemKey.toUpperCase());
						if (str_type[num].equalsIgnoreCase("DECIMAL") || str_type[num].equalsIgnoreCase("NUMBER")) {
							item_database_type = str_type[num];
							li_itemWidth = li_precision[num] + "," + li_scale[num];
						} else {
							item_database_type = str_type[num];
							li_itemWidth = li_length[num] + "";
						}
					} else { //���ݿ���û�еĻ���˵�������뱣�档
						li_itemWidth = "";
					}
					String[] str_data = new String[] { i + 1 + "", str_itemName, str_itemKey, item_database_type, li_itemWidth, str_isMustInput, (isShowable ? "" : "����") }; //str_desc
					al_datas.add(str_data); //
				}
			}
			String[][] str_datas = (String[][]) al_datas.toArray(new String[0][0]); //���!!
			String str_wordFileXml = new WordFileUtil().getWorldFileXmlByImageAndTable(str_imgXml, rec.width, rec.height, str_datas); //�����ļ�������!!

			//���д�ļ�!!!
			String str_fileName = System.getProperty("ClientCodeCache") + "\\BillCardPanel_" + System.currentTimeMillis() + ".doc"; //
			getTBUtil().writeStrToOutputStream(new FileOutputStream(str_fileName, false), str_wordFileXml); //

			Desktop.open(new File(str_fileName)); //���ļ�!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ������Html
	 */
	private void onExportToHtml() {
		try {
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			cardPanel.exportToHTml(); //������Html
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ����html��������ģ���ӱ��е�cardisexport����
	 */
	private void onExportToReportHtml() {
		try {
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			cardPanel.exportToReportHtml();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ����word��������ģ���ӱ��е�cardisexport����
	 */
	private void onExportToReportWord() {
		try {
			BillCardPanel cardPanel = (BillCardPanel) billPanel; //
			cardPanel.exportToReportWord();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	public BillPanel getBillPanel() {
		return billPanel;
	}

	public void setBillPanel(BillPanel billPanel) {
		this.billPanel = billPanel;
	}

	//�����齨�������ͳ�������
	protected void addComponentUndAndRedoFunc(JTextComponent textComp) {
		final UndoManager undo = new UndoManager();
		Document doc = textComp.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});

		//�����¼�����
		textComp.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canUndo()) {
					undo.undo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Z
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "Undo");
		//�����¼�����
		textComp.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canRedo()) {
					undo.redo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Y
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), "Redo");
	}

	private void showItemState() {
		try {
			if (this.billPanel != null) {
				if (billPanel instanceof BillCardPanel) {
					BillCardPanel cardPanel = (BillCardPanel) billPanel;
					new MetaDataUIUtil().showItemState(cardPanel, cardPanel.getTempletVO().getItemVos());
				} else if (billPanel instanceof BillQueryPanel) {
					BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
					new MetaDataUIUtil().showItemState(queryPanel, queryPanel.getTempletVO().getItemVos());
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(billPanel, ex); //
			return; //
		}
	}
}
