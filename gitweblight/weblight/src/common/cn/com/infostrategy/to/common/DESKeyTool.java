package cn.com.infostrategy.to.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES���ܹ���,����md5/sha��������,md5�ǲ�����ļ���,������ǿ���ļ���,���ǿ���ԭ�ⲻ���Ľ��ܳ�ԭ��������!!
 * ���ﻹ�и���Ҫ��ע����Կ�ױ����ǹ̶���! �����ṩ��һ�� getWLTKey()�ķ���,�����ص���һ���̶���Կ��!!
 * ���ֿ����ʹ�ñ�׼��DES��������ܵĹ��ܵ�����Ҫ! ��������Դ���ӵ��������!!!
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
			String str_1 = myTool.encrypt("abcd1234"); //���ܵ�ֵ�ǡ�wAEvayyfYTAFlBVDYTvEvw==��,����ǿմ����ǡ�BZQVQ2E7xL8=��,����ǡ�1���������ǡ�XOIiXdBhpCU=��
			System.out.println("���ܺ�=[" + str_1 + "]"); //
			String str_2 = myTool.decrypt("vhiViGuHGN54IpE9xaiWOQ=="); //
			System.out.println("���ܺ�=[" + str_2 + "]"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����һ���ַ���!
	 * �ʴ���Ŀ�����⹤���м�⵽��������Ŀ�����н�ϵͳ����ת��String���ͣ�����������һ��������ƽ̨������ת���������/2013-03-26��
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
	 * ����һ���ַ���!!
	 * @param _str
	 * @return
	 */
	public String encrypt(String _str) {
		try {
			if (_str == null) {
				return null;
			}
			//long ll_1 = System.currentTimeMillis(); //
			Cipher ecipher = Cipher.getInstance("DES"); //ʹ��JDK�ṩ�ı�׼DES�����㷨ʵ��!
			ecipher.init(Cipher.ENCRYPT_MODE, getWLTKey()); ///ʹ��ָ��˽Կ
			byte[] utf8 = _str.getBytes("UTF8"); //ת��!
			byte[] enc = ecipher.doFinal(utf8); //ʵ�ʽ��м��ܴ���!! ���ؼ��ܺ�Ĵ�,ʹ��Java�ṩ�ı�׼API����!!
			String str_return = new sun.misc.BASE64Encoder().encode(enc);
			//long ll_2 = System.currentTimeMillis(); //
			//System.out.println("���ܺ�ʱ[" + (ll_2 - ll_1) + "]"); //���ܽϺ�ʱ,һ����Ҫ600�����!
			return str_return; //
		} catch (java.lang.Exception e) {
			//e.printStackTrace(); //
			System.err.println("DESKeyTool����ʧ��,ԭ��:[" + e.getMessage() + "]"); //
			return _str;
		}
	}

	/**
	 * �����ֽ�����!!!
	 * @param _bytes
	 * @return
	 */
	public byte[] encrypt(byte[] _bytes) {
		try {
			Cipher ecipher = Cipher.getInstance("DES"); //ʹ��JDK�ṩ�ı�׼DES�����㷨ʵ��!
			ecipher.init(Cipher.ENCRYPT_MODE, getWLTKey()); //ʹ��ָ��˽Կ
			byte[] enc = ecipher.doFinal(_bytes); //ʵ�ʽ��м��ܴ���!! ���ؼ��ܺ�Ĵ�,ʹ��Java�ṩ�ı�׼API����!!
			return enc; //
		} catch (Exception ex) {
			System.err.println("DESKeyTool����ʧ��,ԭ��:[" + ex.getMessage() + "]"); //
			return null; //
		}
	}

	/**
	 * ����һ���ַ���!
	 * @param _str
	 * @return
	 */
	public String decrypt(String _str) {
		try {
			if (_str == null) {
				return null;
			}
			//long ll_1 = System.currentTimeMillis(); //
			Cipher dcipher = Cipher.getInstance("DES"); //ʹ��JDK�ṩ�ı�׼DES�����㷨ʵ��!
			dcipher.init(Cipher.DECRYPT_MODE, getWLTKey()); //ʹ��ָ��˽Կ
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(_str); //Decode base64 to get bytes
			byte[] utf8 = dcipher.doFinal(dec); //ʵ�ʽ��н��ܴ���!! ���ؽ��ܺ�Ĵ�,ʹ��Java�ṩ�ı�׼API����!!
			String str_return = new String(utf8, "UTF8"); // Decode using utf-8
			//long ll_2 = System.currentTimeMillis(); //
			//System.out.println("���ܺ�ʱ[" + (ll_2 - ll_1) + "]"); //���ܷǳ���,һ��ֻҪ0����!
			return str_return; //
		} catch (java.lang.Exception e) {
			//e.printStackTrace(); //
			System.err.println("DESKeyTool����ʧ��,ԭ��:[" + e.getMessage() + "]"); //
			return _str;
		}
	}

	/**
	 * ����һ���ֽ�����!!
	 * @param _bytes
	 * @return
	 */
	public byte[] decrypt(byte[] _bytes) {
		try {
			Cipher dcipher = Cipher.getInstance("DES"); //ʹ��JDK�ṩ�ı�׼DES�����㷨ʵ��!
			dcipher.init(Cipher.DECRYPT_MODE, getWLTKey()); //ʹ��ָ��˽Կ
			byte[] bytes = dcipher.doFinal(_bytes); //ʵ�ʽ��н��ܴ���!! ���ؽ��ܺ�Ĵ�,ʹ��Java�ṩ�ı�׼API����!!
			return bytes; //
		} catch (Exception ex) {
			System.err.println("DESKeyTool����ʧ��,ԭ��:[" + ex.getMessage() + "]"); //
			return null;
		}
	}

	//�õ�Կ��!!!������һ���̶���! ��������ͬ���ļ��ܵ��ַ���,ÿ�ν������һ��,����ʱ�����ü��ܵ�Կ��,�����ʧ��!!!
	private SecretKey getWLTKey() {
		byte[] raw = new byte[] { 0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55 }; //����ԭʼĸԿ��!
		byte[] keyBytes = addParity(raw); //��56λbit���ֽ�ת����һ���̶���key!!!
		//boolean b = DESKeySpec.isParityAdjusted(keyBytes, 0);  //
		SecretKey key = new SecretKeySpec(keyBytes, "DES"); //�ؼ�������,ʹ��JDK�ṩ��API����Կ��!!
		return key;
	}

	//Converting a 56-bit Value to a DES Key,���
	//�������������ʵ�ʵĹ̶���Կ��!!Ҳ���Դ�˽Կ֤���ļ��ж�ȡ! ��1024λ��,��Խ��,���ܺ�����ݰ�ҲԽ��! ��������������!!
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
