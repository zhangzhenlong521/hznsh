package cn.com.infostrategy.ui.mdata.querycomp;

import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 
 * @author lcj
 *
 */
public class CheckKeywordsSQLCreater implements BillQueryQuickSQLCreaterIFC {

	private String uploadfiledir = ""; //查询的文件路径
	private boolean isAllContain = false; //是否必须包含全部的关键字

	public String getQuickSQL(BillQueryPanel panel) {
		String keywords = ""; //要查询的关键字
		String keywords_itemkey = "";
		String keywords_primarykey = "";
		String str_isAllContain = "";
		AbstractWLTCompentPanel[] quickQueryCompents = panel.getAllQuickQueryCompents();
		int num = -1;
		int num_isAllContain = -1;
		StringItemVO itemVO = null;
		StringItemVO itemVO_isAllContain = null;
		String itemKey = "";
		for (int i = 0; i < quickQueryCompents.length; i++) {
			itemKey = quickQueryCompents[i].getItemKey();
			if (itemKey.equalsIgnoreCase("_isallcontain")) {
				itemVO_isAllContain = (StringItemVO) quickQueryCompents[i].getObject();
				if (itemVO_isAllContain != null) {
					str_isAllContain = itemVO_isAllContain.getStringValue();
					if (str_isAllContain.equalsIgnoreCase("Y")) {
						isAllContain = true;
					} else {
						isAllContain = false;
					}
					num_isAllContain = i;
				}
			}
			if (itemKey.startsWith("keywords_")) { //如果有keywords的字段，认为是某文件的关键字查询，关键字字段itemKey格式必须为"keywords_"+附件字段
				itemVO = (StringItemVO) quickQueryCompents[i].getObject();
				if (itemVO != null) {
					keywords = itemVO.getStringValue();
					/** start liuxuanfei **/
					String[] keys = itemKey.substring(9).split("#");
					if (keys.length == 2) {
						keywords_itemkey = keys[0];
						keywords_primarykey = keys[1];
					} else {
						keywords_itemkey = keys[0];
						keywords_primarykey = "id"; // 默认是ID, 其实为了兼容以前的, 应该默认为keywords_itemkey
					}
					/** end **/
					quickQueryCompents[i].setObject(null); //因为数据库表中没有keywords字段，所以设置不让生成 “ and keywords like '%%'” 条件
					quickQueryCompents[i].setValue(null);
					num = i;
				}
			}
		}
		String querysql = panel.getQuerySQL(quickQueryCompents);
		if (num_isAllContain != -1) {
			querysql = querysql.replaceAll("and _isallcontain='N'", "");
			querysql = querysql.replaceAll("and _isallcontain='Y'", "");
		}
		if ("".equals(keywords)) { //如果没有关键字查询则直接返回查询sql，跟以前的sql语句没有区别；如果有，则进行数据过滤处理！
			return querysql;
		}
		quickQueryCompents[num].setObject(itemVO); // 查询后保留关键字的显示
		quickQueryCompents[num].setValue(keywords);

		try {
			/** start liuxuanfei **/
			String[][] filesInfo = UIUtil.getStringArrayByDS(null, "select w." + keywords_primarykey + ", w." + keywords_itemkey + " from (" + querysql + ") w ");
			if (filesInfo == null || filesInfo.length == 0) {
				return querysql + " and (1 = 2)";
			}
			String[] str_keywords = new TBUtil().split(keywords, " ");
			List<String> list_ids = UIUtil.getCommonService().checkWordOrExcelContainKeys(uploadfiledir, filesInfo, str_keywords, isAllContain);
			if (list_ids.size() > 0) {
				String insqls = UIUtil.getSubSQLFromTempSQLTableByIDs((String[]) list_ids.toArray(new String[0]));
				querysql = querysql + " and " + keywords_primarykey + " in (" + insqls + ")";
			} else {
				querysql = querysql + " and (1 = 2)";
			}
			/** end **/
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException(e.getMessage());
		}
		return querysql;
	}

	public String getUploadfiledir() {
		return uploadfiledir;
	}

	public void setUploadfiledir(String uploadfiledir) {
		this.uploadfiledir = uploadfiledir;
	}

}
