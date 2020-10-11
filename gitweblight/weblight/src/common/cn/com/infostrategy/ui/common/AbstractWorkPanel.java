package cn.com.infostrategy.ui.common;

import java.util.HashMap;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * 抽象工作面板根类,非常重要!!所有的工作面都从它继承!!
 * @author xch
 *
 */
public abstract class AbstractWorkPanel extends JPanel {

	private HashMap menuConfMap = new HashMap(); //一个面板的配置参数的哈希表,以后一个面板就造这个参数来决定个性化定义!!!

	private DeskTopPanel deskTopPanel = null; //桌面面板!

	private HashVO selectedMenuVOs = null; //选中的菜单树的数据VO,最后一个就是末级点!!
	private HashMap commandMap = null;

	private Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.AbstractWorkPanel.class); //

	public abstract void initialize(); //初始化抽象方法,由子类实现!!

	/**
	 * 构造方法
	 */
	public AbstractWorkPanel() {
		super(); //
	}

	/**
	 * 构造方法! 系统参数面板!!
	 */
	public AbstractWorkPanel(HashMap _menuConfMap) {
		super(); //
		addMenuConfMap(_menuConfMap); //
	}

	/**
	 * 加入菜单配置参数!!!
	 * @param _menuConfMap
	 */
	public void addMenuConfMap(HashMap _menuConfMap) {
		if (_menuConfMap != null) {
			menuConfMap.putAll(_menuConfMap); //
		}
	}

	//取得个性化配置!
	public HashMap getMenuConfMap() {
		return menuConfMap;
	}

	//从菜单参数中取得某个字符型值!
	public String getMenuConfMapValueAsStr(String _key) {
		return (String) menuConfMap.get(_key); //
	}

	//从菜单参数中取得某个字符型值，如果参数没有配置则取传入的默认值!【李春娟/2012-03-30】
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
	 * 取得菜单路径..
	 * @return
	 */
	public String getSelectedMenuPath() {
		return selectedMenuVOs.getStringValue("$TreePath"); //

	}

	/**
	 * 由菜单带入!!很重要!,它是由DeskToppanel中调用的!!
	 * @param selectedMenuVOs
	 */
	public void setSelectedMenuVOs(HashVO _selectedMenuVOs) {
		this.selectedMenuVOs = _selectedMenuVOs;
	}

	/**
	 * 取得访问菜单的名称!!
	 * @return
	 */
	public String getMenuId() {
		return selectedMenuVOs.getStringValue("id"); //
	}

	/**
	 * 取得访问菜单的名称!!
	 * @return
	 */
	public String getMenuName() {
		return selectedMenuVOs.getStringValue("name"); //
	}

	/**
	 * 取得当前工作菜单的命令类型!!!
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
	 * 取得当前菜单的命令行!!
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
	 * 根据菜单定义的Command取得其解析生成的HashMap
	 * @return
	 */
	public HashMap getCommandMap() {
		if (commandMap == null) {
			commandMap = new HashMap();
			String[] str_items = getCommand().split(";"); //分隔器!!
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
	 * 在状态样栏显示信息
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
	 * 关闭前做的事
	 */
	public void beforeDispose() {
	}

	protected void finalize() throws Throwable {
		super.finalize();
		logger.debug("JVM GC回收器成功回收了页面【" + getSelectedMenuPath() + "】的实现类【" + this.getClass().getName() + "】(" + this.hashCode() + ")的资源..");
	}

}
