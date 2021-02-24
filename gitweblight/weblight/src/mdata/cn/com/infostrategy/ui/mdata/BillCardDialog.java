/**************************************************************************
 * $RCSfile: BillCardDialog.java,v $  $Revision: 1.19 $  $Date: 2012/11/06 07:48:32 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class BillCardDialog extends BillDialog implements ActionListener,
		ChangeListener {

	private static final long serialVersionUID = 1L;

	String str_templete_code = null;
	public BillCardPanel billcardPanel = null;
	private BillHtmlPanel htmlPanel = null; // 
	protected WLTButton btn_save, btn_confirm, btn_cancel;
	protected BillVO billVO = null;
	private WLTRadioPane tabbedPane = null;

	private boolean isClickSaveButton = false;
	private boolean SaveButton=false;
	private boolean bo_isLoadHtml = false;
	private boolean isRealSave = true; // �Ƿ���������
	private TBUtil tbUtil = null; //
	private Container parent = null; // ˢ�¸�ҳ���ģ���õ� Ԭ���� 20130313
	public int count=0;
	
	public BillCardDialog(Container _parent, String _code) {
		this(_parent, _code, WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	public BillCardDialog(Container _parent, String _code, String _edittype) {
		super(_parent, "���ٿ�Ƭ�鿴"); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code); //
		billcardPanel.setEditState(_edittype); //
		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			try {
				billcardPanel.insertRow();
			} catch (WLTAppException e) {
				e.printStackTrace();
			}
			this.setTitle("����");
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.setTitle("�༭");
			billcardPanel.setEditableByEditInit(); //
		}
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);

		int li_width = (int) billcardPanel.getPreferredSize().getWidth() + 60; //
		int li_height = (int) billcardPanel.getPreferredSize().getHeight() + 120; //
		if (li_width > 1000) {
			li_width = 1000;
		}
		if (li_height > 730) {
			li_height = 730;
		}
		this.setSize(li_width, li_height); //
		locationToCenterPosition();
	}

	public BillCardDialog(Container _parent, String _code, Object[] _objs) {
		super(_parent, "���ٿ�Ƭ�鿴", 600, 400); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_objs);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
	}

	public BillCardDialog(Container _parent, String _code, HashMap _map) {
		super(_parent, "���ٿ�Ƭ�鿴", 600, 400); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_map);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		this.locationToCenterPosition(); //
	}

	/**
	 * ��Ƭ���
	 * 
	 * @param _parent
	 * @param _title
	 * @param _width
	 * @param _height
	 * @param _tmo
	 */
	public BillCardDialog(Container _parent, String _title, int _width,
			int _height, AbstractTMO _tmo, String _condition) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
		billcardPanel = new BillCardPanel(_tmo);
		if(_title.equals("�б༭")){//�б༭
			if (parent instanceof BillCardPanel) {
				((BillCardPanel)parent).setCanRefreshParent(true);   //���� �б༭ģʽҳ�汾��Ҳ��billcarddialg������������Ҫ������
				this.billcardPanel.setCanRefreshParent(true);
			}else  if(parent instanceof BillListPanel){
				((BillListPanel)parent).setCanRefreshParent(true);
				this.billcardPanel.setCanRefreshParent(true);
			}else if(parent instanceof BillQueryPanel){
				this.billcardPanel.setCanRefreshParent(true);
			}
		}
		
		if (_condition != null) {
			billcardPanel.queryDataByCondition(_condition); //
		}
		billcardPanel.setEditableByEditInit(); //
		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * ��Ƭ���
	 * 
	 * @param _parent
	 * @param _title
	 * @param _width
	 * @param _height
	 * @param _tmo
	 */
	public BillCardDialog(Container _parent, String _title, String _code,
			int _width, int _height) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel(_code);
		try {
			billcardPanel.insertRow();
		} catch (WLTAppException e) {
			e.printStackTrace();
		}
		billcardPanel.setEditableByInsertInit(); //
		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	public BillCardDialog(Container _parent, String _title, String _code,
			int _width, int _height, BillVO _billvo) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel(_code);
		billcardPanel.setBillVO(_billvo);
		billcardPanel.setEditableByEditInit();
		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	public BillCardDialog(Container _parent, String _title, String[] _items,
			int _width, int _height) {
		this(_parent, _title, _items, _width, _height, true); //
	}

	public BillCardDialog(Container _parent, String _title, String[] _items,
			int _width, int _height, boolean _isConfirm) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel((String) null, _items); //
		billcardPanel.setEditable(true); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(_isConfirm ? 2 : 1),
				BorderLayout.SOUTH);
	}

	public BillCardDialog(Container _parent, String _title, String[][] _items,
			int _width, int _height) {
		this(_parent, _title, _items, _width, _height, true); // ֻ��ȷ����ť
	}

	/**
	 * �����е���һ��С���,Ȼ��ȷ������! ����Լ���������Ū��Dialog,�и�������У��Ϊ��ʱ,���ܿ�ס.. new String[][] { {
	 * "����", "150" }, { "����", "150" }
	 */
	public BillCardDialog(Container _parent, String _title, String[][] _items,
			int _width, int _height, boolean _isOnlyConfirm) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel((String) null, _items); //
		billcardPanel.setEditable(true); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(_isOnlyConfirm ? 2 : 1),
				BorderLayout.SOUTH);
	}

	/**
	 * ��Ƭ���
	 * 
	 * @param _parent
	 * @param _title
	 * @param _width
	 * @param _height
	 * @param _tmo
	 */
	public BillCardDialog(Container _parent, String _title, String _code,
			int _width, int _height, String _edittype) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel(_code);

		billcardPanel.setEditState(_edittype); //

		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			try {
				billcardPanel.insertRow();
			} catch (WLTAppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			billcardPanel.setEditableByEditInit(); //
		}

		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}
	/**
	 * 
	 * @param _parent
	 * @param _code
	 * @param _edittype
	 * @param _haveHtmlTab
	 * ���ϲ�Ʒ��Ӱ����Ų�  �����Ӵ˷�����zzl 2018-4-9��
	 */
	public BillCardDialog(Container _parent, String _code, String _edittype,boolean _haveHtmlTab) {
		super(_parent, "���ٿ�Ƭ�鿴"); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code); //
		billcardPanel.setEditState(_edittype); //
		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			try {
				billcardPanel.insertRow();
			} catch (WLTAppException e) {
				e.printStackTrace();
			}
			this.setTitle("����");
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.setTitle("�༭");
			billcardPanel.setEditableByEditInit(); //
		}
		
		
		if (_haveHtmlTab) { //�������֧��!
			tabbedPane = new WLTRadioPane(); //
			tabbedPane.setFocusable(false);
			htmlPanel = new BillHtmlPanel(); //
			billcardPanel.getBillCardBtnPanel().setVisible(false); //
			tabbedPane.addTab("�ؼ����", billcardPanel); //
			tabbedPane.addTab("��ӡԤ��", htmlPanel); //
			tabbedPane.setBackground(LookAndFeel.cardbgcolor); //
			tabbedPane.addChangeListener(this);
			this.getContentPane().add(tabbedPane, BorderLayout.CENTER); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		} else {
			this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
			
		}
		

	

		int li_width = (int) billcardPanel.getPreferredSize().getWidth() + 60; //
		int li_height = (int) billcardPanel.getPreferredSize().getHeight() + 120; //
		if (li_width > 1000) {
			li_width = 1000;
		}
		if (li_height > 730) {
			li_height = 730;
		}
		this.setSize(li_width, li_height); //
		locationToCenterPosition();
	}

	public BillCardDialog(Container _parent, String _title, String _code,
			int _width, int _height, String _edittype, String _condition) {
		super(_parent, _title, _width, _height); //
		billcardPanel = new BillCardPanel(_code);

		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			try {
				billcardPanel.insertRow();
			} catch (WLTAppException e) {
				e.printStackTrace();
			}
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			if (_condition != null) {
				billcardPanel.queryDataByCondition(_condition); //
			}
			billcardPanel.setEditableByEditInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			if (_condition != null) {
				billcardPanel.queryDataByCondition(_condition); //
			}
		}
		billcardPanel.setEditState(_edittype); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 
	 * @param _parent
	 * @param _cardpanel
	 * @param _edittype
	 */
	public BillCardDialog(Container _parent, String _title,
			BillCardPanel _cardpanel, String _edittype) {
		this(_parent, _title, _cardpanel, _edittype, false); //
	}

	/**
	 * ���뿨Ƭ���
	 * 
	 * @param _parent
	 * @param _title
	 * @param _cardpanel
	 * @param _edittype
	 * @param _haveHtmlTab
	 */
	public BillCardDialog(Container _parent, String _title,
			BillCardPanel _cardpanel, String _edittype, boolean _haveHtmlTab) {
		super(_parent, _title); //
		this.parent = _parent;   //��Ҫ����Զ�ˢ�¹���
		int li_width = (int) _cardpanel.getPreferredSize().getWidth() + 100;
		if (li_width > 1000) {
			li_width = 1000;
		}

		int li_height = (int) _cardpanel.getPreferredSize().getHeight() + 200;
		int li_height_screen = (int) Toolkit.getDefaultToolkit()
				.getScreenSize().getHeight() - 60;
		if (li_height > li_height_screen) {
			this.setSize(li_width, li_height_screen); //
		} else {
			this.setSize(li_width, li_height); //
		}

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				billVO = billcardPanel.getBillVO(); //
				releaseBillHtmlPanel(); //
			}
		});

		billcardPanel = _cardpanel;
		billcardPanel.setEditState(_edittype); //
		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			billcardPanel.setEditableByEditInit(); //
		}

		boolean isShowTwoStyle = getTBUtil().getSysOptionBooleanValue(
				"��Ƭ����Ƿ�֧�����ַ��", true); // �Ƿ���ʾ���ַ��,һ����"�ؼ����",һ����"Html���",��������Ŀ��Ф���ξ�Ȼ��Ҫ�����ַ��!!
										// ��ʵ�󲿷ֿͻ��Ǹ�ϲ��Html����,��Ф���ξ���ϲ���ؼ����!�ٴ�֤��������Ա��ҵ����Ա�İ��ò�һ��!!
		if (_haveHtmlTab && isShowTwoStyle) { // �������֧��!
			tabbedPane = new WLTRadioPane(); //
			tabbedPane.setFocusable(false);
			htmlPanel = new BillHtmlPanel(); //
			billcardPanel.getBillCardBtnPanel().setVisible(false); //
			tabbedPane.addTab("�ؼ����", billcardPanel); //
			tabbedPane.addTab("Html���", htmlPanel); //
			tabbedPane.setBackground(LookAndFeel.cardbgcolor); //
			tabbedPane.addChangeListener(this);
			this.getContentPane().add(tabbedPane, BorderLayout.CENTER); //
		} else {
			this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		}
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition(); //
	}

	public BillCardPanel getCardPanel() {
		return billcardPanel;
	}

	public void setCardEditable(boolean _editable) {
		billcardPanel.setEditable(_editable); //
	}

	public String getCardItemValue(String _itemKey) {
		return billcardPanel.getRealValueAt(_itemKey); //
	}

	public void setCardItemValue(String _key, String _value) {
		billcardPanel.setRealValueAt(_key, _value); //
	}

	public void stateChanged(ChangeEvent e) {
		if (tabbedPane.getSelectIndex() == 1 && !bo_isLoadHtml) {
			String str_html = billcardPanel.getExportHtml();
			htmlPanel.loadHtml(str_html); //
			bo_isLoadHtml = true;
		}
	}

	private JPanel getSouthPanel() {
		return getSouthPanel(0);
	}

	private JPanel getSouthPanel(int _type) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		if (isRealSave) { // �������Ҫ��������ģ���
			String str_conFirmBtnName = TBUtil.getTBUtil()
					.getSysOptionStringValue("���沢���ذ�ť������", "ȷ��"); //
			btn_confirm = new WLTButton(str_conFirmBtnName); // �ʴ���Ȼϲ���С��ύ��
		} else {
			btn_confirm = new WLTButton("ȷ��"); //
		}
		btn_save = new WLTButton("����");
		btn_cancel = new WLTButton("�ر�");

		btn_save.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		if (_type == 0) { // Ĭ�ϵ��߼�
			if (billcardPanel.getEditState().equals("INIT")) {
				panel.add(btn_cancel); //
			} else {
				panel.add(btn_confirm); //
				panel.add(btn_save); //
				panel.add(btn_cancel); //
			}
		} else if (_type == 1) { // ֻ�С�ȷ������ť
			panel.add(btn_confirm); //
		} else if (_type == 2) { // ͬʱ�С�ȷ������ȡ����������ť
			panel.add(btn_confirm); //
			panel.add(btn_cancel); //
		}
		return panel;
	}

	public void setSaveBtnVisiable(boolean _visiable) {
		if (btn_save != null) {
			btn_save.setVisible(_visiable);
		}
	}

	public void setRealSave(boolean _isRealSave) {
		isRealSave = _isRealSave; //
		if (isRealSave && btn_confirm != null) { // �����Ҫ����,��ȷ����ť�����Ǳ������!
			btn_confirm.setText(TBUtil.getTBUtil().getSysOptionStringValue(
					"���沢���ذ�ť������", "ȷ��")); // //
		}
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			//����ӱ��������� �����/2013-03-26��
			billcardPanel.dealChildTable(true);
			onSave(); // ����
		} else if (e.getSource() == btn_confirm) {
			//����ӱ��������� �����/2013-03-26��
			billcardPanel.dealChildTable(true);
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			//ȡ������ɾ���ӱ��������� �����/2013-03-26��
			billcardPanel.dealChildTable(false);
			onCancel();
		}
	}

	/**
	 * ��дBillDialog�ķ������ر�ʱ�ж��Ƿ񱣴��
	 */
	public void closeMe() {
		//ȡ������ɾ���ӱ��������� �����/2013-03-26��
		billcardPanel.dealChildTable(false);
		if (isClickSaveButton) {
			closeType = 1;
			billVO = billcardPanel.getBillVO(); //
		}
		this.dispose();
		// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ���Ͻǹرհ�ť����
		// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
		}
	}

	public void onSave() {
		try {
			billcardPanel.stopEditing(); //
			if (!billcardPanel.newCheckValidate("save")) {
				return;
			}
			billcardPanel.updateData();
			isClickSaveButton = true;
			MessageBox.show(this, "�������ݳɹ�!!"); //
			count=2;
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} //
	}

	public void onConfirm() {
		try {
			billcardPanel.stopEditing(); //
			if (!billcardPanel.newCheckValidate("submit")) {
				return;
			}
			if (isRealSave) { // �������������ű���,��Ϊ��ʱ�򲢲�����������,���ǽ�����У��,��,����ֵ,Ȼ���Լ����߼�����!!!
				billcardPanel.updateData(); // ����������??
			}
			billVO = billcardPanel.getBillVO(); //
			closeType = 1;
			this.dispose(); //
			// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
			if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
				refreshPraent(this.parent);
			}
			SaveButton=true;
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
	public void refreshPraent(Container _parent) {
		if (_parent instanceof BillCardPanel) {
			BillCardPanel bcp = (BillCardPanel) _parent;
			BillVO billvo = bcp.getBillVO();// �Ȼ��ҳ���ֵ��Ҫ�������¼���
			bcp.reload(bcp.getTempletVO().getTempletcode());
			bcp.setBillVO(billvo);
			bcp.updateUI();
			String edittype=billvo.getEditType();//����ˢ�º��ҳ��״̬
			if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)){
			}else if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)){
				bcp.setEditableByEditInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			}else if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)){
				bcp.setEditableByInsertInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			}
		} else if (_parent instanceof BillListPanel) { // �����billlistpanel����Ҫˢ��
//Ԭ���� 20130313
//˼·��1���ȸ��²�ѯ����ģ��  2���ٸ���billlist��ģ��     3����ͳһˢ��billist�����ݣ�Ĭ��Ϊ�Զ�����    �������Ƿֱ������	
			BillListPanel billlist = (BillListPanel) _parent;
			String templete = billlist.getTempletVO().getTempletcode();
			//billlist.getbil
//������Ҫ�����жϣ���Ϊ�е�ϵͳģ�岻�ܹ����أ�������Ҫ���жϸ�ģ���Ƿ����,Ϊ�˷��㲻���ⵥ���������������   20130410   Ԭ����
			int pageNo=billlist.getLi_currpage();
			int selectRow=billlist.getSelectedRow();
			if(null!=templete&&!templete.equals("")&&billlist.getStr_realsql()!=null&&(!("1").equals(billlist.getIsRefreshParent()))){//���һ��������ʾ�����billist����Ҫˢ�£���isRefreshParent����Ϊ1����
				billlist.getQuickQueryPanel().removeAll(); // ���ٲ�ѯ ���¼���
				billlist.getQuickQueryPanel().reload(templete);
				billlist.reload();  //billlist���¼���
				billlist.refreshData(); // �Զ���������
				if(billlist.getPageScrollable()&&billlist.getLi_TotalRecordCount()!=0){//���ֻ��0����¼�򲻽�����ת
					billlist.goToPage(pageNo);
				}
				billlist.setSelectedRow(selectRow);
			}
		} else if (_parent instanceof BillQueryPanel) { // ����һ�billquerypanel���Ҽ�--�༭����ģ��
			BillQueryPanel bqp = (BillQueryPanel) _parent;
			BillListPanel blp = bqp.getBillListPanel();
			String templeteCode = bqp.getTempletVO().getTempletcode(); // �Ȼ��ģ�����
			if(blp!=null){//����Ǳ�����billlistpanelΪ��
				blp.getQuickQueryPanel().removeAll();
				blp.getQuickQueryPanel().reload(templeteCode);
				blp.reload();
				blp.refreshData(); // �Զ���������
			}else{//���Ϊ����
				bqp.removeAll();
				bqp.reload(templeteCode);
				bqp.updateUI();
			}
		} else if (_parent instanceof BillTreePanel) {

		} else if (_parent instanceof BillPropPanel) {
		}
	}

	public boolean isRealSave() {
		return isRealSave;
	}
	
	public void setBillVO(BillVO billVO) {
		this.billVO = billVO;
	}
	
	private void releaseBillHtmlPanel() {
		if (htmlPanel != null) {
			htmlPanel.disPose(); //
		}
	}

	public void onCancel() {
		if (isClickSaveButton) {
			closeType = 1;
			billVO = billcardPanel.getBillVO(); //
		} else {
			closeType = 2;
			billVO = null;
		}
		releaseBillHtmlPanel(); 
		this.dispose();
		// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȡ����ť�¼�
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
		}
	}
	//��д���׵Ĺر�ҳ�淽��
	@Override
	public void dispose() {
		super.dispose();
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
		}
	}
	public int getCloseType() {
		if(SaveButton){
			return 1;
		}
		if (isClickSaveButton) { // ����ǵ��˱��水ť,��
			return 1;
		}else{
			return closeType; //
		}
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil; //
	}

	public BillVO getBillVO() {
		return billVO;
	}

	public WLTButton getBtn_save() {
		return btn_save;
	}

	public WLTButton getBtn_confirm() {
		return btn_confirm;
	}

	public boolean isClickSaveButton() {
		return isClickSaveButton;
	}

	public void setClickSaveButton(boolean isClickSaveButton) {
		this.isClickSaveButton = isClickSaveButton;
	}

	public BillCardPanel getBillcardPanel() {
		return billcardPanel;
	}

}
