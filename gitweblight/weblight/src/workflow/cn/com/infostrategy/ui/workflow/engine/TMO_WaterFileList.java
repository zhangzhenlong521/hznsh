package cn.com.infostrategy.ui.workflow.engine;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_WaterFileList extends AbstractTMO {
	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "id"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "id"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "processid"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "流程主键"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "processid"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "filename"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "盖章文件"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "filename"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "600"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("listishtmlhref", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "fileid"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "文件别名"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "fileid"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "userid"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "用户主键"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "processid"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "username"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "加印人"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "username"); // 显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150*20"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "waterFileListPanel"); // 模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "已盖章文件"); // 模板名称
		vo.setAttributeValue("templetname_e", "waterFileListPanel"); // 模板名称
		vo.setAttributeValue("Islistautorowheight", "Y"); // 模板名称列表是否自动撑高
		return vo;
	}
}