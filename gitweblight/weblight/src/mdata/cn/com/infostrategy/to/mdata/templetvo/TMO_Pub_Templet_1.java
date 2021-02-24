/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1.java,v $  $Revision: 1.21 $  $Date: 2012/11/19 06:16:28 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;

public class TMO_Pub_Templet_1 extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private boolean isAppConf = false; //�Ƿ�ʵʩģʽ!

	public TMO_Pub_Templet_1() {
	}

	public TMO_Pub_Templet_1(boolean _isAppConf) {
		this.isAppConf = _isAppConf; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_TEMPLET_1"); //ģ����룬��������޸�
		vo.setAttributeValue("templetname", "ģ������"); //ģ������
		vo.setAttributeValue("templetname_e", "Pub_Templet_1"); //ģ��Ӣ������
		vo.setAttributeValue("datasourcename", null); //����Դ����!!!�ձ�ʾĬ������Դ!!
		vo.setAttributeValue("tablename", "pub_templet_1"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("dataconstraint", null); //����Ȩ��
		vo.setAttributeValue("pkname", "pk_pub_templet_1"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_TEMPLET_1"); //������
		vo.setAttributeValue("savedtablename", "pub_templet_1"); //�������ݵı���
		vo.setAttributeValue("CardLayout", "FLOWLAYOUT"); //��Ƭ����
		vo.setAttributeValue("CardWidth", "1000"); //��Ƭ���
		vo.setAttributeValue("CardBorder", "BORDER"); //��Ƭ�ؼ��ı߿�
		vo.setAttributeValue("Isshowcardborder", "N"); //��Ƭ�Ƿ���ʾ�߿�
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Isshowlistquickquery", "N"); //�б��Ƿ���ʾ���ٲ�ѯ
		vo.setAttributeValue("isshowlistcustbtn", "Y"); //�б��Ƿ���ʾ�Զ��尴ť��
		vo.setAttributeValue("Listcustbtndesc", "comm_listselect;"); //�б��Զ��尴ť����
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "pk_pub_templet_1"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "PK_ID"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ģ�����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Templet Code"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ģ������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Templet Name"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", null);
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TEMPLETNAME_E"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ģ��Ӣ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "English Name"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATASOURCENAME"); //����Դ����
			itemVO.setAttributeValue("itemname", "����Դ����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "DataSourceName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TABLENAME"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯ���ݱ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Query TabName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "TABLE2:Select t1.tname ����#,t1.tname ����,nvl(t2.comments, t1.tname) ˵��, t1.tabtype ����  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE', 'VIEW') and 1 = 1 order by ����"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATACONSTRAINT"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "��ѯʱSQL����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Filter Condition"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "AUTOLOADCONSTRAINT"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "�Զ�����ʱSQL����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "AutoLoadConstraint"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ORDERCONDITION"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "��������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Order Condition"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "75"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "75,85"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "AUTOLOADS"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "�Զ���������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Autoloads"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "25"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "110,30"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);
			

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATASQLANDORCONDITION"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯ����������Ȩ��sqlƴ�ӷ�ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "DATASQLANDORCONDITION"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "select 'and' id,'and' code,'and' name from wltdual union all select 'or' id,'or' code,'or' name from wltdual"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200,90"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATAPOLICY"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "����Ȩ�޲���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Datapolicy"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "����"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getTableRef(\"select id ����$,'' code$,name Ȩ�޲��� from pub_datapolicy order by seq\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", "setRefItemName(\"DATAPOLICY\",getColValue(\"pub_datapolicy\",\"name\",\"id\",getItemValue(\"DATAPOLICY\")))"); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DATAPOLICYMAP"); //����Ȩ��ӳ��
			itemVO.setAttributeValue("itemname", "����ӳ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "DatapolicyMap"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�Զ������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCustRef(\"cn.com.infostrategy.ui.mdata.DataPolicyMapConfRefDialog\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "300"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "BSDATAFILTERCLASS"); //����Ȩ��
			itemVO.setAttributeValue("itemname", "BS�����ݹ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Bsdatafilterclass"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�Զ������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCustRef(\"��̬�붯̬�ű�����\",\"cn.com.infostrategy.bs.mdata.AbstractBillDataBSFilter\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "138"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "300"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "BSDATAFILTERISSQL"); //BS�˹������Ƿ񷵻�SQL
			itemVO.setAttributeValue("itemname", "BS�������Ƿ񷵻�SQL"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "bsdatafilterissql"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "170,30"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "SAVEDTABLENAME"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�������ݱ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Saved Table"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "TABLE2:Select t1.tname ����#,t1.tname ����,nvl(t2.comments, t1.tname) ˵��, t1.tabtype ����  From tab t1, user_tab_comments t2 where t1.tname = t2.table_name(+) and t1.tabtype in ('TABLE') and 1 = 1 order by ����"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PKNAME"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "PK FieldName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "80,35"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PKSEQUENCENAME"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "PK SequenceName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", ""); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "100,135"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TOSTRINGKEY"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ToString����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "TOSTRINGKEY"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", ""); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "100,75"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDLAYOUT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "CardLayout"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "select 'FLOWLAYOUT' id,'FLOWLAYOUT' code,'FLOWLAYOUT' name from wltdual union all select 'XYLAYOUT' id,'XYLAYOUT' code,'XYLAYOUT' name from wltdual"); //��������
			itemVO.setAttributeValue("refdesc", null); //��������
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTOPEBAR"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ���ݿⰴť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "IsListShowBtn"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "180,60"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", null);
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDWIDTH"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Card Width"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDBORDER"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ�ؼ��߿���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "CardBorder"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "select 'BORDER' id,'BORDER' code,'BORDER' name from wltdual union all select 'LINE' id,'LINE' code,'LINE' name from wltdual"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,80"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCARDBORDER"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾDebug��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "IsShowDebugBorder"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCARDCUSTBTN"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ�Զ���ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ISSHOWCARDCUSTBTN"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cardsaveifcheck"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ���治��������֤"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "cardsaveifcheck"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CARDCUSTPANEL"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ�Զ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Card Cust Panel"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "425"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Cardcustbtndesc"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ��ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Cardcustbtndesc"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //�ؼ�����,ע���������
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"ע�ᰴť\",\"\",\"4\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Cardinitformula"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ��ʼ����ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Cardinitformula"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //�ؼ�����,���ı���
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "cardsaveselfdesccheck"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��Ƭ�Զ�����֤"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "cardsaveselfdesccheck"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //�ؼ�����,���ı���
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);

			/*itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "GROUPISONLYONE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ�ֻ��ͬʱչ��һ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Groupisonlyone"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,30"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "��Ƭ����");
			vector.add(itemVO);*/

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTPAGEBAR"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б��Ƿ���ʾ��ҳ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is Show ListPageBar"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "140,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISLISTPAGEBARWRAP"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ҳ���Ƿ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is PageBar Wrap"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "140,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTQUICKQUERY"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ��ѯ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Isshowlistquickquery"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "160,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO); //

			//��Ϊ������ʱ����,��ʱ����!
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWCOMMQUERYBTN"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾͨ�ò�ѯ��ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Isshowcommquerybtn"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "160,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISCOLLAPSEQUICKQUERY"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ������ѯ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Iscollapsequickquery"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "160,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ListHeaderIsGroup"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ͷ�Ƿ����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "listheaderisgroup"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "140,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Islistautorowheight"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ��Զ��Ÿ�"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Islistautorowheight"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "140,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSHOWLISTCUSTBTN"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ���ٰ�ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ISSHOWLISTCUSTBTN"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "160,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTHEADHEIGHT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б��ͷ�߶�"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "listheadheight"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "160,25"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTROWHEIGHT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б��и�"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ListRowHeight"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "100,125"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTCUSTPANEL"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б��Զ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "List Cust Panel"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "120,700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Listcustbtndesc"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б�ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Listcustbtndesc"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //�ؼ�����,ע���������
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"ע�ᰴť\",\"\",\"4\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);
			
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "listbtnorderdesc"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б�ť����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "listbtnorderdesc"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD); //�ؼ�����,ע���������
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", ""); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "Listinitformula"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б��ʼ����ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Listinitformula"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_BIGAREA); //�ؼ�����,���ı���
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "listweidudesc"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ά�ȶ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "listweidudesc"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����,���ı���
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO(); // gaofeng �Զ��������
			itemVO.setAttributeValue("itemkey", "definerenderer"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Զ��������"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "definerenderer"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����,���ı���
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); // �Ƿ���
			itemVO.setAttributeValue("grouptitle", "�б����");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEPK"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "���������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "TreePK"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEPARENTPK"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����常������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "TreeParentPK"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEVIEWFIELD"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�������ʾ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeviewfield"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREESEQFIELD"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�����������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeseqfield"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISSHOWROOT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeisshowroot"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISSHOWTOOLBAR"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ʾ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeisshowtoolbar"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISONLYONE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ�ֻ��ͬʱչ��һ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeisonlyone"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,30"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TREEISCHECKED"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ��ǹ�ѡ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treeischecked"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150,30"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Ͳ���");
			vector.add(itemVO);

			itemVO = new HashVO(); //
			itemVO.setAttributeValue("itemkey", "Treecustbtndesc"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "���Ͱ�ť"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Treecustbtndesc"); //��ʾ����
			itemVO.setAttributeValue("itemtype", WLTConstants.COMP_REFPANEL_REGFORMAT); //�ؼ�����,ע���������
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getRegFormatRef(\"ע�ᰴť\",\"\",\"4\")"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "700"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y"); //�Ƿ���
			itemVO.setAttributeValue("grouptitle", "���Ͳ���"); //
			vector.add(itemVO); //

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPBEANCLASSNAME"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����BeanClass"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "PropBeanClassName"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "400"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "���Կ����"); //�������
			vector.add(itemVO);
			
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "WFCUSTEXPORT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�������Զ��嵼��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Wfcustexport"); //��ʾ����
			itemVO.setAttributeValue("ITEMTIPTEXT", "<html>����������������=aa,bb,cc;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����,���Ҹ�����,���쵼��<br>�����ɱ���@һ�����С�����=itemkey4,itemkey5,itemkey6;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����,���Ҹ�����,���쵼��</html>"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "������"); //�������
			vector.add(itemVO);
			
		}

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
