package cn.com.infostrategy.bs.common;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 系统配置类,就是处理系统所有配置的类
 * 对应的pub_option表中的值,一开始为空,第一次访问时才会真正加载,但BootServlet中不会加载!
 * @author xch
 *
 */
public class SystemOptions {

	public static SystemOptions sysOptions = null; //实际类
	private HashMap dataMap = null; //
	private TBUtil tbUtil = new TBUtil(); //
	private Logger logger = WLTLogger.getLogger(SystemOptions.class); //

	/**
	 * 构造方法,第一次需要从数据库中加载..
	 */
	private SystemOptions() {
		if (dataMap == null) {
			dataMap = new HashMap(); //
			reLoadDataFromDB(true); //加载数据
		}
	}

	/**
	 * 重新加载缓存!!!
	 * @param _isJudgeTable
	 * @return
	 */
	public String[][] reLoadDataFromDB(boolean _isJudgeTable) {
		try {
			if (!_isJudgeTable || (ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_OPTION"))) { //如果有这个表才做,因为要考虑安装过程!!,安装结束后,清空缓存时,vc_alltables是为空的,结果还是不从数据库取!!所以加了个判断!!【xch/2012-03-06】
				dataMap.clear(); //
				HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_option", true); //
				for (int i = 0; i < hvs.length; i++) {
					//系统参数配置中,若配置的值含有"\r\n",标识着"换行",但系统代码在以字符串获取时,Java会自动将"\"转换为"\\"存储在内存中;系统再次调用该参数时,则不会换行,而是以字符串"\r\n"显示,故需要转换一下
					dataMap.put(hvs[i].getStringValue("parkey"), hvs[i].getStringValue("parvalue", "").replaceAll("\\\\r\\\\n", "\r\n")); //将key与value置入!!
				}
			}
			String[] str_allKeys = (String[]) dataMap.keySet().toArray(new String[0]); //
			String[][] str_return = new String[str_allKeys.length][2]; //
			for (int i = 0; i < str_allKeys.length; i++) {
				str_return[i][0] = str_allKeys[i];
				str_return[i][1] = (String) dataMap.get(str_allKeys[i]);
			}
			return str_return;
		} catch (Exception ex) {
			logger.error("从数据库加载配置信息发生异常!", ex); ////
			return null; //
		}
	}

	//根据key直接取Value,返回字符串
	public static String getStringValue(String _key) {
		return getStringValue(_key, null);
	}

	public static String getStringValue(String _key, String _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		} else {
			return str_strvalue;
		}
	}

	public static int getIntegerValue(String _key) {
		return getIntegerValue(_key, 0);
	}

