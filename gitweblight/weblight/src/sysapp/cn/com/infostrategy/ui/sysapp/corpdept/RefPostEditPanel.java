package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * ��λ��ά��ҳ�棬��λ���ʵ�ʸ�λ��������pub_post���У�ʵ�ʸ�λ������������������Ը���deptid�Ƿ�Ϊ�գ������ж��Ƿ�Ϊ��λ��
 * @author lcj
 *
 */
public class RefPostEditPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {
	private BillListPanel list_post, list_postduty; //��λ,��λְ��
	private WLTButton btn_insert, btn_seq;

	@Override
	public void initialize() {
		list_post = new BillListPanel("PUB_POST_CODE2"); //��λ
		list_post.setTitleLabelText("��λ��");
		list_post.setItemVisible("post_status", false);//��������ģ���ˣ�Ϊ�˲�Ӱ������ҳ�棬��������һ�����ظ�λ״̬����ʾ��λ����
		list_post.setItemVisible("refpostid", false);
		list_post.setItemVisible("descr", true);
		list_post.setQuickQueryPanelVisiable(true);
		list_post.setDataFilterCustCondition("deptid is null");
		list_post.setItemWidth("code", 200);//��������ģ���ˣ�Ϊ�˲�Ӱ������ҳ�棬��������һ���п�
		list_post.setItemWidth("name", 200);
		list_post.setItemWidth("descr", 200);
		String panelStyle = this.getMenuConfMapValueAsStr("ҳ�沼��");//"ҳ�沼��"�������֣�Ĭ��Ϊ��һ�֣���1-��λ��ά��;2-��λ���ѯ;3-��λ��ְ��ά��;4-��λ��ְ���ѯ;
		if (panelStyle == null || panelStyle.trim().equals("")) {
			panelStyle = "1";
		}
		if ("1".equals(panelStyle)) {//��λ��ά��
			list_post.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_DELETE) });
			list_post.repaintBillListButton();
			this.add(list_post); //
		} else {
			list_post.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			list_post.repaintBillListButton();
			if ("2".equals(panelStyle)) {//��λ���ѯ
				this.add(list_post); //
			} else if ("3".equals(panelStyle)) {//��λ��ְ��ά��
				list_post.addBillListSelectListener(this);
				list_post.addBillListAfterQueryListener(this);//���Ӹ�λ��ѯ���¼������ְ���б����/2012-08-08��
				list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //��λְ��
				btn_insert = new WLTButton("����"); //
				btn_insert.addActionListener(this); //
				btn_seq = new WLTButton("����");//����ְ������ť�����/2014-12-16��
				btn_seq.addActionListener(this);

				list_postduty.addBatchBillListButton(new WLTButton[] { btn_insert, WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_DELETE), btn_seq }); //�������ð�ť!!!
				list_postduty.repaintBillListButton(); //

				WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_post, list_postduty); //
				split1.setDividerLocation(250); //

				this.add(split1); //
			} else if ("4".equals(panelStyle)) {//��λ��ְ���ѯ
				list_post.addBillListSelectListener(this);
				list_post.addBillListAfterQueryListener(this);//���Ӹ�λ��ѯ���¼������ְ���б����/2012-08-08��
				list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //��λ����
				list_postduty.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) }); //�������ð�ť!!!
				list_postduty.repaintBillListButton(); //
				WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_post, list_postduty); //
				split1.setDividerLocation(250); //
				this.add(split1); //
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //��������!
		} else if (e.getSource() == btn_seq) {
			onSeqDuty();
		}
	}

	/**
	 * ������λ����!!
	 */
	private void onInsert() {
		BillVO billVO = list_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ����λ���д˲���!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("postid", billVO.getStringValue("id")); //
		list_postduty.doInsert(defaultValueMap); //������λ����!!!
	}

	/**
	 * ְ���������/2014-12-16��
	 */
	private void onSeqDuty() {
		SeqListDialog dialog_post = new SeqListDialog(this, "ְ������", list_postduty.getTempletVO(), list_postduty.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {//������ȷ�����أ�����Ҫˢ��һ��ҳ��
			list_postduty.refreshData(); //
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billvo = list_post.getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		list_postduty.QueryDataByCondition("postid=" + billvo.getStringValue("id"));
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		list_postduty.clearTable();
	}

}
