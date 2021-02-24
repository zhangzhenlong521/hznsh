package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_FormatPanelGetItem extends AbstractTMO {

	private static final long serialVersionUID = 1L;

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "各种控件与布局器"); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "各种控件与布局器"); //模板名称
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "名称"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "名称"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "70"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "例子"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "例子"); //显示名称
		itemVO.setAttributeValue("itemname_e", "例子"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "700"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "说明"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "说明"); //显示名称
		itemVO.setAttributeValue("itemname_e", "说明"); //显示名称
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); //控件类型
		itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "700*350"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]); //
	}

}
