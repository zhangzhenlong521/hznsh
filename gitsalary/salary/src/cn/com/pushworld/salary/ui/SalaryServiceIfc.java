package cn.com.pushworld.salary.ui;

import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface SalaryServiceIfc extends WLTRemoteCallServiceIfc {
	// �õ�һ������ֶκ����� ��table.xml��ȡ
	public String[][] getTableItemAndDescr(String _table) throws Exception;

	// �������������
	public void createScoreTable(HashMap param) throws Exception;

	// ���ָ��Ϸ���
	public HashMap checkViladate(String month) throws Exception;

	// ���ſ��˽��Ȳ�ѯ
	public BillCellVO getTargetCheckProcessVO(String month) throws Exception;

	// Ա�����˽��Ȳ�ѯ
	public BillCellVO getPersonCheckProcessVO(String month) throws Exception;

	// �÷�ͳ��
	public BillCellVO getScoreVO(String month) throws Exception;

	public boolean endPlanDL_Dept(String logid) throws Exception;

	public BillCellVO getScoreVO_Person(String month) throws Exception;

	public BillCellVO getScoreVO_Person(String month, String[] checkeduserids) throws Exception;

	public BillCellVO getPersonTargetCheckResVO(String logid, String checkeduserid) throws Exception;

	// ��������
	public BillVO[] getScoreDetail(String sql, String templetcode) throws Exception;

	// ��������
	public BillCellVO getScoreDetailCellVO(String sql, String templetcode) throws Exception;

	public HashMap endPlan(String logid, boolean isquiet, String state) throws Exception;

	public HashMap endPlanDx(String logid, boolean isquiet, String state) throws Exception;

	public BillCellVO[] getExtDateDirect(String checkdate, String[] deptid) throws Exception;

	public BillCellVO getDeptCheckResVO(String checkdate, String checkedDeptid) throws Exception;

	public BillCellVO getTargetCheckResVO(String checkdate, String checkedDeptid) throws Exception;

	public BillCellVO getPostCheckResVO(String checkdate, String checkeduserid) throws Exception;

	public BillCellVO getPostTargetCheckResVO(String checkdate, String checkeduserid) throws Exception;

	public void onCreateSalaryBill(List sql, String salarybillid, String accountid, String checkdate,BillVO vo) throws Exception;

	public BillCellVO[] getTarget_Check_ReviseVO(String logid) throws Exception;

	public BillCellVO getSalaryVO(String[] column, String checkdate) throws Exception;

	public HashMap endCalcDeptDXScore(String logid, boolean isquiet, String state) throws Exception;

	public HashMap endCalcPersonDXScore(String logid, boolean isquiet, String state) throws Exception;

	public void onePlanCalcAllUserEveryDXTargetScore(String logid) throws Exception;

	public Object[][] calcYearPersonCheckReport(String[] logid) throws Exception;

	/** *** �����ǹ�ʽ����Զ�̵��÷��� **** */
	public Object[] onExecute(HashVO _factorVO, HashVO _baseDataHashVO) throws Exception;

	public void calcDeptDLtarget(HashVO planvo, String state, String _targetType) throws Exception;

	public void onCalcPersonDLTarget(HashVO planvo, String _calbatch) throws Exception;

	public Object getRemoteActionSchedule(String _key, String _billType) throws Exception;

	public void calc_P_Pay(HashVO _planVO, String _calbatch) throws Exception;

	public void calc_QQ_Pay(HashVO _planVO) throws Exception;

	public void calcDelayPay(String _logid) throws Exception;

	public void calcDeptTotleScoreIntoReviseTable(String _logid, String state) throws Exception;

	public HashVO[] calcOnePersonTarget_P_Money(String _targetID, String _checkdate) throws Exception;

	public String calcOneDeptTargetDL(HashVO targetVO, String selectDate) throws Exception;

	//����ָ��������б�չʾ
	public HashVO[] calcOneDeptTargetDL2(HashVO targetVO, String selectDate) throws Exception;

	/** ************* ���� **************** */

	// ���ſ��˻���
	public HashVO[] getDeptCheck(String[] checkids, String[] types) throws Exception;

	// Ա�����˻���
	public HashVO[] getPersonCheck(String[] checkids, String[] types, String[] types_nocou) throws Exception;

	// Ա��ҵ�����
	public HashVO[] getPersonBusiness(String[] checkids, String[] types_id) throws Exception;

	// Ա�����ʻ���
	public HashVO[] getPersonSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// ��λ���ʻ���
	public HashVO[] getPostSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// ���Ź��ʻ���
	public HashVO[] getDeptSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// ����֧������ ������� ��Ȩ��������
	public HashVO[] getPersonStyle(String type, String month, String userid, String excel_code, String[] types, String[] types_nocou) throws Exception;

	// ����֧������ ������� ��Ȩ�������� �ܼ�
	public HashVO[] getPersonStylee(String tablename, String[] dates, String excel_code, String[] types, String[] types_nocou, HashMap _condition) throws Exception;

	// ��Ȩ��������
	public HashVO[] getPersonGqjl(String tablename, int[] years, String excel_code, HashMap _condition) throws Exception;

	// ����֧������
	public HashVO[] getPersonYqzf(String year, String[] months, HashMap _condition) throws Exception;

	/** ************************************ */
	public BillCellVO parseCellTempetToWord(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception;

	/** **Ԥ��*** */

	public HashVO getWarnVoById(String _warnid, String _logid) throws Exception;

	public void sendMessage(String[] _session, String _message) throws Exception;

	public HashVO[] getAllInstallUser() throws Exception;

	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception;

	/** ==============================��λ��ֵ����--lcj================================== */

	// ���ɸ�λ�������¼�����/2013-10-31��
	public void createPostEvalTable(String _planid) throws Exception;

	// ��ø�λ��ֵ�����������ݡ����/2013-10-31��
	public BillCellVO getPostEvalVO(String _planid, String _planname, String _userid, String _postids) throws Exception;

	// �ύ��λ��ֵ���������/2013-11-01��
	public void submitPostEvalTable(String _planid, String _userid) throws Exception;

	// ��ø�λ��ֵ��������ͳ�����ݡ����/2013-10-31��
	public BillCellVO getPostEvalScheduleVO(String _planid) throws Exception;

	// ��ø�λ��ֵ�����÷�ͳ�����ݡ����/2013-10-31��
	public BillCellVO getPostEvalScoreVO(String _planid) throws Exception;

	// ��ø�λ��ֵ�����÷�ͳ����ϸ����--ĳ����λ�÷����顾���/2013-10-31��
	public BillCellVO getPostEvalScoreDetailVO(String _planid, String _postid) throws Exception;

	/** ========================��Ⱦ�Ӫ�ƻ�--lcj================================== */
	// ������Ⱦ�Ӫ�ƻ������/2014-01-07��
	public void copyOperatePlan(String _planid, String _newplanid) throws Exception;

	// �����Ⱦ�Ӫ�ƻ�֮����ʡ����/2014-01-09��
	public HashVO[] getOperatePlanFullfillRate(String _planid, String _months) throws Exception;

	/** ***************** ��������**************************** */

	public List getFeedbackDataHashvo(String _sql, int[] __rowArea) throws Exception;

	public void createPostDutyScoreTable(HashMap param) throws Exception;

	/** *********************����-��λְ���������******************************* */

	public HashMap calcPostDutyTargetScore(HashVO _planvo, String state) throws Exception;

	public BillCellVO getPostDutyCheckProcess(String logid) throws Exception;

	public BillCellVO getPostDutyCheckCompute(String logid) throws Exception;

	public BillCellVO getPostDutyDetailCompute(String logid, String checkeduserid) throws Exception;

	public BillCellVO getDeptTargetQueryCellVO(HashMap hashMap) throws Exception;

	public BillCellVO getPersonPostTargetQueryCellVO(HashMap hashMap) throws Exception;

	public BillCellVO getPostMoneyDif(String _checkdate, String factor[]) throws Exception;

	public HashMap checkDataByPolicy(HashVO[] _data, String _excelID) throws Exception;

	/**********************�ӿڿ���***************************/

	public void createTableByConfig(String _dataIFCmainID) throws Exception;

	public void readDataFromFile(String _dataIFCmainID, String _datadate) throws Exception;
	//zzl[2018-11-23]�����ӿ�����ͬ��
	public String impReadDataFromFile(String _dataIFCmainID, String _datadate) throws Exception;
	//zzl[2019-3-13]�����ϴ�����ʾ����ģ��
	public String impDataUpload(HashMap[] exceldatas,String table_name);

	public String[][] compareDBTOConfig(HashVO hashVO, HashVO[] hashVOs) throws Exception;

	public void convertIFCDataToReportByHand(String _mainid, String _datadate) throws Exception;

	/*****************************************************/
	//���ݲ��żƼ�ָ���ȡ���ŵļ�Ч����
	public BillCellVO getJJTargetDeptJXMoney(String _logid, String _deptids[], String[] _noJoinStationkind) throws Exception;

	//2020��7��12��12:46:01    fj   ��λ���ʻ�����Ȼ���
	public HashVO[] getPostGatherSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;


}
