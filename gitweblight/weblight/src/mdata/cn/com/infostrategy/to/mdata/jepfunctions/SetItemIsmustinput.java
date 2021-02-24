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
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是"true"/"false"
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //
		String str_editable = (String) param_1; //第二个值,应该是"true"/"false"

		if (str_itemKey != null && !str_itemKey.trim().equals("")) {
			str_itemKey = str_itemKey.trim();
			String[] str_allitems = str_itemKey.split(",");
			if (billPanel instanceof BillCardPanel) { //如果是卡片面板
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				if (str_editable.trim().equalsIgnoreCase("true")) { //如果是true,必输项
					for (int i = 0; i < str_allitems.length; i++) {//用公式设置必输项时要联动显示出红星标识，以前没有显示只是在保存时进行了校验【李春娟/2012-06-20】
						Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(str_allitems[i]);
						itemvo.setIsmustinput2("Y");//设置模板vo中必输，使之在保存时进行校验		
						WLTLabel label = (WLTLabel) cardPanel.getCompentByKey(str_allitems[i]).getLabel();
						if (!label.getText().startsWith("*")) {//如果不是以星号开始，则增加红色星号
							label.setText("*" + label.getText()); //真正的文本	
						}
						label.addStrItemColor("*", Color.RED); //设置必输项的星号为红色
					}
				} else if (str_editable.trim().equalsIgnoreCase("false")) { //如果是"false"，不是必输项!!!!
					for (int i = 0; i < str_allitems.length; i++) {
						Pub_Templet_1_ItemVO itemvo = cardPanel.getTempletItemVO(str_allitems[i]);
						itemvo.setIsmustinput2("N");//设置模板vo中自由项，使之在保存时不进行校验
						WLTLabel label = (WLTLabel) cardPanel.getCompentByKey(str_allitems[i]).getLabel();
						if (label.getText().startsWith("*")) {//如果是以星号开始，则需要将必输项星号去掉
							label.setText(label.getText().substring(1)); //真正的文本	
						}
					}
				} else { //如果不小心输了别的参数，则啥都不干!!
				}
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel;
				if (str_editable.trim().equalsIgnoreCase("true")) { //如果是true
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
		inStack.push("ok"); //因为设置值，没有实际返回值，所以就返回一个"ok"表示赋值成功了!!
	}

}
