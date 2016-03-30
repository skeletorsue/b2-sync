package com.skeletorsue;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
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

                String NameHash = Hash.Name("SHA-256", File);

                String SqlStatement = "SELECT file_hash FROM sync_hashes WHERE name_hash = ?";
                PreparedStatement PreparedStatement = this.db.prepare(SqlStatement);
                PreparedStatement.setString(1, NameHash);
                ResultSet result = PreparedStatement.executeQuery();

                String StoredHash = "XXXX";
                File FileObject = new File(Sync.Config.Buckets.get(BucketID).Directory + "/" + File);
                String FileHash = Hash.File("SHA-512", FileObject);

                while (result.next()) {
                    StoredHash = result.getString("file_hash");
                }

                if (!Objects.equals(StoredHash, FileHash)) {
                    // doo the sync!
					B2.Upload(FileObject, Sync.Config.Buckets.get(BucketID));

                    // insert into the db
                    String InsertStatement = "INSERT OR REPLACE INTO sync_hashes (name_hash, file_hash) VALUES (?, ?)";
                    PreparedStatement InsertQuery = this.db.prepare(InsertStatement);
                    InsertQuery.setString(1, NameHash);
                    InsertQuery.setString(2, FileHash);
                    InsertQuery.execute();
                }

                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 45)); // TODO remove this random sleep once we actually sync stuff
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
