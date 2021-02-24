package cn.com.infostrategy.bs.mdata;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import bsh.Interpreter;
import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO;
import cn.com.infostrategy.bs.sysapp.install.templetdata.TempletBuilderDMO;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOComparator;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.BillVOBuilder;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1ParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 *Ԫ���ݴ����DMO,�ؼ���!!
 * @author xch
 * 
 */
public class MetaDataDMO extends AbstractDMO {

	private Logger logger = WLTLogger.getLogger(MetaDataDMO.class);
	private TBUtil tbUtil = new TBUtil(); //
	private CommDMO commDMO = new CommDMO(); //
	private static String templet_1_col = null;
	private static String templet_1_item_col = null;
	int limitcount = tbUtil.getSysOptionIntegerValue("�б�����ѯ����", 2000); //̫ƽ��Ŀ�����Ҫ�����б������ҳ��ֻ�ܲ�ѯ��2000��¼������Ҫ����һ����������������Ϊȫ�ֱ���������Ӧ�÷��������������/2017-09-26��

	public MetaDataDMO() {
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO; //
		}
		commDMO = new CommDMO(); //
		return commDMO; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

	/**
	 * ��һ����������ͼ����һ��Ԫԭģ��..
	 * �����ݱ�ṹ����ͼ�ṹ����һ��ģ��!!
	 * @param _tableName
	 * @param _templetCode
	 * @param _templetName
	 * @throws Exception
	 */
	public void importOneTempletVO(String _tableName, String _templetCode, String _templetName) throws Exception {
		ArrayList al_sql = new ArrayList();
		String str_parentPkId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1"); //
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_templet_1"); //
		isql_1.putFieldValue("pk_pub_templet_1", str_parentPkId); //
		isql_1.putFieldValue("templetcode", _templetCode); //
		isql_1.putFieldValue("templetname", _templetName); //
		isql_1.putFieldValue("templetname_e", _templetName); //
		isql_1.putFieldValue("tablename", _tableName); //��ѯ�ı���
		isql_1.putFieldValue("savedtablename", _tableName); //���浽�ı���
		isql_1.putFieldValue("pkname", "id"); //�����ֶ���
		isql_1.putFieldValue("pksequencename", "S_" + _tableName.toUpperCase()); //������Ӧ��������
		isql_1.putFieldValue("cardwidth", "777"); //��Ƭ���
		isql_1.putFieldValue("cardborder", "BORDER"); //��Ƭ�߿�
		isql_1.putFieldValue("cardlayout", "FLOWLAYOUT"); //��Ƭ���ַ�ʽ
		isql_1.putFieldValue("isshowcardborder", "N"); //��Ƭ�Ƿ���ʾ�߿�,���ڵ���
		isql_1.putFieldValue("isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ,Ĭ�ϲ���ʾ
		isql_1.putFieldValue("isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��?
		isql_1.putFieldValue("cardsaveifcheck", "N"); //��Ƭ����ǰ�Ƿ�У��?
		isql_1.putFieldValue("treepk", "id"); //��������!
		isql_1.putFieldValue("treeparentpk", "parentid");
		isql_1.putFieldValue("treeviewfield", "name"); //
		isql_1.putFieldValue("treeseqfield", "seq"); //���������ֶ�!
		isql_1.putFieldValue("treeisshowroot", "Y"); //���Ƿ���ʾ�����?
		al_sql.add(isql_1.getSQL());

		//��Ҫȡ��XML�еĻ���!!
		HashMap map_xmlRegCol = new HashMap(); //
		String[][] str_tabColDesc = new InstallDMO().getAllIntallTabColumnsDescr(_tableName); //
		if (str_tabColDesc != null && str_tabColDesc.length > 0) {
			for (int i = 0; i < str_tabColDesc.length; i++) {
				map_xmlRegCol.put(str_tabColDesc[i][0], str_tabColDesc[i][1]); //
			}
		}
		String str_sql = "select * from " + _tableName + " where 1=2"; //��һ�±�!!!
		HashVOStruct hvs = getCommDMO().getHashVoStructByDS(null, str_sql); //
		String[] str_colid = hvs.getHeaderName(); //
		String[] str_coltype = hvs.getHeaderTypeName(); //
		int[] li_colLength = hvs.getHeaderLength(); //
		for (int i = 0; i < str_colid.length; i++) { //��������
			String str_item_type = WLTConstants.COMP_TEXTFIELD; // Ĭ�����ı���
			if (str_coltype[i].toUpperCase().startsWith("DECIMAL") || str_coltype[i].toUpperCase().startsWith("NUMBER")) { //������ֿ�!
				str_item_type = WLTConstants.COMP_NUMBERFIELD;
			} else if (str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) { //������ַ���
				if (li_colLength[i] == 1) {
					str_item_type = WLTConstants.COMP_CHECKBOX; // ��ѡ��
				} else if (li_colLength[i] == 10) {
					str_item_type = WLTConstants.COMP_DATE; //
				} else if (li_colLength[i] == 19) {
					str_item_type = WLTConstants.COMP_DATETIME; //
					//�ַ��Ҵ���200�����ı��� �����/2013-03-14�� 
				} else if (li_colLength[i] > 200) {
					str_item_type = WLTConstants.COMP_TEXTAREA;
				}
			}
			String str_childPkId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1_ITEM"); //
			InsertSQLBuilder isql_2 = new InsertSQLBuilder("pub_templet_1_item"); //
			isql_2.putFieldValue("pk_pub_templet_1_item", str_childPkId); //
			isql_2.putFieldValue("pk_pub_templet_1", str_parentPkId); //
			isql_2.putFieldValue("itemkey", str_colid[i]); //
			if (map_xmlRegCol.containsKey(_tableName.toUpperCase() + "." + str_colid[i].toUpperCase())) { //���XMLע���е���!
				isql_2.putFieldValue("itemname", (String) map_xmlRegCol.get(_tableName.toUpperCase() + "." + str_colid[i].toUpperCase())); //
			} else {
				isql_2.putFieldValue("itemname", str_colid[i]); //
			}
			isql_2.putFieldValue("itemname_e", str_colid[i]); //
			isql_2.putFieldValue("itemtype", str_item_type); //��������
			isql_2.putFieldValue("issave", "Y"); //�Ƿ���뱣��
			isql_2.putFieldValue("ismustinput", "N"); //�Ƿ������
			isql_2.putFieldValue("showorder", (i + 1)); //
			isql_2.putFieldValue("listwidth", "125"); //�б���
			//isql_2.putFieldValue("cardwidth", "138"); //��Ƭ���

			//�ַ��Ҵ���200��Ƭ���400 �����/2013-03-14�� 
			if ((str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) && li_colLength[i] > 200) { //������ַ���
				isql_2.putFieldValue("cardwidth", "400"); //��Ƭ��� 
			} else {
				//�޸Ŀ�ƬĬ�Ͽ��Ϊ140 �����/2013-03-13�� 
				isql_2.putFieldValue("cardwidth", "140"); //��Ƭ��� 
			}

			isql_2.putFieldValue("listisshowable", "id".equalsIgnoreCase(str_colid[i]) ? "N" : "Y"); //�б���ʾ
			isql_2.putFieldValue("cardisshowable", "id".equalsIgnoreCase(str_colid[i]) ? "N" : "Y"); //��Ƭ����ʾ
			isql_2.putFieldValue("listiseditable", "4"); //�б�Ĭ�϶����ɱ༭
			isql_2.putFieldValue("listisexport", "Y");//�б�Ĭ�ϲ��뵼�� 
			isql_2.putFieldValue("cardiseditable", "1"); //�б�Ĭ�϶��ɱ༭
			isql_2.putFieldValue("propisshowable", "Y");
			isql_2.putFieldValue("propiseditable", "Y");

			//�ַ��Ҵ���200������һ��Ҳ���� �����/2013-03-14�� 
			if (i > 1 && (str_coltype[i - 1].toUpperCase().startsWith("CHAR") || str_coltype[i - 1].toUpperCase().startsWith("VARCHAR")) && li_colLength[i - 1] > 200) {
				isql_2.putFieldValue("iswrap", "Y"); //����
			} else {
				if ((str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) && li_colLength[i] > 200) { //������ַ���
					isql_2.putFieldValue("iswrap", "Y"); //����
				} else {
					isql_2.putFieldValue("iswrap", (i != 0 && i % 3 == 0) ? "Y" : "N"); //ÿ�����ؼ���һ��!!
				}
			}

			al_sql.add(isql_2.getSQL()); //
		}
		getCommDMO().executeBatchByDS(null, al_sql); // �ύ���ݿ�!!
	}

	/**
	 * �����ݿ��и���ģ����봴��ģ��VO
	 * 
	 * @param _code
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception {
		String str_sql = "select * from pub_templet_1 where templetcode='" + _code + "'"; //����ĳ������,Ϊ�˱�֤������Ч��,ȥ����ǰ����lower����
		HashVO[] vos = getCommDMO().getHashVoStructByDS(null, str_sql).getHashVOs(); //��Զ����Ԥ����!! ��Ϊ����SQL̫Ƶ��ʹ�õ���!
		if (vos != null && vos.length > 0) { //������ݿ������,��ֱ�������ݿ��е�!!!
			HashVO parentVO = vos[0]; //
			String str_sql_item = "select * from pub_templet_1_item where pk_pub_templet_1=" + parentVO.getStringValue("Pk_pub_templet_1") + " order by showorder asc";
			HashVO[] hashVOs_item = getCommDMO().getHashVoStructByDS(null, str_sql_item).getHashVOs(); //
			return getPub_Templet_1VO(parentVO, hashVOs_item, "DB", _code); //����Ǵ����ݿ��ȡ��
		} else { ///������ݱ���û��,���XMLȡ,��ǰ��ֱ���׳�һ���쳣,�µĻ����Ǵ�XML��ȡһ��!!!
			String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //���صİ�˳���磺WebPushƽ̨���Ϲ��Ʒ���ڿز�Ʒ����Ŀ
			String xmlfileName = null;
			ArrayList list_xmlfile = new ArrayList();
			for (int i = 0; i < str_allinstallpackage.length; i++) {
				String str_xmlfileName = str_allinstallpackage[i][0] + _code + ".xml"; //ת��ȥ��XML
				java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
				if (fileUrl != null) { //�����ȷ�������Դ!!!��ȥȡ!
					logger.debug("�����ݿ���û���ҵ�����Ϊ[" + _code + "]�ĵ���ģ��,ת��ɹ���xml�ļ�[" + str_xmlfileName + "]��ȡ��!"); //
					xmlfileName = str_xmlfileName;
					list_xmlfile.add(str_allinstallpackage[i][1]);
				}

			}
			if (xmlfileName != null) {// ����Դ���С�WebPushƽ̨���Ϲ��Ʒ���ڿز�Ʒ����Ŀ�����򷵻ص�ģ��·�������ȼ��� ��Ŀ>�ڿز�Ʒ>�Ϲ��Ʒ>WebPushƽ̨�����/2012-07-18��
				AbstractTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(xmlfileName); //��xml�й���DMO!! �Ƿ���Ҫ������?? ��XML�ļ�ȡӦ�ñȴ�DB��ȡ�����!! 
				if (list_xmlfile.size() > 1) {
					xmlfileName = list_xmlfile.remove(list_xmlfile.size() - 1) + ":" + xmlfileName + "(" + list_xmlfile.toString().substring(1, list_xmlfile.toString().length() - 1) + "��Ҳ�и�ģ��)";
				}
				Pub_Templet_1VO templetVO = getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "XML2", xmlfileName); //����,���ǵڶ���XML,�����ұ��Ҳ���,ת����XML��!!!
				templetVO.setTempletcode(_code); //����������XML�����ģ��������ļ����Ĵ�Сд��һ��,Ϊ�˱�֤һ��,����һ��!!
				return templetVO; //
			}
			throw new WLTAppException("����Ϊ[" + _code + "]��ģ�������ݿ���û��,��XMLҲû����,����ϵͳ��������ϵ!"); //���һֱû�ҵ���Դ,�����쳣!!!
		}
	}

	/**
	*����ģ�����ƴӷ��������ҵ�XMLģ��TMO
	*fromType = 0/�����ݿ��л�ȡ   1/��xml�л�ȡ 2/����ȡ���ݿ⣬���û����ȥxml�С� 3/ֱ�Ӵ�xml·��ȡ
	*/
	public DefaultTMO getDefaultTMOByCode(String _code, int fromType) throws Exception {
		return getDefaultTMOByCode(_code, fromType, false);
	}

	public DefaultTMO getDefaultTMOByCode(String _code, int fromType, boolean issim) throws Exception {
		if (0 == fromType || 2 == fromType) {
			String str_sql = "select * from pub_templet_1 where templetcode='" + _code + "'"; //����ĳ������,Ϊ�˱�֤������Ч��,ȥ����ǰ����lower����
			HashVO[] vos = getCommDMO().getHashVoStructByDS(null, str_sql).getHashVOs(); //��Զ����Ԥ����!! ��Ϊ����SQL̫Ƶ��ʹ�õ���!
			if (vos != null && vos.length > 0) { //������ݿ������,��ֱ�������ݿ��е�!!!
				HashVO parentVO = vos[0]; //
				String str_sql_item = "select * from pub_templet_1_item where pk_pub_templet_1=" + parentVO.getStringValue("Pk_pub_templet_1") + " order by showorder asc";
				HashVO[] hashVOs_item = getCommDMO().getHashVoStructByDS(null, str_sql_item).getHashVOs(); //
				return new DefaultTMO(parentVO, hashVOs_item);
			} else if (0 == fromType) {
				throw new WLTAppException("����Ϊ[" + _code + "]��ģ�������ݿ���û��,����ϵͳ��������ϵ!"); //���һֱû�ҵ���Դ,�����쳣!!!
			}
		}
		if (1 == fromType || 2 == fromType) { //�������ݿ���ʵ��û�У�
			String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //
			for (int i = str_allinstallpackage.length - 1; i >= 0; i--) {
				String str_xmlfileName = str_allinstallpackage[i][0] + _code + ".xml"; //ת��ȥ��XML
				java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
				if (fileUrl != null) { //�����ȷ�������Դ!!!��ȥȡ!
					logger.debug("�����ݿ���û���ҵ�����Ϊ[" + _code + "]�ĵ���ģ��,ת��ɹ���xml�ļ�[" + str_xmlfileName + "]��ȡ��!"); //
					DefaultTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(str_xmlfileName); //��xml�й���DMO!! �Ƿ���Ҫ������?? ��XML�ļ�ȡӦ�ñȴ�DB��ȡ�����!! 
					return tmo; //
				}
			}

		}

		//׷��3-ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
		if (3 == fromType) {
			String str_xmlfileName = _code;
			if (!str_xmlfileName.startsWith("/")) { //���ǰ��û/�����
				str_xmlfileName = "/" + str_xmlfileName;
			}

			_code = str_xmlfileName.substring(str_xmlfileName.lastIndexOf("/") + 1);
			_code = _code.substring(0, _code.lastIndexOf("."));

			java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName);
			if (fileUrl != null) { //�����ȷ�������Դ!!!��ȥȡ!
				logger.debug("�����ݿ���û���ҵ�����Ϊ[" + _code + "]�ĵ���ģ��,ת��ɹ���xml�ļ�[" + str_xmlfileName + "]��ȡ��!");
				DefaultTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(str_xmlfileName); //��xml�й���DMO!! �Ƿ���Ҫ������?? ��XML�ļ�ȡӦ�ñȴ�DB��ȡ�����!! 
				return tmo;
			}
		}

		if (1 == fromType || 3 == fromType) {
			throw new WLTAppException("����Ϊ[" + _code + "]��ģ����XMLҲû����,����ϵͳ��������ϵ!"); //���һֱû�ҵ���Դ,�����쳣!!!	
		} else {
			throw new WLTAppException("����Ϊ[" + _code + "]��ģ�������ݿ���û��,��XMLҲû����,����ϵͳ��������ϵ!"); //���һֱû�ҵ���Դ,�����쳣!!!
		}
	}

	/**
	 * ���������ڷ������˴���ģ�嶨��VO,��TMO�Ƿ��ڷ�������,����Ҫ�ڿͻ�������
	 * @param _serverTMO
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception {
		try {
			if (_serverTMO.getTmoClassName().toLowerCase().endsWith(".xml")) { //�����һ��xml�ļ�
				AbstractTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(_serverTMO.getTmoClassName()); //��xml�й���DMO!! �Ƿ���Ҫ������?? ��XML�ļ�ȡӦ�ñȴ�DB��ȡ�����!! 
				return getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "XML", _serverTMO.getTmoClassName()); //
			} else {
				String str_clsName = _serverTMO.getTmoClassName(); //����!!
				AbstractTMO tmo = null; //
				if (_serverTMO.getConstructorPars() == null) { //���û�й��췽��
					tmo = (AbstractTMO) Class.forName(str_clsName).newInstance(); //����
				} else {
					Class recCls = Class.forName(str_clsName); //����
					Constructor clsCons = recCls.getConstructor(new Class[] { String[].class }); //ȡ�ù��췽��!!������String[]����!!Class.forName("[Ljava.lang.String;")
					tmo = (AbstractTMO) clsCons.newInstance(new Object[] { _serverTMO.getConstructorPars() }); //ʹ�ù��췽������!!!
				}
				return getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "CLASS", "����������:" + _serverTMO.getTmoClassName()); //������ȡ��
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new WLTAppException("����ServerTMODefine����ģ��VOʱ�����쳣,û���ڷ����������ࡾ" + e.getMessage() + "��");
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			throw new WLTAppException("�Ƿ���TMO����" + _serverTMO.getTmoClassName() + "��,����̳���AbstractTMO");
		}

	}

	/**
	 * ֱ�Ӹ�������HashVO����ģ��VO
	 * �������ɵ�ģ��VOӦ���и���������,������ȡֵ:DB,CLASS(�ͻ���/��������),XML,Ȼ���ڽ���ģ��༭����ʱ��Ҫ�ж�һ��! �����Class���򲻿ɱ༭(�ɲ鿴)! �����xml���鸴�Ƶ�DB�н����޸�!!
	 * buildtype,buildinfo  
	 * @param _parentVO
	 * @param _childVOs
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(HashVO _parentVO, HashVO[] _childVOs, String _buildFromType, String _buildFromInfo) throws Exception {
		Pub_Templet_1VO templet1VO = new Pub_Templet_1VO(); //// ����ģ������!!
		templet1VO.setBuildFromType(_buildFromType); //�Ǵ�ʲô���ʹ������ɵ�,����DB,CLASS,XML
		templet1VO.setBuildFromInfo(_buildFromInfo); //�Ǳ��������˵��!!
		templet1VO.setPk_pub_templet_1(_parentVO.getStringValue("Pk_Pub_Templet_1")); // ����
		templet1VO.setTempletcode(_parentVO.getStringValue("Templetcode")); // ģ�����
		templet1VO.setTempletname(_parentVO.getStringValue("Templetname", "")); // ģ������
		templet1VO.setTempletname_e(_parentVO.getStringValue("Templetname_e", "")); // ģ����ʾӢ������
		templet1VO.setTablename(_parentVO.getStringValue("Tablename")); // ����
		templet1VO.setDatasourcename(_parentVO.getStringValue("Datasourcename")); // ����Դ����!!!!!!�����ӵ�
		templet1VO.setDataconstraint(_parentVO.getStringValue("Dataconstraint")); // ����Ȩ��!!!!!!�����ӵ�
		templet1VO.setDataSqlAndOrCondition(_parentVO.getStringValue("DataSqlAndOrCondition", "and"));//sunfujun/20121119/��������������Ȩ��sql��ƴ�ӷ�ʽ
		templet1VO.setAutoloadconstraint(_parentVO.getStringValue("autoloadconstraint")); //�Զ���������ʱ��Լ������!!!!
		templet1VO.setOrdercondition(_parentVO.getStringValue("ordercondition")); // ������������
		templet1VO.setAutoLoads(_parentVO.getIntegerValue("AutoLoads", 0)); //�Զ����ص�����
		templet1VO.setDatapolicy(_parentVO.getStringValue("Datapolicy")); //����Ȩ�޲���!!
		templet1VO.setDatapolicymap(_parentVO.getStringValue("DatapolicyMap")); //����Ȩ�޲��Ե�ӳ��!!
		templet1VO.setPkname(_parentVO.getStringValue("Pkname")); // ������
		templet1VO.setPksequencename(_parentVO.getStringValue("Pksequencename")); // ������Ӧ������
		templet1VO.setTostringkey(_parentVO.getStringValue("tostringkey")); //ToString���ֶ���,�Ժ������չ��֧�ֹ�ʽ�����������!!
		templet1VO.setSavedtablename(_parentVO.getStringValue("Savedtablename")); // �������ݵı���!!
		templet1VO.setBsdatafilterclass(_parentVO.getStringValue("bsdatafilterclass")); // BS�˹�������
		templet1VO.setBsdatafilterissql(_parentVO.getBooleanValue("bsdatafilterissql", false)); //�Ƿ���SQL,Ĭ�ϲ���SQL,��Ĭ����Java�����Ƿ����ʾ!!!
		templet1VO.setCardlayout(_parentVO.getStringValue("cardlayout", "FLOWLAYOUT")); //��Ƭ����!�Ժ�Ҫ���Ӷ�ҳǩʽ�Ĳ���!!!��ΪSAP�����ϵͳ������ô����,���ҵ�ȷ�в��ٿͻ�ϲ����ҳǩ�ķ��!!
		templet1VO.setCardwidth(_parentVO.getIntegerValue("cardwidth", 520)); //��Ƭ���
		templet1VO.setCardBorder(_parentVO.getStringValue("CardBorder", "BORDER")); // �ǵ���..
		templet1VO.setCardinitformula(_parentVO.getStringValue("cardinitformula")); // ��Ƭ��ʼ����ʽ....
		templet1VO.setIsshowcardborder(_parentVO.getBooleanValue("isshowcardborder", false)); // �Ƿ���ʾ��Ƭ�߿�,Ϊ���ڵ�������ҳ��ʱ����ʹ��!
		templet1VO.setIsshowcardcustbtn(_parentVO.getBooleanValue("isshowcardcustbtn", false)); // �Ƿ���ʾ��Ƭ���Զ��尴ť��!
		templet1VO.setIsshowlistpagebar(_parentVO.getBooleanValue("isshowlistpagebar", false)); // �Ƿ���ʾ�б�ķ�ҳ��
		templet1VO.setIslistpagebarwrap(_parentVO.getBooleanValue("islistpagebarwrap", false)); //�б��еĻ�ҳ�Ƿ���?
		templet1VO.setIsshowlistopebar(_parentVO.getBooleanValue("isshowlistopebar", false)); // �Ƿ���ʾ�б�Ĳ�����
		templet1VO.setIsshowlistcustbtn(_parentVO.getBooleanValue("isshowlistcustbtn", false)); // �Ƿ���ʾ�б���Զ��尴ť��!
		templet1VO.setIsshowlistquickquery(_parentVO.getBooleanValue("isshowlistquickquery", false)); // �Ƿ���ʾ�б���ٲ�ѯ!!

		templet1VO.setIsshowcommquerybtn(_parentVO.getBooleanValue("isshowcommquerybtn", false)); // ����ʾͨ�ò�ѯ!!
		templet1VO.setIscollapsequickquery(_parentVO.getBooleanValue("iscollapsequickquery", false)); //�Ƿ�������ٲ�ѯ��!!!,Ĭ����չ����
		templet1VO.setListheadheight(_parentVO.getIntegerValue("listheadheight", 27)); // �б��ͷ�߶�,Ĭ����27�����صĸ߶�!!
		templet1VO.setListrowheight(_parentVO.getStringValue("listrowheight")); // �б����и�
		templet1VO.setListheaderisgroup(_parentVO.getBooleanValue("listheaderisgroup", false)); // �б�ı�ͷ�Ƿ����
		templet1VO.setIslistautorowheight(_parentVO.getBooleanValue("islistautorowheight", false)); // �б�ı�ͷ�Ƿ����
		templet1VO.setListweidudesc(_parentVO.getStringValue("listweidudesc")); // ά�ȶ���
		templet1VO.setDefineRenderer(_parentVO.getStringValue("definerenderer")); //�Զ��������
		templet1VO.setListinitformula(_parentVO.getStringValue("listinitformula")); // �б��ʼ����ʽ....
		templet1VO.setCardcustbtndesc(_parentVO.getStringValue("cardcustbtndesc")); // ��Ƭ�Զ��尴ť˵��,�ԷֺŸ���˵��
		templet1VO.setCardsaveifcheck(_parentVO.getBooleanValue("cardsaveifcheck", false)); // ��Ƭ�����Ƿ���֤����
		templet1VO.setCardsaveselfdesccheck(_parentVO.getStringValue("cardsaveselfdesccheck")); // ��Ƭ�Զ�����֤
		templet1VO.setListcustbtndesc(_parentVO.getStringValue("listcustbtndesc")); // �б��Զ��尴ť˵��,�ԷֺŸ���˵��
		templet1VO.setListbtnorderdesc(getListBtnsOrderDesc(_parentVO.getStringValue("listbtnorderdesc")));//�б�ť����ֺŷָ�
		templet1VO.setTreecustbtndesc(_parentVO.getStringValue("treecustbtndesc")); // �����Զ��尴ť˵��,�ԷֺŸ���˵��
		templet1VO.setCardcustbtns(getRegButtons(_parentVO.getStringValue("cardcustbtndesc"))); //��Ƭ��ť
		templet1VO.setListcustbtns(getRegButtons(_parentVO.getStringValue("listcustbtndesc"))); //�б�ť
		templet1VO.setTreecustbtns(getRegButtons(_parentVO.getStringValue("treecustbtndesc"))); //���Ͱ�ť
		templet1VO.setCardcustpanel(_parentVO.getStringValue("Cardcustpanel")); // ��Ƭ�Զ������İ�ť!!
		templet1VO.setListcustpanel(_parentVO.getStringValue("Listcustpanel")); // �б���Զ������!!!		
		templet1VO.setTreepk(_parentVO.getStringValue("TREEPK")); // ������������!
		templet1VO.setTreeparentpk(_parentVO.getStringValue("TREEPARENTPK")); // �������Ķ�Ӧ�ڸ���¼�����!!
		templet1VO.setTreeviewfield(_parentVO.getStringValue("Treeviewfield")); // ���������ʾ���ֶ�
		templet1VO.setTreeseqfield(_parentVO.getStringValue("Treeseqfield")); // �������������ֶ�
		templet1VO.setTreeisshowroot(_parentVO.getBooleanValue("Treeisshowroot")); // ��������Ƿ���ʾ�����
		templet1VO.setTreeIsChecked(_parentVO.getBooleanValue("treeIsChecked", false)); //
		templet1VO.setTreeisonlyone(_parentVO.getBooleanValue("treeisonlyone", false)); //
		templet1VO.setTreeisshowtoolbar(_parentVO.getBooleanValue("treeisshowtoolbar", false)); //���Ϳؼ��Ƿ���ʾ������,Ϊ�����ʾ����ʾ
		templet1VO.setPropbeanclassname(_parentVO.getStringValue("PROPBEANCLASSNAME")); // �������Ķ�Ӧ��JavaBean������

		templet1VO.setWfcustexport(_parentVO.getStringValue("wfcustexport")); // �������������ֶ�

		String str_templeteDatasourceName = null;
		if (templet1VO.getDatasourcename() != null && !templet1VO.getDatasourcename().trim().equals("")) {
			str_templeteDatasourceName = tbUtil.convertDataSourceName(getCurrSession(), templet1VO.getDatasourcename()); // ��Ҫת��һ��!
		}

		//�ҳ�ȡ���������е���!
		TableDataStruct strct_viewtable = null; //
		if (templet1VO.getTablename() != null && !templet1VO.getTablename().trim().equals("")) {
			try {
				strct_viewtable = getCommDMO().getTableDataStructByDS(str_templeteDatasourceName, "select * from " + templet1VO.getTablename() + " where 1=2", false); //Ϊ�˼��ٿ���̨���! ����SQL����ӡ! �Ժ����Ӧ�ø㻺���!!
				if (strct_viewtable != null) {
					templet1VO.setRealViewColumns(strct_viewtable.getHeaderName()); // ��ѯ���ݵ���ͼ�е�������
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		//�ҳ�����������е���,�������������ѯ����һ��,��ֱ��ʹ�ò�ѯ�����Ϣ!�������һ�����ݿ�,�Ӷ����ܲ���!
		//�Ժ�������SQLһ��Ҫʹ�û���!! ��Ϊ��ṹ�޸ĵĿ����Լ���! ����û��Ҫÿ�ζ���һ��!!! ����Ϊ������SQLִ���ٶȼ���,����Ŀǰ��û���Ż���������!!!
		HashMap map_savedtableColDataTypes = new HashMap(); // ���浽���е�
		HashMap map_itemLength = new HashMap();
		if (templet1VO.getSavedtablename() != null && !templet1VO.getSavedtablename().trim().equals("")) {
			try {
				TableDataStruct strct_savetable = null; //
				if (templet1VO.getSavedtablename().equalsIgnoreCase(templet1VO.getTablename()) && strct_viewtable != null) { //�����������ѯ����һ��,��ֱ���ò�ѯ�����Ϣ!�������ٲ�ѯһ�����ݿ�!�Ӷ��������!!
					strct_savetable = strct_viewtable; //
				} else {
					strct_savetable = getCommDMO().getTableDataStructByDS(str_templeteDatasourceName, "select * from " + templet1VO.getSavedtablename() + " where 1=2", false); //����
				}
				if (strct_savetable != null) {
					templet1VO.setRealSavedTableHaveColumns(strct_savetable.getHeaderName()); // ����ı��е���������
					for (int i = 0; i < strct_savetable.getHeaderName().length; i++) {
						map_savedtableColDataTypes.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getHeaderTypeName()[i].toUpperCase());//
						//map_itemLength.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getHeaderLength()[i] + "");//���߼�
						map_itemLength.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getPrecision()[i] + "**" + strct_savetable.getScale()[i]);//Ԭ���� 20140127 ������Ҫ��Ӿ��ȷ����������ʱ�������֤
					}
				}
			} catch (Exception ex) {
				System.err.println("����ģ�嶨���еı����[" + templet1VO.getTablename() + "]������Ϣʱ�����쳣:" + ex.getClass().getName() + ":" + ex.getMessage()); //
			}
		}

		//����������ģ���ӱ�����
		JepFormulaParseAtBS jepParse = new JepFormulaParseAtBS(true); //��ʽ������!!
		ArrayList al_saveCols = new ArrayList();
		Pub_Templet_1_ItemVO[] itemVOS = new Pub_Templet_1_ItemVO[_childVOs.length]; //
		/***������ӣ�ģ�鿪��ģ�忪������***/

		for (int i = 0; i < itemVOS.length; i++) {
			itemVOS[i] = new Pub_Templet_1_ItemVO();
			itemVOS[i].setId(_childVOs[i].getStringValue("pk_pub_templet_1_item")); // ����!!
			itemVOS[i].setItemkey(_childVOs[i].getStringValue("Itemkey")); // ����key
			itemVOS[i].setItemname(_childVOs[i].getStringValue("Itemname", ""));
			itemVOS[i].setItemname_e(_childVOs[i].getStringValue("Itemname_e", "")); //
			itemVOS[i].setItemtype(_childVOs[i].getStringValue("Itemtype", "�ı���")); //��������
			boolean itemVisible = QuickInstallDMO.checkTempletItemVisible(templet1VO.getTempletcode(), templet1VO.getSavedtablename(), itemVOS[i].getItemkey());//ģ�鹦��������ʾ
			//			boolean itemVisible = true;
			//����ؼ�����ǰ�����ݿ��У��!!
			if (map_itemLength != null && map_itemLength.size() > 0) {
				if (map_itemLength.get(itemVOS[i].getItemkey().toUpperCase()) != null) {
					//itemVOS[i].setSaveLimit(Integer.parseInt((String) map_itemLength.get(itemVOS[i].getItemkey().toUpperCase())));//���߼�
					String templen = (String) map_itemLength.get(itemVOS[i].getItemkey().toUpperCase());//Ԭ���� 20140127 ������Ҫ��Ӿ��ȷ����������ʱ�������֤
					String[] str = getTBUtil().split(templen, "**");//��Oracle��mysql ����֤û������
					itemVOS[i].setSaveLimit(Integer.parseInt(str[0]));//������ʲô���͵Ķ���������ֵprecision��scale
					itemVOS[i].setSaveScale(Integer.parseInt(str[1]));//���scale���ڴ洢ʱ������֤
				}
			}
			itemVOS[i].setItemtiptext(_childVOs[i].getStringValue("Itemtiptext")); //��ǩ˵��!!!
			itemVOS[i].setIssave(_childVOs[i].getBooleanValue("Issave", false)); // �Ƿ���뱣������!
			itemVOS[i].setIsencrypt(_childVOs[i].getBooleanValue("isencrypt", false)); //����ʱ�Ƿ����
			itemVOS[i].setComboxdesc(_childVOs[i].getStringValue("Comboxdesc")); // ������������!!
			itemVOS[i].setRefdesc(_childVOs[i].getStringValue("Refdesc")); // ���ò��ն���!!!!!�ؼ�֮�ؼ�!!!
			itemVOS[i].setQueryItemType(_childVOs[i].getStringValue("queryitemtype")); // ��ѯ�ؼ�����,����Ϊ��
			itemVOS[i].setQueryItemDefine(_childVOs[i].getStringValue("queryitemdefine")); // ��ѯ�ؼ�����,����Ϊ��,���Ϊ��,���ñ༭ʱ��
			itemVOS[i].setHyperlinkdesc(_childVOs[i].getStringValue("Hyperlinkdesc")); // �����Ӷ���
			itemVOS[i].setIsuniquecheck(_childVOs[i].getBooleanValue("isuniquecheck", false)); // �Ƿ�У��Ψһ��!
			itemVOS[i].setIsRefCanEdit(_childVOs[i].getBooleanValue("isrefcanedit", false)); // �Ƿ��������!
			itemVOS[i].setIsmustinput2(_childVOs[i].getStringValue("Ismustinput", "N")); // �Ƿ��������!
			itemVOS[i].setIskeeptrace(_childVOs[i].getBooleanValue("Iskeeptrace", false)); // �Ƿ��������!
			itemVOS[i].setShowbgcolor(_childVOs[i].getStringValue("Showbgcolor")); // ǰ��/������ɫ
			itemVOS[i].setLoadformula(_childVOs[i].getStringValue("Loadformula"));
			itemVOS[i].setEditformula(_childVOs[i].getStringValue("Editformula"));
			itemVOS[i].setShoworder(_childVOs[i].getIntegerValue("Showorder")); // �Ƿ���ʾ�߿�..
			itemVOS[i].setListwidth(_childVOs[i].getIntegerValue("Listwidth", 85)); //�б�ģʽ�µ��ж�,Ĭ����85������!!
			itemVOS[i].setLabelwidth(new Integer(120));
			itemVOS[i].setCardwidth(new Integer(145)); //
			itemVOS[i].setCardHeight(new Integer(20)); //
			int[] li_wh_1 = getComponentWidthHeight(_childVOs[i].getStringValue("Cardwidth"), itemVOS[i].getItemtype()); //��Ƭ�еĿؼ������߶�!!
			if (li_wh_1 != null) {
				itemVOS[i].setLabelwidth(li_wh_1[0]); //
				itemVOS[i].setCardwidth(li_wh_1[1]); //
				itemVOS[i].setCardHeight(li_wh_1[2]); //
			}
			itemVOS[i].setListisshowable(itemVisible ? _childVOs[i].getBooleanValue("Listisshowable") : false); //
			itemVOS[i].setListiseditable(_childVOs[i].getStringValue("Listiseditable", "1")); // �б��Ƿ�ɱ༭
			itemVOS[i].setListishtmlhref(_childVOs[i].getBooleanValue("listishtmlhref", false)); // �б��Ƿ���Html��������ʾ..
			itemVOS[i].setListiscombine(_childVOs[i].getBooleanValue("listiscombine", false)); // �б��Ƿ�ϲ�..
			itemVOS[i].setCardisshowable(itemVisible ? _childVOs[i].getBooleanValue("Cardisshowable") : false); //
			itemVOS[i].setCardisexport(_childVOs[i].getStringValue("cardisexport")); //��Ƭ�Ƿ񵼳�
			itemVOS[i].setListisexport(_childVOs[i].getBooleanValue("listisexport", true)); //�б��Ƿ񵼳�,Ĭ�ϵ���
			itemVOS[i].setCardiseditable(_childVOs[i].getStringValue("Cardiseditable", "1")); // ��Ƭ�Ƿ�ɱ༭
			itemVOS[i].setPropisshowable(itemVisible ? _childVOs[i].getBooleanValue("Propisshowable") : false); // ���Կ����Ƿ���ʾ!!
			itemVOS[i].setPropiseditable(_childVOs[i].getBooleanValue("Propiseditable")); // ���Կ����Ƿ�ɱ༭!!
			itemVOS[i].setDefaultvalueformula(_childVOs[i].getStringValue("Defaultvalueformula")); //
			itemVOS[i].setIswrap(_childVOs[i].getBooleanValue("iswrap", false)); // �Ƿ���!!
			itemVOS[i].setGrouptitle(_childVOs[i].getStringValue("grouptitle")); // ������ʾ�ı���
			int[] li_wh_2 = getComponentWidthHeight(_childVOs[i].getStringValue("querywidth"), itemVOS[i].getQueryItemType()); //��ѯ����еĿؼ������߶�!!!
			if (li_wh_2 != null) {
				itemVOS[i].setQuerylabelwidth(li_wh_2[0]); //
				itemVOS[i].setQuerycompentwidth(li_wh_2[1]); //
				itemVOS[i].setQuerycompentheight(li_wh_2[2]); //
			}
			itemVOS[i].setQuerydefaultformula(_childVOs[i].getStringValue("querydefaultformula")); // ��ѯ��Ĭ��ֵ��ʽ
			itemVOS[i].setQuerycreatetype(_childVOs[i].getStringValue("querycreatetype")); //��ѯ����������..
			itemVOS[i].setQuerycreatecustdef(_childVOs[i].getStringValue("querycreatecustdef")); //��ѯ�������Զ���...
			itemVOS[i].setIsQueryMustInput(_childVOs[i].getStringValue("Isquerymustinput"));
			itemVOS[i].setIsQuickQueryWrap(_childVOs[i].getBooleanValue("isQuickQueryWrap", false)); // ���ٲ�ѯ�Ƿ���!!
			itemVOS[i].setIsQuickQueryShowable(_childVOs[i].getBooleanValue("isQuickQueryShowable", false)); // ���ٲ�ѯ�Ƿ���ʾ!!
			itemVOS[i].setIsQuickQueryEditable(_childVOs[i].getBooleanValue("isQuickQueryEditable", false)); // ���ٲ�ѯ�Ƿ�ɱ༭!!!
			itemVOS[i].setIsCommQueryWrap(_childVOs[i].getBooleanValue("isCommQueryWrap", false)); // ͨ�ò�ѯ�Ƿ���!!
			itemVOS[i].setIsCommQueryShowable(_childVOs[i].getBooleanValue("isCommQueryShowable", false)); // ͨ�ò�ѯ�Ƿ���ʾ!!
			itemVOS[i].setIsCommQueryEditable(_childVOs[i].getBooleanValue("isCommQueryEditable", false)); // ͨ�ò�ѯ�Ƿ�ɱ༭!!
			itemVOS[i].setWorkflowiseditable(_childVOs[i].getBooleanValue("Workflowiseditable", false)); // ���ٲ�ѯ�Ƿ�ɱ༭!!!
			itemVOS[i].setQueryeditformula(_childVOs[i].getStringValue("queryeditformula")); // ��ѯ�ı༭��ʽ
			itemVOS[i].setSavedcolumndatatype((String) map_savedtableColDataTypes.get(itemVOS[i].getItemkey().toUpperCase())); //
			if (WLTConstants.COMP_COMBOBOX.equals(itemVOS[i].getItemtype())) { //�����������,����Ҫ����������ؼ��Ĺ�������,��Ϊ����������ղ�һ��,������Ҫ�ڿͻ��˵��һ����
				itemVOS[i].setComBoxItemVos(getComBoxItemVOs(itemVOS[i].getComboxdesc(), jepParse, itemVOS[i], 1)); //
			}
			if (WLTConstants.COMP_COMBOBOX.equals(itemVOS[i].getQueryItemType())) { // �����ѯ���������������,��ҲҪ�����������������!!
				itemVOS[i].setQueryComBoxItemVos(getComBoxItemVOs(itemVOS[i].getQueryItemDefine(), jepParse, itemVOS[i], 2)); //
			}

			if (itemVOS[i].getIssave().booleanValue()) { //������뱣��,���¼����!
				al_saveCols.add(itemVOS[i].getItemkey()); //
			}

			//����CommUI�Ĺ�ʽ,����CommUCDefineVO����!!!
			//�����������ֹ�ʽͳһ��һ����׼�Ĺ�ʽ,Ȼ���ڴ���ģ��VOʱ��ֱ������,����յȿؼ������ǿ�����UI�˵��ʱ���㶨������,����Ϊ���ϴ��ļ�,�����ӱ�,�ϴ�ͼƬ�ȿؼ�,��ʵ��һ��ʼ����Ҫȡ�ÿؼ�����VO��,����Ϊ��ͳһ,��ֱ���ڷ������˾�����!
			//����������,������UI�˶�̬����ؼ��Ķ�������,����SQL���Ĺ�������,��һ���ؼ���ѡ������仯,��Ӱ����һ���ؼ���SQL��������,����취����UI�˸�һ����ʽ,����ֱ���޸Ŀؼ��е�CommUCDefineVO�е��������ֵ!! �����ͳ���ͳһ�Ľ�������ָ�������!������Ҫ��UI�˼��㶨��VO��
			if (itemVOS[i].getRefdesc() != null && !itemVOS[i].getRefdesc().trim().equals("")) {
				CommUCDefineVO uCDfVO = getCommUCDefineVOByFormula(itemVOS[i].getRefdesc().trim(), itemVOS[i].getItemtype(), jepParse); //
				if (uCDfVO.getTypeName().equals("ע�����")) { //�����ע�����,���ٲ�ѯһ�����ݿ�,��ǰ����ǰ̨����,�����������޸Ĺ���,�о�������ں�̨�����쳤��/2012-08-15��
					String str_regName = uCDfVO.getConfValue("ע������"); //
					String[][] str_regdef = commDMO.getStringArrayByDS(null, "select reftype,refdefine from pub_refregister where name='" + str_regName + "'"); //
					if (str_regdef.length > 0) {
						uCDfVO = getCommUCDefineVOByFormula(str_regdef[0][1], str_regdef[0][0], jepParse); //���½�������!
						uCDfVO.setConfValue("������˵����", "�Ȿ�Ǹ�ע�����,�Ǹ���ע����<" + str_regName + ">�ٴβ��ҵ���"); //�Ӹ��������,ʡ��������Ϊ���ˣ�
						itemVOS[i].setItemtype(str_regdef[0][0]); //���¶�������!
						itemVOS[i].setRefdesc(str_regdef[0][1]); //���¶���ע�������ָ���Ĳ��ն���!
					} else {
						uCDfVO = new CommUCDefineVO("ע�����"); //
						uCDfVO.setConfValue("�����ؼ���ʽ�����쳣", "����ע���������<" + str_regName + ">�ڱ�pub_refregister��û���ҵ�����!"); //
					}
				}
				itemVOS[i].setUCDfVO(uCDfVO); //
				if ("������".equals(uCDfVO.getTypeName()) && (itemVOS[i].getComboxdesc() == null || itemVOS[i].getComboxdesc().trim().equals(""))) { //���������������,�򴴽�����������!֮������ô�������Ժ�ȥ��������������ֶ�,ͳһ��һ���ؼ������ֶ�!!
					itemVOS[i].setComBoxItemVos(getComBoxItemVOsByCommUCDfVO(uCDfVO)); //
				}
			}

			if (itemVOS[i].getQueryItemDefine() != null && !itemVOS[i].getQueryItemDefine().trim().equals("")) { //�����ѯ���ж���,
				CommUCDefineVO uCDfVO = getCommUCDefineVOByFormula(itemVOS[i].getQueryItemDefine().trim(), itemVOS[i].getQueryItemType(), jepParse); //��ǰ�и�Bug���ǲ����õ��ǿؼ�����,�����ǲ�ѯ�ؼ�����!
				if (uCDfVO.getTypeName().equals("ע�����")) { //�����ע�����,���ٲ�ѯһ�����ݿ�
					String str_regName = uCDfVO.getConfValue("ע������"); //
					String[][] str_regdef = commDMO.getStringArrayByDS(null, "select reftype,refdefine from pub_refregister where name='" + str_regName + "'"); //
					if (str_regdef.length > 0) {
						uCDfVO = getCommUCDefineVOByFormula(str_regdef[0][1], str_regdef[0][0], jepParse); //���½�������!
						uCDfVO.setConfValue("������˵����", "�Ȿ�Ǹ�ע�����,�Ǹ���ע����<" + str_regName + ">�ٴβ��ҵ���"); //
						itemVOS[i].setQueryItemType(str_regdef[0][0]); //���¶�������!
						itemVOS[i].setQueryItemDefine(str_regdef[0][1]); //���¶���ע�������ָ���Ĳ��ն���!
					} else {
						uCDfVO = new CommUCDefineVO("ע�����"); //
						uCDfVO.setConfValue("�����ؼ���ʽ�����쳣", "����ע���������<" + str_regName + ">�ڱ�pub_refregister��û���ҵ�����!"); //
					}
				}
				itemVOS[i].setQueryUCDfVO(uCDfVO); //���ÿؼ�����VO!
				if ("������".equals(itemVOS[i].getQueryItemType())) { //���������������,�򴴽�����������!֮������ô�������Ժ�ȥ��������������ֶ�,ͳһ��һ���ؼ������ֶ�!!
					ComBoxItemVO[] ppp = getComBoxItemVOsByCommUCDfVO(uCDfVO); //
					itemVOS[i].setQueryComBoxItemVos(ppp); //
				}
			}
			itemVOS[i].setPub_Templet_1VO(templet1VO); //Ϊ�ӱ������!!
		}
		templet1VO.setItemVos(itemVOS); //Ϊ������ӱ�!!
		templet1VO.setRealSavedTableColumns((String[]) al_saveCols.toArray(new String[0])); // �����������ݿ�����!!!��insert,update,ƴSQLʱ��ֱ�Ӵ��������ת����!!
		return templet1VO; //
	}

