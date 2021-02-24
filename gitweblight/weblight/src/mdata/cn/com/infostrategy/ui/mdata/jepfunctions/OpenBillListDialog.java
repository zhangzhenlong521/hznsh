package cn.com.infostrategy.ui.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ֱ�ӵ���һ���б�Ի���!!!,һ��4������,��һ����ģ�����,��2���Ǵ��б���ʱ�ĳ�ʼ����ѯ����,��3����ʱ���ڴ�С,��4����Ĭ�ϸ߶�!
 * @author xch
 *
 */
public class OpenBillListDialog extends PostfixMathCommand {

	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	private TBUtil tBUtil = null;

	public OpenBillListDialog(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = -1; //�ĸ�����,��һ����ģ�����,��2��SQL��������,�������ǿ����߶�
		this.billPanel = _billPanel;
		this.wltButton = _btn; //

	}

	public void run(Stack inStack) throws ParseException {
		if (this.curNumberOfParameters != 4) {
			System.err.println("��ʽOpenBillListDialog�Ĳ�����������,������4������!!"); //
			return;
		}
		try {
			BillVO billVO = null;
			if (billPanel instanceof BillCardPanel) {
				billVO = ((BillCardPanel) billPanel).getBillVO(); //
			} else if (billPanel instanceof BillListPanel) {
				billVO = ((BillListPanel) billPanel).getSelectedBillVO(); //
			} else if (billPanel instanceof BillTreePanel) {
				billVO = ((BillTreePanel) billPanel).getSelectedVO(); //
			}

			if (billVO == null) {
				MessageBox.showSelectOne(billPanel); //
				return;
			}

			checkStack(inStack);

			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			String str_templetCode = (String) param_4;
			String str_sqlFilterCon = (String) param_3;
			int li_width = Integer.parseInt((String) param_2);
			int li_height = Integer.parseInt((String) param_1);

			if (str_sqlFilterCon != null && !str_sqlFilterCon.trim().equals("")) {
				String[] str_items = getTBUtil().getFormulaMacPars(str_sqlFilterCon, "${", "}"); //
				for (int i = 0; i < str_items.length; i++) {
					str_sqlFilterCon = getTBUtil().replaceAll(str_sqlFilterCon, "${" + str_items[i] + "}", billVO.getStringValue(str_items[i], "")); //
				}
			}
			BillListDialog billListDialog = new BillListDialog(billPanel, wltButton.getText(), str_templetCode, str_sqlFilterCon, li_width, li_height, false); //
			billListDialog.getBilllistPanel().setQuickQueryPanelVisiable(false); //���ٲ�ѯ��������ص�!!��������ٴβ�ѯ�Ͳ�����!!!
			billListDialog.getBilllistPanel().setItemEditable(false); //
			billListDialog.setVisible(true);
		} catch (Exception _ex) {
			getTBUtil().printStackTrace(_ex); //
		}

	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil(); //
		}
		return tBUtil;
	}
}
