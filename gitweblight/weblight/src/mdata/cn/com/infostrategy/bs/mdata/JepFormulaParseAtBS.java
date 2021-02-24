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
 * BS�˵Ĺ�ʽ������
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
	private SetItemBackGround setItemBackGround = null;//���������б�����ɫ�Ĺ�ʽ�����/2014-11-13��
	private GetWFInfo getWFInfo = null; //ȡ�ù����������Ϣ
	private GetMultiRefName getMultiRefName = null; //

	private GetColValue2 getColValue2 = null;

	protected byte getJepType() {
		return WLTConstants.JEPTYPE_BS;
	}

	public JepFormulaParseAtBS() {
		this(null);
	}

	/**
	 * BS�˵Ĺ�ʽ������
	 * @param _rowDataMap
	 */
	public JepFormulaParseAtBS(HashVO[] _allDatas) {
		initNormalFunction(); //���õļ��㺯��!!!

		//BS�˵�������ʽ,��Ҫ�������!!
		parser.addFunction("getItemValue", getGetItemValue()); //ȡ��ĳһ���ֵ!����ʵ��!!���в������Դ���,��ʾ����ȡ�����������յĵ�����һ������!!!!
		parser.addFunction("setItemValue", getSetItemValue()); //����ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("setItemLabel", getSetItemLabel()); //����ĳһ�������!����ʵ��!!!!!

		//BS�˵õ����յ�����
		//parser.addFunction("getRefName", getGetRefName()); //BS�˵õ����յ�����!����ʵ��!!!!!
		parser.addFunction("getWFInfo", getGetWFInfo()); //ȡ�ù����������Ϣ..
		parser.addFunction("getMultiRefName", getMultiRefName()); //ȡ�ù����������Ϣ..

		//���ò��ձ���,����,������!!
		parser.addFunction("setRefItemCode", getSetRefItemCode()); //���ò��ձ���!!!
		parser.addFunction("setRefItemName", getSetRefItemName()); //���ò�������!!!
		parser.addFunction("setItemForeGround", getSetItemForeGround()); //���ÿؼ�ǰ����ɫ!!!
		parser.addFunction("setItemBackGround", getSetItemBackGround()); //���ÿؼ�������ɫ!!!

		//��������2
		parser.addFunction("getColValue2", getColValue2(_allDatas)); // ����getColValue()����
	}

	/**
	 * ���״δ���ģ��ʱ
	 * @param _isCreateTemplet
	 */
	public JepFormulaParseAtBS(boolean _isCreateTemplet) {
		initNormalFunction(); //���õļ��㺯��!!!
		parser.addFunction("getItemValue", new GetItemValue(true)); //

		//ͨ�õĹ�ʽ����!!�Ժ������!!
		parser.addFunction("getCommUC", new GetCommUC()); //

		//������ֲ���
		parser.addFunction("getTableRef", new GetOldCommUCJEP("����")); //������Ͳ���
		parser.addFunction("getTreeRef", new GetOldCommUCJEP("���Ͳ���")); //�������Ͳ���
		parser.addFunction("getMultiRef", new GetOldCommUCJEP("��ѡ����")); //�����ѡ����
		parser.addFunction("getCustRef", new GetOldCommUCJEP("�Զ������")); //�����Զ������
		parser.addFunction("getListTempletRef", new GetOldCommUCJEP("�б�ģ�����")); //�б�ģ��
		parser.addFunction("getTreeTempletRef", new GetOldCommUCJEP("����ģ�����")); //����ģ��
		parser.addFunction("getRegFormatRef", new GetOldCommUCJEP("ע���������")); //ͨ��ע���������
		parser.addFunction("getRegisterRef", new GetOldCommUCJEP("ע�����")); //����ע�����
		parser.addFunction("getExcelRef", new GetOldCommUCJEP("Excel�ؼ�")); //Excel���տؼ�
		parser.addFunction("getOfficeRef", new GetOldCommUCJEP("Office�ؼ�")); //Office���տؼ�
		parser.addFunction("getChildTable", new GetOldCommUCJEP("�����ӱ�")); //�����ӱ�
		parser.addFunction("getChildTableImport", new GetOldCommUCJEP("�����ӱ�")); //�����ӱ�
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

	//ȡ�ù����������Ϣ..
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
		getMultiRefName.setColDataTypeMap(_coldatatypeMap); //��������������!

		setRefItemCode.setColDataTypeMap(_coldatatypeMap); //
		setRefItemName.setColDataTypeMap(_coldatatypeMap); //
		setItemForeGround.setColDataTypeMap(_coldatatypeMap); //
		setItemBackGround.setColDataTypeMap(_coldatatypeMap); //
		getWFInfo.setColDataTypeMap(_coldatatypeMap); //��������������!
		getColValue2.setColDataTypeMap(_coldatatypeMap); //��������������!
	}

	//����ÿһ������
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
