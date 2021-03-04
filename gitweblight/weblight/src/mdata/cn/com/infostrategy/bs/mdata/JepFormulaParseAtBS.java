/**************************************************************************
 * $RCSfile: JepFormulaParseAtBS.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.jepfunctions.GetColValue2;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCommUC;
import cn.com.infostrategy.to.mdata.jepfunctions.GetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetMultiRefName;
import cn.com.infostrategy.to.mdata.jepfunctions.GetOldCommUCJEP;
import cn.com.infostrategy.to.mdata.jepfunctions.GetWFInfo;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemBackGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemForeGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemLabel;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemCode;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemName;

/**
 * BS端的公式解析器
 * @author xch
 *
 */
public class JepFormulaParseAtBS extends JepFormulaParse {

	private GetItemValue getItemValue = null; //
	private SetItemValue setItemValue = null; //
	private SetItemLabel setItemLabel = null; //
	//	private GetRefName getRefName = null; //
	private SetRefItemCode setRefItemCode = null; //
	private SetRefItemName setRefItemName = null; //

	private SetItemForeGround setItemForeGround = null;
	private SetItemBackGround setItemBackGround = null;//增加设置列表背景颜色的公式【李春娟/2014-11-13】
	private GetWFInfo getWFInfo = null; //取得工作流相关信息
	private GetMultiRefName getMultiRefName = null; //

	private GetColValue2 getColValue2 = null;

	protected byte getJepType() {
		return WLTConstants.JEPTYPE_BS;
	}

	public JepFormulaParseAtBS() {
		this(null);
	}

	/**
	 * BS端的公式解析器
	 * @param _rowDataMap
	 */
	public JepFormulaParseAtBS(HashVO[] _allDatas) {
		initNormalFunction(); //常用的计算函数!!!

		//BS端的两个公式,需要带入参数!!
		parser.addFunction("getItemValue", getGetItemValue()); //取得某一项的值!优先实现!!其中参数可以带点,表示可以取到下拉框或参照的的任意一项内容!!!!
		parser.addFunction("setItemValue", getSetItemValue()); //设置某一项的值!优先实现!!!!!
		parser.addFunction("setItemLabel", getSetItemLabel()); //设置某一项的列名!优先实现!!!!!

		//BS端得到参照的名字
		//parser.addFunction("getRefName", getGetRefName()); //BS端得到参照的名字!优先实现!!!!!
		parser.addFunction("getWFInfo", getGetWFInfo()); //取得工作流相关信息..
		parser.addFunction("getMultiRefName", getMultiRefName()); //取得工作流相关信息..

		//设置参照编码,名称,很有用!!
		parser.addFunction("setRefItemCode", getSetRefItemCode()); //设置参照编码!!!
		parser.addFunction("setRefItemName", getSetRefItemName()); //设置参照名称!!!
		parser.addFunction("setItemForeGround", getSetItemForeGround()); //设置控件前景颜色!!!
		parser.addFunction("setItemBackGround", getSetItemBackGround()); //设置控件背景颜色!!!

		//加载数据2
		parser.addFunction("getColValue2", getColValue2(_allDatas)); // 加入getColValue()函数
	}

	/**
	 * 在首次创建模板时
	 * @param _isCreateTemplet
	 */
	public JepFormulaParseAtBS(boolean _isCreateTemplet) {
		initNormalFunction(); //常用的计算函数!!!
		parser.addFunction("getItemValue", new GetItemValue(true)); //

		//通用的公式定义!!以后都用这个!!
		parser.addFunction("getCommUC", new GetCommUC()); //

		//定义各种参照
		parser.addFunction("getTableRef", new GetOldCommUCJEP("参照")); //定义表型参照
		parser.addFunction("getTreeRef", new GetOldCommUCJEP("树型参照")); //定义树型参照
		parser.addFunction("getMultiRef", new GetOldCommUCJEP("多选参照")); //定义多选参照
		parser.addFunction("getCustRef", new GetOldCommUCJEP("自定义参照")); //定义自定义参照
		parser.addFunction("getListTempletRef", new GetOldCommUCJEP("列表模板参照")); //列表模板
		parser.addFunction("getTreeTempletRef", new GetOldCommUCJEP("树型模板参照")); //树型模板
		parser.addFunction("getRegFormatRef", new GetOldCommUCJEP("注册样板参照")); //通过注册面板生成
		parser.addFunction("getRegisterRef", new GetOldCommUCJEP("注册参照")); //定义注册参照
		parser.addFunction("getExcelRef", new GetOldCommUCJEP("Excel控件")); //Excel参照控件
		parser.addFunction("getOfficeRef", new GetOldCommUCJEP("Office控件")); //Office参照控件
		parser.addFunction("getChildTable", new GetOldCommUCJEP("引用子表")); //引用子表
		parser.addFunction("getChildTableImport", new GetOldCommUCJEP("导入子表")); //导入子表
	}

