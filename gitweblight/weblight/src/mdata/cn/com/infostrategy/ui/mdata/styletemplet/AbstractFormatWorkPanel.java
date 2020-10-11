package cn.com.infostrategy.ui.mdata.styletemplet;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * 格式化面板,可以定义一个公式
 * getLabel("abcd")  //
 * getCard("PUB_USER_CODE1")  //得到一个卡片面板
 * getList("PUB_USER_CODE1")  //得到一个列表面板
 * getTree("PUB_USER_CODE1")  //得到一个树型面板 getSplit("sds","sds","上下",100)
 * getSplit(getList(\"PUB_USER_CODE1\"),getCard(\"PUB_USER_CODE1\"),\"左右\",300)  //得到一个分割器,左边是表,右边是树
 * getTab(\"第一页签\",getList(\"PUB_USER_CODE1\"),\"第二页签\",getTree(\"PUB_USER_CODE1\"))  //得到一个Tab面板,有两个页签,分别是列表与树
 * @author xch
 *
 */
public abstract class AbstractFormatWorkPanel extends AbstractWorkPanel {

	private BillFormatPanel billFormatPanel; //

	/**
	 * 定义公式
	 */
	public abstract String getFormatFormula(); //

	public abstract void custInitialize(); //

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billFormatPanel = new BillFormatPanel(getFormatFormula()); //
		this.add(billFormatPanel);
		custInitialize(); //
	}

	public BillFormatPanel getBillFormatPanel() {
		return billFormatPanel;
	}

}
