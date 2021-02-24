package cn.com.infostrategy.bs.common;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * ϵͳ������,���Ǵ���ϵͳ�������õ���
 * ��Ӧ��pub_option���е�ֵ,һ��ʼΪ��,��һ�η���ʱ�Ż���������,��BootServlet�в������!
 * @author xch
 *
 */
public class SystemOptions {

	public static SystemOptions sysOptions = null; //ʵ����
	private HashMap dataMap = null; //
	private TBUtil tbUtil = new TBUtil(); //
	private Logger logger = WLTLogger.getLogger(SystemOptions.class); //

	/**
	 * ���췽��,��һ����Ҫ�����ݿ��м���..
	 */
	private SystemOptions() {
		if (dataMap == null) {
			dataMap = new HashMap(); //
			reLoadDataFromDB(true); //��������
		}
	}

	/**
	 * ���¼��ػ���!!!
	 * @param _isJudgeTable
	 * @return
	 */
	public String[][] reLoadDataFromDB(boolean _isJudgeTable) {
		try {
			if (!_isJudgeTable || (ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_OPTION"))) { //�������������,��ΪҪ���ǰ�װ����!!,��װ������,��ջ���ʱ,vc_alltables��Ϊ�յ�,������ǲ������ݿ�ȡ!!���Լ��˸��ж�!!��xch/2012-03-06��
				dataMap.clear(); //
				HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_option", true); //
				for (int i = 0; i < hvs.length; i++) {
					//ϵͳ����������,�����õ�ֵ����"\r\n",��ʶ��"����",��ϵͳ���������ַ�����ȡʱ,Java���Զ���"\"ת��Ϊ"\\"�洢���ڴ���;ϵͳ�ٴε��øò���ʱ,�򲻻ỻ��,�������ַ���"\r\n"��ʾ,����Ҫת��һ��
					dataMap.put(hvs[i].getStringValue("parkey"), hvs[i].getStringValue("parvalue", "").replaceAll("\\\\r\\\\n", "\r\n")); //��key��value����!!
				}
			}
			String[] str_allKeys = (String[]) dataMap.keySet().toArray(new String[0]); //
			String[][] str_return = new String[str_allKeys.length][2]; //
			for (int i = 0; i < str_allKeys.length; i++) {
				str_return[i][0] = str_allKeys[i];
				str_return[i][1] = (String) dataMap.get(str_allKeys[i]);
			}
			return str_return;
		} catch (Exception ex) {
			logger.error("�����ݿ����������Ϣ�����쳣!", ex); ////
			return null; //
		}
	}

	//����keyֱ��ȡValue,�����ַ���
	public static String getStringValue(String _key) {
		return getStringValue(_key, null);
	}

	public static String getStringValue(String _key, String _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		} else {
			return str_strvalue;
		}
	}

	public static int getIntegerValue(String _key) {
		return getIntegerValue(_key, 0);
	}

