/**************************************************************************
 * $RCSfile: TMO_Pub_Menu.java,v $  $Revision: 1.13 $  $Date: 2012/09/19 11:11:14 $
 **************************************************************************/
package cn.com.infostrategy.to.sysapp.login;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_Pub_Menu extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_MENU_CODE_1"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "�˵�����"); //ģ������
		vo.setAttributeValue("templetname_e", "Menu Manager"); //ģ������
		vo.setAttributeValue("tablename", "VI_MENU"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_MENU"); //������
		vo.setAttributeValue("savedtablename", "PUB_MENU"); //�������ݵı���
		vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

		vo.setAttributeValue("TREEPK", "id"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeviewfield", "name"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowtoolbar", "Y"); //������ʾ������
		vo.setAttributeValue("Treeisonlyone", "N"); //������ʾ������
		vo.setAttributeValue("Treeseqfield", "seq"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowroot", "Y"); //�б��Ƿ���ʾ������ť��
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Id"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�˵�����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Code"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(getItemValue(\"ID\"))"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "325"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ChineseName"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ENAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "Ӣ������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "EnglishName"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PARENTMENUID"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���˵�ID"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ParentMenuId"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PARENTMENUID_NAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���˵�"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ParentMenuName"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", "Select Id ����, Code ����, Name ����, Parentmenuid ���˵�,Seq  From pub_menu where 1=1 Order By seq;���˵�=����"); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", ""); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SEQ"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ʾ˳��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ShowSeq"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "50"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "USECMDTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���õ�·��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "USECMDTYPE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "Select '1' id,'1' code,'��һ������' name from wltdual union all Select '2' id,'2' code,'�ڶ�������' name from wltdual union all Select '3' id,'3' code,'����������' name from wltdual"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getComBoxItemVO(\"1\",\"1\",\"��һ������\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMANDTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·������1"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CommandType"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getMenuCommandType()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", "resetItemValue(\"COMMAND\");"); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMAND"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·��1"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Command"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�Զ������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.mdata.styletemplet.config.StyleConfigRefDialog\")"); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "425"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "60,222"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMANDTYPE2"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·������2"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CommandType2"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getMenuCommandType()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", "resetItemValue(\"COMMAND2\");"); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMAND2"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·��2"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Command2"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�Զ������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.mdata.styletemplet.config.StyleConfigRefDialog\")"); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "425"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "60,222"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMANDTYPE3"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·������3"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CommandType3"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getMenuCommandType()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", "resetItemValue(\"COMMAND3\");"); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMAND3"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "·��3"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Command3"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�Զ������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.mdata.styletemplet.config.StyleConfigRefDialog\")"); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "425"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "60,222"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "HELPFILE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�����ĵ�"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "helpfile"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "Office�ؼ�"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", ""); //��������
		/***
		 * Gwang 2012-09-17�޸�
		 * ��ϵͳ�еİ����ĵ��̶�����webroot��helpĿ¼��[E:\TomCat5.5.23\webapps\icam\help\doc] 
		 * ����Ҫ�ֶ����ƹ�ȥ����ϵͳ����ʱ�ĸ��Ӷ�, �ر����ڲ�Ʒ��װ��ʱ
		 */
		String webrootPath = System.getProperty("APP_DEPLOYPATH");
		itemVO.setAttributeValue("refdesc", "getOfficeRef(\"doc\",\"\",\"\",\"\",getParAsMap(\"�洢Ŀ¼\",\""+webrootPath+"help/doc\",\"�Ƿ����·��\",\"Y\"));"); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "350"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISALWAYSOPEN"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ���Զ����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Isalwaysopen"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISAUTOSTART"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ�������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsAutoStart"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"N\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ICON"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�˵�ͼ��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "MenuIcon"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "ͼƬѡ���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		//�����˵�������ֱ�Ӽ����,�������ټ�����!
		//		itemVO = new HashVO();
		//		itemVO.setAttributeValue("itemkey", "ISINMENUBAR"); //Ψһ��ʶ,����ȡ���뱣��
		//		itemVO.setAttributeValue("itemname", "�Ƿ�˵�������ʾ"); //��ʾ����
		//		itemVO.setAttributeValue("itemname_e", "Isinmenubar"); //��ʾ����
		//		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		//		itemVO.setAttributeValue("comboxdesc", null); //��������
		//		itemVO.setAttributeValue("refdesc", null); //���ն���
		//		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		//		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		//		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		//		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		//		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		//		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"N\")"); //Ĭ��ֵ��ʽ
		//		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		//		itemVO.setAttributeValue("cardwidth", "120,20"); //��Ƭʱ���
		//		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		//		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		//		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		//		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		//		itemVO.setAttributeValue("iswrap", "N");
		//		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "toolbarimg"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������ͼƬ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ToolBarImg"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "ͼƬѡ���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SHOWINTOOLBAR"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ����������ʾ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "showintoolbar"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"N\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "120,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISEXTEND"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ���չ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ISEXTEND"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"N\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "EXTENDHEIGHT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��չ�߶�"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "EXTENDHEIGHT"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"700\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,70"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "OPENTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�򿪷�ʽ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "OPENTYPE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "select 'FRAME' id,'FRAME' code,'Frame' name from wltdual union all select 'TAB' id,'TAB' code,'TAB' from wltdual"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getComBoxItemVO(\"TAB\",\"\",\"TAB\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "80,100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "OPENTYPEWEIGHT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "FRAME���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "OPENTYPEWEIGHT"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"800\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y"); //��Ƭ�Ƿ���!
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "OPENTYPEHEIGHT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "FRAME�߶�"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "OPENTYPEHEIGHT"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(\"600\")"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CONF"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�˵�����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Conf"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "475*70"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "COMMENTS"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "˵��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Descr"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "475*100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
/**************************************************************************
 * $RCSfile: TMO_Pub_Menu.java,v $  $Revision: 1.13 $  $Date: 2012/09/19 11:11:14 $
 *
 **************************************************************************/
