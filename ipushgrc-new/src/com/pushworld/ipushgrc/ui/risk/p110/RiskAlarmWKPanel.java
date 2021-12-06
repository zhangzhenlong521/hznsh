package com.pushworld.ipushgrc.ui.risk.p110;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import com.pushworld.ipushgrc.ui.cmpreport.CommonDialPlotPanel;
import com.pushworld.ipushgrc.ui.report.radar.BillRadarPanel;

/*******************************************************************************
 * ����Ԥ��!!!!
 * 
 * @author xch
 * 
 */
public class RiskAlarmWKPanel extends AbstractWorkPanel implements ActionListener {

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		WLTTabbedPane tabPane = new WLTTabbedPane();
		tabPane.addTab("�Ǳ���Ԥ��", getPlotPanel());
		tabPane.addTab("�״�ͼ", getSpliderPanel());
		this.add(tabPane);
	}
	BillQueryPanel queryPanel_1 ;
	BillQueryPanel queryPanel_2 ;
	public JPanel getPlotPanel() {
		CommonDialPlotPanel cdpp_single = null;
		CommonDialPlotPanel cdpp_double = null;
		JPanel plotPanel = new JPanel(new FlowLayout());
		queryPanel_1 = new BillQueryPanel("TARGETREPORT_CODE1");
		queryPanel_1.addBillQuickActionListener(this);
//		queryPanel_1.setPreferredSize(new Dimension(400,70));
		queryPanel_2 = new BillQueryPanel("TARGETREPORT_CODE1");
//		queryPanel_2.setPreferredSize(new Dimension(400,70));
		queryPanel_2.addBillQuickActionListener(this);
		HashVO[] targetVO = null;
		try {
			targetVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_target where name ='�¼���������ʱ����' and showtype='�Ǳ���'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (targetVO.length > 0) {
			double eventDate = 0d; // ʱ���� ʵ��ֵ
			String type = targetVO[0].getStringValue("cycletype");  //���ȣ��꣬��
			if(type.equals("year")){  //  ���ָ��
				queryPanel_1.getCompentByKey("����");
				queryPanel_1.setRealValueAt("����","��");
			}else if(type.equals("season")){  //����ָ��  
				queryPanel_1.setRealValueAt("����","��");
			}else if(type.equals("month")){  //�¶�ָ��
				queryPanel_1.setRealValueAt("����","��");
			}
			try {
				HashVO[] eventVO = UIUtil.getHashVoArrayByDS(null, "select id,finddate,happendate from cmp_event where 1 = 1");
				CommonDate finddate = null;
				CommonDate happendate = null;
				int days = 0;
				for (int i = 0; i < eventVO.length; i++) {
					finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
					happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
					days += CommonDate.getDaysBetween(happendate, finddate);
				}
				if(eventVO.length != 0){
					eventDate = (double) days / eventVO.length;	
				}
				
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			double d_warnvalue = Double.parseDouble(targetVO[0].getStringValue("warnvalue")); // Ԥ��ֵ
			double d_normalvalue = Double.parseDouble(targetVO[0].getStringValue("normalvalue")); // ����ֵ
			double d_average = Double.parseDouble(targetVO[0].getStringValue("avgvalue")); // ƽ��ֵ
			double d_currvalue = eventDate;// ʵ��ֵ
			cdpp_single = new CommonDialPlotPanel(d_currvalue, d_average, 50D, 0D, 50D, 0D, d_warnvalue, d_normalvalue, 5D, 5D, "�¼�����ʱ����", "   ", "ͳһ");

		}
		// ***** ���������Ǳ��� ********//
		HashVO[] channelVO = null; // ����
		try {
			channelVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_target where name ='�¼���������ָ��' and showtype='�Ǳ���'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (channelVO.length > 0) {
			double channelNum = 0d; // ʱ���� ʵ��ֵ
			try {
				HashVO[] eventVO = UIUtil.getHashVoArrayByDS(null, "select cmp_event.id, dict.seq from cmp_event,pub_comboboxdict dict where cmp_event.findchannel = dict.id");
				double valueSum = 0d;
				for (int i = 0; i < eventVO.length; i++) {
					valueSum += eventVO[i].getIntegerValue("seq");
				}
				if(eventVO.length != 0){
					channelNum = valueSum / eventVO.length;
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			double c_warnvalue = Double.parseDouble(channelVO[0].getStringValue("warnvalue")); // Ԥ��ֵ
			double c_normalvalue = Double.parseDouble(channelVO[0].getStringValue("normalvalue")); // ����ֵ
			double c_average = Double.parseDouble(channelVO[0].getStringValue("avgvalue")); // ƽ��ֵ
			double c_currvalue = channelNum; // ʵ��ֵ
			cdpp_double = new CommonDialPlotPanel(c_currvalue, c_average, 10, 0D, 10D, 0D, c_warnvalue, c_normalvalue, 1D, 1D, "�¼���������ָ��", "   ", "ͳһ");
		}
		WLTPanel panel = new WLTPanel();
		if (cdpp_single != null) {
			JPanel firstpanel = new JPanel(new BorderLayout());
			firstpanel.add(queryPanel_1,BorderLayout.NORTH);
			panel.setPreferredSize(new Dimension(400, 400));
			panel.add(cdpp_single);
			firstpanel.add(panel,BorderLayout.CENTER);
			plotPanel.add(firstpanel);
		}

		WLTPanel panel_2 = new WLTPanel();
		if (cdpp_double != null) {
			panel_2.setPreferredSize(new Dimension(400, 400));
			panel_2.add(cdpp_double);
			JPanel secondpanel = new JPanel(new BorderLayout());
			secondpanel.add(queryPanel_2,BorderLayout.NORTH);
			secondpanel.add(panel_2,BorderLayout.CENTER);
			plotPanel.add(secondpanel);
		}
		if (cdpp_single == null && cdpp_double == null) {
			plotPanel.add(new JLabel("û��ָ��չ�ַ�ʽΪ�Ǳ��̣�"));
		}
		return plotPanel;
	}

	public JPanel getSpliderPanel() {
		HashVO[] vos = null;
		try {
			vos = UIUtil.getHashVoArrayByDS(null, "select '����ֵ' as ����,name ָ������,normalvalue ��ֵ from cmp_target where showtype='�״�ͼ'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		BillRadarPanel spliderPanel = new BillRadarPanel("����Ԥ��", vos, new String[] { "����" }, new String[] { "ָ������" }, "��ֵ", true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(spliderPanel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == queryPanel_1){
			HashMap conditionMap = queryPanel_1.getQuickQueryConditionAsMap();
			Object  obj_1 = conditionMap.get("����");
			Object  obj_2 = conditionMap.get("����");
			StringBuffer condition = new StringBuffer();
			if(obj_1!=null && !obj_1.equals("")){
				String corp = obj_1.toString();
				condition.append(" and eventcorpid = '" + corp +"'");
			}
			if(obj_2!=null && !obj_2.equals("")){
				condition.append(" and  ");
			}
			
		}else if(e.getSource() == queryPanel_2){
			
		}
			
	}

}
