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
 * �������е�BillCellVO��������Ϣ������һ����������ִ�й���BillCellVO��
 * Ȼ����BillCellVO����ֱ������word����BIllCellPanel�򿪣�����Excel.
 * @author haoming
 * create by 2013-10-22
 */
public class UseCellTempletParseUtil {
	private TBUtil tbutil = new TBUtil();
	private SalaryFomulaParseUtil formulaParseUtil = new SalaryFomulaParseUtil();

	/**
	 * 
	 * @param _cellTemplet ����Ľ���ģ�����
	 * @param _baseHVO     ����ʹ�õ����������     
	 */

	public BillCellVO onParse(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception {
		BillCellItemVO cellItemVOs[][] = _cellTemplet.getCellItemVOs(); //�����ԭģ��
		List<BillCellItemVO[]> list_cellvo = new ArrayList<BillCellItemVO[]>();
		for (int i = 0; i < cellItemVOs.length; i++) {
			List<BillCellItemVO> row_vo = new ArrayList<BillCellItemVO>();
			boolean needbreak = false;
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				BillCellItemVO currCellItemVO = cellItemVOs[i][j]; //������Ԫ�����ݡ�
				currCellItemVO.setIseditable("N");
				String span = currCellItemVO.getSpan(); // 
				String spans_row_col[] = span.split(","); //�õ����кϲ�������
				String textValue = currCellItemVO.getCellvalue();
				if (textValue != null && textValue.trim().length() > 0) {
					int index = -1;
					if (textValue.startsWith("=")) {//����ԵȺſ�ͷ
						textValue = textValue.trim();
						String formula = textValue.substring(1, textValue.length()); //��ȡ��ʽ
						String valueFactorName = "Cell������" + i + j;
						formulaParseUtil.putDefaultFactorVO("�ı�", formula, valueFactorName, "", "");
						textValue = String.valueOf(formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(valueFactorName), _baseHVO, new StringBuffer()));
						currCellItemVO.setCellvalue(textValue);//�Ѽ�����Ľ���Ž�ȥ)
						row_vo.add(currCellItemVO);
					} else if ((index = getForStartType(textValue)) >= 0) { //�����forѭ��
						//һ��������ѭ�����У���ô���һ�е�rowVO���´���.
						textValue = textValue.trim();
						if (spans_row_col != null && spans_row_col.length == 2) {
							int spaceStartIndex = 0; //�ո�ʼλ��
							if (index == 0 || index == 1) {
								spaceStartIndex = 5;
							} else {
								spaceStartIndex = 4;
							}
							boolean haveSpace = textValue.startsWith(" ", spaceStartIndex); //�Ƿ��пո�							
							if (haveSpace) {
								int forRowConfigCount[] = dealFor(cellItemVOs, i, j, list_cellvo, _baseHVO);
								i += forRowConfigCount[0];
								needbreak = true;
								break;
							} else {
								throw new Exception("ѭ�����﷨����ȷ����ʼѭ��������һ���ո�");
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
	 * ����forѭ��
	 */
	public int[] dealFor(BillCellItemVO[][] allcellvo, int startRow, int startCol, List<BillCellItemVO[]> list_cellvo, HashVO _baseHVO) throws Exception {
		//�ҵ��������
		int forEndIndexRow = -1;
		for (int i = startRow; i < allcellvo.length; i++) {
			String textValue = allcellvo[i][startCol].getCellvalue();
			if (textValue != null && textValue.trim().length() > 0) {
				if (getForEndType(textValue) >= 0) {
					forEndIndexRow = i;
					break; //�ҵ�������λ��
				}
			}
		}
		if (forEndIndexRow < 0) {
			throw new Exception("ģ���е�" + (startRow + 1) + "����������ѭ����ǣ�����û���ҵ���ƥ��Ľ������.");
		}
		//�ҵ�������
		String forcontent = allcellvo[startRow][startCol].getCellvalue(); //�õ�for����
		int row_col_span[] = getCellSpan(allcellvo[startRow][startCol].getSpan()); //�ҵ����кϲ�
		String configFormula = forcontent.substring(forcontent.indexOf(" "), forcontent.indexOf(">"));
		String twoFactor[] = null; //ѭ����ǰ����������
		HashMap<String, String> configmap = new HashMap<String, String>();
		if (configFormula.contains(";")) { //����з��,�����ѭ������չ������min=6,����6���Զ�����;totle=6,ֻ����6�С�
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
		int totleColIndex = allcellvo[0].length; //һ���ж�����
		if (twoFactor != null && twoFactor.length == 2) {
			String oneSysObjectFactor = twoFactor[0].trim();
			String forSysVectorObjectFactor = twoFactor[1].trim();
			forSysVectorObjectFactor = forSysVectorObjectFactor.substring(1, forSysVectorObjectFactor.length() - 1);
			Object obj = formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(forSysVectorObjectFactor), _baseHVO, new StringBuffer());

			//�ж�ѭ����ʼ����ǰ������к��棬�Ƿ�������ϲ��ġ�
			List<BillCellItemVO> beforeFor = new ArrayList<BillCellItemVO>(); //֧��ѭ��ǰ�ж��У����Ǽ����п϶���ϲ���
			List<BillCellItemVO> endfor = new ArrayList<BillCellItemVO>(); //֧��ѭ��ǰ�ж��У����Ǽ����п϶���ϲ���
			for (int i = startRow; i <= forEndIndexRow; i++) {
				boolean notNull = false; //�жϸ����Ƿ����ں�ѭ����Ľ������ƴ�Ӻϲ���
				for (int j = 0; j < startCol; j++) { //ѭ��ǰ������
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
				HashVO vectorHashvos[] = (HashVO[]) obj; //ִ�к󣬵õ������н����
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
						hashvo = vectorHashvos[i]; //�������ϵ����ݡ�
					}
					String onefactor = oneSysObjectFactor.substring(1, oneSysObjectFactor.length() - 1);
					//					formulaParseUtil.resetAllHashMapHis(true);
					formulaParseUtil.getFoctorHashVO(onefactor);
					formulaParseUtil.forceSetCurrBaseVO(hashvo);
					formulaParseUtil.putDefaultFactorValue(onefactor, hashvo);
					//����������ѭ���ڲ���cell��
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
								if (textValue.startsWith("=")) {//����ԵȺſ�ͷ
									textValue = textValue.trim();
									String formula = textValue.substring(1, textValue.length()); //��ȡ��ʽ
									String valueFactorName_item = "ѭ���ڱ�������" + i + j + row;
									formulaParseUtil.putDefaultFactorVO("�ı�", formula, valueFactorName_item, "", "");
									if (vectorHashvos.length > i) { //���ܻ��ѭ�����Σ�����ո�
										textValue = String.valueOf(formulaParseUtil.onExecute(formulaParseUtil.getFoctorHashVO(valueFactorName_item), hashvo, new StringBuffer()));
									} else {
										textValue = "";
									}
									currCellItemVO = allcellvo[row][j].deepClone();
									currCellItemVO.setCellvalue(textValue);//�Ѽ�����Ľ���Ž�ȥ)
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
			throw new Exception("ģ���е�" + (startRow + 1) + "����������ѭ����ǣ�����û���ҵ�ð�Ż��ߵȺŵ����á�");
		}
	}

	/*
	 * ȡ��forѭ�����������͡�
	 */
	public int getForStartType(String _forContent) {
		int rtvalue = -1;
		if (_forContent.startsWith("<ѭ����ʼ")) {
			rtvalue = 0;
		} else if (_forContent.startsWith("<��ʼѭ��")) {
			rtvalue = 1;
		} else if (_forContent.startsWith("<for")) {
			rtvalue = 2;
		}
		return rtvalue;
	}

	public int getForEndType(String _forContent) {
		int rtvalue = -1;
		if (_forContent.endsWith("ѭ������")) {
			rtvalue = 0;
		} else if (_forContent.endsWith("����ѭ��>")) {
			rtvalue = 1;
		} else if (_forContent.endsWith("for>")) {
			rtvalue = 2;
		} else if (_forContent.endsWith("����>")) {
			rtvalue = 3;
		}
		return rtvalue;
	}

	public int[] getCellSpan(String _span) {
		String spans_row_col[] = _span.split(","); //�õ����кϲ�������
		int rowspan = Integer.parseInt(spans_row_col[0]); //�кϲ�
		int colspan = Integer.parseInt(spans_row_col[1]);//�кϲ� ��ȡ�����ѭ�����ǵĿ��
		return new int[] { rowspan, colspan };
	}
}
