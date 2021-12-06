package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JWindow;

import org.jgraph.JGraph;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.to.WordTBUtil;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * ���������ģ��Ŀͻ��˹�����
 * @author xch
 *
 */
public class WFRiskUIUtil {
	private boolean process_showSeq = new TBUtil().getSysOptionBooleanValue("�����ļ�wordԤ��ʱ����ͼ�Ƿ���ʾ����", false);

	/**
	 * ����һ�������ļ�������,�����һ��Html��ʽ����ʱ�汾!!!
	 * Html��һ���ǳ����õ������ʽ,��Word��Wps���㲻���������,Html��������"�㶨"�ͻ����ռ��ֶ�!!!
	 * ���Ա��뽫Html��÷ǳ�ǿ��!!! �����ȵ���ʾ,������������ת!! ��������չ��������!!!
	 * �����Ժ��ǿ��ܽ���ϵ�ļ�ֱ���Ա��ķ�ʽ�ύ!! ���Ը���ҪHtml�ļ���ʽ!!!
	 * @param _parent �����
	 * @param _cmpfileID �����ļ�id
	 */
	public void openOneFileAsHTML(Container _parent, String _cmpfileID, int _htmlStyle) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//��ʱ�ĵط�,һ����Ҫ1-2��!!!
		if (wfBase64CodesMap.size() > 0) {//���������ͼ����������ͼ��ע�Ỻ��
			HashMap returnMap = UIUtil.commMethod("com.pushworld.ipushgrc.bs.wfrisk.WFRiskHtmlWebCallBean", "registCacheCode", wfBase64CodesMap);
			String str_regid = (String) returnMap.get("regcacheid"); //ע���Ψһ��!!!
			webCallParMap.put("regcacheid", str_regid); //ע��Ļ���ID
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //
		webCallParMap.put("htmlStyle", _htmlStyle); //
		//Զ�̵���,ʹ���������Html!!!
		UIUtil.openRemoteServerHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskHtmlWebCallBean", webCallParMap); //��!!!!!
	}

	/**
	 * ����һ�������ļ����������ݻ��������ݣ������һ��Word��ʽ����ʱ�汾!!!
	 * @param _parent �����
	 * @param _cmpfileID �����ļ�id
	 */
	public void openOneFileAsWord(Container _parent, String _cmpfileid) throws Exception {
		TBUtil tbutil = new TBUtil();
		boolean showreffile = tbutil.getSysOptionBooleanValue("�����ļ��Ƿ�����������word", true);//Ĭ�������ģ�����������word
		if (showreffile) {//�������������word���͵ÿ��Ǻϲ����ĺ�����ͼ������
			//��һ��Զ�̵��ã���ѯ�ļ���Ϣ
			HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
			if (reffileVO == null || reffileVO.length == 0) {
				MessageBox.show(_parent, "���ļ��ѱ�ɾ��,����Ԥ��!"); //
				return;
			} else {
				String reffile = reffileVO[0].getStringValue("reffile");
				if (reffile == null || reffile.trim().equals("")) {
					MessageBox.show(_parent, "���ļ�������δ��д,����Ԥ��!"); //
					return;
				}
			}
			String jacobtype = tbutil.getSysOptionStringValue("JACOB������ʽ", "2");//JACOB������ʽ:[�鿴����]���ĺ�����ͼ�ϲ���ʽ 0-������, 1-�ͻ���, 2-�������� , Ĭ��Ϊ2
			if ("0".equals(jacobtype)) {//ֻ�洢��������,ֱ�Ӵ򿪷����������ļ���
				String reffile = UIUtil.getStringValueByDS(null, "select reffile from cmp_cmpfile where id=" + _cmpfileid);
				UIUtil.openRemoteServerFile("office", reffile);
			} else if ("1".equals(jacobtype)) {//�ӷ��������������ĵ��ͻ��ˣ����ڿͻ�����������˵�����֣�Ȼ��ϲ���ת���ɶ�������������̨����

				String str_ClientCodeCache = System.getProperty("ClientCodeCache");
				System.out.println("!!!!!!!!!!!!!!!!!!!"+str_ClientCodeCache);
				if (str_ClientCodeCache.indexOf("\\") >= 0) {// �任�ͻ��˵�\\Ϊ/
					str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
				}
				if (str_ClientCodeCache.endsWith("/")) {// ����ͻ���·�����һλΪ/��ȥ��
					str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
				}
				String wfpath = str_ClientCodeCache + "/word";//������ʱĿ¼ C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word
				File wffile = new File(wfpath);
				if (!wffile.exists()) {//����ͻ���û�и��ļ��У��򴴽�֮
					wffile.mkdir();
				}

				//�ڶ���Զ�̵��ã����������ļ����ĵ��ͻ��ˣ������ĵ�������ݵ��滻��
				String reffilepath = null;
				try {
					String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//��Ҫ����һ�£���Ϊ�ļ����в��ܰ���һ��������ţ�\/:*?"<>|
					cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "/", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, ":", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "*", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "?", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "<", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, ">", "��");
					cmpfilename = tbutil.replaceAll(cmpfilename, "|", "��");//ע�������������ǲ�һ����Ŷ
					cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
					// cmpfilename + "_" + tbutil.getCurrDate(false, true) + ".doc" ���ﴴ������ʱ�ļ����Ʋ�Ҫ������ʱ��������ʱ�ļ�����������Ԥ����ͬʱ�����з��������ļ��ͻᱨ�����·������̽����ˣ����ļ�����ʧ���ˣ�
					reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, wfpath, cmpfilename + "_" + tbutil.getCurrDate(false, true) + "_" + new Random().nextInt(100) + ".doc", true);//��������һ��������������ڿͻ���wordԤ����δ�ͬһ�������ļ��������ռ�ö��������/2012-11-07��
					reffilepath = UIUtil.replaceAll(reffilepath, "\\", "/"); //
					if (reffilepath.contains("/")) {
						reffilepath = wfpath + reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());
						System.out.println(">>>>>>>" + reffilepath);
					}
				} catch (Exception e) {
					MessageBox.show(_parent, "�޷��ҵ����ļ���Ӧ������,����Ԥ��!");
					e.printStackTrace();
					return;
				}

