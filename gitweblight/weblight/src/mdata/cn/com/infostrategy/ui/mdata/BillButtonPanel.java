package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTScrollPanel;

/**
 * ��ť�����,�ؼ���,�������ڷ�һ�Ű�ť�����...
 * ��ť�Ƿǳ��ؼ��Ķ���,ϵͳ���е�ҵ���߼����������ն���ͨ����ť������������,��ť���߼������Է�װ�ɸ��ַ����Ժ�����������չ��SOA���񡣼���ť����SOA���񡣵���������һ�ֺ�̨�ķ��񡣰�ťֻ��UI�˷���ĸ��
 * ���õĹ���ͨ�ð�ť����Ԥ���߼������磺�б����������б��������ȡ�����ҵ�����ť��������˵��SOA����ť�����ǽ�һЩ����ҵ����з�װ������鿴��ϵ�ļ�����Ȼ������������á�����һ���Ǵ���ͨ��ť��
 * ��ť���������ɷ�����
 * ��һ����ע�ᰴť,�������ݿ��д洢�ģ�������ͨ�����б�ģ���н������ã����������ɵ�ʵ��Ȩ�����á�Ȩ��Ӧͬʱʵ�ֺ����������������ģʽ��
 * �ڶ�����ֱ��ͨ��API������������ģ���ϵİ�ť�������ģ���а�ť��Ȩ����δ���û��á�Ŀǰ��3�ְ취��1-������Ȩ�ޣ�2-�ڷ��ģ���д���3-ר�Ÿ�һ�ű�ָ��ĳһ�˵���ĳһģ���еİ�ťֻ��˭�ܷ���!! 
 * 
 * �ڰ�ť����д����Ұڷ�һ�Ű�ť,�����ΰڷŵ�,��
 * 
 * Ԫԭģ���еİ�ť����ģ�崴���İ�ť�İڷ���һ��ͷ�۵����⣡�������о�Ӧ�����Ȱڷŷ��ģ�嶨��İ�ť���ٰڷ�Ԫԭģ��İ�ť���������������⣺
 * 1.�Ƿ��ڿ��ٲ�ѯ�����Ϸ������·����ܾ��÷��ģ��İ�ťӦ�����Ϸ��Ŷԡ���������б����ڶ��㣬����û�п��ٲ�ѯ��壬������·��Ŷԡ�
 * 2.���������ģ��2�еĲ㲼��ģʽ���������ťʱ��Ҫȡ�ð�ť�������ĸ���������ʱ�ǲ���壬��ʱ�Ƕ�ҳǩ��
 * 
 * API������ťʱ,Ҳ���Դ���Ĭ���߼��İ�ť,���翨Ƭ��������,ɾ���ȡ�
 * @author xch
 *
 */
