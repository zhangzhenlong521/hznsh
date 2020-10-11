/**************************************************************************
 * $RCSfile: QueryCPanel_UIRefPanel.java,v $  $Revision: 1.25 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryEditEvent;
import cn.com.infostrategy.ui.mdata.BillQueryEditListener;
import cn.com.infostrategy.ui.mdata.MetaDataUIUtil;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 查询框中的日期控件!
 * @author xch
 *
 */
public class QueryCPanel_UIRefPanel extends AbstractWLTCompentPanel {
	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String key = null;
	private String name = null;
	private String itemtype = null; //
	private String refdesc = null; //参照说明

	private Vector v_Listeners = new Vector(); //
	private int li_label_width = 120;
	private int li_cardheight = 20;
	private RefItemVO currRefItemVO = null;
	private JLabel label = null;
	private JTextField textField = null;
	protected JButton btn_ref = null; //参照按钮

	protected int li_width_all = 150; //只有卡片中有总宽度中的概念,列表中则是BorderLayout
	private AbstractRefDialog refDialog = null; //
	private TBUtil tBUtil = null; //转换工具!!
	public QueryCPanel_UIRefPanel(Pub_Templet_1_ItemVO _templetVO, BillPanel billPanel, RefItemVO _initRefItemVO) { //
		super();
		this.currRefItemVO = _initRefItemVO;
		this.templetItemVO = _templetVO;
		setBillPanel(billPanel); //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		if (templetItemVO.getQueryItemType() != null && !templetItemVO.getQueryItemType().trim().equals("")) { //如果定义了查询框的定义
			this.itemtype = templetItemVO.getQueryItemType(); //
			this.refdesc = templetItemVO.getQueryItemDefine(); //
		} else {
			this.itemtype = templetItemVO.getItemtype(); //
			this.refdesc = templetItemVO.getRefdesc(); //
		}
		if (templetItemVO.getQueryUCDfVO() == null) { //如果没有定义查询的,则直接使用控件本身定义的!!!
			templetItemVO.setQueryUCDfVO(templetItemVO.getUCDfVO()); //
		}
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //文字说明的宽度
		}
		if (templetItemVO.getQuerycompentwidth() != null) {
			this.li_width_all = templetItemVO.getQuerycompentwidth().intValue(); //查询控件宽度
		}
		if (templetItemVO.getQuerycompentheight() != null) {
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize();
	}

	/**
	 * 初始化页面布局,卡片与列静听的参照面板其实主要就是这儿不一样!!!!
	 * 卡片中的布局是有两个东东,一个是label标签,一个是文本框,而列表中则没有Label标签!
	 */
	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.systembgcolor); //

		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			if (templetItemVO.getIsmustinput()) {//如果卡片定义为必输项，则需要判断查询面板中是否为必输项【李春娟/2012-03-19】
				if (!"Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果查询面板中不是必输项，则需要将必输符号"*"号去掉
					label.setText(label.getText().replace("*", ""));
				}
			} else if ("Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果卡片定义为非必输项，但查询面板中是必输项，则需要添加上必输符号"*"号
				label.setText("*" + label.getText());
				((WLTLabel) label).addStrItemColor("*", Color.RED); //
			}
		} else {
			label = new JLabel(name); //	
		}

		label.setBackground(LookAndFeel.systembgcolor);
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度和高度

		textField = new JTextField();
		addComponentUndAndRedoFunc(textField); // add Redo and Undo function
		textField.setForeground(LookAndFeel.systemLabelFontcolor); //
		textField.setEditable(false); //
		textField.setBackground(LookAndFeel.inputbgcolor_enable); //因为是不可编辑的,所以是灰色的,但兴业客户提出要白的,所以搞了个接近白的(xch)
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		textField.setPreferredSize(new Dimension(li_width_all - 19, li_cardheight)); //设置宽度

		btn_ref = new JButton(UIUtil.getImage(getIconName(this.itemtype))); //不同的控件使用不同的标标!!!  
		btn_ref.setToolTipText("点击右键清空数据"); //
		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(19, li_cardheight)); // 按扭的宽度与高度

		if (getTBUtil().getSysOptionBooleanValue("日期控件是否标上默认值", false)) { //日历控件追加默认值 【杨科/2012-08-27】
			CommUCDefineVO vo = templetItemVO.getQueryUCDfVO();
			if (!(vo != null && vo.getConfValue("是否标上默认值") != null && vo.getConfValue("是否标上默认值").equals("false"))) {
				//如果是日期控件,则强行标上默认值,即当前季度!杨科补充(xch/2012-08-27)
				if (itemtype.equals(WLTConstants.COMP_DATE) || itemtype.equals(WLTConstants.COMP_DATETIME)) {

					String str_curdate = tBUtil.getCurrDate();
					String str_begindate = tBUtil.getBeginDateByMonth(str_curdate);
					String str_enddate = tBUtil.getEndDateByMonth(str_curdate);
					String str_currmonth = str_curdate.substring(0, 4) + "年" + str_curdate.substring(5, 7) + "月;";

					//可以是本月,或本季度,如何取值可以参考RefDialog_QueryDateTime,CommonDateQueryPanel这两个类
					if (templetItemVO.getQueryUCDfVO() != null) { //查询定义
						String str_frontmonth = templetItemVO.getQueryUCDfVO().getConfValue("前推几个月"); //
						if (str_frontmonth != null) { //时间范围自动变成前5个月至今天
							//System.out.println("前推月份:" + str_frontmonth); //
							try {
								String backmonth = tBUtil.getBackMonth(str_curdate, Integer.parseInt(str_frontmonth));
								if (!(backmonth.substring(0, 7)).equals(str_curdate.substring(0, 7))) {
									str_begindate = tBUtil.getBeginDateByMonth(backmonth);
									str_currmonth = backmonth.substring(0, 4) + "年" + backmonth.substring(5, 7) + "月" + "-" + str_curdate.substring(0, 4) + "年" + str_curdate.substring(5, 7) + "月";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					//SQL条件与显示值默认是本月,如果定义了前推,则自动是前5个月,显示也可以改成是【2011年11月-2012年5月】
					HashVO hvo = new HashVO(); //
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_begindate + "' and {itemkey}<='" + str_enddate + " 24:00:00'))"); //怎么算这个的参考CommonDateQueryPanel,将一些关键方法移到TBUtil中!

					//RefItemVO refVO = new RefItemVO("2012年08月;", null, "2012年08月;", hvo);
					RefItemVO refVO = new RefItemVO(str_currmonth, null, str_currmonth, hvo);
					setObject(refVO); //设置默认值!
				}
			}
		}
		//袁江晓  20130515 添加  主要解决按照给定的默认值类型来设置默认值，可分为年、月、天、季
		if (itemtype.equals(WLTConstants.COMP_DATE) || itemtype.equals(WLTConstants.COMP_DATETIME)) {
			CommUCDefineVO vouc = templetItemVO.getQueryUCDfVO();
			if (vouc != null && vouc.getConfValue("查询时日期默认值类型") != null && !vouc.getConfValue("查询时日期默认值类型").equals("")){
				String dateType=vouc.getConfValue("查询时日期默认值类型");
				String str_curdate = tBUtil.getCurrDate();
				String str_currmonth = str_curdate.substring(0, 4) + "年" + str_curdate.substring(5, 7) + "月;";
				String str_curryear = str_curdate.substring(0, 4) + "年;" ;
				String str_currseason = tBUtil.getCurrDateSeason() + ";" ;
				HashVO hvo = new HashVO(); //
				RefItemVO refVO = null;
				if(dateType.equals("年")){
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_curdate.substring(0, 4) + "-01-01" + "' and {itemkey}<='" + str_curdate.substring(0, 4) + "-12-31" + " 24:00:00'))"); //怎么算这个的参考CommonDateQueryPanel,将一些关键方法移到TBUtil中!
					refVO=new RefItemVO(str_curryear, null, str_curryear, hvo);
				}else if(dateType.equals("月")){
					String str_begindate = tBUtil.getBeginDateByMonth(str_curdate);
					String str_enddate = tBUtil.getEndDateByMonth(str_curdate);
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_begindate + "' and {itemkey}<='" + str_enddate + " 24:00:00'))"); //怎么算这个的参考CommonDateQueryPanel,将一些关键方法移到TBUtil中!
					refVO=new RefItemVO(str_currmonth, null, str_currmonth, hvo);
				}else if(dateType.equals("天")){
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + str_curdate + " 00:00:00' and {itemkey}<='" + str_curdate + " 24:00:00'))"); //怎么算这个的参考CommonDateQueryPanel,将一些关键方法移到TBUtil中!
					refVO=new RefItemVO(str_curdate+";", null, str_curdate+";", hvo);
				}else if(dateType.equals("季")){
					String str_year = str_currseason.substring(0, 4); //
					String str_season = str_currseason.substring(5, 6); //
					String firstday="";
					String lastday="";
					if (str_season.equals("1")) {
						firstday= str_year + "-01-01"; //
						lastday= str_year + "-03-31"; //
					} else if (str_season.equals("2")) {
						firstday= str_year + "-04-01"; //
						lastday= str_year + "-06-30"; //
					} else if (str_season.equals("3")) {
						firstday= str_year + "-07-01"; //
						lastday= str_year + "-09-30"; //
					} else if (str_season.equals("4")) {
						firstday= str_year + "-10-01"; //
						lastday= str_year + "-12-31"; //
					} else {
						firstday= str_year + "-01-01"; //
						lastday= str_year + "-12-31"; //
					}
					hvo.setAttributeValue("querycondition", "(({itemkey}>='" + firstday + "' and {itemkey}<='" + lastday + " 24:00:00'))"); //怎么算这个的参考CommonDateQueryPanel,将一些关键方法移到TBUtil中!
					refVO=new RefItemVO(str_currseason, null, str_currseason, hvo);
				}
				setObject(refVO); //设置默认值!
			}
		}
		
		

		btn_ref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getID() != ActionEvent.SHIFT_MASK) {
					textField.putClientProperty("JTextField.DrawFocusBorder", "Y");
					textField.requestFocus();
					onButtonClicked();
					textField.putClientProperty("JTextField.DrawFocusBorder", null);
				}
			}
		});

		btn_ref.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					if (e.isShiftDown()) { //如果按了Shift键!!
						showRefMsg(); //显示参照信息
					} else { //如果是有用的
						if (((JButton) e.getSource()).isEnabled()) {
							reset(); //
							textField.requestFocus();
						}
					}
				}
			}

		}); //

		textField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTextField clickedTextField = ((JTextField) e.getSource()); //
					String str_text = clickedTextField.getText();
					if (str_text != null && !str_text.equals("")) { //如果内容为空，就不弹出。郝明
						str_text = getTBUtil().replaceAll(str_text, ";", ";\n"); //以前是一行,显得很乱!兴业客户石瑜总是抱怨!所以要坚起来!!【xch/2012-04-25】
						MessageBox.showTextArea(clickedTextField, str_text); //
					}
				}
			}
		});
		btn_ref.putClientProperty("JButton.RefTextField", textField);
		textField.putClientProperty("JTextField.OverWidth", (int) btn_ref.getPreferredSize().getWidth());
		this.add(label); //
		this.add(textField); //
		this.add(btn_ref); //

		li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth() + btn_ref.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
	}

	/**
	 * 点击按钮!!!
	 */
	protected void onButtonClicked() {
		refDialog = null; //
		try {
			CommUCDefineVO queryCommUCDfVO = this.templetItemVO.getQueryUCDfVO(); //查询的数据对象!!
			if (this.itemtype.equals(WLTConstants.COMP_REFPANEL) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_TREE) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
					this.itemtype.equals(WLTConstants.COMP_REFPANEL_CUST) || this.itemtype.equals(WLTConstants.COMP_REFPANEL_REGEDIT)) { //
				if (this.refdesc == null || this.refdesc.trim().equalsIgnoreCase("null") || this.refdesc.trim().equals("")) {
					MessageBox.show(this, "没有定义参照说明,请检查!"); //
					return;
				}
				if (queryCommUCDfVO == null) { //这类控件必须有定义!!!
					MessageBox.show(this, "解析定义公式【\r\n" + this.refdesc + "\r\n】时失败,CommUCDefineVO对象为null,请检查定义语法!!\r\n友情提醒:请注意是否多了或少了逗号,双引号之类的(尤其是最后一个参数更容易多个逗号)..."); //
					return;
				}
			}
			if (queryCommUCDfVO == null) {
				queryCommUCDfVO = new CommUCDefineVO(this.itemtype); //
			}

			//将commUCDfVO克隆一个,然后替换一些控件定义的SQL语句中的条件!!因为后来改成了在BS端解析公式,所以无法取得getItemValue()这种公式的值,则只能在服务器端进行宏代码转换!!!
			queryCommUCDfVO = new MetaDataUIUtil().cloneCommUCDefineVO(queryCommUCDfVO, this.billPanel); //之所要克隆,是因为一旦替换后,第二次就不会再替换了,从而达不到想要实现的逻辑了!!//以后在UI端可能有公式来动态修改定义中的值,比如SQL,如果不克隆,直接修改原来的,则会造成第二次执行时逻辑不对!!!
			if (queryCommUCDfVO == null) {
				return; //
			}

			if (itemtype.equals(WLTConstants.COMP_REFPANEL)) { //表形参照
				refDialog = new RefDialog_QueryTable(this, this.name, this.currRefItemVO, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_TREE)) { //树型参照
				refDialog = new RefDialog_QueryTree(this, this.name, null, this.billPanel, queryCommUCDfVO);
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //多选参照
				refDialog = new RefDialog_QueryMulti(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_CUST)) { //自定义参照,比较复杂点			
				refDialog = getCustRefDialog(queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { //列表模板参照
				refDialog = new RefDialog_QueryTableModel(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { //树型模板参照
				refDialog = new RefDialog_QueryTreeModel(this, this.name, this.currRefItemVO, this.billPanel, queryCommUCDfVO); //传递currRefItemVO 树型模板参照的反向勾选用 【杨科/2012-08-31】
			} else if (itemtype.equals(WLTConstants.COMP_REFPANEL_REGFORMAT)) { //注册样板参照
				refDialog = new RefDialog_QueryRegFormat(this, this.name, null, this.billPanel, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_DATE)) { //日历
				refDialog = new RefDialog_QueryDateTime(this, this.name, null, null, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_DATETIME)) { //时间
				refDialog = new RefDialog_QueryDateTime(this, this.name, null, null, queryCommUCDfVO); //
			} else if (itemtype.equals(WLTConstants.COMP_NUMBERFIELD)) { //数字框
				refDialog = new RefDialog_QueryNumber(this, this.name, null, null); //
			}

			refDialog.initialize(); //初始化窗口
			refDialog.setVisible(true); //打开窗口
			textField.requestFocus();
			boolean bo_ifdataChanged = false;
			if (refDialog.getCloseType() == BillDialog.CONFIRM) { //如果是确定返回
				RefItemVO returnVO = refDialog.getReturnRefItemVO();
				textField.setText(returnVO.getName()); //
				bo_ifdataChanged = ifChanged(returnVO, this.currRefItemVO); //比如返回值与当前值是否一样!
				setObject(returnVO); //设置当前值,并修改控件文本框中的值!!
				if (bo_ifdataChanged) { //如果是发生变化,则修改当前值
					onBillQueryValueChanged(new BillQueryEditEvent(this.key, this.getObject(), this)); //如果是卡片,则触发事件!!
				}
			} else {
				bo_ifdataChanged = false; //
			}

			if (currRefItemVO != null) {
				currRefItemVO.setValueChanged(bo_ifdataChanged); //设置数据发生变化
			}

			refDialog = null; //为空
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			refDialog = null;
			Runtime.getRuntime().gc(); //
		}
	}

	//根据自定义参照VO创建参照!!!
	private AbstractRefDialog getCustRefDialog(CommUCDefineVO _dfvo) throws Exception {
		String str_clsName = _dfvo.getConfValue("自定义类名"); //
		String[] str_paras = _dfvo.getAllConfKeys("参数", true); //取所有参数!!
		if (str_paras.length == 0) { //如果没有参数!!
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, RefItemVO.class, BillPanel.class }; //
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(new Object[] { this, this.name, this.currRefItemVO, this.billPanel }); //
		} else { //如果有参数!!
			Class cp[] = new Class[4 + str_paras.length]; //前4个参数是固定的,后面的递增!!
			cp[0] = java.awt.Container.class;
			cp[1] = String.class;
			cp[2] = RefItemVO.class;
			cp[3] = BillPanel.class;
			for (int i = 4; i < cp.length; i++) {
				cp[i] = java.lang.String.class;
			}
			Object[] ob = new Object[4 + str_paras.length];
			ob[0] = this;
			ob[1] = this.name;
			ob[2] = this.currRefItemVO;
			ob[3] = this.billPanel;
			for (int j = 0; j < str_paras.length; j++) {
				ob[4 + j] = _dfvo.getConfValue(str_paras[j]); //把自定义参数值传过去
			}
			Class dialog_class = Class.forName(str_clsName);
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(ob); //
		}
	}

	/**
	 * 根据参照类型,取得参照图片
	 * @param _type
	 * @return
	 */
	private String getIconName(String _type) {
		if (_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
				_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照
				_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照
				_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
				_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) //注册参照
		) {
			return getTBUtil().getSysOptionStringValue("默认参照图标名称", "refsearch.gif"); //zt_012.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		} else if (_type.equals(WLTConstants.COMP_DATE)) { //日期
			return getTBUtil().getSysOptionStringValue("日期参照图标名称", "date.gif"); //zt_030.gif/zt_075.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		} else if (_type.equals(WLTConstants.COMP_DATETIME)) { //时间
			return "time.gif";
		} else if (_type.equals(WLTConstants.COMP_BIGAREA)) { //大文本框
			return "bigtextarea.gif";
		} else if (_type.equals(WLTConstants.COMP_FILECHOOSE)) { //文件选择框
			return "filepath.gif";
		} else if (_type.equals(WLTConstants.COMP_COLOR)) { //颜色选择框
			return "colorchoose.gif";
		} else if (_type.equals(WLTConstants.COMP_CALCULATE)) { //计算器
			return "office_004.GIF";
		} else if (_type.equals(WLTConstants.COMP_PICTURE)) { //图片选择框
			return "pic2.gif";
		} else if (_type.equals(WLTConstants.COMP_NUMBERFIELD)) { //数字框
			return "office_058.gif";
		} else {
			return getTBUtil().getSysOptionStringValue("默认参照图标名称", "refsearch.gif"); //zt_012.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		}
	}

	public int getAllWidth() {
		return li_width_all;
	}

	@Override
	public void focus() {
		textField.requestFocus(); //
		textField.requestFocusInWindow(); //
	}

	@Override
	public String getItemKey() {
		return key;
	}

	@Override
	public String getItemName() {
		return name;
	}

	@Override
	public JLabel getLabel() {
		return this.label;
	}

	private boolean ifChanged(RefItemVO returnVO, RefItemVO _currVO) {
		if (returnVO == null) {
			if (_currVO == null) {
				return false; //设置没有变化
			} else {
				return true;
			}
		} else {
			if (_currVO == null) {
				return true;
			} else {
				if (returnVO.getId().equals(_currVO.getId())) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	public void addBillQueryEditListener(BillQueryEditListener _listener) {
		v_Listeners.add(_listener);
	}

	public void onBillQueryValueChanged(BillQueryEditEvent _evt) {
		for (int i = 0; i < v_Listeners.size(); i++) {
			BillQueryEditListener listener = (BillQueryEditListener) v_Listeners.get(i);
			listener.onBillQueryValueChanged(_evt); //
		}
	}

	@Override
	public Object getObject() {
		return currRefItemVO; //
	}

	@Override
	public String getValue() {
		if (currRefItemVO == null) {
			return null;
		}
		return currRefItemVO.getId();
	}

	@Override
	public void setValue(String _value) {
		if (currRefItemVO == null) {
			currRefItemVO = new RefItemVO(_value, _value, _value);
			this.textField.setText(_value); //
		} else {
			currRefItemVO.setId(_value);
		}
	}

	@Override
	public void reset() {
		currRefItemVO = null; //
		this.textField.setText(""); //清空文本框
	}

	public void showRefMsg() {
		StringBuffer sb_info = new StringBuffer(); //
		if (currRefItemVO == null) {
			sb_info.append("当前值为null"); //
		} else {
			sb_info.append("RefID=[" + currRefItemVO.getId() + "]\r\n");
			sb_info.append("RefCode=[" + currRefItemVO.getCode() + "]\r\n");
			sb_info.append("RefName=[" + currRefItemVO.getName() + "]\r\n");

			sb_info.append("\r\n----------- HashVO数据 -------------\r\n");

			if (currRefItemVO.getHashVO() == null) {
				sb_info.append("HaVO为空\r\n");
			} else {
				String[] keys = currRefItemVO.getHashVO().getKeys(); //
				for (int i = 0; i < keys.length; i++) {
					sb_info.append(keys[i] + "=[" + currRefItemVO.getHashVO().getStringValue(keys[i]) + "]\r\n");
				}
			}
		}
		//////.....

		MessageBox.showTextArea(this, "参照实际绑定的数据", sb_info.toString()); //
	}

	@Override
	public void setItemEditable(boolean _bo) {
		this.btn_ref.setEnabled(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return btn_ref.isEnabled();
	}

	@Override
	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	@Override
	public void setObject(Object _obj) {
		try {
			currRefItemVO = (RefItemVO) _obj; //
			if (currRefItemVO == null) {
				textField.setText("");
			} else {
				textField.setText(currRefItemVO.getName()); //
			}
			textField.select(0, 0); //
		} catch (java.lang.ClassCastException ex) {
			System.out.println("控件[" + key + "][" + name + "]的数据类型不对,需要RefItemVO,实际是[" + _obj.getClass().getName() + "]!"); //
			throw ex;
		}

	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	public Pub_Templet_1_ItemVO getTempletItemVO() {
		return templetItemVO;
	}

	/***后加的两个方法 by hm 2013-04-09***/
	public JButton getBtn_ref() {
		return btn_ref;
	}

	public JTextField getTextField() {
		return textField;
	}

}
