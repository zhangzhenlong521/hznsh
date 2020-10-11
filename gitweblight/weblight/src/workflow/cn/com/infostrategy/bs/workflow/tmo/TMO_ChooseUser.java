package cn.com.infostrategy.bs.workflow.tmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_ChooseUser extends AbstractTMO {

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", ""); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "历史提交者"); //模板名称
		vo.setAttributeValue("templetname_e", ""); //模板名称
		vo.setAttributeValue("tostringkey", "username"); //模板名称
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "currActivityid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "当前环节ID"); //显示名称
		itemVO.setAttributeValue("itemname_e", "currActivityid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "currActivitycode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "环节编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "currActivitycode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "currActivitytype"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "环节类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "currActivitytype"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "currActivityname"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "步骤"); //显示名称,后来统一叫"步骤",这样更口语化!!
		itemVO.setAttributeValue("itemname_e", "currActivityname"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "400"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "参与者ID"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "usercode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人工号"); //显示名称
		itemVO.setAttributeValue("itemname_e", "usercode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("ITEMTIPTEXT", "<html>即人员人号,登录号,编码,帐号,OA帐号,HR帐号,柜员号等.<br>因为人的名字可能重复,所以必须显示编码,且系统也是使用编码计算的!</html>"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "username"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "username"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "115"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userdeptid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人机构主键"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userdeptid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userdeptcode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人机构编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userdeptcode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userdeptname"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人机构"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userdeptname"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "400"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userroleid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人角色主键"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userroleid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userrolecode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人角色编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "username"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userrolename"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "接收人角色"); //显示名称
		itemVO.setAttributeValue("itemname_e", "userrolename"); //显示名称
		itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "275"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "400*100"); //卡片时宽度
		boolean isshowrole = (new TBUtil()).getSysOptionBooleanValue("是否显示接收人角色", true);
		itemVO.setAttributeValue("listisshowable", isshowrole == true ? "Y" : "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", isshowrole == true ? "Y" : "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "fromactivityid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "来自环节id"); //显示名称
		itemVO.setAttributeValue("itemname_e", "fromactivityid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "fromactivityname"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "来自环节"); //显示名称
		itemVO.setAttributeValue("itemname_e", "fromactivityname"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "动作ID"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitioncode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "动作编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitioncode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionname"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "动作名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionname"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionDealtype"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "动作类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionDealtype"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionIntercept"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "动作拦截器"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionIntercept"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "accruserid"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "授权人id"); //显示名称
		itemVO.setAttributeValue("itemname_e", "accruserid"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("colorformula", null); //颜色公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "125"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "accrusercode"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "授权人编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "accrusercode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("colorformula", null); //颜色公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "125"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "accrusername"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "授权人名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "accrusercode"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("colorformula", null); //颜色公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "140"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionMailSubject"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "邮件主题"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionMailSubject"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "transitionMailContent"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "邮件内容"); //显示名称
		itemVO.setAttributeValue("itemname_e", "transitionMailContent"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "successparticipantreason"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "成功参与的原因"); //显示名称
		itemVO.setAttributeValue("itemname_e", "successparticipantreason"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "90"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "135,200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "iseverprocessed"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "是否曾经走过"); //显示名称
		itemVO.setAttributeValue("itemname_e", "iseverprocessed"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "135,200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
