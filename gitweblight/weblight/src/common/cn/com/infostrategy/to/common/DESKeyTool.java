package cn.com.infostrategy.to.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密工具,它与md5/sha的区别是,md5是不可逆的加密,而这个是可逆的加密,即是可以原封不动的解密成原来的样子!!
 * 这里还有个重要的注意是钥匙必须是固定的! 这里提供了一个 getWLTKey()的方法,它返回的是一个固定的钥匙!!
 * 这种可逆的使用标准的DES加密与解密的功能到处需要! 比如数据源连接的密码加密!!!
 * @author xch
 *
 */
public class DESKeyTool {

	public DESKeyTool() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DESKeyTool myTool = new DESKeyTool(); //
			String str_1 = myTool.encrypt("abcd1234"); //加密的值是【wAEvayyfYTAFlBVDYTvEvw==】,如果是空串则是【BZQVQ2E7xL8=】,如果是【1】加密则是【XOIiXdBhpCU=】
			System.out.println("加密后=[" + str_1 + "]"); //
			String str_2 = myTool.decrypt("vhiViGuHGN54IpE9xaiWOQ=="); //
			System.out.println("解密后=[" + str_2 + "]"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 加密一个字符串!
	 * 邮储项目代码监测工具中监测到不能在项目代码中将系统密码转成String类型，故这里新增一个方法在平台代码中转换。【李春娟/2013-03-26】
	 * @param _str
	 * @return
	 */
	public String encrypt(char[] _char) {
		if (_char == null) {
			return null;
		}
		return encrypt(new String(_char));
	}

	/**
	 * 加密一个字符串!!
	 * @param _str
	 * @return
	 */
	public String encrypt(String _str) {
		try {
			if (_str == null) {
				return null;
			}
			//long ll_1 = System.currentTimeMillis(); //
			Cipher ecipher = Cipher.getInstance("DES"); //使用JDK提供的标准DES加密算法实例!
			ecipher.init(Cipher.ENCRYPT_MODE, getWLTKey()); ///使用指定私钥
			byte[] utf8 = _str.getBytes("UTF8"); //转码!
			byte[] enc = ecipher.doFinal(utf8); //实际进行加密处理!! 返回加密后的串,使用Java提供的标准API进行!!
			String str_return = new sun.misc.BASE64Encoder().encode(enc);
			//long ll_2 = System.currentTimeMillis(); //
			//System.out.println("加密耗时[" + (ll_2 - ll_1) + "]"); //加密较耗时,一般需要600多毫秒!
			return str_return; //
		} catch (java.lang.Exception e) {
			//e.printStackTrace(); //
			System.err.println("DESKeyTool加密失败,原因:[" + e.getMessage() + "]"); //
			return _str;
		}
	}

	/**
	 * 加密字节数组!!!
	 * @param _bytes
	 * @return
	 */
	public byte[] encrypt(byte[] _bytes) {
		try {
			Cipher ecipher = Cipher.getInstance("DES"); //使用JDK提供的标准DES加密算法实例!
			ecipher.init(Cipher.ENCRYPT_MODE, getWLTKey()); //使用指定私钥
			byte[] enc = ecipher.doFinal(_bytes); //实际进行加密处理!! 返回加密后的串,使用Java提供的标准API进行!!
			return enc; //
		} catch (Exception ex) {
			System.err.println("DESKeyTool加密失败,原因:[" + ex.getMessage() + "]"); //
			return null; //
		}
	}

	/**
	 * 解密一个字符串!
	 * @param _str
	 * @return
	 */
	public String decrypt(String _str) {
		try {
			if (_str == null) {
				return null;
			}
			//long ll_1 = System.currentTimeMillis(); //
			Cipher dcipher = Cipher.getInstance("DES"); //使用JDK提供的标准DES加密算法实例!
			dcipher.init(Cipher.DECRYPT_MODE, getWLTKey()); //使用指定私钥
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(_str); //Decode base64 to get bytes
			byte[] utf8 = dcipher.doFinal(dec); //实际进行解密处理!! 返回解密后的串,使用Java提供的标准API进行!!
			String str_return = new String(utf8, "UTF8"); // Decode using utf-8
			//long ll_2 = System.currentTimeMillis(); //
			//System.out.println("解密耗时[" + (ll_2 - ll_1) + "]"); //解密非常快,一般只要0毫秒!
			return str_return; //
		} catch (java.lang.Exception e) {
			//e.printStackTrace(); //
			System.err.println("DESKeyTool解密失败,原因:[" + e.getMessage() + "]"); //
			return _str;
		}
	}

	/**
	 * 解密一个字节数组!!
	 * @param _bytes
	 * @return
	 */
	public byte[] decrypt(byte[] _bytes) {
		try {
			Cipher dcipher = Cipher.getInstance("DES"); //使用JDK提供的标准DES加密算法实例!
			dcipher.init(Cipher.DECRYPT_MODE, getWLTKey()); //使用指定私钥
			byte[] bytes = dcipher.doFinal(_bytes); //实际进行解密处理!! 返回解密后的串,使用Java提供的标准API进行!!
			return bytes; //
		} catch (Exception ex) {
			System.err.println("DESKeyTool解密失败,原因:[" + ex.getMessage() + "]"); //
			return null;
		}
	}

	//得到钥匙!!!必须是一个固定的! 否则会出现同样的加密的字符串,每次结果都不一样,解密时必须拿加密的钥匙,否则会失败!!!
	private SecretKey getWLTKey() {
		byte[] raw = new byte[] { 0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55 }; //这是原始母钥串!
		byte[] keyBytes = addParity(raw); //将56位bit的字节转换成一个固定的key!!!
		//boolean b = DESKeySpec.isParityAdjusted(keyBytes, 0);  //
		SecretKey key = new SecretKeySpec(keyBytes, "DES"); //关键代码行,使用JDK提供的API生成钥匙!!
		return key;
	}

	//Converting a 56-bit Value to a DES Key,如果
	//这个串就是我们实际的固定的钥匙!!也可以从私钥证书文件中读取! 即1024位长,但越长,加密后的数据包也越长! 增加网络数据量!!
	private byte[] addParity(byte[] in) {
		byte[] result = new byte[8];
		int resultIx = 1;
		int bitCount = 0;
		for (int i = 0; i < 56; i++) {
			boolean bit = (in[6 - i / 8] & (1 << (i % 8))) > 0;
			if (bit) {
				result[7 - resultIx / 8] |= (1 << (resultIx % 8)) & 0xFF;
				bitCount++;
			}
			if ((i + 1) % 7 == 0) {
				if (bitCount % 2 == 0) {
					result[7 - resultIx / 8] |= 1;
				}
				resultIx++;
				bitCount = 0;
			}
			resultIx++;
		}
		return result;
	}

}
