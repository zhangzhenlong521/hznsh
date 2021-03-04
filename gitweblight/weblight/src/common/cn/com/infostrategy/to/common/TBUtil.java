/**************************************************************************
 * $RCSfile: TBUtil.java,v $  $Revision: 1.92 $  $Date: 2012/12/11 05:30:05 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.report.WordFileUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * ��õĹ�����,����UI����BS�������õ�! �������뵽�ĸ����ַ�,����ȹ���,���ﶼ��:
 * ��:������,����,16����,64λ��,Swing�������ͼƬ,��ɫ,����,�쳣��ջ,���л�,��ȿ�¡,�������ַ�ת��,�ȼ�������Ҳ��һ��ѧϰ�ĵط�!
 * 
 * �ַ������������ط���: boolean isEmpty(String _value) //�ж��ַ����Ƿ�Ϊ�� findCount(String
 * _par, String _separator) //��һ���ַ�����Ѱ����һ���ַ����ĸ��� String[] split(String _par,
 * String _separator) //�ָ� String replaceAll(String str_par, String old_item,
 * String new_item) //�滻 isExistInArray(String _item, String[] _array, boolean
 * _isIgnoreCase) //�ж�һ���ַ����Ƿ������һ��������,����ָ���Ƿ���Դ�Сд isStartWithInArray(String
 * _item, String[] _array, boolean _isIgnoreCase) //�Ƿ�ʲô��ʼ
 * isEndWithInArray(String _item, String[] _array, boolean _isIgnoreCase)
 * //�Ƿ���ʲô���� getStrUnicodeLength(String s) //ȡ�ַ����Ŀ��(��������λ) getStrWidth(Font
 * _font, String _str) //���ַ�����ʲô����ʱ�����ؿ�� convertStrToHexString(String _str);
 * //ת����16���� convertStrTo64Code(String _str); //ת��64λ�� String
 * getDoubleToString(double _ld) //��doubleת�����ַ��� containTwoArrayCompare(String[]
 * _contain, String[] _content) //�������������Ƿ�����ͬ�� getFormulaMacPars(_text, "{",
 * "}"); //�ָ��ַ���,���ض��Ŀ�ʼ��������!��������ʱ,�滻���еĺ��������õ�!
 * getInterSectionFromTwoArray(String[] _array1, String[] _array2) //ȡ��������Ľ���
 * subStrByGBLength(String _str, int _limitLen, boolean _isAppendDot)
 * //ȡ���ַ�����ǰ��λ,��������λ convertStrToMapByExpress(String _str, String _split1,
 * String _split2, boolean _keyLowcase) //���ַ������ηָ�,���һ��map
 * 
 * ������ط���,ƴSQL�ķ��� sortStrs(String[] _str) /�����ַ��� sortHashVOs(HashVO[] _hvs,
 * String[][] _sortColumns) //����HashVO String getInCondition(String[] _allvalue)
 * //����SQL��in����! String getNullCondition(String _nullId)
 * //��һ���ַ�������ǿ�ֵ�����ת����-99999,�Ӷ���֤�������ݿ������¶���ִ��! getHashMapFromHashVOs(HashVO[]
 * _hvs, String _keyField, String _valueField) //��HashVO[]�е�����ת��һ��Map
 * 
 * ϵͳ,����,����/�������ط��� int getJVSite() //ȡ��������ǿͻ��˻��Ƿ������� Object deepClone(Object
 * obj) //��ȿ�¡һ������ byte[] serialize(Object _obj) //���л�һ��������ֽ����� Object
 * deserialize(byte[] _bytes) //�����л��ֽ������һ������ Object
 * refectCallClassStaticMethod(String _className, String _methodName, Object[]
 * _parObjs) //������� Object reflectCallMethod(String _formula) //����ִ��һ������ byte[]
 * readFromInputStreamToBytes(InputStream _ins) //��һ���������е�����ȡ��,����byts[]
 * writeBytesToOutputStream(OutputStream _out, byte[] _bys) //��һ��byte[]д��һ���������
 * 
 * ��ɫ,ͼƬ,ʱ�� java.awt.Color getColor(String _html) //��html��ʽת������ɫ���� String
 * convertColor(java.awt.Color _color) //����ɫת����html��6λ byte[]
 * getCompentBytes(Component _component, int _width, int _height)
 * //��Swing�ؼ�ת���ֽ���,������ֽ���д�ļ�����һ��jpg�ļ� String getCompent64Code(Component
 * _component, int _width, int _height)
 * //��Swing�ؼ�����64λ��,�����64λ��д��mht�ļ���xml��ʽ��word�ļ�,�Ϳ���ֱ�ӿ�! String getCurrDate()
 * //ȡ�õ�ǰ���� String getCurrTime() //ȡ�õ�ǰʱ��
 * 
 * ȡ���� String getSysOptionStringValue(String _key, String _nvl) //ȡϵͳ������ boolean
 * getSysOptionBooleanValue(String _key, boolean _nvl) //ȡϵͳ��������!
 * 
 * תhtml String getHtmlHead() //ȡ��html��ͷ String getHtmlByHashVOs(HashVO[] _hvs)
 * //��HashVO[] ����html writeHashToHtmlTableFile(HashVO _hvs[], String _filename)
 * //��HashVO[]ֱ��д��html�ļ� String getExceptionStringBuffer(Throwable _ex, boolean
 * _isHtmlFormat, boolean _isHtmlContainHead) //���쳣�Ķ�ջ�����ı���html 
 * 
 */
public class TBUtil implements Serializable {

	private static final long serialVersionUID = 1906433042446054793L;

	public static int JVMSITE_SERVER = 0; // ��������JVM
	public static int JVMSITE_CLIENT = 1; // �ͻ���JVM

	private static TBUtil tbUtil = new TBUtil();
	private static HashMap constMap = new HashMap(); //������һЩ

	/**
	 * Ĭ�Ϲ��췽��
	 */
	public TBUtil() {
	}

	/**
	 * �滻�ַ���
	 * 
	 * @param str_par
	 * @param old_item
	 * @param new_item
	 * @return
	 */
	public String replaceAll(String str_par, String old_item, String new_item) {
		if (str_par == null) {
			return null;
		}

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

	public boolean compareTwoString(String _str1, String _str2) {
		return ("" + _str1).equals("" + _str2);
	}

	public boolean compareTwoStringdIgnoreWrap(String _str1, String _str2) {
		if (_str1 == null) {
			_str1 = "";
		}

		if (_str2 == null) {
			_str2 = "";
		}
		_str1 = replaceAll(_str1, "\r", "");
		_str1 = replaceAll(_str1, "\n", "");

		_str2 = replaceAll(_str2, "\r", "");
		_str2 = replaceAll(_str2, "\n", "");

		return _str1.trim().equals(_str2.trim());
	}

	public int getStrWidth(String _str) {
		return getStrWidth(LookAndFeel.font, _str); //
		//return Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font).stringWidth(_str); //
	}

	/**
	 * �����������ĳһ���ַ����Ŀ��!��������Ҫ����ɶʱ����ʱ��Ҫ!!
	 * 
	 * @param _font
	 * @param _str
	 * @return
	 */
	public int getStrWidth(Font _font, String _str) {
		return SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(_font), _str); //
		//return Toolkit.getDefaultToolkit().getFontMetrics(_font).stringWidth(_str); //
	}

	// ȥ��\r\n�Ļ��з�,��ȡʣ�µ��ַ���
	public String getTrimSwapLineStr(String _str) {
		String str_return = replaceAll(_str, "\r", ""); //
		str_return = replaceAll(str_return, "\n", ""); //
		str_return = replaceAll(str_return, "<html>", ""); //
		str_return = replaceAll(str_return, "</html>", ""); //
		str_return = replaceAll(str_return, "<br>", ""); //
		str_return = replaceAll(str_return, "&nbsp;", ""); //
		return str_return; // ȥ��\r\n
	}

