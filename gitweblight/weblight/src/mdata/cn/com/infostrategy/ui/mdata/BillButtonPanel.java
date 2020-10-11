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
 * 按钮栏面板,关键类,即用来摆放一排按钮的面板...
 * 按钮是非常关键的对象,系统所有的业务逻辑本质上最终都是通过按钮“串”起来的,按钮的逻辑还可以封装成各种服务，以后甚至可以扩展成SOA服务。即按钮就是SOA服务。但按道理还有一种后台的服务。按钮只是UI端服务的概念。
 * 常用的功能通用按钮可以预置逻辑，比如：列表弹出新增，列表行新增等。还有业务服务按钮，即上面说的SOA服务按钮，就是将一些常用业务进行封装（比如查看体系文件），然后各处可以重用。还有一种是纯普通按钮。
 * 按钮有两种生成方法：
 * 第一种是注册按钮,即在数据库中存储的，它可以通过在列表模板中进行配置，它可以轻松的实现权限配置。权限应同时实现黑名单与白名单两种模式。
 * 第二种是直接通过API创建，比如风格模板上的按钮。但风格模板中按钮的权限如何处理还没想好。目前有3种办法：1-不处理权限；2-在风格模板中处理。3-专门搞一张表，指定某一菜单，某一模板中的按钮只能谁能访问!! 
 * 
 * 在按钮面板中从左到右摆放一排按钮,是依次摆放的,即
 * 
 * 元原模板中的按钮与风格模板创建的按钮的摆放是一个头疼的问题！！初步感觉应是首先摆放风格模板定义的按钮，再摆放元原模板的按钮。但存在两个问题：
 * 1.是放在快速查询面板的上方还是下方？总觉得风格模板的按钮应该在上方才对。但如果该列表不是在顶层，而且没有快速查询面板，则放在下方才对。
 * 2.如果处理风格模板2中的层布局模式，即点击按钮时需要取得按钮所在面板的父容器，有时是层面板，有时是多页签。
 * 
 * API创建按钮时,也可以创建默认逻辑的按钮,比如卡片弹出新增,删除等。
 * @author xch
 *
 */
