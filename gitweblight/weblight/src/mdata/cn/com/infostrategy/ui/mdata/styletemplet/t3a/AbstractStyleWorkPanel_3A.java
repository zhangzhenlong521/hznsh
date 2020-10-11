/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_3A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t3a;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;
/**
 * ��������A��B��C���ű�����һ��ѡ�����ѡ��A���������һ��B��billlistpanel���ұ�һ��C��billlistpanel��ѡ��Aˢ��B����ѯ����A���������ݡ�ѡ��Bˢ��C����ѯ��
 * ��B���������ݡ�ͬʱ����ά��B��C���Զ�������
 * @author sfj
 *
 */
public abstract class AbstractStyleWorkPanel_3A extends AbstractStyleWorkPanel implements BillCardEditListener, BillListSelectListener {
	private WLTButton insertB = null;
	private WLTButton deleteB = null;
	private WLTButton editB = null;
	private WLTButton insertC = null;
	private WLTButton deleteC = null;
	private WLTButton editC = null;
	private ActionListener btnActionListener = null;
	private CardCPanel_Ref ref = null;
	private IUIIntercept_3A uiIntercept = null; // ui��������

	/**
	 * ѡ��A�Ĳ��ն�����ʾ���ֶ�
	 * @return
	 */
	public abstract String getANameKey();

	/**
	 * ѡ��A�Ĳ��ն��� ���ӣ�"��ѡ��һ����Դ����"
	 * @return
	 */
	public abstract String getRefNameA();

	/**
	 * A��ģ�����
	 * @return
	 */
	public abstract String getTempletcodeA();

	/**
	 * B��ģ�����
	 * @return
	 */
	public abstract String getTempletcodeB();

	/**
	 * C��ģ�����
	 * @return
	 */
	public abstract String getTempletcodeC();

	/**
	 * A����������ͼ������B������ֶ�,A���ֶ�
	 * @return
	 */
	public abstract String getABKey();

	/**
	 * B����A������ֶ�,B���ֶ�
	 * @return
	 */
	public abstract String getBAKey();

	/**
	 * B����C������ֶ�,B���ֶ�
	 * @return
	 */
	public abstract String getBCKey();

	/**
	 * B����C������ֶ�,B����ʾ�ֶ�
	 * @return
	 */
	public abstract String getBCName();

	/**
	 * C����B������ֶ�,C���ֶ�
	 * @return
	 */
	public abstract String getCBKey();

	/**
	 * A��ѡ��� ����
	 * @return
	 */

	private BillListPanel listPanel_A = null; //
	private BillListPanel listPanel_B = null; //
	private BillListPanel listPanel_C = null; //

