package cn.com.pushworld.wn.ui.hz.score.p01;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * zzl
 * ί�ɻ�Ƶ÷ֲ鿴
 */
public class SelectAccountingWKPanel extends AbstractWorkPanel implements ActionListener {
    BillListPanel listPanel=null;
    String date=null;
    String usercode= ClientEnvironment.getInstance().getLoginUserCode();
    BillQueryPanel billQueryPanel=null;
    @Override
    public void initialize() {
        listPanel=new BillListPanel("SAL_WPKJ_SCORE_CODE4");
        billQueryPanel=listPanel.getQuickQueryPanel();
        billQueryPanel.addBillQuickActionListener(this);
        this.add(listPanel);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==billQueryPanel){
            if(billQueryPanel.getRealValueAt("SCOREdate")==null || billQueryPanel.getRealValueAt("SCOREdate").equals(null) || billQueryPanel.getRealValueAt("SCOREdate").equals("")){
                MessageBox.show(this,"��ѡ�����ڲ�ѯ");
                return;
            }else{
                date=billQueryPanel.getRealValueAt("SCOREdate");
                listPanel.QueryDataByCondition("SCOREdate='"+date+"' and usercode='"+usercode+"' and SCORE is not null");
                if(listPanel.getBillVOs().length<=0){
                    MessageBox.show(this,"û�в�ѯ�������ڡ�"+date+"������������");
                    return;
                }
            }
        }
    }
}
