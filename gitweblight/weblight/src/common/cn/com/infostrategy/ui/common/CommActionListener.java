package cn.com.infostrategy.ui.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 * Java��ť���������е��鷳!
 * ��ʡ�µİ취�� btn_1.addActionListener(new CommActionListener(this, "onMySyn")); //
 * �����ĺô���,�Ǹ��಻Ҫ�ټ̳нӿ�ActionListener��,Ȼ������ťͬʱ����ʱ����Ҫ�и����ɷ���!!!���Ұ�ť�����������!!!
 * ���з�������ֱ�Ӽ���һ��,Ȼ����д��������Ӧ�Ͼ�����! ��֮�����ĺô�����д������!! 
 * �����ַ���Ҳ������ȱ��:1.����������public,����˵�Ҳ�������   2.�����޸ķ�����ʱ,����ʹ��Eclipse���������޸�,������Ա�޸��ַ����Զ�Ӧ��!!
 * ��֮,���һ�������д�����ť,Ϊ�˽�Լ����,����������!!��xch/2012-04-27��
 * @author Administrator
 *
 */
public class CommActionListener implements ActionListener {

	private Object obj_source = null; //
	private String str_methodName = null; //

	private CommActionListener() {
	}

	/**
	 * ����!
	 * @param _obj ������,������д����������Ǹ���,һ�����this
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
			JOptionPane.showMessageDialog(null, "����[" + obj_source.getClass().getName() + "]û���ҵ�����[" + str_methodName + "()]ʧ��,���ܸ÷���������,����public"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(null, "ִ����[" + obj_source.getClass() + "�з���[" + str_methodName + "()]ʧ��,ԭ��:" + ex.getMessage()); //
		}
	}

}
