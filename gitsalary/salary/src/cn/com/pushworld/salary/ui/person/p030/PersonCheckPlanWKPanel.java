package cn.com.pushworld.salary.ui.person.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoAdapter;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;

/**
 * 员工考核方案
 * @author Administrator
 *
 */
public class PersonCheckPlanWKPanel extends AbstractWorkPanel {
	private BillListPanel plan_list = null;
	private WLTButton add_btn, delete_btn, edit_btn, watch_btn, moveup_btn0, movedown_btn0, moveup_btn, movedown_btn = null;
	private ImageIcon iconUp, iconDown;
	public void initialize() {
		plan_list = new BillListPanel("SAL_PERSON_CHECK_PLAN_CODE1");
		plan_list.setCanShowCardInfo(false);
		
		iconDown = UIUtil.getImage("down1.gif");
		TBUtil tbUtil = new TBUtil();
		iconUp = new ImageIcon(tbUtil.getImageRotate(iconDown.getImage(), 180)); //转180度!
		
		initBtn();
		plan_list.addBillListButtonActinoListener(new BillListButtonActinoAdapter() {
			public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception {
				addMoveBtn(_event.getCardPanel());
			}
			
			public void onBillListEditButtonClicking(BillListButtonClickedEvent _event) throws Exception{
				addMoveBtn(_event.getCardPanel());
			}
			
			public void onBillListLookAtButtonClicking(BillListButtonClickedEvent _event) {
				addMoveBtn(_event.getCardPanel());
			}
		});
		this.add(plan_list);
	}
	
	private void addMoveBtn(final BillCardPanel bc) {
		final BillListPanel bl = ((CardCPanel_ChildTable)bc.getCompentByKey("scorers")).getBillListPanel();
		moveup_btn = new WLTButton("");
		moveup_btn.setIcon(iconUp);
		moveup_btn.setToolTipText("上移");		
		moveup_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					onBillListRowUp(bl);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		movedown_btn = new WLTButton("");
		movedown_btn.setIcon(iconDown);
		movedown_btn.setToolTipText("下移");
		movedown_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					onBillListRowDown(bl);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		bl.addCustButton(moveup_btn);
		bl.addCustButton(movedown_btn);
		bl.queryDataByCondition("planid=" + bc.getBillVO().getPkValue(), "seq");
	}
	
	private void initBtn() {
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "新增");
		delete_btn = WLTButton.createButtonByType(WLTButton.LIST_DELETE, "删除");
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "修改");
		watch_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看");
		moveup_btn0 = new WLTButton("");
		moveup_btn0.setIcon(iconUp);
		moveup_btn0.setToolTipText("上移");	
		moveup_btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					onBillListRowUp(plan_list);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		movedown_btn0 = new WLTButton("");
		movedown_btn0.setIcon(iconDown);
		movedown_btn0.setToolTipText("下移");
		movedown_btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					onBillListRowDown(plan_list);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		plan_list.addBatchBillListButton(new WLTButton[]{add_btn, delete_btn, edit_btn, watch_btn, moveup_btn0, movedown_btn0});
		plan_list.repaintBillListButton();
	}
	
	private void onBillListRowUp(BillListPanel billList) throws Exception {
		billList.moveUpRow();
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		List sqls = new ArrayList();
		for (int i = 0; i < li_rowcount; i++) {
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild);
				sqls.add("update " + billList.templetVO.getSavedtablename() + " set " + seqfild + " ='" + (i + 1) + "' where id=" + billList.getBillVO(i).getPkValue());
			}
		}
		UIUtil.executeBatchByDS(null, sqls);
	}

	/**
	 * 列表行下移
	 * @throws Exception
	 */
	private void onBillListRowDown(BillListPanel billList) throws Exception {
		billList.moveDownRow();
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		List sqls = new ArrayList();
		for (int i = 0; i < li_rowcount; i++) {
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild);
				sqls.add("update " + billList.templetVO.getSavedtablename() + " set " + seqfild + " ='" + (i + 1) + "' where id=" + billList.getBillVO(i).getPkValue());
			}
		}
		UIUtil.executeBatchByDS(null, sqls);
	}

}
