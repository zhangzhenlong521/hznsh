package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * --初始化-- 构建树 构建复选框（根据树VO） 构建空列表
 * 
 * --树选择事件-- 刷新树选择VO 动态构建复选框（根据树选择VO）清空列表
 * 
 * --复选框事件-- 获取列表数据VO（根据树选择VO） 刷新列表
 * 
 * --列表选择框事件-- 无
 * 
 * --处理返回值-- 返回ids及子孙集合 返回列表选择names
 * 
 * @author 【杨科/2012-11-15】
 *
 */
public class ComplexQueryTreePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private BillTreePanel billTreePanel = null; 
	private RefItemVO refItemVO = null;
	private CommUCDefineVO dfvo = null; 
	private RefItemVO returnRefItemVO = null; 
	
	private JPanel checkBoxPanel = null; //复选框组面板
	private BillListPanel billListPanel = null; //列表
	private JPanel rightPanel = null;
	private WLTSplitPane splitPane = null; 
	
	private JCheckBox[] checkBoxs = null; //复选框组
	private BillVO[] treeBillVOs = null; 
	private ArrayList listBillVOs = new ArrayList(); 

	public ComplexQueryTreePanel(RefItemVO refItemVO, CommUCDefineVO _dfvo) {
		this.refItemVO = refItemVO;
		this.dfvo = _dfvo;
		initialize();
	}

	public void initialize() {
		billTreePanel = new BillTreePanel(dfvo.getConfValue("模板编码")); //通过注册码生成一个格式面板

		if (billTreePanel.isChecked()) { //如果原来模板定义的是多选,则要还原成单选!复杂模式必须单选!
			billTreePanel.reSetTreeChecked(false);
		}

		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.setDragable(false); 

		int li_leaflevel = 0; 
		if (dfvo.getConfValue("只留前几层") != null) {
			try {
				li_leaflevel = Integer.parseInt("" + dfvo.getConfValue("只留前几层"));
			} catch (Exception e) {
				e.printStackTrace(); //可能数字不合法,则吃掉异常,即当0处理,保证界面能出来.
			}
		}

		String queryStr = dfvo.getConfValue("附加SQL条件"); //做成没有执行公式里的过滤sql,而是查询了所有的不对！修改成与卡片的一样
		String str_dataPolicy = dfvo.getConfValue("数据权限策略"); //数据过滤权限！！
		if (str_dataPolicy != null) { //如果有这样一个定义,则强行手工修改模板中的数据权限策略!之所以这么做是因为,如果不这样,则必须为每个策略先配置一个模板！然后依赖更多的模板！如果有这样一个参数,则只需要一个模板!而且可以重用以前的某个模板（哪怕其已经定义了策略）!因为反正我会冲掉之！
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("数据权限策略映射")); 
		}
		billTreePanel.queryDataByCondition(queryStr, li_leaflevel); //查询数据!!!附加条件会自动加上数据过滤权限!!!
		billTreePanel.setHelpInfoVisiable(false);

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

		//复杂模式暂时去掉反选 反选意义不大却加载慢
		//if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
		//	billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); 
		//}	
		
		//追加树选择事件
		billTreePanel.addBillTreeSelectListener(new BillTreeSelectListener(){

			public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
				doTreeClick();
			}
			
		});
		
		//处理返回值
		treeBillVOs = billTreePanel.getAllBillVOs();
		//以下处理复选框组反选 因为复杂模式暂时去掉反选 反选意义不大却加载慢
