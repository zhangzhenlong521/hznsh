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
 * --��ʼ��-- ������ ������ѡ�򣨸�����VO�� �������б�
 * 
 * --��ѡ���¼�-- ˢ����ѡ��VO ��̬������ѡ�򣨸�����ѡ��VO������б�
 * 
 * --��ѡ���¼�-- ��ȡ�б�����VO��������ѡ��VO�� ˢ���б�
 * 
 * --�б�ѡ����¼�-- ��
 * 
 * --������ֵ-- ����ids�����Ｏ�� �����б�ѡ��names
 * 
 * @author �����/2012-11-15��
 *
 */
public class ComplexQueryTreePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private BillTreePanel billTreePanel = null; 
	private RefItemVO refItemVO = null;
	private CommUCDefineVO dfvo = null; 
	private RefItemVO returnRefItemVO = null; 
	
	private JPanel checkBoxPanel = null; //��ѡ�������
	private BillListPanel billListPanel = null; //�б�
	private JPanel rightPanel = null;
	private WLTSplitPane splitPane = null; 
	
	private JCheckBox[] checkBoxs = null; //��ѡ����
	private BillVO[] treeBillVOs = null; 
	private ArrayList listBillVOs = new ArrayList(); 

	public ComplexQueryTreePanel(RefItemVO refItemVO, CommUCDefineVO _dfvo) {
		this.refItemVO = refItemVO;
		this.dfvo = _dfvo;
		initialize();
	}

	public void initialize() {
		billTreePanel = new BillTreePanel(dfvo.getConfValue("ģ�����")); //ͨ��ע��������һ����ʽ���

		if (billTreePanel.isChecked()) { //���ԭ��ģ�嶨����Ƕ�ѡ,��Ҫ��ԭ�ɵ�ѡ!����ģʽ���뵥ѡ!
			billTreePanel.reSetTreeChecked(false);
		}

		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.setDragable(false); 

		int li_leaflevel = 0; 
		if (dfvo.getConfValue("ֻ��ǰ����") != null) {
			try {
				li_leaflevel = Integer.parseInt("" + dfvo.getConfValue("ֻ��ǰ����"));
			} catch (Exception e) {
				e.printStackTrace(); //�������ֲ��Ϸ�,��Ե��쳣,����0����,��֤�����ܳ���.
			}
		}

		String queryStr = dfvo.getConfValue("����SQL����"); //����û��ִ�й�ʽ��Ĺ���sql,���ǲ�ѯ�����еĲ��ԣ��޸ĳ��뿨Ƭ��һ��
		String str_dataPolicy = dfvo.getConfValue("����Ȩ�޲���"); //���ݹ���Ȩ�ޣ���
		if (str_dataPolicy != null) { //���������һ������,��ǿ���ֹ��޸�ģ���е�����Ȩ�޲���!֮������ô������Ϊ,���������,�����Ϊÿ������������һ��ģ�壡Ȼ�����������ģ�壡���������һ������,��ֻ��Ҫһ��ģ��!���ҿ���������ǰ��ĳ��ģ�壨�������Ѿ������˲��ԣ�!��Ϊ�����һ���֮��
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("����Ȩ�޲���ӳ��")); 
		}
		billTreePanel.queryDataByCondition(queryStr, li_leaflevel); //��ѯ����!!!�����������Զ��������ݹ���Ȩ��!!!
		billTreePanel.setHelpInfoVisiable(false);

		int li_allNodeCount = billTreePanel.getAllNodesCount(); 
		if (li_allNodeCount > 0) { //������С��20��,���Զ�ȫ��չ��!ʡ�÷ѹ���!
			if (li_allNodeCount <= 20) { 
				billTreePanel.myExpandAll();
			} else if (li_allNodeCount > 20 && li_allNodeCount < 100) { //������������ҪĬ��չ�������
				if (billTreePanel.getRootNode() != null && billTreePanel.getRootNode().getChildCount() >= 1) {
					TreeNode childNode_1 = billTreePanel.getRootNode().getChildAt(0); 
					if (childNode_1 != null && childNode_1.getChildCount() >= 1) {
						TreeNode childNode_2 = childNode_1.getChildAt(0); 
						billTreePanel.expandOneNodeOnly(childNode_2); 
					}
				}
			}
		}

		//����ģʽ��ʱȥ����ѡ ��ѡ���岻��ȴ������
		//if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
		//	billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); 
		//}	
		
		//׷����ѡ���¼�
		billTreePanel.addBillTreeSelectListener(new BillTreeSelectListener(){

			public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
				doTreeClick();
			}
			
		});
		
		//������ֵ
		treeBillVOs = billTreePanel.getAllBillVOs();
		//���´���ѡ���鷴ѡ ��Ϊ����ģʽ��ʱȥ����ѡ ��ѡ���岻��ȴ������
