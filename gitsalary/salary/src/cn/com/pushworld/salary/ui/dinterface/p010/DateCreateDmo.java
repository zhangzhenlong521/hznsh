package cn.com.pushworld.salary.ui.dinterface.p010;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.UIUtil;

public class DateCreateDmo extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private String tablename;
	private String parentid;
	private String itemname;
	private String[] titlenames;
	private HashMap titlemap;
	private boolean isSql;
	private boolean isedit = false;

	public DateCreateDmo(String tablename, String parentid, String itemname, boolean isSql) {
		this.tablename = tablename;
		this.parentid = parentid;
		this.itemname = itemname;
		this.isSql = isSql;
	}

	public DateCreateDmo(String tablename, String[] titlenames, HashMap titlemap, boolean isSql, boolean _isedit) {
		this.tablename = tablename;
		this.titlenames = titlenames;
		this.titlemap = titlemap;
		this.isSql = isSql;
		this.isedit = _isedit;
	}

	private HashVO[] getTitleHashVOs() {
		HashVO[] titleVO = null;
		try {
			if (isSql) {
				titleVO = UIUtil.getHashVoArrayByDS(null, "select itemcode, itemname from " + itemname + " where parentid ='" + parentid + "' order by seq asc");
			} else {
				titleVO = new HashVO[titlenames.length];
				for (int i = 0; i < titlenames.length; i++) {
					titleVO[i] = new HashVO();
					titleVO[i].setAttributeValue("itemname", titlenames[i]);
					titleVO[i].setAttributeValue("itemcode", titlemap.get(titlenames[i]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return titleVO;
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", tablename + "_code1"); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "数据同步模板"); //模板名称
		vo.setAttributeValue("templetname_e", "数据同步"); //模板名称
		vo.setAttributeValue("tablename", tablename); //查询数据的表(视图)名
		vo.setAttributeValue("pkname", "ID"); //主键名
		vo.setAttributeValue("pksequencename", "S_" + tablename); //序列名
		vo.setAttributeValue("savedtablename", tablename); //保存数据的表名
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
		vo.setAttributeValue("Treeseqfield", "seq"); //列表是否显示操作按钮栏
		vo.setAttributeValue("Treeisshowroot", "Y"); //列表是否显示操作按钮栏

		vo.setAttributeValue("isshowlistquickquery", "Y"); //列表是否显示查询面板

		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "id"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); //显示名称
		itemVO.setAttributeValue("itemname_e", "主键"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", ""); //默认值公式
		itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "325"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", isedit ? "1" : "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		HashVO[] hashVOs = getTitleHashVOs();
		if (hashVOs != null && hashVOs.length > 0) {
			for (int i = 0; i < hashVOs.length; i++) {
				itemVO = new HashVO();
				itemVO.setAttributeValue("itemkey", hashVOs[i].getStringValue("itemcode")); //唯一标识,用于取数与保存
				itemVO.setAttributeValue("itemname", hashVOs[i].getStringValue("itemname")); //显示名称
				itemVO.setAttributeValue("itemname_e", hashVOs[i].getStringValue("itemcode")); //显示名称
				itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
				itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
				itemVO.setAttributeValue("refdesc", null); //参照定义
				itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
				itemVO.setAttributeValue("isdefaultquery", "1"); //1-快速查询;2-通用查询;3-不参与查询
				itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
				itemVO.setAttributeValue("loadformula", null); //加载公式
				itemVO.setAttributeValue("editformula", null); //编辑公式
				itemVO.setAttributeValue("defaultvalueformula", ""); //默认值公式
				itemVO.setAttributeValue("listwidth", "120"); //列表是宽度
				itemVO.setAttributeValue("cardwidth", "325"); //卡片时宽度
				itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
				itemVO.setAttributeValue("listiseditable", isedit ? "1" : "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
				itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
				itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
				itemVO.setAttributeValue("iswrap", "N");
				vector.add(itemVO);
			}
		}
		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
