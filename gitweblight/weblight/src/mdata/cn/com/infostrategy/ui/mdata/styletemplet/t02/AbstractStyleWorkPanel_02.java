/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_02.java,v $  $Revision: 1.24 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t02;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillLevelPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.StyleTempletServiceIfc;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * ģ��ģ��2
 * 
 * @author xch
 * 
 */
public abstract class AbstractStyleWorkPanel_02 extends AbstractWorkPanel implements BillCardEditListener, BillListEditListener, BillListSelectListener, BillListAfterQueryListener {

	private BillFormatPanel formatContentPanel = null; //

	protected BillLevelPanel billLevelPanel = null; // �����
	protected BillListPanel billListPanel = null; // �б�
	protected BillCardPanel billCardPanel = null; // ��Ƭ

	// �ķ����б��ϵİ�ť
	protected WLTButton btn_list_insert = null; // ����
	protected WLTButton btn_list_edit = null; // �޸�
	protected WLTButton btn_list_delete = null; // ɾ��
	protected WLTButton btn_list_browse = null; // ���

	// �ķ��ڿ�Ƭ�ϵİ�ť
	protected WLTButton btn_card_save = null; // ����
	protected WLTButton btn_card_save_return = null; // ���淵��
	protected WLTButton btn_card_save_goon = null; // �������
	protected WLTButton btn_card_return = null; // ����

	protected WLTButton btn_card_previous = null; // ��һ����¼
	protected WLTButton btn_card_next = null; // ��һ����¼
	protected JLabel label_pagedesc = null;

	protected IUIIntercept_02 uiIntercept = null; // ui��������

	public abstract String getTempletcode(); // ģ�����

	private ActionListener buttonActionListener = null;

	private Logger logger = WLTLogger.getLogger(AbstractStyleWorkPanel_02.class); //

