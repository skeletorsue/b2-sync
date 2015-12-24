package com.skeletorsue;

public class Sync {

    public static void main(String[] args) {

        try {
            Config config = LoadConfig(args);
            System.out.printf("%n");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Config LoadConfig(String[] args) throws Exception {
        Stopwatch timer = new Stopwatch();
        System.out.print("Loading config.ini - READING\r");
        Config config = new Config(args);
        System.out.print("Loading config.ini - LOADED " + timer.RunTime(3) + "s\r\n");

        return config;
    }

}
