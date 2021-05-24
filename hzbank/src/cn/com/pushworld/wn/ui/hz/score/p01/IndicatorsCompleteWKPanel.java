package cn.com.pushworld.wn.ui.hz.score.p01;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import cn.com.pushworld.wn.ui.hz.score.p01.Grid.DayAvgPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * IndicatorsCompleteWKPanel
 * zzl
 * ����ָ��鿴T+1
 * @author Dragon
 * @date 2021/1/11
 */
public class IndicatorsCompleteWKPanel extends AbstractWorkPanel implements BillListSelectListener{
    private BillListPanel listPanel;
    private WLTSplitPane wltSplitPane;
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String userId = ClientEnvironment.getCurrLoginUserVO().getId();
    private StringBuffer sbSql=new StringBuffer();
    private HashMap<String,String> deptMap=new HashMap();
    private String datadate=null;
    private BillQueryPanel billQueryPanel=null;
    private WLTButton count_btn=new WLTButton("����");
    @Override
    public void initialize() {
        listPanel=new BillListPanel("SAL_PERSON_CHECK_AUTO_SCORE_CODE1");
        try {
            datadate=UIUtil.getStringValueByDS(null,"select max(datadate) from hzdb.sal_person_check_auto_score");
            listPanel.QueryData("select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname not like '����%' group by targetid,targetname");
            listPanel.addBillListSelectListener(this);
            billQueryPanel=listPanel.getQuickQueryPanel();
            billQueryPanel.setRealValueAt("datadate",datadate);
            billQueryPanel.addBillQuickActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    datadate=billQueryPanel.getRealValueAt("datadate");
                    try {
                        String[][] data=UIUtil.getStringArrayByDS(null,"select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname not like '����%' group by targetid,targetname");
                        if(data==null || data.length==0){
                            MessageBox.show(listPanel,"û�д�ʱ�������");
                            return;
                        }
                        listPanel.QueryData("select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname not like '����%' group by targetid,targetname");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            listPanel.addBillListButton(count_btn);
            count_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    CountDate(listPanel);
                }
            });
            listPanel.repaintBillListButton();
            HashVO[] vos=null;
            String leadervo=null;
            HashMap<String,String> roleMap=new HashMap<String, String>();
                deptMap=UIUtil.getHashMapBySQLByDS(null,"select userid,deptname from hzdb.v_pub_user_post_1");
                vos= UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
                roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
                leadervo=UIUtil.getStringValueByDS(null, "select stationkind from v_sal_personinfo where code='"+USERCODE+"'");
                if(ClientEnvironment.isAdmin() || roleMap.get("��Чϵͳ����Ա")!=null){
                    sbSql.append("where 1=1");
                }else if(leadervo.equals("֧���г�")){
                    String [] userids=UIUtil.getStringArrayFirstColByDS(null,"select userid from hzdb.v_pub_user_post_1  where deptid='"+vos[0].getStringValue("deptid")+"'");
                    StringBuffer dis=new StringBuffer();
                    for(int i=0;i<userids.length;i++){
                        if(i==userids.length-1){
                            dis.append("'"+userids[i]+"'");
                        }else{
                            dis.append("'"+userids[i]+"',");
                        }
                    }
                    sbSql.append("where checkeduser in("+dis.toString()+")");
                }else{
                    sbSql.append("where checkeduser='"+userId+"'");
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wltSplitPane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,listPanel,null);
        wltSplitPane.setDividerLocation(500);
        wltSplitPane.setDividerSize(1);
        this.add(wltSplitPane);
    }

    private void CountDate(BillListPanel listPanel) {
        try{
            String [] col=UIUtil.getStringArrayFirstColByDS(null ,"select targetname from  hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname not like '����%' group by targetname");
            StringBuffer colsb=new StringBuffer();
            StringBuffer zdsb=new StringBuffer();
            LinkedHashMap<String,String> map=new LinkedHashMap();
            map.put("checkeduser","ID");
            map.put("deptname","��������");
            map.put("checkedusername","��Ա����");
            for(int i=0;i<col.length;i++){
                String colstr=null;
                if(col[i].length()>15){
                    colstr=col[i].replace("(","").
                            replace(")","").
                            replace("+","").substring(0,10);
                }else{
                    colstr=col[i].replace("(","").
                            replace(")","").
                            replace("+","");
                }
                if(i==col.length-1){
                    colsb.append(" case when "+colstr+" is null then 0 else "+colstr+" end "+colstr+" ");
                }else{
                    colsb.append(" case when "+colstr+" is null then 0 else "+colstr+" end "+colstr+",");
                }
                if(i==col.length-1){
                    zdsb.append("'"+col[i]+"' as "+colstr+"");
                }else{
                    zdsb.append("'"+col[i]+"' as "+colstr+",");
                }
                map.put(colstr,col[i]);
            }
            map.put("money","�ϼ�");
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select a.*,b.money from(SELECT checkeduser,deptname,checkedusername, "+colsb.toString()+"" +
                    "  FROM (select b.deptname,a.* from(select checkeduser,checkedusername,targetname,money from " +
                    "hzdb.sal_person_check_auto_score "+sbSql.toString()+" and datadate='"+datadate+"' and targetname not like '����%')a left join hzdb.v_pub_user_post_1 b on a.checkeduser=b.userid)\n" +
                    "  PIVOT(SUM(money)\n" +
                    "   FOR targetname IN("+zdsb.toString()+"))) a left join (select checkeduser,sum(money) money from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname not like '����%' group by checkeduser)\n" +
                    "b on a.checkeduser=b.checkeduser");
            String [] columns=new String [map.size()];
            String [] columnNames=new String[map.size()];
            int a=0;
            for(Object str:map.keySet()){
                columns[a]=str.toString();
                columnNames[a]=map.get(str);
                a++;
            }
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("����ָ�����");
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                if(columns[i].equals("checkeduser")){
                    templetItemVOs[i].setListisshowable(false);

                }else{
                    templetItemVOs[i].setListisshowable(true);
                }
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("�ı���");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            BillListPanel list = new BillListPanel(templetVO);
            list.putValue(vos);
            BillListDialog billListDialog=new BillListDialog(listPanel,"",list,1000,800,true);
            billListDialog.setBtn_confirmVisible(false);
            billListDialog.getBtn_confirm().setVisible(false);
            billListDialog.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * ĳ��ָ��չʾ��ֵ
     */
    private BillListPanel viewIndicators(BillVO vo){
        BillListPanel list=null;
        try{
            BillListPanel billListPanel=new BillListPanel("SAL_PERSON_CHECK_AUTO_SCORE_CODE2");
            Pub_Templet_1_ItemVO[] itemVO=billListPanel.getTempletVO().getItemVos();
            HashMap<String,Pub_Templet_1_ItemVO> viewcolMap=new HashMap();
            for(int i=0;i<itemVO.length;i++){
                viewcolMap.put(itemVO[i].getItemkey(),itemVO[i]);
            }
            String [] keyvos =billListPanel.getTempletVO().getItemKeys();
            String [] keyNamevos=billListPanel.getTempletVO().getItemNames();
            String processfactors=UIUtil.getStringValueByDS(null,"select processfactors from hzdb.sal_person_check_auto_score where targetid='"+vo.getStringValue("TARGETID")+"' and datadate='"+datadate+"' and targetname not like '����%' and rownum<=1 ");
            String [] gcyz=processfactors.split(";");
            LinkedHashMap<String,Integer> map=new LinkedHashMap();//zzl װ�¹����е�����
            int mapint=gcyz.length;
            for(int i=0;i<gcyz.length;i++){
                String [] str=gcyz[i].toString().split("&");
                map.put(str[0],mapint);
                mapint--;
            }
            map.put("��������",10);
            String [] columns=new String [keyvos.length+map.size()];
            String [] columnNames=new String[keyNamevos.length+map.size()];
            //��� key
            for(int i=0;i<keyvos.length;i++){
                columns[i]=keyvos[i];
            }
            int xj=0;
            for(String key:map.keySet()){
                columns[columns.length-map.size()+xj]=key;
                xj++;
            }
            //���name
            for(int i=0;i<keyNamevos.length;i++){
                columnNames[i]=keyNamevos[i];
            }
            int namexj=0;
            for(String name:map.keySet()){
                columnNames[columnNames.length-map.size()+namexj]=name;
                namexj++;
            }
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("����ҵ���鿴");
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                if(map.get(columns[i].toString())==null){
                    templetItemVOs[i].setListisshowable(viewcolMap.get(columns[i]).getListisshowable()==null?false: viewcolMap.get(columns[i]).getListisshowable());
                }else{
                    templetItemVOs[i].setListisshowable(true);
                }
                if(columns[i].equals("MONEY")){
                    templetItemVOs[i].setShoworder(columns[i].length()+1);
                }else{
                    if(map.get(columns[i].toString())==null){
                        templetItemVOs[i].setShoworder(viewcolMap.get(columns[i]).getShoworder());
                    }else{
                        templetItemVOs[i].setShoworder(columns.length-map.get(columns[i].toString()));
                    }
                }
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("�ı���");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            list = new BillListPanel(templetVO);
            HashVO[] vos =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.sal_person_check_auto_score "+ sbSql.toString()+" and targetid='"+vo.getStringValue("TARGETID")+"' and datadate='"+datadate+"' and targetname not like '����%'");
            for(int i=0;i<vos.length;i++){
                if(vos[i].getStringValue("processfactors")==null){

                }else{
                    String [] strCol=vos[i].getStringValue("processfactors").split(";");
                    for(int s=0;s<strCol.length;s++){
                        String col[]=strCol[s].split("&");
                        vos[i].setAttributeValue(col[0],(col[1].equals("null") || col[1]==null)?"0":col[1]);

                    }
                    vos[i].setAttributeValue("��������",deptMap.get(vos[i].getStringValue("CHECKEDUSER")));
                }
            }
            list.putValue(vos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onBillListSelectChanged(BillListSelectionEvent _event) {
        BillVO vo=listPanel.getSelectedBillVO();
        wltSplitPane.removeAll();
        wltSplitPane.add(listPanel);
        wltSplitPane.add(viewIndicators(vo));
        wltSplitPane.setDividerLocation(500);
        wltSplitPane.updateUI();

    }

}
