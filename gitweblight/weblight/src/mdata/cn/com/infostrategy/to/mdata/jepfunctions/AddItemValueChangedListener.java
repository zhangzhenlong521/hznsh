package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 为模板上的某个item添加值值改变事件,可以直接传入一个实现了ItemValueListener接口的类 第一个参数是实现类路径。 第二个参数是执行方式，默认只在卡片执行。 可以配置 "卡片执行" "列表执行 " "卡片列表都执行"
 * 
 * @author gaofeng
 * @since 2010-6-23 下午11:24:49
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
		Object[] str_pars = new String[curNumberOfParameters]; // 构造参数列表!应该是一对,即位数和是偶数!
		if (curNumberOfParameters > 0) {
			for (int i = 0; i < curNumberOfParameters; i++) {
				str_pars[i] = inStack.pop();
			}
		} else {
			MessageBox.show(billPanel, "此方法需要传参。");
			return;
		}
		try {
			String classstr = (String) str_pars[0];
			if(str_pars.length == 1){
				classstr = (String) str_pars[0];
			}else{
				classstr = (String) str_pars[1];
			}
			
			if (str_pars.length == 1 && billPanel instanceof BillListPanel) { // 兼容以前
				return;
			} else if ("卡片执行".equals(str_pars[0]) && billPanel instanceof BillListPanel) {
				return;
			} else if ("列表执行".equals(str_pars[0]) && billPanel instanceof BillCardPanel) {
				return;
			}
			ItemValueListener listener = (ItemValueListener) Class.forName(String.valueOf(classstr)).newInstance();
			listener.process(billPanel);
		} catch (Exception e) {
			MessageBox.showException(billPanel, e);
		}

	}
}
