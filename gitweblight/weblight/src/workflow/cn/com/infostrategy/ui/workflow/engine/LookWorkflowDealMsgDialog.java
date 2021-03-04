package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * 至关重要的一个类! 工作流意见全览,包括打印!!
 * 以前工作流一直有个硬伤就是最终输出时总是有客户感觉还不够人性化!
 * 原因就在于流程意见输出非常强调：一眼清,只想看关键环节,只想看关键角色,只想看最终意见等一系列需求...
 * 最终打印时还要将表单的主要字段与流程主要环节与角色的意见拼在一起输出!! 还要将左边的环节名称自动转变成实际机构名称!
 * 将相同部门的意见进行合并...
 * 
 * 现在想彻底搞定所谓流程意见输出不够形象之问题,这里一种有我种表达方式:
 * 1.大文本框输出 
 * 2.
 * @author Administrator
 *
 */
public class LookWorkflowDealMsgDialog extends BillDialog implements ActionListener {

	private BillVO[] billVOs = null; //所有的数据,即原来列表中的所有数据! 如果是勾选的,则就是选中的几条!
	private String reportTitle = null; //报表标题
	private String prinstanceId = null; //流程实例ID

	private ArrayList list_vos = null; //

	private WLTButton btn_confirm; //以后还可能扩展其他按钮..

	public LookWorkflowDealMsgDialog(Container _parent, BillVO[] _billVOs, String _reportTitle, String _prinstanceId) {
		super(_parent, "意见全览", 850, 700);
		if (ClientEnvironment.isAdmin()) {
			//在窗口标题上直接打印出类名,是为了开发人员调试时快速知道是哪个类,然后在Eclipse以最快速度找到代码所在地方!从而提高修改问题效率!!否则只能一步步的跟踪,不熟悉平台代码的人光找到这个地方可能都要半天...
			this.setTitle(this.getTitle() + " 【LookWorkflowDealMsgDialog】"); //
		}
		this.billVOs = _billVOs; //
		this.reportTitle = _reportTitle; //
		this.prinstanceId = _prinstanceId; //
		initialize(); //初始化页面
	}

