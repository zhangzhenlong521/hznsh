package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 系统中参数越来越多,如果找到某个参数并快速修改已成了一个工作量较大的事!
 * 尤其是首页,工作流,Office控件等,现在主要有两个问题,一个是系统到底有多少参数许多人不知道!
 * 二是没有在使用参数的地方挑选出本处最关心的几个参数进行处理! 如果把工作流所有参数都拉出来,其实是太多,没有起到快速配置的效果!!!
 * 即量变导致质变,最好的方法是在某个具体点的地方,只弹出这个点需要的参数!! 随即修改!!!
 * 需要做两个处理,一个是查询数据库中的,一个是所有定义的!搞两个页签!
 * @author Administrator
 *
 */
public class SystemOptionsDialog extends BillDialog implements ActionListener {

	private String optionType; //参数类型
	private String[] initOptions; //初始化参数

	private BillListPanel list_db = null; //
	private WLTButton btn_confirm, btn_reloadCache; //

	/**
	 * 
	 * @param _parent
	 * @param _type  类型,对应于SystemOptions中的getAllPlatformOptions方法中的二维数组中的第一列名称...
	 * @param _optins  可以只送入某几个参数,如果为空,则把前一个参数_type中定义的统统拉出来!
	 */
	public SystemOptionsDialog(java.awt.Container _parent, String _optionType, String[] _initOptins) {
		super(_parent, "修改[" + _optionType + "]相关系统参数", 950, 600); //
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " 【SystemOptionsDialog】"); //
		}
		this.optionType = _optionType; //
		this.initOptions = _initOptins; //

		initialize(); //
	}

	/**
	 * 构造界面
	 */
	private void initialize() {
		try {
			String[][] str_options = UIUtil.getCommonService().getAllPlatformOptions(optionType); //取得所有参数

			JTabbedPane tabb = new JTabbedPane(); //
			tabb.addTab("本处参数在数据库中的值", getDBPanel()); //数据库中的值!

			//系统中所有定义本模块所有参数,即SystemOptions.getAllPlatformOptions()中定义的
			HashVO[] hvs = new HashVO[str_options.length]; //
			HashSet hst = new HashSet(); // 
			for (int i = 0; i < hvs.length; i++) {
				hvs[i] = new HashVO(); //
				hvs[i].setAttributeValue("参数名", str_options[i][1]); //
				hvs[i].setAttributeValue("默认值", str_options[i][2]); //
				hvs[i].setAttributeValue("说明", str_options[i][3]); //
				hst.add(str_options[i][1]); //
			}
			BillListPanel list_all = new BillListPanel("请注意：在SystemOptions中定义的,可能会因为开发过快而没有补全", hvs); //
			list_all.getTitleLabel().setForeground(Color.RED); //;
			tabb.addTab("【" + optionType + "】所有参数", list_all); //所有定义的值!

			//本处的特定参数
			if (initOptions != null) { //如果有本处参数!!!
				BillVO[] dbVOs = list_db.getAllBillVOs(); //
				HashSet hst_db = new HashSet(); //
				for (int i = 0; i < dbVOs.length; i++) {
					hst_db.add(dbVOs[i].getStringValue("parkey")); //
				}
				HashVO[] hvs_thisdef = new HashVO[initOptions.length]; //
				for (int i = 0; i < hvs_thisdef.length; i++) {
					hvs_thisdef[i] = new HashVO(); //
					hvs_thisdef[i].setAttributeValue("参数名", initOptions[i]); //
					if (hst.contains(initOptions[i])) { //如果数据库中已定义
						hvs_thisdef[i].setAttributeValue("系统是否定义", "Y");
					} else {
						hvs_thisdef[i].setAttributeValue("系统是否定义", "N,要在SystemOptions中补全");
					}

					if (hst_db.contains(initOptions[i])) { //如果数据库中已定义
						hvs_thisdef[i].setAttributeValue("数据库中是否定义", "Y,直接在第一个页签中修改"); //
					} else { //如果数据库中没定义
						hvs_thisdef[i].setAttributeValue("数据库中是否定义", "N,手工拷贝到第一个页签中新增"); //
					}
				}
				BillListPanel li_thisdef = new BillListPanel(hvs_thisdef); //
				tabb.addTab("本处参数清单", li_thisdef); //
			}
			this.getContentPane().add(tabb); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.getContentPane().removeAll(); //
			this.getContentPane().setLayout(new BorderLayout()); //
			this.getContentPane().add(new JLabel("构造界面发生异常[" + ex.getClass() + "][" + ex.getMessage() + "],请至控制台查看详细原因...")); //
		}
	}

	/**
	 * 数据库中的值!
	 * @return
	 * @throws Exception
	 */
	private JPanel getDBPanel() throws Exception {
		JPanel panel = new JPanel(new BorderLayout()); //
		list_db = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_option_CODE1.xml")); //
		list_db.setQuickQueryPanelVisiable(true); //
		list_db.setAllBillListBtnVisiable(false); //

		WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		list_db.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete }); //
		list_db.repaintBillListButton(); //

		if (initOptions != null) {
			list_db.queryDataByCondition("parkey in (" + TBUtil.getTBUtil().getInCondition(initOptions) + ")", "parkey asc"); //
		}
		panel.add(list_db); //
		return panel; //
	}

	private JPanel getSouthPanel() {
		btn_confirm = new WLTButton("关闭"); //
		btn_reloadCache = new WLTButton("刷新缓存"); //

		btn_confirm.addActionListener(this);
		btn_reloadCache.addActionListener(this);

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //

		panel.add(btn_reloadCache); //
		panel.add(btn_confirm); //

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else if (e.getSource() == btn_reloadCache) { //
			try {
				if (!MessageBox.confirm(this, "你是否真的想刷新缓存吗?这将影响所有人的操作效果!\r\n刷新后需要重新登录才能看到UI端参数效果!")) {
					return;
				} //
				String[][] str_result = UIUtil.getCommonService().reLoadDataFromDB(false); //
				if (str_result != null) {
					ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions()); //重新得到所有参数
				}
				MessageBox.show(this, "刷新参数成功,需要重新登录才能看到UI端参数效果!"); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		}
	}
}
