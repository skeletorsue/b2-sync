package com.skeletorsue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bucket {
	public String Name, Directory;
	public Integer FileCount = 0;
	public Integer CurrentIndex = 0;
	public Integer ProcessCount = 0;
	public Database DB;
	public List<String> Files = new ArrayList<>();
	private List<Thread> Threads = new ArrayList<>();

	public Bucket(String name, String directory) throws SQLException, IOException {
		String message = "Scanning " + directory;
		Integer line = Sync.ob.print(message);
		this.Name = name;
		this.Directory = directory;

		Stopwatch time = new Stopwatch();
		CountFiles(this.Directory);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Sync.ob.print(message.length(), line, " -- Found " + this.FileCount + " files in " + time.RunTime(2) + " Seconds");
		this.DB = new Database();
		this.DB.conn_info.Driver = "sqlite";
		this.DB.conn_info.DriverClass = "org.sqlite.JDBC";
		this.DB.conn_info.Name = this.Directory + "/.b2-status.sq3";

		if (!new File(this.DB.conn_info.Name).exists()) {
			System.out.println("Creating the table");
			this.DB.insert("CREATE TABLE IF NOT EXISTS sync_hashes( name_hash TEXT PRIMARY KEY NOT NULL, file_hash TEXT NOT NULL )");
		}
	}

	private void CountFiles(String Dir) {
		File[] files = new File(Dir).listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					CountFiles(file.getAbsolutePath());
					continue;
				}
				Files.add(file.getAbsolutePath().substring(this.Directory.length() + 1));
				this.FileCount++;
			}
		}
	}

	public void Process(Integer BucketID) throws IOException, InterruptedException {
		Sync.ob.print("Spawning " + Sync.Config.NumThreads + " threads");
		Integer StartingThreads = Thread.activeCount();

		for (Integer i = 0; i < Sync.Config.NumThreads; i++) {
			Thread t = new Thread(new Uploader(this.DB, BucketID));
			t.start();
		}

		while (Thread.activeCount() > StartingThreads) {
			Sync.ob.print("Active Threads: " + Thread.activeCount());
			Thread.sleep(1000);
		}

		Sync.ob.print("Processed " + ProcessCount + " of " + FileCount + " files");
		Thread.sleep(4000);
	}
}
