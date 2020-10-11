package cn.com.infostrategy.bs.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.RemoteCallParVO;
import cn.com.infostrategy.to.common.RemoteCallReturnVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteFatalException;

public class ClientOLMaService implements WebDispatchIfc {
	private Logger logger = WLTLogger.getLogger(ClientOLMaService.class);
	private TBUtil tbUtil = new TBUtil();
	private BSUtil bsUtil = null;

	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap map) throws Exception {
		_response.setCharacterEncoding("UTF-8");
		_response.setBufferSize(10240);
		_response.setContentType("text/html");
		RemoteCallParVO par_vo = null;
		String actype = _request.getParameter("actype");
		if ("reg".equals(actype)) {
			try {
				InputStream request_in = _request.getInputStream();
				Object obj = null;
				if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) {
					CompressBytesVO compVO = getByteFromInputStreamByMark(request_in);
					byte[] decompressBytes = null;
					if (!compVO.isZip()) {
						decompressBytes = compVO.getBytes();
					} else {
						decompressBytes = decompressBytes(compVO.getBytes());
					}
					if (ServerEnvironment.getProperty("ISENCRYPT") != null && ServerEnvironment.getProperty("ISENCRYPT").equals("Y")) {
						DESKeyTool desTool = new DESKeyTool();
						decompressBytes = desTool.decrypt(decompressBytes);
					}
					obj = deserialize(decompressBytes);
				} else {
					ObjectInputStream request_in_objStream = new ObjectInputStream(request_in);
					obj = request_in_objStream.readObject();
				}
				request_in.close();
				par_vo = (RemoteCallParVO) obj;

				if (par_vo != null) {
					String usercode = par_vo.getCurrSessionVO().getLoginUserCode(); // 用户编码唯一
					String sessionid = par_vo.getCurrSessionVO().getHttpsessionid(); // sessionid
					if (par_vo.getCurrVersion() != null && !par_vo.getCurrVersion().equals(ServerEnvironment.getProperty("LAST_STARTTIME"))) {
						String str_newversion = ServerEnvironment.getProperty("LAST_STARTTIME"); //新的版本号
						String str_datatime = str_newversion.substring(0, 4) + "-" + str_newversion.substring(4, 6) + "-" + str_newversion.substring(6, 8) + " " + str_newversion.substring(8, 10) + ":" + str_newversion.substring(10, 12) + ":" + str_newversion.substring(12, 14); //
//						String str_errtext = "服务器在[" + str_datatime + "]重新重启过,程序将强行退出,退出后请重新登录系统即可!!"; //zzl[2019-2-13] 修改提示
						String str_errtext = "登陆超时,程序将强行退出,退出后请重新登录系统即可!!"; //zzl[2019-2-13] 修改提示
						logger.info(str_errtext); //
						returnExceptionMsg(str_errtext, _response, true); //
						return;
					}
					if (ServerEnvironment.getLoginUserMap().containsKey(sessionid)) { // 如果有则更新一个最后访问时间
						String[] str_onlineusers = (String[]) ServerEnvironment.getLoginUserMap().get(sessionid);
						str_onlineusers[5] = tbUtil.getCurrTime(); // 修改最后访问时间
					} else { // 没有证明被挤掉了
						String str_errtext = "用户已在别处登陆,请退出系统!";
						returnExceptionMsg(str_errtext, _response, true);
						return;
					}
				}
				RemoteCallReturnVO returnVO = new RemoteCallReturnVO();
				outPutToclient(_response, returnVO);
			} catch (Throwable ex) {
				ex.printStackTrace();
				RemoteCallReturnVO returnVO = new RemoteCallReturnVO();
				outPutToclient(_response, returnVO);
			}
		} else if ("kil".equals(actype)) {
			String str_clientsessionID = _request.getParameter("sessionid");
			String usercode = _request.getParameter("usercode");
			killOtherSession(_request.getScheme(), ((HttpServletRequest) _request).getContextPath(), str_clientsessionID, usercode);
		}
	}

	private void killOtherSession(String prot, String con, String newsessionid, String usercode) {
		HashMap olusermap = ServerEnvironment.getLoginUserMap();
		if (olusermap != null && olusermap.size() > 0) {
			String[] keys = (String[]) olusermap.keySet().toArray(new String[0]);
			for (int i = 0; i < olusermap.size(); i++) {
				if (!newsessionid.equals(keys[i]) && usercode.equals(((String[]) olusermap.get(keys[i]))[3])) {
					olusermap.remove(keys[i]);
				}
			}
		}
	}

	private void returnExceptionMsg(String _message, ServletResponse _response, boolean _isExit) throws Exception {
		RemoteCallReturnVO returnVO = new RemoteCallReturnVO(); //
		Exception exx = new Exception(_message); //
		WLTRemoteFatalException remoteEX = new WLTRemoteFatalException(exx.getMessage(), _isExit); //版本异常!!!
		remoteEX.setServerTargetEx(exx);
		returnVO.setReturnObject(remoteEX); //
		returnVO.setCallDBCount(-1);
		outPutToclient(_response, returnVO); //输出至客户端..
	}

	public CompressBytesVO compressBytes(byte[] _initbytes) {
		return getBSUtil().compressBytes(_initbytes); //
	}

	public byte[] decompressBytes(byte[] _initbyte) {
		return getBSUtil().decompressBytes(_initbyte); //
	}

	private CompressBytesVO getByteFromInputStreamByMark(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(); //创建输入流!!!
			byte[] buf = new byte[2048]; //5K,要大!!否则会报错,以前是1024,而且是先取第一位,再取后面的!在LoadRunder压力测试时老是报解压失败(数据格式不对)!在招行项目中也经常发生这个问题! 后来改成现在的这个样子兵力就不报错了!说明效果很明显!!
			int len = -1;
			while ((len = _inputstream.read(buf)) != -1) {
				baos.write(buf, 0, len); //
			}
			_inputstream.close(); //关闭输入流!快速释放连接!!
			_inputstream = null; //
			byte[] byteCodes = baos.toByteArray(); //第一位是标记位!如果是0,则表示是不压缩,如果是1则表示是压缩!!!
			byte firstMark = byteCodes[0]; //
			byte[] realByteCodes = new byte[byteCodes.length - 1]; //
			System.arraycopy(byteCodes, 1, realByteCodes, 0, realByteCodes.length); //拷贝一下!Java应该保证这个性能的! 否则不会放在System对象中!而且入参一般比较小!
			return new CompressBytesVO((firstMark == 0 ? false : true), realByteCodes); //如果第一位是0,则表示没压缩!!!,如果是1则表示是压缩的!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				if (_inputstream != null) {
					_inputstream.close(); //
				}
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			try {
				baos.close(); //
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}

		}
	}

	private String outPutToclient(ServletResponse _response, RemoteCallReturnVO _returnVO) {
		String str_return = "";
		try {
			OutputStream outStream = _response.getOutputStream();
			if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) {
				byte[] bytes = serialize(_returnVO);
				long ll_1 = System.currentTimeMillis();
				CompressBytesVO compressVO = compressBytes(bytes);
				long ll_2 = System.currentTimeMillis();
				byte[] compressBytes = compressVO.getBytes();
				str_return = compressBytes.length + "/" + bytes.length + "Bytes=" + (compressBytes.length / 1024) + "/" + (bytes.length / 1024) + "K,压缩比" + (compressBytes.length * 100 / bytes.length) + "%,压缩耗时" + (ll_2 - ll_1);
				_response.setContentLength(compressBytes.length + 1);
				streamOutPutToClient(outStream, new Boolean(compressVO.isZip()), compressBytes);
			} else {
				byte[] bytes = serialize(_returnVO);
				int li_length = bytes.length;
				str_return = li_length + "Bytes=" + (li_length / 1024) + "K";
				_response.setContentLength(bytes.length);
				streamOutPutToClient(outStream, null, bytes);
			}
			outStream.flush();
			outStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return str_return; ////
	}

	private void streamOutPutToClient(OutputStream _out, Boolean _isZip, byte[] bytes) throws Exception {
		if (_isZip != null) {
			if (!_isZip) { //如果不压缩!!
				_out.write(new byte[] { 0 }); //
			} else { //如果是压缩!!
				_out.write(new byte[] { 1 }); //
			}
		}
		int li_cycleSize = 2048; //每次循环输出多少,越小越细越均匀,经过测试,发现1024是太小,而2048-5012则会明细提高性能!
		int li_start = 0; //定义坐标临时局部变量
		while (1 == 1) { //做死循环,向客户端输出数据,直接数据结束，其中通过每输出1K数据就休息一段时间的方式来控制流量,只要控制
			if ((li_start + li_cycleSize) >= bytes.length - 1) { //结果结尾坐标位置已超过文件长度
				_out.write(bytes, li_start, bytes.length - li_start); //
				break; //中断循环
			} else {
				_out.write(bytes, li_start, li_cycleSize); //每次循环只输出1024字节的数量,即1K的数据
				li_start = li_start + li_cycleSize; //位置坐标加1024
			}
		}
		_out.flush(); //清空缓冲区中内容,显式指定实际输出!!!
	}

	/**
	 * 序列化一个对象..
	 * @param _obj
	 * @return
	 */
	private byte[] serialize(Object _obj) {
		ByteArrayOutputStream buf = null; //
		ObjectOutputStream out = null; //
		try {
			buf = new ByteArrayOutputStream();
			out = new ObjectOutputStream(buf);
			out.writeObject(_obj);
			byte[] bytes = buf.toByteArray();
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		} finally {
			try {
				out.close(); //
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 反向序列化成一个对象
	 * @return
	 */
	private Object deserialize(byte[] _bytes) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(_bytes));
			Object obj = in.readObject();
			in.close();
			return obj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				in.close(); //
			} catch (Exception ex) {
			}
		}
	}

	private BSUtil getBSUtil() {
		if (bsUtil != null) {
			return bsUtil;
		}
		bsUtil = new BSUtil(); //
		return bsUtil;
	}

}
