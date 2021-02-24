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
 * 设置面板中某一项的参照定义!!它的返回值应该不会被别的函数使用!!!
 * @author xch
 *
 */
public class SetRefDefine extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public SetRefDefine(BillPanel _billPanel) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是值! 第一个ItemKey以后可能会有更复杂的变化!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //
		String str_itemValue = (String) param_1; //

		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			str_value = getUIValue(str_itemKey, str_itemValue); //
		}

		inStack.push(str_value); //置入堆栈!!
	}

	private String getUIValue(String _itemkey, String _itemvalue) {
		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			UIRefPanel refPanel = (UIRefPanel) cardPanel.getCompentByKey(_itemkey);
			Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(_itemkey);
			HashMap confMap = TBUtil.getTBUtil().convertStrToMapByExpress(_itemvalue, ";", ","); //if(getItemValue("DATE_TYPE")=="1",setRefDefine("FINDED_DATE","条件,{}>={HAPPENED_DATE}"),setRefDefine("FINDED_DATE","条件,{}>={HAPPENED_BEGIN}"));
			CommUCDefineVO uc = itemvo.getUCDfVO();//这里应该设置CommUCDefineVO【李春娟/2016-12-20】
			uc.setConfValueAll(confMap); //直接加入!!
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
