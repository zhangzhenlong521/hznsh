package cn.com.pushworld.salary.ui.baseinfo.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 废弃！Gwang 2013-08-28
 * 定性与定量考核得分权重 部门考核按照部门设置 人员考核按照岗位分类设置
 * @author Administrator
 */
public class TargetSort_Weight_RatioWKPanel extends AbstractWorkPanel implements ChangeListener, BillTreeSelectListener, BillListSelectListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTTabbedPane maintab = null;
	private WLTSplitPane secondsp = null;
	private BillTreePanel depttree = null;
	private BillCardPanel card1, card2 = null;
	private BillListPanel postsortlist = null;
	private WLTButton cardsave, cardsave2 = null;
	private HashMap allcheckeddeptid = new HashMap();

	public void initialize() {
		//		maintab = new WLTTabbedPane();
		//		maintab.addTab("部门考核", getFirstSP());
		//		maintab.addTab("员工考核", new JPanel(new BorderLayout()));
		//		maintab.addChangeListener(this);
		//		this.add(maintab);

		//不要“部门考核”设置了, 同一用部门指标池里的权重计算  Gwang 2013-08-25		
		this.add(this.getSecondSP());
	}

	/*private WLTSplitPane getFirstSP() {
		if (firstsp == null) {
			depttree = new BillTreePanel("PUB_CORP_DEPT_SELF_SALARY");
			depttree.addBillTreeSelectListener(this);
			depttree.queryDataByCondition(null);
			card1 = new BillCardPanel("SAL_TARGETSORT_WEIGHT_RATIO_CODE1");
			cardsave = new WLTButton("保存");
			cardsave.addActionListener(this);
			card1.addBillCardButton(cardsave);
			card1.repaintBillCardButton();
			firstsp = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, depttree, card1);
			try {
				String[] alldeptid = UIUtil.getStringArrayFirstColByDS(null, "select checkeddept from sal_target_list where type='部门定性指标' union all select deptid from sal_target_checkeddept where targetid in(select id from sal_target_list where type='部门定量指标' )");
				if (alldeptid != null && alldeptid.length > 0) {
					for (int i = 0; i < alldeptid.length; i++) {
						String[] deptids = TBUtil.getTBUtil().split(alldeptid[i], ";");
						if (deptids != null && deptids.length > 0) {
							for (int j = 0; j < deptids.length; j++) {
								if (deptids[j] != null && !"".equals(deptids[j])) {
									allcheckeddeptid.put(deptids[j], "");
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return firstsp;
	}
	*/
	public void stateChanged(ChangeEvent e) {
		if (maintab.getSelectedIndex() == 1) {
			if (secondsp == null) {
				new SplashWindow(maintab, new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent arg0) {
						maintab.getComponentAt(1).add(getSecondSP());
					}
				});
			}
		}
	}

	private WLTSplitPane getSecondSP() {
		if (secondsp == null) {
			postsortlist = new BillListPanel("PUB_COMBOBOXDICT_SALARY");
			postsortlist.queryDataByCondition("type='薪酬_岗位归类'", "seq asc");
			postsortlist.addBillListSelectListener(this);
			card2 = new BillCardPanel("SAL_TARGETSORT_WEIGHT_RATIO_CODE2");
			cardsave2 = new WLTButton("保存");
			cardsave2.addActionListener(this);
			card2.addBillCardButton(cardsave2);
			card2.repaintBillCardButton();
			secondsp = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, postsortlist, card2);
		}
		return secondsp;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent e) {

		if (e.getCurrSelectedNode().isRoot()) {
			card1.setEditable(false);
			card1.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		} else {
			if (!allcheckeddeptid.containsKey(e.getCurrSelectedVO().getPkValue())) {
				int rtn_ = MessageBox.showOptionDialog(this, "指标库中未设置考核此部门,无需设置考核权重!", "友情提示", new String[] { "继续设置", "取消" });
				if (rtn_ != 0) {
					card1.reset();
					card1.setEditable(false);
					card1.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
					cardsave.setVisible(false);
					return;
				}
			}
			card1.queryDataByCondition("deptid=" + e.getCurrSelectedVO().getPkValue());
			cardsave.setVisible(true);
			if (card1.getBillVO().getPkValue() == null || "".equals(card1.getBillVO().getPkValue())) {
				card1.insertRow();
				card1.setEditableByInsertInit();
				card1.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				card1.setValueAt("deptid", new RefItemVO(e.getCurrSelectedVO().getPkValue(), "", e.getCurrSelectedVO().toString()));
			} else {
				card1.setEditableByEditInit();
				card1.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {
		card2.queryDataByCondition("postsortid='" + e.getCurrSelectedVO().getStringValue("id") + "'");
		if (card2.getBillVO().getPkValue() == null || "".equals(card2.getBillVO().getPkValue())) {
			card2.insertRow();
			card2.setEditableByInsertInit();
			card2.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			card2.setValueAt("postsortid", new StringItemVO(e.getCurrSelectedVO().getStringValue("id")));
		} else {
			card2.setEditableByEditInit();
			card2.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		}
	}

	private void onBillCardSave() throws Exception {
		boolean iconchange = card1.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INSERT);
		if (card1.checkValidate(card1)) {
			card1.execEditFormula("qualitativeweights");
			card1.updateData();
			MessageBox.show(this, "保存成功!");
			if (iconchange) {
				BillTreeNodeVO oldvo = (BillTreeNodeVO) depttree.getSelectedNode().getUserObject();
				oldvo.setIconName("office_175.gif");
				depttree.updateUI();
			}
		}
	}

	private void onBillCardSave2() throws Exception {
		if (card2.checkValidate(card2)) {
			card2.execEditFormula("qualitativeweights");
			card2.updateData();
			MessageBox.show(this, "保存成功!");
		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == cardsave) {
				onBillCardSave();
			} else {
				onBillCardSave2();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
