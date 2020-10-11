package cn.com.infostrategy.bs.sysapp.runtime;

import java.util.ArrayList;

import cn.com.infostrategy.to.sysapp.runtime.RtActionTempletVO;

/**
 * ���嶯̬�ű���ģ�嶨�幤��!
 * @author Administrator
 *
 */
public class RuntimeActionTempletFactory {

	private static RuntimeActionTempletFactory instance = null; //

	private RuntimeActionTempletFactory() {
	}

	//ȡ��һ��ʵ��!!
	public static RuntimeActionTempletFactory getInstance() {
		if (instance == null) {
			instance = new RuntimeActionTempletFactory(); //
		}
		return instance; //
	}

	//�������еĶ�ִ̬�нű���ģ��
	public RtActionTempletVO[] getAllTempletVOs() {
		ArrayList al_templets = new ArrayList(); //
		RtActionTempletVO templetVO = null;

		//����Ȩ�޹���
		templetVO = new RtActionTempletVO(); //
		templetVO.setTempletName("cn.com.infostrategy.bs.mdata.AbstractBillDataBSFilter"); //
		templetVO.setSuperClassType("extends"); //
		templetVO.setPackageSubfix("bs"); //��BS�˵�!!!��Ҫ�Ӱ����Ϳ�����!!
		templetVO.setClassPrefix("BSFilter_"); //
		templetVO.setImported(new String[] { //
				"cn.com.infostrategy.to.common.HashVO", //
						"cn.com.infostrategy.to.common.TBUtil", //
						"cn.com.infostrategy.bs.common.CommDMO", //
				}); //

		templetVO.setFunctiondoc("���ݹ�����֮�м����,ʵ�ָ÷���,Ȼ�����ø���HashVO��setVisible,ʵ�ֹ���Ч��"); //
		templetVO.setFunctionName("public void filterBillData(HashVO[] _hvs) throws Exception"); //
		templetVO.setFunctionContent(new String[] { //
				"//for (int i=0;i<_hvs.length;i++) {", //
						"//  _hvs[i].setVisible(true);", //
						"//}", //
				}); ///
		al_templets.add(templetVO); //

		//��ť����¼�!!
		templetVO = new RtActionTempletVO(); //
		templetVO.setTempletName("cn.com.infostrategy.ui.mdata.WLTActionListener"); //
		templetVO.setSuperClassType("implements"); //
		templetVO.setPackageSubfix("ui"); //��BS�˵�!!!��Ҫ�Ӱ����Ϳ�����!!
		templetVO.setClassPrefix("BtnAL_"); //��ť����¼�
		templetVO.setImported(new String[] { //
				"cn.com.infostrategy.ui.common.UIUtil", //
						"cn.com.infostrategy.to.common.TBUtil", //
						"cn.com.infostrategy.to.mdata.BillVO", //
						"cn.com.infostrategy.to.common.HashVO", //
						"cn.com.infostrategy.ui.common.MessageBox", //
				}); //

		templetVO.setFunctiondoc("��ť�������,���Դ�event��ȡ�����������!!"); //
		templetVO.setFunctionName("public void actionPerformed(cn.com.infostrategy.ui.mdata.WLTActionEvent _event) throws Exception"); //
		templetVO.setFunctionContent(new String[] { //
				"return;", //
				}); ///
		al_templets.add(templetVO); //

		//�б�ѡ��仯�¼�

		//����ѡ���¼��仯

		//��Ƭ��ʼ��ʱ�߼�

		//

		return (RtActionTempletVO[]) al_templets.toArray(new RtActionTempletVO[0]); //
	}

	/**
	 * ȡ��ĳһ��ģ�嶨��
	 * @param _name
	 * @return
	 */
	public RtActionTempletVO getTempletVOByName(String _name) {
		RtActionTempletVO[] allVOs = getAllTempletVOs(); //
		for (int i = 0; i < allVOs.length; i++) {
			if (allVOs[i].getTempletName().equals(_name)) {
				return allVOs[i]; //
			}
		}
		return null; //
	}

	/**
	 * �õ�����ģ���Java����...
	 * @param _name
	 * @return
	 */
	public String getWholeTempletJavaCode(String _name) {
		RtActionTempletVO templetVO = getTempletVOByName(_name); //
		if (templetVO == null) {
			return null;
		}

		StringBuffer sb_code = new StringBuffer(); //
		sb_code.append("package cn.com.weblight.runtime;  \r\n"); //����
		sb_code.append("\r\n"); //
		for (int i = 0; i < templetVO.getImported().length; i++) {
			sb_code.append("import " + templetVO.getImported()[i] + "; \r\n"); //
		}
		sb_code.append("\r\n"); //

		sb_code.append("public class " + templetVO.getClassPrefix() + "_��� " + templetVO.getSuperClassType() + " " + templetVO.getTempletName() + " {\r\n"); //
		sb_code.append("\r\n"); //
		sb_code.append("//" + templetVO.getFunctiondoc() + "\r\n"); //

		sb_code.append(templetVO.getFunctionName() + " { \r\n"); //

		for (int i = 0; i < templetVO.getFunctionContent().length; i++) {
			sb_code.append(templetVO.getFunctionContent()[i] + "\r\n"); //
		}

		sb_code.append("}\r\n"); //end function
		sb_code.append("\r\n"); //
		sb_code.append("}\r\n"); //end class
		return sb_code.toString(); //
	}

	//�õ�����ģ�������嵥!!
	public String[] getAllTempletNames() {
		RtActionTempletVO[] allVOs = getAllTempletVOs(); //
		String[] str_names = new String[allVOs.length]; //
		for (int i = 0; i < str_names.length; i++) {
			str_names[i] = allVOs[i].getTempletName(); //
		}
		return str_names; //
	}

	public static void main(String[] _args) {
		System.out.println(RuntimeActionTempletFactory.getInstance().getWholeTempletJavaCode("����Ȩ�޹���")); //

	}
}
