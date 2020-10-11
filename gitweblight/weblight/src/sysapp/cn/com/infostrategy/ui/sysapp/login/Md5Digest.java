/**************************************************************************
 * $RCSfile: Md5Digest.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.security.MessageDigest;

/**
 * <p>Title: 数据签名工具类</p>
 * <p>Description: NOVA </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Gxlu inc., nm2 </p>
 * @author Jedi H. Zheng
 * @version 1.0
 */
public class Md5Digest implements IDigest {
	private static final char WWFHexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static int hexDigitsLen;

	static {
		hexDigitsLen = WWFHexDigits.length;
	}

	/**
	 * 产生MD5密码，长度8位字符
	 * @param username 用户名
	 * @param passwd   密码明文
	 * @return String 密码密文
	 */
	public String generatePasswd(String username, String passwd) {
		/** sa/sa passwd='nbr78a1r' */
		return MD5Str8(username + passwd + "WWF");
	}

	/**
	 * 产生8个字节的MD5数字签名
	 * @param s
	 * @return
	 */
	public final static String MD5Str8(String s) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(s.getBytes());
			byte[] md = mdTemp.digest();
			int md5_len = md.length;
			char str[] = new char[md5_len / 2];
			for (int i = 0; i < md5_len; i++) {
				int int0 = md[i++];
				if (int0 < 0)
					int0 += 256;

				int int1 = md[i];
				if (int1 < 0)
					int1 += 256;

				int int2 = int0 + (int1 << 8);

				str[i / 2] = WWFHexDigits[int2 % hexDigitsLen];
			}

			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成32字节的MD5数字签名
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(s.getBytes());
			byte[] md = mdTemp.digest();

			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * SHA 数字签名
	 * @param s
	 * @return
	 */
	public final static String SHA(String s) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA");
			mdTemp.update(s.getBytes());
			byte[] md = mdTemp.digest();

			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

}
/**************************************************************************
 * $RCSfile: Md5Digest.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:19:33 $
 *
 * $Log: Md5Digest.java,v $
 * Revision 1.5  2012/09/14 09:19:33  xch123
 * 邮储现场回来统一更新
 *
 * Revision 1.1  2012/08/28 09:41:13  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:32:08  wanggang
 * restore
 *
 * Revision 1.2  2010/12/13 05:46:22  xch123
 * 兴业出差回来
 *
 * Revision 1.1  2010/05/17 10:23:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:59  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:48  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:41  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:10  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:36  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:20:38  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