/*		ArrayList returnBillVOs = new ArrayList();
		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			String[] str_ids = refItemVO.getId().split(";");
			for(int i=0; i<treeBillVOs.length; i++){
				for(int j=0; j<str_ids.length; j++){
					String id = treeBillVOs[i].getStringValue(dfvo.getConfValue("ID字段"));
					if (id != null && !id.equals("") && id.equals(str_ids[j])) {
						returnBillVOs.add(treeBillVOs[i]);
					}
				}				
			}
		}
		
		if(returnBillVOs!=null&&returnBillVOs.size()>0){
			treeBillVOs = (BillVO[]) returnBillVOs.toArray(new BillVO[0]);
		}*/

		dealCheckBoxPanel(treeBillVOs);
		dealBillListPanel();
		
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(checkBoxPanel, BorderLayout.NORTH); 
		rightPanel.add(billListPanel, BorderLayout.CENTER); 

		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel, rightPanel);
		splitPane.setDividerLocation(200); 
		
		this.setLayout(new BorderLayout());  
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	//根据树选择VO构建复选框组
	private void dealCheckBoxPanel(BillVO[] billVOs) {
		if(checkBoxPanel != null){
			checkBoxPanel.removeAll();
		}else{
			checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
			checkBoxPanel.setPreferredSize(new Dimension(-1, 55));		
		}
		
		HashMap hm = new HashMap();
		if(billVOs!=null && billVOs.length>0){
			for(int i=0; i<billVOs.length; i++){
				String str = billVOs[i].getStringValue(dfvo.getConfValue("复选框字段"));
				if(!(str == null || str.trim().equals(""))){
					hm.put(str, "");	
				}
			}			
		}
		
		String str = dfvo.getConfValue("复选框种类");
		if(str != null){
			String[] corptype = str.split(";");
			checkBoxs = new JCheckBox[corptype.length];
			for(int i=0; i<corptype.length; i++){
				checkBoxs[i] = new JCheckBox(corptype[i], false);
				checkBoxs[i].setOpaque(false);
				checkBoxs[i].addActionListener(this);
				if(hm.containsKey(corptype[i])){
					checkBoxPanel.add(checkBoxs[i]);
				}
			}
		}
		
		checkBoxPanel.updateUI(); 	
	}
	
	//树选择事件 更新树选择VO 更新复选框组
	public void doTreeClick(){
		//更新树选择VO 更新复选框组
		refreshAllSelectedTreeBillVOs();
		dealCheckBoxPanel(treeBillVOs);
		doCheckBoxClick();
	}
	
    //获取所有选中节点及其子节点VOs
	public void refreshAllSelectedTreeBillVOs() {
		DefaultMutableTreeNode[] checkedNodes = billTreePanel.getSelectedNodes(); 
		for(int i = 0; i < checkedNodes.length; i++){
			if (checkedNodes[i].isRoot()) { //如果选中的是根结点,则返回所有数据VO
				treeBillVOs = billTreePanel.getAllBillVOs();
			}			
		}
		
		HashMap hm = new HashMap();
		BillVO billVO = null;
		for(int i = 0; i < checkedNodes.length; i++){		
			DefaultMutableTreeNode[] nodes = billTreePanel.getOneNodeChildPathNodes(checkedNodes[i], true); //取得所子孙结点,包含自己
			for (int j = 0; j < nodes.length; j++) {
				billVO = billTreePanel.getBillVOFromNode(nodes[j]); 
				if (billVO != null) {
					hm.put(billVO.getStringValue(dfvo.getConfValue("ID字段")), billVO);
				}
			}		
		}
		
		String[] str_keys = (String[])hm.keySet().toArray(new String[0]);
		treeBillVOs = new BillVO[str_keys.length];
		for(int i=0; i<str_keys.length; i++){
			treeBillVOs[i] = (BillVO)hm.get(str_keys[i]);
		}
	}
	
	//选择框选择事件
	public void doCheckBoxClick(){	
		//清空列表VOs
		listBillVOs.clear();
		
		//更新列表框VO
		String[] checkBoxsText = getCheckBoxs();
		if(checkBoxsText!=null){
			for(int i=0; i<checkBoxsText.length; i++){
				for(int j=0; j<treeBillVOs.length; j++){
					String corptype = treeBillVOs[j].getStringValue(dfvo.getConfValue("复选框字段"));
					if(!(corptype == null || corptype.trim().equals(""))){
						if(corptype.equals(checkBoxsText[i])){
							listBillVOs.add(treeBillVOs[j]);
						}					
					}					
				}
			}			
		}
		
		//更新列表框
		dealBillListPanel();
	}
	
	//列表框刷新
	public void dealBillListPanel(){
		HashVO[] hvo = null;
		if(billListPanel == null){	
			hvo = new HashVO[1];	
			HashVO hv = new HashVO();
			hv.setAttributeValue(dfvo.getConfValue("列表名"), null); 
			hv.setAttributeValue("id", null);
			hvo[0] = hv;
			
			billListPanel = new BillListPanel(hvo);	
			billListPanel.setItemWidth(dfvo.getConfValue("列表名"), 330);
		}
		
		billListPanel.removeAllRows();
		
		if(listBillVOs!=null&&listBillVOs.size()>0){
			hvo = new HashVO[listBillVOs.size()];
			
			boolean isTrimFirstLevel = dfvo.getConfBooleanValue("是否截掉第一层", true); 
			for(int i=0; i<listBillVOs.size(); i++){
				HashVO hv = new HashVO();
				String str_pathname = (String) ((BillVO) listBillVOs.get(i)).getUserObject(dfvo.getConfValue("列表数据匹配字段"));
				if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { 
					str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); 
				}
				hv.setAttributeValue(dfvo.getConfValue("列表名"), str_pathname);
				hv.setAttributeValue("id", (String)((BillVO) listBillVOs.get(i)).getStringValue(dfvo.getConfValue("ID字段")));
				hvo[i] = hv;
			}
			billListPanel.putValue(hvo);
		}
		
		billListPanel.setRowNumberChecked(true);
		for(int i=0; i<billListPanel.getRowCount(); i++){
			billListPanel.setCheckedRow(i, true);
		}

		billListPanel.setItemVisible("id", false);
		billListPanel.updateUI();
	}
	
	//获取所有选中的选择框
	private String[] getCheckBoxs() {
		String selected = "";
		for(int i=0; i<checkBoxs.length; i++){
			if(checkBoxs[i].isSelected()){
				selected += checkBoxs[i].getText()+";";
			}
		}

		if(selected.equals("")){
			return null;
		}else{
			return selected.substring(0, selected.length()-1).split(";");
		}
	}
	
	//获取列表选中VO
	private BillVO[] getCheckedBillVOs(){
        return billListPanel.getCheckedBillVOs();
	}
	
	//获取返回值names
	private String getReturnName() {
		String str = "";
		BillVO[] billVOs = getCheckedBillVOs();
		
		if(!(billVOs!=null&&billVOs.length>0)){
			return str;
		}
		
		for(int i=0; i<billVOs.length; i++){
			String str_pathname = billVOs[i].getStringValue(dfvo.getConfValue("列表名"));
			if(str_pathname != null){
				str += str_pathname+";"; 
			}
		}
		return str;
	}
	
    //获取返回值ids
	private String getReturnId() {
		String str = "";
		BillVO[] billVOs = getCheckedBillVOs();
		
		if(!(billVOs!=null&&billVOs.length>0)){
			return str;
		}
		
		HashMap hm = new HashMap();
		for(int i=0; i<billVOs.length; i++){
			hm.put(billVOs[i].getStringValue("id"), "");
		}
		
		int li_virtualCount = 0;
		for(int i=0; i<treeBillVOs.length; i++){
			String parentids = (String) treeBillVOs[i].getUserObject("$parentpathids");
			if(parentids!=null&&!(parentids.equals(""))){
				String[] str_ids = parentids.split(";");
				for (int j = 0; j < str_ids.length; j++) {
					if(hm.containsKey(str_ids[j])){
						if (treeBillVOs[i].isVirtualNode()) { //如果是虚拟结点,则返回-9999
							str += "-99999;";
							li_virtualCount++;
						}else{
							str += treeBillVOs[i].getStringValue(dfvo.getConfValue("ID字段"))+";";
						}
					}
				}
			}				
		}
		
		if (li_virtualCount > 0) {
			returnRefItemVO.setCode("一共有[" + li_virtualCount + "]个虚拟结点"); 
		}
		
		return str;
	}
	
	/**
	 * 监听事件!!
	 */
	public void actionPerformed(ActionEvent e) {
		
		for(int i=0; i<checkBoxs.length; i++){
			if (e.getSource() == checkBoxs[i]) {
				doCheckBoxClick();
			}
		}
		
	}
	
	/**
	 * 点击确定按钮后的逻辑!!!
	 */
	public void onConfirm() {
		returnRefItemVO = new RefItemVO(); 
		returnRefItemVO.setId(getReturnId()); 
		returnRefItemVO.setName(getReturnName()); 
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

}
