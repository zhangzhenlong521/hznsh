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
 * 模板模板2
 * 
 * @author xch
 * 
 */
public abstract class AbstractStyleWorkPanel_02 extends AbstractWorkPanel implements BillCardEditListener, BillListEditListener, BillListSelectListener, BillListAfterQueryListener {

	private BillFormatPanel formatContentPanel = null; //

	protected BillLevelPanel billLevelPanel = null; // 层面板
	protected BillListPanel billListPanel = null; // 列表
	protected BillCardPanel billCardPanel = null; // 卡片

	// 寄放在列表上的按钮
	protected WLTButton btn_list_insert = null; // 新增
	protected WLTButton btn_list_edit = null; // 修改
	protected WLTButton btn_list_delete = null; // 删除
	protected WLTButton btn_list_browse = null; // 浏览

	// 寄放在卡片上的按钮
	protected WLTButton btn_card_save = null; // 保存
	protected WLTButton btn_card_save_return = null; // 保存返回
	protected WLTButton btn_card_save_goon = null; // 保存继续
	protected WLTButton btn_card_return = null; // 返回

	protected WLTButton btn_card_previous = null; // 上一条记录
	protected WLTButton btn_card_next = null; // 下一条记录
	protected JLabel label_pagedesc = null;

	protected IUIIntercept_02 uiIntercept = null; // ui端拦截器

	public abstract String getTempletcode(); // 模板编码

	private ActionListener buttonActionListener = null;

	private Logger logger = WLTLogger.getLogger(AbstractStyleWorkPanel_02.class); //

