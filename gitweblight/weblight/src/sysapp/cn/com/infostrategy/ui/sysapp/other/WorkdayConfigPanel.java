package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillButtonPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.CommonDateQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;

/**
 * 平台配置中有关工作日配置的类！
 * @author lcj
 *
 */
public class WorkdayConfigPanel extends AbstractWorkPanel implements ActionListener {
	private CommonDateQueryPanel commonDateQueryPanel = null;
	private RefItemVO refItemVO = null;
	private BillListPanel billListPanel_day = null;
	private QueryCPanel_UIRefPanel ref_daytime = null;
	private WLTButton btn_search = null;
	private WLTButton btn_add = null;
	private WLTButton btn_save = null;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		commonDateQueryPanel = new CommonDateQueryPanel("年;季;月");
		commonDateQueryPanel.getJtree().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onclick();
				}
			}
		});
		billListPanel_day = new BillListPanel("PUB_DATEOPTION_CODE1");
		GregorianCalendar cal = new GregorianCalendar();
		String str_sql1 = "select  *  from  pub_dateoption where daytime like '" + cal.get(Calendar.YEAR) + "%'"; //初始化显示当年记录
		billListPanel_day.QueryData(str_sql1);

		ref_daytime = new QueryCPanel_UIRefPanel(billListPanel_day.getTempletItemVO("daytime"), billListPanel_day, null);
		ref_daytime.setOpaque(false);
		btn_search = new WLTButton("查询有效工作日");
		btn_add = new WLTButton("添加一年记录");
		btn_save = new WLTButton("保存");
		btn_search.addActionListener(this);
		btn_add.addActionListener(this);
		btn_save.addActionListener(this);

		BillButtonPanel btnpanel = billListPanel_day.getBillListBtnPanel();
		btnpanel.setLayout(new FlowLayout());
		btnpanel.add(ref_daytime);
		btnpanel.add(btn_search);
		btnpanel.add(btn_add);
		btnpanel.add(btn_save);

		WLTSplitPane splitPanel_month_day = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, commonDateQueryPanel, billListPanel_day); //
		splitPanel_month_day.setDividerLocation(200); //	
		this.add(splitPanel_month_day);
	}

	/**
	 * 增加一年的记录，默认周六周日isFree为"N"其他为"Y"
	 * @param _year
	 */
	public void addDatas() {
		refItemVO = commonDateQueryPanel.getSelectedRefItemVO();
		if (refItemVO == null) { //如果未选中树节点，未选择年份，则退出
			return;
		}
		String str_name = refItemVO.getName();
		String str_year = str_name.substring(0, 4);

		String str_sql = "select  count(id)  from  pub_dateoption  where daytime like '" + str_year + "%'";
		String str_count = "";
		try {
			str_count = UIUtil.getStringValueByDS(null, str_sql);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if ("0".equals(str_count)) {
			billListPanel_day.clearTable();//删除页面显示数据，但不操作数据库！				
			GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(str_year), 0, 0);

			for (int i = 0; i < 366; i++) {
				int oldyear = cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				int int_year = cal.get(Calendar.YEAR);
				if (int_year > oldyear && i > 364) {
					break;
				}
				int int_month = cal.get(Calendar.MONTH);
				int int_day = cal.get(Calendar.DAY_OF_MONTH);
				String str_date = "";
				if (int_month < 9) {
					str_date += int_year + "-0" + (int_month + 1);
				} else {
					str_date += int_year + "-" + (int_month + 1);
				}
				if (int_day < 10) {
					str_date += "-0" + int_day;
				} else {
					str_date += "-" + int_day;
				}
				String str_week = this.getWeek(int_year, int_month, int_day);
				String isFree = null;
				if ("周六".equals(str_week) || "周日".equals(str_week)) {
					isFree = "Y";
				} else {
					isFree = "N";
				}
				int li_newrow = billListPanel_day.newRow();
				billListPanel_day.setRealValueAt(isFree, li_newrow, "isfree");
				billListPanel_day.setRealValueAt(str_date, li_newrow, "daytime");
				billListPanel_day.setRealValueAt(str_week, li_newrow, "week");
			}
			billListPanel_day.saveData();
		} else {
			MessageBox.show(this, str_year + "年的记录已添加，请勿重复添加！");
		}
	}

	public void onclick() {
		RefItemVO select_refItemVO = commonDateQueryPanel.getSelectedRefItemVO();
		if (select_refItemVO == null || select_refItemVO.equals(refItemVO)) {//判断如果选中节点不变，就直接返回
			return;
		}
		refItemVO = select_refItemVO;
		if (refItemVO != null && refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
			ref_daytime.setObject(refItemVO); //	控制日历控件中的显示！
			String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
			String str_convertcondition = new TBUtil().replaceAll(str_macrocondition, "{itemkey}", ref_daytime.getItemKey());
			String sql = "select *  from pub_dateOption  where 1=1 and " + str_convertcondition;
			billListPanel_day.QueryData(sql);
		}
	}

	public String getWeek(int _year, int _month, int _day) {
		GregorianCalendar newDate = new GregorianCalendar(_year, _month, _day);
		String week = "";
		switch (newDate.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			week = "周日";
			break;
		case 2:
			week = "周一";
			break;
		case 3:
			week = "周二";
			break;
		case 4:
			week = "周三";
			break;
		case 5:
			week = "周四";
			break;
		case 6:
			week = "周五";
			break;
		case 7:
			week = "周六";
		}
		return week;
	}

	public void getWorkdays() {
		//		String time = ((QueryCPanel_UIRefPanel)billListPanel_day.getBillListBtnPanel().getComponent(2)).getValue();//格式为：2009-12-14;2009-12-23
		//		String countday = null;
		//		if(time!=null&&!"".equals(time)){
		//			countday = new TBUtil().countWorkdays(time, "N");  //查询有效工作日;
		//		}		 
		//		if(countday!=null){
		//			JOptionPane.showMessageDialog(this,"有效工作日为："+countday+"天!");
		//		}else{
		//			MessageBox.show("请在左框中选择日期！");
		//		}

		RefItemVO refItemVO = (RefItemVO) ref_daytime.getObject(); //
		String str_convertcondition = "";
		if (refItemVO != null && refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
			String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
			str_convertcondition = new TBUtil().replaceAll(str_macrocondition, "{itemkey}", ref_daytime.getItemKey());
			String sql = "select count(id) from pub_dateOption where  isFree='N' and " + str_convertcondition;
			try {
				String countday = UIUtil.getStringValueByDS(null, sql);
				String day_sql = "select * from pub_dateOption where  1=1 and " + str_convertcondition;
				billListPanel_day.QueryData(day_sql);
				MessageBox.show(this, refItemVO.getName().replace(";", "") + "的有效工作日为：" + countday + "天!");
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else {
			MessageBox.show(this, "请在左框中选择日期！");
		}
	}

	private void onSave() {
		billListPanel_day.saveData();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_search) {
			getWorkdays();
		} else if (e.getSource() == btn_add) {
			addDatas();
		} else if (e.getSource() == btn_save) {
			onSave();
		}
	}

}
