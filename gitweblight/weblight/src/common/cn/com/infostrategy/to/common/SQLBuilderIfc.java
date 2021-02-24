package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * SQL������,һ��������:insert,update,delete
 * ����������:InsertSQLBuilder,UpdateSQLBuilder,DeleteSQLBuilder
 * ͨ����������ķ�ʽ������SQL,Ȼ���ֱ�֤��һ��������,�Ӷ�ʹ����Ч�ʸ���,�����ʸ���
 * ����ѯ�����ѷ�װ��һ��������,��Ϊ��һЩ�Ӳ�ѯ,������ʲô��,����ֱ�����ַ������ø������
 * @author xch
 *
 */
public interface SQLBuilderIfc extends Serializable{

	/**
	 * ����ʵ�ʿ�ִ�е�SQL
	 * @return
	 */
	public String getSQL();

}