public class BillButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BillPanel billPanelFrom = null; //

	private JPanel panel_flow = null; //	
	private ActionListener[] listBtnActionListeners = null; //

	private JepFormulaParse formulaParse = null;
	private Vector vm_btns = new Vector(); //�洢���а�ť����������!!����ť���洢������,Ȼ����ʱ��Ⱦ����!!!�ܹؼ�
	private boolean isScrollable = true; //
	private TBUtil tb = null;

	/**
	 * Ĭ�Ϲ��췽��!
	 */
	public BillButtonPanel() {
		super();
		initialize(); //
	}

	/**
	 * Ĭ�Ϲ��췽��!
	 */
	public BillButtonPanel(boolean _scrollable) {
		super();
		this.isScrollable = _scrollable;
		initialize(); //
	}

	/**
	 * Ĭ�Ϲ��췽��,����ָ�����ĸ�����
	 */
	public BillButtonPanel(BillPanel _billPanel) {
		super();
		billPanelFrom = _billPanel; //
		initialize(); //
	}

	/**
	 * Ĭ�Ϲ��췽��,����ָ�����ĸ�����
	 */
	public BillButtonPanel(BillPanel _billPanel, boolean _scrollable) {
		super();
		billPanelFrom = _billPanel; //
		this.isScrollable = _scrollable;
		initialize(); //
	}

	/**
	 * ���ݰ�ť�������ɰ�ť���
	 * @param _btns
	 * @param _billPanel
	 */
	public BillButtonPanel(ButtonDefineVO[] _btndfvos, BillPanel _billPanel) {
		billPanelFrom = _billPanel; //
		initialize(); //��ʼ��ҳ��
		addBatchDefineButton(_btndfvos);
	}

	public BillButtonPanel(ButtonDefineVO[] _btndfvos, BillPanel _billPanel, boolean _scrollable) {
		billPanelFrom = _billPanel; //
		this.isScrollable = _scrollable;
		initialize(); //��ʼ��ҳ��
		addBatchDefineButton(_btndfvos);
	}

	/**
	 * ֱ��ͨ��һ����ťע����,����һ����ť
	 * ���ݰ�ťע����ȥϵͳ���в����Щ��ť
	 * �д�ʵ��
	 * @param _btnregcodes
	 */
	public BillButtonPanel(String _btnregcodes) {
		initialize(); //��ʼ��ҳ��..
	}

	public BillButtonPanel(String _btnregcodes, boolean _scrollable) {
		this.isScrollable = _scrollable;
		initialize(); //��ʼ��ҳ��..
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); //͸��
		this.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		panel_flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0)); //
		panel_flow.setOpaque(false); //

		if (isScrollable) {
			WLTScrollPanel scroll = new WLTScrollPanel(panel_flow);
			this.add(scroll);
		} else {
			this.add(panel_flow); //
		}
	}

	/**
	 * ����һ����ť
	 */
	private void addBatchDefineButton(ButtonDefineVO[] _btndfvos) {
		if (_btndfvos != null) {
			for (int i = 0; i < _btndfvos.length; i++) {
				WLTButton btn = new WLTButton(_btndfvos[i]);
				addButton(btn); //
			}
		}
	}

	/**
	 * ��ƨ�ɺ�������һ����ť
	 * @param _btn
	 */
	public void addButton(WLTButton _btn) {
		addButton(_btn, true, true, false);
	}

	/**
	 * ����һ����ť
	 * @param _btns
	 */
	public void addBatchButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			addButton(_btns[i]);
		}
	}

	/**
	 * ��ǰ�����һ����ť
	 * @param _btn
	 */
	public void insertButton(WLTButton _btn) {
		addButton(_btn, true, true, true);
	}

	/**
	 * ��ǰ�����һ����ť
	 * @param _btn
	 */
	public void insertBatchButton(WLTButton[] _btns) {
		for (int i = _btns.length - 1; i >= 0; i--) {
			insertButton(_btns[i]);
		}
	}

	/**
	 * ���Ӱ�ť,�ܹؼ�,���Ӱ�ťֻ�����������ڴ������!!�����������������,ֻ�е���paintButton()����������Ⱦ������������!
	 * @param _btn
	 * @param _enable
	 * @param _visiable
	 */
	public void addButton(WLTButton _btn, boolean _enable, boolean _visiable, boolean _isInsert) {
		if (_btn == null) {//�ϲ�������Ŀ��ǰ�Ĵ��뷢�������δ����İ�ť������ָ���쳣���˵��򲻿������ж�һ�¡����/2016-06-24��
			return;
		}
		_btn.setBillButtonPanelFrom(this); //
		if (_btn.getBillPanelFrom() == null) {
			_btn.setBillPanelFrom(this.billPanelFrom);
		}

		if (_isInsert) { //�������ǰ�����
			vm_btns.insertElementAt(_btn, 0); //���������м���,�ȿ�����ǰ�����Ҳ���ԴӺ������!!
		} else {
			vm_btns.add(_btn); //���������м���,�ȿ�����ǰ�����Ҳ���ԴӺ������!!
		}
	}

	/**
	 * ���溯����Ҫ����ˢ��ģ�����ż�õ�����Ҫ��ȥ����ť
	 */
	public void removeAllButtons() {
		vm_btns.removeAllElements();
	}

	/**
	 * ����ť,�����������Ļ���ť,������������.
	 */
	public void paintButton() {
		String[] _text = null;
		if (this.billPanelFrom instanceof BillListPanel) {
			BillListPanel bl = (BillListPanel) billPanelFrom;
			_text = bl.getTempletVO().getListbtnorderdesc();
		}
		paintButtonByOrderSeq(_text); //
	}

	/**
	 *����ĳ��˳�򻭰�ť,����ť֮������˳���! 
	 */
	public void paintButtonByOrderSeq(String[] _texts) {
		panel_flow.removeAll(); //
		panel_flow.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0)); //���ò���,��������
		WLTButton[] btns = getAllButtons(); //�ɵ�˳��
		if (_texts != null) {
			btns = reorderBtns(btns, _texts); //��������
		}

		for (int i = 0; i < btns.length; i++) {
			WLTButton wltBtn = btns[i]; //
			String btnText = wltBtn.getText();
			if (btnText.trim().length() == 0) {
				panel_flow.add(new WLTLabel(btnText));
			}else {
				wltBtn.setEnabled(wltBtn.getBtnDefineVo().isAllowed()); //
				panel_flow.add(wltBtn); //�������밴ť
			}
			
		}

		panel_flow.setOpaque(false);
		//panel_flow.updateUI(); //
	}

	/**
	 * ����_texts�����˳��������ť˳��
	 * _textsֱ��Ϊ��ť���Ƶ�˳��
	 * @param _btns
	 * @param _texts
	 * @return
	 */
	public WLTButton[] reorderBtns(WLTButton[] _btns, String[] _texts) {
		String[] _str = new String[_btns.length];
		HashMap<String, WLTButton> hp = new HashMap<String, WLTButton>();
		for (int i = 0; i < _str.length; i++) {
			_str[i] = _btns[i].getText();
			hp.put(_str[i], _btns[i]);
		}
		getTBUtil().sortStrsByOrders(_str, _texts);
		ArrayList<WLTButton> bts = new ArrayList<WLTButton>();
		for (int i = 0; i < _str.length; i++) {
			bts.add(hp.get(_str[i]));
		}
		return bts.toArray(new WLTButton[0]);
	}

	public TBUtil getTBUtil() {
		if (tb == null) {
			tb = new TBUtil();
		}
		return tb;
	}

	/**
	 * ���ݱ���ȡ�ð�ť
	 * @param _text
	 * @return
	 */
	public WLTButton getButtonByCode(String _code) {
		WLTButton[] allBtns = getAllButtons(); //
		for (int i = 0; i < allBtns.length; i++) {
			if (allBtns[i].getBtnDefineVo().getCode() != null && allBtns[i].getBtnDefineVo().getCode().equals(_code)) { //���ݱ���ȡ��һ����ť
				return allBtns[i];
			}
		}
		return new WLTButton("����İ�ť[" + _code + "]"); //Ϊ�˷�ֹ�쳣,���һ�������ڵİ�ť!!!
	}

	/**
	 * �Ƿ����һ����ť����ʾ��
	 * @return
	 */
	public boolean hasOneButtonVisiable() {
		WLTButton[] allBtns = getAllButtons(); //
		for (int i = 0; i < allBtns.length; i++) {
			if (allBtns[i].isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �õ����а�ť��
	 * @return
	 */
	public WLTButton[] getAllButtons() {
		WLTButton[] btns = (WLTButton[]) vm_btns.toArray(new WLTButton[0]);
		return btns;
	}

	private BillFormatPanel getBillForMatPanelFrom() {
		if (this.billPanelFrom instanceof BillListPanel) {
			return ((BillListPanel) this.billPanelFrom).getLoaderBillFormatPanel(); //
		} else if (this.billPanelFrom instanceof BillTreePanel) {
			return ((BillTreePanel) this.billPanelFrom).getLoaderBillFormatPanel(); //
		} else if (this.billPanelFrom instanceof BillCardPanel) {
			return ((BillCardPanel) this.billPanelFrom).getLoaderBillFormatPanel(); //
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param _code
	 * @param _visiable
	 */
	public void setBillListBtnVisiable(String _code, boolean _visiable) {
		try {
			WLTButton btn = getButtonByCode(_code); //
			if (btn != null && btn.isEnabled()) {
				btn.setVisible(_visiable); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void setAllBillListBtnVisiable(boolean _visiable) {
		WLTButton[] Btns = getAllButtons();
		for (int i = 0; i < Btns.length; i++) {
			if (Btns[i] != null && Btns[i].isEnabled()) {
				Btns[i].setVisible(_visiable);
			}
		}
	}

	@Override
	public void setVisible(boolean flag) {
		setAllBillListBtnVisiable(flag); //
	}

	/**
	 * �����Զ��尴ť,�����Զ���
	 * @param _btn
	 */
	public void addCustButton(JButton _btn) {
		panel_flow.add(_btn); //
		_btn.setBackground(LookAndFeel.btnbgcolor); //
		//_btn.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY));
		panel_flow.updateUI(); //
	}

	private String getArrayToString(String[] _array) {
		if (_array == null) {
			return "";
		}

		String str_return = "";
		for (int i = 0; i < _array.length; i++) {
			str_return = str_return + _array[i];
			if (i != _array.length - 1) {
				str_return = str_return + ";";
			}
		}

		return str_return; //
	}

	/**
	 * �ж�һ���������Ƿ������һ�������е�һ��ֵ
	 * ��ֻҪ��һ���������˾ͷ���True
	 * @param _
	 * @return
	 */
	private boolean beAccord(String[] _str1, String[] _str2) {
		if (_str1 == null || _str1.length == 0 || _str2 == null || _str2.length == 0) {
			return false;
		}

		//�������!!!!
		for (int i = 0; i < _str1.length; i++) {
			for (int j = 0; j < _str2.length; j++) {
				if (_str1[i].equals(_str2[j])) {
					return true; //
				}
			}
		}
		return false;
	}

	public BillPanel getBillPanelFrom() {
		return billPanelFrom;
	}

	public void setBillPanelFrom(BillPanel billPanelFrom) {
		this.billPanelFrom = billPanelFrom;
	}

	public JPanel getPanel_flow() {
		return panel_flow;
	}
	
}
