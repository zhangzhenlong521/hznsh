package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_Pub_ComboBoxDict extends AbstractTMO {

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "其他基础数据"); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "其他基础数据"); //模板名称
		vo.setAttributeValue("templetname_e", "其他基础数据"); //模板名称
		vo.setAttributeValue("tablename", "PUB_COMBOBOXDICT"); //查询数据的表(视图)名
		vo.setAttributeValue("pkname", "PK_PUB_COMBOBOXDICT"); //主键名
		vo.setAttributeValue("pksequencename", "S_PUB_COMBOBOXDICT"); //序列名
		vo.setAttributeValue("savedtablename", "PUB_COMBOBOXDICT"); //保存数据的表名
		vo.setAttributeValue("CardWidth", "577"); //卡片宽度
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
		vo.setAttributeValue("ordercondition", " seq asc "); //卡片自定义面板
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_COMBOBOXDICT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "PK_PUB_COMBOBOXDICT"); //显示名称
		itemVO.setAttributeValue("itemname_e", "PK_PUB_COMBOBOXDICT"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "70"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "200"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "真实值"); //显示名称
		itemVO.setAttributeValue("itemname_e", "ID"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "110"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "CODE"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "110"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "NAME"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "110"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SEQ"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "顺序"); //显示名称
		itemVO.setAttributeValue("itemname_e", "SEQ"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "60"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "DESCR"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "描述"); //显示名称
		itemVO.setAttributeValue("itemname_e", "DESCR"); //显示名称
		itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "200"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "420"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TYPE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "TYPE"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("listwidth", "110"); //列表时宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]); //
	}

}
