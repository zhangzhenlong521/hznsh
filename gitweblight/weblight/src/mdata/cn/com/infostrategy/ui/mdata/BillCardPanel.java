/**************************************************************************
 *  $RCSfile: BillCardPanel.java,v $  $Revision: 1.69 $  $Date: 2013/01/10 08:31:52 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Button;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTableImport;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_FileDeal;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ImageUpload;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Label;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_StylePadArea;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;
import cn.com.infostrategy.ui.mdata.cardcomp.CardGroupTitlePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RecordShowDialog;

public class BillCardPanel extends BillPanel {

	private static final long serialVersionUID = 9086081912297545806L; //

	private String str_templetcode = null;
	private Pub_Templet_1VO templetVO = null;
	private Pub_Templet_1_ItemVO[] templetItemVOs = null;

	private AbstractWorkPanel loadedWorkPanel = null; // 加载这个卡片面板的frame,比如是各种风格模板Frame!
	private boolean is_admin = false;
	private JScrollPane scrollPanel = null;
	private ArrayList v_compents = new ArrayList();
	private HashMap hm_groupcompents = new HashMap(); //
	private JComponent[] hflowPanels = null; //
	private VFlowLayoutPanel vflowPanel = null; //
	private boolean canRefreshParent = false; //20130509添加 修改刷新父页面问题

	private HashMap hm_hflowPanels_key = new HashMap(); //存储hflowPanels行与key映射,解决setVisiable(key, false)方法空行7像素问题 【杨科/2013-01-09】

	public String str_rownumberMark = "_RECORD_ROW_NUMBER";
	private Vector v_listeners = new Vector(); //反射注册的事件监听者!!!
	private Vector v_cardbtnListener = new Vector(); // 自定义按钮监听事件

	private VectorMap rowPanelMap = new VectorMap(); //

	private Border border = null;

	private TBUtil tBUtil = null; //转换工具!!

	private RowNumberItemVO rowNumberItemVO = null; // 行号数据VO.....
	private boolean bo_isallowtriggereditevent = true; // 是否允许触发编辑事件

	private BillButtonPanel billCardBtnPanel = null; //

	private JPanel mainPanel = null;
	private int li_cardallwidth = 510; //
	private int li_panelallheight = 0;
	private BillFormatPanel loaderBillFormatPanel = null;

	private HashMap lastKeepTrace = new HashMap(); //存放最后一个版本数据的哈希表!!

	private Logger logger = WLTLogger.getLogger(BillCardPanel.class);
	private BillCardPanel() {
	}

	public BillCardPanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	public BillCardPanel(Pub_Templet_1VO _TempletVO) {
		this.str_templetcode = _TempletVO.getTempletcode();
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // 各项
		initialize(); //
	}

	/**
	 * 根据直接生成的抽象模板VO创建页面!!!,即配置数据不存在 pub_teplet_1表与pub_teplet_1_item表中的!!!
	 * 
	 * @param _abstractTempletVO
	 */
	public BillCardPanel(AbstractTMO _abstractTempletVO) {
		init_2(_abstractTempletVO);
	}

	public BillCardPanel(String _templetName, String[] _items) {
		String[][] str_items = new String[_items.length][2]; //
		for (int i = 0; i < str_items.length; i++) {
			str_items[i][0] = _items[i];
			str_items[i][1] = "225";
		}
		AbstractTMO tmo = DefaultTMO.getCardTMO(_templetName, str_items); //
		init_2(tmo);
		setEditable(true); //这种构建的,默认肯定是可编辑!!
	}

	/**
	 * 最快速的构建一个面板!经常到处需要
	 * BillCardPanel card = new BillCardPanel("我的提示框", new String[][] { { "编码", "150" }, { "名称", "150" } }); //
	 * @param _templetName
	 * @param _items
	 */
	public BillCardPanel(String _templetName, String[][] _items) {
		AbstractTMO tmo = DefaultTMO.getCardTMO(_templetName, _items); //
		init_2(tmo);
		setEditable(true); //这种构建的,默认肯定是可编辑!!

	}

	/**
	 * 根据ServerTMO创建..
	 * @param _serverTMO
	 */
	public BillCardPanel(ServerTMODefine _serverTMO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); //各项..
			str_templetcode = templetVO.getTempletcode();
			initialize(); // 初始化!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void init_2(AbstractTMO _abstractTempletVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode(); //
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	/**
	 * 重新加载页面
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * 重新加载页面
	 */
	public void reload(String _templetcode) {
		v_compents.clear(); //
		rowPanelMap.clear(); //
		v_listeners.clear();
		v_cardbtnListener.clear();
		mainPanel = null;
		vflowPanel = null; //重新加载时都刷新页面  20130313  袁江晓  为实现不重新打开页面直接刷新模板而添加   这个东东必须加上，否则不会重新加载
		init_1(_templetcode); //
	}

	/**
	 * 初始化页面
	 * 
	 */
	private void initialize() {
		this.removeAll(); //
		//this.setFocusable(true); //
		li_cardallwidth = templetVO.getCardwidth().intValue(); //卡片宽度
		this.is_admin = ((ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) ? true : false);
		this.setRowNumberItemVO(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, 0)); //
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getBorderName(), TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // 创建边框		

		this.setLayout(new BorderLayout());
		this.setBorderColor(Color.BLACK); //

		JPanel panel_content = new JPanel(new BorderLayout()); //内容面板
		panel_content.setOpaque(false); //透明!
		panel_content.setBorder(border); //设置边框!
		panel_content.add(getMainPanel(), BorderLayout.CENTER); //取得主面板,关键逻辑!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		JPanel wholePanel = null; //整个面板,因为需要处理渐变,但又不想让按钮栏搞得另一块似的! 所以搞了这个面板!!
		boolean isJianbian = true; //是否渐变??
		if (isJianbian) { //如果是渐变!!!
			wholePanel = new WLTPanel(BackGroundDrawingUtil.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		} else {
			wholePanel = new JPanel(); //
			wholePanel.setBackground(LookAndFeel.defaultShadeColor1); //
		}
		wholePanel.add(panel_content, BorderLayout.CENTER);
		wholePanel.add(getBillCardBtnPanel(), BorderLayout.NORTH);

		this.setLayout(new BorderLayout()); //
		this.add(wholePanel); //
	}

	/**
	 * 列表本身可以定义一些快速按钮
	 * 
	 * @return
	 */
	public BillButtonPanel getBillCardBtnPanel() {
		if (billCardBtnPanel != null) {
			return billCardBtnPanel;
		}

		billCardBtnPanel = new BillButtonPanel(this.templetVO.getCardcustbtns(), this); //
		billCardBtnPanel.paintButton(); //
		return billCardBtnPanel;
	}

	/**
	 * 得到某一个按钮
	 * @param _text
	 * @return
	 */
	public WLTButton getBillCardBtn(String _text) {
		return getBillCardBtnPanel().getButtonByCode(_text); //
	}

	/**
	 * 增加一个按钮.
	 * @param _btn
	 */
	public void addBillCardButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillCardBtnPanel().addButton(_btn); //
	}

	/**
	 * 增加一批按钮
	 * @param _btns
	 */
	public void addBatchBillCardButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillCardBtnPanel().addBatchButton(_btns);
	}

	/**
	 * 增加一个按钮.
	 * @param _btn
	 */
	public void insertBillCardButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillCardBtnPanel().insertButton(_btn); //
	}

	/**
	 * 插入一批按钮.
	 * @param _btn
	 */
	public void insertBatchBillCardButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillCardBtnPanel().insertBatchButton(_btns); //
	}

	/**
	 * 重新绘制按钮
	 */
	public void repaintBillCardButton() {
		getBillCardBtnPanel().paintButton(); //
	}

	public void addBillCardButtonActinoListener(BillCardButtonActinoListener _listener) {
		v_cardbtnListener.add(_listener); //
	}

	/**
	 * 所有的布局都在这个方法中!!关键,讲究!!
	 * @return
	 */
	public JPanel getMainPanel() {
		if (vflowPanel != null) {
			return vflowPanel; //
		}

		for (int i = 0; i < templetItemVOs.length; i++) { //遍历所有控件!!
			final String str_itemkey = templetItemVOs[i].getItemkey();
			String str_type = templetItemVOs[i].getItemtype();
			AbstractWLTCompentPanel compentPanel = null; //先定义好控件!!!一个不漏!!
			if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
				compentPanel = new CardCPanel_Label(templetItemVOs[i]); //
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || str_type.equals(WLTConstants.COMP_REGULAR)) { //文本框,数字框,密码框//zzl加入正则表达式
				compentPanel = new CardCPanel_TextField(templetItemVOs[i], str_type, null, this); //
				((CardCPanel_TextField) compentPanel).getTextField().addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) { //光标失去时触发事件!!
						onChanged(str_itemkey);
					}
				});

				//监听事件
				((CardCPanel_TextField) compentPanel).getTextField().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onChangedAndFocusNext(str_itemkey); //如果是敲回车的话,则要跳光标..
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
				compentPanel = new CardCPanel_ComboBox(templetItemVOs[i], null, this); //
				((CardCPanel_ComboBox) compentPanel).getComBox().addItemListener(new ItemListener() { //监听事件
							public void itemStateChanged(ItemEvent e) {
								if (e.getStateChange() == ItemEvent.SELECTED) {
									onChanged(str_itemkey);
								}
							}
						});
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
					str_type.equals(WLTConstants.COMP_DATE) || //日历
					str_type.equals(WLTConstants.COMP_DATETIME) || //时间
					str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
					//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框!!!（以前也是当一个参照处理,但后来无数处用户要求将其拿到主页面上来,以符合大量网页系统的效果,少点一次,）
					str_type.equals(WLTConstants.COMP_COLOR) || //颜色选择框
					str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
					str_type.equals(WLTConstants.COMP_PICTURE) || //图片选择框
					str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
					str_type.equals(WLTConstants.COMP_OFFICE) //office
			) { //如果是各种参照
				compentPanel = new CardCPanel_Ref(templetItemVOs[i], null, this); //创建参照面板!!!一开始是没有初始值的!!
				((CardCPanel_Ref) compentPanel).addBillCardEditListener(new BillCardEditListener() {
					public void onBillCardValueChanged(BillCardEditEvent _evt) {
						onChanged(str_itemkey); //在参照中加入监听器
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //多行文本框
				compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
				((CardCPanel_TextArea) compentPanel).getArea().addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) { //光标失去时触发事件!!
						onChanged(str_itemkey);
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //按钮
				compentPanel = new CardCPanel_Button(templetItemVOs[i], null, this);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //勾选框
				compentPanel = new CardCPanel_CheckBox(templetItemVOs[i]);
				((CardCPanel_CheckBox) compentPanel).getCheckBox().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onChanged(str_itemkey); //
					}
				});
			} else if (str_type.equals(WLTConstants.COMP_LINKCHILD)) { //引用子表,将创建一个表格,从而实现像HTNL效果一样的页面!!
				compentPanel = new CardCPanel_ChildTable(templetItemVOs[i], this); //
				compentPanel.setItemEditable(false); //引用子表默认是不可编辑...
				//暂不做编辑监听!!!
			} else if (str_type.equals(WLTConstants.COMP_IMPORTCHILD)) { //导入子表!!
				compentPanel = new CardCPanel_ChildTableImport(templetItemVOs[i], this); //
				compentPanel.setItemEditable(false); //导入子表默认是不可编辑...
				//暂不做编辑监听!!!
			} else if (str_type.equals(WLTConstants.COMP_STYLEAREA)) { //富文本框
				compentPanel = new CardCPanel_StylePadArea(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { //文件选择框,在大量用户强烈要求下，还是拿到主页面上来，其实这样会使主页面更乱,但用户无所谓!!只要少点一次，关键是与OA的操作习惯是一样的！！！
				compentPanel = new CardCPanel_FileDeal(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_IMAGEUPLOAD)) { //图片上传,即直接上传一个图片存储在数据库中,然后在卡片中直接渲染该图片! 比如HR系统中的人员照片等效果就会遇到这种控件需求! 因为与上传文件存储在目录下不一样,这是存储在数据库中的!所以迁移方便!
				compentPanel = new CardCPanel_ImageUpload(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_SELFDESC)) {
				if (templetItemVOs[i].getUCDfVO() != null) {
					String str_clsName = templetItemVOs[i].getUCDfVO().getConfValue("卡片中的类"); //
					if (str_clsName != null && !str_clsName.trim().equals("")) { //如果不为空,则创建
						try {
							compentPanel = (AbstractWLTCompentPanel) Class.forName(str_clsName).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class, BillPanel.class }).newInstance(new Object[] { templetItemVOs[i], this });
						} catch (Exception e) {
							e.printStackTrace();
							compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
						}
					} else {
						compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
					}
				} else {
					compentPanel = new CardCPanel_TextArea(templetItemVOs[i], null, this);
				}
			} else {
				continue; //
			}

			compentPanel.setOpaque(false); //-isOpaque
			v_compents.add(compentPanel); //在向量中加入!!
			boolean bo_iscardedit = false; // 初始界面不能编辑!!
			compentPanel.setItemEditable(bo_iscardedit); //设置是否可编辑!!
			compentPanel.setBillPanel(this); //在控件中注册BillCardPanel
		} //end for,遍历所有控件类型结束

		ArrayList al_allRows = new ArrayList(); //所有行
		ArrayList al_oneRowCompents = null; //一行
		String last_grouptitle = ""; //最后一个分组的名字

		int li_rowcount = -1; //
		for (int i = 0; i < v_compents.size(); i++) { //
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			String str_itemkey = compent.getItemKey(); ////
			Pub_Templet_1_ItemVO tmpitemVO = findTempletItemVO(str_itemkey); //
			boolean bo_iscardshow = tmpitemVO.getCardisshowable().booleanValue(); //
			boolean bo_iswrap = tmpitemVO.getIswrap().booleanValue(); //
			String str_grouptitle = tmpitemVO.getGrouptitle() == null ? "" : tmpitemVO.getGrouptitle(); //标题..
			if (bo_iscardshow) { //如果在卡片显示!!!因为有的是隐藏的!
				//compent.setVisible(true);
				if (str_grouptitle.equals(last_grouptitle)) { //如果本组名与上一分组名相同,则判断是否换行!!
					if (bo_iswrap) {
						al_oneRowCompents = new ArrayList(); //如果是换行,则重新创建一个向量表!!
						al_allRows.add(al_oneRowCompents); //
						al_oneRowCompents.add(compent); //加入控件
						hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
						hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
						li_rowcount++; //加一行
					} else {
						if (al_oneRowCompents == null) { //
							al_oneRowCompents = new ArrayList(); //
							al_allRows.add(al_oneRowCompents); //
							li_rowcount++; //加一行
						}
						al_oneRowCompents.add(compent); //直接在原来的行中增加新的控件!!!
						hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
						if (hm_hflowPanels_key.containsKey(al_allRows.size())) {
							hm_hflowPanels_key.put(al_allRows.size(), hm_hflowPanels_key.get(al_allRows.size()) + ";" + str_itemkey);
						} else {
							hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
						}
					}

					if (!str_grouptitle.equals("")) { //如果组名不为空，且没绑定，则组定关系
						String str_rowindexs = (String) hm_groupcompents.get(str_grouptitle); //原来的行号
						if (str_rowindexs.indexOf(li_rowcount + ";") < 0) { //如是不包括本行
							hm_groupcompents.put(str_grouptitle, str_rowindexs + li_rowcount + ";"); //新的行号
						}
					}
				} else { //如果本组名与上一分组不同,则说明是新的分组,则强行换行，并且先加入一个分组条!!!
					if (!str_grouptitle.equals("")) {
						li_rowcount++; //加一行
						CardGroupTitlePanel cardTitlePanel = new CardGroupTitlePanel(str_grouptitle, li_rowcount); //组控件,即前面有个按钮可以点击进行展开与收缩操作!!
						cardTitlePanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM, false));
						cardTitlePanel.getButton().addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (e.getModifiers() == 18) {
									onClickGroupTitleCtrl((JButton) e.getSource());
								} else {
									/*if (templetVO.getGroupisonlyone() != null && templetVO.getGroupisonlyone()) {
										onClickGroupTitleOnlyOne((JButton) e.getSource());
									} else*/
									onClickGroupTitle((JButton) e.getSource());
								}
							}
						});

						al_allRows.add(cardTitlePanel); //加入组条
					}

					al_oneRowCompents = new ArrayList(); //如果
					al_allRows.add(al_oneRowCompents); //
					al_oneRowCompents.add(compent); //加入控件
					hm_hflowPanels_key.put(str_itemkey, al_allRows.size());
					hm_hflowPanels_key.put(al_allRows.size(), str_itemkey);
					li_rowcount++; //加一行

					if (!str_grouptitle.equals("")) {
						hm_groupcompents.put(str_grouptitle, li_rowcount + ";"); //先建组，记下绑定关系!!即第几行
					}
				}
				last_grouptitle = str_grouptitle; //记下上一个分组名
			} else {
				//compent.setVisible(false);
			}
		}

		hflowPanels = new JComponent[al_allRows.size()]; //
		for (int i = 0; i < hflowPanels.length; i++) {
			Object obj = al_allRows.get(i); // 
			if (obj instanceof ArrayList) {
				ArrayList al_row = (ArrayList) obj; //
				//hflowPanels[i] = new HFlowLayoutPanel(al_row, LookAndFeel.cardbgcolor); ////
				hflowPanels[i] = new HFlowLayoutPanel(al_row); ////
			} else if (obj instanceof JComponent) { //
				hflowPanels[i] = (JComponent) obj; //
			}
		}

		//vflowPanel = new VFlowLayoutPanel(hflowPanels, LookAndFeel.cardbgcolor); //垂直布局...
		vflowPanel = new VFlowLayoutPanel(hflowPanels); //垂直布局...
		//        vflowPanel.setOpaque(false); //--isOpaque
		//vflowPanel.setBackground(LookAndFeel.cardbgcolor);

		//执行初始化公式!!比如上来先把某些控件隐藏掉
		JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
		String str_cardinitformula = this.templetVO.getCardinitformula(); //卡片的初始化公式
		if (str_cardinitformula != null && !str_cardinitformula.trim().equals("")) {
			String[] str_initformulas = getTBUtil().split1(str_cardinitformula, ";"); //
			for (int i = 0; i < str_initformulas.length; i++) {
				jepParse.execFormula(str_initformulas[i]); //执行初始化公式!!!
			}
		}

		/*if (templetVO.getGroupisonlyone() != null && templetVO.getGroupisonlyone()) {//卡片是否只能打开一组
			setAllGroupVisiable();
		}*/

		return vflowPanel;
	}

	/**
	 * 设置某一组是否显示,即连组本身都处理
	 * @param _groupname
	 * @param _visiable
	 */
	public void setGroupVisiable(String _groupname, boolean _visiable) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		if (al_rowindexs != null && !"".equals(al_rowindexs.trim())) { // 经常有指定了不存在的组报空指针的问题/sunfujun/20130514
			String[] str_items = al_rowindexs.split(";"); //
			int li_grouprow = Integer.parseInt(str_items[0]) - 1; //组所在行就是第一行的前面一行!!!
			CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
			grouptitlePanel.setVisible(_visiable); //
			grouptitlePanel.setExpandSate(_visiable); //
			for (int i = str_items.length - 1; i >= 0; i--) {
				vflowPanel.setRowVisiable(Integer.parseInt(str_items[i]), _visiable);
				//			vflowPanel.setRowItemVisiable(Integer.parseInt(str_items[i]), _visiable); //	如果这一组不显示，那么在必填项校验时就没必要做校验了
			}
		}
	}

	/**
	 * 设置某一组是否展开,即只处理属于该组的控件!!
	 * @param _groupname
	 * @param _expandable
	 */
	public void setGroupExpandable(String _groupname, boolean _expandable) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		if (al_rowindexs != null) {
			String[] str_items = al_rowindexs.split(";"); //算出该组一共包含几列
			int li_grouprow = Integer.parseInt(str_items[0]) - 1; //组所在行就是第一行的前面一行!!!
			for (int i = str_items.length - 1; i >= 0; i--) {
				vflowPanel.setRowVisiable(Integer.parseInt(str_items[i]), _expandable); //
			}

			CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
			grouptitlePanel.setExpandSate(_expandable); //
		}
	}

	public boolean isGroupVisiable(String _groupname) {
		String al_rowindexs = (String) hm_groupcompents.get(_groupname); //
		String[] str_items = al_rowindexs.split(";"); //
		int li_grouprow = Integer.parseInt(str_items[0]) - 1; //组所在行就是第一行的前面一行!!!
		CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
		return grouptitlePanel.isVisible();
	}

	/**
	 * 设置所有组不显示,即连组本身都处理
	 */
	public void setAllGroupVisiable() {
		if (hm_groupcompents != null && hm_groupcompents.size() > 0) {
			String[] str_GroupNames = (String[]) (hm_groupcompents.keySet().toArray(new String[0]));
			int firstGroup = 10000;
			for (int i = 0; i < str_GroupNames.length; i++) {
				String al_rowindexs = (String) hm_groupcompents.get(str_GroupNames[i]); //
				if (al_rowindexs != null) {
					String[] str_items = al_rowindexs.split(";"); //算出该组一共包含几列
					int li_grouprow = Integer.parseInt(str_items[0]) - 1; //组所在行就是第一行的前面一行!!!
					if (li_grouprow < firstGroup) {
						firstGroup = li_grouprow;
					}
					for (int j = str_items.length - 1; j >= 0; j--) {
						vflowPanel.setRowVisiable(Integer.parseInt(str_items[j]), false); //
					}

					CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[li_grouprow]; //
					grouptitlePanel.setExpandSate(false); //
				}
			}
			if (((CardGroupTitlePanel) hflowPanels[firstGroup]) != null) {
				setGroupExpandable(((CardGroupTitlePanel) hflowPanels[firstGroup]).getTitlename(), true); //
			}
		}
	}

	public void onClickGroupTitle(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		if (grouptitlePanel.getExpandState()) {
			setGroupExpandable(str_titlename, false); //
		} else {
			setGroupExpandable(str_titlename, true); //
		}
	}

	public void onClickGroupTitleCtrl(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel2 = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		boolean s = grouptitlePanel2.getExpandState();
		for (int i = 0; i < hm_groupcompents.size(); i++) {
			String al_rowindexs = ((String[]) hm_groupcompents.values().toArray(new String[0]))[i]; //
			if (al_rowindexs != null) {
				CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[Integer.parseInt(al_rowindexs.split(";")[0]) - 1];
				if (s) {
					setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), false); //
				} else {
					setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), true); //
				}
			}
		}
	}

	public void onClickGroupTitleOnlyOne(JButton _btn) {
		String str_titlename = (String) _btn.getClientProperty("grouptitle"); //
		Integer lit_row = (Integer) _btn.getClientProperty("row"); //
		CardGroupTitlePanel grouptitlePanel2 = (CardGroupTitlePanel) hflowPanels[lit_row]; //
		boolean s = grouptitlePanel2.getExpandState();
		for (int i = 0; i < hm_groupcompents.size(); i++) {
			String al_rowindexs = ((String[]) hm_groupcompents.values().toArray(new String[0]))[i]; //
			if (al_rowindexs != null) {
				CardGroupTitlePanel grouptitlePanel = (CardGroupTitlePanel) hflowPanels[Integer.parseInt(al_rowindexs.split(";")[0]) - 1];
				setGroupExpandable((String) grouptitlePanel.getButton().getClientProperty("grouptitle"), false); //
			}
		}
		if (!s) {
			setGroupExpandable((String) grouptitlePanel2.getButton().getClientProperty("grouptitle"), true); //
		}
	}

	public void setRowPanelVisiable(String _itemkey, boolean _visiable) {
		JPanel panel = getRowPanel(_itemkey);
		if (panel != null) {
			if (_visiable) {
				if (!panel.isVisible()) {
					showRowPanel(panel);
				}
			} else {
				if (panel.isVisible()) {
					hiddenRowPanel(panel); //
				}
			}
		}
	}

	private void showRowPanel(JPanel _rowPanel) {
		_rowPanel.setVisible(true);
		li_panelallheight = li_panelallheight + (int) _rowPanel.getPreferredSize().getHeight(); //在原来的高度上减去该行高度!!!
		mainPanel.setBounds(0, 0, (int) mainPanel.getPreferredSize().getWidth(), li_panelallheight);
	}

	private void hiddenRowPanel(JPanel _rowPanel) {
		_rowPanel.setVisible(false);
		li_panelallheight = li_panelallheight - (int) _rowPanel.getPreferredSize().getHeight(); //在原来的高度上减去该行高度!!!
		mainPanel.setBounds(0, 0, (int) mainPanel.getPreferredSize().getWidth(), li_panelallheight);
	}

	public JPanel getRowPanel(String _itemkey) {
		return (JPanel) rowPanelMap.get(_itemkey);
	}

	public JPanel[] getGroupPanel(String _grouptitle) {
		String[] str_rowPanelKeys = rowPanelMap.getKeysAsString();
		Vector v_data = new Vector();
		for (int i = 0; i < str_rowPanelKeys.length; i++) {
			String str_grouptitle = getTempletItemVO(str_rowPanelKeys[i]).getGrouptitle();
			if (str_grouptitle != null && str_grouptitle.equalsIgnoreCase(_grouptitle)) {
				v_data.add(getRowPanel(str_rowPanelKeys[i]));
			}
		}
		return (JPanel[]) v_data.toArray(new JPanel[0]);
	}

	public void setScrollable(boolean _bo) {
		//		if (_bo) {
		//			scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //
		//			scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		//		} else {
		//			scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); //
		//			scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //
		//		}
	}

	public void setTitleable(boolean _bo) {
		if (_bo) {
			border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getBorderName(), TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // 创建边框
			((TitledBorder) border).setTitleColor(Color.BLACK); //
			if (scrollPanel != null) {
				scrollPanel.setBorder(border); //
			}
		} else {
			border = BorderFactory.createEmptyBorder(); // 创建边框
			if (scrollPanel != null) {
				scrollPanel.setBorder(border); //
			}
		}
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();
		JMenuItem menu_table_showRecord = new JMenuItem("查看数据库数据");
		JMenuItem item_table_templetmodify_2 = new JMenuItem("快速配置");
		JMenuItem menu_print = new JMenuItem("打印");

		if (!is_admin) {
			item_table_templetmodify_2.setEnabled(false);
		}

		menu_table_showRecord.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (templetVO.getPkname() == null || templetVO.getPkname().length() <= 0) {
					try {
						String[] allValues = new String[templetVO.getRealViewItemVOs().length];
						for (int i = 0; i < templetVO.getRealViewItemVOs().length; i++) {
							allValues[i] = String.valueOf(getRealValueAt(templetVO.getItemKeys()[i]));
						}
						new RecordShowDialog(BillCardPanel.this, templetVO.getTablename(), templetVO.getItemKeys(), allValues);
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.show(BillCardPanel.this, "该表在数据库中不存在！", WLTConstants.MESSAGE_ERROR);
					}
				} else {
					try {
						new RecordShowDialog(BillCardPanel.this, templetVO.getTablename(), templetVO.getPkname(), getCompentRealValue(templetVO.getPkname()));
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.show(BillCardPanel.this, "该表在数据库中不存在！", WLTConstants.MESSAGE_ERROR);
					}
				}
			}
		});

		item_table_templetmodify_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyTemplet2(templetVO.getTempletcode());
			}
		});

		menu_print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPrintThis(); //
			}
		});

		if (is_admin) {
			popmenu_header.add(item_table_templetmodify_2); // 快速模板编辑
			popmenu_header.add(menu_table_showRecord);
		}

		popmenu_header.add(menu_print);
		popmenu_header.show(_compent, _x, _y); //
	}

	private void modifyTemplet2(String _templetCode) {
		try {
			boolean res = new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, null);
			if (res) {
				this.reload(); //重新刷新页面
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return; //
		}
	}

	public String getSQL(String _condition) {
		String str_constraintFilterCondition = getDataconstraint();
		String str_return = null; //
		if (str_constraintFilterCondition == null) {
			if (_condition == null) {
				str_return = "select * from " + templetVO.getTablename();
			} else {
				str_return = "select * from " + templetVO.getTablename() + " where " + _condition; // 把RowID加上!!
			}
		} else {
			if (_condition == null) {
				str_return = "select * from " + templetVO.getTablename() + " where (" + str_constraintFilterCondition + ")";
			} else {
				str_return = "select * from " + templetVO.getTablename() + " where (" + str_constraintFilterCondition + ") and (" + _condition + ")"; // 把RowID加上!!
			}
		}

		return str_return;
	}

	//	/**
	//	 * 判断是否可以配置模板,即如果是Class类型的,则不可配置,如果是XML类型的,则需要复制到DB中进行配置!! 如果是DB的,则分：如果XML中也有,则可以将XML中搞过来比较,也可以直接删除自己而使用XML中的! 如果XML中没有则是以前的逻辑!! 
	//	 * @return
	//	 */
	//	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
	//		try {
	//			String str_buildFromType = templetVO.getBuildFromType(); //创建的类型!!
	//			String str_buildFromInfo = templetVO.getBuildFromInfo(); //创建的信息!!
	//			String str_templetCode = templetVO.getTempletcode(); //模板编码
	//			String str_templetName = templetVO.getTempletname();
	//			return new MetaDataUIUtil().checkTempletIsCanConfig(this, str_buildFromType, str_buildFromInfo, str_templetCode, str_templetName, _isquiet); //
	//		} catch (Exception ex) {
	//			MessageBox.showException(this, ex); //
	//			return false; //
	//		}
	//	}

	/**
	 * 刷新数据
	 * 
	 * @param _condition
	 */
	public void refreshData(String _condition) {
		queryData(getSQL(_condition)); //
	}

	public void queryDataByCondition(String _condition) {
		queryData(getSQL(_condition)); //
	}

	public void queryDataByDSAndCondition(String _datasourcename, String _condition) {
		queryDataByDS(_datasourcename, getSQL(_condition)); //
	}

	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // 默认数据源
		} else {
			return new TBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // 算出数据源!!
		}
	}

	/**
	 * 得到数据权限过滤条件
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // 默认数据源
		} else {
			return new TBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDataconstraint()); // 算出数据源!!
		}
	}

	/**
	 * 默认取数数!
	 * @param _sql
	 */
	public void queryData(String _sql) {
		queryDataByDS(getDataSourceName(), _sql); //
	}

	/**
	 * 到指定数据源中取数!
	 * @param _dsName
	 * @param _sql
	 */
	public void queryDataByDS(String _dsName, String _sql) {
		this.reset();
		Object[] objs = null;
		try {
			objs = UIUtil.getBillCardDataByDS(_dsName, _sql, this.templetVO);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (objs != null) {
			setValue(objs); // 设置数据,包括行号中的RowID!!
		}

		//this.getRowNumberItemVO().setState(RowNumberItemVO.INIT); // 设置为初始状态
		setBorderColor(Color.BLACK); // 设置边框颜色
		setBorderText("[" + getBorderName() + "]"); // 设置模板名称!!
		//this.updateUI(); //
	}

	/**
	 * 取得参照说明中的SQL语句!!!
	 * 
	 * @param _allrefdesc
	 * @return
	 */
	public String getRefDescSQL(String _allrefdesc) {
		String str_type = null;
		String str_sql = null;
		int li_pos = _allrefdesc.indexOf(":"); //
		if (li_pos < 0) {
			str_type = "TABLE";
		} else {
			str_type = _allrefdesc.substring(0, li_pos).toUpperCase(); //
		}

		if (str_type.equalsIgnoreCase("TABLE")) {
			if (li_pos < 0) {
				str_sql = _allrefdesc;
			} else {
				str_sql = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			}
		} else if (str_type.equalsIgnoreCase("TREE")) {
			_allrefdesc = _allrefdesc.trim(); // 截去空格
			String str_remain = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			int li_pos_tree_1 = str_remain.indexOf(";"); //
			str_sql = str_remain.substring(0, li_pos_tree_1); // SQL语句
		} else if (str_type.equalsIgnoreCase("CUST")) {
		}
		return str_sql;
	}

	public void reset() {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // 重置行号VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			try {
				compents[i].reset(); // 设置值
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
		this.reset();
		this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		this.setEditable(false); //
	}

	public void reset(String _itemkey) {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // 重置行号VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equals(_itemkey)) {
				compents[i].reset(); //设置值
				break;
			}
		}
	}

	public void resetOthers(String[] _itemKeys) {
		//this.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); // 重置行号VO!!
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			boolean bo_iffind = false;
			for (int j = 0; j < _itemKeys.length; j++) {
				if (compents[i].getItemKey().equals(_itemKeys[j])) {
					bo_iffind = true; //说明该项在过滤列中,即在过滤列中找到了该项!!!
				}
			}

			if (bo_iffind) {
			} else { //如果该项不在
				compents[i].reset(); // 设置值
			}
		}
	}

	/**
	 * 清空一组控件的值
	 * @param _grouptitle
	 */
	public void resetByGrouptitle(String _grouptitle) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (_grouptitle.equals(templetItemVOs[i].getGrouptitle())) {
				reset(templetItemVOs[i].getItemkey());
			}
		}
	}

	public void setEditable(boolean _bo) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (this.templetVO.getItemType(compents[i].getItemKey()).equals("按钮")) {
				compents[i].setItemEditable(true); // 设置值

			} else {
				if (!compents[i].getItemKey().equalsIgnoreCase(this.templetVO.getPkname()))
					compents[i].setItemEditable(_bo); // 设置值
			}
		}
	}

	public void setEditable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_itemkey)) {
				compents[i].setItemEditable(_bo); // 设置值
			}
		}
	}

	public void setVisiable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel panel = getCompentByKey(_itemkey);//经常遇到代码中设置某个字段不显示，后来将模板中该字段删除了，则会报错的情况，这里判断一下【李春娟/2012-09-26】
		if (panel != null) {
			panel.setVisible(_bo);
		}

		//隐藏或显示行距 【杨科/2013-01-09】
		if (hm_hflowPanels_key.containsKey(_itemkey)) {
			int row_number = (Integer) hm_hflowPanels_key.get(_itemkey);
			if (_bo) {
				vflowPanel.setRowVisiable(row_number - 1, _bo);
			} else {
				String[] itemkeys = ((String) hm_hflowPanels_key.get(row_number)).split(";");
				for (int i = 0; i < itemkeys.length; i++) {
					if (getCompentByKey(itemkeys[i]).isVisible()) {
						_bo = true;
						break;
					}
				}

				if (!_bo) {
					vflowPanel.setRowVisiable(row_number - 1, _bo);
				}
			}
		}
	}

	public void setVisiable(String[] _itemkeys, boolean _bo) {
		for (int i = 0; i < _itemkeys.length; i++) {
			/*			AbstractWLTCompentPanel panel = getCompentByKey(_itemkeys[i]);//经常遇到代码中设置某个字段不显示，后来将模板中该字段删除了，则会报错的情况，这里判断一下【李春娟/2012-09-26】
						if (panel != null) {
							panel.setVisible(_bo);
						}*/
			setVisiable(_itemkeys[i], _bo);
		}
	}

	public void setBorderTitle(String _title) {
		setBorderText(_title); //
	}

	public String getInsertSQL() {
		return this.getBillVO().getInsertSQL(getiEncryptKeys());
	}

	public String getUpdateSQL() {
		return this.getBillVO().getUpdateSQL(getiEncryptKeys());
	}

	//返回所有需要加密的字段清单!!!
	public String[] getiEncryptKeys() {
		ArrayList al_keys = null; //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getIsencrypt()) { //如果需要加密,则加入
				if (al_keys == null) {
					al_keys = new ArrayList();
				}
				al_keys.add(templetItemVOs[i].getItemkey()); //加入!
			}
		}
		if (al_keys == null) {
			return null; //
		}
		return (String[]) al_keys.toArray(new String[0]); //
	}

	/**
	 * 修改数据!!
	 * @throws Exception
	 */
	public void updateData() throws Exception {
		String str_currEditState = this.getEditState();
		if (str_currEditState == null) {
			return;
		}

		if (!str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_INSERT) && !str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			return;
		}

		String str_sql = getUpdateDataSQL(); //取得SQL,可能是Insert也可能是Update!!!
		if (str_currEditState == WLTConstants.BILLDATAEDITSTATE_INSERT) { //
			boolean isps = UIUtil.getCommonService().getSysOptionBooleanValue("是否启用预编译", false);
			if (isps) {
				UIUtil.executeUpdateByDSPS(getDataSourceName(), str_sql); //有问题!!
			} else {
				UIUtil.executeUpdateByDS(getDataSourceName(), str_sql); //有问题!!
			}
			this.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		} else if (str_currEditState == WLTConstants.BILLDATAEDITSTATE_UPDATE) { ////...
			cascadeUpdate(str_sql); //级联修改!
			this.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		}
		saveKeepTrace(); //保存历史痕迹//如果需要保存历史版本,则存储一下历史版本,即存储进pub_bill_keeptrace表,可能有多个,只有在卡片情况下才能存储历史版本。//新增也要记录，否则第一条记录的内容丢失了
		getUIDataToTraceMap(); //将页面上的数据更新版本缓存
		this.updateUI(); //
	}

	/**
	 * 取得处理的SQL
	 * @return
	 */
	public String getUpdateDataSQL() {
		String str_currEditState = this.getEditState();
		if (str_currEditState == null) {
			return null;
		}

		if (str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { //
			return getInsertSQL(); ////新增的SQL..
		} else if (str_currEditState.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { ////...
			return getUpdateSQL(); ////更改的SQL
		} else {
			return null;
		}
	}

	/**
	 * 将控件中的值送入版式本缓存..
	 */
	private void getUIDataToTraceMap() {
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_itemkey = templetItemVOs[i].getItemkey(); //
			Object obj = getCompentObjectValue(templetItemVOs[i].getItemkey()); // 取得所有对象
			lastKeepTrace.put(str_itemkey, obj); //
		}
	}

	//级联修改!!!
	private void cascadeUpdate(String _sql) {
		try {
			String str_tableName = this.getTempletVO().getSavedtablename(); //保存的表名!!
			if (str_tableName == null || str_tableName.trim().equals("")) {
				return; //
			}
			String[][] str_changed = getChangedItemValues(); //所有变化的数据!!
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class); //
			service.doCascadeUpdateSQL(str_tableName, str_changed, _sql, true); //实际执行,保证是一个事务!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 取得保留痕迹的SQL
	 * @return
	 */
	public void saveKeepTrace() {//sunfujun/20120810/风格模板2需要调一下
		try {
			HashMap map_fieldvalus = new HashMap(); //
			for (int i = 0; i < templetItemVOs.length; i++) {
				if (templetItemVOs[i].getIskeeptrace()) { //如果需要保留痕迹
					String str_itemkey = templetItemVOs[i].getItemkey(); //
					Object obj_ui = getValueAt(str_itemkey); // 从页面上取值
					Object obj_trace = lastKeepTrace.get(str_itemkey); //
					if (obj_ui != null) {
						if (!obj_ui.toString().equals("" + obj_trace)) { //如果页面上的数据与最后修改历史不一样
							map_fieldvalus.put(str_itemkey, obj_ui.toString()); //
						}
					}
				}
			}

			//如果有需要保存的,则调用远程服务进行保存.
			if (map_fieldvalus.size() > 0) {
				String str_tablename = this.templetVO.getTablename(); //表名
				String str_pkname = this.templetVO.getPkname();
				String str_pkvalue = this.getRealValueAt(str_pkname); //
				if (str_tablename != null && str_pkname != null && str_pkvalue != null) {
					String str_tracer = ClientEnvironment.getInstance().getLoginUserName(); //登录人员名称..
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.saveKeepTrace(str_tablename, str_pkname, str_pkvalue, map_fieldvalus, str_tracer); //
				}
			}
		} catch (Exception ex) {
			this.logger.error("保存历史痕迹失败!"); //
			ex.printStackTrace(); //
		}
	}

	/**
	 * 插入新行,并执行默认值公式.
	 * @throws WLTAppException
	 */
	public void insertRow() throws WLTAppException {
		insertRow(true);
	}

	/**
	 * 插入新行数据,并指定是否执行默认值公式!
	 * @param _execdefaultfomula
	 * @throws WLTAppException
	 */
	public void insertRow(boolean _execdefaultfomula) throws WLTAppException {
		reset(); // 先清空数据
		this.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); // 设置行号VO,状态处于新增状态!!!这里rowId为空!!
		resetAllChildTable(); //清空所有引用子表
		setAllChildTableEditable(true); //设置所有引用子表不可编辑

		// 设置处理主键值!!!!
		String str_sequencename = templetVO.getPksequencename(); //
		if (str_sequencename == null || str_sequencename.trim().equals("")) {
			// JOptionPane.showMessageDialog(this, "没有定义序列名,无法为主键项设值!!"); //
		} else {
			if (templetVO.getPkname() != null) {
				AbstractWLTCompentPanel compent = getCompentByKey(templetVO.getPkname());
				if (compent == null) {
					//JOptionPane.showMessageDialog(this, "面板中没有ItemKey等于主键名[" + templetVO.getPkname() + "]的组件!!");
				} else {
					try {
						String sequenceValue = UIUtil.getSequenceNextValByDS(getDataSourceName(), str_sequencename);
						compent.setValue(sequenceValue); //设置主键值!!!
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "创建序列[" + str_sequencename + "]的nextVal失败!" + e.getMessage());
					} //
				}
			}

		}

		// 处理默认值公式!!!!
		if (_execdefaultfomula) { //如果需要执行默认值公式
			execDefaultValueFormula(); // //执行默认值公式
		}
		this.updateUI(); //
	}

	/**
	 * 设置的所有引用子表是否可编辑...
	 * @param _bo
	 */
	public void setAllChildTableEditable(boolean _bo) {
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (object instanceof CardCPanel_ChildTable) { ////
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object; ////
				compent.setItemEditable(_bo); //设置是否可编辑!!!
			}
		}
	}

	/**
	 * 重置所有引用引表...
	 * @param _bo
	 */
	public void resetAllChildTable() {
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (object instanceof CardCPanel_ChildTable) { ////
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object; ////
				if (compent != null && compent.getBillListPanel() != null) {
					compent.getBillListPanel().clearTable(); //
				}
			}
		}
	}

	//取消操作删除子表新增数据 【杨科/2013-03-26】
	public void dealChildTable(boolean isclear) {
		ArrayList list_allsqls = new ArrayList();
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i);
			if (object instanceof CardCPanel_ChildTable) {
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object;
				//若isclear为true 则为新增或保存 清理子表新增临时删除sql 追加子表删除临时删除sql
				//若isclear为false 则为取消或关闭 清理子表删除临时删除sql 追加子表新增临时删除sql
				compent.clearList_sqls(!isclear);
				ArrayList list_sqls = compent.getList_sqls(isclear);
				for (int j = 0; j < list_sqls.size(); j++) {
					list_allsqls.add(list_sqls.get(j));
				}
			}
		}

		if (list_allsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(getDataSourceName(), list_allsqls);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 修改当前行记录!!!!
	 * 
	 */
	public void updateCurrRow() {
		this.getRowNumberItemVO().setState(RowNumberItemVO.UPDATE); //
		setBorderColor(Color.BLUE); // 处于蓝色状态
		setBorderText(this.getBorderName()); //

		// 设置各控件是否可编辑!!!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // 如果是主键,则始终不能编辑!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("3")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false); //
					}
				}
			}
		} //

		this.updateUI(); //
	}

	/**
	 * 修改当前行记录!!!!
	 * 
	 */
	public void initCurrRow() {
		this.getRowNumberItemVO().setState(RowNumberItemVO.INIT); //
		setBorderColor(Color.BLACK); // 处于蓝色状态
		setBorderText(this.getTempletVO().getTempletname()); //

		// 设置各控件是否可编辑!!!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			compent.setItemEditable(false);
		} //

		this.updateUI(); //
	}

	public String getCompentRealValue(String _key) {
		int li_pos = _key.indexOf(".");
		String str_itemkey = null;
		String str_subfix = null;
		if (li_pos > 0) {
			str_itemkey = _key.substring(0, li_pos); //
			str_subfix = _key.substring(li_pos + 1, _key.length()); //
		} else {
			str_itemkey = _key;
		}

		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(str_itemkey)) {
				if (li_pos > 0) { //如果有后辍
					Object obj = compents[i].getObject();
					if (obj instanceof ComBoxItemVO) {
						return ((ComBoxItemVO) obj).getItemValue(str_subfix); //
					} else if (obj instanceof RefItemVO) {
						return ((RefItemVO) obj).getItemValue(str_subfix); //
					}
				} else {
					return compents[i].getValue();
				}
			}
		}
		return "";
	}

	private String getObjectRealValue(Object _obj) {
		if (_obj == null) {
			return null;
		}
		if (_obj instanceof StringItemVO) {
			return ((StringItemVO) _obj).getStringValue(); //
		}

		if (_obj instanceof ComBoxItemVO) {
			return ((ComBoxItemVO) _obj).getId(); //
		}

		if (_obj instanceof RefItemVO) {
			return ((RefItemVO) _obj).getId(); //
		}

		return _obj.toString();
	}

	public Pub_Templet_1_ItemVO getTempletItemVO(String key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(key))
				return templetItemVOs[i];
		}
		return null;
	}

	public Object getCompentObjectValue(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i].getObject(); //
			}
		}
		return null;
	}

	public Object getValueAt(String _key) {
		return getCompentObjectValue(_key);
	}

	public String getRealValueAt(String _key) {
		return getCompentRealValue(_key);
	}

	/**
	 * 设置真正的值
	 * 
	 * @param _key
	 * @param _value
	 */
	public void setRealValueAt(String _key, String _value) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent == null) {
			return;
		}

		compent.reset(); //

		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(_key);
		String str_type = itemVO.getItemtype();
		if (str_type.equals(WLTConstants.COMP_LABEL) || //Labe
				str_type.equals(WLTConstants.COMP_TEXTFIELD) || //文本框
				str_type.equals(WLTConstants.COMP_NUMBERFIELD) || //数字框
				str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || //密码
				str_type.equals(WLTConstants.COMP_TEXTAREA) || //多行文本框
				str_type.equals(WLTConstants.COMP_CHECKBOX) //勾选框
		) { //文本框
			setCompentObjectValue(_key, new StringItemVO(_value));
		} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) { //数字框
			int li_pos = _value.lastIndexOf(".0");
			if (li_pos > 0) {
				setCompentObjectValue(_key, new StringItemVO(_value.substring(0, li_pos))); //
			} else {
				setCompentObjectValue(_key, new StringItemVO(_value)); //
			}
		} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
			CardCPanel_ComboBox comBoxPanel = (CardCPanel_ComboBox) getCompentByKey(_key); //
			JComboBox comboBox = comBoxPanel.getComBox(); // 取得下拉框
			for (int i = 0; i < comboBox.getItemCount(); i++) { // 遍历!!
				ComBoxItemVO vo = (ComBoxItemVO) comboBox.getItemAt(i);
				if (vo.getId().equals(_value)) {
					comboBox.setSelectedIndex(i);
					break;
				}
			}
		} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
				str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT)) { //如果是各种参照1
			CardCPanel_Ref refPanel = (CardCPanel_Ref) getCompentByKey(_key); //
			if (refPanel.getObject() == null) { //如果为空,则直接设置
				refPanel.setObject(new RefItemVO(_value, _value, _value)); //直接设置
			} else { //否则只修改ID
				RefItemVO refitemVO = (RefItemVO) refPanel.getObject(); //
				refitemVO.setId(_value); //
			}
		} else if (str_type.equals(WLTConstants.COMP_DATE) || //日历
				str_type.equals(WLTConstants.COMP_DATETIME) || //时间
				str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
				str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
				str_type.equals(WLTConstants.COMP_PICTURE) || //图片
				str_type.equals(WLTConstants.COMP_COLOR) || //颜色
				str_type.equals(WLTConstants.COMP_LINKCHILD) || //引用子表
				str_type.equals(WLTConstants.COMP_IMPORTCHILD) || //导入子表
				str_type.equals(WLTConstants.COMP_FILECHOOSE) //文件选择
		) {
			setCompentObjectValue(_key, new RefItemVO(_value, null, _value)); //如果是其他控件!!
		} else {
			setCompentObjectValue(_key, new StringItemVO(_value)); //如果是其他控件!!
		}
	}

	//设置各个控件的前景颜色!文本框,下拉框,参照等,暂不实现,以后会慎重考虑是通过修改值再触发事件来实现，还是直接修改控件属性来实现!即如何保证值与控件属性保持一致!
	public void setItemForeGroundColor(String _key, String _foreGroundColor) {

	}

	public HashMap getAllRealValuesWithHashMap() {
		HashMap map = new HashMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase("VERSION")) {
				if (getCompentRealValue(templetItemVOs[i].getItemkey()) != null && !getCompentRealValue(templetItemVOs[i].getItemkey()).toString().equals("")) {
					int preversion = new Integer(getCompentRealValue(templetItemVOs[i].getItemkey()).toString()).intValue();
					Object version = new Integer(preversion + 1);
					map.put("VERSION", version);
				} else
					map.put(templetItemVOs[i].getItemkey(), new Integer(1).toString());
			} else
				map.put(templetItemVOs[i].getItemkey(), getCompentRealValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public Object[] getAllObjectValues() {
		Object[] objs = new Object[templetItemVOs.length];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = getCompentObjectValue(templetItemVOs[i].getItemkey()); // 取得所有对象
		}
		return objs;
	}

	public HashMap getAllObjectValuesWithHashMap() {
		HashMap map = new HashMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase("VERSION")) {
				if (getCompentObjectValue(templetItemVOs[i].getItemkey()) != null && !getCompentObjectValue(templetItemVOs[i].getItemkey()).toString().equals("")) {
					int preversion = new Integer(getCompentObjectValue(templetItemVOs[i].getItemkey()).toString()).intValue();
					Object version = new Integer(preversion + 1);
					map.put("VERSION", version);
				} else
					map.put(templetItemVOs[i].getItemkey(), new Integer(1).toString());
			} else
				map.put(templetItemVOs[i].getItemkey(), getCompentObjectValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public VectorMap getAllObjectValuesWithVectorMap() {
		VectorMap map = new VectorMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			map.put(templetItemVOs[i].getItemkey(), getCompentObjectValue(templetItemVOs[i].getItemkey()));
		}
		return map;
	}

	public void setValue(Object[] _objs) {
		reset(); // 重置所有控件
		this.setRowNumberItemVO((RowNumberItemVO) _objs[0]); // 设置行号数据VO!!!
		for (int i = 1; i < _objs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByIndex(i - 1); //
			String str_key = compent.getItemKey(); //
			setCompentObjectValue(str_key, _objs[i]); //
		}
	}

	public void setValue(HashMap _map) {
		reset();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			setCompentObjectValue(str_key, _map.get(str_key)); //
		}
	}

	//设置BillVO,应该在内存中记录下这时设置的值!然后点击确定按钮时,可以将页面上的实际值与原来的值进行比较,从而判断出是否发生了变化!! 
	public void setBillVO(BillVO _billVO) {
		reset(); //先清空card的所有数据！
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			try {
				setCompentObjectValue(str_key, _billVO.getObject(str_key)); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 如果从别的模板里得到的_billVO中字段要比BillCardPanel 里的字段少的话，那样会把BillCardPanel里的值覆盖，设置为空！所以需提供该方法
	 * @param _billVO
	 */
	public void setBillVOHasKey(BillVO _billVO) {
		String[] billVO_keys = _billVO.getKeys();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			try {
				for (int j = 0; j < billVO_keys.length; j++) {
					if (str_key.equals(billVO_keys[j])) {
						setCompentObjectValue(str_key, _billVO.getObject(str_key)); //
						break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setValue(HashVO _vo) {
		reset();
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			setCompentObjectValue(str_key, _vo.getObjectValue(str_key)); //
		}
	}

	//设置值....
	public void setCompentObjectValue(String _key, Object _obj) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent != null) {
			lastKeepTrace.put(_key, _obj); //先在历史版本中记录下当前页面上的数据!!!!
			compent.setObject(_obj); //
		}
	}

	/**
	 *取得发生变化的数据的旧数据,即最后一次通过API赋值的值!!
	 * @return
	 */
	public HashMap getChangedItemOldValues() {
		HashMap hm_return = new HashMap(); //
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (compent.isVisible()) { //如果该控件是显示,因为不显示,就不能查看,所以没有意义
				String str_itemkey = compent.getItemKey(); ////
				Object old_obj = lastKeepTrace.get(str_itemkey); //oldversion,旧数据
				Object new_obj = compent.getObject(); //当前页面数据!!
				if (compent instanceof CardCPanel_CheckBox) { //如果是勾选框,需要特别处理,很是烦!
					if (old_obj == null) {
						old_obj = new StringItemVO("N"); //
					}

					if (new_obj == null) {
						new_obj = new StringItemVO("N"); //
					}

					if (!old_obj.equals(new_obj)) {
						hm_return.put(str_itemkey, old_obj); //
					}
					continue; //
				}

				if (old_obj == null) { //如果旧值为空!!
					if (new_obj == null) { //如果新值不为空
					} else {
						if (!new_obj.equals(old_obj)) { //如果两者不等
							hm_return.put(str_itemkey, old_obj); //
						}
					}
				} else { //如果旧值不为空
					if (new_obj == null) { //如果新值为空
						if (!old_obj.equals(new_obj)) { //如果两者不等
							hm_return.put(str_itemkey, old_obj); //
						}
					} else {
						if (old_obj.equals(new_obj)) { //两者都不为空!!
						} else {
							hm_return.put(str_itemkey, old_obj); //如者不等,则创建之
						}
					}
				}
			}
		}
		return hm_return; //
	}

	//取得变化的值!!
	public String[][] getChangedItemValues() {
		ArrayList al_return = new ArrayList(); //
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (compent.isVisible()) { //如果该控件是显示,因为不显示,就不能查看,所以没有意义
				String str_itemkey = compent.getItemKey(); ////
				Object old_obj = lastKeepTrace.get(str_itemkey); //oldversion,旧数据
				String str_oldRealValue = getObjectRealValue(old_obj); //
				String str_newRealValue = getRealValueAt(str_itemkey); //当前页面数据!!
				boolean isEquals = getTBUtil().compareTwoString(str_oldRealValue, str_newRealValue); //
				if (!isEquals) { //如果不相等!!
					al_return.add(new String[] { str_itemkey, str_oldRealValue, str_newRealValue }); //
				}
			}
		}
		String[][] str_return = new String[al_return.size()][3]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = (String[]) al_return.get(i); //
		}
		return str_return; //
	}

	/**
	 * 取得发生变化的项的旧数据的SQL
	 * @return
	 */
	public String[] getChangedItemOldValueSQL() {
		HashMap hm_changed = getChangedItemOldValues(); //
		String[] str_keys = (String[]) hm_changed.keySet().toArray(new String[0]); //
		String[] str_sqls = new String[str_keys.length]; //

		for (int i = 0; i < str_keys.length; i++) {
			String str_value = "null"; //
			Object obj = hm_changed.get(str_keys[i]); //
			if (obj != null) {
				str_value = "'" + obj + "'";
			}
			str_sqls[i] = "insert into pub_billcolhistory (id,creater,createtime,tabname,colname,colvalue) value ('','','" + str_keys[i] + "'," + str_value + ")"; //
			//System.out.println(str_sqls[i]); //
		}

		return str_sqls;
	}

	//设置值..
	public void setValueAt(String _key, Object _obj) {
		setCompentObjectValue(_key, _obj);
	}

	public AbstractWLTCompentPanel[] getAllCompents() {
		return (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
	}

	public AbstractWLTCompentPanel getCompentByIndex(int _index) {
		String key = templetItemVOs[_index].getItemkey();
		return getCompentByKey(key);
	}

	public AbstractWLTCompentPanel getCompentByKey(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i];
			}
		}

		return null;
	}

	/**
	 * 取得某一项的所有公式,它是一个数组
	 * 
	 * @param _itemkey
	 * @return
	 */
	public String[] getEditFormulas(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				String str_formula = templetItemVOs[i].getEditformula();
				if (str_formula != null && !str_formula.trim().equals("null") && !str_formula.trim().equals("")) {
					String[] str_editformulas = getTBUtil().split1(str_formula, ";");
					return str_editformulas;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private synchronized void onChanged(String _itemkey) {
		if (bo_isallowtriggereditevent) { // 如果允许触发
			bo_isallowtriggereditevent = false;
			Object obj = this.getCompentObjectValue(_itemkey); // 取得当前值
			execEditFormula(_itemkey, false); // 执行编辑公式..它与下一行代码谁先谁后还有待进一步考虑!!!!!!
			for (int i = 0; i < v_listeners.size(); i++) {
				BillCardEditListener listener = (BillCardEditListener) v_listeners.get(i);
				listener.onBillCardValueChanged(new BillCardEditEvent(_itemkey, obj, this)); //
			}

			bo_isallowtriggereditevent = true; //
		}
	}

	private synchronized void onChangedAndFocusNext(String _itemkey) {
		onChanged(_itemkey);
		AbstractWLTCompentPanel actionpanel = this.getNextCompent(_itemkey); //
		//光标跳到下一个!
		if (actionpanel != null) {
			actionpanel.focus(); //
		}
	}

	/**
	 * 
	 * @param _itemkey
	 * @return
	 */
	private AbstractWLTCompentPanel getNextCompent(String _itemkey) {
		int li_startindex = findIndex(_itemkey);
		for (int i = li_startindex + 1; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].isCardisshowable().booleanValue()) { //如果某个控件是卡片显示的!!
				AbstractWLTCompentPanel panel = getCompentByKey(templetItemVOs[i].getItemkey()); //
				if (panel != null && panel.isVisible()) { //如果控件在,而且显示着!!
					return panel;
				}
			}
		}

		return null;
	}

	private int findIndex(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return i;
			}
		}
		return 9999;
	}

	/**
	 * 发现某一个模板ItemVO
	 * @param _itemkey
	 * @return
	 */
	private Pub_Templet_1_ItemVO findTempletItemVO(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i]; //
			}
		}

		return null; //
	}

	/**
	 * 注册事件
	 * 
	 * @param _listener
	 */
	public void addBillCardEditListener(BillCardEditListener _listener) {
		v_listeners.add(_listener); //
	}

	/**
	 * 执行某一项的加载公式!
	 */
	public void execEditFormula(String _itemkey) {
		execEditFormula(_itemkey, true);
	}

	/**
	 * 是否总是执行即不管控件是否可编辑也执行
	 * @param _itemkey
	 * @param iseverExec
	 */
	public void execEditFormula(String _itemkey, boolean iseverExec) {
		if (!iseverExec) {
			AbstractWLTCompentPanel compent = getCompentByKey(_itemkey);
			if (compent == null || !compent.isItemEditable()) { // 如果控件不可编辑则不执行编辑公式
				return;
			}
		}
		String[] str_editformulas = getEditFormulas(_itemkey); //
		if (str_editformulas == null || str_editformulas.length == 0) {
			return;
		}

		// 循环处理公式...
		for (int i = 0; i < str_editformulas.length; i++) {
			dealFormula(str_editformulas[i]); // 真正执行公式!!!
			logger.debug("卡片执行[" + _itemkey + "]的编辑公式:[" + str_editformulas[i] + "]"); //
		}
	}

	/**
	 * 指定光标停在某个控件上!
	 * @param _itemKey
	 */
	public void focusAt(String _itemKey) {
		AbstractWLTCompentPanel[] itemCompents = getAllCompents(); //
		for (int i = 0; i < itemCompents.length; i++) {
			if (itemCompents[i].getItemKey().equalsIgnoreCase(_itemKey)) {
				itemCompents[i].focus(); //光标停在上面!!!
				break; //
			}
		}
	}

	/**
	 * 以前经常有直接点保存按钮时不执行光标所在的最终后一个控件的编辑公式!
	 * 现在提供该方法,就是去找一下最后所在位置!然后强行执行一下编辑公式!!
	 */
	public void stopEditing() {
		AbstractWLTCompentPanel[] itemCompents = getAllCompents(); //
		for (int i = 0; i < itemCompents.length; i++) {
			if (itemCompents[i].isFocusOwner()) { //如果该控件是光标所在,则执行其编辑公式!!! 有控件重构了该方法!! 比如文本框!!
				execEditFormula(itemCompents[i].getItemKey(), false); //人工执行编辑公式!!
				this.setFocusable(true); //
				this.requestFocus(); //让自己得到光标,保证下次不再重复触发编辑公式!
				break; //
			}
		}
	}

	/**
	 * 执行默认值方式
	 * 
	 */
	public void execDefaultValueFormula() throws WLTAppException {// 目前直接的字符串或者{Date(10)},{Date(19)}
		for (int i = 0; i < templetItemVOs.length; i++) {
			Pub_Templet_1_ItemVO tempitem = templetItemVOs[i];
			String str_key = tempitem.getItemkey(); //
			String str_type = tempitem.getItemtype(); //
			String formula = tempitem.getDefaultvalueformula();
			if (formula != null && !formula.trim().equals("")) {
				JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
				String[] str_formulas = getTBUtil().split1(formula, ";"); //
				for (int j = 0; j < str_formulas.length; j++) {
					//System.out.println("执行默认值公式:\r\n" + str_formulas[j]); 
					Object obj = jepParse.execFormula(str_formulas[j]); //

					//System.out.println("默认值公式执行的结果:" + obj);  
					if (j == str_formulas.length - 1) { //如是是最后一个,则认为是返回值
						if ("Y".equals(tempitem.getIsmustinput2())) { //如果是必输项..
							if (obj == null) { //
								throw new WLTAppException("请选择一个[" + tempitem.getItemname() + "]!"); //
							}

							if (obj instanceof String) { //
								String vo = (String) obj; //
								if (vo == null || vo.equals("")) {
									throw new WLTAppException("请选择一个[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof StringItemVO) {
								StringItemVO vo = (StringItemVO) obj; //
								if (vo == null || vo.getStringValue() == null || vo.getStringValue().equals("")) {
									throw new WLTAppException("请选择一个[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof ComBoxItemVO) {
								ComBoxItemVO vo = (ComBoxItemVO) obj; //
								if (vo == null || vo.getId() == null || vo.getId().equals("")) {
									throw new WLTAppException("请选择一个[" + tempitem.getItemname() + "]!"); //
								}
							} else if (obj instanceof RefItemVO) {
								RefItemVO vo = (RefItemVO) obj; //
								if (vo == null || vo.getId() == null || vo.getId().equals("")) {
									throw new WLTAppException("请选择一个[" + tempitem.getItemname() + "]!"); //
								}
							}
						} //end 如果是必输入项

						try {
							if (obj instanceof String) { //
								String str_objstr = (String) obj; //
								if (str_objstr.equals("ok")) { //如果是ok则跳过
									continue; //
								}

								if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //如果是下拉框
									setCompentObjectValue(str_key, new ComBoxItemVO(str_objstr, str_objstr, str_objstr)); //
								} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
										str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
										str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照1
										str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
										str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
										str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
										str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
										str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
										str_type.equals(WLTConstants.COMP_DATE) || //日历
										str_type.equals(WLTConstants.COMP_DATETIME) || //时间
										str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
										str_type.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框
										str_type.equals(WLTConstants.COMP_COLOR) || //颜色选择框
										str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
										str_type.equals(WLTConstants.COMP_PICTURE) //图片选择框
								) { //如果是参照...
									setCompentObjectValue(str_key, new RefItemVO(str_objstr, str_objstr, str_objstr)); //
								} else {
									setCompentObjectValue(str_key, new StringItemVO(str_objstr)); //
								}
							} else if (obj instanceof java.lang.Double) {
								setCompentObjectValue(str_key, new StringItemVO("" + obj)); //
							} else {
								setCompentObjectValue(str_key, obj); //
							}
						} catch (Exception ex) {
							this.logger.error("设置[" + str_key + "]的默认值失败!!!", ex); //
						}

					}
				}

			}
		}
	} //

	/**
	 * 真正执行某一个公式..使用JEP去执行!!
	 * 
	 * @param _formula
	 */
	private void dealFormula(String _formula) {
		//String str_formula = UIUtil.replaceAll(_formula, " ", "");
		String str_subfix_new_value = getJepFormulaValue(_formula); //
	}

	private String getJepFormulaValue(String _exp) {
		JepFormulaParse jepParse = new JepFormulaParseAtUI(this); //创建解析器!!
		Object obj = jepParse.execFormula(_exp);
		if (obj == null) {
			return "";
		}
		String str_return = "";

		if (obj instanceof Double) {
			Double d_value = (Double) obj;
			double ld_tmp = d_value.doubleValue();
			long ll_tmp = d_value.longValue(); //
			if (ld_tmp == ll_tmp) {
				return "" + ll_tmp;
			} else {
				return "" + ld_tmp;
			}
		} else if (obj instanceof String) {
			str_return = "" + obj;
		}

		return str_return;
	}

	/**
	 * 取得行号数据VO
	 * 
	 * @return
	 */
	public RowNumberItemVO getRowNumberItemVO() {
		return rowNumberItemVO; //
	}

	/**
	 * 设置行号数据VO
	 * 
	 * @param rowNumberItemVO
	 */
	public void setRowNumberItemVO(RowNumberItemVO rowNumberItemVO) {
		this.rowNumberItemVO = rowNumberItemVO;
	}

	/**
	 * 快速取得当前状态!
	 * 
	 * @return
	 */
	public String getEditState() {
		return rowNumberItemVO.getState(); //
	}

	public void setEditState(String _state) {
		if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(Color.BLACK); //
		} else if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(new Color(0, 128, 0)); //
			this.updateUI();
		} else if (_state.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			this.rowNumberItemVO.setState(_state);
			setBorderColor(Color.BLUE); //
		}

		this.updateUI();
	}

	/**
	 * 设置所有控件状态为模板中定义的新增时的状态
	 */
	public void setEditableByInsertInit() {
		this.setEditable(false);
		// 设置各控件是否可编辑!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // 如果是主键,则始终不能编辑!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("2")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false);
					}
				}
			}
		} //
	}

	/**
	 * 设置所有控件状态为模板中定义的编辑时的状态
	 */
	public void setEditableByEditInit() {
		this.setEditable(false);
		// 设置各控件是否可编辑!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			if (compent != null) {
				if (templetItemVOs[i].getItemkey().equalsIgnoreCase(templetVO.getPkname())) {
					compent.setItemEditable(false); // 如果是主键,则始终不能编辑!!!
				} else {
					if (templetItemVOs[i].getCardiseditable().equals("1") || templetItemVOs[i].getCardiseditable().equals("3")) {
						compent.setItemEditable(true);
					} else {
						compent.setItemEditable(false);
					}
				}
			}
		} //
	}

	/**
	 * 设置所有控件状态为模板中定义的工作流时的状态
	 */
	public void setEditableByWorkFlowInit() {
		//this.setEditable(false);  //统统禁用
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			AbstractWLTCompentPanel compent = getCompentByKey(templetItemVOs[i].getItemkey());
			boolean isWFEditable = templetItemVOs[i].getWorkflowiseditable().booleanValue();
			if (compent != null) {
				if (compent instanceof CardCPanel_Button) {
					compent.setItemEditable(true);
				} else if (compent instanceof CardCPanel_ChildTable) {
					CardCPanel_ChildTable cchildTable = (CardCPanel_ChildTable) compent;
					cchildTable.setOnlyLookat(); //只可以查看！
				} else {
					compent.setItemEditable(isWFEditable); //
				}
			}
		}
	}

	private void setBorderColor(Color _color) {
		if (border instanceof TitledBorder) {
			((TitledBorder) this.border).setTitleColor(_color); //
		}
	}

	private void setBorderText(String _text) {
		if (border instanceof TitledBorder) {
			((TitledBorder) this.border).setTitle(_text); //
		}
	}

	public BillVO getBillVO() {
		return getBillVO(true);
	}

	/**
	 * 得到当前数据
	 * 
	 * @return
	 */
	public BillVO getBillVO(boolean _bo) {
		BillVO vo = new BillVO();
		vo.setTempletCode(templetVO.getTempletcode()); //
		vo.setTempletName(templetVO.getTempletname()); //
		vo.setQueryTableName(templetVO.getTablename());
		vo.setSaveTableName(templetVO.getSavedtablename()); //
		vo.setPkName(templetVO.getPkname()); // 主键字段名
		vo.setSequenceName(templetVO.getPksequencename()); // 序列名
		vo.setToStringFieldName(templetVO.getTostringkey()); //设置ToString的字段名!!!后来加了这个,在工作流中需要指定任务事项!!

		int li_length = templetItemVOs.length;

		// 所有ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = this.str_rownumberMark; // 行号
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		// 所有的名称
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = "行号"; // 行号
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = "行号"; // 行号
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // 行号
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = this.templetItemVOs[i - 1].getSavedcolumndatatype(); //
		}

		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // 行号
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = this.templetItemVOs[i - 1].isNeedSave();
		}

		vo.setKeys(all_Keys); //
		vo.setNames(all_Names); //
		vo.setItemType(all_Types); // 控件类型!!
		vo.setColumnType(all_ColumnTypes); // 数据库类型!!
		vo.setNeedSaves(bo_isNeedSaves); // 是否需要保存!!

		Object[] allObjs = getAllObjectValues();
		Object[] newObjs = new Object[allObjs.length + 1]; //
		newObjs[0] = getRowNumberItemVO(); //
		for (int i = 0; i < allObjs.length; i++) {
			newObjs[i + 1] = allObjs[i];
		}
		if (_bo) {
			for (int i = 1; i < all_Types.length; i++) {
				String type = this.templetVO.getItemTypes()[i - 1];
				if (type.endsWith("子表")) {
					String key = all_Keys[i];
					BillListPanel list = null;
					if (type.equals(WLTConstants.COMP_LINKCHILD)) {
						CardCPanel_ChildTable childListPanel = (CardCPanel_ChildTable) this.getCompentByKey(key);
						childListPanel.setObject(null);
						list = childListPanel.getBillListPanel();
					} else {
						CardCPanel_ChildTableImport tableImport = (CardCPanel_ChildTableImport) this.getCompentByKey(key);
						//tableImport.setObject(vo.getRefItemVOValue(key));
						list = tableImport.getBillListPanel();
					}

					Object[] childTableInfo = new Object[2];
					childTableInfo[0] = list.getBillVOs();
					childTableInfo[1] = list.getTempletVO();
					vo.setUserObject(key, childTableInfo);
				}
			}
		}
		vo.setDatas(newObjs); //
		return vo; //
	}

	public boolean checkValidate() {
		return checkValidate(this);
	}
	/**
	 * 校验
	 * @return
	 */
	public boolean checkValidate(java.awt.Container _parent) {
		return (checkIsNullValidate() && checkItemLengthValidate(_parent) && checkIsUniqueValidate(_parent)&&checkSelfDescValidate()); //先只处理非空校验,以后还会增加校验公式!!
	}

	public boolean newCheckValidate(String type) {//包括保存和提交（确定）的验证save与submit
		return newCheckValidate(type, this);
	}

	public boolean newCheckValidate(String type, java.awt.Container _parentContainer) {//包括保存和提交（确定）的验证save与submit
		if ("save".equals(type)) {
			if (this.getTempletVO().getCardsaveifcheck()) {
				return (checkSelfDescValidate() && checkItemLengthValidate(_parentContainer));
			} else {
				return checkValidate();
			}
		} else {
			return checkValidate();
		}

	}

	/**
	 * 自定义验证 
	 * 自定义验证器继承AbstractCardSaveSelfCheck
	 * @return
	 */
	private boolean checkSelfDescValidate() {
		String desc = this.getTempletVO().getCardsaveselfdesccheck();
		if (desc != null && !"".equals(desc.trim())) {
			try {
				AbstractCardSaveSelfCheck acsc = (AbstractCardSaveSelfCheck) Class.forName(desc).newInstance();
				return acsc.checkValidate(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean checkIsNullValidate() {
		return checkIsNullValidate(this); //
	}

	/**
	 * 非空和警告校验,
	 * @return
	 */
	private boolean checkIsNullValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); //所有的key
		String[] str_names = this.getTempletVO().getItemNames(); //所有的Name
		boolean[] bo_isMustInputs = this.getTempletVO().getItemIsMustInputs(); //是否必输入
		boolean[] bo_cardShowAble = this.getTempletVO().getItemCardShowAble();//是否可见
		StringBuffer showmsg = new StringBuffer();
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		boolean ifcheck = false;
		for (int i = 0; i < str_keys.length; i++) { //遍历所有项
			if (bo_isMustInputs[i] && bo_cardShowAble[i]) {
				for (int j = 0; j < compents.length; j++) {
					if (compents[j].getItemKey().equalsIgnoreCase(str_keys[i])) {
						String grouptitle = this.getTempletItemVO(compents[j].getItemKey()).getGrouptitle();
						if (grouptitle == null || "".equals(grouptitle.trim())) {
							if (compents[j].isVisible() && compents[j].isItemEditable()) { //如果该控件没有组的话，并且控件显示，则校验；否则不校验
								ifcheck = true;
								break;
							}
							break;
						} else if (compents[j].isVisible() && compents[j].isItemEditable() && isGroupVisiable(grouptitle)) { //该控件有组，控件显示并且控件所属组显示，则校验；否则不校验
							ifcheck = true;
							break;
						}
						break;
					}
				}

				if (ifcheck) {
					ifcheck = false;
					Object obj = getCompentObjectValue(str_keys[i]);
					if (obj == null) {
						showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】不能为空!\r\n"); //
					} else {
						if (obj instanceof StringItemVO) {
							StringItemVO new_name = (StringItemVO) obj;
							if (new_name == null || "".equals(new_name.toString().trim())) {
								showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】不能为空!\r\n"); //
							}
						} else if (obj instanceof RefItemVO) {
							RefItemVO new_name = (RefItemVO) obj;
							if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {//
								showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】不能为空!\r\n"); //
							}
						} else if (obj instanceof ComBoxItemVO) {
							ComBoxItemVO new_name = (ComBoxItemVO) obj;
							if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {//保存的是id，这里应该判断id是否为空【李春娟/2012-08-15】
								showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】不能为空!\r\n"); //
							}
						} else {
							String new_name = (String) obj;
							if (new_name == null || "".equals(new_name.trim())) {
								showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】不能为空!\r\n"); //
							}
						}
					}
				}
			}
		}

		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) { //如果必输项都不为空，则判断警告项！			
			boolean[] bo_isWarnInputs = this.getTempletVO().getItemIsWarnInputs(); //是否警告项			
			for (int i = 0; i < str_keys.length; i++) {
				if (bo_isWarnInputs[i] && bo_cardShowAble[i]) { //如果是警告项并且可见!!
					for (int j = 0; j < compents.length; j++) {
						if (compents[j].getItemKey().equalsIgnoreCase(str_keys[i])) {
							String grouptitle = this.getTempletItemVO(compents[j].getItemKey()).getGrouptitle();
							if (grouptitle == null || "".equals(grouptitle.trim())) {
								if (compents[j].isVisible()) { //如果该控件没有组的话，并且控件显示，则校验；否则不校验
									ifcheck = true;
									break;
								}
								break;
							} else if (compents[j].isVisible() && isGroupVisiable(grouptitle)) { //该控件有组，控件显示并且控件所属组显示，则校验；否则不校验
								ifcheck = true;
								break;
							}
							break;
						}
					}
					if (ifcheck) {
						ifcheck = false;
						Object obj = getCompentObjectValue(str_keys[i]);
						if (obj == null) {
							showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】为空!\r\n"); //
						} else {
							if (obj instanceof StringItemVO) {
								StringItemVO new_name = (StringItemVO) obj;
								if (new_name == null || "".equals(new_name.toString().trim())) {
									showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】为空!\r\n"); //
								}
							} else if (obj instanceof RefItemVO) {
								RefItemVO new_name = (RefItemVO) obj;
								if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {
									showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】为空!\r\n"); //
								}
							} else if (obj instanceof ComBoxItemVO) {
								ComBoxItemVO new_name = (ComBoxItemVO) obj;
								if (new_name == null || new_name.getId() == null || "".equals(new_name.getId().trim())) {
									showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】为空!\r\n"); //
								}
							} else {
								String new_name = (String) obj;
								if (new_name == null || "".equals(new_name.trim())) {
									showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】为空!\r\n"); //
								}
							}
						}
					}
				}
			}
			if ("".equals(showmsg.toString())) {
				return true;
			}
			showmsg.append(" 是否继续?");
			int option = MessageBox.showConfirmDialog(_parent, showmsg.toString(), "提示", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.show(_parent, str_showmsg);
		return false;
	}

	/**
	 * 长度校验,
	 * @return
	 */
	private boolean checkItemLengthValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); //所有的key
		String[] str_names = this.getTempletVO().getItemNames(); //所有的Name
		int[] str_lengths = this.getTempletVO().getItemLengths(); //所有的Name对应的length
		int[] str_lengthscale = this.getTempletVO().getItemLengthsScale(); //所有的Name对应的length
		StringBuffer showmsg = new StringBuffer();
		String zfj = getTBUtil().getSysOptionStringValue("数据库字符集", "GBK");//以前只判断GBK，如果是UTF-8，一个汉字占三个字节，故校验可能通过，但保存数据库报错，故设置该参数【李春娟/2016-04-26】
		for (int i = 0; i < str_keys.length; i++) {
			Object obj = getCompentObjectValue(str_keys[i]);
			boolean ifsave = getTempletItemVO(str_keys[i]).getIssave();//只有参与保存的才做长度校验
			if (obj != null && ifsave) {
				int length = 0;
				int dobbefore = 0;//最后弹出错误框的时候用到  20140127袁江晓更改
				boolean checkSuc = true;
				boolean checktemp = true;//主要判断输入为整形但是数据库中为浮点型的问题
				if (obj instanceof StringItemVO) {
					StringItemVO new_name = (StringItemVO) obj;
					if (str_lengthscale[i] == 0) {//这个是正常的没有小数点的
						//20140127 袁江晓更改逻辑 如果是数据库中字段带有(9,2)比如mysql中的DECIMAL的长度为10,2之类的
						if (str_lengths[i] != -1) {
							try {
								length = new_name.toString().getBytes(zfj).length;
							} catch (Exception e) {
								this.getTBUtil().getStrUnicodeLength(new_name.toString());//万一根据GBK编码格式来判断出现错误/异常,则根据UNICODE编码格式查询字符串的字节长度
							}
							if (length > str_lengths[i]) {
								checkSuc = false;
							}
						}
					} else {//只能为浮点型
						//如果小数点前的大于数据库中的则报错   如果小数点后的大与数据库中的则截取 
						int dotplace = -1;
						try {
							length = new_name.toString().getBytes(zfj).length;//实际输入的所有字符的长度
							dotplace = new_name.toString().lastIndexOf(".");//小数点的位置
							if (dotplace != -1) {//如果不存在小数点
								dobbefore = dotplace;//求的实际的小数点前的位数
							}
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.toString());//万一根据GBK编码格式来判断出现错误/异常,则根据UNICODE编码格式查询字符串的字节长度
						}
						if (dotplace == -1) {
							if (length > str_lengths[i]) {
								checkSuc = false;
							}
							if (length > str_lengths[i] - str_lengthscale[i]) {//袁江晓 20140226 添加
								checkSuc = false;
								checktemp = false;
							}
						} else if (dobbefore > str_lengths[i] - str_lengthscale[i]) {//如果有小数点则小数点前的位数应该为9-2=7
							checkSuc = false;
						}
					}
				} else if (obj instanceof RefItemVO) {
					if (str_lengths[i] != -1) {
						RefItemVO new_name = (RefItemVO) obj;
						if (new_name.getId() == null) {
							continue;
						}
						try {
							length = new_name.getId().getBytes(zfj).length;
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.getId());//万一根据GBK编码格式来判断出现错误/异常,则根据UNICODE编码格式查询字符串的字节长度
						}
						if (length > str_lengths[i]) {
							checkSuc = false;
						}
					}
				} else if (obj instanceof ComBoxItemVO) {
					if (str_lengths[i] != -1) {
						ComBoxItemVO new_name = (ComBoxItemVO) obj;
						if (new_name.getId() == null) {
							continue;
						}
						try {
							length = new_name.getId().getBytes(zfj).length;
						} catch (Exception e) {
							this.getTBUtil().getStrUnicodeLength(new_name.getId());//万一根据GBK编码格式来判断出现错误/异常,则根据UNICODE编码格式查询字符串的字节长度
						}
						if (length > str_lengths[i]) {
							checkSuc = false;
						}
					}
				}
				if (!checkSuc) {
					if (!checktemp) {
						int ii = str_lengths[i] - str_lengthscale[i];
						showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】的数据长度(" + length + "个字节)超过系统约束的上限(" + ii + "个字节),不能保存!\r\n"); //
					} else if (dobbefore == 0) {//数据库中不带小数点的或者是输入的数据中没有小数点的
						showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】的数据长度(" + length + "个字节)超过系统约束的上限(" + str_lengths[i] + "个字节),不能保存!\r\n"); //
					} else {
						int ii = str_lengths[i] - str_lengthscale[i];
						showmsg.append("【" + getTBUtil().getTrimSwapLineStr(str_names[i]) + "】的小数点前的数据长度(" + dobbefore + "个字节)超过系统约束的上限(" + ii + "个字节),不能保存!\r\n"); //
					}
				}
			}
		}

		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			return true;
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.showTextArea(_parent, str_showmsg);
		return false;
	}

	private boolean checkIsUniqueValidate(java.awt.Container _parent) {
		String[] str_keys = this.getTempletVO().getItemKeys(); // 所有的key
		String[] str_names = this.getTempletVO().getItemNames(); // 所有的Name
		boolean[] bo_isUnique = this.getTempletVO().getItemIsUnique(); // 是否校验唯一性
		StringBuffer showmsg = new StringBuffer();
		Vector v_temp = new Vector();

		for (int j = 0; j < str_keys.length; j++) {
			if (bo_isUnique[j]) { // 如果需要校验唯一性!!
				v_temp.add("select '" + str_names[j] + "' from " + this.getTempletVO().getTablename() + " where " + this.getBillVO().getPkName() + "<>" + this.getBillVO().getPkValue() + " and " + str_keys[j] + "='" + this.getRealValueAt(str_keys[j]) + "'");
			}
		}
		if (v_temp.size() == 0) { //没有需要校验唯一性的项
			return true;
		}
		String[] sqls = (String[]) v_temp.toArray(new String[0]);
		try {
			v_temp = UIUtil.getCommonService().getHashVoArrayReturnVectorByDS(null, sqls);

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (v_temp.size() == 0) {
			return true;
		} else {
			HashVO[] hashvo = null;
			for (int i = 0; i < v_temp.size(); i++) {
				hashvo = (HashVO[]) v_temp.get(i);
				if (hashvo != null && hashvo.length > 0) {
					if (hashvo[0] != null) {
						showmsg.append("【" + hashvo[0].getStringValue(0) + "】不满足唯一性，请输入其他数据!\r\n");
					}
				}
			}
		}
		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			return true;
		}
		str_showmsg = str_showmsg.substring(0, str_showmsg.length() - 2); //
		MessageBox.showTextArea(_parent, str_showmsg);
		return false;
	}

	/**
	 * 显示页面上的实际数据
	 */
	public void showUIRecord() {
		String[] str_tabcolumnnames = new String[] { "ItemKey", "ItemName", "类型", "实际值类型", "实际值内容" }; //
		String[] str_itemkeys = this.templetVO.getItemKeys(); //
		String[] str_itemnames = this.templetVO.getItemNames(); //
		String[] str_itemtypes = this.templetVO.getItemTypes(); //
		String[] str_valuetype = new String[str_itemkeys.length]; //
		String[] str_valuedata = new String[str_itemkeys.length]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			Object obj = this.getValueAt(str_itemkeys[i]); //
			if (obj != null) {
				str_valuetype[i] = obj.getClass().getName(); //
				if (obj instanceof ComBoxItemVO) {
					ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else if (obj instanceof RefItemVO) {
					RefItemVO itemVO = (RefItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else {
					str_valuedata[i] = obj.toString(); //
				}
			}
		}

		String[][] str_tabdata = new String[str_itemkeys.length][5]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			str_tabdata[i][0] = str_itemkeys[i];
			str_tabdata[i][1] = str_itemnames[i];
			str_tabdata[i][2] = str_itemtypes[i];
			str_tabdata[i][3] = str_valuetype[i];
			str_tabdata[i][4] = str_valuedata[i];
		}

		JTable tmptable = new JTable(str_tabdata, str_tabcolumnnames); //
		tmptable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //

		tmptable.getColumnModel().getColumn(0).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(1).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(2).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(3).setPreferredWidth(255);
		tmptable.getColumnModel().getColumn(4).setPreferredWidth(150);

		BillDialog dialog = new BillDialog(this, "查看UI实际数据", 800, 350); //
		dialog.getContentPane().add(new JScrollPane(tmptable)); //

		JLabel label_warn = new JLabel("★★★如果发现某个字段数据为空,则千万要注意可能是从某个视图查询造成的!"); //
		label_warn.setBackground(Color.RED); //
		label_warn.setForeground(Color.WHITE); //
		label_warn.setOpaque(true); //
		dialog.getContentPane().add(label_warn, BorderLayout.NORTH); //

		JLabel label_warn2 = new JLabel("★★★如果感觉某个字段数据不对,则注意使用另一个【查看DB实际数据】功能进行比较!"); //
		label_warn2.setBackground(Color.RED); //
		label_warn2.setForeground(Color.WHITE); //
		label_warn2.setOpaque(true); //
		dialog.getContentPane().add(label_warn2, BorderLayout.SOUTH); //
		dialog.setVisible(true); //
	}

	/**
	 * 显示数据库实际数据
	 */
	public void showDBRecordData() {
		try {
			String str_tablename = this.templetVO.getTablename();
			String str_pkfieldname = this.templetVO.getPkname(); //
			String str_pkvalue = this.getRealValueAt(str_pkfieldname); //
			if (str_tablename == null || str_pkfieldname == null || str_pkvalue == null) {
				MessageBox.show(this, "表名/主键名/主键值为空,tablename=[" + str_tablename + "],pkfieldname=[" + str_pkfieldname + "],pkvalue[" + str_pkvalue + "]");//
				return;
			}
			new RecordShowDialog(this, str_tablename, str_pkfieldname, str_pkvalue);
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 将页面导出成Html
	 */
	public void exportToHTml() throws Exception {
		cn.com.infostrategy.ui.report.BillHtmlDialog dialog = new cn.com.infostrategy.ui.report.BillHtmlDialog(this, this.getTempletVO().getTempletname(), getExportHtml()); //
		dialog.setVisible(true); //
	}

	/**
	 * 导出html报表，根据模板子表中的cardisexport导出
	 */
	public void exportToReportHtml() {
		String htmlStr = "";
		try {
			FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			htmlStr = service.getCardReportHtml(this.getTempletVO(), this.getBillVO());
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		cn.com.infostrategy.ui.report.BillHtmlDialog dialog = new cn.com.infostrategy.ui.report.BillHtmlDialog(this, this.getTempletVO().getTempletname(), htmlStr); //
		dialog.setSize(900, dialog.getHeight());
		dialog.setVisible(true); //
	}

	private String getHtmlHead() {
		StringBuilder sb_html = new StringBuilder(); //
		//生成html头
		sb_html.append("<html>\r\n"); //
		sb_html.append("<head>\r\n"); //
		sb_html.append("<title>BillCard导出</title>\r\n"); //
		sb_html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n"); //
		//sb_html.append("<LINK href=\"./applet/exportHTML.css\" type=text/css rel=stylesheet />\r\n"); //
		sb_html.append("<style type=\"text/css\" src=\"/WEB-INF/classes/test.css\" />\r\n"); //
		sb_html.append("<style type=\"text/css\">\r\n"); //
		//居中显示
		sb_html.append("html{background:#D2D460;text-align:center;}\r\n");
		sb_html.append("body{width:778px;margin:0 auto;background:#fff;text-align:left;}\r\n");

		sb_html.append("<!--\r\n"); //
		sb_html.append(".Style_TRTD  { border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:0px;border-left-width:0px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_Compent { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_CompentLabel {background: #EEEEEE;word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append(".Style_CompentValue { word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:0px;border-right-width:0px;border-bottom-width:0px;border-left-width:1px;font-size:12px; }\r\n"); //
		sb_html.append("#list{border:1px solid;border-collapse: collapse;table-layout: fixed;}\r\n");
		sb_html.append("#list th{background: #EEEEEE;font-weight: normal}\r\n");
		sb_html.append("#list th,td{border:1px solid;font-size:12px;}\r\n");
		//sb_html.append("table {width:100%;}\r\n");
		sb_html.append("fieldset legend {\r\n");
		sb_html.append("    font-size:12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("-->\r\n"); //
		sb_html.append("</style>\r\n"); //
		sb_html.append("<script type=text/javascript>                   \r\n"); //  
		sb_html.append("<!--                                            \r\n"); //  
		sb_html.append("function setFolding(controler, itemName){       \r\n"); //  
		sb_html.append("    var items = document.getElementsByTagName('tr');\r\n");//
		sb_html.append("    var str = controler.innerText;              \r\n"); //  
		sb_html.append("    str = str.substring(1,str.length);		    \r\n"); //  
		sb_html.append("    for(var i=0;i<items.length;i++) {           \r\n"); //  
		sb_html.append("	    var item = items[i];                    \r\n"); // 
		sb_html.append("        if(item.name != itemName) {             \r\n"); // 
		sb_html.append("            continue;                           \r\n"); // 
		sb_html.append("        }                                       \r\n"); // 
		sb_html.append("	    if(item.style.display==\"none\") {      \r\n"); //  
		sb_html.append("		   item.style.display=\"\";		        \r\n"); //  
		sb_html.append("		   controler.innerText= \"-\" + str;    \r\n"); //  
		sb_html.append("		} else {							    \r\n"); //  
		sb_html.append("			item.style.display=\"none\";	    \r\n"); //  
		sb_html.append("			controler.innerText= \"+\" + str;	\r\n"); //  
		sb_html.append("		}									    \r\n"); //  
		sb_html.append("	}										    \r\n"); //  
		sb_html.append("}											    \r\n"); //  
		sb_html.append("-->											    \r\n"); //  
		sb_html.append("</script>									    \r\n"); //  
		sb_html.append("</head>										    \r\n"); //  
		return sb_html.toString();
	}

	/**
	 * 导出Word报表，根据模板子表中的cardisexport导出
	 */
	public void exportToReportWord() {
		FrameWorkMetaDataServiceIfc service = null;
		FileOutputStream os = null;
		try {
			service = UIUtil.getMetaDataService();
			byte[] bytes = service.getCardReportWord(this.getTempletVO(), this.getBillVO());
			if (bytes != null) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File file) {
						String filename = file.getName();
						return file.isDirectory() || filename.endsWith(".doc");
					}

					public String getDescription() {
						return "*.doc";
					}
				});
				File f = new File(new File("c:/exportData.doc").getCanonicalPath());
				chooser.setSelectedFile(f);
				int li_rewult = chooser.showSaveDialog(this);
				if (li_rewult == JFileChooser.APPROVE_OPTION) {
					File curFile = chooser.getSelectedFile(); //
					String fileName = curFile.getName();
					if (!(fileName.endsWith(".doc") || fileName.endsWith(".doc"))) {
						curFile = new File(curFile.getAbsolutePath() + ".doc");
					}
					if (curFile != null) {
						os = new FileOutputStream(curFile, false); //
						os.write(bytes); //
						os.close(); //
						String str_filename = curFile.getAbsolutePath(); //
						if (MessageBox.confirm(this, "导出文件[" + curFile.getAbsolutePath() + "]成功,你是否想立即打开它?")) {
							Desktop.open(curFile); //
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getExportHtml() {
		return getExportHtml(-1); //-1默认宽度
	}

	/**
	 * 取得将该卡片层导出Html的字符串!!!
	 * 可以指定页面宽度
	 * @return
	 */
	public String getExportHtml(int pageWidth) {
		String str_itemkey = ""; //
		StringBuilder sb_html = new StringBuilder();
		sb_html.append(getHtmlHead());
		try {
			JComponent[] allCompents = vflowPanel.getAllCompents(); //得到各层控件,一层就是一个表格

			sb_html.append("<body>										    \r\n"); //  
			sb_html.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=720>\r\n"); //
			TBUtil tbUtil = new TBUtil(); //

			//生成每个cardgroup
			String str_cardGroupId = "";//用来控制每个cardgroup内容的展开和收起
			for (int i = 0; i < allCompents.length; i++) {
				JComponent compent = allCompents[i]; //
				if (!compent.isVisible()) {//保持html和控件显示的内容一致,为什么以前给注释掉了？
					continue;
				}
				if (compent instanceof HFlowLayoutPanel) {
					HFlowLayoutPanel flowPanel = (HFlowLayoutPanel) compent; //
					JComponent[] rowCompents = flowPanel.getAllCompents(); //
					//增加判断，有时候一行只有一个控件，已经设置不显示 但是会显示一个黑条。
					if (rowCompents != null) {
						boolean ifallnovisble = true;
						for (int j = 0; j < rowCompents.length; j++) { //
							if (rowCompents[j].isVisible()) {
								ifallnovisble = false;
								break;
							}
						}
						if (ifallnovisble) {
							continue;
						}
					} else {
						continue;
					}
					sb_html.append("<tr ");
					sb_html.append(" name=\"" + str_cardGroupId + "\">\r\n");
					if (i != allCompents.length - 1) {
						sb_html.append("<td class=\"Style_TRTD\">\r\n");
					} else {
						sb_html.append("<td style=\"border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:1px;border-left-width:0px;font-size:12px;\">\r\n");
					}

					sb_html.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n");
					sb_html.append("<tr>\r\n");
					for (int j = 0; j < rowCompents.length; j++) { //
						AbstractWLTCompentPanel realCompent = (AbstractWLTCompentPanel) rowCompents[j]; //
						if (!realCompent.isVisible()) {
							continue;
						}
						str_itemkey = realCompent.getItemKey(); //
						JLabel label = realCompent.getLabel(); //
						if (label == null) {
							//这个组件取不到值，比如是按钮类型，应该如何处理? 目前先忽略
							continue;
						}
						String str_textColor = "000000"; //
						if (label != null && label.getForeground() != null) {
							str_textColor = tbUtil.convertColor(label.getForeground()); //
						}
						Object objValue = realCompent.getObject();
						int li_label_width = (int) label.getPreferredSize().getWidth(); //
						int li_comp_width = (int) (realCompent.getPreferredSize().getWidth() - li_label_width); //控件宽度拿整个宽度减去Label的宽度!!
						String str_label_text = label.getText(); //
						str_label_text = tbUtil.replaceAll(str_label_text, "<html>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "</html>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "<body>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "</body>", "");
						str_label_text = tbUtil.replaceAll(str_label_text, "\n", "<br>"); //将换行符替换成<br>
						int li_label_height = (int) label.getPreferredSize().getHeight(); //
						if ("% ".equals(str_label_text)) {
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + (li_label_width - 10) + "\" height=\"" + li_label_height + "\" align=\"left\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //控件的Label说明
							continue;

						}
						if (!str_textColor.equals("000000") && str_label_text != null && !str_label_text.trim().equals("")) {
							str_label_text = "<font color=\"#" + str_textColor + "\">" + str_label_text + "</font>"; //
						}

						String str_objectValueText = (objValue == null ? "" : ("" + objValue)); //
						if (realCompent instanceof CardCPanel_TextField) {
							if (((CardCPanel_TextField) realCompent).getType().equals(WLTConstants.COMP_PASSWORDFIELD)) {
								if (str_objectValueText != null && !"".equals(str_objectValueText.trim())) {
									char[] newchar = new char[str_objectValueText.length()];
									for (int ii = 0; ii < str_objectValueText.length(); ii++) {
										newchar[ii] = ('*');
									}
									str_objectValueText = new String(newchar);
								}
							}
						}

						if (realCompent instanceof CardCPanel_ChildTable) {
							//导出引用子表的html  WLTConstants.COMP_LINKCHILD
							BillListPanel list = ((CardCPanel_ChildTable) realCompent).getBillListPanel();
							if (list.getRowCount() == 0) {
								str_objectValueText = "";
							} else {
								str_objectValueText = "\r\n" + list.getHtmlTableText() + "\r\n";
							}
						}

						String str_itemtype = "";
						if (realCompent instanceof CardCPanel_Ref) {
							str_itemtype = ((CardCPanel_Ref) realCompent).getType();
						}
						if (str_itemtype.equals(WLTConstants.COMP_OFFICE)) {
							str_objectValueText = getComponentToHrefHtml(this.getBillVO(), str_objectValueText, str_itemkey, str_itemtype);
						}
						if (realCompent instanceof CardCPanel_FileDeal) {
							str_objectValueText = getComponentToHrefHtml(this.getBillVO(), str_objectValueText, str_itemkey, WLTConstants.COMP_FILECHOOSE);
						}
						if (realCompent instanceof CardCPanel_CheckBox) {
							if ("N".equals(str_objectValueText)) {
								str_objectValueText = "否";
							} else if ("Y".equals(str_objectValueText)) {
								str_objectValueText = "是";
							}
						}

						if (realCompent instanceof CardCPanel_TextArea) {
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //控件的Label说明
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\"  align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText.replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>") + "</td>\r\n"); //控件的内容框中的值
						} else if (realCompent instanceof CardCPanel_StylePadArea) { //富文本框html数据去后缀 【杨科/2012-10-30】
							int li_pos_1 = str_objectValueText.lastIndexOf("#@$");
							int li_pos_2 = str_objectValueText.lastIndexOf("$@#");
							if (str_objectValueText.endsWith("$@#") && li_pos_1 > 0 && li_pos_2 > 0) { //如果是以【$@#】结尾的! #@$12586$@#
								str_objectValueText = str_objectValueText.substring(0, li_pos_1);
							}
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" height=\"" + li_label_height + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //控件的Label说明
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\" " + "height=\"" + li_label_height + "\" align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText + "</td>\r\n"); //控件的内容框中的值
						} else {
							sb_html.append("<td class=\"Style_CompentLabel\" width=\"" + li_label_width + "\" height=\"" + li_label_height + "\" align=\"right\" onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_label_text + "</td>\r\n"); //控件的Label说明
							sb_html.append("<td class=\"Style_CompentValue\" width=\"" + li_comp_width + "\" " + "height=\"" + li_label_height + "\" align=\"left\"  onClick=\"bgColor='#ffffff'\" ondblClick=\"bgColor='#ffff00'\">" + str_objectValueText + "</td>\r\n"); //控件的内容框中的值
						}
					}

					sb_html.append("</tr>\r\n");
					sb_html.append("</table>\r\n");
					sb_html.append("</td>\r\n");
					sb_html.append("</tr>\r\n\r\n");

				} else if (compent instanceof CardGroupTitlePanel) {
					str_cardGroupId = "table" + i;
					sb_html.append("<tr>\r\n");
					sb_html.append("<td class=\"Style_TRTD\">\r\n");
					sb_html.append("<table width=\"100%\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"2\">\r\n");
					sb_html.append("<tr>\r\n");
					sb_html.append("<td class=\"Style_Compent\" width=\"100%\" align=\"left\" bgcolor=\"#EEDDFF\">");
					sb_html.append("<div onclick=\"setFolding(this, '" + str_cardGroupId + "');\">");
					if (((CardGroupTitlePanel) compent).getExpandState()) {
						sb_html.append("-");
					} else {
						sb_html.append("+");
					}
					sb_html.append(((CardGroupTitlePanel) compent).getTitlename());
					sb_html.append("</div></td>\r\n");
					sb_html.append("</tr>\r\n");
					sb_html.append("</table>\r\n");
					sb_html.append("</td>\r\n");
					sb_html.append("</tr>\r\n");
				} else {
					System.out.println("不知啥:" + compent); //
				}
			}
			sb_html.append("</table>\r\n");
			//			sb_html.append("</fieldset>\r\n"); //
			sb_html.append("</body>\r\n");
			sb_html.append("</html>\r\n");
			return sb_html.toString(); //
		} catch (Exception e) {
			logger.error("在为[" + str_itemkey + "]生成Html发生异常", e); //
			return "<html><body><font size=2 color=\"red\">在为[" + str_itemkey + "]生成Html发生异常,请到控制台查看详细信息</font></body></html>"; //
		}
	}

	/**
	 * 取得加载这个billListPanel的Frame,比如是各种风格模板!!
	 * 
	 * @return
	 */
	public AbstractWorkPanel getLoadedWorkPanel() {
		return loadedWorkPanel;
	}

	/**
	 * 打印页面..
	 */
	private void onPrintThis() {
		try {
			String str_fileName = System.getProperty("ClientCodeCache") + "\\BillCardPanel_" + System.currentTimeMillis() + ".jpg"; //
			getTBUtil().saveCompentAsJPGFile(mainPanel, str_fileName); //
			Desktop.open(new File(str_fileName)); //调用操作系统的画图打开这个图片,然后利用画图工具的功能进行打印..
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 设置加载这个billListPanel的Frame,比如是各种风格模板!!
	 * 
	 * @param loadedFrame
	 */
	public void setLoadedWorkPanel(AbstractWorkPanel _loadedWorkPanel) {
		this.loadedWorkPanel = _loadedWorkPanel;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public void setTempletVO(Pub_Templet_1VO templetVO) {
		this.templetVO = templetVO;
	}

	public boolean containsItemKey(String _itemKey) {
		return this.getTempletVO().containsItemKey(_itemKey);
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	private String getBorderName() {
		if (ClientEnvironment.getInstance().isEngligh()) {
			if ("DB".equals(this.getTempletVO().getBuildFromType())) { //有三种类型:XML2,DB,CLASS
				return templetVO.getTempletname_e() + "-";
			} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
				return templetVO.getTempletname_e() + "=";
			} else {
				return templetVO.getTempletname_e();
			}
		} else {
			if ("DB".equals(this.getTempletVO().getBuildFromType())) { //有三种类型:XML2,DB,CLASS
				return templetVO.getTempletname() + "-";
			} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
				return templetVO.getTempletname() + "=";
			} else {
				return templetVO.getTempletname();
			}
		}
	}

	public BillFormatPanel getLoaderBillFormatPanel() {
		return loaderBillFormatPanel;
	}

	public void setLoaderBillFormatPanel(BillFormatPanel loaderBillFormatPanel) {
		this.loaderBillFormatPanel = loaderBillFormatPanel;
	}

	public JComponent[] getHflowPanels() {
		return hflowPanels;
	}

	public boolean isCanRefreshParent() {
		return canRefreshParent;
	}

	public void setCanRefreshParent(boolean canRefreshParent) {
		this.canRefreshParent = canRefreshParent;
	}
	/**
	 * zzl[2018-12-4]
	 * 返回引用子表的BillListPanel
	 * @return
	 */
	public BillListPanel getdealBillListPanel(){
		BillListPanel list=null;
		for (int i = 0; i < v_compents.size(); i++) {
			AbstractWLTCompentPanel object = (AbstractWLTCompentPanel) v_compents.get(i);
			if (object instanceof CardCPanel_ChildTable) {
				CardCPanel_ChildTable compent = (CardCPanel_ChildTable) object;
				list=compent.getBillListPanel();
			}
	    }
		return list;
	}

}
