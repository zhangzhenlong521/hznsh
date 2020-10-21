package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.to.WnUtils;

/**
 * 
 * @author zzl
 * 
 *         2019-5-16-����06:03:20 ������ϸ��ѯ
 */
public class LoansParticularsWKPanel extends AbstractWorkPanel implements
		BillListHtmlHrefListener,ActionListener {
	private BillListPanel list = null;
	private WLTButton importButtoon;//��������
	private JFileChooser fileChooser;
	private String message;
	@Override
	public void initialize() {
		list = new BillListPanel("V_S_LOAN_DK_CODE1");
		list.addBillListHtmlHrefListener(this);
		/*importButtoon=new WLTButton("�������ݵ���");
		importButtoon.addActionListener(this);
		list.addBatchBillListButton(new WLTButton[]{importButtoon});
		list.repaintBillListButton();*/
		this.add(list);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		if (event.getSource() == list) {
			BillVO vo = list.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(this, "������Ϣ",
					"V_S_LOAN_HK_CODE1");
			dialog.getBilllistPanel().QueryDataByCondition(
					"xd_col1='" + vo.getStringValue("xd_col1") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==importButtoon){//��������
			 final String querySQL=list.getQuickQueryPanel().getQuerySQL();////���ɲ�ѯSQL
			 MessageBox.show(this,"��ǰ��ѯִ�е�SQL:"+querySQL);
			 fileChooser=new JFileChooser();//�ļ�ѡ�������
	         String templetName = list.getTempletVO().getTempletname();//��ȡ��ģ������
	         fileChooser.setSelectedFile(new File(templetName+".xls"));
	         int showOpenDialog = fileChooser.showOpenDialog(null);
             final String filePath;
             if(showOpenDialog==JFileChooser.APPROVE_OPTION){//ѡ����ļ�
            	 filePath=fileChooser.getSelectedFile().getAbsolutePath();
             }else {
            	 return;
             }
             new SplashWindow(this , new AbstractAction() {
     			@Override
     			public void actionPerformed(ActionEvent e) {
     				message=WnUtils.ImportExcel(list, filePath, querySQL, "�������ݵ���");
     			}
     		});
             MessageBox.show(this,message);
     	}
		}
}
