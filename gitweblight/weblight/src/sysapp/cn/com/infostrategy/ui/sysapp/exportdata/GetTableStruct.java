/**************************************************************************
 * $RCSfile: GetTableStruct.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.exportdata;

public class GetTableStruct {

	/**
	 * 根据表明返回该表的结构
	 * 
	 * @param _tablename
	 * @return
	 */
	public String getPub_ByTableName(String _tablename) {
		if (_tablename.equalsIgnoreCase("pub_templet_1")) { // 元原模板主表
			return getPub_Templete();
		} else if (_tablename.equalsIgnoreCase("pub_templet_1_item")) { // 元原模板子表
			return getPub_TempleteItem();
		} else if (_tablename.equalsIgnoreCase("pub_menu")) { // 系统菜单
			return getPub_Menu();
		} else if (_tablename.equalsIgnoreCase("pub_user")) { //用户表
			return getPub_User();
		} else if (_tablename.equalsIgnoreCase("pub_querytemplet")) { // 查询模板主表
			return getPub_QueryTemplete();
		} else if (_tablename.equalsIgnoreCase("pub_querytemplet_item")) { // 查询模板子表
			return getPub_QueryTempleteItem();
		} else if (_tablename.equalsIgnoreCase("pub_comboboxdict")) { // 下拉框字典
			return getPub_Combox();
		} else if (_tablename.equalsIgnoreCase("pub_fileupload")) { // 上传文件日志表
			return getPub_FileUpload();
		} else if (_tablename.equalsIgnoreCase("pub_formulafunctions")) { // 用户自定义公式
			return getPub_FormulaFn();
		} else if (_tablename.equalsIgnoreCase("pub_news")) { // 系统公告栏
			return getPub_News();
		} else if (_tablename.equalsIgnoreCase("pub_task_commit")) { // 已提交任务
			return getPub_Task_Commit();
		} else if (_tablename.equalsIgnoreCase("pub_task_deal")) { // 待处理任务
			return getPub_Task_Deal();
		} else if (_tablename.equalsIgnoreCase("pub_clusterhost")) { // 集群信息表
			return getPub_Clusterhost();
		} else if (_tablename.equalsIgnoreCase("pub_table_info")) { // 系统约束
			return getPub_Table_Info();
		} else if (_tablename.equalsIgnoreCase("pub_table_indexes")) { // 系统索引
			return getPub_Table_Indexs();
		} else if (_tablename.equalsIgnoreCase("vi_menu")) { // 系统索引
			return getVI_Menu();
		} else if (_tablename.equalsIgnoreCase("db_table")) { // 系统索引
			return getDB_Table();
		} else if (_tablename.equalsIgnoreCase("db_view")) { // 系统索引
			return getDB_View();
		} else if (_tablename.equalsIgnoreCase("db_table_field")) { // 系统索引
			return getDB_Table_Field();
		}
		return null;
	}

	/**
	 * 返回模板编码的表结构
	 * 
	 * @return
	 */
	public String getPub_Templete() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_templet_1; \r\n"); //
		sb_sql.append("create sequence s_pub_templet_1; \r\n"); //

		sb_sql.append(" \r\n");
		sb_sql.append(" \r\n");

		sb_sql.append("--元原模板表\r\n");
		sb_sql.append("drop table pub_templet_1 cascade constraints; \r\n"); //
		sb_sql.append("create table pub_templet_1 \r\n");
		sb_sql.append("( \r\n"); //
		sb_sql.append("  pk_pub_templet_1             number primary key, \r\n"); //
		sb_sql.append("  templetcode                  varchar2(100) not null,  --模板编码 \r\n"); //
		sb_sql.append("  templetname                  varchar2(200) not null,  --模板名称 \r\n"); //
		sb_sql.append("  datasourcename               varchar2(100),  --数据源名称 \r\n"); //
		sb_sql.append("  tablename                    varchar2(100),  --查询数据的表名 \r\n"); //
		sb_sql.append("  dataconstraint               varchar2(1000),  --数据权限过滤条件 \r\n"); //
		sb_sql.append("  pkname                       varchar2(50),  --主键, \r\n"); //
		sb_sql.append("  pksequencename               varchar2(50),  --主键对应的序列名 \r\n"); //
		sb_sql.append("  savedtablename               varchar2(30),  --保存的表名 \r\n"); //
		sb_sql.append("  cardcustpanel                varchar2(100),  --卡片自定义面板 \r\n");
		sb_sql.append("  listcustpanel                varchar2(100),  --列表自定义面板 \r\n");
		sb_sql.append("  constraint ui_pub_templet_1_1 unique(templetcode) \r\n");
		sb_sql.append("); \r\n");
		sb_sql.append(" \r\n");
		sb_sql.append("comment on table pub_templet_1 is '模板编码表'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pk_pub_templet_1 is '主键'; \r\n");
		sb_sql.append("comment on column pub_templet_1.templetcode is '模板编码'; \r\n");
		sb_sql.append("comment on column pub_templet_1.templetname is '模板名称'; \r\n");
		sb_sql.append("comment on column pub_templet_1.datasourcename is '数据源名称'; \r\n");
		sb_sql.append("comment on column pub_templet_1.tablename is '查询数据的表名'; \r\n");
		sb_sql.append("comment on column pub_templet_1.dataconstraint is '数据权限过滤条件'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pkname is '主键'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pksequencename is '主键对应的序列名'; \r\n");
		sb_sql.append("comment on column pub_templet_1.savedtablename is '保存的表名'; \r\n");
		sb_sql.append("comment on column pub_templet_1.cardcustpanel is '卡片自定义面板'; \r\n");
		sb_sql.append("comment on column pub_templet_1.listcustpanel is '列表自定义面板'; \r\n");
		sb_sql.append(" \r\n");
		sb_sql.append(" \r\n");

		return sb_sql.toString();
	}

	/**
	 * 返回模板编码元素的表结构
	 * 
	 * @return
	 */
	public String getPub_TempleteItem() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_templet_1_item; \r\n"); //
		sb_sql.append("create sequence s_pub_templet_1_item; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--元原模板子表\r\n");
		sb_sql.append("drop table pub_templet_1_item cascade constraints; \r\n");
		sb_sql.append("create table pub_templet_1_item \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_templet_1_item            number primary key,  --主键 \r\n");
		sb_sql.append("  pk_pub_templet_1                 number not null,  --父表主键 \r\n");
		sb_sql.append("  itemkey                          varchar2(100) not null,  --唯一标识 \r\n");
		sb_sql.append("  itemname                         varchar2(100) not null,  --显示名称 \r\n");
		sb_sql.append("  itemtype                         varchar2(50)  not null,  --控件类型 \r\n");
		sb_sql.append("  comboxdesc                       varchar2(2500),  --下拉框定义 \r\n");
		sb_sql.append("  refdesc                          varchar2(2500),   --参照定义 \r\n");
		sb_sql.append("  issave                           char(1) default 'Y' not null,  --是否保存 \r\n");
		sb_sql.append("  isdefaultquery                   char(1) default '2' not null,  --默认查询条件 \r\n");
		sb_sql.append("  ismustinput                      char(1) default 'N' not null,  --是否必输项 \r\n");
		sb_sql.append("  loadformula                      varchar2(2500),  --加载/显示公式 \r\n");
		sb_sql.append("  editformula                      varchar2(2500),  --编辑公式 \r\n");
		sb_sql.append("  defaultvalueformula              varchar2(1000),  --默认值公式 \r\n");
		sb_sql.append("  showorder                        number,  --显示顺序 \r\n");
		sb_sql.append("  listwidth                        number,  --列表显示宽度 \r\n");
		sb_sql.append("  cardwidth                        number,  --卡片显示宽度 \r\n");
		sb_sql.append("  listisshowable                   char(1) default 'Y' not null,  --列表是否显示 \r\n");
		sb_sql.append("  listiseditable                   char(1) default '1' not null,  --列表是否可编辑 \r\n");
		sb_sql.append("  cardisshowable                   char(1) default 'Y' not null,  --卡片是否显示 \r\n");
		sb_sql.append("  cardiseditable                   char(1) default '1' not null,  --卡片是否可编辑 \r\n");
		sb_sql.append("  constraint ui_pub_templet_1_item_1 unique(pk_pub_templet_1,itemkey),  --唯一性约束!!!非常重要! \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_1 check (issave in('Y','N')),  --是否保存约束 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_2 check (isdefaultquery in('1','2','3')),  --参与默认查询条件约束 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_3 check (ismustinput in('Y','N')),  --是否必须项 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_4 check (listisshowable in('Y','N')),  --列表是否显示 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_5 check (listiseditable in('1','2','3','4')),  --列表是否可编辑 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_6 check (cardisshowable in('Y','N')),  --卡片是否显示 \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_7 check (cardiseditable in('1','2','3','4'))  --卡片是否可编辑 \r\n");
		sb_sql.append("); \r\n");
		sb_sql.append("\r\n");
		sb_sql.append("comment on table pub_templet_1_item is '模板元素编码表'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.pk_pub_templet_1_item is '主键'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.pk_pub_templet_1 is '父表主键'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemkey is '唯一标识'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemname is '显示名称'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemtype is '控件类型'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.comboxdesc is '下拉框说明'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.refdesc is '参照说明'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.issave is '是否参与保存'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.isdefaultquery is '默认查询条件'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.ismustinput is '是否必输项'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.loadformula is '加载/显示公式'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.editformula is '编辑公式'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.defaultvalueformula is '默认值公式'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.showorder is '显示顺序'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listwidth is '列表宽度'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardwidth is '卡片宽度'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listisshowable is '列表时是否可显示'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listiseditable is '列表时是否可编辑'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardisshowable is '卡片时是否可显示'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardisshowable is '卡片时是否可编辑'; \r\n");
		sb_sql.append("\r\n");
		sb_sql.append("create index in_pub_templet_1_item_1 on pub_templet_1_item (PK_PUB_TEMPLET_1);");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	/**
	 * 返回平台菜单的表结构
	 * 
	 * @return
	 */
	public String getPub_Menu() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_menu; \r\n"); //
		sb_sql.append("create sequence s_pub_menu; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--系统菜单表\r\n");
		sb_sql.append("drop table pub_menu cascade constraints; \r\n");
		sb_sql.append("create table pub_menu \r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  id                      number(18) primary key, --主键 \r\n");
		sb_sql.append("  name                    varchar2(64) not null, --名称 \r\n");
		sb_sql.append("  localname               varchar2(64)  not null, --中文名 \r\n");
		sb_sql.append("  parentmenuid            number(18), --父菜单ID \r\n");
		sb_sql.append("  seq                     number(5), --序列号 \r\n");
		sb_sql.append("  commandtype             number(2), --命令类型 \r\n");
		sb_sql.append("  command                 varchar2(2500), --命令内容 \r\n");
		sb_sql.append("  showintoolbar           char(1) default 'Y' CHECK(showintoolbar IN ('Y','N')) not null, --是否在工具栏显示 \r\n");
		sb_sql.append("  icon                    varchar2(100), --图标 \r\n");
		sb_sql.append("  constraint ui_pub_menu_1 unique(name)  --唯一性约束 \r\n");
		sb_sql.append("); \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_menu is '系统菜单表';\r\n");
		sb_sql.append("comment on column pub_menu.id is '主键';\r\n");
		sb_sql.append("comment on column pub_menu.name is '名称';\r\n");
		sb_sql.append("comment on column pub_menu.localname is '中文名';\r\n");
		sb_sql.append("comment on column pub_menu.parentmenuid is '父菜单ID';\r\n");
		sb_sql.append("comment on column pub_menu.seq is '序列号';\r\n");
		sb_sql.append("comment on column pub_menu.commandtype is '命令类型';\r\n");
		sb_sql.append("comment on column pub_menu.command is '命令内容';\r\n");
		sb_sql.append("comment on column pub_menu.showintoolbar is '是否在工具栏显示';\r\n");
		sb_sql.append("comment on column pub_menu.icon is '图标';\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	private String getPub_User() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_user; \r\n"); //
		sb_sql.append("create sequence s_pub_user; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--系统用户表\r\n");
		sb_sql.append("drop table pub_user cascade constraints; \r\n");
		sb_sql.append("create table pub_user \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append(" id               number not null,    --主键 \r\n");
		sb_sql.append(" code             varchar2(20),  --编码 \r\n");
		sb_sql.append(" name             varchar2(50),  --名称 \r\n");
		sb_sql.append(" pwd              varchar2(20),  --密码 \r\n");
		sb_sql.append(" adminpwd         varchar2(20),  --管理密码 \r\n");
		sb_sql.append(" creator          varchar2(20),  --创建者 \r\n");
		sb_sql.append(" createdate       date,  --创建日期 \r\n");
		sb_sql.append(" telephone        varchar2(20),  --联系电话 \r\n");
		sb_sql.append(" mobile           varchar2(20),  --手机 \r\n");
		sb_sql.append(" email            varchar2(30),  --邮件 \r\n");
		sb_sql.append(" regionid         number(18),  --所属区域 \r\n");
		sb_sql.append(" regioncode       varchar2(50),  --所属区域编码 \r\n");
		sb_sql.append(" constraint pk_pub_user_id primary key(id),  --设置主键约束 \r\n");
		sb_sql.append(" constraint uk_pub_user_code unique(code)  --编码不能重复 \r\n");
		sb_sql.append("); \r\n\r\n");

		sb_sql.append("comment on table  pub_user is '系统人员表';\r\n");
		sb_sql.append("comment on column pub_user.id is '主键';\r\n");
		sb_sql.append("comment on column pub_user.code is '主键';\r\n");
		sb_sql.append("comment on column pub_user.pwd is '密码';\r\n");
		sb_sql.append("comment on column pub_user.adminpwd is '管理密码';\r\n");
		sb_sql.append("comment on column pub_user.creator is '创建者';\r\n");
		sb_sql.append("comment on column pub_user.createdate is '创建日期';\r\n");
		sb_sql.append("comment on column pub_user.telephone is '电话';\r\n");
		sb_sql.append("comment on column pub_user.mobile is '手机';\r\n");
		sb_sql.append("comment on column pub_user.email is '邮件';\r\n");
		sb_sql.append("comment on column pub_user.regionid is '所属区域';\r\n");
		sb_sql.append("comment on column pub_user.regioncode is '所属区域编码';\r\n");
		
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'gxlu','gxlu','1','1','471'); \r\n"); //
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'admin','admin','1','1','471'); \r\n"); //
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'guest','guest','1','1','471'); \r\n"); //
		sb_sql.append("commit; \r\n");

		return sb_sql.toString(); //
	}

	/**
	 * 返回查询模板编码的表结构
	 * 
	 * @return
	 */
	public String getPub_QueryTemplete() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_querytemplet;\r\n"); //
		sb_sql.append("create sequence s_pub_querytemplet;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--查询模板表\r\n");
		sb_sql.append("drop   table pub_querytemplet cascade constraints;\r\n");
		sb_sql.append("create table pub_querytemplet\r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  pk_pub_querytemplet          number primary key not null,\r\n");
		sb_sql.append("  templetcode                  varchar2(100) not null,  --模板编码\r\n");
		sb_sql.append("  templetname                  varchar2(200) not null,  --模板名称\r\n");
		sb_sql.append("  sql                          varchar2(2500),  --查询数据SQL\r\n");
		sb_sql.append("  constraint ui_pub_querytemplet_1 unique(templetcode)  --唯一性约束 \r\n"); //
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_querytemplet is '查询模板表';\r\n");
		sb_sql.append("comment on column pub_querytemplet.pk_pub_querytemplet is '主键';\r\n");
		sb_sql.append("comment on column pub_querytemplet.templetcode is '模板编码';\r\n");
		sb_sql.append("comment on column pub_querytemplet.templetname is '模板名称';\r\n");
		sb_sql.append("comment on column pub_querytemplet.sql is 'SQL语句';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * 返回查询模板编码元素的表结构
	 * 
	 * @return
	 */
	public String getPub_QueryTempleteItem() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_querytemplet_item;\r\n");
		sb_sql.append("create sequence s_pub_querytemplet_item;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--查询模板子表\r\n");
		sb_sql.append("drop   table pub_querytemplet_item cascade constraints;\r\n");
		sb_sql.append("create table pub_querytemplet_item\r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  pk_pub_querytemplet_item       number primary key not null, --主键\r\n");
		sb_sql.append("  pk_pub_querytemplet            number not null,  --父表主键\r\n");
		sb_sql.append("  itemkey                        varchar2(100) not null,  --唯一标识\r\n");
		sb_sql.append("  itemname                       varchar2(100) not null,  --显示名称\r\n");
		sb_sql.append("  itemtype                       varchar2(50),  --控件类型\r\n");
		sb_sql.append("  comboxdesc                     varchar2(2500),  --下拉框说明\r\n");
		sb_sql.append("  refdesc                        varchar2(2500),  --参照说明\r\n");
		sb_sql.append("  showorder                      number,  --显示顺序\r\n");
		sb_sql.append("  constraint ui_pub_querytemplet_item_1 unique(pk_pub_querytemplet,itemkey)  --唯一性约束 \r\n"); //
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_querytemplet_item is '查询模板子表';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.pk_pub_querytemplet_item is '主键';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.pk_pub_querytemplet is '主表主键';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemkey is '唯一标识';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemname is '显示名称';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemtype is '控件类型';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.comboxdesc is '下拉框说明';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.refdesc is '参照说明';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.refdesc is '显示顺序';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * 返回下拉框数据字典的表结构
	 * 
	 * @return
	 */
	public String getPub_Combox() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_comboboxdict;\r\n");
		sb_sql.append("create sequence s_pub_comboboxdict;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("drop   table pub_comboboxdict cascade constraints;\r\n");
		sb_sql.append("create table pub_comboboxdict\r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  pk_pub_comboboxdict    number primary key,  --主键\r\n");
		sb_sql.append("  type  varchar2(100) not null,  --类型\r\n");
		sb_sql.append("  id    varchar2(30) not null,  --数据ID\r\n");
		sb_sql.append("  code  varchar2(100),  --数据编码\r\n");
		sb_sql.append("  name  varchar2(100),  --数据名称\r\n");
		sb_sql.append("  descr varchar2(500),  --数据说明\r\n");
		sb_sql.append("  seq   number,            --排序\r\n");
		sb_sql.append("  constraint ui_pub_comboboxdict_1 unique(type,id)\r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table  pub_comboboxdict is '下拉框数据字典表';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.pk_pub_comboboxdict is '主键';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.type is '类型';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.id is '数据ID';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.code is '数据编码';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.name is '数据名称';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.descr is '数据说明';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.seq is '排序';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("create index in_pub_ComboBoxdict on pub_ComboBoxdict (type);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("delete from pub_ComboBoxdict;\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'性别','M','Man','男',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'性别','W','Women','女',null,2);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','1','1','初中',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','2','2','高中',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','3','3','中专',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','4','4','大专',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','5','5','本科',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','6','6','硕士',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'学历','7','7','博士',null,7);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','文本框','001','文本框',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','数字框','002','数字框',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','密码框','003','密码框',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','下拉框','004','下拉框',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','参照','005','参照',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','多行文本框','006','多行文本框',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','日历','007','日历',null,7);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','颜色','008','颜色',null,8);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','计算器','009','计算器',null,9);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','文件选择框','010','文件选择框',null,10);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','时间','011','时间',null,11);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','大文本框','012','大文本框',null,12);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','勾选框','013','勾选框',null,13);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'平台控件类型','图片选择框','014','图片选择框',null,14);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','0','0','自定义Frame',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','11','11','1单表列',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','12','12','2单表列/卡',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','13','13','3单表树/卡',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','14','14','4双表树列',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','15','15','5双表树列/卡',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','16','16','6双表列列',null,7);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','17','17','7双表列列/卡',null,8);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','18','18','8主子表',null,9);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','19','19','9多子表',null,10);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'菜单路径','20','20','10主子孙表',null,11);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'参与查询条件类型','1','001','快速查询',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'参与查询条件类型','2','002','通用查询',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'参与查询条件类型','3','003','不允许查询',null,3);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'控件编辑属性设置','1','001','全部可编辑',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'控件编辑属性设置','2','002','仅新增可编辑',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'控件编辑属性设置','3','003','仅修改可编辑',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'控件编辑属性设置','4','004','全部禁用',null,4);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("commit;\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * 返回文件上传管理的表结构
	 * 
	 * @return
	 */
	public String getPub_FileUpload() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_fileupload;\r\n");
		sb_sql.append("create sequence s_pub_fileupload;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--上传文件日志记录表,为文件上传控件用\r\n");
		sb_sql.append("drop   table pub_fileupload cascade constraints;\r\n");
		sb_sql.append("create table pub_fileupload \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_fileupload  number primary key,  --主键,对应序列 \r\n");
		sb_sql.append("  oldfilename  varchar2(100),  --文件原名,它也是参照上显示的名称 \r\n");
		sb_sql.append("  newfilename  varchar2(100),  --文件新名,即放在tomcat上的文件名,它是由序列加上oldfilename拼成的!!它同时也是应用表字段存储的真正内容 \r\n");
		sb_sql.append("  tablename    varchar2(30),   --该记录被哪个表引用 \r\n");
		sb_sql.append("  fieldname    varchar2(30)    --该记录被哪个表的哪个字段引用 \r\n");
		sb_sql.append("); \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_fileupload is '上传/下载文件的存储表'; \r\n");
		sb_sql.append("comment on column pub_fileupload.pk_pub_fileupload is '主键,对应序列'; \r\n");
		sb_sql.append("comment on column pub_fileupload.oldfilename is '文件原名,它也是参照上显示的名称'; \r\n");
		sb_sql.append("comment on column pub_fileupload.newfilename is '文件新名,即放在tomcat上的文件名'; \r\n");
		sb_sql.append("comment on column pub_fileupload.tablename is '该记录被哪个表引用'; \r\n");
		sb_sql.append("comment on column pub_fileupload.fieldname is '该记录被哪个表的哪个字段引用'; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	/**
	 * 返回用户自定义公式的表结构
	 * 
	 * @return
	 */
	public String getPub_FormulaFn() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_formulafunctions;\r\n");
		sb_sql.append("create sequence s_pub_formulafunctions;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--用户自定义函数表,即用户可以自定义函数,然后在公式中使用\r\n");
		sb_sql.append("drop   table pub_formulafunctions cascade constraints;\r\n");
		sb_sql.append("create table pub_formulafunctions \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_formulafunctions number primary key,  --主键\r\n");
		sb_sql.append("  functionname            varchar2(50) not null,  --函数名,比如'toString()'\r\n");
		sb_sql.append("  parcount                number(3,0),  --参数个数,比如1\r\n");
		sb_sql.append("  classname               varchar2(200)  not null,  --实现的类名\r\n");
		sb_sql.append("  formulaformat           varchar2(500)  not null, --使用说明\r\n");
		sb_sql.append("  descr                   varchar2(500),  --备注说明\r\n");
		sb_sql.append("  demo                    varchar2(500)  --示例\r\n");
		sb_sql.append(");");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_formulafunctions is '用户自定义函数表';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.pk_pub_formulafunctions is '主键';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.functionname is '函数名,比如toString()';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.parcount is '参数个数,比如1';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.classname is '实现的类名';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.formulaformat is '使用说明';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.descr is '说明';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.demo is '示例';\r\n");

		return sb_sql.toString();
	}

	public String getPub_News() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("drop   sequence s_pub_news; \r\n");
		sb_sql.append("create sequence s_pub_news; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--主窗口系统公告\r\n");
		sb_sql.append("drop   table pub_news cascade constraints; \r\n");
		sb_sql.append("create table pub_news \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_news                 number primary key not null, --主键 \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --标题 \r\n");
		sb_sql.append("  content                     varchar2(2500),  --内容 \r\n");
		sb_sql.append("  isscrollview                char(1) default 'N' check (isscrollview in ('Y','N')) not null,  --是否滚动 \r\n");
		sb_sql.append("  createtime                  date not null,  --创建日期 \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --创建者 \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	public String getPub_Task_Commit() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("drop   sequence s_pub_task_commit; \r\n");
		sb_sql.append("create sequence s_pub_task_commit; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--主窗口已提交任务\r\n");
		sb_sql.append("drop   table pub_task_commit cascade constraints; \r\n");
		sb_sql.append("create table pub_task_commit \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_task_commit          number primary key not null, --主键 \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --标题 \r\n");
		sb_sql.append("  billtype                    varchar2(20),  --单据类型 \r\n");
		sb_sql.append("  billid                      varchar2(20),  --单据ID \r\n");
		sb_sql.append("  billcode                    varchar2(20),  --单据编码 \r\n");
		sb_sql.append("  createtime                  date not null, --创建日期 \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --创建者 \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	public String getPub_Task_Deal() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("drop   sequence s_pub_task_deal; \r\n");
		sb_sql.append("create sequence s_pub_task_deal; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--主窗口待处理任务\r\n");
		sb_sql.append("drop   table pub_task_deal cascade constraints; \r\n");
		sb_sql.append("create table pub_task_deal \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_task_deal            number primary key not null, --主键 \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --标题 \r\n");
		sb_sql.append("  billtype                    varchar2(20),  --单据类型 \r\n");
		sb_sql.append("  billid                      varchar2(20),  --单据ID \r\n");
		sb_sql.append("  billcode                    varchar2(20),  --单据编码 \r\n");
		sb_sql.append("  createtime                  date not null, --创建日期 \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --创建者 \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	public String getPub_Clusterhost() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--集群信息表\r\n");
		sb_sql.append("drop   table pub_clusterhost cascade constraints; \r\n");
		sb_sql.append("create table pub_clusterhost \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  ip varchar2(15),  --IP地址 \r\n");
		sb_sql.append("  port varchar2(8),  --端口,  --标题 \r\n");
		sb_sql.append("  count number   --在线人数 \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("comment on table pub_clusterhost is '集群信息表';\r\n");
		sb_sql.append("comment on column pub_clusterhost.ip is 'IP地址';\r\n");
		sb_sql.append("comment on column pub_clusterhost.port is '端口';\r\n");
		sb_sql.append("comment on column pub_clusterhost.count is '在线人数';\r\n");

		sb_sql.append("\r\n"); //
		sb_sql.append("\r\n"); //

		return sb_sql.toString();
	}

	public String getPub_Table_Info() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("drop   sequence s_pub_table_info; \r\n");
		sb_sql.append("create sequence s_pub_table_info; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--系统约束表\r\n");
		sb_sql.append("drop   table pub_table_info cascade constraints; \r\n");
		sb_sql.append("create table pub_table_info \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  constraint_name            varchar2(30) not null,  --约束名 \r\n");
		sb_sql.append("  constraint_type            varchar2(1),  --约束类型 \r\n");
		sb_sql.append("  column_name                varchar2(4000),  --列名 \r\n");
		sb_sql.append("  r_column                   varchar2(4000),  --外键名 \r\n");
		sb_sql.append("  table_name                 varchar2(30) not null,  --列名 \r\n");
		sb_sql.append("  r_table                    varchar2(30),  --关联表 \r\n");
		sb_sql.append("  r_constraint_name          varchar2(30),  --关联的列名 \r\n");
		sb_sql.append("  last_change                date  --最后修改日期 \r\n");
		sb_sql.append("); \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	public String getPub_Table_Indexs() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_table_indexes; \r\n");
		sb_sql.append("create sequence s_pub_table_indexes; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--系统索引表\r\n");
		sb_sql.append("drop   table pub_table_indexes cascade constraints; \r\n");
		sb_sql.append("create table pub_table_indexes \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  index_name             varchar2(30) not null, --索引名 \r\n");
		sb_sql.append("  index_type             varchar2(27), --索引类型 \r\n");
		sb_sql.append("  column_name            varchar2(4000),  --列名\r\n");
		sb_sql.append("  table_name             varchar2(30) not null,  --列名\r\n");
		sb_sql.append("  compression            varchar2(8),  --描述\r\n");
		sb_sql.append("  pct_free               number,  --\r\n");
		sb_sql.append("  ini_trans              number, --\r\n");
		sb_sql.append("  max_trans              number,  --\r\n");
		sb_sql.append("  last_analyzed          date  --最后解析日期\r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	public String getDB_Table() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create or replace view DB_TABLE as \r\n");
		sb_sql.append("Select tab.tname,COMMENTS \r\n");
		sb_sql.append("From tab left join user_TAB_COMMENTS on tab.tname = table_name \r\n");
		sb_sql.append("where tabtype='TABLE' \r\n");
		sb_sql.append("/\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	public String getDB_View() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create or replace view DB_TABLE as \r\n");
		sb_sql.append("Select tab.tname,COMMENTS \r\n");
		sb_sql.append("From tab left join user_TAB_COMMENTS on tab.tname = table_name \r\n");
		sb_sql.append("where tabtype='VIEW' \r\n");
		sb_sql.append("/\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	public String getDB_Table_Field() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create or replace view DB_TABLE_FIELD as \r\n");
		sb_sql.append("Select cols.TABLE_NAME,cols.COLUMN_NAME,DATA_TYPE,DATA_LENGTH, \r\n");
		sb_sql.append("DATA_PRECISION,NULLABLE,COLUMN_ID,LAST_ANALYZED,COMMENTS \r\n");
		sb_sql.append("From cols left join user_col_COMMENTS on \r\n");
		sb_sql.append("(cols.COLUMN_NAME = user_col_COMMENTS.COLUMN_NAME \r\n");
		sb_sql.append("And cols.TABLE_NAME = user_col_COMMENTS.TABLE_NAME)\r\n");
		sb_sql.append("/\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	public String getVI_Menu() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create or replace view VI_MENU as Select \r\n");
		sb_sql.append("ID, \r\n");
		sb_sql.append("NAME, \r\n");
		sb_sql.append("LOCALNAME, \r\n");
		sb_sql.append("PARENTMENUID, \r\n");
		sb_sql.append("(select LOCALNAME from pub_menu where id=a.PARENTMENUID) PARENTMENUID_name, \r\n");
		sb_sql.append("SEQ, \r\n");
		sb_sql.append("COMMANDTYPE, \r\n");
		sb_sql.append("COMMAND, \r\n");
		sb_sql.append("SHOWINTOOLBAR, \r\n");
		sb_sql.append("ICON \r\n");
		sb_sql.append("from pub_menu a \r\n");
		sb_sql.append("/\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}
}
/**************************************************************************
 * $RCSfile: GetTableStruct.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 *
 * $Log: GetTableStruct.java,v $
 * Revision 1.4  2012/09/14 09:19:33  xch123
 * 邮储现场回来统一更新
 *
 * Revision 1.1  2012/08/28 09:41:13  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:32:11  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:05  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/13 08:06:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:48  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:41  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:09  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:23  xch
 * *** empty log message ***
 *
 * Revision 1.8  2007/03/15 06:29:37  shxch
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/09 10:07:08  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/09 10:02:11  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/09 09:33:02  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/02 07:54:29  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/02 07:49:19  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/02 07:43:58  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/30 07:41:11  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:00:01  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