public class BillButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BillPanel billPanelFrom = null; //

	private JPanel panel_flow = null; //	
	private ActionListener[] listBtnActionListeners = null; //

	private JepFormulaParse formulaParse = null;
	private Vector vm_btns = new Vector(); //存储所有按钮的向量对象!!即按钮都存储在这里,然后随时渲染生成!!!很关键
	private boolean isScrollable = true; //
	private TBUtil tb = null;

	/**
	 * 默认构造方法!
	 */
	public BillButtonPanel() {
		super();
		initialize(); //
	}

	/**
	 * 默认构造方法!
	 */
	public BillButtonPanel(boolean _scrollable) {
		super();
		this.isScrollable = _scrollable;
		initialize(); //
	}

	/**
	 * 默认构造方法,可以指定与哪个面板绑定
	 */
	public BillButtonPanel(BillPanel _billPanel) {
		super();
		billPanelFrom = _billPanel; //
		initialize(); //
	}

	/**
	 * 默认构造方法,可以指定与哪个面板绑定
	 */
	public BillButtonPanel(BillPanel _billPanel, boolean _scrollable) {
		super();
		billPanelFrom = _billPanel; //
		this.isScrollable = _scrollable;
		initialize(); //
	}

	/**
	 * 根据按钮定义生成按钮面板
	 * @param _btns
	 * @param _billPanel
	 */
	public BillButtonPanel(ButtonDefineVO[] _btndfvos, BillPanel _billPanel) {
		billPanelFrom = _billPanel; //
		initialize(); //初始化页面
		addBatchDefineButton(_btndfvos);
	}

	public BillButtonPanel(ButtonDefineVO[] _btndfvos, BillPanel _billPanel, boolean _scrollable) {
		billPanelFrom = _billPanel; //
		this.isScrollable = _scrollable;
		initialize(); //初始化页面
		addBatchDefineButton(_btndfvos);
	}

	/**
	 * 直接通过一批按钮注册码,生成一串按钮
	 * 根据按钮注册码去系统表中查出这些按钮
	 * 有待实现
	 * @param _btnregcodes
	 */
	public BillButtonPanel(String _btnregcodes) {
		initialize(); //初始化页面..
	}

	public BillButtonPanel(String _btnregcodes, boolean _scrollable) {
		this.isScrollable = _scrollable;
		initialize(); //初始化页面..
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); //透明
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
	 * 增加一批按钮
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
	 * 在屁股后面增加一个按钮
	 * @param _btn
	 */
	public void addButton(WLTButton _btn) {
		addButton(_btn, true, true, false);
	}

	/**
	 * 增加一批按钮
	 * @param _btns
	 */
	public void addBatchButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			addButton(_btns[i]);
		}
	}

	/**
	 * 在前面插入一个按钮
	 * @param _btn
	 */
	public void insertButton(WLTButton _btn) {
		addButton(_btn, true, true, true);
	}

	/**
	 * 在前面插入一个按钮
	 * @param _btn
	 */
	public void insertBatchButton(WLTButton[] _btns) {
		for (int i = _btns.length - 1; i >= 0; i--) {
			insertButton(_btns[i]);
		}
	}

	/**
	 * 增加按钮,很关键,增加按钮只能是往向量内存中添加!!而不是在面板中增加,只有调用paintButton()方法进行渲染才在面板上添加!
	 * @param _btn
	 * @param _enable
	 * @param _visiable
	 */
	public void addButton(WLTButton _btn, boolean _enable, boolean _visiable, boolean _isInsert) {
		if (_btn == null) {//南昌银行项目以前的代码发现添加了未定义的按钮，报空指针异常，菜单打不开，故判断一下【李春娟/2016-06-24】
			return;
		}
		_btn.setBillButtonPanelFrom(this); //
		if (_btn.getBillPanelFrom() == null) {
			_btn.setBillPanelFrom(this.billPanelFrom);
		}

		if (_isInsert) { //如果是在前面插入
			vm_btns.insertElementAt(_btn, 0); //往向量表中加入,既可以在前面插入也可以从后面插入!!
		} else {
			vm_btns.add(_btn); //往向量表中加入,既可以在前面插入也可以从后面插入!!
		}
	}

	/**
	 * 下面函数主要是在刷新模板的是偶用到，主要是去掉按钮
	 */
	public void removeAllButtons() {
		vm_btns.removeAllElements();
	}

	/**
	 * 画按钮,即才是真正的画按钮,即在面板上添加.
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
	 *按照某种顺序画按钮,即按钮之间是有顺序的! 
	 */
	public void paintButtonByOrderSeq(String[] _texts) {
		panel_flow.removeAll(); //
		panel_flow.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0)); //设置布局,从左至右
		WLTButton[] btns = getAllButtons(); //旧的顺序
		if (_texts != null) {
			btns = reorderBtns(btns, _texts); //重新排序
		}

		for (int i = 0; i < btns.length; i++) {
			WLTButton wltBtn = btns[i]; //
			String btnText = wltBtn.getText();
			if (btnText.trim().length() == 0) {
				panel_flow.add(new WLTLabel(btnText));
			}else {
				wltBtn.setEnabled(wltBtn.getBtnDefineVo().isAllowed()); //
				panel_flow.add(wltBtn); //真正加入按钮
			}
			
		}

		panel_flow.setOpaque(false);
		//panel_flow.updateUI(); //
	}

	/**
	 * 按照_texts数组的顺序来排序按钮顺序
	 * _texts直接为按钮名称的顺序
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
	 * 根据编码取得按钮
	 * @param _text
	 * @return
	 */
	public WLTButton getButtonByCode(String _code) {
		WLTButton[] allBtns = getAllButtons(); //
		for (int i = 0; i < allBtns.length; i++) {
			if (allBtns[i].getBtnDefineVo().getCode() != null && allBtns[i].getBtnDefineVo().getCode().equals(_code)) { //根据编码取得一个按钮
				return allBtns[i];
			}
		}
		return new WLTButton("不存的按钮[" + _code + "]"); //为了防止异常,输出一个不存在的按钮!!!
	}

	/**
	 * 是否存在一个按钮是显示的
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
	 * 得到所有按钮。
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
	 * 增加自定义按钮,他会自动在
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
	 * 判断一个数组是是否包括别一个数据中的一个值
	 * 即只要有一个交集上了就返回True
	 * @param _
	 * @return
	 */
	private boolean beAccord(String[] _str1, String[] _str2) {
		if (_str1 == null || _str1.length == 0 || _str2 == null || _str2.length == 0) {
			return false;
		}

		//两层遍历!!!!
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
