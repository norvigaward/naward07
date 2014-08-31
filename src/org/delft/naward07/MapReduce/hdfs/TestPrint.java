package org.delft.naward07.MapReduce.hdfs;

import org.delft.naward07.Utils.MapUtil;
import org.delft.naward07.MapReduce.hdfs.clustering.MeanShiftClustering;
import org.delft.naward07.Utils.ImageUtils.*;

import java.io.*;
import java.security.PrivilegedAction;
import java.util.*;
import java.net.*;

//import nl.surfsara.warcutils.WarcIOConstants;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


/**
 * @author Feng Wang
 */
public class TestPrint implements PrivilegedAction<Object> {
    private final int HASH = 0;
    private final int PHASH = 1;
    private final int HEIGHT = 2;
    private final int WIDTH = 3;
    private final int TIME = 4;
    private final int URL = 5;
    private final int HOST = 6;

	private Configuration conf;
	private String path;
    private String outPath;
    private String log = "";

	public TestPrint(Configuration conf, String path, String outPath) {
		this.conf = conf;
		this.path = path;
        this.outPath = outPath;
	}

    public TestPrint(){}

	@Override
	public Object run() {
		try {
            log = "";
            //mapToFiles();
            mapToFiles2();

		} catch (Exception e) {
			// Just dump the error..
			e.printStackTrace();
		}
		return null;
	}

    private void mapToFiles2(){
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

    private void listToFile(String name, List<String> strings) throws IOException {

        System.out.print("Start to write file " + name);
        long start = System.currentTimeMillis();
        StringBuilder br = new StringBuilder();
        for(String s : strings){
            br.append(s);
            br.append("\n");
        }

        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = null;
        try{
            out = fs.append(new Path(outPath + "/" + name));
        } catch (Exception e){
            out = fs.create(new Path(outPath + "/" + name));
        }

        out.write(br.toString().getBytes());
        out.flush();
        out.close();
        long end = System.currentTimeMillis();
        System.out.println("  -  Elapsed time: " + (end - start)/1000 + "  -  End to write file " + name);
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

//    private void mapToFiles(){
//        try{
//            FileSystem fs = FileSystem.get(conf);
//            FSDataInputStream in = fs.open(new Path(path));
//            InputStream is = in.getWrappedStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//            Map<String, String> map = new HashMap<String, String>();
//
//            // Line format: hash|pHash|width|height|time|url
//            // Example: 87b343c74370370c|f87c3bc0ff848|400|266|20030428|http://www.theroyalforums.com/forums/attachment.php?attachmentid=4887&d=1051557520
//            String line = br.readLine();
//            String[] data = line.split("\\t");
//            String month = data[0];
//
//            long lineNum = 1;
//
//            while (line != null){
//                String record = map.get(month);
//                if(record == null){
//                    record = data[1];
//                } else {
//                    record = record + "\n" + data[1];
//                    //map.remove(month);
//                }
//                map.put(month, record);
//
//                if ((lineNum%10000) == 0)
//                    System.out.println("Reading line: " + lineNum + "  - " + month);
//                lineNum ++;
//
//                line = br.readLine();
//                try{
//                    data = line.split("\\t");
//                    month = data[0];
//                } catch (NullPointerException e){
//                    break;
//                }
//            }
//
//            is.close();
//            in.close();
//
//            System.out.println("-------------------");
//            System.out.println("Total lines (total images): " + lineNum);
//            log += "Total lines (total images): " + lineNum + "\n";
//            System.out.println("-------------------");
//
//            long startTime;
//            long endTime;
//
//            Iterator iterator = map.entrySet().iterator();
//            while (iterator.hasNext()){
//                Map.Entry pairs = (Map.Entry) iterator.next();
//                startTime = System.currentTimeMillis();
//                System.out.print("Output file:" + pairs.getKey());
//                log += "Output file:" + pairs.getKey();
//                FSDataOutputStream out = fs.create(new Path(outPath + "/" + pairs.getKey()));
//                out.write(((String) pairs.getValue()).getBytes());
//                out.flush();
//                out.close();
//                endTime = System.currentTimeMillis();
//                System.out.println("  - Finished - Elapsed time: " + ((endTime - startTime)/1000));
//                log += "  - Finished - Elapsed time: " + ((endTime - startTime)/1000) + "\n";
//            }
//
//            System.out.println(log);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private void cluster(String file){

        Map<String, ImagesInfo> hm;
        String splitRegex = "\\|";
        int[] filter = new int[]{HOST};

        try{
            hm = getMap(file, splitRegex, filter);

            System.out.println(hm.size());

            hm = MapUtil.sortByValue(hm);

            LinkedHashMap hm2 = clusterMeanShift(hm, 10);

            MapUtil.outputMap(hm2, 30);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private LinkedHashMap<String, ImagesInfo> clusterMeanShift(Map<String, ImagesInfo> hm, int width){
        List<String> data = new ArrayList<String>();

        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            Integer num = ((ImagesInfo)pairs.getValue()).getNum();
            String hashcode = (String) pairs.getKey();
            for (int i = 0; i < num; i ++)
                data.add(hashcode);
        }

        MeanShiftClustering meanShiftClustering = new MeanShiftClustering(data, width, false);
        meanShiftClustering.meanShift();
        //meanShiftClustering.meanShiftUsingDistanceMatrix();

        try{
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output005"))));

            System.out.println("Centers:");

            Object[] centers = meanShiftClustering.getCenters().toArray();
            for (Object s: centers)
                System.out.println((String) s);

            System.out.println("Labels:");

            List<Integer> labels = meanShiftClustering.getLabel();
            for (Integer i : labels)
                System.out.println(i);

            System.out.println("Centers in data:");

            List<Integer> centers_index = meanShiftClustering.getCenters_in_data();
            try{
                for (Integer i : centers_index)
                    System.out.println(data.get(i));
            } catch (Exception e){
                //
            }

            System.out.println("Ranked centers:");

            HashMap<String, Integer> rankedCenters = meanShiftClustering.getRankedCenters();
            Iterator iter2 = rankedCenters.entrySet().iterator();
            while (iter2.hasNext()){
                Map.Entry pairs = (Map.Entry) iter2.next();
                System.out.println(pairs.getKey() + "  " + pairs.getValue());
                iter2.remove();
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        LinkedHashMap<String, Integer> clusteredCenters = meanShiftClustering.getRankedCenters();

        LinkedHashMap<String, ImagesInfo> output = new LinkedHashMap<String, ImagesInfo>();
        iter = clusteredCenters.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            ImagesInfo ii = hm.get(pairs.getKey());
            ii.setNum((Integer) pairs.getValue());
            output.put((String) pairs.getKey(), ii);
        }

        return output;
    }

    private Map<String, ImagesInfo> getMap(String file, String slplitRegex, int[] filter) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        ImagesInfo ii;
        String[] items;
        URL aURL = null;

        Map<String, ImagesInfo> hm = new HashMap<String, ImagesInfo>();

        while(line != null){
            items = line.split(slplitRegex);
            items[0] = ImageHelper.hex2Binary(items[0]);
            ii = hm.get(items[0]);

            try{
                aURL = new URL(items[5]);
            } catch (Exception e){
                e.printStackTrace();
            }

            if (ii == null) {
                hm.put(items[0], new ImagesInfo(items[0]));
                hm.get(items[0]).updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[5], aURL.getHost());
            } else {
                //ii.increment(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[4]);
                //ii.increment();
                boolean increase = true;// = ii.increment(aURL.getHost());
                for (int i: filter){
//                    System.out.println("i: " + i);
//                    System.out.println("host: " + HOST);
//                    System.out.println();
                    if (i == HOST) {
                        if(!ii.increment(i, aURL.getHost())){
                            increase = false;
                            break;
                        }
                    } else
                        if(!ii.increment(i, items[i])){
                            increase = false;
                            break;
                        }
                }
                if (increase)
                    ii.updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[5], aURL.getHost());
            }

            line = br.readLine();
            aURL = null;
        }

        br.close();

        return hm;
    }

