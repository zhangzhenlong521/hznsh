package cn.com.infostrategy.bs.sysapp.help;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.WebDispatchIfc;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * �Ժ�Ҫ��һЩ�ǳ��ؼ��ģ�ʵʩ������,��Ҫ�����Ĺؼ����뼼��,ʵʩ�Ӵ���,������Ա����!
 * ���Ӵ���,��ͳͳֱ�ӷ���ϵͳ��,��ʵʩ��Ա,������Ա���ܿ��ٲ鿴!
 * @author xch
 *
 */
public class HelpWebCallBean implements WebDispatchIfc {

	private int li_onerowCount = 5; //���һ���м���?
	private boolean isLocalHref = false; //

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap map) throws Exception {
		String str_helpfile = (String) map.get("file"); //���Ƕ��ĸ��ļ�!
		if (str_helpfile == null || str_helpfile.trim().equals("")) {
			_response.setCharacterEncoding("GBK"); //
			_response.setContentType("text/html"); //
			_response.getWriter().println(getDefaultHtml()); //
		} else {
			String str_fileType = str_helpfile.substring(str_helpfile.indexOf(".") + 1, str_helpfile.length()); //�ļ�����!!
			if (str_fileType.equalsIgnoreCase("txt")) { //������ı��ļ�!!
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/plain"); //���ı�!!
				_response.getWriter().println(getFileTextContent(str_helpfile)); //
				_response.flushBuffer(); //
			} else if (str_fileType.equalsIgnoreCase("htm") || str_fileType.equalsIgnoreCase("html")) { //�����html�ļ�
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println(getFileTextContent(str_helpfile)); //
			} else if (str_fileType.equalsIgnoreCase("doc")) { //�Ժ󻹿�����doc,��Word�ļ�!

			}
		}
	}

	//Ĭ����ҳ!
	private String getDefaultHtml() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\n"); //
		sb_html.append("<head>\n"); //
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\n"); //
		sb_html.append("<TITLE>������/֪ʶ������</TITLE>\n"); //
		sb_html.append("<style type=\"text/css\"> \n"); //
		sb_html.append(".p_text  { font-size: 12px;};\n"); //
		sb_html.append(".table {  border-collapse:   collapse; font-size: 12px; };\n"); //
		sb_html.append(".td    {  border:   solid   1px   #888888; font-size: 12px; };\n"); //
		sb_html.append("</style>\n"); //
		sb_html.append("</head>\n"); //
		sb_html.append("<body>\n"); //
		sb_html.append("<span align=\"center\" class=\"p_text\">������/֪ʶ������</span><br>\n"); //
		sb_html.append("<table width=\"90%\" align=\"center\" class=\"table\" cellspacing=0 cellpadding=8>\n"); //

		//��ҵ��֪ʶ
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��ҵ��</td></tr>\r\n"); //
		ArrayList<String> al_yw = new ArrayList<String>(); //
		al_yw.add(getTD("���е���֯�ṹ", "yw_orgn.txt")); //
		al_yw.add(getTD("������Ҫҵ��", "yw_yw.txt")); //
		al_yw.add(getTD("���ɺϹ沿�Ľṹ��ְ��", "yw_hgb.txt")); //
		al_yw.add(getTD("�ڿػ����淶", "yw_nkgf.txt")); //
		al_yw.add(getTD("18��ָ��", "yw_18zy.txt")); //
		al_yw.add(getTD("������Э��", "yw_basaier.txt")); //
		al_yw.add(getTD("����˹����", "yw_sbs.txt")); //
		al_yw.add(getTD("�������(����,�г�,����)�Ĺ�ϵ������", "yw_risk3.txt")); //
		sb_html.append(getHtmlByList(al_yw)); //

		//��Ʒ���������!!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��Ʒ����</td></tr>\r\n"); //
		ArrayList<String> al_cp = new ArrayList<String>(); //
		al_cp.add(getTD("��Ʒ����˼·����Ҫģ��", "cp_1.txt")); //
		al_cp.add(getTD("��Ʒ��ģ��ĺ��������", "cp_2.txt")); //
		al_cp.add(getTD("��Ʒ�ġ��ؼ������߼�/���̡�", "cp_3.txt")); //
		al_cp.add(getTD("��������Ȩ������Ҫ��ɫ", "cp_datapolicy.htm")); //
		al_cp.add(getTD("���﹦��Ȩ������Ҫ��ɫ", "cp_funcpolicy.htm")); //
		al_cp.add(getTD("�Ϲ�,�ڿ�,���ղ�Ʒ֮�������", "cp_4.txt")); //
		al_cp.add(getTD("��Ʒ����100��", "cp_5.txt")); //
		al_cp.add(getTD("��Ʒ����֮�ؽ������㡱", "cp_liandian.txt")); //
		al_cp.add(getTD("�ڿ���ԭ���ڲ�Ʒ�е����ֵ�", "cp_5.txt")); //
		al_cp.add(getTD("��Ʒ��������Ĺ���ָ��", "cp_5.txt")); //
		sb_html.append(getHtmlByList(al_cp)); //

		//ʵʩ���������!!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">ʵʩ����</td></tr>\r\n"); //
		ArrayList<String> al_ss = new ArrayList<String>(); //
		al_ss.add(getTD("ϵͳʵʩ6����", "ss_6bz.txt")); //
		al_ss.add(getTD("ϵͳʵʩ10���Ӵ���", "ss_10jcm.txt")); //
		al_ss.add(getTD("�������Ҫ��8������", "ss_8xql.txt")); //
		al_ss.add(getTD("��ѯʦ/IT��Ա;��Ʒ/���ο���֮��ı߽�����ԭ��", "ss_6bz.txt")); //
		al_ss.add(getTD("ҵ������ת��ϵͳ����֮����(��ɫ,���ܵ�,����,���ݶ������)", "ss_8xql.txt")); //
		al_ss.add(getTD("ʵʩ���̳�����������Ӧ�Լ���", "ss_custquestion.htm")); //
		al_ss.add(getTD("���Ի��ڹؼ�ע������", "ss_test.htm")); //
		al_ss.add(getTD("ʵʩ��Ա�ر��ĵ���������֪ʶ", "ss_10jcm.txt")); //

		sb_html.append(getHtmlByList(al_ss)); //

		//����-����/����
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��������-����/���õ�</td></tr>\r\n"); //
		ArrayList<String> al_kf_comm = new ArrayList<String>(); //
		al_kf_comm.add(getTD("ƽ̨����·��˵����10���ص�", "kf_ptjslx.htm")); //
		al_kf_comm.add(getTD("ƽ̨����33������", "kf_pt33.htm")); //
		al_kf_comm.add(getTD("ƽ̨-ģ���������й���", "kf_ptbill.htm")); //
		al_kf_comm.add(getTD("ƽ̨-�������������й���", "kf_ptwf.htm")); //
		al_kf_comm.add(getTD("ƽ̨-���������й���", "kf_ptreport.htm")); //

		al_kf_comm.add(getTD("������Աѧϰ·��ͼ", "kf_newstudy.html")); //
		al_kf_comm.add(getTD("��һ������Ӧ��", "kf_comm_firstpage.txt")); //
		al_kf_comm.add(getTD("�Զ���Զ�̵���", "kf_comm_remotecall.htm")); //
		al_kf_comm.add(getTD("дһ����򵥵����", "myfirst.txt")); //
		al_kf_comm.add(getTD("UIUTil,TBUtil,CommDMO,SysAppDMO���ĸ���ĺ���", ".txt")); //
		al_kf_comm.add(getTD("������MessageBoxʹ��", "kf_comm_msgbox.htm")); //
		al_kf_comm.add(getTD("ʹ��SQL��ѯ,�޸����ݿ�", ".txt")); //
		al_kf_comm.add(getTD("��һ�����������ļ�", ".txt")); //
		al_kf_comm.add(getTD("Office�ؼ���ʹ��", ".txt")); //
		al_kf_comm.add(getTD("��ҳ��Ϣ���ĵ����ݹ���", ".txt")); //
		al_kf_comm.add(getTD("�����¼��IE����ԭ��", "kf_comm_singlelogin.htm")); //
		al_kf_comm.add(getTD("����Word,ͼƬ,Excel�ļ���ԭ��", ".txt")); //
		al_kf_comm.add(getTD("��Ӱ�����ܵ�����ע���", ".txt")); //
		al_kf_comm.add(getTD("������Ա�ر�֮Java������", ".txt")); //
		al_kf_comm.add(getTD("������Ա�ر�֮SQL������", ".txt")); //
		al_kf_comm.add(getTD("�������ô�����Ӣ�Ĳ���", "CN_EN.txt")); //
		sb_html.append(getHtmlByList(al_kf_comm)); //

		//����-Ԫ����
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��������-����ģ��</td></tr>\r\n"); //
		ArrayList<String> al_kf_meta = new ArrayList<String>(); //
		al_kf_meta.add(getTD("�б��ü����¼��뺯��", "kf_mt_billlist.htm")); //
		al_kf_meta.add(getTD("�����������¼��뺯��", "kf_mt_billtree.htm")); //
		al_kf_meta.add(getTD("��Ƭ�����¼��뺯��", "kf_mt_billcard.htm")); //
		al_kf_meta.add(getTD("BillHtml����ʹ��", "kf_howtousebillhtmlpanel.htm")); //
		al_kf_meta.add(getTD("BillCellPanel��ʹ�ü���", "kf_howtousebillcellpanel.htm")); //
		al_kf_meta.add(getTD("BillBomPanel��ʹ�ü���", "kf_howtousebillbompanel.htm")); //
		al_kf_meta.add(getTD("����ģ��ĳ��ü��ַ���", "kf_methodofcreatemeta.htm")); //
		al_kf_meta.add(getTD("BillVO��HashVO��������ʹ��", "kf_billvoandhashvo.htm")); //
		al_kf_meta.add(getTD("����Ȩ�޵�ʹ��", "kf_howtousedataright.htm")); //
		al_kf_meta.add(getTD("���ò�ѯ�����������SQL", "kf_getsqlbybillquerypanel.htm")); //
		al_kf_meta.add(getTD("���ع�ʽ/�༭��ʽ", "kf_loadandeditformula.htm")); //
		al_kf_meta.add(getTD("�Զ�����տ���", "kf_selfdescref.htm")); //
		al_kf_meta.add(getTD("�Լ�����ؼ�����", "kf_selfdesccomponent.htm")); //
		al_kf_meta.add(getTD("����һ��������Ŀ����߼���", "")); //
		al_kf_meta.add(getTD("����һ��������Ŀ����߼���", "")); //
		sb_html.append(getHtmlByList(al_kf_meta)); //

		//����-������!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��������-������</td></tr>\r\n"); //
		ArrayList<String> al_kf_wf = new ArrayList<String>(); //
		al_kf_wf.add(getTD("�����������Ļ�������", "")); //
		al_kf_wf.add(getTD("��������ͼ���ԭ��(ʶ���������ֵ�ԭ��)", "kf_wf_design.htm")); //
		al_kf_wf.add(getTD("����ͼ����֮�����߻���˵��", "")); //
		al_kf_wf.add(getTD("�������������������", "")); //
		al_kf_wf.add(getTD("�������漰����,���߼�", "")); //
		al_kf_wf.add(getTD("����ִ����־��������", "")); //
		sb_html.append(getHtmlByList(al_kf_wf)); //

		//����-����!!
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">��������-����</td></tr>\r\n"); //
		ArrayList<String> al_kf_report = new ArrayList<String>(); //
		al_kf_report.add(getTD("��񱨱�1(���б�)", "kf_report_style1.htm"));
		al_kf_report.add(getTD("��񱨱�2(�ɺϲ���ϸ�б�)", "kf_report_style2.htm"));
		al_kf_report.add(getTD("��񱨱�3(Chartͼ��)", "")); //
		al_kf_report.add(getTD("��ά���汨��Ŀ���", "kf_report_multi.htm")); //
		al_kf_report.add(getTD("Html����Ŀ���", "")); //
		al_kf_report.add(getTD("Word����Ŀ���", "")); //
		al_kf_report.add(getTD("4�ַ�񱨱�Ŀ���", "")); //
		sb_html.append(getHtmlByList(al_kf_report)); //

		//����
		sb_html.append("<tr><td colspan=5 bgcolor=\"#CCCCCC\" class=\"td\">����</td></tr>\r\n"); //
		ArrayList<String> al_others = new ArrayList<String>(); //
		al_others.add(getTD("ΪʲôҪ������һ��֪ʶ��?", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		al_others.add(getTD("", "")); //
		sb_html.append(getHtmlByList(al_others)); //

		sb_html.append("</table>\n"); //
		sb_html.append("</body>\n"); //
		sb_html.append("</html>\n"); //
		return sb_html.toString(); //
	}

	//��Ϊ�Ժ�������м����ĳ��������,Ϊ�˶�̬���,ֻ��ʹ��List
	//����ÿ4�����һ��!�ٰ���������һ��!!
	private Object getHtmlByList(ArrayList<String> al_cp) {

		StringBuilder sb_html = new StringBuilder(); //
		int li_size = al_cp.size(); //
		int li_row = li_size / li_onerowCount; //
		int li_mod = li_size % li_onerowCount; //����
		for (int i = 0; i < li_row; i++) { //��������!!
			sb_html.append("<tr>\n"); //
			for (int j = 0; j < li_onerowCount; j++) { //
				sb_html.append((String) al_cp.get(i * li_onerowCount + j)); //
			}
			sb_html.append("</tr>\n"); //
		}
		if (li_mod > 0) { //���������!��������һ��!
			sb_html.append("<tr>\n"); //
			for (int i = 0; i < li_mod; i++) {
				sb_html.append((String) al_cp.get(li_row * li_onerowCount + i)); //
			}
			for (int i = 0; i < li_onerowCount - li_mod; i++) { //�����ÿո�,������ȫ!
				sb_html.append(getTD(null, null)); //�ո�!
			}
			sb_html.append("</tr>\n"); //
		}

		return sb_html.toString(); //
	}

	private String getTD(String _text, String _fileName) {
		if (_text == null || _text.equals("")) {
			return "<td class=\"td\" align=\"center\">&nbsp;</td>\r\n"; //
		} else {
			if (isLocalHref) {
				return "<td class=\"td\" align=\"center\"><a href=\"./" + _fileName + "\" target=\"_blank\">" + _text + "</a></td>\r\n"; //
			} else {
				return "<td class=\"td\" align=\"center\"><a href=\"./WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean&file=" + _fileName + "\" target=\"_blank\">" + _text + "</a></td>\r\n"; //
			}
		}
	}

	//��һ���ı��ļ�������
	private String getFileTextContent(String _fileName) {
		InputStream ins = this.getClass().getResourceAsStream("/cn/com/infostrategy/bs/sysapp/help/" + _fileName); //
		if (ins == null) {
			return "û���ҵ��ļ�[" + _fileName + "]!";
		}
		String str_text = TBUtil.getTBUtil().readFromInputStreamToStr(ins); //
		return str_text; //
	}

	public static void main(String[] _args) {
		HelpWebCallBean wb = new HelpWebCallBean(); //
		wb.isLocalHref = true; //
		String str_html = wb.getDefaultHtml(); //
		try {
			TBUtil.getTBUtil().writeStrToOutputStream(new FileOutputStream(new File("C:/default.htm")), str_html); //
			System.out.println("д�ļ�[C:\\default.htm]�ɹ�!!!"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
}
