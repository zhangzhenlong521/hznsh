package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * zzl
 * grid_data_����
 * ��������һ����ǰ������
 * �������һ��һ�ű�
 */
public class DataFinishing implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run(){
        String state=null;
        try{
            List list=new ArrayList<String>();
            String data[]=dmo.getStringArrayFirstColByDS(null,"\n" +
                    "SELECT table_name FROM all_tables WHERE OWNER = 'HZDB' and table_name like'GRID_DATA%' ORDER BY table_name");
            if(data.length>32){
                for(int i=0;i<30;i++){
                    list.add("drop table "+data[i]);
                }
            }
            dmo.executeBatchByDS(null,list);
            state="drop GRID_DATA__�������ݳɹ�";
        }catch (Exception e){
            state="drop GRID_DATA__������������ǰ�";
            e.printStackTrace();
        }
        return state;
    }


}
