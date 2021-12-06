package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTScrollPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillLevelPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.design.CreateWorkFlowDialog;

import com.pushworld.ipushgrc.to.wfrisk.WFGraphEditPanelBtnUIIntercept;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 流程文件的所有流程图的设计面板
 * 上面是一个导航面板，包括所有流程radio按钮（查看和编辑流程图），流程新建按钮（新建一流程），流程编辑按钮（编辑流程编码和名称），流程删除按钮（删除流程的同时删除流程相关的所有信息，包括环节、环节连线、阶段和部门、流程相关信息和环节相关信息）
 * 下面是一个billLevelPanel，可放多个面板（单个的流程图、流程相关和环节相关的按钮）
 * 
 * @author lcj
 * 
 */
public class WFGraphEditPanel extends JPanel implements ActionListener {

	private Container parentContainer;// 父面板
	private String cmpfileid;// 流程文件id
	private String cmpfilename;// 流程文件名称
	private String cmpfilecode;//流程文件编码
	private boolean editable = true;// 流程图是否可编辑
	private boolean showRefPanel = true;//是否显示按钮面板
	private IPushGRCServiceIfc service;//产品服务
	private JPanel radioBtnPanel;// 所有流程的radio面板
	private WLTScrollPanel scrollPanel;// 嵌套radio面板的滚动面板
	private JPanel wfBtnPanel;// 流程增删改按钮面板
	private WLTSplitPane northSplitPane;// 导航面板，用来显示流程的增删改查按钮
	private BillLevelPanel billLevelPanel;// 多层面板，用来显示流程图和相关按钮
	private JRadioButton currSelectedRadio;// 当前选择的流程radio
	private JRadioButton secondLastSelectedRadio;// 倒数第二次选中的流程radio
	private ButtonGroup buttongroup = new ButtonGroup();// 存储所有流程radiobutton
	private ArrayList allSeletedWfId = new ArrayList();// 存储所有查看过的流程的id

	private WLTButton insertWf;// 新建流程按钮
	private WLTButton editWf;// 编辑流程编码和名称的按钮
	private WLTButton deleteWf;// 删除流程按钮，包括删除流程及相关的所有信息
	private WLTButton copyWf;// 复制流程图按钮
	private WLTButton btn_show;
	private WLTButton seqWf; //流程排序

	private WLTButton importSvg; //导入Visio--Svg文件。
	private int dividerLocation = 460;//以前是430，后来因增加了【复制流程】按钮，故改为460【李春娟/2014-10-10】
	private String blcorpid = null;
	private TBUtil tbutil = new TBUtil();
	private BillListPanel processpanel;//复制流程图的列表PANEL

	public WFGraphEditPanel(Container _parentContainer, String _cmpfileid, String _cmpfilename, String[][] _processes, boolean _editable) {
		this.setLayout(new BorderLayout()); //
		this.parentContainer = _parentContainer;
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.editable = _editable;
		try {
			if (editable) {
				String[][] str_file = UIUtil.getStringArrayByDS(null, "select cmpfilecode,blcorpid from cmp_cmpfile where id =" + this.cmpfileid);
				this.cmpfilecode = str_file[0][0];
				this.blcorpid = str_file[0][1];//查询出流程文件的主管部门，在新增流程时设置流程的所属部门【李春娟/2012-03-14】
				if (cmpfilecode == null || "".equals(cmpfilecode.trim())) {
					cmpfilecode = cmpfilename;//文件编码在新增流程时用到，如果文件编码为空，默认为文件名称
				}

			}
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e1) {
			MessageBox.showException(parentContainer, e1);
		}
		parentContainer.addComponentListener(new ComponentAdapter() {// dialog窗口添加重置大小事件，用来处理导航面板的显示

					public void componentResized(ComponentEvent e) {
						onRadioBtnPanelResized();
					}
				});
		billLevelPanel = new BillLevelPanel(); // 多层面板，用来存放流程图及其相关按钮
		/* 下面两句一定要放在后面的for前 */
		this.add(getNorthPanel(), BorderLayout.NORTH); // 导航面板，对流程的增删改查
		this.add(billLevelPanel, BorderLayout.CENTER); // 多层面板
		// 如果该流程文件有流程，则构建流程radio按钮和多层面板
		if (_processes != null) {//【李春娟/2014-08-21】
			for (int i = 0; i < _processes.length; i++) {
				onAddAndSelectRadioBtn(_processes[i][0], _processes[i][1], _processes[i][2], false, false);
			}
		} else {
			editWf.setEnabled(false);
			deleteWf.setEnabled(false);
			seqWf.setEnabled(false);
		}
	}

