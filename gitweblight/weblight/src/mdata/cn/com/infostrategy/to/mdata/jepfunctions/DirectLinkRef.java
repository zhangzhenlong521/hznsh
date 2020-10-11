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
 * 按钮点击处理
 * 功能：在一个n:m的关系表中新增一行记录
 * 适用：用于主子表情况下，已知主表记录，新增对子表的关联
 * 使用步骤：
 * 1. 建立n:m关系表,如表中冗余了足够的显示信息，则直接建立模板；
 * 2. 若关系表中没有冗余信息，则建立视图，包括关系表（必填字段id--需要保存、冗余字段--可以保存也可以不保存，主要用于显示信息）
 * 和待关联的物理表（信息字段--设成不显示），然后建立模板
 *    如果建立了冗余字段，可以不要关联物理表；否则需要关联物理表。
 * 3. 模板中的前2字段，必须第一个是主表id，第二个是待关联表id
 * 4. 对于关系表中的id字段，建立编辑公式，用来待关联表的模板数据来初始化冗余字段
 * 如：setItemValue("field1",getItemValue("file_id.cmpfilecode"));
 * 5. 建立按钮，设定执行前公式为本公式，如：DirectLinkRef（"待关联id"）
 * 
 * 在Class【cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI】中被定义
 * 
 * @author 1
 *
 */
public class DirectLinkRef extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	public DirectLinkRef(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = 1;
		this.billPanel = _billPanel;
		this.wltButton = _btn; //
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_field2 = (String) inStack.pop();

		//打开参照内容对话框
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
			UIRefPanel tempRefPanel = new UIRefPanel(pub_Templet_1_ItemVO, new RefItemVO(null, null, null), billPanel, false); //以前郝明是用了个类叫RefDialog,其实里面的逻辑与UIRefPanel中的逻辑是重复的,所以可以省去这个类!!!
			AbstractRefDialog refDialog = tempRefPanel.getRefDialog(); //创建参照窗口!!!
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
		int curr_li_col = 2;//特殊设置，必须在模板中将该字段放在第2个
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
