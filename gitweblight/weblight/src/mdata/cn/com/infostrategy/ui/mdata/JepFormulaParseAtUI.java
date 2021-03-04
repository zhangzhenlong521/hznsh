/**************************************************************************
 * $RCSfile: JepFormulaParseAtUI.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.jepfunctions.AddItemValueChangedListener;
import cn.com.infostrategy.to.mdata.jepfunctions.DirectLinkRef;
import cn.com.infostrategy.to.mdata.jepfunctions.ExecWLTAction;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellCurrRowColItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellEditingItemNumberValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellSumValueByKeys;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellSumValueByRowCol;
import cn.com.infostrategy.to.mdata.jepfunctions.GetClientEnvironmentPut;
import cn.com.infostrategy.to.mdata.jepfunctions.GetComBoxItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetEditState;
import cn.com.infostrategy.to.mdata.jepfunctions.GetFormatChildListItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetFormatChildTreeItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetMultiRefName;
import cn.com.infostrategy.to.mdata.jepfunctions.GetNotNullItemCount;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefCode;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefName;
import cn.com.infostrategy.to.mdata.jepfunctions.GetStringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetTreeItemLevelValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetWFInfo;
import cn.com.infostrategy.to.mdata.jepfunctions.GetYearDateRefItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetAllItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetCardGroupValue;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardGroupExpand;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardGroupVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardRowVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCellItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetClientEnvironmentPut;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemBackGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemEditable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemForeGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemIsmustinput;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemLabel;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefDefine;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemCode;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemName;
import cn.com.infostrategy.to.mdata.jepfunctions.ShowMsg;
import cn.com.infostrategy.to.mdata.jepfunctions.ShowNewCardDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.formatcomp.AddBillSelectListenerParse;
import cn.com.infostrategy.ui.mdata.formatcomp.AddSelectEventBindRefresh;
import cn.com.infostrategy.ui.mdata.formatcomp.AddSelectQuickQueryRefresh;
import cn.com.infostrategy.ui.mdata.formatcomp.ClearTable;
import cn.com.infostrategy.ui.mdata.formatcomp.GetFormatItemPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.GetRegFormulaParse;
import cn.com.infostrategy.ui.mdata.formatcomp.GetSelectedBillVOItemValueParse;
import cn.com.infostrategy.ui.mdata.formatcomp.GetUserRole;
import cn.com.infostrategy.ui.mdata.formatcomp.QueryAllDataParse;
import cn.com.infostrategy.ui.mdata.formatcomp.QueryDataByConditionParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetBillPanelBtnVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetBtnBarVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetDefaultRefItemVOReturnFromParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetEveryBtnBarIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQAfterIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQIsVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetQQAfterClearTable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetSelfBtnBarIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetSelfListQQAfterIsVisiable;
import cn.com.infostrategy.ui.mdata.jepfunctions.OpenBillListDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.config.SetSelfListQQIsVisiable;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * UI�˵Ĺ�ʽ������
 * @author xch
 *
 */
public class JepFormulaParseAtUI extends JepFormulaParse {

	protected byte getJepType() {
		return WLTConstants.JEPTYPE_UI;
	}

	public JepFormulaParseAtUI() {
		initNormalFunction(); //���ø��ೣ�õļ��㺯��!!
	}

	public JepFormulaParseAtUI(boolean _isStandard) {
		if (_isStandard) {
			initStandardFunction(); //ֻ���ر�׼������!��JEP�Դ���������ĺ���!
		} else {
			initNormalFunction(); //���ø��ೣ�õļ��㺯��!!
		}
	}

