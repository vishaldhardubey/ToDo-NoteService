package com.bridgeit.noteservice.model;

/**
 * <p>
 * <b>Model class of reminder type</b>
 * </p>
 * @author : Vishal Dhar Dubey
 * @version : 1.0
 */
public class RemindMe {
	private String laterToday;
	private String tomorrow;
	private String NextWeek;

	public RemindMe() {
		super();
	}

	public String getLaterToday() {
		return laterToday;
	}

	public void setLaterToday(String laterToday) {
		this.laterToday = laterToday;
	}

	public String getTomorrow() {
		return tomorrow;
	}

	public void setTomorrow(String tomorrow) {
		this.tomorrow = tomorrow;
	}

	public String getNextWeek() {
		return NextWeek;
	}

	public void setNextWeek(String nextWeek) {
		NextWeek = nextWeek;
	}

}