	private SetItemLabel getSetItemLabel() {
		if (setItemLabel == null) {
			setItemLabel = new SetItemLabel(new HashMap()); //
		}

		return setItemLabel; //
	}

	public SetRefItemCode getSetRefItemCode() {
		if (setRefItemCode == null) {
			setRefItemCode = new SetRefItemCode(new HashMap()); //
		}

		return setRefItemCode;
	}

	public SetRefItemName getSetRefItemName() {
		if (setRefItemName == null) {
			setRefItemName = new SetRefItemName(new HashMap()); //
		}

		return setRefItemName;
	}

	public SetItemForeGround getSetItemForeGround() {
		if (setItemForeGround == null) {
			setItemForeGround = new SetItemForeGround(new HashMap()); //
		}
		return setItemForeGround;
	}

	public SetItemBackGround getSetItemBackGround() {
		if (setItemBackGround == null) {
			setItemBackGround = new SetItemBackGround(new HashMap()); //
		}
		return setItemBackGround;
	}

	//取得工作流相关信息..
	public GetWFInfo getGetWFInfo() {
		if (getWFInfo == null) {
			getWFInfo = new GetWFInfo(new HashMap()); //
		}
		return getWFInfo;
	}

	public GetMultiRefName getMultiRefName() {
		if (getMultiRefName == null) {
			getMultiRefName = new GetMultiRefName(new HashMap()); //
		}
		return getMultiRefName;
	}

	//	public GetRefName getGetRefName() {
	//		if (getRefName == null) {
	//			getRefName = new GetRefName(new HashMap()); //
	//		}
	//
	//		return getRefName;
	//	}

	public GetItemValue getGetItemValue() {
		if (getItemValue == null) {
			getItemValue = new GetItemValue(new HashMap()); //
		}

		return getItemValue;
	}

	public SetItemValue getSetItemValue() {
		if (setItemValue == null) {
			setItemValue = new SetItemValue(new HashMap()); //
		}

		return setItemValue; //
	}

	private GetColValue2 getColValue2(HashVO[] _allDatas) {
		if (getColValue2 == null) {
			getColValue2 = new GetColValue2(getJepType(), _allDatas, new HashMap()); //
		}

		return getColValue2; //
	}

	public void setColDataTypeMap(HashMap _coldatatypeMap) {
		getItemValue.setColDataTypeMap(_coldatatypeMap); //
		setItemValue.setColDataTypeMap(_coldatatypeMap); //
		getMultiRefName.setColDataTypeMap(_coldatatypeMap); //设置列数据类型!

		setRefItemCode.setColDataTypeMap(_coldatatypeMap); //
		setRefItemName.setColDataTypeMap(_coldatatypeMap); //
		setItemForeGround.setColDataTypeMap(_coldatatypeMap); //
		setItemBackGround.setColDataTypeMap(_coldatatypeMap); //
		getWFInfo.setColDataTypeMap(_coldatatypeMap); //设置列数据类型!
		getColValue2.setColDataTypeMap(_coldatatypeMap); //设置列数据类型!
	}

	//设置每一行数据
	public void setRowDataMap(HashMap _rowDataMap) {
		getItemValue.setRowDataMap(_rowDataMap); //
		setItemValue.setRowDataMap(_rowDataMap); //
		getMultiRefName.setRowDataMap(_rowDataMap); //

		setRefItemCode.setRowDataMap(_rowDataMap); //
		setRefItemName.setRowDataMap(_rowDataMap); //
		setItemForeGround.setRowDataMap(_rowDataMap); //
		setItemBackGround.setRowDataMap(_rowDataMap); //
		getWFInfo.setRowDataMap(_rowDataMap); //

		getColValue2.setRowDataMap(_rowDataMap); //
	}

}
