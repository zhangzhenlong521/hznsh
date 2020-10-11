package cn.com.infostrategy.bs.mdata.servertmo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;

/**
 * ģ�屾��
 * @author xch
 *
 */
public class TMO_PubTemplet_1 extends AbstractTMO {

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "pub_templet_1"); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", "Ԫԭģ��"); // ģ������
		vo.setAttributeValue("templetname_e", "Templet"); // ģ������
		vo.setAttributeValue("tablename", "pub_templet_1"); // ��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); // ������
		vo.setAttributeValue("pksequencename", null); // ������
		vo.setAttributeValue("savedtablename", null); // �������ݵı���
		vo.setAttributeValue("CardWidth", "577"); // ��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); // �б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); // �б��Զ������
		vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������

		vo.setAttributeValue("TREEPK", "id"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeviewfield", "name"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeseqfield", "seq"); // �б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowroot", "Y"); // �б��Ƿ���ʾ������ť��
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETCODE"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "Code"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getItemValue(\"ID\")"); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "175"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "60,150"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "60,60"); //
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "TEMPLETNAME"); // Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
		itemVO.setAttributeValue("itemname_e", "ChineseName"); // ��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); // ��������
		itemVO.setAttributeValue("refdesc", null); // ���ն���
		itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
		itemVO.setAttributeValue("editformula", null); // �༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); // �б��ǿ��
		itemVO.setAttributeValue("cardwidth", "225"); // ��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
		itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
		itemVO.setAttributeValue("querywidth", "60,70"); //
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
