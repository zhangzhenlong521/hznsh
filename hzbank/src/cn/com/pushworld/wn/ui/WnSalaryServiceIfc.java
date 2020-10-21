package cn.com.pushworld.wn.ui;

import java.util.Map;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface WnSalaryServiceIfc extends WLTRemoteCallServiceIfc {
	public String getHfWg();
	/**
	 * zzl[��Ա������������]
	 * 
	 * @return
	 */
	public String getSqlInsert(String time);

	/**
	 * zpy[����ָ����]
	 */
	public String getBMsql(String planid);

	public String gradeBMScoreEnd();

	/**
	 * zpy[����ȫ������]
	 * 
	 * @return
	 */
	public String ImportAll();

	/**
	 * zpy[����һ�������]
	 * 
	 * @param date
	 *            :���ڣ������ʽΪ:[20190301]
	 * @return
	 */
	public String ImportDay(String date);

	/**
	 * zpy[����ĳ�ű�ĳ�������]
	 * 
	 * @param filePath
	 * @return
	 */
	public String ImportOne(String filePath);

	/**
	 * zzl[����ͻ�������Ϣ����]
	 * 
	 * @return
	 */
	public String getChange(String date1, String date2);
	
	/**
	 * ZPY ��2020-06-07���´������ݱ��
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String getNewChange(String startDate,String endDate);

	/**
	 * zzl[���ͻ�������Ϣ����]
	 * 
	 * @return
	 */
	public String getCKChange(String date1, String date2);
	/**
	 * zzl[���ͻ�������Ϣ����]
	 *
	 * @return
	 */
	public String getWgChange(String date1, String date2);

	/**
	 * zpy[2019-05-22] Ϊÿ����Ա���ɶ��Կ��˼ƻ�
	 * 
	 * @return:�������ִ�н��
	 */
	public String gradeDXscore(String id);

	/**
	 * zpy[2019-05-22] ������ǰ���˼ƻ�
	 * 
	 * @param planid
	 *            :��ǰ�ƻ�id
	 * @return
	 */
	public String gradeDXEnd(String planid);

	/**
	 * ZPY[2019-05-23] �ͻ������Դ������
	 * 
	 * @param id
	 * @return
	 */
	public String gradeManagerDXscore(String id);

	/**
	 * ZPY[2019-05-23] �������пͻ������Կ���
	 * 
	 * @param id
	 *            :�ƻ�ID
	 * @return
	 */
	public String endManagerDXscore(String id);

	/**
	 * zzl[�������ɱ�]
	 */
	public String getDKFinishB(String date);

	/**
	 * zpy[ǭũE������ɱȼ���]
	 * 
	 * @param date_time
	 *            :��ѯ����
	 * @return
	 */
	public String getQnedRate(String date_time);

	/**
	 * zpy[ǭũE�����������ɱȼ���]
	 * 
	 * @param date_time
	 *            :��ѯ����
	 * @param username
	 *            :�ͻ�������
	 * @return
	 */
	public String getQnedtdRate(String date_time);

	/**
	 * zpy[�ֻ�������ɱȼ���]
	 * 
	 * @param date_time
	 *            :��ѯ����
	 * @return
	 */
	public String getsjRate(String date_time);

	/**
	 * zpy[��ũ�̻�ά����ɱȼ���]
	 * 
	 * @param date_time
	 *            :��ѯ����
	 * @param username
	 *            :�ͻ�����
	 * @return
	 */
	public String getZNRate(String date_time);

	/**
	 * zpy[��ԼС΢�̻���ɱȼ���]
	 * 
	 * @param date_time
	 *            :����
	 * @return
	 */
	public String getTyxwRate(String date_time);

	/**
	 * zzl[�������������ɱ�]
	 */
	public String getDKBalanceXZ(String date);

	/**
	 * zzl[�����������ɱ�]
	 */
	public String getDKHouseholdsXZ(String date);

	/**
	 * zzl [�ջر��ⲻ��������ɱ�]
	 */
	public String getBadLoans(String date);

	/**
	 * zzl[�ջش�������������ɱ�&��������ѹ��]
	 */
	public String getTheStockOfLoan(String date);

	/**
	 * Ϊί�ɻ�����ɴ�ּƻ�
	 * 
	 * @param id
	 * @return
	 */
	public String getKJDXScore(String id);

	/**
	 * ������ǰί�ɻ�ƴ��
	 * 
	 * @param id
	 * @return
	 */
	public String getKJDXEnd(String id);

	/**
	 * fj[ũ������ָ����ɱ�]
	 */
	public String getNhjdHs(String date);

	/**
	 * fj[ǭũe��������������]
	 */
	public String getQnedXstd(String data);

	/**
	 * fj[��λְ����С΢��ҵ������ɱ�]
	 * 
	 * @param data
	 * @return
	 */
	public String getDwzgXwqyRatio(String data);

	/**
	 * 
	 * fj��Ա��Ч���ʵȼ�����
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public String getGyClass(String date) throws Exception;

	/**
	 * fjί�ɻ�Ƽ�Ч���ʵȼ�����
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public String getWpkjClass(String date) throws Exception;

	/**
	 * �Կͻ�������м�Ч����
	 * 
	 * @return
	 */
	public String managerLevelCompute(int dateNum);
	/**
	 * ���ල��ѯ������뵽������� ZPY ��2019-07-25��
	 * @param sql
	 */
	
	public void insertMonitorResult(String sql, Map<String, String> conditionMap);

	public String updateCheckState(BillVO[] checkUsers, Map<String, String> paraMap);

    
	
	
	/**
	 * fjũ�񹤹���ָ��
	 * @param replace
	 * @return
	 */
	public String getNmggl(String replace);
    /**
     * ��Ա����λϵ�������������
     * @param handleDate
     */
	public void insertStaffRadio(String handleDate);
    /**
     * Ա���쳣��Ϊ���ݼ��
     * @param curSelectMonth  �û�ѡ�п�����
     * @param curSelectMonthStart  �û�ѡ�񿼺��³�����
     * @param curSelectDate  �û�ѡ������
     * @return
     */
	public String importMonitorData(String curSelectDate, String curSelectMonthStart, String curSelectMonth,boolean flag);
    /**
     * ����Ա���쳣��Ϊ��Ϣ
     * @param billVos
     * @return
     */
	public String dealExceptionData(BillVO[] billVos,Map<String,String> paraMap);
    /**
     * ������Ա�����������
     * @param bos
     * @param pfUserDept 
     * @param pfUserName 
     */
	public String finishGradeScore(BillVO[] bos, String pfUserName,String pfUserCode, String pfUserDept);
    /**
     * �����Ա�����������
     * @param bos
     */
	public String saveGradeScore(BillVO[] bos);

	/**
	 * ��ũ�̻�ά��ָ�� ����ͳ��
	 * @param curSelectMonthStart:ѡ���µ��¿�ʼʱ��
	 * @param curSelectDate: �û�ѡ��ʱ��
	 * @param curSelectMonth:�û�ѡ����
	 * @param b:�Ƿ����¼���
	 * @return
	 */
	public String znCount(String curSelectMonthStart, String curSelectDate,
			String curSelectMonth,String dateInterVal ,boolean b);
  
}
