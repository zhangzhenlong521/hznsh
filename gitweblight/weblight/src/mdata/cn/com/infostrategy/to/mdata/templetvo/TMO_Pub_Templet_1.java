/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1.java,v $  $Revision: 1.21 $  $Date: 2012/11/19 06:16:28 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;

public class TMO_Pub_Templet_1 extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private boolean isAppConf = false; //是否实施模式!

	public TMO_Pub_Templet_1() {
	}

	public TMO_Pub_Templet_1(boolean _isAppConf) {
		this.isAppConf = _isAppConf; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_TEMPLET_1"); //模版编码，请勿随便修改
		vo.setAttributeValue("templetname", "模板主表"); //模板名称
		vo.setAttributeValue("templetname_e", "Pub_Templet_1"); //模板英文名称
		vo.setAttributeValue("datasourcename", null); //数据源名称!!!空表示默认数据源!!
		vo.setAttributeValue("tablename", "pub_templet_1"); //查询数据的表(视图)名
		vo.setAttributeValue("dataconstraint", null); //数据权限
		vo.setAttributeValue("pkname", "pk_pub_templet_1"); //主键名
		vo.setAttributeValue("pksequencename", "S_PUB_TEMPLET_1"); //序列名
		vo.setAttributeValue("savedtablename", "pub_templet_1"); //保存数据的表名
		vo.setAttributeValue("CardLayout", "FLOWLAYOUT"); //卡片布局
		vo.setAttributeValue("CardWidth", "1000"); //卡片宽度
		vo.setAttributeValue("CardBorder", "BORDER"); //卡片控件的边框
		vo.setAttributeValue("Isshowcardborder", "N"); //卡片是否显示边框
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("Isshowlistquickquery", "N"); //列表是否显示快速查询
		vo.setAttributeValue("isshowlistcustbtn", "Y"); //列表是否显示自定义按钮栏
		vo.setAttributeValue("Listcustbtndesc", "comm_listselect;"); //列表自定义按钮描述
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "pk_pub_templet_1"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); //显示名称
		itemVO.setAttributeValue("itemname_e", "PK_ID"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "模板编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Templet Code"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "模板名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Templet Name"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETNAME_E"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "模板英文名称"); //显示名称
			itemVO.setAttributeValue("itemname_e", "English Name"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATASOURCENAME"); //数据源名称
			itemVO.setAttributeValue("itemname", "数据源名称"); //显示名称
			itemVO.setAttributeValue("itemname_e", "DataSourceName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TABLENAME"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询数据表名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Query TabName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "TABLE2:Select t1.tname 主键#,t1.tname 表名,nvl(t2.comments, t1.tname) 说明, t1.tabtype 类型  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE', 'VIEW') and 1 = 1 order by 表名"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATACONSTRAINT"); //数据权限
			itemVO.setAttributeValue("itemname", "查询时SQL条件"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Filter Condition"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "AUTOLOADCONSTRAINT"); //数据权限
			itemVO.setAttributeValue("itemname", "自动加载时SQL条件"); //显示名称
			itemVO.setAttributeValue("itemname_e", "AutoLoadConstraint"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ORDERCONDITION"); //数据权限
			itemVO.setAttributeValue("itemname", "排序条件"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Order Condition"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "75"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "75,85"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "AUTOLOADS"); //数据权限
			itemVO.setAttributeValue("itemname", "自动加载数据"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Autoloads"); //显示名称
			itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "25"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "110,30"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);
			

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATASQLANDORCONDITION"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询条件与数据权限sql拼接方式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "DATASQLANDORCONDITION"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "select 'and' id,'and' code,'and' name from wltdual union all select 'or' id,'or' code,'or' name from wltdual"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200,90"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATAPOLICY"); //数据权限
			itemVO.setAttributeValue("itemname", "数据权限策略"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Datapolicy"); //显示名称
			itemVO.setAttributeValue("itemtype", "参照"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getTableRef(\"select id 主键$,'' code$,name 权限策略 from pub_datapolicy order by seq\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", "setRefItemName(\"DATAPOLICY\",getColValue(\"pub_datapolicy\",\"name\",\"id\",getItemValue(\"DATAPOLICY\")))"); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATAPOLICYMAP"); //数据权限映射
			itemVO.setAttributeValue("itemname", "策略映射"); //显示名称
			itemVO.setAttributeValue("itemname_e", "DatapolicyMap"); //显示名称
			itemVO.setAttributeValue("itemtype", "自定义参照"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.mdata.DataPolicyMapConfRefDialog\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "300"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "BSDATAFILTERCLASS"); //数据权限
			itemVO.setAttributeValue("itemname", "BS端数据过滤器"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Bsdatafilterclass"); //显示名称
			itemVO.setAttributeValue("itemtype", "自定义参照"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCustRef(\"静态与动态脚本定义\",\"cn.com.infostrategy.bs.mdata.AbstractBillDataBSFilter\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "138"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "300"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "BSDATAFILTERISSQL"); //BS端过滤器是否返回SQL
			itemVO.setAttributeValue("itemname", "BS过滤器是否返回SQL"); //显示名称
			itemVO.setAttributeValue("itemname_e", "bsdatafilterissql"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "170,30"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "SAVEDTABLENAME"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "保存数据表名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Saved Table"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "TABLE2:Select t1.tname 主键#,t1.tname 表名,nvl(t2.comments, t1.tname) 说明, t1.tabtype 类型  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE') and 1 = 1 order by 表名"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PKNAME"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "PK FieldName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "80,35"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PKSEQUENCENAME"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "主键序列名"); //显示名称
			itemVO.setAttributeValue("itemname_e", "PK SequenceName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", ""); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "100,135"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TOSTRINGKEY"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "ToString的列"); //显示名称
			itemVO.setAttributeValue("itemname_e", "TOSTRINGKEY"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", ""); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "100,75"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDLAYOUT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片布局"); //显示名称
			itemVO.setAttributeValue("itemname_e", "CardLayout"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "select 'FLOWLAYOUT' id,'FLOWLAYOUT' code,'FLOWLAYOUT' name from wltdual union all select 'XYLAYOUT' id,'XYLAYOUT' code,'XYLAYOUT' name from wltdual"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //下拉框定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTOPEBAR"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示数据库按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "IsListShowBtn"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "180,60"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDWIDTH"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片宽度"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Card Width"); //显示名称
			itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDBORDER"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片控件边框风格"); //显示名称
			itemVO.setAttributeValue("itemname_e", "CardBorder"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "select 'BORDER' id,'BORDER' code,'BORDER' name from wltdual union all select 'LINE' id,'LINE' code,'LINE' name from wltdual"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,80"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCARDBORDER"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示Debug框"); //显示名称
			itemVO.setAttributeValue("itemname_e", "IsShowDebugBorder"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCARDCUSTBTN"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示自定按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ISSHOWCARDCUSTBTN"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cardsaveifcheck"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片保存不做必填验证"); //显示名称
			itemVO.setAttributeValue("itemname_e", "cardsaveifcheck"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDCUSTPANEL"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片自定义面板"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Card Cust Panel"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "425"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Cardcustbtndesc"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Cardcustbtndesc"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //控件类型,注册样板参照
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"注册按钮\",\"\",\"4\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Cardinitformula"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片初始化公式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Cardinitformula"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //控件类型,大文本框
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cardsaveselfdesccheck"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "卡片自定义验证"); //显示名称
			itemVO.setAttributeValue("itemname_e", "cardsaveselfdesccheck"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //控件类型,大文本框
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);

			/*itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "GROUPISONLYONE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否只能同时展开一组"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Groupisonlyone"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,30"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "卡片参数");
			vector.add(itemVO);*/

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTPAGEBAR"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表是否显示分页"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is Show ListPageBar"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "140,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISLISTPAGEBARWRAP"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "分页条是否换行"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is PageBar Wrap"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "140,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTQUICKQUERY"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示查询面板"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Isshowlistquickquery"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "160,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO); //

			//因为招行临时部署,暂时隐掉!
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCOMMQUERYBTN"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示通用查询按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Isshowcommquerybtn"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "160,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISCOLLAPSEQUICKQUERY"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否收起查询面板"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Iscollapsequickquery"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "160,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ListHeaderIsGroup"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "表头是否分组"); //显示名称
			itemVO.setAttributeValue("itemname_e", "listheaderisgroup"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "140,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Islistautorowheight"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否自动撑高"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Islistautorowheight"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "140,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTCUSTBTN"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示快速按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ISSHOWLISTCUSTBTN"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "160,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTHEADHEIGHT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表表头高度"); //显示名称
			itemVO.setAttributeValue("itemname_e", "listheadheight"); //显示名称
			itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "160,25"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTROWHEIGHT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表行高"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ListRowHeight"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "100,125"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTCUSTPANEL"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表自定义面板"); //显示名称
			itemVO.setAttributeValue("itemname_e", "List Cust Panel"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "120,700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Listcustbtndesc"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Listcustbtndesc"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //控件类型,注册样板参照
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"注册按钮\",\"\",\"4\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);
			
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "listbtnorderdesc"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表按钮排序"); //显示名称
			itemVO.setAttributeValue("itemname_e", "listbtnorderdesc"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //控件类型,注册样板参照
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", ""); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Listinitformula"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表初始化公式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Listinitformula"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //控件类型,大文本框
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "listweidudesc"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "维度定义"); //显示名称
			itemVO.setAttributeValue("itemname_e", "listweidudesc"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型,大文本框
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO(); // gaofeng 自定义绘制器
			itemVO.setAttributeValue("itemkey", "definerenderer"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "自定义绘制器"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "definerenderer"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型,大文本框
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "400"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); // 是否换行
			itemVO.setAttributeValue("grouptitle", "列表参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEPK"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "树面板主键"); //显示名称
			itemVO.setAttributeValue("itemname_e", "TreePK"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEPARENTPK"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "树面板父亲主键"); //显示名称
			itemVO.setAttributeValue("itemname_e", "TreeParentPK"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEVIEWFIELD"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "树面板显示列"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeviewfield"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREESEQFIELD"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "树面板排序列"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeseqfield"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISSHOWROOT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示根结点"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeisshowroot"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISSHOWTOOLBAR"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否显示工具条"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeisshowtoolbar"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISONLYONE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否只能同时展开一层"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeisonlyone"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,30"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISCHECKED"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否是勾选框"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treeischecked"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150,30"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "树型参数");
			vector.add(itemVO);

			itemVO = new HashVO(); //
			itemVO.setAttributeValue("itemkey", "Treecustbtndesc"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "树型按钮"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Treecustbtndesc"); //显示名称
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //控件类型,注册样板参照
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"注册按钮\",\"\",\"4\")"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "400"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y"); //是否换行
			itemVO.setAttributeValue("grouptitle", "树型参数"); //
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPBEANCLASSNAME"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "属性BeanClass"); //显示名称
			itemVO.setAttributeValue("itemname_e", "PropBeanClassName"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "400"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "属性框参数"); //属性面板
			vector.add(itemVO);
			
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "WFCUSTEXPORT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "工作流自定义导出"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Wfcustexport"); //显示名称
			itemVO.setAttributeValue("ITEMTIPTEXT", "<html>【法律意见书◆表单项=aa,bb,cc;流程环节=风险合规部门,行领导;角色=部门负责人,处室负责人,行领导】<br>【法律报告@一级分行◆表单项=itemkey4,itemkey5,itemkey6;流程环节=风险合规部门,行领导;角色=部门负责人,处室负责人,行领导】</html>"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "工作流"); //属性面板
			vector.add(itemVO);
			
		}

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
