package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;

/**
 * ƽ̨��ı�ṹ
 * @author xch
 *
 */
public class WLTTableDf {

	private String tableName; //����
	private String tableDesc; //��˵��
	private ArrayList alColumns = new ArrayList(); //�洢��������Ϣ,һ����Ϣ��һ��һά����.. 
	private String pkFieldName = "id"; //�����ֶ�����,Ĭ����ID
	private String[] indexName = null; //��������,����code,name��
	private ArrayList allIndexs = new ArrayList();
	public static String VARCHAR = "varchar"; //�ַ���
	public static String NUMBER = "number"; //�ַ���
	public static String CHAR = "char"; //
	public static String CLOB = "clob"; //

	//��ṹ
	private WLTTableDf() {
	}

	public WLTTableDf(String _tableName, String _tableDesc) {
		this.tableName = _tableName; //����
		this.tableDesc = _tableDesc; //��˵��
	}

	/**
	 * ����һ����˵��,��ؼ�,������һ�����Ϊ5��һά����,�ֱ�������,��˵��,����,����,�Ƿ�����Ϊ��!
	 * ��5��������������ÿ�����ݿ���ж���!!
	 * @param _colInfo
	 */
	public void addCol(String[] _colInfo) {
		alColumns.add(_colInfo); //
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getPkFieldName() {
		return pkFieldName;
	}

	//���������ֶ���,����Ļ���ʹ��Ĭ�ϵ�ID,�����Ǳز����ٵ�!!
	public void setPkFieldName(String pkFieldName) {
		this.pkFieldName = pkFieldName;
	}

	public String[] getIndexName() {
		return indexName;
	}

	//����������
	public void setIndexName(String[] indexName) {
		this.indexName = indexName;
	}

	//ȡ�������еĶ���
	public String[][] getColDefines() {
		String[][] str_return = new String[alColumns.size()][5]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = (String[]) alColumns.get(i); //
		}
		return str_return; //
	}

	public ArrayList getAllIndexs() {
		return allIndexs;
	}

	public void setAllIndexs(ArrayList allIndexs) {
		this.allIndexs = allIndexs;
	}

}
