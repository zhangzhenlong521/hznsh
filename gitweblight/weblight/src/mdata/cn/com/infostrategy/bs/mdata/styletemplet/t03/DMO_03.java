/**************************************************************************
 * $RCSfile: DMO_03.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/

package cn.com.infostrategy.bs.mdata.styletemplet.t03;


import java.util.ArrayList;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;

public class DMO_03 extends AbstractDMO
{

	public DMO_03( )
	{

	}

	/**
	 * 平台新增
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _insertobj
	 * @return
	 * @throws Exception
	 */
	public BillVO dealInsert( String _dsName, String _bsInterceptName, BillVO _insertobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // 拦截器

		// 新增前拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeInsert( _dsName, _insertobj ); // 可能会抛异常!!!
		}

		// 直正去新增!!!!!!!!!!!!!!
		String str_sql = _insertobj.getInsertSQL( ); // 实际的SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // 平台去提交数据库,可能会抛异常!!!

		// 新增后拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterInsert( _dsName, _insertobj ); // 可能会抛异常!!!
		}

		return _insertobj;
	}

	/**
	 * 平台删除
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _deleteobj
	 * @throws Exception
	 */
	public void dealDelete( String _dsName, String _bsInterceptName, BillVO _deleteobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // 拦截器

		// 删除前拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeDelete( _dsName, _deleteobj );
		}

		// 真正去删除
		String str_sql = _deleteobj.getDeleteSQL( ); // 取得SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // 平台去提交数据库,可能会抛异常!!!

		// 删除后拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterDelete( _dsName, _deleteobj );
		}
	}

	/**
	 * 平台级联删除
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _deleteobj
	 * @param tablename
	 *            要删除的记录所在的表名
	 * @param field
	 *            记录的唯一标识字段,如:ID.
	 * @throws Exception
	 */
	public void dealDelete( String _dsName, String _bsInterceptName, HashVO[] _deleteobj, String tablename, String field )
			throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // 拦截器

		// 删除前拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeCascadeDelete( _dsName, _deleteobj );
		}
		if ( tablename == null || tablename.equals( "" ) )
			throw new Exception( "没有设置删除记录的表名" );
		// 真正去删除
		ArrayList list = new ArrayList( );
		for ( int i = 0; i < _deleteobj.length; i++ )
		{
			if ( _deleteobj[i].getStringValue( field ) != null && !_deleteobj[i].getStringValue( field ).equals( "" ) )
				list.add( "delete from " + tablename + " where " + field + "='" + _deleteobj[i].getStringValue( field )
						+ "'" );
			else
				throw new Exception( "要删除的记录条件不完整" );
		}

		ServerEnvironment.getCommDMO().executeBatchByDS( _dsName, list ); // 平台去提交数据库,可能会抛异常!!!

		// 删除后拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterCascadeDelete( _dsName, _deleteobj );
		}
	}

	/**
	 * 平台修改
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _updateobj
	 * @return
	 * @throws Exception
	 */
	public BillVO dealUpdate( String _dsName, String _bsInterceptName, BillVO _updateobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // 拦截器

		// 修改前拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeUpdate( _dsName, _updateobj );
		}
		// 乐观锁检查,,如果需要有乐观锁处理!!!
		if ( _updateobj.isDealVersion( ) )
		{
			Double ld_version = _updateobj.getVersion( ); // 取得当前版本号!!
			Double ld_currversion = getVersion( _dsName, _updateobj );
			if ( ld_version != null && ld_currversion != null && !ld_version.equals( ld_currversion ) )
			{
				throw new Exception( "所操作数据已经被其他用户修改，请刷新数据后再进行操作！" );
			}
		}

		String str_sql = _updateobj.getUpdateSQL( );
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // 真正去执行!!

		// 修改后拦截!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterUpdate( _dsName, _updateobj );
		}

		return _updateobj;
	}

	/**
	 * 得到版本号
	 * 
	 * @param _updateobj
	 * @return
	 */
	private Double getVersion( String _dsName, BillVO _updateobj )
	{
		StringBuffer str_sql = new StringBuffer( );
		str_sql.append( "select nvl(version,1) from " + _updateobj.getSaveTableName( ) + " where "
				+ _updateobj.getUpdateWhereCondition( ) );
		String[][] data = null;
		try
		{
			data = ServerEnvironment.getCommDMO().getStringArrayByDS( _dsName, str_sql.toString( ) );
			return new Double( data[0][0] );
		}
		catch ( Exception e )
		{ // 吃掉异常,不影响别人!!
			e.printStackTrace( );
			return null;
		}
	}

	/**
	 * 取得服务器端拦截器类
	 * 
	 * @param _interceptName
	 * @return
	 * @throws Exception
	 */
	private IBSIntercept_03 getIntercept( String _interceptName ) throws Exception
	{
		if ( _interceptName != null && !_interceptName.equals( "" ) )
		{
			return (IBSIntercept_03) Class.forName( _interceptName ).newInstance( );
		}
		else
		{
			return null;
		}
	}

}

/*******************************************************************************
 * $RCSfile: DMO_03.java,v $ $Revision: 1.4 $ $Date: 2012/09/14 09:22:57 $
 * 
 * $Log: DMO_03.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:51  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:53  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:55  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:05  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:24  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:19  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/11/13 05:58:01  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:33  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/15 07:00:32  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/12 04:46:21  sunxf
 * *** empty log message ***
 * Revision 1.3 2007/03/09 01:11:58 sunxf *** empty log
 * message ***
 * 
 * Revision 1.2 2007/03/08 11:03:01 shxch *** empty log message ***
 * 
 * Revision 1.1 2007/03/08 10:42:29 shxch *** empty log message ***
 * 
 * Revision 1.6 2007/03/08 07:45:09 shxch *** empty log message ***
 * 
 * Revision 1.5 2007/03/02 05:02:50 shxch *** empty log message ***
 * 
 * Revision 1.4 2007/02/05 04:40:43 lujian *** empty log message ***
 * 
 * Revision 1.3 2007/02/02 08:52:25 lujian *** empty log message ***
 * 
 * Revision 1.2 2007/01/30 04:32:02 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
