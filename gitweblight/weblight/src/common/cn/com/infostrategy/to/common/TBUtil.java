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
 * 最常用的工具类,无论UI还是BS都经常用到! 像所能想到的各种字符,数组等功能,这里都有:
 * 像:输入流,反射,16进制,64位码,Swing组件生成图片,颜色,排序,异常堆栈,序列化,深度克隆,日期与字符转换,等技术这里也是一个学习的地方!
 * 
 * 字符串与数组的相关方法: boolean isEmpty(String _value) //判断字符串是否为空 findCount(String
 * _par, String _separator) //在一个字符串中寻找另一个字符串的个数 String[] split(String _par,
 * String _separator) //分割 String replaceAll(String str_par, String old_item,
 * String new_item) //替换 isExistInArray(String _item, String[] _array, boolean
 * _isIgnoreCase) //判断一个字符串是否存在于一个数组中,可以指定是否忽略大小写 isStartWithInArray(String
 * _item, String[] _array, boolean _isIgnoreCase) //是否经什么开始
 * isEndWithInArray(String _item, String[] _array, boolean _isIgnoreCase)
 * //是否以什么结束 getStrUnicodeLength(String s) //取字符串的宽度(中文算两位) getStrWidth(Font
 * _font, String _str) //算字符串以什么字体时的像素宽度 convertStrToHexString(String _str);
 * //转换成16进制 convertStrTo64Code(String _str); //转换64位码 String
 * getDoubleToString(double _ld) //将double转换成字符串 containTwoArrayCompare(String[]
 * _contain, String[] _content) //比如两个数组是否有相同的 getFormulaMacPars(_text, "{",
 * "}"); //分割字符串,以特定的开始与结束标记!在做报表时,替换其中的宏代码必须用到!
 * getInterSectionFromTwoArray(String[] _array1, String[] _array2) //取两个数组的交集
 * subStrByGBLength(String _str, int _limitLen, boolean _isAppendDot)
 * //取得字符串的前几位,中文算两位 convertStrToMapByExpress(String _str, String _split1,
 * String _split2, boolean _keyLowcase) //将字符串两次分割,变成一个map
 * 
 * 排序相关方法,拼SQL的方法 sortStrs(String[] _str) /排序字符串 sortHashVOs(HashVO[] _hvs,
 * String[][] _sortColumns) //排序HashVO String getInCondition(String[] _allvalue)
 * //生成SQL的in条件! String getNullCondition(String _nullId)
 * //将一个字符串如果是空值则进行转换成-99999,从而保证各种数据库的情况下都能执行! getHashMapFromHashVOs(HashVO[]
 * _hvs, String _keyField, String _valueField) //将HashVO[]中的两列转换一个Map
 * 
 * 系统,反射,输入/输出流相关方法 int getJVSite() //取得虚拟机是客户端还是服务器端 Object deepClone(Object
 * obj) //深度克隆一个对象 byte[] serialize(Object _obj) //序列化一个对象成字节数组 Object
 * deserialize(byte[] _bytes) //反序列化字节数组成一个对象 Object
 * refectCallClassStaticMethod(String _className, String _methodName, Object[]
 * _parObjs) //反射调用 Object reflectCallMethod(String _formula) //反射执行一个方法 byte[]
 * readFromInputStreamToBytes(InputStream _ins) //将一个输入流中的内容取得,返回byts[]
 * writeBytesToOutputStream(OutputStream _out, byte[] _bys) //将一个byte[]写到一个输出流中
 * 
 * 颜色,图片,时间 java.awt.Color getColor(String _html) //将html格式转换成颜色对象 String
 * convertColor(java.awt.Color _color) //将颜色转换成html的6位 byte[]
 * getCompentBytes(Component _component, int _width, int _height)
 * //将Swing控件转成字节码,将这个字节码写文件就是一个jpg文件 String getCompent64Code(Component
 * _component, int _width, int _height)
 * //将Swing控件生成64位码,将这个64位码写成mht文件或xml格式的word文件,就可以直接看! String getCurrDate()
 * //取得当前日期 String getCurrTime() //取得当前时间
 * 
 * 取参数 String getSysOptionStringValue(String _key, String _nvl) //取系统参数据 boolean
 * getSysOptionBooleanValue(String _key, boolean _nvl) //取系统布尔参数!
 * 
 * 转html String getHtmlHead() //取得html的头 String getHtmlByHashVOs(HashVO[] _hvs)
 * //将HashVO[] 生成html writeHashToHtmlTableFile(HashVO _hvs[], String _filename)
 * //将HashVO[]直接写成html文件 String getExceptionStringBuffer(Throwable _ex, boolean
 * _isHtmlFormat, boolean _isHtmlContainHead) //将异常的堆栈生成文本与html 
 * 
 */
public class TBUtil implements Serializable {

	private static final long serialVersionUID = 1906433042446054793L;

	public static int JVMSITE_SERVER = 0; // 服务器端JVM
	public static int JVMSITE_CLIENT = 1; // 客户端JVM

	private static TBUtil tbUtil = new TBUtil();
	private static HashMap constMap = new HashMap(); //经常有一些

	/**
	 * 默认构造方法
	 */
	public TBUtil() {
	}

