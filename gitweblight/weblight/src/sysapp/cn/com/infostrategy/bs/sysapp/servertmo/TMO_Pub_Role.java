package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_Pub_Role extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_ROLE_CODE1"); //ģ����룬��������޸�
		vo.setAttributeValue("templetname", "��ɫ����"); //ģ������
		vo.setAttributeValue("tablename", "PUB_ROLE"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_ROLE"); //������
		vo.setAttributeValue("savedtablename", "PUB_ROLE"); //�������ݵı���
		vo.setAttributeValue("datapolicy", "��ɫ��ѯ����"); //��ѯ����
		vo.setAttributeValue("datapolicymap", "���˷�ʽ=��������;�����ֶ���=createdept;"); //����ӳ�䡾���/2017-06-26��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		vo.setAttributeValue("isshowlistquickquery", "Y"); //��Ƭ�Զ������
		vo.setAttributeValue("ordercondition", "ROLETYPE,CODE"); //��Ƭ�Զ������
		vo.setAttributeValue("autoloads", "-1"); //��Ƭ�Զ������
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ

		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ROLETYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ɫ����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", WLTConstants.COMP_COMBOBOX); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "select id,code,name from pub_comboboxdict where type='��ɫ����' order by seq"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,150"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ɫ����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,150"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ɫ����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "Y"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,150"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "DESCR"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ע"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "300*100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isCommQueryShowable", "Y"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "Y"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryWrap", "Y"); //ͨ�ò�ѯ�Ƿ���
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPPWD"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������Ա����"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,100"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��������Ա�������");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPCORPTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ֻ������Щ��������"); //��ʾ����
		itemVO.setAttributeValue("itemtiptext", "ֻ����Щ�������͵��˲ſ���,����ֻ�������е���!ȡֵ���ǻ�������,����:���в���;һ�����в���;"); //�ؼ�����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,100"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��������Ա�������");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SPDATAOBJS"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ӧ�����ݶ���"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("itemtiptext", "�ó�������Աֻ����Щ���ݱ���Ч,����cmp_;rule_,��ģ���еĲ�ѯ��򱣴��ֻҪ��Ӧ��,����ܿ���!"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "400*70"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "N"); //���ٲ�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isQuickQueryEditable", "N"); //���ٲ�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("isCommQueryShowable", "N"); //ͨ�ò�ѯ�Ƿ���ʾ
		itemVO.setAttributeValue("isCommQueryEditable", "N"); //ͨ�ò�ѯ�Ƿ�ɱ༭
		itemVO.setAttributeValue("querywidth", "100,100"); //���ٲ�ѯʱ�Ŀ��
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��������Ա�������");
		vector.add(itemVO);
		
		

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
