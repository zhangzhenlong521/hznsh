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
					String usercode = par_vo.getCurrSessionVO().getLoginUserCode(); // �û�����Ψһ
					String sessionid = par_vo.getCurrSessionVO().getHttpsessionid(); // sessionid
					if (par_vo.getCurrVersion() != null && !par_vo.getCurrVersion().equals(ServerEnvironment.getProperty("LAST_STARTTIME"))) {
						String str_newversion = ServerEnvironment.getProperty("LAST_STARTTIME"); //�µİ汾��
						String str_datatime = str_newversion.substring(0, 4) + "-" + str_newversion.substring(4, 6) + "-" + str_newversion.substring(6, 8) + " " + str_newversion.substring(8, 10) + ":" + str_newversion.substring(10, 12) + ":" + str_newversion.substring(12, 14); //
//						String str_errtext = "��������[" + str_datatime + "]����������,����ǿ���˳�,�˳��������µ�¼ϵͳ����!!"; //zzl[2019-2-13] �޸���ʾ
						String str_errtext = "��½��ʱ,����ǿ���˳�,�˳��������µ�¼ϵͳ����!!"; //zzl[2019-2-13] �޸���ʾ
						logger.info(str_errtext); //
						returnExceptionMsg(str_errtext, _response, true); //
						return;
					}
					if (ServerEnvironment.getLoginUserMap().containsKey(sessionid)) { // ����������һ��������ʱ��
						String[] str_onlineusers = (String[]) ServerEnvironment.getLoginUserMap().get(sessionid);
						str_onlineusers[5] = tbUtil.getCurrTime(); // �޸�������ʱ��
					} else { // û��֤����������
						String str_errtext = "�û����ڱ𴦵�½,���˳�ϵͳ!";
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
		WLTRemoteFatalException remoteEX = new WLTRemoteFatalException(exx.getMessage(), _isExit); //�汾�쳣!!!
		remoteEX.setServerTargetEx(exx);
		returnVO.setReturnObject(remoteEX); //
		returnVO.setCallDBCount(-1);
		outPutToclient(_response, returnVO); //������ͻ���..
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
			baos = new ByteArrayOutputStream(); //����������!!!
			byte[] buf = new byte[2048]; //5K,Ҫ��!!����ᱨ��,��ǰ��1024,��������ȡ��һλ,��ȡ�����!��LoadRunderѹ������ʱ���Ǳ���ѹʧ��(���ݸ�ʽ����)!��������Ŀ��Ҳ���������������! �����ĳ����ڵ�������ӱ����Ͳ�������!˵��Ч��������!!
			int len = -1;
			while ((len = _inputstream.read(buf)) != -1) {
				baos.write(buf, 0, len); //
			}
			_inputstream.close(); //�ر�������!�����ͷ�����!!
			_inputstream = null; //
			byte[] byteCodes = baos.toByteArray(); //��һλ�Ǳ��λ!�����0,���ʾ�ǲ�ѹ��,�����1���ʾ��ѹ��!!!
			byte firstMark = byteCodes[0]; //
			byte[] realByteCodes = new byte[byteCodes.length - 1]; //
			System.arraycopy(byteCodes, 1, realByteCodes, 0, realByteCodes.length); //����һ��!JavaӦ�ñ�֤������ܵ�! ���򲻻����System������!�������һ��Ƚ�С!
			return new CompressBytesVO((firstMark == 0 ? false : true), realByteCodes); //�����һλ��0,���ʾûѹ��!!!,�����1���ʾ��ѹ����!
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
				str_return = compressBytes.length + "/" + bytes.length + "Bytes=" + (compressBytes.length / 1024) + "/" + (bytes.length / 1024) + "K,ѹ����" + (compressBytes.length * 100 / bytes.length) + "%,ѹ����ʱ" + (ll_2 - ll_1);
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
			if (!_isZip) { //�����ѹ��!!
				_out.write(new byte[] { 0 }); //
			} else { //�����ѹ��!!
				_out.write(new byte[] { 1 }); //
			}
		}
		int li_cycleSize = 2048; //ÿ��ѭ���������,ԽСԽϸԽ����,��������,����1024��̫С,��2048-5012�����ϸ�������!
		int li_start = 0; //����������ʱ�ֲ�����
		while (1 == 1) { //����ѭ��,��ͻ����������,ֱ�����ݽ���������ͨ��ÿ���1K���ݾ���Ϣһ��ʱ��ķ�ʽ����������,ֻҪ����
			if ((li_start + li_cycleSize) >= bytes.length - 1) { //�����β����λ���ѳ����ļ�����
				_out.write(bytes, li_start, bytes.length - li_start); //
				break; //�ж�ѭ��
			} else {
				_out.write(bytes, li_start, li_cycleSize); //ÿ��ѭ��ֻ���1024�ֽڵ�����,��1K������
				li_start = li_start + li_cycleSize; //λ�������1024
			}
		}
		_out.flush(); //��ջ�����������,��ʽָ��ʵ�����!!!
	}

	/**
	 * ���л�һ������..
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
	 * �������л���һ������
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
