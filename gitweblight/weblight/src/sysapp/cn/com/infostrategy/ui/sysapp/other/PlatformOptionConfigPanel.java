package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * 平台参数配置
 * @author lcj
 *
 */
public class PlatformOptionConfigPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel platformOptionPanel = null;
	private WLTButton btn_reloadCache, btn_addNewOptions = null; //

	public void initialize() {		
		/***
		 * 增加参数的模块名称过滤 Gwang 2013-06-22
		 * 用户只配置本模块的下拉框, 如果要新增还是要去平台配置中操作
		 * 通过菜单参数"模块名称"指定, 按==方式查询
		 */
		String moduleName = this.getMenuConfMapValueAsStr("模块名称", "");
		
		this.setLayout(new BorderLayout());
		platformOptionPanel = new BillListPanel("pub_option_CODE1");
		/*
		*平台重新设置了在线人数上限逻辑，该配置不在数据库中取了，而是在weblight中配置“PROJECT_MU”值为“50,3”的16进制码，即“35302C33”，
		*表示在发呆3分钟内账户最多同时有50个（相同账号重新计数），如果达到50个，则表示达到在线人数上限，则不能登录。【李春娟/2012-06-07】 
		*/
		WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //新增
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
		btn_reloadCache = new WLTButton("刷新参数缓存"); //
		btn_addNewOptions = new WLTButton("比较并添加参数"); //
		btn_reloadCache.addActionListener(this); //
		btn_addNewOptions.addActionListener(this); //		
				
		String filter = " parkey not in('注销时是否显示输入管理密码') ";
		//增加参数的模块名称过滤 Gwang 2013-06-22
		if (!"".equals(moduleName)) {
			filter += "and modulename = '" + moduleName + "' ";
			platformOptionPanel.getQuickQueryPanel().setVisible(false);
			platformOptionPanel.addBatchBillListButton(new WLTButton[] { btn_update, btn_reloadCache }); //如果配置了模块名称, 不能新增
		}else {
			platformOptionPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_reloadCache, btn_addNewOptions }); //
		}
		
		platformOptionPanel.repaintBillListButton();
		platformOptionPanel.setDataFilterCustCondition(filter);
		platformOptionPanel.QueryDataByCondition(null);

		this.add(platformOptionPanel);
	}

	private void onReloadCache() {
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

	/**
	 * 新增参数!!!
	 */
	private void insertBatchOption() {
		try {
			String[][] allPlatformOptions = UIUtil.getCommonService().getAllPlatformOptions(); //所有注册的参数!!!
			HashMap allDBOptions = UIUtil.getHashMapBySQLByDS(null, "select parkey,parvalue from pub_option"); //

			StringBuilder sb_info = new StringBuilder(); //
			ArrayList al_news = new ArrayList(); //
			for (int i = 0; i < allPlatformOptions.length; i++) {
				if (!allDBOptions.containsKey(allPlatformOptions[i][1])) { //如果数据库存中没有,则加入!!
					sb_info.append("【" + allPlatformOptions[i][0] + "】【" + allPlatformOptions[i][1] + "】=【" + allPlatformOptions[i][2] + "】\r\n"); ///
					al_news.add(allPlatformOptions[i]); //
				}
			}
			if (al_news.size() == 0) {
				MessageBox.show(this, "没有发现需要新增的参数!"); //
				return; //
			}
			if (!MessageBox.confirm(this, "系统发现[" + al_news.size() + "]个新参数如下:\r\n你是否真的想加入它们?\r\n如果想全部加入,点击【是】!!\r\n如果只要单独加入其中几个,则拷贝下来,点击【否】后手工添加!\r\n\r\n新参数清单:\r\n" + sb_info.toString())) {
				return; //
			}

			String[] str_sqls = new String[al_news.size()]; //
			for (int i = 0; i < al_news.size(); i++) {
				String[] str_item = (String[]) al_news.get(i); //
				InsertSQLBuilder isql = new InsertSQLBuilder("pub_option"); //
				isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_OPTION")); //
				isql.putFieldValue("modulename", str_item[0]); //
				isql.putFieldValue("parkey", str_item[1]); //
				isql.putFieldValue("parvalue", str_item[2]); //
				isql.putFieldValue("pardescr", str_item[3]); //
				str_sqls[i] = isql.getSQL(); //
			}
			UIUtil.executeBatchByDS(null, str_sqls); //
			MessageBox.show(this, "添加[" + al_news.size() + "]个新参数成功,请点击查询按钮重新进行查看!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_reloadCache) {
			onReloadCache(); //
		} else if (e.getSource() == btn_addNewOptions) {
			insertBatchOption(); //
		}
	}

}
