package cn.com.infostrategy.bs.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * ȫ�ļ�����Servlet,����ҵ��Ŀ������4̨�м������Ⱥ,����ȫ����������������X:��,��ʱȫ�ļ�����ʵ���ǽ�1��5ǧ����ƶ����ݴ���һ̨�����϶�����̨������,Ȼ���ټ���!
 * ����һ�����ܷǳ���,�������׺ľ��м�����ڴ�!!
 * @author xch
 *
 */
public class FullTextSearchServlet extends HttpServlet {

	private static final long serialVersionUID = -5879770831393708648L;

	@Override
	public void service(ServletRequest _request, ServletResponse _response) throws ServletException, IOException {
		TBUtil tbUtil = new TBUtil(); //
		byte[] requestBytes = tbUtil.readFromInputStreamToBytes(_request.getInputStream()); //
		HashMap requestMap = (HashMap) tbUtil.deserialize(requestBytes); //��ο϶��Ǹ�HashMap
		String str_serverdir = ServerEnvironment.getProperty("FullTextSearchRootDir"); //ȫ�Ļ������ĸ�Ŀ¼!!
		if (str_serverdir == null || str_serverdir.trim().equals("")) {
			str_serverdir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //���ȫ�ļ����ĸ�Ŀ¼û������ֱ��ʹ��WLTUPLOADFILEDIR
		}
		String str_subdir = (String) requestMap.get("subdir"); //��Ŀ¼!!
		if (str_subdir != null) {
			str_serverdir = str_serverdir + str_subdir; //
		}
		if (!str_serverdir.endsWith("/")) {
			str_serverdir = str_serverdir + "/"; //���������
		}
		System.out.println("ȫ�ļ����ĸ�Ŀ¼=[" + str_serverdir + "]"); //
		String[][] str_fileInfos = (String[][]) requestMap.get("FileInfo"); //
		String[] str_keywords = (String[]) requestMap.get("KeyWords"); //
		boolean isAllContain = (Boolean) requestMap.get("isAllContain"); //
		HashMap responseMap = new HashMap(); ////

		long ll_jvm_total = Runtime.getRuntime().totalMemory() / (1024 * 1024); //
		long ll_jvm_busy = ll_jvm_total - (Runtime.getRuntime().freeMemory() / (1024 * 1024)); //
		if (ll_jvm_busy > 900) { //��ֹ�ڴ����!����900M
			responseMap.put("ReturnFileIds", new Exception("ȫ�Ĳ�ѯ�Ǹ��ǳ�������Դ�Ĳ���,ϵͳ��������ֻ��ͬʱ�в�����Ա���˲���!\r\n������ȫ�Ĳ�ѯ����̫��,���Ժ�����ȫ�Ĳ�ѯ!\r\n��ȥ��ȫ�Ĳ�ѯ������ʱֻʹ����ͨ������ѯ!")); //���ص��ļ�ID
			System.gc(); //
		} else {
			ArrayList al_return = (ArrayList) new BSUtil().checkWordOrExcelContainKeys(str_serverdir, str_fileInfos, str_keywords, isAllContain); //������!!!!
			responseMap.put("ReturnFileIds", al_return); //���ص��ļ�ID
			System.gc(); //
		}
		byte[] responseBytes = tbUtil.serialize(responseMap); //���л�
		tbUtil.writeBytesToOutputStream(_response.getOutputStream(), responseBytes); //���!!!
	}
}
