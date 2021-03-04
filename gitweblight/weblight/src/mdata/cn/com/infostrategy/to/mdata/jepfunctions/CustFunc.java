/**************************************************************************
 * $RCSfile: CustFunc.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import org.nfunk.jep.JEP;

/**
 * An example class to test custom functions with JEP.
 */
class CustFunc {

	/**
	 * Constructor.
	 */
	public CustFunc() {

	}

	/**
	 * Main method. Create a new JEP object and parse an example expression
	 * that uses the SquareRoot function.
	 */
	public static void main(String args[]) {
		String str_serverCallServletURL = "http://127.0.0.1:1111/nmgbizm/RemoteCallServlet"; //
		System.setProperty("RemoteCallServletURL", str_serverCallServletURL); //
		JEP parser = new JEP(); // Create a new parser
		String expr = "getCurrentDate()";
		//	Object value;

		System.out.println("Starting CustFunc...");
		parser.addStandardFunctions();
		parser.addStandardConstants();
		parser.addFunction("getCurrentDate", new GetCurrentTime(GetCurrentTime.DATE)); // Add the custom function

		parser.parseExpression(expr); // Parse the expression
		if (parser.hasError()) {
			System.out.println("Error while parsing");
			System.out.println(parser.getErrorInfo());
			return;
		}
		String str_value = (String) parser.getValueAsObject();
		if (str_value == null) {
			System.out.println("\nreturn null!!!!!!");
		}
		//	value = parser.getValueAsObject();
		System.out.println(expr + " = " + str_value); // Print value
		if (parser.hasError()) {
			System.out.println("Error during evaluation");
			System.out.println(parser.getErrorInfo());
			return;
		}

	}
}
/**************************************************************************
 * $RCSfile: CustFunc.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: CustFunc.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/09 13:08:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/07 02:47:23  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:24  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
