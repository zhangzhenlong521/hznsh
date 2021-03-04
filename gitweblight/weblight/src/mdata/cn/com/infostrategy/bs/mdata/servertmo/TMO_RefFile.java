package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_RefFile extends AbstractTMO {

	private static final long serialVersionUID = 1L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", ""); // 模版编码,请勿随便修改
		vo.setAttributeValue("templetname", ""); // 模板名称
		vo.setAttributeValue("templetname_e", ""); // 模板名称
		vo.setAttributeValue("ISSHOWLISTCUSTBTN", "Y"); // 模板名称

		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "filename"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "文件列表"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "filename"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_CUST); //为了实现显示时只显示文件名,不显示索引号的效果!改成参照
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "205"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "edit"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", ""); // 显示名称
		itemVO.setAttributeValue("itemname_e", ""); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "30"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // 如果可编辑
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "download"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", ""); // 显示名称
		itemVO.setAttributeValue("itemname_e", ""); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "30"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // 如果可编辑
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "delete"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", ""); // 显示名称
		itemVO.setAttributeValue("itemname_e", ""); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "30"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("listishtmlhref", "Y"); // 如果可编辑
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
