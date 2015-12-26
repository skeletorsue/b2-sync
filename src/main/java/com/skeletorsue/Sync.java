package com.skeletorsue;

import java.io.IOException;

public class Sync {

	public static Output ob;
	private static Integer ExitCode = 0;

	public static void main(String[] args) {
		ob = new Output();

		try {
			Config config = LoadConfig(args);

			Stopwatch timer = StartStep("Sync");

			// loop through our found buckets, and do things with them
			for (Integer i = 0; i < config.Buckets.size(); i++) {
				Bucket b = config.Buckets.get(i);
				ob.print(b.Directory);
			}

			StopStep(timer, "Sync");
		} catch (Exception e) {
			e.printStackTrace();
			ExitCode = 1;
		} finally {
			try {
				ob.tearDown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(ExitCode);
	}

	public static Config LoadConfig(String[] args) throws Exception {
		Stopwatch timer = StartStep("Config Loading");
		Config config = new Config(args);
		StopStep(timer, "Config Loading");

		return config;
	}

	public static Stopwatch StartStep(String Step) throws IOException {
		ob.print("########################################################");
		ob.print("### Starting " + Step + " Process");
		ob.print("########################################################");
		return new Stopwatch();
	}

	public static void StopStep(Stopwatch timer, String Step) throws IOException {
		ob.print("########################################################");
		ob.print("### Finished " + Step + " Process (" + timer.RunTime(3) + ")");
		ob.print("########################################################");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ob.ClearScreen();
	}

}
