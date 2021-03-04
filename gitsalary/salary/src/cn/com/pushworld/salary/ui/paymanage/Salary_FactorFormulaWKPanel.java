package cn.com.pushworld.salary.ui.paymanage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 绩效考核指标因子和薪资计算公式因子定义。
 * @author haoming
 * create by 2013-6-20
 */
/**
 * @author haoming create by 2013-6-20
 */
public class Salary_FactorFormulaWKPanel extends AbstractWorkPanel implements ActionListener, TreeSelectionListener {
	private static final long serialVersionUID = -8330192782589565805L;
	private BillListPanel factorListPanel;
	private WLTButton btn_insert, btn_update, btn_delete, btn_execute, btn_check, btn_checkerror, btn_copy;
	private TBUtil tbutil = new TBUtil();
	private SalaryServiceIfc services;
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private TBUtil tbUtil = null; //zzl[2020-5-11]

	public void initialize() {
		String condition = getMenuConfMapValueAsStr("过滤条件");
		factorListPanel = new BillListPanel("SAL_FACTOR_DEF_CODE1");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_delete = new WLTButton("删除");// 删除逻辑需要判断，该公式删除，会影响那些因子（后实现）.
		btn_execute = new WLTButton("算算看");
		btn_copy = new WLTButton("复制");
		btn_check = new WLTButton("检查关联");
		btn_checkerror = new WLTButton("检查错误");
		btn_check.addActionListener(this);
		btn_execute.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_checkerror.addActionListener(this);
		btn_copy.addActionListener(this);
		factorListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_execute, btn_copy, btn_check, btn_checkerror });// 加入按钮
		factorListPanel.repaintBillListButton();

		if (!tbutil.isEmpty(condition)) { //如果不为空
			factorListPanel.setDataFilterCustCondition(condition);
			factorListPanel.QueryDataByCondition(null);
			this.add(factorListPanel);
		} else {
			JTree treePanel = getTreePanel();
			treePanel.getSelectionModel().addTreeSelectionListener(this);
			WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);
			splitPane.setDividerLocation(180);
			splitPane.setLeftComponent(new JScrollPane(treePanel));
			splitPane.setRightComponent(factorListPanel);
			this.add(splitPane);
		}

	}

	HashMap<String, DefaultMutableTreeNode> allFactor = new HashMap<String, DefaultMutableTreeNode>(); //把所有的对象因子名称存储起来。

	//得到树
	public JTree getTreePanel() {
		try {
			TBUtil tbutil = new TBUtil();
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, " select *from SAL_FACTOR_DEF");//读取系统中所有的　对象　因子
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("全部");
			List<DefaultMutableTreeNode> alltreenode = new ArrayList<DefaultMutableTreeNode>(); //把数据库中的所有放到因子实例化成节点。
			for (int i = 0; i < vos.length; i++) {
				vos[i].setToStringFieldName("name");
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(vos[i]);
				allFactor.put(vos[i].getStringValue("name"), node); //把名称和节点缓存。
				alltreenode.add(node);
			}
			DefaultMutableTreeNode commdmonode = new DefaultMutableTreeNode("通用");
			for (int i = 0; i < vos.length; i++) {
				String name = vos[i].getStringValue("name");
				String factorType = vos[i].getStringValue("sourcetype");
				DefaultMutableTreeNode currNode = allFactor.get(name);
				String value = vos[i].getStringValue("value");
				if (value == null) {
					continue;
				}
				if ("代码计算".equals(factorType)) {
					continue;
				}
				String[] childFactor = tbutil.getMacroList(value, "[", "]"); //用[]分割
				for (int j = 0; j < childFactor.length; j++) {
					String childFactorName = childFactor[j];
					if (childFactorName.indexOf("[") != 0) {
						continue;
					}
					String sourceConfig = childFactorName.substring(1, childFactorName.length() - 1);
					if (sourceConfig.contains(".")) {
						sourceConfig = sourceConfig.substring(0, sourceConfig.indexOf("."));
					}
					if (allFactor.containsKey(sourceConfig)) {
						DefaultMutableTreeNode childNode = allFactor.get(sourceConfig);
						HashVO childFactorVo = (HashVO) childNode.getUserObject();
						if ("系统数据".equals(childFactorVo.getStringValue("managetype"))) {
							if (childNode.getParent() != null) {
								commdmonode.add(childNode); //把系统的加入
							}
						} else {
							try {
								currNode.add(childNode);
							} catch (Exception ex) {
								System.out.println(">>" + sourceConfig);
								ex.printStackTrace();
							}
						}
					} else {
						//						System.out.println("有一个找不到[" + sourceConfig + "]");
					}
				}
			}
			for (Iterator iterator = alltreenode.iterator(); iterator.hasNext();) {
				DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) iterator.next();
				if (defaultMutableTreeNode.getParent() == null) {
					if (defaultMutableTreeNode.getChildCount() > 0) {
						root.add(defaultMutableTreeNode);
					} else {
						commdmonode.add(defaultMutableTreeNode);
					}
				}
			}
			if (commdmonode.getChildCount() > 0) {
				root.add(commdmonode);
			}

			int rootCount = root.getChildCount();

			for (int i = 0; i < rootCount; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
				if (node.getUserObject() instanceof HashVO) {
					node.removeAllChildren();
					visitChild(node);
				}
			}
			//找到所有没有被应用的树节点
			JTree tree = new JTree(root);
			tree.setUI(new WLTTreeUI(true)); //
			tree.setOpaque(false);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //只能单选!
			((BasicTreeUI) tree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); //
			((BasicTreeUI) tree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); //
			tree.setCellRenderer(new MyRenderer());
			return tree;
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return null;
	}

	private void visitChild(DefaultMutableTreeNode _parentNode) {
		HashVO factorVO = (HashVO) _parentNode.getUserObject();
		String factorType = factorVO.getStringValue("sourcetype"); //得到对象因子类型
		String name = factorVO.getStringValue("name");//因子名称
		String value = factorVO.getStringValue("value");//公示定义
		if (value == null) {
			return;
		}
		String conditions = factorVO.getStringValue("conditions");
		if ("Excel".equals(factorType)) {
			return;
		}
		String[] childFactor = new String[0];
		if (!"代码计算".equals(factorType)) {
			childFactor = tbutil.getMacroList(value, "[", "]");
		}
		if (conditions != null && conditions.contains("[")) {
			String str[] = tbutil.getMacroList(conditions, "[", "]");
			if (str != null && str.length > 0) {
				List newList = new ArrayList<String>();
				for (int i = 0; i < childFactor.length; i++) {
					newList.add(childFactor[i]);
				}
				for (int i = 0; i < str.length; i++) {
					newList.add(str[i]);
				}
				childFactor = (String[]) newList.toArray(new String[0]);
			}
		}
		HashSet<String> his = new HashSet<String>();
		for (int j = 0; j < childFactor.length; j++) {
			String childFactorName = childFactor[j];
			if (childFactorName.indexOf("[") != 0) {
				continue;
			}
			String sourceConfig = childFactorName.substring(1, childFactorName.length() - 1);
			if (sourceConfig.contains(".")) {
				sourceConfig = sourceConfig.substring(0, sourceConfig.indexOf("."));
			}
			if (his.contains(sourceConfig) || sourceConfig.trim().equals("")) { //如果包含此引用的对象因子，则继续 
				continue;
			}
			if ("传入数据".equals(sourceConfig)) {
				continue; //是
			}
			if (allFactor.containsKey(sourceConfig)) {
				DefaultMutableTreeNode oldnode = allFactor.get(sourceConfig);
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(allFactor.get(sourceConfig).getUserObject());
				//				HashVO childFactorVo = (HashVO) childNode.getUserObject();
				if (!"系统数据".equals(((HashVO) oldnode.getUserObject()).getStringValue("managetype"))) {
					_parentNode.add(childNode);
				}
				his.add(sourceConfig);
				visitChild(childNode);
			} else {
				WLTLogger.getLogger().warn(name + "找不到【" + sourceConfig + "】");
			}
		}

	}

	private void onDelete() {
		BillVO selectVO = factorListPanel.getSelectedBillVO(); //
		if (selectVO == null) {
			MessageBox.showSelectOne(factorListPanel);
			return;
		}
		List relations = checkRelationOthers(selectVO);
		if (relations == null || relations.size() == 0) {
			if (MessageBox.confirmDel(this)) {
				factorListPanel.doDelete(true);
			}
		} else {
			int index = MessageBox.showOptionDialog(this, "此数据被[" + relations.size() + "]条数据引用", "提示", new String[] { "查看", "直接删除", "取消" });
			if (index < 0 || index == 2) { // 关闭或者取消
				return;
			}
			if (index == 0) {
				List ids = new ArrayList();
				for (int i = 0; i < relations.size(); i++) {
					HashVO factor = (HashVO) relations.get(i);
					ids.add(factor.getStringValue("id"));
				}
				BillListDialog dialog = new BillListDialog(this, "查看[" + selectVO.getStringValue("name") + "]被关联项", "SAL_FACTOR_DEF_CODE1", " id in(" + TBUtil.getTBUtil().getInCondition(ids) + ") ", 780, 600) {
					@Override
					public void onConfirm() {
						setCloseType(BillDialog.CONFIRM);
						this.dispose(); //
					}
				};
				BillQueryPanel queryP = dialog.getBilllistPanel().getQuickQueryPanel();
				if (queryP != null) {
					queryP.setVisible(false);
				}
				dialog.getBtn_confirm().setText("继续删除");
				dialog.getBtn_confirm().setToolTipText("继续删除[" + selectVO.getStringValue("name") + "]内容.");
				dialog.getBtn_confirm().setPreferredSize(new Dimension(80, 21));
				dialog.setVisible(true);
				if (dialog.getCloseType() == 1) {
					factorListPanel.doDelete(true);
				}
			} else if (index == 1) {
				factorListPanel.doDelete(true);
			}
		}

	}

	/**
	 * 检测一个条件是否关联其他。
	 */
	private List checkRelationOthers(BillVO _oneFactorBillVO) {
		if (_oneFactorBillVO == null) {
			return null;
		}
		String sourceType = _oneFactorBillVO.getStringValue("sourcetype");
		String name = _oneFactorBillVO.getStringValue("name");
		List retList = new ArrayList();
		try {
			if ("系统对象".equals(sourceType)) { // 系统对象被应用的可能有 [对象名称] [对象名称.字段]
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, " select id from sal_factor_def where value like '%[" + name + "]%' or value like '[%" + name + ".%' or conditions  like '%[" + name + "]%' ");
				//				HashVO[] depttarget = UIUtil.getHashVoArrayByDS(null, "select *from ");
				retList = Arrays.asList(vos);
			} else {
				HashVO vos[] = UIUtil.getHashVoArrayByDS(null, " select id from sal_factor_def where value like '%[" + name + "]%' or  conditions  like '%[" + name + "]%'  ");
				retList = Arrays.asList(vos);
			}
		} catch (WLTRemoteException e) {
			MessageBox.showException(this, e);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return retList;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_execute) {
			try {
				BillVO vo = factorListPanel.getSelectedBillVO();
				if (vo == null) {
					MessageBox.showSelectOne(factorListPanel);
					return;
				}
				onAction(factorListPanel, vo.convertToHashVO());
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		} else if (e.getSource() == btn_check) {
			if (factorListPanel.getSelectedBillVO() == null) {
				MessageBox.showSelectOne(factorListPanel);
				return;
			}
			BillVO billvo = factorListPanel.getSelectedBillVO();
			List l = checkRelationOthers(billvo);
			if (l.size() > 0) {
				BillListDialog dialog = new BillListDialog(this, "查看[" + billvo.getStringValue("name") + "]被关联项", "SAL_FACTOR_DEF_CODE1", " id in(" + stbutil.getInCondition((HashVO[]) l.toArray(new HashVO[0]), "id") + ") ", 780, 600) {
					@Override
					public void onConfirm() {
						setCloseType(BillDialog.CONFIRM);
						this.dispose(); //
					}
				};
				dialog.getBtn_confirm().setText("定位");
				dialog.setVisible(true);
				if (dialog.getCloseType() == 1) { //如果选择多个，需要把已经显示的放到最后，然后把没有加载的补进来。
					BillVO rtvos[] = dialog.getBilllistPanel().getSelectedBillVOs();
					if (rtvos != null && rtvos.length > 0) {
						BillVO allfactorvos[] = factorListPanel.getAllBillVOs();
						HashMap<String, Integer> index = new HashMap<String, Integer>();
						for (int j = 0; j < allfactorvos.length; j++) {
							index.put(allfactorvos[j].getStringValue("name"), j);
						}
						List unfondvo = new ArrayList();
						List findlist = new ArrayList();
						List findvos = new ArrayList();
						for (int i = 0; i < rtvos.length; i++) {
							if (index.containsKey(rtvos[i].getStringValue("name"))) {
								Integer index_1 = index.get(rtvos[i].getStringValue("name")); //找到位置
								findlist.add(index_1);
								findvos.add(allfactorvos[index_1]);
							} else {
								unfondvo.add(rtvos[i]);
							}
						}
						if (unfondvo.size() == 0 && findlist.size() == 1) { //如果只有一个，就直接选中。
							int ind = (Integer) findlist.get(0);
							factorListPanel.setSelectedRow(ind);
						} else {
							int[] haverows = new int[findlist.size()];
							for (int i = 0; i < haverows.length; i++) {
								haverows[i] = (int) (Integer) findlist.get(i);
							}
							factorListPanel.removeRows(haverows);
							int beforecount = factorListPanel.getRowCount();
							factorListPanel.addBillVOs((BillVO[]) findvos.toArray(new BillVO[0]));
							factorListPanel.addBillVOs((BillVO[]) unfondvo.toArray(new BillVO[0]));
							int lastcount = factorListPanel.getRowCount();
							for (int i = beforecount; i < lastcount; i++) {
								factorListPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_INIT);
								if (i == beforecount) {
									factorListPanel.setSelectedRow(i);
								} else {
									factorListPanel.addSelectedRow(i);
								}
							}
						}
					}
				}
			} else {
				MessageBox.show(this, "此数据在公式中没有被关联");
			}
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_checkerror) {
			onCheckError();
		} else if (e.getSource() == btn_copy) {
			onCopy();
		}
	}

	//复制一个副本，直接修改。
	private void onCopy() {
		BillVO bvo = factorListPanel.getSelectedBillVO();
		if (bvo == null) {
			MessageBox.show(factorListPanel, "请选择一条记录.");
			return;
		}
		BillCardDialog dialog = new BillCardDialog(factorListPanel, "SAL_FACTOR_DEF_CODE1", WLTConstants.BILLDATAEDITSTATE_INSERT);
		StringItemVO newid = (StringItemVO) dialog.getCardPanel().getValueAt("id");
		bvo.setObject("id", newid);
		dialog.getCardPanel().setBillVO(bvo);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			BillVO nbvo = dialog.getBillcardPanel().getBillVO();
			int srow = factorListPanel.getSelectedRow() + 1;
			factorListPanel.insertRow(srow, nbvo);
			factorListPanel.setSelectedRow(srow);
		}
	}

	public void onAction(Container _parent, HashVO _factorVO) throws Exception {
		String selectDate = "";
		if (_factorVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		Boolean wgflg=getTBUtil().getSysOptionBooleanValue("是否启动网格指标计算模式", false);//zzl[2020-5-11]
		int index=0;
		if(wgflg){
			index = MessageBox.showOptionDialog(_parent, "选择要执行测试的类型", "提示", new String[] { "系统数据", "工资", "员工指标", "部门指标" ,"网格指标"}, 500, 150);

		}else{
			index = MessageBox.showOptionDialog(_parent, "选择要执行测试的类型", "提示", new String[] { "系统数据", "工资", "员工指标", "部门指标"}, 500, 150);
		}
		String sql = "";
		if (index == 0 || index == 1 || index == 2) {
			String uid = JOptionPane.showInputDialog(_parent, "请输入员工号/姓名/登录号");
			if (uid == null) {
				uid = "666666";
			}
			if (uid == null || uid.equals("")) {
				return;
			}
			if (index == 1 || index == 2) {
				selectDate = getDate(_parent);
			}
			if (index == 2) {
				sql = "select t1.*,t2.mainstation,t2.deptid checkeddept from sal_person_check_score t1 left join v_sal_personinfo t2  on t1.checkeduser = t2.id where (t2.tellerno='" + uid + "'  or t2.code ='" + uid + "' or t2.name='" + uid + "') and t1.targettype='员工定量指标' and t1.checkdate='" + selectDate + "'";
			} else {
				sql = "select sal.*,sal.id checkeduser,sal.deptid checkeddept from v_sal_personinfo sal where tellerno='" + uid + "' or code ='" + uid + "' or name='" + uid + "'";
			}
		} else if (index == 3) {
			selectDate = getDate(_parent);
			String uid = JOptionPane.showInputDialog(_parent, "请输机构简称/机构ID");
			if (uid == null) {
				uid = "5201";
			}
			if (uid == null || uid.equals("")) {
				return;
			}
			sql = "select t1.* from sal_dept_check_score t1 left join pub_corp_dept t2 on t1.checkeddept = t2.id where t1.targettype='部门定量指标' and t1.checkdate='" + selectDate + "'" + (uid == null ? "" : (" and (t2.id = '" + uid + " ' or t2.shortname like '%" + uid + "%')"));
		} else if(index == 4){//zzl[2020-5-9] 网格化测试按钮功能
			String uid = JOptionPane.showInputDialog(_parent, "请输网格名称/网格编码,如果是多套网格的话请输入网格ID测算");
			selectDate = getDate(_parent);
			sql= "select exc.ID wgid,exc.YEAR,exc.MONTH,exc.CREATTIME,exc.A,exc.B,exc.C,exc.D,exc.E,exc.F,exc.G,sal.id userid,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from excel_tab_85 exc left join v_sal_personinfo sal on exc.g=sal.code where exc.C||exc.D='"+uid+"' or E='"+uid+"' or exc.D='"+uid+"' or exc.ID='"+uid+"'";
		} else {
			return;
		}
		HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql);
		if (vos.length == 0) {
			MessageBox.show(_parent, sql + " 没有查到数据,不能演算");
			return;
		}
		HashVO _baseInputVO = vos[0];
		if (index == 1 || index == 0 || index == 4) {
			_baseInputVO.setAttributeValue("checkdate", selectDate);
			_baseInputVO.setAttributeValue("checkeduser", _baseInputVO.getStringValue("id"));
		}
		Object rtobj[] = getService().onExecute(_factorVO, _baseInputVO);
		try {
			StringBuffer rtsb = new StringBuffer();
			if (vos.length > 0) {
				Object obj = rtobj[0];
				rtsb = (StringBuffer) rtobj[1];
				if (obj instanceof Number) {
					MessageBox.show(_parent, "返回值是字符串/数字类型[" + obj.getClass().getName() + "]\r\n" + obj + "\r\n" + rtsb.toString());
				} else if (obj instanceof String) {
					MessageBox.show(_parent, "返回值是字符串/数字类型" + obj.getClass().getName() + "\r\n" + obj + "\r\n" + rtsb.toString());
				} else {
					if (obj instanceof Object[]) { // 如果是数组
						Object[] return_vos = (Object[]) obj;
						if (return_vos.length > 0) {
							StringBuffer sb = new StringBuffer();
							for (int i = 0; i < return_vos.length; i++) {
								sb.append(String.valueOf(return_vos[i]) + ";");
							}
							MessageBox.show(_parent, "返回值是对象[" + return_vos.length + "个长度]\r\n类型[" + return_vos[0].getClass().getName() + "]\r\n" + sb.toString() + "\r\n" + rtsb.toString());
						} else {
							MessageBox.show(_parent, "没有找到对象组\r\n" + obj + "\r\n" + rtsb.toString());
						}

					} else {
						MessageBox.show(_parent, "返回值是对象" + obj.getClass().getName() + "]\r\n" + rtsb.toString());
					}
				}
			}
		} catch (Exception e1) {
			MessageBox.showException(_parent, e1);
		}

	}

	String selectDate = new SalaryUIUtil().getCheckDate();

	private String getDate(Container _parent) {
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "请选择要考核的月份", new RefItemVO(selectDate, "", selectDate), null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			return selectDate;
		} catch (Exception e) {
			WLTLogger.getLogger(Salary_FactorFormulaWKPanel.class).error(e);
		}
		return "2013-08";
	}

	private void onCheckError() {
		BillVO billvos[] = factorListPanel.getSelectedBillVOs();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		StringBuffer sb = new StringBuffer();
		String uid = JOptionPane.showInputDialog(this, "请输入员工号");
		if (uid == null) {
			uid = "666666";
		}
		if (uid == null || uid.equals("")) {
			return;
		}
		HashVO vos[] = null;
		try {
			vos = UIUtil.getHashVoArrayByDS(null, "select * from v_sal_personinfo where tellerno='" + uid + "' or code ='" + uid + "' or name='" + uid + "'");
		} catch (WLTRemoteException e) {
			MessageBox.showException(this, e);
			return;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return;
		}

		for (int j = 0; j < billvos.length; j++) {
			HashVO factorVO = billvos[j].convertToHashVO();
			StringBuffer rtsb = new StringBuffer();
			try {
				util.onExecute(factorVO, vos[0], rtsb);
			} catch (Exception e) {
				WLTLogger.getLogger(Salary_FactorFormulaWKPanel.class).error(e);
				sb.append("[" + factorVO.getStringValue("name") + "]有问题" + rtsb + "  \r\n");
			}
		}
		if (sb.length() > 0) {
			MessageBox.show(this, sb.toString());
		} else {
			MessageBox.show(this, "所选的内容不存在执行问题");
		}
	}

	public void valueChanged(TreeSelectionEvent treeselectionevent) {
		TreePath[] paths = treeselectionevent.getPaths(); //取得所有选中的
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
		Object nodevo = node.getUserObject();
		if (nodevo instanceof HashVO) {
			HashVO[] vo = getAllChildHashVO(node);
			String id = new SalaryTBUtil().getInCondition(vo, "id");
			factorListPanel.QueryDataByCondition(" id in(" + id + ")");
		}
	}

	private HashVO[] getAllChildHashVO(DefaultMutableTreeNode _node) {
		Vector vc = new Vector();
		visitAllNodes(vc, _node);
		Vector hashvos = new Vector();
		for (Iterator iterator = vc.iterator(); iterator.hasNext();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) iterator.next();
			hashvos.add(node.getUserObject());
		}
		return (HashVO[]) hashvos.toArray(new HashVO[0]);
	}

	private void visitAllNodes(Vector _vc, DefaultMutableTreeNode _node) {
		_vc.add(_node);
		int count = _node.getChildCount();
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) _node.getChildAt(i);
			visitAllNodes(_vc, childNode);
		}
	}

	class MyRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Object userobj = ((DefaultMutableTreeNode) value).getUserObject(); //
			JLabel label = new JLabel(String.valueOf(userobj)); //
			label.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
			if (sel) {
				label.setOpaque(true);
				label.setForeground(Color.RED); //
				label.setBackground(Color.YELLOW); //
			} else {
				label.setOpaque(false);
			}
			return label;
		}
	}

	private SalaryServiceIfc getService() {
		if (services == null) {
			try {
				services = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		return services;
	}
	public TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}
}
