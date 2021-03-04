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
 * 图片servlet,关键类,我们以后在进行Html输出时,经常需要输出图片, 方法就<img src="">
 * 但问题是有些图片不是放在WebRoot目录中的,而是有另外两外存储方法,一种是从资源路径中取(即ClassPath中),一种是从数据库中取!!!
 * 另外,还有些图片是需要从Swing控件中渲染出来的,比如Chart图表,工作流图!!
 * 而这些都需要一个Servlet来进行!!!
 * 还有一种情况是有些图片是从客户端渲染出来送到服务器先存储起来,然后二次请求WebCallServlet,然后再输出图片!!!
 * 所有有三种图片输出类型,一种是资源型(fromtype=res),一种是数据库型(fromtype=db),一种是缓存型(fromtype=cache),当然还可以将直接路径也兼容进来(再说吧)!!
 * @author xch
 *
 */
public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = -3504273159428244042L;

	public static HashMap imgCacheMap = new HashMap(); //图片的缓存Map!!

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _reaponse) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //
		String str_fromtype = _request.getParameter("fromtype"); //来源类型
		_reaponse.setContentType("image/jpeg"); //输出类型.
		ServletOutputStream outStream = _reaponse.getOutputStream(); //输出流.
		try {
			if (str_fromtype.equalsIgnoreCase("res")) {
				String str_imgname = _request.getParameter("imgname"); //图片名称,如果是资源的,则是【office_023.gif】的样子,如果是数据库型的,则是一个名称,如果是缓存型,则是 10099231_2的样子!!
				byte[] bys = getImageFromResCalssPath(str_imgname); //取得数据!!!
				outStream.write(bys); //输出!!!
			} else if (str_fromtype.equalsIgnoreCase("db")) {
				String str_imgname = _request.getParameter("imgname"); //图片名称,如果是资源的,则是【office_023.gif】的样子,如果是数据库型的,则是一个名称,如果是缓存型,则是 10099231_2的样子!!
				byte[] bys = getImageFromDataBase(str_imgname); //取得数据!!!
				outStream.write(bys); //输出!!!
			} else if (str_fromtype.equalsIgnoreCase("cache")) { //从缓存取!!!
				String str_cacheid = _request.getParameter("cacheid"); //缓存id
				String str_imgname = _request.getParameter("imgname"); //图片名称,如果是资源的,则是【office_023.gif】的样子,如果是数据库型的,则是一个名称,如果是缓存型,则是 10099231_2的样子!!
				String str_iszip = _request.getParameter("iszip"); //
				boolean isZip = ("Y".equals(str_iszip) ? true : false); //
				byte[] bys = getImageFromCache(str_cacheid, str_imgname, isZip); //取得数据!!!
				outStream.write(bys); //输出!!!
			} else {
				try {
					outStream.write(getImageFromCompent(createErrMsgCompent("未知的fromtyp参数"))); //输出一个错误的图片,看到这个图片就知道出问题了!!!然后到后台的控制台查看!!!
				} catch (Exception exx) {
					exx.printStackTrace(); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				outStream.write(getImageFromCompent(createErrMsgCompent("生成图片失败!"))); //输出一个错误的图片,看到这个图片就知道出问题了!!!然后到后台的控制台查看!!!
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
	 * 如果找不到图片的创建一个错误的提示图片
	 * @return
	 */
	private JComponent createErrMsgCompent(String _text) {
		JTextArea textArea = new JTextArea("※" + _text + "※\r\n选中图片点击右键查看属性!");
		textArea.setOpaque(true); //不透明!!
		textArea.setBackground(Color.WHITE); //
		textArea.setForeground(Color.RED); //
		textArea.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //有边框!!!
		//label.setHorizontalAlignment(SwingConstants.CENTER); //居中!!!
		textArea.setFont(new Font("宋体", Font.PLAIN, 12)); //
		textArea.setPreferredSize(new Dimension(150, 40)); //
		textArea.setBounds(0, 0, 150, 40); //
		return textArea; //
	}

	/**
	 * 从一个Swing控件得到内容!!!
	 * @param _component
	 * @return
	 * @throws Exception
	 */
	public byte[] getImageFromCompent(java.awt.Component _component) throws Exception {
		int li_width = (int) _component.getPreferredSize().getWidth(); //
		int li_height = (int) _component.getPreferredSize().getHeight(); //
		BufferedImage bufferedImage = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics g2d = bufferedImage.createGraphics(); //创建一个画布!!!
		_component.paint(g2d); //渲染进去!!!
		g2d.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "bmp", out); //写入!!
		byte[] returnBys = out.toByteArray(); //
		out.close(); //
		return returnBys; //返回
	}

	/**
	 * 从ClassPath中取文件
	 * @param _imgName
	 * @return
	 */
	private byte[] getImageFromResCalssPath(String _imgName) throws Exception {
		String str_filaName = _imgName; //
		if (!_imgName.startsWith("/")) { //如果没有指定路径,则默认是
			str_filaName = "/cn/com/weblight/images/" + _imgName; //
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		System.out.println("实际资源文件名=[" + str_filaName + "]"); //
		InputStream ins = this.getClass().getResourceAsStream(str_filaName); ////

		byte[] bys = new byte[2048];
		int li_pos = -1; //
		while ((li_pos = ins.read(bys)) != -1) {
			bout.write(bys, 0, li_pos); //
		}
		ins.close(); //
		byte[] returnBys = bout.toByteArray(); //输出!!
		bout.close(); //关闭!!
		return returnBys; //
	}

	/**
	 * 从数据库取!!!先不实现!!!以后要实现!!!
	 * @param _imgName
	 * @return
	 * @throws Exception
	 */
	private byte[] getImageFromDataBase(String _imgName) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _imgName + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //遍历!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //如果有值
					sb_64code.append(str_item.trim()); //拼接起来!!
				} else { //如果值为空
					break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
				}
			}
		}
		byte[] bytes = new TBUtil().convert64CodeToBytes(sb_64code.toString()); //
		return bytes; //
	}

	/**
	 * 从缓存取!!!
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
				bytes = tbUtil.decompressBytes(bytes); //解压一下!
			}
			return bytes;
		}
		return null; //
	}

	/**
	 * 注册一个缓存!!!需要是全局同步的!!
	 * 因为可能是从客户端注册的,所以可能是一批!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public static synchronized String registCacheCode(HashMap _parMap) throws Exception {
		String str_seq = new CommDMO().getSequenceNextValByDS(null, "S_IMAGESERVLET"); //
		String str_id = System.currentTimeMillis() + "_" + str_seq; //
		imgCacheMap.put(str_id, _parMap); //记录下这次远程调用的流程图的码!!!
		return str_id;
	}

	/**
	 * 删除缓存,即清除2分钟之前的数据!!!
	 */
	public static synchronized void removeCacheCode() {
		String[] str_allKeys = (String[]) imgCacheMap.keySet().toArray(new String[0]);
		long ll_currtime = System.currentTimeMillis(); //当前时间!!!
		for (int i = 0; i < str_allKeys.length; i++) {
			String str_time = str_allKeys[i]; //
			if (str_allKeys[i].indexOf("_") > 0) {
				str_time = str_allKeys[i].substring(0, str_allKeys[i].indexOf("_")); //
			}
			long ll_time = Long.parseLong(str_time); //
			if (ll_currtime - ll_time > (2 * 60 * 1000)) {
				imgCacheMap.remove(str_allKeys[i]); //如果超过两分钟的就清除!!
			}
		}
	}

}
