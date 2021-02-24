package cn.com.infostrategy.ui.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 * Java按钮监听总是有点麻烦!
 * 最省事的办法是 btn_1.addActionListener(new CommActionListener(this, "onMySyn")); //
 * 这样的好处是,那个类不要再继承接口ActionListener了,然后多个按钮同时监听时总是要有个分派方法!!!而且按钮必须是类变量!!!
 * 现有方法就是直接监听一个,然后再写个方法对应上就行了! 总之，最大的好处是少写许多代码!! 
 * 但这种方法也有两种缺点:1.方法必须是public,否则说找不到方法   2.重新修改方法名时,不能使用Eclipse工具联动修改,必须人员修改字符串以对应上!!
 * 总之,如果一个类中有大量按钮,为了节约代码,可以这样搞!!【xch/2012-04-27】
 * @author Administrator
 *
 */
public class CommActionListener implements ActionListener {

	private Object obj_source = null; //
	private String str_methodName = null; //

	private CommActionListener() {
	}

	/**
	 * 监听!
	 * @param _obj 对象名,必须是写监听代码的那个类,一般就是this
	 * @param _methodName
	 */
	public CommActionListener(Object _obj, String _methodName) {
		this.obj_source = _obj; //
		str_methodName = _methodName;//
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Method mt = obj_source.getClass().getMethod(str_methodName, null); //
			Object rtObj = mt.invoke(obj_source, null); //
		} catch (java.lang.NoSuchMethodException ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(null, "在类[" + obj_source.getClass().getName() + "]没有找到方法[" + str_methodName + "()]失败,可能该方法不存在,或不是public"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(null, "执行类[" + obj_source.getClass() + "中方法[" + str_methodName + "()]失败,原因:" + ex.getMessage()); //
		}
	}

}
