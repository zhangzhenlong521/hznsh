package cn.com.infostrategy.bs.report.style3;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * ��񱨱�3��̨ʵ�ʼ�����߼�
 * 
 * @author xch
 * 
 */
public class BuildDataDMO extends AbstractDMO {

	public BillChartVO styleReport_3_BuildData(HashMap _condition, String _builderClassName, CurrLoginUserVO _loginUserVO) throws Exception {
		StyleReport_3_BuildDataIFC builder = (StyleReport_3_BuildDataIFC) Class.forName(_builderClassName).newInstance(); //
		HashVO[] hvs = builder.buildDataByCondition(_condition, _loginUserVO); //�ȹ�������,��ʱ�ѻ���������ȣ����������ֵ�ˣ�Ȼ�������ع�����������ȡ����Ҫ��ֵ,����ά������,����ֵ��
		String str_ColHeadName = builder.getColHeadName();
		String str_RowHeadName = builder.getRowHeadName();
		String[] str_rowHeaderSorts = null; //
		String[] str_colHeaderSorts = null; //
		boolean isRowZeroReportType = false; //�Ƿ�����㱨����
		boolean isColZeroReportType = false; //�Ƿ�����㱨����
		String str_title = "ͼ��";
		if (builder instanceof AbstractStyleReport_3_BuildDataAdapter) {
			str_rowHeaderSorts = ((AbstractStyleReport_3_BuildDataAdapter) builder).getRowHeaderViewSort(); //
			str_colHeaderSorts = ((AbstractStyleReport_3_BuildDataAdapter) builder).getColHeaderViewSort(); //
			isRowZeroReportType = ((AbstractStyleReport_3_BuildDataAdapter) builder).isRowHeaderZeroReportType(); //
			isColZeroReportType = ((AbstractStyleReport_3_BuildDataAdapter) builder).isColHeaderZeroReportType(); //
			str_title = ((AbstractStyleReport_3_BuildDataAdapter) builder).getChartTitle(); //ͼ�����
		}

		String str_NeedSelectCol = builder.getComputeItemName();
		String ComputeType = builder.getComputeType();
		BillChartVO chartVO = null;
		ReportUtil reportUtil = new ReportUtil();
		String str_ReportType = "";
		if (ComputeType.equals(StyleReport_3_BuildDataIFC.SELECT)) { //������ҵ�����ݹ��������Ѿ�����ͳ�ƣ�����count,sum��
			if (hvs != null) {
				if (hvs.length == 0) {
					chartVO = new BillChartVO();
					chartVO.setXSerial(new String[] { "û������!" });
					chartVO.setYSerial(new String[] { "û������!" });
					chartVO.setDataVO(new BillChartItemVO[][] { { new BillChartItemVO(0) } });
				} else {
					chartVO = reportUtil.convertHashVOToChartVO(hvs, str_RowHeadName, str_ColHeadName, str_NeedSelectCol, str_rowHeaderSorts, str_colHeaderSorts, isRowZeroReportType, isColZeroReportType); //
					chartVO.setXHeadName(str_ColHeadName);
					chartVO.setYHeadName(str_RowHeadName);
				}
			}
		} else {
			str_ReportType = ComputeType + "(" + str_NeedSelectCol + ") " + str_NeedSelectCol;// ��ĳ���ֶν��л���,����sum(���)
			String str_NeedGroupByField = str_RowHeadName + "," + str_ColHeadName;
			HashVO[] newHvs = reportUtil.groupHashVOs(hvs, str_ReportType, str_NeedGroupByField);
			chartVO = reportUtil.convertHashVOToChartVO(newHvs, str_RowHeadName, str_ColHeadName, str_NeedSelectCol, str_rowHeaderSorts, str_colHeaderSorts, isRowZeroReportType, isColZeroReportType); //
			if (chartVO == null) //wanglei��2010-09-08�޸�
			{
				chartVO = new BillChartVO();
				chartVO.setXSerial(new String[] { "û������!" });
				chartVO.setYSerial(new String[] { "û������!" });
				chartVO.setDataVO(new BillChartItemVO[][] { { new BillChartItemVO(0) } });
			} else {
				chartVO.setXHeadName(str_ColHeadName);
				chartVO.setYHeadName(str_RowHeadName);
			}
		}
		chartVO.setTitle(str_title); //
		return chartVO;
	}

}
