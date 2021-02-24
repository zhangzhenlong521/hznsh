package cn.com.infostrategy.to.mdata.jepfunctions;

import java.awt.Color;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

public class SetItemIsmustinput extends PostfixMathCommand {

	private BillPanel billPanel = null;

	public SetItemIsmustinput(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����"true"/"false"
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //
		String str_editable = (String) param_1; //�ڶ���ֵ,Ӧ����"true"/"false"

		if (str_itemKey != null && !str_itemKey.trim().equals("")) {
			str_itemKey = str_itemKey.trim();
			String[] str_allitems = str_itemKey.split(",");
			if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				if (str_editable.trim().equalsIgnoreCase("true")) { //�����true,������
					for (int i = 0; i < str_allitems.length; i++) {//�ù�ʽ���ñ�����ʱҪ������ʾ�����Ǳ�ʶ����ǰû����ʾֻ���ڱ���ʱ������У�顾���/2012-06-20��
						Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(str_allitems[i]);
						itemvo.setIsmustinput2("Y");//����ģ��vo�б��䣬ʹ֮�ڱ���ʱ����У��		
						WLTLabel label = (WLTLabel) cardPanel.getCompentByKey(str_allitems[i]).getLabel();
						if (!label.getText().startsWith("*")) {//����������Ǻſ�ʼ�������Ӻ�ɫ�Ǻ�
							label.setText("*" + label.getText()); //�������ı�	
						}
						label.addStrItemColor("*", Color.RED); //���ñ�������Ǻ�Ϊ��ɫ
					}
				} else if (str_editable.trim().equalsIgnoreCase("false")) { //�����"false"�����Ǳ�����!!!!
					for (int i = 0; i < str_allitems.length; i++) {
						Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(str_allitems[i]);
						itemvo.setIsmustinput2("N");//����ģ��vo�������ʹ֮�ڱ���ʱ������У��
						WLTLabel label = (WLTLabel) cardPanel.getCompentByKey(str_allitems[i]).getLabel();
						if (label.getText().startsWith("*")) {//��������Ǻſ�ʼ������Ҫ���������Ǻ�ȥ��
							label.setText(label.getText().substring(1)); //�������ı�	
						}
					}
				} else { //�����С�����˱�Ĳ�������ɶ������!!
				}
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel;
				if (str_editable.trim().equalsIgnoreCase("true")) { //�����true
					for (int i = 0; i < str_allitems.length; i++) {
						listPanel.getTempletItemVO(str_allitems[i]).setIsmustinput2("Y");
					}
				} else if (str_editable.trim().equalsIgnoreCase("false")) {
					for (int i = 0; i < str_allitems.length; i++) {
						listPanel.getTempletItemVO(str_allitems[i]).setIsmustinput2("N");
					}
				} else {
				}
			} else if (billPanel instanceof BillPropPanel) {

			}
		}
		inStack.push("ok"); //��Ϊ����ֵ��û��ʵ�ʷ���ֵ�����Ծͷ���һ��"ok"��ʾ��ֵ�ɹ���!!
	}

}