	public static int getIntegerValue(String _key, Integer _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl.intValue();
		} else {
			try {
				return Integer.parseInt(str_strvalue); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
				return _nvl.intValue();
			}
		}
	}

	public static boolean getBooleanValue(String _key) {
		return getBooleanValue(_key, false); //
	}

	public static boolean getBooleanValue(String _key, Boolean _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}

		if (str_strvalue.equalsIgnoreCase("Y") || str_strvalue.equalsIgnoreCase("true") || str_strvalue.equalsIgnoreCase("是")) { //
			return true;
		} else {
			return false;
		}
	}

	//根据key，返回value中以分号分隔的各项中的哈希键值中的某个key的值,比如：【是否可以切换风格=Y;是否可以切换登录模式=N】
	public static String getHashItemStringValue(String _key, String _itemKey) {
		return getHashItemStringValue(_key, _itemKey, null);
	}

	public static String getHashItemStringValue(String _key, String _itemKey, String _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //取得字符串中的对应的值
		if (str_itemValue == null) {
			return _nvl;
		} else {
			return str_itemValue;
		}
	}

	public static int getHashItemIntegerValue(String _key, String _itemKey) {
		return getHashItemIntegerValue(_key, _itemKey, 0);
	}

	public static int getHashItemIntegerValue(String _key, String _itemKey, Integer _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return _nvl;
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //取得字符串中的对应的值
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

	public static boolean getHashItemBooleanValue(String _key, String _itemKey) {
		return getHashItemBooleanValue(_key, _itemKey, false); //
	}

	public static boolean getHashItemBooleanValue(String _key, String _itemKey, Boolean _nvl) {
		String str_strvalue = (String) (getInstance().getDataMap().get(_key)); //
		if (str_strvalue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}
		String str_itemValue = getInstance().getItemValueByItemKey(str_strvalue, _itemKey); //取得字符串中的对应的值
		if (str_itemValue == null) {
			return (_nvl == null ? false : _nvl.booleanValue());
		}
		if (str_itemValue.equalsIgnoreCase("Y") || str_itemValue.equalsIgnoreCase("true") || str_itemValue.equalsIgnoreCase("是")) { //
			return true;
		} else {
			return false;
		}
	}

	private String getItemValueByItemKey(String _value, String _itemKey) {
		String[] str_items = tbUtil.split(_value, ";"); //以分号分隔
		for (int i = 0; i < str_items.length; i++) {
			int li_pos = str_items[i].indexOf("="); //
			if (li_pos > 0) { //如果有等于号
				String str_item_key = str_items[i].substring(0, li_pos); //key名
				if (str_item_key.equals(_itemKey)) {
					return str_items[i].substring(li_pos + 1, str_items[i].length()); //value值
				}
			}
		}
		return null;
	}

	/**
	 * 取得所有的配置项返回,客户端可能需要一眼清的看到所有的值!!
	 * @return
	 */
	public static String[][] getAllOptions() {
		HashMap tmpMap = getInstance().getDataMap(); //
		String[] str_allKeys = (String[]) tmpMap.keySet().toArray(new String[0]); //
		String[][] str_return = new String[str_allKeys.length][2]; //
		for (int i = 0; i < str_allKeys.length; i++) {
			str_return[i][0] = str_allKeys[i];
			str_return[i][1] = (String) tmpMap.get(str_allKeys[i]);
		}
		return str_return;
	}

	//获得缓存数据
	public static HashMap getDataMap() {
		return getInstance().dataMap; //
	}

	public static void setDataMap(HashMap _hashMap) {
		getInstance().setDataMapThis(_hashMap);
	}

	private void setDataMapThis(HashMap _hashMap) {
		this.dataMap = _hashMap; //
	}

	/**
	 * 取得实例
	 * @return
	 */
	public static SystemOptions getInstance() {
		if (sysOptions == null) {
			sysOptions = new SystemOptions();
		}
		return sysOptions;
	}

	/**
	 * 将系统所有参数都列在这,曾经想把这个放在安装数据的XML中,即安装时往数据库中插入值,但后来一想也不对,
	 * 因为按道理,作为标准产品,安装时系统中的参数应该都是默认值,既然是默认值,则数据库中一开始应该是一条数据都没有!
	 * 这样一来,感觉还是放在代码里更合理!
	 * @return
	 */
	public static String[][] getAllPlatformOptions() { //
		return new String[][] {
		//主界面风格
				{ "平台_主界面风格", "是否可以注销", "Y", "是否可以注销,重新登陆" }, //
				{ "平台_主界面风格", "首页是否能修改密码", "Y", "" }, //
				{ "平台_主界面风格", "是否可用抽屉风格", "Y", "" }, //	主界面风格
				{ "平台_主界面风格", "是否可用菜单树风格", "Y", "" }, //	主界面风格
				{ "平台_主界面风格", "是否可用皮肤风格", "Y", "" }, //	主界面风格
				{ "平台_主界面风格", "是否显示滚动消息", "Y", "" }, //
				{ "平台_主界面风格", "首页是否支持多种风格", "Y", "" }, //
				{ "平台_主界面风格", "首页是否显示公告消息", "Y", "" }, //
				{ "平台_主界面风格", "首页是否显示任务中心", "Y", "" }, //
				{ "平台_主界面风格", "首页是否显示快捷访问", "Y", "" }, //
				{ "平台_主界面风格", "首页是否显示管理导航图", "Y", "" }, //
				{ "平台_主界面风格", "首页是否隐藏功能菜单树", "N", "" }, //
				{ "平台_主界面风格", "首页标题中快捷按钮排列位置", "居右", "" }, //
				{ "平台_主界面风格", "首页欢迎提示排列位置", "居左", "" }, //
				{ "平台_主界面风格", "首页右边的图片", "rb1.gif=1244;rb2.gif=1456", "" }, //
				{ "平台_主界面风格", "首页相关链接", "搜狐=http://www.sohu.com;网易=http://www.163.com;新浪=http://www.sina.com", "" }, //
				{ "平台_主界面风格", "首页底部说明", "您是第【${TotalCount}】位访问者,共登录【${UserCount}】次", "" }, //
				{ "平台_主界面风格", "是否显示登陆号", "0", "有些系统登陆账号是身份证，不能显示在首页或者日志中，只能显示姓名。默认0为登录号+姓名，1为只显示人员姓名" }, //
				{ "平台_主界面风格", "BOM模板图片是否居左", "N", "" }, //
				{ "平台_主界面风格", "applet登录界面控件的起始位置", "700,279", "" },//这是客户端登录时的参数配置，在LoginPanel 中用到
				{ "平台_主界面风格", "HTML登录界面控件的起始位置", "700,279", "" },//这是浏览器登录时的参数配置，在LoginJSPUtil中用到
				
				{ "平台_主界面风格", "首页我的所有功能名称", "我的所有功能", "" },//
				{ "平台_主界面风格", "系统菜单树宽度", "150", "" },//
				{ "平台_主界面风格", "首页是否必须显示工具条", "N", "" },//
				{ "平台_主界面风格", "首页是否隐藏工具条", "N", "" },//
				{ "平台_主界面风格", "首页是否隐藏标题图片", "N", "" },//
				{ "平台_主界面风格", "超时时间", "", "默认超时不自动关闭系统，设置值单位为秒，600即10分钟" },//
				{ "平台_主界面风格", "打开首页自动调用类", "", "配置类需要实现WLTJobIFC接口，一般不用" },//
				{ "平台_主界面风格", "首页是否显示动态提醒", "N", "2秒远程调用一次查询提醒数据，需要配合定义参数“首页动态提醒自定义类”" },//
				{ "平台_主界面风格", "首页动态提醒自定义类", "", "配置类需要实现RemindIfc接口，需要配合定义参数“首页是否显示动态提醒”，一般不用，可能会影响性能" },//
				{ "平台_主界面风格", "BOM按钮提示模板", "", "配置元原模板，BOM按钮风格时使用，实现Bom按钮的提示功能" },//
				{ "平台_主界面风格", "是否可以修改个人信息", "N", "首页是否可以修改个人信息" },//
				
				
				//工作流
				{ "平台_工作流", "工作流处理页面中是否显示紧急程度", "Y", "会在提交意见框上面多个紧急程度参照" }, //
				{ "平台_工作流", "工作流处理页面中是否显示最后处理期限", "Y", "" }, //
				{ "平台_工作流", "工作流处理页面中是否显示保存按钮", "N", "流程处理界面下面有保存按钮" }, //
				{ "平台_工作流", "工作流中流程结束时是否要执行后台拦截器", "N", "" }, //
				{ "平台_工作流", "工作流处理按钮的面板中是否显示接收按钮", "Y", "" }, //
				{ "平台_工作流", "工作流退回发起人是否可以修改", "N", "" }, //
				{ "平台_工作流", "流程处理中提交时处理意见是否必填", "N", "" }, //
				{ "平台_工作流", "流程处理中退回时处理意见是否必填", "N", "" }, //
				{ "平台_工作流", "工作流是否显示发送邮件选项", "Y", "" }, //
				{ "平台_工作流", "工作流处理页面中处理意见是否显示为最后提交者意见", "N", "" }, //
				{ "平台_工作流", "工作流是否可以修改历史附件", "N", "" }, //
				{ "平台_工作流", "流程历史提交记录中是否显示提交人联系方式", "N", "" }, //
				{ "平台_工作流", "工作流结束时是否选择正常与否", "Y", "" }, //
				{ "平台_工作流", "工作流历史记录中是否屏蔽处理意见", "Y", "" }, //
				{ "平台_工作流", "工作处理是否支持上传附件", "Y", "流程提交时是否可以上传附件" }, //
				{ "平台_工作流", "工作处理意见列表各列顺序", "步骤;处理人;处理人机构;处理时间;处理意见;附件", "有的客户喜欢将处理意见放在前面" },//
				{ "平台_工作流", "工作流查看意见时是否显示加密解密按钮", "Y", "将加密按钮隐藏" }, //
				{ "平台_工作流", "工作流子流程结束时的提示语", "所有会办结束,系统已自动提交给会办发起人", "这个提示语后面还会自动加上实际人员,几乎每个客户都对提示语要调整" }, //
				{ "平台_工作流", "工作流子流程某一分支结束时的提示语", "本次会办已结束", "几乎每个客户都对提示语要调整" }, //

				//千航控件				
				{ "平台_Office控件", "NTKO_Version", "5,0,1,6", "版本号" }, //
				{ "平台_Office控件", "NTKO_MakerCaption", "北京普信管理咨询有限公司", "一般不修改" }, //
				{ "平台_Office控件", "NTKO_MakerKey", "07E148645F62214BF48F7FB0EC9BDBF327EA975D", "一般不修改" }, //
				{ "平台_Office控件", "NTKO_ProductCaption", "普信咨询", "产品标题(应该为客户名称)" },//
				{ "平台_Office控件", "NTKO_ProductKey", "394B5A6B94416B967CD867F5081FBE7C5057E8DD", "产品键值(由产品标题生成的唯一码)" },//
				{ "平台_Office控件", "NTKO_NeedMaker", "Y", "Y-读取MakerCaption和MakerKey的值" }, //
				{ "平台_Office控件", "NTKO_clsid", "C9BC4DFF-4248-4a3c-8A49-63A7D317F404", "不修改" },//
				{ "平台_Office控件", "NTKO_有保存按钮", "Y", "有保存功能" }, //
				{ "平台_Office控件", "NTKO_有关闭按钮", "Y", "有关闭功能" }, //
				{ "平台_Office控件", "NTKO_有打印按钮", "N", "有打印功能" }, { "千航Office文档", "NTKO_有水印按钮", "N", "有水印功能" }, //
				{ "平台_Office控件", "NTKO_有显示批注按钮", "N", "有显示批注功能" },//
				{ "平台_Office控件", "NTKO_有隐藏批注按钮", "N", "有隐藏批注功能" },//
				{ "平台_Office控件", "NTKO_有修订按钮", "N", "有修订,保留痕迹的功能" }, //
				{ "平台_Office控件", "NTKO_有显示痕迹按钮", "N", "有显示痕迹的功能" }, //
				{ "平台_Office控件", "NTKO_有隐藏痕迹按钮", "N", "有隐藏痕迹的功能" }, //
				{ "平台_Office控件", "NTKO_有接受修订按钮", "N", "有接受修订的功能" },

				//其他
				{ "平台_其他", "日历选择的开始年份", "1950", "" }, //
				{ "平台_其他", "日历选择的年份范围", "100", "比如日历选择的开始年份，年份范围分别为1950，100则日历范围为1950-2049" },//
				{ "平台_其他", "邮件配置", "", "例如：192.168.0.10;pushworldgrc@192.168.0.10;1" }, //
				{ "平台_其他", "自定义邮件发送器", "", "继承AbstractSelfDescSendMail的类" }, //
				{ "平台_其他", "DownloadFileFromInterceptUrl", "https://biz1.cmbchina.com/AIOWService/Member/DownloadProvider.aspx", "点击下载的超链接,下载某个文件的路径" }, //
				{ "平台_其他", "导出是否转行列", "N", "" }, 
				{ "平台_其他", "seq_prefix", "100", "平台生成的id加前缀" },
				{ "平台_其他", "帮助文档是否可编辑", "N", "系统默认点击帮助文档为千航打开，不可编辑" },
				
				//报表
				{ "报表", "office控件是否显示导入按钮", "N", "" }, //
		};
	}
}
