package cn.com.infostrategy.ui.common;

import java.util.HashMap;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * ������������,�ǳ���Ҫ!!���еĹ����涼�����̳�!!
 * @author xch
 *
 */
public abstract class AbstractWorkPanel extends JPanel {

	private HashMap menuConfMap = new HashMap(); //һ���������ò����Ĺ�ϣ��,�Ժ�һ������������������������Ի�����!!!

	private DeskTopPanel deskTopPanel = null; //�������!

	private HashVO selectedMenuVOs = null; //ѡ�еĲ˵���������VO,���һ������ĩ����!!
	private HashMap commandMap = null;

	private Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.AbstractWorkPanel.class); //

	public abstract void initialize(); //��ʼ�����󷽷�,������ʵ��!!

	/**
	 * ���췽��
	 */
	public AbstractWorkPanel() {
		super(); //
	}

	/**
	 * ���췽��! ϵͳ�������!!
	 */
	public AbstractWorkPanel(HashMap _menuConfMap) {
		super(); //
		addMenuConfMap(_menuConfMap); //
	}

	/**
	 * ����˵����ò���!!!
	 * @param _menuConfMap
	 */
	public void addMenuConfMap(HashMap _menuConfMap) {
		if (_menuConfMap != null) {
			menuConfMap.putAll(_menuConfMap); //
		}
	}

	//ȡ�ø��Ի�����!
	public HashMap getMenuConfMap() {
		return menuConfMap;
	}

	//�Ӳ˵�������ȡ��ĳ���ַ���ֵ!
	public String getMenuConfMapValueAsStr(String _key) {
		return (String) menuConfMap.get(_key); //
	}

	//�Ӳ˵�������ȡ��ĳ���ַ���ֵ���������û��������ȡ�����Ĭ��ֵ!�����/2012-03-30��
	public String getMenuConfMapValueAsStr(String _key, String _val) {
		String value = (String) menuConfMap.get(_key);
		if (value == null || "".equals(value)) {
			return _val;
		}
		return value; //
	}

	public HashVO getSelectedMenuVOs() {
		return selectedMenuVOs;
	}

	/**
	 * ȡ�ò˵�·��..
	 * @return
	 */
	public String getSelectedMenuPath() {
		return selectedMenuVOs.getStringValue("$TreePath"); //

	}

	/**
	 * �ɲ˵�����!!����Ҫ!,������DeskToppanel�е��õ�!!
	 * @param selectedMenuVOs
	 */
	public void setSelectedMenuVOs(HashVO _selectedMenuVOs) {
		this.selectedMenuVOs = _selectedMenuVOs;
	}

	/**
	 * ȡ�÷��ʲ˵�������!!
	 * @return
	 */
	public String getMenuId() {
		return selectedMenuVOs.getStringValue("id"); //
	}

	/**
	 * ȡ�÷��ʲ˵�������!!
	 * @return
	 */
	public String getMenuName() {
		return selectedMenuVOs.getStringValue("name"); //
	}

	/**
	 * ȡ�õ�ǰ�����˵�����������!!!
	 * @return
	 */
	public String getCommandType() {
		String str_usecmdtype = selectedMenuVOs.getStringValue("usecmdtype"); //.getUsecmdtype(); /
		if (str_usecmdtype == null || str_usecmdtype.equals("1")) { //
			return selectedMenuVOs.getStringValue("commandtype"); //
		} else if (str_usecmdtype.equals("2")) {
			return selectedMenuVOs.getStringValue("commandtype2"); //
		} else if (str_usecmdtype.equals("3")) {
			return selectedMenuVOs.getStringValue("commandtype3"); //
		} else {
			return selectedMenuVOs.getStringValue("commandtype"); //
		}
	}

	/**
	 * ȡ�õ�ǰ�˵���������!!
	 * @return
	 */
	public String getCommand() {
		String str_usecmdtype = selectedMenuVOs.getStringValue("usecmdtype"); //.getUsecmdtype(); /
		if (str_usecmdtype == null || str_usecmdtype.equals("1")) { //
			return selectedMenuVOs.getStringValue("command"); //
		} else if (str_usecmdtype.equals("2")) {
			return selectedMenuVOs.getStringValue("command2"); //
		} else if (str_usecmdtype.equals("3")) {
			return selectedMenuVOs.getStringValue("command3"); //
		} else {
			return selectedMenuVOs.getStringValue("command"); //
		}
	}

	/**
	 * ���ݲ˵������Commandȡ����������ɵ�HashMap
	 * @return
	 */
	public HashMap getCommandMap() {
		if (commandMap == null) {
			commandMap = new HashMap();
			String[] str_items = getCommand().split(";"); //�ָ���!!
			for (int i = 0; i < str_items.length; i++) {
				String item = str_items[i];
				int li_pos = item.indexOf("=");
				String prefix = item.substring(0, li_pos);
				String subfix = item.substring(li_pos + 1, item.length());
				commandMap.put(prefix.toUpperCase(), subfix); //
			}

		}
		return commandMap;
	}

	/**
	 * ��״̬������ʾ��Ϣ
	 */
	public void setInformation(String _msg) {
		DeskTopPanel.setInfomation(_msg); //
	}

	public DeskTopPanel getDeskTopPanel() {
		return deskTopPanel;
	}

	public void setDeskTopPanel(DeskTopPanel deskTopPanel) {
		this.deskTopPanel = deskTopPanel;
	}

	/**
	 * �ر�ǰ������
	 */
	public void beforeDispose() {
	}

	protected void finalize() throws Throwable {
		super.finalize();
		logger.debug("JVM GC�������ɹ�������ҳ�桾" + getSelectedMenuPath() + "����ʵ���ࡾ" + this.getClass().getName() + "��(" + this.hashCode() + ")����Դ..");
	}

}
