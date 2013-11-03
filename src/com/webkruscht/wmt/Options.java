/**
 * 
 */
package com.webkruscht.wmt;

/**
 * @author frank
 *
 */
public class Options {

	int days;
	String startdate;
	String enddate;
	
	public Options() {
		startdate = null;
		enddate = null;
	}
	
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

}
