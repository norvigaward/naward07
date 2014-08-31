package org.delft.naward07.MapReduce;

import java.util.Arrays;

import org.delft.naward07.MapReduce.hadoop.warc.ImTrends;
import org.delft.naward07.MapReduce.hdfs.Clustering;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Main entry point for the ImTrends.
 *
 * @author Feng Wang
 */
public class Main {
    public enum Programs {
        CLUSTERING("clustering", "Do clustering from a map output file (this is not a mapreduce job)."),
        IMTRENDS("imtrends", "Extract images' hashcodes from http responses in warc (full crawl output) files.");

        private final String name;
        private final String description;

        private Programs(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static void main(String[] args) {
        int retval = 0;
        boolean showUsage = false;
        if (args.length <= 0) {
            showUsage();
            System.exit(0);
        }
        String tool = args[0];
        String[] toolArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            if (Programs.IMTRENDS.getName().equals(tool)) {
                retval = ToolRunner.run(new Configuration(), new ImTrends(), toolArgs);
            }
            else if (Programs.CLUSTERING.getName().equals(tool)) {
                System.out.println("test1");
                Clustering h = new Clustering(args[1], args[2]);
                System.out.println("test2");
                h.run();
			}
            if (showUsage) {
                showUsage();
            }
        } catch (Exception e) {
            showErrorAndExit(e);
        }
        System.exit(retval);
    }

    private static void showErrorAndExit(Exception e) {
        System.out.println("Something didn't quite work like expected: [" + e.getMessage() + "]");
        showUsage();
        System.exit(1);
    }

    private static void showUsage() {
        System.out.println("An example program must be given as the first argument.");
        System.out.println("Valid program names are:");
        for (Programs prog : Programs.values()) {
            System.out.println(" " + prog.getName() + ": " + prog.getDescription());
        }
    }
}
