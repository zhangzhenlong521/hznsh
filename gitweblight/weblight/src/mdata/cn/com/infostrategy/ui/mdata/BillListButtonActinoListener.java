package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillListButtonActinoListener extends EventListener {

	public void onBillListButtonClicked(BillListButtonClickedEvent _event) throws Exception; //����б�����

	public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception; //����б�����

	public void onBillListAddButtonClicked(BillListButtonClickedEvent _event) throws Exception; //����б�����

	public void onBillListEditButtonClicking(BillListButtonClickedEvent _event) throws Exception; //����б�����

	public void onBillListEditButtonClicked(BillListButtonClickedEvent _event); //����б�����

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent _event) throws Exception; //����б�����

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent _event); //����б�����

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent _event); //����б�����

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent _event); //����б�����

}
