package cn.com.infostrategy.bs.mdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 快速修改模板的Servlet
 * 用于在进行模板比较时，可以通过超链接,快速修改模板中的数据.
 * @author xch
 *
 */
public class QuickUpdateTempletServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -722236312253083871L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_response.setCharacterEncoding("GBK");
		_response.setContentType("text/html"); //

		WLTInitContext context = new WLTInitContext(); //
		try {
			StringBuffer sb_html = new StringBuffer(); //
			sb_html.append("<html>\r\n"); //
			sb_html.append("<head>\r\n"); //
			sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
			sb_html.append("</head>\r\n");
			sb_html.append("<body>\r\n");

			String str_ds1 = _request.getParameter("ds1"); //
			String str_ds2 = _request.getParameter("ds2"); //
			String str_ischild = _request.getParameter("ischild"); //
			String str_templetcode = _request.getParameter("templetcode"); //
			String str_itemkey = _request.getParameter("itemkey"); //
			String str_fieldname = _request.getParameter("fieldname"); //

			sb_html.append("ds1=" + str_ds1 + "<br>\r\n"); //
			sb_html.append("ds2=" + str_ds2 + "<br>\r\n"); //
			sb_html.append("ischild=" + str_ischild + "<br>\r\n"); //
			sb_html.append("templetcode=" + str_templetcode + "<br>\r\n"); //
			sb_html.append("itemkey=" + str_itemkey + "<br>\r\n"); //
			sb_html.append("fieldname=" + str_fieldname + "<br>\r\n"); //

			CommDMO commDMO = new CommDMO(); //
			sb_html.append("<br>");

			if (str_ischild.equals("N")) {
				HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(str_ds1, "select " + str_fieldname + " from pub_templet_1 where templetcode='" + str_templetcode + "'"); //
				String str_value = hvs_1[0].getStringValue(0); //
				String str_update_sql = "update pub_templet_1 set " + str_fieldname + "=? where templetcode='" + str_templetcode + "'"; ///
				commDMO.executeUpdateByDSByMacro(str_ds2, str_update_sql, new String[] { str_value }); //
				sb_html.append("成功将数据源[" + str_ds2 + "]中模板[" + str_templetcode + "]中的主表中的[" + str_fieldname + "]项更改成数据源[" + str_ds1 + "]中对应的值:<br>" + convertHtmlText(str_value) + "<br>\r\n"); //
			} else { //模板子表
				HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(str_ds1, "select " + str_fieldname + " from pub_templet_1_item where itemkey='" + str_itemkey + "' and pk_pub_templet_1=(select pk_pub_templet_1 from pub_templet_1 where templetcode='" + str_templetcode + "')"); //
				String str_value = hvs_1[0].getStringValue(0); //
				String str_update_sql = "update pub_templet_1_item set " + str_fieldname + "=? where itemkey='" + str_itemkey + "' and pk_pub_templet_1=(select pk_pub_templet_1 from pub_templet_1 where templetcode='" + str_templetcode + "')"; //
				commDMO.executeUpdateByDSByMacro(str_ds2, str_update_sql, new String[] { str_value }); //
				sb_html.append("成功将数据源[" + str_ds2 + "]中模板[" + str_templetcode + "]中子表中的itemkey=[" + str_itemkey + "]的[" + str_fieldname + "]项更改成数据源[" + str_ds1 + "]中对应的值:<br>" + convertHtmlText(str_value) + "<br>\r\n"); //
			}

			sb_html.append("</body>\r\n"); //
			sb_html.append("</html>\r\n"); //

			context.commitAllTrans(); //
			_response.getWriter().println(sb_html.toString()); //
		} catch (Exception ex) {
			context.rollbackAllTrans();
			ex.printStackTrace();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ex.printStackTrace(new PrintWriter(bos, true));
			byte exbytes[] = bos.toByteArray();
			bos.close();
			String sb_exstack = new String(exbytes, "GBK");
			sb_exstack = (new TBUtil()).replaceAll(sb_exstack, "\r", "<br>");
			StringBuffer sb_exception = new StringBuffer();
			sb_exception.append("<html>\r\n");
			sb_exception.append("<body>\r\n");
			sb_exception.append("<font size=2 color=\"red\">\r\n");
			sb_exception.append(sb_exstack);
			sb_exception.append("</font>\r\n");
			sb_exception.append("</body>\r\n");
			sb_exception.append("</html>\r\n");
			_response.getWriter().println(sb_exception.toString()); //
		} finally {
			context.closeAllConn();
			context.release(); //
		}
	}

	private String convertHtmlText(String _text) {
		if (_text == null) {
			return ""; //
		}

		TBUtil tbUtil = new TBUtil(); //
		_text = tbUtil.replaceAll(_text, "\n", "<br>"); //
		_text = tbUtil.replaceAll(_text, "\r", "<br>"); //
		return "<font color=\"blue\">" + _text + "</font>"; //
	}

}