	public void initialize() {
		initButton();
		this.setLayout(new BorderLayout()); //
		this.add(getNorthPanel(), BorderLayout.NORTH); //
		this.add(getCenterPanel(), BorderLayout.CENTER); //
		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_3A) Class.forName(getUiinterceptor().trim()).newInstance(); //
				uiIntercept.afterInitialize(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private JPanel getCenterPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		panel.add(new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getBillListPanel("B"), getBillListPanel("C")));
		return panel;
	}

	private void initButton() {
		insertB = new WLTButton("�½�");
		deleteB = new WLTButton("ɾ��");
		editB = new WLTButton("�޸�");
		editB.getBtnDefineVo().setBtntype("�б����༭");
		insertC = new WLTButton("�½�");
		deleteC = new WLTButton("ɾ��");
		deleteC.getBtnDefineVo().setBtntype("�б�ֱ��ɾ��");
		editC = new WLTButton("�޸�");
		editC.getBtnDefineVo().setBtntype("�б����༭");
		btnActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (e.getSource() == insertB) {
						onInsertB();
					} else if (e.getSource() == deleteB) {
						onDeleteB();
					} else if (e.getSource() == insertC) {
						onInsertC(); //
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox.showException(AbstractStyleWorkPanel_3A.this, ex); //
				}
			}

		};
		insertB.addActionListener(btnActionListener);
		deleteB.addActionListener(btnActionListener);
		insertC.addActionListener(btnActionListener);
	}

	public BillListPanel getBillListPanel(String which) {
		if ("A".equals(which)) {
			if (listPanel_A == null) {
				listPanel_A = new BillListPanel(getTempletcodeA());
			}
			return listPanel_A;
		} else if ("B".equals(which)) {
			if (listPanel_B == null) {
				listPanel_B = new BillListPanel(getTempletcodeB());
				listPanel_B.addBillListButton(insertB);
				listPanel_B.addBillListButton(deleteB);
				listPanel_B.addBillListButton(editB);
				listPanel_B.repaintBillListButton();
				listPanel_B.addBillListSelectListener(this);
			}
			return listPanel_B;
		} else {
			if (listPanel_C == null) {
				listPanel_C = new BillListPanel(getTempletcodeC());
				listPanel_C.addBillListButton(insertC);
				listPanel_C.addBillListButton(deleteC);
				listPanel_C.addBillListButton(editC);
				listPanel_C.repaintBillListButton();
			}
			return listPanel_C;
		}
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		ref = new CardCPanel_Ref("aKey", getRefNameA(), "getListTempletRef(\"" + getTempletcodeA() + "\",\"" + getABKey() + "\",\"" + getANameKey() + "\",\"\",\"���Զ�ѡ=N;�Զ���ѯ=Y;\");", WLTConstants.COMP_REFPANEL_LISTTEMPLET, null, null); //
		ref.addBillCardEditListener(this);
		panel.add(ref);
		return panel;
	}

	public void onBillCardValueChanged(BillCardEditEvent billcardeditevent) {
		RefItemVO vo = (RefItemVO) billcardeditevent.getNewObject(); //
		String aKeyValue = null;
		if (vo != null) {
			aKeyValue = vo.getItemValue(getABKey());
		}
		if (aKeyValue != null) {
			getBillListPanel("B").QueryDataByCondition(getBAKey() + "=" + aKeyValue); //
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		String _wherecondition = getCBKey() + "=" + billVO.getRealValue(getBCKey());
		getBillListPanel("C").QueryDataByCondition(_wherecondition);
	}

	private void onInsertB() {
		if (ref == null || ref.getRefID() == null) {
			MessageBox.showInfo(getBillListPanel("B"), getRefNameA());
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(getTempletcodeB()); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setValueAt(getBAKey(), new RefItemVO(ref.getRefID(), ref.getRefID(), ref.getRefName()));
		cardPanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(getBillListPanel("B"), cardPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = getBillListPanel("B").newRow(); //
			getBillListPanel("B").setBillVOAt(li_newrow, dialog.getBillVO());
			getBillListPanel("B").setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
		}

	}

	private void onDeleteB() {
		BillVO billvo = getBillListPanel("B").getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showInfo(getBillListPanel("B"), "��ѡ��һ����¼���д˲�����");
			return;
		}
		if (MessageBox.showConfirmDialog(this, "ɾ�������������һ��ɾ������ȷ��ɾ����?", "��ʾ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		getBillListPanel("C").removeAllRows(); //�Ƚ���Դ��ʽɾ������ɾ����Դ��
		getBillListPanel("C").saveData(); //�������ݿ�
		getBillListPanel("B").removeRow(getBillListPanel("B").getSelectedRow());
		getBillListPanel("B").saveData();
	}

	private void onInsertC() {
		BillVO billvo = getBillListPanel("B").getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showInfo(getBillListPanel("C"), "��ѡ��" + getBillListPanel("B").getTempletVO().getTempletname() + "��¼��");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(getTempletcodeC()); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setValueAt(getCBKey(), new RefItemVO(billvo.getRealValue(getBCKey()), billvo.getRealValue(getBCKey()), billvo.getRealValue(getBCName())));
		cardPanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(getBillListPanel("C"), cardPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = getBillListPanel("C").newRow(); //
			getBillListPanel("C").setBillVOAt(li_newrow, dialog.getBillVO());
			getBillListPanel("C").setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
		}
	}

	@Override
	public String getCustBtnPanelName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCanDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCanEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCanInsert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCanWorkFlowDeal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCanWorkFlowMonitor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowsystembutton() {
		// TODO Auto-generated method stub
		return false;
	}
}

/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_3A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: AbstractStyleWorkPanel_3A.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:41:03  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:48  wanggang
 * restore
 *
 * Revision 1.1  2011/04/02 11:43:58  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.10  2010/02/08 11:02:03  sunfujun
 * *** empty log message ***
 *
 * Revision 1.8  2010/02/03 04:55:38  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.7  2010/02/03 04:38:03  sunfujun
 * *** empty log message ***
 *
 * Revision 1.6  2010/02/02 12:38:15  sunfujun
 * *** empty log message ***
 *
 * Revision 1.8  2010/02/02 05:18:32  xuchanghua
 * *** empty log message ***
 *
 **************************************************************************/
