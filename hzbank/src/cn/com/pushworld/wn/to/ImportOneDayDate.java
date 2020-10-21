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
	private static Set<String> addSet = new HashSet<String>();// ��������ı���Ҫ�������⴦��
	private static String ctlLogDir = "/home/oracle/wnCtlLog/";// ��־�ļ�·��
	private static String ctlDataDir = "/home/oracle/wnData/";// �����ļ�·��
	private static String ctlDir = "/home/oracle/wnCtl1/";// ctl�ļ�·��
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
		addSet.add("S_LOAN_DK");// ��Ҫ���⴦��ı�(����һ���ȫ�����룬������Ҫ������һ���µ����ݣ�������(S_LOAN_DK))
		addSet.add("S_CMIS_ACC_LOAN");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			return;
		} else {
			System.out.println("����Ĳ���Ϊ:" + args[0]);
			String filePath = args[0];
			ImportOneDay(filePath);
		}
	}

	/**
	 * ����ĳһ������ݵ����ݿ��л���ĳһ�������ļ������ݿ���
	 * 
	 * @param filePath
	 *            :(��������ĸ�ʽ:20190101����20190101/S_LOAN_DK_ALL_20190101_2820000.
	 *            del)
	 * @return
	 */
	public static String ImportOneDay(String filePath) {
		String result = "";
		try {
			// /home/oracle/wnCtl1/
			ctlDataDir = ctlDataDir + filePath;// ���õ����ļ�·��
			File dataFile = new File(ctlDataDir);
			if (!dataFile.exists()) {// ����Դ�����ڵ������
				result = "ָ��������Դ��" + dataFile.getAbsolutePath()
						+ "��������,��������������Դ";
			} else {// ����Դ���ڵ����(˵��:����Դ���ڵ�����£����ݰ��������ļ�����Ҫ���룬�����κδ���)
				Connection conn = getConn();// ��ȡ�����ݿ�����
				// //ctl�ļ������ݽ�������
				String options = "OPTIONS(ROWS=1000,bindsize=45600000,READSIZE=202400)\n";
				String infile = "infile '";// ���ص��ļ���
				String intoTable = " into table ";// ��Ҫ����ı�
				String fields = "fields terminated by '|$|'\n";// �ָ��
				String trNulls = "trailing   nullcols\n";// ����Ϊ��
				String columns = "(\n";
				String timeType = "TIMESTAMP 'YYYY-MM-DD HH24:MI:SS'";
				String encoding = "CHARACTERSET 'ZHS16GBK'  \n";
				String encode = "UTF-8";// ���ñ���ΪUTF-8
				String ldDate = "load data\n";
				// //�жϵ�ǰ·�������ļ�����Ŀ¼
				if (dataFile.isFile()) {// ������ļ���˵��ֻ��Ҫ����ĳһ����ĳһ�ű������
					// //�����жϵ�ǰ�ļ��Ƿ��ѹ�������ѹû�н�ѹ����ֱ�Ӷ��ļ����н�ѹ������
					uzipFile(ctlDataDir);
					// //����Ҫ����ı�������ݴ���,ɾ������
					deleteTable(ctlDataDir);
					// //��ctl�и������ݽ��д���
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
					} else {// ����ȫ����ĵ���
						String addSetname = fname.substring(0,
								fname.lastIndexOf("_all_2")).toUpperCase();
						if (addSet.contains(addSetname)) {// ��set�����е�������Ҫ�����⴦��
							tableName = fname.substring(0,
									fname.indexOf("_all_2"));// ��ȡ����Ҫ����ı���
							intoTable = "append" + intoTable;
						} else {
							tableName = fname.substring(0,
									fname.indexOf("_all_2")).toUpperCase();// ��ȡ����Ҫ����ı���
							intoTable = "truncate" + intoTable;
						}
					}
					intoTable = intoTable + tableName + ",\n";
					// //���ֶν���ƴ��
					String fPath = dataFile.getAbsolutePath();
					String fddlPath = fPath
							.substring(0, fPath.lastIndexOf("."));
					File ddlFile = new File(fddlPath);
					if (ddlFile.exists()) {// ddl�ļ����ڵ�ʱ��ddl�ļ��е��ֶ���Ҫ�����ݿ��е��ֶν��бȶ�
						String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
								+ tableName + "' ORDER BY COLUMN_ID";
						PreparedStatement state = conn.prepareStatement(sql,
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet query = state.executeQuery(sql);
						if (!query.next()) {// �жϵ�ǰ���ݿ����Ƿ�������ű�
							result = "���ݿ��в����ڱ�" + tableName + "��,����";
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
								String colName = colArray[1];// ��ȡ���ֶε�����
								String colType = colArray[2];// ��ȡ���ֶε�����
								Integer colLenth = Integer
										.parseInt(colArray[3]);// ��ȡ���ֶεĳ���
								if (!map.containsKey(colName.toUpperCase())) {// �жϵ�ǰ���ֶ��Ƿ����
									columns = columns + " " + colName
											+ " filler" + ",\n";
								} else {
									String dateType = map.get(colName
											.toUpperCase());
									if (dateType.contains("(")) {// �жϵ�ǰ�ֶ��Ƿ��������
										dateType = dateType.substring(0,
												dateType.lastIndexOf("("));
									}
									if (!dateType.equalsIgnoreCase(colType)) {// �����������Ͳ�һ������(��������Ч�·�����)
										colType = dateType;
									}
									if ("TIMESTAMP".equals(colType)) {// �жϵ�ǰ�ֶ��Ƿ���TIMESTAMP���͡���Ҫ���⴦��
										columns = columns + " " + colName + " "
												+ timeType + ",\n";
									} else if (("VARCHAR"
											.equalsIgnoreCase(colType) || "VARCHAR2"
											.equalsIgnoreCase(colType))
											&& colLenth >= 255) {// �жϵ�ǰ���ֶ��Ƿ���varchar���ͣ������varchar���ͣ������ֶγ��ȴ���255����Ҫ���ֶκ���ָ�����ͺͳ���
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
					} else {// ���ddl�ļ������ڵ�����£�ֱ���Բ�ѯ�����ֶ�Ϊ��
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
								String colName = query.getString("COLUMN_NAME");// ��ȡ���ֶε�����
								String colType = query.getString("DATA_TYPE");// ��ȡ���ֶε�����
								Integer colLenth = Integer.parseInt(query
										.getString("DATA_LENGTH"));// ��ȡ���ֶεĳ���
								if (colType.contains("(")) {// �жϵ�ǰ�ֶ��Ƿ��������
									colType = colType.substring(0,
											colType.lastIndexOf("("));
								}
								if ("TIMESTAMP".equals(colType)) {// �жϵ�ǰ�ֶ��Ƿ���TIMESTAMP���͡���Ҫ���⴦��
									columns = columns + " " + colName + " "
											+ timeType + ",\n";
								} else if ("VARCHAR2".equalsIgnoreCase(colType)) {// �жϵ�ǰ���ֶ��Ƿ���varchar���ͣ������varchar���ͣ������ֶγ��ȴ���255����Ҫ���ֶκ���ָ�����ͺͳ���
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
								} else {// �����������͵�����
									columns = columns + " " + colName + ",\n";
								}
							}
							columns = columns.substring(0,
									columns.lastIndexOf(","))
									+ ")";
							closeConn(conn, state, query);
						} else {
							result = "���ݿ��в����ڱ�" + tableName + "��,����";
						}
					}
					// //�����ɵ�����д���ļ�
					ctlDir = ctlDir + date + "/" + tableName + ".ctl";
					File cFile = new File(ctlDir);// �����ļ�·��
					cFile.createNewFile();// �����ļ�
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
					// //��������
					ImportOneFile(ctlDir, ctlLogDir + date + "/" + tableName
							+ ".log");
				} else {// �����Ŀ¼
					// //���ȶ�Ŀ¼�µ������ļ����н�ѹ����
					uzipDirectory(ctlDataDir);
					// //��Ŀ¼�¶�Ӧ�����ı������ɾ��
					deleteTableAll(ctlDataDir);
					List<File> fileList = Arrays.asList(dataFile.listFiles());
					Collections.sort(fileList);
					String date = ctlDataDir.substring(ctlDataDir
							.lastIndexOf("/"));
					for (File file : fileList) {
						// //������Ҫ��ȡ���ļ�������,���жϵ�ǰ�ļ��Ƿ���del�ļ�
						String fPath = file.getAbsolutePath();
						String fname = file.getName();// �ļ�����
						String suffix = fname.substring(fname.lastIndexOf("."));// �ļ���׺
						if (!"del".equals(suffix)) {// ��del�ļ�ֱ������
							continue;
						}
						// //��ctl�ļ���infile intoTable columns���д���
						// /��infile intoTable
						infile = "infile '" + fPath + "',\n";// ���ص��ļ���
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
						// /��columns���д���
						String ddlPath = fPath.substring(0,
								fPath.lastIndexOf("_"))
								+ ".ddl";
						File ddlFile = new File(ddlPath);
						if (ddlFile.exists()) {// ddl�ļ����ڵ�ʱ��
							String sql = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from user_tab_columns  WHERE TABLE_NAME='"
									+ tableName + "' ORDER BY COLUMN_ID";// ����Ҫ��ȡ���ֶε�����
																			// ����
																			// ����---->��Ϊ�����߶��п��ܴ��ڲ�һ�µ����
							PreparedStatement state = conn
									.prepareStatement(sql);
							ResultSet query = state.executeQuery();
							if (!query.next()) {// �жϵ�ǰ���ݿ����Ƿ�������ű�
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
								String colName = colArray[1];// ��ȡ���ֶε�����
								String colType = colArray[2];// ��ȡ���ֶε�����
								Integer colLenth = Integer
										.parseInt(colArray[3]);// ��ȡ���ֶεĳ���
								if (!map.containsKey(colName.toUpperCase())) {// �жϵ�ǰ���ֶ��Ƿ����
									columns = columns + " " + colName
											+ " filler" + ",\n";
								} else {// ��ǰ�ֶδ��ڵ�ʱ�򣬻���Ҫ��ǰ�ֶ����ͺ����ݿ����ֶ������Ƿ�һ��
									String dateType = map.get(colName
											.toUpperCase());
									if (dateType.contains("(")) {// �жϵ�ǰ�ֶ��Ƿ��������
										dateType = dateType.substring(0,
												dateType.lastIndexOf("("));
									}
									if (!dateType.equalsIgnoreCase(colType)) {// �����������Ͳ�һ������(��������Ч�·�����)
										colType = dateType;
									}
									if ("TIMESTAMP".equals(colType)) {// �жϵ�ǰ�ֶ��Ƿ���TIMESTAMP���͡���Ҫ���⴦��
										columns = columns + " " + colName + " "
												+ timeType + ",\n";
									} else if (("VARCHAR"
											.equalsIgnoreCase(colType) || "VARCHAR2"
											.equalsIgnoreCase(colType))
											&& colLenth >= 255) {// �жϵ�ǰ���ֶ��Ƿ���varchar���ͣ������varchar���ͣ������ֶγ��ȴ���255����Ҫ���ֶκ���ָ�����ͺͳ���
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
						} else {// ddl�ļ������ڵ�ʱ��
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
												.getString("COLUMN_NAME");// ��ȡ���ֶε�����
										String colType = query
												.getString("DATA_TYPE");// ��ȡ���ֶε�����
										Integer colLenth = Integer
												.parseInt(query
														.getString("DATA_LENGTH"));// ��ȡ���ֶεĳ���
										if (colType.contains("(")) {// �жϵ�ǰ�ֶ��Ƿ��������
											colType = colType.substring(0,
													colType.lastIndexOf("("));
										}
										if ("TIMESTAMP".equals(colType)) {// �жϵ�ǰ�ֶ��Ƿ���TIMESTAMP���͡���Ҫ���⴦��
											columns = columns + " " + colName
													+ " " + timeType + ",\n";
										} else if ("VARCHAR2"
												.equalsIgnoreCase(colType)) {// �жϵ�ǰ���ֶ��Ƿ���varchar���ͣ������varchar���ͣ������ֶγ��ȴ���255����Ҫ���ֶκ���ָ�����ͺͳ���
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
										} else {// �����������͵�����
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
						File cFile = new File(ctlRoute);// �����ļ�·��
						cFile.createNewFile();// �����ļ�
						log.debug("-----�Ѿ�����ctl�ļ���" + cFile.getAbsolutePath()
								+ "��,��ʼ���ļ���д������-------");
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
						ImportOneFile(ctlRoute, logRoute);// ÿ�������һ��ctl�ļ����͵���һ��ctl�ļ�
					}
				}
			}
			result = "���ݵ���ɹ�";
		} catch (Exception e) {
			result = "���ݵ���ʧ��";
			e.printStackTrace();
		}
		return result;
	}

	public static Connection getConn() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");// �������ݿ�����
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.1.25:1521:orcl", "wnbanktest",
					"wnbanktest");// �������ݿ� ///�Ķ�
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * �ر����ݿ�����
	 * 
	 * @param conn
	 *            :���ݿ�����
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
	 * ����������ļ����ƻ�ȡ����Ҫ�����ĵ����ļ�
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
	 * ��ȡ��ǰ����ǰһ��
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
	 * ���뵥���ļ�
	 * 
	 * @param filepath
	 *            :/home/oracle/wnCtl/20190101/S_LOAN_dk.ctl
	 * @param logfilePath
	 *            :��������log�ļ�
	 * @return
	 */
	public static String ImportOneFile(String filepath, String logfilePath) {
		String result = "";
		try {
			String cmdStr = "sqlldr wnbank/wnbank@192.168.1.25:1521/orcl control="
					+ filepath + " log=" + logfilePath;
			// //�жϵ�ǰ��־�Ƿ���ڲ�����
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
				log.error("-----����ֵΪ:" + exitValue + "\n���ݵ���ʧ�ܣ�ʧ�ܵ��ļ���:"
						+ filepath + "-----");
			} else {
				log.debug("-----����ֵΪ:" + exitValue + "\n���ݵ���ɹ����������ļ���:"
						+ filepath + "-----");
			}
			process.getOutputStream().close();
			result = "��ǰ�ļ���" + filepath + "������ɹ������ɵ���־�ļ�Ϊ��" + filepath + "��";
		} catch (Exception e) {
			result = "��ǰ�ļ���" + filepath + "������ʧ�ܣ����ɵ���־�ļ�Ϊ��" + filepath
					+ "��������";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param filePath
	 *            :��Ҫ��ѹ���ļ�·��(/home/oracle/wnData/20181202/
	 *            a_agr_dep_acct_ent_fx_add_20181202_2820000.del)
	 */
	public static void uzipFile(String filePath) {
		// �����ж������·�����ļ������ļ���
		System.out.println("�жϵ��ļ�:" + filePath);
		File fileRoute = new File(filePath);
		try {
			if (!fileRoute.exists()) {// �жϵ�ǰ�ļ��Ƿ���ڣ�����ļ������ڣ����ʾ��ǰ�ļ���δ��ѹ
				String gzfilePath = filePath + ".gz";
				System.out.println("��Ҫ��ѹ���ļ�·��:" + gzfilePath);
				// ����gzip��ѹ���ļ���������
				FileInputStream fin = new FileInputStream(gzfilePath);
				// ����gzip�Ľ�ѹ�ļ�������
				GZIPInputStream gzin = new GZIPInputStream(fin);
				// ������ѹ�ļ��������
				FileOutputStream fout = new FileOutputStream(filePath);
				System.out.println("����ļ�·��:" + filePath);
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
	 * �Ե�ǰ��Ŀ¼�µ������ļ����н�ѹ
	 * 
	 * @param dirPath
	 *            :�������ļ�Ŀ¼(������ʽ:/home/oracle/wnData/20190101)
	 */
	public static void uzipDirectory(String dirPath) {
		File dirRoute = new File(dirPath);
		try {
			if (!dirRoute.exists()) {
				return;
			} else {// �ļ�·�����ڵ�ʱ��
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
	 * ����del�ļ�ɾ�����ű�
	 * 
	 * @param filePath
	 *            :�������ļ�·��(�ļ���ʽ:/home/oracle/wnData/20181202/
	 *            a_agr_dep_acct_ent_fx_add_20181202_2820000.del)
	 */
	public static void deleteTable(String filePath) {
		File file = new File(filePath);
		try {
			System.out.println("��ǰ�������ļ�·��:" + filePath);
			String fname = file.getName();
			String suffix = fname.substring(fname.lastIndexOf("."));
			String date = filePath.substring(ctlDataDir.lastIndexOf("/") - 8,
					ctlDataDir.lastIndexOf("/"));// ��ȡ����ǰ��������
			if (!"del".equals(suffix)) {// �����κδ���
				return;
			} else {
				if (file.exists()) {// ���ļ����ڵ����
					// �ж�����������ȫ����
					if (fname.contains("_add_2")) {// ����������(ɾ���ж���һ���Ƿ�)
						// ��ȡ������
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
						} else {// ����ȫ����
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
	 * ɾ��������Ҫ���������
	 * 
	 * @param fileRoute
	 *            :�����ļ���Ŀ¼(/home/oracle/wnData/20190101)
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