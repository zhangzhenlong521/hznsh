package cn.com.infostrategy.bs.sysapp.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

public class TMO_Pub_ComboBoxDict extends AbstractTMO {

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "������������"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "������������"); //ģ������
		vo.setAttributeValue("templetname_e", "������������"); //ģ������
		vo.setAttributeValue("tablename", "PUB_COMBOBOXDICT"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "PK_PUB_COMBOBOXDICT"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_COMBOBOXDICT"); //������
		vo.setAttributeValue("savedtablename", "PUB_COMBOBOXDICT"); //�������ݵı���
		vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		vo.setAttributeValue("ordercondition", " seq asc "); //��Ƭ�Զ������
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_COMBOBOXDICT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "PK_PUB_COMBOBOXDICT"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "PK_PUB_COMBOBOXDICT"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "70"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "200"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ʵֵ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ID"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "110"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "CODE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "110"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "NAME"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "110"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SEQ"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "˳��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "SEQ"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "60"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);
		
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "DESCR"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "DESCR"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "200"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "420"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "TYPE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("listwidth", "110"); //�б�ʱ���
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("issave", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]); //
	}

}
