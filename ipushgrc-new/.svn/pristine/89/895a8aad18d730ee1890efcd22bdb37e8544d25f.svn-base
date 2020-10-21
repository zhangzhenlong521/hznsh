package com.pushworld.ipushgrc.bs.wfrisk.p010;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;

/**
 * 这个类是把Visio的SVG文件转换成普信流程图的工具类。里面解析SVG文件逻辑很复杂。还需优化！ 框如果窄，文字换行看不到了。。
 * 
 * @author haoming
 */
public class ImportVisioBsUtil implements Serializable {
	private static int flag = 0; // shape控件下g标签嵌套了几层
	private static int typeFlag = 0; // 控件类型
	private static int groupFlag = 0; // 是否为组控件
	private static int shapeNum = 0; // 组控件中的控件个数
	private static int pathNum = 0; // 组控件中的连线个数
	private static int isFirstGroup = 0; // 是否第一个组控件。0初始状态,1是,2已经在第一个组控件之后
	private static int isGetProName = 0; // 是否已经获取到流程图名称。0不是,1是
	private static int isGetGroupName = 0; // 是否已经获取到组控件名
	private static int isGetGroupParas = 0; // 是否已经获取到组控件的各种参数
	private static String groupLineStartX; // 组控件连线起点横坐标
	private static String groupLineStartY; // 组控件连线起点纵坐标
	private static String groupLineEndX; // 组控件连线终点横坐标
	private static String groupLineEndY; // 组控件连线终点纵坐标
	private static String groupTranslateX; // 组横坐标偏移量
	private static String groupTranslateY; // 组纵坐标偏移量
	private static String shapeTranslateX; // 控件横坐标偏移量
	private static String shapeTranslateY; // 控件纵坐标偏移量
	private static float shapeMaxX; // 控件路径中最大的横坐标
	private static float shapeMaxY; // 控件路径中最大的纵坐标
	private static float shapeMinX; // 控件路径中最小的横坐标
	private static float shapeMinY; // 控件路径中最小的纵坐标
	private static String shapeId; // 控件ID
	private static int isGroup; // 是否参照组控件偏移坐标。0参照,1不参照,2参照组偏移不参照自身
	private static StringBuilder insertStrFeGroup; // 非组控件入库SQL语句
	private static StringBuilder insertStrGroup; // 组控件入库SQL语句
	private static int isCoordinateFlag; // 复合框是否已经取到坐标。0取到,1未取到
	private static int isImportFlag; // 是否进行了导入操作。0未导入,1已导入
	private static BufferedReader br; //

	private static final String PHASE_X = "10"; // 流程图里面阶段的固定起始位置
	private static final String PHASE_H = "40"; // 流程图里面阶段的固定高度
	private static final String GroupTypeDept = "DEPT";
	private static final String GroupTypeStation = "STATION";
	private static final int lineAreaR = 45; // 最大放射范围
	private static final double proportion = 1.3; // 放缩比例 1.3-1.5

	private static String processName; // 流程图名
	private static String groupName; // 组控件名称
	private static String groupX; // 组控件横坐标
	private static String groupY; // 组控件纵坐标
	private static String groupW; // 组控件宽度
	private static String groupH; // 组控件高度
	private static String objectName; // 控件名称
	private static String objectX; // 控件横坐标
	private static String objectY; // 控件纵坐标
	private static String objectW; // 控件宽度
	private static String objectH; // 控件高度
	private static String objectLinePath; // 连线控件路径
	private static int objectType; // 控件类型

	private static String processDBId; // 流程入库后id
	private static int activityCode;

	private static int beginX = 70; // 系统第一个部门开始X位置
	private static int beginY = 60; // 系统第一个部门开始Y位置
	private static float moveUp = 0f; // 整体上移距离
	private static float moveLeft = 0f;// 整体左移距离
	private List<GroupVO> group = null; // 所有的部门和阶段
	private List<TransitionBean> transitionList = null; // 所有连线
	private static String firstRectWidth = null; // 第一个矩形的宽度。
	private static String firstRectHeight = null; // 第一个矩形的高度。
	private boolean otherActivityPath = false; // 是否执行过一次其他环节的path，折点判断。
	// 有时带弧线的环节会有两个<path>标签

	CommDMO commdmo = new CommDMO();
	int activityseq = 0;

