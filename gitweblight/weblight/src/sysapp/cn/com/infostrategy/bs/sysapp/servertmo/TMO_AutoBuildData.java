package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_AutoBuildData extends AbstractTMO {

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_AUTOBUILDEDATA_CODE1"); //模版编码，请勿随便修改
		vo.setAttributeValue("templetname", "自动生成数据"); //模板名称
		vo.setAttributeValue("tablename", "wltdual"); //查询数据的表(视图)名
		vo.setAttributeValue("pkname", "id"); //主键名
		vo.setAttributeValue("pksequencename", "S_wltdual"); //序列名
		vo.setAttributeValue("savedtablename", "wltdual"); //保存数据的表名
		vo.setAttributeValue("CardWidth", "577"); //卡片宽度
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板

		vo.setAttributeValue("TREEPK", "id"); //列表是否显示操作按钮栏
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //列表是否显示操作按钮栏
		vo.setAttributeValue("Treeviewfield", "name"); //列表是否显示操作按钮栏
		vo.setAttributeValue("Treeisshowtoolbar", "Y"); //树型显示工具栏
		vo.setAttributeValue("Treeisonlyone", "N"); //树型显示工具栏
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemkey"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "字段"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "name"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "字段名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "info"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "字段说明"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "90"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "type"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "插入类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", "select '序列' id, '序列' code, '序列' name from wltdual " +
				" union all select '单值' id, '单值' code, '单值' name from wltdual union all select '多值' id, '多值' code, '多值' name from wltdual"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "90"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "value"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "取值"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "autono"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "是否有流水号"); //显示名称
		itemVO.setAttributeValue("itemname_e", ""); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
