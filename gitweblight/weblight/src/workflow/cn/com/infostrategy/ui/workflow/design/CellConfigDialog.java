package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * 修改某一个环节,连线,或分组框的配置信息框
 * 包括可以修改字体,字体大小,前景颜色,后景颜色!
 * 字的内容!
 * @author xch
 *
 */
public class CellConfigDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -6389651731318330892L;
	private String fontType = "System";
	private Font font = null;
	private int fontSize = 12; //
	private Color foreGround = null; //
	private Color backGround = null; //
	private String contentStr = null; //内容
	private String imgstr = null; //图片
	private String seq = null; // 排序
	private String postsStr = null; //
	private String celltype = null;//"DEPT","STATION","ACTIVITY","TRANSITION"
	private int viewtype = 1; //环节类型

	private JComboBox combox_fonttype, combox_fontsize; //字体类型,字体大小
	private CardCPanel_Ref ref_foreground, ref_background, ref_img; //前景颜色,后景颜色
	private JTextArea textarea_content; //
	private BillListPanel list_post;
	private JTextField textarea_seq;//排序
	private JComboBox combox_type, combox_single;//环节类型下拉框
	private Integer lineType;
	private boolean isSingle;
	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1; //关闭类型
	private boolean isCanSetCellColor = true;//农行提出，编辑部门阶段及环节时不要出现字体颜色和背景颜色，防止流程图被编辑的花里胡哨的，故设置此参数
	private DefaultTableModel defaultModel = null;//设置表格默认样式   [袁江晓20120907添加]
	JTable table = null;//定义岗位表格   [袁江晓20120907添加]

	/**
	 * 构造方法
	 * @param _parent
	 * @param _title
	 * @param _y 
	 * @param _x 
	 * @param _cellBackcolor 
	 * @param _cellForecolor 
	 * @param _cellFont 
	 * @param _contentStr 
	 */
	public CellConfigDialog(java.awt.Container _parent, String _title, int _x, int _y, Font _cellFont, Color _cellForecolor, Color _cellBackcolor, String _contentStr, String _seq, int _viewtype, String imgstr, String _postsStr, String _celltype) {
		this(_parent, _title, _x, _y, _cellFont, _cellForecolor, _cellBackcolor, _contentStr, _seq, _viewtype, imgstr, _postsStr, _celltype, null, true);
	}

	public CellConfigDialog(java.awt.Container _parent, String _title, int _x, int _y, Font _cellFont, Color _cellForecolor, Color _cellBackcolor, String _contentStr, String _seq, int _viewtype, String imgstr, String _postsStr, String _celltype, Integer _lineType, boolean _isSingle) {
		super(_parent, _title, 389, 280);
		this.setLocation(_x, _y);
		this.setResizable(false); //
		this.font = _cellFont;
		this.foreGround = _cellForecolor;
		this.backGround = _cellBackcolor; //
		this.contentStr = _contentStr; //
		this.seq = _seq;
		this.viewtype = _viewtype;
		this.imgstr = imgstr;
		this.postsStr = _postsStr;
		this.celltype = _celltype;
		this.lineType = _lineType;
		this.isSingle = _isSingle;
		this.setResizable(true);
		initialize(); //初始化页面
	}

	/**
	 * 初始化页面!!
	 */
	private void initialize() {
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //
		JLabel label_fonttype = new JLabel("字体类型 ", JLabel.RIGHT); //

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();

		combox_fonttype = new JComboBox(fontNames); //
		JLabel label_fontsize = new JLabel("字体大小 ", JLabel.RIGHT); //
		combox_fontsize = new JComboBox(new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "24", "36" }); //
		if (font == null) {
			combox_fonttype.setSelectedItem("宋体"); //设置字体
			combox_fontsize.setSelectedItem("12"); //设置字体大小
		} else {
			if (existFont(fontNames, font.getName())) {
				combox_fonttype.setSelectedItem(font.getName()); //
			} else {
				combox_fonttype.setSelectedItem("宋体"); //设置字体
			}
			combox_fontsize.setSelectedItem("" + font.getSize()); //设置字体大小
		}
		ref_foreground = new CardCPanel_Ref("字体颜色 ", "字体颜色 ", null, WLTConstants.COMP_COLOR, 70, 80, null, null); //
		if (foreGround != null) {
			ref_foreground.setObject(new RefItemVO(convertColorToStr(foreGround), null, convertColorToStr(foreGround)));//设置字体颜色
		} else {
			ref_foreground.setObject(new RefItemVO("0,0,0", null, "0,0,0")); //默认颜色是黑色
		}

		ref_background = new CardCPanel_Ref("背景颜色", "背景颜色", null, WLTConstants.COMP_COLOR, 50, 80, null, null); //
		if (backGround != null) {
			ref_background.setObject(new RefItemVO(convertColorToStr(backGround), null, convertColorToStr(backGround)));//设置背景颜色
		} else {
			ref_background.setObject(new RefItemVO("200,214,234", null, "200,214,234")); //默认是Visio的标准色
		}

		ref_img = new CardCPanel_Ref("图片", "图片", null, WLTConstants.COMP_PICTURE, 50, 80, null, null); //
		if (imgstr != null && !"".equals(imgstr)) {
			ref_img.setObject(new RefItemVO(imgstr, null, imgstr));//设置图片
		}

		JLabel label_content = new JLabel("名称 ", JLabel.RIGHT); //
		textarea_content = new JTextArea(); //
		textarea_content.setLineWrap(false); //
		textarea_content.setText(this.contentStr); //设置名称
		textarea_content.requestFocus(); //
		JScrollPane scrollPanel = new JScrollPane(textarea_content); ///

		label_fonttype.setBounds(5, 5, 70, 20); //字体类型Label
		combox_fonttype.setBounds(75, 5, 135, 20); //字体类型下拉框

		label_fontsize.setBounds(210, 5, 60, 20); //字体大小Label
		combox_fontsize.setBounds(270, 5, 50, 20); //字体大小下拉框

		ref_foreground.setBounds(5, 30, 170, 20); //字体颜色
		ref_background.setBounds(175, 30, 160, 20); //背景颜色

		label_content.setBounds(5, 55, 70, 20); //名称Label
		scrollPanel.setBounds(75, 55, 250, 70);//名称

		panel.add(label_fonttype); //字体类型
		panel.add(combox_fonttype); //
		panel.add(label_fontsize); //字体大小
		panel.add(combox_fontsize); //

		panel.add(ref_foreground); //
		if ("ACTIVITY".equalsIgnoreCase(this.celltype) && this.viewtype == 8) {//如果是环节，并且是图片类型则隐藏背景颜色，否则隐藏图片
			ref_background.setItemEditable(false);
		} else {
			ref_img.setVisible(false);
		}
		panel.add(ref_background);//背景颜色
		panel.add(label_content); //
		panel.add(scrollPanel); //

		if ("ACTIVITY".equalsIgnoreCase(this.celltype)) {//如果是环节，则显示排序和环节类型
			JLabel label_seq = new JLabel("排序 ", JLabel.RIGHT); //
			label_seq.setBounds(5, 130, 70, 20); //

			textarea_seq = new JFormattedTextField(); //格式化的数字框
			textarea_seq.setHorizontalAlignment(SwingConstants.RIGHT); //
			textarea_seq.setDocument(new NumberFormatdocument()); //定义数字框只能输入数字,输入字母不让键入!!!!
			textarea_seq.setText(this.seq);
			//textarea_seq.requestFocus(); //panel绘完后，获得焦点才有意义,这里调用requestFocus，不起作用【岳耀彪/2012-03-12】
			textarea_seq.setBounds(75, 130, 100, 20);

			JLabel label_type = new JLabel("环节类型 ", JLabel.RIGHT); //
			label_type.setBounds(5, 150, 70, 20); //
			ref_img.setBounds(175, 150, 160, 20); //图片
			combox_type = new JComboBox(new Object[] { UIUtil.getImage("workflow/cellview_1.gif"), UIUtil.getImage("workflow/cellview_2.gif"), UIUtil.getImage("workflow/cellview_3.gif"), UIUtil.getImage("workflow/cellview_4.gif"), UIUtil.getImage("workflow/cellview_5.gif"), UIUtil.getImage("workflow/cellview_6.gif"), UIUtil.getImage("workflow/cellview_7.gif"), UIUtil.getImage("pic2.gif") }); //

			combox_type.setSelectedIndex(viewtype - 1); //
			combox_type.setBounds(75, 150, 100, 20); //
			combox_type.addItemListener(new ItemListener() { //监听事件
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onTypeChanged();
							}
						}
					});
			panel.add(label_seq); //
			panel.add(textarea_seq); //
			panel.add(label_type); //
			panel.add(combox_type); //
			panel.add(ref_img); //
			//袁江晓20120918添加  修改之前的行添加为列添加，按照可视的宽度和次序来调整岗位
			//袁江晓20121114修改   修改之前的行选择为列选择，并修改了一些bug			
			//-----------------------------------------------------------------begin-----------------------------------------------------------------------------------
		} else if ("DEPT".equalsIgnoreCase(this.celltype)) {//如果是机构，则显示岗位名称
			this.setSize(600, 400);
			ref_background.setVisible(false);//目前部门背景颜色是不允许修改的，故没必要显示出背景颜色框
			JLabel label_posts = new JLabel("岗位名称 ", JLabel.RIGHT); ////
			label_posts.setBounds(5, 130, 70, 20);
			int len = 0;
			if (postsStr != null && !postsStr.trim().equals("")) {
				String[] posts = postsStr.trim().split(";");
				len = posts.length;
			}
			if (len == 0) {
				len += 1;
			}
			if (null == postsStr || "".endsWith(postsStr)) {//设置默认显示列
				len = 3;
			}
			Object[][] rowData = new Object[1][len];//设置数据显示
			Object[] columnNames = new Object[len];//设置表头显示
			int[] colLength = new int[len];
			//先初始化数据，如果有则显示，没有则只显示一列
			if (postsStr != null && !postsStr.trim().equals("")) {
				String[] temp = postsStr.trim().split(";");//首先按照分号截取
				String[] posts = new String[temp.length];//取得名称的数组
				for (int i = 0; i < temp.length; i++) {
					String[] temps = temp[i].split("#");
					posts[i] = temps[0];
					if (temps.length == 1) {
						colLength[i] = 150;//如果有不符合要求的数据，比如"风险部#123;合规部;"，默认设置宽度为150，否则会报异常【李春娟/2014-10-30】
					} else {
						colLength[i] = Integer.parseInt(temps[1]);
					}
				}
				for (int i = 0; i < posts.length; i++) {
					if (posts[i] == null || posts[i].trim().equals("")) {
						continue;
					}
					columnNames[i] = "岗位名称" + (i + 1);
					rowData[0][i] = posts[i];
				}
			} else { //默认的显示三列
				rowData[0][0] = "";
				rowData[0][1] = "";
				rowData[0][2] = "";
				columnNames[0] = "岗位名称1";
				columnNames[1] = "岗位名称2";
				columnNames[2] = "岗位名称3";
			}
			defaultModel = new DefaultTableModel(rowData, columnNames);
			table = new JTable(defaultModel);
			table.setPreferredScrollableViewportSize(new Dimension(600, 200));
			table.setRowHeight(30);//设置行高度
			table.setEditingColumn(2);
			table.setShowGrid(true);//是否显示网格线
			table.setBounds(75, 150, 600, 100);
			table.setRowMargin(5);
			table.setDragEnabled(true);
			table.setColumnSelectionAllowed(true);//设置为列选择模式
			table.setRowSelectionAllowed(false);
			JTableHeader tableHeader = table.getTableHeader();
			tableHeader.setReorderingAllowed(true);
			JScrollPane sss = new JScrollPane(table);//如果需要表头则必须加上这句话
			sss.setBounds(75, 150, 500, 51);//设置表格的高度，默认宽度为500
			WLTButton bt_add = new WLTButton("添加");
			bt_add.setBounds(75, 130, 50, 20);
			//下面这部分为设置从上一步带过来的宽度
			//默认宽度为500，所以要进行相应处理，设置宽度为相应比例
			Float sum = new Float(0);
			for (int l = 0; l < len; l++) { //先求和，再求比例
				sum += colLength[l];
			}
			if (sum != 0) {
				for (int m = 0; m < len; m++) {//根据传过来的值设置每个默认表格的宽度
					TableColumn firstColumn1 = table.getColumnModel().getColumn(m);//获得第m列
					firstColumn1.setPreferredWidth((int) (colLength[m] / sum * 500));//预置宽度,因为默认的宽度为500
				}
			}
			bt_add.addActionListener(new ActionListener() { //添加按钮的响应方法
						public void actionPerformed(ActionEvent e) {
							int columncount = defaultModel.getColumnCount();
							int nowindex = table.getSelectedColumn();//获取当前选择的列
							if (nowindex == -1) {//如果无选择，则默认加在最后一列且不移动
								nowindex = columncount;
								defaultModel.addColumn("岗位名称" + (columncount + 1));
							} else {//如果有选择则先添加再移动
								if (null != table.getCellEditor()) {
									table.getCellEditor().stopCellEditing();//如果当前已经选择了，则把当前的先保存
								}
								defaultModel.addColumn("岗位名称" + (columncount + 1));
								table.getColumnModel().moveColumn(columncount, nowindex + 1);
							}
							//defaultModel.
						}
					});
			WLTButton bt_del = new WLTButton("删除");
			bt_del.setBounds(125, 130, 50, 20);
			bt_del.addActionListener(new ActionListener() { //删除按钮的响应方法
						public void actionPerformed(ActionEvent e) {
							int columncount = defaultModel.getColumnCount() - 1;
							int nowindex = table.getSelectedColumn();//获取当前选择的列
							if (nowindex == -1) {
								nowindex = columncount;
							}
							if (null != defaultModel.getDataVector() && defaultModel.getDataVector().size() > 0) {
								Vector colVector = (Vector) defaultModel.getDataVector().get(0);//获得表数据
								Vector def = new Vector();//构造表头数据
								for (int i = 0; i <= columncount; i++) {
									def.add(defaultModel.getColumnName(i));
								}
								if (columncount > -1) {//加入判断，防止删除完了之后再点删除则报错
									def.removeElementAt(nowindex);//先删除表头数据
									TableColumnModel columnModel = table.getColumnModel();
									TableColumn tableColumn = columnModel.getColumn(nowindex);
									colVector.removeElementAt(nowindex);//删除表数据
									Object[][] rowDatanew = new Object[1][colVector.size()];
									rowDatanew[0] = colVector.toArray();
									defaultModel = new DefaultTableModel(rowDatanew, def.toArray());//构造新数据
									table.setModel(defaultModel);//重载数据
									table.updateUI();//重新刷新数据
								}
							}
						}
					});
			table.revalidate();//表格重绘
			panel.add(label_posts);
			panel.add(bt_add);
			panel.add(bt_del);
			panel.add(sss); //
			//-----------------------------------------------------------------end-----------------------------------------------------------------------------------
		} else if ("STATION".equalsIgnoreCase(this.celltype)) {//如果是阶段

		} else if ("TRANSITION".equalsIgnoreCase(this.celltype)) {//【sunfujun/20120426/增加箭头类型的配置_王钢】
			JLabel label_type = new JLabel("箭头类型", JLabel.RIGHT); //
			label_type.setBounds(5, 130, 70, 20); //
			combox_type = new JComboBox(new Object[] { new ComBoxItemVO("2", "", "默认"), new ComBoxItemVO("1", "", "经典"), new ComBoxItemVO("4", "", "简约"), new ComBoxItemVO("5", "", "圆形"), new ComBoxItemVO("7", "", "单线"), new ComBoxItemVO("8", "", "双线"), new ComBoxItemVO("9", "", "菱形"), new ComBoxItemVO("0", "", "无") }); //
			combox_type.addItemListener(new ItemListener() { //监听事件
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onLineTypeChanged();
							}
						}
					});
			combox_type.setBounds(75, 130, 100, 20); //
			combox_type.setSelectedIndex(0);
			for (int i = 0; i < combox_type.getItemCount(); i++) {
				if ((this.lineType + "").equals(((ComBoxItemVO) combox_type.getItemAt(i)).getId())) {
					combox_type.setSelectedIndex(i);
					break;
				}
			}
			JLabel label_single = new JLabel("是否单向", JLabel.RIGHT); //
			label_single.setBounds(5, 150, 70, 20); //
			combox_single = new JComboBox(new Object[] { "是", "否" }); //
			combox_single.setBounds(75, 150, 100, 20); //
			combox_single.addItemListener(new ItemListener() { //监听事件
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								onSingleChanged();
							}
						}
					});
			if (this.isSingle) {
				combox_single.setSelectedIndex(0);
			} else {
				combox_single.setSelectedIndex(1);
			}
			panel.add(label_type); //
			panel.add(combox_type); //
			panel.add(label_single); //
			panel.add(combox_single); //
		}
		return panel; //
	}

	/**
		 * panel绘完后，获得焦点才有意义【岳耀彪/2012-03-12】
		 * 这里有问题，如果选择字体大小和字体类型时不执行刷新，但如果选择环节类型（图片）则会刷新，
		 * 这时刷新逻辑里又执行了名称框获得焦点的逻辑，故感觉环节类型总点不开。
		 * 【李春娟/2012-05-24】
		 * 
		 */
	/*
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			textarea_content.requestFocus(); //
		}*/

	public String getFontType() {
		return fontType;
	}

	public int getFontSize() {
		return fontSize;
	}

	public Color getForeGround() {
		return foreGround;
	}

	public Color getBackGround() {
		return backGround;
	}

	public String getImgstr() {
		return imgstr;
	}

	public String getContentStr() {
		return contentStr;
	}

	public String getPostsStr() {
		return postsStr;
	}

	public String getSeq() {
		return seq;
	}

	public int getViewType() {
		return combox_type.getSelectedIndex() + 1;
	}

	public int getCloseType() {
		return closeType;
	}

	public boolean isCanSetCellColor() {
		return isCanSetCellColor;
	}

	public void setCanSetCellColor(boolean isCanSetCellColor) {
		this.isCanSetCellColor = isCanSetCellColor;
		if (this.isCanSetCellColor) {
			ref_foreground.setVisible(true);
			ref_background.setVisible(true);
		} else {
			ref_foreground.setVisible(false);
			ref_background.setVisible(false);
		}
	}

	private boolean existFont(String[] _allFonts, String _font) {
		for (int i = 0; i < _allFonts.length; i++) {
			if (_allFonts[i].equals(_font)) {
				return true;
			}
		}
		return false;
	}

	protected void onTypeChanged() {
		if (viewtype == 8 && getViewType() != 8) {//改变前状态为图片类型
			ref_img.setVisible(false);
			ref_background.setItemEditable(true);
			ref_background.getTextField().setBackground(convertStrToColor(ref_background.getValue())); //设置背景颜色
		} else if (viewtype != 8 && getViewType() == 8) {
			ref_img.setVisible(true);
			ref_background.setItemEditable(false);
		}
		viewtype = getViewType();
	}

	protected void onLineTypeChanged() {
		this.lineType = Integer.parseInt(((ComBoxItemVO) combox_type.getSelectedItem()).getId());
	}

	protected void onSingleChanged() {
		if (combox_single.getSelectedIndex() == 0) {
			this.isSingle = true;
		} else {
			this.isSingle = false;
		}
	}

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}

	/**
	 * 将颜色转换成字符串,以逗号将R,G,B相隔
	 * @param _color
	 * @return
	 */
	private String convertColorToStr(Color _color) {
		return _color.getRed() + "," + _color.getGreen() + "," + _color.getBlue(); //
	}

	/**
	 * 将字符串转换成颜色
	 * @param _str
	 * @return
	 */
	private Color convertStrToColor(String _str) {
		String[] items = _str.split(","); //
		return new Color(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]));
	}

	/**
	 * 按钮面板
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void onConfirm() {
		if (null != table) {//袁江晓20121119更改，主要解决修改环节名称时为null的问题
			if (null != table.getCellEditor()) {
				table.getCellEditor().stopCellEditing();//如果当前已经选择了，则把当前的先保存
			}
		}
		if (combox_fonttype.getSelectedItem() != null && !((String) combox_fonttype.getSelectedItem()).equals("")) {
			this.fontType = (String) combox_fonttype.getSelectedItem(); //
		}
		if (combox_fontsize.getSelectedItem() != null && !((String) combox_fontsize.getSelectedItem()).equals("")) {
			this.fontSize = Integer.parseInt((String) combox_fontsize.getSelectedItem()); //
		}
		this.foreGround = convertStrToColor(ref_foreground.getValue()); //前景颜色.
		this.backGround = convertStrToColor(ref_background.getValue()); //后景颜色.
		this.imgstr = ref_img.getValue();
		this.contentStr = textarea_content.getText(); //
		if ("ACTIVITY".equalsIgnoreCase(this.celltype)) {//如果是环节
			this.seq = textarea_seq.getText();
		} else if ("DEPT".equalsIgnoreCase(this.celltype)) {
			//袁江晓20120906添加  修改之前的行添加为列添加，按照可视的宽度和次序来调整岗位
			//-----------------------------------------------------------------begin-----------------------------------------------------------------------------------
			StringBuffer sb_post = new StringBuffer();
			for (int i = 0; i < table.getColumnCount(); i++) {
				String columnName = table.getColumnName(i);
				if (null != table.getValueAt(0, i) && !table.getValueAt(0, i).equals("")) {//如果岗位不为空则添加，否则不添加
					sb_post.append(table.getValueAt(0, i) + "#" + table.getColumn(columnName).getWidth() + ";");//返回岗位名称和宽度，中间用分号隔开，岗位名称和宽度之间用$$隔开
				}
			}
			this.postsStr = sb_post.toString();
			//-----------------------------------------------------------------end-----------------------------------------------------------------------------------			
		}

		this.closeType = 1;
		this.dispose(); //
	}

	private void onCancel() {
		this.closeType = 2;
		this.dispose(); //
	}
}
