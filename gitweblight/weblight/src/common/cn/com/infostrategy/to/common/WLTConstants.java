/**************************************************************************
 * $RCSfile: WLTConstants.java,v $  $Revision: 1.13 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

/**
 * ����������ƽ̨�ĳ�����!!
 * @author xch
 *
 */
public class WLTConstants {
	public static String ORACLE = "ORACLE"; //Oracle��������
	public static String SQLSERVER = "SQLSERVER"; //SQLServer
	public static String MYSQL = "MYSQL"; //MYSQL
	public static String DB2 = "DB2"; //MYSQL
	public static byte JEPTYPE_UI = 0;
	public static byte JEPTYPE_BS = 1;

	public static byte LEVELTYPE_LIST = 1;
	public static byte LEVELTYPE_CARD = 2;

	public static int THREAD_OVERTIME_VALUE = 3000; //ϵͳ���ܳ�ʱ��ֵ,��λ�Ǻ���
	public static int THREAD_OVERWEIGHT_VALUE = 204800; //���ʳ��صķ�ֵ,��λ���ֽ�,����200K�;���!!
	public static int THREAD_OVERJVM_VALUE = 2048; //JVM��������,��λ��,������2048K(2M)�;���,��ֻ��һ���û����ʵ������,���Ǹ���ָ��,�ڴ��������û����ʵ����Ҳ��˵�����ʱ����Σ�յ�!!

	public static String SIMPLECHINESE = "SIMPLECHINESE"; //����,��������
	public static String ENGLISH = "ENGLISH"; //����,Ӣ��
	public static String TRADITIONALCHINESE = "TRADITIONALCHINESE"; //����,��������()

	//���еĿؼ�����!!Ŀǰ��15��,�Ժ󻹻�����!!
	public static String COMP_LABEL = "Label";
	public static String COMP_TEXTFIELD = "�ı���";
	public static String COMP_NUMBERFIELD = "���ֿ�";
	public static String COMP_PASSWORDFIELD = "�����";
	public static String COMP_COMBOBOX = "������";
	public static String COMP_REFPANEL = "����"; //���Ͳ���1,��ֱ��һ��SQL���ڵ�һ�ξͲ���������ݷ���Ԫԭģ��VO�У�������ÿһ�ε�����ʱ��ѯ!�ʺ��������ٵ����ñ�!
	//public static String COMP_REFPANEL2 = "����2"; //���Ͳ���2,���ڵ�һ�ξͲ�����ݣ�����ÿһ�ε��ʱ��ѯ����,������Ҫ�ټ�һ���У�Ȼ��ʹ�ü��ع�ʽ+�༭��ʽ����.
	public static String COMP_REFPANEL_TREE = "���Ͳ���";
	//public static String COMP_REFPANEL_TREE2 = "���Ͳ���2";
	public static String COMP_REFPANEL_MULTI = "��ѡ����"; //����һ��SQL,Ȼ����Զ�ѡ,Ȼ����Խ��������е����ݶ�ѡ�󷵻�!!
	//public static String COMP_REFPANEL_MULTI2 = "��ѡ����2"; //���ǵ�һ�β�����,����ÿ�ε����ѯ,������Ҫʹ�ü��ع�ʽ+�༭��ʽ����.
	public static String COMP_REFPANEL_CUST = "�Զ������"; //�Զ������,��Ҫ�̳���cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog
	public static String COMP_REFPANEL_LISTTEMPLET = "�б�ģ�����"; //ֱ����һ��BillListPanel
	public static String COMP_REFPANEL_TREETEMPLET = "����ģ�����"; //ֱ����һ��BillTreePanel
	public static String COMP_REFPANEL_REGFORMAT = "ע���������"; //ͨ������ע���BillFormat��崴��һ������,����������Ҫͨ��һ�����������!!!
	public static String COMP_REFPANEL_REGEDIT = "ע�����"; //ϵͳע��Ĳ���,�ǳ�����,�Ժ�������ն���Ҫע��,������߿���Ч��,����Ҳ��ר�˸��������뿪��!
	public static String COMP_TEXTAREA = "�����ı���";
	public static String COMP_BIGAREA = "���ı���";
	public static String COMP_STYLEAREA = "���ı���"; //��֧�ִ���,б��,�»��ߵ�Ч�����ı���!
	public static String COMP_DATE = "����";
	public static String COMP_FILECHOOSE = "�ļ�ѡ���"; //�����ϴ������ļ��ļ�ѡ���
	public static String COMP_SELFDESC = "�Զ���ؼ�"; //�����ϴ������ļ��ļ�ѡ���
	public static String COMP_COLOR = "��ɫ";
	public static String COMP_CALCULATE = "������";
	public static String COMP_DATETIME = "ʱ��";
	public static String COMP_CHECKBOX = "��ѡ��";
	public static String COMP_LINKCHILD = "�����ӱ�"; //����ֱ�Ӵ�һ���ӱ���б���,ʵ���ϻ���һ�Զ�Ĺ�ϵ
	public static String COMP_IMPORTCHILD = "�����ӱ�"; //����ֱ�ӵ���һ���ӱ�������ά����ϵ�ֺŷָ�
	public static String COMP_PICTURE = "ͼƬѡ���";
	public static String COMP_IMAGEUPLOAD = "ͼƬ�ϴ�";  //ֱ��ֻ�ϴ�һ��ͼƬȻ��洢�����ݿ���,Ȼ���ڿ�Ƭ��ֱ����Ⱦ!!! ����HRϵͳ�е���Ա��Ƭ,BOMͼ��,��ҳ�������Ŷ��õ�����ؼ�! �����ϴ��ļ���һ����!�ϴ��ļ��Ǵ洢��Ŀ¼��,�����Ǵ洢�����ݿ��е�!!
	public static String COMP_EXCEL = "Excel�ؼ�"; //Excel�ؼ�
	public static String COMP_BUTTON = "��ť"; //��ť
	public static String COMP_OFFICE = "Office�ؼ�"; //Office��ť
	public static String COMP_REGULAR = "������ʽ�ؼ�"; //[zzl]

