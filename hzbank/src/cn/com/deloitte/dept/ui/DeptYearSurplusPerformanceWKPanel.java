package cn.com.deloitte.dept.ui;
/**
 * 
 * @author penzhao
 *  ��Ƚ��༨Ч
 * [2020-12-22]
 */

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.sysapp.other.BigFileUpload;
import cn.com.infostrategy.ui.sysapp.other.RefDialog_Month;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class DeptYearSurplusPerformanceWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton accountBtn;
	private String selectDate="";
	String message ="";
	@Override
	public void initialize() {
		listPanel=new BillListPanel("HZ_NDJY_RESULT_ZPY_CODE1"); // ģ������
		accountBtn=new WLTButton("����");
		accountBtn.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] {accountBtn});
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == accountBtn) { // ������ť
			// ��ѡ��һ������ (һ��ѡ�����)
			String[] datas = getDate(this);
			selectDate=datas[0];
			String year = selectDate.substring(0,4); // ��ȡ����ǰ��
			// �ж��Ƿ���Ҫ���¼���
			try {
				String exists = UIUtil.getStringValueByDS(null, "select 1 from hz_ndjy_result where \"year\" ='"+year+"'");
				if(exists != null && "".equals(exists)) {// 
					if(!MessageBox.confirm(this, "���ڡ�" + year + "������Ƚ����Ѿ����㣬ȷ���ظ�������")) {
						return;
					}
			}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			// �����û��������������һ��ֵ(��λ:��Ԫ)
		    BillDialog dialog=	new BillDialog(this, 500, 200);
		    dialog.setTitle("��������Ƚ���ָ�����ֵ(��λ:��Ԫ)");
		    JPanel panel = new JPanel();
		    panel.setBounds(0, 20, 900, 500);
			panel.setLayout(null);
			JLabel label = new JLabel();
			label.setText("��Ƚ������ֵ��");
			label.setBounds(60, 20, 140, 20);
			panel.add(label);
			JTextField text=new JTextField();
			text.setBounds(180, 20, 200, 20);
			text.setEnabled(true);// ���ÿɱ༭
			panel.add(text);
			dialog.add(panel);
			dialog.addConfirmButtonPanel();
			dialog.setVisible(true);
			
			if (dialog.intlickThisConfirmBtn() == 1) {
				// ��ȡ�������ֵ
				String jyMoneyStr = text.getText();
				if(jyMoneyStr==null|| "".equals(jyMoneyStr)) { // �жϵ�ǰ�����Ƿ�Ϊ��
					MessageBox.show(this, "��ǰ������Ϊ�գ���������ȷ��ֵ!!!");
					return;
				}
				Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
		         boolean matchNums = pattern.matcher(jyMoneyStr).matches();  
				if(!matchNums) {// ������
					MessageBox.show(this, "����Ľ���а������������ݣ�����������!!!");
					return;
				}
				float jyMoney=Float.parseFloat(jyMoneyStr);
				if(jyMoney<=0) { // С��0 
					MessageBox.show(this,"���������0����Ƚ�����!!!");
					return ;
				}
				
				try {
					// ִ�м��㷽��
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					message = service.accountJyMoney(jyMoney,selectDate);
				} catch (Exception e2) {
					message="��ǰ��������쳣:"+e2.getMessage()+",�������Ա��ϵ!!!";
				}finally {
					MessageBox.show(this,message);
					return;
				}
			}
			
		}
		
	}

	private String[] getDate(Container _parent) {
		String[] str = null;
		int a = 0;
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent,
					"��ѡ���ϴ����ݵ��·�", new RefItemVO(selectDate, "", selectDate),
					null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			a = chooseMonth.getCloseType();
			str = new String[] { selectDate, String.valueOf(a) };
			return str;
		} catch (Exception e) {
			WLTLogger.getLogger(BigFileUpload.class).error(e);
		}
		return new String[] { "2013-08", String.valueOf(a) };
	}

}
