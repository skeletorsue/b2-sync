package com.skeletorsue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Bucket {

    public String Name, Directory;
    public Integer FileCount = 0;
    public Integer CurrentIndex = 0;
    public Integer ProcessCount = 0;
    public Database DB;
    public List<String> Files = new ArrayList<>();

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

                if (file.getName().equals(".b2-status.sq3")) {
                    continue;
                }

                Files.add(file.getAbsolutePath().substring(this.Directory.length() + 1));
                this.FileCount++;
            }
        }
    }

    public void Process(Integer BucketID) throws IOException, InterruptedException {
        Integer startLine = Sync.ob.print("Processing: " + Directory);

        for (Integer i = 0; i < Sync.Config.NumThreads; i++) {
            Thread t = new Thread(new Uploader(this.DB, BucketID));
            t.setName("SYNC_" + t.getId());
            t.start();
        }

        int SyncThreads = 999;
        while (SyncThreads > 0) {
            SyncThreads = 0;
            ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
            int noThreads = currentGroup.activeCount();
            Thread[] lstThreads = new Thread[noThreads];
            currentGroup.enumerate(lstThreads);
            for (int i = 0; i < noThreads; i++) {
                if (lstThreads[i].getName().length() > 5 && Objects.equals(lstThreads[i].getName().substring(0, 5), "SYNC_"))
                    SyncThreads++;
            }

            Thread.sleep(1000);
        }

        Sync.ob.print(startLine, Directory + " finished processing " + ProcessCount + " of " + FileCount + " files");

        Thread.sleep(4000);
        Sync.ob.TrimScreen(startLine);
    }
}
