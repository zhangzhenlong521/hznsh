package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * ∂®“ÂLog4j≈‰÷√µƒVO
 * @author xch
 *
 */
public class Log4jConfigVO implements Serializable {

	private static final long serialVersionUID = 7381026221298149240L;

	private String server_level = null; //
	private String server_outputtype = null; //
	private String client_level = null; //
	private String client_outputtype = null; //

	public String getServer_level() {
		return server_level;
	}

	public void setServer_level(String server_level) {
		this.server_level = server_level;
	}

	public String getServer_outputtype() {
		return server_outputtype;
	}

	public void setServer_outputtype(String server_outputtype) {
		this.server_outputtype = server_outputtype;
	}

	public String getClient_level() {
		return client_level;
	}

	public void setClient_level(String client_level) {
		this.client_level = client_level;
	}

	public String getClient_outputtype() {
		return client_outputtype;
	}

	public void setClient_outputtype(String client_outputtype) {
		this.client_outputtype = client_outputtype;
	}
}
