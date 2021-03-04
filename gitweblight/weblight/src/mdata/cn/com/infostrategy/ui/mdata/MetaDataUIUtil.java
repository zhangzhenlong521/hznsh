package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1_item;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTComBoBoxUI;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;

public class MetaDataUIUtil {
	private TBUtil tbutil = null;

	/**
	 * 在直接模板编辑配置时,根据新的机制要进行复杂的计算! 即根据XML与DB中实际存的情况而决定是如何处理!!
	 * 
	 * @param _container
	 * @param _buildFromType
	 * @param _buildFromInfo
	 * @param _templetCode
	 * @return
	 * @throws Exception
	 */
	public boolean checkTempletIsCanConfig(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, null);
	}

	/**
	 * 将模板编辑抽象到此类中
	 */
	public boolean modifyTemplet(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, _colKey);
	}

	public boolean modifyTemplet2(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, _colKey);
	}

	public boolean checkTempletIsCanConfig(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {

		String tableName = null;
		if (_container instanceof BillCardPanel) {
			tableName = ((BillCardPanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillListPanel) {
			tableName = ((BillListPanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillTreePanel) {
			tableName = ((BillTreePanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillPropPanel) {
		}

		if (_buildFromType.equals("CLASS")) {
			MessageBox.show(_container, "直接由类代码[" + _buildFromInfo + "]创建,不可以编辑!\r\n只能由开发人员通过修改代码来修改!"); //
			return false; //
		} else if (_buildFromType.equals("XML")) { // 直接明文指定从XML中创建的!!!
			// 比如工作流的一些单!!
			MessageBox.show(_container, "直接指定的XML[" + _buildFromInfo + "]创建,不可以编辑!\r\n只能由开发人员通过修改代码来修改!"); //
			return false; //
		} else if (_buildFromType.equals("XML2") || _buildFromType.equals("DB")) { // 先从DB中没找到,转向XML中创建的!
			HashMap returnBooleans = isDBAndXMLHaveTempletByCode(_templetCode); // 数据库中是否有!
			boolean isDBHave = (Boolean) returnBooleans.get("ISDBHAVE"); // 数据库中是否有
			boolean isXMLHave = (Boolean) returnBooleans.get("ISXMLHAVE"); // XML中是否有?
			_buildFromInfo = (String) returnBooleans.get("XMLINFO");//这里才是提示信息，包括有几个包中含该模板【李春娟/2012-07-18】
			String str_xmlpath = (String) returnBooleans.get("XMLPATH");//这里是xml包路径

			if (isDBHave) { // 如果数据库中有
				if (isXMLHave) { // 如果XML文件中有
					if (_isquiet) { // 如果是安静模式!
						return true; //
					} else {
						int li_result = MessageBox.showOptionDialog(_container, "该模板的创建类型是[" + _buildFromType + "],创建来源是[" + _buildFromInfo + "],模板编码是[" + _templetCode + "]\r\n现在数据库与系统XML注册中都有该模板,你可以有如下操作:", "提示", new String[] { "查看XML中的", "比较两者", "删除数据库中的", "编辑数据库中的", "查看关联模板", " 取  消 " }, 570, 150); //
						if (li_result == 0) { //
							viewXmlTempletByCode(_container, _templetCode, _colKey);
							return false; //
						} else if (li_result == 1) { //
							compareTempletFromXmlAndDatabase(_container, _templetCode);
							return false; //
						} else if (li_result == 2) { //
							if (MessageBox.confirm(_container, "您确定要删除数据库中的记录吗？")) {//开发人员经常误删除，故这里做个提示【李春娟/2018-06-15】
								deleteOneTempletByCode(_templetCode); // 删除
								MessageBox.show(_container, "删除了数据库中的记录,请重新打开页面而使用XML中的!"); //
							}
							return false; //
						} else if (li_result == 3) { //
							editDbTempletByCode(_container, _templetCode, _colKey);
							return true; // 直接编辑数据库中的
						} else if (li_result == 4) { //查看关联模板
							viewRefTemplet(_container, tableName);
							return true; //
						} else if (li_result == 5) { //
							return false; //
						} else {
							return false;
						}
					}
				} else { // 如果XML中没有
					if (_isquiet) {
						return true; //
					} else {
						editDbTempletByCode(_container, _templetCode, _colKey);
						return true;
					}
				}
			} else { // 如果数据库中没有!!
				if (isXMLHave) { // 如果XML中有
					if (_isquiet) { // 如果是安静模式!
						MessageBox.show(_container, "该模板的创建类型是[" + _buildFromType + "],创建来源是[" + _buildFromInfo + "],模板编码是[" + _templetCode + "]!\r\n现在数据库中没有,XML注册中有,必须复制过来才能进行该处理!"); //
						return false; //
					} else {
						int li_result = MessageBox.showOptionDialog(_container, "该模板的创建类型是[" + _buildFromType + "],创建来源是[" + _buildFromInfo + "],模板编码是[" + _templetCode + "]!\r\n现在数据库中没有,XML注册中有,如果你想编辑与修改必须要先复制到数据库中才行!", "提示", new String[] { "查看XML中的", "复制到数据库中", " 取  消 " }, 550, 150); //
						if (li_result == 0) {
							viewXmlTempletByCode(_container, _templetCode, _colKey);
							// MessageBox.show(_container, "直接查看XM中的!开发中....");
							// //
							return false; //
						} else if (li_result == 1) {
							importXMLToDB(_container, str_xmlpath, _templetCode, _templetName, _colKey); //
							return false;
						} else if (li_result == 2) {
							return false;
						} else {
							return false; //
						}
					}
				} else {
					MessageBox.show(_container, "该模板的创建类型是[" + _buildFromType + "],创建来源是[" + _buildFromInfo + "],模板编码是[" + _templetCode + "]!\r\n但现在XML与数据库中都没有定义信息,这是不正常的,可能被人误删除了,请与系统开发商联系!"); //
					return false; //
				}
			}
		} else {
			MessageBox.show(_container, "未知的创建方式[" + _buildFromType + "],不能编辑"); //
			return false; //
		}
	}

	/**
	 * 去数据库中实际查找是否有该模板!!!
	 * 
	 * @param _code
	 * @return
	 * @throws Exception
	 */
	private HashMap isDBAndXMLHaveTempletByCode(String _code) throws Exception {
		HashMap parMap = new HashMap(); //
		parMap.put("templetcode", _code); // 模板编码
		HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.mdata.MetaDataBSUtil", "checkDBAndRegistXMLHaveTemplet", parMap); // 远程调用!!!
		//		boolean isDBHave = (Boolean) returnMap.get("ISDBHAVE"); //
		//		boolean isXMLHave = (Boolean) returnMap.get("ISXMLHAVE"); //
		return returnMap; //

	}

	private void viewRefTemplet(Container _parent, String tableName) {
		if (tableName == null) {
			MessageBox.show(_parent, "保存表名为空!");
			return;
		}
		BillDialog bd = new BillDialog(_parent, "关联模板", 1020, 500);//sunfujun/20120621/太短有个按钮没显示出来
		MetaTempletConfigPanel cp = new MetaTempletConfigPanel();
		cp.initialize();
		cp.getSearch_box().setSelectedItem("ALL");
		cp.getSearch_table().setText(tableName);
		cp.onSearch();
		bd.add(cp);
		bd.setVisible(true);
	}

	private boolean importXMLToDB(Container _parent, String _xmlFileName, String templetcode, String _templetName, String _colKey) throws Exception {
		ShowCopyTempleteDialog showCopyTempleteDialog = new ShowCopyTempleteDialog(_parent, _templetName, templetcode, _templetName);
		showCopyTempleteDialog.getTemplet_code_text().setText(templetcode);//这里需要设置一下，使复制到数据库中的模板编码和复制的xml模板编码一致，这样以后在导出xml就可以替换了【李春娟/2012-07-16】
		showCopyTempleteDialog.setVisible(true);
		if (showCopyTempleteDialog.getCloseType() == 0) {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			HashMap param = new HashMap();
			param.put("pub_templet_1_templetcode", showCopyTempleteDialog.getTempleteCode());
			param.put("pub_templet_1_templetname", showCopyTempleteDialog.getTempleteName());
			service.importRecordsXMLToTable(new String[] { _xmlFileName }, null, true, param); // 重新生成主键!!!
			if (MessageBox.confirm(_parent, "复制到数据库中成功!你现在是否想立即编辑?")) { //
				return editDbTempletByCode(_parent, showCopyTempleteDialog.getTempleteCode(), _colKey); //
			} else {
				return false; //
			}
		} else {
			return false;
		}
	}

	/**
	 * 根据模板编码删除某个模板数据!!
	 * 
	 * @param _code
	 */
	private void deleteOneTempletByCode(String _code) throws Exception {
		HashMap parMap = new HashMap(); //
		parMap.put("templetcode", _code); // 模板编码!!!
		UIUtil.commMethod("cn.com.infostrategy.bs.mdata.MetaDataBSUtil", "deleteOneTempletByCode", parMap); // 远程调用!!
	}

	/**
	 * 取得克隆的对象!!因为在UIRefPanel与QueryCPanel_UIRefPanel都用到这个类,所以放在公式类中!!
	 * 
	 * @return
	 */
	public CommUCDefineVO cloneCommUCDefineVO(CommUCDefineVO _olddfVO, BillPanel billPanel) {
		if (billPanel == null) {
			return _olddfVO;
		}
		try {
			String[] str_allKeys = _olddfVO.getAllConfKeys(); //
			if (str_allKeys == null || str_allKeys.length <= 0) {
				return _olddfVO; // 如果没有参数,则直接返回原来的!!!
			}
			TBUtil tbUtil = new TBUtil(); //
			CommUCDefineVO cloneUCDfVO = (CommUCDefineVO) tbUtil.deepClone(_olddfVO); // 克隆一个!!!关键
			boolean isConverted = false;
			for (int i = 0; i < str_allKeys.length; i++) {
				String str_value = cloneUCDfVO.getConfValue(str_allKeys[i]); // 原来的公式!!
				String[] str_macro = tbUtil.getFormulaMacPars(str_value, "${", "}"); //
				if (str_macro != null && str_macro.length > 0) { // 如果有需要进行替换的!!
					for (int j = 0; j < str_macro.length; j++) { // 遍历进行替换!!!
						String str_billValue = null;
						if (billPanel instanceof BillCardPanel) { // 如果是卡片
							str_billValue = ((BillCardPanel) billPanel).getBillVO().getRealValue(str_macro[j]); //
						} else if (billPanel instanceof BillListPanel) { // 如果是列表!!
							BillVO billVO = ((BillListPanel) billPanel).getSelectedBillVO(); //
							if (billVO != null) {
								str_billValue = billVO.getRealValue(str_macro[j]); //
							}
						} else if (billPanel instanceof BillQueryPanel) {
							str_billValue = ((BillQueryPanel) billPanel).getRealValueAt(str_macro[j]); //
						}
						/*
						 * 以后可能根据参数,决定直接弹出提示框,必须先录入另一个字段的值!!!
						 * 这个参数是系统参数还是该控件的参数呢? 初步感觉应该是个系统参数!!! String
						 * str_itemName =
						 * this.templetItemVO.getPub_Templet_1VO().getItemVo(str_macro[j]).getItemname();
						 * MessageBox.show(this, "请先录入[" + str_itemName+"]的值!"); //
						 * return null;
						 */
						if (str_billValue == null || str_billValue.trim().equals("")) {
							str_value = tbUtil.replaceAll(str_value, "${" + str_macro[j] + "}", "-99999"); // 先做成替换-9999,即不提示!!!
						} else { // 是否进行直接报错,提醒必须录入某个值!
							str_value = tbUtil.replaceAll(str_value, "${" + str_macro[j] + "}", str_billValue); // 替换一下!!!
						}
					}
					cloneUCDfVO.setConfValue(str_allKeys[i], str_value); // 重新设置
					isConverted = true; // 替换过了!!
				}
			}
			if (isConverted) { // 如果发生过替换处理了,则返回
				return cloneUCDfVO; // 返回克隆的对象!!
			} else { // 如果没有发生转换,则返回原来的! 因为大部分是这种情况,因为怕克隆发生问题,为了保险用原来的!!
				return _olddfVO; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(billPanel, "克隆并替换公式中的${itemkey}的宏代码时发生异常!!\r\n请至控制强查看详细信息!"); //
			return null;
		}
	}

	/*
	 * 根据模板编码打开xml中的模板 如果_colKey值存在则是第一种编辑方式，编辑列。
	 */
	private void viewXmlTempletByCode(Container _parent, String _templetCode, String _colKey) {
		if (_colKey != null && !_colKey.equals("")) {
			this.TempletModify1(_parent, _templetCode, _colKey);
		} else {
			TempletModify2 mfdialog = new TempletModify2(_parent, _templetCode, false, true);
		}
	}

	private boolean editDbTempletByCode(Container _parent, String _templetCode, String _colKey) {
		if (_colKey != null && !"".equals(_colKey.trim())) {
			try {
				String str_pk_templet = UIUtil.getStringValueByDS(null, "select pk_pub_templet_1 from pub_templet_1 where templetcode='" + _templetCode + "'"); //
				String str_id = UIUtil.getStringValueByDS(null, "select pk_pub_templet_1_item from pub_templet_1_item where pk_pub_templet_1=" + str_pk_templet + " and itemkey='" + _colKey + "'"); //主键!!!去数据库重新取一下!!
				if (str_id == null) {
					MessageBox.showTextArea(_parent, "当前模板子表中记录的主键为空,可能是直接由类创建,请检查!");
				}
				int li_return = 1;
				if (!getTBUtil().getSysOptionBooleanValue("开发人员模式", false)) {
					li_return = MessageBox.showOptionDialog(_parent, "请确认使用哪种模式进行配置?", "提示", new String[] { "实施人员模式", "开发人员模式", "取消" }, 0, 325, 120); //	
				}
				if (li_return == 0 || li_return == 1) { //如果是实施人员/开发人员模式!
					BillCardDialog dialog = new BillCardDialog(_parent, "列编辑", 900, 700, new TMO_Pub_Templet_1_item(li_return == 0 ? true : false), "pk_pub_templet_1_item='" + str_id + "'"); //
					dialog.setVisible(true); //
					if (dialog.getCloseType() == 1) {
						return true;
					}
				}
			} catch (Exception ex) {
				MessageBox.showException(_parent, ex); //
			}
		} else {
			int li_return = 1;
			if (!getTBUtil().getSysOptionBooleanValue("开发人员模式", false)) {
				li_return = MessageBox.showOptionDialog(_parent, "请确认使用哪种模式进行配置?", "提示", new String[] { "实施人员模式", "开发人员模式", "取消" }, 0, 325, 120); //	
			}
			if (li_return == 0 || li_return == 1) { //实施人员模式!
				TempletModify2 m2 = new TempletModify2(_parent, _templetCode, li_return == 0 ? true : false);
				if (m2.getCloseType() == 1) {
					return true;
				}
			}
		}
		return false;
	}

	// 第一种编辑方式 查看xml中[郝明2012-04-26]
	private void TempletModify1(Container _parent, String _templetCode, String _colKey) {
		BillCardPanel cardPanel = new BillCardPanel(new TMO_Pub_Templet_1_item());
		cardPanel.setEditable(true);
		FrameWorkMetaDataServiceIfc service;
		try {
			service = UIUtil.getMetaDataService();
			DefaultTMO tmo = service.getDefaultTMOByCode(_templetCode, 1); // 取到
			HashVO[] vos = tmo.getPub_templet_1_itemData();
			Object obj[][] = null;
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("itemkey").equals(_colKey)) {
					obj = service.getBillListDataByHashVOs(cardPanel.getTempletVO().getParPub_Templet_1VO(), new HashVO[] { vos[i] }); // 直接根据hashvo得到控件内容
				}
			}
			if (obj != null && obj.length > 0) {
				cardPanel.setValue(obj[0]); //
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final BillDialog dialog = new BillDialog(_parent, 900, 700);
		dialog.setLayout(new BorderLayout());
		dialog.add(cardPanel, BorderLayout.CENTER);
		WLTPanel btn_panel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout());
		WLTButton btn = new WLTButton("关闭");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		btn_panel.add(btn);
		dialog.add(btn_panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	/*
	 * 对比模板 xml中和模板中！
	 */
	private void compareTempletFromXmlAndDatabase(Container _parent, String _templetCode) throws Exception {
		new TempletCompareDialog(_parent, _templetCode);
	}

	/**【sunfujun/20120508/对模板新增删除字段的优化_xch】
	 * @param _parentContainer
	 * @param str_tablename
	 * @param str_itemkey
	 * @param str_itemname
	 */
	public void showDropColumnPanel(Container _parentContainer, String str_tablename, String str_itemkey, String str_itemname) {
		try {
			FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			String dropSql = service.getDropColumnSql(null, str_tablename, str_itemkey).trim();
			//怎么搞的ORACEL 执行SQL时如果后面是;不行了？
			dropSql = dropSql.replaceAll(";", " ");
			JLabel sqlLabel = new JLabel("您将执行以下SQL:");
			sqlLabel.setFont(LookAndFeel.font); //
			sqlLabel.setPreferredSize(new Dimension(200, 22));
			JTextField sqlArea = new JTextField();
			sqlArea.setPreferredSize(new Dimension(420, 22));
			sqlArea.setFont(LookAndFeel.font); //
			sqlArea.setText(dropSql);
			VFlowLayoutPanel vf = new VFlowLayoutPanel(new JComponent[] { sqlLabel, sqlArea });
			vf.setUI(new WLTPanelUI());
			if (JOptionPane.showConfirmDialog(_parentContainer, vf, "您确认删除字段吗", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, UIUtil.getImage("info.gif")) == JOptionPane.OK_OPTION) {
				if (dropSql != null && !"".equals(dropSql)) {
					UIUtil.executeUpdateByDS(null, dropSql);
					MessageBox.show("在表[" + str_tablename + "]上删除[" + str_itemname + "]列成功!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(_parentContainer, e.getMessage());
		}
	}

	/**
	 * 【sunfujun/20120508/对模板新增删除字段的优化_xch】
	 * @param _parentContainer
	 * @param str_tablename
	 * @param str_itemkey
	 * @param str_itemname
	 * @param str_itemtype
	 */
	public void showAddColumnPanel(Container _parentContainer, String str_tablename, String str_itemkey, String str_itemname, String str_itemtype) {
		try {
			final FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			JLabel keyLabel = new JLabel("字段名 [" + str_itemkey + "]", JLabel.RIGHT);
			keyLabel.setFont(LookAndFeel.font);
			keyLabel.setPreferredSize(new Dimension(keyLabel.getPreferredSize().width, 22));
			JLabel typeLabel = new JLabel("  类型 ", JLabel.RIGHT);
			typeLabel.setFont(LookAndFeel.font);
			typeLabel.setPreferredSize(new Dimension(60, 22));
			final JComboBox typeCom = new JComboBox(new String[] { "字符", "数字" });
			typeCom.setUI(new WLTComBoBoxUI());
			typeCom.setPreferredSize(new Dimension(100, 22));
			typeCom.setSelectedIndex(0);
			JLabel lengthLabel = new JLabel(" 长度 ", JLabel.RIGHT);
			lengthLabel.setFont(LookAndFeel.font);
			lengthLabel.setPreferredSize(new Dimension(80, 22));
			final JTextField lengthFied = new JFormattedTextField();
			lengthFied.setHorizontalAlignment(JTextField.RIGHT); //
			lengthFied.setDocument(new NumberFormatdocument());
			lengthFied.setText("50");
			if (WLTConstants.COMP_DATE.equals(str_itemtype)) {
				lengthFied.setText("10");
			} else if (WLTConstants.COMP_DATETIME.equals(str_itemtype)) {
				lengthFied.setText("19");
			} else if (WLTConstants.COMP_BIGAREA.equals(str_itemtype)) {
				lengthFied.setText("100");
			} else if (WLTConstants.COMP_TEXTAREA.equals(str_itemtype)) {
				lengthFied.setText("100");
			} else if (WLTConstants.COMP_CHECKBOX.equals(str_itemtype)) {
				lengthFied.setText("1");
			} else if (WLTConstants.COMP_NUMBERFIELD.equals(str_itemtype)) {
				lengthFied.setText("22");
				typeCom.setSelectedIndex(1);
			}
			lengthFied.setPreferredSize(new Dimension(50, 22));
			JLabel sqlLabel = new JLabel("您将执行以下SQL:");
			sqlLabel.setFont(LookAndFeel.font);
			final JTextField sqlArea = new JTextField();
			sqlArea.setPreferredSize(new Dimension(420, 22));
			sqlArea.setFont(LookAndFeel.font);
			final String tablename = str_tablename;
			final String itemkey = str_itemkey;
			typeCom.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					try {
						sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("数字") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
						//是不是不太好呢？考虑到不经常进行此操作所以不再重写代码，每次都远程调用
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			lengthFied.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					try {
						sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("数字") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("数字") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
			VFlowLayoutPanel vf = new VFlowLayoutPanel(new JComponent[] { new HFlowLayoutPanel(new JComponent[] { keyLabel, typeLabel, typeCom, lengthLabel, lengthFied }), sqlLabel, sqlArea });
			vf.setUI(new WLTPanelUI());
			if (JOptionPane.showConfirmDialog(_parentContainer, vf, "您确认新增字段吗", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, UIUtil.getImage("info.gif")) == JOptionPane.OK_OPTION) {
				String sql = sqlArea.getText();
				if (sql != null && !"".equals(sql)) {
					UIUtil.executeUpdateByDS(null, sql);
					MessageBox.show("在表[" + str_tablename + "]上创建[" + str_itemname + "]列成功!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(_parentContainer, e.getMessage());
		}
	}

	public void showItemState(Container parent, Pub_Templet_1_ItemVO[] templetItemVOs) {
		WLTPanel p = new WLTPanel(0);
		p.setLayout(new BorderLayout());
		if (templetItemVOs != null && templetItemVOs.length > 0) {
			BillListPanel bl = new BillListPanel(new DefaultTMO("子项状态", new String[][] { { "子项名称", "100" }, { "保存表是否存在", "150" }, { "是否参与保存", "150" }, { "查询表是否存在", "150" } }));
			WLTLabel fi = new WLTLabel("字体红色代表参与保存但无法保存", -1);
			fi.setPreferredSize(new Dimension(190, 20));
			fi.setForeground(Color.RED);
			WLTLabel s = new WLTLabel("※代表必填或警告项", -1);
			s.setPreferredSize(new Dimension(120, 20));
			s.addStrItemColor("※", Color.RED);
			WLTLabel t = new WLTLabel("★代表无法查询", -1);
			t.setPreferredSize(new Dimension(98, 20));
			t.setForeground(Color.BLUE);
			WLTLabel fo = new WLTLabel("◎代表不参与保存", -1);
			fo.setPreferredSize(new Dimension(110, 20));
			fo.setForeground(Color.GREEN);
			bl.getBillListBtnPanel().getPanel_flow().add(fi);
			bl.getBillListBtnPanel().getPanel_flow().add(s);
			bl.getBillListBtnPanel().getPanel_flow().add(t);
			bl.getBillListBtnPanel().getPanel_flow().add(fo);
			bl.getTable().getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel label = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					WLTLabel a = new WLTLabel(label.getText(), 0);
					a.addStrItemColor("※", Color.RED);
					a.addStrItemColor("★", Color.BLUE);
					a.addStrItemColor("◎", Color.GREEN);
					if (flag1) {
						a.setBorder(BorderFactory.createLineBorder(new Color(99, 130, 191), 0)); //
					} else {
						a.setBorder(BorderFactory.createEmptyBorder()); //
					}
					if (flag) {
						a.setBackground(LookAndFeel.tablerowselectbgcolor);
					} else {
						if (i % 2 == 0) {
							a.setBackground(LookAndFeel.table_bgcolor_odd); //
						} else {
							a.setBackground(LookAndFeel.tablebgcolor); //
						}
					}
					if (obj != null && obj instanceof BillItemVO) {
						try {
							String str_foreColor = ((BillItemVO) obj).getForeGroundColor(); //这行代码的逻辑以前不知怎么被人改了!!导致颜色公式都不能用了!
							if (str_foreColor != null) { //如果前景颜色不为空
								a.addStrItemColor(str_foreColor.split(";")[0], getTBUtil().getColor(str_foreColor.split(";")[1]));
							}
						} catch (Exception exx) {
							exx.printStackTrace(); //
						}
					}
					return a;
				}
			});
			bl.getTable().getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			bl.getTable().getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			bl.getTable().getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			for (int i = 0; i < templetItemVOs.length; i++) {
				if (templetItemVOs[i].isCardisshowable() || templetItemVOs[i].isListisshowable()) {
					StringItemVO aa = new StringItemVO(templetItemVOs[i].getItemname());
					if (!templetItemVOs[i].isCanSave() && templetItemVOs[i].isNeedSave()) {
						aa.setForeGroundColor(aa.getStringValue() + ";FF0000");
					}
					if ("Y".equalsIgnoreCase(templetItemVOs[i].getIsmustinput2()) || "W".equalsIgnoreCase(templetItemVOs[i].getIsmustinput2())) {
						aa.setStringValue(aa.getStringValue() + "※");
					}
					if (!templetItemVOs[i].isViewColumn()) {
						aa.setStringValue(aa.getStringValue() + "★");
					}
					if (!templetItemVOs[i].isNeedSave()) {
						aa.setStringValue(aa.getStringValue() + "◎");
					}
					bl.insertRow(bl.getRowCount(), new Object[] { (bl.getRowCount() + 1) + "", aa, new StringItemVO(templetItemVOs[i].isCanSave() ? "是" : "否"), new StringItemVO(templetItemVOs[i].isNeedSave() ? "是" : "否"), new StringItemVO(templetItemVOs[i].isViewColumn() ? "是" : "否") });

					bl.setRowStatusAs(bl.getRowCount() - 1, WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			}
			p.add(bl);
		}
		BillDialog bd = new BillDialog(parent, "模板子项状态查看", 800, 500);
		bd.add(p);
		bd.addConfirmButtonPanel(2);
		bd.setVisible(true);
	}

	public JLabel getTableCellRendererComponent_(JLabel a, JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
		if ("是".equals(a.getText())) {
			a.setIcon(UIUtil.getImage("zt_028.gif"));
			a.setText("");
		} else if ("否".equals(a.getText())) {
			a.setIcon(UIUtil.getImage("zt_031.gif"));
			a.setText("");
		}
		if (flag1) {
			a.setBorder(BorderFactory.createLineBorder(new Color(99, 130, 191), 0)); //
		} else {
			a.setBorder(BorderFactory.createEmptyBorder()); //
		}
		if (flag) {
			a.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			if (i % 2 == 0) {
				a.setBackground(LookAndFeel.table_bgcolor_odd); //
			} else {
				a.setBackground(LookAndFeel.tablebgcolor); //
			}
		}
		return a;
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}
}