				//������Զ�̵��ã��������˵�����ݵĶ�������
				IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				byte[] wfbytes = service.getDocContextBytes(_cmpfileid, getWfBase64CodesMap(_cmpfileid), true);
				String wffilename = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// ����˵��word������·������C:/WebPushTemp/word/258_1.2.doc
				wffile = new File(wffilename);//�ڿͻ��˴�������˵���ĵ�
				FileOutputStream output = new FileOutputStream(wffile);
				output.write(wfbytes);
				output.close();

				WordTBUtil wordutil = new WordTBUtil();
				HashMap textmap = new HashMap();//Ҫ�滻���ı�map,��key="$��������$",value="2011-05-24"
				textmap.put("$�ļ�����$", reffileVO[0].getStringValue("cmpfilename", ""));
				textmap.put("$����$", reffileVO[0].getStringValue("cmpfilecode", ""));
				textmap.put("$���Ƶ�λ$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
				textmap.put("$��������$", tbutil.getCurrDate() + "    ");
				textmap.put("$����ļ�$", reffileVO[0].getStringValue("item_addenda", ""));//�����˼����滻�����/2014-09-22��
				textmap.put("$��ر�$", reffileVO[0].getStringValue("item_formids", ""));
				wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$һͼ����$", textmap, _cmpfileid);//�ϲ��ļ����滻�ı�
				String str_url = "file://" + reffilepath;
				Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");//���ļ�
				File reffile = new File(reffilepath);
				if (reffile.exists()) {
					reffile.deleteOnExit();//java������˳�ʱɾ��
				}
				if (wffile.exists()) {
					wffile.deleteOnExit();//java������˳�ʱɾ��
				}
			} else {//�ڷ�������������ʱ�ϲ��ļ�
				//��һ��Զ�̵��ã���ѯ�ļ���Ϣ
				IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				try {
					String filename = service.getServerCmpfilePath(_cmpfileid, getWfBase64CodesMap(_cmpfileid)); //Զ�̵���,��ʱ�ϲ��ļ�   C:\WebPushTemp\officecompfile�ļ��µ�·�� ���硰word/cmpfile.doc��
					UIUtil.openRemoteServerFile("office", filename);
					//�����˳�ϵͳ�Զ�ɾ����ʱ���ɵ�WORD[YangQing/2013-09-09]
					String filepath = System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + filename;
					File wordfile = new File(filepath);
					if (wordfile.exists()) {
						wordfile.deleteOnExit();//java������˳�ʱɾ��
					}
				} catch (Exception e) {
					MessageBox.show(_parent, "�޷��ҵ����ļ���Ӧ������,����Ԥ��!"); //
				}
			}
		} else {//���������������word����ֱ����iText���ɼ���
			openOneFileAsWord2(_parent, _cmpfileid);
		}
	}

	/**
	 * ����һ�������ļ����������ݣ���Ŀ�ġ����÷�Χ�������һ��Word��ʽ����ʱ�汾!!!
	 * @param _parent �����
	 * @param _cmpfileID �����ļ�id
	 */
	private void openOneFileAsWord2(Container _parent, String _cmpfileID) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//��ʱ�ĵط�,һ����Ҫ1-2��!!!
		if (wfBase64CodesMap.size() > 0) {
			webCallParMap.put("ImgCode", wfBase64CodesMap); //ͼƬ����!!!
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //��ϵ�ļ�ID
		UIUtil.loadHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskDocViewWebDisPatch", webCallParMap, true); ////
	}

	/**
	 * ����һ�������ļ���id,������°汾��html���,�����ݿ���ȡ�����������°汾��html����!!!
	 * @param _parent �����
	 * @param _cmpfileID �����ļ�id
	 */
	public void openOneFileAsHTMLByHist(Container _parent, String _cmpfileID) throws Exception {
		//ȡ����ʷ�汾������
		String str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + " group by cmpfile_id");
		if (str_cmpfile_histid == null || "".equals(str_cmpfile_histid)) {
			MessageBox.show(_parent, "���ļ�δ������������html���");
			return;
		}
		String str_webean = "com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistHtmlViewWebCallBean"; //
		HashMap parMap = new HashMap(); //
		parMap.put("cmpfilehistid", str_cmpfile_histid); //
		UIUtil.openRemoteServerHtml(str_webean, parMap); //��!

	}

	/**
	 * ����һ�������ļ���id,������°汾��word���,�����ݿ���ȡ�����������°汾��word����!!!
	 * @param _parent �����
	 * @param _cmpfileID �����ļ�id
	 */
	public void openOneFileAsWordLByHist(Container _parent, String _cmpfileID, boolean _bigVersion) throws Exception {
		//ȡ����ʷ�汾������
		String str_cmpfile_histid = null;
		if (_bigVersion) {//�����Ҫ��ʾ��汾��
			str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + "  and cmpfile_versionno not like '%._1' group by cmpfile_id");
		} else {
			str_cmpfile_histid = UIUtil.getStringValueByDS(null, "select max(id) from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileID + " group by cmpfile_id");
		}

		if (str_cmpfile_histid == null || "".equals(str_cmpfile_histid)) {
			MessageBox.show(_parent, "���ļ�δ������������Word���");
			return;
		}
		String str_webean = "com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistDocViewWebCallBean"; //
		HashMap parMap = new HashMap(); //
		parMap.put("cmpfilehistid", str_cmpfile_histid); //
		UIUtil.openRemoteServerHtml(str_webean, parMap, true); //��!!
	}

	public void ExportHandBook(Container _parent, String _cmpfileID) throws Exception {
		HashMap webCallParMap = new HashMap(); //

		HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileID);//��ʱ�ĵط�,һ����Ҫ1-2��!!!
		if (wfBase64CodesMap.size() > 0) {
			webCallParMap.put("ImgCode", wfBase64CodesMap); //ͼƬ����!!!
		} else {
			MessageBox.show(_parent, "���ļ�û������,���ܲ鿴!");
			return;
		}
		webCallParMap.put("cmpfileid", _cmpfileID); //��ϵ�ļ�ID
		webCallParMap.put("ishandbook", "Y");
		UIUtil.loadHtml("com.pushworld.ipushgrc.bs.wfrisk.WFRiskDocViewWebDisPatch", webCallParMap, true); ////
	}

	/**
	 * ��������״̬Ϊ����Ч���������ļ�Ϊ1.0�汾�����������ʷ��¼���Ժ���Ż�������
	 * @param _wfmap
	 * @throws Exception
	 */
	public HashMap publishAllCmpFile() throws Exception {
		HashMap hashmap = new HashMap();
		String[] cmpfileids = UIUtil.getStringArrayFirstColByDS(null, "select id from cmp_cmpfile where filestate='3'");//������Ч��״̬�������ļ�ȫ�������
		for (int i = 0; i < cmpfileids.length; i++) {
			hashmap.put(cmpfileids[i], getWfBase64CodesMap(cmpfileids[i]));
		}
		IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		return service.publishAllCmpFile(hashmap); //Զ�̵���,�����°汾��������
	}

	/**
	 * ʵ�ʷ������߼�������word��html��ʽ����ʷ��¼
	 * @param _parent     �������
	 * @param _cmpfileid     �ļ�id
	 * @param _cmpfilename   �ļ�����
	 * @param _showreffile   �Ƿ�ʹ������
	 * @param _newversionno  �°汾��
	 */
	public void dealpublish(Container _parent, String _cmpfileid, String _cmpfilename, boolean _showreffile, String _newversionno) {
		this.dealpublish(_parent, _cmpfileid, _cmpfilename, _showreffile, _newversionno, false);
	}

	/**
	 * ʵ�ʷ������߼�������word��html��ʽ����ʷ��¼
	 * @param _parent     �������
	 * @param _cmpfileid     �ļ�id
	 * @param _cmpfilename   �ļ�����
	 * @param _showreffile   �Ƿ�ʹ������
	 * @param _newversionno  �°汾��
	 * @param _overwrite     ������ݿ������и��°汾�ţ��Ƿ�Ҫ���ǡ�������ɾ��ͬ�汾�ŵ�������ʷ��¼
	 */
	public void dealpublish(Container _parent, String _cmpfileid, String _cmpfilename, boolean _showreffile, String _newversionno, boolean _overwrite) {
		String str_cmpfile_histid = null;
		try {
			IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			if (_showreffile) {//�������������word���͵ÿ��Ǻϲ����ĺ�����ͼ������
				//��һ��Զ�̵��ã���ѯ�ļ���Ϣ
				HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
				if (reffileVO == null || reffileVO.length == 0) {
					MessageBox.show(_parent, "���ļ��ѱ�ɾ��,���ܷ���!"); //
					return;
				} else {
					String reffile = reffileVO[0].getStringValue("reffile");
					if (reffile == null || reffile.trim().equals("")) {
						MessageBox.show(_parent, "���ļ�������δ��д,���ܷ���!"); //
						return;
					}
				}
				TBUtil tbutil = new TBUtil();
				String jacobtype = tbutil.getSysOptionStringValue("JACOB������ʽ", "2");//JACOB������ʽ:[�鿴����]���ĺ�����ͼ�ϲ���ʽ 0-������, 1-�ͻ���, 2-�������� , Ĭ��Ϊ2
				if ("0".equals(jacobtype)) {//ֻ�洢��������
					service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), _overwrite);//ֻ��������
				} else if ("1".equals(jacobtype)) {//�ӷ��������������ĵ��ͻ��ˣ����ڿͻ�����������˵�����֣�Ȼ��ϲ���ת���ɶ�������������̨����
					String str_ClientCodeCache = System.getProperty("ClientCodeCache");
					if (str_ClientCodeCache.indexOf("\\") >= 0) {// �任�ͻ��˵�\\Ϊ/
						str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
					}
					if (str_ClientCodeCache.endsWith("/")) {// ����ͻ���·�����һλΪ/��ȥ��
						str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
					}
					String wfpath = str_ClientCodeCache + "/word";//C:/WebPushTemp/word
					File wffile = new File(wfpath);
					if (!wffile.exists()) {//������ʱĿ¼ C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word
						wffile.mkdir();
					}

					//�ڶ���Զ�̵��ã����������ļ����ĵ��ͻ��ˣ������ĵ�������ݵ��滻��
					String reffilepath = null;
					try {
						String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//��Ҫ����һ�£���Ϊ�ļ����в��ܰ���һ��������ţ�\/:*?"<>|
						cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "/", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, ":", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "*", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "?", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "<", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, ">", "��");
						cmpfilename = tbutil.replaceAll(cmpfilename, "|", "��");//ע�������������ǲ�һ����Ŷ
						cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
						// tbutil.getCurrTime(false, false) + "_" + cmpfilename + ".doc" ����ʱ��������ʱ�ļ����Ʋ�Ҫ��Ԥ��ʱ��������ʱ�ļ�����������Ԥ����ͬʱ�������ļ��ͻᱨ�����·������̽����ˣ����ļ�����ʧ���ˣ�
						reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, wfpath, tbutil.getCurrTime(false, false) + "_" + cmpfilename + "_" + new Random().nextInt(100) + ".doc", true);//��������һ��������������ڿͻ���wordԤ����δ�ͬһ�������ļ��������ռ�ö��������/2012-11-07��
					} catch (Exception e) {
						MessageBox.show(_parent, "�޷��ҵ����ļ���Ӧ������,���ܷ���!");
						e.printStackTrace();
						return;
					}

					//������Զ�̵��ã��������˵�����ݵĶ�������
					HashMap wfBase64CodesMap = getWfBase64CodesMap(_cmpfileid);//��ʱ�ĵط�,һ����Ҫ1-2��!!!
					byte[] wfbytes = service.getDocContextBytes(_cmpfileid, wfBase64CodesMap, true);
					String wffilename = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// ����˵��word������·������C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\word\258_1.2.doc
					wffile = new File(wffilename);//�ڿͻ��˴�������˵���ĵ�
					FileOutputStream output = new FileOutputStream(wffile);
					output.write(wfbytes);
					output.close();

					//���Ĵ�Զ�̵��ã��������ݿ�������һ����ʷ�汾��¼��Ϊ���ڿͻ��������ĵ�ʱ�޸ļ�¼���е�ǰ�汾�������Ѿ�������_overwrite���ں���ķ����Ͳ�Ҫ�ж��ˣ������Լ�Ҳɾ����
					str_cmpfile_histid = service.addCmpfileHist(_cmpfileid, _cmpfilename, _newversionno, _overwrite);

					WordTBUtil wordutil = new WordTBUtil();
					HashMap textmap = new HashMap();//Ҫ�滻���ı�map,��key="$��������$",value="2011-05-24"
					textmap.put("$�ļ�����$", reffileVO[0].getStringValue("cmpfilename", ""));
					textmap.put("$����$", reffileVO[0].getStringValue("cmpfilecode", ""));
					textmap.put("$���Ƶ�λ$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
					textmap.put("$��������$", tbutil.getCurrDate() + "    ");
					textmap.put("$����ļ�$", reffileVO[0].getStringValue("item_addenda", ""));//�����˼����滻�����/2014-09-22��
					textmap.put("$��ر�$", reffileVO[0].getStringValue("item_formids", ""));
					wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$һͼ����$", textmap, _cmpfileid);//�ϲ��ļ����滻�ı�
					InputStream input = new FileInputStream(reffilepath);
					byte readDocType[] = tbutil.readFromInputStreamToBytes(input);
					byte[] zipDocBytes = tbutil.compressBytes(readDocType);
					String str_doc64code = tbutil.convertBytesTo64Code(zipDocBytes); // ת��64λ����!!!

					//�����Զ�̵��ã�������word�ϲ���Ķ�����������̨��ֱ�ӱ��浽���ݿ⣬Ȼ���������html���߼�������ʷ��¼��������һ�����ݣ�
					//�������˵��word�����ĸ���wordɾ��
					//Զ�̵���,�����°汾����Ϊǰ���������°汾��ʱ���Ѿ��ж����Ƿ�Ҫ������ͬ�汾����ʷ��¼����������Ͳ����ж��ˡ�����Ψһһ�������ж��Ƿ񸲸ǵķ�������publishCmpFile
					service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, str_cmpfile_histid, wfBase64CodesMap, str_doc64code);

					File reffile = new File(reffilepath);
					if (wffile.exists()) {
						wffile.delete();
					}
					if (reffile.exists()) {
						reffile.delete();
					}
				} else {//�ɷ������˺ϲ�word
					try {
						service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), true, _overwrite); //Զ�̵���,�����°汾��������
					} catch (Exception e) {
						e.printStackTrace();
						MessageBox.show(_parent, "�޷��ҵ����ļ���Ӧ������,���ܷ���!");
						return;
					}
				}
			} else {//���������������word����ֱ����iText���ɼ���
				service.publishCmpFile(_cmpfileid, _cmpfilename, _newversionno, getWfBase64CodesMap(_cmpfileid), false, _overwrite); //Զ�̵���,�����°汾��������
			}
			UIUtil.executeUpdateByDS(null, "update cmp_risk_editlog set filestate='3' where cmpfile_id =" + _cmpfileid);//�����յ���־�����һ��״̬���Ա�ͳ��
			MessageBox.show(_parent, "�����°汾�ɹ�,�°汾��[" + _newversionno + "]!"); //
		} catch (Throwable e) {
			if (str_cmpfile_histid != null) {//�����ʷ��¼���������˼�¼��������ִ�б����ˣ�Ҫɾ�������ļ�¼
				try {
					UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_hist where id=" + str_cmpfile_histid);
				} catch (Exception e1) {
					MessageBox.showException(_parent, e1);
				}
			}
			MessageBox.showException(_parent, e);
		}
	}

	public HashMap getWfBase64CodesMap(String _cmpfileID) throws Exception {
		HashMap wfBase64CodesMap = new LinkedHashMap();
		//��һ����Զ�̷��ʲ�������ļ����е����̵�id
		String[] str_wfs = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_wf_process where cmpfileid=" + _cmpfileID + " order by userdef04,id");
		if (str_wfs == null || str_wfs.length < 1) {//����������ļ�û��������ֱ�ӷ���
			return wfBase64CodesMap;
		}
		//�ڶ�����Զ�̷���һ���Ӳ�����з��յ�
		HashVO[] hvs_risk = UIUtil.getHashVoArrayByDS(null, "select wfprocess_id,wfactivity_id,rank from cmp_risk where cmpfile_id='" + _cmpfileID + "'"); //

		//����������������ͼ,Ȼ������64λ��!!!
		//֧������,��ÿ�������˳��һ��!!!
		for (int i = 0; i < str_wfs.length; i++) { //������������
			WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel(false); //����һ��������!����ʾ�����䡾���/2012-03-08��
			wfPanel.loadGraphByID(str_wfs[i]);//һ��Ҫ��id���أ���Ϊ���̱������ظ���
			wfPanel.setGridVisible(false);//���ò���ʾ������㡾���/2012-11-16��
			wfPanel.showStaff(false);//���ò���ʾ���
			boolean isExportTitle = TBUtil.getTBUtil().getSysOptionBooleanValue("�����������Ƿ��б���", true); //�ڿ�ϵͳ����һЩ�ͻ�Ҫ�󵼳�ʱ��Ҫ����!
			if (!isExportTitle) {
				wfPanel.setTitleCellForeground(Color.WHITE);//���ñ�����ɫΪ��ɫ��������ʾ
			}
			wfPanel.reSetAllLayer(false);//��������һ��,����׶�������µļ�ͷ�п��ܲ���ʾ[���/2012-11-19]
			//�ӷ��յ�
			HashMap riskMap = new HashMap(); //
			for (int j = 0; j < hvs_risk.length; j++) {
				if (hvs_risk[j].getStringValue("wfprocess_id") != null && hvs_risk[j].getStringValue("wfprocess_id").equals(str_wfs[i])) { //���ڱ����̵�
					String str_activity_id = hvs_risk[j].getStringValue("wfactivity_id"); //
					if (str_activity_id != null) {
						int li_1 = 0, li_2 = 0, li_3 = 0;
						if (riskMap.containsKey(str_activity_id)) { //����Ѿ����˷��յ�
							RiskVO rvo = (RiskVO) riskMap.get(str_activity_id); ////
							li_1 = rvo.getLevel1RiskCount();
							li_2 = rvo.getLevel2RiskCount();
							li_3 = rvo.getLevel3RiskCount();
						}
						String str_rank = hvs_risk[j].getStringValue("rank"); //���յȼ�
						if (str_rank != null) {
							if (str_rank.equals("�߷���") || str_rank.equals("�������")) {
								li_1++;
							} else if (str_rank.equals("�ͷ���") || str_rank.equals("��С����")) {
								li_3++;
							} else {
								li_2++; //�еȷ���
							}
						} else {
							li_2++; //�еȷ���
						}
						RiskVO rsvo = new RiskVO(li_1, li_2, li_3); //
						riskMap.put(str_activity_id, rsvo); //��������!!!
					}
				}
			}
			String[] str_keys = (String[]) riskMap.keySet().toArray(new String[0]); //
			for (int k = 0; k < str_keys.length; k++) {
				RiskVO rvo = (RiskVO) riskMap.get(str_keys[k]); ////
				if (rvo != null) {
					wfPanel.setCellAddRisk(str_keys[k], rvo); ////
				}
			}
			if (process_showSeq) {//����word�����ͼ��ʾ����
				wfPanel.doShowOrder(true);
			}
			JGraph graph = wfPanel.getGraph(); ////
			int li_width = (int) graph.getPreferredSize().getWidth(); //
			int li_height = (int) graph.getPreferredSize().getHeight(); //

			JWindow win = new JWindow(); //����һ������,��֪��Ϊʲôһ��ҪŪһ��������ʾ����,���ܰ�ͼ����ȥ!!!
			win.setSize(0, 0); //
			win.getContentPane().add(wfPanel); // 
			win.toBack(); //
			win.setVisible(true); //
			if (li_width == 0 || li_height == 0) {//�������û�л���
				li_width = 1;
				li_height = 1;
			}
			BufferedImage image = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB); //����һ���հ׵�ͼƬ!!
			Graphics g = image.createGraphics(); //ΪͼƬ����һ���µĻ���!!
			graph.paint(g); //���ؼ���ͼ��д�뵽����µĻ�����
			g.dispose();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "JPEG", out);
			byte[] imgBytes = out.toByteArray(); //���ɶ���������!!!
			byte[] zipedImgBytes = new TBUtil().compressBytes(imgBytes); //ѹ��һ��!!
			wfBase64CodesMap.put(str_wfs[i], zipedImgBytes); //�����ϣ��!!ע��!!
			win.dispose();
			win = null; //�ڴ��ͷ�
		}
		return wfBase64CodesMap; //����!!
	}

	public void insertAddRiskLog(BillVO _billvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //����
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "�������յ�");//�༭����
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//�༭��ʱ��
		isql.putFieldValue("cmpfile_id", _billvo.getStringValue("cmpfile_id"));//�ļ�id
		isql.putFieldValue("cmpfile_name", _billvo.getStringValue("cmpfile_name"));//�ļ�����
		isql.putFieldValue("filestate", "1"); //�ļ�״̬,Ĭ��Ϊ�༭�У�����ļ��������ֹ�ˣ�Ӧ�øı��״̬
		isql.putFieldValue("blcorpid", _billvo.getStringValue("blcorpid"));//��������id
		isql.putFieldValue("blcorpname", _billvo.getStringValue("blcorpname"));//������������
		isql.putFieldValue("bsactid", _billvo.getStringValue("bsactid"));//ҵ��id
		isql.putFieldValue("bsactname", _billvo.getStringValue("bsactname"));//ҵ������
		isql.putFieldValue("possible", _billvo.getStringValue("possible"));//������
		isql.putFieldValue("serious", _billvo.getStringValue("serious"));//���س̶�
		isql.putFieldValue("rank", _billvo.getStringValue("rank"));//���յȼ� 
		isql.putFieldValue("ctrlfneffect", _billvo.getStringValue("ctrlfneffect"));//���ƴ�ʩ��Ч��
		isql.putFieldValue("resdpossible", _billvo.getStringValue("resdpossible"));//ʣ����յĿ�����
		isql.putFieldValue("resdserious", _billvo.getStringValue("resdserious"));//ʣ����յ����س̶� 
		isql.putFieldValue("resdrank", _billvo.getStringValue("resdrank"));//ʣ����յķ��յȼ�
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//����������
		UIUtil.executeUpdateByDS(null, isql);
	}

	public void insertEditRiskLog(BillVO _oldbillvo, BillVO _newbillvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //����
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "�޸ķ��յ�");//�༭����
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//�༭��ʱ��
		isql.putFieldValue("cmpfile_id", _newbillvo.getStringValue("cmpfile_id"));//�ļ�id
		isql.putFieldValue("cmpfile_name", _newbillvo.getStringValue("cmpfile_name"));//�ļ�����
		isql.putFieldValue("filestate", "1"); //�ļ�״̬,Ĭ��Ϊ�༭�У�����ļ��������ֹ�ˣ�Ӧ�øı��״̬
		isql.putFieldValue("blcorpid", _newbillvo.getStringValue("blcorpid"));//��������id
		isql.putFieldValue("blcorpname", _newbillvo.getStringValue("blcorpname"));//������������
		isql.putFieldValue("bsactid", _newbillvo.getStringValue("bsactid"));//ҵ��id
		isql.putFieldValue("bsactname", _newbillvo.getStringValue("bsactname"));//ҵ������
		isql.putFieldValue("possible", _newbillvo.getStringValue("possible"));//������
		isql.putFieldValue("serious", _newbillvo.getStringValue("serious"));//���س̶�
		isql.putFieldValue("rank", _newbillvo.getStringValue("rank"));//���յȼ� 
		isql.putFieldValue("ctrlfneffect", _newbillvo.getStringValue("ctrlfneffect"));//���ƴ�ʩ��Ч��
		isql.putFieldValue("resdpossible", _newbillvo.getStringValue("resdpossible"));//ʣ����յĿ�����
		isql.putFieldValue("resdserious", _newbillvo.getStringValue("resdserious"));//ʣ����յ����س̶� 
		isql.putFieldValue("resdrank", _newbillvo.getStringValue("resdrank"));//ʣ����յķ��յȼ�
		isql.putFieldValue("possible2", _oldbillvo.getStringValue("possible"));//������
		isql.putFieldValue("serious2", _oldbillvo.getStringValue("serious"));//���س̶�
		isql.putFieldValue("rank2", _oldbillvo.getStringValue("rank"));//���յȼ� 
		isql.putFieldValue("ctrlfneffect2", _oldbillvo.getStringValue("ctrlfneffect"));//���ƴ�ʩ��Ч��
		isql.putFieldValue("resdpossible2", _oldbillvo.getStringValue("resdpossible"));//ʣ����յĿ�����
		isql.putFieldValue("resdserious2", _oldbillvo.getStringValue("resdserious"));//ʣ����յ����س̶� 
		isql.putFieldValue("resdrank2", _oldbillvo.getStringValue("resdrank"));//ʣ����յķ��յȼ�
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//�޸�������
		UIUtil.executeUpdateByDS(null, isql);
	}

	public void insertDeleteRiskLog(BillVO _billvo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("cmp_risk_editlog"); //
		String str_newId = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISK_EDITLOG"); //����
		isql.putFieldValue("id", str_newId); //
		isql.putFieldValue("edittype", "ɾ�����յ�");//�༭����
		isql.putFieldValue("edittime", new TBUtil().getCurrTime());//�༭��ʱ��
		isql.putFieldValue("cmpfile_id", _billvo.getStringValue("cmpfile_id"));//�ļ�id
		isql.putFieldValue("cmpfile_name", _billvo.getStringValue("cmpfile_name"));//�ļ�����
		isql.putFieldValue("filestate", "1"); //�ļ�״̬,Ĭ��Ϊ�༭�У�����ļ��������ֹ�ˣ�Ӧ�øı��״̬
		isql.putFieldValue("blcorpid", _billvo.getStringValue("blcorpid"));//��������id
		isql.putFieldValue("blcorpname", _billvo.getStringValue("blcorpname"));//������������
		isql.putFieldValue("bsactid", _billvo.getStringValue("bsactid"));//ҵ��id
		isql.putFieldValue("bsactname", _billvo.getStringValue("bsactname"));//ҵ������
		isql.putFieldValue("possible2", _billvo.getStringValue("possible"));//������
		isql.putFieldValue("serious2", _billvo.getStringValue("serious"));//���س̶�
		isql.putFieldValue("rank2", _billvo.getStringValue("rank"));//���յȼ� 
		isql.putFieldValue("ctrlfneffect2", _billvo.getStringValue("ctrlfneffect"));//���ƴ�ʩ��Ч��
		isql.putFieldValue("resdpossible2", _billvo.getStringValue("resdpossible"));//ʣ����յĿ�����
		isql.putFieldValue("resdserious2", _billvo.getStringValue("resdserious"));//ʣ����յ����س̶� 
		isql.putFieldValue("resdrank2", _billvo.getStringValue("resdrank"));//ʣ����յķ��յȼ�
		isql.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getName());//ɾ��������
		UIUtil.executeUpdateByDS(null, isql);
	}

	/**
	 * һ��BOMͼ�е������ȵ��RiskVO
	 * @param _bomtype  "RISK"��"PROCESS"��"CMPFILE"
	 * @param _datatype  "BLCORPNAME"��"ICTYPENAME"
	 * @param _alldatas  BOMͼ�����ȵ�ֵ��ֻ�л�������Ҫ����
	 * @param _isSelfCorp  �Ƿ��ѯ������
	 * @return
	 * @throws Exception
	 */
	public Hashtable getHashtableRiskVO(String _bomtype, String _datatype, ArrayList _alldatas, boolean _isSelfCorp) throws Exception {
		IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		return service.getHashtableRiskVO(_bomtype, _datatype, _alldatas, _isSelfCorp);
	}

	/**
	 * �������������ļ����ļ�����ͼ�����/2015-07-13��
	 * ֻ�������ڿͻ��˺ϲ�����������������word�����
	 * @param _parent �����
	 * @param _billVOs �����ļ�
	 */
	public void exportFilesAsWord(Container _parent, BillVO[] _billVOs) throws Exception {
		//��ѡ��Ŀ¼
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("ѡ��һ��Ŀ¼!"); //
		int flag = chooser.showOpenDialog(_parent);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		String filedir = file.getPath();
		TBUtil tbutil = new TBUtil();
		StringBuffer sb_msg = new StringBuffer();
		int count = 0;
		for (int i = 0; i < _billVOs.length; i++) {
			String cmpfileid = _billVOs[i].getStringValue("id");
			//��һ��Զ�̵��ã���ѯ�ļ���Ϣ
			HashVO[] reffileVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + cmpfileid + "'");
			if (reffileVO == null || reffileVO.length == 0) {
				continue;
			} else {
				String reffile = reffileVO[0].getStringValue("reffile");
				if (reffile == null || reffile.trim().equals("")) {
					sb_msg.append("��" + reffileVO[0].getStringValue("cmpfilename") + "�������Ĳ�����!\r\n");
					continue;
				}
			}
			if (filedir.indexOf("\\") >= 0) {// �任�ͻ��˵�\\Ϊ/
				filedir = UIUtil.replaceAll(filedir, "\\", "/"); //
			}
			if (filedir.endsWith("/")) {// ����ͻ���·�����һλΪ/��ȥ��
				filedir = filedir.substring(0, filedir.length() - 1);
			}
			File wffile = new File(filedir);

			//�ڶ���Զ�̵��ã����������ļ����ĵ��ͻ��ˣ������ĵ�������ݵ��滻��
			String reffilepath = null;
			try {
				String cmpfilename = reffileVO[0].getStringValue("cmpfilename");//��Ҫ����һ�£���Ϊ�ļ����в��ܰ���һ��������ţ�\/:*?"<>|
				cmpfilename = tbutil.replaceAll(cmpfilename, "\\", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "/", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, ":", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "*", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "?", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "\"", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "<", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, ">", "��");
				cmpfilename = tbutil.replaceAll(cmpfilename, "|", "��");//ע�������������ǲ�һ����Ŷ
				cmpfilename = tbutil.replaceAll(cmpfilename, " ", "");
				reffilepath = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/officecompfile", reffileVO[0].getStringValue("reffile"), true, filedir, cmpfilename + "_" + tbutil.getCurrDate(true, true) + ".doc", true);//��������һ��������������ڿͻ���wordԤ����δ�ͬһ�������ļ��������ռ�ö��������/2012-11-07��
				reffilepath = UIUtil.replaceAll(reffilepath, "\\", "/"); //
				if (reffilepath.contains("/")) {
					reffilepath = filedir + reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());
					System.out.println(">>>>>>>" + reffilepath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			//������Զ�̵��ã��������˵�����ݵĶ�������
			IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			byte[] wfbytes = service.getDocContextBytes(cmpfileid, getWfBase64CodesMap(cmpfileid), true);
			String wffilename = filedir + "/wf_" + System.currentTimeMillis() + ".doc";// ����˵��word������·������C:/WebPushTemp/word/258_1.2.doc
			wffile = new File(wffilename);//�ڿͻ��˴�������˵���ĵ�
			FileOutputStream output = new FileOutputStream(wffile);
			output.write(wfbytes);
			output.close();

			WordTBUtil wordutil = new WordTBUtil();
			HashMap textmap = new HashMap();//Ҫ�滻���ı�map,��key="$��������$",value="2011-05-24"
			textmap.put("$�ļ�����$", reffileVO[0].getStringValue("cmpfilename", ""));
			textmap.put("$����$", reffileVO[0].getStringValue("cmpfilecode", ""));
			textmap.put("$���Ƶ�λ$", tbutil.changeStrToLonger(reffileVO[0].getStringValue("blcorpname", ""), 14, true));
			textmap.put("$��������$", tbutil.getCurrDate() + "    ");
			textmap.put("$����ļ�$", reffileVO[0].getStringValue("item_addenda", ""));//�����˼����滻�����/2014-09-22��
			textmap.put("$��ر�$", reffileVO[0].getStringValue("item_formids", ""));
			wordutil.mergeOrReplaceFile(wffilename, reffilepath, "$һͼ����$", textmap, cmpfileid);//�ϲ��ļ����滻�ı�
			if (wffile.exists()) {
				wffile.delete();//java������˳�ʱɾ��
			}
			count++;
		}
		sb_msg.append("\r\n������" + count + "���ļ���");
		MessageBox.show(_parent, sb_msg.toString());
	}

}
