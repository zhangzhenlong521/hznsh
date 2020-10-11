package cn.com.pushworld.salary.bs.report_;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public interface ReportService {
	
	/**
	 * ��������
	 */
	public BillCellVO getReportResult(HashVO[] _hvos, String _filename) throws Exception;
	
	/**
	 * ��������
	 */
	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception;
	
}