    private void cluster(String time, List<String> hashcodes) throws IOException{
        if (time.startsWith("00")){
            time = time.substring(1);
            time = "2" + time;
        }

        ImagesInfo ii;
        String[] items;
        URL aURL = null;

        Map<String, ImagesInfo> hm = new HashMap<String, ImagesInfo>();
        //Map<String, ImagesInfo> hm2 = new HashMap<String, ImagesInfo>();

        for(String s : hashcodes){
            items = s.split("\\|");
            ii = hm.get(items[0]);

            try{
                aURL = new URL(items[5]);
            } catch (Exception e){
                e.printStackTrace();
            }

            if (ii == null) {
                hm.put(items[0], new ImagesInfo(items[0]));
                hm.get(items[0]).updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[5], aURL.getHost());
            } else {
                //ii.increment(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[4]);
                boolean increase = ii.incrementByHost(aURL.getHost());
                if (increase)
                    ii.updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[5], aURL.getHost());
            }
        }

        System.out.print(time + "  Hash map size: " + hm.size());

        hm = MapUtil.sortByValue(hm);

        System.out.print("  - Sorted");

        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.create(new Path(outPath + "/" + time));

        out.write(MapUtil.map2String(hm, -1).getBytes());

        out.flush();
        out.close();

        System.out.println("  - Output finished.");
    }

    public static void main(String[] args) {
        TestPrint tp = new TestPrint();
        tp.cluster("200808");
    }
}