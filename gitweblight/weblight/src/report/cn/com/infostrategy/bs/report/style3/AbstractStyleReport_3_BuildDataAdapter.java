package cn.com.infostrategy.bs.report.style3;

/**
 * ȱʡ������ģʽ,ʵ�ִ������ʵ��4�����󷽷����ֱ�Ϊ�õ����ݵ�Hashvo�����������У������У�������Ҫ��ѯ���С�
 * ������2������Ϊ��ͨ������һ��Ϊ�Ƿ���Ҫ���飬һ��Ϊͳ�����ͣ������Ҫ����Ļ�����ô����ָ��ͳ�����͡�
 * �������Ҫ����Ļ�����ô���ص�Hashvo����ָ��ͳ�����ͣ�Ҳ������sql��д��count����sum��.
 */
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public abstract class AbstractStyleReport_3_BuildDataAdapter implements StyleReport_3_BuildDataIFC {

	public String getChartTitle() {
		return "ͼ��";
	}

	public abstract HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception;

	public abstract String getColHeadName(); // �õ���ͷ

	//��ͷ����ʾ˳��,������ָ����ʲô˳������ʾ!!
	public String[] getColHeaderViewSort() {
		return null; //
	}

	//��ͷ�Ƿ�����㱨����,��ν��㱨���ƾ��������ֶ��д��ڵĶ���������������Ƿ���ʵ�������д��ڹ��������û�з���ҲҪ���������ķ�ʽ���!!
	//����"�ǵ�ʱ��",����ĸ�ʡ��ʹû�з����ǵ�ҲҪ���棬���淢������Ϊ0���ʳ���㱨���ƣ�
	public boolean isColHeaderZeroReportType() {
		return false;
	}

	public abstract String getRowHeadName();// �õ���ͷ

	//��ͷ����ʾ˳��
	public String[] getRowHeaderViewSort() {
		return null; //
	}

	//��ͷ�Ƿ�����㱨����,��ν��㱨���ƾ��������ֶ��д��ڵĶ���������������Ƿ���ʵ�������д��ڹ��������û�з���ҲҪ���������ķ�ʽ���!!
	//����"�ǵ�ʱ��",����ĸ�ʡ��ʹû�з����ǵ�ҲҪ���棬���淢������Ϊ0���ʳ���㱨���ƣ�
	public boolean isRowHeaderZeroReportType() {
		return false; //Ĭ��Ϊ��,���Ǹ���ʵ����������ʾ,�������Ϊ��,����ΪtrueҲû��Ч��!!
	}

	public abstract String getComputeItemName(); //��Ҫ��ѯ����

	public abstract String getComputeType();// ͳ�Ƶ�����SELECT,COUNT,SUM,AVG

}
