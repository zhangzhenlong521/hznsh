package cn.com.infostrategy.ui.mdata.styletemplet;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * ��ʽ�����,���Զ���һ����ʽ
 * getLabel("abcd")  //
 * getCard("PUB_USER_CODE1")  //�õ�һ����Ƭ���
 * getList("PUB_USER_CODE1")  //�õ�һ���б����
 * getTree("PUB_USER_CODE1")  //�õ�һ��������� getSplit("sds","sds","����",100)
 * getSplit(getList(\"PUB_USER_CODE1\"),getCard(\"PUB_USER_CODE1\"),\"����\",300)  //�õ�һ���ָ���,����Ǳ�,�ұ�����
 * getTab(\"��һҳǩ\",getList(\"PUB_USER_CODE1\"),\"�ڶ�ҳǩ\",getTree(\"PUB_USER_CODE1\"))  //�õ�һ��Tab���,������ҳǩ,�ֱ����б�����
 * @author xch
 *
 */
public abstract class AbstractFormatWorkPanel extends AbstractWorkPanel {

	private BillFormatPanel billFormatPanel; //

	/**
	 * ���幫ʽ
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
