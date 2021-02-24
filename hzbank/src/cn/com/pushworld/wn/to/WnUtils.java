package cn.com.pushworld.wn.to;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import freemarker.template.SimpleDate;

/**
 * ������Ŀ������
 * 
 */
public class WnUtils {
	/**
	 * ���ݴ�������ڻ�ȡ����ĩ����
	 * 
	 * @param date
	 *            (���ڸ�ʽΪ:20190101����2019-01-01����2019��1��1��)
	 * @return
	 */
	public static void main(String[] args) {
		String monthEnd = getMonthEnd("2019-05-01");
		System.out.println(monthEnd);
	}

	public static String getMonthEnd(String date) {
		String result = "";
		try {
			SimpleDateFormat simple = null;
			if (date.contains("��")) {
				simple = new SimpleDateFormat("yyyy��MM��dd��");
			} else if (date.contains("-")) {
				simple = new SimpleDateFormat("yyyy-MM-dd");
			} else {
				simple = new SimpleDateFormat("yyyyMMdd");
			}
			Date parse = simple.parse(date);
			int year = Integer.parseInt(new SimpleDateFormat("yyyy")
					.format(parse));
			int month = Integer.parseInt(new SimpleDateFormat("MM")
					.format(parse));
			String day = "";
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = "31";
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = "30";
			case 2:
				if (year % 4 == 0 && year % 100 != 0) {
					day = "29";
				} else {
					day = "28";
				}
				break;
			}

			result = year
					+ "-"
					+ (month >= 10 ? String.valueOf(month) : String.valueOf("0"
							+ month)) + "-" + day;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//
	/**
	 * ��ȡ����һ����������
	 * 
	 * @param date
	 *            ()
	 * @return
	 */
	public static String getYearEnd(String date) {
		String result = "";
		try {
			SimpleDateFormat simple = null;
			if (date.contains("��")) {
				simple = new SimpleDateFormat("yyyy��MM��dd��");
			} else if (date.contains("-")) {
				simple = new SimpleDateFormat("yyyy-MM-dd");
			} else {
				simple = new SimpleDateFormat("yyyyMMdd");
			}
			Date parse = simple.parse(date);
			int year = Integer.parseInt(new SimpleDateFormat("yyyy")
					.format(parse)) - 1;
			result = year + "-12-31";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// ��ȡ�����������ʱ��
	public static String getYearStart() {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy");
		String yearStartDate = "";
		try {
			yearStartDate = simple.format(new Date()) + "-01-01";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yearStartDate;
	}

	/*
	 * ���ݿ������ڻ�ȡ�����˵����ʱ��
	 */
	public static String getYearStart(String date_time) {
		String[] yearMonthDay = getYearMonthDay(date_time);
		SimpleDateFormat simple = new SimpleDateFormat("yyyy");
		int currentYear = Integer.parseInt(simple.format(new Date()));
		int lastYear = Integer.parseInt(yearMonthDay[0]);
		if (currentYear > lastYear) {
			return lastYear + "-01" + "-01";
		} else {
			return currentYear + "-01" + "-01";
		}
	}

	// ��ȡ���������ڵ�������
	public static String[] getYearMonthDay(String date_time) {
		String[] result = new String[3];
		if (date_time.contains("��")) {
			date_time = date_time.replaceAll("��", "-").replaceAll("��", "-");
		}
		if (date_time.length() == 10) {
			result = date_time.split("-");
		} else {
			result = date_time.split("");
		}
		return result;
	}

	public static SimpleDateFormat formatDate(String date_time) {
		SimpleDateFormat simple = null;
		if (date_time.contains("��")) {
			simple = new SimpleDateFormat("yyyy��MM��dd��");
		} else if (date_time.contains("-")) {
			simple = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			simple = new SimpleDateFormat("yyyyMMdd");
		}
		return simple;
	}
	
	public static boolean isEmpty(String str){
		if(str==null|| "".equals(str)){
			return false;
		}
		return true;
	}
	/**
	 * ����Excel���
	 * @param listPanel
	 * @param filePath
	 * @param selectSQL
	 * @param sheetName
	 * @return
	 */
	public static  String ImportExcel(BillListPanel listPanel,String filePath,String selectSQL,String sheetName){
		String result="";//
		try {
			String templetName = listPanel.getTempletVO().getTempletname();//��ȡ��ģ������
			Workbook monitorResult=new SXSSFWorkbook(100);
            Sheet firstSheet = monitorResult.createSheet(sheetName);//����sheet
            Row firstRow = firstSheet.createRow(0);//��������
            //��ȡ����ͷ��Ϣ
            Pub_Templet_1_ItemVO[] templetItemVOs = listPanel.getTempletItemVOs();
            List<String> unShowList=new ArrayList<String>();
            for (int i = 0,n = 0; i < templetItemVOs.length; i++) {
           	 Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = templetItemVOs[i];
           	 String cellKey=pub_Templet_1_ItemVO.getItemkey();
           	 String cellName=pub_Templet_1_ItemVO.getItemname();
           	 if(pub_Templet_1_ItemVO.isListisshowable()){
           		 firstRow.createCell(i-n).setCellValue(cellName);
				 }else{
					 unShowList.add(cellKey);
					 n++;
				 }
			}
           HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null, selectSQL);
           Row nextRow = null;
           for (int i = 0; i < hashVos.length; i++) {
           	nextRow=firstSheet.createRow(i+1);//������һ������
           	String[] keys = hashVos[i].getKeys();
           	for (int j = 0,n=0; j < keys.length; j++) {//��ÿһ�����ݽ��д���
					if(unShowList.contains(keys[j].toUpperCase())){//�жϵ�ǰ���Ƿ��Ѿ�����
						n++;
						continue;
					}
					nextRow.createCell(j-n).setCellValue(hashVos[i].getStringValue(keys[j]));
				}
			}
           String fileName=filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf("."));
           filePath=filePath.substring(0,filePath.lastIndexOf("\\"));
           File file=new File(filePath+"/"+fileName+".xls");
           int i=1;
           while(file.exists()){
           	fileName=templetName+i+".xls";
           	file=new File(filePath+"/"+fileName);
           	i++;
           }
           file.createNewFile();
           FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
           monitorResult.write(fout);
           fout.close();
		} catch (Exception e) {
			result="���ݵ���ʧ��";
			e.printStackTrace();
		}
		return result;
	}
}
