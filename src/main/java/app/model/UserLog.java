package app.model;

import java.util.Date;
import java.util.Map;

public class UserLog {
	int userId;
	String logType;
	Object logInfo;
	String logDate;
	String ipAddress;
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
