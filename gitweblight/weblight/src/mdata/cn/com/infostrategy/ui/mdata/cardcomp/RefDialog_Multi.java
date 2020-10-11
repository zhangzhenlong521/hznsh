package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ��Ƭ�ж�ѡ���մ���
 * 
 * @author xch
 * 
 */
public class RefDialog_Multi extends AbstractRefDialog implements BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = 2210719677766465354L;
	private Pub_Templet_1VO tmpletVO = null;

	private BillListPanel billListPanel_1 = null;
	private BillListPanel billListPanel_2 = null; // �����Ȩ��

	private String idFildName = null; // Ψһ��ʶ������,Լ�����ǵ�һ��!!
	private String nameFildName = null; // Ψһ��ʶ������,Լ�����ǵ�һ��!!

	private JButton bnt_1, bnt_2, bnt_3, bnt_4 = null;

	private String refId = null;
	private String refName = null;

	private String[] allcolumns = null;
	private HashVO[] hvs = null;
	private String realvalue = null;
	private RefItemVO refinitvalue;
	private int openType = 0; // �ҿ���ʽ
	private String dsname;
	private String sql;

	public RefDialog_Multi(Container _parent, String _title, RefItemVO _refinitvalue, BillPanel _billPanel, CommUCDefineVO _dfvo) {
		super(_parent, _title, _refinitvalue, _billPanel); //
		this.dsname = _dfvo.getConfValue("����Դ"); //
		this.sql = _dfvo.getConfValue("SQL���"); //
		this.refinitvalue = _refinitvalue;
		this.openType = 1;

	}

	public RefDialog_Multi(Container _parent, String _title, RefItemVO _refinitvalue, BillPanel _billPanel, String[] _allcolumns, HashVO[] _vos, String _value) {
		super(_parent, _title, _refinitvalue, _billPanel); //
		this.allcolumns = _allcolumns;
		this.hvs = _vos;
		this.idFildName = _allcolumns[0];
		this.nameFildName = _allcolumns[2]; //
		this.realvalue = _value; //
		this.refinitvalue = _refinitvalue;
		this.openType = 2;

	}

	public void initialize() {
		try {
			if (openType == 1) {
				HashVOStruct hashVOStructs = UIUtil.getHashVoStructByDS(dsname, sql); // ����ȡ����!!!
				allcolumns = hashVOStructs.getHeaderName(); // ���е�����
				hvs = hashVOStructs.getHashVOs(); // ���е�����
				idFildName = allcolumns[0]; // �����ֶ���
				nameFildName = allcolumns[2]; // �����ֶ���
				tmpletVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns)); // Ԫԭģ��VO
			} else if (openType == 2) {
				tmpletVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns)); // Ԫԭģ��VO
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getCenterPanel(), BorderLayout.CENTER); //
		this.getContentPane().add(getSourthPanel(), BorderLayout.SOUTH); //
		if (refinitvalue != null) {
			String hvs_name = refinitvalue.getName();
			String[] str_keys = hvs_name.split(";"); //
			Vector v_data = new Vector();
			String str_compareFieldName = null;
			if (openType == 1) {
				str_compareFieldName = this.nameFildName;
			} else if (openType == 2) {
				str_compareFieldName = this.idFildName;
			}
			for (int i = 0; i < str_keys.length; i++) {
				for (int j = 0; j < hvs.length; j++) {
					if (hvs[j].getStringValue(idFildName).equals(str_keys[i]) || str_keys[i].equals(hvs[j].getStringValue(str_compareFieldName))) {
						v_data.add(hvs[j].deepClone());
					}
				}
			}
			HashVO[] realVOs = (HashVO[]) v_data.toArray(new HashVO[0]);
			getBillListPanel_2().putValue(realVOs);//

		}

		getBillListPanel_1().putValue(hvs); //
		if (realvalue != null) {
			String[] str_keys = realvalue.split(";"); //
			Vector v_data = new Vector();
			BillVO[] billVOs = getBillListPanel_1().getAllBillVOs();

			String str_compareFieldName = null;
			if (openType == 1) {
				str_compareFieldName = this.nameFildName;
			} else if (openType == 2) {
				str_compareFieldName = this.idFildName;
			}
			for (int i = 0; i < str_keys.length; i++) {
				for (int j = 0; j < billVOs.length; j++) {
					if (billVOs[j].getStringValue(str_compareFieldName).equals(str_keys[i])) {
						v_data.add(hvs[j].deepClone());
						billVOs[j].getRowNumberItemVO().setState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
						break;
					}
				}
			}
			HashVO[] realVOs = (HashVO[]) v_data.toArray(new HashVO[0]);
			getBillListPanel_2().putValue(realVOs);//
		}

	}

	private JPanel getCenterPanel() {
		bnt_1 = new JButton(" >>");
		bnt_2 = new JButton(" > ");
		bnt_3 = new JButton(" < ");
		bnt_4 = new JButton(" <<");

		bnt_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_1(); //
			}
		});//

		bnt_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_2(); //
			}
		});//

		bnt_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_3(); //
			}
		});//

		bnt_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_4(); //
			}
		});//

		bnt_1.setPreferredSize(new Dimension(100, 20));
		bnt_2.setPreferredSize(new Dimension(100, 20));
		bnt_3.setPreferredSize(new Dimension(100, 20));
		bnt_4.setPreferredSize(new Dimension(85, 20));

		JPanel panel_temp = WLTPanel.createDefaultPanel(new BorderLayout()); //
		Box rightAndLeftBox = Box.createVerticalBox();
		rightAndLeftBox.add(Box.createGlue());
		rightAndLeftBox.add(bnt_1); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_2); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_3); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_4); //
		rightAndLeftBox.add(Box.createGlue());

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(20));

		JLabel label_left = new JLabel("��������:", SwingConstants.LEFT);
		label_left.setForeground(java.awt.Color.GRAY);
		JPanel left_panel = new JPanel();
		left_panel.setOpaque(false); //
		left_panel.setLayout(new BorderLayout()); //
		left_panel.add(label_left, BorderLayout.NORTH); //
		left_panel.add(getBillListPanel_1(), BorderLayout.CENTER); //

		box.add(left_panel);
		box.add(Box.createHorizontalStrut(20)); ////
		box.add(rightAndLeftBox); ////

		JLabel label_right = new JLabel("ѡ�������:", SwingConstants.LEFT);
		label_right.setForeground(java.awt.Color.GRAY);
		JPanel right_panel = new JPanel();
		right_panel.setOpaque(false); //
		right_panel.setLayout(new BorderLayout()); //
		right_panel.add(label_right, BorderLayout.NORTH); //
		right_panel.add(getBillListPanel_2(), BorderLayout.CENTER); //

		box.add(right_panel);
		box.add(Box.createHorizontalStrut(20)); //

		panel_temp.add(box, BorderLayout.CENTER);
		panel_temp.updateUI(); //
		return panel_temp;
	}

	private JPanel getSourthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		//panel.setOpaque(false); //͸��
		//panel.setLayout(); //
		JButton btn_confirm = new JButton("ȷ��");
		JButton btn_cancel = new JButton("ȡ��");
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	private void onConfirm() {
		BillVO[] vos = getBillListPanel_2().getAllBillVOs(); //
		String str_allkeys = "";
		String str_allnames = "";
		for (int i = 0; i < vos.length; i++) {
			str_allkeys = str_allkeys + vos[i].getStringValue(this.idFildName) + ";";
		}
		for (int i = 0; i < vos.length; i++) {
			str_allnames = str_allnames + vos[i].getStringValue(this.nameFildName) + ";";
		}
		if (!str_allkeys.equals("")) { // �����Ϊ��,��ǰ���ټӷֺ�!!!
			str_allkeys = ";" + str_allkeys;
		}
		refId = str_allkeys;
		refName = str_allnames;
		this.setCloseType(1);
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(2);
		this.dispose(); //
	}

	/**
	 * ����TMO
	 * 
	 * @param _allcolumns
	 * @return
	 */
	private AbstractTMO getTMO(String[] _allcolumns) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // ģ�����,��������޸�

		HashVO[] childVOs = new HashVO[_allcolumns.length];
		for (int i = 0; i < childVOs.length; i++) {
			childVOs[i] = new HashVO();
			childVOs[i].setAttributeValue("itemkey", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemname", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //
			childVOs[i].setAttributeValue("LISTWIDTH", "125"); //
			childVOs[i].setAttributeValue("ISWRAP", "Y"); // �Ƿ���!
			if (_allcolumns[i].endsWith(WLTConstants.STRING_REFPANEL_UNSHOWSIGN)) {
				childVOs[i].setAttributeValue("listisshowable", "N"); //
			} else {
				childVOs[i].setAttributeValue("listisshowable", "Y"); //
			}
		}

		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs); // ����Ԫԭģ������
		return tmo;
	}

	private BillListPanel getBillListPanel_1() {
		if (billListPanel_1 == null) {
			billListPanel_1 = new BillListPanel(this.tmpletVO); //
			billListPanel_1.setItemEditable(false);
			billListPanel_1.getQuickQueryPanel().setVisible(false);
			billListPanel_1.setToolbarVisiable(false);
			billListPanel_1.addBillListMouseDoubleClickedListener(this);
			billListPanel_1.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		return billListPanel_1;
	}

	private BillListPanel getBillListPanel_2() {
		if (billListPanel_2 == null) {
			billListPanel_2 = new BillListPanel(this.tmpletVO); //
			billListPanel_2.setItemEditable(false);
			billListPanel_2.getQuickQueryPanel().setVisible(false);
			billListPanel_2.setToolbarVisiable(false);
			billListPanel_2.addBillListMouseDoubleClickedListener(this);
			billListPanel_2.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		return billListPanel_2;
	}

	/**
	 * �������еĽ�ɫ
	 * 
	 */
	private void onClicked_1() {
		BillVO[] vos = billListPanel_1.getAllBillVOs(); //
		BillVO[] vos_right = billListPanel_2.getAllBillVOs();
		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getStringValue(idFildName); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getStringValue(idFildName).equals(str_id)) {
					iffind = true;
					break;
				}
			}
			if (!iffind) {
				billListPanel_2.addRow(vos[i].deepClone()); //
			}
		}
	}

	/**
	 * ����ѡ�еĽ�ɫ
	 * 
	 */
	private void onClicked_2() {
		BillVO[] vos = billListPanel_1.getSelectedBillVOs(); //
		BillVO[] vos_right = billListPanel_2.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getStringValue(idFildName); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getStringValue(idFildName).equals(str_id)) {
					iffind = true;
					break;
				}
			}
			if (!iffind) {
				billListPanel_2.addRow(vos[i].deepClone()); //
			}
		}
	}

	private void onClicked_3() {
		billListPanel_2.removeSelectedRows(); //
	}

	private void onClicked_4() {
		billListPanel_2.clearTable(); //
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(refId, refId, refName); //	
	}

	public int getInitWidth() {
		return 800;
	}

	public int getInitHeight() {
		return 500;
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getBillListPanel() == billListPanel_1) {
			onClicked_2();
		} else if (_event.getBillListPanel() == billListPanel_2) {
			onClicked_3();
		}
	}
}
