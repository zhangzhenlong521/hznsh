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
	 * ƽ̨����
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _insertobj
	 * @return
	 * @throws Exception
	 */
	public BillVO dealInsert( String _dsName, String _bsInterceptName, BillVO _insertobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // ������

		// ����ǰ����!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeInsert( _dsName, _insertobj ); // ���ܻ����쳣!!!
		}

		// ֱ��ȥ����!!!!!!!!!!!!!!
		String str_sql = _insertobj.getInsertSQL( ); // ʵ�ʵ�SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // ƽ̨ȥ�ύ���ݿ�,���ܻ����쳣!!!

		// ����������!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterInsert( _dsName, _insertobj ); // ���ܻ����쳣!!!
		}

		return _insertobj;
	}

	/**
	 * ƽ̨ɾ��
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _deleteobj
	 * @throws Exception
	 */
	public void dealDelete( String _dsName, String _bsInterceptName, BillVO _deleteobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // ������

		// ɾ��ǰ����!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeDelete( _dsName, _deleteobj );
		}

		// ����ȥɾ��
		String str_sql = _deleteobj.getDeleteSQL( ); // ȡ��SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // ƽ̨ȥ�ύ���ݿ�,���ܻ����쳣!!!

		// ɾ��������!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterDelete( _dsName, _deleteobj );
		}
	}

	/**
	 * ƽ̨����ɾ��
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _deleteobj
	 * @param tablename
	 *            Ҫɾ���ļ�¼���ڵı���
	 * @param field
	 *            ��¼��Ψһ��ʶ�ֶ�,��:ID.
	 * @throws Exception
	 */
	public void dealDelete( String _dsName, String _bsInterceptName, HashVO[] _deleteobj, String tablename, String field )
			throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // ������

		// ɾ��ǰ����!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeCascadeDelete( _dsName, _deleteobj );
		}
		if ( tablename == null || tablename.equals( "" ) )
			throw new Exception( "û������ɾ����¼�ı���" );
		// ����ȥɾ��
		ArrayList list = new ArrayList( );
		for ( int i = 0; i < _deleteobj.length; i++ )
		{
			if ( _deleteobj[i].getStringValue( field ) != null && !_deleteobj[i].getStringValue( field ).equals( "" ) )
				list.add( "delete from " + tablename + " where " + field + "='" + _deleteobj[i].getStringValue( field )
						+ "'" );
			else
				throw new Exception( "Ҫɾ���ļ�¼����������" );
		}

		ServerEnvironment.getCommDMO().executeBatchByDS( _dsName, list ); // ƽ̨ȥ�ύ���ݿ�,���ܻ����쳣!!!

		// ɾ��������!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterCascadeDelete( _dsName, _deleteobj );
		}
	}

	/**
	 * ƽ̨�޸�
	 * 
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _updateobj
	 * @return
	 * @throws Exception
	 */
	public BillVO dealUpdate( String _dsName, String _bsInterceptName, BillVO _updateobj ) throws Exception
	{
		IBSIntercept_03 bsIntercept = getIntercept( _bsInterceptName ); // ������

		// �޸�ǰ����!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitBeforeUpdate( _dsName, _updateobj );
		}
		// �ֹ������,,�����Ҫ���ֹ�������!!!
		if ( _updateobj.isDealVersion( ) )
		{
			Double ld_version = _updateobj.getVersion( ); // ȡ�õ�ǰ�汾��!!
			Double ld_currversion = getVersion( _dsName, _updateobj );
			if ( ld_version != null && ld_currversion != null && !ld_version.equals( ld_currversion ) )
			{
				throw new Exception( "�����������Ѿ��������û��޸ģ���ˢ�����ݺ��ٽ��в�����" );
			}
		}

		String str_sql = _updateobj.getUpdateSQL( );
		ServerEnvironment.getCommDMO().executeUpdateByDS( _dsName, str_sql ); // ����ȥִ��!!

		// �޸ĺ�����!!
		if ( bsIntercept != null )
		{
			bsIntercept.dealCommitAfterUpdate( _dsName, _updateobj );
		}

		return _updateobj;
	}

	/**
	 * �õ��汾��
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
		{ // �Ե��쳣,��Ӱ�����!!
			e.printStackTrace( );
			return null;
		}
	}

	/**
	 * ȡ�÷���������������
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
 * �ʴ��ֳ�����ͳһ�޸�
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