	//���ð�ť����,��Ϊ��ʱ����һЩ�û�Ҫ��"�༭"�г�"�޸�",��"�½�"�г�"����",�����б�Ҫ����Щ��Ϊ����,������д�ڸ����ط�,����������ǳ��鷳
	public static String BUTTON_TEXT_INSERT = "����"; //
	public static String BUTTON_TEXT_EDIT = "�޸�"; //
	public static String BUTTON_TEXT_DELETE = "ɾ��"; //
	public static String BUTTON_TEXT_SAVE = "����"; //
	public static String BUTTON_TEXT_SEARCH = "��ѯ"; //
	public static String BUTTON_TEXT_BROWSE = "���"; //
	public static String BUTTON_TEXT_RETURN = "����"; //
	public static String BUTTON_TEXT_CANCEL = "ȡ��"; //
	public static String BUTTON_TEXT_SAVE_RETURN = "���淵��"; //
	public static String BUTTON_TEXT_CANCEL_RETURN = "ȡ������"; //
	public static String BUTTON_TEXT_RESET = "���"; //
	public static String BUTTON_TEXT_REFRESH = "ˢ��"; //
	public static String BUTTON_TEXT_PRINT = "��ӡ"; //�����������ʵ��,Ҳ���м�ֵ!һֱ����ֱ�ӽ�һ��BillCard��ӡԤ������!!

	// ģ�����,BSUitl����ʵ��ʱ����
	public static final String STRING_TEMPLET_CODE = "TEMPLETCODE";

	// �ͻ��˻���,BSUitl����ʵ��ʱ����
	public static final String STRING_CLIENT_ENVIRONMENT = "CLIENTENV";

