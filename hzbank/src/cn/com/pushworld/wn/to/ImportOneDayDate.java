package cn.com.pushworld.wn.to;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;

public class ImportOneDayDate {
	private static Logger log = Logger.getLogger(ImportOneDayDate.class);
	private static Map<String, String> maxMap = new HashMap<String, String>();
	private static Set<String> addSet = new HashSet<String>();// 对于特殊的表，需要进行特殊处理
	private static String ctlLogDir = "/home/oracle/wnCtlLog/";// 日志文件路径
	private static String ctlDataDir = "/home/oracle/wnData/";// 数据文件路径
	private static String ctlDir = "/home/oracle/wnCtl1/";// ctl文件路径
	static {
		maxMap.put("S_EBNK_PB_FIXED_DEPOSIT_TRANFLOW", "S_EBNK_PB_FIXED_");
		maxMap.put("S_FSMS_SALE_CUST_SIGN_CARD_INFO", "S_FSMS_SALE_CUST_SIGN_");
		maxMap.put("S_OFCR_LN_ACCT_INT_BALANCE_DTLS",
				"S_OFCR_LN_ACCT_INT_BALANCE");
		maxMap.put("S_OFCR_LN_ACCT_PRICING_RATE_DETL",
				"S_OFCR_LN_ACCT_PRICING_");
		maxMap.put("S_OFCR_LN_TF_RCC_DRV_CRR_MOVEMENT",
				"S_OFCR_LN_TF_RCC_DRV_CRR");
		maxMap.put("S_OFCR_TF_CASH_PAYMENT_EXTERNAL", "S_OFCR_TF_CASH_PAYMENT");
		maxMap.put("S_OFCR_TF_INVENTORY_MAST_DETAIL",
				"S_OFCR_TF_INVENTORY_MAST_");
		maxMap.put("S_OFCR_TF_LARGE_DEP_CERT_TRANSFER_REG",
				"S_OFCR_TF_LARGE_DEP_CERT_");
		maxMap.put("S_OFCR_TF_LN_ENTRUST_BOD_PAYMENT",
				"S_OFCR_TF_LN_ENTRUST_BOD");
		maxMap.put("S_OFCR_TF_LN_RCC_ACCT_CRR_MOVEMENT",
				"S_OFCR_TF_LN_RCC_ACCT_CRR");
		maxMap.put("S_OFCR_XFACE_ADDL_DETAILS_TXNLOG",
				"S_OFCR_XFACE_ADDL_DETAILS_");
		addSet.add("S_LOAN_DK");// 需要特殊处理的表(存在一类表，全量导入，但是需要保留上一个月的数据，如贷款表(S_LOAN_DK))
		addSet.add("S_CMIS_ACC_LOAN");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			return;
		} else {
			System.out.println("传入的参数为:" + args[0]);
			String filePath = args[0];
			ImportOneDay(filePath);
		}
	}

	/**
	 * 导入某一天的数据到数据库中或者某一个数据文件到数据库中
	 * 
	 * @param filePath
	 *            :(传入参数的格式:20190101或者20190101/S_LOAN_DK_ALL_20190101_2820000.
	 *            del)
	 * @return
	 */
	public static String ImportOneDay(String filePath) {
		String result = "";
		try {
			// /home/oracle/wnCtl1/
			ctlDataDir = ctlDataDir + filePath;// 设置导入文件路径
			File dataFile = new File(ctlDataDir);
			if (!dataFile.exists()) {// 数据源不存在的情况下
				result = "指定的数据源【" + dataFile.getAbsolutePath()
						+ "】不存在,请重新输入数据源";
			} else {// 数据源存在的情况(说明:数据源存在的情况下，数据包中所有文件都需要导入，不做任何处理)
				Connection conn = getConn();// 获取到数据库连接
				// //ctl文件中内容进行设置
				String options = "OPTIONS(ROWS=1000,bindsize=45600000,READSIZE=202400)\n";
				String infile = "infile '";// 加载的文件名
				String intoTable = " into table ";// 需要处理的表
				String fields = "fields terminated by '|$|'\n";// 分割符
				String trNulls = "trailing   nullcols\n";// 允许为空
				String columns = "(\n";
				String timeType = "TIMESTAMP 'YYYY-MM-DD HH24:MI:SS'";
				String encoding = "CHARACTERSET 'ZHS16GBK'  \n";
				String encode = "UTF-8";// 设置编码为UTF-8
				String ldDate = "load data\n";
				// //判断当前路径属于文件还是目录
				if (dataFile.isFile()) {// 如果是文件，说明只需要导入某一天中某一张表的数据
					// //首先判断当前文件是否解压，如果解压没有解压，则直接对文件进行解压操作。
					uzipFile(ctlDataDir);
					// //对需要导入的表进行数据处理,删除操作
					deleteTable(ctlDataDir);
					// //对ctl中各项内容进行处理
					infile = infile + ctlDataDir + "'\n,";
					String fname = dataFile.getName();
					String tableName = "";
					String date = ctlDataDir.substring(
							ctlDataDir.lastIndexOf("/") - 8,
							ctlDataDir.lastIndexOf("/"));
					if (fname.equals("_add_2")) {
						tableName = fname.substring(0,
								fname.lastIndexOf("_add_2")).toUpperCase();
						intoTable = "append" + intoTable;
					} else {// 对于全量表的导入
						String addSetname = fname.substring(0,
								fname.lastIndexOf("_all_2")).toUpperCase();
						if (addSet.contains(addSetname)) {// 在set集合中的数据需要做特殊处理
							tableName = fname.substring(0,
									fname.indexOf("_all_2"));// 获取到需要处理的表名
							intoTable = "append" + intoTable;
						} else {
							tableName = fname.substring(0,
									fname.indexOf("_all_2")).toUpperCase();// 获取到需要处理的表名
							intoTable = "truncate" + intoTable;
						}
					}
					intoTable = intoTable + tableName + ",\n";
					// //对字段进行拼接
					String fPath = dataFile.getAbsolutePath();
					String fddlPath = fPath
							.substring(0, fPath.lastIndexOf("."));
					File ddlFile = new File(fddlPath);
					if (ddlFile.exists()) {// ddl文件存在的时候，ddl文件中的字段需要和数据库中的字段进行比对
						String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
								+ tableName + "' ORDER BY COLUMN_ID";
						PreparedStatement state = conn.prepareStatement(sql,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet query = state.executeQuery(sql);
						if (!query.next()) {// 判断当前数据库中是否存在这张表
							result = "数据库中不存在表【" + tableName + "】,请检查";
						} else {
							query.beforeFirst();
							FileInputStream fInput = new FileInputStream(
									ddlFile);
							InputStreamReader reader = new InputStreamReader(
									fInput, encode);
							BufferedReader bReader = new BufferedReader(reader);
							Map<String, String> map = new HashMap<String, String>();
							while (query.next()) {
								map.put(query.getString("COLUMN_NAME"),
										query.getString("DATA_TYPE"));
							}
							String lineTxt = "";
							while ((lineTxt = bReader.readLine()) != null) {
								String[] colArray = lineTxt.split("\\|");
								String colName = colArray[1];// 获取到字段的名称
								String colType = colArray[2];// 获取到字段的类型
								Integer colLenth = Integer
										.parseInt(colArray[3]);// 获取到字段的长度
								if (!map.containsKey(colName.toUpperCase())) {// 判断当前的字段是否存在
									columns = columns + " " + colName
											+ " filler" + ",\n";
								} else {
									String dateType = map.get(colName
											.toUpperCase());
									if (dateType.contains("(")) {// 判断当前字段是否存在括号
										dateType = dateType.substring(0,
												dateType.lastIndexOf("("));
									}
									if (!dateType.equalsIgnoreCase(colType)) {// 处理数据类型不一致问题(负责解决无效月份问题)
										colType = dateType;
									}
									if ("TIMESTAMP".equals(colType)) {// 判断当前字段是否是TIMESTAMP类型。需要特殊处理
										columns = columns + " " + colName + " "
												+ timeType + ",\n";
									} else if (("VARCHAR"
											.equalsIgnoreCase(colType) || "VARCHAR2"
											.equalsIgnoreCase(colType))
											&& colLenth >= 255) {// 判断当前的字段是否是varchar类型，如果是varchar类型，并且字段长度大于255。需要在字段后面指定类型和长度
										columns = columns + " " + colName
												+ " char(" + colLenth + ")"
												+ ",\n";
									} else {
										columns = columns + " " + colName
												+ ",\n";
									}
								}
							}
							columns = columns + " " + "LOAD_DATES \""
									+ getDateDesc(date) + "\",\n";
							columns = columns.substring(0,
									columns.lastIndexOf(","))
									+ "\n)";
							closeConn(conn, state, query);
						}
					} else {// 如果ddl文件不存在的情况下，直接以查询到的字段为主
						String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
								+ tableName + "' ORDER BY COLUMN_ID";
						PreparedStatement state = conn.prepareStatement(sql,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet query = state.executeQuery();
						query.last();
						int row = query.getRow();
						if (row > 0) {
							query.beforeFirst();
							while (query.next()) {
								String colName = query.getString("COLUMN_NAME");// 获取到字段的名称
								String colType = query.getString("DATA_TYPE");// 获取到字段的类型
								Integer colLenth = Integer.parseInt(query
										.getString("DATA_LENGTH"));// 获取到字段的长度
								if (colType.contains("(")) {// 判断当前字段是否存在括号
									colType = colType.substring(0,
											colType.lastIndexOf("("));
								}
								if ("TIMESTAMP".equals(colType)) {// 判断当前字段是否是TIMESTAMP类型。需要特殊处理
									columns = columns + " " + colName + " "
											+ timeType + ",\n";
								} else if ("VARCHAR2".equalsIgnoreCase(colType)) {// 判断当前的字段是否是varchar类型，如果是varchar类型，并且字段长度大于255。需要在字段后面指定类型和长度
									// && colLenth >= 255
									colType = colType.substring(0,
											colType.length() - 1);
									if (colLenth >= 255) {
										columns = columns + " " + colName
												+ " char(" + colLenth + ")"
												+ ",\n";
									} else {
										// if
										// (colName.equalsIgnoreCase("seqid")) {
										// // columns = columns + "  " + colName
										// + " \"" + seqname + ".NEXTVAL\",\n";
										// } else
										if (colName
												.equalsIgnoreCase("LOAD_DATES")) {
											columns = columns + " "
													+ "LOAD_DATES \""
													+ getDateDesc(date)
													+ "\",\n";
										} else {
											columns = columns + " " + colName
													+ ",\n";
										}
									}
								} else {// 处理其他类型的数据
									columns = columns + " " + colName + ",\n";
								}
							}
							columns = columns.substring(0,
									columns.lastIndexOf(","))
									+ ")";
							closeConn(conn, state, query);
						} else {
							result = "数据库中不存在表【" + tableName + "】,请检查";
						}
					}
					// //将生成的内容写入文件
					ctlDir = ctlDir + date + "/" + tableName + ".ctl";
					File cFile = new File(ctlDir);// 设置文件路径
					cFile.createNewFile();// 创建文件
					FileOutputStream fwirte = new FileOutputStream(cFile);
					fwirte.write(options.getBytes());
					fwirte.write(ldDate.getBytes());
					fwirte.write(encoding.getBytes());
					fwirte.write(infile.getBytes());
					fwirte.write(intoTable.getBytes());
					fwirte.write(fields.getBytes());
					fwirte.write(trNulls.getBytes());
					fwirte.write(columns.getBytes());
					closeFw(fwirte);
					cFile = null;
					// //导入数据
					ImportOneFile(ctlDir, ctlLogDir + date + "/" + tableName
							+ ".log");
				} else {// 如果是目录
					// //首先对目录下的所有文件进行解压操作
					uzipDirectory(ctlDataDir);
					// //将目录下对应操作的表的数据删除
					deleteTableAll(ctlDataDir);
					List<File> fileList = Arrays.asList(dataFile.listFiles());
					Collections.sort(fileList);
					String date = ctlDataDir.substring(ctlDataDir
							.lastIndexOf("/"));
					for (File file : fileList) {
						// //首先需要获取到文件的名字,并判断当前文件是否是del文件
						String fPath = file.getAbsolutePath();
						String fname = file.getName();// 文件名称
						String suffix = fname.substring(fname.lastIndexOf("."));// 文件后缀
						if (!"del".equals(suffix)) {// 非del文件直接跳过
							continue;
						}
						// //对ctl文件中infile intoTable columns进行处理
						// /对infile intoTable
						infile = "infile '" + fPath + "',\n";// 加载的文件名
						intoTable = " into table ";
						columns = "(\n";
						String tableName = "";
						if (fname.contains("_add_2")) {
							tableName = fname.substring(0,
									fname.lastIndexOf("_add_2"));
							intoTable = "append " + intoTable;
						} else {
							tableName = fname.substring(0,
									fname.lastIndexOf("_all_2")).toUpperCase();
							if (addSet.contains(tableName)) {
								intoTable = "append " + intoTable + tableName
										+ ",\n";
							} else {
								intoTable = "truncate " + intoTable + tableName
										+ ",\n";
							}

						}
						// /对columns进行处理
						String ddlPath = fPath.substring(0,
								fPath.lastIndexOf("_"))
								+ ".ddl";
						File ddlFile = new File(ddlPath);
						if (ddlFile.exists()) {// ddl文件存在的时候
							String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
									+ tableName + "' ORDER BY COLUMN_ID";// 我需要获取到字段的名称
																			// 类型
																			// 长度---->因为这三者都有可能存在不一致的情况
							PreparedStatement state = conn
									.prepareStatement(sql);
							ResultSet query = state.executeQuery();
							if (!query.next()) {// 判断当前数据库中是否存在这张表
								continue;
							}
							Map<String, String> map = new HashMap<String, String>();
							map.put(query.getString("COLUMN_NAME"),
									query.getString("DATA_TYPE"));
							while (query.next()) {
								map.put(query.getString("COLUMN_NAME"),
										query.getString("DATA_TYPE"));
							}
							FileInputStream fInput = new FileInputStream(
									ddlFile);
							InputStreamReader reader = new InputStreamReader(
									fInput, encode);
							BufferedReader bReader = new BufferedReader(reader);
							String lineTxt = "";
							while ((lineTxt = bReader.readLine()) != null) {
								String[] colArray = lineTxt.split("\\|");
								String colName = colArray[1];// 获取到字段的名称
								String colType = colArray[2];// 获取到字段的类型
								Integer colLenth = Integer
										.parseInt(colArray[3]);// 获取到字段的长度
								if (!map.containsKey(colName.toUpperCase())) {// 判断当前的字段是否存在
									columns = columns + " " + colName
											+ " filler" + ",\n";
								} else {// 当前字段存在的时候，还需要当前字段类型和数据库中字段类型是否一致
									String dateType = map.get(colName
											.toUpperCase());
									if (dateType.contains("(")) {// 判断当前字段是否存在括号
										dateType = dateType.substring(0,
												dateType.lastIndexOf("("));
									}
									if (!dateType.equalsIgnoreCase(colType)) {// 处理数据类型不一致问题(负责解决无效月份问题)
										colType = dateType;
									}
									if ("TIMESTAMP".equals(colType)) {// 判断当前字段是否是TIMESTAMP类型。需要特殊处理
										columns = columns + " " + colName + " "
												+ timeType + ",\n";
									} else if (("VARCHAR"
											.equalsIgnoreCase(colType) || "VARCHAR2"
											.equalsIgnoreCase(colType))
											&& colLenth >= 255) {// 判断当前的字段是否是varchar类型，如果是varchar类型，并且字段长度大于255。需要在字段后面指定类型和长度
										columns = columns + " " + colName
												+ " char(" + colLenth + ")"
												+ ",\n";
									} else {
										columns = columns + " " + colName
												+ ",\n";
									}
								}
							}
							columns = columns + " " + "LOAD_DATES \""
									+ getDateDesc(date) + "\"\n)";
							closeConn(null, state, query);
						} else {// ddl文件不存在的时候
							String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
									+ tableName + "' ORDER BY COLUMN_ID";
							PreparedStatement state = conn.prepareStatement(
									sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							ResultSet query = state.executeQuery();
							query.last();
							int row = query.getRow();
							if (row > 0) {
								query.beforeFirst();
								if (query.next()) {
									query.beforeFirst();
									while (query.next()) {
										String colName = query
												.getString("COLUMN_NAME");// 获取到字段的名称
										String colType = query
												.getString("DATA_TYPE");// 获取到字段的类型
										Integer colLenth = Integer
												.parseInt(query
														.getString("DATA_LENGTH"));// 获取到字段的长度
										if (colType.contains("(")) {// 判断当前字段是否存在括号
											colType = colType.substring(0,
													colType.lastIndexOf("("));
										}
										if ("TIMESTAMP".equals(colType)) {// 判断当前字段是否是TIMESTAMP类型。需要特殊处理
											columns = columns + " " + colName
													+ " " + timeType + ",\n";
										} else if ("VARCHAR2"
												.equalsIgnoreCase(colType)) {// 判断当前的字段是否是varchar类型，如果是varchar类型，并且字段长度大于255。需要在字段后面指定类型和长度
											// && colLenth >= 255
											colType = colType.substring(0,
													colType.length() - 1);
											if (colLenth >= 255) {
												columns = columns + " "
														+ colName + " char("
														+ colLenth + ")"
														+ ",\n";
											} else {
												// if
												// (colName.equalsIgnoreCase("seqid"))
												// {
												// // columns = columns + "  " +
												// colName + " \"" + seqname +
												// ".NEXTVAL\",\n";
												// } else
												if (colName
														.equalsIgnoreCase("LOAD_DATES")) {
													columns = columns + " "
															+ "LOAD_DATES \""
															+ getDateDesc(date)
															+ "\",\n";
												} else {
													columns = columns + " "
															+ colName + ",\n";
												}
											}
										} else {// 处理其他类型的数据
											columns = columns + " " + colName
													+ ",\n";
										}
									}
									columns = columns.substring(0,
											columns.lastIndexOf(","))
											+ "\n)";
									closeConn(null, state, query);
								}
							}
						}
						String ctlRoute = ctlDir + "/" + tableName + ".ctl";
						String logRoute = ctlLogDir + date + "/" + tableName
								+ ".log";
						File cFile = new File(ctlRoute);// 设置文件路径
						cFile.createNewFile();// 创建文件
						log.debug("-----已经生成ctl文件【" + cFile.getAbsolutePath()
								+ "】,开始向文件中写入数据-------");
						FileOutputStream fwirte = new FileOutputStream(cFile);
						fwirte.write(options.getBytes());
						fwirte.write(ldDate.getBytes());
						fwirte.write(encoding.getBytes());
						fwirte.write(infile.getBytes());
						fwirte.write(intoTable.getBytes());
						fwirte.write(fields.getBytes());
						fwirte.write(trNulls.getBytes());
						fwirte.write(columns.getBytes());
						closeFw(fwirte);
						cFile = null;
						ImportOneFile(ctlRoute, logRoute);// 每创建完成一个ctl文件，就导入一次ctl文件
					}
				}
			}
			result = "数据导入成功";
		} catch (Exception e) {
			result = "数据导入失败";
			e.printStackTrace();
		}
		return result;
	}

	public static Connection getConn() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");// 加载数据库驱动
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.1.25:1521:orcl", "wnbanktest",
					"wnbanktest");// 连接数据库 ///改动
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param conn
	 *            :数据库连接
	 */
	public void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			} else {
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}
	}

	public static void closeConn(Connection conn, Statement state,
			ResultSet result) {
		try {
			if (result != null) {
				result.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			result = null;
		}
		try {
			if (state != null) {
				state.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			state = null;
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}
	}

	/**
	 * 根据输入的文件名称获取到需要操作的得了文件
	 * 
	 * @param fileName
	 * @return
	 */
	public String getTableName(String fileName) {
		String result = "";
		try {
			if (fileName.lastIndexOf("_add_2") != -1) {
				result = fileName.substring(0, fileName.indexOf("_add_2"))
						.toUpperCase();
			} else if (fileName.lastIndexOf("_all_2") != -1) {
				result = fileName.substring(0, fileName.indexOf("_all_2"))
						.toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取当前日期前一天
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateDesc(String date) {
		String lastDate = "";
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date d = format.parse(date);
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - 1);
			lastDate = format.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastDate;
	}

	public static void closeFw(FileOutputStream fw) {
		try {
			if (fw != null) {
				fw.close();
			} else {
				fw = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fw = null;
		}
	}

	/**
	 * 导入单个文件
	 * 
	 * @param filepath
	 *            :/home/oracle/wnCtl/20190101/S_LOAN_dk.ctl
	 * @param logfilePath
	 *            :导入生成log文件
	 * @return
	 */
	public static String ImportOneFile(String filepath, String logfilePath) {
		String result = "";
		try {
			String cmdStr = "sqlldr wnbank/wnbank@192.168.1.25:1521/orcl control="
					+ filepath + " log=" + logfilePath;
			// //判断当前日志是否存在并创建
			File logFile = new File(logfilePath);
			if (logFile.exists()) {
				logFile.delete();
			}
			logFile.createNewFile();
			Process process = Runtime.getRuntime().exec(cmdStr);
			InputStream ins = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ins));
			String line = null;
			int exitValue = process.waitFor();
			while ((line = reader.readLine()) != null) {
				String msg = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				log.debug("-----" + msg + "-----");
			}
			if (exitValue != 0) {
				log.error("-----返回值为:" + exitValue + "\n数据导入失败，失败的文件是:"
						+ filepath + "-----");
			} else {
				log.debug("-----返回值为:" + exitValue + "\n数据导入成功，操作的文件是:"
						+ filepath + "-----");
			}
			process.getOutputStream().close();
			result = "当前文件【" + filepath + "】导入成功，生成的日志文件为【" + filepath + "】";
		} catch (Exception e) {
			result = "当前文件【" + filepath + "】导入失败，生成的日志文件为【" + filepath
					+ "】，请检查";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param filePath
	 *            :需要解压的文件路径(/home/oracle/wnData/20181202/
	 *            a_agr_dep_acct_ent_fx_add_20181202_2820000.del)
	 */
	public static void uzipFile(String filePath) {
		// 首先判断输入的路径是文件还是文件夹
		System.out.println("判断的文件:" + filePath);
		File fileRoute = new File(filePath);
		try {
			if (!fileRoute.exists()) {// 判断当前文件是否存在，如果文件不存在，则表示当前文件尚未解压
				String gzfilePath = filePath + ".gz";
				System.out.println("需要解压的文件路径:" + gzfilePath);
				// 建立gzip的压缩文件的输入流
				FileInputStream fin = new FileInputStream(gzfilePath);
				// 建立gzip的解压文件工作流
				GZIPInputStream gzin = new GZIPInputStream(fin);
				// 建立解压文件的输出流
				FileOutputStream fout = new FileOutputStream(filePath);
				System.out.println("输出文件路径:" + filePath);
				int num = 0;
				byte[] buff = new byte[1024];
				while ((num = gzin.read(buff, 0, buff.length)) != -1) {
					fout.write(buff, 0, num);
				}
				gzin.close();
				fout.close();
				fin.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对当前那目录下的所有文件进行解压
	 * 
	 * @param dirPath
	 *            :操作的文件目录(参数格式:/home/oracle/wnData/20190101)
	 */
	public static void uzipDirectory(String dirPath) {
		File dirRoute = new File(dirPath);
		try {
			if (!dirRoute.exists()) {
				return;
			} else {// 文件路径存在的时候
				List<File> fileList = Arrays.asList(dirRoute.listFiles());
				Collections.sort(fileList);
				for (File file : fileList) {
					String fname = file.getName();
					String suffix = fname.substring(fname.lastIndexOf("."));
					if (!suffix.equals(".gz")) {
						continue;
					} else {
						String filePath = file.getAbsolutePath();
						uzipFile(filePath.substring(0,
								filePath.lastIndexOf(".")));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据del文件删除单张表
	 * 
	 * @param filePath
	 *            :操作的文件路径(文件格式:/home/oracle/wnData/20181202/
	 *            a_agr_dep_acct_ent_fx_add_20181202_2820000.del)
	 */
	public static void deleteTable(String filePath) {
		File file = new File(filePath);
		try {
			System.out.println("当前操作的文件路径:" + filePath);
			String fname = file.getName();
			String suffix = fname.substring(fname.lastIndexOf("."));
			String date = filePath.substring(ctlDataDir.lastIndexOf("/") - 8,
					ctlDataDir.lastIndexOf("/"));// 获取到当前导入日期
			if (!"del".equals(suffix)) {// 不做任何处理
				return;
			} else {
				if (file.exists()) {// 表文件存在的情况
					// 判断是增量表还是全量表
					if (fname.contains("_add_2")) {// 对于增量表(删除判断这一天是否)
						// 获取到表名
						String tableName = fname.substring(0,
								fname.lastIndexOf("_add_2"));
						Connection conn = getConn();
						String selectSql = "select count(*) as num  from "
								+ tableName + "  where load_dates='"
								+ getDateDesc(date) + "'";
						Statement state = conn.prepareStatement(selectSql,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet num = state.executeQuery(selectSql);
						num.beforeFirst();
						int rownum = 0;
						while (num.next()) {
							rownum = Integer.parseInt(num.getString("num"));
						}
						if (rownum > 0) {
							String deleteSql = "delete from " + tableName
									+ " where load_dates='" + getDateDesc(date)
									+ "'";
							state.execute(deleteSql);
						}
						closeConn(conn, state, num);
					} else if (fname.contains("_all_2")) {
						String tableName = fname.substring(0,
								fname.lastIndexOf("_all_2"));
						if (addSet.contains(tableName)) {
							Connection conn = getConn();
							String selectSql = "select count(*) as num  from "
									+ tableName + "  where load_dates like '"
									+ getDateDesc(date).substring(0, 6) + "'";
							Statement state = conn.prepareStatement(selectSql,
									ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							ResultSet result = state.executeQuery(selectSql);
							int rownum = 0;
							result.beforeFirst();
							while (result.next()) {
								rownum = Integer.parseInt(result
										.getString("num"));
							}
							if (rownum > 0) {
								String deleteSql = "delete from " + tableName
										+ " where load_dates like '"
										+ getDateDesc(date).substring(0, 6)
										+ "'";
								state.execute(deleteSql);
							}
							closeConn(conn, state, result);
						} else {// 其他全量表
							Connection conn = getConn();
							String deleteSql = "delete from " + tableName;
							Statement state = conn.createStatement();
							closeConn(conn, state, null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有需要导入的数据
	 * 
	 * @param fileRoute
	 *            :导入文件的目录(/home/oracle/wnData/20190101)
	 */
	public static void deleteTableAll(String fileRoute) {
		try {
			File file = new File(fileRoute);
			if (!file.exists()) {
				return;
			} else {
				List<File> files = Arrays.asList(file.listFiles());
				for (File f : files) {
					String fname = f.getName();
					String suffix = fname.substring(fname.lastIndexOf("."));
					if (!"del".equals(suffix)) {
						continue;
					} else {
						deleteTable(f.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}