/*		ArrayList returnBillVOs = new ArrayList();
		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			String[] str_ids = refItemVO.getId().split(";");
			for(int i=0; i<treeBillVOs.length; i++){
				for(int j=0; j<str_ids.length; j++){
					String id = treeBillVOs[i].getStringValue(dfvo.getConfValue("ID�ֶ�"));
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
	
	//������ѡ��VO������ѡ����
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
				String str = billVOs[i].getStringValue(dfvo.getConfValue("��ѡ���ֶ�"));
				if(!(str == null || str.trim().equals(""))){
					hm.put(str, "");	
				}
			}			
		}
		
		String str = dfvo.getConfValue("��ѡ������");
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
	
	//��ѡ���¼� ������ѡ��VO ���¸�ѡ����
	public void doTreeClick(){
		//������ѡ��VO ���¸�ѡ����
		refreshAllSelectedTreeBillVOs();
		dealCheckBoxPanel(treeBillVOs);
		doCheckBoxClick();
	}
	
    //��ȡ����ѡ�нڵ㼰���ӽڵ�VOs
	public void refreshAllSelectedTreeBillVOs() {
		DefaultMutableTreeNode[] checkedNodes = billTreePanel.getSelectedNodes(); 
		for(int i = 0; i < checkedNodes.length; i++){
			if (checkedNodes[i].isRoot()) { //���ѡ�е��Ǹ����,�򷵻���������VO
				treeBillVOs = billTreePanel.getAllBillVOs();
			}			
		}
		
		HashMap hm = new HashMap();
		BillVO billVO = null;
		for(int i = 0; i < checkedNodes.length; i++){		
			DefaultMutableTreeNode[] nodes = billTreePanel.getOneNodeChildPathNodes(checkedNodes[i], true); //ȡ����������,�����Լ�
			for (int j = 0; j < nodes.length; j++) {
				billVO = billTreePanel.getBillVOFromNode(nodes[j]); 
				if (billVO != null) {
					hm.put(billVO.getStringValue(dfvo.getConfValue("ID�ֶ�")), billVO);
				}
			}		
		}
		
		String[] str_keys = (String[])hm.keySet().toArray(new String[0]);
		treeBillVOs = new BillVO[str_keys.length];
		for(int i=0; i<str_keys.length; i++){
			treeBillVOs[i] = (BillVO)hm.get(str_keys[i]);
		}
	}
	
	//ѡ���ѡ���¼�
	public void doCheckBoxClick(){	
		//����б�VOs
		listBillVOs.clear();
		
		//�����б��VO
		String[] checkBoxsText = getCheckBoxs();
		if(checkBoxsText!=null){
			for(int i=0; i<checkBoxsText.length; i++){
				for(int j=0; j<treeBillVOs.length; j++){
					String corptype = treeBillVOs[j].getStringValue(dfvo.getConfValue("��ѡ���ֶ�"));
					if(!(corptype == null || corptype.trim().equals(""))){
						if(corptype.equals(checkBoxsText[i])){
							listBillVOs.add(treeBillVOs[j]);
						}					
					}					
				}
			}			
		}
		
		//�����б��
		dealBillListPanel();
	}
	
	//�б��ˢ��
	public void dealBillListPanel(){
		HashVO[] hvo = null;
		if(billListPanel == null){	
			hvo = new HashVO[1];	
			HashVO hv = new HashVO();
			hv.setAttributeValue(dfvo.getConfValue("�б���"), null); 
			hv.setAttributeValue("id", null);
			hvo[0] = hv;
			
			billListPanel = new BillListPanel(hvo);	
			billListPanel.setItemWidth(dfvo.getConfValue("�б���"), 330);
		}
		
		billListPanel.removeAllRows();
		
		if(listBillVOs!=null&&listBillVOs.size()>0){
			hvo = new HashVO[listBillVOs.size()];
			
			boolean isTrimFirstLevel = dfvo.getConfBooleanValue("�Ƿ�ص���һ��", true); 
			for(int i=0; i<listBillVOs.size(); i++){
				HashVO hv = new HashVO();
				String str_pathname = (String) ((BillVO) listBillVOs.get(i)).getUserObject(dfvo.getConfValue("�б�����ƥ���ֶ�"));
				if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { 
					str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); 
				}
				hv.setAttributeValue(dfvo.getConfValue("�б���"), str_pathname);
				hv.setAttributeValue("id", (String)((BillVO) listBillVOs.get(i)).getStringValue(dfvo.getConfValue("ID�ֶ�")));
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
	
	//��ȡ����ѡ�е�ѡ���
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
	
	//��ȡ�б�ѡ��VO
	private BillVO[] getCheckedBillVOs(){
        return billListPanel.getCheckedBillVOs();
	}
	
	//��ȡ����ֵnames
	private String getReturnName() {
		String str = "";
		BillVO[] billVOs = getCheckedBillVOs();
		
		if(!(billVOs!=null&&billVOs.length>0)){
			return str;
		}
		
		for(int i=0; i<billVOs.length; i++){
			String str_pathname = billVOs[i].getStringValue(dfvo.getConfValue("�б���"));
			if(str_pathname != null){
				str += str_pathname+";"; 
			}
		}
		return str;
	}
	
    //��ȡ����ֵids
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
						if (treeBillVOs[i].isVirtualNode()) { //�����������,�򷵻�-9999
							str += "-99999;";
							li_virtualCount++;
						}else{
							str += treeBillVOs[i].getStringValue(dfvo.getConfValue("ID�ֶ�"))+";";
						}
					}
				}
			}				
		}
		
		if (li_virtualCount > 0) {
			returnRefItemVO.setCode("һ����[" + li_virtualCount + "]��������"); 
		}
		
		return str;
	}
	
	/**
	 * �����¼�!!
	 */
	public void actionPerformed(ActionEvent e) {
		
		for(int i=0; i<checkBoxs.length; i++){
			if (e.getSource() == checkBoxs[i]) {
				doCheckBoxClick();
			}
		}
		
	}
	
	/**
	 * ���ȷ����ť����߼�!!!
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
