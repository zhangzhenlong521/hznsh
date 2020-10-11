package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class ProcessAnalysisBean implements Serializable {

	private Integer id = null; //
	private Integer processid = null; //
	private String input = null;
	private String majorsteps = null;
	private String controlposts = null;
	private String proxyposts = null; //
	private String duties = null;
	private String authority = null;
	private String incompduties = null; //
	private String output = null; //
	private String Description = null;
	private String formname = null; //
	private String sysdocs = null; //
	private String itsys = null; //

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProcessid() {
		return processid;
	}

	public void setProcessid(Integer processid) {
		this.processid = processid;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getMajorsteps() {
		return majorsteps;
	}

	public void setMajorsteps(String majorsteps) {
		this.majorsteps = majorsteps;
	}

	public String getControlposts() {
		return controlposts;
	}

	public void setControlposts(String controlposts) {
		this.controlposts = controlposts;
	}

	public String getDuties() {
		return duties;
	}

	public void setDuties(String duties) {
		this.duties = duties;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getIncompduties() {
		return incompduties;
	}

	public void setIncompduties(String incompduties) {
		this.incompduties = incompduties;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getSysdocs() {
		return sysdocs;
	}

	public void setSysdocs(String sysdocs) {
		this.sysdocs = sysdocs;
	}

	public String getItsys() {
		return itsys;
	}

	public void setItsys(String itsys) {
		this.itsys = itsys;
	}

	public String getProxyposts() {
		return proxyposts;
	}

	public void setProxyposts(String proxyposts) {
		this.proxyposts = proxyposts;
	}

}
