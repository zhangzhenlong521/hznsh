package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 查询面板中的树型参照!!!
 * 一种通过将注册面板快速转换成一个自定义参照的基类!!
 * 即一般一个自定义参照都可以通过一个注册面板生成主页面,然后在下面增加一个确定与取消按扭从而形成一个参照!!!
 * @author xch
 *
 */
public class RefDialog_QueryTreeModel extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillTreePanel billTreePanel = null; 
	private CommUCDefineVO dfvo = null; //
	private RefItemVO refItemVO = null;
	
	private JTabbedPane tabbedPane = null;
	private JPanel tab2Panel = null;
	private ComplexQueryTreePanel complexPanel = null;

	//private HashMap nameMap, parentidMap;

	public RefDialog_QueryTreeModel(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		this.dfvo = _dfvo;
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + ",实现类是[RefDialog_QueryTreeModel]"); //
		}
		
		//复杂树型重置宽度 【杨科/2012-11-15】
		String str_state = dfvo.getConfValue("是否复杂树型状态"); 
		if (str_state != null && str_state.equalsIgnoreCase("Y")) {
			super.setSize(600, 500);
		}
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		billTreePanel = new BillTreePanel(dfvo.getConfValue("模板编码")); //通过注册码生成一个格式面板

		String str_isMultiChoose = dfvo.getConfValue("可以多选"); //
		if (str_isMultiChoose != null) { //如果显式指定了,则处理,即如果没此参数!则原来参照模板中定义的是啥就是啥! 经前不是这样的! 以前没定义,则强行变成了N
			if (str_isMultiChoose.equalsIgnoreCase("Y")) {
				if (!billTreePanel.isChecked()) { //如果原来不是勾选框的样子
					billTreePanel.reSetTreeChecked(true); //则设成勾选框
				}
			} else if (str_isMultiChoose.equalsIgnoreCase("N")) {
				if (billTreePanel.isChecked()) { //如果原来模板定义的是多选,则要还原成单选!
					billTreePanel.reSetTreeChecked(false);
				}
			}
		}

		//如果是多选,则还要判断是否有“连动勾选”的参数!
		if (billTreePanel.isChecked()) {
			boolean isLinkCheck = dfvo.getConfBooleanValue("是否连动勾选", true); //
			billTreePanel.setDefaultLinkedCheck(isLinkCheck); //不联动!
		} else {
			//billTreePanel.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //单选
		}

		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.setDragable(false); //

		int li_leaflevel = 0; //
		if (dfvo.getConfValue("只留前几层") != null) {
			try {
				li_leaflevel = Integer.parseInt("" + dfvo.getConfValue("只留前几层"));
			} catch (Exception e) {
				e.printStackTrace(); //可能数字不合法,则吃掉异常，即当0处理,保证界面能出来.
			}
		}

		String queryStr = dfvo.getConfValue("附加SQL条件");//做成没有执行公式里的过滤sql 而是查询了所有的 不对！ 修改成与卡片的一样
		String str_dataPolicy = dfvo.getConfValue("数据权限策略"); //数据过滤权限！！
		if (str_dataPolicy != null) { //如果有这样一个定义,则强行手工修改模板中的数据权限策略!之所以这么做是因为，如果不这样，则必须为每个策略先配置一个模板！然后依赖更多的模板！如果有这样一个参数,则只需要一个模板!而且可以重用以前的某个模板（哪怕其已经定义了策略）!因为反正我会冲掉之！
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("数据权限策略映射")); //
		}
		billTreePanel.queryDataByCondition(queryStr, li_leaflevel); //查询数据!!!附加条件会自动加上数据过滤权限!!!
		billTreePanel.setHelpInfoVisiable(true);
		
		int li_allNodeCount = billTreePanel.getAllNodesCount(); 
		if (li_allNodeCount > 0) { //如果结点小于20个,则自动全部展开!省得费功了!
			if (li_allNodeCount <= 20) { 
				billTreePanel.myExpandAll();
			} else if (li_allNodeCount > 20 && li_allNodeCount < 100) { //绝大多数情况是要默认展“长孙”的
				if (billTreePanel.getRootNode() != null && billTreePanel.getRootNode().getChildCount() >= 1) {
					TreeNode childNode_1 = billTreePanel.getRootNode().getChildAt(0); 
					if (childNode_1 != null && childNode_1.getChildCount() >= 1) {
						TreeNode childNode_2 = childNode_1.getChildAt(0); 
						billTreePanel.expandOneNodeOnly(childNode_2); 
					}
				}
			}
		}

		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); //树型模板参照的反向勾选 【杨科/2012-08-31】
		}	
		
		//追加复杂模式 【杨科/2012-11-15】
		String str_state = dfvo.getConfValue("是否复杂树型状态"); 
		if (str_state != null && str_state.equalsIgnoreCase("Y")) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setFocusable(false);
			tabbedPane.addTab("简单模式", billTreePanel);
			
			tab2Panel = new JPanel(new BorderLayout());
			tabbedPane.addTab("复杂模式", tab2Panel);
			
			tabbedPane.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					if(tabbedPane.getSelectedIndex()==1&&complexPanel==null){
						//延迟加载
						tabbedPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						tab2Panel.removeAll();
						complexPanel = new ComplexQueryTreePanel(refItemVO, dfvo);
						tab2Panel.add(complexPanel);
						tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
					}	
				}
				
			}); 
			
			this.getContentPane().add(tabbedPane, BorderLayout.CENTER);		
		}else{
			this.getContentPane().add(billTreePanel, BorderLayout.CENTER); 
		}
		
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); 
	}

	/**
	 * 点击确定按钮后的逻辑!!!
	 */
	private void onConfirm() {
		//复杂模式返回值处理 【杨科/2012-11-15】
		String str_state = dfvo.getConfValue("是否复杂树型状态"); 
		if (str_state != null && str_state.equalsIgnoreCase("Y") && tabbedPane.getSelectedIndex()==1) {
			complexPanel.onConfirm();
			returnRefItemVO = complexPanel.getReturnRefItemVO();
			if(!(returnRefItemVO.getId()!=null&&!returnRefItemVO.getId().equals(""))){
				MessageBox.showSelectOne(this); 
				return; 	
			}
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose(); 
			return;
		}
		
		String str_id_field = dfvo.getConfValue("ID字段"); //
		String str_name_field = dfvo.getConfValue("NAME字段"); //
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("是否截掉第一层", true); //是否截掉第一层??默认是截的!
		boolean isOnlyChooseLeafNode = dfvo.getConfBooleanValue("只能选叶子", false); //只能选叶子结点,默认是否

		if (str_id_field == null) {
			str_id_field = billTreePanel.getTempletVO().getPkname(); //如果没定义则使用模板的!
		}
		if (str_id_field == null) {
			MessageBox.show(this, "公式中没有定义返回的ID字段名,模板中也没有定义表的主键！"); //
			return; //
		}
		if (!billTreePanel.getTempletVO().containsItemKey(str_id_field)) { //
			MessageBox.show(this, "指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
			return; //
		}

		boolean isLikeOrModel = false; //
		if (billTreePanel.isChecked()) { //如果发现树是联动的,且又是多选的!!则来一个强烈提醒!!
			if (this.getParentContainer() != null && getParentContainer() instanceof QueryCPanel_UIRefPanel) {
				QueryCPanel_UIRefPanel refPanel = (QueryCPanel_UIRefPanel) getParentContainer(); //
				if ("LikeOr机制".equals(refPanel.getTempletItemVO().getQuerycreatetype())) { //如果是like or机制!!
					isLikeOrModel = true; //
					if (billTreePanel.isDefaultLinkedCheck() && ClientEnvironment.isAdmin()) {
						StringBuilder sb_help = new StringBuilder(); //
						sb_help.append("hi,尊敬的开发或实施人员,你好！\r\n");
						sb_help.append("发现当前树形控件多选模式,且连动选择子结点,而该控件的查询条件又是“LikeOr机制”,强烈建议不要这么做！\r\n"); //
						sb_help.append("这是因为,连动会导致返回大量的结点值,然就就会拼接太长的[cscorp like '%;123;%' or cscorp like '%;125;%'....],从而导致SQL变态超长而无法执行！\r\n");
						sb_help.append("查询条件是like or机制,一般用来查询录入的是多个值的情况,如果录入值是单值,则就使用In机制!而like or机制控制只选择少量结点也是合理的！\r\n");
						sb_help.append("\r\n");
						sb_help.append("可以通过参数[\"是否连动勾选\",\"N\"]来控制不连动！！！\r\n");
						sb_help.append("以关键设计原则,请知悉！！！此信息只对管理模式提示！");
						MessageBox.show(this, sb_help.toString()); //
					}
				}
			}
		} else {
			if (ClientEnvironment.isAdmin()) {
				MessageBox.show(this, "hi,尊敬的开发或实施人员,你好！查询模式下强烈建议使用勾选框多选方式!!请确认需求是否真的是单选?\r\n关键设计原则,请知悉!!!此信息只对管理模式提示！"); //
			}
		}

		if (!billTreePanel.isChecked()) { //如果单选！！！！
			BillVO billvo = billTreePanel.getSelectedVO();
			if (billvo == null) {
				MessageBox.showSelectOne(this); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //只能选叶子结点!
				if (!billTreePanel.getSelectedNode().isLeaf()) {
					MessageBox.show(this, "只能选择叶子结点,即最末级的结点!"); //
					return; //
				}
			}

			HashVO hvo = billvo.convertToHashVO(); //先创建HashVO

			//即使是单选,也自动将所有子孙结点计算出来!!!,然后如果使用in条件,则自动从特定
			BillVO[] billVOs = billTreePanel.getSelectedChildPathBillVOs(); //保持得所有子孙结点!!
			StringBuilder sb_allChildIds = new StringBuilder(); //
			for (int i = 0; i < billVOs.length; i++) {
				if (billVOs[i].isVirtualNode()) { //如果是虚拟结点,则返回-9999,
					sb_allChildIds.append("-99999;"); //
				} else {
					sb_allChildIds.append(billVOs[i].getStringValue(str_id_field) + ";"); //主键字段名!!
				}
			}
			hvo.setAttributeValue("$所有子结点ID", sb_allChildIds.toString()); //

			returnRefItemVO = new RefItemVO(hvo); //创建参照VO		
			//设置参照VO的id
			if (billvo.isVirtualNode()) { //如果是虚拟结点,则返回-99999
				returnRefItemVO.setId("-99999"); //
				returnRefItemVO.setCode("因为选中的结点是虚拟结点,所以返回-99999"); //
			} else {
				returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //使用实际值!
			}
			String str_pathname = hvo.getStringValue("$parentpathname"); //
			if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { //如果要裁掉第一层!比如第一层永远是"兴业银行",纯属脱库子放屁,多余的!!!
				str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); //支付结算业务-资金往来
			}
			returnRefItemVO.setName(str_pathname); //			
		} else { //如果是多选的..
			BillVO[] billVOs = billTreePanel.getCheckedVOs(); //取得勾选住的所有结点!
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "请至少勾选一条数据!\r\n温馨提示:这是可以多选的!\r\n点击结点前的那个勾选方框,才算真正选中!"); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //只能选叶子结点!!
				DefaultMutableTreeNode[] allCheckedNodes = billTreePanel.getCheckedNodes(); //
				for (int i = 0; i < allCheckedNodes.length; i++) {
					if (!allCheckedNodes[i].isLeaf()) {
						MessageBox.show(this, "只能选择叶子结点,即最末级的结点!"); //
						return; //
					}
				}
			}
			//如果Likeor机制,且又是多选联动
			if (isLikeOrModel) {
				String str_likeOrLimit = dfvo.getConfValue("LikeOr机制返回结点数上限", "300"); //默认500个上限!
				if (billVOs.length > Integer.parseInt(str_likeOrLimit)) {
					MessageBox.show(this, "因为考虑到系统性能,系统限制只能选择[" + str_likeOrLimit + "]个数据,现在却选择了[" + billVOs.length + "]个,请重新选择!\r\n可通过查询参照的参数[\"LikeOr机制返回结点数上限\",\"300\"]来调整这个上限!"); //
					return; //
				}
			}

			returnRefItemVO = new RefItemVO(); //
			//后来增加一个智能判断,即如果这个人的权限正好是全部数据,然后他又想查询所有范围!如果是in机制,则性能很慢!其实就应该是1=1,但如何知道是全部数据呢?只能去数据库中再查一下!
			//如果选中的数据结果集的个数正好等于数据库中的,则必须说明是全部数据,则返回1=1
			boolean isSelAlldata = false; //
			String str_recordCount = null; //
			if (billVOs.length >= 1000) { //如果发现选中的结点数量大于1000了,则担心是选择了全行数据,如果真的是全行,则直接返回1=1
				try {
					str_recordCount = UIUtil.getStringValueByDS(null, "select count(*) from " + billTreePanel.getTempletVO().getTablename()); //
					if (billVOs.length >= Integer.parseInt(str_recordCount)) {
						isSelAlldata = true; //
					}
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}

			if (isSelAlldata) { //如果算出来,发现选中的就是所有数据!
				returnRefItemVO.setId("'AllData'='AllData'"); //返回一个恒等式,查询框处理时自动只加上这个!
				returnRefItemVO.setCode("数据库中有[" + str_recordCount + "]条记录,选中了[" + billVOs.length + "]条记录,所以认为是所有数据!"); //
				returnRefItemVO.setName("所有数据"); //
			} else {
				StringBuffer sb_ids = new StringBuffer();
				StringBuffer sb_names = new StringBuffer(); //
				int li_virtualCount = 0; //虚拟结点的数量
				//循环处理!!!
				for (int i = 0; i < billVOs.length; i++) {
					if (billVOs[i] == null) {
						continue;
					}

					//拼接多个!!!
					if (billVOs[i].isVirtualNode()) { //如果是虚拟结点,则使用-99999代替,
						sb_ids.append("-99999;"); //
						li_virtualCount++; //
					} else {
						sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); //
					}

					String str_pathname = (String) billVOs[i].getUserObject("$ParentPathName"); //路径全名
					if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { //如果要裁掉第一层!比如第一层永远是"兴业银行",纯属脱库子放屁,多余的!!!
						str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); //
					}
					sb_names.append(str_pathname + ";"); //
				}
				returnRefItemVO.setId(sb_ids.toString()); //
				returnRefItemVO.setName(sb_names.toString()); //
				if (li_virtualCount > 0) {
					returnRefItemVO.setCode("一共有[" + li_virtualCount + "]个虚拟结点"); //
				}
			}
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	/**
	 * 监听事件!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //如果是确定则返回数据	
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		}
	}

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

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 500;
	}

}
