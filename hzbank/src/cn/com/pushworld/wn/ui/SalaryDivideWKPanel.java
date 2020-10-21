package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class SalaryDivideWKPanel extends AbstractWorkPanel implements ActionListener {
    private WLTTabbedPane pane = null;//zzl[2020-6-16]
    private String deptId= ClientEnvironment.getInstance().getLoginUserDeptId();
    private String deptName= ClientEnvironment.getLoginUserDeptName();
    private BillQueryPanel query =new BillQueryPanel("SAL_TARGET_CHECK_LOG_PERSON");
    private WLTButton btn_commit,btn_addUser;
    private String mon=null;
    private WLTSplitPane splitpanel=null;
    private Boolean userFlag= TBUtil.getTBUtil().getSysOptionBooleanValue("二次分配是否可以添加人员",false);
    @Override
    public void initialize() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, -1);
            Date m = c.getTime();
            mon = format.format(m);
            query.addBillQuickActionListener(this);
            QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel)query.getCompentByKey("checkdate");
            String checkDate = (new SalaryUIUtil()).getCheckDate().equals("")?mon:(new SalaryUIUtil()).getCheckDate();
            dateRef.setValue(checkDate);
            onQuery(checkDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==query){
            onQuery(query.getRealValueAt("checkdate"));
        }

    }
    public void  onQuery(String dates){
        pane=new WLTTabbedPane();
        dates=dates.replace("-","");
        HashVO [] excelVos= null;
        HashVO [] vos =null;
        try {
            excelVos = UIUtil.getHashVoArrayByDS(null,"select * from EXCEL_TAB where 1=1 and code like'%dxzb%'");
            for(int i=0;i<excelVos.length;i++){
                vos=UIUtil.getHashVoArrayByDS(null,"select * from wn_secondary where dates='"+dates+"' and zbname='"+excelVos[i].getStringValue("EXCELNAME")+"'");
                if(vos.length>0){
                    String strVlaue=UIUtil.getStringValueByDS(null,"select sum(VALUE) from wn_secondary where dates='"+dates+"' and zbname='"+excelVos[i].getStringValue("EXCELNAME")+"' group by zbname");
                    pane.addTab(excelVos[i].getStringValue("EXCELNAME"),getBillListPanel(strVlaue,vos,dates,false));
                }else{
                    vos=UIUtil.getHashVoArrayByDS(null,"select * from "+excelVos[i].getStringValue("tablename")+" where YEAR||MONTH='"+dates+"' and instr(A,'"+deptName+"')>0");
                    if(vos.length>0){
                        pane.addTab(excelVos[i].getStringValue("EXCELNAME"),getBillListPanel(vos[0].getStringValue("B"),vos,dates,true));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(pane.getTabCount()>0){
            this.removeAll();
            splitpanel = new WLTSplitPane(0, query, pane);
            splitpanel.setOpaque(false);
            splitpanel.setDividerLocation(40);
            this.add(splitpanel);
            this.repaint();
        }else{
            this.removeAll();
            WLTLabel label=new WLTLabel("没有找到可以分配的指标.....");
            splitpanel = new WLTSplitPane(0, query, label);
            splitpanel.setOpaque(false);
            splitpanel.setDividerLocation(40);
            this.add(splitpanel);
            this.repaint();
        }
    }
    public BillListPanel getBillListPanel(final String value, HashVO [] vos, final String dates, Boolean flag){
        btn_commit=new WLTButton("提交");
        btn_addUser=new WLTButton("添加人员");
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        String [] columns=null;
        Pub_Templet_1_ItemVO[] templetItemVOs=null;
        try {
            if(flag){
                vos=UIUtil.getHashVoArrayByDS(null,"select DEPTNAME,POSTCODE,USERNAME,'0.0' value from v_pub_user_post_1 where 1=1  and (deptid='"+deptId+"')");

            }
            columns = new String[]{"DEPTNAME","POSTCODE","USERNAME","value"};
            String [] columnNames=new String[]{"网点名称","被考核岗位","考核人名称","分配额度"};
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            templetVO.setListrowheight("20");
            templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                templetItemVOs[i].setListisshowable(true);
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("文本框");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final BillListPanel list=new BillListPanel(templetVO);
//        list.setBillListTitleName("本指标分配额度"+value,255,0,0);
        list.setBillListTitleName("本指标分配额度"+value);
        final int width=templetItemVOs[0].getListwidth()*columns.length;
        final int height=Integer.parseInt(templetVO.getListrowheight())*(vos.length+1);
        list.putValue(vos);
        if(userFlag){
            list.addBillListButton(btn_addUser);
        }
        list.addBillListButton(btn_commit);
        list.repaintBillListButton();
        if(flag){
            list.setItemEditable("value",flag);
        }else{
            list.setItemEditable("value",flag);
        }
        btn_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final BillVO [] billVOS=list.getBillVOs();
                String str=onCheck(billVOS,value);
                if(!str.equals("OK")){
                    MessageBox.show(list,str);
                    return;
                }
                InsertSQLBuilder insert=new InsertSQLBuilder("wn_secondary");
                String text=pane.getJButtonAt(pane.getSelectedIndex()).getText();
                List list1= new ArrayList();
                try {
                    for(int i=0;i<billVOS.length;i++){
                        String id=UIUtil.getSequenceNextValByDS(null,"S_wn_secondary");
                        insert.putFieldValue("id",id);
                        insert.putFieldValue("DEPTNAME",billVOS[i].getStringValue("DEPTNAME"));
                        insert.putFieldValue("POSTCODE",billVOS[i].getStringValue("POSTCODE"));
                        insert.putFieldValue("USERNAME ",billVOS[i].getStringValue("USERNAME"));
                        insert.putFieldValue("value",billVOS[i].getStringValue("value"));
                        insert.putFieldValue("dates",dates);
                        insert.putFieldValue("zbname",text);
                        insert.putFieldValue("state","已提交");
                        list1.add(insert.getSQL());
                    }
                    UIUtil.executeBatchByDS(null, list1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setCurrCellPanelStatus(list,"已提交",width,height);
                btn_commit.setEnabled(false);
                btn_addUser.setEnabled(false);
                list.repaint();
            }
        });
        btn_addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BillListDialog billListDialog=new BillListDialog(list,"添加人员","PUB_USER_POST_DEFAULT_1",800,600);
                billListDialog.setVisible(true);
                if(billListDialog.getCloseType()==1){
                    BillVO vo=billListDialog.getBilllistPanel().getSelectedBillVO();
                    list.addRow(vo);
                    list.repaint();
                }
            }
        });
        if(flag){
            btn_commit.setEnabled(flag);
            btn_addUser.setEnabled(flag);
        }else{
            btn_commit.setEnabled(flag);
            btn_addUser.setEnabled(flag);
            setCurrCellPanelStatus(list,"已提交",width,height);
        }
        return list;
    }

    /**
     * zzl 盖章
     * @param parent
     * @param _status
     */
    private void setCurrCellPanelStatus(final JComponent parent, final String _status,final int width,final int height) {
        new Timer().schedule(new TimerTask() {
            public void run() {
                JPanel jp = new JPanel() {
                    protected void paintComponent(Graphics g) {
                        // 盖章
                        if (parent != null && parent.isShowing()) { // 搞到bufferedimage中特别不清楚。
                            super.paintComponent(g);
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.translate(15, 8);
                            g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
                            g2d.setColor(Color.red.brighter());
                            g2d.rotate(0.4);
                            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2d.setStroke(new BasicStroke(3.5f));
                            g2d.drawRect(1, 1, 150, 30);
                            g2d.setFont(new Font("黑体", Font.BOLD, 25));
                            g2d.drawString(_status, 15, 25);
                        }
                    }
                };
                jp.setOpaque(false);
                int i = 0;
                while (!parent.isShowing()) {
                    i++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i > 300) { // 30秒再出不来，直接返回。应该不会这么慢。
                        return;
                    }
                }
                Point p = parent.getLocationOnScreen();
                SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
                Rectangle rect = new Rectangle((width / 2), height / 2, 180, 130);
                DivComponentLayoutUtil.getInstanse().addComponentOnDiv(parent, jp, rect, null); // 添加浮动层
            }
        }, 100);
    }

    /**
     * 校验
     * zzl
     * @return
     */
    public String onCheck(BillVO [] billVOS,String value){
        String str=null;
        Double count=0.0;
        for(int i=0;i<billVOS.length;i++){
            count+=Double.parseDouble(billVOS[i].getStringValue("value"));
        }
        if(count==Double.parseDouble(value)){
            str="OK";
        }else if(count>Double.parseDouble(value)){
            str="实际分配的额度【"+count+"】大于需要分配的额度【"+value+"】请重新分配";
        }else{
            str="实际分配的额度【"+count+"】小于需要分配的额度【"+value+"】请重新分配";
        }
       return str;
    }
}
