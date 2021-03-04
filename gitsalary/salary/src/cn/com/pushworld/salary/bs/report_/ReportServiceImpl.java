package cn.com.pushworld.salary.bs.report_;

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public class ReportServiceImpl implements ReportService{
	private CommDMO dmo = null;
	
	public BillCellVO getReportResult(HashVO[] _hvos, String _filename) throws Exception {
		ReportConfigVO rcv = new XmlReportEngine(_filename).getReportConfigVO();
		return getCellVO(new ReportCrossEngine(getHashMaps(_hvos), rcv).getResult());
	}
	
	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception {
		return getCellVO(getReportResult(_where,_filename));
	}
	
	public HashMap getReportResult(HashMap _where, String _filename) throws Exception{
		ReportConfigVO rcv = new XmlReportEngine(_filename).getReportConfigVO();
		String sql = rcv.getSql();
		HashMap[] hms = getHashMaps(sql, _where); 
	    if(hms!=null){
	    	return new ReportCrossEngine(hms, rcv).getResult();	
	    }else{
	    	return null;
	    }
	}

	private HashMap[] getHashMaps(String _sql, HashMap _where) throws Exception{
		Iterator iterator = _where.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String> entry = (Entry) iterator.next();
            _sql = _sql.replace(entry.getKey(), entry.getValue()); //替换where
        }
		
		HashVO[] hvos = getDmo().getHashVoArrayByDS(null, _sql);
		return getHashMaps(hvos);
	}
	
	//将HashVO[]转换为HashMap[]
	private HashMap[] getHashMaps(HashVO[] _hvos) throws Exception{
		HashMap[] hms = null;
		if(_hvos!=null&&_hvos.length>0){
			hms = new HashMap[_hvos.length];
			String[] keys = _hvos[0].getKeys();
			for (int i = 0; i < _hvos.length; i++) {
				hms[i] = new HashMap(); 
				for (int j = 0; j < keys.length; j++) {
					hms[i].put(keys[j], _hvos[i].getStringValue(keys[j]));
				}
			}
		}
		
		return hms;
	}
	
	private BillCellVO getCellVO(HashMap _hm){
		if(_hm==null){
			return null;
		}
		HashMap hm_result = (HashMap)_hm.get("result");
		HashMap hm_combine = (HashMap)_hm.get("combine");
		int rows = (Integer)_hm.get("rows");
		int cols = (Integer)_hm.get("cols");
		int lefttitle_len = (Integer)_hm.get("lefttitle_len");
		int toptitle_len = (Integer)_hm.get("toptitle_len");
		
		BillCellVO cellvo = new BillCellVO();
		cellvo.setRowlength(rows);
		cellvo.setCollength(cols);
		
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[rows][cols];
		
		for(int i=0; i<rows; i++){
			for(int j=0; j<cols; j++){
				cellItemVOs[i][j] = new BillCellItemVO();
				cellItemVOs[i][j].setCellkey(i + "," + j);
				cellItemVOs[i][j].setCelltype("TEXTAREA");
				cellItemVOs[i][j].setCellrow(i);
				cellItemVOs[i][j].setCellcol(j);
				cellItemVOs[i][j].setValign(2);
				cellItemVOs[i][j].setHalign(2);
				cellItemVOs[i][j].setRowheight("25");
				cellItemVOs[i][j].setColwidth("80");
				cellItemVOs[i][j].setFontsize("12");
				cellItemVOs[i][j].setFonttype("宋体");
				cellItemVOs[i][j].setFontstyle(Font.PLAIN+"");
				cellItemVOs[i][j].setSpan("1,1");
				cellItemVOs[i][j].setCellvalue("");
				
				if(i<toptitle_len||j<lefttitle_len){
					cellItemVOs[i][j].setBackground("232,255,255"); 	
				}
				
				if(hm_combine.containsKey(i+"_"+j)){
					cellItemVOs[i][j].setSpan((String)hm_combine.get(i+"_"+j));
				}
				
				if(hm_result.containsKey(i+"_"+j)&&hm_result.get(i+"_"+j)!=null){
					cellItemVOs[i][j].setCellvalue(""+hm_result.get(i+"_"+j));
				}
			}
		}
		cellvo.setCellItemVOs(cellItemVOs);
		
		return cellvo;
	}
	
	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}
}
