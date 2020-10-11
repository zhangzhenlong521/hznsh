package cn.com.infostrategy.ui.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ReportUIUtil {

	public synchronized static void openBillCardHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillCardHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openBillListHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillListHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openBillTreeHtmlFrame(String _title, String _templetCode, String _sql) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getBillTreeHtml"); //
			parMap.put("templetCode", _templetCode); //
			parMap.put("sql", _sql);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public synchronized static void openMultiHtmlFrame(String _title, String[][] _multiSQLs) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("calltype", "getMultiHtml"); //
			parMap.put("multisqls", _multiSQLs);
			UIUtil.openBillHtmlFrame(_title, "cn.com.infostrategy.bs.report.ReportWebCallBean", parMap); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 钻取明細,因为万维报表与图表中都存在钻取明细的逻辑,为了共享同一份代码,所以长期到这里了!
	 * @param _parent
	 * @param str_ids
	 * @param _objItemVO
	 * @param str_builderClassName
	 * @param queryConditionMap
	 * @param _defaultDetailColNames
	 * @param str_drillActionClass
	 * @param str_drillTempletCode
	 * @throws Exception
	 * 
	 */
	public void onDrillDetail(java.awt.Container _parent, String str_ids, Object _objItemVO, String str_builderClassName, HashMap queryConditionMap, String[] _defaultDetailColNames, String str_drillActionClass, String str_drillTempletCode) throws Exception {
		//首先说明点击数字进行钻取必须具有相同的模板，否则无法进行显示  [袁江晓2012-08-21]
		//袁江晓  2012-08-21 更改逻辑
		//基本逻辑：如果sql语句中没有id则直接退出，否则走下面逻辑(注：有#表示有多个表组合而成的数据，即模板不一样)
		//优先级从高到低：如果没有反射类也没有模板并且id也没有#则走默认的维度，否则如果有反射类则走反射类，如果是多个表组合而成的则走不同的模板，否则如果只有一个模板则走一个模板，
		//根据李国立要求，譬如说如果点击数字之类的可以不需要id因此将str_drillActionClass提到最外面
		if(str_drillActionClass != null){   //
			 //如果定义以反射类,则直接调用反射类!
			Object obj = Class.forName(str_drillActionClass).newInstance(); // 实例化钻取的实现类。
			BillReportDrillActionIfc action = (BillReportDrillActionIfc) obj;
			if (obj instanceof BillReportDrillActionIfc) {
				action.drillAction(str_ids, _objItemVO, _parent,queryConditionMap); // 执行
			}
		}else if (str_ids == null) { //袁江晓更改，将次判断提到外面   对于显示为0则不进行钻取
			//MessageBox.show(_parent, "没有id字段,结果#value为空!所以无法做钻取操作!"); //既没有定义反射类,也没有定义模板,
			return;
		}else if(str_drillTempletCode == null&& str_ids.indexOf("#")==-1) {//如果什么都没有则走默认的
			 //如果两者都为空!则直接弹出ids,然后从服务器端再次查询数据,得到HashVO[],并过滤!!!然后直接使用动态构建BillListPanel的方式弹出
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashVO[] hvsDetail = service.queryMultiLevelReportDataDrillDetail(queryConditionMap, str_builderClassName, str_ids); //
			if (hvsDetail.length <= 0) {
				MessageBox.show(_parent, "根据ids=[" + str_ids + "]钻取明细的结果竟然为零,可能是数据发生了变化!请重新查询试下!"); //
				return; //
			}
			String[] str_keys = hvsDetail[0].getKeys(); //实际的所有列!
			TBUtil.getTBUtil().sortStrsByOrders(str_keys, _defaultDetailColNames); //排序一把
			String[][] str_tmodefine = new String[str_keys.length][2]; //
			for (int i = 0; i < str_keys.length; i++) {
				if (TBUtil.getTBUtil().isExistInArray(str_keys[i], _defaultDetailColNames)) { //只显示参与维度计算的列!
					str_tmodefine[i] = new String[] { str_keys[i], str_keys[i], "105", "Y" }; //
				} else {
					str_tmodefine[i] = new String[] { str_keys[i], str_keys[i], "105", "N" }; //
				}
			}
			DefaultTMO tmo = new DefaultTMO("钻取明细", str_tmodefine); //
			BillListPanel list = new BillListPanel(tmo); //
			list.putValue(hvsDetail); //
			list.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //设置所有状态为Init，否则行事情都是绿色的,即新增
			list.setTitleLabelText("钻取明细,一共[" + hvsDetail.length + "]条记录"); //
			String str_title = "钻取明细"; //
			if (ClientEnvironment.isAdmin()) {
				str_title = str_title + " 【在构造类中重构getDrillTempletCode(),就可以用指定模板显示明细,当前类名是" + str_builderClassName + "】";
			}
			BillDialog dialog = new BillDialog(_parent, str_title, 900, 500);
			dialog.getContentPane().add(list); //
			dialog.addConfirmButtonPanel(2); //
			dialog.setVisible(true); //
		
		}else{
			if(str_ids.indexOf("#")>0){  //袁江晓添加，解决特殊情况：对于一个统计结果是从多个表得到的数据，不同的数据可能对于不同的模板，在前台封装数据时将ID+'#'+模板名称这样的方式来组装IDS，并且前台不需要再重写getDrillTempletCode方法
				/*
				 * 此部分判断主要解决一个表中有多个表的模板  [袁江晓2012-08-21添加]
				 */
				String []str_id=new TBUtil().split(str_ids, ";");
				String template="";
				List <String>listId=new ArrayList<String>();
				//获取模板
				if(null!=str_id[0]&&!str_id[0].equals("")&&str_id[0].indexOf("#")>0){//第一个不为null并且不为空字符串并且有#号
					template=str_id[0].substring( str_id[0].indexOf("#")+1);
				}
				//StringBuffer strids=new StringBuffer();
				//首先需要对ids进行截取，去除掉#后再拼接成ids
				for(int i=0;i<str_id.length;i++){  //20121217袁江晓更改，之前为什么是str_id-1呢，想不明白
					String str_temp=str_id[i].substring(0, str_id[i].indexOf("#"));
					listId.add(str_temp);
					//strids.append(str_temp).append(",");//进行拼接，去除掉"#"后再进行id拼接
				}
				//strids.append(str_id[str_id.length-1].substring(0, str_id[str_id.length-1].indexOf("#")));
				Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(template);
				templetVO.setAutoLoads(0); //默认不加载
				templetVO.setDatapolicy(""); //去掉数据策略
				templetVO.setDatapolicymap("");
				templetVO.setDataconstraint("");//去掉默认的权限策略，因为在统计的时候已经加入了权限策略，如果这里不去除掉，则显示的数目跟前台统计的数目不一致     [袁江晓2012-08-20]
				templetVO.setAutoloadconstraint("");//去掉默认的权限策略，因为在统计的时候已经加入了权限策略，如果这里不去除掉，则显示的数目跟前台统计的数目不一致    [袁江晓2012-08-20]
				templetVO.setIsshowlistpagebar(true);//不分页，否则前台显示的数据后台一个页面最多只能显示20条    [袁江晓2012-08-20]
				BillListPanel drillList = new BillListPanel(templetVO);
				//drillList.setBillQueryPanelVisible(true); //隐藏查询
				drillList.setPagePanelVisible(true); //分页
				drillList.setItemEditable(false); //不可编辑!!
				drillList.setQuickQueryPanelVisiable(false);//不可以查询只能显示
				drillList.setAllBillListBtnVisiable(false);//禁用所用的按钮只让浏览按钮管用 [袁江晓2012-08-20]
				drillList.setIsRefreshParent("1");//设置不刷新模板 袁江晓 20130423添加
				WLTButton btn_lookupresult;
				btn_lookupresult=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);//添加浏览按钮
				drillList.addBillListButton(btn_lookupresult);
				drillList.repaintBillListButton();
				//获取列名
				StringBuffer str_col=new StringBuffer();
				if(drillList.getColumnModel().getColumnCount()>0){
					for(int k=0;k<drillList.getColumnModel().getColumnCount();k++ ){
						str_col.append(drillList.getColumnModel().getColumn(k).getHeaderValue().toString());
					}
				}
				String tempstr=TBUtil.getTBUtil().getInCondition(listId);
				drillList.QueryDataByCondition(drillList.getTempletVO().getPkname() +" in(" + tempstr+")"); //如果开发人员自定义模板，数据需要根据主键ID查一遍。[郝明2012-08-02]  20121126袁江晓更改 主要防止sql语句超过1000报错的bug
				drillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //设置所有状态为Init，否则行事情都是绿色的,即新增
				String str_title = "钻取明细"; //
				if (ClientEnvironment.isAdmin()) {
					str_title = str_title + ",实际返回数据的列是【" + str_col.toString() + "】"; //实施人员可以直接看到可以在模板中增加哪些列!!
				}
				drillList.setTitleLabelText("钻取明细,一共有[" + str_id.length + "]条记录"); //
				BillDialog dialog = new BillDialog(_parent, str_title, 800, 600); //
				dialog.getContentPane().add(drillList);
				dialog.addConfirmButtonPanel(2);
				dialog.setVisible(true);
			}else if (str_drillTempletCode != null) { //如果没有定义反射类,则尝试使用模板编码个钻取出来!
				Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(str_drillTempletCode);
				templetVO.setAutoLoads(0); //默认不加载
				templetVO.setDatapolicy(""); //去掉数据策略
				templetVO.setDatapolicymap("");
				templetVO.setDataconstraint("");//去掉默认的权限策略，因为在统计的时候已经加入了权限策略，如果这里不去除掉，则显示的数目跟前台统计的数目不一致     [袁江晓2012-08-20]
				templetVO.setAutoloadconstraint("");//去掉默认的权限策略，因为在统计的时候已经加入了权限策略，如果这里不去除掉，则显示的数目跟前台统计的数目不一致    [袁江晓2012-08-20]
				templetVO.setIsshowlistpagebar(true);//不分页，否则前台显示的数据后台一个页面最多只能显示20条    [袁江晓2012-08-20]
				BillListPanel drillList = new BillListPanel(templetVO);
				//drillList.setBillQueryPanelVisible(true); //隐藏查询
				drillList.setPagePanelVisible(true); //分页
				drillList.setItemEditable(false); //不可编辑!!
				drillList.setQuickQueryPanelVisiable(false);//不可以查询只能显示
				drillList.setAllBillListBtnVisiable(false);//禁用所用的按钮只让浏览按钮管用 [袁江晓2012-08-20]
				drillList.setIsRefreshParent("1");//设置不刷新模板  袁江晓 20130423添加
				WLTButton btn_lookupresult;
				btn_lookupresult=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);//添加浏览按钮
				drillList.addBillListButton(btn_lookupresult);
				drillList.repaintBillListButton();
				//远程取数,置入界面!!!
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvsDetail = service.queryMultiLevelReportDataDrillDetail(queryConditionMap, str_builderClassName, str_ids); //
				//				drillList.putValue(hvsDetail); //塞入数据!!
			/*	StringBuilder incondition = new StringBuilder("-999999"); //id串
				for (int i = 0; i < hvsDetail.length; i++) {
					String id = hvsDetail[i].getStringValue("id");
					if (id != null && !id.equals("")) {
						incondition.append("," + id);
					}
				}*/
				List <String>listId=new ArrayList<String>();
				for (int i = 0; i < hvsDetail.length; i++) {
					String id = hvsDetail[i].getStringValue("id");
					if (id != null && !id.equals("")) {
						listId.add(id);
					}
				}
				String tempstr=TBUtil.getTBUtil().getInCondition(listId);
				StringBuilder sb_info = new StringBuilder(); //
				if (hvsDetail.length > 0) {
					String[] str_keys = hvsDetail[0].getKeys(); //实际的所有列!
					for (int i = 0; i < str_keys.length; i++) {
						sb_info.append(str_keys[i] + ","); //
					}
				}
				drillList.QueryDataByCondition(drillList.getTempletVO().getPkname() +" in(" + tempstr+")"); //如果开发人员自定义模板，数据需要根据主键ID查一遍。[郝明2012-08-02]  20121126袁江晓更改 主要防止sql语句超过1000报错的bug
				drillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //设置所有状态为Init，否则行事情都是绿色的,即新增
				String str_title = "钻取明细"; //
				if (ClientEnvironment.isAdmin()) {
					str_title = str_title + ",实际返回数据的列是【" + sb_info.toString() + "】"; //实施人员可以直接看到可以在模板中增加哪些列!!
				}
				drillList.setTitleLabelText("钻取明细,一共有[" + hvsDetail.length + "]条记录"); //
				BillDialog dialog = new BillDialog(_parent, str_title, 800, 600); //
				dialog.getContentPane().add(drillList);
				dialog.addConfirmButtonPanel(2);
				dialog.setVisible(true);
			}
		}
	}

}
