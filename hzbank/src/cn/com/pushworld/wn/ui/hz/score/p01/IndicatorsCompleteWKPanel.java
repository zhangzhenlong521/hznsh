package cn.com.pushworld.wn.ui.hz.score.p01;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.pushworld.wn.ui.hz.score.p01.Grid.DayAvgPanel;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * IndicatorsCompleteWKPanel
 * zzl
 *
 * @author Dragon
 * @date 2021/1/11
 */
public class IndicatorsCompleteWKPanel extends AbstractWorkPanel implements BillListSelectListener {
    private BillListPanel listPanel;
    private WLTSplitPane wltSplitPane;
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String userId = ClientEnvironment.getCurrLoginUserVO()
            .getId();
    private StringBuffer sbSql=new StringBuffer();
    private HashMap<String,String> deptMap=new HashMap();
    @Override
    public void initialize() {
        listPanel=new BillListPanel("SAL_PERSON_CHECK_AUTO_SCORE_CODE1");
        listPanel.QueryData("select targetid,targetname from hzdb.sal_person_check_auto_score group by targetid,targetname");
        listPanel.addBillListSelectListener(this);
        wltSplitPane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,listPanel,null);
        wltSplitPane.setDividerLocation(500);
        wltSplitPane.setDividerSize(1);
        HashVO[] vos=null;
        HashMap<String,String> roleMap=new HashMap<String, String>();
        try{
            deptMap=UIUtil.getHashMapBySQLByDS(null,"select userid,deptname from hzdb.v_pub_user_post_1");
            vos= UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
            roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
            if(ClientEnvironment.isAdmin() || roleMap.get("��Чϵͳ����Ա")!=null){
                sbSql.append("where 1=1");
            }else if(vos[0].getStringValue("POSTNAME").contains("�г�")){
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
        }catch (Exception e){
            e.printStackTrace();
        }
        this.add(wltSplitPane);
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
            String processfactors=UIUtil.getStringValueByDS(null,"select processfactors from hzdb.sal_person_check_auto_score where targetid='"+vo.getStringValue("TARGETID")+"' and rownum<=1 ");
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
            HashVO[] vos =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.sal_person_check_auto_score "+ sbSql.toString()+" and targetid='"+vo.getStringValue("TARGETID")+"'");
            for(int i=0;i<vos.length;i++){
                String [] strCol=vos[i].getStringValue("processfactors").split(";");
                for(int s=0;s<strCol.length;s++){
                    String col[]=strCol[s].split("&");
                    vos[i].setAttributeValue(col[0],(col[1].equals("null") || col[1]==null)?"0":col[1]);

                }
                vos[i].setAttributeValue("��������",deptMap.get(vos[i].getStringValue("CHECKEDUSER")));
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