	/**
	 * ���Բ����ؼ��ĺ���,�����˳��ú����⣬�����Ͽؼ���������!!!�����ã�Ҳ����������!!!
	 * �ؼ�������UI�����ѵĵط���Ҳ�ǹ��������ĵط�!!!����������Ҳ�ʹ������˿���Ч�ʣ�Ŀǰ���ڶ�û�кܺõ��ⷽ������!!Ҳ��WebLight���ⷽ�潫��������õ�!!
	 * @param _panel,
	 * @param _row,�кţ���Ϊ�б������еģ����д����кŵ�����!!!!!���ڿ�Ƭ��˵��������ǲ���Ч����!!
	 */
	public JepFormulaParseAtUI(BillPanel _panel) {
		initNormalFunction(); //���ø��ೣ�õļ��㺯��!!

		//����8�������Ķ�����BS��һ��,��ʵ���෽������BS�˲�һ��,��ΪBS��û��Panel..���Ǵ���HashMap!!!
		//8�����õĿؼ���������������6������ʵ��!!��Щ�����е�ItemKey�Ժ���и����ӵı仯��������������ǰ꡼������ĸ�������������Ӷ�ʵ���������!!!!
		//��������ʽ,��Ƭ���б�����!!
		parser.addFunction("getColValue2", new GetItemValue(_panel)); //ȡ�ñ���ĳһ���ֶε�ֵ!!

		parser.addFunction("getItemValue", new GetItemValue(_panel)); //ȡ��ĳһ���ֵ!����ʵ��!!���в������Դ���,��ʾ����ȡ�����������յĵ�����һ������!!!!
		parser.addFunction("getRefName", new GetRefName(_panel)); //ȡ��ĳһ���ֵ!����ʵ��!!���в������Դ���,��ʾ����ȡ�����������յĵ�����һ������!!!!
		parser.addFunction("getRefCode", new GetRefCode(_panel)); //ȡ��ĳһ���ֵ!����ʵ��!!���в������Դ���,��ʾ����ȡ�����������յĵ�����һ������!!!!
		parser.addFunction("getMultiRefName", new GetMultiRefName(_panel)); //����ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("AddItemValueChangedListener", new AddItemValueChangedListener(_panel)); //gaofeng ���������ƽ���Ԫ�صĹ�ʽ
		parser.addFunction("getStringItemVO", new GetStringItemVO(_panel)); //ȡ��һ���ı�������
		parser.addFunction("getComBoxItemVO", new GetComBoxItemVO(_panel)); //ȡ��һ������������
		parser.addFunction("getRefItemVO", new GetRefItemVO(_panel)); //ȡ��һ����������
		parser.addFunction("getYearDateRefItemVO", new GetYearDateRefItemVO(_panel)); //ȡ��һ���������ݡ����/2016-03-20��

		parser.addFunction("getNotNullItemCount", new GetNotNullItemCount(_panel)); //ȡ��һ����������

		parser.addFunction("setItemValue", new SetItemValue(_panel)); //����ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("setItemLabel", new SetItemLabel(_panel)); //����ĳһ�������!����ʵ��!!!!!

		parser.addFunction("resetAllItemValue", new ResetAllItemValue(_panel)); //���ĳһ���ֵ!!����ʵ��!!!!
		parser.addFunction("resetItemValue", new ResetItemValue(_panel)); //���ĳһ���ֵ!!����ʵ��!!!!
		parser.addFunction("resetCardGroupValue", new ResetCardGroupValue(_panel)); //���ĳһ���ֵ!!����ʵ��!!!!
		parser.addFunction("getClientEnvironmentPut", new GetClientEnvironmentPut()); // ����getLoginCode()����
		parser.addFunction("setClientEnvironmentPut", new SetClientEnvironmentPut()); // ����getLoginCode()����

		//���湫ʽӦֻ���ڿ�Ƭ
		parser.addFunction("SetItemIsmustinput", new SetItemIsmustinput(_panel));
		parser.addFunction("setItemEditable", new SetItemEditable(_panel)); //����ĳһ���Ƿ�ɱ༭!����ʵ��!!
		parser.addFunction("setItemVisiable", new SetItemVisiable(_panel)); //����ĳһ���Ƿ�ɱ༭!����ʵ��!!

		parser.addFunction("setItemForeGround", new SetItemForeGround(_panel)); //����ĳһ��ĵ�ǰ����ɫ!!
		parser.addFunction("setItemBackGround", new SetItemBackGround(_panel)); //����ĳһ��ĵı�����ɫ!ֻ֧���б����/2014-11-13��

		parser.addFunction("setCardRowVisiable", new SetCardRowVisiable(_panel)); //���ÿ�Ƭ��ĳһ�е����пؼ�����ʾ/����
		parser.addFunction("setCardGroupVisiable", new SetCardGroupVisiable(_panel)); //����ĳһ���Ƿ�ɱ༭!����ʵ��!!
		parser.addFunction("setCardGroupExpand", new SetCardGroupExpand(_panel)); //����ĳһ���Ƿ�ɱ༭!����ʵ��!!
		parser.addFunction("getEditState", new GetEditState(_panel)); //��ÿ�Ƭ�ı༭״̬�����/2013-05-09��
		
		//parser.addFunction("reloadItemValue", new ReloadItemValue(_panel, _row)); //���¼�������,����һ���������е�����!!�����Ҫ��?

		//���ò���Code
		parser.addFunction("setRefItemCode", new SetRefItemCode(_panel)); //���ò��ձ���
		parser.addFunction("setRefItemName", new SetRefItemName(_panel)); //���ò��ձ���
		parser.addFunction("setRefDefine", new SetRefDefine(_panel)); //���ò��ձ���

		//ȡ�ÿؼ��е�ֵ!
		parser.addFunction("getTreeItemLevelValue", new GetTreeItemLevelValue(_panel)); //����ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("getFormatChildListItemValue", new GetFormatChildListItemValue(_panel)); //����ע�����
		parser.addFunction("getFormatChildTreeItemValue", new GetFormatChildTreeItemValue(_panel)); //����ע�����

		//���������е�ֵ
		parser.addFunction("setCellItemValue", new SetCellItemValue(_panel)); //����ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("getCellItemValue", new GetCellItemValue(_panel, false)); //ȡ��ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("getCellItemNumberValue", new GetCellItemValue(_panel, true)); //ȡ��ĳһ���ֵ!����ʵ��!!!!!

		//�빤������صĹ�ʽ,������ݱ��е�ֵ,ȡ������ʵ��,Ȼ��ȡ�����̵������Ϣ,���統ǰ���̱���,��ǰ���ڱ���,��ǰ���̴�����,������ʷ������!!!
		parser.addFunction("getWFInfo", new GetWFInfo(_panel, true)); //ȡ�ù��������
		parser.addFunction("showMsg", new ShowMsg(_panel));
	}

