import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;

public class PingUtility {
	private String ipAddress = "www.google.com";

	private final long msDelay = 1000;
	private final int timeOutTime = 2000;
	private final String fileName = "PingUtilResults.txt";
	private final boolean showRuns = true;

	private int trials;
	private int runs;
	private long [] timeArray;

	private BufferedWriter writer;

	public PingUtility(int trials, int runs){
		this.trials = trials;
		this.runs = runs;

		System.out.println("Starting ping test to " + ipAddress);
		System.out.println("Test will have " + trials + " trial(s) with " + runs + " run(s).");

		//open file for writing results
		FileWriter directory;
		try {
			//open file with fileName in current directory
			directory = new FileWriter(System.getProperty("user.dir") + "\\" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		writer = new BufferedWriter(directory);
		strToFile("This test consists of " + trials + " trial(s) and " + runs + " run(s). \n\n");

		//ping server and get times
		timeArray = new long[runs];
		for(int currTrial = 0; currTrial < trials; currTrial += 1) {
			int droppedPackets = pingTest(runs, timeArray);
			System.out.println("Trial " + (currTrial + 1) + " completed");
			//append trial to file
			appendReport(droppedPackets, timeArray, currTrial+1);
		}

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public int pingTest(int testNum, long[] times){
		long startTime, finishTime, sleepTime, pingTime = 0;
		int droppedPackets = 0;

		for(int currRun = 0; currRun < testNum; currRun += 1) {
			try {
				InetAddress inet = InetAddress.getByName(ipAddress);
				startTime = System.currentTimeMillis();

				if (inet.isReachable(timeOutTime)) {
					finishTime = System.currentTimeMillis();
					pingTime = finishTime - startTime;
					times[currRun] = pingTime;
				} else {
					pingTime = 0;
					droppedPackets += 1;
				}
			} catch (Exception e) {
				System.out.println("Exception " + e.getMessage());
			}

			try {
				//calculate sleep delay
				if(pingTime < msDelay) {
					sleepTime = msDelay - pingTime;
				} else {
					sleepTime = 0;
				}

				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return droppedPackets;
	}

	public void appendReport(int dropped, long[] numbers, int trialNum){
		long average;
		long sum = 0;

		StringBuilder outString = new StringBuilder();
		outString.append("RESULTS FOR TRIAL: " + trialNum + "\n");

		//calculate the average and append run times
		for(int n = 0; n < numbers.length; n += 1){
			sum += numbers[n];
			if(showRuns) {
				outString.append("Ping RTT " + numbers[n] + "ms\n");
			}
		}
		average = sum/numbers.length;

		outString.append("AVERAGE: " + average + "ms\tDROPPED PACKETS: " + dropped + "\n\n");

		strToFile(outString.toString());
	}

	public void strToFile(String text){
		try {
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		int trials = 0;
		int runs = 0;

		Scanner input = new Scanner(System.in);

		//validate trial number
		do {
			System.out.print("Number of trials: ");
			while (!input.hasNextInt()) {
				System.out.println("Error: that isn't a valid number");
				input.next();
				System.out.print("Number of trials: ");
			}
			trials = input.nextInt();
		} while (trials < 1);

		//validate run number
		do {
			System.out.print("Number of runs in trial: ");
			while (!input.hasNextInt()) {
				System.out.println("Error: that isn't a valid number");
				input.next();
				System.out.print("Number of runs in trial: ");
			}
			runs = input.nextInt();
		} while (runs < 1);

		PingUtility pingTest = new PingUtility(trials, runs);
	}
}
