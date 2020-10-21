package cn.com.pushworld.wn.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
//import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

/**
 * 
 * @author zzl
 * 
 *         2019-4-26-����09:36:19 �ͻ�������Ϣ���
 *         ���µĴ���ͻ�������Ϣ�ȶ����µĴ���ͻ�������Ϣ���޸����µĿͻ������Ӧ������Ϣ
 */
public class CustomerInformationChanges extends AbstractWorkPanel implements
		ActionListener {
	private WLTButton btn_BG = new WLTButton("����������Ϣ���");
	private WLTButton btn_BGCK = new WLTButton("�ͻ�������Ϣ���");

	@Override
	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		btn_BG.setPreferredSize(new Dimension(130, 50));
		btn_BG.addActionListener(this);
		btn_BGCK.setPreferredSize(new Dimension(130, 50));
		btn_BGCK.addActionListener(this);
		this.add(btn_BG);
		this.add(btn_BGCK);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == btn_BG) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					getChange();
				}
			});
		}
		if (a.getSource() == btn_BGCK) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					getChangeCK();
				}
			});
		}

	}

	private void getChange() {// ����ͻ�������Ϣ���
		final String date = getDate();
		if (TBUtil.isEmpty(date)) {
			return;
		}
		String[] dates = date.split(";");
		try {
			WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
//			String str = service.getChange(dates[0].toString(),
//					dates[1].toString());  // ����Ĵ����Ҳ����ˣ�ֱ����д
			System.out.println("last:"+dates[0].toString()+",cur:"+dates[1].toString());
			String str=service.getNewChange(dates[1].toString(), dates[0].toString());
			MessageBox.show(this, str);
		} catch (Exception e) {
			MessageBox.show(this, "�ͻ�������Ϣ���ʧ��");
			e.printStackTrace();
		}
	}

	private void getChangeCK() {
		final String date = getDate();
		if (TBUtil.isEmpty(date)) {
			return;
		}
		String[] dates = date.split(";");
		try {
			WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			String str = service.getWgChange(dates[0].toString(),
					dates[1].toString());
			MessageBox.show(this, str);
		} catch (Exception e) {
			MessageBox.show(this, "�ͻ�������Ϣ���ʧ��");
			e.printStackTrace();
		}
	}



	public static void main(String[] args) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		cal.setTime(date);
		// cal.add(Calendar.YEAR, -1);//ȡ��ǰ���ڵ�ǰһ��.
		// cal.add(Calendar.MONTH, -1);//ȡ��ǰ���ڵĺ�һ��.
		cal.set(Calendar.DAY_OF_MONTH, 0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.format(cal.getTime());
		System.out.println("-0>>>>" + df.format(cal.getTime()));
		Calendar cal2 = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		cal2.setTime(date);
		cal2.set(Calendar.DAY_OF_MONTH, 31);
		cal2.add(Calendar.MONTH, -2);// ȡ��ǰ���ڵĺ�һ��.
		df.format(cal2.getTime());
		System.out.println("-1>>>>" + df.format(cal2.getTime()));
	}

	/**
	 * 
	 * @param _parent
	 * @return ѡ������
	 */
	private String getDate() {
		BillCardDialog cd = new BillCardDialog(this, "ѡ����������",
				"WN_QJ_DATE_CODE1", 600, 200);
		cd.setSaveBtnVisiable(false);
		cd.setVisible(true);
		if (cd.getCloseType() == -1) {
			return null;
		}
		String qdate = cd.getBillcardPanel().getRealValueAt("DQDATE_TIME");
		String hdate = cd.getBillcardPanel().getRealValueAt("XGDATE_TIME");
		return qdate + ";" + hdate;
	}

}
