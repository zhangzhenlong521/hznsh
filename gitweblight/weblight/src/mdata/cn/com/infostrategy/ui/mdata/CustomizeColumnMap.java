package cn.com.infostrategy.ui.mdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class CustomizeColumnMap extends HashMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private TBUtil tbutil = TBUtil.getTBUtil();
	public static String HIS_TYPE_QUERYPANEL = "Q";
	public static String HIS_TYPE_CARDPANEL = "C";
	private static String path = System.getProperty("ClientCodeCache").contains("\\") ? System.getProperty("ClientCodeCache").replace("\\", "/") : System.getProperty("ClientCodeCache");//缓存路径,替换斜杠
	private static String cachePath = path.contains("WEBLIGHT_CODECACHE_DEBUG") ? path : path.substring(0, path.substring(0, path.lastIndexOf("/")).lastIndexOf("/") + 1); // 得到客户端缓存位置。

	public final static String cache_key = "CUSTOMIZE_COLUMN_KEY";
	public final static String file_str = "customizecolumn";

	//把保存的值放入记录中，以模板名为key ，BillListColBean为value，因为一个模板只可能有一条自定义显示列
	public void put(String _templetName, String _text) {
		String currDate = UIUtil.getCurrTime();
		String key = "";
		BillListColBean bean = null;
		if (!tbutil.isEmpty(_templetName) && !tbutil.isEmpty(_text.toString())) { //如果不为空
			key = _templetName.toUpperCase();//key 为模板，这里只用模板名作为唯一标示，如果在不同的菜单下用到的同一个模板都会受到影响
			if (this.containsKey(key)) {//如果有key 
				bean = (BillListColBean) this.get(key);
				bean.setLastDate(currDate);
				bean.set_text(_text);
			} else { //第一次添加
				bean = new BillListColBean(_templetName, currDate, _text);
			}
		} else { //如果为空
			key = _templetName;
			bean = new BillListColBean(_templetName, currDate, _text);
		}
		this.put(key, bean);
	}

	/*public BillListColBean get(String _templetName ) {
		BillListColBean billbean=null;
		if (!tbutil.isEmpty(_templetName)) {
			billbean= get(_templetName);
		}
		return billbean;
	}*/

	public static void putValue(String _templetName, String _text) {
		getCustomizeColMap().put(_templetName, _text);
	}

	public static CustomizeColumnMap getCustomizeColMap() {
		if (!ClientEnvironment.getInstance().containsKey(cache_key)) {
			CustomizeColumnMap hismap = null;
			File file = new File(cachePath + file_str + File.separator + ClientEnvironment.getInstance().getLoginUserID() + File.separator + "config.ini");
			if (file.exists()) {
				ObjectInputStream input;
				try {
					input = new ObjectInputStream(new FileInputStream(file));
					hismap = (CustomizeColumnMap) input.readObject();
					input.close();
				} catch (Exception e) {
					//文件被篡改后。
					hismap = new CustomizeColumnMap();
					e.printStackTrace();
				}
			} else {
				hismap = new CustomizeColumnMap();
			}
			ClientEnvironment.getInstance().put(cache_key, hismap);
		}
		return (CustomizeColumnMap) ClientEnvironment.getInstance().get(cache_key);
	}

	public static BillListColBean getCustomizeBean(String _templetName) {
		CustomizeColumnMap map = getCustomizeColMap();
		BillListColBean colbean = null;
		if (map != null && map.size()>0) {//zzl[2020-5-14] map.size>0  严格判断
			if (map.get(_templetName.toUpperCase()) == null) {//流程处理界面，点击【查看常用意见】这里为空，故需要判断一下【李春娟/2014-12-29】
				return null;
			}
			colbean = (BillListColBean) map.get(_templetName.toUpperCase());
		}
		return colbean;
	}

	public static void writeQuickSearchHisToCache() {
		if (ClientEnvironment.getInstance().containsKey(cache_key)) {
			Object obj = ClientEnvironment.getInstance().get(cache_key);
			if (obj != null) {
				File file = new File(cachePath + file_str + File.separator + ClientEnvironment.getInstance().getLoginUserID() + File.separator + "config.ini");
				try {
					if (!file.exists()) {
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						file.createNewFile();
					}
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
					out.writeObject(obj);
					out.flush();
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class BillListColBean implements Serializable {
		private static final long serialVersionUID = 1L;
		private String userid;
		private String templetName; //模板名称
		private String lastDate;// 最后更新日期
		private String value;

		public String getUserid() {
			return userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

		public String getTempletName() {
			return templetName;
		}

		public void setTempletName(String templetName) {
			this.templetName = templetName;
		}

		public String getLastDate() {
			return lastDate;
		}

		public void setLastDate(String lastDate) {
			this.lastDate = lastDate;
		}

		public String get_text() {
			return value;
		}

		public void set_text(String _text) {
			this.value = _text;
		}

		public BillListColBean(String _templetName, String _lastDate, String _text) {
			if (!tbutil.isEmpty(_templetName) && !tbutil.isEmpty(_text.toString())) {
				this.userid = ClientEnvironment.getInstance().getLoginUserID();
				templetName = _templetName; //模板名称
				lastDate = _lastDate;// 最后更新日期
				value = _text;
			}
		}
	}
}
