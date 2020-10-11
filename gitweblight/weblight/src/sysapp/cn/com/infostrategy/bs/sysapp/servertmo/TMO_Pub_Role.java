package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_Pub_Role extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_ROLE_CODE1"); //模版编码，请勿随便修改
		vo.setAttributeValue("templetname", "角色管理"); //模板名称
		vo.setAttributeValue("tablename", "PUB_ROLE"); //查询数据的表(视图)名
		vo.setAttributeValue("pkname", "ID"); //主键名
		vo.setAttributeValue("pksequencename", "S_PUB_ROLE"); //序列名
		vo.setAttributeValue("savedtablename", "PUB_ROLE"); //保存数据的表名
		vo.setAttributeValue("datapolicy", "角色查询策略"); //查询策略
		vo.setAttributeValue("datapolicymap", "过滤方式=机构过滤;机构字段名=createdept;"); //策略映射【李春娟/2017-06-26】
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
		vo.setAttributeValue("isshowlistquickquery", "Y"); //卡片自定义面板
		vo.setAttributeValue("ordercondition", "ROLETYPE,CODE"); //卡片自定义面板
		vo.setAttributeValue("autoloads", "-1"); //卡片自定义面板
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式

		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ROLETYPE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "角色分类"); //显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_COMBOBOX); //控件类型
		itemVO.setAttributeValue("comboxdesc", "select id,code,name from pub_comboboxdict where type='角色分类' order by seq"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,150"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "角色编码"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,150"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "角色名称"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "Y"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,150"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "DESCR"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "备注"); //显示名称
		itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "300*100"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //通用查询是否可编辑
		itemVO.setAttributeValue("isCommQueryWrap", "Y"); //通用查询是否换行
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPPWD"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "超级管理员密码"); //显示名称
		itemVO.setAttributeValue("itemtype", "密码框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,100"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "超级管理员相关属性");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPCORPTYPE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "只限于哪些机构类型"); //显示名称
		itemVO.setAttributeValue("itemtiptext", "只有哪些机构类型的人才可以,比如只允许总行的人!取值就是机构类型,比如:总行部门;一级分行部门;"); //控件类型
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,100"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "超级管理员相关属性");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPDATAOBJS"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "对应的数据对象"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("itemtiptext", "该超级管理员只对哪些数据表起效,比如cmp_;rule_,即模板中的查询表或保存表只要对应上,则就能控制!"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "400*70"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //快速查询是否显示
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //快速查询是否可编辑
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //通用查询是否显示
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //通用查询是否可编辑
		itemVO.setAttributeValue("querywidth", "100,100"); //快速查询时的宽度
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "超级管理员相关属性");
		vector.add(itemVO);
		
		

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
