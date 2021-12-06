package com.pushworld.ipushgrc.ui.lawcase.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class DeCaseCarryOutWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener{
	private BillListPanel caseList;
	private BillListPanel mainPointList;
	WLTButton new_point_btn;
	WLTButton edit_btn;
	WLTButton delete_btn;
	@Override
	public void initialize() {
		caseList=new BillListPanel("LBS_DECASE_CODE1");
		caseList.setDataFilterCustCondition("judgedate is not null");
		mainPointList=new BillListPanel("LBS_CASETRACK_CODE1");
		new_point_btn=new WLTButton("新增执行要点");
		edit_btn=WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "跟踪");
		delete_btn=WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		mainPointList.addBillListButton(new_point_btn);
		mainPointList.addBillListButton(edit_btn);
		mainPointList.addBillListButton(delete_btn);
		mainPointList.repaintBillListButton();
		new_point_btn.addActionListener(this);
		caseList.addBillListSelectListener(this);
		WLTSplitPane split=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,caseList,mainPointList);
		this.add(split);
	}
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billvo=_event.getCurrSelectedVO();
		String case_id=billvo.getStringValue("id");
		mainPointList.QueryDataByCondition("defendantcase_id="+case_id);
	}
	public void actionPerformed(ActionEvent e) {
		if(new_point_btn==e.getSource()){
			BillVO selbillvo=caseList.getSelectedBillVO();
			if(selbillvo==null){
				MessageBox.show(this,"请先选择一个案件！");
			}else{
				String defendantcase_id=selbillvo.getStringValue("id");
				HashMap initMap=new HashMap();
				initMap.put("defendantcase_id", new StringItemVO(defendantcase_id));
				mainPointList.doInsert(initMap);
			}
		}
	}
}

