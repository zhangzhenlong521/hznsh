package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ������Ϣ�޸�
 * 
 * @author zzl
 * 
 */
public class GridDataManageWkPanel extends AbstractWorkPanel implements
		ActionListener, BillListHtmlHrefListener {

	private String code = "EXCEL_TAB_85_CODE";
	private BillListPanel listPanel = null;
	private WLTButton btn_add, btn_update, btn_delete, btn_log;// ���� �޸� ɾ��
	private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
			.getCode();
	private final String USERNAME = ClientEnvironment.getCurrSessionVO()
			.getLoginUserName();
	private BillListPanel list;
	private WLTTabbedPane tabbedPane = null; // ҳǩ zzl[2020-9-18]�������ҳǩ
	private WLTButton btn_dr=new WLTButton("����");//zzl[2020-9-18] ��ӵ��빦��
	@Override
	public void initialize() {
		listPanel = new BillListPanel(code);
		btn_add = new WLTButton("����");
		btn_add.addActionListener(this);
		btn_update = new WLTButton("�޸�");
		btn_update.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		btn_log = new WLTButton("��־�鿴");
		btn_log.addActionListener(this);
		HashVO [] vos=null;
		try{
			vos=UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
		}catch (Exception e){

		}
		if(ClientEnvironment.isAdmin()){
			listPanel.QueryDataByCondition("PARENTID='1'");//zzl[20201012]
			listPanel.addBatchBillListButton(new WLTButton[] {btn_add, btn_update});
			listPanel.setDataFilterCustCondition("PARENTID='1'");
		}else if(vos[0].getStringValue("POSTNAME").contains("�г�")){
			listPanel.QueryDataByCondition("PARENTID='1' and F='"+vos[0].getStringValue("DEPTCODE")+"'");//zzl[20201012]
			listPanel.addBatchBillListButton(new WLTButton[] {btn_add, btn_update});
			listPanel.setDataFilterCustCondition("PARENTID='1' and F='"+vos[0].getStringValue("DEPTCODE")+"'");
		}else{
			listPanel.QueryDataByCondition("PARENTID='1' and G='"+vos[0].getStringValue("USERCODE")+"'");//zzl[20201012]
//			listPanel.addBatchBillListButton(new WLTButton[] {btn_update});
			listPanel.setDataFilterCustCondition("PARENTID='1' and G='"+vos[0].getStringValue("USERCODE")+"'");
		}
		list = new BillListPanel("WN_WGINFOUPDATE_LOG_CODE");
		listPanel.repaintBillListButton();// ˢ�°�ť
		listPanel.addBillListHtmlHrefListener(this); // zzl[20201012]
		tabbedPane =new WLTTabbedPane();
		tabbedPane.addTab("�������",listPanel);
		if(ClientEnvironment.isAdmin() || vos[0].getStringValue("POSTNAME").contains("�г�") || vos[0].getStringValue("POSTNAME").contains("�ͻ�����")){
			GridDataManageDKWkPanel dk=new GridDataManageDKWkPanel();
			tabbedPane.addTab("��������",dk.getListPanel());
		}
		this.add(tabbedPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {// ������ť
		 BillCardDialog dialog=new BillCardDialog(listPanel,"����","EXCEL_TAB_85_EDIT_CODE",900,300);
		 dialog.getBillcardPanel().setEditable("PARENTID",false);
		 dialog.getBillcardPanel().setRealValueAt("PARENTID","1");
		 dialog.setSaveBtnVisiable(false);
		 dialog.setVisible(true);
		 listPanel.addRow(dialog.getBillcardPanel().getBillVO());
		} else if (e.getSource() == btn_update) {// �޸Ĳ���
			BillVO vo=listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "��ѡ��һ�����ݽ����޸�");
				return;
			}
			BillCardPanel cardPanel = new BillCardPanel(
					"EXCEL_TAB_85_EDIT_CODE");
			cardPanel.setBillVO(vo);
			BillCardDialog dialog = new BillCardDialog(listPanel, "�޸�",
					cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);// �޸�����
			dialog.setSaveBtnVisiable(false);
			dialog.setVisible(true);
			try {
				int closeType = dialog.getCloseType();
				System.out.printf("closeType=%d", closeType);
				List<String> list = new ArrayList<String>();
				if (closeType == 1) {// �����ȷ������1
					BillVO newVo = dialog.getBillVO();
					String gyName = UIUtil.getStringValueByDS(null,
							"SELECT NAME FROM WNSALARYDB.PUB_USER WHERE CODE='"
									+ newVo.getStringValue("G") + "'");
					String deptName = UIUtil.getStringValueByDS(null,
							"SELECT NAME FROM HZDB.pub_corp_dept WHERE CODE='"
									+ newVo.getStringValue("F") + "'");
					String wgNum = newVo.getStringValue("E");// �����
					UpdateSQLBuilder update = new UpdateSQLBuilder();
					update.setTableName("EXCEL_TAB_85");
					update.setWhereCondition("E='" + wgNum + "'");
					update.putFieldValue("D", newVo.getStringValue("D"));// ��������
					update.putFieldValue("F", newVo.getStringValue("F"));
					update.putFieldValue("A", deptName);
					update.putFieldValue("G", newVo.getStringValue("G"));
					update.putFieldValue("B", gyName);
					list.add(update.getSQL());
					InsertSQLBuilder insert = new InsertSQLBuilder();
					insert.setTableName("wn_wginfoupdate_log");
					insert.putFieldValue("code", USERCODE);
					insert.putFieldValue("name", USERNAME);
					insert.putFieldValue("dept_old", vo.getStringValue("F"));
					insert.putFieldValue("dept_new", newVo.getStringValue("F"));
					insert.putFieldValue("person_old", vo.getStringValue("G"));
					insert.putFieldValue("person_new",
							newVo.getStringValue("G"));
					insert.putFieldValue("wgname_old", vo.getStringValue("D"));
					insert.putFieldValue("wgname_new",
							newVo.getStringValue("D"));
					insert.putFieldValue("operate_name", "�޸�");
					insert.putFieldValue("update_time", new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()));
					list.add(insert.getSQL());
					UIUtil.executeBatchByDS(null, list);
				} else {
					return;
				}
				MessageBox.show(this, "�޸����");
				listPanel.refreshData();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			listPanel.refreshData();
		} else if (e.getSource() == btn_delete) {
			BillVO vo = listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "��ѡ��һ������ɾ��");
				return;
			}
			boolean confirm = MessageBox.confirm("ȷ��ɾ����ǰѡ��������Ϣ��?");
			if (confirm) {
				try {
					/**
					 * ������־��¼
					 */
					InsertSQLBuilder insert = new InsertSQLBuilder();
					insert.setTableName("wn_wginfoupdate_log");// ��¼������־
					insert.putFieldValue("operate_name", "ɾ��");
					insert.putFieldValue("code", USERCODE);
					insert.putFieldValue("name", USERNAME);
					insert.putFieldValue("dept_old", vo.getStringValue("A"));
					insert.putFieldValue("person_old", vo.getStringValue("B"));
					insert.putFieldValue("wgname_old", vo.getStringValue("D"));
					insert.putFieldValue("update_time", new SimpleDateFormat(
							"yyyy-MM-dd").format(new Date()));
					UIUtil.executeUpdateByDS(null, insert.getSQL());
					DeleteSQLBuilder delete = new DeleteSQLBuilder(
							"EXCEL_TAB_85");
					delete.setWhereCondition("E='" + vo.getStringValue("E")
							+ "'");
					UIUtil.executeUpdateByDS(null, delete.getSQL());
					listPanel.refreshData();
					MessageBox.show(this, "ɾ���ɹ�");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (e.getSource() == btn_log) {// ��־�鿴
			BillListDialog dialog = new BillListDialog(listPanel, "����ϵ����������鿴",
					list);
			dialog.getBtn_confirm().setVisible(false);
			dialog.getBilllistPanel().QueryDataByCondition("1=1");
			dialog.setVisible(true);
		}

	}

	/**
	 * zzl [2020-10-12]
	 * @param _event
	 */
	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		final BillVO vo=listPanel.getSelectedBillVO();
		final BillListDialog dialog=new BillListDialog(this,"������Ϣ�鿴","S_LOAN_KHXX_202001_CODE1",1200,800);
		dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"'");
		dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				StringBuffer sb=new StringBuffer();
				String A=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A");
				String G=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("G");
				if(A==null || A.equals("") || A.equals(null) || A.equals(" ")){
				}else{
					sb.append(" and A='"+A+"'");
				}
				if(G==null || G.equals("") || G.equals(null) || G.equals(" ")){
				}else{
					sb.append(" and G='"+G+"'");
				}
				if(sb.toString()==null){
					dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"'");
				}else{
					dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' "+sb.toString()+"");
				}
			}
		});
		btn_dr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				onImpData(dialog,vo);
			}
		});
		dialog.getBilllistPanel().addBillListButton(btn_dr);
		dialog.getBilllistPanel().repaintBillListButton();
		dialog.setBtn_confirmVisible(false);
		dialog.setVisible(true);
	}

	/**
	 * zzl��20201012��
	 * ���뻧������
	 */
	public void onImpData(BillListDialog dialog,final BillVO vo){
		final BillCardDialog cardDialog=new BillCardDialog(dialog,"������Ϣ�鿴","S_LOAN_KHXX_202001_CODE1",600,400);
		cardDialog.getBillcardPanel().setRealValueAt("J",vo.getStringValue("C"));
		cardDialog.getBillcardPanel().setRealValueAt("K",vo.getStringValue("D"));
		cardDialog.getBillcardPanel().setRealValueAt("deptcode",vo.getStringValue("F"));
		cardDialog.getBtn_save().setVisible(false);
		cardDialog.getBtn_confirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try{//and J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"'
					HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from S_LOAN_KHXX_202001 where G='"+
							cardDialog.getBillcardPanel().getRealValueAt("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
					if(vos.length>0){
						if(vos[0].getStringValue("J")==null && vos[0].getStringValue("K")==null){//zzl �Ѵ��ڵ���û�л�������
							UIUtil.executeUpdateByDS(null,"update S_LOAN_KHXX_202001 set J='"+vo.getStringValue("C")+"',K='"+vo.getStringValue("D")+"' where G='"+vos[0].getStringValue("G")+"'");
							MessageBox.show(cardDialog,"����ɹ����²�ѯ����");
							cardDialog.dispose();
						}else{
							//zzl �Ѵ��ڲ���������
							MessageBox.show(cardDialog,"���֤��Ϊ��"+vos[0].getStringValue("G")+"���Ŀͻ��Ѵ��ڲ�����������");
						}
					}else{
						cardDialog.getBillcardPanel().updateData();
						MessageBox.show(cardDialog,"����ɹ����²�ѯ����");
						cardDialog.dispose();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		cardDialog.setVisible(true);
	}
}
