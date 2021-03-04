/**************************************************************************
 * $RCSfile: StyleConfigPanel_Styles.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.sysapp.login.StyleTempletDefineBuilder;
import cn.com.infostrategy.to.sysapp.login.StyleTempletDefineVO;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatEventBindFormulaParse;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

/**
 * 格式化面板配置页面...
 * 
 * @author xch
 * 
 */
public class StyleConfigPanel_Styles extends AbstractTempletRefPars implements ActionListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -6147862007359706108L;

	private WLTButton btnAddFont, btnDelFont, resetFont, clearText; //

	private MultiStyleTextPanel textPanel = null;
	private JTextField textField = null;

	private static int[][] column_width = null; //各列宽
	private BillListPanel listPanel_allstyles = new BillListPanel(new TMO_AllStyles()); //所有模板
	private BillListPanel listPanel_templet = new BillListPanel(new TMO_TEMPLET()); //所有模板

	private JScrollPane jsp_sys_exp = null; //滚动框
	private MouseAdapter adapter = null; //适配器
	private WLTTabbedPane jtp_detail = null; //多页签
	private JPanel southPanel = null;
	private final static Font textPaneFont = new Font("宋体", Font.PLAIN, 16);//

	public StyleConfigPanel_Styles(String _text) {
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(_text)); //
	}

	private JPanel getCenterPanel(String _text) {
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //  
		btnAddFont = new WLTButton("增加字体");
		btnDelFont = new WLTButton("缩小字体");
		resetFont = new WLTButton("重置字体"); //
		clearText = new WLTButton("清空"); //

		btnAddFont.addActionListener(this);
		btnDelFont.addActionListener(this);
		resetFont.addActionListener(this);
		clearText.addActionListener(this); //

		panel_north.add(btnAddFont); //
		panel_north.add(btnDelFont); //
		panel_north.add(resetFont); //
		panel_north.add(clearText); //

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(panel_north, BorderLayout.NORTH);

		textPanel = new MultiStyleTextPanel(_text); // 
		textPanel.addKeyWordStyle(new String[] { "风格模板类型" }, Color.RED, true); ////
		textPanel.addKeyWordStyle(new StyleTempletDefineBuilder().gettAllStyleTempletNames(), Color.MAGENTA, false); //

		jtp_detail = getDetailPane(); //
		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPanel), jtp_detail); //
		splitPane.setDividerLocation(500); //
		panel.add(splitPane, BorderLayout.CENTER); //
		return panel;
	}

	/**
	 * 获得下边的页签面板
	 * 
	 * @return
	 */
	private WLTTabbedPane getDetailPane() {
		if (jtp_detail != null) {
			return jtp_detail;
		}
		jtp_detail = new WLTTabbedPane(); //
		StyleTempletDefineBuilder stdb = new StyleTempletDefineBuilder(); //
		StyleTempletDefineVO[] stdvos = stdb.getAllStyleTempletDefineVOs(); //
		for (int i = 0; i < stdvos.length; i++) {
			int li_newrow = listPanel_allstyles.addEmptyRow(); //
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getName()), li_newrow, "Name"); //名称
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getDescr()), li_newrow, "Descr"); //说明
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getFormulaDefine()), li_newrow, "Formula"); //公式
			listPanel_allstyles.setValueAt(new StringItemVO(stdvos[i].getDefaultClassName()), li_newrow, "ClassName"); //实现类
		}
		listPanel_allstyles.clearSelection(); //

		listPanel_allstyles.addBillListMouseDoubleClickedListener(this); //
		listPanel_templet.addBillListMouseDoubleClickedListener(this); //

		jtp_detail.addTab("所有风格模板", listPanel_allstyles); //
		jtp_detail.addTab("元原模板", listPanel_templet); //
		jtp_detail.setPreferredSize(new Dimension(425, 500)); //
		return jtp_detail;
	}

	/**
	 * 判断文本区域是否有选中的文本
	 * 
	 * @return
	 */
	private boolean isTextSelected() {
		return (textPanel.getSelectionEnd() - textPanel.getSelectionStart()) != 0;
	}

	/**
	 * 获得系统公式
	 * 
	 * @return
	 */
	private String[][] getExpression() {
		Vector vec_function = FormatEventBindFormulaParse.getFunctionDetail();
		String[][] str_values = new String[vec_function.size()][];
		for (int i = 0; i < str_values.length; i++) {
			str_values[i] = (String[]) vec_function.get(i);
		}
		return str_values;
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("FORMATFORMULA", textPanel.getText().trim()); //
		return map;
	}

	public void stopEdit() {

	}

	protected String bsInformation() {
		return null;
	}

	protected String uiInformation() {
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddFont) { //增加字体
			addFont();
		} else if (e.getSource() == btnDelFont) { //缩小字体
			delFont();
		} else if (e.getSource() == resetFont) {
			resetFont();
		} else if (e.getSource() == clearText) {
			clearText(); //
		}
	}

	private void addFont() {
		int li_size = textPanel.getFont().getSize();
		textPanel.setFont(new Font("宋体", Font.PLAIN, li_size + 2)); //
		textPanel.updateUI();
	}

	private void delFont() {
		int fontsize = textPanel.getFont().getSize();
		int li_newSize = fontsize - 2; //
		if (li_newSize < 9) {
			li_newSize = 9;
		}
		textPanel.setFont(new Font("宋体", Font.PLAIN, li_newSize)); //
		textPanel.updateUI();
	}

	private void resetFont() {
		textPanel.setFont(new Font("宋体", Font.PLAIN, 12)); //
		textPanel.updateUI();
	}

	private void clearText() {
		textPanel.setText(""); //
	}

	private void putStrIntextArea(String _text) {
		textPanel.inputText(_text); //
	}

	public MultiStyleTextPanel getTextArea() {
		return textPanel;
	}

	/**
	 * 双击事件..
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getSource() == listPanel_allstyles) {
			String str_formula = _event.getCurrSelectedVO().getStringValue("Formula"); //
			putStrIntextArea(str_formula); //
		} else if (_event.getSource() == listPanel_templet) {
			String str_templetCode = _event.getCurrSelectedVO().getStringValue("TEMPLETCODE"); //
			putStrIntextArea(str_templetCode); //
		}
	}

	class TMO_AllStyles extends AbstractTMO {
		private static final long serialVersionUID = 1L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "allstyles"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "所有风格模板"); // 模板名称
			vo.setAttributeValue("templetname_e", "AllStyles"); // 模板名称
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector(); //
			HashVO itemVO = null; //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Name"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "名称"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Name"); // 显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表是否显示
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardwidth", "150"); //卡片宽度 
			itemVO.setAttributeValue("listwidth", "75"); //列表宽度 
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Descr"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "描述"); // 显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
			itemVO.setAttributeValue("itemname_e", "Descr"); // 显示名称
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表是否显示
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardwidth", "175"); //卡片宽度 
			itemVO.setAttributeValue("listwidth", "85"); //列表宽度 
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Formula"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "公式"); // 显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); // 控件类型
			itemVO.setAttributeValue("itemname_e", "Formula"); // 显示名称
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表是否显示
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardwidth", "600*100"); //列表宽度 
			itemVO.setAttributeValue("listwidth", "150"); //卡片宽度
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行 
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ClassName"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "实现类"); // 显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
			itemVO.setAttributeValue("itemname_e", "DefaultClassName"); // 显示名称
			itemVO.setAttributeValue("listisshowable", "N"); // 列表是否显示
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片是否显示
			itemVO.setAttributeValue("cardwidth", "600"); //列表宽度 
			itemVO.setAttributeValue("listwidth", "150"); //卡片宽度
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行 
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]); //
		}

	}

	class TMO_TEMPLET extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "pub_templet_1"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "元原模板"); // 模板名称
			vo.setAttributeValue("templetname_e", "Templet"); // 模板名称
			vo.setAttributeValue("tablename", "pub_templet_1"); // 查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); // 主键名
			vo.setAttributeValue("pksequencename", null); // 序列名
			vo.setAttributeValue("savedtablename", null); // 保存数据的表名
			vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板

			vo.setAttributeValue("TREEPK", "id"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeviewfield", "name"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeseqfield", "seq"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowroot", "Y"); // 列表是否显示操作按钮栏
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "编码"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Templetcode"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); // 默认值公式
			itemVO.setAttributeValue("listwidth", "175"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,60"); //
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "名称"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Templetname"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "3"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "125"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "225"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,70"); //
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