	/**
	 *  重置所有流程的radio面板
	 */
	protected void onRadioBtnPanelResized() {
		northSplitPane.setDividerLocation(parentContainer.getWidth() - dividerLocation);
		int count = radioBtnPanel.getComponentCount();
		int containerlength = 0;
		float otherlength = 5f;//流程间隙
		float alllength = 0;
		int showlength = 3;
		float bytelength = 6.1f;//一位的像素

		for (int i = 0; i < count; i++) {
			JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(i);
			String[] processmsg = (String[]) radioBtnPanel.getClientProperty(radioBtn);
			if (processmsg[2].getBytes().length > showlength) {
				showlength = processmsg[2].getBytes().length;
			}
			alllength += processmsg[2].getBytes().length + otherlength;
		}
		containerlength = parentContainer.getWidth();
		if (editable) {//如果可编辑，要减去新建流程等按钮面板
			containerlength -= (dividerLocation + 30);
		} else {
			containerlength -= 100;
		}
		if (containerlength < alllength * bytelength) {
			while (true) {
				if (showlength <= 7) {
					showlength = 7;//至少留三个字
					break;
				}
				showlength--;
				alllength = 0;
				for (int i = 0; i < count; i++) {
					JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(i);
					String[] processmsg = (String[]) radioBtnPanel.getClientProperty(radioBtn);
					if (processmsg[2].getBytes().length > showlength) {
						alllength += (showlength + "...".getBytes().length + otherlength);
					} else {
						alllength += (processmsg[2].getBytes().length + otherlength);
					}
				}
				if (containerlength >= alllength * bytelength) {
					break;
				}
			}
			for (int i = 0; i < count; i++) {
				JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(i);
				String[] processmsg = (String[]) radioBtnPanel.getClientProperty(radioBtn);
				if (processmsg[2] != null && processmsg[2].getBytes().length > showlength) {
					for (int j = 3; j < processmsg[2].length(); j++) {
						if (processmsg[2].substring(0, j - 1).getBytes().length < showlength && processmsg[2].substring(0, j).getBytes().length >= showlength) {
							radioBtn.setText(processmsg[2].substring(0, (j - 1)) + "...");
							break;
						}
					}
				} else {
					radioBtn.setText(processmsg[2]);
				}
			}
		} else {
			for (int i = 0; i < count; i++) {
				JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(i);
				String[] processmsg = (String[]) radioBtnPanel.getClientProperty(radioBtn);
				radioBtn.setText(processmsg[2]);
			}
		}
		billLevelPanel.revalidate();//需要重新加载一下,否则会出现白屏现象【李春娟/2012-03-21】
	}

	public BillLevelPanel getBillLevelPanel() {
		return billLevelPanel;
	}

	/**
	 * 当前选择的流程radio的get方法
	 * 
	 * @return
	 */
	protected JRadioButton getCurrSelectedRadio() {
		return currSelectedRadio;
	}

	/**
	 * 倒数第二次选中的流程radio的get方法
	 * 
	 * @return
	 */
	public JRadioButton getSecondLastSelectedRadio() {
		return secondLastSelectedRadio;
	}

	/**
	 * 倒数第二次选中的流程radio的set方法
	 * 
	 * @param secondLastSelectedRadio
	 *            倒数第二次选中的流程radio
	 */
	public void setSecondLastSelectedRadio(JRadioButton secondLastSelectedRadio) {
		this.secondLastSelectedRadio = secondLastSelectedRadio;
	}

	/**
	 * 获得当前选择的流程的信息
	 * 
	 * @return
	 */
	protected Object getCurrSelectedWf() {
		if (getCurrSelectedRadio() == null) {
			return null;
		}
		return radioBtnPanel.getClientProperty(getCurrSelectedRadio());
	}

