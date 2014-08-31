package org.delft.naward07.MapReduce.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivilegedAction;

/**
 * Created by Feng Wang on 14-8-25.
 */

public class SplitMapOutput implements PrivilegedAction<Object> {
    private Configuration conf;
    private String path;
    private String outPath;
    private String log = "";

    public SplitMapOutput(Configuration conf, String path, String outPath) {
        this.conf = conf;
        this.path = path;
        this.outPath = outPath;
    }

    @Override
    public Object run() {
        try {
            log = "";
            //mapToFiles();
            clusteringOnTimePeriod();

        } catch (Exception e) {
            // Just dump the error..
            e.printStackTrace();
        }
        return null;
    }

    private void clusteringOnTimePeriod(){
        try{
            FileSystem fs = FileSystem.get(conf);
            FSDataInputStream in = fs.open(new Path(path));
            InputStream is = in.getWrappedStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = br.readLine();
            StringBuilder sbPeriod = new StringBuilder();
            String[] data = line.split("\\t");
            String time = data[0];
            String oldTime = data[0];
            long lineNum = 1;
            long lineReaded = 0;

            boolean skip = true;

            while(line != null){
//                if (skip && !time.equals("201310")){
//                    line = br.readLine();
//                    try{
//                        data = line.split("\\t");
//                        time = data[0];
//                    } catch (NullPointerException e){
//                        break;
//                    }
//                    lineNum ++;
//                    oldTime = time;
//                    if ((lineNum%100000) == 0)
//                        System.out.println("Reading line: " + lineNum + "  - " + time);
//                    continue;
//                } else {
//                    skip = false;
//                }

                if(!time.equals(oldTime) || lineReaded > 300000){
                    listToFile(oldTime, sbPeriod);
                    sbPeriod = new StringBuilder();
                    lineReaded = 0;
                    System.out.println("Line readed: " + lineNum);
                }
                sbPeriod.append(data[1]);
                sbPeriod.append("\n");
                lineReaded++;

                oldTime = time;

                line = br.readLine();
                try{
                    data = line.split("\\t");
                    time = data[0];
                } catch (NullPointerException e){
                    break;
                }
                lineNum ++;
            }

            System.out.println("Total number of lines: " + lineNum);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listToFile(String name, StringBuilder sb) throws IOException {
        System.out.print("String length: " + sb.length());
        String sub;

        System.out.print("  -  Start to write file " + name);
        long startTime = System.currentTimeMillis();

        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = null;
        try{
            out = fs.append(new Path(outPath + "/" + name));
        } catch (Exception e){
            out = fs.create(new Path(outPath + "/" + name));
        }

        out.write(sb.toString().getBytes());
        out.flush();
        out.close();
        long endTime = System.currentTimeMillis();
        System.out.println("  -  Elapsed time: " + (endTime - startTime)/1000 + "  -  End to write file " + name);
    }

}
