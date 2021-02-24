package cn.com.infostrategy.ui.report;

import java.io.Serializable;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.JACobUtil;

/**
 * Word�ļ��Ŀͻ��˹�����
 * @author xch
 *
 */
public class WordFileUIUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4998276217180229721L;
	private JACobUtil jACobUtil = null;
	private String clientPath = null;
	private String serverPath = null;
	private String defaultClientPath = "c:/wkm";
	private String paramError = "��������������ʵ��Ĵ���ֵ��";
	private String fileSaveToServerError = "�ļ����浽����������";
	/**
	 * �滻������ĳ��Word�ļ��е�����
	 * �ȴӷ�����������Word�ļ����ͻ���,�ٵ���JACobUtil�滻�ļ��е�����,���ϴ��ļ�����������!
	 * @param _serverdir
	 * @param _serverfilename
	 * @param _isabsoulate
	 * @param _oldtext
	 * @param _newtext
	 * @throws Exception
	 */
	public void replaceServerFileText(String _serverdir, String _serverfilename, boolean _isabsoulate, String _oldtext, String _newtext) throws Exception {

		//·����Ϣ���
		if(_serverdir == null || _serverfilename == null || _oldtext == null || _newtext == null){
			System.out.println(paramError);
			return ;
		}
		//�����ļ����ͻ��ˣ������ĵ�������ݵ��滻��
		defaultClientPath = System.getProperty("WLTUPLOADFILEDIR");
		clientPath = UIUtil.downLoadFile(_serverdir, _serverfilename, _isabsoulate, defaultClientPath, _serverfilename, true);
		jACobUtil = new JACobUtil();
		jACobUtil.replaceWordFileText(clientPath, _oldtext, _newtext);
		serverPath = UIUtil.upLoadFile(_serverdir, _serverfilename, _isabsoulate, defaultClientPath, _serverfilename, true);
		if(serverPath == null || serverPath.equals("")){
			System.out.println(fileSaveToServerError);
			return ;
		}
	}

	public static void main(String[] args) throws Exception{
		//������������...........
		new WordFileUIUtil().replaceServerFileText(System.getProperty("WLTUPLOADFILEDIR"), "a.doc", true, "wiwccrwwwiwmkksimasdsd", "pushworld");
	}
}
