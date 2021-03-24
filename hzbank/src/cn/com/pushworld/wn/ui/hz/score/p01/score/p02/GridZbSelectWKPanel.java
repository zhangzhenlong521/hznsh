package cn.com.pushworld.wn.ui.hz.score.p01.score.p02;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * GridZbSelectWKPanel
 * zzl
 * 网格指标查看
 * @author Dragon
 * @date 2021/3/24
 */
public class GridZbSelectWKPanel extends AbstractWorkPanel implements BillListSelectListener {
    private BillListPanel listPanel;
    private WLTSplitPane wltSplitPane;
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String userId = ClientEnvironment.getCurrLoginUserVO()
            .getId();
    private StringBuffer sbSql=new StringBuffer();
    private HashMap<String,String> deptMap=new HashMap();
    private String datadate=null;
    private BillQueryPanel billQueryPanel=null;
    @Override
    public void initialize() {
    listPanel=new BillListPanel("SAL_PERSON_CHECK_AUTO_SCORE_CODE1");
        try {
        datadate= UIUtil.getStringValueByDS(null,"select max(datadate) from hzdb.sal_person_check_auto_score");
        listPanel.QueryData("select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname like '网格%' group by targetid,targetname");
        listPanel.addBillListSelectListener(this);
        billQueryPanel=listPanel.getQuickQueryPanel();
        billQueryPanel.setRealValueAt("datadate",datadate);
        billQueryPanel.addBillQuickActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                datadate=billQueryPanel.getRealValueAt("datadate");
                try {
                    String[][] data=UIUtil.getStringArrayByDS(null,"select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname like '网格%' group by targetid,targetname");
                    if(data==null || data.length==0){
                        MessageBox.show(listPanel,"没有此时间的数据");
                        return;
                    }
                    listPanel.QueryData("select targetid,targetname from hzdb.sal_person_check_auto_score where datadate='"+datadate+"' and targetname like '网格%' group by targetid,targetname");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        wltSplitPane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,listPanel,null);
        wltSplitPane.setDividerLocation(500);
        wltSplitPane.setDividerSize(1);
        HashVO[] vos=null;
        String leadervo=null;
        HashMap<String,String> roleMap=new HashMap<String, String>();
        deptMap=UIUtil.getHashMapBySQLByDS(null,"select userid,deptname from hzdb.v_pub_user_post_1");
        vos= UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
        roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
        leadervo=UIUtil.getStringValueByDS(null, "select stationkind from v_sal_personinfo where code='"+USERCODE+"'");
        if(ClientEnvironment.isAdmin() || roleMap.get("绩效系统管理员")!=null){
            sbSql.append("where 1=1");
        }else if(leadervo.equals("支行行长")){
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
        this.add(wltSplitPane);
}

    /**
     * 某个指标展示的值
     */
    private BillListPanel viewIndicators(BillVO vo){
        BillListPanel list=null;
        try{
            BillListPanel billListPanel=new BillListPanel("V_GRID_SELECT_CODE1");
            Pub_Templet_1_ItemVO[] itemVO=billListPanel.getTempletVO().getItemVos();
            HashMap<String,Pub_Templet_1_ItemVO> viewcolMap=new HashMap();
            for(int i=0;i<itemVO.length;i++){
                viewcolMap.put(itemVO[i].getItemkey(),itemVO[i]);
            }
            String [] keyvos =billListPanel.getTempletVO().getItemKeys();
            String [] keyNamevos=billListPanel.getTempletVO().getItemNames();
            String processfactors=UIUtil.getStringValueByDS(null,"select processfactors from hzdb.sal_person_check_auto_score where targetid='"+vo.getStringValue("TARGETID")+"' and datadate='"+datadate+"' and targetname like '网格%' and rownum<=1 ");
            String [] gcyz=processfactors.split(";");
            LinkedHashMap<String,Integer> map=new LinkedHashMap();//zzl 装下过程中得因子
            int mapint=gcyz.length;
            for(int i=0;i<gcyz.length;i++){
                String [] str=gcyz[i].toString().split("&");
                map.put(str[0],mapint);
                mapint--;
            }
//            map.put("机构名称",10);
            String [] columns=new String [keyvos.length+map.size()];
            String [] columnNames=new String[keyNamevos.length+map.size()];
            //添加 key
            for(int i=0;i<keyvos.length;i++){
                columns[i]=keyvos[i];
            }
            int xj=0;
            for(String key:map.keySet()){
                columns[columns.length-map.size()+xj]=key;
                xj++;
            }
            //添加name
            for(int i=0;i<keyNamevos.length;i++){
                columnNames[i]=keyNamevos[i];
            }
            int namexj=0;
            for(String name:map.keySet()){
                columnNames[columnNames.length-map.size()+namexj]=name;
                namexj++;
            }
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("网格业绩查看");
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
                templetItemVOs[i].setItemtype("文本框");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            list = new BillListPanel(templetVO);
            HashVO[] vos =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.V_GRID_SELECT "+ sbSql.toString()+" and targetid='"+vo.getStringValue("TARGETID")+"' and datadate='"+datadate+"' and targetname like '网格%'");
            for(int i=0;i<vos.length;i++){
                if(vos[i].getStringValue("processfactors")==null){

                }else{
                    String [] strCol=vos[i].getStringValue("processfactors").split(";");
                    for(int s=0;s<strCol.length;s++){
                        String col[]=strCol[s].split("&");
                        vos[i].setAttributeValue(col[0],(col[1].equals("null") || col[1]==null)?"0":col[1]);

                    }
//                    vos[i].setAttributeValue("机构名称",deptMap.get(vos[i].getStringValue("CHECKEDUSER")));
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
