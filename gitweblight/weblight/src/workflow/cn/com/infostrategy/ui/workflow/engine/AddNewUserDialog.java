package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 工作流中选择参与时者,可以手工添加新的人员!! 即自由增加新的参与者!!
 * 这个功能非常重要,因为以后的自由流程就靠这个来实现!! 以前我们都是约束流程,结果流程中最常见的问题就是找不到对应的参与者!!然后给我们实施带来巨大的挑战!!
 * 实际上,只是有这个自由流程的功能后,大部分流程都不需要约束参与者了,我们的开发与实施工作量都大为减轻!! 再加上自循环功能,更能简化流程环节!! 从而使工作流应用变得越来越轻松!!
 * 所以说,自由流程,自循环,再加上：人工选工流程启动(不设业务类型),省去单据类型与业务类型维护,直接在模板中分配流程,友情提示流程四个字段,首页待办任务的健壮,等等这些功能都将大为简化流程开发与实施!  
 * @author Administrator
 *
 */
public class AddNewUserDialog extends BillDialog implements BillTreeSelectListener, ActionListener, ChangeListener {

	private static final long serialVersionUID = -2457287774661601679L;
	private BillTreePanel billTree_corp = null; //机构树!
	private BillListPanel billList_user = null; //人员列表,主要字段有,userid,username,人员角色id,人员角色名称,显示的就三列
	private BillListPanel billList_user_shopcar = null; //购物车中的人员!!
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser, btn_SuperSearch; //增加购物车人员,删除购物车人员!
	private WLTTabbedPane tab = null;
	private BillVO billVO = null; //业务单据上的BillVO对象!!
	private WLTButton btn_confirm, btn_cancel; //
	private int closeType = -1; //
	private ChooseUserByGroupPanel cubgp = null;
	private BillVO returnCorpVO = null; //返回的机构VO
	private BillVO[] returnUserVOs = null; //
	private boolean ishavegroup = false;

	boolean isShopCarUI = false; //是否是购物车的界面风格!! 因为南京油运客户死活要求像他们OA一样使用"购物车"风格一下子添加所有人员!!

	//增加人员!!
	public AddNewUserDialog(Container _parent, BillVO _billVO) {
		super(_parent, 900, 500); //
		this.billVO = _billVO; //
		isShopCarUI = new TBUtil().getSysOptionBooleanValue("工作流添加人员是否购物车风格", false); //从系统参数取!!
		this.initialize(); //
		if (isShopCarUI) { //如果是购物车风格界面,则高度要变大点!!
			this.setSize(910, 650); //
			this.locationToCenterPosition(); //界面居中!
		}

	}

