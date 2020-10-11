/**************************************************************************
 * $RCSfile: GetTableStruct.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.exportdata;

public class GetTableStruct {

	/**
	 * ���ݱ������ظñ�Ľṹ
	 * 
	 * @param _tablename
	 * @return
	 */
	public String getPub_ByTableName(String _tablename) {
		if (_tablename.equalsIgnoreCase("pub_templet_1")) { // Ԫԭģ������
			return getPub_Templete();
		} else if (_tablename.equalsIgnoreCase("pub_templet_1_item")) { // Ԫԭģ���ӱ�
			return getPub_TempleteItem();
		} else if (_tablename.equalsIgnoreCase("pub_menu")) { // ϵͳ�˵�
			return getPub_Menu();
		} else if (_tablename.equalsIgnoreCase("pub_user")) { //�û���
			return getPub_User();
		} else if (_tablename.equalsIgnoreCase("pub_querytemplet")) { // ��ѯģ������
			return getPub_QueryTemplete();
		} else if (_tablename.equalsIgnoreCase("pub_querytemplet_item")) { // ��ѯģ���ӱ�
			return getPub_QueryTempleteItem();
		} else if (_tablename.equalsIgnoreCase("pub_comboboxdict")) { // �������ֵ�
			return getPub_Combox();
		} else if (_tablename.equalsIgnoreCase("pub_fileupload")) { // �ϴ��ļ���־��
			return getPub_FileUpload();
		} else if (_tablename.equalsIgnoreCase("pub_formulafunctions")) { // �û��Զ��幫ʽ
			return getPub_FormulaFn();
		} else if (_tablename.equalsIgnoreCase("pub_news")) { // ϵͳ������
			return getPub_News();
		} else if (_tablename.equalsIgnoreCase("pub_task_commit")) { // ���ύ����
			return getPub_Task_Commit();
		} else if (_tablename.equalsIgnoreCase("pub_task_deal")) { // ����������
			return getPub_Task_Deal();
		} else if (_tablename.equalsIgnoreCase("pub_clusterhost")) { // ��Ⱥ��Ϣ��
			return getPub_Clusterhost();
		} else if (_tablename.equalsIgnoreCase("pub_table_info")) { // ϵͳԼ��
			return getPub_Table_Info();
		} else if (_tablename.equalsIgnoreCase("pub_table_indexes")) { // ϵͳ����
			return getPub_Table_Indexs();
		} else if (_tablename.equalsIgnoreCase("vi_menu")) { // ϵͳ����
			return getVI_Menu();
		} else if (_tablename.equalsIgnoreCase("db_table")) { // ϵͳ����
			return getDB_Table();
		} else if (_tablename.equalsIgnoreCase("db_view")) { // ϵͳ����
			return getDB_View();
		} else if (_tablename.equalsIgnoreCase("db_table_field")) { // ϵͳ����
			return getDB_Table_Field();
		}
		return null;
	}

	/**
	 * ����ģ�����ı�ṹ
	 * 
	 * @return
	 */
	public String getPub_Templete() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_templet_1; \r\n"); //
		sb_sql.append("create sequence s_pub_templet_1; \r\n"); //

		sb_sql.append(" \r\n");
		sb_sql.append(" \r\n");

		sb_sql.append("--Ԫԭģ���\r\n");
		sb_sql.append("drop table pub_templet_1 cascade constraints; \r\n"); //
		sb_sql.append("create table pub_templet_1 \r\n");
		sb_sql.append("( \r\n"); //
		sb_sql.append("  pk_pub_templet_1             number primary key, \r\n"); //
		sb_sql.append("  templetcode                  varchar2(100) not null,  --ģ����� \r\n"); //
		sb_sql.append("  templetname                  varchar2(200) not null,  --ģ������ \r\n"); //
		sb_sql.append("  datasourcename               varchar2(100),  --����Դ���� \r\n"); //
		sb_sql.append("  tablename                    varchar2(100),  --��ѯ���ݵı��� \r\n"); //
		sb_sql.append("  dataconstraint               varchar2(1000),  --����Ȩ�޹������� \r\n"); //
		sb_sql.append("  pkname                       varchar2(50),  --����, \r\n"); //
		sb_sql.append("  pksequencename               varchar2(50),  --������Ӧ�������� \r\n"); //
		sb_sql.append("  savedtablename               varchar2(30),  --����ı��� \r\n"); //
		sb_sql.append("  cardcustpanel                varchar2(100),  --��Ƭ�Զ������ \r\n");
		sb_sql.append("  listcustpanel                varchar2(100),  --�б��Զ������ \r\n");
		sb_sql.append("  constraint ui_pub_templet_1_1 unique(templetcode) \r\n");
		sb_sql.append("); \r\n");
		sb_sql.append(" \r\n");
		sb_sql.append("comment on table pub_templet_1 is 'ģ������'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pk_pub_templet_1 is '����'; \r\n");
		sb_sql.append("comment on column pub_templet_1.templetcode is 'ģ�����'; \r\n");
		sb_sql.append("comment on column pub_templet_1.templetname is 'ģ������'; \r\n");
		sb_sql.append("comment on column pub_templet_1.datasourcename is '����Դ����'; \r\n");
		sb_sql.append("comment on column pub_templet_1.tablename is '��ѯ���ݵı���'; \r\n");
		sb_sql.append("comment on column pub_templet_1.dataconstraint is '����Ȩ�޹�������'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pkname is '����'; \r\n");
		sb_sql.append("comment on column pub_templet_1.pksequencename is '������Ӧ��������'; \r\n");
		sb_sql.append("comment on column pub_templet_1.savedtablename is '����ı���'; \r\n");
		sb_sql.append("comment on column pub_templet_1.cardcustpanel is '��Ƭ�Զ������'; \r\n");
		sb_sql.append("comment on column pub_templet_1.listcustpanel is '�б��Զ������'; \r\n");
		sb_sql.append(" \r\n");
		sb_sql.append(" \r\n");

		return sb_sql.toString();
	}

	/**
	 * ����ģ�����Ԫ�صı�ṹ
	 * 
	 * @return
	 */
	public String getPub_TempleteItem() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_templet_1_item; \r\n"); //
		sb_sql.append("create sequence s_pub_templet_1_item; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--Ԫԭģ���ӱ�\r\n");
		sb_sql.append("drop table pub_templet_1_item cascade constraints; \r\n");
		sb_sql.append("create table pub_templet_1_item \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_templet_1_item            number primary key,  --���� \r\n");
		sb_sql.append("  pk_pub_templet_1                 number not null,  --�������� \r\n");
		sb_sql.append("  itemkey                          varchar2(100) not null,  --Ψһ��ʶ \r\n");
		sb_sql.append("  itemname                         varchar2(100) not null,  --��ʾ���� \r\n");
		sb_sql.append("  itemtype                         varchar2(50)  not null,  --�ؼ����� \r\n");
		sb_sql.append("  comboxdesc                       varchar2(2500),  --�������� \r\n");
		sb_sql.append("  refdesc                          varchar2(2500),   --���ն��� \r\n");
		sb_sql.append("  issave                           char(1) default 'Y' not null,  --�Ƿ񱣴� \r\n");
		sb_sql.append("  isdefaultquery                   char(1) default '2' not null,  --Ĭ�ϲ�ѯ���� \r\n");
		sb_sql.append("  ismustinput                      char(1) default 'N' not null,  --�Ƿ������ \r\n");
		sb_sql.append("  loadformula                      varchar2(2500),  --����/��ʾ��ʽ \r\n");
		sb_sql.append("  editformula                      varchar2(2500),  --�༭��ʽ \r\n");
		sb_sql.append("  defaultvalueformula              varchar2(1000),  --Ĭ��ֵ��ʽ \r\n");
		sb_sql.append("  showorder                        number,  --��ʾ˳�� \r\n");
		sb_sql.append("  listwidth                        number,  --�б���ʾ��� \r\n");
		sb_sql.append("  cardwidth                        number,  --��Ƭ��ʾ��� \r\n");
		sb_sql.append("  listisshowable                   char(1) default 'Y' not null,  --�б��Ƿ���ʾ \r\n");
		sb_sql.append("  listiseditable                   char(1) default '1' not null,  --�б��Ƿ�ɱ༭ \r\n");
		sb_sql.append("  cardisshowable                   char(1) default 'Y' not null,  --��Ƭ�Ƿ���ʾ \r\n");
		sb_sql.append("  cardiseditable                   char(1) default '1' not null,  --��Ƭ�Ƿ�ɱ༭ \r\n");
		sb_sql.append("  constraint ui_pub_templet_1_item_1 unique(pk_pub_templet_1,itemkey),  --Ψһ��Լ��!!!�ǳ���Ҫ! \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_1 check (issave in('Y','N')),  --�Ƿ񱣴�Լ�� \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_2 check (isdefaultquery in('1','2','3')),  --����Ĭ�ϲ�ѯ����Լ�� \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_3 check (ismustinput in('Y','N')),  --�Ƿ������ \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_4 check (listisshowable in('Y','N')),  --�б��Ƿ���ʾ \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_5 check (listiseditable in('1','2','3','4')),  --�б��Ƿ�ɱ༭ \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_6 check (cardisshowable in('Y','N')),  --��Ƭ�Ƿ���ʾ \r\n");
		sb_sql.append("  constraint ck_pub_templet_1_item_7 check (cardiseditable in('1','2','3','4'))  --��Ƭ�Ƿ�ɱ༭ \r\n");
		sb_sql.append("); \r\n");
		sb_sql.append("\r\n");
		sb_sql.append("comment on table pub_templet_1_item is 'ģ��Ԫ�ر����'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.pk_pub_templet_1_item is '����'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.pk_pub_templet_1 is '��������'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemkey is 'Ψһ��ʶ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemname is '��ʾ����'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.itemtype is '�ؼ�����'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.comboxdesc is '������˵��'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.refdesc is '����˵��'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.issave is '�Ƿ���뱣��'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.isdefaultquery is 'Ĭ�ϲ�ѯ����'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.ismustinput is '�Ƿ������'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.loadformula is '����/��ʾ��ʽ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.editformula is '�༭��ʽ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.defaultvalueformula is 'Ĭ��ֵ��ʽ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.showorder is '��ʾ˳��'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listwidth is '�б���'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardwidth is '��Ƭ���'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listisshowable is '�б�ʱ�Ƿ����ʾ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.listiseditable is '�б�ʱ�Ƿ�ɱ༭'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardisshowable is '��Ƭʱ�Ƿ����ʾ'; \r\n");
		sb_sql.append("comment on column pub_templet_1_item.cardisshowable is '��Ƭʱ�Ƿ�ɱ༭'; \r\n");
		sb_sql.append("\r\n");
		sb_sql.append("create index in_pub_templet_1_item_1 on pub_templet_1_item (PK_PUB_TEMPLET_1);");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	/**
	 * ����ƽ̨�˵��ı�ṹ
	 * 
	 * @return
	 */
	public String getPub_Menu() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_menu; \r\n"); //
		sb_sql.append("create sequence s_pub_menu; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--ϵͳ�˵���\r\n");
		sb_sql.append("drop table pub_menu cascade constraints; \r\n");
		sb_sql.append("create table pub_menu \r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  id                      number(18) primary key, --���� \r\n");
		sb_sql.append("  name                    varchar2(64) not null, --���� \r\n");
		sb_sql.append("  localname               varchar2(64)  not null, --������ \r\n");
		sb_sql.append("  parentmenuid            number(18), --���˵�ID \r\n");
		sb_sql.append("  seq                     number(5), --���к� \r\n");
		sb_sql.append("  commandtype             number(2), --�������� \r\n");
		sb_sql.append("  command                 varchar2(2500), --�������� \r\n");
		sb_sql.append("  showintoolbar           char(1) default 'Y' CHECK(showintoolbar IN ('Y','N')) not null, --�Ƿ��ڹ�������ʾ \r\n");
		sb_sql.append("  icon                    varchar2(100), --ͼ�� \r\n");
		sb_sql.append("  constraint ui_pub_menu_1 unique(name)  --Ψһ��Լ�� \r\n");
		sb_sql.append("); \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_menu is 'ϵͳ�˵���';\r\n");
		sb_sql.append("comment on column pub_menu.id is '����';\r\n");
		sb_sql.append("comment on column pub_menu.name is '����';\r\n");
		sb_sql.append("comment on column pub_menu.localname is '������';\r\n");
		sb_sql.append("comment on column pub_menu.parentmenuid is '���˵�ID';\r\n");
		sb_sql.append("comment on column pub_menu.seq is '���к�';\r\n");
		sb_sql.append("comment on column pub_menu.commandtype is '��������';\r\n");
		sb_sql.append("comment on column pub_menu.command is '��������';\r\n");
		sb_sql.append("comment on column pub_menu.showintoolbar is '�Ƿ��ڹ�������ʾ';\r\n");
		sb_sql.append("comment on column pub_menu.icon is 'ͼ��';\r\n");
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

		sb_sql.append("--ϵͳ�û���\r\n");
		sb_sql.append("drop table pub_user cascade constraints; \r\n");
		sb_sql.append("create table pub_user \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append(" id               number not null,    --���� \r\n");
		sb_sql.append(" code             varchar2(20),  --���� \r\n");
		sb_sql.append(" name             varchar2(50),  --���� \r\n");
		sb_sql.append(" pwd              varchar2(20),  --���� \r\n");
		sb_sql.append(" adminpwd         varchar2(20),  --�������� \r\n");
		sb_sql.append(" creator          varchar2(20),  --������ \r\n");
		sb_sql.append(" createdate       date,  --�������� \r\n");
		sb_sql.append(" telephone        varchar2(20),  --��ϵ�绰 \r\n");
		sb_sql.append(" mobile           varchar2(20),  --�ֻ� \r\n");
		sb_sql.append(" email            varchar2(30),  --�ʼ� \r\n");
		sb_sql.append(" regionid         number(18),  --�������� \r\n");
		sb_sql.append(" regioncode       varchar2(50),  --����������� \r\n");
		sb_sql.append(" constraint pk_pub_user_id primary key(id),  --��������Լ�� \r\n");
		sb_sql.append(" constraint uk_pub_user_code unique(code)  --���벻���ظ� \r\n");
		sb_sql.append("); \r\n\r\n");

		sb_sql.append("comment on table  pub_user is 'ϵͳ��Ա��';\r\n");
		sb_sql.append("comment on column pub_user.id is '����';\r\n");
		sb_sql.append("comment on column pub_user.code is '����';\r\n");
		sb_sql.append("comment on column pub_user.pwd is '����';\r\n");
		sb_sql.append("comment on column pub_user.adminpwd is '��������';\r\n");
		sb_sql.append("comment on column pub_user.creator is '������';\r\n");
		sb_sql.append("comment on column pub_user.createdate is '��������';\r\n");
		sb_sql.append("comment on column pub_user.telephone is '�绰';\r\n");
		sb_sql.append("comment on column pub_user.mobile is '�ֻ�';\r\n");
		sb_sql.append("comment on column pub_user.email is '�ʼ�';\r\n");
		sb_sql.append("comment on column pub_user.regionid is '��������';\r\n");
		sb_sql.append("comment on column pub_user.regioncode is '�����������';\r\n");
		
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'gxlu','gxlu','1','1','471'); \r\n"); //
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'admin','admin','1','1','471'); \r\n"); //
		sb_sql.append("insert into pub_user(id,code,name,pwd,adminpwd,regioncode) values (s_pub_user.nextval,'guest','guest','1','1','471'); \r\n"); //
		sb_sql.append("commit; \r\n");

		return sb_sql.toString(); //
	}

	/**
	 * ���ز�ѯģ�����ı�ṹ
	 * 
	 * @return
	 */
	public String getPub_QueryTemplete() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_querytemplet;\r\n"); //
		sb_sql.append("create sequence s_pub_querytemplet;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--��ѯģ���\r\n");
		sb_sql.append("drop   table pub_querytemplet cascade constraints;\r\n");
		sb_sql.append("create table pub_querytemplet\r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  pk_pub_querytemplet          number primary key not null,\r\n");
		sb_sql.append("  templetcode                  varchar2(100) not null,  --ģ�����\r\n");
		sb_sql.append("  templetname                  varchar2(200) not null,  --ģ������\r\n");
		sb_sql.append("  sql                          varchar2(2500),  --��ѯ����SQL\r\n");
		sb_sql.append("  constraint ui_pub_querytemplet_1 unique(templetcode)  --Ψһ��Լ�� \r\n"); //
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_querytemplet is '��ѯģ���';\r\n");
		sb_sql.append("comment on column pub_querytemplet.pk_pub_querytemplet is '����';\r\n");
		sb_sql.append("comment on column pub_querytemplet.templetcode is 'ģ�����';\r\n");
		sb_sql.append("comment on column pub_querytemplet.templetname is 'ģ������';\r\n");
		sb_sql.append("comment on column pub_querytemplet.sql is 'SQL���';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * ���ز�ѯģ�����Ԫ�صı�ṹ
	 * 
	 * @return
	 */
	public String getPub_QueryTempleteItem() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_querytemplet_item;\r\n");
		sb_sql.append("create sequence s_pub_querytemplet_item;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--��ѯģ���ӱ�\r\n");
		sb_sql.append("drop   table pub_querytemplet_item cascade constraints;\r\n");
		sb_sql.append("create table pub_querytemplet_item\r\n");
		sb_sql.append("(\r\n");
		sb_sql.append("  pk_pub_querytemplet_item       number primary key not null, --����\r\n");
		sb_sql.append("  pk_pub_querytemplet            number not null,  --��������\r\n");
		sb_sql.append("  itemkey                        varchar2(100) not null,  --Ψһ��ʶ\r\n");
		sb_sql.append("  itemname                       varchar2(100) not null,  --��ʾ����\r\n");
		sb_sql.append("  itemtype                       varchar2(50),  --�ؼ�����\r\n");
		sb_sql.append("  comboxdesc                     varchar2(2500),  --������˵��\r\n");
		sb_sql.append("  refdesc                        varchar2(2500),  --����˵��\r\n");
		sb_sql.append("  showorder                      number,  --��ʾ˳��\r\n");
		sb_sql.append("  constraint ui_pub_querytemplet_item_1 unique(pk_pub_querytemplet,itemkey)  --Ψһ��Լ�� \r\n"); //
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_querytemplet_item is '��ѯģ���ӱ�';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.pk_pub_querytemplet_item is '����';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.pk_pub_querytemplet is '��������';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemkey is 'Ψһ��ʶ';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemname is '��ʾ����';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.itemtype is '�ؼ�����';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.comboxdesc is '������˵��';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.refdesc is '����˵��';\r\n");
		sb_sql.append("comment on column pub_querytemplet_item.refdesc is '��ʾ˳��';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * ���������������ֵ�ı�ṹ
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
		sb_sql.append("  pk_pub_comboboxdict    number primary key,  --����\r\n");
		sb_sql.append("  type  varchar2(100) not null,  --����\r\n");
		sb_sql.append("  id    varchar2(30) not null,  --����ID\r\n");
		sb_sql.append("  code  varchar2(100),  --���ݱ���\r\n");
		sb_sql.append("  name  varchar2(100),  --��������\r\n");
		sb_sql.append("  descr varchar2(500),  --����˵��\r\n");
		sb_sql.append("  seq   number,            --����\r\n");
		sb_sql.append("  constraint ui_pub_comboboxdict_1 unique(type,id)\r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table  pub_comboboxdict is '�����������ֵ��';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.pk_pub_comboboxdict is '����';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.type is '����';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.id is '����ID';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.code is '���ݱ���';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.name is '��������';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.descr is '����˵��';\r\n");
		sb_sql.append("comment on column pub_comboboxdict.seq is '����';\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("create index in_pub_ComboBoxdict on pub_ComboBoxdict (type);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("delete from pub_ComboBoxdict;\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�Ա�','M','Man','��',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�Ա�','W','Women','Ů',null,2);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','1','1','����',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','2','2','����',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','3','3','��ר',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','4','4','��ר',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','5','5','����',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','6','6','˶ʿ',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ѧ��','7','7','��ʿ',null,7);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','�ı���','001','�ı���',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','���ֿ�','002','���ֿ�',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','�����','003','�����',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','������','004','������',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','����','005','����',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','�����ı���','006','�����ı���',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','����','007','����',null,7);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','��ɫ','008','��ɫ',null,8);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','������','009','������',null,9);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','�ļ�ѡ���','010','�ļ�ѡ���',null,10);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','ʱ��','011','ʱ��',null,11);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','���ı���','012','���ı���',null,12);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','��ѡ��','013','��ѡ��',null,13);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'ƽ̨�ؼ�����','ͼƬѡ���','014','ͼƬѡ���',null,14);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','0','0','�Զ���Frame',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','11','11','1������',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','12','12','2������/��',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','13','13','3������/��',null,4);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','14','14','4˫������',null,5);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','15','15','5˫������/��',null,6);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','16','16','6˫������',null,7);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','17','17','7˫������/��',null,8);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','18','18','8���ӱ�',null,9);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','19','19','9���ӱ�',null,10);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�˵�·��','20','20','10�������',null,11);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�����ѯ��������','1','001','���ٲ�ѯ',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�����ѯ��������','2','002','ͨ�ò�ѯ',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�����ѯ��������','3','003','�������ѯ',null,3);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�ؼ��༭��������','1','001','ȫ���ɱ༭',null,1);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�ؼ��༭��������','2','002','�������ɱ༭',null,2);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�ؼ��༭��������','3','003','���޸Ŀɱ༭',null,3);\r\n");
		sb_sql.append("insert into pub_ComboBoxdict values (s_pub_ComboBoxdict.nextval,'�ؼ��༭��������','4','004','ȫ������',null,4);\r\n");
		sb_sql.append("\r\n");
		sb_sql.append("commit;\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	/**
	 * �����ļ��ϴ�����ı�ṹ
	 * 
	 * @return
	 */
	public String getPub_FileUpload() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_fileupload;\r\n");
		sb_sql.append("create sequence s_pub_fileupload;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--�ϴ��ļ���־��¼��,Ϊ�ļ��ϴ��ؼ���\r\n");
		sb_sql.append("drop   table pub_fileupload cascade constraints;\r\n");
		sb_sql.append("create table pub_fileupload \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_fileupload  number primary key,  --����,��Ӧ���� \r\n");
		sb_sql.append("  oldfilename  varchar2(100),  --�ļ�ԭ��,��Ҳ�ǲ�������ʾ������ \r\n");
		sb_sql.append("  newfilename  varchar2(100),  --�ļ�����,������tomcat�ϵ��ļ���,���������м���oldfilenameƴ�ɵ�!!��ͬʱҲ��Ӧ�ñ��ֶδ洢���������� \r\n");
		sb_sql.append("  tablename    varchar2(30),   --�ü�¼���ĸ������� \r\n");
		sb_sql.append("  fieldname    varchar2(30)    --�ü�¼���ĸ�����ĸ��ֶ����� \r\n");
		sb_sql.append("); \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_fileupload is '�ϴ�/�����ļ��Ĵ洢��'; \r\n");
		sb_sql.append("comment on column pub_fileupload.pk_pub_fileupload is '����,��Ӧ����'; \r\n");
		sb_sql.append("comment on column pub_fileupload.oldfilename is '�ļ�ԭ��,��Ҳ�ǲ�������ʾ������'; \r\n");
		sb_sql.append("comment on column pub_fileupload.newfilename is '�ļ�����,������tomcat�ϵ��ļ���'; \r\n");
		sb_sql.append("comment on column pub_fileupload.tablename is '�ü�¼���ĸ�������'; \r\n");
		sb_sql.append("comment on column pub_fileupload.fieldname is '�ü�¼���ĸ�����ĸ��ֶ�����'; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");
		return sb_sql.toString();
	}

	/**
	 * �����û��Զ��幫ʽ�ı�ṹ
	 * 
	 * @return
	 */
	public String getPub_FormulaFn() {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop   sequence s_pub_formulafunctions;\r\n");
		sb_sql.append("create sequence s_pub_formulafunctions;\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--�û��Զ��庯����,���û������Զ��庯��,Ȼ���ڹ�ʽ��ʹ��\r\n");
		sb_sql.append("drop   table pub_formulafunctions cascade constraints;\r\n");
		sb_sql.append("create table pub_formulafunctions \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_formulafunctions number primary key,  --����\r\n");
		sb_sql.append("  functionname            varchar2(50) not null,  --������,����'toString()'\r\n");
		sb_sql.append("  parcount                number(3,0),  --��������,����1\r\n");
		sb_sql.append("  classname               varchar2(200)  not null,  --ʵ�ֵ�����\r\n");
		sb_sql.append("  formulaformat           varchar2(500)  not null, --ʹ��˵��\r\n");
		sb_sql.append("  descr                   varchar2(500),  --��ע˵��\r\n");
		sb_sql.append("  demo                    varchar2(500)  --ʾ��\r\n");
		sb_sql.append(");");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("comment on table pub_formulafunctions is '�û��Զ��庯����';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.pk_pub_formulafunctions is '����';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.functionname is '������,����toString()';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.parcount is '��������,����1';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.classname is 'ʵ�ֵ�����';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.formulaformat is 'ʹ��˵��';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.descr is '˵��';\r\n");
		sb_sql.append("comment on column pub_formulafunctions.demo is 'ʾ��';\r\n");

		return sb_sql.toString();
	}

	public String getPub_News() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("drop   sequence s_pub_news; \r\n");
		sb_sql.append("create sequence s_pub_news; \r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--������ϵͳ����\r\n");
		sb_sql.append("drop   table pub_news cascade constraints; \r\n");
		sb_sql.append("create table pub_news \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_news                 number primary key not null, --���� \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --���� \r\n");
		sb_sql.append("  content                     varchar2(2500),  --���� \r\n");
		sb_sql.append("  isscrollview                char(1) default 'N' check (isscrollview in ('Y','N')) not null,  --�Ƿ���� \r\n");
		sb_sql.append("  createtime                  date not null,  --�������� \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --������ \r\n");
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

		sb_sql.append("--���������ύ����\r\n");
		sb_sql.append("drop   table pub_task_commit cascade constraints; \r\n");
		sb_sql.append("create table pub_task_commit \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_task_commit          number primary key not null, --���� \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --���� \r\n");
		sb_sql.append("  billtype                    varchar2(20),  --�������� \r\n");
		sb_sql.append("  billid                      varchar2(20),  --����ID \r\n");
		sb_sql.append("  billcode                    varchar2(20),  --���ݱ��� \r\n");
		sb_sql.append("  createtime                  date not null, --�������� \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --������ \r\n");
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

		sb_sql.append("--�����ڴ���������\r\n");
		sb_sql.append("drop   table pub_task_deal cascade constraints; \r\n");
		sb_sql.append("create table pub_task_deal \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  pk_pub_task_deal            number primary key not null, --���� \r\n");
		sb_sql.append("  title                       varchar2(500) not null,  --���� \r\n");
		sb_sql.append("  billtype                    varchar2(20),  --�������� \r\n");
		sb_sql.append("  billid                      varchar2(20),  --����ID \r\n");
		sb_sql.append("  billcode                    varchar2(20),  --���ݱ��� \r\n");
		sb_sql.append("  createtime                  date not null, --�������� \r\n");
		sb_sql.append("  creater                     varchar2(20) not null  --������ \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		return sb_sql.toString();
	}

	public String getPub_Clusterhost() {
		StringBuffer sb_sql = new StringBuffer(); //

		sb_sql.append("\r\n");
		sb_sql.append("\r\n");

		sb_sql.append("--��Ⱥ��Ϣ��\r\n");
		sb_sql.append("drop   table pub_clusterhost cascade constraints; \r\n");
		sb_sql.append("create table pub_clusterhost \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  ip varchar2(15),  --IP��ַ \r\n");
		sb_sql.append("  port varchar2(8),  --�˿�,  --���� \r\n");
		sb_sql.append("  count number   --�������� \r\n");
		sb_sql.append(");\r\n");

		sb_sql.append("comment on table pub_clusterhost is '��Ⱥ��Ϣ��';\r\n");
		sb_sql.append("comment on column pub_clusterhost.ip is 'IP��ַ';\r\n");
		sb_sql.append("comment on column pub_clusterhost.port is '�˿�';\r\n");
		sb_sql.append("comment on column pub_clusterhost.count is '��������';\r\n");

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

		sb_sql.append("--ϵͳԼ����\r\n");
		sb_sql.append("drop   table pub_table_info cascade constraints; \r\n");
		sb_sql.append("create table pub_table_info \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  constraint_name            varchar2(30) not null,  --Լ���� \r\n");
		sb_sql.append("  constraint_type            varchar2(1),  --Լ������ \r\n");
		sb_sql.append("  column_name                varchar2(4000),  --���� \r\n");
		sb_sql.append("  r_column                   varchar2(4000),  --����� \r\n");
		sb_sql.append("  table_name                 varchar2(30) not null,  --���� \r\n");
		sb_sql.append("  r_table                    varchar2(30),  --������ \r\n");
		sb_sql.append("  r_constraint_name          varchar2(30),  --���������� \r\n");
		sb_sql.append("  last_change                date  --����޸����� \r\n");
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

		sb_sql.append("--ϵͳ������\r\n");
		sb_sql.append("drop   table pub_table_indexes cascade constraints; \r\n");
		sb_sql.append("create table pub_table_indexes \r\n");
		sb_sql.append("( \r\n");
		sb_sql.append("  index_name             varchar2(30) not null, --������ \r\n");
		sb_sql.append("  index_type             varchar2(27), --�������� \r\n");
		sb_sql.append("  column_name            varchar2(4000),  --����\r\n");
		sb_sql.append("  table_name             varchar2(30) not null,  --����\r\n");
		sb_sql.append("  compression            varchar2(8),  --����\r\n");
		sb_sql.append("  pct_free               number,  --\r\n");
		sb_sql.append("  ini_trans              number, --\r\n");
		sb_sql.append("  max_trans              number,  --\r\n");
		sb_sql.append("  last_analyzed          date  --����������\r\n");
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
 * �ʴ��ֳ�����ͳһ����
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
