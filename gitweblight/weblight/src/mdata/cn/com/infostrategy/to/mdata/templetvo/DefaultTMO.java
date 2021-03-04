package cn.com.infostrategy.to.mdata.templetvo;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 默认的TMO,即元原模板数据VO!!
 * @author xch
 *
 */
public class DefaultTMO extends AbstractTMO {

	private static final long serialVersionUID = -5510217186481101473L;
	private HashVO parVO = null;
	private HashVO[] childVOs = null;

	public DefaultTMO() {
	}

	/**
	 * 有时快速创建一个简单表格,用来查询什么的,如果都是构造HashVO什么的还是麻烦,想直接最快创建
	 * 这个表格的每一列都是文本框,不可编辑...
	 * 只要指定每一列的列名与宽度就够了,非常简单.
	 * @param _templetName
	 * @param _itemAndWidth 两列,第一列是说明,第二列是宽度 比如：new String[][]{{"code","100"},{"name","150"}}
	 */
	public DefaultTMO(String _templetName, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", _templetName); //模板名称
		vo.setAttributeValue("templetname_e", _templetName); //模板名称
		vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
		vo.setAttributeValue("savedtablename", null); //保存数据的表名
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板

		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i] = new HashVO(); //
			if (_itemAndWidth[i].length == 2) { //{"code","100"},key与name是一样的!!!
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][1])); //列表是宽度
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][1])); //卡片时宽度
				itemVOs[i].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			} else if (_itemAndWidth[i].length == 3) { //{"code","编码","100"},如果是3列,即key与name分别定义!!
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][1]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][2])); //列表是宽度
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][2])); //卡片时宽度
				itemVOs[i].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			} else if (_itemAndWidth[i].length == 4) { //{"code","编码","100","Y"},如果是4列,即key与name分别定义!!且还指定列表卡片是否显示
				itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][1]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][2])); //列表是宽度
				itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][2])); //卡片时宽度
				itemVOs[i].setAttributeValue("listisshowable", _itemAndWidth[i][3]); //列表时是否显示(Y,N)
				itemVOs[i].setAttributeValue("cardisshowable", _itemAndWidth[i][3]); //卡片时是否显示(Y,N)
			}

			itemVOs[i].setAttributeValue("itemtype", "文本框"); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVOs[i].setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVOs[i].setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVOs[i].setAttributeValue("iswrap", "Y"); //卡片是否换行
		}

		this.parVO = vo;
		this.childVOs = itemVOs; //
	}

	/**
	 * 快速创建TMO,第一个参数是指定哪些类型!!! 即_type的宽度与第二个参数的二维数组的列宽是一样的!!!
	 * @param _templetName
	 * @param _type
	 * @param _itemAndWidth
	 */
	public DefaultTMO(String _templetName, String[] _type, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", _templetName); //模板名称
		vo.setAttributeValue("templetname_e", _templetName); //模板名称
		vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
		vo.setAttributeValue("savedtablename", null); //保存数据的表名
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板

		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) { //遍历!!!
			itemVOs[i] = new HashVO(); //
			for (int j = 0; j < _type.length; j++) { //
				itemVOs[i].setAttributeValue(_type[j], _itemAndWidth[i][j]);
			}
		}

		this.parVO = vo;
		this.childVOs = itemVOs; //
	}

	/**
	 * 卡片的TMO,专门用于卡片输出的
	 * @param _templetName
	 * @param _items
	 * @return
	 */
	public static DefaultTMO getCardTMO(String _templetName, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", _templetName); //模板名称
		vo.setAttributeValue("templetname_e", _templetName); //模板名称
		vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
		vo.setAttributeValue("savedtablename", null); //保存数据的表名
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
		HashVO[] itemVOs = new HashVO[_itemAndWidth.length]; //
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i] = new HashVO(); //
			itemVOs[i].setAttributeValue("itemtype", "文本框"); //默认是文本框
			if (_itemAndWidth[i][1].indexOf("*") > 0) { //如果有星号,则自动认为是多行文本框
				itemVOs[i].setAttributeValue("itemtype", "多行文本框"); //默认是文本框
			}
			String str_listWidth = _itemAndWidth[i][1]; //
			if (str_listWidth.indexOf(",") > 0) {
				str_listWidth = str_listWidth.substring(str_listWidth.indexOf(",") + 1, str_listWidth.length()); //
			}
			if (str_listWidth.indexOf("*") > 0) {
				str_listWidth = str_listWidth.substring(0, str_listWidth.indexOf("*")); //
			}

			boolean isMustInut = false; //
			String str_itemName = _itemAndWidth[i][0]; //
			if (str_itemName.startsWith("*")) {
				isMustInut = true; //星号开头
				str_itemName = str_itemName.substring(1, str_itemName.length()); //
			}
			itemVOs[i].setAttributeValue("itemkey", str_itemName); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("itemname", str_itemName); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("itemname_e", str_itemName); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("cardwidth", _itemAndWidth[i][1]); //卡片时宽度
			itemVOs[i].setAttributeValue("listwidth", str_listWidth); //列表是宽度
			itemVOs[i].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVOs[i].setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVOs[i].setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVOs[i].setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVOs[i].setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVOs[i].setAttributeValue("ismustinput", isMustInut ? "Y" : "N"); //是否必输

			if (_itemAndWidth[i].length == 2) { //{"code","100"},key与name是一样的!!!
				itemVOs[i].setAttributeValue("iswrap", "Y"); //卡片是否换行
			} else if (_itemAndWidth[i].length == 3) { //{"code","编码","100"},如果是3列,即key与name分别定义!!
				itemVOs[i].setAttributeValue("iswrap", _itemAndWidth[i][2]); //卡片是否换行
			}

		}

		return new DefaultTMO(vo, itemVOs); //
	}

	/**
	 * 有参数的构造方法!!!
	 * @param _parVO
	 * @param _childVOs
	 */
	public DefaultTMO(HashVO _parVO, HashVO[] _childVOs) {
		this.parVO = _parVO;
		this.childVOs = _childVOs;
	}

	/**
	 * 主表
	 */
	public HashVO getPub_templet_1Data() {
		return parVO;
	}

	/**
	 * 子表
	 */
	public HashVO[] getPub_templet_1_itemData() {
		return childVOs;
	}

	/**
	 * 主表
	 */
	public void setPub_templet_1Data(HashVO _parVO) {
		this.parVO = _parVO;
	}

	/**
	 * 子表
	 */
	public void setPub_templet_1_itemData(HashVO[] _childVOs) {
		this.childVOs = _childVOs;
	}

	private int getWidth(String _s) {
		try {
			return Integer.parseInt(_s);
		} catch (Exception e) {
			e.printStackTrace();
			return 100;
		}
	}
}
