package cn.com.pushworld.wn.bs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class YktWcrate extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener{
	private UIUtil uiUtil = new UIUtil();
	private BillListDialog dialog=null;
	private String deptname;
	private BillListPanel listPanel = new BillListPanel("V_HZ_YKT_RATE_20210127_CODE1");
	@Override
	public void actionPerformed(ActionEvent e) {
		if (listPanel.getQuickQueryPanel() == e.getSource()) {
			try {
				QuickQuery();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	@Override
	public void initialize() {
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// 获取到快速查询事件
		listPanel.setVisible(true);
		listPanel.addBillListHtmlHrefListener(this);
		this.add(listPanel);
	}
	private void QuickQuery() throws ParseException {
//		String date = listPanel.getQuickQueryPanel().getCompentRealValue("date");
//		String selDate = getSelTime(date);//暂时还没想好要怎么用，暂时默认查前一天吧
		listPanel.queryDataByDS(null, "select * from v_hz_ykt_rate_"+getQYTTime()+"");
	}
	public String getSelTime(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date sed = format.parse(date);
		String selDate = format.format(sed);
		return selDate;
	}

	public String getQYTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
			final BillVO vo=listPanel.getSelectedBillVO();
	        deptname=vo.getStringValue("name");
	        dialog=new BillListDialog(listPanel,"网格信息查看","HZ_YKT_RATE_20210127_CODE1",2000,800);
	        dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_"+getQYTTime()+" where  name='"+deptname+"'");
	        dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						StringBuffer sb=new StringBuffer();
	                    String A014=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A014");
	                    String A015=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A015");
	                    String A018=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A018");
	                    String A020=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A020");
	                    String A021=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A021");
	                    String NEW_SF_YKT=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("NEW_SF_YKT");
	                    String H=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("H");
	                    if(A014==null || A014.equals("") || A014.equals(null) || A014.equals(" ")){
	                    }else{
	                        sb.append(" and A014='"+A014+"'");
	                    }
	                    if(A015==null || A015.equals("") || A015.equals(null) || A015.equals(" ")){
	                    }else{
	                        sb.append(" and A015='"+A015+"'");
	                    }
	                    if(A018==null || A018.equals("") || A018.equals(null) || A018.equals(" ")){
	                    }else{
	                        sb.append(" and A018='"+A018+"'");
	                    }if(A020==null || A020.equals("") || A020.equals(null) || A020.equals(" ")){
	                    }else{
	                        sb.append(" and A020='"+A020+"'");
	                    }
	                    if(A021==null || A021.equals("") || A021.equals(null) || A021.equals(" ")){
	                    }else{
	                        sb.append(" and A021='"+A021+"'");
	                    }
	                    if(NEW_SF_YKT==null || NEW_SF_YKT.equals("") || NEW_SF_YKT.equals(null) || NEW_SF_YKT.equals(" ")){
	                    }else{
	                        sb.append(" and NEW_SF_YKT='"+NEW_SF_YKT+"'");
	                    }if(sb.toString()==null){
	                        dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_"+getQYTTime()+" where  name='"+deptname+"'");
	                    }else{
	                        dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_"+getQYTTime()+" where  name='"+deptname+"'"+sb.toString()+"");
	                    }
					}});
				dialog.getBtn_confirm().setVisible(false);
				dialog.setVisible(true);
			}
	
	
	


}