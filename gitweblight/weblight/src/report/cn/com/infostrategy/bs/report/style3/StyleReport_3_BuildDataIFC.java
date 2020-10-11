package cn.com.infostrategy.bs.report.style3;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * ��񱨱�3�����ݹ����� ��񱨱�3��������һ��BillChart�����,���ݲ�ѯ��������һ��BillChartPanel
 * ���ݹ�������Ҫָ�����ص����ݼ�,�У���,��Ҫ��ѯ��������
 * 
 * @author xch
 * 
 */
public interface StyleReport_3_BuildDataIFC {

	//select(c1),count(*),sum(c1),avg(c1)
	
	public static String SELECT = "select"; //
	public static String COUNT = "count"; //
	public static String SUM = "sum"; //
	public static String MAX = "max"; //
	public static String MIN = "min"; //
	
	public String getColHeadName(); // ��Ҫ�е�����
	public String getRowHeadName(); // ��Ҫ�е�����
	
	public String getComputeType();  //
	public String getComputeItemName();  //

	/**
	 * ����������,������Ҫ�������������ϲ�����,ֻҪ��ҵ������"ת"��������!!!
	 * �����������������������е���,���Կ����ڸ÷����������һЩ�������ֵ,Ȼ���������������Ե���!!
	 * @param _condition
	 *            ��ѯ���������Ĳ�ѯ����!!!
	 * @param _loginUserVO
	 *            ��¼��Ա�������Ϣ
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}