	/**
	 * 初始化界面!!!
	 * 左边是个机构树,右边是个人员清单(包括角色id,角色编码,角色名称!)
	 */
	private void initialize() {
		try {
			billList_user = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml")); //人员列表,自动带出角色!
			billTree_corp = new BillTreePanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/PUB_CORP_DEPT_1.xml")); //创建机构树
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("工作流提交时添加接收者是否显示高级查找按钮", true)) {
				btn_SuperSearch = WLTButton.createButtonByType(WLTButton.COMM, "高级查找");
				btn_SuperSearch.addActionListener(this);
				billTree_corp.addBillTreeButton(btn_SuperSearch);
				billTree_corp.repaintBillTreeButton();
			}

			HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$本机构"); //找到本机构 默认是这样然后在树上增加一个超级查询的按钮可以根据机构类型、扩展类型、角色等来找总的范围有数据权限
			String str_zhid = null; //有时需要永远把总行带出来！
			boolean isq = false;
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("工作流提交时添加接收者机构是否按照数据权限查找", false)) {
				try {
					HashMap _parMap = new HashMap();
					_parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					_parMap.put("datapolicy", "工作流选人机构数据权限");
					_parMap.put("returnCol", "id");
					HashMap corpmap = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.DataPolicyDMO", "getDataPolicyTargetCorpsByUserId", _parMap);
					String[] ids = (String[]) corpmap.get("AllCorpIDs");
					if (ids != null && ids.length > 0) {
						billTree_corp.queryDataByDS(null, "select id,code,name,parentid,seq from pub_corp_dept where id in (" + TBUtil.getTBUtil().getInCondition(ids) + ")");
						isq = true;
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					isq = false;
				}
			}
			if (!isq) {
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("工作流提交时添加接收者是否永远包括总行", false)) { //
					str_zhid = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='总行'"); //总行ID
				}
				if (hvs_mycorp != null) { //如果找到本机构!!
					String str_myParentCorpId = hvs_mycorp[0].getStringValue("id"); //
					String str_condition = null; //二次过滤条件！！！
					if (str_zhid == null) { //如果不带总行!!
						str_condition = "id = " + str_myParentCorpId + ""; //
					} else { //如果有总行!!
						str_condition = "id in (" + str_myParentCorpId + "," + str_zhid + ")"; //
					}
					HashVO[] hvs_myAllChildrens = UIUtil.getHashVoArrayAsTreeStructByDS(null, "select id,code,name,parentid,seq from pub_corp_dept", "id", "parentid", "seq", str_condition); //
					billTree_corp.queryDataByDS(hvs_myAllChildrens, -1); //
				} else { //如果找不到本机构!
					billTree_corp.queryDataByDS(null, "select id,code,name,parentid,seq from pub_corp_dept"); //查询所有机构树！！！这里必须过滤一下！！	
				}
			}
			String str_billType = null; //
			String str_busiType = null; //
			HashMap parMap = new HashMap(); //
			str_billType = billVO.getStringValue("BILLTYPE"); //单据类型!
			str_busiType = billVO.getStringValue("BUSITYPE"); //业务类型!
			parMap.put("billtype", str_billType); //单据类型
			parMap.put("busitype", str_busiType); //业务类型
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsers", parMap); //去服务器端取得登录人员的机构,以及该机构下的所有其他人员!!关于授权以前有个Bug,以前没有根据单据类型与业务类型重新计算一下审批模式,结果导致在提交界面有授权计算,而在点击"接收人员"时却没有!!
			if (returnMap != null) { //如果有值
				String str_loginUserDeptid = (String) returnMap.get("userCorp"); //取得机构!
				if (str_loginUserDeptid != null) { //如果登录人员的所属机构不为空,则自动滚动到当前人员的所在机构,同时立即查询出右边的人员!!
					DefaultMutableTreeNode currUserNode = billTree_corp.findNodeByKey(str_loginUserDeptid); //
					if (currUserNode != null) {
						billTree_corp.scrollToOneNode(currUserNode); //
					}
				}
				HashVO[] hvsUser = (HashVO[]) returnMap.get("sameCorpUsers"); //根据上面远程方法取得的人员列表
				if (hvsUser != null && hvsUser.length > 0) { //如果有数据,则加载数据!!!
					putUserDataByHashVO(hvsUser); //将人员置入
				}
			}
			billTree_corp.addBillTreeSelectListener(this); //监听机构树选择变化事件!!!
			ishavegroup = TBUtil.getTBUtil().getSysOptionBooleanValue("工作流添加人员是否按群组选择", true);
			if (!isShopCarUI) { //如果不是购物车风格
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, billList_user); //分割，左边是机构,右边是人员!! 南京油运客户死活要求该界面要搞成"购物车"的样子!!
				splitPanel.setDividerLocation(250); //
				if (ishavegroup) {
					tab = new WLTTabbedPane();
					tab.addTab("按部门选择", UIUtil.getImage("office_014.gif"), splitPanel);
					tab.addTab("按群组选择", UIUtil.getImage("user.gif"), new JPanel());
					tab.addChangeListener(this);
					this.getContentPane().add(tab, BorderLayout.CENTER);
				} else {
					this.getContentPane().add(splitPanel, BorderLayout.CENTER);
				}
			} else { //如果是购物车风格,则是三个框!!!
				billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone()); //直接使用人员表的模板创建,这样会少一次远程访问!!
				billList_user_shopcar.setTitleLabelText("已选择的人员"); //
				btn_addShopCarUser = new WLTButton("添加", "office_059.gif"); //
				btn_delShopCarUser = new WLTButton("删除", "office_081.gif"); //
				btn_addAllShopCarUser = new WLTButton("全部添加", "office_160.gif"); //
				btn_delAllShopCarUser = new WLTButton("全部删除", "office_125.gif"); //
				btn_addShopCarUser.setToolTipText("将上方选中的人员添加进来"); //
				btn_delShopCarUser.setToolTipText("将下方已选择的人员删除掉"); //
				btn_addShopCarUser.addActionListener(this); //
				btn_delShopCarUser.addActionListener(this); //
				btn_addAllShopCarUser.addActionListener(this); //全部添加!
				btn_delAllShopCarUser.addActionListener(this); //全部删除!
				billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser }); //
				billList_user_shopcar.repaintBillListButton(); //刷新按钮!!!
				WLTSplitPane splitPanel_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar); //上下分割,上面是某机构所有人员,下面是购物车中的人员!!
				splitPanel_2.setDividerLocation(365); //
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, splitPanel_2); //分割，左边是机构,右边是人员!! 南京油运客户死活要求该界面要搞成"购物车"的样子!!
				splitPanel.setDividerLocation(250); //
				if (ishavegroup) {
					tab = new WLTTabbedPane();
					tab.addTab("按部门选择", UIUtil.getImage("office_014.gif"), splitPanel);
					tab.addTab("按群组选择", UIUtil.getImage("user.gif"), new JPanel());
					tab.addChangeListener(this);
					this.getContentPane().add(tab, BorderLayout.CENTER);
				} else {
					this.getContentPane().add(splitPanel, BorderLayout.CENTER);
				}
			}

			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER)); //
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //

		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel; //
	}

	/**
	 * 机构树选择变化
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billList_user.getRowCount() > 0) {
			billList_user.clearTable(); //先清空右边表格!!
		}
		if (_event.getSource() == billTree_corp) {
			BillVO billVO = _event.getCurrSelectedVO(); //
			if (billVO != null) {
				String str_corpId = billVO.getStringValue("id"); //机构id
				HashVO[] hvs = queryUserByCorpId(str_corpId); // 
				putUserDataByHashVO(hvs); //送入数据!!!
			}
		}
	}

	/**
	 * 根据机构id刷新人员清单!!
	 */
	private HashVO[] queryUserByCorpId(String _corpId) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("corpid", _corpId); //
			String str_billType = billVO.getStringValue("BILLTYPE"); //单据类型!
			String str_busiType = billVO.getStringValue("BUSITYPE"); //业务类型!
			parMap.put("billtype", str_billType); //单据类型
			parMap.put("busitype", str_busiType); //业务类型
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersBycorpId", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("sameCorpUsers"); //进行远程调用,找到某个机构下的所有人员(带出角色)
			return hvs; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//根据查询的数据,置入到页面中!!!
	private void putUserDataByHashVO(HashVO[] _hvs) {
		billList_user.clearTable(); //先清空右边表格!!
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = billList_user.newRow(false); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept"); //机构id
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname"); //机构名称

				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid"); //授权人id
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode"); //授权人编码
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername"); //授权人名称!!

			}
			billList_user.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (_event.getSource() == btn_cancel) {
			onCancel(); //
		} else if (_event.getSource() == btn_addShopCarUser) { //往购物车添加人员!!
			onAddShopCarUser(); //
		} else if (_event.getSource() == btn_delShopCarUser) { //从购物车移除人员!!
			onDelShopCarUser(); //
		} else if (_event.getSource() == btn_addAllShopCarUser) { //全部添加
			onAddAllShopCarUser(); //
		} else if (_event.getSource() == btn_delAllShopCarUser) { //全部删除
			onDelAllShopCarUser(); //
		} else if (_event.getSource() == btn_SuperSearch) {
			onSuperSearch();
		}
	}

	/**
	 * 确认!!
	 */
	private void onConfirm() {

		if (ishavegroup && tab.getSelectedIndex() == 1) {
			if (cubgp.check(this)) {
				returnUserVOs = cubgp.getRtnVOS();
				closeType = 1;
				this.dispose();
			}
		} else {
			BillVO[] selVOS = null; //选中的人员!!!!
			if (!isShopCarUI) { //如果不是购物车风格!!
				selVOS = billList_user.getSelectedBillVOs(); //则从第一个人员框中取
			} else { //如果是购物车风格!!
				selVOS = billList_user_shopcar.getAllBillVOs(); //则从第二个人员框中取!!!
			}
			if (selVOS == null || selVOS.length == 0) {
				MessageBox.show(this, "请选择一个人员!"); //
				return; //
			}

			HashSet hst_allaccrUser = new HashSet(); //
			for (int i = 0; i < billList_user.getRowCount(); i++) {
				String str_accruser = billList_user.getRealValueAtModel(i, "accruserid"); //
				if (str_accruser != null) {
					hst_allaccrUser.add(str_accruser); //
				}
			}

			StringBuilder sb_allAccrUserNames = new StringBuilder(); //
			for (int i = 0; i < selVOS.length; i++) {
				String str_userid = selVOS[i].getStringValue("userid"); //
				String str_userName = selVOS[i].getStringValue("username"); //
				String str_accruserid = selVOS[i].getStringValue("accruserid"); //授权人
				if (hst_allaccrUser.contains(str_userid) || hst_allaccrUser.contains(str_accruserid)) { //
					sb_allAccrUserNames.append("【" + str_userName + "】"); //
				}
			}
			if (!sb_allAccrUserNames.toString().equals("")) { //如果你想让被授权能也能接收,必须选择***(已授权***)样子的人员
				if (JOptionPane.showConfirmDialog(this, "你选择的以下用户有授权处理:" + sb_allAccrUserNames.toString() + "\r\n请确认提交是否正确?\r\n", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			returnCorpVO = billTree_corp.getSelectedVO(); //机构的数据!
			returnUserVOs = selVOS; //选中的人员!!
			closeType = 1; //
			this.dispose(); //
		}
	}

	/**
	 * 取消
	 */
	private void onCancel() {
		returnCorpVO = null; //返回的机构VO
		returnUserVOs = null; //返回的人员VO
		closeType = 2; //
		this.dispose(); //
	}

	/**
	 * 往购物车添加人员!!
	 */
	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从上方人员列表中选择一个或多个人员进行此操作!"); //
			return; //
		}
		onAddShopCarUserByBillVOs(billVOs); //
	}

	/**
	 * 全部添加
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "没有可以添加的人员,无法进行此操作!"); //
			return; //
		}
		onAddShopCarUserByBillVOs(billVOs); //
	}

	/**
	 * 往购物车添加人员!!
	 */
	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		//将已加入的人员统计一下,防止重复加入!!
		HashSet hst = new HashSet(); //如果已经加入的,则不必重复加入!!
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs(); //
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) { //userid
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid"); //
				if (str_userid != null) {
					hst.add(str_userid); //人员id
				}
			}
		}
		StringBuilder sb_reduplicate_user = new StringBuilder(); //
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid"); //
			String str_usercode = _billVOs[i].getStringValue("usercode"); //人员编码!!
			String str_username = _billVOs[i].getStringValue("username"); //人员名称!!
			if (hst.contains(str_userid)) { //
				sb_reduplicate_user.append("【" + str_usercode + "/" + str_username + "】"); //
			}
		}
		String str_reduplicate_user = sb_reduplicate_user.toString(); //
		if (!str_reduplicate_user.equals("")) {
			MessageBox.show(this, "用户" + str_reduplicate_user + "已经添加过了,不能重复添加,请重新选择!"); //
			return; //
		}
		billList_user_shopcar.addBillVOs(_billVOs); //
	}

	/**
	 * 从购物车移除人员!!
	 */
	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从下方人员列表中选择一个或多个人员进行此操作!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "您确定要删除选中的人员吗?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows(); //删除所有选择中的行!!
	}

	/**
	 * 高级查询
	 */
	public void onSuperSearch() {
		WorkFlowSuperChooseUserDialog scud = new WorkFlowSuperChooseUserDialog(this);
		scud.setVisible(true);
		if (scud.getCloseType() == 1) {
			onAddShopCarUserByBillVOs(scud.getVos());
		}
	}

	/**
	 * 删除所有人员!!
	 */
	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount(); //
		if (li_count <= 0) {
			MessageBox.show(this, "已选择的人员为空,无法进行此操作!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "您确定要删除所有已选择的人员么?")) {
			return;
		}
		billList_user_shopcar.clearTable(); //清除整个表格		
	}

	public BillVO getReturnCorpVO() {
		return returnCorpVO;
	}

	public BillVO[] getReturnUserVOs() {
		return returnUserVOs;
	}

	public int getCloseType() {
		return closeType;
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1) {
			JPanel jp = (JPanel) tab.getComponentAt(1);
			if (jp.getClientProperty("aa") == null) {
				jp.setLayout(new BorderLayout());
				jp.removeAll();
				cubgp = new ChooseUserByGroupPanel(true, true, ClientEnvironment.getCurrLoginUserVO().getId(), this.billVO);//早就有参数可以设置这里可以设置平台参数设置，是否有用户列表，是否购物车跟按部门选择一样可以设置
				jp.add(cubgp);
				jp.putClientProperty("aa", "aa");
			}
		}
	}

}
