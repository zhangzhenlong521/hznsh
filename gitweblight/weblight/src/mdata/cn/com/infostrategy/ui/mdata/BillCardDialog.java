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
	private boolean isRealSave = true; // 是否真正保存
	private TBUtil tbUtil = null; //
	private Container parent = null; // 刷新父页面的模板用到 袁江晓 20130313
	public int count=0;
	
	public BillCardDialog(Container _parent, String _code) {
		this(_parent, _code, WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	public BillCardDialog(Container _parent, String _code, String _edittype) {
		super(_parent, "快速卡片查看"); //
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
			this.setTitle("新增");
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.setTitle("编辑");
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
		super(_parent, "快速卡片查看", 600, 400); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_objs);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
	}

	public BillCardDialog(Container _parent, String _code, HashMap _map) {
		super(_parent, "快速卡片查看", 600, 400); //
		str_templete_code = _code;
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(str_templete_code);
		billcardPanel.setValue(_map);
		billcardPanel.setEditable(false);
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		this.locationToCenterPosition(); //
	}

	/**
	 * 卡片面板
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
		if(_title.equals("列编辑")){//列编辑
			if (parent instanceof BillCardPanel) {
				((BillCardPanel)parent).setCanRefreshParent(true);   //首先 列编辑模式页面本身也是billcarddialg，所以这里需要加两条
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
	 * 卡片面板
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
		this(_parent, _title, _items, _width, _height, true); // 只有确定按钮
	}

	/**
	 * 到处有弹出一个小框框,然后确定返回! 如果自己搞个面板再弄个Dialog,有个问题是校验为空时,不能卡住.. new String[][] { {
	 * "编码", "150" }, { "名称", "150" }
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
	 * 卡片面板
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
	 * 整合产品添加案件排查  故增加此方法【zzl 2018-4-9】
	 */
	public BillCardDialog(Container _parent, String _code, String _edittype,boolean _haveHtmlTab) {
		super(_parent, "快速卡片查看"); //
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
			this.setTitle("新增");
			billcardPanel.setEditableByInsertInit(); //
		} else if (_edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.setTitle("编辑");
			billcardPanel.setEditableByEditInit(); //
		}
		
		
		if (_haveHtmlTab) { //必须参数支持!
			tabbedPane = new WLTRadioPane(); //
			tabbedPane.setFocusable(false);
			htmlPanel = new BillHtmlPanel(); //
			billcardPanel.getBillCardBtnPanel().setVisible(false); //
			tabbedPane.addTab("控件风格", billcardPanel); //
			tabbedPane.addTab("打印预览", htmlPanel); //
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
	 * 送入卡片面板
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
		this.parent = _parent;   //主要解决自动刷新功能
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
				"卡片浏览是否支持两种风格", true); // 是否显示两种风格,一种是"控件风格",一种是"Html风格",中铁建项目中肖主任竟然不要这两种风格!!
										// 其实大部分客户是更喜欢Html风格的,但肖主任就是喜欢控件风格!再次证明技术人员与业务人员的爱好不一样!!
		if (_haveHtmlTab && isShowTwoStyle) { // 必须参数支持!
			tabbedPane = new WLTRadioPane(); //
			tabbedPane.setFocusable(false);
			htmlPanel = new BillHtmlPanel(); //
			billcardPanel.getBillCardBtnPanel().setVisible(false); //
			tabbedPane.addTab("控件风格", billcardPanel); //
			tabbedPane.addTab("Html风格", htmlPanel); //
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
		if (isRealSave) { // 如果是需要真正保存的！！
			String str_conFirmBtnName = TBUtil.getTBUtil()
					.getSysOptionStringValue("保存并返回按钮的名称", "确定"); //
			btn_confirm = new WLTButton(str_conFirmBtnName); // 邮储竟然喜欢叫【提交】
		} else {
			btn_confirm = new WLTButton("确定"); //
		}
		btn_save = new WLTButton("保存");
		btn_cancel = new WLTButton("关闭");

		btn_save.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		if (_type == 0) { // 默认的逻辑
			if (billcardPanel.getEditState().equals("INIT")) {
				panel.add(btn_cancel); //
			} else {
				panel.add(btn_confirm); //
				panel.add(btn_save); //
				panel.add(btn_cancel); //
			}
		} else if (_type == 1) { // 只有【确定】按钮
			panel.add(btn_confirm); //
		} else if (_type == 2) { // 同时有【确定】【取消】两个按钮
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
		if (isRealSave && btn_confirm != null) { // 如果需要保存,则确定按钮可能是别的名字!
			btn_confirm.setText(TBUtil.getTBUtil().getSysOptionStringValue(
					"保存并返回按钮的名称", "确定")); // //
		}
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			//清空子表新增数据 【杨科/2013-03-26】
			billcardPanel.dealChildTable(true);
			onSave(); // 保存
		} else if (e.getSource() == btn_confirm) {
			//清空子表新增数据 【杨科/2013-03-26】
			billcardPanel.dealChildTable(true);
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			//取消操作删除子表新增数据 【杨科/2013-03-26】
			billcardPanel.dealChildTable(false);
			onCancel();
		}
	}

	/**
	 * 重写BillDialog的方法，关闭时判断是否保存过
	 */
	public void closeMe() {
		//取消操作删除子表新增数据 【杨科/2013-03-26】
		billcardPanel.dealChildTable(false);
		if (isClickSaveButton) {
			closeType = 1;
			billVO = billcardPanel.getBillVO(); //
		}
		this.dispose();
		// 不重新打开菜单实现刷新 袁江晓 20130313 右上角关闭按钮方法
		// 不重新打开菜单实现刷新 袁江晓 20130313 确定按钮事件
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
			MessageBox.show(this, "保存数据成功!!"); //
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
			if (isRealSave) { // 如果是真正保存才保存,因为有时候并不想真正保存,而是借用其校验,表单,返回值,然后自己做逻辑处理!!!
				billcardPanel.updateData(); // 保存数据了??
			}
			billVO = billcardPanel.getBillVO(); //
			closeType = 1;
			this.dispose(); //
			// 不重新打开菜单实现刷新 袁江晓 20130313 确定按钮事件
			if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
				refreshPraent(this.parent);
			}
			SaveButton=true;
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	// 不重新打开菜单实现刷新 袁江晓 20130313 确定按钮事件
	public void refreshPraent(Container _parent) {
		if (_parent instanceof BillCardPanel) {
			BillCardPanel bcp = (BillCardPanel) _parent;
			BillVO billvo = bcp.getBillVO();// 先获得页面的值需要后面重新加载
			bcp.reload(bcp.getTempletVO().getTempletcode());
			bcp.setBillVO(billvo);
			bcp.updateUI();
			String edittype=billvo.getEditType();//设置刷新后的页面状态
			if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)){
			}else if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)){
				bcp.setEditableByEditInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			}else if(edittype!=null&&edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)){
				bcp.setEditableByInsertInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			}
		} else if (_parent instanceof BillListPanel) { // 如果是billlistpanel则需要刷新
//袁江晓 20130313
//思路：1、先更新查询面板的模板  2、再更新billlist的模板     3、再统一刷新billist的数据，默认为自动加载    这三个是分别独立的	
			BillListPanel billlist = (BillListPanel) _parent;
			String templete = billlist.getTempletVO().getTempletcode();
			//billlist.getbil
//这里需要进行判断，因为有的系统模板不能够加载，所以需要先判断该模板是否存在,为了方便不另外单独搞个函数来处理   20130410   袁江晓
			int pageNo=billlist.getLi_currpage();
			int selectRow=billlist.getSelectedRow();
			if(null!=templete&&!templete.equals("")&&billlist.getStr_realsql()!=null&&(!("1").equals(billlist.getIsRefreshParent()))){//最后一个参数表示如果该billist不需要刷新，将isRefreshParent设置为1即可
				billlist.getQuickQueryPanel().removeAll(); // 快速查询 重新加载
				billlist.getQuickQueryPanel().reload(templete);
				billlist.reload();  //billlist重新加载
				billlist.refreshData(); // 自动加载数据
				if(billlist.getPageScrollable()&&billlist.getLi_TotalRecordCount()!=0){//如果只有0条记录则不进行跳转
					billlist.goToPage(pageNo);
				}
				billlist.setSelectedRow(selectRow);
			}
		} else if (_parent instanceof BillQueryPanel) { // 如果右击billquerypanel的右键--编辑整个模板
			BillQueryPanel bqp = (BillQueryPanel) _parent;
			BillListPanel blp = bqp.getBillListPanel();
			String templeteCode = bqp.getTempletVO().getTempletcode(); // 先获得模板编码
			if(blp!=null){//如果是报表则billlistpanel为空
				blp.getQuickQueryPanel().removeAll();
				blp.getQuickQueryPanel().reload(templeteCode);
				blp.reload();
				blp.refreshData(); // 自动加载数据
			}else{//如果为报表
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
		// 不重新打开菜单实现刷新 袁江晓 20130313 取消按钮事件
		if (ClientEnvironment.isAdmin()&&this.billcardPanel.isCanRefreshParent()) {
			refreshPraent(this.parent);
		}
	}
	//重写父亲的关闭页面方法
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
		if (isClickSaveButton) { // 如果是点了保存按钮,则
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
