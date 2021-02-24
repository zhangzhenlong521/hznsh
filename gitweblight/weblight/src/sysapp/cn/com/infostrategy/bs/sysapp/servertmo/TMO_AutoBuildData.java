package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_AutoBuildData extends AbstractTMO {

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_AUTOBUILDEDATA_CODE1"); //ģ����룬��������޸�
		vo.setAttributeValue("templetname", "�Զ���������"); //ģ������
		vo.setAttributeValue("tablename", "wltdual"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "id"); //������
		vo.setAttributeValue("pksequencename", "S_wltdual"); //������
		vo.setAttributeValue("savedtablename", "wltdual"); //�������ݵı���
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
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "itemkey"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ֶ�"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "name"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ֶ�����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "info"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ֶ�˵��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "90"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "type"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "select '����' id, '����' code, '����' name from wltdual " +
				" union all select '��ֵ' id, '��ֵ' code, '��ֵ' name from wltdual union all select '��ֵ' id, '��ֵ' code, '��ֵ' name from wltdual"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "90"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "value"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ȡֵ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
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
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "autono"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ�����ˮ��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", ""); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
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
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);
		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
