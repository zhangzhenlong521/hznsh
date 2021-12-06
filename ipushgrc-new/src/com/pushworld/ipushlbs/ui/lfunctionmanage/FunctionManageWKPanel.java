package com.pushworld.ipushlbs.ui.lfunctionmanage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
/**
 * 实现功能可配 未完成。
 * @author yinliang
 *
 */
public class FunctionManageWKPanel extends AbstractWorkPanel implements ActionListener{
	private WLTButton btn_confirm;  //执行按钮
	private JCheckBox checkbox_water ;
	@Override
	public void initialize() {
		InitFunction();
		JPanel jpanel = new JPanel(); //总的panel
		jpanel.setLayout(new BorderLayout());
		
		/**
		 * 上边设置的panel
		 */
		JPanel jpanel_1 = new JPanel();
		// 是否打印时加入水印
		checkbox_water = new JCheckBox();
		checkbox_water.setText("打印是否加入水印");
		isPrintWater();
		jpanel_1.add(checkbox_water);
		
		/**
		 * 下边按钮的panel
		 */
		JPanel jpanel_2 = new JPanel();
		btn_confirm = new WLTButton("确定");
		btn_confirm.addActionListener(this);
		jpanel_2.setLayout(new FlowLayout(FlowLayout.CENTER));
		jpanel_2.add(btn_confirm);
		
		jpanel.add(jpanel_1, BorderLayout.CENTER);
		jpanel.add(jpanel_2, BorderLayout.SOUTH);
		this.add(jpanel);
	}
	private void isPrintWater() {
		String flag = FunctionManageImp.GetFuntionManageImp().getIsPrintWater();
		if(flag.equals("1")) //如果是要求加水印
			checkbox_water.setSelected(true);
		else
			checkbox_water.setSelected(false);
	}
	/**
	 *  功能开关加载，应该写在服务启动类里，先写到这里
	 */
	private void InitFunction() {
		try {
			HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select * from pub_functionmanage");
			FunctionManageImp.GetFuntionManageImp().FunctionManage(hashvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_confirm)
			onSave();
	}
	private void onSave() {
		String upsql = "";
		// 打印时是否有水印信息
		if(checkbox_water.isSelected()){
			FunctionManageImp.GetFuntionManageImp().setIsPrintWater("1");
			upsql = "update pub_functionmanage set attvalue = '1' where attkey = 'isPrintWater'" ;
		}else{
			FunctionManageImp.GetFuntionManageImp().setIsPrintWater("0");
			upsql = "update pub_functionmanage set attvalue = '0' where attkey = 'isPrintWater'" ;
		}
		/**
		 *   ...... others info
		 */
		try {
			UIUtil.executeUpdateByDS(null, upsql );
			MessageBox.show(this,"保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
