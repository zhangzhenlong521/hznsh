package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillListButtonActinoListener extends EventListener {

	public void onBillListButtonClicked(BillListButtonClickedEvent _event) throws Exception; //点击列表按表动作

	public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception; //点击列表按表动作

	public void onBillListAddButtonClicked(BillListButtonClickedEvent _event) throws Exception; //点击列表按表动作

	public void onBillListEditButtonClicking(BillListButtonClickedEvent _event) throws Exception; //点击列表按表动作

	public void onBillListEditButtonClicked(BillListButtonClickedEvent _event); //点击列表按表动作

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent _event) throws Exception; //点击列表按表动作

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent _event); //点击列表按表动作

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent _event); //点击列表按表动作

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent _event); //点击列表按表动作

}
