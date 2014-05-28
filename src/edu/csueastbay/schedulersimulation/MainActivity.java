package edu.csueastbay.schedulersimulation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity {
	
	private static Scanner x;
	public static Jobs j;			// an object of Jobs
	public static  List<Jobs> readyQ = new ArrayList<Jobs>();  		// use ArrayList as ready queue, easy to sort based on burst time or remaining time.
	
	public static void main(String[] args) {
		
		int index = 0;
		Jobs jobQ[] = new Jobs[8];		// use array to store jobs.
		// open input file
		try{
			x = new Scanner(new File("/Users/Frank/Desktop/CS4560/SchedulerSimulation/src/edu/csueastbay/schedulersimulation/inputfile.txt"));
		} catch(Exception e){
			System.out.println("File doesn't exist.");
		}
		while (x.hasNext()){
			String a = x.next();							// reading data from input file.
			String b = x.next();
			String c = x.next();
			
			int num1 = Integer.parseInt(b);
			int num2 = Integer.parseInt(c);
			j = new Jobs(a,num1,num2);					// put elements onto the job queue.
			jobQ[index] = j;
			index++;
		}
		x.close();
		sort(jobQ);				// sort all the jobs based on arrival time.
		System.out.println("SJF Scheduling: ");
		ShortestJobFirst(jobQ);			// calling SJF function.
		for (int i=0; i<index; i++){
			jobQ[i].reset();			// reset each job's remaining time and start and complete time
		}								// to prepare for next scheduling algorithm
		System.out.println("------------------------------------------");
		System.out.println("SRJF Scheduling: ");
		ShortestRemainingTimeFirst(jobQ); 		// calling SRTF function.
		for (int i=0; i<index; i++){			// reset each job's remaining time and start and complete time
			jobQ[i].reset();					// to prepare for next scheduling algorithm
		}
		System.out.println("------------------------------------------");
		System.out.println("RR Scheduling: ");
		RoundRobin(jobQ);					// calling RR function.
	}
	/* the function that implements the Round Robin scheduling algorithm */
	public static void RoundRobin(Jobs job[]) {
		Jobs runningJob = new Jobs();				// the job that currently running.
		int seconds = job[0].getArrivalT();			// seconds is a counter use to control the scheduling algorithm.
		int qu = 3;									// quantum = 3.
		boolean insert = false;
		for (int i=0; i<job.length; i++){			// put everything in the job array into ready queue.
			readyQ.add(job[i]);
		}
		do
		{
			
			if (runningJob.getJobName().equals(" ")){		// the situation that the first job arrives.
				runningJob = readyQ.remove(0);
				runningJob.setStartT(seconds);
				if (runningJob.getBurstT() <= qu ){	
					seconds+=runningJob.getBurstT();
					runningJob.setRemainT(0);
					System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process terminated");
					runningJob.setCompleteT(seconds);
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
			}else {										
				seconds+=qu;
				runningJob.setRemainT(runningJob.getBurstT()-qu);
				System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Quantum expired");
				for (int i=0; i<readyQ.size(); i++){						// if the process's remaining time longer than quantum
					if (readyQ.get(i).getArrivalT()>=seconds){				// then put it back into the ready queue and
						insert = true;										// make sure its position is before the process that arrives
						readyQ.add(readyQ.get(readyQ.size()-1));			// at or after this second.
						for (int j=readyQ.size()-1; j>i+1; j--){
							readyQ.set(j-1,readyQ.get(j-2));
						}
						readyQ.set(i, runningJob);
					}
					if (insert){
						break;
					}
				}
				if (!insert){
					readyQ.add(runningJob);
				}
				runningJob = readyQ.remove(0);
				runningJob.setStartT(seconds);
			}
		}else{												// the situation that there is already a job that is currently running.
			if (runningJob.getRemainT() <= qu){
				seconds+=runningJob.getRemainT();
				runningJob.setRemainT(0);
				System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process terminated");
				runningJob.setCompleteT(seconds);
				if (!readyQ.isEmpty()){
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
				}
			}else {
				seconds+=qu;
				runningJob.setRemainT(runningJob.getRemainT()-qu);
				System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Quantum expired");
				for (int i=0; i<readyQ.size(); i++){
					if (readyQ.get(i).getArrivalT()>=seconds){
						insert = true;
						readyQ.add(readyQ.get(readyQ.size()-1));
						for (int j=readyQ.size()-1; j>i+1; j--){
							readyQ.set(j-1,readyQ.get(j-2));
						}
						readyQ.set(i, runningJob);
					}
					if (insert){
						break;
					}
				}
				if (!insert){
					readyQ.add(runningJob);
				}
				if (!readyQ.isEmpty()){
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
				}
			}
		}
		}while((!readyQ.isEmpty()) || (runningJob.getRemainT()>0));    	// exit the loop when ready is empty or the last running job's remaining time is 0.
		System.out.printf("ProcessID     Turnaround time     Waiting time\n"); // start printing out the summary.
		float aveTAT = 0;				// average turn around time.
		float aveWT = 0;				// average waiting time.
		for (int i = 0; i<job.length; i++){
			aveTAT += job[i].getCompleteT()-job[i].getArrivalT();
			aveWT += job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT();
			System.out.printf("  %s               %d                    %d\n", job[i].getJobName(), job[i].getCompleteT()-job[i].getArrivalT(),
								job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT());
		}
		System.out.printf("Average Turnaround time: %.2f\n", aveTAT/job.length);
		System.out.printf("Average Waiting time: %.2f\n", aveWT/job.length);
	}
	/* the function that sort the entire job array based on arrival time */
	public static void sort(Jobs job[]){
		Jobs myJob;
		for (int i = 7; i > 1; i--){										// use bubble sort since there are only 8 elements in the array.
			for (int j = 0; j < i; j++){
				if (job[j].getArrivalT()>job[j+1].getArrivalT()){
					myJob = job[j+1];
					job[j+1] = job[j];
					job[j] = myJob;
				}else if(job[j].getArrivalT()==job[j+1].getArrivalT()){
					if (job[j].getJobName().compareTo(job[i].getJobName())>0){
						myJob = job[j+1];
						job[j+1] = job[j];
						job[j] = myJob;
					}
				}
			}
		}
	}
	/* the function that implements the shortest job first scheduling algorithm */
	public static void ShortestJobFirst(Jobs job[]){
		int seconds = job[0].getArrivalT();
		List<Jobs> jAST = new ArrayList<Jobs>();
		Jobs temp,runningJob = new Jobs();
		
		do{
			for (int i = 0; i<job.length; i++){						// add job to ready queue based on the arrival time.
				if (job[i].getArrivalT()==seconds){					// check for every second if there is any job that is arrived.
					jAST.add(job[i]);
				}
			}
			if (!jAST.isEmpty()){									// add the job that is arrived at this second to ready queue.
				for (int i = jAST.size()-1; i >=1; i--){
					for (int j = 0; j < i; j++){
						if (jAST.get(j).getBurstT() > jAST.get(j+1).getBurstT()){
							temp = jAST.get(j+1);
							jAST.set(j+1, jAST.get(j));
							jAST.set(j, temp);
						}
					}
				}
				for (int i = 0; i < jAST.size(); i++){
					readyQ.add(jAST.get(i));
				}
				jAST.clear();
			}
			if (runningJob.getJobName().equals(" ")){				// first arrived job.
				if (readyQ.size()==1){
					runningJob = readyQ.remove(0);					// move it to running state then sort the ready queue based on burst time.
				}else if (readyQ.size()>1){
					for (int i = readyQ.size()-1; i >=1; i--){
						for (int j = 0; j < i; j++){
							if (readyQ.get(j).getBurstT() > readyQ.get(j+1).getBurstT()){
								temp = readyQ.get(j+1);
								readyQ.set(j+1, readyQ.get(j));
								readyQ.set(j, temp);
							}
						}
					}
					runningJob = readyQ.remove(0);
				}
			}
			if (runningJob.getBurstT() == runningJob.getRemainT()){
				runningJob.setStartT(seconds);
			}
			if (runningJob.getRemainT() == 0){				// remaining time is 0 means that the job is completed.
				runningJob.setCompleteT(seconds);
				System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process terminated");
				if (!readyQ.isEmpty()){
					for (int i = readyQ.size()-1; i >=1; i--){
						for (int j = 0; j < i; j++){
							if (readyQ.get(j).getBurstT() > readyQ.get(j+1).getBurstT()){		// sort ready queue then move new job to running state
								temp = readyQ.get(j+1);
								readyQ.set(j+1, readyQ.get(j));
								readyQ.set(j, temp);
							}
						}
					}
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
					runningJob.setRemainT(runningJob.getRemainT()-1);
				}
			}else {
				runningJob.setRemainT(runningJob.getRemainT()-1);
			}
			seconds++;
		}while((!readyQ.isEmpty()) || (runningJob.getRemainT()>0));
		runningJob.setCompleteT(seconds);
		System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process terminated");			// the last job that is completed.
		System.out.printf("ProcessID     Turnaround time     Waiting time\n");			// printing out summary.
		float aveTAT = 0;
		float aveWT = 0;
		for (int i = 0; i<job.length; i++){
			aveTAT += job[i].getCompleteT()-job[i].getArrivalT();
			aveWT += job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT();
			System.out.printf("  %s               %d                    %d\n", job[i].getJobName(), job[i].getCompleteT()-job[i].getArrivalT(),
								job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT());
		}
		System.out.printf("Average Turnaround time: %.2f\n", aveTAT/job.length);
		System.out.printf("Average Waiting time: %.2f\n", aveWT/job.length);
	}
	/* the function that implements the shortest remaining time first scheduling algorithm */
	public static void ShortestRemainingTimeFirst(Jobs job[]){
		int seconds = job[0].getArrivalT();
		List<Jobs> jAST = new ArrayList<Jobs>();
		Jobs temp,runningJob = new Jobs();
		runningJob.setRemainT(1000);					// set remaining time to 1000 to make sure it will never be running.
		do{
			for (int i = 0; i<job.length; i++){
				if (job[i].getArrivalT()==seconds){
					jAST.add(job[i]);
				}
			}
			if (!jAST.isEmpty()){
				for (int i = jAST.size()-1; i >=1; i--){
					for (int j = 0; j < i; j++){
						if (jAST.get(j).getBurstT() > jAST.get(j+1).getBurstT()){
							temp = jAST.get(j+1);
							jAST.set(j+1, jAST.get(j));
							jAST.set(j, temp);
						}
					}
				}
				for (int i = 0; i < jAST.size(); i++){
					readyQ.add(jAST.get(i));
				}
				jAST.clear();
			}
			if (readyQ.size() == 1){
				if ((!runningJob.getJobName().equals(" ")) && (runningJob.getRemainT() != 0)){
					if (runningJob.getRemainT()>readyQ.get(0).getRemainT()){
						System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process preempted by process with shorter burst time");				
						runningJob.setRemainT(runningJob.getRemainT()-1);			// process being preempted, add back to the ready queue, then 
						readyQ.add(runningJob);										// sort.
						runningJob = readyQ.remove(0);
						runningJob.setStartT(seconds);
					}else {				
						runningJob.setRemainT(runningJob.getRemainT()-1);		// just in case the job ends here.
						if (runningJob.getRemainT() == 0){
							runningJob.setRemainT(1);
						}
					}
				}else{
					if (runningJob.getJobName().equals(" ")){				
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
					}
			}
			}else if (readyQ.size() > 1){
				for (int i = readyQ.size()-1; i >=1; i--){			// sort ready queue based on remaining time.
					for (int j = 0; j < i; j++){
						if (readyQ.get(j).getRemainT() > readyQ.get(j+1).getRemainT()){
							temp = readyQ.get(j+1);
							readyQ.set(j+1, readyQ.get(j));
							readyQ.set(j, temp);
						}else if (readyQ.get(j).getRemainT() == readyQ.get(j+1).getRemainT() && readyQ.get(j).getJobName().compareTo(readyQ.get(j+1).getJobName())>0){
							temp = readyQ.get(j+1);										// if remaining time is the same then sort based on alphabet.
							readyQ.set(j+1, readyQ.get(j));
							readyQ.set(j, temp);
						}
					}
				}
				if ((!runningJob.getJobName().equals(" ")) && (runningJob.getRemainT() != 0)){      		// situation that the job is not the first one
					if (runningJob.getRemainT()>readyQ.get(0).getRemainT()){
						System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process preempted by process with shorter burst time");
						runningJob.setRemainT(runningJob.getRemainT()-1);
						readyQ.add(runningJob);
						runningJob = readyQ.remove(0);					
						runningJob.setStartT(seconds);
					}else {
						runningJob.setRemainT(runningJob.getRemainT()-1);
						if (runningJob.getRemainT() == 0){
							runningJob.setRemainT(1);
						}
					}
				}else{
					if (runningJob.getJobName().equals(" ")){
					runningJob = readyQ.remove(0);
					runningJob.setStartT(seconds);
					}
				}
			}
			if (runningJob.getRemainT() == 0){				// when a job is finished.
				if (runningJob.getBurstT()<=seconds-runningJob.getStartT()){
					runningJob.setCompleteT(seconds);
					System.out.printf("%d %s  %s\n", seconds, runningJob.getJobName(), "Process terminated");
					if (!readyQ.isEmpty()){
						runningJob = readyQ.remove(0);
						if (runningJob.getBurstT() == runningJob.getRemainT()){
							runningJob.setStartT(seconds);
						}
					}
				}else if(runningJob.getBurstT() > seconds-runningJob.getStartT()){
					runningJob.setRemainT(runningJob.getRemainT()+1);
				}
			}else {
				runningJob.setRemainT(runningJob.getRemainT()-1);
			}
			seconds++;
		}while((!readyQ.isEmpty()) || (runningJob.getRemainT()>0));
		runningJob.setCompleteT(seconds-1);
		System.out.printf("%d %s  %s\n", seconds-1, runningJob.getJobName(), "Process terminated");		// the information about the last finished job.
		System.out.printf("ProcessID     Turnaround time     Waiting time\n");				// summary of this algorithm
		float aveTAT = 0;
		float aveWT = 0;
		for (int i = 0; i<job.length; i++){
			aveTAT += job[i].getCompleteT()-job[i].getArrivalT();
			aveWT += job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT();
			System.out.printf("  %s               %d                    %d\n", job[i].getJobName(), job[i].getCompleteT()-job[i].getArrivalT(),
								job[i].getCompleteT()-job[i].getArrivalT()-job[i].getBurstT());
		}
		System.out.printf("Average Turnaround time: %.2f\n", aveTAT/job.length);
		System.out.printf("Average Waiting time: %.2f\n", aveWT/job.length);
	}

}
