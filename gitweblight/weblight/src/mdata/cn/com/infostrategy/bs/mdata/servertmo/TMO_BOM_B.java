package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

/**
 * BOMģ�����õ�Ԫ����!
 * @author xch
 *
 */
public class TMO_BOM_B extends AbstractTMO {

	private static final long serialVersionUID = 8380921970700280061L;
	String str_selfid = null;

	public TMO_BOM_B() {
	}

	public TMO_BOM_B(String[] _selfid) {
		str_selfid = _selfid[0]; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "pub_bom_b"); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", "�ȵ���Ϣ"); // ģ������
		vo.setAttributeValue("templetname_e", "pub_bom_b"); // ģ������
		vo.setAttributeValue("tablename", "pub_bom_b"); // ��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); // ������
		vo.setAttributeValue("pksequencename", "s_pub_bom_b"); // ������
		vo.setAttributeValue("savedtablename", "pub_bom_b"); // �������ݵı���
		vo.setAttributeValue("CardWidth", "577"); // ��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); // �б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); // �б��Զ������
		vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "Id"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bomid"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "Bomid"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "bomid"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemkey"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ȵ���������"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "itemkey"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "Y"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemname"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ȵ�������"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "itemname"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "x"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "x"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "x"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "y"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "y"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "y"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "width"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "width"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "width"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "height"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "height"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "height"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindtype"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�󶨵�����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "bindtype"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", "select '�˵�' id,'�˵�' code,'�˵�' name from wltdual union all select 'Bomͼ' id,'Bomͼ' code,'Bomͼ' name from wltdual union all select 'Class��' id,'Class��' code,'Class��' name from wltdual"); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "\"Bomͼ\""); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindbomcode"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���ӵ�Bomͼ"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "bindbomcode"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "����"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", "getTableRef(\"select code id$,code,bgimgname,descr from pub_bom where id<>" + str_selfid + "\")"); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindclassname"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���ӵ�Panel��"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "bindclassname"); // ��ʾ����
		itemVO.setAttributeValue("itemtiptext", "<html>�̳���JPanel����ͨ�༴��,�����췽����֧�ֶ�����,��ע������������ʵ�ʹ��췽��������Ҫ����,����ᱨ��!!<br>����ע��������com.pushworld.MyTestPanel(\"���1\",\"���2\"),<br>���캯����public MyTestPanel(String _par1,String _par2,BillBomPanel _bomPanel){},���Ҫ�������BillBomPanel!<br>���Զ���ע��Ĳ���������!</html>"); // ��ʾ���ƣ�����������ϸ����,���򿪷���Ա������֪����ôŪ!��xch/2012-03-07��
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "400"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "bindmenu"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�󶨵Ĳ˵�"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "bindmenu"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "����ģ�����"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", "getTreeTempletRef(\"cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu\",\"id\",\"name\",\"\",\"����·������=N\");"); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", "setRefItemName(\"bindmenu\",getColValue(\"pub_menu\",\"name\",\"id\",getItemValue(\"bindmenu\")))"); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "400"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "loadtype"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��һ�²㷽ʽ"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "loadtype"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", ""); // ��������
		itemVO.setAttributeValue("refdesc", "getCommUC(\"������\",\"SQL���\",\"select '1' id,'' code,'��һ��' name from wltdual union all select '2' id,'' code,'��������' name from wltdual union all select '3' id,'' code,'ֱ���滻' name from wltdual\");"); // ���ն���
		itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", ""); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "120"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);


		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
