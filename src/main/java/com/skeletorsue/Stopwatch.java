package com.skeletorsue;

public class Stopwatch {

    private double Start, Stop;

    public Stopwatch() {
        Start = System.nanoTime();
    }

    public void Stop() {
        Stop = System.nanoTime();
    }

    public double RunTime() {
        if (Stop == 0)
            Stop();

        return (Stop - Start) / 1000 / 1000 / 1000;
    }

    public double RunTime(Integer Precision) {
        return Parser.round(RunTime(), Precision);
    }

    private static void time_test() {
        try {
            double start = System.nanoTime();
            System.out.println(start);
            System.out.println("\r\n");
            for (int i = 0; i <= 100; i++) {
                System.out.printf(((char) 0x1b) + "[1A\r" + "Item 1: %d", i);
                System.out.printf(((char) 0x1b) + "[1B\r" + "Item 2: %d", (i + 100));
                System.out.printf(((char) 0x1b) + "[1C\r" + "");

                Thread.sleep(20);

            }
            System.out.println("\r\n");
            double end = System.nanoTime();
            System.out.println(end);
            double run_time = (end - start) / 1000 / 1000 / 1000;
            System.out.println(run_time + " second run time");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
