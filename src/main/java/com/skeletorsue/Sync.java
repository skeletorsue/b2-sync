package com.skeletorsue;

import java.io.IOException;

public class Sync {

	public static Output ob;
	private static Integer ExitCode = 0;

	public static void main(String[] args) {
		ob = new Output();

		try {
			ob.print("Hello World");
			Config config = LoadConfig(args);

			
			for (Integer i = 0; i < config.Buckets.size(); i++) {
				ob.print(config.Buckets.get(i).Directory);
			}

		} catch (Exception e) {
			e.printStackTrace();
			ExitCode = 1;
		} finally {
			try {
				ob.tearDown();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(ExitCode);
		}
	}

	public static Config LoadConfig(String[] args) throws Exception {
		Stopwatch timer = new Stopwatch();
		ob.print("Loading config.ini - READING");
		Config config = new Config(args);
		ob.print("Loading config.ini - LOADED " + timer.RunTime(3) + "s");

		return config;
	}

}
