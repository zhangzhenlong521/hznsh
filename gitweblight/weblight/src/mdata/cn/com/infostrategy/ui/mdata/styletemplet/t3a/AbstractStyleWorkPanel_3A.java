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
 * 描述的是A、B、C三张表，上面一个选择框来选择A，下面左边一个B的billlistpanel，右边一个C的billlistpanel。选择A刷新B，查询出与A关联的数据。选择B刷新C，查询出
 * 与B关联的数据。同时可以维护B与C，自动关联。
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
	private IUIIntercept_3A uiIntercept = null; // ui端拦截器

	/**
	 * 选择A的参照定义显示的字段
	 * @return
	 */
	public abstract String getANameKey();

	/**
	 * 选择A的参照定义 例子："请选择一个资源类型"
	 * @return
	 */
	public abstract String getRefNameA();

	/**
	 * A的模板编码
	 * @return
	 */
	public abstract String getTempletcodeA();

	/**
	 * B的模板编码
	 * @return
	 */
	public abstract String getTempletcodeB();

	/**
	 * C的模板编码
	 * @return
	 */
	public abstract String getTempletcodeC();

	/**
	 * A（可能是视图）表与B表关联字段,A表字段
	 * @return
	 */
	public abstract String getABKey();

	/**
	 * B表与A表关联字段,B表字段
	 * @return
	 */
	public abstract String getBAKey();

	/**
	 * B表与C表关联字段,B表字段
	 * @return
	 */
	public abstract String getBCKey();

	/**
	 * B表与C表关联字段,B表显示字段
	 * @return
	 */
	public abstract String getBCName();

	/**
	 * C表与B表关联字段,C表字段
	 * @return
	 */
	public abstract String getCBKey();

	/**
	 * A的选择框 定义
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
		insertB = new WLTButton("新建");
		deleteB = new WLTButton("删除");
		editB = new WLTButton("修改");
		editB.getBtnDefineVo().setBtntype("列表弹出编辑");
		insertC = new WLTButton("新建");
		deleteC = new WLTButton("删除");
		deleteC.getBtnDefineVo().setBtntype("列表直接删除");
		editC = new WLTButton("修改");
		editC.getBtnDefineVo().setBtntype("列表弹出编辑");
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

		ref = new CardCPanel_Ref("aKey", getRefNameA(), "getListTempletRef(\"" + getTempletcodeA() + "\",\"" + getABKey() + "\",\"" + getANameKey() + "\",\"\",\"可以多选=N;自动查询=Y;\");", WLTConstants.COMP_REFPANEL_LISTTEMPLET, null, null); //
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
		BillCardPanel cardPanel = new BillCardPanel(getTempletcodeB()); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setValueAt(getBAKey(), new RefItemVO(ref.getRefID(), ref.getRefID(), ref.getRefName()));
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(getBillListPanel("B"), cardPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = getBillListPanel("B").newRow(); //
			getBillListPanel("B").setBillVOAt(li_newrow, dialog.getBillVO());
			getBillListPanel("B").setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
		}

	}

	private void onDeleteB() {
		BillVO billvo = getBillListPanel("B").getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showInfo(getBillListPanel("B"), "请选择一条记录进行此操作！");
			return;
		}
		if (MessageBox.showConfirmDialog(this, "删除此项则其子项将一并删除，您确定删除吗?", "提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		getBillListPanel("C").removeAllRows(); //先将资源公式删除，再删除资源！
		getBillListPanel("C").saveData(); //操作数据库
		getBillListPanel("B").removeRow(getBillListPanel("B").getSelectedRow());
		getBillListPanel("B").saveData();
	}

	private void onInsertC() {
		BillVO billvo = getBillListPanel("B").getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showInfo(getBillListPanel("C"), "请选择" + getBillListPanel("B").getTempletVO().getTempletname() + "记录！");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(getTempletcodeC()); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setValueAt(getCBKey(), new RefItemVO(billvo.getRealValue(getBCKey()), billvo.getRealValue(getBCKey()), billvo.getRealValue(getBCName())));
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(getBillListPanel("C"), cardPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = getBillListPanel("C").newRow(); //
			getBillListPanel("C").setBillVOAt(li_newrow, dialog.getBillVO());
			getBillListPanel("C").setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
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
 * 邮储现场回来统一修改
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
