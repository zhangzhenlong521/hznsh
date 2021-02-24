package cn.com.infostrategy.bs.common;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.RemoteCallParVO;
/**
 * 刷新集群的缓存服务 
 */
public class RegisteCacheDataMaService implements WebDispatchIfc {
	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap map) throws Exception {
		_response.setCharacterEncoding("UTF-8");
		_response.setBufferSize(10240);
		_response.setContentType("text/html");
		RemoteCallParVO par_vo = null;
		WLTInitContext initContext = new WLTInitContext();
		try {
			InputStream request_in = _request.getInputStream();
			Object obj = null;
			ObjectInputStream request_in_objStream = new ObjectInputStream(request_in);
			obj = request_in_objStream.readObject();
			request_in.close();
			par_vo = (RemoteCallParVO) obj;
			if (par_vo != null) {
				ServerCacheDataFactory fc = ServerCacheDataFactory.getInstance();
				Method mtehod = ServerCacheDataFactory.class.getMethod(par_vo.getMethodName(), par_vo.getParClasses());
				mtehod.invoke(fc, par_vo.getParObjs());
				initContext.commitAllTrans();
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			rollbackTrans(initContext);
		} finally {
			closeConn(initContext);
			releaseContext(initContext);
		}
	}

	private int rollbackTrans(WLTInitContext _initContext) {
		int li_allStmtCount = 0;
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					li_allStmtCount = li_allStmtCount + conns[i].getOpenStmtCount();
					conns[i].transRollback();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return li_allStmtCount;
	}

	private void closeConn(WLTInitContext _initContext) {
		try {
			if (_initContext.isGetConn()) {
				WLTDBConnection[] conns = _initContext.GetAllConns();
				for (int i = 0; i < conns.length; i++) {
					try {
						conns[i].close(); //关闭指定数据源连接
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private void releaseContext(WLTInitContext _initContext) {
		try {
			_initContext.release();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