	/**
	 * 替换字符串
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
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
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
	 * 根据字体算出某一个字符串的宽度!经常有需要计算啥时折行时需要!!
	 * 
	 * @param _font
	 * @param _str
	 * @return
	 */
	public int getStrWidth(Font _font, String _str) {
		return SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(_font), _str); //
		//return Toolkit.getDefaultToolkit().getFontMetrics(_font).stringWidth(_str); //
	}

	// 去掉\r\n的换行符,截取剩下的字符串
	public String getTrimSwapLineStr(String _str) {
		String str_return = replaceAll(_str, "\r", ""); //
		str_return = replaceAll(str_return, "\n", ""); //
		str_return = replaceAll(str_return, "<html>", ""); //
		str_return = replaceAll(str_return, "</html>", ""); //
		str_return = replaceAll(str_return, "<br>", ""); //
		str_return = replaceAll(str_return, "&nbsp;", ""); //
		return str_return; // 去掉\r\n
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
				al_temp.add(str_1); // 加入!!!
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
	 * 在配置公式时，处理字符串分割，有时在字符串中需要分隔符的情况，只需要在分隔符前加“\”即可!比如 “ABC\BEBFG”用“B”分割，则得到“A”、“CBE”、“FG”三个字符串
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
		_par = replaceAll(_par, "\\" + _separator, "$☆$");
		String[] str_pars = split(_par, _separator); //
		for (int i = 0; i < str_pars.length; i++) {
			str_pars[i] = replaceAll(str_pars[i].trim(), "$☆$", _separator);
		}
		return str_pars;
	}

	/**
	 * 将一个大字符分割,指定一行有几列,每一列的宽度!它非常适用将一个大文本分割以行数据的方式存储在一张表中!!
	 * 
	 * @param _str
	 * @param _oneRowCols
	 * @param _oneColWidth
	 * @return
	 */
	public ArrayList split(String _str, int _oneRowCols, int _oneColWidth) {
		// 压缩后将byte每4000个转换成一个16进制的字符串转换成字符串! 然后每50个拼成一条SQL! 最后插入到表中!!!
		int li_fileLength = _str.length(); // 宽度!
		int li_allColCount = li_fileLength / _oneColWidth; // 有多少列
		int li_left = li_fileLength % _oneColWidth; // 余数!
		if (li_left != 0) { // 如果有余数!
			li_allColCount = li_allColCount + 1; // 列数加1
		}

		int li_rows = li_allColCount / _oneRowCols; // 多少行!!
		if (li_allColCount % _oneRowCols != 0) {
			li_rows = li_rows + 1;
		}

		ArrayList al_data = new ArrayList(); //
		for (int i = 0; i < li_rows; i++) { // 遍历每一行!
			int li_beginCol = i * _oneRowCols; // 累加!
			int li_thisRowCols = 0; // 默认是50,即全的!
			if (i == li_rows - 1) { // 如果是最后一行,则结算该行的最后列!!
				li_thisRowCols = li_allColCount - li_beginCol; // 结尾列!!
			} else {
				li_thisRowCols = _oneRowCols; // 默认就是表的列数
			}
			String[] str_rowData = new String[li_thisRowCols]; // 这一行的数据!!!
			for (int j = 0; j < li_thisRowCols; j++) { // 遍历各列
				int li_substr_1 = (li_beginCol + j) * _oneColWidth; // 截取字符串的起始位置的绝对值!!!
				int li_substr_2 = 0;
				if (i == li_rows - 1 && j == li_thisRowCols - 1) { // 如果是最后一行的最后一列,则可能不满!!
					li_substr_2 = li_fileLength; // 则直接文件的结尾!
				} else {
					li_substr_2 = (li_beginCol + j + 1) * _oneColWidth; //
				}
				str_rowData[j] = _str.substring(li_substr_1, li_substr_2); //
			}
			al_data.add(str_rowData); // 加入这一行数据!!!
		}
		return al_data;
	}

	public String subStrByGBLength(String _str, int _limitLen) {
		return subStrByGBLength(_str, _limitLen, false);
	}

	/**
	 * 对一个字符串进行截取指定的宽度,而且是中文的计算方式! 即经常有这种需求,一个字符取前15位,但因为字里面有中文也有英文,中文算2位,英文算1位!
	 * 是按这样的算法计算的! 而不是直接的substring(0,15),因为如果是中文,则取出来是30位宽度! 数据库就可能存不下了!
	 * 上传文件,对大字符串写数据库前进行截取,都经常需要这个算法!!!
	 * 
	 * @param _str
	 * @param _limitLen
	 * @param _isAppendDot,
	 *            如果的确需要截取,则是否加省略号,好让其他人知道是否做了截取..
	 * @return
	 */
	public String subStrByGBLength(String _str, int _limitLen, boolean _isAppendDot) {
		if (_str == null || _str.equals("")) {
			return _str;
		}
		int li_strlength = _str.length(); //
		if (li_strlength * 2 <= _limitLen) { // 如果你的范围太大了,即使我都是中文,即每一位是是两位(length()*2),也没要求的范围大,则直接返回字符串,即根本不需要计算了!!!
			return _str; //
		}
		int li_substrPos = getSubStrPosByGBLength(_str, _limitLen); // 取得实际截取的位置!!
		if (_isAppendDot && li_substrPos != li_strlength && (li_strlength - 3) > 0) { // 如果需要加省略号,并且实际上需要进行截取,则补充
			return _str.substring(0, li_substrPos - 3) + "..."; // 再往前移3位然后加上三个点,表示省略号!!!
			// 其实更好的算法是还要判断最后三位的实际长度,如果是中文的话,则可能只要倒退1-2步!
			// 这个以后再优化!
		} else {
			return _str.substring(0, li_substrPos); // 如果的确是太长了,需要截取,即找到了截取的位置!!!则返回
		}
	}

	/**
	 * 取得一个字符串按照中文长度计算方法算出的在指定上限内的位置! 为了保证能插入数据库,即经常需要对一个字符串进行截取! 而且是中文长两位的截法!
	 * 该方法是结合上面的方法而用的,即实际是调用用上面方法更多!!!但考虑到也有可能需要该方法,所以还是分成了两个方法!!!
	 */
	public int getSubStrPosByGBLength(String _str, int _limitLen) {
		int li_strlength = _str.length(); //
		if (li_strlength * 2 <= _limitLen) { // 如果你的范围太大了,即使我都是中文,即每一位是是两位(length()*2),也没要求的范围大,则直接返回字符串,即根本不需要计算了!!!
			return li_strlength; //
		}
		int li_lenCount = 0; // 累数器!!!
		int li_substrPos = -1; // 真实需要截取的位置!
		for (int i = 0; i < li_strlength; i++) { // 如果是3位(abc),0[0-1],1[1-2],2[2-3]
			if (_str.charAt(i) >= 0x100) { // 如果是中文
				li_lenCount = li_lenCount + 2; //
			} else { // 如果是英文!
				li_lenCount = li_lenCount + 1; //
			}
			if (li_lenCount > _limitLen) { // 如果宽度总数大于上限,则退出!!
				li_substrPos = i; //
				break; //
			}
		}
		if (li_substrPos == -1) { // 如果没有超过上限,则直接整个长度!!!!
			li_substrPos = li_strlength; //
		}
		return li_substrPos;
	}

	/**
	 * 发现某个字特的个数
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
			li_count++; // 只要发现就累加1
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		return li_count; //

	}

	/**
	 * 判断一个字符串是否为空!!
	 * 
	 * @param _value
	 * @return
	 */
	public static boolean isEmpty(String _value) {
		if (_value == null || _value.trim().equals("")) { // 如果为null或者为空字符串...
			return true;
		} else {
			return false;
		}
	}

	// 判断一个字符串是否都是数字!! 即每一位都是0-9中的一个! 
	//原有判断日期也会判断为全数字的bug。郝明改为用正则表达式判断 2015-01-07
	public boolean isStrAllNunbers(String _str) {
		if (isEmpty(_str)) {
			return false;
		}
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		return _str.matches(regex);
	}

	// 判断一个字符串是否以某些类型的开始的,比如 N_,...默认区分大小写!!
	public boolean isStartWithInArray(String _item, String[] _array) {
		return isStartWithInArray(_item, _array, false); //
	}

	// 判断一个字符串是否以某些类型的开始的,比如 N_,...默认区分大小写!!
	public boolean isStartWithInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null) {
				if (_isIgnoreCase) { // 如果忽略大小写
					if (_item.toLowerCase().startsWith(_array[i].toLowerCase())) { // 则两个人都转成小写!!!
						return true;
					}
				} else { // 如果区分大小写
					if (_item.startsWith(_array[i])) {
						return true;
					}
				}
			}
		}
		return false; //
	}

	// 判断一个字符串是否以某些类型的结尾的,比如 .doc,.xls,.ppt等!!默认区分大小写!!
	public boolean isEndWithInArray(String _item, String[] _array) {
		return isEndWithInArray(_item, _array, false); //
	}

	// 判断一个字符串是否以某些类型的结尾的,比如 .doc,.xls,.ppt等!!
	public boolean isEndWithInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null) {
				if (_isIgnoreCase) { // 如果忽略大小写
					if (_item.toLowerCase().endsWith(_array[i].toLowerCase())) { // 则两个人都转成小写!!!
						return true;
					}
				} else { // 如果区分大小写
					if (_item.endsWith(_array[i])) {
						return true;
					}
				}
			}
		}
		return false; //
	}

	// 判断某一个字符串是否存在于一个一维数组中!
	public boolean isExistInArray(String _item, String[] _array) {
		return isExistInArray(_item, _array, false); //
	}

	// 判断某一个字符串是否存在于一个一维数组中!
	public boolean isExistInArray(String _item, String[] _array, boolean _isIgnoreCase) {
		if (_array == null || _array.length <= 0) {
			return false;
		}
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null && _array[i].equalsIgnoreCase(_item)) {
				if (_isIgnoreCase) { // 如果是忽略大小写!!!!
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
	 * 两个数组包含性比较,即检查数组1是否包含了数组2中的一项!如果数组1中的有一项与数组2中的一项对应上了,则返回true.
	 * 
	 * @param _contain
	 *            容器数组
	 * @param _content
	 *            内容数组
	 * @return
	 */
	public boolean containTwoArrayCompare(String[] _contain, String[] _content) {
		for (int i = 0; i < _contain.length; i++) {
			for (int j = 0; j < _content.length; j++) {
				if (_contain[i] != null && _contain[i].equals(_content[j])) { // 如果对上了,就立即返回
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 取得两个字符串中的交集!!!!
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
	 * 取得两个字符串中的并集!!!!
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
	 * 将一个字符串转换成16进制字符串的样子,即返回1A3D4E的样子!!16进制的好处是彻底去掉了中文问题的各种烦恼,但缺点是需要更长的位,浪费空间!!
	 * 其中一个汉字占两个字节,一个字节返回两个字母,即一个英文字母返回2位,一个汉字返回4个字母!! 比如:
	 * a=[61],b=[62],c=[63],中=[D6D0],国=[B9FA]
	 * 
	 * @param _str
	 *            原来的字符串!
	 * @return 16进制码
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
	 * 将一个字符串转换成64位编码!! 经常需要对一些二进制文件转成字符串存储! 64位码是最节约空间的!
	 * 但64位码有/,+等特殊符号!不适合创建文件名!!!所以那种情况是使用16进制码!
	 */
	public String convertStrTo64Code(String _str) {
		try {
			return convertBytesTo64Code(_str.getBytes("GBK")); //
		} catch (Exception e) {
			e.printStackTrace(); //
			return null; //
		}
	}

	// 将字节码转换成64位码!经常需要用到!比如将文件进行字符串方式存储时! 比如将文件上传进数据库中!!
	public String convertBytesTo64Code(byte[] _bytes) {
		return new sun.misc.BASE64Encoder().encode(_bytes); // 64位码
	}

	// 将64位码转换成实际字符串!
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

	// 将64位码转换成byte[],经常需要用到!
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
	 * 将一个二进制数组转换成16进制字符串
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
	 * 将一个16进制格式的字符串转换成原始的字符串格式!!!!
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
	 * 将16进制字符串转换成二进制数组
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
	 * 将短的字符串后面或前面加空格，变成不小于某长度的字符串
	 * 
	 * @param _oldstr
	 *            原字符串
	 * @param _length
	 *            最短长度
	 * @param _prefix
	 *            原字符串是否在前
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
	 * 将一种特定格式的语法转换成一个哈希表,字符串的格式是【12=张三;15=李四;17=王五;】,即先以分号相隔,然后是一对对的以"="相隔的关键值,等号左边的是key,右边的是value！
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
		LinkedHashMap map = new LinkedHashMap(); //有顺序!
		if (_str == null || _str.trim().equals("")) {
			return map;
		}
		String[] str_items = split(_str, _split1); //
		for (int i = 0; i < str_items.length; i++) {
			String[] str_items2 = split(str_items[i], _split2); //
			if (str_items2.length >= 2) {
				if (_keyLowcase) { // 如果要转小写
					map.put(str_items2[0].toLowerCase(), str_items2[1]); // //送入哈希表!!!!
				} else {
					map.put(str_items2[0], str_items2[1]); // //送入哈希表!!!!
				}
			}
		}
		return map;
	}

	// 将一个字符串进行唯一性过滤
	public String[] distinctStrArray(String[] _str) {
		return distinctStrArray(_str, true); //
	}

	// 将一个字符串进行唯一性合并后返回
	public String[] distinctStrArray(String[] _str, boolean _isTrim) {
		if (_str == null || _str.length <= 0) {
			return _str;
		}
		LinkedHashSet hst_str = new LinkedHashSet(); //
		for (int i = 0; i < _str.length; i++) {
			if (_isTrim) { // 如果是忽略空格,并且自动去头尾的空格
				if (_str[i] != null && !_str[i].trim().equals("")) { // 如果不能是空值或空串!!!
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

	// 将一个字符串解析后生成一个哈希表,在公式参数中经常有这种需求,即参数格式是 par1=Y;par2=aaa;par3=bbb;par4=1,2,3
	// 即这个字符串必须先是以分号分隔,然后每一部分又是以等号相隔!!!!
	public HashMap parseStrAsMap(String _str) {
		HashMap returnMap = new HashMap(); // 先创建哈希表
		String[] str_items = split(_str, ";"); // 先是以分号分隔
		if (str_items == null || str_items.length <= 0) {
			return returnMap;
		}
		String str_itemkey = null;
		String str_itemvalue = null;
		for (int i = 0; i < str_items.length; i++) {
			if (str_items[i] != null && str_items[i].indexOf("=") > 0) { // 如果有等于号
				str_itemkey = str_items[i].substring(0, str_items[i].indexOf("=")); // 等号前的
				str_itemvalue = str_items[i].substring(str_items[i].indexOf("=") + 1, str_items[i].length()); // 等号后的
				returnMap.put(str_itemkey, str_itemvalue); // 送入哈希表
			}
		}
		return returnMap;
	}

	/**
	 * 对一个字符串数组进行排序,因为默认的Arrays.sort(String[])在处理中文时不对,故提出该方法
	 * 其他对数字数组排序时，仍可以用Arrays.sort(double[])
	 * 
	 * @param _str
	 */
	public void sortStrs(String[] _str) {
		Arrays.sort(_str, new StrComparator()); //
	}

	/**
	 * 使用一个指定的顺序对一个字符串排序,比如指定一个顺序数组{"高中","中专","大专","本科","硕士","博士"}
	 * 然后对实际值按该顺序输出，即实际值可能多于或少于该数组的个数，但只要出现了属于该数组中的值，则相对顺序必然是按该顺序输出!!
	 * 
	 * @param _str
	 *            实际值,
	 * @param _orders
	 *            指定的顺序,如果是零汇报机制,可能造成的情况是_str会增长
	 */
	public void sortStrsByOrders(String[] _str, String[] _orders) {
		Arrays.sort(_str, new StrComparator(_orders)); //

	}

	/**
	 * 对一个HashVO进行排序,使用方法是 sortHashVOs(hvs,new
	 * String[][]{{"code","N","N"},{"age","Y","Y"}});
	 * 
	 * @param _hvs,
	 *            就是想要排序的哈希数组..
	 * @param
	 *            _sortColumns,指定排序列,n行3列的数据,第一个布尔值表示是否是倒序(Y=倒序,N=升序),第二个布尔值表示是否是数字型(Y=数字,N=字符),比如：
	 *            new String[][]{{"code","N","N"},{"age","Y","Y"}}
	 *            //先对工号按升序排(字符型),如果相同再按年龄倒序排(数字型)
	 */
	public void sortHashVOs(HashVO[] _hvs, String[][] _sortColumns) {
		Arrays.sort(_hvs, new HashVOComparator(_sortColumns));
	}

	//
	public void sortHashVOs(HashVO[] _hvs, String[][] _sortColumns, String itemKey, String[] orders) {
		Arrays.sort(_hvs, new HashVOComparator(_sortColumns, itemKey, orders));
	}

	/**
	 * 对一个HashVO进行排序,使用方法是 sortHashVOs(hvs,new
	 * String[][]{{"code","N","N"},{"age","Y","Y"}});
	 * 
	 * @param _hvs,
	 *            就是想要排序的哈希数组..
	 * @param
	 *            _sortColumns,指定排序列,n行3列的数据,第一个布尔值表示是否是倒序(Y=倒序,N=升序),第二个布尔值表示是否是数字型(Y=数字,N=字符),比如：
	 *            new String[][]{{"code","N","N"},{"age","Y","Y"}}
	 *            //先对工号按升序排(字符型),如果相同再按年龄倒序排(数字型)
	 */
	public void sortTableDataStruct(TableDataStruct _tds, String[][] _sortColumns) {
		Arrays.sort(_tds.getBodyData(), new TableDataStructComparator(_tds.getHeaderName(), _sortColumns));
	}

	/**
	 * 删除HashVO中的列
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
	 * 删除HashVO中的列
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
			if (_hvs[i].containsKey(_fromItemKey)) { // 如果包含列名
				String str_oldValue = _hvs[i].getStringValue(_fromItemKey); //
				if (_oldValue.equals(str_oldValue)) { // 如果品配上
					_hvs[i].setAttributeValue(_fromItemKey, _newValue); //
				} else {
					_hvs[i].setAttributeValue(_fromItemKey, _others); //
				}
			}
		}
	}

	/**
	 * 替换HashVO[]中的值..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _replaceValue,是n行,2列的字符串数组.
	 *            比如 new String[][]{{"Y","民报送"},{"N","未报送"}}
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String[][] _replaceValue) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].containsKey(_fromItemKey)) { // 如果包含列名
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
	 * 取得一个字符串中以{}包括的所有宏代码参数集,比如{aa} and {bb} 就返回[aa][bb]
	 * 
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String[] getFormulaMacPars(String _sql) throws Exception {
		return getFormulaMacPars(_sql, "{", "}"); //
	}

	/**
	 * 从一个字符串找到以前后辍品配的一段宏代码,即宏代码参数
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
			int li_pos_2 = str_leftsubfix.indexOf(_subfix); // 必须有后辍!!!必须有一对!
			if (li_pos_2 < 0) {//{}也是允许的。把<=0改为<0。否则报错！[郝明2012-04-18]
				throw new Exception("公式[" + _inittext + "]语句不合法,某个前辍没有对应的后辍!!!!!");
			}
			al_temp.add(str_leftsubfix.substring(0, li_pos_2)); // 截取!
			str_remain = str_leftsubfix.substring(li_pos_2 + _subfix.length(), str_leftsubfix.length()); //
			li_pos_1 = str_remain.indexOf(_prefix); //
		}
		return (String[]) al_temp.toArray(new String[0]); //
	}

	// 取宏代码!
	public String[] getMacroList(String _inittext) {
		return getMacroList(_inittext, "${", "}"); //
	}

	/**
	 * 宏返回一个字特串中包括${dsdfsd}的符代码串列表
	 * 
	 * @return 字符串,将其中${dssss}专门挖出来形成一行! 如果是"abcd${123}dfwer${98432}kpsdf";
	 *         返回abcd/${123}/dfwer/${98432}/kpsdf
	 */
	public String[] getMacroList(String _inittext, String _str1, String _str2) {
		ArrayList al_text = new ArrayList(); // //
		int li_type = 1; //
		String str_remain = _inittext; //
		for (;;) { // 死循环遍历!!
			if (li_type == 1) {
				int li_pos = str_remain.indexOf(_str1); // 如果找到
				if (li_pos >= 0) {
					if (li_pos > 0) {
						al_text.add(str_remain.substring(0, li_pos)); // 加入
					}
					str_remain = str_remain.substring(li_pos, str_remain.length()); // 剩下的
					li_type = 2; // 指明下一循环是找}
				} else {
					break;
				}
			} else if (li_type == 2) {
				int li_pos = str_remain.indexOf(_str2); // 如果找到
				if (li_pos >= 0) {
					al_text.add(str_remain.substring(0, li_pos + 1)); // 加入
					str_remain = str_remain.substring(li_pos + 1, str_remain.length()); //
					li_type = 1; // 指明下一循环找头
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
	 * 转换公式中宏代码的内容,为客户端缓存或页面上的值!!!
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
			String str_key = str_allkeys[i]; // key名!!!!

			String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // 优先从客户端缓存取数!!
			if (str_clientenvvalue != null) { // 如果从客户端缓存取到数了!!
				str_newformula = replaceAll(str_newformula, "{" + str_key + "}", str_clientenvvalue); // 替换
			} else // 从页面控件取
			{
				int li_pos = str_key.indexOf("."); // 看有没有"点操作"??
				String str_itemvalue = "";
				if (li_pos <= 0) { // 如果没有点操作!!则直接取
					Object obj = _map.get(str_key);
					if (obj != null) {
						if (obj instanceof String) {
							str_itemvalue = "" + obj; //
						} else if (obj instanceof ComBoxItemVO) { // 如果是下拉框
							str_itemvalue = ((ComBoxItemVO) obj).getId(); //
						} else if (obj instanceof RefItemVO) { // 如果是参照!!
							str_itemvalue = ((RefItemVO) obj).getId(); //
						} else {
							str_itemvalue = "" + obj;
						}
					} else {

					}
				} else // 如果有点操作!!
				{
					String str_key_prefix = str_key.substring(0, li_pos); // 前辍
					String str_key_subfix = str_key.substring(li_pos + 1, str_key.length()); // 后辍

					Object obj = _map.get(str_key_prefix); //
					if (obj != null) {
						if (obj instanceof String) {
							str_itemvalue = "" + obj; //
						} else if (obj instanceof ComBoxItemVO) { // 如果是下拉框
							str_itemvalue = ((ComBoxItemVO) obj).getItemValue(str_key_subfix); //
						} else if (obj instanceof RefItemVO) { // 如果是参照!!
							str_itemvalue = ((RefItemVO) obj).getItemValue(str_key_subfix); //
						} else {
							str_itemvalue = "" + obj;
						}
					} else {
					}
				}

				str_newformula = replaceAll(str_newformula, "{" + str_key + "}", str_itemvalue); // 用页面控件的值替换!!!
			}
		} // end for 循环
		return str_newformula; //
	}

	/**
	 * 根据一个数组,拼成一个select a,b,c from table中的那些列的字符串!!
	 * 
	 * @param _allcols
	 * @return
	 */
	public String getSelectCols(String[] _allcols) {
		String str_return = " "; // 一开始有个空格
		for (int i = 0; i < _allcols.length; i++) {
			if (i == _allcols.length - 1) {
				str_return = str_return + _allcols[i]; //
			} else {
				str_return = str_return + _allcols[i] + ","; //
			}
		}

		str_return = str_return + " "; // 最后再加个空格
		return str_return;
	}

	/**
	 * 取得In条件
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
	 * 将一个数组转换成in ('a','b')的样子!
	 * 
	 * @param _allcols
	 * @return
	 */
	public String getInCondition(String[] _allvalue) {
		if (_allvalue == null || _allvalue.length == 0) {
			return "'-99999'"; // 仿照PowerBuild的异常编码,即-5个9
		} else {
			java.util.List list = Arrays.asList(_allvalue); // 转换成一个List
			return getInCondition(list); //
		}
	}

	/**
	 * 拼接Like or条件!
	 * 
	 * @param _colKey
	 * @param _condition
	 * @return
	 */
	public String getLikeOrCondition(String _colName, String[] _condition) {
		if (_condition == null || _condition.length <= 0) {
			return "3=4"; // 如果为空,则直接返回一个肯定匹配不上的!
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append(" ("); // 左括号
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
	 * 经常有空值来拼SQL,比如where id='',或 where id='null',很不方便!
	 * 尤其是SQLserver,如果字段类型是Number的,如果SQL是 where id='' 甚至还会报错! Oracle就不会,着实麻烦!!
	 * 为了兼容所有数据库,需要对这种空值进行转换! 即如果是空值则转换统一转成'-99999',如果有值则拼上实际值!
	 * 这样就能兼容所有数据库,也不会报错! 而且看到-99999就知道其实是空值! "where id='" +
	 * tbUtil.getNullCondition(prinstanceId) + "'"
	 * 
	 * @param _nullId
	 * @return
	 */
	public String getNullCondition(String _nullId) {
		if (_nullId == null || _nullId.trim().equals("") || _nullId.trim().equalsIgnoreCase("null")) { // 如果是空值!
			return "-99999"; // 仿照PowerBuild的异常编码,即-5个9,这样无论是数字还是字符型的,SQL都不会报错!
			// 而且由于不存有-99999的记录,业务逻辑上也是对的! 关键是所有数据库都通用!!!
		}
		return _nullId; // 如果不为空,则返回原来的值!!
	}

	/**
	 * 将一个数组转换成in ('a','b')的样子!
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
				str_ids = distinctStrArray(str_ids); // 唯一性过滤
				if (str_ids == null || str_ids.length <= 0) { //唯一性过滤后,该数组宽度完全可能是0,比如原来的参数就是一个String[]{""},兴业项目中后来就发生 【where id in ()】的情况,所以必须要有这个判断!!【xch/2012-04-28】
					return "'-99999'"; //
				}
				if (str_ids.length <= 999) { // 如果在999个以内,则直接拼起来返回!!!!!!即不会太长了,这样兼容直接in的情况!!!
					StringBuffer sb_alltext = new StringBuffer(); //
					for (int i = 0; i < str_ids.length; i++) { // //
						sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); //必须将null转化成"",否则会出现where id in ('12','null','15')，会报错
						if (i != str_ids.length - 1) { // 如果不是最后一个则加上逗号!!!!!
							sb_alltext.append(","); // 如果不是最后一个,则加逗号!!!!!
						}
					}
					return sb_alltext.toString(); //
				} else { // 如果超过1000个则使用插入数据库的技术,因为Oracle在超过1000个in条件时会报错!!!但Mysql却是可以的!!
					int li_jvm = getJVSite();
					if (li_jvm == TBUtil.JVMSITE_CLIENT) { // 如果当前是客户端
						return UIUtil.getSubSQLFromTempSQLTableByIDs(str_ids); //
					} else { // 如果当前是服务器端,则直接New,在JDK1.5中,其ClassLoader会导致客户商下载CommDMO类,即jdk1.5是解析Class时就加载类,但JDK1.6就变成了按需加载,即只有调用到这行代码才会加载CommDMO类,所以可以这样写,而不担心客户羰会下载CommDMO.class了!!
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

	// 有时为了提高性能,使用Hashmap快速查找,需要将一个HashVO[]的某两列转换成一个HasMap
	public HashMap getHashMapFromHashVOs(HashVO[] _hvs, String _keyField, String _valueField) {
		// long ll_1 = System.currentTimeMillis(); //
		HashMap map = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) { // 遍历各行数据
			if (_valueField.equalsIgnoreCase("$rownum")) { // 如果是特殊符号,表示行号
				map.put(_hvs[i].getStringValue(_keyField), new Integer(i)); // //
			} else {
				map.put(_hvs[i].getStringValue(_keyField), _hvs[i].getStringValue(_valueField)); // //
			}
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("将[" + _hvs.length + "]条数据转换成HashMap耗时[" + (ll_2 -
		// ll_1) + "]");
		// //一般来说在我笔记本上将3000多个机构转换一下耗时16毫秒,在生产环境的好机器上可能只需5毫秒,所以不是性能瓶颈!!
		return map;
	}

	/**
	 * 与上面方法一样,只不过是有时需要通过一次循环一次输出多个HashMap,比如id<=>name,id<=>parentid,id<=>type等
	 * 如果都是一次次调上面的方法,则每次要循环一大遍,性能不高,而是最好成在一次循环中搞定!!
	 * 
	 * @param _hvs
	 * @param _keyValueFields,是N行2列的参数,第一列是key名,第二列是value名,行表示有多组参数!!
	 * @return
	 */
	public HashMap[] getHashMapsFromHashVOs(HashVO[] _hvs, String[][] _keyValueFields) {
		// long ll_1 = System.currentTimeMillis(); //
		HashMap[] maps = new HashMap[_keyValueFields.length]; //
		for (int j = 0; j < maps.length; j++) { // //
			maps[j] = new HashMap(); //
		}
		for (int i = 0; i < _hvs.length; i++) { // 遍历各行数据
			for (int j = 0; j < maps.length; j++) { // 遍历各个Hash表
				if (_keyValueFields[j][1].equalsIgnoreCase("$rownum")) { // 如果是特殊符号,表示行号
					maps[j].put(_hvs[i].getStringValue(_keyValueFields[j][0]), new Integer(i)); //
				} else {
					maps[j].put(_hvs[i].getStringValue(_keyValueFields[j][0]), _hvs[i].getStringValue(_keyValueFields[j][1])); //
				}
			}
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("将[" + _hvs.length + "]条数据转换成HashMap数组耗时[" + (ll_2
		// - ll_1) + "]");
		return maps; //
	}

	/**
	 * 取得一个字符串的unicode的长度!!
	 * 
	 * @param s
	 * @return
	 */
	public int getStrUnicodeLength(String s) { // 这个方法是指送入一个字符串,算出其字节长度,如果字符串中有个中文字符,那么他的长度就算2
		int j = 0;
		if (s == null || s.length() == 0) {
			return 0;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 0x100) { // 如果是中文
				j = j + 2;
			} else {
				j = j + 1;
			}
		}
		return j;
	}

	/**
	 * 将一个double转换成string,否则会出现科学计算法!! 这个方法比较傻,以后再优化..
	 * 
	 * @param _ld
	 * @return
	 */
	public String getDoubleToString(double _ld) {
		boolean isscale = false; // 是否精确到小数位..

		BigDecimal bigd = new BigDecimal(_ld); //
		bigd.setScale(2, BigDecimal.ROUND_HALF_UP);
		String str_tostr = bigd.toString(); //
		int li_pos = str_tostr.indexOf("."); //

		if (isscale) { // 如果精确到小数位
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
	 * 深度克隆某一个对象!!
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
	 * 序列化一个对象..
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
	 * 反向序列化成一个对象
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
	 * 压缩一个二进制流
	 * 
	 * @param _initbytes
	 * @return
	 */
	public byte[] compressBytes(byte[] _initbytes) {
		try {
			java.util.zip.Deflater compressor = new java.util.zip.Deflater();
			compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //
			compressor.setInput(_initbytes); //
			compressor.finish(); // 真正进行压缩...
			ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); // //
			byte[] buf = new byte[2048]; //
			while (!compressor.finished()) {
				int count = compressor.deflate(buf); // 将数据压缩到buf中,一般来说,除了最后一次外都会返回1024,即用满了!!!!!
				bos.write(buf, 0, count);
			}
			bos.close();
			byte[] compressedData = bos.toByteArray(); // 压缩后的大小
			return compressedData; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * 解压一个二进制数组!!!
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
	 * 取得
	 * 
	 * @return
	 */
	public int getJVSite() {
		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) { // 客户端,服务器端是取不到的,因为服务器端可能因为权限问题不能设置系统属性,但客户端是可以的
			return JVMSITE_CLIENT;
		} else {
			return JVMSITE_SERVER; //
		}
	}

	public String[][] getAllOptions() {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
			try {
				if (ClientEnvironment.getClientSysOptions() == null) { // 如果客户端缓存为空,则第一次初始化一下
					ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions());
				}
				return ClientEnvironment.getClientSysOptions();
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getAllOptions(); //
		}
		return null;
	}

	public String getSysOptionStringValue(String _key, String _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
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
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getStringValue(_key, _nvl); //
		}
	}

	public int getSysOptionIntegerValue(String _key, int _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
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
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getIntegerValue(_key, _nvl); //
		}
	}

	public boolean getSysOptionBooleanValue(String _key, boolean _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						if (str_alloptions[i][1] != null && (str_alloptions[i][1].equalsIgnoreCase("Y") || str_alloptions[i][1].equalsIgnoreCase("true") || str_alloptions[i][1].equalsIgnoreCase("是"))) { //
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
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue(_key, _nvl); //
		}
	}

	public String getSysOptionHashItemStringValue(String _key, String _itemKey, String _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // 取得字符串中的对应的值
						if (str_itemValue == null) {
							return _nvl;
						} else {
							return str_itemValue;
						}
					}
				}
				return _nvl; // 从str_alloptions中取
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemStringValue(_key, _itemKey, _nvl); //
		}
	}

	public int getSysOptionHashItemIntegerValue(String _key, String _itemKey, int _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // 取得字符串中的对应的值
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
				return _nvl; // 从str_alloptions中取
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemIntegerValue(_key, _itemKey, _nvl); //
		}
	}

	public boolean getSysOptionHashItemBooleanValue(String _key, String _itemKey, boolean _nvl) {
		if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端..
			try {
				String[][] str_alloptions = getAllOptions(); //
				for (int i = 0; i < str_alloptions.length; i++) {
					if (_key.equals(str_alloptions[i][0])) {
						String str_strvalue = str_alloptions[i][1]; //
						if (str_strvalue == null) {
							return _nvl;
						}
						String str_itemValue = getItemValueByItemKey(str_strvalue, _itemKey); // 取得字符串中的对应的值
						if (str_itemValue == null) {
							return _nvl;
						}
						if (str_itemValue.equalsIgnoreCase("Y") || str_itemValue.equalsIgnoreCase("true") || str_itemValue.equalsIgnoreCase("是")) { //
							return true;
						} else {
							return false;
						}
					}
				}
				return _nvl; // 从str_alloptions中取
			} catch (Exception e) {
				e.printStackTrace(); //
				return _nvl;
			}
		} else { // 如果在服务器端,则必须使用反射调用,否则会下载服务器端的代码至客户端!!
			return cn.com.infostrategy.bs.common.SystemOptions.getHashItemBooleanValue(_key, _itemKey, _nvl); //
		}
	}

	private String getItemValueByItemKey(String _value, String _itemKey) {
		String[] str_items = split(_value, ";"); // 以分号分隔
		for (int i = 0; i < str_items.length; i++) {
			int li_pos = str_items[i].indexOf("="); //
			if (li_pos > 0) { // 如果有等于号
				String str_item_key = str_items[i].substring(0, li_pos); // key名
				if (str_item_key.equals(_itemKey)) {
					return str_items[i].substring(li_pos + 1, str_items[i].length()); // value值
				}
			}
		}
		return null;
	}

	// 反射调用某个类的某个静态方法
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
						parCls[i] = _parObjs[i].getClass(); // 用该对象的类
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
	 * 取得当前Session
	 * 
	 * @return
	 */
	public CurrSessionVO getCurrSession() {
		if (getJVSite() == JVMSITE_SERVER) { // 如果是server端
			try {
				return new cn.com.infostrategy.bs.common.WLTInitContext().getCurrSession(); //
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端
			return ClientEnvironment.getInstance().getCurrSessionVO(); //
		} else {
			return null;
		}
	}

	public String getDefaultDataSourceName() {
		if (getJVSite() == JVMSITE_SERVER) { // 如果是server端
			return cn.com.infostrategy.bs.common.ServerEnvironment.getDefaultDataSourceName();
		} else if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); //
		} else {
			return null;
		}
	}

	public String getDefaultDataSourceType() {
		if (getJVSite() == JVMSITE_SERVER) { // 如果是server端
			return cn.com.infostrategy.bs.common.ServerEnvironment.getDefaultDataSourceType();
		} else if (getJVSite() == JVMSITE_CLIENT) { // 如果是客户端
			return ClientEnvironment.getInstance().getDefaultDataSourceType(); //
		} else {
			return null;
		}
	}

	/**
	 * 找出某一个表所有字段的SQL
	 * 
	 * @param _tableName
	 * @return
	 */
	public String getSQL_findAllCols(String _tableName) {
		if (getDefaultDataSourceType().equals(WLTConstants.ORACLE)) {
			if (_tableName == null) {
				return "select cname as 列名,coltype 列类型,cname　显示列名 from col ";
			} else {
				return "select cname as 列名,coltype 列类型,cname　显示列名 from col where tname='" + _tableName + "'";
			}
		} else if (getDefaultDataSourceType().equals(WLTConstants.SQLSERVER)) {
			if (_tableName == null) {
				return "select t1.name 列名,t2.name + '(' + CAST(t1.length AS varchar) + ')' 列类型,t1.name 显示列名 from syscolumns t1,systypes t2 where t1.xtype=t2.xtype";
			} else {
				return "select t1.name 列名,t2.name + '(' + CAST(t1.length AS varchar) + ')' 列类型,t1.name 显示列名 from syscolumns t1,systypes t2 where t1.xtype=t2.xtype and t1.id=(select id from sysobjects where name='" + _tableName + "')";
			}
		} else {
			return null;
		}
	}

	/**
	 * 找到所有的表!!
	 * 
	 * @return
	 */
	public String getSQL_findAllTables() {
		if (getDefaultDataSourceType().equals(WLTConstants.ORACLE)) {
			return "Select t1.tname tname,nvl(t2.comments, t1.tname) tdesc, t1.tabtype 类型  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE') and 1 = 1 order by tname";
		} else {
			return "select name tname,name tdesc,crdate createdate from sysobjects where xtype='U' and name<>'dtproperties' order by name"; //
		}
	}

	private void getBracketID(String _sql, Vector _vec) throws Exception {
		String temp_sql = _sql;
		if (temp_sql.indexOf("{") < 0) {
			if (temp_sql.indexOf("}") >= 0) {
				throw new Exception("语句[" + _sql + "]中的大括号\"{}\"没有成对品配!!");
			}
			return;
		}
		String mid_sql = temp_sql.substring(temp_sql.indexOf("{"), temp_sql.length());

		int end_index = -1;
		if (mid_sql.indexOf("}") > 0) {
			end_index = mid_sql.indexOf("}");
		}
		if (end_index == -1) {
			throw new Exception("语句[" + _sql + " ]中的大括号\"{}\"没有成对品配!!");
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
	 *            比如: getColValue("n1_menu","{menutype}","{menuid}");
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
			throw new Exception("SQL语句错误，{}不匹配！");
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
				throw new Exception("SQL语句错误，{}不匹配！");
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
			throw new Exception("SQL语句错误，{}不匹配！");
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
	 * 从客户端环境或页面中找出某个key的值
	 * 
	 * @param _key
	 * @param _otherkey
	 * @param _env
	 * @param _map
	 * @return
	 */
	private String getParameter(String _key, String _otherkey, CurrSessionVO _currSessionVO, HashMap _map) {
		String _bracket_parameter = null;
		Object obj = _currSessionVO.getCustMap().get(_key); // 从客户端环境变量取
		if (obj == null) {
			Object _obj = _map.get(_key); // 再从页面数据取
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
						_bracket_parameter = ((ComBoxItemVO) _obj).getItemValue(_otherkey); // 取下拉框VO的其他字段!!!
					}
				} else if (_obj instanceof RefItemVO) {
					if (_otherkey == null) {
						_bracket_parameter = ((RefItemVO) _obj).getId(); //
					} else {
						_bracket_parameter = ((RefItemVO) _obj).getItemValue(_otherkey); // 取参照VO的其他字段!!!
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
	 * 处理内嵌的{}
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
			throw new Exception("SQL语句错误，{}不匹配！");
		}

		int begin_index = str_mid.indexOf("{");

		if (begin_index >= 0 && begin_index < end_index) {
			convert(str_mid, begin_index, end_index, _currSessionVO, _map);
		}

		String bracket_id = str_mid.substring(0, end_index); // 这是{}中间的内容
		int li_pos = bracket_id.indexOf("."); //
		String str_itemkey = null;
		String str_itemkey_otherfield = null;
		if (li_pos > 0) { // 如果发现有个"."
			str_itemkey = bracket_id.substring(0, li_pos);
			str_itemkey_otherfield = bracket_id.substring(li_pos + 1, bracket_id.length()); // 处理内嵌"{}"
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
	 * 转换某个参照SQL的{}为客户端缓存或页面上的值!!!
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
			String str_key = (String) v_allkeys.get(i); // key名
			String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // 从客户端缓存取数!!
			if (str_clientenvvalue != null) { // 如果从客户端缓存取到数了!!
				str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_clientenvvalue); // 替换
			} else // 如果从客户端环境变量没取到!!
			{
				str_newsql = replaceAll(str_newsql, "{" + str_key + "}", ""); // 替换
			}
		} // end for 循环
		return str_newsql; //
	}

	/**
	 * 转换某个参照SQL的{}为客户端缓存或页面上的值!!!
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
			Vector v_allkeys = findItemKey(str_newsql); // 找出一个字符串中所有{}的子项!!
			for (int i = 0; i < v_allkeys.size(); i++) {
				String str_key = (String) v_allkeys.get(i); // key名
				String str_clientenvvalue = (String) _currSessionVO.getCustMap().get(str_key); // 从客户端缓存取数!!
				if (str_clientenvvalue != null) { // 如果从客户端缓存取到数了!!
					str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_clientenvvalue); // 先替换一下
				} else // 从页面控件取
				{
					if (_map != null && _map.size() > 1) { // 如果有值
						int li_pos = str_key.indexOf("."); // 看有没有"点操作"??
						String str_itemvalue = "";
						if (li_pos <= 0) { // 如果没有点操作!!则直接取
							Object obj = _map.get(str_key);
							if (obj != null) {
								if (obj instanceof String) {
									str_itemvalue = "" + obj; //
								} else if (obj instanceof ComBoxItemVO) { // 如果是下拉框
									str_itemvalue = ((ComBoxItemVO) obj).getId(); //
								} else if (obj instanceof RefItemVO) { // 如果是参照!!
									str_itemvalue = ((RefItemVO) obj).getId(); //
								} else {
									str_itemvalue = "" + obj;
								}
							} else {

							}
						} else // 如果有点操作!!
						{
							String str_key_prefix = str_key.substring(0, li_pos); // 前辍
							String str_key_subfix = str_key.substring(li_pos + 1, str_key.length()); // 后辍

							Object obj = _map.get(str_key_prefix); //
							if (obj != null) {
								if (obj instanceof String) {
									str_itemvalue = "" + obj; //
								} else if (obj instanceof ComBoxItemVO) { // 如果是下拉框
									str_itemvalue = ((ComBoxItemVO) obj).getItemValue(str_key_subfix); //
								} else if (obj instanceof RefItemVO) { // 如果是参照!!
									str_itemvalue = ((RefItemVO) obj).getItemValue(str_key_subfix); //
								} else {
									str_itemvalue = "" + obj;
								}
							} else {
							}
						}

						str_newsql = replaceAll(str_newsql, "{" + str_key + "}", str_itemvalue); // 用页面控件的值替换!!!
					}
				}
			} // end for 循环

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

		str_sql = replaceStrWithSessionOrHashData(str_split[0], _currSessionVO, _map); // 第一个肯定要有!!
		if (str_split.length == 2) { // 如果没有定义数据源
			str_dsname = replaceStrWithSessionOrHashData(str_split[1], _currSessionVO, _map); // 转换数据源!!
		} else { // 如果定义数据源
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

		str_sql = replaceStrWithSessionOrHashData(str_split[0], _currSessionVO, _map); // 第一个肯定要有!!
		str_pkfieldname = split(str_split[1], "=")[1].trim();
		str_parentPkFieldName = split(str_split[1], "=")[0].trim();
		if (str_split.length == 3) { // 如果没有定义数据源
			str_dsname = replaceStrWithSessionOrHashData(str_split[2], _currSessionVO, _map); // 转换数据源!!
		} else { // 如果定义数据源
			str_dsname = getDefaultDataSourceName();
		}
		return new String[] { str_sql, str_pkfieldname, str_parentPkFieldName, str_dsname }; //
	}

	/**
	 * 根据参照原始定义取出参照的类型与SQL
	 * 
	 * @param _refdesc
	 * @return
	 */
	public String[] getRefDescTypeAndSQL(String _refdesc) {
		if (_refdesc == null) {
			return null;
		}

		String str_type = null; // 类型
		String str_realsql = null; // 真正的SQL
		String str_parentfieldname = null; // 树型参照的父亲字段
		String str_pkfieldname = null; // 树型参照的主键
		String str_datasourcename = null; // 数据源名称

		int li_pos = _refdesc.indexOf(":"); //
		if (li_pos < 0) {
			str_type = "TABLE";
		} else {
			str_type = _refdesc.substring(0, li_pos).toUpperCase(); //
		}

		if (str_type.equalsIgnoreCase("TABLE") || str_type.equalsIgnoreCase("TABLE2") || str_type.equalsIgnoreCase("TABLE3")) {
			if (li_pos < 0) {
				String str_remain = _refdesc.trim(); // 剩下的
				String[] str_arrays = split(str_remain, ";"); //
				str_realsql = str_arrays[0];
				if (str_arrays.length == 1) {
					str_datasourcename = getDefaultDataSourceName(); // 默认数据源!!!
				} else if (str_arrays.length == 2) {
					str_datasourcename = getDataSourceName(str_arrays[1]); //
				}

			} else {
				String str_remains = _refdesc.substring(li_pos + 1, _refdesc.length()).trim();
				str_realsql = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); // 表型
			}
		} else if (str_type.equalsIgnoreCase("TREE") || str_type.equalsIgnoreCase("TREE2") || str_type.equalsIgnoreCase("TREE3")) { // 树型..
			try {
				String str_remain = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); //
				String[] str_arrays = split(str_remain, ";"); //
				str_realsql = str_arrays[0].trim();
				String[] str_treeFieldNames = getTreeFielNames(str_arrays[1].trim()); //
				str_parentfieldname = str_treeFieldNames[0];
				str_pkfieldname = str_treeFieldNames[1];
				if (str_arrays.length == 2) {
					str_datasourcename = getDefaultDataSourceName(); // 默认数据源!!!
				} else if (str_arrays.length == 3) {
					str_datasourcename = getDataSourceName(str_arrays[2]); //
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (str_type.equalsIgnoreCase("CUST")) {
			str_realsql = _refdesc.substring(li_pos + 1, _refdesc.length()).trim(); // 表型
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

		return str_new; // 取得数据源名称
	}

	private String[] getTreeFielNames(String _des) {
		int li_pos = _des.indexOf("="); //
		String str_parentfieldname = _des.substring(0, li_pos); // ParentFieldID
		String str_pkfieldname = _des.substring(li_pos + 1, _des.length()); // PKFieldID

		str_parentfieldname = str_parentfieldname.trim();// 截去空格
		str_pkfieldname = str_pkfieldname.trim();// 截去空格
		return new String[] { str_parentfieldname, str_pkfieldname };
	}

	/**
	 * 转换一条SQL,替换其中的{}为'',然后强行加上1=2,以使快速转出其中的结构!!
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

		if (li_pos_where > 0) // 如果有Where条件
		{
			if (li_pos_order > 0) { // 如果有order
				String str_prefix = str_sql_1.substring(0, li_pos_order); // 前辍
				String str_subfix = str_sql_1.substring(li_pos_order, str_sql_1.length()); // 后辍
				return str_prefix + " AND " + _pkname + "='" + _pkvalue + "' " + str_subfix; //
			} else {
				return str_sql_1 + " AND " + _pkname + "='" + _pkvalue + "'";
			}
		} else { // 如果没有where条件
			if (li_pos_order > 0) { // 如果有order
				String str_prefix = str_sql_1.substring(0, li_pos_order); // 前辍
				String str_subfix = str_sql_1.substring(li_pos_order, str_sql_1.length()); // 后辍
				return str_prefix + " WHERE " + _pkname + "='" + _pkvalue + "' " + str_subfix; //
			} else {
				return str_sql_1 + " WHERE " + _pkname + "='" + _pkvalue + "'";
			}
		}
	}

	/**
	 * 分析一条SQL,找出其中第一列的名称!
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
			int li_pos = str_sql.indexOf(","); // 找出第一个
			str_sql = str_sql.substring(6, li_pos).trim();
			int li_pos_2 = str_sql.indexOf(" ");
			if (li_pos_2 > 0) {
				str_sql = str_sql.substring(0, li_pos_2).trim();
			} else { //
			}
			return str_sql;
		} catch (Exception ex) {
			System.out.println("找出参照SQL[" + _sql + "]的第一列名失败!!");
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
	 * 将一个SQL中有{}的地方替换成_env中的值或_,map中的值!!!
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
				throw new Exception("SQL语句错误，{}不匹配！");
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
				mid_sql = convert(mid_sql, begin_index, end_index, _currSessionVO, _map);// 处理内嵌"{}"
			}
		} else {
			throw new Exception("SQL语句错误，{}不匹配！");
		}

		end_index = mid_sql.indexOf("}");

		String bracket_id = mid_sql.substring(0, end_index); // 这是{}中间的内容
		int li_pos = bracket_id.indexOf("."); //
		String str_itemkey = null;
		String str_itemkey_otherfield = null;
		if (li_pos > 0) { // 如果发现有个"."
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
	 * 取得Excel的安装地址
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
			throw new Exception("可能没装Excel");
		}

		try {
			RegistryKey node_2 = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE").openSubKey("Classes").openSubKey("CLSID").openSubKey(str_1).openSubKey("LocalServer32"); //
			String str_2 = node_2.getDefaultValue(); //
			return str_2;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("可能该操作系统不是Windows2000/XP/2003,对应位置找不到配置!");
		}
	}

	/**
	 * 创建TMO
	 * 
	 * @param _allcolumns
	 * @return
	 */
	public AbstractTMO getTMO(String[] _allcolumns) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // 模版编码,请勿随便修改

		HashVO[] childVOs = new HashVO[_allcolumns.length];
		for (int i = 0; i < childVOs.length; i++) {
			childVOs[i] = new HashVO();
			childVOs[i].setAttributeValue("itemkey", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemname", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); // 都是文本框
			childVOs[i].setAttributeValue("listwidth", "125"); //

			if (_allcolumns[i].endsWith("#")) {
				childVOs[i].setAttributeValue("listisshowable", "N"); //
			} else {
				childVOs[i].setAttributeValue("listisshowable", "Y"); //
			}
		}

		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs); // 创建元原模板数据
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
		// String fn_name = str_fun[i][1]; // 应从数据库取!!
		// String str_className = str_fun[i][3]; // 应从数据库取!!
		// try {
		// PostfixMathCommand formula = (PostfixMathCommand)
		// Class.forName(str_className).newInstance(); //
		// parser.addFunction(fn_name, formula);// 替换字符串中的内容
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
	 * 谁搞的代码,还做什么静态变量,其实没必要,一是这个计算其实很快,二是该类有静态变量导致垃圾回收有问题,特别是被其他人引用后!
	 * 再有,即使缓存也不要放在类中,统一在ClientEnv中,或专门搞一个ClientCache类就行了! 里面放个HashMap
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
		constMap.put(_html, color); //因为颜色这种计算永远不会变化,且计算量小,所以适合做缓存...
		return color; // 
	}

	/**
	 * 是255,0,0,的样子
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
			System.err.println("转换颜色[" + _rgb + "]错误:" + ex.getClass() + ":" + ex.getMessage()); //
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

	// 判断一个字符串是否是16进制,但不准!!
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

	// 取得当前日期!如果有bank标记,则表示有-,否则就是连在一起的数字!
	public String getCurrDate(boolean _isHaveBank, boolean _isClientDiffServer) {
		long ll_diff = 0; //
		if (getJVSite() == JVMSITE_CLIENT && _isClientDiffServer) { // 如果是客户端,则要计算一下差异!!!!
			ll_diff = getDiffServerTime(); //
		}
		SimpleDateFormat sdf_curr = new SimpleDateFormat((_isHaveBank ? "yyyy-MM-dd" : "yyyyMMdd"), Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis() - ll_diff));
	}

	// 取得当前时间
	public String getCurrTime() {
		return getCurrTime(true, true);
	}

	// 取得当前时间,可以反映定是否是纯数字的! 有时在生成文件时需要去掉空格，冒号等符号!
	public String getCurrTime(boolean _isHaveBank) {
		return getCurrTime(_isHaveBank, true);
	}

	/**
	 * 取得当前时间!
	 * 
	 * @param _isTrimBank
	 *            是否截掉中间的所有格子符,即有时不需要-与:符号,只需一个纯数字拼起来的!
	 * @param _isClientDiffServer
	 *            如果是客户端的话,是否与服务器进行比较与同步,默认肯定是需要的!
	 * @return
	 */
	public String getCurrTime(boolean _isHaveBank, boolean _isClientDiffServer) {
		// 因为服务器与客户端经常差异好几分钟,为了更准,同时又能保证性能,第一次先取下,算出两者差异!
		long ll_diff = 0; //
		if (getJVSite() == JVMSITE_CLIENT && _isClientDiffServer) { // 如果是客户端,且必须与服务器端比较!
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
	 * 返回当前季度,格式是[2012年1季度]
	 * @param _date
	 * @return
	 */
	public String getCurrDateSeason(String _date) {
		String str_season = _date.substring(0, 4) + "年" + getSeason(_date.substring(5, 7)) + "季度"; //
		return str_season; //
	}

	public String getCurrDateMonth() {
		return getCurrDateMonth(getCurrDate()); //
	}

	/**
	 * 返回当前月份,格式为[2012年05月][2012年10月]
	 * @param _date
	 * @return
	 */
	public String getCurrDateMonth(String _date) {
		String str_month = _date.substring(0, 4) + "年" + _date.substring(5, 7) + "月"; //
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

	// 根据Long值,返回时间!!
	public String getTimeByLongValue(long _longValue) {
		return getTimeByLongValue(_longValue, "yyyy-MM-dd HH:mm:ss"); //
	}

	// 根据Long值,返回时间!!
	public String getTimeByLongValue(long _longValue, String _format) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat(_format, Locale.SIMPLIFIED_CHINESE); //
		return sdf_curr.format(new Date(_longValue)); // 补上差异!!
	}

	// 取得客户端与服务器端时间的差异!
	public long getDiffServerTime() {
		if (ClientEnvironment.llDiffServerTime == null) { // 如果没定义
			try {
				long ll_begin = System.currentTimeMillis(); //
				long ll_serverTime = UIUtil.getServerCurrTimeLongValue(); //
				long ll_end = System.currentTimeMillis(); //
				long ll_clientTime = System.currentTimeMillis() - (ll_end - ll_begin); // (ll_end
				// -
				// ll_begin)客户端的当前时间,必须减去执行上面远程调用的时间(这里有个前提,即假设服务器取时耗时是0)
				ClientEnvironment.llDiffServerTime = new Long(ll_clientTime - ll_serverTime); // 算出差异,是服务器端比服务器多多少,即当客户端晚于服务器端是正数,
			} catch (Exception ex) {
				ex.printStackTrace(); //
				ClientEnvironment.llDiffServerTime = new Long(0); //
			}
		}
		return ClientEnvironment.llDiffServerTime.longValue(); //
	}

	// 将long值格式化成字符串的日期字符串
	public String formatDateToStr(long _long) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_long));
	}

	// 将long值格式化成字符串的时间字符串
	public String formatTimeToStr(long _long) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_long));
	}

	// 将一个字符串的日期反转成Long值,用来计算前两天的之用
	public long parseDateToLongValue(String _dateStr) {
		try {
			SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
			return sdf_curr.parse(_dateStr).getTime(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	// 将一个字符串的到秒的时间值反转成LongValue
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
		return getCompentBytes(_component, _width, _height, true); // 默认是bmp还是jpg,兴业中bmp很高清,但航天科工项目中竟然遇到过bmp文件过大导致内存溢出的情况!!
		// 但也有可能是客户端开发的虚拟机内存没设的原因!
	}

	public Image getCompentImage(Component _component) throws Exception {
		Rectangle rect = _component.getBounds();
		BufferedImage image = (BufferedImage) _component.createImage(rect.width, rect.height); //
		Graphics g = image.getGraphics();
		_component.paint(g); // 将控件的图画写入到这个新的画板中
		g.dispose();
		return image;
	}

	// 得到Swing组件的字节码
	public byte[] getCompentBytes(Component _component, int _width, int _height, boolean _isBmp) throws Exception {

		//稍微改了下便于理解, 加了个边框好看点! Gwang 2014-04-21
		BufferedImage bi = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB); //创建一个缓冲图形对象
		Graphics2D g2d = (Graphics2D) bi.createGraphics(); //创建画板
		_component.paint(g2d); //将控件的图画写入bi		
		g2d.setColor(Color.BLACK); //设置画笔颜色
		g2d.drawRect(0, 0, _width - 2, _height - 2); //画个边框
		g2d.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream(); // 输出流!!
		String str_pictype = (_isBmp ? "bmp" : "jpeg"); // bmp与jpg各有优势,bmp清晰但文件大,jpg反之!
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
		return getCompent64Code(_component, _width, _height, true); // 默认是bmp,因为兴业项目中生成图片都是需要高清的!
	}

	// 得到Swing组件的64位码,用生生成mht,word的xml格式等!
	public String getCompent64Code(Component _component, int _width, int _height, boolean _isBmp) throws Exception {
		byte[] bytes = getCompentBytes(_component, _width, _height, _isBmp); // 先得到字节码
		String str_64code = convertBytesTo64Code(bytes); // 转成64位码
		return str_64code; //
	}

	public void saveCompentAsJPGFile(Component _component, String _fileName) throws Exception {
		saveCompentAsPicture(_component, _fileName, false); //
	}

	// 将Swing组件生成一个jpg文件!!它与下面的生成word文件的方法不一样!
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
		// //就bmp与jpeg两种,bmp精度高,JPG文件小,各有适用场合!!!
		// Iterator iter = ImageIO.getImageWriters(type, str_picturetype); //
		// ImageWriter writer = (ImageWriter) iter.next(); //
		// IIOImage iioImage = new IIOImage(image, null, null);
		// ImageWriteParam param = writer.getDefaultWriteParam();
		// param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		// if (!_isBmp) { //如果是jpg格式
		// param.setCompressionQuality(1.0f); //设置压缩质量
		// //param.setTilingMode(ImageWriteParam.MODE_EXPLICIT); //
		// }
		// ImageOutputStream outputStream = ImageIO.createImageOutputStream(new
		// File(_fileName));
		// writer.setOutput(outputStream);
		// writer.write(null, iioImage, param);
		// outputStream.close(); //
		byte[] bytes = getCompentBytes(_component, rect.width, rect.height, _isBmp); // 以前是用上面注释的那段方法!但感觉很麻烦!还是使用简单的办法!
		// 但就怕有时会不会图片黑屏的情况!!在BS端生成时就遇到黑屏的情况!一时还不知道是何原因!
		writeBytesToOutputStream(new FileOutputStream(new File(_fileName), false), bytes); // 写文件!!
	}

	// 将Swing组件生成Word文件!!!它与上面生成jpg的方法不一样,但都行,是两种方法!
	public void saveCompentAsWordFile(Component _component, String _fileName, int _width, int _height) throws Exception {
		WordFileUtil wordFileUtil = new WordFileUtil();
		StringBuilder sb_text = new StringBuilder(); //
		sb_text.append(wordFileUtil.getWordFileBegin()); // word的头部!
		sb_text.append(wordFileUtil.getWordTitle("工作流图片导出至Word")); // 标题!
		int li_picwidth = _width; //
		int li_picheight = _height; //
		if (_width > 400) { // 如果宽度大于420,则按比例缩小!!
			li_picwidth = 400;
			li_picheight = (_height * 400) / _width; //
		}
		if (li_picheight > 620) {
			li_picheight = 620;
		}
		String str_64code = getCompent64Code(_component, _width, _height, true); // 将图片搞成64位码!
		String str_imgxml = wordFileUtil.getImageXml(str_64code, li_picwidth, li_picheight); // 得到图片的Word的XML格式的字符串!
		sb_text.append(str_imgxml); // 加入!
		sb_text.append(wordFileUtil.getWordFileEnd()); // 加入文件尾!!
		writeStrToOutputStream(new FileOutputStream(_fileName), sb_text.toString()); // 写文件!
	}

	//将一个图片对象缩放成指定大小的图像!因为UI端与BS端都有可能用到,所以放在TB中
	public Image getImageScale(Image _image, int _width, int _height) {
		int li_width = _image.getWidth(null); //原来图片宽度!
		int li_height = _image.getHeight(null); //原来图片高度!
		double ld_scale_x = (double) _width / (double) li_width; //宽度的比例!!必须先转换成double再相除,否则得不到小数位!
		double ld_scale_y = (double) _height / (double) li_height; //高度的比例!!

		//先创建BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB); //
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		//转换!!
		AffineTransform tx = new AffineTransform();
		tx.scale(ld_scale_x, ld_scale_y); //参数是比例!!!
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR); //
		bimage = op.filter(bimage, null); //
		return bimage; //
	}

	//将一个图片对象翻转/镜像!
	public Image getImageFlipping(Image _image, int _type) {
		int li_width = _image.getWidth(null); //原来图片宽度!
		int li_height = _image.getHeight(null); //原来图片高度!
		//先创建BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		//转换
		if (_type == 1) { //上下翻转!!
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -_image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null);
		} else if (_type == 2) { //左右翻转!!
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-_image.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null);
		} else if (_type == 3) { //上下与左右同时翻转!!
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			tx.translate(-_image.getWidth(null), -_image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bimage = op.filter(bimage, null); //
		}
		return bimage; //
	}

	//旋转!!!
	public Image getImageRotate(Image _image, double _theta) {
		int li_width = _image.getWidth(null); //原来图片宽度!
		int li_height = _image.getHeight(null); //原来图片高度!
		//先创建BufferImage
		BufferedImage bimage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimage.getGraphics();
		g.drawImage(_image, 0, 0, null);
		g.dispose();

		AffineTransform tx = new AffineTransform(); //
		tx.rotate(Math.toRadians(_theta), bimage.getWidth() / 2, bimage.getHeight() / 2); //旋转多少度,比如90,270,-90
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage bimage2 = op.filter(bimage, null);
		return bimage2; //
	}

	// 读取一个输入流成字符串,而且是中文格式!
	public String readFromInputStreamToStr(InputStream _ins) {
		try {
			byte[] bys = readFromInputStreamToBytes(_ins); //
			return new String(bys, "GBK"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	// 读取一个输入流,返回其所有字节! 比如读文件
	public byte[] readFromInputStreamToBytes(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // 通过循环读取,更流畅,更稳定!!节约内存!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				bout.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
			try {
				_ins.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
		}
	}

	// 将一个字符串写到文件中去!而且必须是中文格式
	public void writeStrToOutputStream(OutputStream _out, String _str) {
		try {
			writeBytesToOutputStream(_out, _str.getBytes("GBK")); // 中文的!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	// 将一个字节数组写到文件中去!
	public void writeBytesToOutputStream(OutputStream _out, byte[] _bys) {
		ByteArrayInputStream bins = null; //
		try {
			bins = new ByteArrayInputStream(_bys); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] tmpbys = new byte[2048]; //
			int pos = -1; //
			while ((pos = bins.read(tmpbys)) != -1) { // 循环读入
				_out.write(tmpbys, 0, pos); // 写入
			}
		} catch (Exception ex) { //
			ex.printStackTrace(); //
		} finally {
			try {
				_out.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
			try {
				bins.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
		}
	}

	/**
	 * 将一个HashVO[]转换成一个Html表格
	 * 
	 * @param _hvs
	 * @return
	 */
	public String getHtmlByHashVOs(HashVO[] _hvs) {
		StringBuffer sb_html = new StringBuffer(); //
		if (_hvs != null && _hvs.length > 0) {
			sb_html.append("<table width=\"100%\" style=\"border-collapse:   collapse; font-size: 12px; \" style=\"word-break:break-all\">\r\n");
			sb_html.append("<tr><td align=\"center\" bgcolor=\"EEEEEE\" style=\"border:   solid   1px   #888888; font-size: 12px;\">序号</td>");
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
	 * 根据一个VectorMap生成Html,这个Html是一个个区域,每个区域上面都有一个标题,点击这个标题可以展开与收缩这个区域
	 * 
	 * @param _vm
	 * @return
	 */
	public String getHtmlByVectorMap(VectorMap _vm, int _type) {
		StringBuffer sb_html = new StringBuffer(); //
		if (_vm == null) {
			return "没有数据!"; //
		}

		String[] str_keys = _vm.getKeysAsString(); // 得到所有索引
		if (str_keys.length == 0) {
			return "没有数据!"; //
		}
		if (_type == 1) { // 如果是展开收缩型的
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

			sb_html.append("<input id=\"button_expandall\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"全部展开\"  onclick=\"showall();\">\r\n");
			sb_html.append("<input id=\"button_closeall\"  type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"全部收缩\"  onclick=\"closeall();\">\r\n");

			for (int i = 0; i < str_keys.length; i++) {
				sb_html.append("<table width=\"100%\" border=\"0\">\r\n");
				sb_html.append("<tr onclick=\"hid_show('" + str_keys[i] + "');\"  border=\"0\"><td bgcolor=\"CCCCCC\" border=\"0\" style=\"font-size: 15px; color: #333333; line-height: 18px; font-family: 宋体\"><img id=\"img_" + str_keys[i] + "\" src=\"./images/zoomout.gif\"/><strong>" + str_keys[i] + "</strong></td></tr>\r\n");
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
		} else if (_type == 2) { // 如果是一排按钮型的
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

			// 输出各个按钮
			for (int i = 0; i < str_keys.length; i++) {
				if (i == 0) {
					sb_html.append("<input id=\"button_" + str_keys[i] + "\" type=\"button\" style=\"" + str_btnstyle_2 + "\"  value=\"" + str_keys[i] + "\"  onclick=\"hid_show('" + str_keys[i] + "')\">\r\n");
				} else {
					sb_html.append("<input id=\"button_" + str_keys[i] + "\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"" + str_keys[i] + "\"  onclick=\"hid_show('" + str_keys[i] + "')\">\r\n");
				}
			}

			sb_html.append("<input id=\"button_showalltables\" type=\"button\" style=\"" + str_btnstyle_1 + "\"  value=\"显示所有\"  onclick=\"showall();\">\r\n");
			sb_html.append("<br>"); //

			for (int i = 0; i < str_keys.length; i++) {
				if (i == 0) {
					sb_html.append("<table id=\"table_" + str_keys[i] + "\" width=\"100%\" style=\"display:\" border=0>\r\n"); // 显示
				} else {
					sb_html.append("<table id=\"table_" + str_keys[i] + "\" width=\"100%\" style=\"display:none\" border=0>\r\n"); // 隐藏
				}
				sb_html.append("<tr><td bgcolor=\"CCCCCC\"><strong>" + str_keys[i] + "</strong></td></tr>\r\n");
				sb_html.append("<tr><td>\r\n");
				sb_html.append("" + _vm.get(str_keys[i])); //
				sb_html.append("</td></tr>\r\n");
				sb_html.append("</table>\r\n");
			}
		} else {
			sb_html.append("未知的风格\r\n");
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
			sb_html.append("<title>将HashVO数组生成Html</title>\r\n");
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
				sb_header.append("<tr><td>序号</td>");
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
				WLTLogger.getLogger(TBUtil.class).debug("将一个HashVO[]数组的【" + _hvs.length + "】条记录输出至文件【" + _filename + "】成功!!!"); //
			} else {
				print.println("没有数据!");
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
				print.println("没有数据!");
			}
			print.println("</body>");
			print.println("</html>");
			System.out.println("输出文件[" + _filename + "]成功!!!"); //
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			print.close(); //
		}

	}

	/**
	 * 转换Image数据为byte数组
	 * 
	 * @param image
	 *            Image对象
	 * @param format
	 *            image格式字符串.如"jpeg","png"
	 * @return byte数组
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
	 * 将BillVO中的数据转换成一个HashMap..
	 * 
	 * @param _billVO
	 * @return
	 */
	public HashMap getMapFromBillVO(BillVO _billVO) {
		HashMap map = new HashMap(); // 创建哈希表
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
	 * 将一个异常的堆栈转换成字符串...
	 * 
	 * @return
	 */
	public String getExceptionStringBuffer(Throwable _ex, boolean _isHtmlFormat, boolean _isHtmlContainHead) {
		try {
			if (_ex instanceof WLTAppException) { //如果是业务异常,则直接返回说明,不要堆栈,否则客户反感!!
				return _ex.getMessage(); //
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 创建一个二进制流
			PrintWriter pw = new PrintWriter(bos, true); //
			_ex.printStackTrace(pw); // 将异常打印到二进流上
			if (_ex.getCause() != null) { //如果还有原因...以后这里应该是个递归循环
				pw.println("此异常产生的原因:"); //
				_ex.getCause().printStackTrace(pw); //
			}
			byte exbytes[] = bos.toByteArray(); // 取得二进流的二进制数组
			bos.close(); // 关闭流.
			String sb_exstack = new String(exbytes, "GBK"); // 取得字符串s
			if (!_isHtmlFormat) {
				return sb_exstack; //
			} else { // 如果是html风格,则要转换一把
				sb_exstack = "<font size=2 color=\"red\">\r\n" + replaceAll(replaceHtmlEncode(sb_exstack), "\r", "<br>") + "</font>"; //	
				if (!_isHtmlContainHead) {
					return sb_exstack;
				} else { // 如果想输出的html是整个!
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

	// 打印一个异常堆栈
	public static synchronized void printStackTrace(Throwable _ex) {
		if (tbUtil.getJVSite() == TBUtil.JVMSITE_CLIENT) { // 如果是客户端则直接输出
			_ex.printStackTrace();
		} else { // 如果是服务器端,则根据一个参数,判断是输出堆栈还是只输出一行话!!
			if (cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack) { // 如果定义了输出异常!!
				_ex.printStackTrace(); //
			} else {
				System.err.println("发生异常,只输出异常内容:【" + _ex.getMessage() + "】,想了解堆栈信息与管理员联系."); //
			}
		}
	}

	/**
	 * 反射调用一个类的一个方法,只支持String类型参数,但个数不限 比如"com.pushworld.TestAction.fn_1()"
	 * 即反射类中的方法是一个一维数组的参数列表!!! 一维数组的好处是简洁,但缺点为移位与扩展不方便,
	 * 所以下面又搞了一个HashMap参数的方法,根据长期的经验来看,凡是遇到这种参数配置的地方(包括公式,参照定义等),应该统一使用HashMap,因为它有三大好处:
	 * 1.顺序调整没事!!! 不像String[],一调就出问题!!
	 * 2.key本身就是一个帮助说明,不像String[],时间长了连自己都不知道第几位是什么意思!!!
	 * 3.哈希表的Value可以是任意对象,非常容易与原有参数进行合并,很简洁清爽!!!
	 * 
	 * @param _formula
	 * @return
	 */
	public Object reflectCallMethod(String _formula) {
		try {
			String str_className = _formula.substring(0, _formula.lastIndexOf(".")); // 类名
			String str_methodName = _formula.substring(_formula.lastIndexOf(".") + 1, _formula.indexOf("(")); // 方法名
			String str_pars = _formula.substring(_formula.indexOf("(") + 1, _formula.lastIndexOf(")")); // 所有参数!
			String[] str_parItems = split(str_pars, ","); //
			return reflectCallMethod(str_className, str_methodName, str_parItems); //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// 反射调用!
	public Object reflectCallMethod(String _className, String _methodName, String[] _pars) {
		try {
			String str_className = _className; // 类名
			String str_methodName = _methodName; // 方法名
			String[] str_parItems = _pars; // 参数!!
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
			Class cls = Class.forName(str_className); // 这一行很可能抛ClassNotFound异常
			Object obj = cls.newInstance();
			Class[] parcls = new Class[str_parItems.length]; // 
			for (int i = 0; i < parcls.length; i++) {
				parcls[i] = String.class;
			}
			Method method = cls.getMethod(str_methodName, parcls); // 这一行很可能抛java.lang.NoSuchMethodException异常..
			Object returnObj = method.invoke(obj, str_parItems); //
			return returnObj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 通过反射通用调用一个类方法,这个方法必须是参数与返回值都是HashMap!!! 这个方法非常有用,适用于任何场合的需要进行反射调用的地方!!!
	 * 
	 * @param _formula
	 *            公式,很关键,格式是类名加方法名的样子,方法里面还可以支持参数!!!
	 *            比如:cn.com.xch.TestCls.fn_1(),或者cn.com.xch.TestCls.fn_1("是否展开,"Y","层级,"3"),然后程序会自动将括号中的参数拼成哈希表,然后合并在一起送入!!!这样就可以更多可能的重用某个类了!!
	 *            根据长期的经验来看,凡是遇到这种参数配置的地方(包括公式,参照定义等),应该统一使用HashMap,因为它有三大好处:
	 *            1.顺序调整没事!!! 不像String[],一调就出问题!!
	 *            2.key本身就是一个帮助说明,不像String[],时间长了连自己都不知道第几位是什么意思!!!
	 *            3.哈希表的Value可以是任意对象,非常容易与原有参数进行合并,很简洁清爽!!!
	 * 
	 * 以前有一种哈希表的配置方法是"是否展开=Y"的样子,后来经过反复实践检验,这样在非公式执行的时候倒也行(即就是纯字符串),但在遇到公式执行的时候,有时候值就不一定是字符串,比如getClassValue(),它常常需要得到整个BillCardPanel句柄,以前只是支持String就不行了!!!
	 * 所以参考Swing中LookAndFeel的思路,就以奇偶位为一对,奇数位是key,偶数位是Value,第1-2,3-4,5-6，一对一对的,key都是字符串,但Value可以是各种数据类型!!!
	 * 比如BillCardPanel等!!
	 * 这种奇偶位对应的方法是一个万能的,通用的,极易扩展与兼容旧配置的方法,而且Jdk已有这样的设计方法,像参照定义,公式定义等都应该这么搞!!!可为什么一开始没有想到呢???
	 * 看来还是经验不够啊!!
	 * @param _parMap
	 * @return
	 */
	public HashMap reflectCallCommMethod(String _formula, HashMap _parMap) {
		try {
			HashMap realParMap = _parMap; //
			String str_className = _formula.substring(0, _formula.lastIndexOf(".")); // 类名
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
					realParMap.put(str_key, str_value); // 加入进去!!!
				}
			}
			Class cls = Class.forName(str_className); // 这一行很可能抛ClassNotFound异常
			Object obj = cls.newInstance();
			Method method = cls.getMethod(str_methodName, new Class[] { java.util.HashMap.class }); // 该方法只支持HashMap参数,这一行很可能抛java.lang.NoSuchMethodException异常..
			HashMap returnMap = (HashMap) method.invoke(obj, realParMap); //
			return returnMap; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据标签定义结合是否可查询，可保存，是否html转换成实际的
	 * 
	 * @param _text
	 * @param _isAdmin
	 * @param _ismustInput
	 * @param _isCanQuery
	 * @param _isCanSave
	 * @return
	 */
	//sunfujun/20120720/解决表头红星问题
	public String getLableRealText(String _text, boolean _isAdmin, boolean _ismustInput, boolean _isCanQuery, boolean _isCanSave) {//sunfujun/20120720/隐藏列表表头的*
		return this.getLableRealText(_text, _isAdmin, _ismustInput, _isCanQuery, _isCanSave, true);
	}

	public String getLableRealText(String _text, boolean _isAdmin, boolean _ismustInput, boolean _isCanQuery, boolean _isCanSave, boolean isCard) {//sunfujun/20120626/偶然一次需求的修改_xch
		if (_text == null) {
			return "没有定义标签名";
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
		//		if (_isAdmin) { // 如果是管理身份 //这里统统去掉改成右键可以查看
		//			if (!_isCanQuery) {
		//				str_prefix = str_prefix + "★";
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
		sb_html.append("<TITLE>服务器端状态</TITLE>\r\n"); // 标题名
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
	 * time 格式为："2008年;" 或 "2008年1季度;" 或 2008年01月;" 或 "2008-01-01;" 或
	 * "2008-01-01;2008-02-01" isFree 格式为："N" 表示查询某时间段内有效工作日天数，"Y"
	 * 表示查询某时间段内放假天数，其他表示某时间段内所有天数
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
	 * 将日历返回的时间转换成 "2008;2009" 或 "2008-01;2008-02" 或 "2008-01-01;2009-01-01"
	 * 格式的时间段
	 * 
	 * @param _time
	 * @return
	 */
	public String convertComp_dateTimeFormat(String _time) {
		String begin = "";
		String end = "";
		if (_time == null || "".equals(_time)) {
			return null;
		} else if (_time.endsWith("年;")) { // 格式："2008年;"
			begin = _time.substring(0, 4);
			end = Integer.parseInt(begin) + 1 + "";
		} else if (_time.endsWith("季度;")) { // 格式："2008年1季度;"
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
		} else if (_time.endsWith("月;")) { // 格式："2011年02月;"
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
		} else if (_time.endsWith(";")) { // 格式为："2008-01-01;"
			begin = _time.substring(0, 10);
			end = begin;
		} else { // 格式为："2008-01-01;2008-02-01"
			return _time;
		}
		return begin + ";" + end;

	}

	/**
	 * time 格式为："2008-01-01;2008-02-01" isFree 格式为："N" 表示查询某时间段内有效工作日天数，"Y"
	 * 表示查询某时间段内放假天数，其他表示某时间段内所有天数
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
	 * 两个字符串的时间差
	 * @param _date1
	 * @param _date2
	 * @return
	 * @throws Exception
	 */
	public long betweenTwoTimeSecond(String _date1, String _date2) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date1 = sdf.parse(_date1);
		java.util.Date date2 = sdf.parse(_date2);

		long li_second = (date2.getTime() - date1.getTime()) / 1000; // 秒
		return li_second; //
	}

	/**
	 * 从BillVO数组里随机取_limit条数据，不重复！
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
	 * 得到指定表的所有列
	 */
	public String getTableAllCols(String tableName) throws Exception {
		TableDataStruct templetStruct = null;
		StringBuffer sb_item = new StringBuffer();
		String names[] = null;
		boolean flag = false; //从oracle系统表的表结构
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
		String regex1 = "^.*\\p{Digit}.*$"; //包含数字
		String regex2 = "^.*\\p{Punct}.*$"; //包含字符
		String regex3 = "^.*\\p{Lower}.*$"; //小写
		String regex4 = "^.*\\p{Upper}.*$"; //大写
		String regex[] = new String[] { regex1, regex2, regex3, regex4 };
		int matchcount = 0; //匹配次数
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
	 * 图片BASE64编码转为图片
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
	 * 在流程图以及bom图中画风险点时
	 * 不仅需要圆形还可以画其他形状
	 * 此方法返回Shape对象供此使用
	 * 以后如果需要多种可以在这个方法
	 * 里增加,
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
	 * 此方法是在画风险点的个数时
	 * 取得字的初始X坐标
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
	 *此方法是在画风险点的个数时
	 * 取得字的初始Y坐标
	 * @param g2
	 * @param a
	 * @param s
	 * @return
	 */
	public float getStrY(Graphics2D g2, Shape a, String s) {
		BigDecimal acy = new BigDecimal(a.getBounds().getCenterY());
		BigDecimal sh = new BigDecimal(g2.getFontMetrics().getHeight());
		return acy.add(sh.divide(new BigDecimal(4), 6, BigDecimal.ROUND_HALF_UP)).floatValue();//为什么是除以4是合适的，应该是除以2啊 没懂
	}

	//许多类中到处使用到TBUtil,都在在各个类中创建一个,麻烦，而且多耗内存,这么频繁使用的类，干脆就直接使用静态变量！【xch/2012-05-14】
	//在客户端(UI)使用肯定没事，但在服务器端，发生高并发时,是否有同步问题？请注意！
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
	 * 取得某一个月起始天
	 * @param _month
	 * @return
	 */
	public String getBeginDateByMonth(String date) { // 【杨科/2012-08-27】
		String str_year = date.substring(0, 4); //
		String str_month = date.substring(5, 7); //
		return str_year + "-" + str_month + "-01"; //
	}

	/**
	 * 取得某一个月结束天
	 * @param _month
	 * @return
	 */
	public String getEndDateByMonth(String date) { // 【杨科/2012-08-27】
		String str_year = date.substring(0, 4); //
		String str_month = date.substring(5, 7); //
		return str_year + "-" + str_month + "-" + getOneMonthDays(Integer.parseInt(str_year), Integer.parseInt(str_month)); //
	}

	/**
	 * 取得某一个月的总共天数
	 * @param _year
	 * @param _month
	 * @return
	 */
	public int getOneMonthDays(int _year, int _month) { // 【杨科/2012-08-27】
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
	 * 前推几个月
	 * @param _month
	 * @return
	 * @throws Exception 
	 */
	public String getBackMonth(String date, int frontmonth) throws Exception { // 【杨科/2012-08-27】
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		Calendar c = new GregorianCalendar();
		c.setTime(sdf.parse(date));
		c.add(c.MONTH, -frontmonth);
		return sdf.format(c.getTime());
	}

	/**
	 * 获取IP
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
	 * 两个时间的天数差。 first-second @haoming 2013-3-8
	 */
	public int compareTwoDate(String firstDate, String secondDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(firstDate);
		Date date2 = sdf.parse(secondDate);
		long mills = date1.getTime() - date2.getTime();
		return (int) (mills / (long) (1000 * 60 * 60 * 24));
	}

	/**
	 * 复制文件逻辑处理 【李春娟/2013-05-21】
	 * */
	private boolean copyFile(String _oldFilePath, String _newFilePath) {
		File oldfile = new File(_oldFilePath);
		if (!oldfile.exists()) {
			return false;//如果没有找到需要复制的文件则返回false
		}
		if (_newFilePath.indexOf("\\") >= 0) {
			_newFilePath = UIUtil.replaceAll(_newFilePath, "\\", "/"); //先进行替换
		}
		File newfile = new File(_newFilePath.substring(0, _newFilePath.lastIndexOf("/")));//新文件的文件夹
		if (!newfile.exists()) {//判断新文件的文件夹是否存在，如果不存在则创建
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
					output.close(); // 关闭输出流!!!
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (input != null) {
					input.close(); // 关闭输入流!!
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;//如果复制成功了则返回true
	}

	/**
	 * 压缩成ZIP文件,并转为byte[],可压缩文件或文件夹
	 * @param filePath 文件路径名 例：d:/ABC.doc
	 * @return Object[0]存放返回的压缩文件byte[],Object[1]存放压缩文件的文件名 
	 * @author YangQing-2013-05-13
	 */
	public Object[] compressFile(String filePath) {
		Object[] returnObject = new Object[2];
		byte[] compressBytes = null;
		try {
			if (filePath.contains("\\")) {
				filePath.replace("\\", "/");
			}
			if (filePath.endsWith("/")) {//如果以'/'结尾，则去掉
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
	 * 压缩ZIP
	 * @param _file 要压缩的(子)文件
	 * @param filePath 要压缩的总目录或文件
	 * @param out ZIP输出流
	 * @author YangQing/2013-05-13
	 */
	private void zipFile(File _file, String filePath, ZipOutputStream out) {
		try {
			byte data[] = new byte[2048];
			File[] files = new File[0];
			if (_file.isDirectory()) {//如果是文件夹目录
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
				if (files[i].isDirectory()) {//子文件夹	
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
	 * 解压缩ZIP文件
	 * @param basePath 要解压到哪 如:D:/xx/
	 * @param zipPath 被解压的ZIP路径 如:d:/xx/ABC.doc
	 * @return 解压后的文件路径
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
				//会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
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
	 * 主要作用是根据数据库类型从ds中获取数据库的名称  袁江晓 20130603
	 * 譬如从ds为new获取该数据库的用户名  如果为mysql则从<dburl>jdbc:mysql://192.168.0.5:3000/gdnx_xu?characterEncoding=GBK</dburl>中获取gdnx_xu
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
			String url_dest = dsv.getDburl();//目标数据库URL
			String temp = url_dest.substring(0, url_dest.indexOf("?"));
			String temp1 = temp.substring(temp.lastIndexOf("/") + 1, temp.length());
			ret_value = temp1;
		}
		return ret_value;
	}

	//替换文本中的html特殊标记
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
	 * 制度名称加书名号【李春娟/2016-08-16】
	 */
	public static String getLawOrRuleName(String _name) {
		return getFormatText(_name, "《", "》");
	}

	/*
	 * 制度编号加括号【李春娟/2016-08-16】
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
