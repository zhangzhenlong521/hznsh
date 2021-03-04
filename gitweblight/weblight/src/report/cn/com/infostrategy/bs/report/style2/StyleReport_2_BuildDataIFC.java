package cn.com.infostrategy.bs.report.style2;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * ��񱨱�2�����ݹ�����
 * ��񱨱�2��������һ��BillCell�����,���ݲ�ѯ��������һ��BillCellPanel
 * ���ݹ�������Ҫָ�����ص����ݼ�,������,��Ҫ������ͬ�ϲ�����..
 * �Ժ󻹿���ָ������С�Ƶ���
 * @author xch
 *
 */
public interface StyleReport_2_BuildDataIFC {

	/**
	 * �������
	 * @return
	 */
	public String getTitle(); //

	/**
	 * �������
	 * @return ����һ��n�����е�����,���е�һ��������,�����뷵�ص�HashVO�е�����Ʒ��,�ڶ���ָ���Ƿ��ǵ���(Y/N),��������ָ���Ƿ�������("Y"/"N")
	 * ���� new String[][]{{"����","N","N"},{"����","Y","N"}}
	 */
	public String[][] getSortColumns(); //

	/**
	 * ������Щ����Ҫ������ͬ�е�ֵ���кϲ���ʾ!!,������ϲ�����Ӧ�ñ��붨������
	 * @return ָ����Ҫ��һ�ϲ�����,���� new String[]{"����","����"}
	 */
	public String[] getSpanColumns(); //��Ҫ����

	/**
	 * ����������,������Ҫ�������������ϲ�����,ֻҪ��ҵ������"ת"��������!!!
	 * @param _condition   ��ѯ���������Ĳ�ѯ����!!!
	 * @param _loginUserVO ��¼��Ա�������Ϣ
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}
