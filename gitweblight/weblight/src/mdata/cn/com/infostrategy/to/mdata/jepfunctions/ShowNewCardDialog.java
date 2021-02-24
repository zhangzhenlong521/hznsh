package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class ShowNewCardDialog extends PostfixMathCommand {
    private int callType = -1; //

    //UI传入的参数
    private BillPanel billPanel = null;
    private WLTButton wltButton = null;
    
    public ShowNewCardDialog(BillPanel _billPanel, WLTButton _btn) {
        numberOfParameters = 1;
        this.billPanel = _billPanel;
        this.wltButton = _btn; //
        callType = WLTConstants.JEPTYPE_UI; //客户端调用
    }
    
    public void run(Stack inStack) throws ParseException {
        checkStack(inStack);
        String str_templetCode = (String) inStack.pop(); 
        
        /*---以下写自己的实现--*/
        BillListPanel list = (BillListPanel)billPanel;
        int row = list.getSelectedRow();
        if(row < 0) {
            MessageBox.show("请先选择一条记录！");
            return;
        }
        if(str_templetCode==null || str_templetCode.equals("")) {
            str_templetCode = list.getTempletVO().getTempletcode() + "_v";
        }
        
        try {
            int count = Integer.valueOf(
                UIUtil.getStringValueByDS(null, "select count(*) from pub_templet_1 where templetcode = '" + str_templetCode + "'"
                ) 
            ).intValue();
            if (count <= 0) {
                MessageBox.show("模版[" + str_templetCode + "]没有定义！");
                return;
            }
        } catch (WLTRemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        BillVO billVO = list.getBillVO(row);
        
        BillCardPanel card = new BillCardPanel(str_templetCode);
        card.setBillVO(billVO);
        
        BillCardDialog dialog = new BillCardDialog(billPanel, "", card, WLTConstants.BILLDATAEDITSTATE_INIT, true);
        dialog.setVisible(true);
        
        inStack.push("ok"); 
    }
}
