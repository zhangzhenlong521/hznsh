/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1_item.java,v $  $Revision: 1.15 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;

public class TMO_Pub_Templet_1_item extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;
	private boolean isAppConf = false; //�Ƿ�ʵʩģʽ!

	public TMO_Pub_Templet_1_item() {
	}

	//
	public TMO_Pub_Templet_1_item(boolean _isAppConf) {
		this.isAppConf = _isAppConf; //
	}

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_TEMPLET_1_ITEM"); //ģ����룬��������޸�
		vo.setAttributeValue("templetname", "ģ���ӱ�"); //ģ������
		vo.setAttributeValue("templetname_e", "Pub_Templet_1_Item"); //ģ������
		vo.setAttributeValue("tablename", "PUB_TEMPLET_1_ITEM"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "PK_PUB_TEMPLET_1_ITEM"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_TEMPLET_1_ITEM"); //������
		vo.setAttributeValue("savedtablename", "PUB_TEMPLET_1_ITEM"); //�������ݵı���
		vo.setAttributeValue("CardLayout", "FLOWLAYOUT"); //��Ƭ����
		vo.setAttributeValue("CardWidth", "520"); //��Ƭ���
		vo.setAttributeValue("CardBorder", "BORDER"); //��Ƭ���
		vo.setAttributeValue("Isshowcardborder", "N"); //��Ƭ�Ƿ���ʾ�߿�
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_TEMPLET_1_ITEM"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "PK_PUB_TEMPLET_1"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "PK_PUB_TEMPLET_1"); //��ʾ����
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
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "PK_PUB_TEMPLET_1"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "PK_PUB_TEMPLET_1"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "PK_PUB_TEMPLET_1"); //��ʾ����
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
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMKEY"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ItemKey"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ItemKey"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		if (isAppConf) {
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		} else {
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		}
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMNAME"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ItemName"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ItemName"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138*40"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ITEMNAME_E"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ItemName_e"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ItemName_e"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "138*40"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMTIPTEXT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ǩ����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "ItemTiptext"); //��ʾ����
		itemVO.setAttributeValue("itemtiptext", "�������ȥ��ʾ����ϸ��ǩ,���𵽰���˵��������!"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138*40"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ITEMTYPE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ؼ�����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Comp Type"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemType()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "138"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		if (isAppConf) {
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		} else {
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		}
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) { //ֻ�п���ģʽ�ż���,��ʵʩģʽ�ǲ���ʾ�����!!!
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "COMBOXDESC"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "������˵��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ComboBox Desc"); //��ʾ����
			itemVO.setAttributeValue("itemtiptext", "���µĻ��ƿ���ֱ���ڿؼ������ж���������");
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCommUC(\"���ı���\",\"��ʾ�Ŀؼ���ť\",\"������\");"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "395"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "REFDESC"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�ؼ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Ref Desc"); //��ʾ����
			itemVO.setAttributeValue("itemtiptext", "��ǰ������Ҫ��������,\r\n�����ĳ����пؼ���ʹ����һ���ֶζ���!");
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCommUC(\"���ı���\",\"��ʾ�Ŀؼ���ť\",\"����\");"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "450"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		//		itemVO = new HashVO();
		//		itemVO.setAttributeValue("itemkey", "isrefcanedit"); //Ψһ��ʶ,����ȡ���뱣��
		//		itemVO.setAttributeValue("itemname", "�����Ƿ��ܱ༭"); //��ʾ����
		//		itemVO.setAttributeValue("itemname_e", "Is Ref Can Edit"); //��ʾ����
		//		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		//		itemVO.setAttributeValue("comboxdesc", null); //��������
		//		itemVO.setAttributeValue("refdesc", null); //���ն���
		//		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		//		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		//		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		//		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		//		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		//		itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
		//		itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
		//		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		//		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		//		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		//		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		//		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		//		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		//		itemVO.setAttributeValue("iswrap", "N");
		//		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "HYPERLINKDESC"); //�ұ߳�����˵��
			itemVO.setAttributeValue("itemname", "��ӳ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "HyperLink Desc"); //Ӣ����ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "50"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "450"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISMUSTINPUT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "����������"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Must Input"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������");//�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemMustInputType()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISSAVE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���뱣��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is NeedSave"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISENCRYPT"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ���ܱ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is Encrypt"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISUNIQUECHECK"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����Ψһ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is Unique Check"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISKEEPTRACE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ����ۼ�"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Is Keep Trace"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LOADFORMULA"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "���ع�ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Load Formual"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "450"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "EDITFORMULA"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�༭��ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Edit Formual"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "450"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "SHOWORDER"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ʾ˳��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "List Seq"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "50"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "SHOWBGCOLOR"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ǰ��/������ɫ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ShowBgColor"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "DEFAULTVALUEFORMULA"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "Ĭ��ֵ��ʽ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "DefaultValue Formual"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "GROUPTITLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "������ʾ����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "GroupTitle"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "145"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		/*itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "UPLOADPATH"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�ļ��ϴ�·��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "UploadPath"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "145"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);*/

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISSHOWABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ƭ���Ƿ���ʾ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Show in Card"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��Ƭ��Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISEXPORT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ���뵼��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Export"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "select '0' id,null code,'������' name from wltdual union all select '1' id,null code,'���е���' name from wltdual union all select '2' id,null code,'ȫ�е���' name from wltdual"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "100,75"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��Ƭ��Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDWIDTH"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ƭ���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Card Width"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "50"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "80,80"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��Ƭ��Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CARDISEDITABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ƭ���Ƿ�ɱ༭"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Edit in Card"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemEditable()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��Ƭ��Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISWRAP"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ƭ���Ƿ���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Wrap"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��Ƭ��Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISSHOWABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�б����Ƿ���ʾ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Show in List"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISEXPORT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�б����Ƿ���뵼��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Export"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTWIDTH"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�б���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "List Width"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "���ֿ�"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "50"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "80,80"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "LISTISEDITABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�б����Ƿ�ɱ༭"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is Edit in List"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemEditable()"); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,100"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTISHTMLHREF"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б����Ƿ���Html"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "List is HtmlHref"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
			vector.add(itemVO);
		}

		if (!isAppConf) { //�б��Ƿ�ϲ�[���2013-07-26]
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LISTISCOMBINE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�б����Ƿ�ϲ�"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "List is Combine"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", ""); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "125,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "�б�������Ϣ");
			vector.add(itemVO);
		}
		
		
		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYITEMTYPE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯ�ؼ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Query Type"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getTempletItemType()"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYITEMDEFINE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯ�ؼ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Query Define"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCommUC(\"���ı���\",\"��ʾ�Ŀؼ���ť\",\"����\");"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "125"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "350"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYCREATETYPE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯ����������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Query CreateType"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getQueryItemCreateType()"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYCREATECUSTDEF"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�������Զ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Query CreateCustDef"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", "getCommUC(\"���ı���\",\"��ʾ�Ŀؼ���ť\",\"��ѯ������\");"); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "120"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "350"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N"); //
			itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUERYMUSTINPUT"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "�Ƿ���������ѯ����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsQueryMustInput"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc",
				"select 'N' id,'' code,'�Ǳ�����' name from wltdual union all select 'Y' id,'' code,'������' name from wltdual union all select 'A' id,'' code,'������A��' name from wltdual union all select 'B' id,'' code,'������B��' name from wltdual union all select 'C' id,'' code,'������C��' name from wltdual "); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "70"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "150,80"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������.
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������.
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "QUERYWIDTH"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��ѯ����"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Querywidth"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "80"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������.
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������.
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "QUERYDEFAULTFORMULA"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��ѯĬ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "QueryDefaultFormula"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "���ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
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
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYSHOWABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���ٲ�ѯ�Ƿ���ʾ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsQuickQueryShowable"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYWRAP"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���ٲ�ѯ�Ƿ���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Is QuickQuery Wrap"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "175,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ"); //
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISQUICKQUERYEDITABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "���ٲ�ѯ�Ƿ�ɱ༭"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsQuickQueryEditable"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "195,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ"); //
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYSHOWABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ͨ�ò�ѯ�Ƿ���ʾ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsCommQueryShowable"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYWRAP"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ͨ�ò�ѯ�Ƿ���"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "isCommQueryWrap"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "175,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ISCOMMQUERYEDITABLE"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "ͨ�ò�ѯ�Ƿ�ɱ༭"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "IsCommQueryEditable"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "195,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "N");
		itemVO.setAttributeValue("grouptitle", "��ѯ��������Ϣ");
		vector.add(itemVO);

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPISSHOWABLE"); //itemkey
			itemVO.setAttributeValue("itemname", "���Կ��Ƿ���ʾ"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "PropIsShowable"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			itemVO.setAttributeValue("grouptitle", "����������Ϣ");
			vector.add(itemVO);
		}

		if (!isAppConf) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PROPISEDITABLE"); //itemkey
			itemVO.setAttributeValue("itemname", "���Կ��Ƿ�ɱ༭"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "PropIsEditable"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "175,20"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			itemVO.setAttributeValue("grouptitle", "����������Ϣ");
			vector.add(itemVO);
		}

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "WORKFLOWISEDITABLE"); //itemkey
		itemVO.setAttributeValue("itemname", "�������Ƿ�ɱ༭"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "WorkFlowIsEditablE"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "Y"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "80"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "130,20"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("propisshowable", "Y"); //���Կ����Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("propiseditable", "Y"); //���Կ����Ƿ�ɱ༭(Y,N)
		itemVO.setAttributeValue("iswrap", "Y");
		itemVO.setAttributeValue("grouptitle", "����������Ϣ");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}
}
/**************************************************************************
 * $RCSfile: TMO_Pub_Templet_1_item.java,v $  $Revision: 1.15 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: TMO_Pub_Templet_1_item.java,v $
 * Revision 1.15  2012/09/14 09:22:58  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.2  2012/08/30 07:25:57  Administrator
 * ȥ����һ��û�õ��İ�
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.14  2012/02/06 12:01:07  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.13  2011/11/04 13:36:22  xch123
 * *** empty log message ***
 *
 * Revision 1.12  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.10  2011/06/14 11:46:19  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2011/06/14 09:54:04  xch123
 * *** empty log message ***
 *
 * Revision 1.8  2011/04/08 12:15:12  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2011/04/08 11:54:09  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2011/04/08 08:48:20  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2011/03/23 12:17:40  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2011/03/22 09:55:27  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/01/27 09:55:53  xch123
 * ��ҵ����ǰ����
 *
 * Revision 1.2  2010/12/28 10:30:11  xch123
 * 12��28���ύ
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2010/04/21 02:32:56  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.6  2010/04/20 13:09:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/04/16 07:50:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/04/16 03:19:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/04/12 06:20:20  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.2  2010/04/08 10:11:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.10  2010/03/23 12:07:58  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.9  2010/03/18 10:41:33  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.8  2010/03/16 08:28:00  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.7  2010/03/05 09:13:02  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.6  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.4  2010/01/26 10:41:26  gaofeng
 * *** empty log message ***
 *
 * Revision 1.3  2009/12/09 06:20:38  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2009/12/09 06:08:34  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/13 07:28:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/08/14 03:57:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/18 01:57:03  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2009/01/12 08:38:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/09/27 02:22:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/09/05 08:07:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/08/27 10:04:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:11  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.9  2008/06/24 02:46:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.8  2008/06/23 09:05:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2008/06/23 08:36:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/22 02:41:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/04/12 16:32:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/04/12 05:04:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/03/05 06:52:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/02/29 17:29:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:24  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/10/09 03:03:13  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/21 07:45:12  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:34  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/27 06:03:01  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:12:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
