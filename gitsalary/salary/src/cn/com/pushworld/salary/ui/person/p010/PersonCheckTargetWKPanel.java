package cn.com.pushworld.salary.ui.person.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 员工评价标准维护。
 * 
 * @author haoming create by 2013-7-12
 */
public class PersonCheckTargetWKPanel extends AbstractWorkPanel implements ChangeListener, BillListSelectListener, ActionListener {

	private static final long serialVersionUID = 337419196967903418L;
	private BillListPanel targetListPanel;
	private WLTTabbedPane maintab = null;
	private WLTSplitPane mainsp = null;
	private BillListPanel type_list, target_list = null;
	private WLTButton add_btn, delete_btn, edit_btn, watch_btn, btn_moveUp, btn_moveDown, btn_moveUp2, btn_moveDown2 = null;
	private ImageIcon iconUp, iconDown;

	public void initialize() {
		type_list = new BillListPanel("SAL_PERSON_CHECK_TYPE_CODE2");
		type_list.addBillListSelectListener(this);
		target_list = new BillListPanel("SAL_PERSON_CHECK_LIST_CODE2");
		mainsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, type_list, target_list);
		mainsp.setDividerLocation(200);
		initBtn();
		maintab = new WLTTabbedPane();
		maintab.addTab("按考核对象显示", mainsp);
		maintab.addTab("全部显示", new JPanel(new BorderLayout()));
		maintab.addChangeListener(this);
		this.add(maintab);
	}
	
	private void initBtn() {
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "新增");
		add_btn.addActionListener(this);
		delete_btn = WLTButton.createButtonByType(WLTButton.LIST_DELETE, "删除");
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "修改");
		watch_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看");
		
		//上移， 下移按钮
		iconDown = UIUtil.getImage("down1.gif");
		TBUtil tbUtil = new TBUtil();
		iconUp = new ImageIcon(tbUtil.getImageRotate(iconDown.getImage(), 180)); //转180度!	
		btn_moveUp = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP);
		btn_moveDown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN);
		btn_moveUp.setToolTipText("上移");
		btn_moveDown.setToolTipText("下移");
		btn_moveUp.setIcon(iconUp);
		btn_moveDown.setIcon(iconDown);
		btn_moveUp.setText("");
		btn_moveDown.setText("");
		btn_moveUp.setPreferredSize(new Dimension(20, 23));
		btn_moveDown.setPreferredSize(new Dimension(20, 23));
		btn_moveUp.addActionListener(this);
		btn_moveDown.addActionListener(this);
		
		target_list.addBatchBillListButton(new  WLTButton[]{add_btn, delete_btn, edit_btn, watch_btn, btn_moveUp, btn_moveDown});
		target_list.repaintBillListButton();
	}

	public void stateChanged(ChangeEvent arg0) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				onTabChange();
			}
		});
	}
	
	private void onTargetAdd() {
		BillVO typevo = type_list.getSelectedBillVO();
		if (typevo == null) {
			MessageBox.show(this, "请选择考核对象归类来进行此操作!");
			return;
		}
		HashMap _defaultValue = new HashMap();
		_defaultValue.put("type", new ComBoxItemVO(typevo.getPkValue(), "", typevo.getRealValue("name")));
		target_list.doInsert(_defaultValue);
	}

	private void onTabChange() {
		if (maintab.getSelectedIndex() == 1) {
			if (targetListPanel == null) {
				targetListPanel = new BillListPanel("SAL_PERSON_CHECK_LIST_CODE1");
				WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT);
				WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_SAVE);
				WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
				
				//上移， 下移按钮
				btn_moveUp2 = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP);
				btn_moveDown2 = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN);
				btn_moveUp2.setToolTipText("上移");
				btn_moveDown2.setToolTipText("下移");
				btn_moveUp2.setIcon(iconUp);
				btn_moveDown2.setIcon(iconDown);
				btn_moveUp2.setText("");
				btn_moveDown2.setText("");
				btn_moveUp2.setPreferredSize(new Dimension(20, 23));
				btn_moveDown2.setPreferredSize(new Dimension(20, 23));
				btn_moveUp2.addActionListener(this);
				btn_moveDown2.addActionListener(this);
				targetListPanel.addBatchBillListButton(new WLTButton[] {btn_insert, btn_update, btn_delete ,btn_moveUp2,btn_moveDown2});
				targetListPanel.repaintBillListButton();
				maintab.getComponentAt(1).add(targetListPanel, BorderLayout.CENTER);
			}
//			targetListPanel.QueryDataByCondition("1=1");
			
		}else {
//			target_list.QueryDataByCondition("1=1");
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		onSelectType();
	}

	private void onSelectType() {
		target_list.setDataFilterCustCondition("type='" + type_list.getSelectedBillVO().getPkValue() + "'");
		target_list.QueryDataByCondition("1=1");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add_btn) {
			onTargetAdd();
		} else if (e.getSource() == btn_moveUp) {
			doBillListRowMove(target_list, true);
		} else if (e.getSource() == btn_moveDown) {
			doBillListRowMove(target_list, false);
		} else if (e.getSource() == btn_moveUp2) {
			doBillListRowMove(targetListPanel, true);
		} else if (e.getSource() == btn_moveDown2) {
			doBillListRowMove(targetListPanel, false);
		}
	}
	
	/**
	 * 列表行上移/下移 -- Gwang 2013-8-7
	 * isUP: true-上移, false-下移
	 * @throws Exception
	 */
	private void doBillListRowMove(BillListPanel billList, boolean isUP) {
		if (isUP) {
			billList.moveUpRow();
		} else {
			billList.moveDownRow();
		}
		int rowCount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		List sqls = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild);
			sqls.add("update " + billList.templetVO.getSavedtablename() + " set " + seqfild + " ='" + (i + 1) + "' where id=" + billList.getBillVO(i).getPkValue());
		}
		try {
			UIUtil.executeBatchByDS(null, sqls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
