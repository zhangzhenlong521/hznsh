package cn.com.pushworld.salary.bs.dmo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public class ReportDataBuilderDMO extends SalaryAbstractCommDMO {
	public BillCellVO getPersonSalaryDif(String _checkdate, String factor[]) throws Exception {
		HashVO[] bill = getDmo().getHashVoArrayByDS(null, "select t1.* from sal_salarybill t1 left join sal_account_set t2 on t1.sal_account_setid = t2.id where t2.isdefault='Y' and t1.monthly in(" + getTb().getInCondition(_checkdate) + ")");
		HashVO[] factorvos = getDmo().getHashVoArrayByDS(null, "select t1.* from sal_account_factor t1 left join sal_account_set t2 on t1.accountid = t2.id where t2.isdefault = 'Y' and t1.factorid in(" + getTb().getInCondition(factor) + ") order by t1.seq");
		List<BillCellItemVO[]> cellrows = new ArrayList<BillCellItemVO[]>();

		for (int i = 0; i < bill.length; i++) {
			HashVO billvo = bill[i];
			LinkedHashMap<String, HashVO> post_salary = new LinkedHashMap<String, HashVO>();
			LinkedHashSet postname = new LinkedHashSet();
			//考勤扣减绩效工资
			String qingjiaUser[] = getDmo().getStringArrayFirstColByDS(
					null,
					"select distinct(t1.id) id from v_sal_personinfo t1 left join sal_salarybill_detail t2 on t1.id= t2.userid where (t2.factorid ='3096' and t2.salarybillid = '" + billvo.getStringValue("id") + "' and t2.factorvalue>0) or (t2.factorid ='1288' and t2.salarybillid = '" + billvo.getStringValue("id")
							+ "' and t2.factorvalue=0) union all select distinct(checkeduser) id from v_sal_score_post_dept  where targetid = 148 and checkdate='" + _checkdate + "' and realvalue = 0");
			for (int j = 0; j < factorvos.length; j++) {
				HashVO[] vo = getDmo().getHashVoArrayByDS(
						null,
						"select t1.stationkind,max(t2.factorvalue+0) 最大值,min(t2.factorvalue+0) 最小值,avg(t2.factorvalue+0) 平均值 from v_sal_personinfo t1 left join sal_salarybill_detail t2 on t1.id= t2.userid where t2.factorid ='" + factorvos[j].getStringValue("factorid") + "' and t2.salarybillid = '" + billvo.getStringValue("id") + "' and t2.factorvalue>0 and  t1.id not in("
								+ getTb().getInCondition(qingjiaUser) + ") group by t1.stationkind");
				for (int k = 0; k < vo.length; k++) {
					post_salary.put(vo[k].getStringValue("stationkind") + "_" + factorvos[j].getStringValue("viewname"), vo[k]);
					postname.add(vo[k].getStringValue("stationkind"));
				}
			}

			String[] allpost = getDmo().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where 1=1  and (type = '薪酬_岗位归类')  and id in(" + getTb().getInCondition((String[]) postname.toArray(new String[0])) + ") order by seq");
			List<BillCellItemVO> title_1 = new ArrayList<BillCellItemVO>();
			List<BillCellItemVO> title_2 = new ArrayList<BillCellItemVO>();
			title_1.add(getBillTitleCellItemVO("工资条目"));
			title_2.add(getBillTitleCellItemVO("岗位类别"));
			for (int j = 0; j < allpost.length; j++) {
				List<BillCellItemVO> col = new ArrayList<BillCellItemVO>();
				for (int k = 0; k < factorvos.length; k++) {
					if (j == 0) {
						String factorname = factorvos[k].getStringValue("viewname");
						BillCellItemVO itemvo = getBillTitleCellItemVO(factorname);
						itemvo.setHalign(2);
						itemvo.setSpan("1,4");
						title_1.add(itemvo);
						title_1.add(getBillTitleCellItemVO(factorname));
						title_1.add(getBillTitleCellItemVO(factorname));
						title_1.add(getBillTitleCellItemVO(factorname));

						title_2.add(getBillTitleCellItemVO("最大值"));
						title_2.add(getBillTitleCellItemVO("最小值"));
						title_2.add(getBillTitleCellItemVO("平均值"));
						title_2.add(getBillTitleCellItemVO("大小差"));
					}
					String key = allpost[j] + "_" + factorvos[k].getStringValue("viewname");
					HashVO value = post_salary.get(key);
					if (k == 0) {
						col.add(getBillNormalCellItemVO(0, allpost[j]));
					}
					if (value != null) {
						BigDecimal max = new BigDecimal(value.getStringValue("最大值", "0")).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal min = new BigDecimal(value.getStringValue("最小值", "0")).setScale(2, BigDecimal.ROUND_HALF_UP);
						col.add(getBillNormalCellItemVO(0, max.toString()));
						col.add(getBillNormalCellItemVO(0, min.toString()));
						col.add(getBillNormalCellItemVO(0, new BigDecimal(value.getStringValue("平均值", "0")).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
						BillCellItemVO difvo = getBillNormalCellItemVO(0, max.subtract(min).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						difvo.setCustProperty("factor", factorvos[k]);
						difvo.setCustProperty("post", allpost[j]);
						difvo.setCustProperty("bill", billvo.getStringValue("id"));
						difvo.setCustProperty("checkdate", _checkdate);
						difvo.setCustProperty("max", value.getStringValue("最大值"));
						difvo.setCustProperty("min", value.getStringValue("最小值"));

						difvo.setIshtmlhref("Y");
						col.add(difvo);
					} else {
						col.add(getBillNormalCellItemVO(0, "-"));
						col.add(getBillNormalCellItemVO(0, "-"));
						col.add(getBillNormalCellItemVO(0, "-"));
						col.add(getBillNormalCellItemVO(0, "-"));
					}
				}
				if (j == 0) {
					cellrows.add(title_1.toArray(new BillCellItemVO[0]));
					cellrows.add(title_2.toArray(new BillCellItemVO[0]));
				}
				cellrows.add(col.toArray(new BillCellItemVO[0]));
			}

		}
		BillCellVO cellvo = new BillCellVO();
		if (cellrows.size() == 0) {
			BillCellItemVO itemvo = getBillTitleCellItemVO("没有查到数据");
			itemvo.setHalign(2);
			itemvo.setColwidth("200");
			cellrows.add(new BillCellItemVO[] { itemvo });
		}
		BillCellItemVO cellitemvos[][] = cellrows.toArray(new BillCellItemVO[0][0]);
		cellvo.setCellItemVOs(cellitemvos);
		cellvo.setCollength(cellitemvos[0].length);
		cellvo.setRowlength(cellitemvos.length);
		return cellvo;
	}

	/*
		public String[] getDeskTopReport() throws Exception {
			HashVO report[] = getDmo().getHashVoArrayByDS(null, "select * from pub_desktop_new where 1=1  and datatype!='文字' order by showorder ");
			List list = new ArrayList();
			int index = 0;
			for (int i = 0; i < report.length; i++) {
				String reporttitle = report[i].getStringValue("title");
				String type = report[i].getStringValue("datatype");
				String databuildername = report[i].getStringValue("databuildername");
				Object obj = Class.forName(databuildername).newInstance();
				HashVO rtvalue[] = null;
				if (obj instanceof DeskTopNewsDataBuilderIFC2) {
					DeskTopNewsDataBuilderIFC2 ifc = (DeskTopNewsDataBuilderIFC2) obj;
					HashMap map = getTb().convertStrToMapByExpress(report[i].getStringValue("otherconfig"));
					rtvalue = ifc.getNewData(null, map);
				} else if (obj instanceof DeskTopNewsDataBuilderIFC) {
					DeskTopNewsDataBuilderIFC ifc = (DeskTopNewsDataBuilderIFC) obj;
					rtvalue = ifc.getNewData(null);
				}
				JSONObject jsonobj = new JSONObject();
				if (rtvalue == null) {
					continue;
				}

				Object[] objs = getBarOrLineData(rtvalue);
				String[] str_x = (String[]) objs[0];
				String[] str_y = (String[]) objs[1];
				double[][] ld_value = (double[][]) objs[2]; //

				HashMap chart = new HashMap();
				if ("曲线图".equals(type)) {
					chart.put("type", "line");
				} else if ("柱形图".equals(type)) {
					chart.put("type", "column");
				}

				jsonobj.put("chart", chart);

				HashMap title = new HashMap();
				title.put("text", reporttitle);
				jsonobj.put("title", title);
				HashMap xAxis = new HashMap();
				List value = new ArrayList();
				for (int j = 0; j < str_y.length; j++) {
					value.add(str_y[j]);
				}
				JSONArray array = new JSONArray(value);
				xAxis.put("categories", array);
				jsonobj.put("xAxis", xAxis);
				JSONArray se = new JSONArray();
				for (int j = 0; j < str_x.length; j++) {
					HashMap series_1 = new HashMap();
					series_1.put("name", str_x[j]);
					double[] cvalue = ld_value[j];
					JSONArray ar = new JSONArray();
					for (int k = 0; k < cvalue.length; k++) {
						ar.put(cvalue[k]);
					}
					series_1.put("data", ar);

					se.put(series_1);
				}
				jsonobj.put("series", se);
				HashMap plotOptions = new HashMap();

				HashMap line = new HashMap();

				HashMap dataLabels = new HashMap();

				dataLabels.put("enabled", true);
				line.put("enableMouseTracking", false);
				line.put("dataLabels", dataLabels);

				if ("曲线图".equals(type)) {
					plotOptions.put("line", new jstring(""));
				} else if ("柱形图".equals(type)) {
					plotOptions.put("column", new jstring(""));
				}

				jsonobj.put("plotOptions", plotOptions);

				System.out.println(jsonobj.toString());

				list.add("$('#container" + index + "').highcharts(" + jsonobj.toString().replace("=", ":"));
				index++;
			}

			StringBuffer tablesb = new StringBuffer();
			tablesb.append("<table>\n");
			for (int i = 0; i < list.size(); i++) {
				if (i % 2 == 0) {
					tablesb.append("<tr>\n");
					tablesb.append("<td>\n");
					tablesb.append("<div id=\"container" + i + "\"></div>\n");
					tablesb.append("</td>\n");
				} else {
					tablesb.append("<td>\n");
					tablesb.append("<div id=\"container" + i + "\"></div>\n");
					tablesb.append("</td>\n");
					tablesb.append("</tr>\n");
				}
			}
			if (list.size() % 2 == 0) {

				tablesb.append("</tr>\n");
			}

			tablesb.append("</table>\n");
			list.add(tablesb.toString());

			return (String[]) list.toArray(new String[0]);
		}*/

	//因为柱形图与曲线图的数据是一样的,所以搞成一个方法,以便重用!!!
	private Object[] getBarOrLineData(HashVO[] hvs) {
		LinkedHashSet xSet = new LinkedHashSet(); //
		LinkedHashSet ySet = new LinkedHashSet(); //
		HashMap dataMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			String str_key_1 = hvs[i].getStringValue(0); //
			String str_key_2 = hvs[i].getStringValue(1); //
			double ld_count = convertDoubleValue(hvs[i].getStringValue(2)); //
			xSet.add(str_key_1); //
			ySet.add(str_key_2); //

			String str_key_span = "#" + str_key_1 + "#" + str_key_2 + "#"; //用于处理值的标记!!!
			if (!dataMap.containsKey(str_key_span)) {
				dataMap.put(str_key_span, ld_count); //
			} else {
				double oldCount = (Double) dataMap.get(str_key_span); //
				ld_count = ld_count + oldCount; //
				dataMap.put(str_key_span, ld_count); //重新置入!!!
			}
		}

		String[] str_x = (String[]) xSet.toArray(new String[0]); //
		String[] str_y = (String[]) ySet.toArray(new String[0]); //
		double[][] ld_values = new double[str_x.length][str_y.length]; //
		for (int i = 0; i < str_x.length; i++) { //
			for (int j = 0; j < str_y.length; j++) { //
				String str_key_span = "#" + str_x[i] + "#" + str_y[j] + "#"; //用于处理值的标记!!!
				Double ld_count = (Double) dataMap.get(str_key_span); ////
				ld_values[i][j] = (ld_count == null ? 0 : ld_count); //
			}
		}
		return new Object[] { str_x, str_y, ld_values }; //
	}

	private double convertDoubleValue(String _text) {
		if (_text == null || _text.trim().equals("")) { //
			return 0; //
		}
		boolean isNumber = getTb().isStrAllNunbers(_text); //
		if (isNumber) {
			return Double.parseDouble(_text); //
		} else {
			return 0; //
		}
	}

/*	public static void main(String[] args) throws JSONException {
		JSONObject jsonobj = new JSONObject();

		HashMap chart = new HashMap();
		chart.put("type", "line");
		jsonobj.put("chart", chart);

		HashMap title = new HashMap();
		title.put("text", "你是谁");
		jsonobj.put("title", title);

		HashMap xAxis = new HashMap();
		List<String> value = new ArrayList();
		JSONArray jsonStrs = new JSONArray();
		jsonStrs.put(0, "cat");
		jsonStrs.put(1, "dog");
		jsonStrs.put(2, "bird");
		jsonStrs.put(3, "duck");
		value.add("2014-01");
		value.add("2014-02");
		value.add("2014-03");

		xAxis.put("categories", jsonStrs);
		jsonobj.put("xAxis", xAxis);

		HashMap series_1 = new HashMap();
		series_1.put("name", "目标值");
		List value2 = new ArrayList();
		value2.add(1d);
		value2.add(2d);
		value2.add(3d);

		series_1.put("data", new JSONArray(value2));

		HashMap series_2 = new HashMap();
		series_2.put("name", "实际值");
		series_2.put("data", value2);
		JSONArray se = new JSONArray();
		se.put(series_1);
		se.put(series_2);
		jsonobj.put("series", se);
		HashMap plotOptions = new HashMap();

		HashMap line = new HashMap();

		HashMap dataLabels = new HashMap();

		dataLabels.put("enabled", true);
		line.put("enableMouseTracking", false);
		line.put("dataLabels", dataLabels);

		plotOptions.put("line", new jstring("123123"));

		jsonobj.put("plotOptions", plotOptions);
		System.out.println(jsonobj.toString());
	}*/

}

/*class jstring implements JSONString {
	private String str;

	public jstring(String _str) {
		str = _str;
	}

	public String toJSONString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{dataLabels: {");
		sb.append("    enabled: true");
		sb.append("},");
		sb.append("enableMouseTracking: false}");

		return sb.toString();
	}

}*/