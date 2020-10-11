package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * SQL构造器,一般有三种:insert,update,delete
 * 有三个子类:InsertSQLBuilder,UpdateSQLBuilder,DeleteSQLBuilder
 * 通过创建对象的方式来创建SQL,然后又保证在一个事务内,从而使开发效率更高,出错率更低
 * 但查询语句很难封装成一个个对象,因为有一些子查询,外连接什么的,还是直接用字符串来得更清楚。
 * @author xch
 *
 */
public interface SQLBuilderIfc extends Serializable{

	/**
	 * 返回实际可执行的SQL
	 * @return
	 */
	public String getSQL();

}
