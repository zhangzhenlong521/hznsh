package cn.com.pushworld.wn.ui.hz.score.p01.score.p02;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * ChargeWKPanel
 * zzl
 * 领导分管网格调整
 * @author Dragon
 * @date 2020/12/14
 */
public class ChargeWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillListPanel billListPanel=new BillListPanel("SAL_FG_ADJUST_CODE1");
    private WLTButton btn_insert,btn_update;
        @Override
        public void initialize() {
            btn_insert=new WLTButton("新增");
            btn_update=new WLTButton("修改");
            btn_insert.addActionListener(this);
            btn_update.addActionListener(this);
            billListPanel.addBatchBillListButton(new WLTButton[]{btn_insert,btn_update});
            billListPanel.repaintBillListButton();
            this.add(billListPanel);
        }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btn_insert) {
            final BillCardDialog billCardDialog=new BillCardDialog(this,"新增","SAL_FG_ADJUST_CODE1",800,400);
            billCardDialog.setSaveBtnVisiable(false);
            billCardDialog.getBtn_confirm().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String code=billCardDialog.getBillcardPanel().getRealValueAt("zhcode");
                    code=code.substring(1,code.length()-1);
                    String [] zhcode=code.split(";");
                    StringBuffer sb=new StringBuffer();
                    for(int i=0;i<zhcode.length;i++){
                        if(i==0){
                            sb.append("'"+zhcode[i]);
                        }else if(i==zhcode.length-1){
                            sb.append("','"+zhcode[i]+"'");
                        }else{
                            sb.append("','"+zhcode[i]);
                        }
                    }
                    BillVO vo=billCardDialog.getBillcardPanel().getBillVO();
                    vo.setObject("ZHNAME",sb.toString());
                    vo.setObject("ZHCOUNT",zhcode.length);

                   try {
                       save(vo);
                       MessageBox.show(billCardDialog,"保存成功");
                       billListPanel.refreshData();
                       billListPanel.repaint();
                       billCardDialog.dispose();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            billCardDialog.setVisible(true);
        }else if(actionEvent.getSource()==btn_update){
            final BillCardDialog billCardDialog=new BillCardDialog(this,"修改","SAL_FG_ADJUST_CODE1",800,400);
            billCardDialog.getBillcardPanel().setBillVO(billListPanel.getSelectedBillVO());
            billCardDialog.setSaveBtnVisiable(false);
            billCardDialog.getBtn_confirm().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String code=billCardDialog.getBillcardPanel().getRealValueAt("zhcode");
                    code=code.substring(1,code.length()-1);
                    String [] zhcode=code.split(";");
                    StringBuffer sb=new StringBuffer();
                    for(int i=0;i<zhcode.length;i++){
                        if(i==0){
                            sb.append("'"+zhcode[i]);
                        }else if(i==zhcode.length-1){
                            sb.append("','"+zhcode[i]+"'");
                        }else{
                            sb.append("','"+zhcode[i]);
                        }
                    }
                    BillVO vo=billCardDialog.getBillcardPanel().getBillVO();
                    vo.setObject("ZHNAME",sb.toString());
                    vo.setObject("ZHCOUNT",zhcode.length);
                    try {
                        update(vo);
                        MessageBox.show(billCardDialog,"修改成功");
                        billListPanel.refreshData();
                        billListPanel.repaint();
                        billCardDialog.dispose();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            billCardDialog.setVisible(true);
        }
    }
    public void save(BillVO vo){
        InsertSQLBuilder insert=new InsertSQLBuilder("SAL_FG_ADJUST");
        String key[]=vo.getKeys();
        for(int i=1;i<key.length;i++){
            insert.putFieldValue(key[i],vo.getStringValue(key[i]));
        }
        try{
            UIUtil.executeUpdateByDS(null,insert.getSQL());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void  update(BillVO vo){
        UpdateSQLBuilder insert=new UpdateSQLBuilder("SAL_FG_ADJUST");
        String key[]=vo.getKeys();
        for(int i=1;i<key.length;i++){
            if(key[i]=="ID"){
                insert.setWhereCondition("ID="+"'"+vo.getStringValue(key[i])+"'");
            }
            insert.putFieldValue(key[i],vo.getStringValue(key[i]));
        }
        try{
            UIUtil.executeUpdateByDS(null,insert.getSQL());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
