package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ��ť�������
 * ���ܣ���һ��n:m�Ĺ�ϵ��������һ�м�¼
 * ���ã��������ӱ�����£���֪�����¼���������ӱ�Ĺ���
 * ʹ�ò��裺
 * 1. ����n:m��ϵ��,������������㹻����ʾ��Ϣ����ֱ�ӽ���ģ�壻
 * 2. ����ϵ����û��������Ϣ��������ͼ��������ϵ�������ֶ�id--��Ҫ���桢�����ֶ�--���Ա���Ҳ���Բ����棬��Ҫ������ʾ��Ϣ��
 * �ʹ��������������Ϣ�ֶ�--��ɲ���ʾ����Ȼ����ģ��
 *    ��������������ֶΣ����Բ�Ҫ���������������Ҫ���������
 * 3. ģ���е�ǰ2�ֶΣ������һ��������id���ڶ����Ǵ�������id
 * 4. ���ڹ�ϵ���е�id�ֶΣ������༭��ʽ���������������ģ����������ʼ�������ֶ�
 * �磺setItemValue("field1",getItemValue("file_id.cmpfilecode"));
 * 5. ������ť���趨ִ��ǰ��ʽΪ����ʽ���磺DirectLinkRef��"������id"��
 * 
 * ��Class��cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI���б�����
 * 
 * @author 1
 *
 */
public class DirectLinkRef extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	public DirectLinkRef(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = 1;
		this.billPanel = _billPanel;
		this.wltButton = _btn; //
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_field2 = (String) inStack.pop();

		//�򿪲������ݶԻ���
		RefItemVO refItemVO = openRefDialog();
		if (refItemVO == null) {
			return;
		}

		insertRowIntoList(refItemVO, str_field2);
		inStack.push("ok");
	}

	private RefItemVO openRefDialog() {
		BillListPanel list = (BillListPanel) billPanel;
		try {
			Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = list.getTempletItemVOs()[1];
			UIRefPanel tempRefPanel = new UIRefPanel(pub_Templet_1_ItemVO, new RefItemVO(null, null, null), billPanel, false); //��ǰ���������˸����RefDialog,��ʵ������߼���UIRefPanel�е��߼����ظ���,���Կ���ʡȥ�����!!!
			AbstractRefDialog refDialog = tempRefPanel.getRefDialog(); //�������մ���!!!
			if (refDialog == null) {
				return null;
			}
			refDialog.initialize(); //
			refDialog.setVisible(true); //

			if (refDialog.getCloseType() == 1) {
				return refDialog.getReturnRefItemVO();
			} else {
				return null;
			}
		} catch (Exception ex) {
			MessageBox.showException(list, ex); //
			return null;
		}
	}

	private void insertRowIntoList(RefItemVO refItemVO, String _key) {
		BillListPanel list = (BillListPanel) billPanel;
		//insert a row
		int curr_li_row = list.newRow();
		int curr_li_col = 2;//�������ã�������ģ���н����ֶη��ڵ�2��
		//set value
		list.setValueAt(refItemVO, curr_li_row, _key);
		list.stopEditing();
		list.execEditFormula(curr_li_row, curr_li_col);
		//save
		if (list.checkValidate()) {
			list.saveData(); //
			list.refreshData();
		}
	}

}
