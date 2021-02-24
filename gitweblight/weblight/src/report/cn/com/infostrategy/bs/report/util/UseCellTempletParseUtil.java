package cn.com.infostrategy.bs.report.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;

/**
 * 根据现有的BillCellVO的配置信息，生成一个解析并且执行过的BillCellVO。
 * 然后新BillCellVO可以直接生存word，用BIllCellPanel打开，到出Excel.
 * @author haoming
 * create by 2013-10-22
 */
public class UseCellTempletParseUtil {
	private TBUtil tbutil = new TBUtil();
	private SalaryFomulaParseUtil formulaParseUtil = new SalaryFomulaParseUtil();

	/**
	 * 
	 * @param _cellTemplet 传入的解析模版对象
	 * @param _baseHVO     传入使用的最基础对象。     
	 */

	public BillCellVO onParse(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception {
		BillCellItemVO cellItemVOs[][] = _cellTemplet.getCellItemVOs(); //导入的原模版
		List<BillCellItemVO[]> list_cellvo = new ArrayList<BillCellItemVO[]>();
		for (int i = 0; i < cellItemVOs.length; i++) {
			List<BillCellItemVO> row_vo = new ArrayList<BillCellItemVO>();
			boolean needbreak = false;
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				BillCellItemVO currCellItemVO = cellItemVOs[i][j]; //遍历单元格数据。
				currCellItemVO.setIseditable("N");
				String span = currCellItemVO.getSpan(); // 
				String spans_row_col[] = span.split(","); //得到行列合并的数量
				String textValue = currCellItemVO.getCellvalue();
				if (textValue != null && textValue.trim().length() > 0) {
					int index = -1;
					if (textValue.startsWith("=")) {//如果以等号开头
						textValue = textValue.trim();
						String formula = textValue.substring(1, textValue.length()); //截取公式
						String valueFactorName = "Cell计算结果" + i + j;
						formulaParseUtil.putDefaultFactorVO("文本", formula, valueFactorName, "", "");
						textValue = String.valueOf(formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(valueFactorName), _baseHVO, new StringBuffer()));
						currCellItemVO.setCellvalue(textValue);//把计算完的结果放进去)
						row_vo.add(currCellItemVO);
					} else if ((index = getForStartType(textValue)) >= 0) { //如果是for循环
						//一旦发现有循环的行，那么这个一行的rowVO重新处理.
						textValue = textValue.trim();
						if (spans_row_col != null && spans_row_col.length == 2) {
							int spaceStartIndex = 0; //空格开始位置
							if (index == 0 || index == 1) {
								spaceStartIndex = 5;
							} else {
								spaceStartIndex = 4;
							}
							boolean haveSpace = textValue.startsWith(" ", spaceStartIndex); //是否有空格							
							if (haveSpace) {
								int forRowConfigCount[] = dealFor(cellItemVOs, i, j, list_cellvo, _baseHVO);
								i += forRowConfigCount[0];
								needbreak = true;
								break;
							} else {
								throw new Exception("循环的语法不正确，开始循环后必须加一个空格。");
							}
						} else {

						}
					} else {
						row_vo.add(currCellItemVO);
					}
				} else {
					row_vo.add(currCellItemVO);
				}
			}
			if (needbreak) {
				continue;
			}
			list_cellvo.add(row_vo.toArray(new BillCellItemVO[0]));
		}
		BillCellItemVO lastitemvos[][] = list_cellvo.toArray(new BillCellItemVO[0][0]);
		BillCellVO cellvo = new BillCellVO();
		cellvo.setRowlength(lastitemvos.length);
		cellvo.setCollength(lastitemvos[0].length);
		cellvo.setCellItemVOs(lastitemvos);
		return cellvo;
	}

	/*
	 * 处理for循环
	 */
	public int[] dealFor(BillCellItemVO[][] allcellvo, int startRow, int startCol, List<BillCellItemVO[]> list_cellvo, HashVO _baseHVO) throws Exception {
		//找到结束标记
		int forEndIndexRow = -1;
		for (int i = startRow; i < allcellvo.length; i++) {
			String textValue = allcellvo[i][startCol].getCellvalue();
			if (textValue != null && textValue.trim().length() > 0) {
				if (getForEndType(textValue) >= 0) {
					forEndIndexRow = i;
					break; //找到结束的位置
				}
			}
		}
		if (forEndIndexRow < 0) {
			throw new Exception("模版中第" + (startRow + 1) + "行中配置了循环标记，但是没有找到相匹配的结束标记.");
		}
		//找到结束后。
		String forcontent = allcellvo[startRow][startCol].getCellvalue(); //得到for配置
		int row_col_span[] = getCellSpan(allcellvo[startRow][startCol].getSpan()); //找到行列合并
		String configFormula = forcontent.substring(forcontent.indexOf(" "), forcontent.indexOf(">"));
		String twoFactor[] = null; //循环的前后两个因子
		HashMap<String, String> configmap = new HashMap<String, String>();
		if (configFormula.contains(";")) { //如果有封号,添加了循环的扩展方法。min=6,少于6行自动补齐;totle=6,只能有6行。
			String[] configs = tbutil.split(configFormula, ";");
			if (configs.length > 0) {
				configFormula = configs[0];
			}
			if (configs.length > 1) {
				String forExtend = configs[1];
				configmap = tbutil.parseStrAsMap(forExtend);
			}
		}
		if (configFormula.contains(":")) {
			twoFactor = tbutil.split(configFormula, ":");
		} else if (configFormula.contains("=")) {
			twoFactor = tbutil.split(configFormula, "=");
		}
		int totleColIndex = allcellvo[0].length; //一共有多少列
		if (twoFactor != null && twoFactor.length == 2) {
			String oneSysObjectFactor = twoFactor[0].trim();
			String forSysVectorObjectFactor = twoFactor[1].trim();
			forSysVectorObjectFactor = forSysVectorObjectFactor.substring(1, forSysVectorObjectFactor.length() - 1);
			Object obj = formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(forSysVectorObjectFactor), _baseHVO, new StringBuffer());

			//判断循环起始的列前和最后列后面，是否有整体合并的。
			List<BillCellItemVO> beforeFor = new ArrayList<BillCellItemVO>(); //支持循环前有多列，但是计算中肯定会合并。
			List<BillCellItemVO> endfor = new ArrayList<BillCellItemVO>(); //支持循环前有多列，但是计算中肯定会合并。
			for (int i = startRow; i <= forEndIndexRow; i++) {
				boolean notNull = false; //判断该行是否用于和循环后的结果就行拼接合并。
				for (int j = 0; j < startCol; j++) { //循环前的内容
					BillCellItemVO cellvo = allcellvo[i][j];
					if (!tbutil.isEmpty(cellvo.getCellvalue())) {
						notNull = true;
						beforeFor.add(cellvo); //
						continue;
					}
					if (notNull) {
						beforeFor.add(cellvo); //
					}
				}
				for (int j = startCol + row_col_span[1]; j < totleColIndex; j++) {
					BillCellItemVO cellvo = allcellvo[i][j];
					if (notNull) {
						endfor.add(cellvo);
						continue;
					}
					if (!tbutil.isEmpty(cellvo.getCellvalue())) {
						notNull = true;
						endfor.add(cellvo);
						continue;
					}
				}
				if (notNull) {
					break;
				}
			}

			if (obj instanceof HashVO[]) {
				int rowcount = 0;
				HashVO vectorHashvos[] = (HashVO[]) obj; //执行后，得到了所有结果。
				int rowAllRow = vectorHashvos.length;
				if (configmap.containsKey("min")) {
					int forminnum = Integer.parseInt(configmap.get("min"));
					if (rowAllRow < forminnum) {
						rowAllRow = forminnum;
					}
				} else if (configmap.containsKey("totle")) {
					int forminnum = Integer.parseInt(configmap.get("totle"));
					rowAllRow = forminnum;
				}
				for (int i = 0; i < rowAllRow; i++) {
					HashVO hashvo = new HashVO();
					if (vectorHashvos.length > i) {
						hashvo = vectorHashvos[i]; //遍历集合的内容。
					}
					String onefactor = oneSysObjectFactor.substring(1, oneSysObjectFactor.length() - 1);
					//					formulaParseUtil.resetAllHashMapHis(true);
					formulaParseUtil.getFoctorHashVO(onefactor);
					formulaParseUtil.forceSetCurrBaseVO(hashvo);
					formulaParseUtil.putDefaultFactorValue(onefactor, hashvo);
					//接下来遍历循环内部的cell行
					for (int row = startRow + 1; row < forEndIndexRow; row++) {
						List<BillCellItemVO> rowlist = new ArrayList<BillCellItemVO>();
						if (rowcount == 0) {
							for (int j = 0; j < beforeFor.size(); j++) {
								BillCellItemVO cellitemvo = beforeFor.get(j).deepClone();
								int span[] = getCellSpan(cellitemvo.getSpan());
								cellitemvo.setSpan((span[0] > 0 ? (rowAllRow * (forEndIndexRow - startRow - 1)) : span[0]) + "," + span[1]);
								rowlist.add(cellitemvo);
							}
							rowcount++;
						} else {
							for (int j = 0; j < beforeFor.size(); j++) {
								BillCellItemVO cellitemvo = beforeFor.get(j).deepClone();
								cellitemvo.setCellvalue("");
								cellitemvo.setSpan("1,1");
								rowlist.add(cellitemvo);
							}
						}
						for (int j = startCol; j < totleColIndex; j++) {
							BillCellItemVO currCellItemVO = allcellvo[row][j];
							currCellItemVO.setIseditable("N");
							String textValue = allcellvo[row][j].getCellvalue();
							if (textValue != null && textValue.trim().length() > 0) {
								if (textValue.startsWith("=")) {//如果以等号开头
									textValue = textValue.trim();
									String formula = textValue.substring(1, textValue.length()); //截取公式
									String valueFactorName_item = "循环内表格计算结果" + i + j + row;
									formulaParseUtil.putDefaultFactorVO("文本", formula, valueFactorName_item, "", "");
									if (vectorHashvos.length > i) { //可能会多循环几次，补足空格。
										textValue = String.valueOf(formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(valueFactorName_item), hashvo, new StringBuffer()));
									} else {
										textValue = "";
									}
									currCellItemVO = allcellvo[row][j].deepClone();
									currCellItemVO.setCellvalue(textValue);//把计算完的结果放进去)
									rowlist.add(currCellItemVO);
								} else {
									rowlist.add(currCellItemVO);
								}
							} else {
								rowlist.add(currCellItemVO);
							}
						}
						list_cellvo.add(rowlist.toArray(new BillCellItemVO[0]));
					}
				}
				return new int[] { forEndIndexRow - startRow };
			} else if (obj instanceof HashVO) {
			}
			return new int[] { 0, 0 };
		} else {
			throw new Exception("模版中第" + (startRow + 1) + "行中配置了循环标记，但是没有找到冒号或者等号的配置。");
		}
	}

	/*
	 * 取得for循环的配置类型。
	 */
	public int getForStartType(String _forContent) {
		int rtvalue = -1;
		if (_forContent.startsWith("<循环开始")) {
			rtvalue = 0;
		} else if (_forContent.startsWith("<开始循环")) {
			rtvalue = 1;
		} else if (_forContent.startsWith("<for")) {
			rtvalue = 2;
		}
		return rtvalue;
	}

	public int getForEndType(String _forContent) {
		int rtvalue = -1;
		if (_forContent.endsWith("循环结束")) {
			rtvalue = 0;
		} else if (_forContent.endsWith("结束循环>")) {
			rtvalue = 1;
		} else if (_forContent.endsWith("for>")) {
			rtvalue = 2;
		} else if (_forContent.endsWith("结束>")) {
			rtvalue = 3;
		}
		return rtvalue;
	}

	public int[] getCellSpan(String _span) {
		String spans_row_col[] = _span.split(","); //得到行列合并的数量
		int rowspan = Integer.parseInt(spans_row_col[0]); //行合并
		int colspan = Integer.parseInt(spans_row_col[1]);//列合并 ，取得这个循环覆盖的宽度
		return new int[] { rowspan, colspan };
	}
}
