package cn.com.deloitte.dept.ui;
/**
 * 
 * @author penzhao
 *  年度结余绩效
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
		listPanel=new BillListPanel("HZ_NDJY_RESULT_ZPY_CODE1"); // 模板设置
		accountBtn=new WLTButton("计算");
		accountBtn.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] {accountBtn});
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == accountBtn) { // 监听按钮
			// 先选择一个日期 (一般选择年度)
			String[] datas = getDate(this);
			selectDate=datas[0];
			String year = selectDate.substring(0,4); // 获取到当前年
			// 判断是否需要重新计算
			try {
				String exists = UIUtil.getStringValueByDS(null, "select 1 from hz_ndjy_result where \"year\" ='"+year+"'");
				if(exists != null && "".equals(exists)) {// 
					if(!MessageBox.confirm(this, "日期【" + year + "】的年度结余已经计算，确定重复计算吗？")) {
						return;
					}
			}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			// 在请用户在输入框中输入一个值(单位:万元)
		    BillDialog dialog=	new BillDialog(this, 500, 200);
		    dialog.setTitle("请输入年度结余指标计算值(单位:万元)");
		    JPanel panel = new JPanel();
		    panel.setBounds(0, 20, 900, 500);
			panel.setLayout(null);
			JLabel label = new JLabel();
			label.setText("年度结余计算值：");
			label.setBounds(60, 20, 140, 20);
			panel.add(label);
			JTextField text=new JTextField();
			text.setBounds(180, 20, 200, 20);
			text.setEnabled(true);// 设置可编辑
			panel.add(text);
			dialog.add(panel);
			dialog.addConfirmButtonPanel();
			dialog.setVisible(true);
			
			if (dialog.intlickThisConfirmBtn() == 1) {
				// 获取到输入的值
				String jyMoneyStr = text.getText();
				if(jyMoneyStr==null|| "".equals(jyMoneyStr)) { // 判断当前内容是否为空
					MessageBox.show(this, "当前输入金额为空，请输入正确的值!!!");
					return;
				}
				Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
		         boolean matchNums = pattern.matcher(jyMoneyStr).matches();  
				if(!matchNums) {// 非数字
					MessageBox.show(this, "输入的金额中包含非数字内容，请重新输入!!!");
					return;
				}
				float jyMoney=Float.parseFloat(jyMoneyStr);
				if(jyMoney<=0) { // 小于0 
					MessageBox.show(this,"请输入大于0的年度结余金额!!!");
					return ;
				}
				
				try {
					// 执行计算方法
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					message = service.accountJyMoney(jyMoney,selectDate);
				} catch (Exception e2) {
					message="当前结算出现异常:"+e2.getMessage()+",请与管理员联系!!!";
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
					"请选择上传数据的月份", new RefItemVO(selectDate, "", selectDate),
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
