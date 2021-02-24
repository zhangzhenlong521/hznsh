package cn.com.infostrategy.bs.sysapp.runtime;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 自己定义的ClassLoad,不是从ClassPath中取类,而是从数据库中取类!!!
 * @author Administrator
 *
 */
public class WLTRuntimeClassLoader extends ClassLoader {

	public WLTRuntimeClassLoader(ClassLoader _clsLoader) {
		super(_clsLoader); //
	}

	/**
	 * 取类!!!
	 */
	public Class myloadClass(String _actionName) {
		try {
			//应该是从数据库取!!
			String str_sql = "select classcode from pub_rtaction_class where actionname='" + _actionName + "' order by seq"; //SQL语句!!一定要排序!!!
			HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //查询数据!!
			StringBuffer sb_clscode = new StringBuffer(); //
			for (int i = 0; i < hvs.length; i++) {
				sb_clscode.append(hvs[i].getStringValue("classcode")); //加入
			}
			byte[] bytes = new TBUtil().convertHexStringToBytes(sb_clscode.toString()); //将16进制的字符串转换成直进制数组
			Class clses = this.defineClass(null, bytes, 0, bytes.length); //用这个类来创建Class类!!!!
			return clses; //返回这个类!!!
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

}
