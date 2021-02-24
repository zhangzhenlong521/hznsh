/**************************************************************************
 * $RCSfile: WLTConstants.java,v $  $Revision: 1.13 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

/**
 * 该类是整个平台的常量库!!
 * @author xch
 *
 */
public class WLTConstants {
	public static String ORACLE = "ORACLE"; //Oracle数据类型
	public static String SQLSERVER = "SQLSERVER"; //SQLServer
	public static String MYSQL = "MYSQL"; //MYSQL
	public static String DB2 = "DB2"; //MYSQL
	public static byte JEPTYPE_UI = 0;
	public static byte JEPTYPE_BS = 1;

	public static byte LEVELTYPE_LIST = 1;
	public static byte LEVELTYPE_CARD = 2;

	public static int THREAD_OVERTIME_VALUE = 3000; //系统性能超时阀值,单位是毫秒
	public static int THREAD_OVERWEIGHT_VALUE = 204800; //访问超重的阀值,单位是字节,超过200K就警告!!
	public static int THREAD_OVERJVM_VALUE = 2048; //JVM消耗上限,单位是,即超过2048K(2M)就警告,在只有一个用户访问的情况下,这是刚性指标,在大量并发用户访问的情况也能说明这段时间是危险的!!

	public static String SIMPLECHINESE = "SIMPLECHINESE"; //语言,简体中文
	public static String ENGLISH = "ENGLISH"; //语言,英文
	public static String TRADITIONALCHINESE = "TRADITIONALCHINESE"; //语言,繁体中文()

	//所有的控件名称!!目前有15种,以后还会增加!!
	public static String COMP_LABEL = "Label";
	public static String COMP_TEXTFIELD = "文本框";
	public static String COMP_NUMBERFIELD = "数字框";
	public static String COMP_PASSWORDFIELD = "密码框";
	public static String COMP_COMBOBOX = "下拉框";
	public static String COMP_REFPANEL = "参照"; //表型参照1,即直接一个SQL，在第一次就查出所有数据放在元原模板VO中，而不是每一次弹出框时查询!适合数据量少的引用表!
	//public static String COMP_REFPANEL2 = "参照2"; //表型参照2,不在第一次就查出数据，而是每一次点击时查询数据,所以需要再加一个列，然后使用加载公式+编辑公式处理.
	public static String COMP_REFPANEL_TREE = "树型参照";
	//public static String COMP_REFPANEL_TREE2 = "树型参照2";
	public static String COMP_REFPANEL_MULTI = "多选参照"; //定义一个SQL,然后可以多选,然后可以将弹出框中的数据多选后返回!!
	//public static String COMP_REFPANEL_MULTI2 = "多选参照2"; //不是第一次查数据,而是每次点击查询,所以需要使用加载公式+编辑公式处理.
	public static String COMP_REFPANEL_CUST = "自定义参照"; //自定义参照,需要继承于cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog
	public static String COMP_REFPANEL_LISTTEMPLET = "列表模板参照"; //直接是一个BillListPanel
	public static String COMP_REFPANEL_TREETEMPLET = "树型模板参照"; //直接是一个BillTreePanel
	public static String COMP_REFPANEL_REGFORMAT = "注册样板参照"; //通过调用注册的BillFormat面板创建一个参照,返回数据需要通过一个生成器完成!!!
	public static String COMP_REFPANEL_REGEDIT = "注册参照"; //系统注册的参照,非常有用,以后大量参照都需要注册,极大提高开发效率,参照也由专人负责配置与开发!
	public static String COMP_TEXTAREA = "多行文本框";
	public static String COMP_BIGAREA = "大文本框";
	public static String COMP_STYLEAREA = "富文本框"; //即支持粗体,斜体,下划线等效果的文本框!
	public static String COMP_DATE = "日历";
	public static String COMP_FILECHOOSE = "文件选择框"; //可以上传下载文件文件选择框
	public static String COMP_SELFDESC = "自定义控件"; //可以上传下载文件文件选择框
	public static String COMP_COLOR = "颜色";
	public static String COMP_CALCULATE = "计算器";
	public static String COMP_DATETIME = "时间";
	public static String COMP_CHECKBOX = "勾选框";
	public static String COMP_LINKCHILD = "引用子表"; //可以直接打开一个子表进行保存,实际上还是一对多的关系
	public static String COMP_IMPORTCHILD = "导入子表"; //可以直接导入一个子表由主表维护关系分号分隔
	public static String COMP_PICTURE = "图片选择框";
	public static String COMP_IMAGEUPLOAD = "图片上传";  //直接只上传一个图片然后存储在数据库中,然后在卡片中直接渲染!!! 比如HR系统中的人员照片,BOM图形,首页滚动新闻都用到这个控件! 它与上传文件不一样的!上传文件是存储在目录下,而这是存储在数据库中的!!
	public static String COMP_EXCEL = "Excel控件"; //Excel控件
	public static String COMP_BUTTON = "按钮"; //按钮
	public static String COMP_OFFICE = "Office控件"; //Office按钮
	public static String COMP_REGULAR = "正则表达式控件"; //[zzl]

	//常用按钮名字,因为有时遇到一些用户要将"编辑"叫成"修改",将"新建"叫成"新增",所以有必要将这些作为变量,而不能写在各个地方,否则改起来非常麻烦
	public static String BUTTON_TEXT_INSERT = "新增"; //
	public static String BUTTON_TEXT_EDIT = "修改"; //
	public static String BUTTON_TEXT_DELETE = "删除"; //
	public static String BUTTON_TEXT_SAVE = "保存"; //
	public static String BUTTON_TEXT_SEARCH = "查询"; //
	public static String BUTTON_TEXT_BROWSE = "浏览"; //
	public static String BUTTON_TEXT_RETURN = "返回"; //
	public static String BUTTON_TEXT_CANCEL = "取消"; //
	public static String BUTTON_TEXT_SAVE_RETURN = "保存返回"; //
	public static String BUTTON_TEXT_CANCEL_RETURN = "取消返回"; //
	public static String BUTTON_TEXT_RESET = "清空"; //
	public static String BUTTON_TEXT_REFRESH = "刷新"; //
	public static String BUTTON_TEXT_PRINT = "打印"; //这个功能最难实现,也最有价值!一直梦想直接将一个BillCard打印预览出来!!

