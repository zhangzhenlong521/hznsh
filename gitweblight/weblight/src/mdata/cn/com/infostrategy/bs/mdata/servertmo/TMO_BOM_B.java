package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

/**
 * BOM模板配置的元数据!
 * @author xch
 *
 */
public class TMO_BOM_B extends AbstractTMO {

	private static final long serialVersionUID = 8380921970700280061L;
	String str_selfid = null;

	public TMO_BOM_B() {
	}

	public TMO_BOM_B(String[] _selfid) {
		str_selfid = _selfid[0]; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "pub_bom_b"); // 模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "热点信息"); // 模板名称
		vo.setAttributeValue("templetname_e", "pub_bom_b"); // 模板名称
		vo.setAttributeValue("tablename", "pub_bom_b"); // 查询数据的表(视图)名
		vo.setAttributeValue("pkname", "ID"); // 主键名
		vo.setAttributeValue("pksequencename", "s_pub_bom_b"); // 序列名
		vo.setAttributeValue("savedtablename", "pub_bom_b"); // 保存数据的表名
		vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
		vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bomid"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "Bomid"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "bomid"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemkey"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "热点索引键名"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "itemkey"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "Y"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemname"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "热点索引名"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "itemname"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "x"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "x"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "x"); // 显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "y"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "y"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "y"); // 显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "width"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "width"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "width"); // 显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "height"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "height"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "height"); // 显示名称
		itemVO.setAttributeValue("itemtype", "数字框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindtype"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "绑定的类型"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "bindtype"); // 显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", "select '菜单' id,'菜单' code,'菜单' name from wltdual union all select 'Bom图' id,'Bom图' code,'Bom图' name from wltdual union all select 'Class类' id,'Class类' code,'Class类' name from wltdual"); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", "\"Bom图\""); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindbomcode"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "链接的Bom图"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "bindbomcode"); // 显示名称
		itemVO.setAttributeValue("itemtype", "参照"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", "getTableRef(\"select code id$,code,bgimgname,descr from pub_bom where id<>" + str_selfid + "\")"); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindclassname"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "链接的Panel类"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "bindclassname"); // 显示名称
		itemVO.setAttributeValue("itemtiptext", "<html>继承于JPanel的普通类即可,但构造方法可支持多个入参,但注册的入参数量与实际构造方法的数量要对上,否则会报错!!<br>比如注册类名是com.pushworld.MyTestPanel(\"入参1\",\"入参2\"),<br>则构造函数是public MyTestPanel(String _par1,String _par2,BillBomPanel _bomPanel){},最后要多个参数BillBomPanel!<br>则自动将注册的参数传过来!</html>"); // 显示名称，帮助搞上详细帮助,否则开发人员根本不知道怎么弄!【xch/2012-03-07】
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "400"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindmenu"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "绑定的菜单"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "bindmenu"); // 显示名称
		itemVO.setAttributeValue("itemtype", "树型模板参照"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", "getTreeTempletRef(\"cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu\",\"id\",\"name\",\"\",\"返回路径链名=N\");"); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", "setRefItemName(\"bindmenu\",getColValue(\"pub_menu\",\"name\",\"id\",getItemValue(\"bindmenu\")))"); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "400"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "loadtype"); // 唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "打开一下层方式"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "loadtype"); // 显示名称
		itemVO.setAttributeValue("itemtype", "下拉框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", ""); // 下拉框定义
		itemVO.setAttributeValue("refdesc", "getCommUC(\"下拉框\",\"SQL语句\",\"select '1' id,'' code,'下一层' name from wltdual union all select '2' id,'' code,'弹出窗口' name from wltdual union all select '3' id,'' code,'直接替换' name from wltdual\");"); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", ""); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "120"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "138"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);


		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
