package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.workflow.jepfunctions.AddWFRouterMarkValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetOneTransThrowCount;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFBillItemValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFRouterMarkCount;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFRouterMarkValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.SetWFRouterMarkValue;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetParAsMap;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

public class JepFormulaParseAtWorkFlow extends JepFormulaParse {

	@Override
	protected byte getJepType() {
		return WLTConstants.JEPTYPE_BS;
	}

	/**
	 * ���췽��
	 * @param __billvo 
	 * @param callVO 
	 */
	public JepFormulaParseAtWorkFlow(WFParVO _callVO, String _prinstanceid, String _dealpoolid, BillVO _billvo) {
		initNormalFunction(); //���õļ��㺯��!!!
		parser.addFunction("setWFRouterMarkValue", new SetWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //���ù����������·�ɱ��!
		parser.addFunction("addWFRouterMarkValue", new AddWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //����һ��·�ɱ��!
		parser.addFunction("getWFRouterMarkValue", new GetWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //���ù����������·�ɱ��!
		parser.addFunction("getWFRouterMarkCount", new GetWFRouterMarkCount(_callVO, _prinstanceid, _dealpoolid)); //ȡ��ĳ��·�ɱ�ǵĸ���!!
		parser.addFunction("getWFBillItemValue", new GetWFBillItemValue(_callVO, _prinstanceid, _dealpoolid, _billvo)); //���ù����������·�ɱ��!
		parser.addFunction("getOneTransThrowCount", new GetOneTransThrowCount(_callVO, _prinstanceid, _dealpoolid)); //���ù����������·�ɱ��!
		parser.addFunction("setExtConfMap", new GetParAsMap()); //������չ����
	}
}
