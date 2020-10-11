package cn.com.infostrategy.ui.mdata.formatcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 一种通过将注册面板快速转换成一个自定义参照的基类!!
 * 即一般一个自定义参照都可以通过一个注册面板生成主页面,然后在下面增加一个确定与取消按扭从而形成一个参照!!!
 * @author xch
 *
 */
public abstract class AbstractRegFormatRefDialog extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillFormatPanel billFormatPanel = null;
	private BillListPanel selectedDataListPanel = null; //选中数据的列表
	private WLTButton btn_add, btn_addall, btn_remove, btn_removeall; //四个按钮
	private WLTButton btn_moveup, btn_movedown, btn_addregbtn; //上移,下移!!

	public AbstractRegFormatRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, "[注]" + _title, refItemVO, panel);
	}

	public abstract String getRegFormatCode(); //注册面板的编码!!!必须要有

	public int getRefStyleType() { //参照风格
		return 0; //
	}

	public abstract RefItemVO checkBeforeClose() throws Exception; //注册面板的编码!!!必须要有

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billFormatPanel = BillFormatPanel.getRegisterFormatPanel(getRegFormatCode()); //通过注册码生成一个格式面板
		JPanel panel_refcontent = new JPanel(new BorderLayout()); //
		int li_seltype = getRefStyleType(); //选择类型,0-单选;1-多选;2-多选(左右分割);3-多选(上下分割)4;-多选(两个页签)
		if (li_seltype == 0) { //单选,但只能选一个..
			panel_refcontent.add(billFormatPanel); //

			BillPanel returnPanel = this.getBillFormatPanel().getReturnRefItemVOFrom();
			if (returnPanel != null) {
				if (returnPanel instanceof BillListPanel) {
					BillListPanel billList = (BillListPanel) returnPanel;
					billList.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
				} else if (returnPanel instanceof BillTreePanel) {
					BillTreePanel billTree = (BillTreePanel) returnPanel;
					billTree.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //只能单选
				}
			}

			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 1) { //单选,但可以选多个
			panel_refcontent.add(billFormatPanel); //
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 2) //左右分割
		{
			WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT); //
			splitPane.setDividerLocation(billFormatPanel.getSuggestWidth() - 400); // 
			splitPane.setLeftComponent(billFormatPanel); //

			btn_add = new WLTButton("     >    "); //
			btn_addall = new WLTButton("   >>    "); //
			btn_remove = new WLTButton("     <    "); //
			btn_removeall = new WLTButton("   <<    "); //
			//增加监听事件!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			JPanel panel_btn = new JPanel(new BorderLayout()); //
			panel_btn.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
			Box bntBox = Box.createVerticalBox();
			bntBox.add(Box.createGlue());
			bntBox.add(btn_add); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_addall); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_remove); //
			bntBox.add(Box.createVerticalStrut(10));
			bntBox.add(btn_removeall); //
			bntBox.add(Box.createGlue());
			panel_btn.add(bntBox); //
			panel_btn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //

			JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_btn, getSelectedDataListPanel()); //
			splitPane2.setDividerLocation(55); //
			splitPane2.setDividerSize(0); //
			splitPane2.setEnabled(false); //
			splitPane.setRightComponent(splitPane2); //

			panel_refcontent.add(splitPane); //
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 3) {
			WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT); //
			splitPane.setDividerLocation(billFormatPanel.getSuggestHeight() - 300); // 
			splitPane.setLeftComponent(billFormatPanel); //

			btn_add = new WLTButton("加"); //
			btn_addall = new WLTButton("全加"); //
			btn_remove = new WLTButton("减"); //
			btn_removeall = new WLTButton("全减"); //

			//增加监听事件!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			btn_add.setPreferredSize(new Dimension(65, 20)); //
			btn_addall.setPreferredSize(new Dimension(65, 20)); //
			btn_remove.setPreferredSize(new Dimension(65, 20)); //
			btn_removeall.setPreferredSize(new Dimension(65, 20)); //

			JPanel panel_btn = new JPanel(new FlowLayout()); //
			panel_btn.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
			panel_btn.add(btn_add); //
			panel_btn.add(btn_addall); //
			panel_btn.add(btn_remove); //
			panel_btn.add(btn_removeall); //

			JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel_btn, getSelectedDataListPanel()); //
			splitPane2.setDividerLocation(30); //
			splitPane2.setDividerSize(0); //
			splitPane2.setEnabled(false); //
			splitPane.setRightComponent(splitPane2); //

			panel_refcontent.add(splitPane);
			this.add(panel_refcontent, BorderLayout.CENTER); //
			this.add(getSouthPanel(), BorderLayout.SOUTH); //
		} else if (li_seltype == 4) {
			btn_add = new WLTButton(">"); //
			btn_addall = new WLTButton(">>"); //
			btn_remove = new WLTButton("<"); //
			btn_removeall = new WLTButton("<<"); //

			//增加监听事件!!!
			btn_add.addActionListener(this); //
			btn_addall.addActionListener(this); //
			btn_remove.addActionListener(this); //
			btn_removeall.addActionListener(this); //

			btn_add.setPreferredSize(new Dimension(65, 20)); //
			btn_addall.setPreferredSize(new Dimension(65, 20)); //
			btn_remove.setPreferredSize(new Dimension(65, 20)); //
			btn_removeall.setPreferredSize(new Dimension(65, 20)); //

			JPanel panel_btn_1 = new JPanel(new FlowLayout()); //
			panel_btn_1.add(btn_add); //
			panel_btn_1.add(btn_addall); //

			JPanel panel_btn_2 = new JPanel(new FlowLayout()); //
			panel_btn_2.add(btn_remove); //
			panel_btn_2.add(btn_removeall); //
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_cancel.addActionListener(this); //
			btn_confirm.addActionListener(this); //

			panel_btn_2.add(btn_remove); //
			panel_btn_2.add(btn_removeall); //
			panel_btn_2.add(btn_confirm); //
			panel_btn_2.add(btn_cancel); //

			JPanel panel_1 = new JPanel(new BorderLayout()); //
			panel_1.add(billFormatPanel, BorderLayout.CENTER); //
			panel_1.add(panel_btn_1, BorderLayout.SOUTH); //

			JPanel panel_2 = new JPanel(new BorderLayout()); //
			panel_2.add(getSelectedDataListPanel(), BorderLayout.CENTER); //
			panel_2.add(panel_btn_2, BorderLayout.SOUTH); //

			JTabbedPane tabbedPane = new JTabbedPane(); //
			tabbedPane.addTab("参照选择", panel_1); //
			tabbedPane.addTab("已选择内容", panel_2); //
			tabbedPane.setSelectedIndex(1); //
			panel_refcontent.add(tabbedPane); //

			this.add(panel_refcontent, BorderLayout.CENTER); //
		}

		if (getRefStyleType() == 1) { //如果是多选,但不分割!
			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				String[] str_ids = getInitRefItemVO().getId().split(";"); //
				setInitSelectRow(getBillFormatPanel().getReturnRefItemVOFrom(), str_ids, getBillFormatPanel().getReturnRefItemVOIDFieldName()); //
			}
		} else if (getRefStyleType() == 2 || getRefStyleType() == 3 || getRefStyleType() == 4) {
			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				String[] str_ids = getInitRefItemVO().getId().split(";"); //
				String[] str_names = getInitRefItemVO().getName().split(";"); //
				for (int i = 0; i < str_ids.length; i++) {
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_ids[i]), li_row, "RefID"); //插入数据
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_names[i]), li_row, "RefName"); //
				}
			}
		}

		//设置在大小与居中位置
		this.setSize(billFormatPanel.getSuggestWidth(), billFormatPanel.getSuggestHeight()); //设置建议宽度与高度
		int li_screenwidth = (int) UIUtil.getScreenMaxDimension().getWidth(); //
		int li_screenheight = (int) UIUtil.getScreenMaxDimension().getHeight(); //
		int li_x = (li_screenwidth - billFormatPanel.getSuggestWidth()) / 2;
		int li_y = (li_screenheight - billFormatPanel.getSuggestHeight()) / 2;
		li_x = (li_x < 0 ? 0 : li_x);
		li_y = (li_y < 0 ? 0 : li_y);
		this.setLocation(li_x, li_y); //

		if (getRefStyleType() != 0 && billFormatPanel.getReturnRefItemVOFrom() == null) {
			this.setTitle(this.getTitle() + "<有问题:定义为多选返回,但没有定义返回区域>"); //
		}
	}

	private void setInitSelectRow(BillPanel _billPanel, String[] str_ids, String _returnRefItemVOIDFieldName) {
		if (_billPanel != null) {
			if (_billPanel instanceof BillListPanel) {
				BillListPanel billList = (BillListPanel) _billPanel; //
				int li_rowcount = billList.getRowCount(); //
				for (int i = 0; i < li_rowcount; i++) {
					String str_idvalue = billList.getRealValueAtModel(i, _returnRefItemVOIDFieldName); //
					if (str_idvalue != null) {
						for (int j = 0; j < str_ids.length; j++) {
							if (str_idvalue.equals(str_ids[j])) {
								billList.getTable().getSelectionModel().addSelectionInterval(i, i); //
								break; //
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showInfo(); //
				}
			}
		});

		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	protected void showInfo() {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_regformatpanel where code = '" + getRegFormatCode() + "'"); //
			String str_info = "";//
			str_info = str_info + "实现类:" + this.getClass().getName() + "\r\n";
			str_info = str_info + "引用的注册面板Code:" + hvs[0].getStringValue("code") + "\r\n";
			str_info = str_info + "\r\n-------------- 定义公式 ----------------\r\n";
			str_info = str_info + hvs[0].getStringValue("formatformula") + "\r\n"; //

			str_info = str_info + "\r\n-------------- 事件公式 ----------------\r\n";
			str_info = str_info + hvs[0].getStringValue("eventbindformula") + "\r\n"; //
			MessageBox.showTextArea(this, "自定义参照引用的注册面板信息", str_info); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //如果是确定则返回数据
			try {
				returnRefItemVO = checkBeforeClose(); // 返回前必须有一个校验,如果成功返回则返回数据,否则抛异常,退出!
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return; //
			}
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		} else if (e.getSource() == btn_add) {
			onAdd(); //
		} else if (e.getSource() == btn_addall) {
			onAddAll(); //
		} else if (e.getSource() == btn_remove) {
			onRemove(); //
		} else if (e.getSource() == btn_removeall) {
			onRemoveAll(); //
		} else if (e.getSource() == btn_addregbtn) {
			onAddSysRegButton(); //增加系统注册型按钮!!
		}
	}

	/**
	 * 增加一个
	 */
	public void onAdd() {
	}

	public void onAddAll() {
	}

	public void onRemove() {
	}

	public void onRemoveAll() {
	}

	public void onAddSysRegButton() {
		String[][] str_allbtns = WLTButton.getSysRegButtonType(); ////
		JComboBox comBox = new JComboBox(); //
		for (int i = 1; i < str_allbtns.length; i++) {
			comBox.addItem(str_allbtns[i][0]); //
		}

		JPanel panel = new JPanel(null); //
		comBox.setBounds(5, 5, 165, 20); //
		panel.add(comBox); //
		panel.setPreferredSize(new Dimension(180, 25)); //
		if (JOptionPane.showConfirmDialog(this, panel, "提示", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			String str_seltext = (String) comBox.getSelectedItem(); //
			int li_newrow = selectedDataListPanel.addEmptyRow(); //
			selectedDataListPanel.setValueAt(new StringItemVO("$" + str_seltext), li_newrow, "RefID"); //
			selectedDataListPanel.setValueAt(new StringItemVO("$" + str_seltext), li_newrow, "RefName"); //
		}
	}

	public BillFormatPanel getBillFormatPanel() {
		return billFormatPanel;
	}

	public BillListPanel getSelectedDataListPanel() {
		if (selectedDataListPanel == null) {
			selectedDataListPanel = new BillListPanel(new TMO_SelectedData()); //
			btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //上移!!
			btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //下移!!
			btn_addregbtn = new WLTButton("系统注册型"); //
			btn_addregbtn.addActionListener(this); //
			selectedDataListPanel.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown, btn_addregbtn }); //
			selectedDataListPanel.repaintBillListButton(); //
		}
		return selectedDataListPanel; // 
	}

	class TMO_SelectedData extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "selecteddata"); //模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "已选择数据"); //模板名称
			vo.setAttributeValue("templetname_e", "selecteddata"); //模板名称
			vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); //主键名
			vo.setAttributeValue("pksequencename", null); //序列名
			vo.setAttributeValue("savedtablename", null); //保存数据的表名
			vo.setAttributeValue("CardWidth", "577"); //卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
			vo.setAttributeValue("listcustpanel", null); //列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板

			vo.setAttributeValue("ISSHOWLISTCUSTBTN", "Y"); //列表是否显示自定义按钮
			vo.setAttributeValue("Listcustbtndesc", null); //列表自定义按钮

			vo.setAttributeValue("TREEPK", "id"); //列表是否显示操作按钮栏
			vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //列表是否显示操作按钮栏
			vo.setAttributeValue("Treeviewfield", "name"); //列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowtoolbar", "Y"); //树型显示工具栏
			vo.setAttributeValue("Treeisonlyone", "N"); //树型显示工具栏
			vo.setAttributeValue("Treeseqfield", "seq"); //列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowroot", "Y"); //列表是否显示操作按钮栏
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "RefID"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "参照存储值"); //显示名称
			itemVO.setAttributeValue("itemname_e", "RefID"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "RefName"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "参照显示值"); //显示名称
			itemVO.setAttributeValue("itemname_e", "RefName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "155"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
