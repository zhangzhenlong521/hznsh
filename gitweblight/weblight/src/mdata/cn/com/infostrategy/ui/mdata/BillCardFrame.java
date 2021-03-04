/**************************************************************************
 * $RCSfile: BillCardFrame.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class BillCardFrame extends BillFrame implements ActionListener,
		ChangeListener {

	private static final long serialVersionUID = 1L;

	String str_templete_code = null;
	public BillCardPanel billcardPanel = null;
	private BillHtmlPanel htmlPanel = null; // 
	protected WLTButton btn_save, btn_confirm, btn_cancel;
	protected int closeType = -1;
	protected BillVO billVO = null;
	private WLTRadioPane tabbedPane = null;
	private boolean bo_isLoadHtml = false;
	private TBUtil tbUtil = null; //
	private Container parent = null; // ˢ�¸�ҳ���ģ���õ� Ԭ���� 20130313

	public BillCardFrame(Container _parent, String _code) {
		super(_parent, "��������"); //
		this.parent = _parent;
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
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

	public BillCardFrame(Container _parent, String _code, Object[] _objs) {
		super(_parent, "���ٿ�Ƭ�鿴", 600, 400); //
		this.parent = _parent;
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_objs);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		locationToCenterPosition();
	}

	public BillCardFrame(Container _parent, String _code, HashMap _map) {
		super(_parent, "���ٿ�Ƭ�鿴", 600, 400); //
		this.parent = _parent;
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_map);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		locationToCenterPosition();
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
	public BillCardFrame(Container _parent, String _title, int _width,
			int _height, AbstractTMO _tmo, String _condition) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
		billcardPanel = new BillCardPanel(_tmo);
		billcardPanel.queryDataByCondition(_condition); //
		billcardPanel.setEditableByEditInit(); //
		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition();
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
	public BillCardFrame(Container _parent, String _title, String _code,
			int _width, int _height) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
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
		locationToCenterPosition();
	}

	public BillCardFrame(Container _parent, String _title, String _code,
			int _width, int _height, BillVO _billvo) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
		billcardPanel = new BillCardPanel(_code);
		billcardPanel.setBillVO(_billvo);
		billcardPanel.setEditableByEditInit();
		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition();
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
	public BillCardFrame(Container _parent, String _title, String _code,
			int _width, int _height, String _edittype) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
		billcardPanel = new BillCardPanel(_code);
		billcardPanel.setEditState(_edittype); //
		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			try {
				billcardPanel.insertRow();
			} catch (WLTAppException e) {
				e.printStackTrace();
			}
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			billcardPanel.setEditableByEditInit(); //
		}

		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition();
	}

	public BillCardFrame(Container _parent, String _title, String _code,
			int _width, int _height, String _edittype, String _condition) {
		super(_parent, _title, _width, _height); //
		this.parent = _parent;
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
		}
		billcardPanel.setEditState(_edittype); //

		this.getContentPane().add(billcardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition();
	}

	/**
	 * 
	 * @param _parent
	 * @param _cardpanel
	 * @param _edittype
	 */
	public BillCardFrame(Container _parent, String _title,
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
	public BillCardFrame(Container _parent, String _title,
			BillCardPanel _cardpanel, String _edittype, boolean _haveHtmlTab) {
		super(_parent, _title); //
		this.parent = _parent;
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

		this.setSize(li_width, li_height); //
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
		if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			billcardPanel.setEditable(false); // ����������ı༭��ʽ��ִ�У�������Щ����ܻ�ɱ༭�������ڴ��������ã�
		}
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		this.addWindowCloseListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				beforeClose(); //
			}
		});
		locationToCenterPosition();
	}

	public BillCardPanel getCardPanel() {
		return billcardPanel;
	}

	public void stateChanged(ChangeEvent e) {
		if (tabbedPane.getSelectIndex() == 1 && !bo_isLoadHtml) {
			String str_html = billcardPanel.getExportHtml();
			htmlPanel.loadHtml(str_html); //
			bo_isLoadHtml = true;
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_save = new WLTButton("����");
		btn_cancel = new WLTButton("�ر�");

		btn_save.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		if (billcardPanel.getEditState().equals("INIT")) {

			panel.add(btn_cancel); //
		} else {
			panel.add(btn_confirm); //
			panel.add(btn_save); //
			panel.add(btn_cancel); //
		}

		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			try {
				if (!billcardPanel.checkValidate()) {
					return;
				}
				billcardPanel.updateData();
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			} //
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		try {
			if (!billcardPanel.checkValidate()) {
				return;
			}
			billcardPanel.updateData(); //
			closeType = 1;
			billVO = billcardPanel.getBillVO(); //
			this.dispose(); //
			// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
			if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
				refreshPraent(this.parent);
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} //
	}
	//��д���׵Ĺر�ҳ�淽��
	@Override
	public void dispose() {
		super.dispose();
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
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
//������Ҫ�����жϣ���Ϊ�е�ϵͳģ�岻�ܹ����أ�������Ҫ���жϸ�ģ���Ƿ����,Ϊ�˷��㲻���ⵥ���������������   20130410   Ԭ����
			int selectRow=billlist.getSelectedRow();
			int pageNo=billlist.getLi_currpage();
			billlist.getSelectedRow();
			if(null!=templete&&!templete.equals("")&&billlist.getStr_realsql()!=null&&(!("1").equals(billlist.getIsRefreshParent()))){//���һ��������ʾ�����billist����Ҫˢ�£���isRefreshParent����Ϊ1����
				billlist.getQuickQueryPanel().removeAll(); // ���ٲ�ѯ ���¼���
				billlist.getQuickQueryPanel().reload(templete);
				billlist.reload();  //billlist���¼���
				billlist.refreshData(); // �Զ���������
				if(billlist.getPageScrollable()&&billlist.getLi_TotalRecordCount()!=0){ //���ֻ��0����¼�򲻽�����ת
					billlist.goToPage(pageNo);
				}
				billlist.setSelectedRow(selectRow);
			}
			/*if (billlist.getTempletVO() != null && billlist.getTempletVO().getAutoLoads() != 0) {
				billlist.refreshData(); // �Զ���������
			}*/
		} else if (_parent instanceof BillQueryPanel) { // ����һ�billquerypanel���Ҽ�--�༭����ģ��
			BillQueryPanel bqp = (BillQueryPanel) _parent;
			BillListPanel blp = bqp.getBillListPanel();
			String templeteCode = bqp.getTempletVO().getTempletcode(); // �Ȼ��ģ�����
			if(blp!=null){//����Ǳ�����billlistpanelΪ��
				blp.getQuickQueryPanel().removeAll();
				blp.getQuickQueryPanel().reload(templeteCode);
				blp.reload();
				blp.refreshData(); // �Զ���������
				/*if (bqp.getTempletVO() != null && bqp.getTempletVO().getAutoLoads() != 0) {
					blp.refreshData(); // �Զ���������
				}*/
			}else{//���Ϊ����
				bqp.removeAll();
				bqp.reload(templeteCode);
				bqp.updateUI();
			}
		} else if (_parent instanceof BillTreePanel) {

		} else if (_parent instanceof BillPropPanel) {
		}
	}

	private void releaseBillHtmlPanel() {
		if (htmlPanel != null) {
			htmlPanel.disPose(); //
			htmlPanel = null;
		}
	}

	private void beforeClose() {
		closeType = 2;
		billVO = null;
		releaseBillHtmlPanel(); //
	}

	public void onCancel() {
		beforeClose(); //
		this.dispose();
		// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
		}
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil; //
	}

	public int getCloseType() {
		return closeType;
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
}