	/**
	 * 完成初始化.
	 */
	public void initialize() {
		try {
			formatContentPanel = new BillFormatPanel("getLevel(\"table\",getList(\"" + getTempletcode() + "\"),\"card\",getCard(\"" + getTempletcode() + "\"))"); // 直接用公式创建!!
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
			btn_card_save_goon = new WLTButton("保存继续"); //
			btn_card_return = new WLTButton(WLTConstants.BUTTON_TEXT_RETURN); //

			btn_card_previous = new WLTButton("上一条"); //
			btn_card_next = new WLTButton("下一条"); //
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

			initUIIntercept(); // 初始化客户端拦截器类!!!
			afterInitialize(); // 调用拦截器的方法,这个后续初始化人方法其实非常重要,甚至可以说是最迫切的,最不知为什么一开始竟然忘记了没加???罪过。。
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
	}

	private void initUIIntercept() {
		try {
			String str_UIIntercept = getMenuConfMapValueAsStr("UIIntercept"); //
			if (str_UIIntercept != null && !str_UIIntercept.trim().equals("")) { // 如果不为空!
				uiIntercept = (IUIIntercept_02) Class.forName(str_UIIntercept).newInstance(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 初始化结束后要做的事,可以被子类覆盖..
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
		return true; // 显示系统按钮!!!
	}

	protected void onSaveReturn() {
		if (!onSave()) {// 如果保存不成功则直接返回
			return;
		}
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		setInformation("保存成功");
		onReturn();
	}

	protected void onSaveGoon() {
		if (!onSave()) {// 如果保存不成功则直接返回
			return;
		}
		billCardPanel.insertRow();
		billCardPanel.setEditableByInsertInit();
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		setInformation("保存成功继续");
		updateLabel();
	}

	protected void onCancelReturn() {
		billCardPanel.reset();
		billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		billListPanel.getTable().clearSelection();
		setInformation("放弃保存");
		onReturn();
	}

	/**
	 * 点击新增按钮做的动作!!
	 * 
	 */
	protected void onInsert() {
		try {
			setInformation("新增记录");
			billCardPanel.insertRow(); // 调用卡片的方法创建创建新的一行!!!!一定要要这个方法做!!
			billCardPanel.setEditableByInsertInit(); // 设置所有控件处于模板中定义的新增时的状态!!
			btn_card_save.setVisible(true);
			btn_card_save_return.setVisible(true);
			btn_card_save_goon.setVisible(true);
			btn_card_previous.setVisible(false);
			btn_card_next.setVisible(false);
			switchToCard(); // 切换到卡片!!
			// 执行拦截器操作!!
			if (uiIntercept != null) {
				try {
					uiIntercept.actionAfterInsert(getBillCarPanel()); // 新增后操作
				} catch (Exception e) {
					MessageBox.showException(this, e);
					return; // 不往下走了!!
				}
			}

		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 点击删除做的动作!!
	 * 
	 */
	protected void onDelete() {
		int li_row = billListPanel.getTable().getSelectedRow(); // 取得选中的行!!
		if (li_row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!MessageBox.confirm(this, WLTConstants.STRING_DEL_CONFIRM)) {
			return;
		}

		// 执行拦截器删除前操作!!
		if (uiIntercept != null) {
			try {
				setInformation("执行拦截器删除前动作");
				uiIntercept.actionBeforeDelete(getBillListPanel(), li_row); // 执行删除前的动作!!
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return; // 不往下走了!!
			}
		}

		// 提交删除数据!!!
		try {
			BillVO vo = getBillListPanel().getBillVO(li_row); //
			dealDelete(vo); // 真正删除
			billListPanel.removeRow(li_row); // 如果成功
			setInformation("删除记录成功");
		} catch (Exception ex) {
			ex.printStackTrace(); //
			setInformation("删除记录失败"); //
			MessageBox.showException(this, ex);
		}

	}

	/**
	 * 点击编辑做的动作!!
	 * 
	 */
	protected void onEdit() {
		int li_row = billListPanel.getTable().getSelectedRow();
		if (li_row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		// 执行拦截器操作!!
		if (uiIntercept != null) {
			try {
				uiIntercept.actionBeforeUpdate(getBillCarPanel()); // 修改前操作
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return; // 不往下走了!!
			}
		}
		try {
			setInformation("修改记录");
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			billCardPanel.setEditableByEditInit();
			btn_card_save.setVisible(true);
			btn_card_save_return.setVisible(true);
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);
			switchToCard(); // 切换到卡片
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 点击保存做的动作，返回是否保存成功。
	 * 以前是如果保存不成功直接抛出异常，在调用本方法时捕获异常，这样就会出现两个提示框，一个是卡片校验时弹出(如:"密码不能为空!"
	 * )，一个是捕获异常时弹出(如:"校验不成功!")。 现改为只弹出一个提示框即可！李春娟2012-02-23修改
	 * 
	 */
	protected boolean onSave() {
		getBillCarPanel().stopEditing(); //
		if (!getBillCarPanel().checkValidate()) {
			return false;
		}

		if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) { // 如果是新增提交
			BillVO billVO = getBillCarPanel().getBillVO(); //
			try {
				dealInsert(billVO); // 新增提交
				getBillCarPanel().saveKeepTrace();
				setInformation("新增记录成功");
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				HashMap map = billCardPanel.getAllObjectValuesWithHashMap();
				billListPanel.insertRowWithInitStatus(billListPanel.getSelectedRow(), map);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
				return false;
			}
		} else if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // 如果是修改提交
			BillVO billVO = getBillCarPanel().getBillVO(); //
			try {
				dealUpdate(billVO); // 修改提交
				getBillCarPanel().saveKeepTrace();
				billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
				billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
				setInformation("修改记录成功");
			} catch (Exception e1) {
				MessageBox.showException(this, e1); //
				return false;
			}
		}
		return true;
	}

	/**
	 * 新增
	 * 
	 */
	protected void dealInsert(BillVO _insertVO) throws Exception {
		// 执行新增提交前的拦截器
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitBeforeInsert(this, _insertVO);
		}

		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		BillVO returnVO = service.style02_dealInsert(billListPanel.getDataSourceName(), null, _insertVO); // 直接提交数据库,这里可能抛异常!!

		// 执行新增提交后的拦截器
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitAfterInsert(this, returnVO); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 修改提交
	 * 
	 */
	protected void dealUpdate(BillVO _updateVO) throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitBeforeUpdate(this, _updateVO); // 修改提交前拦截器
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
	 * 删除提交
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
	 * 查看
	 */
	protected void onList() {
		try {
			int li_row = billListPanel.getTable().getSelectedRow(); //
			if (li_row < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			setInformation("查看记录"); //
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			billCardPanel.setRowNumberItemVO((RowNumberItemVO) billListPanel.getValueAt(billListPanel.getSelectedRow(), 0)); // 设置行号
			billCardPanel.setEditable(false);
			billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);

			btn_card_save.setVisible(false);
			btn_card_save_return.setVisible(false);
			btn_card_save_goon.setVisible(false);
			btn_card_previous.setVisible(true);
			btn_card_next.setVisible(true);

			switchToCard(); // 切换到卡片
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 在列表和卡片间切换
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
	 * 查看下一条记录
	 */

	private void onNext() {
		try {
			boolean flag = false;
			if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // 如果是修改提交
				flag = true;
				if (getBillCarPanel().checkValidate()) {// 这里需要对卡片进行校验，否则必输项可能就漏填了。李春娟2012-02-23修改
					BillVO billVO = getBillCarPanel().getBillVO(); //
					try {
						dealUpdate(billVO); // 修改提交
						billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
						billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
					} catch (Exception e1) {
						MessageBox.showException(this, e1); //
						throw e1;
					}
				} else {// 如果校验不成功则直接返回
					return;
				}
			}
			int rows = billListPanel.getRowCount(); // 当前页行数！
			int li_row = billListPanel.getSelectedRow() + 1; // 被选中记录的下一条记录的行号
			if (li_row >= rows) {
				if (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()) { // 判断是否有分页！
					int currpage = billListPanel.getLi_currpage();
					billListPanel.goToPage(billListPanel.getLi_currpage() + 1, false); // 跳到下一页，如果找不到不需要提示，如果本来就是第一页，则不跳转，所以跳转前后页数相同！李春娟2012-02-22修改
					int nextpage = billListPanel.getLi_currpage();
					if (currpage == nextpage) {
						MessageBox.showInfo(billCardPanel, "这已经是最后一条记录!!");// 这里有自定义的提示，故上面不需要提示了
						return;
					} else {
						li_row = 0; // 设置被选中行为翻页后的第一行，行号为0
					}
				} else {
					MessageBox.showInfo(billCardPanel, "这已经是最后一条记录!!");
					return;
				}
			}
			setInformation("查看记录"); //
			billCardPanel.setBillVO(billListPanel.getBillVO(li_row)); //
			if (flag) {
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				btn_card_save.setVisible(true);
				btn_card_save_return.setVisible(true);
				billCardPanel.setEditableByEditInit(); // 设置为模板配置的编辑状态！一定要设置哦！
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
			switchToCard(); // 切换到卡片
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 查看上一条记录
	 */
	private void onPrevious() {
		try {
			boolean flag = false; // 记录是点击修改后查看还是点击预览后查看
			if (getBillCarPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // 如果是修改提交
				if (getBillCarPanel().checkValidate()) {// 这里需要对卡片进行校验，否则必输项可能就漏填了。李春娟2012-02-23修改
					flag = true;
					BillVO billVO = getBillCarPanel().getBillVO(); //
					try {
						dealUpdate(billVO); // 修改提交
						billListPanel.setValueAtRow(billListPanel.getSelectedRow(), billVO);
						billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), "INIT");
					} catch (Exception e1) {
						MessageBox.showException(this, e1); //
						throw e1;
					}
				} else {// 如果校验不成功则直接返回
					return;
				}
			}
			int li_row = billListPanel.getSelectedRow() - 1; //
			if (li_row < 0) {
				if (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()) { // 判断是否有分页！
					int currpage = billListPanel.getLi_currpage();
					billListPanel.goToPage(currpage - 1, false); // 跳到上一页，如果找不到不需要提示，如果本来就是第一页，则不跳转，所以跳转前后页数相同！李春娟2012-02-22修改
					int previouspage = billListPanel.getLi_currpage();
					if (currpage == previouspage) {
						MessageBox.showInfo(billCardPanel, "这已经是第一条记录!!");// 这里有自定义的提示，故上面不需要提示了
						return;
					} else {
						li_row = billListPanel.getTable().getRowCount() - 1;
					}
				} else {
					MessageBox.showInfo(billCardPanel, "这已经是第一条记录!!");
					return;
				}
			}
			setInformation("查看记录"); //
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
			switchToCard(); // 切换到卡片
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 卡片显示时，更新显示条数的JLabel 后来采用了新的分页机制,结果导致不能跑了,所以先注销掉!!!
	 */
	private void updateLabel() {
		// int currNumber = billListPanel.getSelectedRow() + 1;
		// int datalength = billListPanel.getTable().getRowCount();
		// String pagedesc = billListPanel.getLabel_pagedesc().getText();
		// //第1页,共1页46条,每页46条 第1页,共3页46条,每页20条 现在改为 共28条,第1/2页(排序:无)
		// 共28条,第1/1页(排序:无)
		// if
		// (billListPanel.getTempletVO().getIsshowlistpagebar().booleanValue()
		// && !"".equals(pagedesc)) { //判断是否有分页！
		// String[] descs = pagedesc.split(",");
		// datalength = Integer.parseInt(descs[0].trim().substring(1,
		// descs[0].trim().length() - 1));// 共多少条记录
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
		// int currpage = billListPanel.getLi_currpage();//当前页数
		// currNumber = (currpage - 1) * lengthPerPage +
		// billListPanel.getSelectedRow() + 1;
		// }
		// }
		// if (billCardPanel.getEditState() ==
		// WLTConstants.BILLDATAEDITSTATE_INSERT && currNumber == 0) {
		// //未选择一条记录时，新增，在列表最后添加
		// currNumber = billListPanel.getRowCount() + 1;
		// } else if (billCardPanel.getEditState() ==
		// WLTConstants.BILLDATAEDITSTATE_INSERT) { //选择了一条记录时，新增，在其后添加
		// currNumber = billListPanel.getSelectedRow() + 2;
		// }
		// label_pagedesc.setText("[第" + currNumber + "条/共" + datalength +
		// "条]");
		// billCardPanel.getBillCardBtnPanel().getPanel_flow().updateUI(); //必要
	}

	/**
	 * 卡片会调用这里
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
	 * 对某个表格查询后需要再次进行处理的逻辑...
	 */
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		// setWorkFlowDealBillVO(null); //
	}

	/**
	 * 取得工作流处理的BillVO
	 * 
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getBillListPanel().getSelectedBillVO(); // 返回选中行的数据即是工作流需要处理的数据
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
