package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ���������ĳһ��ı�����ɫ�����/2014-11-13��
 * @author lcj
 *
 */
public class SetItemBackGround extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	private HashVO[] hvs_data = null; //���е�����

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetItemBackGround(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	public SetItemBackGround(HashMap _dataMap) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();

		String str_itemKey = (String) param_2; //itemKey
		String str_itemBackGroundColor = (param_1 == null ? "" : String.valueOf(param_1)); //�ڶ���ֵ�������ַ���������!!

		String str_value = null;

		if (str_itemKey != null && str_itemKey.contains(",")) {//�����ö��
			String[] keys = TBUtil.getTBUtil().split(str_itemKey, ",");
			if (keys != null && keys.length > 0) {
				for (int i = 0; i < keys.length; i++) {
					if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
						str_value = getUIValue(keys[i], str_itemBackGroundColor); //
					} else if (callType == WLTConstants.JEPTYPE_BS) {
						str_value = getBSValue(keys[i], str_itemBackGroundColor); //
					}
				}
			}
		} else {
			if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
				str_value = getUIValue(str_itemKey, str_itemBackGroundColor); //
			} else if (callType == WLTConstants.JEPTYPE_BS) {
				str_value = getBSValue(str_itemKey, str_itemBackGroundColor); //
			}
		}
		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}

		inStack.push(str_value); //�����ջ!!

	}

	private String getUIValue(String _itemkey, String _itemBackGround) {
		if (billPanel instanceof BillListPanel) {//ֻʵ���б���ʾ������ɫ����
			BillListPanel listPanel = (BillListPanel) billPanel;
			int li_row = listPanel.getSelectedRow(); //ѡ�е���,�༭��ʽ����ʱ��Ȼ��������ѡ�е���
			listPanel.setItemBackGroundColor(_itemBackGround, li_row, _itemkey); //����ʵ��ֵ
		}
		return "ok"; //
	}

	private String getBSValue(String _itemkey, String _itemBackGround) {
		if (colDataTypeMap.containsKey(_itemkey)) {//����������keyֵ
			BillItemVO rowItemVO = (BillItemVO) rowDataMap.get(_itemkey); //
			if (rowItemVO != null) {
				rowItemVO.setBackGroundColor(_itemBackGround); //
			}
		}
		return "ok";
	}

	public void setRowDataMap(HashMap _rowDataMap) {
		this.rowDataMap = _rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