	public String[] init(String cmpfileid, String cmpfilecode, String str) throws Exception {
		String fileID = cmpfileid;
		isImportFlag = 0;
		activityCode = 0;
		groupTranslateX = "0.0";
		groupTranslateY = "0.0";
		shapeTranslateX = "0.0";
		shapeTranslateY = "0.0";
		groupFlag = 0; // 是否为组控件
		shapeNum = 0; // 组控件中的控件个数
		pathNum = 0; // 组控件中的连线个数
		isFirstGroup = 0; // 是否第一个组控件。0初始状态,1是,2已经在第一个组控件之后
		isGetProName = 0; // 是否已经获取到流程图名称。0不是,1是
		isGetGroupName = 0; // 是否已经获取到组控件名
		isGetGroupParas = 0; // 是否已经获取到组控件的各种参数
		moveUp = 0f; // 整体上移距离
		moveLeft = 0f;// 整体左移距离
		group = new ArrayList<GroupVO>();
		transitionList = new ArrayList<TransitionBean>();
		String process[] = new String[3];
		// 选择文件、读取文件内容
		String insertProcessSql = null;
		try {
			br = new BufferedReader(new StringReader(str));
			int readNum = 0;
			String strLine;
			insertProcessSql = insertProcessSQL(fileID);
			while ((strLine = br.readLine()) != null) {
				if (readNum < 2) {
					if (readNum == 0 && !strLine.trim().equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")) {
						throw new WLTAppException("文件不是标准svg文件,请把Visio文件另存为可缩放的向量图格式(svg)文件");
					}
					if (readNum == 1 && (!strLine.trim().contains("svg") || !strLine.trim().contains("SVG"))) {
						throw new WLTAppException("文件不是标准svg文件,请把Visio文件另存为可缩放的向量图格式(svg)文件");
					}
					readNum++;
				}
				if (strLine.trim().length() >= 12) {

					if ("<g id=".equals(strLine.trim().substring(0, 6)) && "group".equals(strLine.trim().substring(7, 12))) {
						groupFlag = 1;
						shapeNum = 0;
						pathNum = 0;
						isGetGroupName = 0;
						isGetGroupParas = 0;

						// 判断是否第一个组控件
						if (isFirstGroup == 0) {
							isFirstGroup = 1;
						} else if (isFirstGroup == 1) {
							isFirstGroup = 2;
						}
						// 组偏移量获取
						if (strLine.contains("translate")) {//有些图可能不包含位置信息。
							groupTranslateX = strLine.trim().substring(strLine.trim().indexOf("translate") + 10, strLine.trim().indexOf(",", strLine.trim().indexOf("translate")));
							groupTranslateY = strLine.trim().substring(strLine.trim().indexOf(",", strLine.trim().indexOf("translate")) + 1, strLine.trim().indexOf(")", strLine.trim().indexOf("translate")));
						}
					}
				}
				// 获取流程名
				if (strLine.trim().length() > 13) {
					if (isFirstGroup == 1 && isGetProName == 0 && strLine.trim().contains("<desc>")) {
						processName = strLine.trim().substring(strLine.trim().indexOf("<desc>") + 6, strLine.trim().indexOf("</desc>"));
						processName = processName.replaceAll("&#60;", "<");
						processName = processName.replaceAll("&#62;", ">");
						isGetProName = 1;
						insertProcessSql = insertProcessSQL(cmpfileid);
					}
				}

				// 当被包含在组控件中时的处理
				if (groupFlag >= 1) {

					// 判断是否为图形控件
					if (strLine.trim().length() >= 12) {
						if ("<g id=".equals(strLine.trim().substring(0, 6)) && "shape".equals(strLine.trim().substring(7, 12))) {
							flag = 1;
							isGroup = 0;
							objectName = null;
							shapeId = strLine.trim().substring(7, strLine.trim().indexOf("\"", strLine.trim().indexOf("id=") + 4));
							if (objectType != 10)
								objectType = 0;
							// 控件偏移量获取
							if (strLine.trim().contains("translate")) {
								shapeTranslateX = strLine.trim().substring(strLine.trim().indexOf("translate") + 10, strLine.trim().indexOf(",", strLine.trim().indexOf("translate")));
								shapeTranslateY = strLine.trim().substring(strLine.trim().indexOf(",", strLine.trim().indexOf("translate")) + 1, strLine.trim().indexOf(")", strLine.trim().indexOf("translate")));
								if (strLine.trim().contains("v:layerMember=") && strLine.trim().contains("translate")) {
									isGroup = 1;
								} else {
									isGroup = 0;
								}
							} else {
								if (strLine.trim().contains("v:layerMember=")) {
									isGroup = 2;
								}
							}
							shapeNum++;
						}
					}

					// 获取控件类型
					if (strLine.trim().length() >= 15) {
						if ("<title>".equals(strLine.trim().substring(0, 7))) {
							otherActivityPath = true;
							if (!strLine.trim().contains(".")) {
								if ("Process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "流程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 1; // 矩形
								} else if ("Predefined process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "预先定义的进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 2; // 带边的矩形
								} else if ("Decision".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "判定".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 3; // 菱形
								} else if ("Terminator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "终结符".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 5; // 终结符
								} else if ("Data".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "数据".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 6; // 平行四边形
								} else if ("Internal storage".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "内部存储器".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 8; // 复合框
									isCoordinateFlag = 0;
								} else if ("Dynamic connector".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "动态连接线".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 9; // 连线
								} else if ("Document".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "文档".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 7; // 文档
								} else if ("Separator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "分隔符".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 10; // 分割线
								} else if (!"Functional band".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) && !"Annotation".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = -1; // 其他
								}
							} else {
								if ("Process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "流程".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 1; // 矩形
								} else if ("Predefined process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "预先定义的进程".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 2; // 带边的矩形
								} else if ("Decision".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "判定".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 3; // 菱形
								} else if ("Terminator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "终结符".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 5; // 终结符
								} else if ("Data".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "数据".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 6; // 平行四边形
								} else if ("Internal storage".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "内部存储器".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 8; // 复合框
									isCoordinateFlag = 0;
								} else if ("Dynamic connector".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "动态连接线".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 9; // 连线
								} else if ("Document".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "文档".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 7; // 文档
								} else if ("Separator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "分隔符".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 10; // 分割线
								} else if (!"Functional band".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) && !"Annotation".equals(strLine.trim().substring(7, strLine.trim().indexOf("."))) && !"工作表".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = -1;
								}
							}

						}

					}

					// 判断是否节点开始结尾标识
					if (strLine.trim().length() >= 4) {

						if ("<g".equals(strLine.trim().substring(0, 2))) {
							flag += 1;
							groupFlag++;
						} else if ("</g>".equals(strLine.trim().substring(0, 4)) || "</g>".equals(strLine.trim().substring(strLine.trim().length() - 4))) {
							flag -= 1;
							groupFlag--;
							if (groupFlag == 1 && pathNum == 2) {
								// //system.out.println("阶段分割线");
							}
						}

					}

					// 判断控件类型
					if (strLine.trim().length() >= 6) {
						// 组控件名称获取
						if (groupFlag == 2 && strLine.trim().contains("<desc>") && isGetGroupName == 0) {
							groupName = strLine.trim().substring(strLine.trim().indexOf("<desc>") + 6, strLine.trim().indexOf("/desc>") - 1);
							groupName = groupName.replaceAll("&#60;", "<");
							groupName = groupName.replaceAll("&#62;", ">");
							isGetGroupName = 1;
						}
						// 普通控件名称获取
						if (strLine.trim().contains("<desc>") && (isGroup == 1 || isGroup == 2)) {
							objectName = strLine.trim().substring(strLine.trim().indexOf("<desc>") + 6, strLine.trim().indexOf("/desc>") - 1);
							objectName = objectName.replaceAll("&#60;", "<");
							objectName = objectName.replaceAll("&#62;", ">");
						}
						// 矩形
						if (flag == 2 && "<rect".equals(strLine.trim().substring(0, 5)) && !strLine.trim().contains("v:rectContext=")) {

							final String initX = strLine.trim().substring(strLine.trim().indexOf("x=") + 3, strLine.trim().indexOf("y=") - 2);
							final String initY = strLine.trim().substring(strLine.trim().indexOf("y=") + 3, strLine.trim().indexOf("width=") - 2);
							final String initW = strLine.trim().substring(strLine.trim().indexOf("width=") + 7, strLine.trim().indexOf("height=") - 2);
							final String initH = strLine.trim().substring(strLine.trim().indexOf("height=") + 8, strLine.trim().indexOf("class=") - 2);
							String absoluteX = "0.0";
							String absoluteY = "0.0";

							if (groupFlag > 1 && isGroup == 0) {
								absoluteX = Float.toString(Float.parseFloat(initX) + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX));
								absoluteY = Float.toString(Float.parseFloat(initY) + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY));
							} else {
								if (isGroup == 1) {
									absoluteX = Float.toString(Float.parseFloat(initX) + Float.parseFloat(shapeTranslateX));
									absoluteY = Float.toString(Float.parseFloat(initY) + Float.parseFloat(shapeTranslateY));
								} else if (isGroup == 2) {
									absoluteX = Float.toString(Float.parseFloat(initX) + Float.parseFloat(groupTranslateX));
									absoluteY = Float.toString(Float.parseFloat(initY) + Float.parseFloat(groupTranslateY));
								}

								// 普通控件参数赋值(矩形)
								objectX = absoluteX;
								objectY = absoluteY;
								objectW = initW;
								objectH = initH;
								feGroupObjectImport();
							}
							// 组控件参数赋值(矩形)
							if (isGetGroupParas == 0 && isFirstGroup > 1 && isGroup != 2) {
								groupX = absoluteX;
								groupY = absoluteY;
								groupW = initW;
								groupH = initH;
								groupObjectIntoList(GroupTypeDept);
								isGetGroupParas = 1;
							}
							typeFlag = 0;
							flag = 0;
						} else if (flag == 2 && "<path d".equals(strLine.trim().substring(0, 7))) { // 如果是连线

							typeFlag = 1;
							String initX_Line;
							String initY_Line;
							String absoluteX_Line;
							String absoluteY_Line;
							String subStr;
							String strLineFormula;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								final StringBuilder pathStr = new StringBuilder(); // 路径记录
								strLineFormula = strLine.trim();
								strLineFormula = formulaLine(strLineFormula);

								indexFlag = strLineFormula.indexOf("d=\"M");
								initX_Line = strLineFormula.substring(strLineFormula.indexOf("d=\"M") + 4, strLineFormula.indexOf(" ", strLineFormula.indexOf("d=\"M")));
								initY_Line = strLineFormula.substring(strLineFormula.indexOf(" ", strLineFormula.indexOf("d=\"M")) + 1, strLineFormula.indexOf("L", strLineFormula.indexOf("d=\"M")) - 1);

								if (groupFlag > 1 && isGroup == 0) {
									absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX));
									absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY));
								} else {
									absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(shapeTranslateX));
									absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(shapeTranslateY));
								}

								pathStr.append(absoluteX_Line + ",");
								pathStr.append(absoluteY_Line + ";");

								// 设置组控件参数
								if (isGetGroupParas == 0 && isFirstGroup > 1) {

									groupLineStartX = absoluteX_Line;
									groupLineStartY = absoluteY_Line;

								}
								// 获取所有拐点坐标
								while (strLineFormula.substring(indexFlag).indexOf("L") > 0) {

									subStr = strLineFormula.substring(indexFlag);
									indexFlag = strLineFormula.indexOf("L", strLineFormula.indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (groupFlag > 1 && isGroup == 0) {
										absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX));
										absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY));
									} else if (isGroup == 2) {
										absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(groupTranslateX));
										absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(groupTranslateY));
									} else {
										absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(shapeTranslateX));
										absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(shapeTranslateY));
									}

									pathStr.append(absoluteX_Line + ",");
									pathStr.append(absoluteY_Line + ";");
								}

								if (isGroup == 1 && objectType == 9) {

									objectLinePath = getChangedLine(pathStr.toString());
									final TransitionBean bean = new TransitionBean();
									bean.setWfname(formulaStr(objectName));
									bean.setPoint(objectLinePath);
									transitionList.add(bean);
								}
								// 设置组控件参数
								if (isGetGroupParas == 0 && isFirstGroup > 1 && isGroup != 2 && objectType == 10) {

									groupLineEndX = absoluteX_Line;
									groupLineEndY = absoluteY_Line;

									groupX = PHASE_X;
									groupY = groupLineStartY;
									groupW = Float.toString(Float.parseFloat(groupLineEndX) - Float.parseFloat(groupLineStartX));
									groupH = PHASE_H;
									groupObjectIntoList(GroupTypeStation);
									isGetGroupParas = 1;

								}

							}
							pathNum++;
						}
						// 带边的矩形
						if (flag == 2 && objectType == 2 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {
								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								if (groupFlag > 1 && isGroup == 0) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();
								flag = 0;
							}
						}

						// 菱形
						else if (flag == 2 && objectType == 3 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								if (groupFlag > 1 && isGroup == 0) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								if (isGroup == 1) {
									feGroupObjectImport();

								}
							}

						}

						// 椭圆
						else if (flag == 2 && objectType == 5) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							String r = "0.0";
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));

									initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("A", subStr.indexOf("L") + 1) - 1);
									if ("0.0".equals(r)) {
										if (subStr.contains("A")) {
											r = subStr.substring(subStr.indexOf("A") + 1, subStr.indexOf(" ", subStr.indexOf("A")));
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX - Float.parseFloat(r));
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX + Float.parseFloat(r) + Float.parseFloat(r));
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();

							}

						}

						// 平行四边形
						else if (flag == 2 && objectType == 6 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								if (groupFlag > 1 && isGroup == 0) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								feGroupObjectImport();

							}

						}
						// 其他 全部转为矩形。
						else if (flag == 2 && objectType == -1) {

							initMaxMinValue();
							int indexFlag = 0;
							String subStr;
							String initX_Line;
							String initY_Line = "";
							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {
								indexFlag = strLine.trim().indexOf("d=\"M");
								strLine = getWholeLine(strLine);
								strLine = formulaLine(strLine);
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								if (strLine.trim().substring(0, 27).contains("L")) {
									initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);
								} else if (strLine.trim().substring(0, 27).contains("A")) {
									initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("A", strLine.trim().indexOf("d=\"M")) - 1);
								}
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMinY = Float.parseFloat(initY_Line);
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}
								if (groupFlag > 1 && isGroup == 0) {
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}
								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = firstRectWidth == null ? "72.81" : firstRectWidth;
								objectH = firstRectHeight == null ? "45.51" : firstRectHeight;
								if (isGroup == 1 && otherActivityPath == true) {
									feGroupObjectImport();
									otherActivityPath = false;
								}
							}
						}
						// 文档
						else if (flag == 2 && objectType == 7) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);
								if (strLine.contains("A")) {
									strLine = strLine.substring(0, strLine.indexOf("A")) + "class=\"st1\"/>";
								}
								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								if (groupFlag > 1 && isGroup == 0) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								if (isGroup == 1) {

									feGroupObjectImport();

								}
							}
						}

						// 复合框处理
						else if (flag == 2 && objectType == 8 && isCoordinateFlag == 0 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {
								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								if (groupFlag > 1 && isGroup == 0) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMaxX = shapeMaxX + Float.parseFloat(groupTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(groupTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
									shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								feGroupObjectImport();

								isCoordinateFlag = 1;

							}

						}

					}

					if (groupFlag == 1) {
						groupFlag = 0; // 初始化
					}

				} else {// 不为组控件时

					// 判断是否为图形控件
					if (strLine.trim().length() >= 12) {
						if ("<g id=".equals(strLine.trim().substring(0, 6)) && "shape".equals(strLine.trim().substring(7, 12))) {
							flag = 1;
							objectName = null;
							objectType = 0;
							shapeId = strLine.trim().substring(7, strLine.trim().indexOf("\"", strLine.trim().indexOf("id=") + 4));
							if (strLine.trim().contains("translate")) {
								shapeTranslateX = strLine.trim().substring(strLine.trim().indexOf("translate") + 10, strLine.trim().indexOf(",", strLine.trim().indexOf("translate")));
								shapeTranslateY = strLine.trim().substring(strLine.trim().indexOf(",", strLine.trim().indexOf("translate")) + 1, strLine.trim().indexOf(")", strLine.trim().indexOf("translate")));
							}
						}
					}

					// 判断是否节点开始结尾标识
					if (strLine.trim().length() >= 4) {

						if ("<g".equals(strLine.trim().substring(0, 2))) {
							flag += 1;
						} else if ("</g>".equals(strLine.trim().substring(0, 4)) || "</g>".equals(strLine.trim().substring(strLine.trim().length() - 4))) {
							flag -= 1;
						}

					}

					// 获取控件类型
					if (strLine.trim().length() >= 15) {

						if ("<title>".equals(strLine.trim().substring(0, 7))) { // 4是标注
							otherActivityPath = true;
							if (!strLine.trim().contains(".")) {
								if ("Process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "流程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 1; // 矩形
								} else if ("Predefined process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "预先定义的进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 2; // 带边的矩形
								} else if ("Decision".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "判定".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 3; // 菱形
								} else if ("Terminator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "终结符".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 5; // 终结符
								} else if ("Data".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "数据".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 6; // 平行四边形
								} else if ("Internal storage".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "内部存储器".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 8; // 复合框
									isCoordinateFlag = 0;
								} else if ("Dynamic connector".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "动态连接线".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 9; // 连线
								} else if ("Document".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) || "文档".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = 7; // 文档
								} else if (!"Functional band".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("</title>"))) && !"Annotation".equals(strLine.trim().substring(7, strLine.trim().indexOf("</title>")))) {
									objectType = -1; // 其他
								}
							} else {
								if ("Process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "进程".equals(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "流程".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 1; // 矩形
								} else if ("Predefined process".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "预先定义的进程".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 2; // 带边的矩形
								} else if ("Decision".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "判定".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 3; // 菱形
								} else if ("Terminator".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "终结符".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 5; // 终结符
								} else if ("Data".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "数据".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 6; // 平行四边形
								} else if ("Internal storage".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "内部存储器".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 8; // 复合框
									isCoordinateFlag = 0;
								} else if ("Dynamic connector".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "动态连接线".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 9; // 连线
								} else if ("Document".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) || "文档".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = 7; // 文档
								} else if (!"Functional band".equalsIgnoreCase(strLine.trim().substring(7, strLine.trim().indexOf("."))) && !"Annotation".equals(strLine.trim().substring(7, strLine.trim().indexOf("."))) && !"工作表".equals(strLine.trim().substring(7, strLine.trim().indexOf(".")))) {
									objectType = -1;
								}
							}
						}

					}

					// 判断控件类型
					if (strLine.trim().length() >= 6) {

						// 普通控件名称获取
						if (strLine.trim().contains("<desc>")) {
							objectName = strLine.trim().substring(strLine.trim().indexOf("<desc>") + 6, strLine.trim().indexOf("/desc>") - 1);
						}

						// 矩形
						if (flag == 2 && objectType == 1 && "<rect".equals(strLine.trim().substring(0, 5)) && strLine.trim().contains("class=") && !strLine.trim().contains("v:rectContext=")) {

							final String initX = strLine.trim().substring(strLine.trim().indexOf("x=") + 3, strLine.trim().indexOf("y=") - 2);
							final String initY = strLine.trim().substring(strLine.trim().indexOf("y=") + 3, strLine.trim().indexOf("width=") - 2);
							final String initW = strLine.trim().substring(strLine.trim().indexOf("width=") + 7, strLine.trim().indexOf("height=") - 2);
							final String initH = strLine.trim().substring(strLine.trim().indexOf("height=") + 8, strLine.trim().indexOf("class=") - 2);
							String absoluteX;
							String absoluteY;

							absoluteX = Float.toString(Float.parseFloat(initX) + Float.parseFloat(shapeTranslateX));
							absoluteY = Float.toString(Float.parseFloat(initY) + Float.parseFloat(shapeTranslateY));

							// 普通控件参数赋值(矩形)
							objectX = absoluteX;
							objectY = absoluteY;
							objectW = initW;
							objectH = initH;
							feGroupObjectImport();
							typeFlag = 0;
							flag = 0;
						}

						// 如果是连线
						else if (flag == 2 && objectType == 9 && "<path d".equals(strLine.trim().substring(0, 7))) {

							typeFlag = 1;
							String initX_Line;
							String initY_Line;
							String absoluteX_Line;
							String absoluteY_Line;
							String subStr;
							String strLineFormula;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								final StringBuilder pathStr = new StringBuilder(); // 路径记录
								strLineFormula = strLine.trim();
								strLineFormula = formulaLine(strLineFormula);

								indexFlag = strLineFormula.indexOf("d=\"M");
								initX_Line = strLineFormula.substring(strLineFormula.indexOf("d=\"M") + 4, strLineFormula.indexOf(" ", strLineFormula.indexOf("d=\"M")));
								initY_Line = strLineFormula.substring(strLineFormula.indexOf(" ", strLineFormula.indexOf("d=\"M")) + 1, strLineFormula.indexOf("L", strLineFormula.indexOf("d=\"M")) - 1);

								absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(shapeTranslateX));
								absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(shapeTranslateY));

								pathStr.append(absoluteX_Line + ",");
								pathStr.append(absoluteY_Line + ";");

								// 获取所有拐点坐标
								while (strLineFormula.substring(indexFlag).indexOf("L") > 0) {

									subStr = strLineFormula.substring(indexFlag);
									indexFlag = strLineFormula.indexOf("L", strLineFormula.indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									absoluteX_Line = Float.toString(Float.parseFloat(initX_Line) + Float.parseFloat(shapeTranslateX));
									absoluteY_Line = Float.toString(Float.parseFloat(initY_Line) + Float.parseFloat(shapeTranslateY));

									pathStr.append(absoluteX_Line + ",");
									pathStr.append(absoluteY_Line + ";");

								}
								objectLinePath = getChangedLine(pathStr.toString());
								final TransitionBean bean = new TransitionBean();
								bean.setWfname(formulaStr(objectName));
								bean.setPoint(objectLinePath);
								transitionList.add(bean);
							}
							pathNum++;
						}

						// 带边的矩形
						if (flag == 2 && objectType == 2 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);
								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								feGroupObjectImport();

								flag = 0;

							}
						}

						// 菱形
						else if (flag == 2 && objectType == 3 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();

							}

						}

						// 椭圆
						else if (flag == 2 && objectType == 5) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							String r = "0.0";
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));

									initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("A", subStr.indexOf("L") + 1) - 1);
									if ("0.0".equals(r)) {
										if (subStr.contains("A")) {
											r = subStr.substring(subStr.indexOf("A") + 1, subStr.indexOf(" ", subStr.indexOf("A")));
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX - Float.parseFloat(r));
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX + Float.parseFloat(r) + Float.parseFloat(r));
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();

							}

						}

						// 平行四边形
						else if (flag == 2 && objectType == 6 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();

							}

						} else if (flag == 2 && objectType == 7 && "<path d".equals(strLine.trim().substring(0, 7))) {
							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);
								if (strLine.contains("A")) {
									strLine = strLine.substring(0, strLine.indexOf("A")) + "class=\"st1\"/>";
								}

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);

								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);

								feGroupObjectImport();

							}

						}
						// 其他 全部转为矩形。
						else if (flag == 2 && objectType == -1) {
							String subStr;
							initMaxMinValue();
							int indexFlag = 0; // 断点标记
							String initX_Line;
							String initY_Line = "";
							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {
								indexFlag = strLine.trim().indexOf("d=\"M");
								strLine = getWholeLine(strLine);
								strLine = formulaLine(strLine);
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								if (strLine.trim().substring(0, 27).contains("L")) {
									initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);
								} else if (strLine.trim().substring(0, 27).contains("A")) {
									initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("A", strLine.trim().indexOf("d=\"M")) - 1);
								}
								if (!TBUtil.isEmpty(initX_Line)) {
									shapeMinX = Float.parseFloat(initX_Line);
								}
								if (!TBUtil.isEmpty(initY_Line)) {
									shapeMinY = Float.parseFloat(initY_Line);
								}
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}
								if (groupFlag > 1 && isGroup == 0) {
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
								} else if (isGroup == 2) {
									shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
								} else {
									shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								}
								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								if (shapeMinY < 0) {
									initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);
									shapeMinY = Float.parseFloat(initY_Line);
									if (groupFlag > 1 && isGroup == 0) {
										shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY) + Float.parseFloat(shapeTranslateY);
									} else if (isGroup == 2) {
										shapeMinY = shapeMinY + Float.parseFloat(groupTranslateY);
									} else {
										shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
									}
									objectY = Float.toString(shapeMinY);
								}
								if (shapeMinX < 0) {
									initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
									shapeMinX = Float.parseFloat(initX_Line);
									if (groupFlag > 1 && isGroup == 0) {
										shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX) + Float.parseFloat(shapeTranslateX);
									} else if (isGroup == 2) {
										shapeMinX = shapeMinX + Float.parseFloat(groupTranslateX);
									} else {
										shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
									}
									objectX = Float.toString(shapeMinX);
								}
								objectW = firstRectWidth == null ? "72.81" : firstRectWidth;
								objectH = firstRectHeight == null ? "45.51" : firstRectHeight;
								if (otherActivityPath) {
									feGroupObjectImport();
									otherActivityPath = false;
								}
							}
						}
						// 复合框处理
						else if (flag == 2 && objectType == 8 && isCoordinateFlag == 0 && "<path d".equals(strLine.trim().substring(0, 7))) {

							initMaxMinValue();

							String initX_Line;
							String initY_Line;
							String subStr;
							int indexFlag = 0; // 断点标记

							// 获取连线关键点坐标
							if (strLine.trim().contains("<path d=\"M")) {

								strLine = getWholeLine(strLine);

								indexFlag = strLine.trim().indexOf("d=\"M");
								initX_Line = strLine.trim().substring(strLine.trim().indexOf("d=\"M") + 4, strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")));
								initY_Line = strLine.trim().substring(strLine.trim().indexOf(" ", strLine.trim().indexOf("d=\"M")) + 1, strLine.trim().indexOf("L", strLine.trim().indexOf("d=\"M")) - 1);

								shapeMaxX = Float.parseFloat(initX_Line);
								shapeMinX = Float.parseFloat(initX_Line);
								shapeMaxY = Float.parseFloat(initY_Line);
								shapeMinY = Float.parseFloat(initY_Line);

								// 获取所有拐点坐标
								while (strLine.trim().substring(indexFlag).indexOf("L") > 0) {

									subStr = strLine.trim().substring(indexFlag);
									indexFlag = strLine.trim().indexOf("L", strLine.trim().indexOf(subStr)) + 1;
									initX_Line = subStr.substring(subStr.indexOf("L") + 1, subStr.indexOf(" ", subStr.indexOf("L")));
									// 如果还有其他拐点
									if (subStr.substring(subStr.indexOf("L") + 1).contains("L")) {
										initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("L", subStr.indexOf("L") + 1) - 1);
									} else {
										// path中是否存在以Z结尾的情况
										if (subStr.contains("Z")) {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("Z", subStr.indexOf("L") + 1) - 2);
										} else {
											initY_Line = subStr.substring(subStr.indexOf(" ", subStr.indexOf("L")) + 1, subStr.indexOf("class", subStr.indexOf("L") + 1) - 2);
										}
									}

									if (shapeMaxX < Float.parseFloat(initX_Line)) {
										shapeMaxX = Float.parseFloat(initX_Line);
									}
									if (shapeMinX > Float.parseFloat(initX_Line)) {
										shapeMinX = Float.parseFloat(initX_Line);
									}
									if (shapeMaxY < Float.parseFloat(initY_Line)) {
										shapeMaxY = Float.parseFloat(initY_Line);
									}
									if (shapeMinY > Float.parseFloat(initY_Line)) {
										shapeMinY = Float.parseFloat(initY_Line);
									}

								}

								// 绝对化处理
								shapeMaxX = shapeMaxX + Float.parseFloat(shapeTranslateX);
								shapeMaxY = shapeMaxY + Float.parseFloat(shapeTranslateY);
								shapeMinX = shapeMinX + Float.parseFloat(shapeTranslateX);
								shapeMinY = shapeMinY + Float.parseFloat(shapeTranslateY);
								objectX = Float.toString(shapeMinX);
								objectY = Float.toString(shapeMinY);
								objectW = Float.toString(shapeMaxX - shapeMinX);
								objectH = Float.toString(shapeMaxY - shapeMinY);
								feGroupObjectImport();
								isCoordinateFlag = 1;
							}
						}
					}
				}
			}

			br.close();
			if (insertProcessSql == null || insertProcessSql.equals("")) {
				throw new WLTAppException("该文件中可能没有职能带区，导致读取不到流程信息，请检查！");
			}
			setMove();
			final boolean flag = ImportVisioToProcess(insertProcessSql, activityList, insertGroup(), moveActivity(), insertTransition(), processDBId);
			isImportFlag = flag ? 1 : 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		isFirstGroup = 0;
		isGetProName = 0;
		process[0] = processDBId;
		process[1] = processName;
		process[2] = processName;
		if (isImportFlag == 1) {
			return process;
		}
		return process;
	}

	public String moveActivity() {
		return "update pub_wf_activity set y=y-(" + moveUp + "),x=x-(" + moveLeft + ") where processid=" + processDBId;
	}

	/**
	 * 组控件让如缓存
	 * 
	 * @param _groupType
	 */
	private void groupObjectIntoList(final String _groupType) {
		insertStrGroup = new StringBuilder();
		final String changeGroupX = Double.toString(getNewLength(Double.parseDouble(groupX)));
		final String changeGroupY = Double.toString(getNewLength(Double.parseDouble(groupY)));
		final String changeGroupW = Double.toString(getNewLength(Double.parseDouble(groupW)));
		final String changeGroupH = Double.toString(getNewLength(Double.parseDouble(groupH)));
		final GroupVO vo = new GroupVO();
		vo.setGrouptype(_groupType);
		vo.setCode(groupName);
		vo.setWfname(groupName);
		vo.setX(Float.parseFloat(changeGroupX));
		vo.setY(Float.parseFloat(changeGroupY));
		vo.setWidth(Float.parseFloat(changeGroupW));
		vo.setHeight(Float.parseFloat(changeGroupH));
		group.add(vo);
	}

	// 非组控件时的导入
	List activityList = new ArrayList();

	private void feGroupObjectImport() {

		insertStrFeGroup = new StringBuilder();

		try {
			activityCode++;
			if (firstRectWidth == null || firstRectHeight == null) {
				firstRectWidth = objectW;
				firstRectHeight = objectH;
			}
			double x = getNewLength(Double.parseDouble(objectX));
			double y = getNewLength(Double.parseDouble(objectY));
			double w = getNewLength(Double.parseDouble(objectW));
			double h = getNewLength(Double.parseDouble(objectH));
			if (objectName != null && w < 70) {
				x = x - 3;
				y = y - 3;
				w = w + 6;
				h = h + 6;
			}
			final String changedX = Double.toString(x);
			final String changedY = Double.toString(y);
			final String changedW = Double.toString(w);
			final String changedH = Double.toString(h);
			insertStrFeGroup.append("insert into ");
			insertStrFeGroup.append("pub_wf_activity(");
			insertStrFeGroup.append("id,");
			insertStrFeGroup.append("code,");
			insertStrFeGroup.append("processid,");
			insertStrFeGroup.append("wfname,");
			insertStrFeGroup.append("uiname,");
			insertStrFeGroup.append("viewtype,");
			insertStrFeGroup.append("x,");
			insertStrFeGroup.append("y,");
			insertStrFeGroup.append("width,");
			insertStrFeGroup.append("height,");
			insertStrFeGroup.append("activitytype");
			insertStrFeGroup.append(") values(");
			insertStrFeGroup.append("'" + commdmo.getSequenceNextValByDS(null, "S_pub_wf_activity") + "',");
			insertStrFeGroup.append("'WFA" + activityCode + "',");
			insertStrFeGroup.append("'" + processDBId + "',");
			insertStrFeGroup.append(formulaStr(objectName));
			insertStrFeGroup.append(activityCode + ",");
			if (objectType == 8 || objectType == -1) {
				insertStrFeGroup.append("'1',");
			} else {
				insertStrFeGroup.append("'" + objectType + "',");
			}
			insertStrFeGroup.append("'" + changedX + "',");
			insertStrFeGroup.append("'" + changedY + "',");
			insertStrFeGroup.append("'" + changedW + "',");
			insertStrFeGroup.append("'" + changedH + "',");
			insertStrFeGroup.append("'NORMAL'");
			insertStrFeGroup.append(")");
			activityList.add(insertStrFeGroup.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WLTAppException("导入数据出错,请查看控制台相关信息");
		}
	}

	private String insertProcessSQL(String fileID) {
		String userdef01 = "";//所属机构
		try {
			processDBId = commdmo.getSequenceNextValByDS(null, "S_pub_wf_process");
			userdef01 = commdmo.getStringValueByDS(null, "select blcorpid from cmp_cmpfile where id='" + fileID + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		final StringBuilder insertStr = new StringBuilder();
		insertStr.append("insert into ");
		insertStr.append("pub_wf_process(");
		insertStr.append("id,");
		insertStr.append("code,");
		insertStr.append("name,");
		insertStr.append("cmpfileid,");
		insertStr.append("userdef01,");//所属机构【李春娟/2012-03-14】
		insertStr.append("wftype");
		insertStr.append(") values(");
		insertStr.append("'" + processDBId + "',");
		if (processName == null || processName.equals("")) {
			processName = "没有读取到流程名称";
		}
		insertStr.append("'" + processName + "',");
		insertStr.append("'" + processName + "',");
		insertStr.append("'" + fileID + "',");
		insertStr.append("'" + userdef01 + "',");
		insertStr.append("'体系流程'");
		insertStr.append(")");
		return insertStr.toString();
	}

	// 连线语句完整化
	private String getWholeLine(final String _lineStr) {
		String wholeLine = _lineStr.trim();
		String tempStr;
		if (wholeLine.contains("<path d=\"M")) {
			while (!wholeLine.contains("/>")) {
				try {
					tempStr = br.readLine().trim();
					if (tempStr != null && tempStr.length() > 0)
						wholeLine = wholeLine.trim() + " " + tempStr;
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return wholeLine.trim();
	}

	// 连线标准化
	private String formulaLine(final String _str) {
		String strLine = _str;
		try {
			if (strLine.contains("A")) {
				while (strLine.contains("A")) {
					final String newStr = strLine.substring(strLine.indexOf("A"), strLine.length());
					strLine = strLine.substring(0, strLine.indexOf("A")) + newStr.substring(newStr.indexOf("L", newStr.indexOf("A") + 1));
				}
			}
		} catch (final Exception ex) {
			strLine = _str;
			strLine = strLine.substring(0, strLine.indexOf("A"));
			strLine = strLine + " L35.43 799.37 L70.87 820.63 L35.43 841.89 L0 820.63 Z\" class=\"st1\"/>";
		}
		return strLine;
	}

	// 获得按比例放缩后的长度
	private double getNewLength(final double _oldLength) {
		double newLength = _oldLength;
		if (_oldLength > 0.0 && proportion > 0.0) {
			newLength = _oldLength * proportion;
		}
		final DecimalFormat formatObj = new DecimalFormat();
		formatObj.applyPattern("0.000");
		newLength = Double.parseDouble(formatObj.format(newLength));
		return newLength;
	}

	// 连线放缩
	private String getChangedLine(final String _oldLine) {
		final StringBuilder newLine = new StringBuilder();
		double tempChangedLengthX = 0.0; // 变化后的横坐标
		double tempChangedLengthY = 0.0; // 变化后的纵坐标

		if (_oldLine.contains(";")) {
			final String[] pars = getSplitStrs(_oldLine, ";");
			if (pars != null && pars.length > 0) {
				for (int i = 0; i < pars.length; i++) {
					tempChangedLengthX = getNewLength(Double.parseDouble(pars[i].substring(0, pars[i].indexOf(","))));
					tempChangedLengthY = getNewLength(Double.parseDouble(pars[i].substring(pars[i].indexOf(",") + 1)));
					pars[i] = Double.toString(tempChangedLengthX) + "," + Double.toString(tempChangedLengthY);
					newLine.append(pars[i]).append(";");
				}
			}
		} else {
			newLine.append(_oldLine);
		}

		return newLine.toString();
	}

	// 分割字符串
	private String[] getSplitStrs(final String _par, final String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		final Vector v_return = new Vector();
		final StringTokenizer st = new StringTokenizer(_par, _separator);
		while (st.hasMoreTokens()) {
			v_return.add(st.nextToken());
		}
		return (String[]) v_return.toArray(new String[0]);
	}

	// 字符串空值判定
	public String formulaStr(final String _str) {
		String returnStr;
		if (_str != null) {
			returnStr = "'" + _str + "',";
		} else {
			returnStr = "null,";
		}
		return returnStr;
	}

	// 字符串数字化处理
	public int formulaStrToNum(final String _str) {
		int res = 0;
		if (_str != null && _str.length() > 0) {
			try {
				res = Integer.parseInt(_str);
			} catch (final NumberFormatException nfe) {
				res = 0;
			}
		} else {
			res = 0;
		}
		return res;
	}

	// 将控件路劲最大最小坐标初始化
	public void initMaxMinValue() {
		shapeMaxX = (float) -1.0;
		shapeMaxY = (float) -1.0;
		shapeMinX = (float) -1.0;
		shapeMinY = (float) -1.0;
	}

	/**
	 * 设置移动Visio和系统的移动距离。如果不设置这个地方，起始部门坐标偏右下方。
	 */
	public void setMove() {
		final String[] str = getFirstGroup();
		moveLeft = Float.parseFloat(str[0]) - beginX;
		moveUp = Float.parseFloat(str[1]) - beginY;
	}

	// 可以优化。一起提到排序中。
	public String[] getFirstGroup() {
		final String str[] = new String[] { "999999", "999999" };
		if (group.size() > 0) {
			for (int i = 0; i < group.size(); i++) {
				if (group.get(i).getGrouptype().equals(GroupTypeDept) && Float.parseFloat(str[0]) > group.get(i).getX()) {
					str[0] = group.get(i).getX() + "";
					str[1] = group.get(i).getY() + "";
				}
			}
		}
		return str;
	}

	/**
	 * 排序所有部门 //并且把所有部门位置递减1
	 */
	public void descGroup() {
		if (group.size() > 0) {
			float stationWidth = 0f;
			for (int i = 0; i < group.size(); i++) {
				for (int j = 0; j < group.size() - i - 1; j++) {
					if (group.get(j).getX() > group.get(j + 1).getX()) {
						final GroupVO vo = group.get(j);
						group.set(j, group.get(j + 1));
						group.set(j + 1, vo);
					}
				}
				if (group.get(i).getGrouptype().equals(GroupTypeDept)) {
					stationWidth += group.get(i).getWidth();
				}
			}
			int num = 0;
			float left = beginX;
			for (int i = 0; i < group.size(); i++) {
				final GroupVO vo = group.get(i);
				if (vo.getGrouptype().equals(GroupTypeDept)) {
					if (num != 0) {
						vo.setX(left - num);
					} else {
						vo.setX(beginX);
					}
					left += vo.getWidth();
					vo.setY(beginY);
					;
					group.set(i, vo);
					num++;
				} else {
					vo.setX(10);
					vo.setY(vo.getY() - moveUp);
					vo.setWidth(stationWidth + beginX);
					vo.setHeight(Float.parseFloat(PHASE_H));
					group.set(i, vo);
				}

			}
		}
	}

	/**
	 * 一下子插入所有组。减少调用和查询次数。需要先调格式。 执行的sql已经移动过整体位置了.
	 */
	public List insertGroup() throws Exception {
		descGroup();
		final List<String> sql = new ArrayList<String>();
		for (int i = 0; i < group.size(); i++) {
			final StringBuffer insertStrGroup = new StringBuffer();
			insertStrGroup.append("insert into ");
			insertStrGroup.append("pub_wf_group(");
			insertStrGroup.append("id,");
			insertStrGroup.append("processid,");
			insertStrGroup.append("grouptype,");
			insertStrGroup.append("code,");
			insertStrGroup.append("wfname,");
			insertStrGroup.append("x,");
			insertStrGroup.append("y,");
			insertStrGroup.append("width,");
			insertStrGroup.append("height,");
			insertStrGroup.append("fonttype,");
			insertStrGroup.append("fontsize,");
			insertStrGroup.append("foreground,");
			insertStrGroup.append("background");
			insertStrGroup.append(") values(");
			insertStrGroup.append("'" + commdmo.getSequenceNextValByDS(null, "S_pub_wf_group") + "',");
			insertStrGroup.append("'" + processDBId + "',");
			insertStrGroup.append("'" + group.get(i).getGrouptype() + "',");
			insertStrGroup.append("'" + group.get(i).getCode() + "',");
			insertStrGroup.append("'" + group.get(i).getWfname() + "',");
			insertStrGroup.append("'" + group.get(i).getX() + "',");
			insertStrGroup.append("'" + group.get(i).getY() + "',");
			insertStrGroup.append("'" + group.get(i).getWidth() + "',");
			insertStrGroup.append("'" + group.get(i).getHeight() + "',");
			insertStrGroup.append("'" + group.get(i).getFonttype() + "',");
			insertStrGroup.append("'" + group.get(i).getFontsize() + "',");
			insertStrGroup.append("'" + group.get(i).getForeground() + "',");
			insertStrGroup.append("'" + group.get(i).getBackground() + "'");
			insertStrGroup.append(")");
			sql.add(insertStrGroup.toString());
		}
		return sql;
	}

	/**
	 * 一下子插入所有线条。没有起始和终止，只有点的路径。
	 */
	public List insertTransition() throws Exception {
		if (transitionList != null && transitionList.size() > 0) {
			final List<String> sql = new ArrayList<String>();
			for (int i = 0; i < transitionList.size(); i++) {
				final StringBuilder insertStr = new StringBuilder();
				insertStr.append("insert into ");
				insertStr.append("pub_wf_transition(");
				insertStr.append("id,");
				insertStr.append("processid,");
				insertStr.append("code,");
				insertStr.append("wfname,");
				insertStr.append("uiname,");
				insertStr.append("dealtype,");
				insertStr.append("points");
				insertStr.append(") values(");
				insertStr.append("'" + commdmo.getSequenceNextValByDS(null, "S_pub_wf_transition") + "',");
				insertStr.append("'" + processDBId + "',");
				insertStr.append("'WFT',");
				insertStr.append(transitionList.get(i).getWfname());
				insertStr.append(transitionList.get(i).getWfname());
				insertStr.append("'SUBMIT',");
				insertStr.append("'" + getFormatPoint(transitionList.get(i).getPoint()) + "'");
				insertStr.append(")");
				sql.add(insertStr.toString());
			}
			return sql;
		}
		return null;

	}

	/**
	 * 修改点，如果所有的横坐标相同，说明是一条横向直线，只保留头尾。 纵向同理。
	 * 
	 * @param point
	 * @return
	 */
	public String getFormatPoint(final String point) {
		if (point == null || point.equals(""))
			return "";
		final String[] str = point.split(";");

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			final String xy[] = str[i].split(",");
			sb.append((Float.parseFloat(xy[0]) - moveLeft));
			sb.append("," + (Float.parseFloat(xy[1]) - moveUp));
			sb.append(";");
		}
		return sb.toString();
	}

	/*
	 * 执行所有SQL的地方
	 */
	private boolean ImportVisioToProcess(String insertProcessSql, List activityList, List insertGroup, String moveSql, List insertTransition, String processID) throws Exception {
		processDBId = processID;
		try {
			commdmo.executeBatchByDS(null, new String[] { insertProcessSql });
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WLTAppException("插入流程失败，详细请查看控制台！");
		}
		try {
			if (insertGroup != null) {
				commdmo.executeBatchByDS(null, activityList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WLTAppException("批量插入环节失败，详细请查看控制台！");
		}
		try {
			if (insertGroup != null) {
				commdmo.executeBatchByDS(null, insertGroup);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WLTAppException("批量插入部门、阶段失败，详细请查看控制台！");
		}
		commdmo.executeBatchByDS(null, new String[] { moveSql }); // 移动所有环节
		try {
			if (insertTransition != null) {
				commdmo.executeBatchByDS(null, insertTransition);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WLTAppException("批量插入连线失败，详细请查看控制台！");
		}

		updateDbDate();
		deleteErrorDatas();
		lastdo();
		return true;

	}

	private void lastdo() throws Exception {
		String sql1 = "update pub_wf_transition set fonttype='System',fontsize='12',foreground='0,0,0',background='70,119,191' where fonttype is null or fonttype=''";
		String sql2 = "update pub_wf_transition set linetype=2,issingle='true' where linetype is null";
		String sql3 = "update pub_wf_activity set fonttype='System',fontsize='12',foreground='0,0,0',background='232,238,247' where fonttype is null or fonttype=''";
		commdmo.executeBatchByDS(null, new String[] { sql1, sql2, sql3 });
	}

	// 将可能错误的数据去除
	private void deleteErrorDatas() throws Exception {
		String deleteSqlStr1 = "delete from pub_wf_transition where fromactivity is null or toactivity is null and processid = '" + processDBId + "'";
		try {
			commdmo.executeBatchByDS(null, new String[] { deleteSqlStr1 });
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("此次导入的流程图可能遗留了某些错误的数据,请注意检查该流程图是否能正确显示");
		}
	}

	/*
	 * 
	 */
	private void updateDbDate() throws Exception {

		String getActivityParas = "select * from pub_wf_activity where processid='" + processDBId + "'";
		HashVO[] activityParasHVS = commdmo.getHashVoArrayByDS(null, getActivityParas);
		String getTransitionParas = "select * from pub_wf_transition where processid='" + processDBId + "'";
		HashVO[] transitionParasHVS = commdmo.getHashVoArrayByDS(null, getTransitionParas);

		String startPoint; // 起点坐标字符串
		String endPoint; // 终点坐标字符串
		String startX; // 起点横坐标
		String startY; // 起点纵坐标
		String endX; // 终点横坐标
		String endY; // 终点纵坐标
		float highX; // 横坐标上限
		float highY; // 纵坐标上限
		float lowX; // 横坐标下限
		float lowY; // 纵坐标下限
		String fromParaId;
		String toParaId;
		List<String> sqlStrList = new ArrayList<String>();

		for (int i = 0; i < transitionParasHVS.length; i++) {
			fromParaId = null;
			toParaId = null;
			if (transitionParasHVS[i].getStringValue("points") != null && transitionParasHVS[i].getStringValue("points").length() > 0) {

				startPoint = transitionParasHVS[i].getStringValue("points").substring(0, transitionParasHVS[i].getStringValue("points").indexOf(";"));
				startX = startPoint.substring(0, startPoint.indexOf(","));
				startY = startPoint.substring(startPoint.indexOf(",") + 1);
				endPoint = transitionParasHVS[i].getStringValue("points").substring(transitionParasHVS[i].getStringValue("points").substring(0, transitionParasHVS[i].getStringValue("points").length() - 1).lastIndexOf(";") + 1, transitionParasHVS[i].getStringValue("points").length() - 1);
				endX = endPoint.substring(0, endPoint.indexOf(","));
				endY = endPoint.substring(endPoint.indexOf(",") + 1);

				for (int k = 1; k < lineAreaR; k++) {
					for (int j = 0; j < activityParasHVS.length; j++) {

						String x = activityParasHVS[j].getStringValue("x");
						String y = activityParasHVS[j].getStringValue("y");
						String width = activityParasHVS[j].getStringValue("width");
						String height = activityParasHVS[j].getStringValue("height");

						highX = (float) (Float.parseFloat(x) + Float.parseFloat(width) + k);
						highY = (float) (Float.parseFloat(y) + Float.parseFloat(height) + k);
						lowX = (float) (Float.parseFloat(x) - k);
						lowY = (float) (Float.parseFloat(y) - k);

						if (Float.parseFloat(startX) <= highX && Float.parseFloat(startY) <= highY && Float.parseFloat(startX) >= lowX && Float.parseFloat(startY) >= lowY) {
							if (fromParaId == null || "null".equals(fromParaId) || fromParaId.length() == 0) {
								if (toParaId == null || (toParaId != null && !toParaId.equals(activityParasHVS[j].getStringValue("id")))) {
									fromParaId = activityParasHVS[j].getStringValue("id");
								}
							}
						}

						if (Float.parseFloat(endX) <= highX && Float.parseFloat(endY) <= highY && Float.parseFloat(endX) >= lowX && Float.parseFloat(endY) >= lowY) {
							if (toParaId == null || "null".equals(toParaId) || toParaId.length() == 0) {
								if (fromParaId == null || (fromParaId != null && !fromParaId.equals(activityParasHVS[j].getStringValue("id")))) {
									toParaId = activityParasHVS[j].getStringValue("id");
								}
							}
						}

						if (fromParaId != null && toParaId != null && fromParaId.length() > 0 && toParaId.length() > 0 && !"null".equals(fromParaId) && !"null".equals(toParaId)) {
							break;
						}

					}

					if (fromParaId != null && toParaId != null && fromParaId.length() > 0 && toParaId.length() > 0 && !"null".equals(fromParaId) && !"null".equals(toParaId)) {
						break;
					}

				}

				String point = formatPoint(transitionParasHVS[i].getStringValue("points"));
				String updateStr = "update pub_wf_transition set fromactivity=" + (fromParaId == null ? null : "'" + fromParaId + "'") + ",toactivity=" + (toParaId == null ? null : "'" + toParaId + "'") + ",points='" + point + "' where id='" + transitionParasHVS[i].getStringValue("id") + "'";
				sqlStrList.add(updateStr);
			}
		}
		commdmo.executeBatchByDS(null, sqlStrList);

	}

	public String formatPoint(String points) {
		if (points != null && points.length() > 0) {
			String[] point = points.split(";");
			StringBuffer newPoint = new StringBuffer();
			int equalX = 1; // 判断X相等的点
			int equalY = 1;// 判断Y相等的点
			boolean xflag = false; // 两个点在水平
			boolean yflag = false; // 两个点在垂直
			float b_x = 0f;// 上一个点
			float b_y = 0f;
			for (int i = 0; i < point.length; i++) {
				String xy[] = point[i].split(",");
				float x = Float.parseFloat(xy[0]);
				float y = Float.parseFloat(xy[1]);
				if (i == 0) {
					b_x = x;
					b_y = y;
					continue;
				}
				if (x == b_x) {
					if (equalY > 1) {
						newPoint.append(b_x + ",");
						newPoint.append(b_y + ";");
						equalY = 1;
					}
					if (!xflag) {
						equalX = 1;
					}
					equalX++;
					xflag = true;
					yflag = false;
					b_x = x;
					b_y = y;
				} else if (y == b_y) {
					if (equalX > 1) {
						newPoint.append(b_x + ",");
						newPoint.append(b_y + ";");
						equalX = 1;
					}
					if (!yflag) {
						equalY = 1;
					}
					equalY++;
					xflag = false;
					yflag = true;
					b_x = x;
					b_y = y;
				}

			}
			return newPoint.toString();

		}
		return points;
	}

}

class GroupVO {
	private String wfname = null;
	private float x = 0f;
	private float y = 0f;
	private float width = 0f;
	private float height = 0f;
	private String grouptype = null;
	private String code = null;
	// 字体与颜色
	private String fonttype = "System"; // 字体类型
	private Integer fontsize = 12; // 字体大小
	private String foreground = "0,0,0"; // 前景颜色
	private String background = "0,0,0"; // 背景颜色

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getGrouptype() {
		return grouptype;
	}

	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFonttype() {
		return fonttype;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public Integer getFontsize() {
		return fontsize;
	}

	public void setFontsize(Integer fontsize) {
		this.fontsize = fontsize;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
}

class TransitionBean {
	private String wfname = null; // 流程图中显示的名称
	private String point = null;

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}
}
