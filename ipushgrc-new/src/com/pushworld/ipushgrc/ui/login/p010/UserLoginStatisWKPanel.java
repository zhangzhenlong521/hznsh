package com.pushworld.ipushgrc.ui.login.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class UserLoginStatisWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private BillCellPanel billCellPanel;//�������
	private BillQueryPanel queryPanel;//��ѯ���
	private BillDialog billDialog;//��ȡ��ʾ����
	private WLTPanel btnPanel;//����Excel������ť�����
	private WLTButton btn_export;//Excel������ť
	private JLabel label;
	private String date = "";//��¼��ѯ����в�ѯ��ѡ�������
	private Calendar calendar;//����
	private HashVO[] alluser = null;//������Ա����Ϣ
	private HashVO[] user = null;//�м�¼����Ա�ĵ�¼��Ϣ
	private HashVO[] valuesvo;//��¼��Ա�ĵ�¼��Ϣ
	private BillCellVO cellVO;//
	private BillListPanel billListPanel;//
	private String[] dates;//���һ�ܵ�����
	private String str_weekofyear;//�����еĵڼ���
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private RefItemVO returnRefItemVO = null; //
	private String today;

	@Override
	public void initialize() {
		returnRefItemVO = new RefItemVO();
		today = UIUtil.getCurrDate();
		returnRefItemVO.setId(today); //
		returnRefItemVO.setName(today); //
		billCellPanel = new BillCellPanel();
		btn_export = new WLTButton("����Excel");
		label = new JLabel("����ʱ��������׼��һ������ʱ����ڵ���20СʱΪ�ϸ� ��¼����������׼��ÿ�����ٵ�¼���Ϊ�ϸ�");
		label.setForeground(Color.RED);
		queryPanel = new BillQueryPanel("DEPTLOGINREPORT_YQ_Q01");
		queryPanel.setCompentObjectValue("date", returnRefItemVO);
		btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btn_export.addActionListener(this);
		btnPanel.add(btn_export, FlowLayout.LEFT);
		btnPanel.add(label, FlowLayout.CENTER);
		billCellPanel.add(btnPanel, BorderLayout.NORTH);
		billCellPanel.setEditable(false);
		billCellPanel.setToolBarVisiable(false);
		billCellPanel.addBillCellHtmlHrefListener(this);//���html�������
		queryPanel.addBillQuickActionListener(this);
		this.add(queryPanel, BorderLayout.NORTH);
		this.add(billCellPanel, BorderLayout.CENTER);
	}

	private void onQuery() {
		if (queryPanel.checkValidate()) {
			try {
				date = queryPanel.getCompentRealValue("date");//ȡ�ò�ѯ���е�ֵ
				billCellPanel.loadBillCellData(getBillcellVO(date));//������������
				billCellPanel.setEditable(false);//���ò��ɱ༭
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private BillCellVO getBillcellVO(String date) throws ParseException {
		calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(dateFormat1.parse(date));
		dates = getDayofWeek(date);//ȡ��һ�ܵ�����
		int weekofyear = calendar.get(Calendar.WEEK_OF_YEAR);//������һ���еڼ��ܣ�ע�⣬������ܿ����ˣ�����Ϊ�����Ǵ���ĵ�1��
		//������꣬����������һ��
		String year1 = dates[0].substring(0, 4);
		String year2 = dates[6].substring(0, 4);
		str_weekofyear = year1 + "���" + weekofyear + "��( " + dates[0] + " �� " + dates[6] + " )";//Ҫ�����¼���е�ֵ
		if (!year1.equals(year2)) {//�����ˣ��ô������
			str_weekofyear = year2 + "���" + weekofyear + "��( " + dates[0] + " �� " + dates[6] + " )";
		}
		try {
			user = UIUtil.getHashVoArrayByDS(null, "select * from pub_sysdeallog where substr(dealtime,1,10) >='" + dates[0] + "' and substr(dealtime,1,10) <='" + dates[6] + "' and DEALUSERID is not null  order by DEALUSERID ,dealtime asc ");
			alluser = UIUtil
					.getHashVoArrayByDS(null,
							"select t1.userdept deptid,t3.name deptname,t2.id  id,t2.name name  from pub_user_post t1 left join pub_user t2 on t1.userid =t2.id left join PUB_CORP_DEPT t3 on t1.userdept = t3.id where t1.isdefault= 'Y' and t2.name not like 'Admin' and t3.name is not null order by t3.LINKCODE asc");
			valuesvo = new HashVO[alluser.length];
			for (int k = 0; k < alluser.length; k++) {
				boolean haslogin = false;//�ж��Ƿ��¼��ϵͳ
				boolean end = false;//�ж��Ƿ��˳�ϵͳ
				boolean newday = true;//�ж��Ƿ����µ�һ�쿪ʼ
				String name = alluser[k].getStringValue("id");
				String starttime = "";//��¼ϵͳʱ��
				String endtime = "";//�˳�ϵͳʱ��
				long onlinesecond = 0;//��¼����ʱ��
				List list = new ArrayList();//��ŵ�¼����,Ϊ�˼���һ���ڵ�¼�Ĵ���
				valuesvo[k] = new HashVO();
				valuesvo[k].setAttributeValue("���ڲ���", alluser[k].getStringValue("deptname"));
				valuesvo[k].setAttributeValue("�û���", alluser[k].getStringValue("name"));
				int size = 0;//�Ա�list�Ĵ�С���ж��Ƿ�ʼ���µ�һ��
				for (int index = 0; index < user.length; index++) {
					String names = user[index].getStringValue("DEALUSERID");
					String type = user[index].getStringValue("DEALTYPE");
					String dealtime = user[index].getStringValue("DEALTIME").substring(0, 10);
					if (names.equals(name)) {//�ж���Ա�Ƿ��¼
						if (!haslogin) {
							haslogin = true;
						}
						size = list.size();//��¼�ϴ�ѭ����list�Ĵ�С
						if (!list.contains(dealtime)) {//�ж�list���Ƿ�����˸�����
							list.add(dealtime);
						}
						if (size != list.size()) {//���size��ֵ��list�Ĵ�С��ͬ,˵����ʼ�����µ�һ��
							newday = true;
						} else {
							newday = false;
						}
						if (newday) {
							if (!starttime.equals("") && !end && "��¼ϵͳ".equals(user[index - 1].getStringValue("DEALTYPE"))) {//�ж��Ƿ���һ���¼ϵͳ��û���˳�
								endtime = starttime.substring(0, 10) + " 24:00:00";//Ĭ����һ����˳�ʱ��Ϊ24��00��00
								onlinesecond += dateFormat2.parse(endtime).getTime() - dateFormat2.parse(starttime).getTime();//��������ʱ��
							}
							if ("��¼ϵͳ".equals(type)) {
								starttime = user[index].getStringValue("DEALTIME");
								end = false;
							}
						} else {
							if (!end && "�˳�ϵͳ".equals(type)) {
								endtime = user[index].getStringValue("DEALTIME");
								onlinesecond += dateFormat2.parse(endtime).getTime() - dateFormat2.parse(starttime).getTime();//��������ʱ��
								end = true;
							} else if (end && "��¼ϵͳ".equals(type)) {
								starttime = user[index].getStringValue("DEALTIME");
								end = false;
							}
						}

					}
				}
				long hour = onlinesecond / (1000 * 60 * 60);//����Сʱ
				long m = (onlinesecond - hour * 1000 * 60 * 60) / (60 * 1000);//��¼����
				long s = (onlinesecond - hour * 1000 * 60 * 60 - m * 60 * 1000) / 1000;//��¼��
				String onlinehours = hour + "Сʱ" + m + "��" + s + "��";
				valuesvo[k].setAttributeValue("����ʱ��", onlinehours);
				String online_ok = onlinesecond / 1000 % (24 * 3600) / 3600 >= 20 ? "�ϸ�" : "���ϸ�";//��Ա����ʱ���Ƿ�ϸ�
				valuesvo[k].setAttributeValue("����ʱ������", online_ok);
				valuesvo[k].setAttributeValue("��½����", list.size());
				if (list.size() > 5) {
					System.out.println(list);
				}
				String count_ok = list.size() >= 5 ? "�ϸ�" : "���ϸ�";//��Ա��¼�����Ƿ�ϸ�
				valuesvo[k].setAttributeValue("��½��������", count_ok);
				if (!haslogin) {
					valuesvo[k].setAttributeValue("��½����", "0");
					valuesvo[k].setAttributeValue("��½��������", "���ϸ�");
					valuesvo[k].setAttributeValue("����ʱ��", "0");
					valuesvo[k].setAttributeValue("����ʱ������", "���ϸ�");
				}
			}
			BillCellItemVO[][] billCellItemVO = new BillCellItemVO[alluser.length + 2][6];
			for (int row = 0; row < billCellItemVO.length; row++) {
				String titlename[] = new String[] { "���ڲ���", "�û���", "����ʱ��", "����ʱ������", "��¼����", "��¼��������" };
				for (int col = 0; col < billCellItemVO[0].length; col++) {
					billCellItemVO[row][col] = new BillCellItemVO();
					billCellItemVO[row][col].setCellrow(row);
					billCellItemVO[row][col].setCellcol(col);
					if (col == 0) {
						billCellItemVO[row][col].setColwidth("150"); //
					} else {
						billCellItemVO[row][col].setColwidth("100"); //
					}
					if (row > 1) {
						billCellItemVO[row][col].setRowheight("20");
						billCellItemVO[row][col].setFontsize("12");
						billCellItemVO[row][col].setCellvalue(valuesvo[row - 2].getStringValue(col)); //
						if (col == 2) {//������ʱ��һ�м���html����
							billCellItemVO[row][col].setIshtmlhref("Y");
							billCellItemVO[row][col].setForeground("0,0,255");
							billCellItemVO[row][col].setCellkey(valuesvo[row - 2].getStringValue(1));//����keyֵ�����������Ӻ󵯳��Ĵ��������ݵĹ���
						}
					} else if (row == 1) {//��ͷ��ʾ����
						billCellItemVO[row][col].setHalign(2);
						billCellItemVO[row][col].setRowheight("35");
						billCellItemVO[row][col].setFontsize("14");
						billCellItemVO[row][col].setBackground("200,255,230");
						billCellItemVO[row][col].setCellvalue(titlename[col]); //
					} else if (row == 0 && col == 0) {//�������ʾ
						billCellItemVO[0][0].setCellvalue(str_weekofyear); //
						billCellItemVO[0][0].setHalign(2);
						billCellItemVO[0][0].setFonttype("����");
						billCellItemVO[0][0].setFontsize("14"); //
						billCellItemVO[0][0].setFontstyle("1"); //
						billCellItemVO[0][0].setRowheight("40"); //
						billCellItemVO[0][0].setBackground("200,255,230");
						billCellItemVO[0][0].setSpan("1,6"); //
					}
				}
			}
			cellVO = new BillCellVO();
			cellVO.setRowlength(alluser.length + 2); //
			cellVO.setCollength(6); //
			cellVO.setCellItemVOs(billCellItemVO);

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cellVO;

	}

	/* �õ�һ����������һ�ܵ�����
	*/
	private String[] getDayofWeek(String str_date) throws ParseException {
		String[] week = new String[7];
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFormat1.parse(str_date));
		int weekNum = cal.get(Calendar.DAY_OF_WEEK);
		if (weekNum == 1) {//��������գ���һ�죬�ٵñ�������
			cal.add(cal.DATE, -1);
		}
		for (int i = 1; i <= 7; i++) {//�����Ϊ1�ܵĵ�һ�������ܵ�������,��һ��2�죬
			cal.set(Calendar.DAY_OF_WEEK, i);//iֵ����һ�ܵĵڼ���
			cal.add(Calendar.DAY_OF_YEAR, 1);//Ϊ�õ������ڶ�����һ�죬�����͵õ��й�����һ��
			week[i - 1] = dateFormat1.format(cal.getTime());
		}
		return week;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_export) {
			billCellPanel.exportExcel();//����Excel
		} else {
			onQuery();//��ѯ
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent e) {
		if (e.getCellItemValue().equals("0")) {
			MessageBox.show(this, "����Ա������û�е�¼ϵͳ.");
			return;
		}
		billDialog = new BillDialog(this, str_weekofyear + "��Ա��¼��Ϣ����", 600, 650);
		billListPanel = new BillListPanel("PUB_SYSDEALLOG_CODE1_ZYC");
		billListPanel.QueryDataByCondition(" substr(dealtime,1,10) >='" + dates[0] + "' and substr(dealtime,1,10) <='" + dates[6] + "' and DEALUSERNAME like '%/" + e.getCellItemKey() + "'");//��������
		billDialog.add(billListPanel, BorderLayout.CENTER);
		billDialog.addConfirmButtonPanel(1);//���ȷ����ť
		billDialog.setVisible(true);
	}

}