	public JepFormulaParseAtUI(BillPanel _panel, WLTButton _btn) {
		this(_panel);
		parser.addFunction("execWLTAction", new ExecWLTAction(_panel, _btn)); //ִ�а�ť����
		// ��ť���������ݲ�����ò�ͬ��չʾ
		parser.addFunction("showNewCardDialog", new ShowNewCardDialog(_panel, _btn)); //ִ�а�ť����
		parser.addFunction("openBillListDialog", new OpenBillListDialog(_panel, _btn)); //ִ�а�ť����
	}

	public JepFormulaParseAtUI(BillPanel _panel, WLTButton _btn, BillFormatPanel _billformatpanel, String _regformula1, String _regformula2, String _regformula3) {
		this(_panel);
		parser.addFunction("execWLTAction", new ExecWLTAction(_panel, _btn)); //ִ�а�ť����
		//ע�����к���
		parser.addFunction("getCard", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_CARD)); //
		parser.addFunction("getList", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_LIST)); //
		parser.addFunction("getTree", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_TREE)); //
		parser.addFunction("getCell", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_CELL)); //

		parser.addFunction("getSelectedBillVOItemValue", new GetSelectedBillVOItemValueParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("addSelectEventBindRefresh", new AddSelectEventBindRefresh(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("addSelectQuickQueryRefresh", new AddSelectQuickQueryRefresh(_billformatpanel)); //�¼��󶨺���

		parser.addFunction("queryAllData", new QueryAllDataParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("getUserRole", new GetUserRole()); //�¼��󶨺���

		parser.addFunction("isSelected", new IsSelected(_billformatpanel)); //�¼��󶨺���

		parser.addFunction("queryDataByCondition", new QueryDataByConditionParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setEveryBtnBarIsVisiable", new SetEveryBtnBarIsVisiable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfBtnBarIsVisiable", new SetSelfBtnBarIsVisiable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setBtnBarVisiable", new SetBtnBarVisiableParse(_billformatpanel)); //���ð�ť�Ƿ�ȫ����ʾ/����
		parser.addFunction("setBillPanelBtnVisiable", new SetBillPanelBtnVisiableParse(_billformatpanel)); //����ĳ����ť�Ƿ���ʾ

		parser.addFunction("setListQQVisiable", new SetListQQVisiableParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("clearTable", new ClearTable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfListQQIsVisiable", new SetSelfListQQIsVisiable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setListQQIsVisiable", new SetListQQIsVisiableParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setDefaultRefItemVOReturnFrom", new SetDefaultRefItemVOReturnFromParse(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setListQQAfterIsVisiable", new SetListQQAfterIsVisiable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfListQQAfterIsVisiable", new SetSelfListQQAfterIsVisiable(_billformatpanel)); //�¼��󶨺���
		parser.addFunction("setQQAfterClearTable", new SetQQAfterClearTable(_billformatpanel)); //�¼��󶨺���

		//ע���¼��󶨺���
		parser.addFunction("addBillSelectListener", new AddBillSelectListenerParse(_billformatpanel)); //�б����,ѡ��仯ʱ�������¼�,�����õĹ�ʽ!!!

		parser.addFunction("getRegFormula", new GetRegFormulaParse(_billformatpanel, _regformula1, _regformula2, _regformula3)); //�б����,ѡ��仯ʱ�������¼�,�����õĹ�ʽ!!!

		// ��ť���������ݲ�����ò�ͬ��չʾ
		parser.addFunction("showNewCardDialog", new ShowNewCardDialog(_panel, _btn)); //ִ�а�ť����

		// ��ť��ʽ��ֱ�ӵ������գ����ڹ�����������
		parser.addFunction("DirectLinkRef", new DirectLinkRef(_panel, _btn)); //ִ�а�ť����

		parser.addFunction("openBillListDialog", new OpenBillListDialog(_panel, _btn)); //ִ�а�ť����

	}

	/**
	 * BillCellPanel�Ĺ�ʽ������
	 * @param _cellPanel
	 * @param _row
	 * @param _col
	 */
	public JepFormulaParseAtUI(BillCellPanel _cellPanel, int _row, int _col) {
		this(_cellPanel);
		parser.addFunction("getCellCurrRowItemValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 1, false)); //ȡ��������ͬһ����ĳһ���ϵ�ֵ!!
		parser.addFunction("getCellCurrRowItemNumberValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 1, true)); //ȡ��������ͬһ����ĳһ���ϵ�ֵ,ת��������!!
		parser.addFunction("getCellCurrColItemValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 2, false)); //ȡ��������ͬһ����ĳһ���ϵ�ֵ!!
		parser.addFunction("getCellCurrColItemNumberValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 2, true)); //ȡ��������ͬһ����ĳһ���ϵ�ֵ,ת��������!!
		parser.addFunction("getCellSumValueByRowCol", new GetCellSumValueByRowCol(_cellPanel, _row, _col)); //ȡ��ĳһ���ֵ!����ʵ��!!!!!
		parser.addFunction("getCellSumValueByKeys", new GetCellSumValueByKeys(_cellPanel, _row, _col)); //ȡ��ĳһ���ֵ!����ʵ��!!!!!
	}

	/**
	 * BillCellPanel�Ĺ�ʽ������
	 * @param _cellPanel
	 * @param _editingValue
	 * @param _row
	 * @param _col
	 */
	public JepFormulaParseAtUI(BillCellPanel _cellPanel, String _editingValue, int _row, int _col) {
		this(_cellPanel, _row, _col);
		parser.addFunction("getCellEditingItemNumberValue", new GetCellEditingItemNumberValue(_cellPanel, _editingValue)); //ȡ�����������ڱ༭�ĸ����е�ֵ..
	}

}
