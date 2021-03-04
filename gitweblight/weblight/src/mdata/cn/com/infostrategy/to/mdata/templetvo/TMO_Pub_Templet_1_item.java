/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1_item.java,v $  $Revision: 1.15 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;

public class TMO_Pub_Templet_1_item extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private boolean isAppConf = false; //是否实施模式!

	public TMO_Pub_Templet_1_item() {
	}

	//
	public TMO_Pub_Templet_1_item(boolean _isAppConf) {
		this.isAppConf = _isAppConf; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_TEMPLET_1_ITEM"); //模版编码，请勿随便修改
		vo.setAttributeValue("templetname", "模板子表"); //模板名称
		vo.setAttributeValue("templetname_e", "Pub_Templet_1_Item"); //模板名称
		vo.setAttributeValue("tablename", "PUB_TEMPLET_1_ITEM"); //查询数据的表(视图)名
		vo.setAttributeValue("pkname", "PK_PUB_TEMPLET_1_ITEM"); //主键名
		vo.setAttributeValue("pksequencename", "S_PUB_TEMPLET_1_ITEM"); //序列名
		vo.setAttributeValue("savedtablename", "PUB_TEMPLET_1_ITEM"); //保存数据的表名
		vo.setAttributeValue("CardLayout", "FLOWLAYOUT"); //卡片布局
		vo.setAttributeValue("CardWidth", "520"); //卡片宽度
		vo.setAttributeValue("CardBorder", "BORDER"); //卡片宽度
		vo.setAttributeValue("Isshowcardborder", "N"); //卡片是否显示边框
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_TEMPLET_1_ITEM"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "PK_PUB_TEMPLET_1"); //显示名称
		itemVO.setAttributeValue("itemname_e", "PK_PUB_TEMPLET_1"); //显示名称
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
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_TEMPLET_1"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "PK_PUB_TEMPLET_1"); //显示名称
		itemVO.setAttributeValue("itemname_e", "PK_PUB_TEMPLET_1"); //显示名称
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
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMKEY"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "ItemKey"); //显示名称
		itemVO.setAttributeValue("itemname_e", "ItemKey"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		if (isAppConf) {
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		} else {
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		}
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMNAME"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "ItemName"); //显示名称
		itemVO.setAttributeValue("itemname_e", "ItemName"); //显示名称
		itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "138*40"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ITEMNAME_E"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "ItemName_e"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ItemName_e"); //显示名称
			itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "138*40"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMTIPTEXT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "标签帮助"); //显示名称
		itemVO.setAttributeValue("itemname_e", "ItemTiptext"); //显示名称
		itemVO.setAttributeValue("itemtiptext", "鼠标移上去显示的详细标签,能起到帮助说明的作用!"); //显示名称
		itemVO.setAttributeValue("itemtype", "多行文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "138*40"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMTYPE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "控件类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Comp Type"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemType()"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "138"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		if (isAppConf) {
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		} else {
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		}
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) { //只有开发模式才加入,即实施模式是不显示这个的!!!
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "COMBOXDESC"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "下拉框说明"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ComboBox Desc"); //显示名称
			itemVO.setAttributeValue("itemtiptext", "最新的机制可以直接在控件定义中定义下拉框");
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCommUC(\"大文本框\",\"显示的控件按钮\",\"下拉框\");"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "395"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "REFDESC"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "控件定义"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Ref Desc"); //显示名称
			itemVO.setAttributeValue("itemtiptext", "以前下拉框要单独定义,\r\n后来改成所有控件都使用这一个字段定义!");
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCommUC(\"大文本框\",\"显示的控件按钮\",\"所有\");"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "450"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		//		itemVO = new HashVO();
		//		itemVO.setAttributeValue("itemkey", "isrefcanedit"); //唯一标识,用于取数与保存
		//		itemVO.setAttributeValue("itemname", "参照是否能编辑"); //显示名称
		//		itemVO.setAttributeValue("itemname_e", "Is Ref Can Edit"); //显示名称
		//		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		//		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		//		itemVO.setAttributeValue("refdesc", null); //参照定义
		//		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		//		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		//		itemVO.setAttributeValue("loadformula", null); //加载公式
		//		itemVO.setAttributeValue("editformula", null); //编辑公式
		//		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		//		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		//		itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
		//		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		//		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		//		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		//		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		//		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		//		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		//		itemVO.setAttributeValue("iswrap", "N");
		//		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "HYPERLINKDESC"); //右边超链接说明
			itemVO.setAttributeValue("itemname", "后接超链接"); //显示名称
			itemVO.setAttributeValue("itemname_e", "HyperLink Desc"); //英文显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "50"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "450"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISMUSTINPUT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "必输项类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Must Input"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框");//控件类型
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemMustInputType()"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "100"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSAVE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否参与保存"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is NeedSave"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISENCRYPT"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否加密保存"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is Encrypt"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISUNIQUECHECK"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "检验唯一性"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is Unique Check"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISKEEPTRACE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "是否保留痕迹"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Is Keep Trace"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LOADFORMULA"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "加载公式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Load Formual"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "450"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "EDITFORMULA"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "编辑公式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Edit Formual"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "450"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SHOWORDER"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "显示顺序"); //显示名称
		itemVO.setAttributeValue("itemname_e", "List Seq"); //显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "50"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "SHOWBGCOLOR"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "前景/背景颜色"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ShowBgColor"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DEFAULTVALUEFORMULA"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "默认值公式"); //显示名称
			itemVO.setAttributeValue("itemname_e", "DefaultValue Formual"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "GROUPTITLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "分组显示标题"); //显示名称
		itemVO.setAttributeValue("itemname_e", "GroupTitle"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "145"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		/*itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "UPLOADPATH"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "文件上传路径"); //显示名称
		itemVO.setAttributeValue("itemname_e", "UploadPath"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "145"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);*/

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISSHOWABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "卡片中是否显示"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Show in Card"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "卡片信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISEXPORT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "是否参与导出"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Export"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", "select '0' id,null code,'不导出' name from wltdual union all select '1' id,null code,'半列导出' name from wltdual union all select '2' id,null code,'全列导出' name from wltdual"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "100,75"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "卡片信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDWIDTH"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "卡片宽度"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Card Width"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "50"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "80,80"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "卡片信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISEDITABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "卡片中是否可编辑"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Edit in Card"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemEditable()"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "130,100"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "卡片信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISWRAP"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "卡片中是否换行"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Wrap"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "卡片信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISSHOWABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "列表中是否显示"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Show in List"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "列表配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISEXPORT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "列表中是否参与导出"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Export"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "列表配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTWIDTH"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "列表宽度"); //显示名称
		itemVO.setAttributeValue("itemname_e", "List Width"); //显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "50"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "80,80"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "列表配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISEDITABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "列表中是否可编辑"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is Edit in List"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemEditable()"); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "130,100"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "列表配置信息");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTISHTMLHREF"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表中是否是Html"); //显示名称
			itemVO.setAttributeValue("itemname_e", "List is HtmlHref"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "列表配置信息");
			vector.add(itemVO);
		}

		if (!isAppConf) { //列表是否合并[杨科2013-07-26]
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTISCOMBINE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "列表中是否合并"); //显示名称
			itemVO.setAttributeValue("itemname_e", "List is Combine"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", ""); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "125,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "列表配置信息");
			vector.add(itemVO);
		}
		
		
		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYITEMTYPE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询控件类型"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Query Type"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemType()"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "查询框配置信息");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYITEMDEFINE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询控件定义"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Query Define"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCommUC(\"大文本框\",\"显示的控件按钮\",\"所有\");"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "350"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "查询框配置信息");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYCREATETYPE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询创建器类型"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Query CreateType"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getQueryItemCreateType()"); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "查询框配置信息");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYCREATECUSTDEF"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "创建器自定义"); //显示名称
			itemVO.setAttributeValue("itemname_e", "Query CreateCustDef"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", "getCommUC(\"大文本框\",\"显示的控件按钮\",\"查询创建器\");"); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "350"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N"); //
			itemVO.setAttributeValue("grouptitle", "查询框配置信息");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUERYMUSTINPUT"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "是否必须输入查询条件"); //显示名称
		itemVO.setAttributeValue("itemname_e", "IsQueryMustInput"); //显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型
		itemVO.setAttributeValue("comboxdesc",
				"select 'N' id,'' code,'非必输项' name from wltdual union all select 'Y' id,'' code,'必输项' name from wltdual union all select 'A' id,'' code,'必输项A组' name from wltdual union all select 'B' id,'' code,'必输项B组' name from wltdual union all select 'C' id,'' code,'必输项C组' name from wltdual "); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "70"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150,80"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用.
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用.
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "QUERYWIDTH"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "查询框宽度"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Querywidth"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "80"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用.
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用.
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYDEFAULTFORMULA"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "查询默认条件"); //显示名称
			itemVO.setAttributeValue("itemname_e", "QueryDefaultFormula"); //显示名称
			itemVO.setAttributeValue("itemtype", "大文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "查询框配置信息");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYSHOWABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "快速查询是否显示"); //显示名称
		itemVO.setAttributeValue("itemname_e", "IsQuickQueryShowable"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYWRAP"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "快速查询是否换行"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Is QuickQuery Wrap"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "175,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息"); //
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYEDITABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "快速查询是否可编辑"); //显示名称
		itemVO.setAttributeValue("itemname_e", "IsQuickQueryEditable"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "195,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息"); //
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYSHOWABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "通用查询是否显示"); //显示名称
		itemVO.setAttributeValue("itemname_e", "IsCommQueryShowable"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYWRAP"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "通用查询是否换行"); //显示名称
		itemVO.setAttributeValue("itemname_e", "isCommQueryWrap"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "175,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYEDITABLE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "通用查询是否可编辑"); //显示名称
		itemVO.setAttributeValue("itemname_e", "IsCommQueryEditable"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "195,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "查询框配置信息");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPISSHOWABLE"); //itemkey
			itemVO.setAttributeValue("itemname", "属性框是否显示"); //显示名称
			itemVO.setAttributeValue("itemname_e", "PropIsShowable"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "属性配置信息");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPISEDITABLE"); //itemkey
			itemVO.setAttributeValue("itemname", "属性框是否可编辑"); //显示名称
			itemVO.setAttributeValue("itemname_e", "PropIsEditable"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "175,20"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "属性配置信息");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "WORKFLOWISEDITABLE"); //itemkey
		itemVO.setAttributeValue("itemname", "流程中是否可编辑"); //显示名称
		itemVO.setAttributeValue("itemname_e", "WorkFlowIsEditablE"); //显示名称
		itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "Y"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "80"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "130,20"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "流程配置信息");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1_item.java,v $  $Revision: 1.15 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: TMO_Pub_Templet_1_item.java,v $
 * Revision 1.15  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.2  2012/08/30 07:25:57  Administrator
 * 去掉了一个没用到的包
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.14  2012/02/06 12:01:07  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.13  2011/11/04 13:36:22  xch123
 * *** empty log message ***
 *
 * Revision 1.12  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.10  2011/06/14 11:46:19  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2011/06/14 09:54:04  xch123
 * *** empty log message ***
 *
 * Revision 1.8  2011/04/08 12:15:12  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2011/04/08 11:54:09  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2011/04/08 08:48:20  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2011/03/23 12:17:40  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2011/03/22 09:55:27  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/01/27 09:55:53  xch123
 * 兴业春节前回来
 *
 * Revision 1.2  2010/12/28 10:30:11  xch123
 * 12月28日提交
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2010/04/21 02:32:56  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.6  2010/04/20 13:09:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/04/16 07:50:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/04/16 03:19:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/04/12 06:20:20  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.2  2010/04/08 10:11:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.10  2010/03/23 12:07:58  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.9  2010/03/18 10:41:33  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.8  2010/03/16 08:28:00  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.7  2010/03/05 09:13:02  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.6  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.4  2010/01/26 10:41:26  gaofeng
 * *** empty log message ***
 *
 * Revision 1.3  2009/12/09 06:20:38  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2009/12/09 06:08:34  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/13 07:28:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/08/14 03:57:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/18 01:57:03  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2009/01/12 08:38:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/09/27 02:22:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/09/05 08:07:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/08/27 10:04:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:11  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.9  2008/06/24 02:46:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.8  2008/06/23 09:05:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2008/06/23 08:36:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/22 02:41:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/04/12 16:32:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/04/12 05:04:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/03/05 06:52:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/02/29 17:29:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:24  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/10/09 03:03:13  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/21 07:45:12  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:34  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/27 06:03:01  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:12:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