	//
	//�������ݱ༭״̬ ..��ʼ
	//
	public static final String BILLDATAEDITSTATE_INIT = "INIT"; //

	public static final String BILLDATAEDITSTATE_INSERT = "INSERT"; // 

	public static final String BILLDATAEDITSTATE_UPDATE = "UPDATE"; // 

	public static final String BILLDATAEDITSTATE_DELETE = "DELETE"; //

	//
	//	�������ݱ༭״̬..����
	//

	//��Ƭ/�б��¿ؼ��༭״̬����
	public static final String BILLCOMPENTEDITABLE_ALL = "1"; //ȫ���ɱ༭

	public static final String BILLCOMPENTEDITABLE_ONLYINSERT = "2"; //ֻ������ʱ�ɱ༭

	public static final String BILLCOMPENTEDITABLE_ONLYUPDATE = "3"; //ֻ���޸�ʱ�ɱ༭

	public static final String BILLCOMPENTEDITABLE_NONE = "4"; //ȫ�����ɱ༭!

	// ��Ϣ����
	public static final int MESSAGE_ERROR = 0; //
	public static final int MESSAGE_INFO = 1; //
	public static final int MESSAGE_WARN = 2;
	public static final int MESSAGE_QUESTION = 3; //
	public static final int MESSAGE_CONFIRM = 4;

	// ��Ϣ���ڱ���
	public static final String MESSAGE_INFO_TITLE = "��ʾ��Ϣ";

	public static final String MESSAGE_WARN_TITLE = "Warn";

	public static final String MESSAGE_ERROR_TITLE = "Error";

	public static final String MESSAGE_CONFIRM_TITLE = "Confirm";

	// ���õ���ʾ��Ϣ
	public static final String STRING_DEL_CONFIRM = "ȷ��Ҫɾ����?";

	public static final String STRING_DEL_SELECTION_NEED = "��ѡ��һ��Ҫɾ���ļ�¼!";

	public static final String STRING_OPERATION_SUCCESS = "�����ɹ�!";

	public static final String STRING_OPERATION_FAILED = "����ʧ��!";

	public static final String STRING_CURRENT_USER = "��ǰ�û�: ";

	public static final String STRING_LOGIN_TIME = "��¼ʱ��: ";

	public static final String STRING_CURRENT_POSITION = "��ǰλ��: ";

	public static final String STRING_CURRENT_POSITION_DIVIDER = " >> ";

	public static final String STRING_REFPANEL_CUSTOMER_TITLE = "�Զ������";

	public static final String STRING_REFPANEL_COMMON_TITLE = "������Ϣ";

	public static final String STRING_REFPANEL_UIINTERCEPTOR = "UI������";

	public static final String STRING_REFPANEL_BSINTERCEPTOR = "BS������";

	// ʱ�����ͣ�0��ʾ���ݿ��ֶε�ʱ��Ϊchar�ͣ�1��ʾΪdate��
	public static final int DATETYPE = 0;

	//�ϴ��ļ����Ŀ¼ 
	public static final String UPLOAD_DIRECTORY = "upload";//

	public static final String UPLOAD_FILE_NAME = "filename";

	public static final String UPLOAD_RESULT = "result";
	//�����в���ʾ�еı�Ƿ�
	public static final String STRING_REFPANEL_UNSHOWSIGN = "$";

	//�û���λ
	public static final String USER_WORKPOSITION = "SYS_WORKPOSITION";

	public static final String SYS_LOGIN = "��¼ϵͳ";

	public static final String SYS_LOGINOUT = "�˳�ϵͳ";
	
	public static final String MODULE_INSTALL_STATUS_WSQ="δ��Ȩ";
	public static final String MODULE_INSTALL_STATUS_YAZ="�Ѱ�װ";
	public static final String MODULE_INSTALL_STATUS_KSJ="������";
	public static final String MODULE_INSTALL_STATUS_KAZ="�ɰ�װ";
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
