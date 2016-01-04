package com.skeletorsue;

import org.ini4j.Ini;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Config {

	@Option(name = "--conf", usage = "Specify the config file location relative to the execution directory", metaVar = "/home/user/config.ini")
	private String ConfigFileName = "/root/.b2-buckets";

	@Option(name = "-h", help = true, usage = "Displays this help screen")
	private Boolean Help = false;

	private File ConfigFile;
	public Ini Raw;
	public Ini.Section Credentials;
	public List<Bucket> Buckets = new ArrayList();
	public Integer NumThreads;

	public Config(String[] args) throws IOException, SQLException {

		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException cle) {
			System.out.println(cle.getMessage());
			Help = true;
		}

		if (Help) {
			Help = false;
			parser.printUsage(System.out);
			System.exit(1);
		}

		this.ConfigFile = new File(ConfigFileName);
		BuildConfig();
	}

	private void BuildConfig() throws IOException, SQLException {
		this.Raw = new Ini(ConfigFile);
		this.Credentials = this.Raw.get("credentials");
		this.NumThreads = Parser.get(this.Raw.get("general", "num-threads"), 1);

		for (String Key : this.Raw.get("buckets").keySet()) {
			Buckets.add(new Bucket(Key, this.Raw.get("buckets", Key)));
		}
	}
}
