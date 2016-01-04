package com.skeletorsue;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Uploader implements Runnable {
	private Database db;
	private Integer OutputLine;
	private Integer BucketID;
	private String Bucket;

	public Uploader(Database db, Integer BucketID) throws IOException {
		this.db = db;
		OutputLine = Sync.ob.print("Thread Ready");
		this.BucketID = BucketID;
		this.Bucket = Sync.Config.Buckets.get(BucketID).Name;
	}

	@Override
	public void run() {
		while (Sync.Config.Buckets.get(BucketID).CurrentIndex < Sync.Config.Buckets.get(BucketID).FileCount) {
			String File = Sync.Config.Buckets.get(BucketID).Files.get(Sync.Config.Buckets.get(BucketID).CurrentIndex++);
			try {
				Sync.ob.print(OutputLine, "Processing " + File + " for " + Bucket);
				Sync.Config.Buckets.get(BucketID).ProcessCount++;
				Thread.sleep(ThreadLocalRandom.current().nextInt(10, 45)); // TODO remove this random sleep once we actually sync stuff
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
