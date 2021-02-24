package cn.com.infostrategy.ui.report.cellcompent;

import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * ��Excel���ɵĹ���!!
 * @author xch
 *
 */
public class BillCellExcelUtil {

	/**
	 * ����Excel�ļ�����һ��BillCellPanel
	 */
	public void createByExcel(String _excelfilename, String _templetcode) throws Exception {
		//���ȴ���һ��BillCellVO
		BillCellVO billCellVO = new BillCellVO();
		billCellVO.setId(null); //������s_pub_billcelltemplet_dȡ
		billCellVO.setTempletcode(_templetcode); //

		//��Excel����...

		String[][] str_exceldata = new ExcelUtil().getExcelFileData(_excelfilename); //

		BillCellItemVO[][] itemVOs = new BillCellItemVO[str_exceldata.length][str_exceldata[0].length]; //����Excel�е�����������������
		for (int i = 0; i < str_exceldata.length; i++) {
			for (int j = 0; j < str_exceldata[i].length; j++) {
				itemVOs[i][j] = new BillCellItemVO(); //
				itemVOs[i][j].setCellrow(i);
				itemVOs[i][j].setCellcol(j);
				itemVOs[i][j].setRowheight("20");
				itemVOs[i][j].setColwidth("100"); //
				itemVOs[i][j].setForeground("0,0,255");
				itemVOs[i][j].setCellvalue(str_exceldata[i][j]); //
			}
			
		}
//		for(int i=0;i<itemVOs.length;i++){
//			for(int j=0;j<itemVOs[i].length;j++){
//				int max=0;
//				if(itemVOs[i][j].getCellvalue().length()>0){
//					max=j;
//					
//				}
//			}
//		}
	
		billCellVO.setId(UIUtil.getSequenceNextValByDS(null, "s_pub_billcelltemplet_h")); //
		billCellVO.setCellItemVOs(itemVOs); ///
		billCellVO.setRowlength(str_exceldata.length);
		billCellVO.setCollength(str_exceldata[0].length); //
		

		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		service.saveBillCellVO(null, billCellVO); //��������
	}

	

}
