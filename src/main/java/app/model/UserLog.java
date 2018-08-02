package app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserLog {
	int userId;
	String logType;
	Object logInfo;
	String logDate;
	String ipAddress;
	Map<String,String> LogInfoEach;
	String logTypeKeyword;
	String startingTimeCuration;
	String finishingTimeCuration;
	String logTypeQuestion;
	String sparqlCases;
	public String getSparqlCases() {
		return sparqlCases;
	}

	public void setSparqlCases(String sparqlCases) {
		this.sparqlCases = sparqlCases;
	}
	int revision;
	
	
	
	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getStartingTimeCuration() {
		return startingTimeCuration;
	}

	public void setStartingTimeCuration(String startingTimeCuration) {
		this.startingTimeCuration = startingTimeCuration;
	}

	public String getFinishingTimeCuration() {
		return finishingTimeCuration;
	}

	public void setFinishingTimeCuration(String finishingTimeCuration) {
		this.finishingTimeCuration = finishingTimeCuration;
	}
	
	public String getLogTypeKeyword() {
		return logTypeKeyword;
	}

	public void setLogTypeKeyword(String logTypeKeyword) {
		this.logTypeKeyword = logTypeKeyword;
	}

	public String getLogTypeQuestion() {
		return logTypeQuestion;
	}

	public void setLogTypeQuestion(String logTypeQuestion) {
		this.logTypeQuestion = logTypeQuestion;
	}
	
	public Map<String, String> getLogInfoEach() {
		return LogInfoEach;
	}

	public void setLogInfoEach(Map<String, String> logInfoEach) {
		LogInfoEach = logInfoEach;
	}

	public UserLog() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public Object getLogInfo() {
		return logInfo;
	}

	public void setLogInfo(Object logInfo) {
		this.logInfo = logInfo;
	}
	public String getLogDate() {
		return logDate;
	}
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
