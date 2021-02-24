package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class RiskAnalysisBean implements Serializable {

	private Integer id = null; //
	private Integer processid = null;
	private String atrisk = null; //
	private String risksource = null;
	private String affectedareas = null;
	private String riskPossibility = null;
	private String riskConsequences = null;
	private String risklevel = null;
	private String innerControls = null;
	private String outerControls = null;
	private String controlPossibility = null;
	private String controlConsequences = null;
	private String controlrisklevel = null;
	private String KeyControlmeasures = null;
	private String ControlEvaluation = null;
	private String ControlObjectives = null;
	private String controlDepts = null;
	private String wfinnerMeasures = null;
	private String wfouterMeasures = null; //
	private String newcaseMeasures = null;
	private String newcasePossibility = null; //
	private String newcaseConsequences = null;
	private String newcaserisklevel = null; //

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

	public String getAtrisk() {
		return atrisk;
	}

	public void setAtrisk(String atrisk) {
		this.atrisk = atrisk;
	}

	public String getRisksource() {
		return risksource;
	}

	public void setRisksource(String risksource) {
		this.risksource = risksource;
	}

	public String getAffectedareas() {
		return affectedareas;
	}

	public void setAffectedareas(String affectedareas) {
		this.affectedareas = affectedareas;
	}

	public String getRiskPossibility() {
		return riskPossibility;
	}

	public void setRiskPossibility(String riskPossibility) {
		this.riskPossibility = riskPossibility;
	}

	public String getRiskConsequences() {
		return riskConsequences;
	}

	public void setRiskConsequences(String riskConsequences) {
		this.riskConsequences = riskConsequences;
	}

	public String getRisklevel() {
		return risklevel;
	}

	public void setRisklevel(String risklevel) {
		this.risklevel = risklevel;
	}

	public String getInnerControls() {
		return innerControls;
	}

	public void setInnerControls(String innerControls) {
		this.innerControls = innerControls;
	}

	public String getOuterControls() {
		return outerControls;
	}

	public void setOuterControls(String outerControls) {
		this.outerControls = outerControls;
	}

	public String getControlPossibility() {
		return controlPossibility;
	}

	public void setControlPossibility(String controlPossibility) {
		this.controlPossibility = controlPossibility;
	}

	public String getControlConsequences() {
		return controlConsequences;
	}

	public void setControlConsequences(String controlConsequences) {
		this.controlConsequences = controlConsequences;
	}

	public String getControlrisklevel() {
		return controlrisklevel;
	}

	public void setControlrisklevel(String controlrisklevel) {
		this.controlrisklevel = controlrisklevel;
	}

	public String getKeyControlmeasures() {
		return KeyControlmeasures;
	}

	public void setKeyControlmeasures(String keyControlmeasures) {
		KeyControlmeasures = keyControlmeasures;
	}

	public String getControlEvaluation() {
		return ControlEvaluation;
	}

	public void setControlEvaluation(String controlEvaluation) {
		ControlEvaluation = controlEvaluation;
	}

	public String getControlObjectives() {
		return ControlObjectives;
	}

	public void setControlObjectives(String controlObjectives) {
		ControlObjectives = controlObjectives;
	}

	public String getControlDepts() {
		return controlDepts;
	}

	public void setControlDepts(String controlDepts) {
		this.controlDepts = controlDepts;
	}

	public String getWfinnerMeasures() {
		return wfinnerMeasures;
	}

	public void setWfinnerMeasures(String wfinnerMeasures) {
		this.wfinnerMeasures = wfinnerMeasures;
	}

	public String getWfouterMeasures() {
		return wfouterMeasures;
	}

	public void setWfouterMeasures(String wfouterMeasures) {
		this.wfouterMeasures = wfouterMeasures;
	}

	public String getNewcaseMeasures() {
		return newcaseMeasures;
	}

	public void setNewcaseMeasures(String newcaseMeasures) {
		this.newcaseMeasures = newcaseMeasures;
	}

	public String getNewcasePossibility() {
		return newcasePossibility;
	}

	public void setNewcasePossibility(String newcasePossibility) {
		this.newcasePossibility = newcasePossibility;
	}

	public String getNewcaseConsequences() {
		return newcaseConsequences;
	}

	public void setNewcaseConsequences(String newcaseConsequences) {
		this.newcaseConsequences = newcaseConsequences;
	}

	public String getNewcaserisklevel() {
		return newcaserisklevel;
	}

	public void setNewcaserisklevel(String newcaserisklevel) {
		this.newcaserisklevel = newcaserisklevel;
	}

}