	//初始化页面..
	private void initialize() {
		WLTTabbedPane tabb = new WLTTabbedPane(); //多页签!

		tabb.addTab("所有意见之文本风格", UIUtil.getImage("office_172.gif"), getTextPanel()); //
		tabb.addTab("最后意见之文本风格", UIUtil.getImage("office_174.gif"), getTextPanel2()); //
		tabb.addTab("所有意见之表格风格", UIUtil.getImage("office_070.gif"), getCellPanel()); //
		tabb.addTab("最后意见之表格风格", UIUtil.getImage("office_071.gif"), getCellPanel2()); //
		//tabb.addTab("Html风格", UIUtil.getImage("office_062.gif"), getHtmlPanel()); //以后再弄..

		this.getContentPane().add(tabb, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getTextPanel() {
		return getTextPanel(getDealMsgList()); //
	}

	private JPanel getTextPanel2() {
		return getTextPanel(getDealMsgDistinctList()); //
	}

	/**
	 * 文本框导出!!!
	 * @return
	 */
	private JPanel getTextPanel(ArrayList list) {
		StringBuilder sb_text = new StringBuilder(); ////
		for (int i = 0; i < list.size(); i++) {
			ArrayList al_row = (ArrayList) list.get(i); //

			BillVO firstVO = (BillVO) al_row.get(0); //
			String str_deptName = firstVO.getStringValue("participant_userdeptname"); //部门名称
			sb_text.append("-------------------- 【" + str_deptName + "】 -------------------\r\n"); //

			for (int j = 0; j < al_row.size(); j++) { //
				BillVO billVO = (BillVO) al_row.get(j); //
				String str_userName = billVO.getStringValue("participant_username"); //
				sb_text.append("处理人:" + str_userName); //

				int li_textLength = TBUtil.getTBUtil().getStrUnicodeLength(str_userName); //
				int li_appendlength = 26 - li_textLength; //
				for (int k = 0; k < li_appendlength; k++) { //这样做是为了时间对齐...
					sb_text.append(" ");
				}
				sb_text.append("处理时间:" + billVO.getStringValue("submittime") + "\r\n"); //
				sb_text.append("处理意见:" + billVO.getStringValue("submitmessage", "") + "\r\n\r\n"); //
			}

			sb_text.append("\r\n\r\n"); //
		}

		JPanel panel = new JPanel(new BorderLayout()); //
		JTextArea textArea = new WLTTextArea(sb_text.toString()); //
		textArea.setBackground(new Color(250, 253, 250)); //
		textArea.setLineWrap(true); //
		textArea.setEditable(false); //

		panel.add(new JScrollPane(textArea)); //
		return panel; //
	}

	private JPanel getCellPanel() {
		return getCellPanel(getDealMsgList()); //
	}

	private JPanel getCellPanel2() {
		return getCellPanel(getDealMsgDistinctList()); //唯一性处理...
	}

	/**
	 * 使用BillCellPanel创建表格输出...
	 * 这里关键是做了分组合并处理,让人感觉很形象,而且右以导出Excel与Html
	 */
	private JPanel getCellPanel(ArrayList list) {
		int li_count = 0; //
		for (int i = 0; i < list.size(); i++) {
			ArrayList al_row = (ArrayList) list.get(i); //
			li_count = li_count + al_row.size(); //
		}

		//
		BillCellItemVO[][] itemVOs = new BillCellItemVO[li_count * 2 + 1][5]; //
		for (int i = 0; i < itemVOs.length; i++) {
			for (int j = 0; j < 5; j++) {
				itemVOs[i][j] = new BillCellItemVO(); //
				itemVOs[i][j].setCelltype("TEXT"); //
				itemVOs[i][j].setCellvalue(""); //
				itemVOs[i][j].setRowheight("25"); //
			}
		}

		//标题
		itemVOs[0][0].setCellvalue(this.reportTitle); //
		itemVOs[0][0].setHalign(2); //
		itemVOs[0][0].setCelltype("TEXT"); //
		itemVOs[0][0].setSpan("1,5"); //
		itemVOs[0][0].setRowheight("35"); //
		itemVOs[0][0].setBackground("255,255,220"); //
		itemVOs[0][0].setForeground("0,0,255"); //

		//下面的数据
		String str_label_width = "100"; //
		String str_text_width = "150"; //
		int li_col1_pos = 1; //
		String[] str_spanDeptColor = new String[] { "255,240,255", "233,233,243" }; //
		for (int i = 0; i < list.size(); i++) {
			ArrayList list_row = (ArrayList) list.get(i); //
			BillVO firstVO = (BillVO) list_row.get(0); //

			int li_rowSpan = list_row.size() * 2; //
			itemVOs[li_col1_pos][0].setSpan(li_rowSpan + ",1"); //
			itemVOs[li_col1_pos][0].setColwidth("100"); //
			itemVOs[li_col1_pos][0].setBackground(str_spanDeptColor[i % 2]); //
			itemVOs[li_col1_pos][0].setCelltype("TEXTAREA"); //
			itemVOs[li_col1_pos][0].setHalign(2); //
			itemVOs[li_col1_pos][0].setValign(2); //

			String str_deptName = firstVO.getStringValue("participant_userdeptname"); //
			str_deptName = TBUtil.getTBUtil().replaceAll(str_deptName, "-", "\r\n"); //
			itemVOs[li_col1_pos][0].setCellvalue(str_deptName); //

			//遍历各个VO
			for (int j = 0; j < list_row.size(); j++) { //
				BillVO billVO = (BillVO) list_row.get(j); //

				//处理人:
				itemVOs[li_col1_pos + j * 2][1].setCellvalue("处理人"); //
				itemVOs[li_col1_pos + j * 2][1].setHalign(3); //
				itemVOs[li_col1_pos + j * 2][1].setColwidth(str_label_width); //
				itemVOs[li_col1_pos + j * 2][1].setBackground("217,255,217"); //

				//dealuser content
				itemVOs[li_col1_pos + j * 2][2].setColwidth(str_text_width); //
				itemVOs[li_col1_pos + j * 2][2].setCellvalue(billVO.getStringValue("participant_username")); //
				itemVOs[li_col1_pos + j * 2][2].setBackground("252,252,252"); //

				//处理时间
				itemVOs[li_col1_pos + j * 2][3].setCellvalue("处理时间"); //
				itemVOs[li_col1_pos + j * 2][3].setHalign(3); //
				itemVOs[li_col1_pos + j * 2][3].setColwidth(str_label_width); //
				itemVOs[li_col1_pos + j * 2][3].setBackground("217,255,217"); //

				//
				itemVOs[li_col1_pos + j * 2][4].setColwidth(str_text_width); //
				itemVOs[li_col1_pos + j * 2][4].setCellvalue(billVO.getStringValue("submittime")); //
				itemVOs[li_col1_pos + j * 2][4].setBackground("252,252,252"); //

				//处理意见:
				itemVOs[li_col1_pos + j * 2 + 1][1].setCelltype("TEXTAREA"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setHalign(1); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setValign(1); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setSpan("1,4"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setRowheight("70"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setColwidth("20"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setCellvalue("处理意见：" + billVO.getStringValue("submitmessage", "")); //
			}

			li_col1_pos = li_col1_pos + li_rowSpan; //下一次的位置!
		}

		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setCellItemVOs(itemVOs); //
		cellVO.setRowlength(itemVOs.length); //
		cellVO.setCollength(5); //

		BillCellPanel cellPanel = new BillCellPanel(cellVO); //
		cellPanel.setCrystal(true); //

		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(cellPanel); //

		WLTButton btn_exportExcel = new WLTButton("导出Excel", "icon_xls.gif"); //
		WLTButton btn_exportHtml = new WLTButton("导出Html", "office_136.gif"); //

		btn_exportExcel.putClientProperty("BindCellPanel", cellPanel); //
		btn_exportHtml.putClientProperty("BindCellPanel", cellPanel); //

		btn_exportExcel.addActionListener(this); //
		btn_exportHtml.addActionListener(this); //

		JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT)); //
		btnPanel.add(btn_exportExcel); //
		btnPanel.add(btn_exportHtml); //
		contentPanel.add(btnPanel, BorderLayout.NORTH); //
		return contentPanel; //
	}

	/**
	 * 处理意见清单...
	 * 一行是需要合并的,每行又是一个List
	 * @return
	 */
	private ArrayList getDealMsgList() {
		if (list_vos != null) {
			return list_vos; //
		}
		list_vos = new ArrayList(); //
		for (int i = 0; i < billVOs.length; i++) {
			if (i == 0) { //如果是第一个,则直接塞入
				ArrayList list_row = new ArrayList(); //
				list_row.add(billVOs[i]); //
				list_vos.add(list_row); //
			} else {
				String str_thisActName = billVOs[i].getStringValue("participant_userdeptname"); //
				String str_lastActName = billVOs[i - 1].getStringValue("participant_userdeptname"); //前一个!
				if (str_thisActName != null && str_thisActName.equals(str_lastActName)) { //如果与前面一样!
					ArrayList list_row = (ArrayList) list_vos.get(list_vos.size() - 1); //
					list_row.add(billVOs[i]); //
				} else { //如果发生不一样,则新建!
					ArrayList list_row = new ArrayList(); //
					list_row.add(billVOs[i]); //
					list_vos.add(list_row); //
				}
			}
		}
		return list_vos; //
	}

	/**
	 * 取得唯一性的清单...即一个部门只会出现一次,一个部门内部的一个人员也只会出现一次!
	 * @return
	 */
	private ArrayList getDealMsgDistinctList() {
		LinkedHashMap deptMap = new LinkedHashMap(); //用来判断是否只出现过一次!
		for (int i = 0; i < billVOs.length; i++) { //
			String str_deptname = billVOs[i].getStringValue("participant_userdeptname"); //机构名称!!!
			String str_userName = billVOs[i].getStringValue("participant_username"); //人员名称!!!
			if (deptMap.containsKey(str_deptname)) { //如果已注册
				LinkedHashMap userMap = (LinkedHashMap) deptMap.get(str_deptname); //
				if (!userMap.containsKey(str_userName)) { //如果这个人员不存在
					userMap.put(str_userName, billVOs[i]); //则加入,如果已有,则什么都不做
				}
			} else { //如果机构都不存在!
				LinkedHashMap userMap = new LinkedHashMap(); //
				userMap.put(str_userName, billVOs[i]); //
				deptMap.put(str_deptname, userMap); //
			}
		}

		ArrayList list_dist = new ArrayList(); //
		LinkedHashMap[] list_Maps = (LinkedHashMap[]) deptMap.values().toArray(new LinkedHashMap[0]); //
		for (int i = 0; i < list_Maps.length; i++) {
			BillVO[] vos = (BillVO[]) list_Maps[i].values().toArray(new BillVO[0]); //所有VO
			ArrayList al_row = new ArrayList(); //
			for (int j = 0; j < vos.length; j++) {
				al_row.add(vos[j]); //
			}
			list_dist.add(al_row); //
		}

		return list_dist; //
	}

	/**
	 * 使用标准的工作流报表输出,导出Html...
	 * @return
	 */
	private JPanel getHtmlPanel() {
		return new JPanel(); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else {
			WLTButton btn = (WLTButton) e.getSource();
			BillCellPanel cellPanel = (BillCellPanel) btn.getClientProperty("BindCellPanel"); //
			if (cellPanel != null) {
				if (btn.getText().equals("导出Excel")) {
					cellPanel.exportExcel("流程处理意见"); //
				} else if (btn.getText().equals("导出Html")) {
					cellPanel.exportHtml("流程处理意见"); //
				}
			}
		}
	}

}
