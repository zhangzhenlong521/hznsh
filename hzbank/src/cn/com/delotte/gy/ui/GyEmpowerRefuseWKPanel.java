package cn.com.delotte.gy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComboBox;

import com.lowagie.text.List;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/**
 * ��Ա��Ȩҵ�񱻾ܾ�
 * @author ZPY
 *
 */
public class GyEmpowerRefuseWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel panel=null;
	private  String deptName=ClientEnvironment.getInstance().getLoginUserDeptName();// ��ȡ����¼�˵Ļ�������
	private  String code=ClientEnvironment.getInstance().getLoginUserCode();//��ȡ����ǰ��¼�˹�Ա��
	private String name=ClientEnvironment.getInstance().getLoginUserName();// ��ȡ����ǰ��¼������
	private  WLTButton gy_confirm,cw_confirm;
	private JComboBox comboBox = null;// ���Ӹ�ѡ��
	private String message=null;
	
	@Override
	public void initialize() {
		panel=new BillListPanel("EXCEL_TAB_148_CODE");
		if(deptName.contains("����")){// �жϵ�ǰ��¼�˵Ļ��� ������ǲ���
			cw_confirm=new WLTButton("���");
			cw_confirm.addActionListener(this);
			panel.addBillListButton(cw_confirm);
			panel.QueryDataByCondition("cw_confirm is null  and gy_confirm ='������'");
		}else  {
			gy_confirm=new WLTButton("ȷ��");
			gy_confirm.addActionListener(this);
			panel.addBillListButton(gy_confirm);
			panel.QueryDataByCondition("(M LIKE '%"+code+"%' or N LIKE '%"+code+"%') and  GY_CONFIRM IS NULL");
		}
		panel.repaintBillListButton();
		panel.setRowNumberChecked(true);// ��������
		this.add(panel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== gy_confirm){ // ��Աȷ��
			message="�ѹر�";
			// ��Աȷ�ϣ��Ա��ܾ���ҵ�����ȷ��: ȷ�� ������,��������飬��˵��ԭ��
			final BillVO[] vos = panel.getCheckedBillVOs();
			if(vos== null || vos.length<=0){
				MessageBox.show(this,"��ѡ��һ�����߶������ݽ��д���");
				return;
			}
			final BillCardDialog dialog=new BillCardDialog(this,"��Ϣȷ��", "CONFIRM_TABLE_CODE",600,300);
			dialog.getBtn_save().setVisible(false); // ���水ť���ɼ�
			dialog.getBtn_confirm().addActionListener(new  ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //���û����ȷ��ʱ�������Ĳ���
					try {
						message=conirm(vos, dialog,"gy");
						dialog.closeMe();
					} catch (Exception e1) {
						message="����ʧ�ܣ�ʧ��ԭ��:\n"+e1.getMessage()+"\n �������Ա��ϵ";
					}finally{
						MessageBox.show(message);
					}
				}
			});
			dialog.setVisible(true);// ���ÿɼ�
			panel.refreshData();
			
		}else if(e.getSource() == cw_confirm){ // ����ȷ��
			message="�ѹر�";
			// �Թ�Ա�ύ���������ҵ���������: ͬ�⣬�ܾ���
			final BillVO[] vos = panel.getCheckedBillVOs();
			if(vos== null || vos.length<=0){
				MessageBox.show(this,"��ѡ��һ�����߶������ݽ��д���");
				return;
			}
			final BillCardDialog dialog=new BillCardDialog(this,"��Ϣȷ��", "CONFIRM_TABLE_CODE_CW",600,300);
			dialog.getBtn_save().setVisible(false); // ���水ť���ɼ�
			dialog.getBtn_confirm().addActionListener(new  ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //���û����ȷ��ʱ�������Ĳ���
					try {
						message=conirm(vos, dialog,"cw");
						dialog.closeMe();
					} catch (Exception e1) {
						message="����ʧ�ܣ�ʧ��ԭ��:\n"+e1.getMessage()+"\n �������Ա��ϵ";
					}finally{
						MessageBox.show(message);
					}
				}
			});
			dialog.setVisible(true);// ���ÿɼ�
			panel.refreshData();
		}
	}
	private String conirm(BillVO[] vos, BillCardDialog dialog,String type) throws Exception{
		
		final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
				.lookUpRemoteService(WnSalaryServiceIfc.class);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate=format.format(new Date());//��ȡ����ǰʱ��
		HashMap<String, String> map=new HashMap<String, String>();
	    String state=	dialog.getCardItemValue("state");
	    map.put(type+"_confirm",state);
	    String confirmComment =dialog.getCardItemValue("confirm_comment");//��ȡ����������
	    map.put(type+"_confirm_comment", confirmComment);
	    map.put(type+"_confirm_name", name);
	    map.put(type+"_confirm_time", curDate);
	    message=service.confirm(vos,map);
	    return message;
	}
	
}
