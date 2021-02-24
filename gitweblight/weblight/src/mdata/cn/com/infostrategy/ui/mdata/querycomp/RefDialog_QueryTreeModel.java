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
 * ��ѯ����е����Ͳ���!!!
 * һ��ͨ����ע��������ת����һ���Զ�����յĻ���!!
 * ��һ��һ���Զ�����ն�����ͨ��һ��ע�����������ҳ��,Ȼ������������һ��ȷ����ȡ����Ť�Ӷ��γ�һ������!!!
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
			this.setTitle(this.getTitle() + ",ʵ������[RefDialog_QueryTreeModel]"); //
		}
		
		//�����������ÿ�� �����/2012-11-15��
		String str_state = dfvo.getConfValue("�Ƿ�������״̬"); 
		if (str_state != null && str_state.equalsIgnoreCase("Y")) {
			super.setSize(600, 500);
		}
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		billTreePanel = new BillTreePanel(dfvo.getConfValue("ģ�����")); //ͨ��ע��������һ����ʽ���

		String str_isMultiChoose = dfvo.getConfValue("���Զ�ѡ"); //
		if (str_isMultiChoose != null) { //�����ʽָ����,����,�����û�˲���!��ԭ������ģ���ж������ɶ����ɶ! ��ǰ����������! ��ǰû����,��ǿ�б����N
			if (str_isMultiChoose.equalsIgnoreCase("Y")) {
				if (!billTreePanel.isChecked()) { //���ԭ�����ǹ�ѡ�������
					billTreePanel.reSetTreeChecked(true); //����ɹ�ѡ��
				}
			} else if (str_isMultiChoose.equalsIgnoreCase("N")) {
				if (billTreePanel.isChecked()) { //���ԭ��ģ�嶨����Ƕ�ѡ,��Ҫ��ԭ�ɵ�ѡ!
					billTreePanel.reSetTreeChecked(false);
				}
			}
		}

		//����Ƕ�ѡ,��Ҫ�ж��Ƿ��С�������ѡ���Ĳ���!
		if (billTreePanel.isChecked()) {
			boolean isLinkCheck = dfvo.getConfBooleanValue("�Ƿ�������ѡ", true); //
			billTreePanel.setDefaultLinkedCheck(isLinkCheck); //������!
		} else {
			//billTreePanel.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //��ѡ
		}

		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.setDragable(false); //

		int li_leaflevel = 0; //
		if (dfvo.getConfValue("ֻ��ǰ����") != null) {
			try {
				li_leaflevel = Integer.parseInt("" + dfvo.getConfValue("ֻ��ǰ����"));
			} catch (Exception e) {
				e.printStackTrace(); //�������ֲ��Ϸ�,��Ե��쳣������0����,��֤�����ܳ���.
			}
		}

		String queryStr = dfvo.getConfValue("����SQL����");//����û��ִ�й�ʽ��Ĺ���sql ���ǲ�ѯ�����е� ���ԣ� �޸ĳ��뿨Ƭ��һ��
		String str_dataPolicy = dfvo.getConfValue("����Ȩ�޲���"); //���ݹ���Ȩ�ޣ���
		if (str_dataPolicy != null) { //���������һ������,��ǿ���ֹ��޸�ģ���е�����Ȩ�޲���!֮������ô������Ϊ������������������Ϊÿ������������һ��ģ�壡Ȼ�����������ģ�壡���������һ������,��ֻ��Ҫһ��ģ��!���ҿ���������ǰ��ĳ��ģ�壨�������Ѿ������˲��ԣ�!��Ϊ�����һ���֮��
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("����Ȩ�޲���ӳ��")); //
		}
		billTreePanel.queryDataByCondition(queryStr, li_leaflevel); //��ѯ����!!!�����������Զ��������ݹ���Ȩ��!!!
		billTreePanel.setHelpInfoVisiable(true);
		
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

		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); //����ģ����յķ���ѡ �����/2012-08-31��
		}	
		
		//׷�Ӹ���ģʽ �����/2012-11-15��
		String str_state = dfvo.getConfValue("�Ƿ�������״̬"); 
		if (str_state != null && str_state.equalsIgnoreCase("Y")) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setFocusable(false);
			tabbedPane.addTab("��ģʽ", billTreePanel);
			
			tab2Panel = new JPanel(new BorderLayout());
			tabbedPane.addTab("����ģʽ", tab2Panel);
			
			tabbedPane.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					if(tabbedPane.getSelectedIndex()==1&&complexPanel==null){
						//�ӳټ���
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
	 * ���ȷ����ť����߼�!!!
	 */
	private void onConfirm() {
		//����ģʽ����ֵ���� �����/2012-11-15��
		String str_state = dfvo.getConfValue("�Ƿ�������״̬"); 
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
		
		String str_id_field = dfvo.getConfValue("ID�ֶ�"); //
		String str_name_field = dfvo.getConfValue("NAME�ֶ�"); //
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("�Ƿ�ص���һ��", true); //�Ƿ�ص���һ��??Ĭ���ǽص�!
		boolean isOnlyChooseLeafNode = dfvo.getConfBooleanValue("ֻ��ѡҶ��", false); //ֻ��ѡҶ�ӽ��,Ĭ���Ƿ�

		if (str_id_field == null) {
			str_id_field = billTreePanel.getTempletVO().getPkname(); //���û������ʹ��ģ���!
		}
		if (str_id_field == null) {
			MessageBox.show(this, "��ʽ��û�ж��巵�ص�ID�ֶ���,ģ����Ҳû�ж�����������"); //
			return; //
		}
		if (!billTreePanel.getTempletVO().containsItemKey(str_id_field)) { //
			MessageBox.show(this, "ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
			return; //
		}

		boolean isLikeOrModel = false; //
		if (billTreePanel.isChecked()) { //�����������������,�����Ƕ�ѡ��!!����һ��ǿ������!!
			if (this.getParentContainer() != null && getParentContainer() instanceof QueryCPanel_UIRefPanel) {
				QueryCPanel_UIRefPanel refPanel = (QueryCPanel_UIRefPanel) getParentContainer(); //
				if ("LikeOr����".equals(refPanel.getTempletItemVO().getQuerycreatetype())) { //�����like or����!!
					isLikeOrModel = true; //
					if (billTreePanel.isDefaultLinkedCheck() && ClientEnvironment.isAdmin()) {
						StringBuilder sb_help = new StringBuilder(); //
						sb_help.append("hi,�𾴵Ŀ�����ʵʩ��Ա,��ã�\r\n");
						sb_help.append("���ֵ�ǰ���οؼ���ѡģʽ,������ѡ���ӽ��,���ÿؼ��Ĳ�ѯ�������ǡ�LikeOr���ơ�,ǿ�ҽ��鲻Ҫ��ô����\r\n"); //
						sb_help.append("������Ϊ,�����ᵼ�·��ش����Ľ��ֵ,Ȼ�;ͻ�ƴ��̫����[cscorp like '%;123;%' or cscorp like '%;125;%'....],�Ӷ�����SQL��̬�������޷�ִ�У�\r\n");
						sb_help.append("��ѯ������like or����,һ��������ѯ¼����Ƕ��ֵ�����,���¼��ֵ�ǵ�ֵ,���ʹ��In����!��like or���ƿ���ֻѡ���������Ҳ�Ǻ���ģ�\r\n");
						sb_help.append("\r\n");
						sb_help.append("����ͨ������[\"�Ƿ�������ѡ\",\"N\"]�����Ʋ�����������\r\n");
						sb_help.append("�Թؼ����ԭ��,��֪Ϥ����������Ϣֻ�Թ���ģʽ��ʾ��");
						MessageBox.show(this, sb_help.toString()); //
					}
				}
			}
		} else {
			if (ClientEnvironment.isAdmin()) {
				MessageBox.show(this, "hi,�𾴵Ŀ�����ʵʩ��Ա,��ã���ѯģʽ��ǿ�ҽ���ʹ�ù�ѡ���ѡ��ʽ!!��ȷ�������Ƿ�����ǵ�ѡ?\r\n�ؼ����ԭ��,��֪Ϥ!!!����Ϣֻ�Թ���ģʽ��ʾ��"); //
			}
		}

		if (!billTreePanel.isChecked()) { //�����ѡ��������
			BillVO billvo = billTreePanel.getSelectedVO();
			if (billvo == null) {
				MessageBox.showSelectOne(this); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //ֻ��ѡҶ�ӽ��!
				if (!billTreePanel.getSelectedNode().isLeaf()) {
					MessageBox.show(this, "ֻ��ѡ��Ҷ�ӽ��,����ĩ���Ľ��!"); //
					return; //
				}
			}

			HashVO hvo = billvo.convertToHashVO(); //�ȴ���HashVO

			//��ʹ�ǵ�ѡ,Ҳ�Զ�������������������!!!,Ȼ�����ʹ��in����,���Զ����ض�
			BillVO[] billVOs = billTreePanel.getSelectedChildPathBillVOs(); //���ֵ�����������!!
			StringBuilder sb_allChildIds = new StringBuilder(); //
			for (int i = 0; i < billVOs.length; i++) {
				if (billVOs[i].isVirtualNode()) { //�����������,�򷵻�-9999,
					sb_allChildIds.append("-99999;"); //
				} else {
					sb_allChildIds.append(billVOs[i].getStringValue(str_id_field) + ";"); //�����ֶ���!!
				}
			}
			hvo.setAttributeValue("$�����ӽ��ID", sb_allChildIds.toString()); //

			returnRefItemVO = new RefItemVO(hvo); //��������VO		
			//���ò���VO��id
			if (billvo.isVirtualNode()) { //�����������,�򷵻�-99999
				returnRefItemVO.setId("-99999"); //
				returnRefItemVO.setCode("��Ϊѡ�еĽ����������,���Է���-99999"); //
			} else {
				returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //ʹ��ʵ��ֵ!
			}
			String str_pathname = hvo.getStringValue("$parentpathname"); //
			if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { //���Ҫ�õ���һ��!�����һ����Զ��"��ҵ����",�����ѿ��ӷ�ƨ,�����!!!
				str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); //֧������ҵ��-�ʽ�����
			}
			returnRefItemVO.setName(str_pathname); //			
		} else { //����Ƕ�ѡ��..
			BillVO[] billVOs = billTreePanel.getCheckedVOs(); //ȡ�ù�ѡס�����н��!
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "�����ٹ�ѡһ������!\r\n��ܰ��ʾ:���ǿ��Զ�ѡ��!\r\n������ǰ���Ǹ���ѡ����,��������ѡ��!"); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //ֻ��ѡҶ�ӽ��!!
				DefaultMutableTreeNode[] allCheckedNodes = billTreePanel.getCheckedNodes(); //
				for (int i = 0; i < allCheckedNodes.length; i++) {
					if (!allCheckedNodes[i].isLeaf()) {
						MessageBox.show(this, "ֻ��ѡ��Ҷ�ӽ��,����ĩ���Ľ��!"); //
						return; //
					}
				}
			}
			//���Likeor����,�����Ƕ�ѡ����
			if (isLikeOrModel) {
				String str_likeOrLimit = dfvo.getConfValue("LikeOr���Ʒ��ؽ��������", "300"); //Ĭ��500������!
				if (billVOs.length > Integer.parseInt(str_likeOrLimit)) {
					MessageBox.show(this, "��Ϊ���ǵ�ϵͳ����,ϵͳ����ֻ��ѡ��[" + str_likeOrLimit + "]������,����ȴѡ����[" + billVOs.length + "]��,������ѡ��!\r\n��ͨ����ѯ���յĲ���[\"LikeOr���Ʒ��ؽ��������\",\"300\"]�������������!"); //
					return; //
				}
			}

			returnRefItemVO = new RefItemVO(); //
			//��������һ�������ж�,���������˵�Ȩ��������ȫ������,Ȼ���������ѯ���з�Χ!�����in����,�����ܺ���!��ʵ��Ӧ����1=1,�����֪����ȫ��������?ֻ��ȥ���ݿ����ٲ�һ��!
			//���ѡ�е����ݽ�����ĸ������õ������ݿ��е�,�����˵����ȫ������,�򷵻�1=1
			boolean isSelAlldata = false; //
			String str_recordCount = null; //
			if (billVOs.length >= 1000) { //�������ѡ�еĽ����������1000��,������ѡ����ȫ������,��������ȫ��,��ֱ�ӷ���1=1
				try {
					str_recordCount = UIUtil.getStringValueByDS(null, "select count(*) from " + billTreePanel.getTempletVO().getTablename()); //
					if (billVOs.length >= Integer.parseInt(str_recordCount)) {
						isSelAlldata = true; //
					}
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}

			if (isSelAlldata) { //��������,����ѡ�еľ�����������!
				returnRefItemVO.setId("'AllData'='AllData'"); //����һ�����ʽ,��ѯ����ʱ�Զ�ֻ�������!
				returnRefItemVO.setCode("���ݿ�����[" + str_recordCount + "]����¼,ѡ����[" + billVOs.length + "]����¼,������Ϊ����������!"); //
				returnRefItemVO.setName("��������"); //
			} else {
				StringBuffer sb_ids = new StringBuffer();
				StringBuffer sb_names = new StringBuffer(); //
				int li_virtualCount = 0; //�����������
				//ѭ������!!!
				for (int i = 0; i < billVOs.length; i++) {
					if (billVOs[i] == null) {
						continue;
					}

					//ƴ�Ӷ��!!!
					if (billVOs[i].isVirtualNode()) { //�����������,��ʹ��-99999����,
						sb_ids.append("-99999;"); //
						li_virtualCount++; //
					} else {
						sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); //
					}

					String str_pathname = (String) billVOs[i].getUserObject("$ParentPathName"); //·��ȫ��
					if (isTrimFirstLevel && str_pathname != null && str_pathname.indexOf("-") > 0) { //���Ҫ�õ���һ��!�����һ����Զ��"��ҵ����",�����ѿ��ӷ�ƨ,�����!!!
						str_pathname = str_pathname.substring(str_pathname.indexOf("-") + 1, str_pathname.length()); //
					}
					sb_names.append(str_pathname + ";"); //
				}
				returnRefItemVO.setId(sb_ids.toString()); //
				returnRefItemVO.setName(sb_names.toString()); //
				if (li_virtualCount > 0) {
					returnRefItemVO.setCode("һ����[" + li_virtualCount + "]��������"); //
				}
			}
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	/**
	 * �����¼�!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //�����ȷ���򷵻�����	
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
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
	 * ��ʼ���
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 500;
	}

}