	public static int getIntegerValue(String _key, Integer _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl.intValue();
		} else {
			try {
				return Integer.parseInt(str_strvalue); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
				return _nvl.intValue();
			}
		}
	}

	public static boolean getBooleanValue(String _key) {
		return getBooleanValue(_key, false); //
	}

	public static boolean getBooleanValue(String _key, Boolean _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}

		if (str_strvalue.equalsIgnoreCase("Y") || str_strvalue.equalsIgnoreCase("true") || str_strvalue.equalsIgnoreCase("��")) { //
			return true;
		} else {
			return false;
		}
	}

	//����key������value���Էֺŷָ��ĸ����еĹ�ϣ��ֵ�е�ĳ��key��ֵ,���磺���Ƿ�����л����=Y;�Ƿ�����л���¼ģʽ=N��
	public static String getHashItemStringValue(String _key, String _itemKey) {
		return getHashItemStringValue(_key, _itemKey, null);
	}

	public static String getHashItemStringValue(String _key, String _itemKey, String _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //ȡ���ַ����еĶ�Ӧ��ֵ
		if (str_itemValue == null) {
			return _nvl;
		} else {
			return str_itemValue;
		}
	}

	public static int getHashItemIntegerValue(String _key, String _itemKey) {
		return getHashItemIntegerValue(_key, _itemKey, 0);
	}

	public static int getHashItemIntegerValue(String _key, String _itemKey, Integer _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //ȡ���ַ����еĶ�Ӧ��ֵ
		if (str_itemValue == null) {
			return _nvl;
		} else {
			try {
				return Integer.parseInt(str_itemValue); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
				return _nvl;
			}
		}
	}

	public static boolean getHashItemBooleanValue(String _key, String _itemKey) {
		return getHashItemBooleanValue(_key, _itemKey, false); //
	}

	public static boolean getHashItemBooleanValue(String _key, String _itemKey, Boolean _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //ȡ���ַ����еĶ�Ӧ��ֵ
		if (str_itemValue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}
		if (str_itemValue.equalsIgnoreCase("Y") || str_itemValue.equalsIgnoreCase("true") || str_itemValue.equalsIgnoreCase("��")) { //
			return true;
		} else {
			return false;
		}
	}

	private String getItemValueByItemKey(String _value, String _itemKey) {
		String[] str_items = tbUtil.split(_value, ";"); //�Էֺŷָ�
		for (int i = 0; i < str_items.length; i++) {
			int li_pos = str_items[i].indexOf("="); //
			if (li_pos > 0) { //����е��ں�
				String str_item_key = str_items[i].substring(0, li_pos); //key��
				if (str_item_key.equals(_itemKey)) {
					return str_items[i].substring(li_pos + 1, str_items[i].length()); //valueֵ
				}
			}
		}
		return null;
	}

	/**
	 * ȡ�����е��������,�ͻ��˿�����Ҫһ����Ŀ������е�ֵ!!
	 * @return
	 */
	public static String[][] getAllOptions() {
		HashMap tmpMap = getInstance().getDataMap(); //
		String[] str_allKeys = (String[]) tmpMap.keySet().toArray(new String[0]); //
		String[][] str_return = new String[str_allKeys.length][2]; //
		for (int i = 0; i < str_allKeys.length; i++) {
			str_return[i][0] = str_allKeys[i];
			str_return[i][1] = (String) tmpMap.get(str_allKeys[i]);
		}
		return str_return;
	}

	//��û�������
	public static HashMap getDataMap() {
		return getInstance().dataMap; //
	}

	public static void setDataMap(HashMap _hashMap) {
		getInstance().setDataMapThis(_hashMap);
	}

	private void setDataMapThis(HashMap _hashMap) {
		this.dataMap = _hashMap; //
	}

	/**
	 * ȡ��ʵ��
	 * @return
	 */
	public static SystemOptions getInstance() {
		if (sysOptions == null) {
			sysOptions = new SystemOptions();
		}
		return sysOptions;
	}

	/**
	 * ��ϵͳ���в�����������,�������������ڰ�װ���ݵ�XML��,����װʱ�����ݿ��в���ֵ,������һ��Ҳ����,
	 * ��Ϊ������,��Ϊ��׼��Ʒ,��װʱϵͳ�еĲ���Ӧ�ö���Ĭ��ֵ,��Ȼ��Ĭ��ֵ,�����ݿ���һ��ʼӦ����һ�����ݶ�û��!
	 * ����һ��,�о����Ƿ��ڴ����������!
	 * @return
	 */
	public static String[][] getAllPlatformOptions() { //
		return new String[][] {
		//��������
				{ "ƽ̨_��������", "�Ƿ����ע��", "Y", "�Ƿ����ע��,���µ�½" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ����޸�����", "Y", "" }, //
				{ "ƽ̨_��������", "�Ƿ���ó�����", "Y", "" }, //	��������
				{ "ƽ̨_��������", "�Ƿ���ò˵������", "Y", "" }, //	��������
				{ "ƽ̨_��������", "�Ƿ����Ƥ�����", "Y", "" }, //	��������
				{ "ƽ̨_��������", "�Ƿ���ʾ������Ϣ", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ�֧�ֶ��ַ��", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ���ʾ������Ϣ", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ���ʾ��������", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ���ʾ��ݷ���", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ���ʾ������ͼ", "Y", "" }, //
				{ "ƽ̨_��������", "��ҳ�Ƿ����ع��ܲ˵���", "N", "" }, //
				{ "ƽ̨_��������", "��ҳ�����п�ݰ�ť����λ��", "����", "" }, //
				{ "ƽ̨_��������", "��ҳ��ӭ��ʾ����λ��", "����", "" }, //
				{ "ƽ̨_��������", "��ҳ�ұߵ�ͼƬ", "rb1.gif=1244;rb2.gif=1456", "" }, //
				{ "ƽ̨_��������", "��ҳ�������", "�Ѻ�=http://www.sohu.com;����=http://www.163.com;����=http://www.sina.com", "" }, //
				{ "ƽ̨_��������", "��ҳ�ײ�˵��", "���ǵڡ�${TotalCount}��λ������,����¼��${UserCount}����", "" }, //
				{ "ƽ̨_��������", "�Ƿ���ʾ��½��", "0", "��Щϵͳ��½�˺������֤��������ʾ����ҳ������־�У�ֻ����ʾ������Ĭ��0Ϊ��¼��+������1Ϊֻ��ʾ��Ա����" }, //
				{ "ƽ̨_��������", "BOMģ��ͼƬ�Ƿ����", "N", "" }, //
				{ "ƽ̨_��������", "applet��¼����ؼ�����ʼλ��", "700,279", "" },//���ǿͻ��˵�¼ʱ�Ĳ������ã���LoginPanel ���õ�
				{ "ƽ̨_��������", "HTML��¼����ؼ�����ʼλ��", "700,279", "" },//�����������¼ʱ�Ĳ������ã���LoginJSPUtil���õ�
				
				{ "ƽ̨_��������", "��ҳ�ҵ����й�������", "�ҵ����й���", "" },//
				{ "ƽ̨_��������", "ϵͳ�˵������", "150", "" },//
				{ "ƽ̨_��������", "��ҳ�Ƿ������ʾ������", "N", "" },//
				{ "ƽ̨_��������", "��ҳ�Ƿ����ع�����", "N", "" },//
				{ "ƽ̨_��������", "��ҳ�Ƿ����ر���ͼƬ", "N", "" },//
				{ "ƽ̨_��������", "��ʱʱ��", "", "Ĭ�ϳ�ʱ���Զ��ر�ϵͳ������ֵ��λΪ�룬600��10����" },//
				{ "ƽ̨_��������", "����ҳ�Զ�������", "", "��������Ҫʵ��WLTJobIFC�ӿڣ�һ�㲻��" },//
				{ "ƽ̨_��������", "��ҳ�Ƿ���ʾ��̬����", "N", "2��Զ�̵���һ�β�ѯ�������ݣ���Ҫ��϶����������ҳ��̬�����Զ����ࡱ" },//
				{ "ƽ̨_��������", "��ҳ��̬�����Զ�����", "", "��������Ҫʵ��RemindIfc�ӿڣ���Ҫ��϶����������ҳ�Ƿ���ʾ��̬���ѡ���һ�㲻�ã����ܻ�Ӱ������" },//
				{ "ƽ̨_��������", "BOM��ť��ʾģ��", "", "����Ԫԭģ�壬BOM��ť���ʱʹ�ã�ʵ��Bom��ť����ʾ����" },//
				{ "ƽ̨_��������", "�Ƿ�����޸ĸ�����Ϣ", "N", "��ҳ�Ƿ�����޸ĸ�����Ϣ" },//
				
				
				//������
				{ "ƽ̨_������", "����������ҳ�����Ƿ���ʾ�����̶�", "Y", "�����ύ����������������̶Ȳ���" }, //
				{ "ƽ̨_������", "����������ҳ�����Ƿ���ʾ���������", "Y", "" }, //
				{ "ƽ̨_������", "����������ҳ�����Ƿ���ʾ���水ť", "N", "���̴�����������б��水ť" }, //
				{ "ƽ̨_������", "�����������̽���ʱ�Ƿ�Ҫִ�к�̨������", "N", "" }, //
				{ "ƽ̨_������", "����������ť��������Ƿ���ʾ���հ�ť", "Y", "" }, //
				{ "ƽ̨_������", "�������˻ط������Ƿ�����޸�", "N", "" }, //
				{ "ƽ̨_������", "���̴������ύʱ��������Ƿ����", "N", "" }, //
				{ "ƽ̨_������", "���̴������˻�ʱ��������Ƿ����", "N", "" }, //
				{ "ƽ̨_������", "�������Ƿ���ʾ�����ʼ�ѡ��", "Y", "" }, //
				{ "ƽ̨_������", "����������ҳ���д�������Ƿ���ʾΪ����ύ�����", "N", "" }, //
				{ "ƽ̨_������", "�������Ƿ�����޸���ʷ����", "N", "" }, //
				{ "ƽ̨_������", "������ʷ�ύ��¼���Ƿ���ʾ�ύ����ϵ��ʽ", "N", "" }, //
				{ "ƽ̨_������", "����������ʱ�Ƿ�ѡ���������", "Y", "" }, //
				{ "ƽ̨_������", "��������ʷ��¼���Ƿ����δ������", "Y", "" }, //
				{ "ƽ̨_������", "���������Ƿ�֧���ϴ�����", "Y", "�����ύʱ�Ƿ�����ϴ�����" }, //
				{ "ƽ̨_������", "������������б����˳��", "����;������;�����˻���;����ʱ��;�������;����", "�еĿͻ�ϲ���������������ǰ��" },//
				{ "ƽ̨_������", "�������鿴���ʱ�Ƿ���ʾ���ܽ��ܰ�ť", "Y", "�����ܰ�ť����" }, //
				{ "ƽ̨_������", "�����������̽���ʱ����ʾ��", "���л�����,ϵͳ���Զ��ύ����췢����", "�����ʾ����滹���Զ�����ʵ����Ա,����ÿ���ͻ�������ʾ��Ҫ����" }, //
				{ "ƽ̨_������", "������������ĳһ��֧����ʱ����ʾ��", "���λ���ѽ���", "����ÿ���ͻ�������ʾ��Ҫ����" }, //

				//ǧ���ؼ�				
				{ "ƽ̨_Office�ؼ�", "NTKO_Version", "5,0,1,6", "�汾��" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_MakerCaption", "�������Ź�����ѯ���޹�˾", "һ�㲻�޸�" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_MakerKey", "07E148645F62214BF48F7FB0EC9BDBF327EA975D", "һ�㲻�޸�" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_ProductCaption", "������ѯ", "��Ʒ����(Ӧ��Ϊ�ͻ�����)" },//
				{ "ƽ̨_Office�ؼ�", "NTKO_ProductKey", "394B5A6B94416B967CD867F5081FBE7C5057E8DD", "��Ʒ��ֵ(�ɲ�Ʒ�������ɵ�Ψһ��)" },//
				{ "ƽ̨_Office�ؼ�", "NTKO_NeedMaker", "Y", "Y-��ȡMakerCaption��MakerKey��ֵ" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_clsid", "C9BC4DFF-4248-4a3c-8A49-63A7D317F404", "���޸�" },//
				{ "ƽ̨_Office�ؼ�", "NTKO_�б��水ť", "Y", "�б��湦��" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_�йرհ�ť", "Y", "�йرչ���" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_�д�ӡ��ť", "N", "�д�ӡ����" }, { "ǧ��Office�ĵ�", "NTKO_��ˮӡ��ť", "N", "��ˮӡ����" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_����ʾ��ע��ť", "N", "����ʾ��ע����" },//
				{ "ƽ̨_Office�ؼ�", "NTKO_��������ע��ť", "N", "��������ע����" },//
				{ "ƽ̨_Office�ؼ�", "NTKO_���޶���ť", "N", "���޶�,�����ۼ��Ĺ���" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_����ʾ�ۼ���ť", "N", "����ʾ�ۼ��Ĺ���" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_�����غۼ���ť", "N", "�����غۼ��Ĺ���" }, //
				{ "ƽ̨_Office�ؼ�", "NTKO_�н����޶���ť", "N", "�н����޶��Ĺ���" },

				//����
				{ "ƽ̨_����", "����ѡ��Ŀ�ʼ���", "1950", "" }, //
				{ "ƽ̨_����", "����ѡ�����ݷ�Χ", "100", "��������ѡ��Ŀ�ʼ��ݣ���ݷ�Χ�ֱ�Ϊ1950��100��������ΧΪ1950-2049" },//
				{ "ƽ̨_����", "�ʼ�����", "", "���磺192.168.0.10;pushworldgrc@192.168.0.10;1" }, //
				{ "ƽ̨_����", "�Զ����ʼ�������", "", "�̳�AbstractSelfDescSendMail����" }, //
				{ "ƽ̨_����", "DownloadFileFromInterceptUrl", "https://biz1.cmbchina.com/AIOWService/Member/DownloadProvider.aspx", "������صĳ�����,����ĳ���ļ���·��" }, //
				{ "ƽ̨_����", "�����Ƿ�ת����", "N", "" }, 
				{ "ƽ̨_����", "seq_prefix", "100", "ƽ̨���ɵ�id��ǰ׺" },
				{ "ƽ̨_����", "�����ĵ��Ƿ�ɱ༭", "N", "ϵͳĬ�ϵ�������ĵ�Ϊǧ���򿪣����ɱ༭" },
				
				//����
				{ "����", "office�ؼ��Ƿ���ʾ���밴ť", "N", "" }, //
		};
	}
}
