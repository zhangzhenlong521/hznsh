package cn.com.infostrategy.ui.sysapp.corpdept;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ��������BillTreePanel,�µĻ���Ȩ��ģ��!!!�Ժ󽫳�Ϊˮƽ��Ʒ�ı�׼!!!!!
 * @author xch
 *
 */
public class CorpDeptBillTreePanel extends BillTreePanel {

	private static final long serialVersionUID = 1L; //

	private int li_granuType = -1; //��������(��3������),1-����,����,��ҵ��;2-����,���в���,��ҵ��,��ҵ���ֲ�,����(��õ�),֧��;3-����

	private boolean isFilterByLoginUser = false;

	private boolean custConditionIsNull = false; //

	public CorpDeptBillTreePanel() {
		super("pub_corp_dept_CODE1"); //������
	}

	/**
	 * �����������Ƿ�Ȩ�޹���,���ɲ�ͬ��ʽ����!!
	 * @param _granuType
	 * @param _isFilterByLoginUser
	 */
	public CorpDeptBillTreePanel(int _granuType, boolean _isFilterByLoginUser) {
		super("pub_corp_dept_Ref"); //������
		this.li_granuType = _granuType; //
		this.isFilterByLoginUser = _isFilterByLoginUser; //
	}

	/**
	 * �����������Ƿ�Ȩ�޹���,���ɲ�ͬ��ʽ����!!
	 * @param _granuType
	 * @param _isFilterByLoginUser
	 */
	public CorpDeptBillTreePanel(String _templetCode, int _granuType, boolean _isFilterByLoginUser) {
		super(_templetCode); //������
		this.li_granuType = _granuType; //
		this.isFilterByLoginUser = _isFilterByLoginUser; //
	}

	@Override
	/**
	 * �Զ���������������Ϊ��,�������ʾ"���з�Χ"��ť������������ҪΪcorpdept in ()ƴһ���ϳ���SQL,���ܻ�����
	 */
	public String getCustCondition() {
		String str_granuCondition = null; ////
		if (li_granuType == 1) { ////����ǵ�һ������ģʽ
			str_granuCondition = "corptype in ('����','���в���','����','��ҵ��')"; //
		} else if (li_granuType == 2) { ////����ǵڶ�������ģʽ
			str_granuCondition = "corptype in ('����','���в���','��ҵ��','��ҵ���ֲ�','����','���в���','֧��')"; //..
		} else if (li_granuType == 3) { //�����3,��Ϊ��,Ϊ�վͱ�ʾ������е�!!
			str_granuCondition = null; //
		} else if (li_granuType == 4) {
			str_granuCondition = "corptype in ('����','һ������')";
		}

		String str_filterCondition = null; //Ȩ�޹���,�����п�����,�����п������е�!
		//�����Ҫ����Ȩ�޹���..
		if (isFilterByLoginUser) { //
			str_filterCondition = UIUtil.getLoginUserBLDeptCondition(); //
		}

		String str_allcondition = null; //
		if (str_granuCondition == null) { //��������ȹ���
			if (str_filterCondition == null) { //������߶�Ϊ��,�����κδ���!!
			} else {
				str_allcondition = str_filterCondition; //
			}
		} else {
			if (str_filterCondition == null) {
				str_allcondition = str_granuCondition; //
			} else {
				str_allcondition = str_granuCondition + " and " + str_filterCondition; //
			}
		}
		if (str_allcondition == null) {
			custConditionIsNull = true; //Ϊ��
		} else {
			custConditionIsNull = false; //
		}
		return str_allcondition; //
	}

	public boolean isCustConditionIsNull() {
		return custConditionIsNull;
	}

}