	// 模板编码,BSUitl工厂实现时引用
	public static final String STRING_TEMPLET_CODE = "TEMPLETCODE";

	// 客户端环境,BSUitl工厂实现时引用
	public static final String STRING_CLIENT_ENVIRONMENT = "CLIENTENV";

	//
	//单据数据编辑状态 ..开始
	//
	public static final String BILLDATAEDITSTATE_INIT = "INIT"; //

	public static final String BILLDATAEDITSTATE_INSERT = "INSERT"; // 

	public static final String BILLDATAEDITSTATE_UPDATE = "UPDATE"; // 

	public static final String BILLDATAEDITSTATE_DELETE = "DELETE"; //

	//
	//	单据数据编辑状态..结束
	//

	//卡片/列表下控件编辑状态设置
	public static final String BILLCOMPENTEDITABLE_ALL = "1"; //全部可编辑

	public static final String BILLCOMPENTEDITABLE_ONLYINSERT = "2"; //只有新增时可编辑

	public static final String BILLCOMPENTEDITABLE_ONLYUPDATE = "3"; //只有修改时可编辑

	public static final String BILLCOMPENTEDITABLE_NONE = "4"; //全部不可编辑!

	// 消息类型
	public static final int MESSAGE_ERROR = 0; //
	public static final int MESSAGE_INFO = 1; //
	public static final int MESSAGE_WARN = 2;
	public static final int MESSAGE_QUESTION = 3; //
	public static final int MESSAGE_CONFIRM = 4;

	// 消息窗口标题
	public static final String MESSAGE_INFO_TITLE = "提示信息";

	public static final String MESSAGE_WARN_TITLE = "Warn";

	public static final String MESSAGE_ERROR_TITLE = "Error";

	public static final String MESSAGE_CONFIRM_TITLE = "Confirm";

	// 常用的提示信息
	public static final String STRING_DEL_CONFIRM = "确定要删除吗?";

	public static final String STRING_DEL_SELECTION_NEED = "请选择一条要删除的记录!";

	public static final String STRING_OPERATION_SUCCESS = "操作成功!";

	public static final String STRING_OPERATION_FAILED = "操作失败!";

	public static final String STRING_CURRENT_USER = "当前用户: ";

	public static final String STRING_LOGIN_TIME = "登录时间: ";

	public static final String STRING_CURRENT_POSITION = "当前位置: ";

	public static final String STRING_CURRENT_POSITION_DIVIDER = " >> ";

	public static final String STRING_REFPANEL_CUSTOMER_TITLE = "自定义面板";

	public static final String STRING_REFPANEL_COMMON_TITLE = "基本信息";

	public static final String STRING_REFPANEL_UIINTERCEPTOR = "UI拦截器";

	public static final String STRING_REFPANEL_BSINTERCEPTOR = "BS拦截器";

	// 时间类型；0表示数据库字段的时间为char型，1表示为date型
	public static final int DATETYPE = 0;

	//上传文件存放目录 
	public static final String UPLOAD_DIRECTORY = "upload";//

	public static final String UPLOAD_FILE_NAME = "filename";

	public static final String UPLOAD_RESULT = "result";
	//参照中不显示列的标记符
	public static final String STRING_REFPANEL_UNSHOWSIGN = "$";

	//用户工位
	public static final String USER_WORKPOSITION = "SYS_WORKPOSITION";

	public static final String SYS_LOGIN = "登录系统";

	public static final String SYS_LOGINOUT = "退出系统";
	
	public static final String MODULE_INSTALL_STATUS_WSQ="未授权";
	public static final String MODULE_INSTALL_STATUS_YAZ="已安装";
	public static final String MODULE_INSTALL_STATUS_KSJ="可升级";
	public static final String MODULE_INSTALL_STATUS_KAZ="可安装";
}

/**************************************************************************
 * $RCSfile: WLTConstants.java,v $  $Revision: 1.13 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: WLTConstants.java,v $
 * Revision 1.13  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:49  Administrator
 * *** empty log message ***
 *
 * Revision 1.12  2011/10/10 06:31:36  wanggang
 * restore
 *
 * Revision 1.10  2011/04/28 10:55:59  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2011/04/19 13:10:12  xch123
 * *** empty log message ***
 *
 * Revision 1.8  2011/04/19 09:12:12  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2011/02/25 12:43:34  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2010/06/07 11:34:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/06/07 06:11:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/05/30 13:49:54  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/05/30 10:16:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/05/20 08:28:08  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/02/23 11:07:32  sunfujun
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/08 11:01:56  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2010/02/04 04:04:14  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/06/11 06:51:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/06/04 10:00:53  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/04/27 01:25:33  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/07/30 02:49:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/07/28 06:48:40  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:38  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.8  2008/05/09 13:38:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2008/05/06 14:04:37  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/05/03 16:05:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/04/21 04:17:54  wangjian
 * *** empty log message ***
 *
 * Revision 1.4  2008/04/12 16:32:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/04/11 01:44:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/16 11:26:39  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:14  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/11/06 03:16:47  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/10/15 10:23:44  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/23 08:03:04  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:08  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/01 02:28:38  sunxf
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:41:28  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
