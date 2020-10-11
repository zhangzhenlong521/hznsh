package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

/**
 * ���������ĳһ��Ĳ��ն���!!���ķ���ֵӦ�ò��ᱻ��ĺ���ʹ��!!!
 * @author xch
 *
 */
public class SetRefDefine extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetRefDefine(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //
		String str_itemValue = (String) param_1; //

		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_value = getUIValue(str_itemKey, str_itemValue); //
		}

		inStack.push(str_value); //�����ջ!!
	}

	private String getUIValue(String _itemkey, String _itemvalue) {
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			UIRefPanel refPanel = (UIRefPanel) cardPanel.getCompentByKey(_itemkey);
			Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(_itemkey);
			HashMap confMap = TBUtil.getTBUtil().convertStrToMapByExpress(_itemvalue, ";", ","); //if(getItemValue("DATE_TYPE")=="1",setRefDefine("FINDED_DATE","����,{}>={HAPPENED_DATE}"),setRefDefine("FINDED_DATE","����,{}>={HAPPENED_BEGIN}"));
			CommUCDefineVO uc = itemvo.getUCDfVO();//����Ӧ������CommUCDefineVO�����/2016-12-20��
			uc.setConfValueAll(confMap); //ֱ�Ӽ���!!
			itemvo.setUCDfVO(uc);
		} else if (billPanel instanceof BillListPanel) {
			//			BillListPanel listPanel = (BillListPanel) billPanel;
			//			RefItemVO refItemVO = (RefItemVO) listPanel.getValueAt(listPanel.getSelectedRow(), _itemkey); //
			//			refItemVO.setRefDesc(_itemvalue); //
		} else if (billPanel instanceof BillPropPanel) {
		}

		return "ok"; //
	}

}
