package com.skeletorsue;

import java.io.File;
import java.sql.SQLException;

public class Bucket {
	public String Name, Directory;
	public Integer FileCount = 0;
	public Database DB;

	public Bucket(String name, String directory) throws SQLException {
		System.out.print("Scanning " + directory);
		this.Name = name;
		this.Directory = directory;

		Stopwatch time = new Stopwatch();
		CountFiles(this.Directory);

		System.out.println(" -- Found " + this.FileCount + " files in " + time.RunTime(2) + " Seconds");
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
			for (int i = 0; i < files.length; i++) {
				File file = files[i];

				if (file.isDirectory()) {
					CountFiles(file.getAbsolutePath());
					continue;
				}
				this.FileCount++;

			}
		}
	}
}
