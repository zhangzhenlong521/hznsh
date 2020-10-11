package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

/**
 * 模板本身
 * @author xch
 *
 */
public class TMO_PubTemplet_1 extends AbstractTMO {

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "pub_templet_1"); // 模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "元原模板"); // 模板名称
		vo.setAttributeValue("templetname_e", "Templet"); // 模板名称
		vo.setAttributeValue("tablename", "pub_templet_1"); // 查询数据的表(视图)名
		vo.setAttributeValue("pkname", "ID"); // 主键名
		vo.setAttributeValue("pksequencename", null); // 序列名
		vo.setAttributeValue("savedtablename", null); // 保存数据的表名
		vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
		vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板

		vo.setAttributeValue("TREEPK", "id"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeviewfield", "name"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeseqfield", "seq"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeisshowroot", "Y"); // 列表是否显示操作按钮栏
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "编码"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "Code"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); // 默认值公式
		itemVO.setAttributeValue("listwidth", "175"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "60,150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "60,60"); //
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "名称"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "ChineseName"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "125"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "225"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "60,70"); //
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
