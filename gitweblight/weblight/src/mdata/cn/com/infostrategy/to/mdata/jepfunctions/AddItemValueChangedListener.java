package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * Ϊģ���ϵ�ĳ��item���ֵֵ�ı��¼�,����ֱ�Ӵ���һ��ʵ����ItemValueListener�ӿڵ��� ��һ��������ʵ����·���� �ڶ���������ִ�з�ʽ��Ĭ��ֻ�ڿ�Ƭִ�С� �������� "��Ƭִ��" "�б�ִ�� " "��Ƭ�б�ִ��"
 * 
 * @author gaofeng
 * @since 2010-6-23 ����11:24:49
 */
public class AddItemValueChangedListener extends PostfixMathCommand {
	private BillPanel billPanel = null;

	public AddItemValueChangedListener() {
		numberOfParameters = -1;
	}

	public AddItemValueChangedListener(BillPanel _billPanel) {
		numberOfParameters = -1;
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object[] str_pars = new String[curNumberOfParameters]; // ��������б�!Ӧ����һ��,��λ������ż��!
		if (curNumberOfParameters > 0) {
			for (int i = 0; i < curNumberOfParameters; i++) {
				str_pars[i] = inStack.pop();
			}
		} else {
			MessageBox.show(billPanel, "�˷�����Ҫ���Ρ�");
			return;
		}
		try {
			String classstr = (String) str_pars[0];
			if(str_pars.length == 1){
				classstr = (String) str_pars[0];
			}else{
				classstr = (String) str_pars[1];
			}
			
			if (str_pars.length == 1 && billPanel instanceof BillListPanel) { // ������ǰ
				return;
			} else if ("��Ƭִ��".equals(str_pars[0]) && billPanel instanceof BillListPanel) {
				return;
			} else if ("�б�ִ��".equals(str_pars[0]) && billPanel instanceof BillCardPanel) {
				return;
			}
			ItemValueListener listener = (ItemValueListener) Class.forName(String.valueOf(classstr)).newInstance();
			listener.process(billPanel);
		} catch (Exception e) {
			MessageBox.showException(billPanel, e);
		}

	}
}
