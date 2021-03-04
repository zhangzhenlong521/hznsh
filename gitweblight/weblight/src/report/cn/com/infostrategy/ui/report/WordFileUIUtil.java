package cn.com.infostrategy.ui.report;

import java.io.Serializable;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.JACobUtil;

/**
 * Word文件的客户端工具类
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
	private String paramError = "传入参数有误，请核实你的传入值！";
	private String fileSaveToServerError = "文件保存到服务器错误！";
	/**
	 * 替换服务器某个Word文件中的内容
	 * 先从服务器端下载Word文件到客户端,再调用JACobUtil替换文件中的内容,再上传文件到服务器端!
	 * @param _serverdir
	 * @param _serverfilename
	 * @param _isabsoulate
	 * @param _oldtext
	 * @param _newtext
	 * @throws Exception
	 */
	public void replaceServerFileText(String _serverdir, String _serverfilename, boolean _isabsoulate, String _oldtext, String _newtext) throws Exception {

		//路径信息审核
		if(_serverdir == null || _serverfilename == null || _oldtext == null || _newtext == null){
			System.out.println(paramError);
			return ;
		}
		//下载文件到客户端，进行文档相关内容的替换。
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
		//测试所用数据...........
		new WordFileUIUtil().replaceServerFileText(System.getProperty("WLTUPLOADFILEDIR"), "a.doc", true, "wiwccrwwwiwmkksimasdsd", "pushworld");
	}
}
