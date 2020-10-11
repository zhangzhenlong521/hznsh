package cn.com.infostrategy.ui.sysapp.dataaccess;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.styletemplet.t06.AbstractStyleWorkPanel_06;

/**
 * 数据权限配置!!!
 * @author xch
 *
 */
public class DataPolicyConfigWKPanel extends AbstractStyleWorkPanel_06 {

	private static final long serialVersionUID = 1L;

	@Override
	public String getParentTempletCode() {
		return "PUB_DATAPOLICY_CODE1";
	}

	@Override
	public String getChildTempletCode() {
		return "PUB_DATAPOLICY_B_CODE1";
	}

	@Override
	public String getParentAssocField() {
		return "id";
	}

	@Override
	public String getChildAssocField() {
		return "datapolicy_id";
	}

	@Override
	public void afterInitialize() throws Exception {
		//主表加按钮!
		BillListPanel parentList = getParentBillListPanel(); //
		WLTButton btn_testCorp = new WLTButton("演练策略"); //
		btn_testCorp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTestCorpPolicy(); //
			}
		}); //
		parentList.addBatchBillListButton(new WLTButton[] { btn_testCorp }); //
		parentList.repaintBillListButton(); //

		//子表加按钮!!
		BillListPanel billList = getChildBillListPanel(); //
		WLTButton btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
		WLTButton btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
		WLTButton btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE, "保存顺序"); //
		billList.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown, btn_save }); //
		billList.repaintBillListButton(); //

	}

	protected void onTestCorpPolicy() {
		try {
			BillListPanel parentList = getParentBillListPanel(); //
			BillVO billVO = parentList.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "请选择一条策略进行此操作!"); //
				return; //
			}
			String str_policyName = billVO.getStringValue("name"); //策略名称!!

			JLabel label_1 = new JLabel("人员编码:", SwingConstants.RIGHT); //
			JTextField textfield_1 = new JTextField(ClientEnvironment.getCurrLoginUserVO().getCode()); //
			JLabel label_2 = new JLabel("是否计算附加SQL:", SwingConstants.RIGHT); //
			JCheckBox checkBox = new JCheckBox(); //
			checkBox.setSelected(true); //
			String str_toolTip = "如果附加SQL中的字段不是机构表中的,会报错!即只有该策略就是针对机构的才会成功!"; //
			label_2.setToolTipText(str_toolTip); //
			checkBox.setToolTipText(str_toolTip); //

			label_1.setBounds(5, 5, 100, 20); //
			textfield_1.setBounds(110, 5, 100, 20); //
			label_2.setBounds(5, 30, 100, 20); //
			checkBox.setBounds(110, 30, 40, 20); //

			JPanel panel = new JPanel(null); //
			panel.add(label_1); //
			panel.add(textfield_1); //
			panel.add(label_2); //
			panel.add(checkBox);
			panel.setPreferredSize(new Dimension(225, 60)); //

			if (JOptionPane.showConfirmDialog(this, panel, "请输入一个人员编码,计算出它的机构权限", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			String str_userCode = textfield_1.getText(); //008834

			String[][] str_userIDs = UIUtil.getStringArrayByDS(null, "select id,name from pub_user where code='" + str_userCode + "'"); //
			if (str_userIDs == null || str_userIDs.length <= 0) {
				MessageBox.show(this, "根据人员编码[" + str_userCode + "]没有找到一个人员,不能继续计算!"); //
				return; //
			}

			if (str_userIDs.length > 1) {
				MessageBox.show(this, "根据人员编码[" + str_userCode + "]共找到【" + str_userIDs.length + "】个人员,不能继续计算!"); //
				return; //
			}

			TBUtil tbUtil = new TBUtil(); //
			String str_userId = str_userIDs[0][0]; //人员id
			String str_userName = str_userIDs[0][1]; //人员名称!
			String str_title = "人员[" + str_userId + "/" + str_userCode + "/" + str_userName + "]执行策略【" + str_policyName + "】的结果,是否计算附加SQL(即拿实际完整返回值计算)=[" + checkBox.isSelected() + "]"; //

			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			String[] str_result = service.getDataPolicyCondition(str_userId, str_policyName, 1, "id", null); // 

			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO("PUB_CORP_DEPT_CODE1"); // 取得元原模板主表
			templetVO.setDatapolicy(null); //策略名称
			templetVO.setBsdatafilterclass(null); // 
			templetVO.setDataconstraint(null); //
			BillTreePanel treePanel = new BillTreePanel(templetVO); //
			treePanel.getBtnPanel().setVisible(false); //
			if (str_result.length == 5) {
				String str_info = str_result[0]; //
				String str_realSql = str_result[1]; //
				String str_virtualCorpIds = str_result[2]; //虚拟结点!
				String str_allCorpIds = str_result[3]; //
				String str_isAllCorp = str_result[4]; //

				if (str_virtualCorpIds != null) { //如果有虚拟结点!
					str_virtualCorpIds = str_virtualCorpIds.substring(str_virtualCorpIds.indexOf("(") + 1, str_virtualCorpIds.indexOf(")")); //
				}

				String str_treeSql = null; // 
				if (checkBox.isSelected()) { //如果是勾选上的
					if (str_virtualCorpIds != null) { //如果有虚拟结点!
						str_treeSql = "(" + str_realSql + ") or id in (" + str_virtualCorpIds + ")"; //
					} else {
						str_treeSql = str_realSql; //
					}
					if (str_virtualCorpIds != null) { //如果有虚拟结点
						String[] str_virtualIds = tbUtil.split(str_virtualCorpIds, ","); //
						treePanel.getVirtualCorpIdsHst().addAll(Arrays.asList(str_virtualIds)); //先加入!
					}
				} else {

					if (str_isAllCorp.equals("Y")) { //如果是所有数据!
						str_treeSql = "1=1"; //
					} else { //如果不是所有数据
						String[] str_idArray = tbUtil.split(str_allCorpIds, ";"); //
						if (str_virtualCorpIds != null) { //如果有虚拟结点!
							str_treeSql = " id in (" + tbUtil.getInCondition(str_idArray) + ") or id in (" + str_virtualCorpIds + ")"; //
						} else {
							str_treeSql = " id in (" + tbUtil.getInCondition(str_idArray) + ")"; //
						}
					}

					if (str_virtualCorpIds != null) { //如果有虚拟结点
						String[] str_virtualIds = tbUtil.split(str_virtualCorpIds, ","); //
						treePanel.getVirtualCorpIdsHst().addAll(Arrays.asList(str_virtualIds)); //先加入!
					}
				}

				JTabbedPane tabbedPanel = new JTabbedPane();
				try {
					treePanel.queryDataByCondition(str_treeSql); //真正查询!!!
					tabbedPanel.addTab("机构过滤", treePanel); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
					tabbedPanel.addTab("机构过滤", new JLabel("查询发生异常:" + ex.getMessage() + ",请控制台查看详细!")); //
				}

				JTextArea textArea_1 = getTextArea(); //
				textArea_1.setText(str_info);
				tabbedPanel.addTab("详细计算过程", new JScrollPane(textArea_1)); //

				JTextArea textArea_2 = getTextArea(); //

				textArea_2.setText("策略最终实际返回SQL,它可能加上后面的附加SQL将对应一个业务表,并不一定对应机构:\r\n" + str_realSql + "\r\n\r\n本机构树实际执行的SQL" + (str_virtualCorpIds == null ? "" : "【自动加上了虚拟机构结点】") + "：\r\n" + str_treeSql);
				tabbedPanel.addTab("实际返回SQL", new JScrollPane(textArea_2)); //

				JTextArea textArea_3 = getTextArea(); //
				textArea_3.setText("返回的虚拟结点:【" + str_virtualCorpIds + "】,是否是所有机构【" + str_isAllCorp + "】,返回的机构id清单:\r\n" + str_allCorpIds);
				tabbedPanel.addTab("返回的机构Id清单", new JScrollPane(textArea_3)); //

				BillDialog dialog = new BillDialog(this, str_title, 900, 550); //
				dialog.getContentPane().add(tabbedPanel); //
				dialog.setVisible(true); //显示窗口!!
			} else {
				if (str_result[1].equals("'全部机构'='全部机构'") || str_result[1].equals("99=99")) {
					treePanel.queryDataByCondition(str_result[1]); //真正查询!!!

					JTabbedPane tabbedPanel = new JTabbedPane(); //
					tabbedPanel.addTab("机构过滤", treePanel); //

					JTextArea textArea_1 = getTextArea(); //
					textArea_1.setText(str_result[0]);
					tabbedPanel.addTab("详细计算过程", new JScrollPane(textArea_1)); //

					JTextArea textArea_2 = getTextArea(); //
					textArea_2.setText("策略最终实际返回SQL:\r\n" + str_result[1] + "\r\n\r\n本机构树实际执行的SQL：\r\n" + str_result[1]);
					tabbedPanel.addTab("实际返回SQL", new JScrollPane(textArea_2)); //

					BillDialog dialog = new BillDialog(this, str_title, 900, 550); //
					dialog.getContentPane().add(tabbedPanel); //
					dialog.setVisible(true); //显示窗口!!
				} else {
					MessageBox.show(this, "没有返回值,可能该策略没有配置明细或没有匹配上一个主体!\r\n具体执行结果是:\r\n" + str_result[0] + "\r\n\r\n返回的SQL条件是:\r\n" + str_result[1]); //
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private JTextArea getTextArea() {
		JTextArea textArea = new WLTTextArea(); //
		textArea.setOpaque(true); //
		textArea.setBackground(LookAndFeel.systembgcolor); //
		textArea.setWrapStyleWord(true); //
		textArea.setEditable(false); //
		return textArea;
	}
}