	public String[] split(String _par, String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		ArrayList al_temp = new ArrayList(); //
		String str_remain = _par; //
		int li_pos = str_remain.indexOf(_separator); //
		while (li_pos >= 0) {
			String str_1 = str_remain.substring(0, li_pos); //
			if (str_1 != null && !str_1.trim().equals("")) {
				al_temp.add(str_1); // ����!!!
			}
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		if (str_remain != null && !str_remain.trim().equals("")) {
			al_temp.add(str_remain); //
		}

		return (String[]) al_temp.toArray(new String[0]); // //
	}

	/**
	 * �����ù�ʽʱ�������ַ����ָ��ʱ���ַ�������Ҫ�ָ����������ֻ��Ҫ�ڷָ���ǰ�ӡ�\������!���� ��ABC\BEBFG���á�B���ָ��õ���A������CBE������FG�������ַ���
	 * @param _par
	 * @param _separator
	 * @return
	 */
	public String[] split1(String _par, String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		_par = replaceAll(_par.trim(), "\r", "");
		_par = replaceAll(_par, "\n", "");
		_par = replaceAll(_par, "\\" + _separator, "$��$");
		String[] str_pars = split(_par, _separator); //
		for (int i = 0; i < str_pars.length; i++) {
			str_pars[i] = replaceAll(str_pars[i].trim(), "$��$", _separator);
		}
		return str_pars;
	}

	/**
	 * ��һ�����ַ��ָ�,ָ��һ���м���,ÿһ�еĿ��!���ǳ����ý�һ�����ı��ָ��������ݵķ�ʽ�洢��һ�ű���!!
	 * 
	 * @param _str
	 * @param _oneRowCols
	 * @param _oneColWidth
	 * @return
	 */
	public ArrayList split(String _str, int _oneRowCols, int _oneColWidth) {
		// ѹ����byteÿ4000��ת����һ��16���Ƶ��ַ���ת�����ַ���! Ȼ��ÿ50��ƴ��һ��SQL! �����뵽����!!!
		int li_fileLength = _str.length(); // ���!
		int li_allColCount = li_fileLength / _oneColWidth; // �ж�����
		int li_left = li_fileLength % _oneColWidth; // ����!
		if (li_left != 0) { // ���������!
			li_allColCount = li_allColCount + 1; // ������1
		}

		int li_rows = li_allColCount / _oneRowCols; // ������!!
		if (li_allColCount % _oneRowCols != 0) {
			li_rows = li_rows + 1;
		}

		ArrayList al_data = new ArrayList(); //
		for (int i = 0; i < li_rows; i++) { // ����ÿһ��!
			int li_beginCol = i * _oneRowCols; // �ۼ�!
			int li_thisRowCols = 0; // Ĭ����50,��ȫ��!
			if (i == li_rows - 1) { // ��������һ��,�������е������!!
				li_thisRowCols = li_allColCount - li_beginCol; // ��β��!!
			} else {
				li_thisRowCols = _oneRowCols; // Ĭ�Ͼ��Ǳ������
			}
			String[] str_rowData = new String[li_thisRowCols]; // ��һ�е�����!!!
			for (int j = 0; j < li_thisRowCols; j++) { // ��������
				int li_substr_1 = (li_beginCol + j) * _oneColWidth; // ��ȡ�ַ�������ʼλ�õľ���ֵ!!!
				int li_substr_2 = 0;
				if (i == li_rows - 1 && j == li_thisRowCols - 1) { // ��������һ�е����һ��,����ܲ���!!
					li_substr_2 = li_fileLength; // ��ֱ���ļ��Ľ�β!
				} else {
					li_substr_2 = (li_beginCol + j + 1) * _oneColWidth; //
				}
				str_rowData[j] = _str.substring(li_substr_1, li_substr_2); //
			}
			al_data.add(str_rowData); // ������һ������!!!
		}
		return al_data;
	}

	public String subStrByGBLength(String _str, int _limitLen) {
		return subStrByGBLength(_str, _limitLen, false);
	}

	/**
	 * ��һ���ַ������н�ȡָ���Ŀ��,���������ĵļ��㷽ʽ! ����������������,һ���ַ�ȡǰ15λ,����Ϊ������������Ҳ��Ӣ��,������2λ,Ӣ����1λ!
	 * �ǰ��������㷨�����! ������ֱ�ӵ�substring(0,15),��Ϊ���������,��ȡ������30λ���! ���ݿ�Ϳ��ܴ治����!
	 * �ϴ��ļ�,�Դ��ַ���д���ݿ�ǰ���н�ȡ,��������Ҫ����㷨!!!
	 * 
	 * @param _str
	 * @param _limitLen
	 * @param _isAppendDot,
	 *            �����ȷ��Ҫ��ȡ,���Ƿ��ʡ�Ժ�,����������֪���Ƿ����˽�ȡ..
	 * @return
	 */
	public String subStrByGBLength(String _str, int _limitLen, boolean _isAppendDot) {
		if (_str == null || _str.equals("")) {
			return _str;
		}
		int li_strlength = _str.length(); //
		if (li_strlength * 2 <= _limitLen) { // �����ķ�Χ̫����,��ʹ�Ҷ�������,��ÿһλ������λ(length()*2),ҲûҪ��ķ�Χ��,��ֱ�ӷ����ַ���,����������Ҫ������!!!
			return _str; //
		}
		int li_substrPos = getSubStrPosByGBLength(_str, _limitLen); // ȡ��ʵ�ʽ�ȡ��λ��!!
		if (_isAppendDot && li_substrPos != li_strlength && (li_strlength - 3) > 0) { // �����Ҫ��ʡ�Ժ�,����ʵ������Ҫ���н�ȡ,�򲹳�
			return _str.substring(0, li_substrPos - 3) + "..."; // ����ǰ��3λȻ�����������,��ʾʡ�Ժ�!!!
			// ��ʵ���õ��㷨�ǻ�Ҫ�ж������λ��ʵ�ʳ���,��������ĵĻ�,�����ֻҪ����1-2��!
			// ����Ժ����Ż�!
		} else {
			return _str.substring(0, li_substrPos); // �����ȷ��̫����,��Ҫ��ȡ,���ҵ��˽�ȡ��λ��!!!�򷵻�
		}
	}

	/**
	 * ȡ��һ���ַ����������ĳ��ȼ��㷽���������ָ�������ڵ�λ��! Ϊ�˱�֤�ܲ������ݿ�,��������Ҫ��һ���ַ������н�ȡ! ���������ĳ���λ�Ľط�!
	 * �÷����ǽ������ķ������õ�,��ʵ���ǵ��������淽������!!!�����ǵ�Ҳ�п�����Ҫ�÷���,���Ի��Ƿֳ�����������!!!
	 */
	public int getSubStrPosByGBLength(String _str, int _limitLen) {
		int li_strlength = _str.length(); //
		if (li_strlength * 2 <= _limitLen) { // �����ķ�Χ̫����,��ʹ�Ҷ�������,��ÿһλ������λ(length()*2),ҲûҪ��ķ�Χ��,��ֱ�ӷ����ַ���,����������Ҫ������!!!
			return li_strlength; //
		}
		int li_lenCount = 0; // ������!!!
		int li_substrPos = -1; // ��ʵ��Ҫ��ȡ��λ��!
		for (int i = 0; i < li_strlength; i++) { // �����3λ(abc),0[0-1],1[1-2],2[2-3]
			if (_str.charAt(i) >= 0x100) { // ���������
				li_lenCount = li_lenCount + 2; //
			} else { // �����Ӣ��!
				li_lenCount = li_lenCount + 1; //
			}
			if (li_lenCount > _limitLen) { // ������������������,���˳�!!
				li_substrPos = i; //
				break; //
			}
		}
		if (li_substrPos == -1) { // ���û�г�������,��ֱ����������!!!!
			li_substrPos = li_strlength; //
		}
		return li_substrPos;
	}

	/**
	 * ����ĳ�����صĸ���
	 * 
	 * @param _par
	 * @param _separator
	 * @return
	 */
	public int findCount(String _par, String _separator) {
		if (_par == null) {
			return 0;
		}
		if (_par.trim().equals("")) {
			return 0;
		}
		if (_par.indexOf(_separator) < 0) {
			return 0;
		}

		int li_count = 0; //
		String str_remain = _par; //
		int li_pos = str_remain.indexOf(_separator); //
		while (li_pos >= 0) {
			li_count++; // ֻҪ���־��ۼ�1
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		return li_count; //

	}

	/**
	 * �ж�һ���ַ����Ƿ�Ϊ��!!
	 * 
	 * @param _value
	 * @return
	 */
	public static boolean isEmpty(String _value) {
		if (_value == null || _value.trim().equals("")) { // ���Ϊnull����Ϊ���ַ���...
			return true;
		} else {
			return false;
		}
	}

	// �ж�һ���ַ����Ƿ�������!! ��ÿһλ����0-9�е�һ��! 
	//ԭ���ж�����Ҳ���ж�Ϊȫ���ֵ�bug��������Ϊ��������ʽ�ж� 2015-01-07
	public boolean isStrAllNunbers(String _str) {
		if (isEmpty(_str)) {
			return false;
		}
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		return _str.matches(regex);
	}

	// �ж�һ���ַ����Ƿ���ĳЩ���͵Ŀ�ʼ��,���� N_,...Ĭ�����ִ�Сд!!
	public boolean isStartWithInArray(String _item, String[] _array) {
		return isStartWithInArray(_item, _array, false); //
	}

	// �ж�һ���ַ����Ƿ���ĳЩ���͵Ŀ�ʼ��,���� N_,...Ĭ�����ִ�Сд!!
	public boolean isStartWithInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null) {
				if (_isIgnoreCase) { // ������Դ�Сд
					if (_item.toLowerCase().startsWith(_array[i].toLowerCase())) { // �������˶�ת��Сд!!!
						return true;
					}
				} else { // ������ִ�Сд
					if (_item.startsWith(_array[i])) {
						return true;
					}
				}
			}
		}
		return false; //
	}

	// �ж�һ���ַ����Ƿ���ĳЩ���͵Ľ�β��,���� .doc,.xls,.ppt��!!Ĭ�����ִ�Сд!!
	public boolean isEndWithInArray(String _item, String[] _array) {
		return isEndWithInArray(_item, _array, false); //
	}

	// �ж�һ���ַ����Ƿ���ĳЩ���͵Ľ�β��,���� .doc,.xls,.ppt��!!
	public boolean isEndWithInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null) {
				if (_isIgnoreCase) { // ������Դ�Сд
					if (_item.toLowerCase().endsWith(_array[i].toLowerCase())) { // �������˶�ת��Сд!!!
						return true;
					}
				} else { // ������ִ�Сд
					if (_item.endsWith(_array[i])) {
						return true;
					}
				}
			}
		}
		return false; //
	}

	// �ж�ĳһ���ַ����Ƿ������һ��һά������!
	public boolean isExistInArray(String _item, String[] _array) {
		return isExistInArray(_item, _array, false); //
	}

	// �ж�ĳһ���ַ����Ƿ������һ��һά������!
	public boolean isExistInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		if (_array == null || _array.length <= 0) {
			return false;
		}
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null && _array[i].equalsIgnoreCase(_item)) {
				if (_isIgnoreCase) { // ����Ǻ��Դ�Сд!!!!
					if (_array[i].equalsIgnoreCase(_item)) {
						return true;
					}
				} else {
					if (_array[i].equals(_item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * ������������ԱȽ�,���������1�Ƿ����������2�е�һ��!�������1�е���һ��������2�е�һ���Ӧ����,�򷵻�true.
	 * 
	 * @param _contain
	 *            ��������
	 * @param _content
	 *            ��������
	 * @return
	 */
	public boolean containTwoArrayCompare(String[] _contain, String[] _content) {
		for (int i = 0; i < _contain.length; i++) {
			for (int j = 0; j < _content.length; j++) {
				if (_contain[i] != null && _contain[i].equals(_content[j])) { // ���������,����������
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ȡ�������ַ����еĽ���!!!!
	 */
	public String[] getInterSectionFromTwoArray(String[] _array1, String[] _array2) {
		LinkedHashSet set_return = new LinkedHashSet(); ////
		for (int i = 0; i < _array1.length; i++) { //
			for (int j = 0; j < _array2.length; j++) {
				if (_array1[i] != null && _array1[i].equals(_array2[j])) {
					set_return.add(_array1[i]); //
					break; //
				}
			}
		}
		return (String[]) (set_return.toArray(new String[0])); //
	}

	/**
	 * ȡ�������ַ����еĲ���!!!!
	 */
	public String[] getSpanFromTwoArray(String[] _array1, String[] _array2) {
		LinkedHashSet set_return = new LinkedHashSet(); ////
		if (_array1 != null) {
			for (int i = 0; i < _array1.length; i++) { //
				if (_array1[i] != null) {
					set_return.add(_array1[i]); //
				}
			}
		}
		if (_array2 != null) {
			for (int i = 0; i < _array2.length; i++) { //
				if (_array2[i] != null) {
					set_return.add(_array2[i]); //
				}
			}
		}
		return (String[]) (set_return.toArray(new String[0])); //
	}

	/**
	 * ��һ���ַ���ת����16�����ַ���������,������1A3D4E������!!16���Ƶĺô��ǳ���ȥ������������ĸ��ַ���,��ȱ������Ҫ������λ,�˷ѿռ�!!
	 * ����һ������ռ�����ֽ�,һ���ֽڷ���������ĸ,��һ��Ӣ����ĸ����2λ,һ�����ַ���4����ĸ!! ����:
	 * a=[61],b=[62],c=[63],��=[D6D0],��=[B9FA]
	 * 
	 * @param _str
	 *            ԭ�����ַ���!
	 * @return 16������
	 */
	public String convertStrToHexString(String _str) {
		try {
			byte[] bytes = _str.getBytes("GBK"); //
			if (bytes == null) {
				return null;
			}
			return convertBytesToHexString(bytes);
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public String convertBytesToHexString(byte[] _bytes) {
		return convertBytesToHexString(_bytes, false); //
	}

	/**
	 * ��һ���ַ���ת����64λ����!! ������Ҫ��һЩ�������ļ�ת���ַ����洢! 64λ�������Լ�ռ��!
	 * ��64λ����/,+���������!���ʺϴ����ļ���!!!�������������ʹ��16������!
	 */
	public String convertStrTo64Code(String _str) {
		try {
			return convertBytesTo64Code(_str.getBytes("GBK")); //
		} catch (Exception e) {
			e.printStackTrace(); //
			return null; //
		}
	}

	// ���ֽ���ת����64λ��!������Ҫ�õ�!���罫�ļ������ַ�����ʽ�洢ʱ! ���罫�ļ��ϴ������ݿ���!!
	public String convertBytesTo64Code(byte[] _bytes) {
		return new sun.misc.BASE64Encoder().encode(_bytes); // 64λ��
	}

	// ��64λ��ת����ʵ���ַ���!
	public String convert64CodeToStr(String _64Code) {
		try {
			byte[] bytes = convert64CodeToBytes(_64Code); //
			if (bytes == null) {
				return null;
			}
			return new String(bytes, "GBK"); //
		} catch (Exception e) {
			e.printStackTrace(); //
			return null; //
		}
	}

	// ��64λ��ת����byte[],������Ҫ�õ�!
	public byte[] convert64CodeToBytes(String _64Code) {
		try {
			byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(_64Code); //
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ��һ������������ת����16�����ַ���
	 * 
	 * @param _bytes
	 * @return
	 */
	public String convertBytesToHexString(byte[] _bytes, boolean _isPrefix) {
		if (_bytes == null) {
			return null;
		}

		try {
			byte ch = 0x00;
			int i = 0;
			if (_bytes == null || _bytes.length <= 0) {
				return null;
			}
			String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
			StringBuffer out = new StringBuffer(_bytes.length * 2); //
			while (i < _bytes.length) {
				if (_isPrefix) {
					out.append("\\\\x"); // "\\\\x"
				}
				ch = (byte) (_bytes[i] & 0xF0); // Strip offhigh nibble
				ch = (byte) (ch >>> 4); // shift the bits down
				ch = (byte) (ch & 0x0F); // must do this is high order bit is
				// on!
				out.append(pseudo[(int) ch]); // convert the nibble to a
				// String
				// Character
				ch = (byte) (_bytes[i] & 0x0F); // Strip offlow nibble
				out.append(pseudo[(int) ch]); // convert the nibble to a
				// String
				// Character
				i++;
			}
			return out.toString();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ��һ��16���Ƹ�ʽ���ַ���ת����ԭʼ���ַ�����ʽ!!!!
	 * 
	 * @return
	 */
	public String convertHexStringToStr(String _hexText) {
		try {
			byte[] byte_return = convertHexStringToBytes(_hexText);
			if (byte_return == null) {
				return null;
			}
			String str_return = new String(byte_return, "GBK"); //
			return str_return; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ��16�����ַ���ת���ɶ���������
	 * 
	 * @param _hexText
	 * @return
	 */
	public byte[] convertHexStringToBytes(String _hexText) {
		if (_hexText == null) {
			return null;
		}

		try {
			String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
			byte[] byte_return = new byte[_hexText.length() / 2]; //
			for (int r = 0; r < _hexText.length() / 2; r++) {
				String str_item = _hexText.substring(r * 2, r * 2 + 2); // ////
				String str_1 = str_item.substring(0, 1); //
				String str_2 = str_item.substring(1, 2); //
				int li_pos_1 = 0; //
				for (int i = 0; i < pseudo.length; i++) {
					if (pseudo[i].equalsIgnoreCase(str_1)) {
						li_pos_1 = i;
						break;
					}
				}

				int li_pos_2 = 0; //
				for (int i = 0; i < pseudo.length; i++) {
					if (pseudo[i].equalsIgnoreCase(str_2)) {
						li_pos_2 = i;
						break;
					}
				}

				int li_value = 16 * li_pos_1 + li_pos_2; //
				byte by_value = (byte) li_value; //
				byte_return[r] = by_value; //
			}

			return byte_return;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ���̵��ַ��������ǰ��ӿո񣬱�ɲ�С��ĳ���ȵ��ַ���
	 * 
	 * @param _oldstr
	 *            ԭ�ַ���
	 * @param _length
	 *            ��̳���
	 * @param _prefix
	 *            ԭ�ַ����Ƿ���ǰ
	 * @return
	 */
	public String changeStrToLonger(String _oldstr, int _length, boolean _prefix) {
		if (_oldstr == null) {
			_oldstr = "";
		}
		int i = _oldstr.getBytes().length;
		StringBuffer sb_str = new StringBuffer();
		for (; i < _length; i++) {
			sb_str.append(" ");
		}
		if (_prefix) {
			return sb_str.insert(0, _oldstr).toString();
		}
		return sb_str.append(_oldstr).toString();
	}

	/**
	 * ��һ���ض���ʽ���﷨ת����һ����ϣ��,�ַ����ĸ�ʽ�ǡ�12=����;15=����;17=����;��,�����Էֺ����,Ȼ����һ�ԶԵ���"="����Ĺؼ�ֵ,�Ⱥ���ߵ���key,�ұߵ���value��
	 * 
	 * @param _str
	 * @return
	 */
	public HashMap convertStrToMapByExpress(String _str) {
		return convertStrToMapByExpress(_str, ";", "=");
	}

	public HashMap convertStrToMapByExpress(String _str, String _split1, String _split2) {
		return convertStrToMapByExpress(_str, _split1, _split2, false); //
	}

	public HashMap convertStrToMapByExpress(String _str, String _split1, String _split2, boolean _keyLowcase) {
		LinkedHashMap map = new LinkedHashMap(); //��˳��!
		if (_str == null || _str.trim().equals("")) {
			return map;
		}
		String[] str_items = split(_str, _split1); //
		for (int i = 0; i < str_items.length; i++) {
			String[] str_items2 = split(str_items[i], _split2); //
			if (str_items2.length >= 2) {
				if (_keyLowcase) { // ���ҪתСд
					map.put(str_items2[0].toLowerCase(), str_items2[1]); // //�����ϣ��!!!!
				} else {
					map.put(str_items2[0], str_items2[1]); // //�����ϣ��!!!!
				}
			}
		}
		return map;
	}

	// ��һ���ַ�������Ψһ�Թ���
	public String[] distinctStrArray(String[] _str) {
		return distinctStrArray(_str, true); //
	}

	// ��һ���ַ�������Ψһ�Ժϲ��󷵻�
	public String[] distinctStrArray(String[] _str, boolean _isTrim) {
		if (_str == null || _str.length <= 0) {
			return _str;
		}
		LinkedHashSet hst_str = new LinkedHashSet(); //
		for (int i = 0; i < _str.length; i++) {
			if (_isTrim) { // ����Ǻ��Կո�,�����Զ�ȥͷβ�Ŀո�
				if (_str[i] != null && !_str[i].trim().equals("")) { // ��������ǿ�ֵ��մ�!!!
					hst_str.add(_str[i].trim()); //
				}
			} else {
				if (_str[i] != null) {
					hst_str.add(_str[i]); //
				}
			}
		}
		return (String[]) hst_str.toArray(new String[0]); //
	}

	// ��һ���ַ�������������һ����ϣ��,�ڹ�ʽ�����о�������������,��������ʽ�� par1=Y;par2=aaa;par3=bbb;par4=1,2,3
	// ������ַ������������Էֺŷָ�,Ȼ��ÿһ���������ԵȺ����!!!!
	public HashMap parseStrAsMap(String _str) {
		HashMap returnMap = new HashMap(); // �ȴ�����ϣ��
		String[] str_items = split(_str, ";"); // �����Էֺŷָ�
		if (str_items == null || str_items.length <= 0) {
			return returnMap;
		}
		String str_itemkey = null;
		String str_itemvalue = null;
		for (int i = 0; i < str_items.length; i++) {
			if (str_items[i] != null && str_items[i].indexOf("=") > 0) { // ����е��ں�
				str_itemkey = str_items[i].substring(0, str_items[i].indexOf("=")); // �Ⱥ�ǰ��
				str_itemvalue = str_items[i].substring(str_items[i].indexOf("=") + 1, str_items[i].length()); // �Ⱥź��
				returnMap.put(str_itemkey, str_itemvalue); // �����ϣ��
			}
		}
		return returnMap;
	}

	/**
	 * ��һ���ַ��������������,��ΪĬ�ϵ�Arrays.sort(String[])�ڴ�������ʱ����,������÷���
	 * ������������������ʱ���Կ�����Arrays.sort(double[])
	 * 
	 * @param _str
	 */
	public void sortStrs(String[] _str) {
		Arrays.sort(_str, new StrComparator()); //
	}

	/**
	 * ʹ��һ��ָ����˳���һ���ַ�������,����ָ��һ��˳������{"����","��ר","��ר","����","˶ʿ","��ʿ"}
	 * Ȼ���ʵ��ֵ����˳���������ʵ��ֵ���ܶ��ڻ����ڸ�����ĸ�������ֻҪ���������ڸ������е�ֵ�������˳���Ȼ�ǰ���˳�����!!
	 * 
	 * @param _str
	 *            ʵ��ֵ,
	 * @param _orders
	 *            ָ����˳��,�������㱨����,������ɵ������_str������
	 */
	public void sortStrsByOrders(String[] _str, String[] _orders) {
		Arrays.sort(_str, new StrComparator(_orders)); //

	}

	/**
	 * ��һ��HashVO��������,ʹ�÷����� sortHashVOs(hvs,new
	 * String[][]{{"code","N","N"},{"age","Y","Y"}});
	 * 
	 * @param _hvs,
	 *            ������Ҫ����Ĺ�ϣ����..
	 * @param
	 *            _sortColumns,ָ��������,n��3�е�����,��һ������ֵ��ʾ�Ƿ��ǵ���(Y=����,N=����),�ڶ�������ֵ��ʾ�Ƿ���������(Y=����,N=�ַ�),���磺
	 *            new String[][]{{"code","N","N"},{"age","Y","Y"}}
	 *            //�ȶԹ��Ű�������(�ַ���),�����ͬ�ٰ����䵹����(������)
	 */
	public void sortHashVOs(HashVO[] _hvs, String[][] _sortColumns) {
		Arrays.sort(_hvs, new HashVOComparator(_sortColumns));
	}

	//
	public void sortHashVOs(HashVO[] _hvs, String[][] _sortColumns, String itemKey, String[] orders) {
		Arrays.sort(_hvs, new HashVOComparator(_sortColumns, itemKey, orders));
	}

	/**
	 * ��һ��HashVO��������,ʹ�÷����� sortHashVOs(hvs,new
	 * String[][]{{"code","N","N"},{"age","Y","Y"}});
	 * 
	 * @param _hvs,
	 *            ������Ҫ����Ĺ�ϣ����..
	 * @param
	 *            _sortColumns,ָ��������,n��3�е�����,��һ������ֵ��ʾ�Ƿ��ǵ���(Y=����,N=����),�ڶ�������ֵ��ʾ�Ƿ���������(Y=����,N=�ַ�),���磺
	 *            new String[][]{{"code","N","N"},{"age","Y","Y"}}
	 *            //�ȶԹ��Ű�������(�ַ���),�����ͬ�ٰ����䵹����(������)
	 */
	public void sortTableDataStruct(TableDataStruct _tds, String[][] _sortColumns) {
		Arrays.sort(_tds.getBodyData(), new TableDataStructComparator(_tds.getHeaderName(), _sortColumns));
	}

	/**
	 * ɾ��HashVO�е���
	 * 
	 * @param _hvs
	 * @param _itemkeys
	 */
	public void removeHashVOItems(HashVO[] _hvs, String[] _itemkeys) {
		for (int i = 0; i < _hvs.length; i++) {
			for (int j = 0; j < _itemkeys.length; j++) {
				_hvs[i].removeItem(_itemkeys[j]); //
			}
		}
	}

	/**
	 * ɾ��HashVO�е���
	 * 
	 * @param _hvs
	 * @param _itemkeys
	 */
	public void removeHashVOItems(HashVO[] _hvs, String _itemkey) {
		removeHashVOItems(_hvs, new String[] { _itemkey });
	}

	/**
	 * 
	 * @param _hvs
	 * @param _
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String _oldValue, String _newValue, String _others) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].containsKey(_fromItemKey)) { // �����������
				String str_oldValue = _hvs[i].getStringValue(_fromItemKey); //
				if (_oldValue.equals(str_oldValue)) { // ���Ʒ����
					_hvs[i].setAttributeValue(_fromItemKey, _newValue); //
				} else {
					_hvs[i].setAttributeValue(_fromItemKey, _others); //
				}
			}
		}
	}

	/**
	 * �滻HashVO[]�е�ֵ..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _replaceValue,��n��,2�е��ַ�������.
	 *            ���� new String[][]{{"Y","����"},{"N","δ����"}}
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String[][] _replaceValue) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].containsKey(_fromItemKey)) { // �����������
				String str_oldValue = _hvs[i].getStringValue(_fromItemKey); //
				for (int j = 0; j < _replaceValue.length; j++) { //
					if (_replaceValue[j][0] == null && str_oldValue == null) {
						_hvs[i].setAttributeValue(_fromItemKey, _replaceValue[j][1]); //
					} else if (_replaceValue[j][0].equals(str_oldValue)) {
						_hvs[i].setAttributeValue(_fromItemKey, _replaceValue[j][1]); //
					}
				}
			}
		}
	}

	/**
	 * ȡ��һ���ַ�������{}���������к���������,����{aa} and {bb} �ͷ���[aa][bb]
	 * 
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String[] getFormulaMacPars(String _sql) throws Exception {
		return getFormulaMacPars(_sql, "{", "}"); //
	}

	/**
	 * ��һ���ַ����ҵ���ǰ���Ʒ���һ�κ����,����������
	 * 
	 * @param _inittext
	 * @param _prefix
	 * @param _subfix
	 * @return
	 * @throws Exception
	 */
	public String[] getFormulaMacPars(String _inittext, String _prefix, String _subfix) throws Exception {
		ArrayList al_temp = new ArrayList(); //
		String str_remain = _inittext;
		int li_pos_1 = str_remain.indexOf(_prefix); //
		while (li_pos_1 >= 0) {
			String str_leftsubfix = str_remain.substring(li_pos_1 + _prefix.length(), str_remain.length()); //
			int li_pos_2 = str_leftsubfix.indexOf(_subfix); // �����к��!!!������һ��!
			if (li_pos_2 < 0) {//{}Ҳ������ġ���<=0��Ϊ<0�����򱨴�[����2012-04-18]
				throw new Exception("��ʽ[" + _inittext + "]��䲻�Ϸ�,ĳ��ǰ�û�ж�Ӧ�ĺ��!!!!!");
			}
			al_temp.add(str_leftsubfix.substring(0, li_pos_2)); // ��ȡ!
			str_remain = str_leftsubfix.substring(li_pos_2 + _subfix.length(), str_leftsubfix.length()); //
			li_pos_1 = str_remain.indexOf(_prefix); //
		}
		return (String[]) al_temp.toArray(new String[0]); //
	}

	// ȡ�����!
	public String[] getMacroList(String _inittext) {
		return getMacroList(_inittext, "${", "}"); //
	}

	/**
	 * �귵��һ�����ش��а���${dsdfsd}�ķ����봮�б�
	 * 
	 * @return �ַ���,������${dssss}ר���ڳ����γ�һ��! �����"abcd${123}dfwer${98432}kpsdf";
	 *         ����abcd/${123}/dfwer/${98432}/kpsdf
	 */
	public String[] getMacroList(String _inittext, String _str1, String _str2) {
		ArrayList al_text = new ArrayList(); // //
		int li_type = 1; //
		String str_remain = _inittext; //
		for (;;) { // ��ѭ������!!
			if (li_type == 1) {
				int li_pos = str_remain.indexOf(_str1); // ����ҵ�
				if (li_pos >= 0) {
					if (li_pos > 0) {
						al_text.add(str_remain.substring(0, li_pos)); // ����
					}
					str_remain = str_remain.substring(li_pos, str_remain.length()); // ʣ�µ�
					li_type = 2; // ָ����һѭ������}
				} else {
					break;
				}
			} else if (li_type == 2) {
				int li_pos = str_remain.indexOf(_str2); // ����ҵ�
				if (li_pos >= 0) {
					al_text.add(str_remain.substring(0, li_pos + 1)); // ����
					str_remain = str_remain.substring(li_pos + 1, str_remain.length()); //
					li_type = 1; // ָ����һѭ����ͷ
				} else {
					break;
				}
			}
		}
		al_text.add(str_remain); //
		return (String[]) al_text.toArray(new String[0]);
	}

	public String convertDataSourceName(CurrSessionVO _currSessionVO, String _dsn) {
		try {
			if (_dsn.startsWith("\"")) {
				return "" + new JepFormulaParseAtUI(null).execFormula(_dsn); //
			} else {
				return _dsn;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return _dsn; // //
		} //
	}

	/**
	 * ת����ʽ�к���������,Ϊ�ͻ��˻����ҳ���ϵ�ֵ!!!
	 * 
	 * @param _inittext
	 * @param _env
	 * @param _map
	 * @return
	 * @throws Exception
	 */
	public String convertFormulaMacPars(String _oldsql, CurrSessionVO _currSessionVO, HashMap _map) throws Exception {
		String str_newformula = _oldsql;
		String[] str_allkeys = getFormulaMacPars(str_newformula); //

		for (int i = 0; i < str_allkeys.length; i++) {
			String str_key = str_allkeys[i]; // key��!!!!

			String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // ���ȴӿͻ��˻���ȡ��!!
			if (str_clientenvvalue != null) { // ����ӿͻ��˻���ȡ������!!
				str_newformula = replaceAll(str_newformula, "{" + str_key + "}", str_clientenvvalue); // �滻
			} else // ��ҳ��ؼ�ȡ
			{
				int li_pos = str_key.indexOf("."); // ����û��"�����"??
				String str_itemvalue = "";
				if (li_pos <= 0) { // ���û�е����!!��ֱ��ȡ
					Object obj = _map.get(str_key);
					if (obj != null) {
						if (obj instanceof String) {
							str_itemvalue = "" + obj; //
						} else if (obj instanceof ComBoxItemVO) { // �����������
							str_itemvalue = ((ComBoxItemVO) obj).getId(); //
						} else if (obj instanceof RefItemVO) { // ����ǲ���!!
							str_itemvalue = ((RefItemVO) obj).getId(); //
						} else {
							str_itemvalue = "" + obj;
						}
					} else {

					}
				} else // ����е����!!
				{
					String str_key_prefix = str_key.substring(0, li_pos); // ǰ�
					String str_key_subfix = str_key.substring(li_pos + 1, str_key.length()); // ���

					Object obj = _map.get(str_key_prefix); //
					if (obj != null) {
						if (obj instanceof String) {
							str_itemvalue = "" + obj; //
						} else if (obj instanceof ComBoxItemVO) { // �����������
							str_itemvalue = ((ComBoxItemVO) obj).getItemValue(str_key_subfix); //
						} else if (obj instanceof RefItemVO) { // ����ǲ���!!
							str_itemvalue = ((RefItemVO) obj).getItemValue(str_key_subfix); //
						} else {
							str_itemvalue = "" + obj;
						}
					} else {
					}
				}

				str_newformula = replaceAll(str_newformula, "{" + str_key + "}", str_itemvalue); // ��ҳ��ؼ���ֵ�滻!!!
			}
		} // end for ѭ��
		return str_newformula; //
	}

	/**
	 * ����һ������,ƴ��һ��select a,b,c from table�е���Щ�е��ַ���!!
	 * 
	 * @param _allcols
	 * @return
	 */
	public String getSelectCols(String[] _allcols) {
		String str_return = " "; // һ��ʼ�и��ո�
		for (int i = 0; i < _allcols.length; i++) {
			if (i == _allcols.length - 1) {
				str_return = str_return + _allcols[i]; //
			} else {
				str_return = str_return + _allcols[i] + ","; //
			}
		}

		str_return = str_return + " "; // ����ټӸ��ո�
		return str_return;
	}

	/**
	 * ȡ��In����
	 * 
	 * @param _initcondition
	 * @return
	 */
	public String getInCondition(String _initcondition) {
		if (_initcondition == null || _initcondition.trim().equals("")) {
			return "'-99999'"; //
		} else {
			String[] str_items = split(_initcondition, ";"); //
			return getInCondition(str_items); //
		}
	}

	/**
	 * ��һ������ת����in ('a','b')������!
	 * 
	 * @param _allcols
	 * @return
	 */
	public String getInCondition(String[] _allvalue) {
		if (_allvalue == null || _allvalue.length == 0) {
			return "'-99999'"; // ����PowerBuild���쳣����,��-5��9
		} else {
			java.util.List list = Arrays.asList(_allvalue); // ת����һ��List
			return getInCondition(list); //
		}
	}

	/**
	 * ƴ��Like or����!
	 * 
	 * @param _colKey
	 * @param _condition
	 * @return
	 */
	public String getLikeOrCondition(String _colName, String[] _condition) {
		if (_condition == null || _condition.length <= 0) {
			return "3=4"; // ���Ϊ��,��ֱ�ӷ���һ���϶�ƥ�䲻�ϵ�!
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append(" ("); // ������
		for (int i = 0; i < _condition.length; i++) { //
			if (_condition[i] == null || _condition[i].trim().equals("")) {
				if (i == 0) {
					sb_sql.append("3=4 "); //
				} else {
					sb_sql.append("or 3=4 "); //
				}
			} else {
				if (i == 0) {
					sb_sql.append(_colName + " like '" + _condition[i] + "%' "); //
				} else {
					sb_sql.append("or " + _colName + " like '" + _condition[i] + "%' "); //
				}
			}
		}
		sb_sql.append(") ");
		return sb_sql.toString(); //
	}

	/**
	 * �����п�ֵ��ƴSQL,����where id='',�� where id='null',�ܲ�����!
	 * ������SQLserver,����ֶ�������Number��,���SQL�� where id='' �������ᱨ��! Oracle�Ͳ���,��ʵ�鷳!!
	 * Ϊ�˼����������ݿ�,��Ҫ�����ֿ�ֵ����ת��! ������ǿ�ֵ��ת��ͳһת��'-99999',�����ֵ��ƴ��ʵ��ֵ!
	 * �������ܼ����������ݿ�,Ҳ���ᱨ��! ���ҿ���-99999��֪����ʵ�ǿ�ֵ! "where id='" +
	 * tbUtil.getNullCondition(prinstanceId) + "'"
	 * 
	 * @param _nullId
	 * @return
	 */
	public String getNullCondition(String _nullId) {
		if (_nullId == null || _nullId.trim().equals("") || _nullId.trim().equalsIgnoreCase("null")) { // ����ǿ�ֵ!
			return "-99999"; // ����PowerBuild���쳣����,��-5��9,�������������ֻ����ַ��͵�,SQL�����ᱨ��!
			// �������ڲ�����-99999�ļ�¼,ҵ���߼���Ҳ�ǶԵ�! �ؼ����������ݿⶼͨ��!!!
		}
		return _nullId; // �����Ϊ��,�򷵻�ԭ����ֵ!!
	}

	/**
	 * ��һ������ת����in ('a','b')������!
	 * 
	 * @param _allcols
	 * @return
	 */
	public String getInCondition(java.util.List _sqllist) {
		try {
			if (_sqllist == null || _sqllist.size() == 0) {
				return "'-99999'"; //
			} else {
				String[] str_ids = (String[]) _sqllist.toArray(new String[0]); //
				str_ids = distinctStrArray(str_ids); // Ψһ�Թ���
				if (str_ids == null || str_ids.length <= 0) { //Ψһ�Թ��˺�,����������ȫ������0,����ԭ���Ĳ�������һ��String[]{""},��ҵ��Ŀ�к����ͷ��� ��where id in ()�������,���Ա���Ҫ������ж�!!��xch/2012-04-28��
					return "'-99999'"; //
				}
				if (str_ids.length <= 999) { // �����999������,��ֱ��ƴ��������!!!!!!������̫����,��������ֱ��in�����!!!
					StringBuffer sb_alltext = new StringBuffer(); //
					for (int i = 0; i < str_ids.length; i++) { // //
						sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); //���뽫nullת����"",��������where id in ('12','null','15')���ᱨ��
						if (i != str_ids.length - 1) { // ����������һ������϶���!!!!!
							sb_alltext.append(","); // ����������һ��,��Ӷ���!!!!!
						}
					}
					return sb_alltext.toString(); //
				} else { // �������1000����ʹ�ò������ݿ�ļ���,��ΪOracle�ڳ���1000��in����ʱ�ᱨ��!!!��Mysqlȴ�ǿ��Ե�!!
					int li_jvm = getJVSite();
					if (li_jvm == TBUtil.JVMSITE_CLIENT) { // �����ǰ�ǿͻ���
						return UIUtil.getSubSQLFromTempSQLTableByIDs(str_ids); //
					} else { // �����ǰ�Ƿ�������,��ֱ��New,��JDK1.5��,��ClassLoader�ᵼ�¿ͻ�������CommDMO��,��jdk1.5�ǽ���Classʱ�ͼ�����,��JDK1.6�ͱ���˰������,��ֻ�е��õ����д���Ż����CommDMO��,���Կ�������д,�������Ŀͻ��ʻ�����CommDMO.class��!!
						return new cn.com.infostrategy.bs.common.CommDMO().getSubSQLFromTempSQLTableByIDs(str_ids); //
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "'-99998'"; //
		}
	}

	public String getInCondition(HashVO _hashvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getHashVosItemList(_hashvo, _item));
	}

	public String getInCondition(BillVO billvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getBillVosItemList(billvo, _item));
	}

	public List getHashVosItemList(HashVO _vos[], String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++)
			l.add(_vos[i].getStringValue(_item));

		return l;
	}

	public List getBillVosItemList(BillVO _vos[], String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++)
			l.add(_vos[i].getStringValue(_item));

		return l;
	}

	// ��ʱΪ���������,ʹ��Hashmap���ٲ���,��Ҫ��һ��HashVO[]��ĳ����ת����һ��HasMap
	public HashMap getHashMapFromHashVOs(HashVO[] _hvs, String _keyField, String _valueField) {
		// long ll_1 = System.currentTimeMillis(); //
		HashMap map = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) { // ������������
			if (_valueField.equalsIgnoreCase("$rownum")) { // ������������,��ʾ�к�
				map.put(_hvs[i].getStringValue(_keyField), new Integer(i)); // //
			} else {
				map.put(_hvs[i].getStringValue(_keyField), _hvs[i].getStringValue(_valueField)); // //
			}
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("��[" + _hvs.length + "]������ת����HashMap��ʱ[" + (ll_2 -
		// ll_1) + "]");
		// //һ����˵���ұʼǱ��Ͻ�3000�������ת��һ�º�ʱ16����,�����������ĺû����Ͽ���ֻ��5����,���Բ�������ƿ��!!
		return map;
	}

	/**
	 * �����淽��һ��,ֻ��������ʱ��Ҫͨ��һ��ѭ��һ��������HashMap,����id<=>name,id<=>parentid,id<=>type��
	 * �������һ�δε�����ķ���,��ÿ��Ҫѭ��һ���,���ܲ���,������ó���һ��ѭ���и㶨!!
	 * 
	 * @param _hvs
	 * @param _keyValueFields,��N��2�еĲ���,��һ����key��,�ڶ�����value��,�б�ʾ�ж������!!
	 * @return
	 */
	public HashMap[] getHashMapsFromHashVOs(HashVO[] _hvs, String[][] _keyValueFields) {
		// long ll_1 = System.currentTimeMillis(); //
		HashMap[] maps = new HashMap[_keyValueFields.length]; //
		for (int j = 0; j < maps.length; j++) { // //
			maps[j] = new HashMap(); //
		}
		for (int i = 0; i < _hvs.length; i++) { // ������������
			for (int j = 0; j < maps.length; j++) { // ��������Hash��
				if (_keyValueFields[j][1].equalsIgnoreCase("$rownum")) { // ������������,��ʾ�к�
					maps[j].put(_hvs[i].getStringValue(_keyValueFields[j][0]), new Integer(i)); //
				} else {
					maps[j].put(_hvs[i].getStringValue(_keyValueFields[j][0]), _hvs[i].getStringValue(_keyValueFields[j][1])); //
				}
			}
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("��[" + _hvs.length + "]������ת����HashMap�����ʱ[" + (ll_2
		// - ll_1) + "]");
		return maps; //
	}

	/**
	 * ȡ��һ���ַ�����unicode�ĳ���!!
	 * 
	 * @param s
	 * @return
	 */
	public int getStrUnicodeLength(String s) { // ���������ָ����һ���ַ���,������ֽڳ���,����ַ������и������ַ�,��ô���ĳ��Ⱦ���2
		int j = 0;
		if (s == null || s.length() == 0) {
			return 0;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 0x100) { // ���������
				j = j + 2;
			} else {
				j = j + 1;
			}
		}
		return j;
	}

	/**
	 * ��һ��doubleת����string,�������ֿ�ѧ���㷨!! ��������Ƚ�ɵ,�Ժ����Ż�..
	 * 
	 * @param _ld
	 * @return
	 */
	public String getDoubleToString(double _ld) {
		boolean isscale = false; // �Ƿ�ȷ��С��λ..

		BigDecimal bigd = new BigDecimal(_ld); //
		bigd.setScale(2, BigDecimal.ROUND_HALF_UP);
		String str_tostr = bigd.toString(); //
		int li_pos = str_tostr.indexOf("."); //

		if (isscale) { // �����ȷ��С��λ
			if (li_pos >= 0) {
				String str_prefix = str_tostr.substring(0, li_pos); //
				String str_subfix = str_tostr.substring(li_pos + 1, str_tostr.length()); //
				if (str_subfix.length() > 2) {
					return str_prefix + "." + str_subfix.substring(0, 2); // 
				} else {
					if (str_subfix.length() == 0) {
						return str_prefix + "." + str_subfix + "00"; //
					} else if (str_subfix.length() == 1) {
						return str_prefix + "." + str_subfix + "0"; //
					} else {
						return str_tostr;
					}
				}
			} else {
				return str_tostr + ".00"; //
			}
		} else {
			if (li_pos >= 0) {
				String str_prefix = str_tostr.substring(0, li_pos); //
				String str_subfix = str_tostr.substring(li_pos + 1, str_tostr.length()); //
				if (str_subfix.length() > 2) {
					return str_prefix + "." + str_subfix.substring(0, 2); // 
				} else {
					return str_tostr;
				}
			} else {
				return str_tostr;
			}
		}
	}

	/**
	 * ��ȿ�¡ĳһ������!!
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object deepClone(Object obj) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(buf);
		out.writeObject(obj);
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
		return in.readObject();
	}

	/**
	 * ���л�һ������..
	 * 
	 * @param _obj
	 * @return
	 */
	public byte[] serialize(Object _obj) {
		ByteArrayOutputStream buf = null; //
		ObjectOutputStream out = null; //
		try {
			buf = new ByteArrayOutputStream();
			out = new ObjectOutputStream(buf);
			out.writeObject(_obj);
			byte[] bytes = buf.toByteArray();
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		} finally {
			try {
				out.close(); //
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �������л���һ������
	 * 
	 * @return
	 */
	public Object deserialize(byte[] _bytes) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(_bytes));
			Object obj = in.readObject();
			in.close();
			return obj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				in.close(); //
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * ѹ��һ����������
	 * 
	 * @param _initbytes
	 * @return
	 */
	public byte[] compressBytes(byte[] _initbytes) {
		try {
			java.util.zip.Deflater compressor = new java.util.zip.Deflater();
			compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //
			compressor.setInput(_initbytes); //
			compressor.finish(); // ��������ѹ��...
			ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); // //
			byte[] buf = new byte[2048]; //
			while (!compressor.finished()) {
				int count = compressor.deflate(buf); // ������ѹ����buf��,һ����˵,�������һ���ⶼ�᷵��1024,��������!!!!!
				bos.write(buf, 0, count);
			}
			bos.close();
			byte[] compressedData = bos.toByteArray(); // ѹ����Ĵ�С
			return compressedData; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ��ѹһ������������!!!
	 * 
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		try {
			Inflater decompressor = new Inflater();
			decompressor.setInput(_initbyte);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbyte.length);
			byte[] buf = new byte[2048];
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			}
			bos.close();
			byte[] decompressedData = bos.toByteArray();
			return decompressedData; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ȡ��
	 * 
	 * @return
	 */
	public int getJVSite() {
		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) { // �ͻ���,����������ȡ������,��Ϊ�������˿�����ΪȨ�����ⲻ������ϵͳ����,���ͻ����ǿ��Ե�
			return JVMSITE_CLIENT;
		} else {
			return JVMSITE_SERVER; //
		}
	}

	public String[][] getAllOptions() {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				if (ClientEnvironment.getClientSysOptions() == null) { // ����ͻ��˻���Ϊ��,���һ�γ�ʼ��һ��
					ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions());
				}
				return ClientEnvironment.getClientSysOptions();
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getAllOptions(); //
		}
		return null;
	}

	public String getSysOptionStringValue(String _key, String _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						return str_alloptions[i][1];
					}
				}
				return _nvl;
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getStringValue(_key, _nvl); //
		}
	}

	public int getSysOptionIntegerValue(String _key, int _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0]) && str_alloptions[i][1] != null) {
						return Integer.parseInt(str_alloptions[i][1]);
					}
				}
				return _nvl;
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getIntegerValue(_key, _nvl); //
		}
	}

	public boolean getSysOptionBooleanValue(String _key, boolean _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						if (str_alloptions[i][1] != null && (str_alloptions[i][1].equalsIgnoreCase("Y") || str_alloptions[i][1].equalsIgnoreCase("true") || str_alloptions[i][1].equalsIgnoreCase("��"))) { //
							return true;
						} else {
							return false;
						}
					}
				}
				return _nvl;
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue(_key, _nvl); //
		}
	}

	public String getSysOptionHashItemStringValue(String _key, String _itemKey, String _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // ȡ���ַ����еĶ�Ӧ��ֵ
						if (str_itemValue == null) {
							return _nvl;
						} else {
							return str_itemValue;
						}
					}
				}
				return _nvl; // ��str_alloptions��ȡ
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemStringValue(_key, _itemKey, _nvl); //
		}
	}

	public int getSysOptionHashItemIntegerValue(String _key, String _itemKey, int _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // ȡ���ַ����еĶ�Ӧ��ֵ
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
				}
				return _nvl; // ��str_alloptions��ȡ
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemIntegerValue(_key, _itemKey, _nvl); //
		}
	}

	public boolean getSysOptionHashItemBooleanValue(String _key, String _itemKey, boolean _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // ȡ���ַ����еĶ�Ӧ��ֵ
						if (str_itemValue == null) {
							return _nvl;
						}
						if (str_itemValue.equalsIgnoreCase("Y") || str_itemValue.equalsIgnoreCase("true") || str_itemValue.equalsIgnoreCase("��")) { //
							return true;
						} else {
							return false;
						}
					}
				}
				return _nvl; // ��str_alloptions��ȡ
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // ����ڷ�������,�����ʹ�÷������,��������ط������˵Ĵ������ͻ���!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemBooleanValue(_key, _itemKey, _nvl); //
		}
	}

	private String getItemValueByItemKey(String _value, String _itemKey) {
		String[] str_items = split(_value, ";"); // �Էֺŷָ�
		for (int i = 0; i < str_items.length; i++) {
			int li_pos = str_items[i].indexOf("="); //
			if (li_pos > 0) { // ����е��ں�
				String str_item_key = str_items[i].substring(0, li_pos); // key��
				if (str_item_key.equals(_itemKey)) {
					return str_items[i].substring(li_pos + 1, str_items[i].length()); // valueֵ
				}
			}
		}
		return null;
	}

	// �������ĳ�����ĳ����̬����
	public Object refectCallClassStaticMethod(String _className, String _methodName, Object[] _parObjs) {
		try {
			Class cls = Class.forName(_className); //
			Class[] parCls = null; //
			if (_parObjs != null) {
				parCls = new Class[_parObjs.length]; //
				for (int i = 0; i < _parObjs.length; i++) {
					if (_parObjs[i] == null) {
						parCls[i] = java.lang.String.class;
					} else {
						parCls[i] = _parObjs[i].getClass(); // �øö������
					}
				}
			}
			Method method = cls.getMethod(_methodName, parCls); //
			Object obj = method.invoke(null, _parObjs); //
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ȡ�õ�ǰSession
	 * 
	 * @return
	 */
	public CurrSessionVO getCurrSession() {
		if (getJVSite() == JVMSITE_SERVER) { // �����server��
			try {
				return new cn.com.infostrategy.bs.common.WLTInitContext().getCurrSession(); //
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���
			return ClientEnvironment.getInstance().getCurrSessionVO(); //
		} else {
			return null;
		}
	}

	public String getDefaultDataSourceName() {
		if (getJVSite() == JVMSITE_SERVER) { // �����server��
			return cn.com.infostrategy.bs.common.ServerEnvironment.getDefaultDataSourceName();
		} else if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); //
		} else {
			return null;
		}
	}

	public String getDefaultDataSourceType() {
		if (getJVSite() == JVMSITE_SERVER) { // �����server��
			return cn.com.infostrategy.bs.common.ServerEnvironment.getDefaultDataSourceType();
		} else if (getJVSite() == JVMSITE_CLIENT) { // ����ǿͻ���
			return ClientEnvironment.getInstance().getDefaultDataSourceType(); //
		} else {
			return null;
		}
	}

	/**
	 * �ҳ�ĳһ���������ֶε�SQL
	 * 
	 * @param _tableName
	 * @return
	 */
	public String getSQL_findAllCols(String _tableName) {
		if (getDefaultDataSourceType().equals(WLTConstants.ORACLE)) {
			if (_tableName == null) {
				return "select cname as ����,coltype ������,cname����ʾ���� from col ";
			} else {
				return "select cname as ����,coltype ������,cname����ʾ���� from col where tname='" + _tableName + "'";
			}
		} else if (getDefaultDataSourceType().equals(WLTConstants.SQLSERVER)) {
			if (_tableName == null) {
				return "select t1.name ����,t2.name + '(' + CAST(t1.length AS varchar) + ')' ������,t1.name ��ʾ���� from syscolumns t1,systypes t2 where t1.xtype=t2.xtype";
			} else {
				return "select t1.name ����,t2.name + '(' + CAST(t1.length AS varchar) + ')' ������,t1.name ��ʾ���� from syscolumns t1,systypes t2 where t1.xtype=t2.xtype and t1.id=(select id from sysobjects where name='" + _tableName + "')";
			}
		} else {
			return null;
		}
	}

	/**
	 * �ҵ����еı�!!
	 * 
	 * @return
	 */
	public String getSQL_findAllTables() {
		if (getDefaultDataSourceType().equals(WLTConstants.ORACLE)) {
			return "Select t1.tname tname,nvl(t2.comments, t1.tname) tdesc, t1.tabtype ����  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE') and 1 = 1 order by tname";
		} else {
			return "select name tname,name tdesc,crdate createdate from sysobjects where xtype='U' and name<>'dtproperties' order by name"; //
		}
	}

	private void getBracketID(String _sql, Vector _vec) throws Exception {
		String temp_sql = _sql;
		if (temp_sql.indexOf("{") < 0) {
			if (temp_sql.indexOf("}") >= 0) {
				throw new Exception("���[" + _sql + "]�еĴ�����\"{}\"û�гɶ�Ʒ��!!");
			}
			return;
		}
		String mid_sql = temp_sql.substring(temp_sql.indexOf("{"), temp_sql.length());

		int end_index = -1;
		if (mid_sql.indexOf("}") > 0) {
			end_index = mid_sql.indexOf("}");
		}
		if (end_index == -1) {
			throw new Exception("���[" + _sql + " ]�еĴ�����\"{}\"û�гɶ�Ʒ��!!");
		}
		String bracket_id = mid_sql.substring(1, end_index);
		_vec.add(bracket_id);

		getBracketID(mid_sql.substring(end_index + 1, mid_sql.length()), _vec);
	}

	public String getStrBracketID(String _sql) throws Exception {
		Vector vec = new Vector();
		try {
			getBracketID(_sql, vec);
		} catch (Exception e) {
			throw e;
		}
		if (vec.size() == 0)
			return null;
		else
			return (String) vec.get(0);
	}

	public Vector getVecBracketID(String _sql) throws Exception {
		Vector vec = new Vector();
		try {
			getBracketID(_sql, vec);
		} catch (Exception e) {
			throw e;
		}
		return vec;
	}

	public String getBaseSql(String _sql) {
		String temp_sql = _sql;
		int sql_index = temp_sql.toUpperCase().indexOf("WHERE");
		String result_str = temp_sql.substring(0, sql_index - 1);
		return result_str;
	}

	/**
	 * 
	 * @param _inittext
	 *            ����: getColValue("n1_menu","{menutype}","{menuid}");
	 * @return [menutype][menuid];
	 * @throws Exception
	 */
	public Vector findItemKey(String _inittext) throws Exception {
		Vector vec = new Vector();
		getBracketID(_inittext, vec);
		return vec;
	}

	private String convertStr(String _str, int _begin, int _end, Vector _v, int _index) throws Exception {
		if (_str == null) {
			return null;
		}
		System.out.println("The get str is:" + _str);
		String str_begin = _str.substring(0, _begin);
		String str_mid = _str.substring(_begin + 1, _str.length());

		int end_index = str_mid.indexOf("}");
		if (end_index <= 0) {
			throw new Exception("SQL������{}��ƥ�䣡");
		}

		int begin_index = str_mid.indexOf("{");

		if (begin_index >= 0 && begin_index < end_index) {
			convertStr(str_mid, begin_index, end_index, _v, _index);
		}

		String _bracket_parameter = (String) _v.get(_index);

		if (_bracket_parameter == null) {
			_bracket_parameter = "";
		}

		String str_end = str_mid.substring(end_index + 1, str_mid.length());

		return str_begin + _bracket_parameter + str_end;
	}

	public String replace_ItemKey(String _inittext, Vector _v, int _index) throws Exception {
		String temp_sql = _inittext;

		int _begin = temp_sql.indexOf("{");
		if (_begin < 0) {
			if (temp_sql.indexOf("}") >= 0) {
				throw new Exception("SQL������{}��ƥ�䣡");
			}
			return temp_sql;
		}
		if (_index == _v.size()) {
			return temp_sql;
		}

		String begin_sql = temp_sql.substring(0, _begin);
		String mid_sql = temp_sql.substring(_begin + 1, temp_sql.length());

		int end_index = mid_sql.indexOf("}");
		if (end_index >= 0) {
			end_index = mid_sql.indexOf("}");
			int begin_index = mid_sql.indexOf("{");
			if (begin_index >= 0 && begin_index < end_index) {
				mid_sql = convertStr(mid_sql, begin_index, end_index, _v, _index);
				System.out.println("\nThe get mid_str is:" + mid_sql);
			}
		} else {
			throw new Exception("SQL������{}��ƥ�䣡");
		}
		end_index = mid_sql.indexOf("}");

		String _bracket_parameter = (String) _v.get(_index);

		if (_bracket_parameter == null) {
			_bracket_parameter = "";
		}
		String end_sql = "";

		if (end_index + 1 < mid_sql.length()) {
			end_sql = replace_ItemKey(mid_sql.substring(end_index + 1, mid_sql.length()), _v, _index + 1);
		} else {
			end_sql = "";
		}
		return begin_sql + _bracket_parameter + end_sql;
	}

	/**
	 * 
	 * @param _inittext
	 * @param _newtext
	 * @return
	 * @throws Exception
	 */
	public String replace_ItemKey(String _inittext, Vector _v) throws Exception {
		if (_v.size() == 0) {
			return _inittext;
		}
		String str = "";
		try {
			str = replace_ItemKey(_inittext, _v, 0);
		} catch (Exception e) {
			throw e;
		}
		return str;
	}

	/**
	 * �ӿͻ��˻�����ҳ�����ҳ�ĳ��key��ֵ
	 * 
	 * @param _key
	 * @param _otherkey
	 * @param _env
	 * @param _map
	 * @return
	 */
	private String getParameter(String _key, String _otherkey, CurrSessionVO _currSessionVO, HashMap _map) {
		String _bracket_parameter = null;
		Object obj = _currSessionVO.getCustMap().get(_key); // �ӿͻ��˻�������ȡ
		if (obj == null) {
			Object _obj = _map.get(_key); // �ٴ�ҳ������ȡ
			if (_obj == null) {
				_bracket_parameter = "";
			} else {
				if (_obj instanceof String) {
					_bracket_parameter = _obj.toString();
				}
				if (_obj instanceof ComBoxItemVO) {
					if (_otherkey == null) {
						_bracket_parameter = ((ComBoxItemVO) _obj).getId(); //
					} else {
						_bracket_parameter = ((ComBoxItemVO) _obj).getItemValue(_otherkey); // ȡ������VO�������ֶ�!!!
					}
				} else if (_obj instanceof RefItemVO) {
					if (_otherkey == null) {
						_bracket_parameter = ((RefItemVO) _obj).getId(); //
					} else {
						_bracket_parameter = ((RefItemVO) _obj).getItemValue(_otherkey); // ȡ����VO�������ֶ�!!!
					}

				} else {
					_bracket_parameter = _obj.toString();
				}
			}
		} else {
			_bracket_parameter = obj.toString(); //
		}
		return _bracket_parameter;
	}

	/**
	 * ������Ƕ��{}
	 * 
	 * @param _str
	 * @param _begin
	 * @param _end
	 * @param _env
	 * @param _map
	 * @return
	 * @throws Exception
	 */
	private String convert(String _str, int _begin, int _end, CurrSessionVO _currSessionVO, HashMap _map) throws Exception {
		if (_str == null) {
			return null;
		}
		String str_begin = _str.substring(0, _begin);
		String str_mid = _str.substring(_begin + 1, _str.length());

		int end_index = str_mid.indexOf("}");
		if (end_index <= 0) {
			throw new Exception("SQL������{}��ƥ�䣡");
		}

		int begin_index = str_mid.indexOf("{");

		if (begin_index >= 0 && begin_index < end_index) {
			convert(str_mid, begin_index, end_index, _currSessionVO, _map);
		}

		String bracket_id = str_mid.substring(0, end_index); // ����{}�м������
		int li_pos = bracket_id.indexOf("."); //
		String str_itemkey = null;
		String str_itemkey_otherfield = null;
		if (li_pos > 0) { // ��������и�"."
			str_itemkey = bracket_id.substring(0, li_pos);
			str_itemkey_otherfield = bracket_id.substring(li_pos + 1, bracket_id.length()); // ������Ƕ"{}"
		} else {
			str_itemkey = bracket_id;
		}

		String _bracket_parameter = getParameter(str_itemkey, str_itemkey_otherfield, _currSessionVO, _map);
		if (_bracket_parameter == null) {
			_bracket_parameter = "";
		}

		String str_end = str_mid.substring(end_index + 1, str_mid.length());

		return str_begin + _bracket_parameter + str_end;
	}

	/**
	 * ת��ĳ������SQL��{}Ϊ�ͻ��˻����ҳ���ϵ�ֵ!!!
	 * 
	 * @param _inittext
	 * @param _env
	 * @param _map
	 * @return
	 * @throws Exception
	 */
	public String convertComboBoxDescSQL(String _oldsql, CurrSessionVO _currSessionVO) throws Exception {
		String str_newsql = _oldsql;
		Vector v_allkeys = findItemKey(str_newsql); //

		for (int i = 0; i < v_allkeys.size(); i++) {
			String str_key = (String) v_allkeys.get(i); // key��
			String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // �ӿͻ��˻���ȡ��!!
			if (str_clientenvvalue != null) { // ����ӿͻ��˻���ȡ������!!
				str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_clientenvvalue); // �滻
			} else // ����ӿͻ��˻�������ûȡ��!!
			{
				str_newsql = replaceAll(str_newsql, "{" + str_key + "}", ""); // �滻
			}
		} // end for ѭ��
		return str_newsql; //
	}

	/**
	 * ת��ĳ������SQL��{}Ϊ�ͻ��˻����ҳ���ϵ�ֵ!!!
	 * 
	 * @param _inittext
	 * @param _env
	 * @param _map
	 * @return
	 * @throws Exception
	 */
	public String replaceStrWithSessionOrHashData(String _oldsql, CurrSessionVO _currSessionVO, HashMap _map) {
		try {
			String str_newsql = _oldsql;
			Vector v_allkeys = findItemKey(str_newsql); // �ҳ�һ���ַ���������{}������!!
			for (int i = 0; i < v_allkeys.size(); i++) {
				String str_key = (String) v_allkeys.get(i); // key��
				String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // �ӿͻ��˻���ȡ��!!
				if (str_clientenvvalue != null) { // ����ӿͻ��˻���ȡ������!!
					str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_clientenvvalue); // ���滻һ��
				} else // ��ҳ��ؼ�ȡ
				{
					if (_map != null && _map.size() > 1) { // �����ֵ
						int li_pos = str_key.indexOf("."); // ����û��"�����"??
						String str_itemvalue = "";
						if (li_pos <= 0) { // ���û�е����!!��ֱ��ȡ
							Object obj = _map.get(str_key);
							if (obj != null) {
								if (obj instanceof String) {
									str_itemvalue = "" + obj; //
								} else if (obj instanceof ComBoxItemVO) { // �����������
									str_itemvalue = ((ComBoxItemVO) obj).getId(); //
								} else if (obj instanceof RefItemVO) { // ����ǲ���!!
									str_itemvalue = ((RefItemVO) obj).getId(); //
								} else {
									str_itemvalue = "" + obj;
								}
							} else {

							}
						} else // ����е����!!
						{
							String str_key_prefix = str_key.substring(0, li_pos); // ǰ�
							String str_key_subfix = str_key.substring(li_pos + 1, str_key.length()); // ���

							Object obj = _map.get(str_key_prefix); //
							if (obj != null) {
								if (obj instanceof String) {
									str_itemvalue = "" + obj; //
								} else if (obj instanceof ComBoxItemVO) { // �����������
									str_itemvalue = ((ComBoxItemVO) obj).getItemValue(str_key_subfix); //
								} else if (obj instanceof RefItemVO) { // ����ǲ���!!
									str_itemvalue = ((RefItemVO) obj).getItemValue(str_key_subfix); //
								} else {
									str_itemvalue = "" + obj;
								}
							} else {
							}
						}

						str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_itemvalue); // ��ҳ��ؼ���ֵ�滻!!!
					}
				}
			} // end for ѭ��

			return str_newsql; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return _oldsql; //
		}
	}

	public String[] getTableRefDefine(String _refdesc, CurrSessionVO _currSessionVO, HashMap _map) {
		String[] str_split = split(_refdesc, ";"); //
		String str_sql = null;
		String str_dsname = null;

		str_sql = replaceStrWithSessionOrHashData(str_split[0], _currSessionVO, _map); // ��һ���϶�Ҫ��!!
		if (str_split.length == 2) { // ���û�ж�������Դ
			str_dsname = replaceStrWithSessionOrHashData(str_split[1], _currSessionVO, _map); // ת������Դ!!
		} else { // �����������Դ
			str_dsname = getDefaultDataSourceName(); //
		}

		return new String[] { str_sql, str_dsname }; //
	}

	public String[] getTreeRefDefine(String _refdesc, CurrSessionVO _currSessionVO, HashMap _map) {
		String[] str_split = split(_refdesc, ";"); //
		String str_sql = null;
		String str_pkfieldname = null; //
		String str_parentPkFieldName = null; //
		String str_dsname = null;

		str_sql = replaceStrWithSessionOrHashData(str_split[0], _currSessionVO, _map); // ��һ���϶�Ҫ��!!
		str_pkfieldname = split(str_split[1], "=")[1].trim();
		str_parentPkFieldName = split(str_split[1], "=")[0].trim();
		if (str_split.length == 3) { // ���û�ж�������Դ
			str_dsname = replaceStrWithSessionOrHashData(str_split[2], _currSessionVO, _map); // ת������Դ!!
		} else { // �����������Դ
			str_dsname = getDefaultDataSourceName();
		}
		return new String[] { str_sql, str_pkfieldname, str_parentPkFieldName, str_dsname }; //
	}

	/**
	 * ���ݲ���ԭʼ����ȡ�����յ�������SQL
	 * 
	 * @param _refdesc
	 * @return
	 */
	public String[] getRefDescTypeAndSQL(String _refdesc) {
		if (_refdesc == null) {
			return null;
		}

		String str_type = null; // ����
		String str_realsql = null; // ������SQL
		String str_parentfieldname = null; // ���Ͳ��յĸ����ֶ�
		String str_pkfieldname = null; // ���Ͳ��յ�����
		String str_datasourcename = null; // ����Դ����

		int li_pos = _refdesc.indexOf(":"); //
		if (li_pos < 0) {
			str_type = "TABLE";
		} else {
			str_type = _refdesc.substring(0, li_pos).toUpperCase(); //
		}

		if (str_type.equalsIgnoreCase("TABLE") || str_type.equalsIgnoreCase("TABLE2") || str_type.equalsIgnoreCase("TABLE3")) {
			if (li_pos < 0) {
				String str_remain = _refdesc.trim(); // ʣ�µ�
				String[] str_arrays = split(str_remain, ";"); //
				str_realsql = str_arrays[0];
				if (str_arrays.length == 1) {
					str_datasourcename = getDefaultDataSourceName(); // Ĭ������Դ!!!
				} else if (str_arrays.length == 2) {
					str_datasourcename = getDataSourceName(str_arrays[1]); //
				}

			} else {
				String str_remains = _refdesc.substring(li_pos + 1, _refdesc.length()).trim();
				str_realsql = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); // ����
			}
		} else if (str_type.equalsIgnoreCase("TREE") || str_type.equalsIgnoreCase("TREE2") || str_type.equalsIgnoreCase("TREE3")) { // ����..
			try {
				String str_remain = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); //
				String[] str_arrays = split(str_remain, ";"); //
				str_realsql = str_arrays[0].trim();
				String[] str_treeFieldNames = getTreeFielNames(str_arrays[1].trim()); //
				str_parentfieldname = str_treeFieldNames[0];
				str_pkfieldname = str_treeFieldNames[1];
				if (str_arrays.length == 2) {
					str_datasourcename = getDefaultDataSourceName(); // Ĭ������Դ!!!
				} else if (str_arrays.length == 3) {
					str_datasourcename = getDataSourceName(str_arrays[2]); //
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (str_type.equalsIgnoreCase("CUST")) {
			str_realsql = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); // ����
		} else {
		}

		return new String[] { str_type, str_realsql, str_parentfieldname, str_pkfieldname, str_datasourcename }; //
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

	private String[] getTreeFielNames(String _des) {
		int li_pos = _des.indexOf("="); //
		String str_parentfieldname = _des.substring(0, li_pos); // ParentFieldID
		String str_pkfieldname = _des.substring(li_pos + 1, _des.length()); // PKFieldID

		str_parentfieldname = str_parentfieldname.trim();// ��ȥ�ո�
		str_pkfieldname = str_pkfieldname.trim();// ��ȥ�ո�
		return new String[] { str_parentfieldname, str_pkfieldname };
	}

	/**
	 * ת��һ��SQL,�滻���е�{}Ϊ'',Ȼ��ǿ�м���1=2,��ʹ����ת�����еĽṹ!!
	 * 
	 * @param _sql
	 * @return
	 */
	public String convertSQLValue(String _value) {
		if (_value == null) {
			return "null";
		} else {
			return "'" + replaceAll(_value, "'", "''") + "'"; //
		}
	}

	public String complieWhereConditionOnSQL(String _sql, String _pkname, String _pkvalue) {
		String str_sql_1 = _sql;
		String str_sql_2 = _sql.toUpperCase(); //

		int li_pos_where = str_sql_2.lastIndexOf("WHERE"); //
		int li_pos_order = str_sql_2.lastIndexOf("ORDER"); //

		if (li_pos_where > 0) // �����Where����
		{
			if (li_pos_order > 0) { // �����order
				String str_prefix = str_sql_1.substring(0, li_pos_order); // ǰ�
				String str_subfix = str_sql_1.substring(li_pos_order, str_sql_1.length()); // ���
				return str_prefix + " AND " + _pkname + "='" + _pkvalue + "' " + str_subfix; //
			} else {
				return str_sql_1 + " AND " + _pkname + "='" + _pkvalue + "'";
			}
		} else { // ���û��where����
			if (li_pos_order > 0) { // �����order
				String str_prefix = str_sql_1.substring(0, li_pos_order); // ǰ�
				String str_subfix = str_sql_1.substring(li_pos_order, str_sql_1.length()); // ���
				return str_prefix + " WHERE " + _pkname + "='" + _pkvalue + "' " + str_subfix; //
			} else {
				return str_sql_1 + " WHERE " + _pkname + "='" + _pkvalue + "'";
			}
		}
	}

	/**
	 * ����һ��SQL,�ҳ����е�һ�е�����!
	 * 
	 * @param _sql
	 * @return
	 */
	public String findSQLFirstName(String _sql) {
		if (_sql == null) {
			return null;
		}

		try {
			String str_sql = _sql.trim().toUpperCase(); //
			int li_pos = str_sql.indexOf(","); // �ҳ���һ��
			str_sql = str_sql.substring(6, li_pos).trim();
			int li_pos_2 = str_sql.indexOf(" ");
			if (li_pos_2 > 0) {
				str_sql = str_sql.substring(0, li_pos_2).trim();
			} else { //
			}
			return str_sql;
		} catch (Exception ex) {
			System.out.println("�ҳ�����SQL[" + _sql + "]�ĵ�һ����ʧ��!!");
			ex.printStackTrace();
			return null;
		}
	}

	public HashVO findHashVOFromHVS(HashVO[] _hvs, String _itemkey, String _itemValue) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue(_itemkey, "").equals(_itemValue == null ? "" : _itemValue)) {
				return _hvs[i]; //
			}
		}
		return null;
	}

	/**
	 * ��һ��SQL����{}�ĵط��滻��_env�е�ֵ��_,map�е�ֵ!!!
	 * 
	 * @param _inittext
	 * @param _env
	 * @param _map
	 * @return
	 * @throws Exception
	 */
	public String convertMacroStr(String _inittext, CurrSessionVO _currSessionVO, HashMap _map) throws Exception {
		String temp_sql = _inittext;

		int _begin = temp_sql.indexOf("{");
		if (_begin < 0) {
			if (temp_sql.indexOf("}") >= 0) {
				throw new Exception("SQL������{}��ƥ�䣡");
			}
			return temp_sql;
		}

		String begin_sql = temp_sql.substring(0, _begin);
		String mid_sql = temp_sql.substring(_begin + 1, temp_sql.length());

		int end_index = mid_sql.indexOf("}");
		if (end_index >= 0) {
			end_index = mid_sql.indexOf("}");
			int begin_index = mid_sql.indexOf("{");
			if (begin_index >= 0 && begin_index < end_index) {
				mid_sql = convert(mid_sql, begin_index, end_index, _currSessionVO, _map);// ������Ƕ"{}"
			}
		} else {
			throw new Exception("SQL������{}��ƥ�䣡");
		}

		end_index = mid_sql.indexOf("}");

		String bracket_id = mid_sql.substring(0, end_index); // ����{}�м������
		int li_pos = bracket_id.indexOf("."); //
		String str_itemkey = null;
		String str_itemkey_otherfield = null;
		if (li_pos > 0) { // ��������и�"."
			str_itemkey = bracket_id.substring(0, li_pos);
			str_itemkey_otherfield = bracket_id.substring(li_pos + 1, bracket_id.length()); //
		} else {
			str_itemkey = bracket_id;
		}

		String _bracket_parameter = getParameter(str_itemkey, str_itemkey_otherfield, _currSessionVO, _map);
		String end_sql = "";
		if (end_index + 1 < mid_sql.length()) {
			end_sql = convertMacroStr(mid_sql.substring(end_index + 1, mid_sql.length()), _currSessionVO, _map);
		} else {
			end_sql = "";
		}

		return begin_sql + _bracket_parameter + end_sql;
	}

	/**
	 * ȡ��Excel�İ�װ��ַ
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getExcelExePath() throws Exception {
		String str_1 = null;
		try {
			RegistryKey note_1 = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE").openSubKey("Classes").openSubKey("Excel.Application").openSubKey("CLSID");
			str_1 = note_1.getDefaultValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("����ûװExcel");
		}

		try {
			RegistryKey node_2 = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE").openSubKey("Classes").openSubKey("CLSID").openSubKey(str_1).openSubKey("LocalServer32"); //
			String str_2 = node_2.getDefaultValue(); //
			return str_2;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("���ܸò���ϵͳ����Windows2000/XP/2003,��Ӧλ���Ҳ�������!");
		}
	}

	/**
	 * ����TMO
	 * 
	 * @param _allcolumns
	 * @return
	 */
	public AbstractTMO getTMO(String[] _allcolumns) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // ģ�����,��������޸�

		HashVO[] childVOs = new HashVO[_allcolumns.length];
		for (int i = 0; i < childVOs.length; i++) {
			childVOs[i] = new HashVO();
			childVOs[i].setAttributeValue("itemkey", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemname", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // �����ı���
			childVOs[i].setAttributeValue("listwidth", "125"); //

			if (_allcolumns[i].endsWith("#")) {
				childVOs[i].setAttributeValue("listisshowable", "N"); //
			} else {
				childVOs[i].setAttributeValue("listisshowable", "Y"); //
			}
		}

		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs); // ����Ԫԭģ������
		return tmo;
	}

	public Vector getUserFun() {
		// Object[] obj_temp =
		// ClientEnvironment.getInstance().getRowValue("USER_FUNCTION");
		// if (obj_temp != null && obj_temp[1].equals("JepFormulaParse")) {
		// vec_user_fun = (Vector) obj_temp[2];
		// return vec_user_fun;
		// }
		//
		// String[][] str_fun = null;
		// String str_sql = "Select * From pub_formulafunctions";
		// if (li_type == li_bs) {
		// try {
		// str_fun = new CommDMO().getStringArrayByDS(null, str_sql);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// } else {
		// try {
		// str_fun = UIUtil.getStringArrayByDS(null, str_sql);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// for (int i = 0; i < str_fun.length; i++) {
		// String fn_name = str_fun[i][1]; // Ӧ�����ݿ�ȡ!!
		// String str_className = str_fun[i][3]; // Ӧ�����ݿ�ȡ!!
		// try {
		// PostfixMathCommand formula = (PostfixMathCommand)
		// Class.forName(str_className).newInstance(); //
		// parser.addFunction(fn_name, formula);// �滻�ַ����е�����
		// vec_user_fun.add(new String[] { fn_name, str_fun[i][5], str_fun[i][6]
		// });
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// return vec_user_fun;
		return null;
	}

	/**
	 * ˭��Ĵ���,����ʲô��̬����,��ʵû��Ҫ,һ�����������ʵ�ܿ�,���Ǹ����о�̬����������������������,�ر��Ǳ����������ú�!
	 * ����,��ʹ����Ҳ��Ҫ��������,ͳһ��ClientEnv��,��ר�Ÿ�һ��ClientCache�������! ����Ÿ�HashMap
	 * @param _html
	 * @return
	 */
	public java.awt.Color getColor(String _html) {
		if (constMap.containsKey(_html)) {
			return (Color) constMap.get(_html); // 
		}
		String str_r = _html.substring(0, 2).toUpperCase();
		String str_g = _html.substring(2, 4).toUpperCase();
		String str_b = _html.substring(4, 6).toUpperCase();
		java.awt.Color color = new java.awt.Color(getHexIntValue(str_r), getHexIntValue(str_g), getHexIntValue(str_b));
		constMap.put(_html, color); //��Ϊ��ɫ���ּ�����Զ����仯,�Ҽ�����С,�����ʺ�������...
		return color; // 
	}

	/**
	 * ��255,0,0,������
	 * @param _rgb
	 * @param _nvl
	 * @return
	 */
	public java.awt.Color getColor(String _rgb, Color _nvl) {
		if (_rgb == null || _rgb.trim().equals("")) {
			return _nvl;
		}
		String[] item = split(_rgb, ","); //
		try {
			Color color = new Color(Integer.parseInt(item[0]), Integer.parseInt(item[1]), Integer.parseInt(item[2])); //
			return color; //
		} catch (Exception ex) {
			System.err.println("ת����ɫ[" + _rgb + "]����:" + ex.getClass() + ":" + ex.getMessage()); //
			return _nvl; //
		}
	}

	public String getHtmlColor(String _rgb) {
		if (_rgb == null || _rgb.trim().equals("")) {
			return "#FFFFFF";
		}
		String[] item = split(_rgb, ","); //
		return "#" + getHexString(Integer.parseInt(item[0].trim())) + getHexString(Integer.parseInt(item[1].trim())) + getHexString(Integer.parseInt(item[2].trim())); //
	}

	// ..
	public String convertColor(java.awt.Color _color) {
		int li_r = _color.getRed();
		int li_g = _color.getGreen();
		int li_b = _color.getBlue();

		return getHexString(li_r) + getHexString(li_g) + getHexString(li_b);
	}

	public String getHexString(int _int) {
		String str_value = Integer.toHexString(_int); //
		if (str_value.length() == 1) {
			str_value = "0" + str_value; //
		}
		return str_value.toUpperCase(); //
	}

	// ...
	public int getHexIntValue(String _par) {
		String str_1 = _par.substring(0, 1);
		String str_2 = _par.substring(1, 2);
		int li_1 = convertHex(str_1);
		int li_2 = convertHex(str_2);
		return li_1 * 16 + li_2;
	}

	public int convertHex(String _par) {
		int li_return = 0;
		if (_par.equals("0")) {
			li_return = 0;
		} else if (_par.equals("1")) {
			li_return = 1;
		} else if (_par.equals("2")) {
			li_return = 2;
		} else if (_par.equals("3")) {
			li_return = 3;
		} else if (_par.equals("4")) {
			li_return = 4;
		} else if (_par.equals("5")) {
			li_return = 5;
		} else if (_par.equals("6")) {
			li_return = 6;
		} else if (_par.equals("7")) {
			li_return = 7;
		} else if (_par.equals("8")) {
			li_return = 8;
		} else if (_par.equals("9")) {
			li_return = 9;
		} else if (_par.equalsIgnoreCase("A")) {
			li_return = 10;
		} else if (_par.equalsIgnoreCase("B")) {
			li_return = 11;
		} else if (_par.equalsIgnoreCase("C")) {
			li_return = 12;
		} else if (_par.equalsIgnoreCase("D")) {
			li_return = 13;
		} else if (_par.equalsIgnoreCase("E")) {
			li_return = 14;
		} else if (_par.equalsIgnoreCase("F")) {
			li_return = 15;
		}
		return li_return;
	}

	// �ж�һ���ַ����Ƿ���16����,����׼!!
	public boolean isHexStr(String _str) {
		if (_str == null || _str.equals("")) {
			return false;
		}
		for (int i = 0; i < _str.length(); i++) {
			if ("0123456789ABCDEF".indexOf(_str.charAt(i)) < 0) {
				return false;
			}
		}
		return true;
	}

	public String getCurrDate() {
		return getCurrDate(true, true); //
	}

	// ȡ�õ�ǰ����!�����bank���,���ʾ��-,�����������һ�������!
	public String getCurrDate(boolean _isHaveBank, boolean _isClientDiffServer) {
		long ll_diff = 0; //
		if (getJVSite() == JVMSITE_CLIENT && _isClientDiffServer) { // ����ǿͻ���,��Ҫ����һ�²���!!!!
			ll_diff = getDiffServerTime(); //
		}
		SimpleDateFormat sdf_curr = new SimpleDateFormat((_isHaveBank ? "yyyy-MM-dd" : "yyyyMMdd"), Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis() - ll_diff));
	}

	// ȡ�õ�ǰʱ��
	public String getCurrTime() {
		return getCurrTime(true, true);
	}

	// ȡ�õ�ǰʱ��,���Է�ӳ���Ƿ��Ǵ����ֵ�! ��ʱ�������ļ�ʱ��Ҫȥ���ո�ð�ŵȷ���!
	public String getCurrTime(boolean _isHaveBank) {
		return getCurrTime(_isHaveBank, true);
	}

	/**
	 * ȡ�õ�ǰʱ��!
	 * 
	 * @param _isTrimBank
	 *            �Ƿ�ص��м�����и��ӷ�,����ʱ����Ҫ-��:����,ֻ��һ��������ƴ������!
	 * @param _isClientDiffServer
	 *            ����ǿͻ��˵Ļ�,�Ƿ�����������бȽ���ͬ��,Ĭ�Ͽ϶�����Ҫ��!
	 * @return
	 */
	public String getCurrTime(boolean _isHaveBank, boolean _isClientDiffServer) {
		// ��Ϊ��������ͻ��˾�������ü�����,Ϊ�˸�׼,ͬʱ���ܱ�֤����,��һ����ȡ��,������߲���!
		long ll_diff = 0; //
		if (getJVSite() == JVMSITE_CLIENT && _isClientDiffServer) { // ����ǿͻ���,�ұ�����������˱Ƚ�!
			ll_diff = getDiffServerTime(); //
		}
		String str_format = _isHaveBank ? "yyyy-MM-dd HH:mm:ss" : "yyyyMMddHHmmss"; //
		long ll_value = System.currentTimeMillis() - ll_diff; //
		return getTimeByLongValue(ll_value, str_format); //
	}

	public String getCurrDateSeason() {
		return getCurrDateSeason(getCurrDate()); //
	}

	/**
	 * ���ص�ǰ����,��ʽ��[2012��1����]
	 * @param _date
	 * @return
	 */
	public String getCurrDateSeason(String _date) {
		String str_season = _date.substring(0, 4) + "��" + getSeason(_date.substring(5, 7)) + "����"; //
		return str_season; //
	}

	public String getCurrDateMonth() {
		return getCurrDateMonth(getCurrDate()); //
	}

	/**
	 * ���ص�ǰ�·�,��ʽΪ[2012��05��][2012��10��]
	 * @param _date
	 * @return
	 */
	public String getCurrDateMonth(String _date) {
		String str_month = _date.substring(0, 4) + "��" + _date.substring(5, 7) + "��"; //
		return str_month;
	}

	private String getSeason(String _month) {
		if (_month.equals("01") || _month.equals("02") || _month.equals("03")) {
			return "1";
		} else if (_month.equals("04") || _month.equals("05") || _month.equals("06")) {
			return "2";
		} else if (_month.equals("07") || _month.equals("08") || _month.equals("09")) {
			return "3";
		} else if (_month.equals("10") || _month.equals("11") || _month.equals("12")) {
			return "4";
		}
		return "";
	}

	// ����Longֵ,����ʱ��!!
	public String getTimeByLongValue(long _longValue) {
		return getTimeByLongValue(_longValue, "yyyy-MM-dd HH:mm:ss"); //
	}

	// ����Longֵ,����ʱ��!!
	public String getTimeByLongValue(long _longValue, String _format) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat(_format, Locale.SIMPLIFIED_CHINESE); //
		return sdf_curr.format(new Date(_longValue)); // ���ϲ���!!
	}

	// ȡ�ÿͻ������������ʱ��Ĳ���!
	public long getDiffServerTime() {
		if (ClientEnvironment.llDiffServerTime == null) { // ���û����
			try {
				long ll_begin = System.currentTimeMillis(); //
				long ll_serverTime = UIUtil.getServerCurrTimeLongValue(); //
				long ll_end = System.currentTimeMillis(); //
				long ll_clientTime = System.currentTimeMillis() - (ll_end - ll_begin); // (ll_end
				// -
				// ll_begin)�ͻ��˵ĵ�ǰʱ��,�����ȥִ������Զ�̵��õ�ʱ��(�����и�ǰ��,�����������ȡʱ��ʱ��0)
				ClientEnvironment.llDiffServerTime = new Long(ll_clientTime - ll_serverTime); // �������,�Ƿ������˱ȷ����������,�����ͻ������ڷ�������������,
			} catch (Exception ex) {
				ex.printStackTrace(); //
				ClientEnvironment.llDiffServerTime = new Long(0); //
			}
		}
		return ClientEnvironment.llDiffServerTime.longValue(); //
	}

	// ��longֵ��ʽ�����ַ����������ַ���
	public String formatDateToStr(long _long) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_long));
	}

	// ��longֵ��ʽ�����ַ�����ʱ���ַ���
	public String formatTimeToStr(long _long) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_long));
	}

	// ��һ���ַ��������ڷ�ת��Longֵ,��������ǰ�����֮��
	public long parseDateToLongValue(String _dateStr) {
		try {
			SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
			return sdf_curr.parse(_dateStr).getTime(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	// ��һ���ַ����ĵ����ʱ��ֵ��ת��LongValue
	public long parseTimeToLongValue(String _timeStr) {
		try {
			SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
			return sdf_curr.parse(_timeStr).getTime(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	public byte[] getCompentBytes(Component _component) throws Exception {
		Rectangle rect = _component.getBounds();
		return getCompentBytes(_component, rect.width, rect.height, false);
	}

	public byte[] getCompentBytes(Component _component, boolean _isBmp) throws Exception {
		Rectangle rect = _component.getBounds();
		return getCompentBytes(_component, rect.width, rect.height, _isBmp);
	}

	public byte[] getCompentBytes(Component _component, int _width, int _height) throws Exception {
		return getCompentBytes(_component, _width, _height, true); // Ĭ����bmp����jpg,��ҵ��bmp�ܸ���,������ƹ���Ŀ�о�Ȼ������bmp�ļ��������ڴ���������!!
		// ��Ҳ�п����ǿͻ��˿�����������ڴ�û���ԭ��!
	}

	public Image getCompentImage(Component _component) throws Exception {
		Rectangle rect = _component.getBounds();
		BufferedImage image = (BufferedImage) _component.createImage(rect.width, rect.height); //
		Graphics g = image.getGraphics();
		_component.paint(g); // ���ؼ���ͼ��д�뵽����µĻ�����
		g.dispose();
		return image;
	}

	// �õ�Swing������ֽ���
	public byte[] getCompentBytes(Component _component, int _width, int _height, boolean _isBmp) throws Exception {

		//��΢�����±������, ���˸��߿�ÿ���! Gwang 2014-04-21
		BufferedImage bi = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB); //����һ������ͼ�ζ���
		Graphics2D g2d = (Graphics2D) bi.createGraphics(); //��������
		_component.paint(g2d); //���ؼ���ͼ��д��bi		
		g2d.setColor(Color.BLACK); //���û�����ɫ
		g2d.drawRect(0, 0, _width - 2, _height - 2); //�����߿�
		g2d.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream(); // �����!!
		String str_pictype = (_isBmp ? "bmp" : "jpeg"); // bmp��jpg��������,bmp�������ļ���,jpg��֮!
		ImageIO.write(bi, str_pictype, out); //
		byte[] bytes = out.toByteArray();
		out.close(); //
		return bytes; //
	}

	public String getCompent64Code(Component _component) throws Exception {
		Rectangle rect = _component.getBounds();
		return getCompent64Code(_component, rect.width, rect.height, true);
	}

	public String getCompent64Code(Component _component, int _width, int _height) throws Exception {
		return getCompent64Code(_component, _width, _height, true); // Ĭ����bmp,��Ϊ��ҵ��Ŀ������ͼƬ������Ҫ�����!
	}

	// �õ�Swing�����64λ��,��������mht,word��xml��ʽ��!
	public String getCompent64Code(Component _component, int _width, int _height, boolean _isBmp) throws Exception {
		byte[] bytes = getCompentBytes(_component, _width, _height, _isBmp); // �ȵõ��ֽ���
		String str_64code = convertBytesTo64Code(bytes); // ת��64λ��
		return str_64code; //
	}

	public void saveCompentAsJPGFile(Component _component, String _fileName) throws Exception {
		saveCompentAsPicture(_component, _fileName, false); //
	}

	// ��Swing�������һ��jpg�ļ�!!�������������word�ļ��ķ�����һ��!
	public void saveCompentAsPicture(Component _component, String _fileName, boolean _isBmp) throws Exception {
		Rectangle rect = _component.getBounds();
		// BufferedImage image = (BufferedImage)
		// _component.createImage(rect.width, rect.height); //
		// Graphics g = image.getGraphics();
		// _component.paint(g);
		// g.dispose();
		// ImageTypeSpecifier type =
		// ImageTypeSpecifier.createFromRenderedImage(image); //
		// String str_picturetype = (_isBmp ? "bmp" : "jpeg");
		// //��bmp��jpeg����,bmp���ȸ�,JPG�ļ�С,�������ó���!!!
		// Iterator iter = ImageIO.getImageWriters(type, str_picturetype); //
		// ImageWriter writer = (ImageWriter) iter.next(); //
		// IIOImage iioImage = new IIOImage(image, null, null);
		// ImageWriteParam param = writer.getDefaultWriteParam();
		// param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		// if (!_isBmp) { //�����jpg��ʽ
		// param.setCompressionQuality(1.0f); //����ѹ������
		// //param.setTilingMode(ImageWriteParam.MODE_EXPLICIT); //
		// }
		// ImageOutputStream outputStream = ImageIO.createImageOutputStream(new
		// File(_fileName));
		// writer.setOutput(outputStream);
		// writer.write(null, iioImage, param);
		// outputStream.close(); //
		byte[] bytes = getCompentBytes(_component, rect.width, rect.height, _isBmp); // ��ǰ��������ע�͵��Ƕη���!���о����鷳!����ʹ�ü򵥵İ취!
		// ��������ʱ�᲻��ͼƬ���������!!��BS������ʱ���������������!һʱ����֪���Ǻ�ԭ��!
		writeBytesToOutputStream(new FileOutputStream(new File(_fileName), false), bytes); // д�ļ�!!
	}

	// ��Swing�������Word�ļ�!!!������������jpg�ķ�����һ��,������,�����ַ���!
	public void saveCompentAsWordFile(Component _component, String _fileName, int _width, int _height) throws Exception {
		WordFileUtil wordFileUtil = new WordFileUtil();
		StringBuilder sb_text = new StringBuilder(); //
		sb_text.append(wordFileUtil.getWordFileBegin()); // word��ͷ��!
		sb_text.append(wordFileUtil.getWordTitle("������ͼƬ������Word")); // ����!
		int li_picwidth = _width; //
		int li_picheight = _height; //
		if (_width > 400) { // �����ȴ���420,�򰴱�����С!!
			li_picwidth = 400;
			li_picheight = (_height * 400) / _width; //
		}
		if (li_picheight > 620) {
			li_picheight = 620;
		}
		String str_64code = getCompent64Code(_component, _width, _height, true); // ��ͼƬ���64λ��!
		String str_imgxml = wordFileUtil.getImageXml(str_64code, li_picwidth, li_picheight); // �õ�ͼƬ��Word��XML��ʽ���ַ���!
		sb_text.append(str_imgxml); // ����!
		sb_text.append(wordFileUtil.getWordFileEnd()); // �����ļ�β!!
		writeStrToOutputStream(new FileOutputStream(_fileName), sb_text.toString()); // д�ļ�!
	}

	//��һ��ͼƬ�������ų�ָ����С��ͼ��!��ΪUI����BS�˶��п����õ�,���Է���TB��
	public Image getImageScale(Image _image, int _width, int _height) {
		int li_width = _image.getWidth(null); //ԭ��ͼƬ���!
		int li_height = _image.getHeight(null); //ԭ��ͼƬ�߶�!
		double ld_scale_x = (double) _width / (double) li_width; //��ȵı���!!������ת����double�����,����ò���С��λ!
		double ld_scale_y = (double) _height / (double) li_height; //�߶ȵı���!!

		//�ȴ���BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB); //
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		//ת��!!
		AffineTransform tx = new AffineTransform();
		tx.scale(ld_scale_x, ld_scale_y); //�����Ǳ���!!!
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR); //
		bimage = op.filter(bimage, null); //
		return bimage; //
	}

	//��һ��ͼƬ����ת/����!
	public Image getImageFlipping(Image _image, int _type) {
		int li_width = _image.getWidth(null); //ԭ��ͼƬ���!
		int li_height = _image.getHeight(null); //ԭ��ͼƬ�߶�!
		//�ȴ���BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		//ת��
		if (_type == 1) { //���·�ת!!
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -_image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null);
		} else if (_type == 2) { //���ҷ�ת!!
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-_image.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null);
		} else if (_type == 3) { //����������ͬʱ��ת!!
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			tx.translate(-_image.getWidth(null), -_image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null); //
		}
		return bimage; //
	}

	//��ת!!!
	public Image getImageRotate(Image _image, double _theta) {
		int li_width = _image.getWidth(null); //ԭ��ͼƬ���!
		int li_height = _image.getHeight(null); //ԭ��ͼƬ�߶�!
		//�ȴ���BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		AffineTransform tx = new AffineTransform(); //
		tx.rotate(Math.toRadians(_theta), bimage.getWidth() / 2, bimage.getHeight() / 2); //��ת���ٶ�,����90,270,-90
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage bimage2 = op.filter(bimage, null);
		return bimage2; //
	}

	// ��ȡһ�����������ַ���,���������ĸ�ʽ!
	public String readFromInputStreamToStr(InputStream _ins) {
		try {
			byte[] bys = readFromInputStreamToBytes(_ins); //
			return new String(bys, "GBK"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	// ��ȡһ��������,�����������ֽ�! ������ļ�
	public byte[] readFromInputStreamToBytes(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java�ٷ���վǿ�ҽ���ʹ�øö����������,˵�Ǹ���׳,��ƽ��,���ȶ�!!!��Ϊ����һ��������!���ڴ���Ӳ�����ľ��Ѻ�!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // ͨ��ѭ����ȡ,������,���ȶ�!!��Լ�ڴ�!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				bout.close(); // �ر������!!!
			} catch (Exception exx1) {
			}
			try {
				_ins.close(); // �ر�������!!
			} catch (Exception exx1) {
			}
		}
	}

	// ��һ���ַ���д���ļ���ȥ!���ұ��������ĸ�ʽ
	public void writeStrToOutputStream(OutputStream _out, String _str) {
		try {
			writeBytesToOutputStream(_out, _str.getBytes("GBK")); // ���ĵ�!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	// ��һ���ֽ�����д���ļ���ȥ!
	public void writeBytesToOutputStream(OutputStream _out, byte[] _bys) {
		ByteArrayInputStream bins = null; //
		try {
			bins = new ByteArrayInputStream(_bys); // Java�ٷ���վǿ�ҽ���ʹ�øö����������,˵�Ǹ���׳,��ƽ��,���ȶ�!!!��Ϊ����һ��������!���ڴ���Ӳ�����ľ��Ѻ�!
			byte[] tmpbys = new byte[2048]; //
			int pos = -1; //
			while ((pos = bins.read(tmpbys)) != -1) { // ѭ������
				_out.write(tmpbys, 0, pos); // д��
			}
		} catch (Exception ex) { //
			ex.printStackTrace(); //
		} finally {
			try {
				_out.close(); // �ر�������!!
			} catch (Exception exx1) {
			}
			try {
				bins.close(); // �ر������!!!
			} catch (Exception exx1) {
			}
		}
	}

	/**
	 * ��һ��HashVO[]ת����һ��Html���
	 * 
	 * @param _hvs
	 * @return
	 */
	public String getHtmlByHashVOs(HashVO[] _hvs) {
		StringBuffer sb_html = new StringBuffer(); //
		if (_hvs != null && _hvs.length > 0) {
			sb_html.append("<table width=\"100%\" style=\"border-collapse:   collapse; font-size: 12px; \" style=\"word-break:break-all\">\r\n");
			sb_html.append("<tr><td align=\"center\" bgcolor=\"EEEEEE\" style=\"border:   solid   1px   #888888; font-size: 12px;\">���</td>");
			String[] str_titles = _hvs[0].getKeys(); //
			for (int i = 0; i < str_titles.length; i++) {
				sb_html.append("<td align=\"center\" bgcolor=\"EEEEEE\" style=\"border:   solid   1px   #888888; font-size: 12px;\">" + str_titles[i] + "</td>");
			}
			sb_html.append("</tr>\r\n");

			for (int i = 0; i < _hvs.length; i++) {
				sb_html.append("<tr onMouseOut=\"if(bgColor!='#ffff00'){bgColor='#ffffff'}\" onMouseOver=\"if(bgColor!='#ffff00'){bgColor='#00ffff'}\" onClick=\"bgColor='#ffff00'\" ondblClick=\"bgColor='#ffffff'\">\r\n");
				sb_html.append("<td style=\"border:   solid   1px   #888888; font-size: 12px;\">" + (i + 1) + "</td>\r\n");
				for (int j = 0; j < str_titles.length; j++) {
					sb_html.append("<td style=\"border:   solid   1px   #888888; font-size: 12px;\">" + _hvs[i].getStringValue(str_titles[j], "&nbsp;") + "</td>\r\n"); //
				}
				sb_html.append("</tr>\r\n");
			}
			sb_html.append("</table>\r\n"); //
		} else {

		}
		return sb_html.toString(); //
	}

	/**
	 * ����һ��VectorMap����Html,���Html��һ��������,ÿ���������涼��һ������,�������������չ���������������
	 * 
	 * @param _vm
	 * @return
	 */
	public String getHtmlByVectorMap(VectorMap _vm, int _type) {
		StringBuffer sb_html = new StringBuffer(); //
		if (_vm == null) {
			return "û������!"; //
		}

		String[] str_keys = _vm.getKeysAsString(); // �õ���������
		if (str_keys.length == 0) {
			return "û������!"; //
		}
		if (_type == 1) { // �����չ�������͵�
			String str_btnstyle_1 = "BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 20px; WIDTH=70px; BACKGROUND-COLOR=#EEEEEE;";

			sb_html.append("<script language=\"javascript\" type=\"text/javascript\">\r\n");
			sb_html.append("function hid_show(par_1)\r\n");
			sb_html.append("{\r\n");
			sb_html.append("  var tbobj=document.getElementById(\"table_\" + par_1);\r\n");
			sb_html.append("  if(tbobj.currentStyle.display == \"none\") {\r\n");
			sb_html.append("     tbobj.style.display=\"\";\r\n");
			sb_html.append("     document.getElementById(\"img_\" + par_1).src=\"./images/zoomout.gif\";\r\n");
			sb_html.append("   }else{\r\n");
			sb_html.append("     tbobj.style.display=\"none\";\r\n");
			sb_html.append("     document.getElementById(\"img_\" + par_1).src=\"./images/zoomin.gif\";\r\n");
			sb_html.append("   }\r\n");
			sb_html.append("}\r\n");

			sb_html.append("function showall()\r\n");
			sb_html.append("{\r\n");
			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("     document.getElementById(\"table_" + str_keys[i] + "\").style.display=\"\";\r\n");
				sb_html.append("     document.getElementById(\"img_" + str_keys[i] + "\").src=\"./images/zoomout.gif\";\r\n");
			}
			sb_html.append("}\r\n");

			sb_html.append("function closeall()\r\n");
			sb_html.append("{\r\n");
			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("     document.getElementById(\"table_" + str_keys[i] + "\").style.display=\"none\";\r\n");
				sb_html.append("     document.getElementById(\"img_" + str_keys[i] + "\").src=\"./images/zoomin.gif\";\r\n");
			}
			sb_html.append("}\r\n");

			sb_html.append("</script>\r\n"); //

			sb_html.append("<input id=\"button_expandall\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"ȫ��չ��\"  onclick=\"showall();\">\r\n");
			sb_html.append("<input id=\"button_closeall\"  type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"ȫ������\"  onclick=\"closeall();\">\r\n");

			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("<table width=\"100%\" border=\"0\">\r\n");
				sb_html.append("<tr onclick=\"hid_show('" + str_keys[i] + "');\"  border=\"0\"><td bgcolor=\"CCCCCC\" border=\"0\" style=\"font-size: 15px; color: #333333; line-height: 18px; font-family: ����\"><img id=\"img_" + str_keys[i] + "\" src=\"./images/zoomout.gif\"/><strong>" + str_keys[i] + "</strong></td></tr>\r\n");
				sb_html.append("<tr><td border=0>\r\n");
				sb_html.append("<table id=\"table_" + str_keys[i] + "\" width=\"100%\" align=\"center\" border=0>\r\n");
				sb_html.append("<tr><td>\r\n");
				sb_html.append("" + _vm.get(str_keys[i])); //
				sb_html.append("</td></tr>\r\n");
				sb_html.append("</table>\r\n"); //
				sb_html.append("</td></tr>\r\n"); //
				sb_html.append("</table>\r\n"); //
				// sb_html.append("<br>\r\n"); //
			}
		} else if (_type == 2) { // �����һ�Ű�ť�͵�
			String str_btnstyle_1 = "BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 20px; WIDTH=70px; BACKGROUND-COLOR=#EEEEEE;";
			String str_btnstyle_2 = "BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 20px; WIDTH=70px; BACKGROUND-COLOR=#CAFFFF;";
			sb_html.append("<script language=\"javascript\" type=\"text/javascript\">\r\n");
			sb_html.append("function hid_show(par_1)\r\n");
			sb_html.append("{\r\n");
			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("   document.getElementById(\"button_" + str_keys[i] + "\").style.background=\"#EEEEEE\";\r\n");
				sb_html.append("   document.getElementById(\"table_" + str_keys[i] + "\").style.display=\"none\";\r\n");
			}

			sb_html.append("   document.getElementById(\"button_showalltables\").style.background=\"#EEEEEE\";\r\n");
			sb_html.append("   document.getElementById(\"button_\" + par_1).style.background=\"#CAFFFF\";\r\n");
			sb_html.append("   document.getElementById(\"table_\" + par_1).style.display=\"\";\r\n"); //
			sb_html.append("}\r\n");

			sb_html.append("function showall()\r\n");
			sb_html.append("{\r\n");
			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("   document.getElementById(\"button_" + str_keys[i] + "\").style.background=\"#EEEEEE\";\r\n");
				sb_html.append("   document.getElementById(\"table_" + str_keys[i] + "\").style.display=\"\";\r\n");
			}

			sb_html.append("   document.getElementById(\"button_showalltables\").style.background=\"#CAFFFF\";\r\n");
			sb_html.append("}\r\n");
			sb_html.append("</script>\r\n"); //

			// ���������ť
			for (int i = 0; i < str_keys.length; i++) {
				if (i == 0) {
					sb_html.append("<input id=\"button_" + str_keys[i] + "\" type=\"button\" style=\"" + str_btnstyle_2 + "\"  value=\"" + str_keys[i] + "\"  onclick=\"hid_show('" + str_keys[i] + "')\">\r\n");
				} else {
					sb_html.append("<input id=\"button_" + str_keys[i] + "\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"" + str_keys[i] + "\"  onclick=\"hid_show('" + str_keys[i] + "')\">\r\n");
				}
			}

			sb_html.append("<input id=\"button_showalltables\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"��ʾ����\"  onclick=\"showall();\">\r\n");
			sb_html.append("<br>"); //

			for (int i = 0; i < str_keys.length; i++) {
				if (i == 0) {
					sb_html.append("<table id=\"table_" + str_keys[i] + "\" width=\"100%\" style=\"display:\" border=0>\r\n"); // ��ʾ
				} else {
					sb_html.append("<table id=\"table_" + str_keys[i] + "\" width=\"100%\" style=\"display:none\" border=0>\r\n"); // ����
				}
				sb_html.append("<tr><td bgcolor=\"CCCCCC\"><strong>" + str_keys[i] + "</strong></td></tr>\r\n");
				sb_html.append("<tr><td>\r\n");
				sb_html.append("" + _vm.get(str_keys[i])); //
				sb_html.append("</td></tr>\r\n");
				sb_html.append("</table>\r\n");
			}
		} else {
			sb_html.append("δ֪�ķ��\r\n");
		}
		return sb_html.toString(); //
	}

	public void writeHashToHtmlTableFile(HashVO[] _hvs, String _filename) {
		writeHashToHtmlTableFile(_hvs, _filename, null); //
	}

	public void writeHashToHtmlTableFile(HashVO _hvs[], String _filename, String[] _itemKeys) {
		PrintWriter print = null;
		try {
			print = new PrintWriter(new FileOutputStream(new File(_filename), false));
			StringBuffer sb_html = new StringBuffer(); //
			sb_html.append("<html>\r\n");
			sb_html.append("<head>\r\n");
			sb_html.append("<title>��HashVO��������Html</title>\r\n");
			sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
			sb_html.append("<style   type=\"text/css\"> \r\n");
			sb_html.append("<!--   \r\n");
			sb_html.append(" table   {  border-collapse:   collapse; }   \r\n");
			sb_html.append("td   {  font-size: 12px; border:solid   1px   #888888;  }   \r\n");
			sb_html.append(" -->   \r\n");
			sb_html.append(" </style>   \r\n");
			sb_html.append("</head>\r\n");
			sb_html.append("<body>\r\n");
			print.println(sb_html.toString());

			if (_hvs.length > 0) {
				print.println("<table>");
				String[] str_keys = null;
				if (_itemKeys != null) {
					str_keys = _itemKeys;
				} else {
					str_keys = _hvs[0].getKeys();
				}
				StringBuffer sb_header = new StringBuffer();
				sb_header.append("<tr><td>���</td>");
				for (int i = 0; i < str_keys.length; i++) {
					sb_header.append((new StringBuilder("<td>")).append(str_keys[i]).append("</td>").toString());
				}
				sb_header.append("</tr>\r\n");
				print.println(sb_header.toString());
				for (int i = 0; i < _hvs.length; i++) {
					sb_header = new StringBuffer();
					sb_header.append("<tr>");
					sb_header.append("<td>" + (i + 1) + "</td>");

					for (int j = 0; j < str_keys.length; j++)
						sb_header.append((new StringBuilder("<td>")).append(_hvs[i].getStringValue(str_keys[j], "&nbsp;")).append("</td>").toString());

					sb_header.append("</tr>");
					print.println(sb_header.toString());
				}

				print.println("</table>");
				print.println("</body>");
				print.println("</html>");
				WLTLogger.getLogger(TBUtil.class).debug("��һ��HashVO[]����ġ�" + _hvs.length + "������¼������ļ���" + _filename + "���ɹ�!!!"); //
			} else {
				print.println("û������!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (print != null) {
					print.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

	}

	public void writeStringArrayToHtmlTableFile(String _data[][], String _filename) {
		PrintWriter print = null;
		try {
			print = new PrintWriter(new FileOutputStream(new File(_filename), false));
			print.println("<html>");
			print.println("<head>");
			print.println("<meta http-equiv=\"Content-Language\" content=\"zh-cn\">");
			print.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">");
			print.println("<title>\u5BFC\u51FA\u6570\u636E</title>");
			print.println("<style type=\"text/css\">");
			print.println(".style_table {");
			print.println(" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px");
			print.println("}");
			print.println(".style_td {");
			print.println(" font-size: 12px; color: #333333; line-height: 18px; font-family: \u5B8B\u4F53");
			print.println("}");
			print.println("</style>");
			print.append("</head>");
			print.println("<body>");
			if (_data != null && _data.length > 0) {
				print.println("<table id=\"table1\" class=\"style_table\" cellSpacing=\"1\" cellPadding=\"5\" align=\"left\" bgColor=\"#999999\" border=\"0\">");
				for (int i = 0; i < _data.length; i++) {
					StringBuffer sb_header = new StringBuffer();
					sb_header.append("<tr>");
					for (int j = 0; j < _data[i].length; j++) {
						sb_header.append((new StringBuilder("<td bgColor=\"#ffffff\" class=\"style_td\">")).append(_data[i][j] != null ? _data[i][j] : "&nbsp;").append("</td>").toString());
					}
					sb_header.append("</tr>");
					print.println(sb_header.toString());
				}

				print.println("</table>");
			} else {
				print.println("û������!");
			}
			print.println("</body>");
			print.println("</html>");
			System.out.println("����ļ�[" + _filename + "]�ɹ�!!!"); //
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			print.close(); //
		}

	}

	/**
	 * ת��Image����Ϊbyte����
	 * 
	 * @param image
	 *            Image����
	 * @param format
	 *            image��ʽ�ַ���.��"jpeg","png"
	 * @return byte����
	 */
	private byte[] imageToBytes(Image image, String format) {
		BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bImage.getGraphics();
		bg.drawImage(image, 0, 0, null);
		bg.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImage, format, out);
		} catch (IOException e) {
			// Log.log(null, "imageToBytes(): " + e);
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * ��BillVO�е�����ת����һ��HashMap..
	 * 
	 * @param _billVO
	 * @return
	 */
	public HashMap getMapFromBillVO(BillVO _billVO) {
		HashMap map = new HashMap(); // ������ϣ��
		String[] str_keys = _billVO.getKeys(); //

		for (int i = 0; i < str_keys.length; i++) {
			map.put(str_keys[i], _billVO.getObject(str_keys[i]) == null ? "" : _billVO.getObject(str_keys[i]).toString()); //
		}

		return map; //
	}

	public String getExceptionStringBuffer(Throwable _ex) {
		return getExceptionStringBuffer(_ex, false, false); //
	}

	/**
	 * ��һ���쳣�Ķ�ջת�����ַ���...
	 * 
	 * @return
	 */
	public String getExceptionStringBuffer(Throwable _ex, boolean _isHtmlFormat, boolean _isHtmlContainHead) {
		try {
			if (_ex instanceof WLTAppException) { //�����ҵ���쳣,��ֱ�ӷ���˵��,��Ҫ��ջ,����ͻ�����!!
				return _ex.getMessage(); //
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // ����һ����������
			PrintWriter pw = new PrintWriter(bos, true); //
			_ex.printStackTrace(pw); // ���쳣��ӡ����������
			if (_ex.getCause() != null) { //�������ԭ��...�Ժ�����Ӧ���Ǹ��ݹ�ѭ��
				pw.println("���쳣������ԭ��:"); //
				_ex.getCause().printStackTrace(pw); //
			}
			byte exbytes[] = bos.toByteArray(); // ȡ�ö������Ķ���������
			bos.close(); // �ر���.
			String sb_exstack = new String(exbytes, "GBK"); // ȡ���ַ���s
			if (!_isHtmlFormat) {
				return sb_exstack; //
			} else { // �����html���,��Ҫת��һ��
				sb_exstack = "<font size=2 color=\"red\">\r\n" + replaceAll(replaceHtmlEncode(sb_exstack), "\r", "<br>") + "</font>"; //	
				if (!_isHtmlContainHead) {
					return sb_exstack;
				} else { // ����������html������!
					StringBuffer sb_exception = new StringBuffer(); //
					sb_exception.append("<html>\r\n");
					sb_exception.append("<head>\r\n");
					sb_exception.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
					sb_exception.append("</head>\r\n");
					sb_exception.append("<body>\r\n");
					sb_exception.append(sb_exstack);
					sb_exception.append("\r\n</body>\r\n");
					sb_exception.append("</html>\r\n");
					return sb_exception.toString(); //
				}
			}
		} catch (Exception e) {
			return e.getMessage(); //
		}
	}

	// ��ӡһ���쳣��ջ
	public static synchronized void printStackTrace(Throwable _ex) {
		if (tbUtil.getJVSite() == TBUtil.JVMSITE_CLIENT) { // ����ǿͻ�����ֱ�����
			_ex.printStackTrace();
		} else { // ����Ƿ�������,�����һ������,�ж��������ջ����ֻ���һ�л�!!
			if (cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack) { // �������������쳣!!
				_ex.printStackTrace(); //
			} else {
				System.err.println("�����쳣,ֻ����쳣����:��" + _ex.getMessage() + "��,���˽��ջ��Ϣ�����Ա��ϵ."); //
			}
		}
	}

	/**
	 * �������һ�����һ������,ֻ֧��String���Ͳ���,���������� ����"com.pushworld.TestAction.fn_1()"
	 * ���������еķ�����һ��һά����Ĳ����б�!!! һά����ĺô��Ǽ��,��ȱ��Ϊ��λ����չ������,
	 * ���������ָ���һ��HashMap�����ķ���,���ݳ��ڵľ�������,�����������ֲ������õĵط�(������ʽ,���ն����),Ӧ��ͳһʹ��HashMap,��Ϊ��������ô�:
	 * 1.˳�����û��!!! ����String[],һ���ͳ�����!!
	 * 2.key�������һ������˵��,����String[],ʱ�䳤�����Լ�����֪���ڼ�λ��ʲô��˼!!!
	 * 3.��ϣ���Value�������������,�ǳ�������ԭ�в������кϲ�,�ܼ����ˬ!!!
	 * 
	 * @param _formula
	 * @return
	 */
	public Object reflectCallMethod(String _formula) {
		try {
			String str_className = _formula.substring(0, _formula.lastIndexOf(".")); // ����
			String str_methodName = _formula.substring(_formula.lastIndexOf(".") + 1, _formula.indexOf("(")); // ������
			String str_pars = _formula.substring(_formula.indexOf("(") + 1, _formula.lastIndexOf(")")); // ���в���!
			String[] str_parItems = split(str_pars, ","); //
			return reflectCallMethod(str_className, str_methodName, str_parItems); //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// �������!
	public Object reflectCallMethod(String _className, String _methodName, String[] _pars) {
		try {
			String str_className = _className; // ����
			String str_methodName = _methodName; // ������
			String[] str_parItems = _pars; // ����!!
			for (int i = 0; i < str_parItems.length; i++) {
				str_parItems[i] = str_parItems[i].trim(); //
				if (str_parItems[i].startsWith("\"")) {
					str_parItems[i] = str_parItems[i].substring(1, str_parItems[i].length()); //
				}
				if (str_parItems[i].startsWith("'")) {
					str_parItems[i] = str_parItems[i].substring(1, str_parItems[i].length()); //
				}
				if (str_parItems[i].endsWith("\"")) {
					str_parItems[i] = str_parItems[i].substring(0, str_parItems[i].length() - 1); //
				}
				if (str_parItems[i].endsWith("'")) {
					str_parItems[i] = str_parItems[i].substring(0, str_parItems[i].length() - 1); //
				}
			}
			Class cls = Class.forName(str_className); // ��һ�кܿ�����ClassNotFound�쳣
			Object obj = cls.newInstance();
			Class[] parcls = new Class[str_parItems.length]; // 
			for (int i = 0; i < parcls.length; i++) {
				parcls[i] = String.class;
			}
			Method method = cls.getMethod(str_methodName, parcls); // ��һ�кܿ�����java.lang.NoSuchMethodException�쳣..
			Object returnObj = method.invoke(obj, str_parItems); //
			return returnObj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * ͨ������ͨ�õ���һ���෽��,������������ǲ����뷵��ֵ����HashMap!!! ��������ǳ�����,�������κγ��ϵ���Ҫ���з�����õĵط�!!!
	 * 
	 * @param _formula
	 *            ��ʽ,�ܹؼ�,��ʽ�������ӷ�����������,�������滹����֧�ֲ���!!!
	 *            ����:cn.com.xch.TestCls.fn_1(),����cn.com.xch.TestCls.fn_1("�Ƿ�չ��,"Y","�㼶,"3"),Ȼ�������Զ��������еĲ���ƴ�ɹ�ϣ��,Ȼ��ϲ���һ������!!!�����Ϳ��Ը�����ܵ�����ĳ������!!
	 *            ���ݳ��ڵľ�������,�����������ֲ������õĵط�(������ʽ,���ն����),Ӧ��ͳһʹ��HashMap,��Ϊ��������ô�:
	 *            1.˳�����û��!!! ����String[],һ���ͳ�����!!
	 *            2.key�������һ������˵��,����String[],ʱ�䳤�����Լ�����֪���ڼ�λ��ʲô��˼!!!
	 *            3.��ϣ���Value�������������,�ǳ�������ԭ�в������кϲ�,�ܼ����ˬ!!!
	 * 
	 * ��ǰ��һ�ֹ�ϣ������÷�����"�Ƿ�չ��=Y"������,������������ʵ������,�����ڷǹ�ʽִ�е�ʱ��Ҳ��(�����Ǵ��ַ���),����������ʽִ�е�ʱ��,��ʱ��ֵ�Ͳ�һ�����ַ���,����getClassValue(),��������Ҫ�õ�����BillCardPanel���,��ǰֻ��֧��String�Ͳ�����!!!
	 * ���Բο�Swing��LookAndFeel��˼·,������żλΪһ��,����λ��key,ż��λ��Value,��1-2,3-4,5-6��һ��һ�Ե�,key�����ַ���,��Value�����Ǹ�����������!!!
	 * ����BillCardPanel��!!
	 * ������żλ��Ӧ�ķ�����һ�����ܵ�,ͨ�õ�,������չ����ݾ����õķ���,����Jdk������������Ʒ���,����ն���,��ʽ����ȶ�Ӧ����ô��!!!��Ϊʲôһ��ʼû���뵽��???
	 * �������Ǿ��鲻����!!
	 * @param _parMap
	 * @return
	 */
	public HashMap reflectCallCommMethod(String _formula, HashMap _parMap) {
		try {
			HashMap realParMap = _parMap; //
			String str_className = _formula.substring(0, _formula.lastIndexOf(".")); // ����
			String str_methodName = _formula.substring(_formula.lastIndexOf(".") + 1, _formula.indexOf("(")); //
			String str_pars = _formula.substring(_formula.indexOf("(") + 1, _formula.lastIndexOf(")"));
			String[] str_parItems = split(str_pars, ","); //
			if (str_parItems != null && str_parItems.length > 0) {
				if (realParMap == null) {
					realParMap = new HashMap(); //
				}
				for (int i = 0; i < (str_parItems.length / 2); i++) {
					String str_key = str_parItems[i * 2].trim(); // //
					String str_value = str_parItems[i * 2 + 1].trim(); //
					if (str_key.startsWith("\"") || str_key.startsWith("'")) {
						str_key = str_key.substring(1, str_key.length()); //
					}
					if (str_key.endsWith("\"") || str_key.endsWith("'")) {
						str_key = str_key.substring(0, str_key.length() - 1); //
					}
					if (str_value.startsWith("\"") || str_value.startsWith("'")) {
						str_value = str_value.substring(1, str_value.length()); //
					}
					if (str_value.endsWith("\"") || str_value.endsWith("'")) {
						str_value = str_value.substring(0, str_value.length() - 1); //
					}
					realParMap.put(str_key, str_value); // �����ȥ!!!
				}
			}
			Class cls = Class.forName(str_className); // ��һ�кܿ�����ClassNotFound�쳣
			Object obj = cls.newInstance();
			Method method = cls.getMethod(str_methodName, new Class[] { java.util.HashMap.class }); // �÷���ֻ֧��HashMap����,��һ�кܿ�����java.lang.NoSuchMethodException�쳣..
			HashMap returnMap = (HashMap) method.invoke(obj, realParMap); //
			return returnMap; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * ���ݱ�ǩ�������Ƿ�ɲ�ѯ���ɱ��棬�Ƿ�htmlת����ʵ�ʵ�
	 * 
	 * @param _text
	 * @param _isAdmin
	 * @param _ismustInput
	 * @param _isCanQuery
	 * @param _isCanSave
	 * @return
	 */
	//sunfujun/20120720/�����ͷ��������
	public String getLableRealText(String _text, boolean _isAdmin, boolean _ismustInput, boolean _isCanQuery, boolean _isCanSave) {//sunfujun/20120720/�����б��ͷ��*
		return this.getLableRealText(_text, _isAdmin, _ismustInput, _isCanQuery, _isCanSave, true);
	}

	public String getLableRealText(String _text, boolean _isAdmin, boolean _ismustInput, boolean _isCanQuery, boolean _isCanSave, boolean isCard) {//sunfujun/20120626/żȻһ��������޸�_xch
		if (_text == null) {
			return "û�ж����ǩ��";
		}
		int li_pos_1 = _text.trim().toLowerCase().indexOf("<html>");
		int li_pos_2 = _text.trim().toLowerCase().lastIndexOf("</html>");
		boolean bo_ishtml = false; //
		if (li_pos_1 == 0 && li_pos_2 > 0) {
			bo_ishtml = true;
		}
		String str_prefix = "";
		if (_ismustInput) {
			if (isCard)
				str_prefix = str_prefix + "*";
		}
		//		if (_isAdmin) { // ����ǹ������ //����ͳͳȥ���ĳ��Ҽ����Բ鿴
		//			if (!_isCanQuery) {
		//				str_prefix = str_prefix + "��";
		//			}
		//			if (!_isCanSave) {
		//				str_prefix = str_prefix + "-";
		//			}
		//		}

		if (bo_ishtml) {
			return "<html>" + str_prefix + _text.substring(6, li_pos_2) + "&nbsp;</html>"; //
		} else {
			return str_prefix + _text + " ";
		}
	}

	public String getHtmlHead() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<TITLE>��������״̬</TITLE>\r\n"); // ������
		sb_html.append("<style type=\"text/css\">\r\n");
		sb_html.append(".p_text {\r\n");
		sb_html.append(" font-size: 12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".style_button {\r\n");
		sb_html.append(" BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH=55px; BACKGROUND-COLOR: #EEEEEE;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("table   {  border-collapse:   collapse; font-size: 12px;};\r\n");
		sb_html.append("td   {  border:   solid   1px   #888888; font-size: 12px; };\r\n");
		sb_html.append("</style>\r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<script language=\"JavaScript\" src=\"./applet/calendar.js\"></script>\r\n"); //
		sb_html.append("<body bgcolor=\"#FFFFFF\" topmargin=10 leftmargin=10 rightmargin=0 bottommargin=0 marginwidth=10 marginheight=10>\r\n");
		sb_html.append("<p class=\"p_text\">\r\n"); //
		return sb_html.toString(); //
	}

	public String getHtmlTail() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("</p>\r\n");
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * time ��ʽΪ��"2008��;" �� "2008��1����;" �� 2008��01��;" �� "2008-01-01;" ��
	 * "2008-01-01;2008-02-01" isFree ��ʽΪ��"N" ��ʾ��ѯĳʱ�������Ч������������"Y"
	 * ��ʾ��ѯĳʱ����ڷż�������������ʾĳʱ�������������
	 * 
	 * @param _time
	 * @param _isFree
	 * @return
	 */

	public String countWorkdays(String _time, String _isFree) {
		String time = convertComp_dateTimeFormat(_time);
		String[] times = time.split(";");
		String begin = times[0];
		String end = times[1];
		String sql = "select count(id) from pub_dateOption where  dayTime>='" + begin + "' and  dayTime<='" + end + "' ";
		if ("N".equalsIgnoreCase(_isFree)) {
			sql += "  and isFree='N'";
		} else if ("Y".equalsIgnoreCase(_isFree)) {
			sql += "  and isFree='Y'";
		}
		String countday = "";
		try {
			if (tbUtil.getJVSite() == TBUtil.JVMSITE_CLIENT) {
				countday = UIUtil.getStringValueByDS(null, sql);
			} else {
				countday = new cn.com.infostrategy.bs.common.CommDMO().getStringValueByDS(null, sql);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countday;
	}

	/**
	 * ���������ص�ʱ��ת���� "2008;2009" �� "2008-01;2008-02" �� "2008-01-01;2009-01-01"
	 * ��ʽ��ʱ���
	 * 
	 * @param _time
	 * @return
	 */
	public String convertComp_dateTimeFormat(String _time) {
		String begin = "";
		String end = "";
		if (_time == null || "".equals(_time)) {
			return null;
		} else if (_time.endsWith("��;")) { // ��ʽ��"2008��;"
			begin = _time.substring(0, 4);
			end = Integer.parseInt(begin) + 1 + "";
		} else if (_time.endsWith("����;")) { // ��ʽ��"2008��1����;"
			String quarter = _time.substring(5, 6);
			String month1 = Integer.parseInt(quarter) * 3 - 2 + "";
			String month2 = Integer.parseInt(quarter) * 3 + 1 + "";
			if (month1.length() == 1) {
				begin = _time.substring(0, 4) + "-0" + month1;
			} else {
				begin = _time.substring(0, 4) + "-" + month1;
			}
			if (month2.length() == 1) {
				end = _time.substring(0, 4) + "-0" + month2;
			} else {
				end = _time.substring(0, 4) + "-" + month2;
			}
		} else if (_time.endsWith("��;")) { // ��ʽ��"2011��02��;"
			String month1 = _time.substring(5, 7);
			String month2 = Integer.parseInt(month1) + 1 + "";
			if (month2.length() == 1) {
				month2 = "0" + month2;
			}
			begin = _time.substring(0, 4) + "-" + month1;
			end = _time.substring(0, 4) + "-" + month2;
			if ("12".equals(month1)) {
				end = _time.substring(0, 4) + "-12-31";
			}
		} else if (_time.endsWith(";")) { // ��ʽΪ��"2008-01-01;"
			begin = _time.substring(0, 10);
			end = begin;
		} else { // ��ʽΪ��"2008-01-01;2008-02-01"
			return _time;
		}
		return begin + ";" + end;

	}

	/**
	 * time ��ʽΪ��"2008-01-01;2008-02-01" isFree ��ʽΪ��"N" ��ʾ��ѯĳʱ�������Ч������������"Y"
	 * ��ʾ��ѯĳʱ����ڷż�������������ʾĳʱ�������������
	 * 
	 * @param _time
	 * @param _isFree
	 * @return
	 */

	public Vector countWorkdays(String[] _time, String _isFree) {
		if (_time == null || _time.length <= 0) {
			return null;
		}
		String[] time = null;
		String[] sqls = new String[_time.length];
		String condition = "";
		Vector countday = null;
		if ("N".equalsIgnoreCase(_isFree)) {
			condition = "  and isFree='N'";
		} else if ("Y".equalsIgnoreCase(_isFree)) {
			condition = "  and isFree='Y'";
		}
		for (int i = 0; i < _time.length; i++) {
			time = _time[i].split(";");
			sqls[i] = "select count(id)  days from pub_dateOption where  dayTime>='" + time[0] + "' and  dayTime<='" + time[1] + "' " + condition;
		}
		try {
			if (tbUtil.getJVSite() == TBUtil.JVMSITE_CLIENT) {
				countday = UIUtil.getHashVoArrayReturnVectorByMark(null, sqls);
			} else {
				countday = new cn.com.infostrategy.bs.common.CommDMO().getHashVoArrayReturnVectorByDS(null, sqls);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countday;
	}

	/**
	 * �����ַ�����ʱ���
	 * @param _date1
	 * @param _date2
	 * @return
	 * @throws Exception
	 */
	public long betweenTwoTimeSecond(String _date1, String _date2) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date1 = sdf.parse(_date1);
		java.util.Date date2 = sdf.parse(_date2);

		long li_second = (date2.getTime() - date1.getTime()) / 1000; // ��
		return li_second; //
	}

	/**
	 * ��BillVO���������ȡ_limit�����ݣ����ظ���
	 * 
	 * @param _arrs
	 * @param _limit
	 * @return
	 */
	public BillVO[] getArrayOnRandom(BillVO[] _arrs, int _limit) {
		BillVO[] returns = new BillVO[_limit];
		int arrsLength = _arrs.length;
		Vector rows = new Vector();
		if (_arrs != null && arrsLength > _limit) {
			for (int i = 0; i < _limit;) {
				int row = (int) (Math.random() * arrsLength);
				if (rows.contains(row)) {
					continue;
				}
				rows.add(row);
				returns[i] = _arrs[row];
				i++;
			}
			return returns;
		}
		return _arrs;

	}

	/*
	 * �õ�ָ�����������
	 */
	public String getTableAllCols(String tableName) throws Exception {
		TableDataStruct templetStruct = null;
		StringBuffer sb_item = new StringBuffer();
		String names[] = null;
		boolean flag = false; //��oracleϵͳ��ı�ṹ
		if (getJVSite() == TBUtil.JVMSITE_CLIENT) {
			if (getDefaultDataSourceType().equalsIgnoreCase("ORACLE")) {
				try {
					names = UIUtil.getStringArrayFirstColByDS(null, "select column_name from user_tab_columns where table_name ='" + tableName.toUpperCase() + "'");
					flag = true;
				} catch (Exception ex) {
					templetStruct = UIUtil.getTableDataStructByDS(null, "select * from " + tableName + " where 1=2");
					ex.printStackTrace();
				}
			} else {
				templetStruct = UIUtil.getTableDataStructByDS(null, "select * from " + tableName + " where 1=2");
			}
		} else {
			if (getDefaultDataSourceType().equalsIgnoreCase("ORACLE")) {
				try {
					names = new cn.com.infostrategy.bs.common.CommDMO().getStringArrayFirstColByDS(null, "select column_name from user_tab_columns where table_name ='" + tableName.toUpperCase() + "'");
					flag = true;
				} catch (Exception ex) {
					templetStruct = new cn.com.infostrategy.bs.common.CommDMO().getTableDataStructByDS(null, "select * from " + tableName + " where 1=2");
					ex.printStackTrace();
				}
			} else {
				templetStruct = new cn.com.infostrategy.bs.common.CommDMO().getTableDataStructByDS(null, "select * from " + tableName + " where 1=2");
			}
		}
		if (!flag) {
			names = templetStruct.getHeaderName();
		}
		int itemlength = names.length;
		for (int i = 0; i < itemlength; i++) {
			if (sb_item.length() == 0) {
				sb_item.append(" " + names[i]);
			} else {
				sb_item.append("," + names[i]);
			}
		}
		sb_item.append(" ");
		return sb_item.toString();
	}

	public static boolean checkPasswordComplex(String str, int level) {
		String regex1 = "^.*\\p{Digit}.*$"; //��������
		String regex2 = "^.*\\p{Punct}.*$"; //�����ַ�
		String regex3 = "^.*\\p{Lower}.*$"; //Сд
		String regex4 = "^.*\\p{Upper}.*$"; //��д
		String regex[] = new String[] { regex1, regex2, regex3, regex4 };
		int matchcount = 0; //ƥ�����
		for (int j = 0; j < regex.length; j++) {
			if (str.matches(regex[j])) {
				matchcount++;
			}
		}
		if (matchcount >= level) {
			return true;
		}
		return false;
	}

	/**
	 * ͼƬBASE64����תΪͼƬ
	 * @param img_base64code
	 * @return
	 */
	public Image convertBase64CodeToImg(String img_base64code) {
		byte[] bytes = tbUtil.convert64CodeToBytes(img_base64code);
		InputStream is = new ByteArrayInputStream(bytes);
		JPEGImageDecoder jpeg_decoder = JPEGCodec.createJPEGDecoder(is);
		BufferedImage buffer_img = null;
		try {
			buffer_img = jpeg_decoder.decodeAsBufferedImage();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return buffer_img;
	}

	/**
	 * ������ͼ�Լ�bomͼ�л����յ�ʱ
	 * ������ҪԲ�λ����Ի�������״
	 * �˷�������Shape���󹩴�ʹ��
	 * �Ժ������Ҫ���ֿ������������
	 * ������,
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 */
	public Shape getShape(int x, int y, int width, int height, int type) {//[sunfujun/20120528]
		Shape pl = null;
		if (type == 0) {
			pl = new java.awt.geom.Ellipse2D.Float(x, y, width, height);
		} else if (type == 1) {
			pl = new Rectangle(x, y, width, height);
		} else if (type == 2) {
			pl = new java.awt.geom.RoundRectangle2D.Float(x, y, width, height, 10, 10);
		} else if (type == 3) {
			pl = new Polygon(new int[] { x, x + width / 2, x + width }, new int[] { y + height, y, y + height }, 3);
		} else if (type == 4) {
			pl = new Polygon(new int[] { x, x + width / 2, x + width, x + width / 2 }, new int[] { y + height / 2, y, y + height / 2, y + height }, 4);
		}
		return pl;
	}

	/**
	 * �˷������ڻ����յ�ĸ���ʱ
	 * ȡ���ֵĳ�ʼX����
	 * @param g2
	 * @param a
	 * @param s
	 * @return
	 */
	public float getStrX(Graphics2D g2, Shape a, String s) {
		BigDecimal acx = new BigDecimal(a.getBounds().getCenterX());
		BigDecimal sw = new BigDecimal(g2.getFontMetrics().stringWidth(s));
		return acx.subtract(sw.divide(new BigDecimal(2), 6, BigDecimal.ROUND_HALF_UP)).floatValue();
	}

	/**
	 *�˷������ڻ����յ�ĸ���ʱ
	 * ȡ���ֵĳ�ʼY����
	 * @param g2
	 * @param a
	 * @param s
	 * @return
	 */
	public float getStrY(Graphics2D g2, Shape a, String s) {
		BigDecimal acy = new BigDecimal(a.getBounds().getCenterY());
		BigDecimal sh = new BigDecimal(g2.getFontMetrics().getHeight());
		return acy.add(sh.divide(new BigDecimal(4), 6, BigDecimal.ROUND_HALF_UP)).floatValue();//Ϊʲô�ǳ���4�Ǻ��ʵģ�Ӧ���ǳ���2�� û��
	}

	//������е���ʹ�õ�TBUtil,�����ڸ������д���һ��,�鷳�����Ҷ���ڴ�,��ôƵ��ʹ�õ��࣬�ɴ��ֱ��ʹ�þ�̬��������xch/2012-05-14��
	//�ڿͻ���(UI)ʹ�ÿ϶�û�£����ڷ������ˣ������߲���ʱ,�Ƿ���ͬ�����⣿��ע�⣡
	public static TBUtil getTBUtil() {
		return tbUtil;
	}

	public static void main(String[] _args) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-M-d HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //
		String str_text = sdf_curr.format(new Date()); //
		System.out.println(str_text); //

		try {
			new TBUtil().getFormulaMacPars("{}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ȡ��ĳһ������ʼ��
	 * @param _month
	 * @return
	 */
	public String getBeginDateByMonth(String date) { // �����/2012-08-27��
		String str_year = date.substring(0, 4); //
		String str_month = date.substring(5, 7); //
		return str_year + "-" + str_month + "-01"; //
	}

	/**
	 * ȡ��ĳһ���½�����
	 * @param _month
	 * @return
	 */
	public String getEndDateByMonth(String date) { // �����/2012-08-27��
		String str_year = date.substring(0, 4); //
		String str_month = date.substring(5, 7); //
		return str_year + "-" + str_month + "-" + getOneMonthDays(Integer.parseInt(str_year), Integer.parseInt(str_month)); //
	}

	/**
	 * ȡ��ĳһ���µ��ܹ�����
	 * @param _year
	 * @param _month
	 * @return
	 */
	public int getOneMonthDays(int _year, int _month) { // �����/2012-08-27��
		if (_month == 2) {
			if (_year % 4 == 0) {
				return 29;
			} else {
				return 28;
			}
		} else if (_month == 1 || _month == 3 || _month == 5 || _month == 7 || _month == 8 || _month == 10 || _month == 12) {
			return 31;
		} else {
			return 30; //
		}
	}

	/**
	 * ǰ�Ƽ�����
	 * @param _month
	 * @return
	 * @throws Exception 
	 */
	public String getBackMonth(String date, int frontmonth) throws Exception { // �����/2012-08-27��
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		Calendar c = new GregorianCalendar();
		c.setTime(sdf.parse(date));
		c.add(c.MONTH, -frontmonth);
		return sdf.format(c.getTime());
	}

	/**
	 * ��ȡIP
	 * @return
	 */
	public String getLocalHostIP() {
		try {
			String str_ip = InetAddress.getLocalHost().getHostAddress();
			return str_ip;
		} catch (Exception ex) {
			return "0.0.0.0";
		}
	}

	/*
	 * ����ʱ�������� first-second @haoming 2013-3-8
	 */
	public int compareTwoDate(String firstDate, String secondDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(firstDate);
		Date date2 = sdf.parse(secondDate);
		long mills = date1.getTime() - date2.getTime();
		return (int) (mills / (long) (1000 * 60 * 60 * 24));
	}

	/**
	 * �����ļ��߼����� �����/2013-05-21��
	 * */
	private boolean copyFile(String _oldFilePath, String _newFilePath) {
		File oldfile = new File(_oldFilePath);
		if (!oldfile.exists()) {
			return false;//���û���ҵ���Ҫ���Ƶ��ļ��򷵻�false
		}
		if (_newFilePath.indexOf("\\") >= 0) {
			_newFilePath = UIUtil.replaceAll(_newFilePath, "\\", "/"); //�Ƚ����滻
		}
		File newfile = new File(_newFilePath.substring(0, _newFilePath.lastIndexOf("/")));//���ļ����ļ���
		if (!newfile.exists()) {//�ж����ļ����ļ����Ƿ���ڣ�����������򴴽�
			newfile.mkdirs();
		}
		InputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(_oldFilePath);
			byte[] by = readFromInputStreamToBytes(input);
			output = new FileOutputStream(_newFilePath);
			output.write(by);
			input.close();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return false;
		} finally {
			try {
				if (output != null) {
					output.close(); // �ر������!!!
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (input != null) {
					input.close(); // �ر�������!!
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;//������Ƴɹ����򷵻�true
	}

	/**
	 * ѹ����ZIP�ļ�,��תΪbyte[],��ѹ���ļ����ļ���
	 * @param filePath �ļ�·���� ����d:/ABC.doc
	 * @return Object[0]��ŷ��ص�ѹ���ļ�byte[],Object[1]���ѹ���ļ����ļ��� 
	 * @author YangQing-2013-05-13
	 */
	public Object[] compressFile(String filePath) {
		Object[] returnObject = new Object[2];
		byte[] compressBytes = null;
		try {
			if (filePath.contains("\\")) {
				filePath.replace("\\", "/");
			}
			if (filePath.endsWith("/")) {//�����'/'��β����ȥ��
				filePath = filePath.substring(0, filePath.length() - 1);
			}
			File zipfile = new File(filePath.substring(filePath.lastIndexOf("/") + 1) + ".zip");

			FileOutputStream fileOutputStream = new FileOutputStream(zipfile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
			File file = new File(filePath);
			zipFile(file, filePath, out);

			out.closeEntry();
			out.close();

			FileInputStream input = new FileInputStream(zipfile);
			compressBytes = new byte[(int) zipfile.length()];
			input.read(compressBytes);
			input.close();

			if (zipfile.exists()) {
				zipfile.delete();
			}
			returnObject[0] = compressBytes;
			returnObject[1] = zipfile.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObject;
	}

	/**
	 * ѹ��ZIP
	 * @param _file Ҫѹ����(��)�ļ�
	 * @param filePath Ҫѹ������Ŀ¼���ļ�
	 * @param out ZIP�����
	 * @author YangQing/2013-05-13
	 */
	private void zipFile(File _file, String filePath, ZipOutputStream out) {
		try {
			byte data[] = new byte[2048];
			File[] files = new File[0];
			if (_file.isDirectory()) {//������ļ���Ŀ¼
				files = _file.listFiles();
			} else {
				files = new File[1];
				files[0] = _file;
			}

			for (int i = 0; i < files.length; i++) {
				String folder = files[i].getPath();
				if (folder.contains("\\")) {
					folder = folder.replace("\\", "/");
				}
				if (files[i].isDirectory()) {//���ļ���	
					ZipEntry entry = new ZipEntry(folder.substring(filePath.length() + 1) + "/");
					out.putNextEntry(entry);
					zipFile(files[i], filePath, out);
				} else {
					String fn = folder.substring(folder.lastIndexOf("/") + 1);
					FileInputStream inputStream = new FileInputStream(files[i]);
					BufferedInputStream bis = new BufferedInputStream(inputStream, 2048);
					ZipEntry entry = new ZipEntry(fn);
					out.putNextEntry(entry);

					int count;
					while ((count = bis.read(data, 0, 2048)) != -1) {
						out.write(data, 0, count);
					}
					bis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ѹ��ZIP�ļ�
	 * @param basePath Ҫ��ѹ���� ��:D:/xx/
	 * @param zipPath ����ѹ��ZIP·�� ��:d:/xx/ABC.doc
	 * @return ��ѹ����ļ�·��
	 * @author YangQing/2013-05-14
	 */
	public void unZip(String basePath, String zipPath) {
		if (basePath.contains("\\")) {
			basePath.replace("\\", "/");
		}
		if (zipPath.contains("\\")) {
			zipPath.replace("\\", "/");
		}
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}
		try {
			ZipFile zip = new ZipFile(zipPath);
			Enumeration en = zip.entries();
			while (en.hasMoreElements()) {
				ZipEntry entry2 = (ZipEntry) en.nextElement();
				//���Ŀ¼��Ϊһ��file����һ�Σ�����ֻ����Ŀ¼�Ϳ��ԣ�֮�µ��ļ����ᱻ��������
				if (entry2.isDirectory()) {
					new File(basePath + entry2.getName()).mkdirs();
					continue;
				}
				BufferedInputStream bis2 = new BufferedInputStream(zip.getInputStream(entry2));
				File file = new File(basePath + entry2.getName());
				File parent = file.getParentFile();
				if ((parent != null) && (!parent.exists())) {
					parent.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos, 2048);
				int count1;
				byte data2[] = new byte[2048];
				while ((count1 = bis2.read(data2, 0, 2048)) != -1) {
					bos.write(data2, 0, count1);
				}
				bos.flush();
				bos.close();
				bis2.close();
			}
			zip.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��Ҫ�����Ǹ������ݿ����ʹ�ds�л�ȡ���ݿ������  Ԭ���� 20130603
	 * Ʃ���dsΪnew��ȡ�����ݿ���û���  ���Ϊmysql���<dburl>jdbc:mysql://192.168.0.5:3000/gdnx_xu?characterEncoding=GBK</dburl>�л�ȡgdnx_xu
	 * @param dsName
	 * @return
	 */
	public String getDBUserFromDSName(DataSourceVO[] dsvss, String dsName, String dbType) {
		DataSourceVO[] dsvs = dsvss;
		DataSourceVO dsv = null;
		String ret_value = "";
		if (null != dbType && dbType.trim().toLowerCase().equals("mysql")) {
			for (int ii = 0; ii < dsvs.length; ii++) {
				if (dsvs[ii].getName().equals(dsName)) {
					dsv = dsvs[ii];
				}
			}
			String url_dest = dsv.getDburl();//Ŀ�����ݿ�URL
			String temp = url_dest.substring(0, url_dest.indexOf("?"));
			String temp1 = temp.substring(temp.lastIndexOf("/") + 1, temp.length());
			ret_value = temp1;
		}
		return ret_value;
	}

	//�滻�ı��е�html������
	public String replaceHtmlEncode(String inputString) {
		if (isEmpty(inputString)) {
			return inputString;
		}
		inputString = inputString.trim();
		inputString = inputString.replace("'", "&quot;");
		inputString = inputString.replace("<", "&lt;");
		inputString = inputString.replace(">", "&gt;");
		inputString = inputString.replace(" ", "&nbsp;");
		inputString = inputString.replace("\n", "<br>");
		return inputString;
	}

	/*
	 * �ƶ����Ƽ������š����/2016-08-16��
	 */
	public static String getLawOrRuleName(String _name) {
		return getFormatText(_name, "��", "��");
	}

	/*
	 * �ƶȱ�ż����š����/2016-08-16��
	 */
	public static String getLawOrRuleCode(String _code) {
		return getFormatText(_code, "(", ")");
	}

	public static String getFormatText(String _text, String _beginChar, String _endChar) {
		if (_text != null) {
			_text = _text.trim();
		}
		if (_text != null && _text.length() > 2 && _text.indexOf(_beginChar) == 0 && _text.lastIndexOf(_endChar) == (_text.length() - 1)) {
		} else {
			_text = _beginChar + _text + _endChar;
		}
		return _text;
	}
}
