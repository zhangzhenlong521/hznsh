package cn.com.pushworld.wn.bs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sun.mail.handlers.message_rfc822;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

public class YktWcrate extends AbstractWorkPanel implements BillListHtmlHrefListener{
	private CommDMO dmo = new CommDMO();
	private BillListDialog dialog=null;
	private String deptname;
    private String time=null;
	private BillQueryPanel billQueryPanel=null;
	private BillListPanel listPanel ;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_HZ_YKT_RATE_20210127_CODE1");
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.QueryData("select * from hzdb.v_hz_ykt_rate_"+getQYTTime()+"");
        billQueryPanel=listPanel.getQuickQueryPanel();
        billQueryPanel.setRealValueAt("time",getQYTTime());
        billQueryPanel.addBillQuickActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
                try {
					time=getSelTime(billQueryPanel.getRealValueAt("time"));
					try {
						String [][] column=dmo.getStringArrayByDS(null,"select table_name,table_name from user_tables where table_name='HZ_YKT_RATE_"+getQYTTime() +"'");
						if(column==null||column.length==0){
							MessageBox.show(listPanel,"没有此时间的数据");
                            return;
						}else{
							listPanel.QueryData("select * from hzbank.v_hz_ykt_rate_"+getQYTTime() +"");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (ParseException e1) {
					
					e1.printStackTrace();
				}
				// TODO Auto-generated method stub
				
			
			}
	});
        this.add(listPanel);
	}

//	private void QuickQuery() throws ParseException {
//			listPanel.queryDataByDS(null, "select * from v_hz_ykt_rate_"+getQYTTime()+"");
//		listPanel.queryDataByDS(null, "select * from v_hz_ykt_rate_20210514");
			
//	}
	public String getSelTime(String date) throws ParseException{
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
        cal.set(Calendar.DATE, day - 2);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
			final BillVO vo=listPanel.getSelectedBillVO();
	        deptname=vo.getStringValue("name");
	        dialog=new BillListDialog(listPanel,"一卡通明细查看","HZ_YKT_RATE_20210127_CODE1",2000,800);
	        dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
	        		@Override
					public void actionPerformed(ActionEvent e) {
						StringBuffer sb=new StringBuffer();
						String name=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("NAME");
	                    String A014=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A014");
	                    String A015=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A015");
	                    String A018=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A018");
	                    String A020=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A020");
	                    String A021=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A021");
	                    String A003=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A003");
	                    String A004=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A004");
	                    String A034=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A034");
	                    String NEW_SF_YKT=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("NEW_SF_YKT");
	                    String H=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("H");
	                    if(name==null || name.equals("") || name.equals(null) || name.equals(" ")){
	                    }else{
	                        sb.append(" and name='"+name+"'");
	                    }
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
	                    }
	                    if(A003==null || A003.equals("") || A003.equals(null) || A003.equals(" ")){
	                    }else{
	                        sb.append(" and A003='"+A003+"'");
	                    }
	                    if(A004==null || A004.equals("") || A004.equals(null) || A004.equals(" ")){
	                    }else{
	                        sb.append(" and A004='"+A004+"'");
	                    }
	                    if(A034==null || A034.equals("") || A034.equals(null) || A034.equals(" ")){
	                    }else{
	                        sb.append(" and A034='"+A034+"'");
	                    }
	                    if(sb.toString()==null){
	                        dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_"+getQYTTime() +"");
//	                    	dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_20210514");
	                    }else{
	                        dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_"+getQYTTime() +" where  1=1 "+sb.toString()+"");
//	                    	dialog.getBilllistPanel().queryDataByDS(null,"select * from hzbank.hz_ykt_rate_20210514 where  1=1 "+sb.toString()+"");
	                    }
					}});
				dialog.getBtn_confirm().setVisible(false);
				dialog.setVisible(true);
			}
	
	
	


}