	/**
	 * ��ɳ�ʼ��.
	 */
	public void initialize() {
		try {
			formatContentPanel = new BillFormatPanel("getLevel(\"table\",getList(\"" + getTempletcode() + "\"),\"card\",getCard(\"" + getTempletcode() + "\"))"); // ֱ���ù�ʽ����!!
			billLevelPanel = formatContentPanel.getBillLevelPanel(); //
			billListPanel = formatContentPanel.getBillListPanel(); //
			billCardPanel = formatContentPanel.getBillCardPanel(); //
			billCardPanel.addBillCardEditListener(this); //

			btn_list_insert = new WLTButton(WLTConstants.BUTTON_TEXT_INSERT);
			btn_list_edit = new WLTButton(WLTConstants.BUTTON_TEXT_EDIT);
			btn_list_delete = new WLTButton(WLTConstants.BUTTON_TEXT_DELETE);
			btn_list_browse = new WLTButton(WLTConstants.BUTTON_TEXT_BROWSE);

			btn_card_save = new WLTButton(WLTConstants.BUTTON_TEXT_SAVE); //
			btn_card_save_return = new WLTButton(WLTConstants.BUTTON_TEXT_SAVE_RETURN); //
			btn_card_save_goon = new WLTButton("�������"); //
			btn_card_return = new WLTButton(WLTConstants.BUTTON_TEXT_RETURN); //

			btn_card_previous = new WLTButton("��һ��"); //
			btn_card_next = new WLTButton("��һ��"); //
			label_pagedesc = new JLabel(""); //
			label_pagedesc.setForeground(Color.GRAY); //

			buttonActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == btn_list_insert) {
							onInsert();
						} else if (e.getSource() == btn_list_edit) {
							onEdit();
						} else if (e.getSource() == btn_list_delete) {
							onDelete();
						} else if (e.getSource() == btn_list_browse) {
							onList(); //
						} else if (e.getSource() == btn_card_save) {
							onSave();
						} else if (e.getSource() == btn_card_save_return) {
							onSaveReturn();
						} else if (e.getSource() == btn_card_save_goon) {
							onSaveGoon();
						} else if (e.getSource() == btn_card_return) {
							onReturn(); //
						} else if (e.getSource() == btn_card_previous) {
							onPrevious();
						} else if (e.getSource() == btn_card_next) {
							onNext();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.showException(AbstractStyleWorkPanel_02.this, ex); //
					}
				}

			};

			btn_list_insert.addActionListener(buttonActionListener); //
			btn_list_edit.addActionListener(buttonActionListener); //
			btn_list_delete.addActionListener(buttonActionListener); //
			btn_list_browse.addActionListener(buttonActionListener); //

			btn_card_save.addActionListener(buttonActionListener); //
			btn_card_save_return.addActionListener(buttonActionListener); //
			btn_card_save_goon.addActionListener(buttonActionListener); //
			btn_card_return.addActionListener(buttonActionListener); //
			btn_card_previous.addActionListener(buttonActionListener); //
			btn_card_next.addActionListener(buttonActionListener); //

			billListPanel.insertBatchBillListButton(new WLTButton[] { btn_list_insert, btn_list_edit, btn_list_delete, btn_list_browse }); //
			billCardPanel.insertBatchBillCardButton(new WLTButton[] { btn_card_save_goon, btn_card_save, btn_card_save_return, btn_card_return, btn_card_previous, btn_card_next }); //			
			getBillListPanel().repaintBillListButton();
			getBillCarPanel().repaintBillCardButton(); //

			billCardPanel.getBillCardBtnPanel().getPanel_flow().add(label_pagedesc);
			billCardPanel.getBillCardBtnPanel().getPanel_flow().updateUI();
			this.add(formatContentPanel, BorderLayout.CENTER); //

			initUIIntercept(); // ��ʼ���ͻ�����������!!!
			afterInitialize(); // �����������ķ���,���������ʼ���˷�����ʵ�ǳ���Ҫ,��������˵�������е�,�֪Ϊʲôһ��ʼ��Ȼ������û��???�������
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
	}

	private void initUIIntercept() {
		try {
			String str_UIIntercept = getMenuConfMapValueAsStr("UIIntercept"); //
			if (str_UIIntercept != null && !str_UIIntercept.trim().equals("")) { // �����Ϊ��!
				uiIntercept = (IUIIntercept_02) Class.forName(str_UIIntercept).newInstance(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ��ʼ��������Ҫ������,���Ա����า��..
	 */
	public void afterInitialize() throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.afterInitialize(this); //
		}
	}

	public BillListPanel getBillListPanel() throws WLTAppException {
		return billListPanel;
	}

	public BillCardPanel getBillCarPanel() {
		return billCardPanel;
	}

	public boolean isShowsystembutton() {
		return true; // ��ʾϵͳ��ť!!!
	}

	protected void onSaveReturn() {
		if (!onSave()) {// ������治�ɹ���ֱ�ӷ���
			return;
		}
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		setInformation("����ɹ�");
		onReturn();
	}

	protected void onSaveGoon() {
		if (!onSave()) {// ������治�ɹ���ֱ�ӷ���
			return;
		}
		billCardPanel.insertRow();
		billCardPanel.setEditableByInsertInit();
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		setInformation("����ɹ�����");
		updateLabel();
	}

	protected void onCancelReturn() {
		billCardPanel.reset();
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		billListPanel.getTable().clearSelection();
		setInformation("��������");
		onReturn();
	}

	/**
	 * ���������ť���Ķ���!!
	 * 
	 */
	protected void onInsert() {
		try {
			setInformation("������¼");
			billCardPanel.insertRow(); // ���ÿ�Ƭ�ķ������������µ�һ��!!!!һ��ҪҪ���������!!
			billCardPanel.setEditableByInsertInit(); // �������пؼ�����ģ���ж��������ʱ��״̬!!
			btn_card_save.setVisible(true);
			btn_card_save_return.setVisible(true);
			btn_card_save_goon.setVisible(true);
			btn_card_previous.setVisible(false);
			btn_card_next.setVisible(false);
			switchToCard(); // �л�����Ƭ!!
			// ִ������������!!
			if (uiIntercept != null) {
				try {
					uiIntercept.actionAfterInsert(getBillCarPanel()); // ���������
				} catch (Exception e) {
					MessageBox.showException(this, e);
					return; // ����������!!
				}
			}

		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ���ɾ�����Ķ���!!
	 * 
	 */
	protected void onDelete() {
		int li_row = billListPanel.getTable().getSelectedRow(); // ȡ��ѡ�е���!!
		if (li_row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!MessageBox.confirm(this, WLTConstants.STRING_DEL_CONFIRM)) {
			return;
		}

		// ִ��������ɾ��ǰ����!!
		if (uiIntercept != null) {
			try {
				setInformation("ִ��������ɾ��ǰ����");
				uiIntercept.actionBeforeDelete(getBillListPanel(), li_row); // ִ��ɾ��ǰ�Ķ���!!
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return; // ����������!!
			}
		}

		// �ύɾ������!!!
		try {
			BillVO vo = getBillListPanel().getBillVO(li_row); //
			dealDelete(vo); // ����ɾ��
			billListPanel.removeRow(li_row); // ����ɹ�
			setInformation("ɾ����¼�ɹ�");
		} catch (Exception ex) {
			ex.printStackTrace(); //
			setInformation("ɾ����¼ʧ��"); //
			MessageBox.showException(this, ex);
		}

	}

	/**
	 * ����༭���Ķ���!!
	 * 
	 */
	protected void onEdit() {
		int li_row = billListPanel.getTable().getSelectedRow();
		if (li_row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		// ִ������������!!
		if (uiIntercept != null) {
			try {
				uiIntercept.actionBeforeUpdate(getBillCarPanel()); // �޸�ǰ����
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return; // ����������!!
			}
		}
		try {
			setInformation("�޸ļ�¼");
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			billCardPanel.setEditableByEditInit();
			btn_card_save.setVisible(true);
			btn_card_save_return.setVisible(true);
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);
			switchToCard(); // �л�����Ƭ
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ����������Ķ����������Ƿ񱣴�ɹ���
	 * ��ǰ��������治�ɹ�ֱ���׳��쳣���ڵ��ñ�����ʱ�����쳣�������ͻ����������ʾ��һ���ǿ�ƬУ��ʱ����(��:"���벻��Ϊ��!"
	 * )��һ���ǲ����쳣ʱ����(��:"У�鲻�ɹ�!")�� �ָ�Ϊֻ����һ����ʾ�򼴿ɣ����2012-02-23�޸�
	 * 
	 */
	protected boolean onSave() {
		getBillCarPanel().stopEditing(); //
		if (!getBillCarPanel().checkValidate()) {
			return false;
		}

		if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) { // ����������ύ
			BillVO billVO = getBillCarPanel().getBillVO(); //
			try {
				dealInsert(billVO); // �����ύ
				getBillCarPanel().saveKeepTrace();
				setInformation("������¼�ɹ�");
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				HashMap map = billCardPanel.getAllObjectValuesWithHashMap();
				billListPanel.insertRowWithInitStatus(billListPanel.getSelectedRow(), map);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
				return false;
			}
		} else if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // ������޸��ύ
			BillVO billVO = getBillCarPanel().getBillVO(); //
			try {
				dealUpdate(billVO); // �޸��ύ
				getBillCarPanel().saveKeepTrace();
				billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
				billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
				setInformation("�޸ļ�¼�ɹ�");
			} catch (Exception e1) {
				MessageBox.showException(this, e1); //
				return false;
			}
		}
		return true;
	}

	/**
	 * ����
	 * 
	 */
	protected void dealInsert(BillVO _insertVO) throws Exception {
		// ִ�������ύǰ��������
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitBeforeInsert(this, _insertVO);
		}

		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		BillVO returnVO = service.style02_dealInsert(billListPanel.getDataSourceName(), null, _insertVO); // ֱ���ύ���ݿ�,����������쳣!!

		// ִ�������ύ���������
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitAfterInsert(this, returnVO); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * �޸��ύ
	 * 
	 */
	protected void dealUpdate(BillVO _updateVO) throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitBeforeUpdate(this, _updateVO); // �޸��ύǰ������
		}

		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		BillVO returnvo = service.style02_dealUpdate(billListPanel.getDataSourceName(), null, _updateVO); //
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitAfterUpdate(this, returnvo); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
		}
	}

	/**
	 * ɾ���ύ
	 * 
	 */
	protected void dealDelete(BillVO _deleteVO) throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitBeforeDelete(this, _deleteVO);
		}

		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		service.style02_dealDelete(billListPanel.getDataSourceName(), null, _deleteVO); //
		billListPanel.clearDeleteBillVOs();
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitAfterDelete(this, _deleteVO);
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
		}
	}

	/**
	 * �鿴
	 */
	protected void onList() {
		try {
			int li_row = billListPanel.getTable().getSelectedRow(); //
			if (li_row < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			setInformation("�鿴��¼"); //
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			billCardPanel.setRowNumberItemVO((RowNumberItemVO) billListPanel.getValueAt(billListPanel.getSelectedRow(), 0)); // �����к�
			billCardPanel.setEditable(false);
			billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);

			btn_card_save.setVisible(false);
			btn_card_save_return.setVisible(false);
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);

			switchToCard(); // �л�����Ƭ
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ���б�Ϳ�Ƭ���л�
	 */
	protected void onReturn() {
		switchToTable(); //
	}

	public void switchToCard() {
		billLevelPanel.showLevel("card"); //
		updateLabel();
	}

	public void switchToTable() {
		billLevelPanel.showLevel("table"); //
	}

	/**
	 * �鿴��һ����¼
	 */

	private void onNext() {
		try {
			boolean flag = false;
			if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // ������޸��ύ
				flag = true;
				if (getBillCarPanel().checkValidate()) {// ������Ҫ�Կ�Ƭ����У�飬�����������ܾ�©���ˡ����2012-02-23�޸�
					BillVO billVO = getBillCarPanel().getBillVO(); //
					try {
						dealUpdate(billVO); // �޸��ύ
						billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
						billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
					} catch (Exception e1) {
						MessageBox.showException(this, e1); //
						throw e1;
					}
				} else {// ���У�鲻�ɹ���ֱ�ӷ���
					return;
				}
			}
			int rows = billListPanel.getRowCount(); // ��ǰҳ������
			int li_row = billListPanel.getSelectedRow() + 1; // ��ѡ�м�¼����һ����¼���к�
			if (li_row >= rows) {
				if (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()) { // �ж��Ƿ��з�ҳ��
					int currpage = billListPanel.getLi_currpage();
					billListPanel.goToPage(billListPanel.getLi_currpage() + 1, false); // ������һҳ������Ҳ�������Ҫ��ʾ������������ǵ�һҳ������ת��������תǰ��ҳ����ͬ�����2012-02-22�޸�
					int nextpage = billListPanel.getLi_currpage();
					if (currpage == nextpage) {
						MessageBox.showInfo(billCardPanel, "���Ѿ������һ����¼!!");// �������Զ������ʾ�������治��Ҫ��ʾ��
						return;
					} else {
						li_row = 0; // ���ñ�ѡ����Ϊ��ҳ��ĵ�һ�У��к�Ϊ0
					}
				} else {
					MessageBox.showInfo(billCardPanel, "���Ѿ������һ����¼!!");
					return;
				}
			}
			setInformation("�鿴��¼"); //
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			if (flag) {
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				btn_card_save.setVisible(true);
				btn_card_save_return.setVisible(true);
				billCardPanel.setEditableByEditInit(); // ����Ϊģ�����õı༭״̬��һ��Ҫ����Ŷ��
			} else {
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
				btn_card_save.setVisible(false);
				btn_card_save_return.setVisible(false);
				billCardPanel.setEditable(false);
			}
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);

			billListPanel.setSelectedRow(li_row);
			switchToCard(); // �л�����Ƭ
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * �鿴��һ����¼
	 */
	private void onPrevious() {
		try {
			boolean flag = false; // ��¼�ǵ���޸ĺ�鿴���ǵ��Ԥ����鿴
			if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // ������޸��ύ
				if (getBillCarPanel().checkValidate()) {// ������Ҫ�Կ�Ƭ����У�飬�����������ܾ�©���ˡ����2012-02-23�޸�
					flag = true;
					BillVO billVO = getBillCarPanel().getBillVO(); //
					try {
						dealUpdate(billVO); // �޸��ύ
						billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
						billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
					} catch (Exception e1) {
						MessageBox.showException(this, e1); //
						throw e1;
					}
				} else {// ���У�鲻�ɹ���ֱ�ӷ���
					return;
				}
			}
			int li_row = billListPanel.getSelectedRow() - 1; //
			if (li_row < 0) {
				if (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()) { // �ж��Ƿ��з�ҳ��
					int currpage = billListPanel.getLi_currpage();
					billListPanel.goToPage(currpage - 1, false); // ������һҳ������Ҳ�������Ҫ��ʾ������������ǵ�һҳ������ת��������תǰ��ҳ����ͬ�����2012-02-22�޸�
					int previouspage = billListPanel.getLi_currpage();
					if (currpage == previouspage) {
						MessageBox.showInfo(billCardPanel, "���Ѿ��ǵ�һ����¼!!");// �������Զ������ʾ�������治��Ҫ��ʾ��
						return;
					} else {
						li_row = billListPanel.getTable().getRowCount() - 1;
					}
				} else {
					MessageBox.showInfo(billCardPanel, "���Ѿ��ǵ�һ����¼!!");
					return;
				}
			}
			setInformation("�鿴��¼"); //
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //		
			if (flag) {
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				btn_card_save.setVisible(true);
				btn_card_save_return.setVisible(true);

				billCardPanel.setEditableByEditInit();
			} else {
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
				btn_card_save.setVisible(false);
				btn_card_save_return.setVisible(false);
				billCardPanel.setEditable(false);
			}
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);

			billListPanel.setSelectedRow(li_row);
			switchToCard(); // �л�����Ƭ
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ��Ƭ��ʾʱ��������ʾ������JLabel �����������µķ�ҳ����,������²�������,������ע����!!!
	 */
	private void updateLabel() {
		// int currNumber = billListPanel.getSelectedRow() + 1;
		// int datalength = billListPanel.getTable().getRowCount();
		// String pagedesc = billListPanel.getLabel_pagedesc().getText();
		// //��1ҳ,��1ҳ46��,ÿҳ46�� ��1ҳ,��3ҳ46��,ÿҳ20�� ���ڸ�Ϊ ��28��,��1/2ҳ(����:��)
		// ��28��,��1/1ҳ(����:��)
		// if
		// (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()
		// && !"".equals(pagedesc)) { //�ж��Ƿ��з�ҳ��
		// String[] descs = pagedesc.split(",");
		// datalength = Integer.parseInt(descs[0].trim().substring(1,
		// descs[0].trim().length() - 1));// ����������¼
		// String curr_allPages = descs[1].substring(1, descs[1].indexOf("(") -
		// 1);
		// String allPages = curr_allPages.split("/")[1];
		// int lengthPerPage = 0;
		// if ("1".equals(allPages)) {
		// currNumber = billListPanel.getSelectedRow() + 1;
		// } else {
		// int num = datalength / Integer.parseInt(allPages) / 10;
		// if (num == 0) {
		// lengthPerPage = 10;
		// } else if (num == 1) {
		// lengthPerPage = 20;
		// } else {
		// lengthPerPage = 50;
		// }
		// int currpage = billListPanel.getLi_currpage();//��ǰҳ��
		// currNumber = (currpage - 1) * lengthPerPage +
		// billListPanel.getSelectedRow() + 1;
		// }
		// }
		// if (billCardPanel.getEditState() ==
		// WLTConstants.BILLDATAEDITSTATE_INSERT && currNumber == 0) {
		// //δѡ��һ����¼ʱ�����������б�������
		// currNumber = billListPanel.getRowCount() + 1;
		// } else if (billCardPanel.getEditState() ==
		// WLTConstants.BILLDATAEDITSTATE_INSERT) { //ѡ����һ����¼ʱ����������������
		// currNumber = billListPanel.getSelectedRow() + 2;
		// }
		// label_pagedesc.setText("[��" + currNumber + "��/��" + datalength +
		// "��]");
		// billCardPanel.getBillCardBtnPanel().getPanel_flow().updateUI(); //��Ҫ
	}

	/**
	 * ��Ƭ���������
	 */
	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		if (uiIntercept != null) {
			BillCardPanel card_tmp = (BillCardPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiIntercept.actionAfterUpdate(card_tmp, tmp_itemkey);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	public void onBillListValueChanged(BillListEditEvent _evt) {

	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		// BillVO billVO = _event.getCurrSelectedVO();
		// setWorkFlowDealBillVO(billVO); //
	}

	/**
	 * ��ĳ������ѯ����Ҫ�ٴν��д�����߼�...
	 */
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		// setWorkFlowDealBillVO(null); //
	}

	/**
	 * ȡ�ù����������BillVO
	 * 
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getBillListPanel().getSelectedBillVO(); // ����ѡ���е����ݼ��ǹ�������Ҫ���������
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return true;
	}

	public void onWorkFlowDeal(String _dealtype) throws Exception {
		String str_pkvalue = billListPanel.getSelectedBillVO().getStringValue("id"); //
		String str_sql = "select wfprinstanceid from " + billListPanel.getTempletVO().getTablename() + " where id=" + str_pkvalue; //
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(billListPanel.getTempletVO().getDatasourcename(), str_sql); //
		billListPanel.setValueAt(new StringItemVO(hvs[0].getStringValue("wfprinstanceid")), billListPanel.getSelectedRow(), "wfprinstanceid"); //
	}

	private WorkFlowServiceIfc getWFService() throws Exception {
		WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
		return service;
	}

	public BillFormatPanel getFormatContentPanel() {
		return formatContentPanel;
	}

	public BillLevelPanel getBillLevelPanel() {
		return billLevelPanel;
	}

	public BillCardPanel getBillCardPanel() {
		return billCardPanel;
	}

	public WLTButton getBtn_list_insert() {
		return btn_list_insert;
	}

	public WLTButton getBtn_list_edit() {
		return btn_list_edit;
	}

	public WLTButton getBtn_list_delete() {
		return btn_list_delete;
	}

	public WLTButton getBtn_list_browse() {
		return btn_list_browse;
	}

	public WLTButton getBtn_card_save() {
		return btn_card_save;
	}

	public WLTButton getBtn_card_save_return() {
		return btn_card_save_return;
	}

	public WLTButton getBtn_card_save_goon() {
		return btn_card_save_goon;
	}

	public WLTButton getBtn_card_return() {
		return btn_card_return;
	}

	public WLTButton getBtn_card_previous() {
		return btn_card_previous;
	}

	public WLTButton getBtn_card_next() {
		return btn_card_next;
	}

	public JLabel getLabel_pagedesc() {
		return label_pagedesc;
	}

	public IUIIntercept_02 getUiIntercept() {
		return uiIntercept;
	}

	public ActionListener getButtonActionListener() {
		return buttonActionListener;
	}

	public Logger getLogger() {
		return logger;
	}

}