	/**
	 * 当前选择的流程radio的set方法
	 * 
	 * @param currSelectedRadio
	 */
	public void setCurrSelectedRadio(JRadioButton currSelectedRadio) {
		this.currSelectedRadio = currSelectedRadio;
	}

	/**
	 * 当前选择的流程面板
	 * 
	 * @param currSelectedRadio
	 */
	public WFGraphEditItemPanel getCurrSelectedItemPanel() {
		Object obj_process = getCurrSelectedWf();
		if (obj_process == null) {
			return null;
		}
		String[] process = (String[]) obj_process;
		String processid = process[0];// 流程主键
		return (WFGraphEditItemPanel) billLevelPanel.getLevelPanel(processid);
	}

	/**
	 * 带按钮的导航面板,可对流程进行增删改查。 当新增或删除流程时,需要从buttongroup列表中重新构造按钮!!
	 * 
	 * @return
	 */
	public WLTSplitPane getNorthPanel() {
		if (northSplitPane == null) {
			/* 带按钮的导航面板,当新增或删除流程时,需要从buttongroup列表中重新构造按钮!! */
			radioBtnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); // 所有流程的radio面板
			radioBtnPanel.setOpaque(false);// 设置透明
			scrollPanel = new WLTScrollPanel(radioBtnPanel);// 滚动面板
			scrollPanel.setOpaque(false);// 设置透明
			if (this.editable) {
				wfBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); //
				insertWf = new WLTButton("新建流程");// 新建流程按钮
				editWf = new WLTButton("更改名称");// 编辑流程编码和名称的按钮
				deleteWf = new WLTButton("删除流程");// 删除流程按钮，包括删除流程及相关的所有信息
				copyWf = new WLTButton("复制流程"); //复制其它流程的流程图[YangQing/2013-08-01]
				copyWf.setToolTipText("复制其它流程的流程图");
				seqWf = new WLTButton("流程排序");
				importSvg = new WLTButton("导入Visio");
				insertWf.addActionListener(this);
				editWf.addActionListener(this);
				deleteWf.addActionListener(this);
				copyWf.addActionListener(this);
				seqWf.addActionListener(this);
				importSvg.addActionListener(this);
				wfBtnPanel.add(insertWf);
				wfBtnPanel.add(copyWf);
				wfBtnPanel.add(editWf);
				wfBtnPanel.add(deleteWf);
				wfBtnPanel.add(seqWf);
				boolean showvisio = tbutil.getSysOptionBooleanValue("一图两表导入Visio按钮是否显示", true);//事业单位内控【李春娟/2013-03-07】
				if (showvisio) {
					wfBtnPanel.add(importSvg);
				}

				final String definedbtn = tbutil.getSysOptionHashItemStringValue("自定义一图两表上方按钮", "按钮事件类", "");//配置的按钮事件类需要实现接口WFGraphEditPanelBtnUIIntercept【李春娟/2012-04-11】
				if (!"".equals(definedbtn)) {
					String btntext = tbutil.getSysOptionHashItemStringValue("自定义一图两表上方按钮", "按钮名称", "导入主流程");
					WLTButton importMainProcess = new WLTButton(btntext);
					importMainProcess.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								WFGraphEditPanelBtnUIIntercept intercept = (WFGraphEditPanelBtnUIIntercept) Class.forName(definedbtn).newInstance(); //
								intercept.afterClick(WFGraphEditPanel.this);
							} catch (Exception e1) {
								MessageBox.showException(WFGraphEditPanel.this, e1);
							}
						}
					});
					wfBtnPanel.add(importMainProcess);
					this.dividerLocation += importMainProcess.getPreferredSize().getWidth();//重新设置splitpane的分割位置
				}
			}
			northSplitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, scrollPanel, wfBtnPanel); // 导航面板
			northSplitPane.setDividerLocation(parentContainer.getWidth() - dividerLocation);
			northSplitPane.setDividerSize(0);
			northSplitPane.setOpaque(false);// 设置透明
		}
		return northSplitPane; //
	}

	/**
	 * 流程增删改按钮的逻辑
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == insertWf) {// 新建流程，需要设置默认编码和名称
			onInsertWf(buttongroup.getButtonCount() + 1);
		} else if (e.getSource() == editWf) {// 编辑流程编码或名称
			onEditWf();
		} else if (e.getSource() == deleteWf) {// 删除流程
			onDeleteWf();
		} else if (e.getSource() == copyWf) {// 复制流程图
			onCopyWf();
		} else if (e.getSource() == btn_show) {// 复制流程图时，预览流程图[YangQing/2013-08-05]
			BillVO billVO = processpanel.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.showSelectOne(processpanel);
				return;
			}
			processpanel.refreshCurrSelectedRow();
			billVO = processpanel.getSelectedBillVO();
			BillDialog dialog = new BillDialog(processpanel, "预览流程图", 1000, 700);
			String cmpfilename = billVO.getStringValue("cmpfilename", "");
			WFGraphEditPanel wfpanel = new WFGraphEditPanel(processpanel, billVO.getStringValue("cmpfileid"), cmpfilename, new String[][] { { billVO.getStringValue("id"), billVO.getStringValue("code"), billVO.getStringValue("name"), cmpfilename } }, false);
			dialog.setLayout(new BorderLayout());
			wfpanel.showLevel(billVO.getStringValue("id"));
			dialog.add(wfpanel);
			dialog.setVisible(true);
		} else if (e.getSource() == seqWf) {// 流程排序
			onSeqWf();
		} else if (e.getSource() == importSvg) {
			try {
				new ImportSVGUtil().init(cmpfileid, cmpfilecode, this);
				if (wfBtnPanel != null && buttongroup.getButtonCount() == 1) {
					for (int i = 0; i < wfBtnPanel.getComponentCount(); i++) {
						wfBtnPanel.getComponent(i).setEnabled(true);//如果所有流程都删除完了的话，除了新建按钮和导入visio按钮可用，其他按钮都不可用，故需要在导入visio后设置一下按钮可用//【李春娟/2012-04-20】
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {// radio按钮点击事件，如果某个按钮被选中，则将其他按钮设为未选中状态
			JRadioButton radioBtn_selected = (JRadioButton) e.getSource();
			if (radioBtn_selected.isSelected()) {// radio按钮是否被选中
				onSelectRadioBtn(radioBtn_selected, true);
			}
		}
	}

	/**
	 * 创建一个流程，自动设置流程默认编码和名称
	 * 
	 */
	private void onInsertWf(int _index) {
		try {
			String str_processcode = this.cmpfilecode + "_流程" + _index;
			String str_processname = this.cmpfilename + "_流程" + _index;
			String str_id = service.insertOneWf(str_processcode, str_processname, this.blcorpid, this.cmpfileid);//创建流程
			onAddAndSelectRadioBtn(str_id, str_processcode, str_processname, true, _index != 1);
			if (wfBtnPanel != null && buttongroup.getButtonCount() == 1) {
				for (int i = 0; i < wfBtnPanel.getComponentCount(); i++) {
					wfBtnPanel.getComponent(i).setEnabled(true);//如果所有流程都删除完了的话，除了新建按钮和导入visio按钮可用，其他按钮都不可用，故需要在新建流程后设置一下按钮可用//【李春娟/2012-04-12】
				}
			}
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 编辑流程，只用来编辑流程编码和名称 这里以后要记录日志
	 */
	protected void onEditWf() {
		Object obj_process = getCurrSelectedWf();
		if (obj_process == null) {
			MessageBox.show(this, "请选择一个流程进行编辑操作!");
			return;
		}
		String[] str_process = (String[]) obj_process;
		CreateWorkFlowDialog dialog = new CreateWorkFlowDialog(this);
		dialog.setTitle("更改流程名称");
		dialog.getBilCardPanel().setVisiable("USERDEF01", true);//设置负责部门可见
		dialog.getBilCardPanel().queryDataByCondition("id='" + str_process[0] + "'");
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {// 判断创建流程窗口的关闭状态，1-点击确定关闭；2-点击取消关闭；0-点击窗口右上角关闭而关闭
			String str_processid = str_process[0];// 流程id，未变
			String str_processcode = dialog.getProcessCode();// 流程新的编码
			String str_processname = dialog.getProcessName();// 流程新的名称
			String str_blcorpid = dialog.getDeptId();// 流程新的所属部门
			try {
				UpdateSQLBuilder sql_update = new UpdateSQLBuilder("pub_wf_process", "id=" + str_processid); //
				sql_update.putFieldValue("code", str_processcode); // 流程编码
				sql_update.putFieldValue("name", str_processname); // 流程名称
				sql_update.putFieldValue("userdef01", str_blcorpid); // 流程的所属部门
				UIUtil.executeUpdateByDS(null, sql_update.getSQL());// 执行sql

				WFGraphEditItemPanel itemPanel = (WFGraphEditItemPanel) billLevelPanel.getLevelPanel(str_processid);
				itemPanel.setProcesscode(str_processcode);
				itemPanel.setProcessname(str_processname);
				JRadioButton currRadio = getCurrSelectedRadio();
				currRadio.setText(str_processname);// 此处刷新更新radio的名称
				currRadio.setToolTipText(str_processname);
				currRadio.repaint();
				radioBtnPanel.putClientProperty(currRadio, new String[] { str_processid, str_processcode, str_processname });
				onRadioBtnPanelResized();
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 删除流程，包括删除流程相关和环节相关的所有信息， 这里以后要记录日志
	 */
	protected void onDeleteWf() {
		Object obj_process = getCurrSelectedWf();
		if (obj_process == null) {
			MessageBox.show(this, "请选择一个流程进行删除操作!");
			return;
		}
		if (MessageBox.showConfirmDialog(this, "此操作会删除本流程的所有相关信息,是否删除?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		String[] process = (String[]) obj_process;
		String processid = process[0];// 流程主键
		// 删除流程要记录日志，后台处理
		try {
			service.deleteWfById(processid);
			/* radio面板需要重绘 */
			buttongroup.remove(getCurrSelectedRadio());// 从所有的radio按钮组中删除
			radioBtnPanel.remove(getCurrSelectedRadio());// 从radio面板删除该radiobutton
			radioBtnPanel.repaint();
			billLevelPanel.removeLevelPanel(processid);// 从多层面板中删除单个流程设计面板
			allSeletedWfId.remove(processid);// 从记录查看过的流程的主键hashmap中删除
			if (getSecondLastSelectedRadio() != null) {// 判断是否存在上次选中的按钮
				onSelectRadioBtn(getSecondLastSelectedRadio(), false);// 设置上次选中的流程radio为选中状态
				setSecondLastSelectedRadio(null);// 需要设置一下，否则上次选中和当前选中的radio为同一个了
			} else if (buttongroup.getButtonCount() > 0) {
				boolean flag = true;
				Enumeration e = buttongroup.getElements();
				while (e.hasMoreElements()) {
					JRadioButton radioBtn = (JRadioButton) e.nextElement();
					Object obj = radioBtnPanel.getClientProperty(radioBtn);
					if (obj != null && allSeletedWfId.contains(((String[]) obj)[0])) {// 判断是否有曾打开过的页面，只要找到就打开并且推出循环
						flag = false;
						onSelectRadioBtn(radioBtn, false);
						break;
					}
				}
				Enumeration e1 = buttongroup.getElements();
				if (flag) {// 如果没有找到打开过的页面，则打开第一个页面
					JRadioButton radioBtn = (JRadioButton) e1.nextElement();
					onSelectRadioBtn(radioBtn, false);
				}
			} else {
				//				if (MessageBox.showConfirmDialog(this, "该文件没有流程,是否创建流程?") == JOptionPane.YES_OPTION) {// 该文件没有流程，提示是否创建
				//					onInsertWf(1);
				//					setSecondLastSelectedRadio(null);
				//				} else {// 如果选择取消，不创建流程则直接返回
				//					if (parentContainer instanceof JDialog) {
				//						((JDialog) parentContainer).dispose();
				//					}
				//				}				
				// 1.可以留一个空WFGraphEditItemPanel，到保存的时候提醒该流程是新建的流程(这里点击流程相关和环节相关的按钮是不允许的)，需要填写流程编码和流程名称.
				// 2.提示该文件没有流程,是否创建流程? 如果选择是则弹出填写流程编码和流程名的提示框，否则直接关闭dialog
				// 3.直接弹出提示框提示该文件没有流程本窗口将关闭。只有一个确定按钮，点击确定，然后将提示框和dialog都关闭

				//【李春娟/2012-04-12】
				//王钢提出，如果流程全部删除后, 只留一个空白的Panel, 【修改】,【删除】,【排序】,【导入】按钮都不可用, 只留一个【新建】。
				//因后来新增了增加自定义按钮的逻辑，故需要设置按钮面板里除了新建按钮外其他所有按钮都不可用
				if (wfBtnPanel != null) {
					for (int i = 0; i < wfBtnPanel.getComponentCount(); i++) {
						wfBtnPanel.getComponent(i).setEnabled(false);
					}
					insertWf.setEnabled(true);//最后设置新建流程按钮可用
					importSvg.setEnabled(true);//设置导入visio按钮可用【李春娟/2012-04-17】
				}
			}
			onRadioBtnPanelResized();
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 复制流程图[YangQing/2013-08-05]
	 */
	private void onCopyWf() {
		final Object obj_process = getCurrSelectedWf();
		if (obj_process == null) {
			MessageBox.show(this, "请选择流程进行操作.");
			return;
		}
		final BillListDialog processDialog = new BillListDialog(this, "请选择复制哪一个流程", "PUB_WF_PROCESS_YQ_Q01");
		processpanel = processDialog.getBilllistPanel();
		btn_show = new WLTButton("预览", "office_155.gif");
		btn_show.addActionListener(this);
		processpanel.addBillListButton(btn_show);
		processpanel.repaintBillListButton();
		String[] str_process = (String[]) obj_process;
		String currProcessid = str_process[0];//当前编辑的流程ID
		processpanel.setDataFilterCustCondition("id !=" + currProcessid);//以前出现过复制了本流程，导致流程相关和环节相关数据无法显示，故增加过滤不显示本流程【李春娟/2015-10-28】
		WLTButton btn_comfirm = processDialog.getBtn_confirm();
		btn_comfirm.setText("复制");
		btn_comfirm.setToolTipText("复制此流程图");
		btn_comfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BillVO copy_processVO = processpanel.getSelectedBillVO();
				if (copy_processVO == null) {
					MessageBox.show(processpanel, "请选择一个流程进行复制.");
					return;
				}

				String processname = copy_processVO.getStringValue("name");
				if (!MessageBox.confirm(processpanel, "确定要复制[" + processname + "]到当前流程吗?")) {//个别咨询师理解错误，会将空的流程图复制到当前流程，故这里提示一下【李春娟/2015-07-28】
					return;
				}
				String copy_processid = copy_processVO.getStringValue("id");//要拷贝的流程ID
				String[] str_process = (String[]) obj_process;
				String currProcessid = str_process[0];//当前编辑的流程ID
				try {

					if (getCurrSelectedItemPanel() != null) {
						ActivityVO[] vos = getCurrSelectedItemPanel().getWorkFlowPanel().getActivityVOs();
						if (vos != null && vos.length > 0) {
							//为了防止复制错误，这里校验一下，如果当前流程有环节，则提示【李春娟/2015-07-28】
							if (!MessageBox.confirm(processpanel, "复制流程前会删除当前流程的所有环节及环节相关信息，是否继续?")) {
								return;
							}
						}
					}
					if (service == null) {
						service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
					}
					String xmlcontent = service.getXMLProcess(copy_processid);//写出流程图XML内容
					//写入流程图XML内容
					service.importXMLProcess(xmlcontent, currProcessid);
					//重新加载一下【李春娟/2014-09-22】
					if (getCurrSelectedItemPanel() != null) {
						getCurrSelectedItemPanel().reloadWorkFlow();
					}
					processDialog.closeMe();//关闭窗口
					processDialog.dispose();
				} catch (WLTRemoteException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		processDialog.setVisible(true);
	}

	/**
	 * 流程排序
	 */
	private void onSeqWf() {
		int count = radioBtnPanel.getComponentCount();
		String[][] process = new String[count][3];
		for (int i = 0; i < count; i++) {
			JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(i);
			process[i] = (String[]) radioBtnPanel.getClientProperty(radioBtn);
		}
		LookWfSeqDialog seqdialog = new LookWfSeqDialog(this, "流程排序", process);
		seqdialog.setVisible(true);
		if (seqdialog.getCloseType() == 1) {
			ArrayList sqllist = new ArrayList();
			String[] process_return = seqdialog.getReturnArray();
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					JRadioButton radioBtn = (JRadioButton) radioBtnPanel.getComponent(j);
					String[] process1 = (String[]) radioBtnPanel.getClientProperty(radioBtn);
					if (process1[0].equals(process_return[i])) {
						radioBtnPanel.add(radioBtn);
						if (i < 9) {//userdef04是排序字段
							sqllist.add("update pub_wf_process set userdef04='0" + (i + 1) + "' where id=" + process1[0]);
						} else {
							sqllist.add("update pub_wf_process set userdef04='" + (i + 1) + "' where id=" + process1[0]);
						}
						break;
					}
				}
			}
			radioBtnPanel.revalidate();
			try {
				UIUtil.executeBatchByDS(null, sqllist);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 如果某个按钮被选中，显示相应流程的同时将其他按钮设为未选中状态
	 * 
	 * @param radioBtn_selected
	 *            被选中的按钮
	 * @param _isSetSecondRadio
	 *            是否设置上次选中的radio
	 */
	protected void onSelectRadioBtn(JRadioButton radioBtn_selected, boolean _isSetSecondRadio) {
		radioBtn_selected.setSelected(true);
		if (radioBtn_selected != getCurrSelectedRadio() && _isSetSecondRadio) {
			this.setSecondLastSelectedRadio(getCurrSelectedRadio());
		}
		setCurrSelectedRadio(radioBtn_selected);// 设置当前选择的流程radio
		String[] process = (String[]) radioBtnPanel.getClientProperty(radioBtn_selected);
		if (!allSeletedWfId.contains(process[0])) {// 判断是否查看过,如果查看过就直接打开，否则，需要构建流程页面
			allSeletedWfId.add(process[0]);// 记录点击过的流程名
			WFGraphEditItemPanel itempanel = new WFGraphEditItemPanel(this.cmpfileid, this.cmpfilename, process[0], process[1], process[2], editable, false);
			itempanel.setGraphEditPanel(this);//这一行必须在下一行上面，将流程文件的所有流程图的设计面板设置一下，这样在new WFGraphEditDialog时可以给WFGraphEditPanel设置属性，通过WFGraphEditItemPanel获得属性进行不同操作【李春娟/2012-06-13】
			itempanel.setShowRefPanel(this.showRefPanel);
			billLevelPanel.addLevelPanel(process[0], itempanel); // 多层面板加入一层
		}
		billLevelPanel.showLevel(process[0]);
	}

	/**
	 * 判断是否有流程编辑了但未保存
	 * 
	 * @return
	 */
	public boolean isWfEditChanged() {
		if (!editable) {// 如果流程不可编辑，则直接返回false
			return false;
		}
		StringBuffer sb_msg = new StringBuffer("以下流程已发生变化,是否需要保存：\n");
		boolean ischanged = false;
		for (int i = 0; i < allSeletedWfId.size(); i++) {
			WFGraphEditItemPanel itemPanel = (WFGraphEditItemPanel) billLevelPanel.getLevelPanel((String) allSeletedWfId.get(i));
			if (itemPanel.isEditChanged()) {
				sb_msg.append("【" + itemPanel.getProcessname() + "】\n");
				ischanged = true;
			}
		}
		if (!ischanged) {
			return false;
		}
		if (MessageBox.confirm(parentContainer, sb_msg.toString())) {
			saveAllWf();//保存流程
		} else {
			deleteAllAddActivity();//删除新增环节的环节相关信息
		}
		return false;
	}

	/**
	 * 保存所有流程，只判断查看过的流程图
	 */
	public void saveAllWf() {
		try {
			long ll_1 = System.currentTimeMillis(); //
			for (int i = 0; i < allSeletedWfId.size(); i++) {
				WFGraphEditItemPanel itemPanel = (WFGraphEditItemPanel) billLevelPanel.getLevelPanel((String) allSeletedWfId.get(i));
				if (itemPanel.isEditChanged()) {
					itemPanel.onSaveWf(false);// 保存流程，不提示提示信息
				}
			}
			long ll_2 = System.currentTimeMillis(); //
			MessageBox.show(this, "保存成功,共耗时[" + (ll_2 - ll_1) + "]毫秒!"); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 删除所有流程中是增加状态的环节的相关信息，只判断看过的流程图
	 */
	public void deleteAllAddActivity() {
		StringBuffer sb_addActivityIds = new StringBuffer();
		for (int i = 0; i < allSeletedWfId.size(); i++) {
			WFGraphEditItemPanel itemPanel = (WFGraphEditItemPanel) billLevelPanel.getLevelPanel((String) allSeletedWfId.get(i));
			if (itemPanel.isEditChanged()) {
				ArrayList addActivityIds = itemPanel.getWorkFlowPanel().getAddActivityIds();
				if (addActivityIds.size() > 0) {
					sb_addActivityIds.append(tbutil.getInCondition(addActivityIds) + ",");
				}
			}
		}
		if (sb_addActivityIds.length() > 0) {
			try {
				service.deleteActivityRelationByActivityIds(sb_addActivityIds.substring(0, sb_addActivityIds.length() - 1));
			} catch (Exception e) {
				MessageBox.showException(parentContainer, e);
			}
		}
	}

	public void showLevel(String _processid) {
		if (allSeletedWfId.contains(_processid)) {
			billLevelPanel.showLevel(_processid);
		} else {
			Enumeration e = buttongroup.getElements();
			while (e.hasMoreElements()) {
				JRadioButton radioBtn = (JRadioButton) e.nextElement();
				Object obj = radioBtnPanel.getClientProperty(radioBtn);
				if (obj != null && _processid.equals(((String[]) obj)[0])) {
					onSelectRadioBtn(radioBtn, false);
					break;
				}
			}
		}
	}

	public int getWfProcessCount() {
		return buttongroup.getButtonCount();
	}

	/**
	 * 增加一个流程的JRadioButton【李春娟/2012-03-21】
	 * @param processID
	 * @param str_processcode
	 * @param str_processname
	 * @param _isSelectRadio		是否选中该按钮，如果只是初始化页面时，不需要选中，如果是后来的新增流程，则需要选中
	 * @param _isSetSecondRadio	是否设置上次选中的按钮，如果在打开界面未操作时不需要设置，如果再手动点击了其他的JRadioButton或新增了流程图，则需要设置
	 */
	public void onAddAndSelectRadioBtn(String processID, String str_processcode, String str_processname, boolean _isSelectRadio, boolean _isSetSecondRadio) {
		JRadioButton radioBtn = new JRadioButton(str_processname); //
		radioBtn.setToolTipText(str_processname);
		radioBtn.setOpaque(false);// 设置为透明
		radioBtn.addActionListener(this);
		radioBtnPanel.add(radioBtn);
		radioBtnPanel.putClientProperty(radioBtn, new String[] { processID, str_processcode, str_processname });
		buttongroup.add(radioBtn);
		if (_isSelectRadio) {
			onSelectRadioBtn(radioBtn, _isSetSecondRadio);// 设置radio按钮为选中状态,并执行一下按钮选中的逻辑	
			onRadioBtnPanelResized();
		}
	}

	public boolean isShowRefPanel() {
		return showRefPanel;
	}

	public void setShowRefPanel(boolean _showRefPanel) {
		if (this.showRefPanel == _showRefPanel) {
			return;
		}
		this.showRefPanel = _showRefPanel;

		Hashtable hashtable = billLevelPanel.getHt_levels();
		String[] keys = (String[]) hashtable.keySet().toArray(new String[0]);
		for (int i = 0; i < keys.length; i++) {
			WFGraphEditItemPanel itempanel = (WFGraphEditItemPanel) billLevelPanel.getLevelPanel(keys[i]);
			itempanel.setShowRefPanel(showRefPanel);
		}
	}

	public void setDividerLocation(int dividerLocation) {
		this.dividerLocation = dividerLocation;
	}

	/**
	 * 流程按钮面板新增自定义按钮【李春娟/2012-04-10】
	 * @param _list
	 */
	public void addWfBtn(ArrayList _list) {
		int width = dividerLocation;
		for (int i = 0; i < _list.size(); i++) {
			JButton btn = (JButton) (_list.get(i));
			width += btn.getPreferredSize().getWidth();
			wfBtnPanel.add(btn);
		}
		this.setDividerLocation(width);
	}

	public String getCmpfileid() {
		return cmpfileid;
	}

	public JPanel getWfBtnPanel() {
		return wfBtnPanel;
	}

	public ButtonGroup getButtongroup() {
		return buttongroup;
	}
}
