package cn.com.infostrategy.ui.mdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class QuickSearchHisMap extends HashMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private TBUtil tbutil = TBUtil.getTBUtil();
	public static String HIS_TYPE_QUERYPANEL = "Q";
	public static String HIS_TYPE_CARDPANEL = "C";
	public final static String cache_key = "QUICK_SEARCH_KEY";
	private final int newNum_1 = 10; //���µļ�¼��
	private final int oftenNum_1 = 10; //���ü�¼��

	//��ѡ���ֵ�����¼�С�
	public void put(String _templetName, String _item, String _type, Object _text) {
		if (!tbutil.isEmpty(_templetName) && !tbutil.isEmpty(_item) && !tbutil.isEmpty(_type) && _text != null && !tbutil.isEmpty(_text.toString())) {
			String key = _templetName.toUpperCase() + "." + _item.toUpperCase() + "." + _type;
			String currDate = UIUtil.getCurrTime();
			if (this.containsKey(key)) {
				List beanList = (List) this.get(key); //ȡ��
				boolean exist = false;
				for (int i = 0; i < beanList.size(); i++) {
					HisBean bean = (HisBean) beanList.get(i);
					if (_text.equals(bean.get_text())) {
						bean.setCount(bean.getCount() + 1);
						bean.setLastDate(currDate);
						//��ʼ����
						beanList.remove(i);//�Ƴ��ɵ�
						beanList.add(0, bean);//�¼���ķŵ���ˡ�
						exist = true;
						break;
					}
				}
				if (!exist) { //�����һ����д
					HisBean bean = new HisBean(_templetName, _item, _type, currDate, 1, _text);
					beanList.add(0, bean); //��¼��ŵ���ǰ
				}
				/*
				 * ����Ҫ�ѳ��õĽ�������
				 */
				if (beanList.size() > newNum_1) {
					HisBean bean = (HisBean) beanList.get(newNum_1); //��������������һ��ȡ����
					for (int i = newNum_1 + 1; i < beanList.size(); i++) {
						HisBean comPareBean = (HisBean) beanList.get(i); //�õ��ȽϵĶ���
						if (bean.getCount() >= comPareBean.getCount()) {
							beanList.add(i, bean); //���뵽��λ��
							beanList.remove(newNum_1); //���߾ɵ�
							break;
						}
					}
					while (beanList.size() > (newNum_1 + oftenNum_1)) {
						beanList.remove(beanList.size() - 1);
					}
				}
				this.put(key, beanList);
			} else {
				List beanlist = new ArrayList();
				HisBean bean = new HisBean(_templetName, _item, _type, currDate, 1, _text);
				beanlist.add(bean);
				this.put(key, beanlist);
			}
		}
	}

	public void removeItem(String _templetName, String _item, String _type, Object _text) {
		List list = get(_templetName, _item, _type);
		if (list != null) {
			String key = _templetName.toUpperCase() + "." + _item.toUpperCase() + "." + _type;
			boolean find = false;
			for (int i = 0; i < list.size(); i++) {
				HisBean bean = (HisBean) list.get(i);
				if (_text.toString().equals(bean.get_text().toString())) {
					list.remove(i); //ɾ��
					find = true;
					break;
				}
			}
			if (find) {
				this.put(key, list);
			}
		}
	}

	public List get(String _templetName, String _item, String _type) {
		if (!tbutil.isEmpty(_templetName) && !tbutil.isEmpty(_item) && !tbutil.isEmpty(_type)) {
			String key = _templetName.toUpperCase() + "." + _item.toUpperCase() + "." + _type;
			return (List) this.get(key);
		}
		return null;
	}

	//ͨ�������ȡ
	private static String path = System.getProperty("ClientCodeCache").contains("\\") ? System.getProperty("ClientCodeCache").replace("\\", "/") : System.getProperty("ClientCodeCache");//����·��,�滻б��
	//��debugģʽ��¼���ͻ��˻���·�����磺"C:/Users/MrQ/WEBLIGHT_CODECACHE/192.168.0.100_9002_WorkLog/V20130422000000/",
	//���ȡΪ"C:/Users/MrQ/WEBLIGHT_CODECACHE/192.168.0.100_9002_WorkLog/",�õ��汾���·��[YangQing/2013-05-02]
	private static String cachePath = path.contains("WEBLIGHT_CODECACHE_DEBUG") ? path : path.substring(0, path.substring(0, path.lastIndexOf("/")).lastIndexOf("/") + 1); // �õ��ͻ��˻���λ�á�

	public static void putValue(String _templetName, String _item, String _type, Object _text) {
		getQuickSearchMap().put(_templetName, _item, _type, _text);
	}

	public static void removeValue(String _templetName, String _item, String _type, Object _text) {
		QuickSearchHisMap map = getQuickSearchMap();
		map.removeItem(_templetName, _item, _type, _text);
	}

	public static QuickSearchHisMap getQuickSearchMap() {
		if (!ClientEnvironment.getInstance().containsKey(cache_key)) {
			QuickSearchHisMap hismap = null;
			File file = new File(cachePath + "quicksearch" + File.separator + ClientEnvironment.getInstance().getLoginUserID() + File.separator + "config.ini");
			if (file.exists()) {
				ObjectInputStream input;
				try {
					input = new ObjectInputStream(new FileInputStream(file));
					hismap = (QuickSearchHisMap) input.readObject();
					input.close();
				} catch (Exception e) {
					//�ļ����۸ĺ�
					hismap = new QuickSearchHisMap();
					e.printStackTrace();
				}
			} else {
				hismap = new QuickSearchHisMap();
			}
			ClientEnvironment.getInstance().put(cache_key, hismap);
		}
		return (QuickSearchHisMap) ClientEnvironment.getInstance().get(cache_key);
	}

	public static List getQuickSearchHisValues(String _templetName, String _item, String _type) {
		QuickSearchHisMap map = getQuickSearchMap();
		if (map != null) {
			return map.get(_templetName, _item, _type);
		}
		return null;
	}

	public static void writeQuickSearchHisToCache() {
		if (ClientEnvironment.getInstance().containsKey(cache_key)) {
			Object obj = ClientEnvironment.getInstance().get(cache_key);
			if (obj != null) {
				File file = new File(cachePath + "quicksearch" + File.separator + ClientEnvironment.getInstance().getLoginUserID() + File.separator + "config.ini");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public class HisBean implements Serializable {
		private static final long serialVersionUID = 1L;
		private String userid;
		private String templetName; //ģ������
		private String item;//�ֶ�����
		private String type;// ����
		private String lastDate;// ����������
		private int count; //���ʴ���
		private Object value;

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

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLastDate() {
			return lastDate;
		}

		public void setLastDate(String lastDate) {
			this.lastDate = lastDate;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public Object get_text() {
			return value;
		}

		public void set_text(Object _text) {
			this.value = _text;
		}

		public HisBean(String _templetName, String _item, String _type, String _lastDate, int _count, Object _text) {
			if (!tbutil.isEmpty(_templetName) && !tbutil.isEmpty(_item) && !tbutil.isEmpty(_type) && _text != null && !tbutil.isEmpty(_text.toString())) {
				this.userid = ClientEnvironment.getInstance().getLoginUserID();
				templetName = _templetName; //ģ������
				item = _item;//�ֶ�����
				type = _type;// ����
				lastDate = _lastDate;// ����������
				count = _count; //���ʴ���
				value = _text;
			}
		}
	}
}
