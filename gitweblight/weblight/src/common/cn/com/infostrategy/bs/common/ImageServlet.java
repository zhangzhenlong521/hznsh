package cn.com.infostrategy.bs.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * ͼƬservlet,�ؼ���,�����Ժ��ڽ���Html���ʱ,������Ҫ���ͼƬ, ������<img src="">
 * ����������ЩͼƬ���Ƿ���WebRootĿ¼�е�,��������������洢����,һ���Ǵ���Դ·����ȡ(��ClassPath��),һ���Ǵ����ݿ���ȡ!!!
 * ����,����ЩͼƬ����Ҫ��Swing�ؼ�����Ⱦ������,����Chartͼ��,������ͼ!!
 * ����Щ����Ҫһ��Servlet������!!!
 * ����һ���������ЩͼƬ�Ǵӿͻ�����Ⱦ�����͵��������ȴ洢����,Ȼ���������WebCallServlet,Ȼ�������ͼƬ!!!
 * ����������ͼƬ�������,һ������Դ��(fromtype=res),һ�������ݿ���(fromtype=db),һ���ǻ�����(fromtype=cache),��Ȼ�����Խ�ֱ��·��Ҳ���ݽ���(��˵��)!!
 * @author xch
 *
 */
public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = -3504273159428244042L;

	public static HashMap imgCacheMap = new HashMap(); //ͼƬ�Ļ���Map!!

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _reaponse) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //
		String str_fromtype = _request.getParameter("fromtype"); //��Դ����
		_reaponse.setContentType("image/jpeg"); //�������.
		ServletOutputStream outStream = _reaponse.getOutputStream(); //�����.
		try {
			if (str_fromtype.equalsIgnoreCase("res")) {
				String str_imgname = _request.getParameter("imgname"); //ͼƬ����,�������Դ��,���ǡ�office_023.gif��������,��������ݿ��͵�,����һ������,����ǻ�����,���� 10099231_2������!!
				byte[] bys = getImageFromResCalssPath(str_imgname); //ȡ������!!!
				outStream.write(bys); //���!!!
			} else if (str_fromtype.equalsIgnoreCase("db")) {
				String str_imgname = _request.getParameter("imgname"); //ͼƬ����,�������Դ��,���ǡ�office_023.gif��������,��������ݿ��͵�,����һ������,����ǻ�����,���� 10099231_2������!!
				byte[] bys = getImageFromDataBase(str_imgname); //ȡ������!!!
				outStream.write(bys); //���!!!
			} else if (str_fromtype.equalsIgnoreCase("cache")) { //�ӻ���ȡ!!!
				String str_cacheid = _request.getParameter("cacheid"); //����id
				String str_imgname = _request.getParameter("imgname"); //ͼƬ����,�������Դ��,���ǡ�office_023.gif��������,��������ݿ��͵�,����һ������,����ǻ�����,���� 10099231_2������!!
				String str_iszip = _request.getParameter("iszip"); //
				boolean isZip = ("Y".equals(str_iszip) ? true : false); //
				byte[] bys = getImageFromCache(str_cacheid, str_imgname, isZip); //ȡ������!!!
				outStream.write(bys); //���!!!
			} else {
				try {
					outStream.write(getImageFromCompent(createErrMsgCompent("δ֪��fromtyp����"))); //���һ�������ͼƬ,�������ͼƬ��֪����������!!!Ȼ�󵽺�̨�Ŀ���̨�鿴!!!
				} catch (Exception exx) {
					exx.printStackTrace(); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				outStream.write(getImageFromCompent(createErrMsgCompent("����ͼƬʧ��!"))); //���һ�������ͼƬ,�������ͼƬ��֪����������!!!Ȼ�󵽺�̨�Ŀ���̨�鿴!!!
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		} finally {
			try {
				outStream.close(); //
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		}
	}

	/**
	 * ����Ҳ���ͼƬ�Ĵ���һ���������ʾͼƬ
	 * @return
	 */
	private JComponent createErrMsgCompent(String _text) {
		JTextArea textArea = new JTextArea("��" + _text + "��\r\nѡ��ͼƬ����Ҽ��鿴����!");
		textArea.setOpaque(true); //��͸��!!
		textArea.setBackground(Color.WHITE); //
		textArea.setForeground(Color.RED); //
		textArea.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //�б߿�!!!
		//label.setHorizontalAlignment(SwingConstants.CENTER); //����!!!
		textArea.setFont(new Font("����", Font.PLAIN, 12)); //
		textArea.setPreferredSize(new Dimension(150, 40)); //
		textArea.setBounds(0, 0, 150, 40); //
		return textArea; //
	}

	/**
	 * ��һ��Swing�ؼ��õ�����!!!
	 * @param _component
	 * @return
	 * @throws Exception
	 */
	public byte[] getImageFromCompent(java.awt.Component _component) throws Exception {
		int li_width = (int) _component.getPreferredSize().getWidth(); //
		int li_height = (int) _component.getPreferredSize().getHeight(); //
		BufferedImage bufferedImage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics g2d = bufferedImage.createGraphics(); //����һ������!!!
		_component.paint(g2d); //��Ⱦ��ȥ!!!
		g2d.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "bmp", out); //д��!!
		byte[] returnBys = out.toByteArray(); //
		out.close(); //
		return returnBys; //����
	}

	/**
	 * ��ClassPath��ȡ�ļ�
	 * @param _imgName
	 * @return
	 */
	private byte[] getImageFromResCalssPath(String _imgName) throws Exception {
		String str_filaName = _imgName; //
		if (!_imgName.startsWith("/")) { //���û��ָ��·��,��Ĭ����
			str_filaName = "/cn/com/weblight/images/" + _imgName; //
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		System.out.println("ʵ����Դ�ļ���=[" + str_filaName + "]"); //
		InputStream ins = this.getClass().getResourceAsStream(str_filaName); ////

		byte[] bys = new byte[2048];
		int li_pos = -1; //
		while ((li_pos = ins.read(bys)) != -1) {
			bout.write(bys, 0, li_pos); //
		}
		ins.close(); //
		byte[] returnBys = bout.toByteArray(); //���!!
		bout.close(); //�ر�!!
		return returnBys; //
	}

	/**
	 * �����ݿ�ȡ!!!�Ȳ�ʵ��!!!�Ժ�Ҫʵ��!!!
	 * @param _imgName
	 * @return
	 * @throws Exception
	 */
	private byte[] getImageFromDataBase(String _imgName) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _imgName + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //����!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //�����ֵ
					sb_64code.append(str_item.trim()); //ƴ������!!
				} else { //���ֵΪ��
					break; //�ж��˳�!!!��Ϊֻ���������һ������ͷ,����ֻ��Ҫ��ѭ���жϼ���!!
				}
			}
		}
		byte[] bytes = new TBUtil().convert64CodeToBytes(sb_64code.toString()); //
		return bytes; //
	}

	/**
	 * �ӻ���ȡ!!!
	 * @param _imgName
	 * @return
	 * @throws Exception
	 */
	private byte[] getImageFromCache(String _cacheid, String _imgName, boolean _iszip) throws Exception {
		HashMap batchData = (HashMap) imgCacheMap.get(_cacheid); //
		if (batchData != null) {
			TBUtil tbUtil = new TBUtil(); //
			byte[] bytes = (byte[]) batchData.get(_imgName); //
			if (_iszip) {
				bytes = tbUtil.decompressBytes(bytes); //��ѹһ��!
			}
			return bytes;
		}
		return null; //
	}

	/**
	 * ע��һ������!!!��Ҫ��ȫ��ͬ����!!
	 * ��Ϊ�����Ǵӿͻ���ע���,���Կ�����һ��!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public static synchronized String registCacheCode(HashMap _parMap) throws Exception {
		String str_seq = new CommDMO().getSequenceNextValByDS(null, "S_IMAGESERVLET"); //
		String str_id = System.currentTimeMillis() + "_" + str_seq; //
		imgCacheMap.put(str_id, _parMap); //��¼�����Զ�̵��õ�����ͼ����!!!
		return str_id;
	}

	/**
	 * ɾ������,�����2����֮ǰ������!!!
	 */
	public static synchronized void removeCacheCode() {
		String[] str_allKeys = (String[]) imgCacheMap.keySet().toArray(new String[0]);
		long ll_currtime = System.currentTimeMillis(); //��ǰʱ��!!!
		for (int i = 0; i < str_allKeys.length; i++) {
			String str_time = str_allKeys[i]; //
			if (str_allKeys[i].indexOf("_") > 0) {
				str_time = str_allKeys[i].substring(0, str_allKeys[i].indexOf("_")); //
			}
			long ll_time = Long.parseLong(str_time); //
			if (ll_currtime - ll_time > (2 * 60 * 1000)) {
				imgCacheMap.remove(str_allKeys[i]); //������������ӵľ����!!
			}
		}
	}

}