	/**
	 * ���ݿؼ���ʽ����ȡ�ÿؼ����ö���CommUCDefineVO
	 */
	private CommUCDefineVO getCommUCDefineVOByFormula(String _refdesc, String _itemtype, JepFormulaParseAtBS _jepParse) {
		try {
			if (_itemtype.equals(WLTConstants.COMP_FILECHOOSE) && !_refdesc.startsWith("getCommUC(")) { //������ļ�ѡ����Ҳ����µ��﷨,����þɵĽ���! ��Ȼ�����ĵ��̬,��Ϊ�˼��ݾɵ��﷨,ʵ��û�취!
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_FILECHOOSE); //
				HashMap confMap = getTBUtil().convertStrToMapByExpress(_refdesc); //
				uCDfVO.setConfValueAll(confMap); //ֱ�Ӽ���!!
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_SELFDESC) && !_refdesc.startsWith("getCommUC(")) { //������Զ���ؼ�,�Ҳ����µ��﷨!
				String[] str_fs = getTBUtil().split(_refdesc, ";"); //
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_SELFDESC); //�Զ���ؼ�!!
				uCDfVO.setConfValue("��Ƭ�е���", str_fs[0]); //
				uCDfVO.setConfValue("�б���Ⱦ��", str_fs[1]); //
				uCDfVO.setConfValue("�б�༭��", str_fs[2]); //�����������������쳣!!
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_COMBOBOX) && !_refdesc.startsWith("getCommUC(")) {
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_COMBOBOX); //�Զ���ؼ�!!
				uCDfVO.setConfValue("SQL���", _refdesc); //
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_BUTTON) && !_refdesc.startsWith("getCommUC(")) { // Ϊ��֧��ԭ���߼�ֱ�������·��
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_BUTTON); //�Զ���ؼ�!!
				uCDfVO.setConfValue("����¼�", _refdesc); //
				return uCDfVO;
			} else {
				//_refdesc = tbUtil.replaceAll(_refdesc, "\r", ""); //��Ҫ�滻,��Ϊ�ҷ���JEP�������Զ���������//��ע�����,���һ�滻����������!!!
				Object obj = _jepParse.execFormula(_refdesc); //ִ�й�ʽ
				if (obj instanceof CommUCDefineVO) {
					return (CommUCDefineVO) obj; //
				} else {
					CommUCDefineVO uCDfVO = new CommUCDefineVO(_itemtype); //
					uCDfVO.setConfValue("�����ؼ���ʽʧ��", "û�гɹ�����CommUCDefineVO����,�õ��Ķ���������[" + (obj == null ? "null" : obj.getClass()) + "],����ֵ��[" + obj + "]"); //
					return uCDfVO; //
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(); //
			CommUCDefineVO uCDfVO = new CommUCDefineVO(_itemtype); //
			uCDfVO.setConfValue("�����ؼ���ʽ�����쳣", "�쳣��:" + ex.getClass().getName() + ",�쳣˵��:" + ex.getMessage()); //
			return uCDfVO; //
		}
	}

	//����������Ķ��幹������������
	private ComBoxItemVO[] getComBoxItemVOs(String _comboxdesc, JepFormulaParse _jep, Pub_Templet_1_ItemVO _pub_Templet_1_ItemVO, int _type) {
		if (_comboxdesc == null || _comboxdesc.trim().equals("")) {
			return null; //
		}
		try {
			_comboxdesc = _comboxdesc.trim(); //
			if (_comboxdesc.startsWith("=>")) { //�������SQL������ֱ�ӵ��÷�����,����:=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getMenuCommandType(),����ݹ�ʽֱ��ȡֵ
				String str_clsName = _comboxdesc.substring(2, _comboxdesc.length()).trim(); //���������
				if (str_clsName.endsWith(";")) {
					str_clsName = str_clsName.substring(0, str_clsName.length() - 1);
				}
				String[][] str_data = (String[][]) getTBUtil().reflectCallMethod(str_clsName); //�������!!!
				if (str_data != null) {
					ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_data.length];
					for (int i = 0; i < str_data.length; i++) {
						comBoxItemVOs[i] = new ComBoxItemVO(str_data[i][0], str_data[i][1], str_data[i][2]);
					}
					return comBoxItemVOs; //
				}
			} else if (_comboxdesc.startsWith("getCommUC(")) { //������µĿؼ�����!
				_comboxdesc = tbUtil.replaceAll(_comboxdesc, "\r", ""); //������ȥ��!!
				_comboxdesc = tbUtil.replaceAll(_comboxdesc, "\n", ""); //
				//System.out.println("������ʽ[" + _comboxdesc + "]");  //
				CommUCDefineVO uCDfVO = (CommUCDefineVO) _jep.execFormula(_comboxdesc); //ִ�й�ʽ
				if (uCDfVO != null) {
					if (_type == 1) {
						_pub_Templet_1_ItemVO.setUCDfVO(uCDfVO); //
					} else if (_type == 2) {
						_pub_Templet_1_ItemVO.setQueryUCDfVO(uCDfVO); //���ÿؼ�����VO!
					}
					return getComBoxItemVOsByCommUCDfVO(uCDfVO); //
				}
			} else { //ֱ�����ַ���,�����ϵ�ֱ��д��SQL�͸㶨��!!
				String[] str_array = tbUtil.split(_comboxdesc, ";");
				String str_comboxitem_sql = str_array[0]; //
				String str_datasourcename = null;
				if (str_array.length == 1) {
					str_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); // Ĭ������Դ!!
				} else if (str_array.length == 2) {
					str_datasourcename = tbUtil.convertDataSourceName(getCurrSession(), getDataSourceName(str_array[1])); //������ָ������Դ!!
				}
				String modify_str = tbUtil.convertComboBoxDescSQL(str_comboxitem_sql, getCurrSession()); //
				HashVO[] hvs = getCommDMO().getHashVoArrayByDS(str_datasourcename, modify_str); // �����������е�SQLȡ�����������������ݵ�HashVO,���ܻ����쳣!!
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[hvs.length];
				for (int i = 0; i < hvs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(hvs[i]); //
				}
				return comBoxItemVOs; //
			}
			return null; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//�����µ�ͨ�ñ�׼�Ŀؼ�����VO��������������!!
	private ComBoxItemVO[] getComBoxItemVOsByCommUCDfVO(CommUCDefineVO _uCDfVO) throws Exception {
		try {
			if (_uCDfVO.containsConfKey("ֱ��ֵ")) {
				String str_item = _uCDfVO.getConfValue("ֱ��ֵ"); //
				String[] str_items = tbUtil.split(str_item, ";"); //
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_items.length];
				for (int i = 0; i < comBoxItemVOs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(str_items[i], str_items[i], str_items[i]); //
				}
				return comBoxItemVOs; //
			} else if (_uCDfVO.containsConfKey("SQL���")) { //�����SQL���
				HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_uCDfVO.getConfValue("����Դ����"), _uCDfVO.getConfValue("SQL���")); // �����������е�SQLȡ�����������������ݵ�HashVO,���ܻ����쳣!!
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[hvs.length]; //��ײ
				for (int i = 0; i < hvs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(hvs[i]); //ֱ�Ӵ���!!
				}
				return comBoxItemVOs; //
			} else if (_uCDfVO.containsConfKey("������")) { //ͨ��������
				String str_clsName = _uCDfVO.getConfValue("������"); //
				if (str_clsName.endsWith(";")) {
					str_clsName = str_clsName.substring(0, str_clsName.length() - 1);
				}
				String[][] str_data = (String[][]) getTBUtil().reflectCallMethod(str_clsName); //�������!!!
				if (str_data != null) {
					ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_data.length];
					for (int i = 0; i < str_data.length; i++) {
						comBoxItemVOs[i] = new ComBoxItemVO(str_data[i][0], str_data[i][1], str_data[i][2]);
					}
					return comBoxItemVOs; //
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ȡ��ע�ᰴť�Ķ���
	 * @param _btndesc
	 * @return
	 * @throws Exception
	 */
	public ButtonDefineVO[] getRegButtons(String _btndesc) throws Exception {
		if (_btndesc == null || _btndesc.trim().equals("")) { //���Ϊ��,��ֱ�ӷ��ؿ�!!
			return null;
		}
		String[] str_codes = _btndesc.split(";"); //
		ButtonDefineVO[] btns = new ButtonDefineVO[str_codes.length]; // ����������ť.
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_regbuttons where code in (" + tbUtil.getInCondition(str_codes) + ")"); // ȥע�ᰴť���������еİ�ť
		Hashtable ht_btns = new Hashtable();
		for (int i = 0; i < hvs.length; i++) {
			ht_btns.put(hvs[i].getStringValue("code"), hvs[i]); // ���ҵ��İ�ť�ŵ���ϣ����!��Ϊ����Ǩ�Ƶ�����,���ܻ������ģ���ж���,���ڰ�ťע�����ȴû��!!!
		}

		//����������ť!
		String[][] str_allregbtns = WLTButton.getSysRegButtonType(); //
		for (int i = 0; i < str_codes.length; i++) {
			btns[i] = new ButtonDefineVO(str_codes[i]); //
			if (str_codes[i].startsWith("$")) { //�����$��ͷ��,����Ҫע����ϵͳ����!
				String str_btntype = str_codes[i].substring(1, str_codes[i].length()); //
				btns[i].setBtntype(str_btntype); //
				btns[i].setBtntext(str_btntype); //
				for (int j = 0; j < str_allregbtns.length; j++) {
					if (str_allregbtns[j][0].equals(str_btntype)) {
						btns[i].setBtntext(str_allregbtns[j][1]); //
						btns[i].setBtnimg(str_allregbtns[j][3]); //
						break; //
					}
				}
			} else {
				if (ht_btns.containsKey(str_codes[i])) { //���ע����!
					HashVO hvBtn = (HashVO) ht_btns.get(str_codes[i]); //
					btns[i].setBtntype(hvBtn.getStringValue("btntype")); //
					btns[i].setBtntext(hvBtn.getStringValue("btntext")); //
					btns[i].setBtnimg(hvBtn.getStringValue("btnimg")); ////
					btns[i].setBtntooltiptext(hvBtn.getStringValue("btntooltiptext")); //
					btns[i].setBtnpars(hvBtn.getStringValue("btnpars")); //��ť����
					btns[i].setClickingformula(hvBtn.getStringValue("clickingformula")); //
					btns[i].setClickedformula(hvBtn.getStringValue("clickedformula")); //
					btns[i].setAllowposts(hvBtn.getStringValue("allowposts")); //
					btns[i].setAllowroles(hvBtn.getStringValue("allowroles")); //
					btns[i].setAllowroletype(hvBtn.getStringValue("allowroletype")); //
					btns[i].setAllowusers(hvBtn.getStringValue("allowusers")); //
					btns[i].setAllowusertype(hvBtn.getStringValue("allowusertype")); //
					btns[i].setAllowifcbyinit(hvBtn.getStringValue("allowifcbyinit")); //BS�˳�ʼ��ʱ��������!
					btns[i].setAllowifcbyclick(hvBtn.getStringValue("allowifcbyclick")); //UI�˵����ѡ��仯ʱ��������!
					btns[i].setBtndescr(hvBtn.getStringValue("btndescr")); //
					btns[i].setRegisterBtn(true); //��ע���͵İ�ť!!!
				} else { //
					btns[i].setBtntext("ûע��"); //
					btns[i].setBtndescr("��ϵͳ��û���ҵ�CodeΪ[" + str_codes[i] + "]��ע�ᰴť");
					btns[i].setRegisterBtn(true); //��ע���͵İ�ť
				}
			}
		}
		MetaDataBSUtil bsUtil = new MetaDataBSUtil();
		HashMap sharePoolMap = new HashMap(); //

		CurrSessionVO sessionVO = new WLTInitContext().getCurrSession();
		String str_loginUserId = (sessionVO == null ? null : sessionVO.getLoginUserId()); //�ڹ�������������ʱ,��Ϊ����Ĭ�ϵ�Զ�̵���,����sessionVOΪ��,�������ָ���쳣!! �ʼӴ��ж�!��xch/2012-11-09��
		if (str_loginUserId != null) {
			for (int i = 0; i < btns.length; i++) {
				bsUtil.setWltBtnAllow(btns[i], str_loginUserId, sharePoolMap); //
			}
		}
		return btns; //
	}

	private String[] getListBtnsOrderDesc(String str_listorder) {
		if (str_listorder == null || str_listorder.trim().equals("")) {
			return null;
		}
		return getTBUtil().split(str_listorder, ";");
	}

	//ȡ�ÿؼ������߶�
	private int[] getComponentWidthHeight(String str_widthstr, String _itemType) {
		try {
			if (str_widthstr == null || str_widthstr.trim().equals("")) {
				return null;
			}
			int[] li_return = new int[] { 120, 138, 20 }; //Ĭ��
			int li_pos_1 = str_widthstr.indexOf(","); //
			String str_widthstr2 = str_widthstr; //
			if (li_pos_1 >= 0) { // ���ָ����label�Ŀ��
				li_return[0] = Integer.parseInt(str_widthstr.substring(0, li_pos_1)); //
				str_widthstr2 = str_widthstr.substring(li_pos_1 + 1, str_widthstr.length()); //
			}
			int li_pos_2 = str_widthstr2.indexOf("*"); //����û�и߶�!
			if (li_pos_2 >= 0) {
				li_return[1] = Integer.parseInt(str_widthstr2.substring(0, li_pos_2)); //
				li_return[2] = Integer.parseInt(str_widthstr2.substring(li_pos_2 + 1, str_widthstr2.length())); //
			} else {
				li_return[1] = Integer.parseInt(str_widthstr2); //���
				if ("�����ı���".equals(_itemType)) { //����Ƕ����ı���,��߶���
					li_return[2] = 75; //
				}
			}
			return li_return; //
		} catch (Exception _ex) {
			System.err.println("����ؼ��߶ȡ�" + str_widthstr + "��ʱ�����쳣:" + _ex.getClass().getName() + ":" + _ex.getMessage()); //
			return null; //
		}
	}

	/**
	 * ȡ��BillListData
	 * 
	 * @param _sql
	 * @param _templetVO
	 * @param _env
	 * @return
	 * @throws Exception
	 */
	public Object[] getBillCardDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Object[][] objs_formuls = getBillListDataByDS(_datasourcename, _sql, _templetVO); // ִ�м��ع�ʽ���ֵ
		if (objs_formuls != null && objs_formuls.length > 0) {
			return objs_formuls[0]; // ���ص�һ��
		} else {
			return null;
		}
	}

	/**
	 * ��������
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetCode
	 * @return
	 * @throws Exception
	 */
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Pub_Templet_1VO templetVO = getPub_Templet_1VO(_templetCode); //
		return getBillVOBuilder(_datasourcename, _sql, templetVO); //
	}

	/**
	 * ����billVO Builder
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetVO
	 * @return
	 * @throws Exception
	 */
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception { //
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		TableDataStruct tbs = getCommDMO().getTableDataStructByDS(_datasourcename, _sql); //
		BillVOBuilder billVOBuilder = new BillVOBuilder(); //
		billVOBuilder.setTableDataStruct(tbs); //
		billVOBuilder.setTempletVO(_templetVO); //
		return billVOBuilder; //
	}

	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillVOsByDS(_datasourcename, _sql, _templetVO, true); //
	}

	/**
	 * ���BillVO����....
	 * 
	 * @return
	 * @throws Exception
	 */
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
		}
		Pub_Templet_1_ItemParVO[] templetItemVOs = _templetVO.getItemVos(); // ������.
		int li_length = templetItemVOs.length;

		// ��ȡ����������
		Object[][] objs = getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula); // ȡ����������!!

		BillVO[] billVOs = new BillVO[objs.length]; //
		for (int r = 0; r < billVOs.length; r++) {
			billVOs[r] = new BillVO();
			billVOs[r].setQueryTableName(_templetVO.getTablename()); // ���ò�ѯ��
			billVOs[r].setSaveTableName(_templetVO.getSavedtablename()); // ���ñ����
			billVOs[r].setPkName(_templetVO.getPkname()); // ���������ֶ���
			billVOs[r].setSequenceName(_templetVO.getPksequencename()); // ������

			// ����ItemKey
			String[] all_Keys = new String[li_length + 1]; //
			all_Keys[0] = "_RECORD_ROW_NUMBER"; // �к�
			for (int i = 1; i < all_Keys.length; i++) {
				all_Keys[i] = _templetVO.getItemKeys()[i - 1];
			}

			// ���е�����
			String[] all_Names = new String[li_length + 1]; //
			all_Names[0] = "�к�"; // �к�
			for (int i = 1; i < all_Names.length; i++) {
				all_Names[i] = _templetVO.getItemNames()[i - 1];
			}

			String[] all_Types = new String[li_length + 1]; //
			all_Types[0] = "�к�"; // �к�
			for (int i = 1; i < all_Types.length; i++) {
				all_Types[i] = _templetVO.getItemTypes()[i - 1];
			}

			String[] all_ColumnTypes = new String[li_length + 1]; //
			all_ColumnTypes[0] = "NUMBER"; // �к�
			for (int i = 1; i < all_ColumnTypes.length; i++) {
				all_ColumnTypes[i] = templetItemVOs[i - 1].getSavedcolumndatatype(); //
			}

			boolean[] bo_isNeedSaves = new boolean[li_length + 1];
			bo_isNeedSaves[0] = false; // �к�
			for (int i = 1; i < bo_isNeedSaves.length; i++) {
				bo_isNeedSaves[i] = templetItemVOs[i - 1].isNeedSave();
			}

			billVOs[r].setKeys(all_Keys); // �������е�key
			billVOs[r].setNames(all_Names); // �������е�Name
			billVOs[r].setItemType(all_Types); // �ؼ�����!!�������е�����!!
			billVOs[r].setColumnType(all_ColumnTypes); // ���ݿ�����!!
			billVOs[r].setNeedSaves(bo_isNeedSaves); // �Ƿ���Ҫ����!!
			billVOs[r].setToStringFieldName(_templetVO.getTostringkey()); //
			billVOs[r].setDatas(objs[r]); // ��������������!!
		}

		// ִ�����ݹ�����!!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterclass().trim().equals("")) { // ���������������
			ArrayList v_filters = new ArrayList();
			IBsDataFilter_IFC filter = (IBsDataFilter_IFC) Class.forName(_templetVO.getBsdatafilterclass()).newInstance(); //
			for (int i = 0; i < billVOs.length; i++) {
				if (filter.filterBillVO(billVOs[i])) { // ���ͨ����������һ��!!!�����
					v_filters.add(billVOs[i]); //
				}
			}
			return (BillVO[]) v_filters.toArray(new BillVO[0]); //
		}

		return billVOs; //
	}

	/**
	 * ȡ�����ͽṹ������,��������ͽṹ��һ��,����Ҫһ����ȫ��ȡ����,���Է��ظ��ͻ��˵�����ҪС,�����ڿͻ��˹�������!!!
	 * @return
	 */
	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); ////
		}
		if (_templetVO.getBsdatafilterclass() != null) { //
			if (_templetVO.getBsdatafilterissql()) { //���BS�˹������Ƿ��ص�SQL
				String str_sqlcons = getFilterByBSFilterSQLCons(_templetVO.getBsdatafilterclass()); //
				if (str_sqlcons != null && !str_sqlcons.trim().equals("")) {
					int li_orderpos = _sql.toLowerCase().indexOf("order by"); //
					if (li_orderpos > 0) { //���ԭ����order by,��Ҫ���м����!!!
						_sql = _sql.substring(0, li_orderpos) + " and (" + str_sqlcons + ") " + _sql.substring(li_orderpos, _sql.length()); //
					} else {
						_sql = _sql + " and (" + str_sqlcons + ") ";
					}
				}
			}
		}

		HashVOStruct hvsStruct = getCommDMO().getHashVoStructByDS(_datasourcename, _sql); //ȡ������������,��HashVO!!!
		HashVO[] hashVOs = hvsStruct.getHashVOs(); ////��ȡ����������,����Ƿ�ҳ�Ļ�,ֻ�᷵��ĳһҳ������!!!

		//��һ�ι���!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterissql()) { //��������˹�����,����й���!�ķ�����ͨ������HashVO��setVisible()��ʵ��!
			hashVOs = filterByBSFilter(hashVOs, _templetVO.getBsdatafilterclass()); //
		}

		TableDataStruct tds = new TableDataStruct(); //
		tds.setTableName(hvsStruct.getTableName()); //
		tds.setHeaderName(hvsStruct.getHeaderName()); //
		tds.setHeaderType(hvsStruct.getHeaderType()); //
		tds.setHeaderTypeName(hvsStruct.getHeaderTypeName()); //
		tds.setHeaderLength(hvsStruct.getHeaderLength()); //

		String[][] tds_bodyData = new String[hashVOs.length][hvsStruct.getHeaderName().length]; //
		for (int i = 0; i < tds_bodyData.length; i++) {
			for (int j = 0; j < tds_bodyData[i].length; j++) {
				tds_bodyData[i][j] = hashVOs[i].getStringValue(hvsStruct.getHeaderName()[j]); //
			}
		}
		tds.setBodyData(tds_bodyData); //���ñ�������
		return tds; //
	}

	/**
	 * ȡ��BS�˹������з��ص�SQL!
	 * @param _bsFilterDefine
	 * @return
	 */
	private String getFilterByBSFilterSQLCons(String _bsFilterDefine) {
		try {
			if (_bsFilterDefine.startsWith("@")) { //ֱ��ĳ�������뷽����
				String str_realClassName = _bsFilterDefine.substring(1, _bsFilterDefine.length()); //
				str_realClassName = getTBUtil().replaceAll(str_realClassName, "\r", ""); //
				str_realClassName = getTBUtil().replaceAll(str_realClassName, "\n", ""); //
				String str_sqlcons = (String) getTBUtil().reflectCallMethod(str_realClassName); //�������!!
				return str_sqlcons; //
			} else { //ʹ��BSH��ʽִ��!!!!������Bsh2.4����ִ��!!!
				Interpreter inter = new Interpreter(); //����BSH������
				String str_sqlcons = (String) inter.eval(_bsFilterDefine); //ִ�й�ʽ!!!
				return str_sqlcons; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}

	}

	/**
	 * ʹ��BS�˹���������!!!
	 * @param _hashVOs
	 * @param _bsFilterDefine
	 */
	private HashVO[] filterByBSFilter(HashVO[] _hashVOs, String _bsFilterDefine) {
		try {
			if (_bsFilterDefine.startsWith("@")) {
				String str_realClassName = _bsFilterDefine.substring(1, _bsFilterDefine.length()); //
				str_realClassName = this.tbUtil.replaceAll(str_realClassName, "\r", ""); //
				str_realClassName = this.tbUtil.replaceAll(str_realClassName, "\n", ""); //
				BillDataBSFilterIFC bsfilter = (BillDataBSFilterIFC) Class.forName(str_realClassName).newInstance(); //
				bsfilter.filterBillData(_hashVOs); //ʵ�ʹ���
			} else { //ʹ��BSH��ʽִ��!!!!������Bsh2.4����ִ��!!!
				Interpreter inter = new Interpreter(); //����BSH������
				inter.set("_hvs", _hashVOs); //�������!!!
				inter.eval(_bsFilterDefine); //ִ�й�ʽ!!! 
			}

			//ֻ�����ʾ��!!!!
			ArrayList al_tempHvs = new ArrayList(); //
			for (int i = 0; i < _hashVOs.length; i++) {
				if (_hashVOs[i].isVisible()) { //ֻ�������˿�����ʾ,�ſ��Լ���!
					al_tempHvs.add(_hashVOs[i]); //
				}
			}
			return (HashVO[]) al_tempHvs.toArray(new HashVO[0]); //���¸�ֵ!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return _hashVOs; //
		}
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, true, null); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, true, null); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula, _rowArea, false); //
	}

	/**
	 * ȡ����!!!
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetVO
	 * @param _isExecLoadFormula
	 * @param _rowArea
	 * @param _isRegHVOinRowNumberVO
	 * @param isps
	 * @return
	 * @throws Exception
	 */
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea, boolean _isRegHVOinRowNumberVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); ////
		}
		//ʵ�ʲ�ѯ���ݿ�! ѹ������ʱ���������������!
		WLTInitContext initContext = new WLTInitContext(); //
		CurrSessionVO sessionVO = initContext.getCurrSession(); //
		boolean isLRCall = false; //
		if (sessionVO != null && sessionVO.isLRCall()) {
			isLRCall = true; //�жϳ���LR�ڵ���!! �����LV�ڵ���,�������Ǵ���æµ״̬,���¿��߳�ȥ����!!Ȼ��ֱ�ӷ���һ��������!!!
			//��֮,�����ֱ�ӷ���,����Զֱ�Ӳ����ݿ�! ����˵��Ȼ��LR����,��ֻҪ����̫æ,��Ҳ��ֱ�Ӳ�ѯ���ݿ�!
			//��������ǰ��������,���ʹ��LR����,��ʹ���Ƿ���æ���㷨������,��Ϊ�ǵ����̲߳�ѯ���ݿ�,��ʵ����Ҳ�ǻ��ѯ���ݿ��,�������Ϳ���������!!
			//Ϊ�˱�֤Ч��Ψ��ΨФ,Ӧ��Sleepһ��!!
		}

		boolean isRealBusy = new BSUtil().isRealBusiCall(); //�ж��Ƿ����µ�æ!!�ܹؼ�!��������æ�ǲ�Ҫ����! ���¸�æ��û�������!!

		//��ǰĬ������ѯ2000��
		boolean isUpperLimit = false;
		if (limitcount > 0) {
			isUpperLimit = true;
		}

		if (_templetVO.getBsdatafilterclass() != null) { //���������Ȩ�޹�����,��ǿ�в�ʹ��SQL��ҳ!!!���ǲ����������!! �Ժ�Ӧ���Ż�,������������,Ȼ�����ʱ����20����ֱ�ӷ���! �������Ȳ������,Ȼ���ٹ���!
			if (_templetVO.getBsdatafilterissql()) { //���BS�˹������Ƿ��ص�SQL
				String str_sqlcons = getFilterByBSFilterSQLCons(_templetVO.getBsdatafilterclass()); //
				if (str_sqlcons != null && !str_sqlcons.trim().equals("")) {
					int li_orderpos = _sql.toLowerCase().indexOf("order by"); //
					if (li_orderpos > 0) { //���ԭ����order by,��Ҫ���м����!!!
						_sql = _sql.substring(0, li_orderpos) + " and (" + str_sqlcons + ") " + _sql.substring(li_orderpos, _sql.length()); //
					} else {
						_sql = _sql + " and (" + str_sqlcons + ") ";
					}
				}
				initContext.addCurrSessionForClientTrackMsg("����BS���������ص���SQL,��ȡ�õ�������[" + str_sqlcons + "]\r\n"); //
			} else {
				_rowArea = null; //
				isUpperLimit = false; //�Ƿ���������!!
			}
		}

		HashVOStruct hvsStruct = null; //�����������ȫ�����İ취,���BS�˹�����SQL��ҳ���˾ͺ���!!!�����������ܵ��������! �д�ͻ��!!!
		String str_fromtabName = null; //
		int li_from_pos = _sql.toLowerCase().indexOf("from"); //
		if (li_from_pos > 0) {
			String str_tmp = _sql.substring(li_from_pos + 4, _sql.length()).trim(); //
			int li_blank_pos = str_tmp.indexOf(" "); //��һ���ո�,��Ϊ�� from pub_user where 1=1
			if (li_blank_pos > 0) {
				str_fromtabName = str_tmp.substring(0, li_blank_pos).toLowerCase(); //����!
			}
		}

		//�����LR����,��ָ��
		if (ServerEnvironment.isPageFalsity && isLRCall && isRealBusy && _rowArea != null && str_fromtabName != null && ServerEnvironment.pageFalsityMapData.containsKey(str_fromtabName)) { //�����LR�ڵ���,���ҵ�ȷ��æ!���һ�������
			new FalsityThread().getHashVoStructByDS(_datasourcename, _sql, true, true, _rowArea); //�ȿ�һ���߳�ȥ��!��֤���ݿ����и��ص�!!
			ServerEnvironment.newThreadCallCount++; //
			hvsStruct = (HashVOStruct) ServerEnvironment.pageFalsityMapData.get(str_fromtabName); //�ӻ���ȡֱ�ӷ���!
			Thread.currentThread().sleep(ServerEnvironment.falsitySleep); //��Ϣ600����!!�γ�Ч��!!!��Ҳ����̫��!!
			//System.out.println("�¿��߳�,��Ϣ��[" + ServerEnvironment.falsitySleep + "]"); //
		} else { //�����ǵ��߳�ֱ��ȡ!!
			hvsStruct = getCommDMO().getHashVoStructByDS(_datasourcename, _sql, true, isUpperLimit, _rowArea, true, false); //ȡ������������,��HashVO!!!�б��ѯ�������޿��Ƶ�! ����������̫��ʱ���ŷ�ҳ! new int[] {1,20},{21-40}
			if (ServerEnvironment.isPageFalsity && !ServerEnvironment.pageFalsityMapData.containsKey(str_fromtabName)) { //���ָ���������,�������⻺����û�иñ�! ������!!
				ServerEnvironment.pageFalsityMapData.put(str_fromtabName, hvsStruct); //
			}
		}
		HashVO[] hashVOs = hvsStruct.getHashVOs(); //�õ������б�����!!

		//��һ�ι���!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterissql()) { //��������˹�����,����й���!�ķ�����ͨ������HashVO��setVisible()��ʵ��!
			int li_oldcount = hashVOs.length; //
			hashVOs = filterByBSFilter(hashVOs, _templetVO.getBsdatafilterclass()); //ʹ��Java�����Ƿ����ʾ�����й���!!!
			initContext.addCurrSessionForClientTrackMsg("BS������������setVisiable,����ǰ��¼����[" + li_oldcount + "]��,���˺���[" + hashVOs.length + "]��\r\n"); //
		}

		//��װ��һ������!!��Ϊ��������һ�����,������ͨ��SQL����HashVO[],����ֱ�Ӵ���HashVO[],���罫XML�е���������������!!!
		Object[][] valueData = getBillListDataByHashVOs(_templetVO, hashVOs, hvsStruct.getTotalRecordCount(), _isRegHVOinRowNumberVO);

		Object[][] returnObjects = null; //ʵ�ʷ���ֵ!!
		if (_isExecLoadFormula) { // �����Ҫִ�м��ع�ʽ
			//long ll_a = System.currentTimeMillis();  //
			returnObjects = execLoadformula(hashVOs, valueData, _templetVO, getCurrSession()); //ִ�м��ع�ʽ!!���������ײ���ƿ���ĵط�,�����������Ա�������������ܸ�����,һ����˵,һ��������5����ʽ,ʵ��ִ��100�εĴ�������������Ҫ800�����,���������ֻ��Ҫ15����!!!
			//long ll_b = System.currentTimeMillis();  //
			//System.out.println("***************ִ�м��ع�ʽ��ʱ[" + (ll_b - ll_a) + "]"); //
		} else {
			returnObjects = valueData;
		}
		//��ǰ������ʹ��Java�����,������������Ŀ�з������ܲ��Ը�������ȥ!!!���Է�����!
		return returnObjects; //
	}//

	/**
	 * ֱ�Ӹ���ģ��VO��HashVO[]�õ�ʵ������!��ֱ�Ӵ�XML�ĵط��õ�!!
	 */
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs) {
		return getBillListDataByHashVOs(_templetVO, _hashVOs, _hashVOs.length, false); //ת��!!
	}

	/**
	 * ���������������һ��HashVO[]���ݸ���ģ��VO�ж���Ŀؼ�����ת��������ģ���ʽ������!!!
	 * @param _templetVO
	 * @param _hashVOs
	 * @param totalRecordCount
	 * @param _isRegHVOinRowNumberVO
	 * @return
	 */
	private Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs, int totalRecordCount, boolean _isRegHVOinRowNumberVO) {
		Pub_Templet_1_ItemParVO[] itemVos = _templetVO.getItemVos(); // ������.
		String[] itemKeys = _templetVO.getItemKeys(); // �����key
		String[] itemTypes = _templetVO.getItemTypes(); // ���������
		boolean[] isEncrypts = _templetVO.getItemIsEncrypt(); //�����Ƿ���Ҫ���ܴ洢!!!
		int li_length = _templetVO.getItemKeys().length; // �ܹ��ж�����,�����к�,��Ӧ��һ��!!

		//��HashVO��������װ��BillVO,���ʱ�ĵط�,��û�м��ع�ʽ�������,ռ������ʱ��80%����!!!
		//�������Ǵ��ڴ����,���������ǻ����൱�ߵ�,��һ��20��30�й���600�δ���Ľṹ,�в��յȸ�������,һ���ʱ��16��������!��һ��һ�еĴ�����0.02����,һ�м�¼�Ĵ�����0.8����!!!
		Object[][] valueData = new Object[_hashVOs.length][li_length + 1]; // �������ݶ���!!!,��ģ���һ�����к�,��Զ�ڵ�һ��!!
		String str_key = null;
		String str_type = null;
		String str_value = null;
		String str_datetimevalue = null;
		DESKeyTool desTool = new DESKeyTool(); //DES��������ܹ���!!!
		for (int i = 0; i < _hashVOs.length; i++) { // ��������
			Object[] rowobjs = new Object[li_length + 1]; // һ�е�����
			RowNumberItemVO rowNumberVO = new RowNumberItemVO(); // �к�VO
			rowNumberVO.setTotalRecordCount(totalRecordCount); //����������¼��ʵ�ʵ��ܹ���¼��!
			rowNumberVO.setState(WLTConstants.BILLDATAEDITSTATE_INIT); //
			if (_isRegHVOinRowNumberVO) {
				rowNumberVO.setRecordHVO(_hashVOs[i]); //�����Ҫ���к���ע��HashVO,����һ��!!
			}
			//rowNumberVO.setRecordIndex(i); // ��¼��
			rowobjs[0] = rowNumberVO; // ��һ����Զ���к�VO

			// Ȼ���ٴ���ÿһ�е�ֵ
			for (int j = 0; j < li_length; j++) { // ��������
				str_key = itemKeys[j]; //
				str_type = itemTypes[j]; //����
				str_value = _hashVOs[i].getStringValue(str_key); // ��ȡ�ó�ʵ��ֵ!!!
				if (str_value != null && !str_value.trim().equals("")) { //
					//�������ֶ��Ǽ��ܵ�,��Ҫ�����ܴ���!!!
					if (isEncrypts[j]) { //����Ǽ��ܴ洢
						str_value = desTool.decrypt(str_value); //����һ��!!!
					}
					if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || //�ı���
							str_type.equals(WLTConstants.COMP_LABEL) || //label
							str_type.equals(WLTConstants.COMP_NUMBERFIELD) || //���ֿ�
							str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || //�����
							str_type.equals(WLTConstants.COMP_TEXTAREA) || //�����ı���
							str_type.equals(WLTConstants.COMP_BUTTON) || //��ť
							str_type.equals(WLTConstants.COMP_CHECKBOX)) { //��ѡ�� 
						rowobjs[j + 1] = new StringItemVO(str_value); //
					} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // �����������
						ComBoxItemVO matchVO = findComBoxItemVO(itemVos[j].getComBoxItemVos(), str_value); //�������������������׺�ʱ�ĵط�
						if (matchVO != null) {
							rowobjs[j + 1] = matchVO;
						} else {
							rowobjs[j + 1] = new ComBoxItemVO(str_value, null, str_value); // ������VO
						}
					} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���
							str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���
							str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����
							str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
							str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
							str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
							str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
							str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����!!
							str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
							str_type.equals(WLTConstants.COMP_COLOR) || //��ɫ
							str_type.equals(WLTConstants.COMP_CALCULATE) || //������
							str_type.equals(WLTConstants.COMP_PICTURE) || //ͼƬ
							str_type.equals(WLTConstants.COMP_LINKCHILD) || //�����ӱ�
							str_type.equals(WLTConstants.COMP_IMPORTCHILD) //�����ӱ� 
					) {
						if (str_value != null && str_value.startsWith(";")) { //���ں����д����Ķ�ѡ����,ǰ�����һ���ֺ�,�ɴ�������ֱ�Ӹɵ�����!
							String str_viewvalue = str_value.substring(1, str_value.length()); //
							rowobjs[j + 1] = new RefItemVO(str_value, null, str_viewvalue); // 
						} else {
							rowobjs[j + 1] = new RefItemVO(str_value, null, str_value); // ����ȡ�õ�ֱֵ�Ӹ���ĳ������
						}
					} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { //�ļ�ѡ���
						String str_refId = str_value; //
						String[] str_items = tbUtil.split(str_refId, ";"); //
						StringBuilder sb_names = new StringBuilder(); //
						for (int ff = 0; ff < str_items.length; ff++) {
							sb_names.append(getViewFileName(str_items[ff])); //
							if (ff != str_items.length - 1) {
								sb_names.append(";");
							}
						}
						rowobjs[j + 1] = new RefItemVO(str_refId, null, sb_names.toString()); ////
					} else if (str_type.equals(WLTConstants.COMP_DATE)) { //����
						str_datetimevalue = _hashVOs[i].getStringValueForDay(str_key);
						rowobjs[j + 1] = new RefItemVO(str_datetimevalue, null, str_datetimevalue); //itemTypes[j].equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���
					} else if (str_type.equals(WLTConstants.COMP_DATETIME)) {
						str_datetimevalue = _hashVOs[i].getStringValueForSecond(str_key);
						rowobjs[j + 1] = new RefItemVO(str_datetimevalue, null, str_datetimevalue); //
					} else if (str_type.equals(WLTConstants.COMP_EXCEL)) {
						rowobjs[j + 1] = new RefItemVO(str_value, null, (str_value.indexOf("#") > 0 ? str_value.substring(str_value.indexOf("#") + 1, str_value.length()) : "����鿴Excel����")); //
					} else if (str_type.equals(WLTConstants.COMP_OFFICE)) { //
						String str_viewName = "����鿴"; //Ĭ�Ͻ�������ɵ��ļ���!��ǰ�ǽе���鿴!! �����������ϴ����ܺ�!������16���Ƶ��ļ���!
						int li_markcount = getTBUtil().findCount(str_value, "_"); //
						if (li_markcount == 0) { //������������ʱ,����һ��û��ǰ꡵�!!
							String str_masterName = null, str_extentName = null; //��������չ��
							int li_dot_pos = str_value.lastIndexOf("."); //����û�е�,�����,��˵��������չ����!
							if (li_dot_pos > 0) { //�������չ��!
								str_masterName = str_value.substring(0, li_dot_pos); //�ļ�����!!
								str_extentName = str_value.substring(li_dot_pos, str_value.length()); //��չ��!!����[.doc][.xls][.pdf]
							} else {
								str_masterName = str_value; //�ļ�����!!��ȫ����
								str_extentName = ""; //��չ��Ϊ�մ�
							}
							boolean isHexNo = getTBUtil().isHexStr(str_masterName); //
							if (isHexNo) {
								str_viewName = tbUtil.convertHexStringToStr(str_masterName) + str_extentName; //
							}
						} else if (li_markcount == 1) { //���ֻ��һ���»���,�������ܲ���������ɵ�!��Ϊ������ɵ��Ǻü����»���
							boolean isStartWith_N = str_value.startsWith("N"); //
							String str_serialNo = str_value.substring((isStartWith_N ? 1 : 0), str_value.indexOf("_")); //�»���ǰ��,N���ź�������!!
							String str_masterName = null, str_extentName = null; //��������չ��
							int li_dot_pos = str_value.lastIndexOf("."); //����û�е�,�����,��˵��������չ����!
							if (li_dot_pos > 0) { //�������չ��!
								str_masterName = str_value.substring(str_value.indexOf("_") + 1, li_dot_pos); //�ļ�����!!
								str_extentName = str_value.substring(li_dot_pos, str_value.length()); //��չ��!!����[.doc][.xls][.pdf]
							} else {
								str_masterName = str_value.substring(str_value.indexOf("_") + 1, str_value.length()); //�ļ�����!!��ȫ����
								str_extentName = ""; //��չ��Ϊ�մ�
							}
							boolean isRealNo = getTBUtil().isStrAllNunbers(str_serialNo); //�Ƿ�������
							boolean isHexNo = getTBUtil().isHexStr(str_masterName); //
							if (isRealNo && isHexNo) { //���ǰ�������,��������16����,��϶���Ҫת������!!�ϸ��ж�!!
								str_viewName = tbUtil.convertHexStringToStr(str_masterName) + str_extentName; //
							}
						}
						rowobjs[j + 1] = new RefItemVO(str_value, null, str_viewName); //
					} else {
						rowobjs[j + 1] = new StringItemVO(str_value); //
					}
				} else { // ���ûȡ����,��Ϊ��!
					rowobjs[j + 1] = null; //
				}
			} // һ�������еĸ���ȫ�����������!!!

			valueData[i] = rowobjs; //
		} // end for ���������ݴ������!! ��ǰ����ǰ��������߼�����������! ������֤ʵ����û��̫������,�ٴ�֤�����ڴ��߼������Ƿǳ����!

		return valueData;
	}

	//ȡ����ʾ���ļ���!��ȥ��������
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				int li_extentNamePos = param.lastIndexOf("."); //�ļ�����չ����λ��!�������и���!������ҵ��Ŀ��������ļ��ǴӺ�̨�����!!Ҳ������û��꡵�!!���Ա���!
				if (li_extentNamePos > 0) {
					return getTBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return getTBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //��ǰ�İ汾Ҳ�д�·���ģ�
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	/**
	 * ȡ��������
	 * @param _orderCondition ��������
	 * @param _itemKeys ����!!
	 * @param _itemTypes ������!!
	 * @return
	 */
	private String[][] getOrderCol(String _orderCondition, String[] _itemKeys, String[] _itemTypes) {
		String str_ordercondition = _orderCondition; //
		if (str_ordercondition.startsWith("\"")) {
			str_ordercondition = str_ordercondition.substring(1, str_ordercondition.length()); //�����˫���ſ�ͷ,��ȥ��
		}
		if (str_ordercondition.endsWith("\"")) {
			str_ordercondition = str_ordercondition.substring(0, str_ordercondition.length() - 1); //�����˫���Ž�β����ȥ�������Ǽ���˫����д��..
		}
		String[] str_item = this.tbUtil.split(str_ordercondition, ","); //�ָ�,���м�����������
		String[][] str_returns = new String[str_item.length][3]; ////��3��,[itemkey][�Ƿ�����][�Ƿ���]
		for (int i = 0; i < str_item.length; i++) {
			String[] str_item_asc = tbUtil.split(str_item[i], " "); //�Կո񻮷�
			str_returns[i][0] = str_item_asc[0]; //
			if (str_item_asc.length >= 2 && str_item_asc[1].trim().equalsIgnoreCase("desc")) { //���������ʽ����Ϊdesc,������
				str_returns[i][1] = "Y"; //�ǵ���
			} else {
				str_returns[i][1] = "N"; //���ǵ���,����˳��
			}
			str_returns[i][2] = testIsNumber(_itemKeys, _itemTypes, str_item_asc[0]); //�жϸ����Ƿ������ֿ�!!
		}
		return str_returns;
	}

	/**
	 * ȡ��һ���ַ����������е�λ��...
	 * @param _itemKeys
	 * @param _itemKey
	 * @return
	 */
	private String testIsNumber(String[] _itemKeys, String[] _itemTypes, String _itemKey) {
		int li_index = -1;
		for (int i = 0; i < _itemKeys.length; i++) {
			if (_itemKeys[i].equalsIgnoreCase(_itemKey)) { ////
				li_index = i;
				break;
			}
		}
		if (li_index < 0) {
			return "N"; //��������,�����ַ���
		}

		if (_itemTypes[li_index].equals(WLTConstants.COMP_NUMBERFIELD)) { //��������ֿ�...
			return "Y"; //������
		} else {
			return "N"; //��������,�����ַ�!!
		}
	}

	/**
	 * ִ�м��ع�ʽ!!!������Ҫ ����������setRefCode(),setRefName()�ȹ�ʽ!!!�ǳ�����!!
	 * ���ع�ʽ����ƶ�������ս����!!�����ѷ�����Ƶ��ʹ��jep���㹫ʽʱ,�������������⣡�� 
	 * @param _hashVOs
	 * @param _data
	 * @param _templetVO
	 * @param _env
	 * @return
	 */
	private Object[][] execLoadformula(HashVO[] _hashVOs, Object[][] _data, Pub_Templet_1ParVO _templetVO, CurrSessionVO _currSessionVO) {
		if (_data == null) {
			return null;
		}
		Pub_Templet_1_ItemParVO[] itemVos = _templetVO.getItemVos(); // ������.
		int li_length = itemVos.length; //
		VectorMap vm_loadFormulas = new VectorMap(); //LinkedHashSet
		for (int j = 0; j < li_length; j++) { // ����ÿһ��!!!!!
			String str_itemkey = itemVos[j].getItemkey(); //itemKey...
			String str_loadformula_define = itemVos[j].getLoadformula(); // ȡ�ü��ع�ʽ����!!
			if (str_loadformula_define != null && !str_loadformula_define.trim().equals("")) { // ����м��ع�ʽ����
				String[] str_loadformulas = tbUtil.split1(str_loadformula_define.trim(), ";"); // ��ֳ����й�ʽ��!!!
				for (int k = 0; k < str_loadformulas.length; k++) { // ������ʽ!!�����зֺŸ���!!!�ֱ�ִ��ÿ����ʽ��!!!!
					vm_loadFormulas.put(str_loadformulas[k], str_itemkey); //
				}
			}
		}
		if (vm_loadFormulas.size() == 0) { //���û���ҵ�һ�����ع�ʽ,��ֱ�ӷ���ԭ��������!!!
			return _data;
		}

		String[] itemKeys = _templetVO.getItemKeys(); // �����key
		String[] itemTypes = _templetVO.getItemTypes(); // ���������
		String[] str_allLoadFormulaItems = vm_loadFormulas.getKeysAsString(); //���������й�ʽ!!!
		JepFormulaParseAtBS jepFormulaAtBS = new JepFormulaParseAtBS(_hashVOs); // ��ʽ������ʵ��!!!!!!
		HashMap colDataTypeMap = new HashMap();
		for (int i = 0; i < itemKeys.length; i++) {
			colDataTypeMap.put(itemKeys[i], itemTypes[i]); ///
		}
		jepFormulaAtBS.setColDataTypeMap(colDataTypeMap); //

		HashMap mapRowCache = null; // �洢һ�����ݵĻ���..
		String[] str_viewdatakeys = null;
		long ll_realexeccount = 0; //
		long ll_maxdeal = 0; //
		int li_biger10 = 0; //
		//����ÿһ������,�ֱ�ִ�м��ع�ʽ!!!
		long ll_exec_1 = 0, ll_exec_2 = 0;
		HashMap map_itemkey_dealcount = new HashMap(); //��ϣ��!!!
		HashMap map_itemkey_recordcount = new HashMap(); //��ϣ��!!!
		long ll_1 = System.currentTimeMillis(); //
		for (int i = 0; i < _data.length; i++) {
			mapRowCache = new HashMap(); //�����м�¼����,�������洢һ�е�����,��Ϊ����ĳһ�е�ֵ��Ҫ����һ�м����!!
			str_viewdatakeys = _hashVOs[i].getKeys(); //
			for (int k = 0; k < str_viewdatakeys.length; k++) {
				mapRowCache.put(str_viewdatakeys[k], _hashVOs[i].getStringValue(str_viewdatakeys[k])); // �Ƚ������ݿ����ȡ���������е�ֵ���������HashMap,�����õ�!!
			}
			for (int j = 0; j < li_length; j++) {
				mapRowCache.put(itemKeys[j], _data[i][j + 1]); //��һ�����ڻ����м�����һ�е�����,�е��ǲ���,�е���������,Ȼ��ÿִ��һ����������û����м�������!!
			}

			jepFormulaAtBS.setRowDataMap(mapRowCache); // ����ĳһ������
			for (int j = 0; j < str_allLoadFormulaItems.length; j++) { // ����ÿһ��!!!!!��Ҫ��ִ�е�ĳһ��ItemKey�ĺ�ʱ�ۼӵ���Ӧ��ItemKey��!!!!!
				ll_exec_1 = System.currentTimeMillis(); //
				jepFormulaAtBS.execFormula(str_allLoadFormulaItems[j]); //ִ�й�ʽ!!!!��һ����Debugģʽ��ʱ���ڼ������ص���������!!��������ģʽ��ȴû����������,��֪Ϊʲô�����ô��?
				ll_realexeccount++; //ʵ���ۼ�
				ll_exec_2 = System.currentTimeMillis(); //
				long ll_deal = ll_exec_2 - ll_exec_1; //
				if (ll_deal > ll_maxdeal) {
					ll_maxdeal = ll_deal; //
				}
				if (ll_deal > 10) {
					li_biger10 = li_biger10 + 1;
				}
				String str_itemKey = (String) vm_loadFormulas.get(str_allLoadFormulaItems[j]); //
				if (!map_itemkey_dealcount.containsKey(str_itemKey)) { //�����������Ӧ��key
					map_itemkey_dealcount.put(str_itemKey, new Long(ll_exec_2 - ll_exec_1)); ////
					map_itemkey_recordcount.put(str_itemKey, new Integer(1)); //
				} else {
					long ll_olddeal = (Long) map_itemkey_dealcount.get(str_itemKey); //
					int li_oldrecords = (Integer) map_itemkey_recordcount.get(str_itemKey); //
					map_itemkey_dealcount.put(str_itemKey, new Long(ll_olddeal + (ll_exec_2 - ll_exec_1))); ////
					map_itemkey_recordcount.put(str_itemKey, new Integer(li_oldrecords + 1)); //
				}
			}
			for (int j = 0; j < li_length; j++) {
				_data[i][j + 1] = mapRowCache.get(itemKeys[j]); //
			}
		}
		long ll_2 = System.currentTimeMillis(); //
		long ll_dealcount = ll_2 - ll_1; //�ܼƺ�ʱ!!!

		String[] str_itemkey_keys = (String[]) map_itemkey_dealcount.keySet().toArray(new String[0]); //
		ArrayList al_msgs = new ArrayList(); //
		for (int i = 0; i < str_itemkey_keys.length; i++) {
			long ll_dealcount2 = (Long) map_itemkey_dealcount.get(str_itemkey_keys[i]); //
			al_msgs.add((1000000000 + ll_dealcount2) + "[" + str_itemkey_keys[i] + "(" + map_itemkey_recordcount.get(str_itemkey_keys[i]) + "��)=" + ll_dealcount2 + "����]"); //
		}
		String[] str_msgs = (String[]) al_msgs.toArray(new String[0]); //ת��������,��Ϊ��Ҫ��������,�������ʱ�ķ���ǰ��!!!
		getTBUtil().sortStrs(str_msgs); //����һ��,�����ʱ�ķ���ǰ��!!!
		StringBuilder sb_msg = new StringBuilder(); //ƴ����ϸ���ַ���!!
		for (int i = str_msgs.length - 1; i >= 0; i--) {
			sb_msg.append(str_msgs[i].substring(10, str_msgs[i].length())); ////
		}
		if (_data.length > 0 && ll_dealcount > 0) { //��ʾ�����ʱ(�Ժ���ר���͵�һ��ϵͳ��������ƿ������ȥ)!һ����˵,һ��������5����ʽ,ʵ��ִ��100�εĴ�������������Ҫ800�����,���������ֻ��Ҫ15����!!!
			logger.info("����[" + str_allLoadFormulaItems.length + "]�����ع�ʽ,ʵ��ִ��[" + ll_realexeccount + "]��,����ʱ[" + ll_dealcount + "]����(ǿ������:�����Debugģʽ,��Щ���ع�ʽ��ֵ���),��ϸ" + sb_msg.toString() + ",����ʱ[" + ll_maxdeal + "]����,����10��������[" + li_biger10 + "]��"); //
		}
		return _data; //
	}

	/**
	 * Ҫ��ƥ���ComBoxItemVO
	 * 
	 * @param _vos
	 * @param _id
	 * @return
	 */
	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { // ����������������Ϊ��!!
				return (ComBoxItemVO) _vos[k].deepClone(); //�����¡һ��!
			}
		}
		return null;
	}

	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception {
		int li_length = _templetVO.getItemKeys().length; // �ܹ��ж�����,�����к�,��Ӧ��һ��!!
		Pub_Templet_1_ItemVO[] itemVos = _templetVO.getItemVos(); // ������.
		String[] itemKeys = _templetVO.getItemKeys(); // �����key
		String[] itemTypes = _templetVO.getItemTypes(); // ���������
		HashVO[] hashVOs = getCommDMO().getHashVoArrayByDS(_datasourcename, _sql); // ȡ������������

		Object[][] valueData = new Object[hashVOs.length][li_length]; // �������ݶ���!!!,��ģ���һ�����к�,��Զ�ڵ�һ��!!

		// HashMap hm_RefItemVos = new HashMap(); // һ����Ϊ�������չ������еĲ�������!!!
		for (int i = 0; i < hashVOs.length; i++) { // ��������
			Object[] rowobjs = new Object[li_length]; // һ�е�����
			HashMap map_cache = new HashMap(); // һ�������еĻ���!!!��Value������String,ComBoxItemVO,RefItemVO

			// ����һ���ӽ�����ֵ����....
			String[] str_realvalues = new String[li_length]; //
			for (int j = 0; j < li_length; j++) { // ��������
				String str_key = itemKeys[j];
				String str_type = itemTypes[j]; //
				if (str_type.equals("����")) {
					str_realvalues[j] = hashVOs[i].getStringValueForDay(str_key); //
				} else if (str_type.equals("ʱ��")) {
					str_realvalues[j] = hashVOs[i].getStringValueForSecond(str_key); //
				} else {
					str_realvalues[j] = hashVOs[i].getStringValue(str_key); //
				}

				if (str_realvalues[j] != null) {
					if (itemTypes[j].equals("�ı���")) { // ������ı���
						map_cache.put(str_key, str_realvalues[j]);
					} else if (itemTypes[j].equals("������")) {
						map_cache.put(str_key, new ComBoxItemVO(str_realvalues[j], str_realvalues[j], str_realvalues[j]));
					} else if (itemTypes[j].equals("����")) {
						map_cache.put(str_key, new RefItemVO(str_realvalues[j], str_realvalues[j], str_realvalues[j]));
					} else {
						map_cache.put(str_key, str_realvalues[j]);//
					}
				}
			}
			// Ȼ���ٴ���ÿһ�е�ֵ
			for (int j = 0; j < li_length; j++) // ��������
			{
				String str_key = itemKeys[j];
				String str_type = itemTypes[j];
				String str_value = null;
				if (str_type.equals("����")) {
					str_value = hashVOs[i].getStringValueForDay(str_key); //
				} else if (str_type.equals("ʱ��")) {
					str_value = hashVOs[i].getStringValueForSecond(str_key); //
				} else {
					str_value = hashVOs[i].getStringValue(str_key); //
				}
				if (str_value != null) {
					if (itemTypes[j].equals("�ı���")) { // ������ı���
						rowobjs[j] = str_value;
					} else if (itemTypes[j].equals("������")) { // �����������..
						ComBoxItemVO[] comBoxItemVos = itemVos[j].getComBoxItemVos();
						ComBoxItemVO matchVO = findComBoxItemVO(comBoxItemVos, str_value); // //..
						if (matchVO != null) {
							rowobjs[j] = matchVO;
						} else {
							rowobjs[j] = new ComBoxItemVO(str_value, str_value, str_value); // ������VO
						}
					} else if (itemTypes[j].equals("����")) {
						if (itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL) || itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL_TREE) || itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL_MULTI)) { // ����Ǳ������
							String str_refsql = itemVos[j].getRefdesc(); //
							if (str_refsql != null && !str_refsql.trim().equals("")) {
								String modify_str = null; //
								try {
									modify_str = tbUtil.replaceStrWithSessionOrHashData(str_refsql, getCurrSession(), map_cache); // !!!!�õ�ת�����SQL,��ֱ�ӿ���ִ�е�SQL,�������е�{aaa}����ת��,!!!���ǹؼ�
								} catch (Exception ex) {
									System.out.println("ת������[" + _templetVO.getTempletcode() + "][" + itemVos[j].getItemkey() + "][" + itemVos[j].getItemname() + "]���ն���SQL[" + str_refsql + "]ʧ��!!!");
									ex.printStackTrace(); //
								}

								if (modify_str != null) { // ����ɹ�ת��SQL!!
									// modify_str =
									// tbUtil.replaceAll(modify_str, "1=1",
									// itemVos[j].getRefdesc_firstColName() +
									// "='" + str_value + "'"); //
									// ��SQL����е�1=1�滻��..
									System.out.println("��ʼִ�в��ն���ת�����SQL:" + modify_str); //
									HashVO[] ht_allRefItemVOS = null;
									try {
										ht_allRefItemVOS = getCommDMO().getHashVoArrayByDS(_datasourcename, modify_str); // ִ��SQL!!!���ܻ����쳣!!
									} catch (Exception ex) {
										System.out.println("ִ�в���[" + _templetVO.getTempletcode() + "][" + itemVos[j].getItemkey() + "][" + itemVos[j].getItemname() + "]ת����SQL[" + modify_str + "]ʧ��,ԭ��:" + ex.getMessage()); //
										ex.printStackTrace();
									}

									if (ht_allRefItemVOS != null) { // ���ȡ�õ�����!!
										boolean bo_iffindref = false;
										for (int pp = 0; pp < ht_allRefItemVOS.length; pp++) { // ����ȥ��!!!
											if (str_value.equals(ht_allRefItemVOS[pp].getStringValue(0))) {
												rowobjs[j] = new RefItemVO(ht_allRefItemVOS[pp]); //
												bo_iffindref = true; // ����ǳ����˵��ҵ�Ʒ�����!!!!!!!!!
												break;

											}
										}
										if (!bo_iffindref) { // ���û�ҵ�Ʒ���,��ֱ�Ӵ�������VO
											rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // ����VO
										}
									} else
									// ���ִ��SQLȡ��ʧ��!!
									{
										rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // ����VO
									}
								} else { // ���ת��SQLʧ��
									rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // ����VO
								}
							} else {
								rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // ����VO
							}
						} else { // ������Ǳ���1������1����!!,�����Զ���,TABLE2,TREE2��
							rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // ����VO
						}
					} else { // ����������ؼ�
						rowobjs[j] = str_value;
					}
				} else { // ���ûȡ����,��Ϊ��!
					rowobjs[j] = null;
				}

				map_cache.put(str_key, rowobjs[j]); // ������������
			} // һ�������еĸ���ȫ�����������!!!

			valueData[i] = rowobjs; //
		} // ���������ݴ������!!

		return valueData; //
	}//

	// �ύBillVO�������ݿ����!!
	public void commitBillVO(String _dsName, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception {
		String[] str_sql_deletes = null;
		if (_deleteVOs != null && _deleteVOs.length > 0) {
			str_sql_deletes = new String[_deleteVOs.length]; //
			for (int i = 0; i < str_sql_deletes.length; i++) {
				str_sql_deletes[i] = _deleteVOs[i].getDeleteSQL(); //  
			}
		}

		String[] str_sql_inserts = null;
		if (_insertVOs != null && _insertVOs.length > 0) {
			str_sql_inserts = new String[_insertVOs.length]; //
			for (int i = 0; i < str_sql_inserts.length; i++) {
				str_sql_inserts[i] = _insertVOs[i].getInsertSQL(); //
			}
		}

		String[] str_sql_updates = null;
		if (_updateVOs != null && _updateVOs.length > 0) {
			str_sql_updates = new String[_updateVOs.length]; //
			for (int i = 0; i < str_sql_updates.length; i++) {
				str_sql_updates[i] = _updateVOs[i].getUpdateSQL(); //
			}
		}

		if (str_sql_deletes != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_deletes); // ��ɾ��!!
		}

		if (str_sql_inserts != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_inserts); // �����!!
		}

		if (str_sql_updates != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_updates); // ����޸�!!
		}

	}

	/**
	 * Ȩ��Ȩ�޲��Թ���,���ص���SQL����,���硾blcorpid in ('12','13')��
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _datapolicyMap
	 * @return
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _datapolicyMap); //
	}

	/**
	 * �滻�ַ�
	 * 
	 * @param str_par
	 * @param old_item
	 * @param new_item
	 * @return
	 */
	private String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // ����Ҳ���,�򷵻�
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // ������ַ�������ԭ��ǰ�
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
		return str_return;
	}

	private String getDataSourceName(String _des) {
		String str_new = _des;
		int li_pos = str_new.indexOf("="); //
		str_new = str_new.substring(li_pos + 1, str_new.length()).trim();

		if (str_new.startsWith("\"") || str_new.startsWith("'")) {
			str_new = str_new.substring(1, str_new.length());
		}

		if (str_new.endsWith("\"") || str_new.endsWith("'")) {
			str_new = str_new.substring(0, str_new.length() - 1);
		}

		return str_new; // ȡ������Դ����
	}

	/**
	 * ȡ��
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 */
	public String getCreateSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop table " + _tabname + ";\r\n"); //
		sb_sql.append("create table " + _tabname + "\r\n(\r\n");
		for (int i = 0; i < str_coltypes.length; i++) {
			String str_coltype = convertColType(str_coltypes[i], _dbtype); //
			if (str_cols[i].equalsIgnoreCase("ID")) {
				sb_sql.append(str_cols[i] + " " + str_coltype); //
			} else {
				sb_sql.append(str_cols[i] + " " + str_coltype); //	
			}

			if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob") || str_coltype.equalsIgnoreCase("integer") || str_coltype.equalsIgnoreCase("DECIMAL")) {
			} else {
				sb_sql.append("(" + li_collength[i] + ")");
			}
			sb_sql.append(",\r\n"); //
		}
		sb_sql.append("constraint pk_" + _tabname + " primary key (id)\r\n");
		sb_sql.append(");\r\n\r\n");

		sb_sql.append("delete from pub_sequence where sename='S_" + _tabname.toUpperCase() + "';\r\n"); //
		sb_sql.append("insert into pub_sequence (sename,currvalue) values('S_" + _tabname.toUpperCase() + "',0);\r\n"); //
		sb_sql.append("commit;\r\n"); //
		return sb_sql.toString();
	}

	private String convertColType(String _oldtype, String _destdbtype) {
		String str_oldtype = _oldtype.toUpperCase(); // //
		_destdbtype = _destdbtype.toUpperCase(); // //
		if (str_oldtype.startsWith("NUMBER") || str_oldtype.startsWith("DECIMAL") || str_oldtype.startsWith("INTEGER")) { //
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "number"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "decimal"; //
			} else {
				return "decimal"; //
			}
		} else if (str_oldtype.startsWith("VARCHAR")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "varchar2"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "varchar"; //
			} else {
				return "varchar"; //
			}
		} else if (str_oldtype.startsWith("CHAR") || str_oldtype.startsWith("CHARACTER")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "character"; //
			} else {
				return "char"; //
			}
		} else if (str_oldtype.startsWith("TEXT") || str_oldtype.startsWith("CLOB")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "clob"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "clob"; //
			} else {
				return "text"; //
			}
		} else {
			return "varchar"; //
		}

	}

	/**
	 * ȡ��ĳ���������SQL
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 * @throws Exception
	 */
	public String getInserTableSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("����") ? false : true); //
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("\r\ntruncate table " + _tabname + ";\r\n\r\n"); //

		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=1"); //
		String[] str_keys = hvst.getHeaderName(); // ȡ�������ֶ���.
		String sequencecount = getCommDMO().getStringValueByDS(null, "select currvalue from pub_sequence where sename='S_" + _tabname.toUpperCase() + "'");
		int sequencecounts = Integer.parseInt(sequencecount);
		int[] str_type = new int[str_keys.length];
		str_type = hvst.getHeaderType();
		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // �����ֶ���
			String str_columvalues = ""; // �����ֶ�ֵ
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];
				if (str_keys[i].equalsIgnoreCase("ID") || str_type[i] == 3) { // ���������

					str_columvalues = str_columvalues + ++sequencecounts;
				} else {
					str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype); //
				}

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // �������
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
				}
			}

			if (_iswrap) { // �������
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		sb_sql.append("update pub_sequence set currvalue='" + sequencecounts + "' where sename='S_" + _tabname.toUpperCase() + "';\r\n");

		return sb_sql.toString();
	}

	/**
	 * ȡ��ĳ���������SQLȫ��
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 * @throws Exception
	 */
	public String getInserTableSQLAll(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("����") ? false : true); //
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("\r\ntruncate table " + _tabname + ";\r\n\r\n"); //

		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=1"); //
		String[] str_keys = hvst.getHeaderName(); // ȡ�������ֶ���.
		int[] str_type = new int[str_keys.length];
		str_type = hvst.getHeaderType();
		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // �����ֶ���
			String str_columvalues = ""; // �����ֶ�ֵ
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];

				str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype, str_type[i]); //

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // �������
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
				}
			}

			if (_iswrap) { // �������
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		return sb_sql.toString();
	}

	private String getInsertValue(String _value, String _dbtype) {
		String str_value = null;
		if (_value == null || _value.equals("")) {
			str_value = "null";
		} else {
			str_value = "'" + convert(_value, _dbtype) + "'";
		}
		return str_value;
	}

	/**
	 * ����_value��ȷ��Ҫ�����ֵ
	 * 
	 * @param _value
	 * @return
	 */
	private String getInsertValue(String _value, String _dbtype, int type) {
		String str_value = null;
		if (_value == null || _value.equals("")) {
			str_value = "null";
		} else if (type == 3) {
			str_value = "" + convert(_value, _dbtype) + "";
		} else {
			str_value = "'" + convert(_value, _dbtype) + "'";
		}
		return str_value;
	}

	private String convert(String _str, String _dbtype) {
		if (_str == null) {
			return "";
		}
		if (_dbtype.equalsIgnoreCase(WLTConstants.MYSQL)) {
			_str = tbUtil.replaceAll(_str, "\\", "\\\\"); //
			_str = tbUtil.replaceAll(_str, "'", "''"); //

		} else {
			_str = tbUtil.replaceAll(_str, "\\", "\\\\"); //
			_str = tbUtil.replaceAll(_str, "'", "''"); //
		}
		return _str; //
	}

	/**
	 * ȡ��һ��BillCellVO,��������һ��BillCellPanel
	 * 
	 * @param _templetCode
	 * @param _billNo
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getBillCellVO(String _templetCode, String _billNo, String _descr) throws Exception {
		String str_sql_1 = null; //
		if (_billNo == null) {
			str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetCode + "' and billno is null"; //
		} else {
			str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetCode + "' and billno ='" + _billNo + "' and descr='" + _descr + "'"; //
		}
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1.length < 0) {
			throw new WLTAppException("Cant' found BillCellTemplet[" + _templetCode + "," + _billNo + "]."); // 
		}

		BillCellVO cellVO = new BillCellVO(); // //...
		cellVO.setId(hvs_1[0].getStringValue("id")); //
		cellVO.setBillNo(hvs_1[0].getStringValue("billno")); //
		cellVO.setTempletcode(hvs_1[0].getStringValue("templetcode")); //
		cellVO.setTempletname(hvs_1[0].getStringValue("templetname")); //
		cellVO.setRowlength(hvs_1[0].getIntegerValue("rowlength").intValue()); //
		cellVO.setCollength(hvs_1[0].getIntegerValue("collength").intValue()); //
		cellVO.setDescr(hvs_1[0].getStringValue("descr")); // ��ע˵��....
		cellVO.setSeq(hvs_1[0].getStringValue("seq"));

		String str_sql_2 = "select * from pub_billcelltemplet_d where templet_h_id=" + cellVO.getId() + " order by cellrow,cellcol";
		HashVO[] hvs_2 = getCommDMO().getHashVoArrayByDS(null, str_sql_2); //
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[cellVO.getRowlength()][cellVO.getCollength()]; //
		for (int i = 0; i < hvs_2.length; i++) {
			int li_row = hvs_2[i].getIntegerValue("cellrow").intValue(); //
			int li_col = hvs_2[i].getIntegerValue("cellcol").intValue(); //

			cellItemVOs[li_row][li_col] = new BillCellItemVO(); // //
			cellItemVOs[li_row][li_col].setCellrow(li_row); // //
			cellItemVOs[li_row][li_col].setCellcol(li_col); // //
			cellItemVOs[li_row][li_col].setCellkey(hvs_2[i].getStringValue("cellkey")); // Ψһ��ʶ
			cellItemVOs[li_row][li_col].setCellvalue(hvs_2[i].getStringValue("cellvalue")); // ֵ
			cellItemVOs[li_row][li_col].setCellhelp(hvs_2[i].getStringValue("cellhelp")); // ����˵��
			cellItemVOs[li_row][li_col].setCelltype(hvs_2[i].getStringValue("celltype")); // ����
			cellItemVOs[li_row][li_col].setCelldesc(hvs_2[i].getStringValue("celldesc")); // ���Ӷ���..

			cellItemVOs[li_row][li_col].setIseditable(hvs_2[i].getStringValue("iseditable")); // �Ƿ�ɱ༭
			cellItemVOs[li_row][li_col].setIshtmlhref(hvs_2[i].getStringValue("ishtmlhref")); //�Ƿ�html����!

			cellItemVOs[li_row][li_col].setRowheight(hvs_2[i].getStringValue("rowheight")); // �и�
			cellItemVOs[li_row][li_col].setColwidth(hvs_2[i].getStringValue("colwidth")); // �п�

			cellItemVOs[li_row][li_col].setForeground(hvs_2[i].getStringValue("foreground")); // ǰ����ɫ
			cellItemVOs[li_row][li_col].setBackground(hvs_2[i].getStringValue("background")); // ����ɫ

			cellItemVOs[li_row][li_col].setLoadformula(hvs_2[i].getStringValue("loadformula")); // ���ع�ʽ
			cellItemVOs[li_row][li_col].setEditformula(hvs_2[i].getStringValue("editformula")); // �༭��ʽ
			cellItemVOs[li_row][li_col].setValidateformula(hvs_2[i].getStringValue("validateformula")); // У�鹫ʽ..
			cellItemVOs[li_row][li_col].setAvgformula(hvs_2[i].getStringValue("avgformula"));

			cellItemVOs[li_row][li_col].setFonttype(hvs_2[i].getStringValue("fonttype")); // ��������
			cellItemVOs[li_row][li_col].setFontstyle(hvs_2[i].getStringValue("fontstyle")); // ������
			cellItemVOs[li_row][li_col].setFontsize(hvs_2[i].getStringValue("fontsize")); // �����С

			Integer li_halign = hvs_2[i].getIntegerValue("halign"); // ��������
			Integer li_valign = hvs_2[i].getIntegerValue("valign"); // ��������

			cellItemVOs[li_row][li_col].setHalign(li_halign == null ? 1 : li_halign.intValue()); //
			cellItemVOs[li_row][li_col].setValign(li_valign == null ? 1 : li_valign.intValue()); //

			cellItemVOs[li_row][li_col].setSpan(hvs_2[i].getStringValue("span")); //
		}

		cellVO.setCellItemVOs(cellItemVOs); //
		return cellVO; //
	}

	/**
	 * ����BillCellVO
	 * 
	 * @param _datasourcename
	 * @param _cellvo
	 */
	public void saveBillCellVO(String _datasourcename, BillCellVO _cellvo) throws Exception {
		Vector v_sqls = new Vector(); //
		String str_parentid = _cellvo.getId(); //
		v_sqls.add("delete from pub_billcelltemplet_d where templet_h_id=" + str_parentid);
		v_sqls.add("delete from pub_billcelltemplet_h where id=" + str_parentid); //

		// ��������������
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_billcelltemplet_h"); //
		isql.putFieldValue("id", str_parentid); //
		isql.putFieldValue("templetcode", _cellvo.getTempletcode()); //
		isql.putFieldValue("templetname", _cellvo.getTempletname()); //
		isql.putFieldValue("rowlength", _cellvo.getRowlength()); //
		isql.putFieldValue("collength", _cellvo.getCollength()); //
		isql.putFieldValue("billno", _cellvo.getBillNo()); //
		isql.putFieldValue("seq", _cellvo.getSeq()); //
		isql.putFieldValue("descr", _cellvo.getDescr()); //

		v_sqls.add(isql.getSQL()); //�������SQL!
		BillCellItemVO[][] cellItemVOs = _cellvo.getCellItemVOs(); //�����ӱ�����!!

		//long ll_1 = System.currentTimeMillis(); //
		int li_batchNo = (_cellvo.getRowlength() * _cellvo.getCollength()) / 2; //�������ŵĴ���!!��Ϊ��¼̫��ʱ,��ʱ�Ǻ���������������,�����������ܵĹؼ�,��Ϊһ��1000�����ӵı��,������������Ҫ�������ݿ�50��,���������������Ҫ��ʱ��2��!!!
		if (li_batchNo < 20) {
			li_batchNo = 20;
		}
		for (int i = 0; i < _cellvo.getRowlength(); i++) {
			for (int j = 0; j < _cellvo.getCollength(); j++) {
				v_sqls.add(getBillCellTemplet_d_SQL(li_batchNo, str_parentid, i, j, cellItemVOs[i][j])); //
			}
		}
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("ƴ���ӱ�SQL��ʱ[" + (ll_2 - ll_1) + "]"); //����ʵ�����,����߼������ʱ�ĵط�,��һ��150*6������,��Ҫ2���,���Һ�ʱ�Ǻ�ʱ�Ǻ�������������,������������Ԥ������,����ڴ��������SQLֻ����20��������,Ҳ����99%��ʱ����������������,1%��ʱ��������ƴSQL,�ٴ�֤���������ݿ��Ǻ�ʱ�Ĳ���,�����ڴ��Ǽ����!

		//long ll_3 = System.currentTimeMillis(); //
		getCommDMO().executeBatchByDS(_datasourcename, v_sqls); // �ύ���ݿ�
		//long ll_4 = System.currentTimeMillis(); //
		//System.out.println("����[" + v_sqls.size() + "]��SQL�����ݿ��ʱ[" + (ll_4 - ll_3) + "]"); //��һ��150*6������,һ��921��SQL�ύ���ݿ��,��Ҫ1�����ҵ�ʱ��,һ����900��������!

	}

	/**
	 * 
	 * @param _row
	 * @param _col
	 * @param _value
	 * @return
	 */
	private String getBillCellTemplet_d_SQL(long _batchNo, String _parentID, int _row, int _col, BillCellItemVO _cellItemVO) throws Exception {
		String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_d", _batchNo); // ����д����,���ǳ������ظ�������
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_billcelltemplet_d"); //ǿ�ҽ���Ҫʹ��InsertSQLBuilder���󹹽�SQL,�������Լ�ʹ��StringBuffer��"ƴ"��,��Ϊ�����������������׳���,һ���ǿ�ֵ�Ĵ���,һ����Ĭ��ֵ�Ĵ���,һ���Ƕ��ŵĴ���,һ���ǵ����ŵĴ���!
		isql.putFieldValue("id", str_newid); //
		isql.putFieldValue("templet_h_id", _parentID); //
		isql.putFieldValue("cellrow", _row); //
		isql.putFieldValue("cellcol", _col); //
		isql.putFieldValue("cellkey", _cellItemVO.getCellkey()); //
		isql.putFieldValue("cellvalue", _cellItemVO.getCellvalue()); //
		isql.putFieldValue("cellhelp", _cellItemVO.getCellhelp()); //
		isql.putFieldValue("celltype", _cellItemVO.getCelltype(), "TEXT"); //
		isql.putFieldValue("celldesc", _cellItemVO.getCelldesc()); //
		isql.putFieldValue("iseditable", _cellItemVO.getIseditable(), "Y"); //�Ƿ�ɱ༭,Ϊnullʱ,Ĭ��ֵ��Y
		isql.putFieldValue("ishtmlhref", _cellItemVO.getIshtmlhref(), "N"); //�Ƿ��ǳ���,Ϊnullʱ��Ĭ��ֵ��N
		isql.putFieldValue("rowheight", _cellItemVO.getRowheight()); //
		isql.putFieldValue("colwidth", _cellItemVO.getColwidth()); //
		isql.putFieldValue("foreground", _cellItemVO.getForeground()); //
		isql.putFieldValue("background", _cellItemVO.getBackground()); //
		isql.putFieldValue("loadformula", _cellItemVO.getLoadformula()); //
		isql.putFieldValue("editformula", _cellItemVO.getEditformula()); //
		isql.putFieldValue("validateformula", _cellItemVO.getValidateformula()); //
		isql.putFieldValue("avgformula", _cellItemVO.getAvgformula()); //ƽ������ʽ,�߷�����ӵ�,Ӧ���Ժ��Ǽ���(����3��)�Զ�����,�����Ͳ�Ҫ��չ���ַ�ͨ�õ�������!!
		isql.putFieldValue("fonttype", _cellItemVO.getFonttype()); //
		isql.putFieldValue("fontstyle", _cellItemVO.getFontstyle()); //
		isql.putFieldValue("fontsize", _cellItemVO.getFontsize()); //
		isql.putFieldValue("halign", _cellItemVO.getHalign()); //�������о���/��/��?
		isql.putFieldValue("valign", _cellItemVO.getValign()); //
		isql.putFieldValue("span", _cellItemVO.getSpan()); //�ϲ��Ķ���,����Ҫ������!!!!!
		return isql.getSQL(); //
	}

	/**
	 * insert 10 demo records
	 * 
	 * @param _datasourcename
	 * @param _tablename
	 * @throws Exception
	 */
	public void createTenDemoRecords(String _datasourcename, String _tablename) throws Exception {
		String str_sql = "select * from v_pub_syscolumns where tabname='" + _tablename + "'";
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, str_sql);
		String[] str_sqls = new String[10];
		for (int i = 0; i < 10; i++) {
			String str_seq_Name = "s_" + _tablename;
			String seq_number = getCommDMO().getSequenceNextValByDS(_datasourcename, str_seq_Name);// ��ö�Ӧ���seq

			StringBuffer str_item = new StringBuffer();
			str_item.append("insert into ");
			str_item.append(_tablename);
			str_item.append("(");
			for (int j = 0; j < hvs.length; j++) {
				String type_str = hvs[j].getStringValue("datatype");
				str_item.append(hvs[j].getStringValue("colcode"));
				if (j != hvs.length - 1) {
					str_item.append(",");
				}
			}
			str_item.append(") values (");
			String str_values = null;

			for (int j = 0; j < hvs.length; j++) {
				String str_colcode = hvs[j].getStringValue("colcode"); //
				String type_str = hvs[j].getStringValue("datatype");

				if (str_colcode.equalsIgnoreCase("parentid")) { // �����ParentId�򲻼���
					str_item.append("null");
				} else {
					if (type_str.startsWith("decimal")) { // ����
						str_item.append(seq_number); //
					} else if (type_str.startsWith("char(1)")) { // ����
						str_item.append("'Y'"); //
					} else if (type_str.startsWith("datetime")) { // ʱ��
						str_item.append("getdate()");
					} else if (type_str.startsWith("varchar")) { // �ַ�
						String str_value = hvs[j].getStringValue("colcode") + "_" + seq_number;
						int li_collength = Integer.parseInt(type_str.substring(type_str.indexOf("(") + 1, type_str.length() - 1)); //
						if (str_value.length() >= li_collength) {
							str_value = str_value.substring(0, li_collength - 1); //
						}
						str_item.append("'" + str_value + "'"); //
					} else if (type_str.startsWith("text")) { // �ַ�
						String str_value = hvs[j].getStringValue("colcode") + "_" + seq_number;
						str_item.append("'" + str_value + "'"); //
					}
				}
				if (j != hvs.length - 1) {
					str_item.append(",");
				}
			}

			str_item.append(")");
			str_sqls[i] = str_item.toString();
			System.out.println(str_sqls[i]);
		}

		getCommDMO().executeBatchByDS(_datasourcename, str_sqls); //
	}

	/**
	 * ����һ��BillCell����
	 * 
	 * @param _templetcode
	 * @param _billNO
	 */
	public void copyBillCellData(String _templetcode, String _billNO, String _descr) throws Exception {
		String str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetcode + "' and billno is null"; // ���ҵ�ģ��
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1);
		if (hvs_1.length == 0) {
			throw new WLTAppException("Not Find BillCellTemplet[" + _templetcode + "]"); //
		}

		String str_oldparentid = hvs_1[0].getStringValue("id"); //

		Vector v_sqls = new Vector(); //
		String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_h"); //
		v_sqls.add("insert into pub_billcelltemplet_h (id,templetcode,templetname,billno,rowlength,collength,descr) select '" + str_newid + "',templetcode,templetname,'" + _billNO + "',rowlength,collength,'" + _descr + "' from pub_billcelltemplet_h where id=" + str_oldparentid);

		String str_sql_2 = "select * from pub_billcelltemplet_d where templet_h_id='" + str_oldparentid + "' order by cellrow,cellcol";
		HashVO[] hvs_2 = getCommDMO().getHashVoArrayByDS(null, str_sql_2);
		for (int i = 0; i < hvs_2.length; i++) {
			String str_oldchildid = hvs_2[i].getStringValue("id"); //
			String str_newchildid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_d"); //

			StringBuffer sb_sal_child = new StringBuffer(); //
			sb_sal_child.append("insert into pub_billcelltemplet_d"); //
			sb_sal_child.append("(");
			sb_sal_child.append("id,");
			sb_sal_child.append("templet_h_id,");
			sb_sal_child.append("cellrow,");
			sb_sal_child.append("cellcol,");
			sb_sal_child.append("cellkey,");
			sb_sal_child.append("cellvalue,");
			sb_sal_child.append("cellhelp,"); // ����˵��
			sb_sal_child.append("celltype,"); // Cell����
			sb_sal_child.append("celldesc,"); // Cell����
			sb_sal_child.append("iseditable,"); // �Ƿ�ɱ༭
			sb_sal_child.append("rowheight,");
			sb_sal_child.append("colwidth,");
			sb_sal_child.append("foreground,");
			sb_sal_child.append("background,");
			sb_sal_child.append("fonttype,");
			sb_sal_child.append("fontstyle,");
			sb_sal_child.append("fontsize,");
			sb_sal_child.append("span");
			sb_sal_child.append(")");
			sb_sal_child.append(" select ");
			sb_sal_child.append("'" + str_newchildid + "',");
			sb_sal_child.append("'" + str_newid + "',");
			sb_sal_child.append("cellrow,");
			sb_sal_child.append("cellcol,");
			sb_sal_child.append("cellkey,"); // cellkey
			sb_sal_child.append("cellvalue,");
			sb_sal_child.append("cellhelp,"); // ����˵��
			sb_sal_child.append("celltype,"); // Cell����
			sb_sal_child.append("celldesc,"); // Cell������
			sb_sal_child.append("iseditable,"); // �Ƿ�ɱ༭
			sb_sal_child.append("rowheight,");
			sb_sal_child.append("colwidth,");
			sb_sal_child.append("foreground,");
			sb_sal_child.append("background,");
			sb_sal_child.append("fonttype,");
			sb_sal_child.append("fontstyle,");
			sb_sal_child.append("fontsize,");
			sb_sal_child.append("span");
			sb_sal_child.append(" from pub_billcelltemplet_d where id='" + str_oldchildid + "'");
			v_sqls.add(sb_sal_child.toString()); //
		}

		// �ύ���ݿ�
		getCommDMO().executeBatchByDS(null, v_sqls); //
	}

	/**
	 * �滻SQL�еĵ�����,��Ϊ�����Żᵼ�±���ʧ��!!
	 * 
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return null;
		} else {
			return replaceAll(_value, "'", "''");
		}
	}

	/***************************************************************************
	 * ͨ��ģ���޸ı�
	 * 
	 * @param _datasourcename
	 * @param _tablename
	 * @param _template
	 */
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) {
		HashVO[] hashvo1 = null;
		HashVO[] hashvo2 = null;
		HashVO[] hashvo3 = null;

		boolean bool = false;
		try {
			for (int j = 0; j < _template.length; j++) {
				hashvo2 = new HashVO[_template.length];
				hashvo2 = getCommDMO().getHashVoArrayByDS(null, "select pub.tablename,item.itemkey,item.itemtype,item.issave from pub_templet_1_item item,pub_templet_1 pub " + "where item.pk_pub_templet_1=pub.pk_pub_templet_1 and pub.templetcode='" + _template[j] + "'");
				hashvo1 = new HashVO[_tablename.length];
				hashvo1 = getCommDMO().getHashVoArrayByDS(null, "select * from v_pub_syscolumns where tabname='" + _tablename[j] + "'");

				for (int i = 0; i < hashvo2.length; i++) {
					for (int k = 0; k < hashvo1.length; k++) {
						if (!hashvo2[i].getStringValue("itemkey").equals(hashvo1[k].getStringValue("colcode"))) {

							if (hashvo2[i].getStringValue("issave").equals("Y")) {
								// UIUtil.executeUpdateByDS(null, );
								bool = false;
							} else {
								bool = true;
							}
						} else {
							bool = true;
							break;
						}

					}
					if (bool != true) {
						if (hashvo2[i].getStringValue("itemtype").equals("�ı���")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(100)");
						} else if (hashvo2[i].getStringValue("itemtype").equals("���ֿ�")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " decimal");
						} else if (hashvo2[i].getStringValue("itemtype").equals("��ѡ��")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " char(1)");
						} else if (hashvo2[i].getStringValue("itemtype").equals("ʱ��")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " datetime");
						} else if (hashvo2[i].getStringValue("itemtype").equals("����")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " datetime");
						} else if (hashvo2[i].getStringValue("itemtype").equals("�����ı���")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(1000)");
						} else {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(100)");
						}

					}
					if (bool != true) {
						System.out.println("alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " " + hashvo2[i].getStringValue("itemtype") + "");
					}
				}

			}

		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/***************************************************************************
	 * ��BillCellPanel���ȡ���ݵķ���
	 * 
	 * @param templethid
	 * @param _billno
	 * 
	 */
	public HashVO[] getCellLoadDate(String templethid, String _billno) {
		HashVO[] hashvo = null;
		try {
			hashvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_billcelltemplet_data where billno='" + _billno + "' and templet_h_id='" + templethid + "'");
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();

			return null;
		}
		return hashvo;

	}

	/***************************************************************************
	 * ��BillCellPanel��洢���ݵķ���
	 * 
	 * @param templethid
	 * @param _billno
	 * 
	 */
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) {
		try {
			getCommDMO().executeUpdateByDS(null, "delete from pub_billcelltemplet_data where billno='" + _billno + "' and templet_h_id='" + templethid + "'");
			String[] sql = new String[hashmap.size()];
			String[] str_keys = (String[]) hashmap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_data"); // ����д����,���ǳ������ظ�������
				StringBuffer sb_sql = new StringBuffer(); //
				String value = (String) hashmap.get(str_keys[i]);
				value = (value == null ? "" : value);
				sb_sql.append("insert into pub_billcelltemplet_data");
				sb_sql.append("(");
				sb_sql.append("id,"); // id (null)
				sb_sql.append("templet_h_id,"); // templet_h_id (null)
				sb_sql.append("billno,"); //
				sb_sql.append("cellkey,"); // cellkey (null)
				sb_sql.append("cellvalue) values("); // cellvalue (null)
				sb_sql.append("" + str_newid + ",'" + templethid + "',"); //
				sb_sql.append("'" + _billno + "','" + str_keys[i] + "','" + convertSQLValue(value) + "')"); //
				sql[i] = sb_sql.toString();
				System.out.println(sql[i]);
			}
			getCommDMO().executeBatchByDS(null, sql); //
		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * @param string
	 * @param _dbtype
	 * @param _sqlviewtype
	 */
	public String getOneRegFormatPanelSQL(String _code, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("����") ? false : true); //
		StringBuffer sb_return = new StringBuffer(); //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from pub_regformatpanel where code='" + _code + "'"); //
		String[] str_keys = hvst.getHeaderName(); // ȡ�������ֶ���.

		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // �����ֶ���
			String str_columvalues = ""; // �����ֶ�ֵ
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];
				if (str_keys[i].equalsIgnoreCase("ID")) { // ���������
					str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_REGFORMATPANEL')"; //
				} else {
					str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype); //
				}

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // �������
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n"; //
				}
			}

			sb_return.append("delete from pub_regformatpanel where code='" + _code + "';\r\n"); // ��ɾ��֮
			sb_return.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_REGFORMATPANEL';\r\n");
			if (_iswrap) { // �������
				sb_return.append("insert into pub_regformatpanel (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_return.append("insert into pub_regformatpanel (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		return sb_return.toString();
	}

	/**
	 * �����б�ģ��SQL
	 * 
	 * @param _code
	 * @param _dbtype
	 * @param _iswrap
	 * @return
	 */
	public String getExportBillTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("delete pub_templet_1_item where pk_pub_templet_1 in " + "(select pk_pub_templet_1 from pub_templet_1 where templetcode = '" + _code + "');\r\n");
		sb_sql.append("delete pub_templet_1 where templetcode ='" + _code + "';\r\n");
		sb_sql.append("\r\n");

		HashVO[] hv_data = getCommDMO().getHashVoArrayByDS(null, "Select * From PUB_TEMPLET_1 Where templetcode ='" + _code + "'"); //
		if (hv_data.length <= 0) {
			return "";
		}

		String str_PK_PUB_TEMPLET_1 = hv_data[0].getStringValue("PK_PUB_TEMPLET_1"); //

		String[] str_keys = hv_data[0].getKeys(); // ȡ�������ֶ���.
		String str_columnnames = ""; // �����ֶ���
		String str_columvalues = ""; // �����ֶ�ֵ
		for (int i = 0; i < str_keys.length; i++) {
			str_columnnames = str_columnnames + str_keys[i];
			if (str_keys[i].equalsIgnoreCase("PK_PUB_TEMPLET_1")) { // ���������
				str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1')"; //
			} else {
				str_columvalues = str_columvalues + getInsertValue(hv_data[0].getStringValue(str_keys[i]), _dbtype); //
			}

			if (i != str_keys.length - 1) {
				str_columnnames = str_columnnames + ",";
				str_columvalues = str_columvalues + ",";
			}

			if (_iswrap) { // �������
				// str_columnnames = str_columnnames + "\r\n";
				str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
			}
		}

		sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_TEMPLET_1';\r\n");
		if (_iswrap) { // �������
			sb_sql.append("insert into pub_templet_1 (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
		} else {
			sb_sql.append("insert into pub_templet_1 (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
		}

		// ��ÿ��Item���к�����д���ļ�
		String _sql_item_context = "Select * From PUB_TEMPLET_1_ITEM Where PK_PUB_TEMPLET_1='" + str_PK_PUB_TEMPLET_1 + "'";
		HashVO[] hv_item = getCommDMO().getHashVoArrayByDS(null, _sql_item_context); //
		for (int i = 0; i < hv_item.length; i++) {
			String[] str_itemkeys = hv_item[i].getKeys(); // �õ���������
			String str_itemcolumnnames = ""; // �����ֶ���
			String str_iemcolumvalues = ""; // �����ֶ�ֵ
			for (int j = 0; j < str_itemkeys.length; j++) {
				str_itemcolumnnames = str_itemcolumnnames + str_itemkeys[j];
				if (str_itemkeys[j].equalsIgnoreCase("PK_PUB_TEMPLET_1_ITEM")) { // ���������
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1_ITEM')"; // ��������ȡ��..
				} else if (str_itemkeys[j].equalsIgnoreCase("PK_PUB_TEMPLET_1")) { // ����Ǹ���¼����
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1')"; // ��������ȡ��..
				} else {
					str_iemcolumvalues = str_iemcolumvalues + getInsertValue(hv_item[i].getStringValue(str_itemkeys[j]), _dbtype); //
				}

				if (j != str_itemkeys.length - 1) {
					str_itemcolumnnames = str_itemcolumnnames + ",";
					str_iemcolumvalues = str_iemcolumvalues + ",";
				}

				if (_iswrap) { // �������
					// str_itemcolumnnames = str_itemcolumnnames + "\r\n";
					str_iemcolumvalues = str_iemcolumvalues + " --" + str_itemkeys[j] + "\r\n";
				}
			}

			sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_TEMPLET_1_ITEM';\r\n");
			if (_iswrap) { // �������
				sb_sql.append("insert into pub_templet_1_item (" + str_itemcolumnnames + ")\r\nvalues\r\n(\r\n" + str_iemcolumvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into pub_templet_1_item (" + str_itemcolumnnames + ") values (" + str_iemcolumvalues + ");\r\n\r\n");
			}
		}

		return sb_sql.toString(); //
	}

	/**
	 * ת������
	 * 
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param _tables
	 * @param _iscreate
	 * @param _isinsert
	 * @return
	 */
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		// //
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // �����һ������PK��ͷ������Ϊ������
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // �����id�ֶ�����id�ֶ�
					break; //
				}
			}
		}

		//
		String str_destdbtype = getDBType(_destdsname); // Ŀ��������..
		if (_iscreate) {
			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("create table " + _table + " (");
			for (int i = 0; i < str_coltypes.length; i++) {
				String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
				if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
					sb_sql.append(str_cols[i] + " " + "clob"); //
				} else {
					sb_sql.append(str_cols[i] + " " + str_coltype); //
					if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
					} else {

						sb_sql.append("(" + li_collength[i] + ")");

					}
				}
				if (i == str_coltypes.length - 1) { // ��������һ��
					if (pk_fieldname != null) {
						sb_sql.append(","); // �������������Ӷ���..
					}
				} else {
					sb_sql.append(","); //
				}
			}

			if (pk_fieldname != null) {
				if (_table.length() < 25) {
					sb_sql.append("constraint pk_" + _table + " primary key (" + pk_fieldname + ")"); //
				} else {
					sb_sql.append("primary key (" + pk_fieldname + ")"); //
				}
			}
			sb_sql.append(")");

			getCommDMO().executeUpdateByDSImmediately(_destdsname, sb_sql.toString()); // ��Ŀ���������ִ��
		}

		int li_insertrecords = 0; //
		if (_isinsert) { // �����Ҫ��������
			int li_batchrecords = 1; //
			HashVO[] hvs_count = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select count(*) c1 from " + _table); // //
			int li_recordcount = hvs_count[0].getIntegerValue("c1").intValue(); //
			if (li_recordcount > 0) {
				if (pk_fieldname == null) { // �������Ϊ��
					li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // ���û������,ֱ�Ӳ�����������
				} else {
					HashVO[] hvs_min = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select min(" + pk_fieldname + ") minc1 from " + _table); //
					Integer li_MinID = hvs_min[0].getIntegerValue("minc1"); //
					if (li_MinID == null) { // �������ֵΪ��
						li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // ���û������,ֱ�Ӳ�����������
					} else {
						int li_minid = li_MinID.intValue(); //
						HashVO[] hvs_max = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select max(" + pk_fieldname + ") maxc1 from " + _table); //
						int li_maxid = hvs_max[0].getIntegerValue("maxc1").intValue(); //

						int li_begin = li_minid;
						int li_end = li_begin + li_batchrecords; //
						ArrayList v_sqls = null; //
						while (li_begin <= li_maxid) {
							HashVO[] hvs_record = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select * from " + _table + " where " + pk_fieldname + " between '" + li_begin + "' and '" + li_end + "'"); //
							li_insertrecords = li_insertrecords + hvs_record.length; // //
							v_sqls = new ArrayList(); //
							for (int r = 0; r < hvs_record.length; r++) {
								String str_columnnames = ""; // �����ֶ���
								String str_columvalues = ""; // �����ֶ�ֵ
								for (int i = 0; i < str_cols.length; i++) {
									str_columnnames = str_columnnames + str_cols[i]; //
									str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
									if (i != str_cols.length - 1) { //
										str_columnnames = str_columnnames + ",";
										str_columvalues = str_columvalues + ",";
									}
								}
								v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ")"); //
							}
							getCommDMO().executeBatchByDSImmediately(_destdsname, v_sqls); // ��������..
							li_begin = li_end + 1;
							li_end = li_begin + li_batchrecords; //
						}
					}
				}
			}
		}
		return li_insertrecords;
	}

	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls) throws Exception {
		return getXMlFromTableRecords(_sourcedsname, _sqls, null, null, null); //
	}

	/**
	 * ����SQL�����ݵ�����XML,��Ϊ��֮���й�ϵ,������ǰ�ķ�����ͨ��!! ���������������ͨ�õ�!! Ҳ�����Ժ󵼳�������Ҫ�ķ���!!
	 * @param _sourcedsname
	 * @param _sqls
	 * @param _pkConstraint ����Լ������, N��3��,��1���Ǳ���,��2���������ֶ���,��3����������
	 * @param _foreignPKConstraint ���Լ������,N��2��,��һ���Ǳ���.�ֶ���,��2���Ƕ�Ӧ���ű���ĸ��ֶ�!!
	 * @param _xmlFile���ָ����xml�ļ�·����ֱ�ӷ��ش�xml����
	 * @return
	 * @throws Exception
	 */
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls, String[][] _pkConstraint, String[][] _foreignPKConstraint, String _xmlFile) throws Exception {
		if (_xmlFile != null) {
			InputStream fileInStream = this.getClass().getResourceAsStream(_xmlFile); //
			if (fileInStream != null) { //������ڴ��ļ�!
				return new String(getTBUtil().readFromInputStreamToBytes(fileInStream), "UTF-8");
			}
		}

		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < _sqls.length; i++) {
			HashVOStruct hvst = new CommDMO().getHashVoStructByDS(_sourcedsname, _sqls[i]); //ȡ�ýṹ!!
			String str_tabname = hvst.getTableName(); //����
			String[] str_keys = hvst.getHeaderName(); //�ֶ���
			HashVO[] hvs = hvst.getHashVOs(); //���м�¼
			String str_itemValue = null; //
			for (int j = 0; j < hvs.length; j++) {
				sb_xml.append("<record tabname=\"" + str_tabname + "\" pkname=\"" + getPKOrSEName(_pkConstraint, str_tabname, 1) + "\" sename=\"" + getPKOrSEName(_pkConstraint, str_tabname, 2) + "\">\r\n"); //ͬʱָ������,�����ֶ���,������
				for (int k = 0; k < str_keys.length; k++) { //
					str_itemValue = hvs[j].getStringValue(str_keys[k], ""); //
					if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //��������<>��&
						str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
					}
					sb_xml.append("  <col name=\"" + str_keys[k] + "\"" + getForeignPKRef(_foreignPKConstraint, str_tabname, str_keys[k]) + ">" + str_itemValue + "</col>\r\n"); //ȡ���е�ֵ! ������������,��Ҫָ�����!!!
				}
				sb_xml.append("</record>\r\n"); //
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * ��ȡ����ģ��
	 * @param templetcode
	 * @param tablename
	 * @return
	 * @throws Exception
	 */
	public List<Object> getAllTemplate(String templetcode, String tablename, String type) throws Exception {
		List<Object> rtn = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(" select pk_pub_templet_1,templetcode,templetname,tablename,savedtablename,datapolicy,'���ݿ�' savetype from pub_templet_1 where 1=1 ");
		if (templetcode != null && !"".equals(templetcode.trim())) {
			sb.append(" and (lower(templetcode) like '%" + templetcode.trim().toLowerCase() + "%' " + " or lower(templetname) like '%" + templetcode.trim().toLowerCase() + "%' ) ");
		}
		if (tablename != null && !"".equals(tablename.trim())) {
			sb.append(" and (lower(savedtablename) like '%" + tablename.trim().toLowerCase() + "%' " + " or lower(tablename) like '%" + tablename.trim().toLowerCase() + "%' ) ");
		}
		sb.append(" order by templetcode asc  ");
		HashVO[] t_db = null;
		HashVO[] all = null;
		List<Object> t_xml = null;
		if ("DB".equals(type)) {
			t_db = getCommDMO().getHashVoArrayByDS(null, sb.toString());
			rtn.add(t_db);
			rtn.add("");
		} else if ("XML".equals(type)) {
			t_xml = getAllXmlTemplet_(templetcode, tablename);
			rtn.add((HashVO[]) t_xml.get(0));
			rtn.add(t_xml.get(1));
		} else {
			t_db = getCommDMO().getHashVoArrayByDS(null, sb.toString());
			t_xml = getAllXmlTemplet_(templetcode, tablename);
			all = (HashVO[]) ArrayUtils.addAll(t_db, (HashVO[]) t_xml.get(0));
			Arrays.sort(all, new HashVOComparator(new String[][] { { "templetcode", "N", "N" } }));
			rtn.add(all);
			rtn.add(t_xml.get(1));
		}
		return rtn;
	}

	public List<Object> getAllXmlTemplet_(String templetcode, String tablename) throws Exception {
		List<Object> rtn = new ArrayList<Object>();
		List all = new ArrayList();
		DefaultTMO tmp_o = null;
		HashVO tmp = null;
		String platdir = "cn/com/infostrategy/bs/sysapp/install/templetdata/"; //
		List<String[]> other_dir = new ArrayList<String[]>();
		other_dir.add(new String[] { platdir, "ƽ̨" });
		String str_installs = ServerEnvironment.getProperty("INSTALLAPPS");
		if (str_installs != null && !str_installs.trim().equals("")) {
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_installs, ";");
			for (int i = 0; i < str_items.length; i++) {
				int li_pos = str_items[i].indexOf("-"); //
				String str_package = null; //
				String str_packdescr = null; //
				if (li_pos > 0) {
					str_package = str_items[i].substring(0, li_pos);
					str_packdescr = str_items[i].substring(li_pos + 1, str_items[i].length());
				} else {
					str_package = str_items[i];
					str_packdescr = "δ����";
				}
				str_package = tbUtil.replaceAll(str_package, ".", "/");
				str_package = str_package + "/templetdata/";
				other_dir.add(new String[] { str_package, str_packdescr });
			}
		}
		//�ҵ����ģ���ļ��İ���Ϳ�ʼ����
		URL url = null;
		String protocol = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < other_dir.size(); i++) {
			ClassLoader l = Thread.currentThread().getContextClassLoader();
			url = l.getResource(other_dir.get(i)[0]);//����ط����źܾã������Ǵ�jar��ʱ��������
			//			if(url == null) {//��ʼ��ôд����Ϊ���ǵò������Ǵ��jar���İ�����Ϊ��classloader�����⣬��ʵ���ǣ������Ǵ�jar��������
			//				while(true) {
			//					l = l.getParent();
			//					if(l == null) {
			//						break;
			//					}
			//					url = l.getResource(other_dir.get(i)[0]);
			//					if(url != null) {
			//						break;
			//					}
			//				}
			//			}
			if (url == null) {
				if (sb.length() <= 0) {
					sb.append(other_dir.get(i)[1]);
				} else {
					sb.append("��" + other_dir.get(i)[1]);
				}
				continue;
			}
			protocol = url.getProtocol();
			try {
				if ("jar".equals(protocol)) {
					JarURLConnection con = (JarURLConnection) url.openConnection();
					JarFile file = con.getJarFile();
					Enumeration enu = file.entries();
					String className = "";
					String entryName = "";
					while (enu.hasMoreElements()) {
						JarEntry element = (JarEntry) enu.nextElement();
						entryName = element.getName();
						className = entryName.substring(entryName.lastIndexOf("/") + 1);
						if (!className.equals("") && entryName.equals(other_dir.get(i)[0] + className)) {
							if (className.endsWith(".xml") || className.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && className.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
								tmp_o = getDefaultTMOByCode(entryName, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", className.substring(0, className.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml�в�ѯ��Ҫ��ʾ���ԡ����/2016-06-15��
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + className);
								all.add(tmp);
							}
						}
					}
				} else if ("zip".equals(protocol)) { //weblogic����������zip ���/2013-12-30
					String str_url = url.getPath();
					if (str_url.lastIndexOf("!") > -1) {
						str_url = str_url.substring(0, str_url.lastIndexOf("!"));
					}
					ZipFile file = new ZipFile(str_url);
					Enumeration enu = file.entries();
					String className = "";
					String entryName = "";
					while (enu.hasMoreElements()) {
						ZipEntry element = (ZipEntry) enu.nextElement();
						entryName = element.getName();
						className = entryName.substring(entryName.lastIndexOf("/") + 1);
						if (!className.equals("") && entryName.equals(other_dir.get(i)[0] + className)) {
							if (className.endsWith(".xml") || className.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && className.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
								tmp_o = getDefaultTMOByCode(entryName, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", className.substring(0, className.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml�в�ѯ��Ҫ��ʾ���ԡ����/2016-06-15��
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + className);
								all.add(tmp);
							}
						}
					}
				} else {
					File file = new File(new URI(url.toExternalForm()));
					File[] files = file.listFiles();
					String name = null;
					for (int j = 0; j < files.length; j++) {
						if (!files[j].isDirectory()) {
							name = files[j].getName();
							if (name.endsWith(".xml") || name.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && name.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
								tmp_o = getDefaultTMOByCode(other_dir.get(i)[0] + name, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", name.substring(0, name.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml�в�ѯ��Ҫ��ʾ���ԡ����/2016-06-15��
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + name);
								all.add(tmp);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		rtn.add((HashVO[]) all.toArray(new HashVO[0]));
		if (sb.length() > 0) {
			rtn.add("δȡ��" + sb + "XMLģ��,����Ϊ�ڴ�jar��ʱδ��ѡ��add directory entries��!");
		} else {
			rtn.add(sb);
		}

		return rtn;
	}

	/**
	 * ��XML�е����ݵ��뵽���ݿ���!! �÷���������ķ�����һ��! Ҳ�����Ժ�������ݵ���,����,Ǩ�Ƶ�����Ҫ�ķ���!! ��һ�ж��ǻ�������������! XML���ݸ�ʽҲ������,ƽ�̵�!
	 * @return
	 * @throws Exception
	 */
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId) throws Exception {
		return this.importRecordsXMLToTable(_xmlFileNams, _dsName, _isReCreateId, null);
	}

	/**
	 * param����ָ��һЩ�ֶε�����ֵ,��ʽΪ:����_�ֶ���,ֵ
	 * @param _xmlFileNams
	 * @param _dsName
	 * @param _isReCreateId
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId, HashMap param) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		CommDMO commDMO = new CommDMO();
		HashMap targetTaColsMap = new HashMap(); //Ϊ�˱�֤�������ڷ����ֶβ�һ�µ����Ҳ�ܲ���!! ��������Ҫ�൱��׳! ����Ҫ֪��Ŀ����е�ĳ�ű��ʵ������!!
		HashMap pkValueMap = new HashMap(); //��¼ĳ�����ĳ���ɵ�id��Ӧ���µ�ֵ!!! key�ǡ�����.����.��ֵ��,value����ֵ
		for (int i = 0; i < _xmlFileNams.length; i++) { //���������ļ�!!
			InputStream fileInStream = this.getClass().getResourceAsStream(_xmlFileNams[i]); //�ļ���
			if (fileInStream == null) {
				throw new WLTAppException("�ļ�[" + _xmlFileNams[i] + "]������!"); //
			}
			org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(fileInStream); // ����XML!!
			java.util.List recordList = doc.getRootElement().getChildren("record"); //ȡ�����е�record�ֶ�!!
			for (int j = 0; j < recordList.size(); j++) { //����������¼!!
				org.jdom.Element recordNode = (org.jdom.Element) recordList.get(j); //
				String str_tabName = recordNode.getAttributeValue("tabname"); //����!
				String str_pkName = recordNode.getAttributeValue("pkname"); //�����ֶ���!
				String str_seName = recordNode.getAttributeValue("sename"); //�����ֶζ�Ӧ��������!����Ϊ��

				List targetTabColList = null; //
				if (!targetTaColsMap.containsKey(str_tabName.toUpperCase())) { //���û��,���DBȡ
					String[] str_taregtTableCols = commDMO.getTableDataStructByDS(_dsName, "select * from " + str_tabName + " where 1=2").getHeaderName(); //����!
					for (int p = 0; p < str_taregtTableCols.length; p++) {
						str_taregtTableCols[p] = str_taregtTableCols[p].toUpperCase(); //Ϊ�˾�׼,��ת���ɴ�д!!!
					}
					targetTabColList = Arrays.asList(str_taregtTableCols); //
					targetTaColsMap.put(str_tabName.toUpperCase(), targetTabColList); //
				} else { //�����,��ȡ����
					targetTabColList = (List) targetTaColsMap.get(str_tabName.toUpperCase()); ////
				}

				InsertSQLBuilder isql = new InsertSQLBuilder(str_tabName); //����SQL������!!
				java.util.List colList = recordNode.getChildren(); //ȡ�ø�����!
				for (int k = 0; k < colList.size(); k++) { //����������!!!
					org.jdom.Element colNode = (org.jdom.Element) colList.get(k); //ĳһ����!!
					String str_colName = colNode.getAttributeValue("name"); //����!!!
					String str_refTabid = colNode.getAttributeValue("reftabid"); //��Ӧ�����
					String str_colValue = colNode.getValue(); //valueֵ!!

					if (targetTabColList.contains(str_colName.toUpperCase())) { //����Ŀ����о��и��ֶ��Ҳż���ƴ��SQL!
						if (param != null) {//�˴�����ָ��ĳЩ�ֶ�Ϊ����ֵ�����縴��ģ������õ�
							if (param.containsKey((str_tabName + "_" + str_colName).toUpperCase())) {
								isql.putFieldValue(str_colName, (String) param.get((str_tabName + "_" + str_colName).toUpperCase()));
								continue;
							} else if (param.containsKey((str_tabName + "_" + str_colName).toLowerCase())) {
								isql.putFieldValue(str_colName, (String) param.get((str_tabName + "_" + str_colName).toLowerCase()));
								continue;
							}
						}
						if (_isReCreateId) { //�����������������!!!
							if (str_colName.equalsIgnoreCase(str_pkName)) { //���������!
								String str_newValue = commDMO.getSequenceNextValByDS(_dsName, str_seName.toUpperCase()); //������ȡ���µ�����ֵ!!!
								isql.putFieldValue(str_colName, str_newValue); //������ֵ!!
								String str_pkMapKey = (str_tabName + "." + str_colName + "." + str_colValue).toUpperCase(); //
								pkValueMap.put(str_pkMapKey, str_newValue); //
							} else { //�����������
								if (str_refTabid != null) { //��������!!! �����鷳!!! ����ȥȡһ��!!
									String str_pkMapKey = (str_refTabid + "." + str_colValue).toUpperCase(); //key
									String str_convertValue = (String) pkValueMap.get(str_pkMapKey); //�ڻ������ҵ���Ӧ��ֵ
									isql.putFieldValue(str_colName, str_convertValue); //����ֵ!!
								} else { //����������,Ҳ�������!!
									isql.putFieldValue(str_colName, str_colValue); //����ֵ!!
								}
							}
						} else {
							isql.putFieldValue(str_colName, str_colValue); //����ֵ!!
						}
					}
				}

				al_sqls.add(isql.getSQL()); //�����б�
			}
		}
		commDMO.executeBatchByDS(_dsName, al_sqls); //ʵ�ʲ������ݿ�!!
		return "�ɹ�"; //
	}

	private String getPKOrSEName(String[][] _pkConstraint, String _tableName, int _type) {
		if (_pkConstraint == null || _pkConstraint.length == 0) {
			return "";
		}
		for (int i = 0; i < _pkConstraint.length; i++) {
			if (_pkConstraint[i][0].equalsIgnoreCase(_tableName)) { //���ƥ����!!
				return _pkConstraint[i][_type]; //
			}
		}
		return "";
	}

	private String getForeignPKRef(String[][] _foreignPKConstraint, String _tabName, String _colName) {
		if (_foreignPKConstraint == null || _foreignPKConstraint.length == 0) { //���û����,��ֱ�ӷ���!!!
			return "";
		}
		String str_key = _tabName + "." + _colName; //������ı������ֶ�����ƴ����!!��Ϊ����!!
		for (int i = 0; i < _foreignPKConstraint.length; i++) { //�����������Լ��!!!
			if (_foreignPKConstraint[i][0].equalsIgnoreCase(str_key)) {
				return " " + "reftabid=\"" + _foreignPKConstraint[i][1] + "\""; //
			}
		}
		return "";
	}

	/**
	 * ��һ�ű���ȡ��1000����¼! �Ժ�Ҫ��֤����һ�ű��ܵ���XML,Ϊ�˱�֤�ڴ治���,����һǧ��һǧ���ĵ�!
	 * ���ͻ��������������ÿһǧ�β�ѯһ��!
	 * @param _dsName
	 * @param _tableName
	 * @param _pkName
	 * @return
	 * @throws Exception
	 */
	public HashMap getXMlFromTable1000Records(String _dsName, String _tableName, String _pkName, int _beginNo) throws Exception {
		return getXMLMapDataBySQL(_dsName, _tableName, _pkName, _beginNo); //
	}

	//ȡ����
	private HashMap getXMLMapDataBySQL(String _dsName, String _tabName, String _pkName, int _beginNo) throws Exception {
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		HashVOStruct hvst = null;
		if (_pkName == null) { //�������Ϊ��
			hvst = new CommDMO().getHashVoStructByDS(_dsName, "select * from " + _tabName); //ȡ�����м�¼!!!
		} else {
			hvst = new CommDMO().getHashVoStructByDS(_dsName, "select * from " + _tabName + " where " + _pkName + ">" + _beginNo + " order by " + _pkName, 500); //ȡ��ǰ500����¼!!
		}
		String[] str_keys = hvst.getHeaderName(); //����
		HashVO[] hvs = hvst.getHashVOs(); //
		if (hvs == null || hvs.length <= 0) { //���û�ҵ�����,�򷵻ؿ�!!!
			return null; //
		}
		String str_itemValue = null; //
		int li_realEndPKValue = -1; //������¼������ֵ!!
		int li_returnRecordCount = 0; //
		for (int i = 0; i < hvs.length; i++) { //��������!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { //��������!!
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); //ȡ��ֵ!!
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //��������<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); ////
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
			li_returnRecordCount++; //

			if (_pkName != null && i == hvs.length - 1) { //��������һ��!!
				li_realEndPKValue = hvs[i].getIntegerValue(_pkName); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		HashMap returnMap = new HashMap(); //
		returnMap.put("��¼��", new Integer(li_returnRecordCount)); //
		returnMap.put("������", new Integer(li_realEndPKValue)); //
		returnMap.put("����", sb_xml.toString()); //
		return returnMap; //�����µ�idֵ!!
	}

	//��һ�ű������ȫ������Ȼ���������ĳ��������ֵ,����1��ʼ,��������õ�Ǩ�ƻ򵼳�����ʱ�õ�!!!
	public String getXMLDataBySQLAsResetId(String _dsName, String _sql, String _tabName, String _resetIdField) throws Exception {
		HashVOStruct hvst = new CommDMO().getHashVoStructByDS(_dsName, _sql); //ȡ�����м�¼!!!
		String[] str_keys = hvst.getHeaderName(); //����
		HashVO[] hvs = hvst.getHashVOs(); //
		if (hvs == null || hvs.length <= 0) { //���û�ҵ�����,�򷵻ؿ�!!!
			return null; //
		}
		String str_itemValue = null; //
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < hvs.length; i++) { //��������!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { //��������!!
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); //ȡ��ֵ!!
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //��������<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				if (_resetIdField != null && str_keys[j].equalsIgnoreCase(_resetIdField)) { //��������Ҫ�������õ��ֶ���
					sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + (i + 1) + "</col>\r\n"); ////
				} else {
					sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); ////
				}
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	//����1000����¼!
	public void importXmlToTable1000Records(String _dsName, String _fileName, String _xml) throws Exception {
		_xml = _xml.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
		java.io.ByteArrayInputStream bins = new java.io.ByteArrayInputStream(_xml.getBytes("GBK")); //
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document rootNode = null; //
		try {
			rootNode = builder.build(bins); //
		} catch (Exception ex) {
			System.err.println("��ȡxml�ļ�[" + _fileName + "]�����쳣:"); //
			ex.printStackTrace(); //
			throw ex; //
		}
		bins.close(); //�ر�!!
		java.util.List recordNodeList = rootNode.getRootElement().getChildren("record"); //�������м�¼!

		HashSet hstCols = new HashSet(); //
		ArrayList al_sqls = new ArrayList(); //

		for (int i = 0; i < recordNodeList.size(); i++) { //����������¼!!!
			org.jdom.Element recordNode = (org.jdom.Element) recordNodeList.get(i);
			String str_tableName = recordNode.getAttributeValue("tabname"); //����
			if (i == 0) { //��¼��Ŀ���Ľṹ,������������ֻ����Ŀ����д��ڵ���!
				TableDataStruct tabstrct = getCommDMO().getTableDataStructByDS(_dsName, "select * from " + str_tableName + " where 1=2"); //
				String[] str_cols = tabstrct.getHeaderName(); //
				for (int k = 0; k < str_cols.length; k++) {
					hstCols.add(str_cols[k].toLowerCase()); //
				}
			}
			//System.out.println("����[" + str_tableName + "]"); ////
			InsertSQLBuilder isql = new InsertSQLBuilder(str_tableName); //
			java.util.List colNodeList = recordNode.getChildren("col"); //
			for (int j = 0; j < colNodeList.size(); j++) { //��������Col�ӽ��!!������!!
				org.jdom.Element colNode = (org.jdom.Element) colNodeList.get(j);
				String str_colName = colNode.getAttributeValue("name"); //����!
				String str_colValue = colNode.getText(); //��ֵ!
				if (hstCols.contains(str_colName.toLowerCase())) { //���Ŀ�����ʵ�ʰ�������,��Ž��в���!! �Ӷ���֤�ṹ��������ʱ���ܵ���!!
					isql.putFieldValue(str_colName, str_colValue); //����ֵ!!�е����Żᴦ��!
				}
			}
			al_sqls.add(isql.getSQL()); //����������¼
		}
		getCommDMO().executeBatchByDS(_dsName, al_sqls, false); //��Ŀ���������������500����¼!!!
	}

	public ArrayList getExportSql(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		//StringBuffer returnStr = new StringBuffer();
		ArrayList v_sqls = new ArrayList(); //
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		// //
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // �����һ������PK��ͷ������Ϊ������
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // �����id�ֶ�����id�ֶ�
					break; //
				}
			}
		}

		//
		String str_destdbtype = getDBType(_destdsname); // Ŀ��������..
		if (_iscreate) {
			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("create table " + _table + " (");
			for (int i = 0; i < str_coltypes.length; i++) {
				String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
				if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
					sb_sql.append(str_cols[i] + " " + "clob"); //
				} else {
					sb_sql.append(str_cols[i] + " " + str_coltype); //
					if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
					} else {

						sb_sql.append("(" + li_collength[i] + ")");

					}
				}
				if (i == str_coltypes.length - 1) { // ��������һ��
					if (pk_fieldname != null) {
						sb_sql.append(","); // �������������Ӷ���..
					}
				} else {
					sb_sql.append(","); //
				}
			}

			if (pk_fieldname != null) {
				if (_table.length() < 25) {
					sb_sql.append("constraint pk_" + _table + " primary key (" + pk_fieldname + ")"); //
				} else {
					sb_sql.append("primary key (" + pk_fieldname + ")"); //
				}
			}
			sb_sql.append(");\r\n");
			//returnStr.append(sb_sql.toString());
			v_sqls.add(sb_sql.toString());
		}

		int li_insertrecords = 0; //
		if (_isinsert) { // �����Ҫ��������
			int li_batchrecords = 1; //
			HashVO[] hvs_count = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select count(*) c1 from " + _table); // //
			int li_recordcount = hvs_count[0].getIntegerValue("c1").intValue(); //
			if (li_recordcount > 0) {
				if (pk_fieldname == null) { // �������Ϊ��
					li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // ���û������,ֱ�Ӳ�����������
				} else {
					HashVO[] hvs_min = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select min(" + pk_fieldname + ") minc1 from " + _table); //
					Integer li_MinID = hvs_min[0].getIntegerValue("minc1"); //
					if (li_MinID == null) { // �������ֵΪ��
						li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // ���û������,ֱ�Ӳ�����������
					} else {
						int li_minid = li_MinID.intValue(); //
						HashVO[] hvs_max = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select max(" + pk_fieldname + ") maxc1 from " + _table); //
						int li_maxid = hvs_max[0].getIntegerValue("maxc1").intValue(); //

						int li_begin = li_minid;
						int li_end = li_begin + li_batchrecords; //

						while (li_begin <= li_maxid) {
							HashVO[] hvs_record = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select * from " + _table + " where " + pk_fieldname + " between '" + li_begin + "' and '" + li_end + "'"); //
							li_insertrecords = li_insertrecords + hvs_record.length; // //

							for (int r = 0; r < hvs_record.length; r++) {
								String str_columnnames = ""; // �����ֶ���
								String str_columvalues = ""; // �����ֶ�ֵ
								for (int i = 0; i < str_cols.length; i++) {
									str_columnnames = str_columnnames + str_cols[i]; //
									str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
									if (i != str_cols.length - 1) { //
										str_columnnames = str_columnnames + ",";
										str_columvalues = str_columvalues + ",";
									}
								}
								//returnStr.append("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n");
								v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ");"); //
							}
							li_begin = li_end + 1;
							li_end = li_begin + li_batchrecords; //
						}
					}
				}
			}
		}
		return v_sqls;
	}

	/**
	 * ��Դ����Ŀ��������������...
	 * 
	 * @param str_cols
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param str_destdbtype
	 * @param _table
	 * @throws Exception
	 */
	private int insertAllData(String _sourcedsname, String _destdsname, String str_destdbtype, String _table) throws Exception {
		HashVOStruct hvstruct = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table); //
		String[] str_cols = hvstruct.getHeaderName(); //
		HashVO[] hvs_record = hvstruct.getHashVOs(); //

		ArrayList v_sqls = new ArrayList(); //
		for (int r = 0; r < hvs_record.length; r++) {
			String str_columnnames = ""; // �����ֶ���
			String str_columvalues = ""; // �����ֶ�ֵ
			for (int i = 0; i < str_cols.length; i++) {
				str_columnnames = str_columnnames + str_cols[i]; //
				str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
				if (i != str_cols.length - 1) { //
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}
			}
			v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ")"); //
		}
		getCommDMO().executeBatchByDSImmediately(_destdsname, v_sqls); // ��������..
		return hvs_record.length; //
	}

	private String getDBType(String _dsname) {
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_dsname); //
		return dsVO.getDbtype(); //
	}

	/**
	 * ����ͼ����
	 * 
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param _viewname
	 * @return
	 */
	public Object transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception {
		String str_sourcedbtype = getDBType(_sourcedsname); // Դ������..
		if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.SQLSERVER)) { // SQLServer
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				String str_viewdefine = hvs[0].getStringValue("view_definition"); //
				if (str_viewdefine != null) { //
					createView(_destdsname, str_viewdefine); //
				} else {
					throw new WLTAppException("��ͼ����Ϊ��"); //
				}
			} else {
				throw new WLTAppException("û���ҵ���ͼ����"); //
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.ORACLE)) { // Oracle
			String define = commDMO.getStringValueByDS(_sourcedsname, "select text from user_views where view_name = '" + _viewname.toUpperCase() + "'");
			if (define == null || define.equals("")) {
				throw new WLTAppException("û���ҵ���ͼ����"); //
			} else {
				createViewFromOracle(_destdsname, define, _viewname);
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.MYSQL)) { // Mysql
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition,table_schema from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				String str_viewdefine = hvs[0].getStringValue("view_definition"); //
				String table_schema = hvs[0].getStringValue("table_schema"); //
				if (str_viewdefine != null && !"".equals(str_viewdefine)) { //
					createViewMySQL(_destdsname, str_viewdefine, _viewname, table_schema); //
				} else {
					throw new WLTAppException("��ͼ����Ϊ��"); //
				}
			} else {
				//������ط��������һ��ͨ�÷������Ͳ�Ʒ��װԭ��һ������views.xml�ж�ȡ��ͼddl
				throw new WLTAppException("û���ҵ���ͼ����"); //
			}
		}
		return null;
	}

	//��oracle��ȡ����ͼ�ṹ
	private void createViewFromOracle(String _destdsname, String _define, String _viewName) throws Exception {
		StringBuffer viewsb = new StringBuffer();
		viewsb.append(" create or replace view " + _viewName + " as \r\n");
		viewsb.append(_define);
		getCommDMO().executeBatchByDSImmediately(_destdsname, new String[] { viewsb.toString() });
	}

	/**
	 * ������ͼ..
	 */
	private void createView(String _destdsname, String _define) throws Exception {
		String str_destdbtype = getDBType(_destdsname); // Ŀ�������
		_define = tbUtil.replaceAll(_define, "dbo.", ""); //
		String[] str_defineitems = tbUtil.split(_define, "\r"); //
		StringBuffer sb_sql = new StringBuffer(); //
		for (int i = 0; i < str_defineitems.length; i++) {
			if (str_destdbtype.equals(WLTConstants.MYSQL)) { // �����MYSQL,����Ҫ�ص�--���������!!!
				int li_pos = str_defineitems[i].indexOf("--"); //
				if (li_pos > 0) {
					sb_sql.append(str_defineitems[i].subSequence(0, li_pos)); //
				} else {
					sb_sql.append(str_defineitems[i]); //
				}
			} else {
				sb_sql.append(str_defineitems[i]); //
			}
		}

		getCommDMO().executeUpdateByDS(_destdsname, sb_sql.toString()); //
	}

	/**
	 * ������ͼԴΪMYSQL..
	 */
	private void createViewMySQL(String _destdsname, String _define, String table_name, String table_schema) throws Exception {
		String str_destdbtype = getDBType(_destdsname); // Ŀ�������
		int endlength = _define.indexOf("*/");
		if (endlength > 0) { //��ע����Ҫ��ȡ��
			_define = _define.substring(endlength + 2, _define.length());
		}
		_define = tbUtil.replaceAll(_define, "`", ""); //
		_define = tbUtil.replaceAll(_define, table_schema + ".", ""); //

		String[] str_defineitems = tbUtil.split(_define, "\r"); //
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create view " + table_name + " as ");
		for (int i = 0; i < str_defineitems.length; i++) {
			if (str_destdbtype.equals(WLTConstants.MYSQL)) { // �����MYSQL,����Ҫ�ص�--���������!!!
				int li_pos = str_defineitems[i].indexOf("--"); //
				if (li_pos > 0) {
					sb_sql.append(str_defineitems[i].subSequence(0, li_pos)); //
				} else {
					sb_sql.append(str_defineitems[i]); //
				}
			} else {
				sb_sql.append(str_defineitems[i]); //
			}
		}

		getCommDMO().executeUpdateByDS(_destdsname, sb_sql.toString()); //
	}

	/**
	 * ���ı��򱣴����ݵķ���!!
	 * @param _batchid
	 * @param _doc64code
	 * @param _text
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_batchid = _batchid; //
		if (str_batchid == null) {
			str_batchid = commDMO.getSequenceNextValByDS(null, "S_PUB_STYLEPADDOC_BATCHID"); //ȡ�µ�!!!
		} else {
			commDMO.executeUpdateByDS(null, "delete from pub_stylepaddoc where batchid='" + str_batchid + "'"); //��ɾ����ԭ��������
		}
		if (_doc64code != null) { //��������ݲŽ���������ɾ��!!
			ArrayList al_data = tbUtil.split(_doc64code, 10, 2000); //�ָ�
			ArrayList al_sql = new ArrayList(); //
			for (int i = 0; i < al_data.size(); i++) { //��������!
				String[] str_rowData = (String[]) al_data.get(i); //���е�����
				String str_id = commDMO.getSequenceNextValByDS(null, "S_PUB_STYLEPADDOC"); //����
				for (int j = 0; j < str_rowData.length; j++) {
					if (j == 0) { //����ǵ�һ��
						InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_stylepaddoc"); //
						isql_insert.putFieldValue("id", str_id); //
						isql_insert.putFieldValue("batchid", str_batchid); //����
						isql_insert.putFieldValue("seq", "" + (i + 1)); //���
						isql_insert.putFieldValue("doc0", str_rowData[0]); //��һ�е�ֵ
						al_sql.add(isql_insert.getSQL()); //
					} else {
						UpdateSQLBuilder isql_update = new UpdateSQLBuilder("pub_stylepaddoc", "id='" + str_id + "'"); //
						isql_update.putFieldValue("doc" + j, str_rowData[j]); //��һ�е�ֵ
						al_sql.add(isql_update.getSQL()); //
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sql); //ʵ�ʲ�������!!!
		}

		if (_sqls != null) {
			try {
				UpdateSQLBuilder isql_update = new UpdateSQLBuilder(_sqls[0], _sqls[2] + "='" + _sqls[3] + "'"); //
				if (_text == null || _text.trim().equals("")) {
					isql_update.putFieldValue(_sqls[1], ""); //
				} else {
					isql_update.putFieldValue(_sqls[1], _text + "#@$" + str_batchid + "$@#"); //
				}
				commDMO.executeUpdateByDS(null, isql_update); //
			} catch (Exception ex) {
				ex.printStackTrace(); //�Ե��쳣,����ʹ�������ʧ����Ȼ�ǿ��Լ�����,��Ϊ������ܼ�������!
			}
		}
		return str_batchid; //
	}

	/**
	 * ���ı��򱣴����ݵķ���!!
	 * @param _batchid
	 * @param _doc64code
	 * @param _text
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_batchid = _batchid; //
		if (str_batchid == null) {
			str_batchid = commDMO.getSequenceNextValByDS(null, "S_PUB_IMGUPLOAD_BATCHID"); //ȡ�µ�!!!
		} else {
			commDMO.executeUpdateByDS(null, "delete from pub_imgupload where batchid='" + str_batchid + "'"); //��ɾ����ԭ��������
		}
		if (_doc64code != null) { //��������ݲŽ���������ɾ��!!
			ArrayList al_data = tbUtil.split(_doc64code, 10, 2000); //�ָ�
			ArrayList al_sql = new ArrayList(); //
			for (int i = 0; i < al_data.size(); i++) { //��������!
				String[] str_rowData = (String[]) al_data.get(i); //���е�����
				String str_id = commDMO.getSequenceNextValByDS(null, "S_PUB_IMGUPLOAD"); //����
				for (int j = 0; j < str_rowData.length; j++) {
					if (j == 0) { //����ǵ�һ��
						InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_imgupload"); //
						isql_insert.putFieldValue("id", str_id); //
						isql_insert.putFieldValue("batchid", str_batchid); //����
						isql_insert.putFieldValue("billtable", _tabName); //
						isql_insert.putFieldValue("billpkname", _pkName); //
						isql_insert.putFieldValue("billpkvalue", _pkValue); //
						isql_insert.putFieldValue("seq", "" + (i + 1)); //���
						isql_insert.putFieldValue("img0", str_rowData[0]); //��һ�е�ֵ
						al_sql.add(isql_insert.getSQL()); //
					} else {
						UpdateSQLBuilder isql_update = new UpdateSQLBuilder("pub_imgupload", "id='" + str_id + "'"); //
						isql_update.putFieldValue("img" + j, str_rowData[j]); //��һ�е�ֵ
						al_sql.add(isql_update.getSQL()); //
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sql); //ʵ�ʲ�������!!!
		}
		return str_batchid; //
	}

	/*
	 * �õ�������ı�׼��䣡
	 */
	public void getCreateTableSQL(String dsName, String _destdsname, String table) throws Exception {
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(dsName, "select * from " + table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //
		int[] li_precision = hvst.getPrecision(); //��ȷ��
		int[] li_scale = hvst.getScale(); //С����Чλ��
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // �����һ������PK��ͷ������Ϊ������
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // �����id�ֶ�����id�ֶ�
					break; //
				}
			}
		}
		//
		String str_destdbtype = getDBType(_destdsname); // Ŀ��������..
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create table " + table + " (");
		for (int i = 0; i < str_coltypes.length; i++) {
			String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
			if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
				sb_sql.append(str_cols[i] + " " + "clob"); //
			} else {
				sb_sql.append(str_cols[i] + " " + str_coltype); //
				if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
				} else {
					if ((str_coltype.equalsIgnoreCase("decimal") || str_coltype.equalsIgnoreCase("number")) && li_scale[i] > 0) {
						sb_sql.append("(" + li_precision[i] + "," + li_scale[i] + ")"); //decimal(3,1);
					} else {
						sb_sql.append("(" + li_collength[i] + ")");
					}
				}
				if (str_cols[i].equalsIgnoreCase(pk_fieldname)) {
					sb_sql.append(" not null");
				}
			}
			if (i == str_coltypes.length - 1) { // ��������һ��
				if (pk_fieldname != null) {
					sb_sql.append(","); // �������������Ӷ���..
				}
			} else {
				sb_sql.append(","); //
			}
		}
		if (pk_fieldname != null) {
			if (table.length() < 25) {
				sb_sql.append("constraint pk_" + table + " primary key (" + pk_fieldname + ")"); //
			} else {
				sb_sql.append("primary key (" + pk_fieldname + ")"); //
			}
		}
		sb_sql.append(")");
		getCommDMO().executeUpdateByDSImmediately(_destdsname, sb_sql.toString()); // ��Ŀ���������ִ��
	}

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception { // һ�ű�һ�����500����¼/��
		if (type.equalsIgnoreCase("VIEW")) {//��ͼ
			Object obj = transferDBView(_sourcedsname, _destdsname, tablename);
			if (obj == null) {
				return "��ͼ[" + tablename + "]�����ɹ�!\r\n";
			}
			return "";
		}
		//�ڶ������ݿ��Ƿ���ڴ˱������ھʹ�����
		StringBuffer record = new StringBuffer();
		if (!tableMap.containsKey(tablename.toLowerCase())) { // �������˱���Ҫ����
			try {
				getCreateTableSQL(_sourcedsname, _destdsname, tablename);
			} catch (Exception ex) {
				ex.printStackTrace();
				record.append("[" + tablename + "]����ʧ��!��ϸ��鿴�������˿���̨!\r\n");
			}
			record.append("[" + tablename + "]�����ɹ�!\r\n");
		}
		boolean check_data = true;
		if (conditon != null) {
			check_data = (Boolean) conditon.get("check_data");
			if (!check_data) { //�����Ǩ�����ݣ�ֻ������Ϳ�����!
				return record.length() == 0 ? "[" + tablename + "]�Ѵ���!\r\n" : record.toString();
			}
		}
		StringBuffer retsb = new StringBuffer();
		int li_beginNo = 0; // ��ʼ��!
		String con_sql = "select count(*) from " + tablename + " where 1=1";
		int li_countall = Integer.parseInt(commDMO.getStringValueByDS(_sourcedsname, con_sql)); //
		if (li_countall == 0) {
			return "[" + tablename + "]������Ϊ�գ�\r\n";
		}
		while (1 == 1) { // ��ѭ��
			long ll_1 = System.currentTimeMillis(); //
			if (li_beginNo >= li_countall) {
				break;
			}
			HashMap returnMap = safeMoveDataby500(_sourcedsname, _destdsname, tablename, li_beginNo, conditon);
			retsb.append(returnMap.get("��־"));
			if (returnMap == null) { // ���Ϊ������ֱ�ӷ���
				break; //
			}
			li_beginNo = (Integer) returnMap.get("������"); // ʵ�����ݵĽ�����!
		}
		hvst_new = null; // һ�η��ʺ����գ� 
		return "[" + tablename + "]��ɹ�Ǩ��" + li_countall + "������\r\n" + retsb.toString();
	}

	public HashMap safeMoveDataby500(String _sourcedsname, String _destdsname, String table, int _beginNo, HashMap conditon) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // һ��ȡ500����¼!!!
		int li_endNo = _beginNo + li_batchRecords; // [>=1 and <=1000][>=1001
		// and <=2000][>=2001 and
		// <=3000]
		String dbType = ServerEnvironment.getDefaultDataSourceType();
		StringBuffer sql_sb = new StringBuffer();
		if (_sourcedsname != null && !_sourcedsname.equals("null")) {
			DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_sourcedsname);
			if (vo != null) {
				dbType = vo.getDbtype();
			}
		}
		if (dbType.equalsIgnoreCase("MYSQL")) {
			sql_sb.append("select *  from " + _tableName + " order by 1 limit " + _beginNo + "," + li_batchRecords);
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " order by 1 )" + _tableName);
			sql_sb.append("  where Rownum <=" + (_beginNo + li_batchRecords) + ") " + _tableName);
			sql_sb.append(" where RN > " + _beginNo);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by 1 asc) _rownum,");
			sb_sql_new.append(" * from " + _tableName); // ��ԭ����select���濪ʼ�����ݽ�����!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // ��ҳ!!!
			sql_sb.append(sb_sql_new.toString()); //
		} else if (dbType.equalsIgnoreCase("db2")) { //2013-6-21�������֧�ִ�db2Ǩ�����ݵ�������
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with t1_ as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by 1 asc) RN,"); ////
			sb_sql_new.append(table + ".* from " + table); // //���ȡ���ֶξ���*,��DB2�и���̬�ĵط������Ǳ�����*ǰ����ϱ���������ᱨ��,SQLServer��Oracle��û���������!!!
			sb_sql_new.append(") ");
			sb_sql_new.append("select * from t1_ where RN > " + _beginNo + "  and RN<=" + (_beginNo + li_batchRecords)); //��ҳ!!!
			sql_sb.append(sb_sql_new.toString());
		}
		return safeMoveDataCurrDo(_sourcedsname, _destdsname, _tableName, sql_sb.toString(), _beginNo + li_batchRecords, conditon); //
	}

	HashVOStruct hvst_new = null;

	public HashMap safeMoveDataCurrDo(String _sourcedsname, String _destdsname, String table, String sql, int lastNum, HashMap conditon) throws Exception {
		boolean check_quick = true;
		if (conditon != null) {
			check_quick = (Boolean) conditon.get("check_quick");
		}
		StringBuffer record = new StringBuffer();
		HashMap returnMap = new HashMap();
		HashVOStruct hvst = commDMO.getHashVoStructByDS(_sourcedsname, sql); // ȡ��500�����ݡ�
		String[] str_keys = hvst.getHeaderName(); // ����
		List l = new ArrayList();
		boolean haveRN = false;
		for (int i = 0; i < str_keys.length; i++) {
			if (!str_keys[i].equalsIgnoreCase("RN")) {
				l.add(str_keys[i]);
			} else {
				haveRN = true;
			}
		}
		if (haveRN) {
			str_keys = (String[]) l.toArray(new String[0]); //oracle 
		}
		HashVO[] str_vos = hvst.getHashVOs();
		List insertSql = new ArrayList();
		DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_destdsname);
		String _desttype = ""; // �õ������ݿ�����
		if (vo != null) {
			_desttype = vo.getDbtype();
		}

		if (hvst_new == null) {
			hvst_new = commDMO.getHashVoStructByDS(_destdsname, "select * from " + table + " where 1=2"); // �µı�ṹ		
		}
		//���������⣡������������������  �±���ϱ�Ľṹ��һ����˳��ͬ��
		String[] head = hvst_new.getHeaderName();
		String[] headtype = hvst_new.getHeaderTypeName();
		int[] str_length_new = hvst_new.getHeaderLength();
		int[] l_type = hvst_new.getHeaderType();
		HashMap newTableInfo = new HashMap(); //���hashmap��������±����Ϣ[���ͣ����ȣ�headerType]
		for (int i = 0; i < head.length; i++) {
			newTableInfo.put(head[i].toLowerCase(), new Object[] { headtype[i], str_length_new[i], l_type[i] });
		}
		for (int i = 0; i < str_vos.length; i++) { //
			StringBuffer items = new StringBuffer();
			StringBuffer values = new StringBuffer();
			for (int j = 0; j < str_keys.length; j++) { // ��������!!
				if (str_keys[j].equalsIgnoreCase("RN"))
					continue; // ������RN�����������ķ�Χ���ˣ����Զ��RN�����С�
				String str_itemValue = str_vos[i].getStringValue(str_keys[j], ""); // ȡ��ֵ!!
				if (str_itemValue == null || str_itemValue.trim().equals(""))
					continue;
				Object[] obj = (Object[]) newTableInfo.get(str_keys[j].toLowerCase());
				if (obj == null) {
					continue;
				}
				String itemtype = (String) obj[0];
				int itemlength = (Integer) obj[1];
				int l_type_n = (Integer) obj[2];
				int newLength = compareHeadlength(_desttype, str_itemValue, itemlength);
				if (newLength > 0 && newLength <= 4000 && itemlength != 4000) {
					String alterSQL = null;
					if (itemtype.equalsIgnoreCase("decimal") || itemtype.equalsIgnoreCase("number")) {
						if (_desttype.equalsIgnoreCase("DB2")) {
							alterSQL = " alter table " + table + " alter column " + str_keys[j] + " set data type " + itemtype + "(" + newLength + ")";
						} else {
							alterSQL = " alter table " + table + " modify " + str_keys[j] + " " + itemtype + "(" + newLength + ")";
						}
					} else {
						if (_desttype.equalsIgnoreCase("DB2")) {
							alterSQL = " alter table " + table + " alter column " + str_keys[j] + " set data type " + itemtype + "(" + newLength + ")";
						} else {
							alterSQL = " alter table " + table + " modify " + str_keys[j] + " " + itemtype + "(" + newLength + ")";
						}
					}
					if (check_quick) {
						commDMO.executeBatchByDSImmediately(_destdsname, new String[] { alterSQL });
					} else {
						commDMO.executeBatchByDS(_destdsname, new String[] { alterSQL });
					}
					hvst_new = commDMO.getHashVoStructByDS(_destdsname, "select * from " + table + " where 1=2"); // �µı�ṹ
					record.append("��[" + table + "]���ֶ�" + str_keys[j] + "����Ϊ:" + str_length_new[j] + "̫Сǿ��ת��Ϊ:" + newLength + "\r\n");
					str_length_new = hvst_new.getHeaderLength(); //���»���
					newTableInfo.put(str_keys[j].toLowerCase(), new Object[] { itemtype, newLength, l_type_n });
				} else if (newLength > 4000) { //�������4000�ͽ�ȡ
					if ("MYSQL".equalsIgnoreCase(_desttype)) {
						str_itemValue = str_itemValue.substring(0, 3999);
						record.append("MYSQL:��[" + table + "]���ֶ�" + str_keys[j] + "����Ϊ:" + str_length_new[j] + "��������ǿ�ƽ�ȡǰ4000���ַ�" + "\r\n");
					} else { //oracle���ֽ���
						String s = new String(str_itemValue.getBytes(), "GBK");
						byte[] b = s.getBytes("utf-8"); //�����ֽ�
						str_itemValue = new String(b, 0, 3996, "utf-8");
						record.append("ORACLE[DB2]:��[" + table + "]���ֶ�" + str_keys[j] + "����Ϊ:" + str_length_new[j] + "��������ǿ�ƽ�ȡǰ4000���ֽ�" + "\r\n");
					}
				}
				if (items.length() == 0) {
					items.append(str_keys[j]);
					values.append(getInsertValue(str_itemValue, _desttype, l_type_n));
				} else {
					items.append("," + str_keys[j]);
					values.append("," + getInsertValue(str_itemValue, _desttype, l_type_n));
				}
			}
			String insertSQL = "insert into " + table + " (" + items.toString() + ") values (" + values.toString() + ") ";
			insertSql.add(insertSQL);
		}
		if (check_quick) {
			commDMO.executeBatchByDSImmediately(_destdsname, insertSql, false);
		} else {
			commDMO.executeBatchByDS(_destdsname, insertSql, false, false);
		}
		returnMap.put("������", lastNum);
		returnMap.put("��־", record);
		return returnMap;
	}

	public int compareHeadlength(String dbType, String value, int oldLength) {
		if ("MYSQL".equalsIgnoreCase(dbType)) {
			int length = value.length();
			if (length > oldLength) {
				if (length < 100) { //С��100��ֱ�Ӹ��100
					length = 100;
				} else if (length >= 100 && length < 1000) {
					length = (length % 100 + 1) * 100; //��100��ȡ��
				} else if (length >= 1000 && length <= 4000) { //����1000, ���4000����
					length = 4000;
				} else {
					length = 4001; //Խ���Զ���ȡ�ַ���.
				}
				return length;
			}
		} else {
			int length = 0;
			try {
				length = value.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				length = value.getBytes().length;
				e.printStackTrace();
			}
			if (length > oldLength) {
				if (length < 100) { //С��100��ֱ�Ӹ��200
					length = 200;
				} else if (length >= 100 && length < 1000) {
					length = (length / 100 + 1) * 100; //��100��ȡ��
				} else if (length >= 1000 && length < 4000) { //����1000, ���4000����
					length = 4000;
				} else {
					length = 4001; //Խ���Զ���ȡ�ַ���.
				}
				return length;
			}
		}
		return -1; //����Ҫ�޸�
	}
}