package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

/**
 * ȡ������е�ĳһ���ֵ!!
 * �ú����ڿͻ�����������˶���ʹ��!!!
 * @author xch
 *
 */
public class GetItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetItemValue(BillPanel _billPanel) {
		numberOfParameters = 1; //ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetItemValue(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �Ƿ��Ǵ���ģ��ʱ
	 * @param _isCreateTemplet
	 */
	public GetItemValue(boolean _isCreateTemplet) {
		numberOfParameters = 1; //
		callType = 2; //
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_value = getUIValue(str_itemKey); //
		} else if (callType == WLTConstants.JEPTYPE_BS) {
			str_value = getBSValue(str_itemKey); //
		} else if (callType == 2) { //����ģ��ʱ
			str_value = "${" + str_itemKey + "}"; //ʵ��û�취,�ڴ���ģ��ʱ�޷�ȡ��ҳ����ĳһ���ֵ,����Ҫ���ݾɵĹ�ʽ����,ֻ��ת����${corpid}������,Ȼ��ǰִ̨��ʱ�ٷ���!! �쳤��(2011-11-1 14:40)
		}
		inStack.push(str_value); //�����ջ!!
	}

	private String getUIValue(String _itemkey) {
		String str_value = null;
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			str_value = cardPanel.getRealValueAt(_itemkey); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			str_value = listPanel.getRealValueAtModel(listPanel.getSelectedRow(), _itemkey); //ȡ��ѡ���е�����
		} else if (billPanel instanceof BillQueryPanel) {
			BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
			str_value = queryPanel.getRealValueAt(_itemkey); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}

		return str_value; //
	}

	/**
	 * ȡ�÷�������ֵ!!!
	 * @param _itemkey
	 * @return
	 */
	private String getBSValue(String _itemkey) {
		String str_value = null;
		if (rowDataMap.containsKey(_itemkey)) {//����������keyֵ 
			Object obj = rowDataMap.get(_itemkey); //

			if (obj != null) {
				if (obj instanceof String) {
					str_value = (String) obj; //
				} else if (obj instanceof StringItemVO) {
					StringItemVO itemVO = (StringItemVO) obj; //
					str_value = itemVO.getStringValue(); //
				} else if (obj instanceof ComBoxItemVO) {
					ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
					str_value = itemVO.getId(); //
				} else if (obj instanceof RefItemVO) {
					RefItemVO itemVO = (RefItemVO) obj; //
					str_value = itemVO.getId(); //
				} else {
					str_value = obj.toString(); //
				}
			}
		}

		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}
		//System.out.println("���ع�ʽȡ��[" + _itemkey + "]=[" + str_value + "]!!!"); //
		return str_value; //
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
