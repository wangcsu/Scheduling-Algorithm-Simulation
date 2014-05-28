
package edu.csueastbay.schedulersimulation;

public class Jobs {

	private String JobName;				// a Jobs class that represent each job being processed, include
	private int burstT;					// basic information of name, burst time, arrival time, and additional
	private int arrivalT;				// information of start time and complete time to calculate the turn 
	private int startT;					// around time and waiting time and also remain time of each process
	private int completeT;				// to implement the SRTF algorithm.
	private int remainT;
	
	public Jobs() {						// default constructor
		JobName = " ";
		burstT = 0;
		arrivalT = 0;
	}
	
	public Jobs(String jname, int btime, int atime){		// constructor
		JobName = jname;
		burstT = btime;
		arrivalT = atime;
		remainT = btime;
	}

	public String getJobName() {						// setters and getters
		return JobName;
	}

	public void setJobName(String jobName) {
		JobName = jobName;
	}

	public int getBurstT() {
		return burstT;
	}

	public void setBurstT(int burstT) {
		this.burstT = burstT;
	}

	public int getArrivalT() {
		return arrivalT;
	}

	public void setArrivalT(int arrivalT) {
		this.arrivalT = arrivalT;
	}

	public int getStartT() {
		return startT;
	}

	public void setStartT(int startT) {
		this.startT = startT;
	}

	public int getCompleteT() {
		return completeT;
	}

	public void setCompleteT(int completeT) {
		this.completeT = completeT;
	}

	public int getRemainT() {
		return remainT;
	}

	public void setRemainT(int remainT) {
		this.remainT = remainT;
	}
	
	public void reset(){						// a reset method to make sure that every field is unchanged
		this.remainT = this.burstT;				// after being used in the last algorithm.
		this.completeT = 0;
		this.startT = 0;
	}

}
