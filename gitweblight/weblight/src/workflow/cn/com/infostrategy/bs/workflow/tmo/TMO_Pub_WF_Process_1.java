package cn.com.infostrategy.bs.workflow.tmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

//pub_wf_process��VO
public class TMO_Pub_WF_Process_1 extends AbstractTMO {

	private static final long serialVersionUID = 1L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_WF_PROCESS"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "������"); //ģ������
		vo.setAttributeValue("tablename", "pub_wf_process"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "id"); //������
		vo.setAttributeValue("pksequencename", null); //������
		vo.setAttributeValue("savedtablename", "pub_wf_process"); //�������ݵı���
		vo.setAttributeValue("ORDERCONDITION", "id");
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		vo.setAttributeValue("isshowlistquickquery", "Y"); //��ʾ���ٲ�ѯ���
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
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "225"); //�б��ǿ��..
		itemVO.setAttributeValue("cardwidth", "125"); //��Ƭʱ���..
		itemVO.setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N).
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N).
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "code"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���̱���"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "225"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "100,135"); //
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "name"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��������"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "300"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "125"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "100,135"); //
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
