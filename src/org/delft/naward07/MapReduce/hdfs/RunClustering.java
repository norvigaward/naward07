package org.delft.naward07.MapReduce.hdfs;

import org.delft.naward07.MapReduce.hdfs.clustering.*;
import org.delft.naward07.Utils.ImageUtils.*;
import org.delft.naward07.Utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Created by Feng Wang on 14-8-25.
 */

public class RunClustering implements PrivilegedAction<Object> {

    private final int HOST = 6;

    private Configuration conf;
    private String path;
    private String outPath;
    private String log = "";

    public RunClustering(Configuration conf, String path, String outPath) {
        this.conf = conf;
        this.path = path;
        this.outPath = outPath;
    }

    public RunClustering() {}

    @Override
    public Object run() {
        try {
            log = "";
            cluster();

        } catch (Exception e) {
            // Just dump the error..
            e.printStackTrace();
        }
        return null;
    }

    private void cluster(){
        Map<String, SimpleImagesInfo> hm;
        String splitRegex = "\\|";

        try{
            hm = getMap(splitRegex);

            System.out.println(hm.size());

            LinkedHashMap tempHM = MapUtil.sortByValue(hm);

            meanShift2(tempHM, 10);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, SimpleImagesInfo> getMap(String slplitRegex) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(new Path(path));
        InputStream is = in.getWrappedStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        //ImagesInfo ii;
        SimpleImagesInfo sii;
        String[] items;
        URL aURL = null;

        Map<String, SimpleImagesInfo> hm = new HashMap<String, SimpleImagesInfo>();

        while(line != null){
            items = line.split(slplitRegex);
            items[0] = ImageHelper.hex2Binary(items[0]);
            sii = hm.get(items[0]);

            try{
                aURL = new URL(items[5]);
            } catch (Exception e){
                e.printStackTrace();
            }

            if (sii == null) {
                hm.put(items[0], new SimpleImagesInfo(line, aURL.getHost()));
            } else {
                boolean increase = sii.increment(aURL.getHost());
                if (increase)
                    sii.update(line, aURL.getHost());
            }

            line = br.readLine();
            aURL = null;
        }

        br.close();
        return hm;
    }

    private void meanShift2(Map<String, SimpleImagesInfo> hm, int width){
        List<String> data = new ArrayList<String>();

        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            Integer num = ((SimpleImagesInfo)pairs.getValue()).getNumber();
            String hashcode = (String) pairs.getKey();
            for (int i = 0; i < num; i ++)
                data.add(hashcode);
        }

        MeanShiftClustering meanShiftClustering = new MeanShiftClustering(data, width, false);
        meanShiftClustering.meanShift();

        try{
            FileSystem fs = FileSystem.get(conf);
            FSDataOutputStream out = null;
            try{
                out = fs.append(new Path(outPath + "/testout001"));
            } catch (Exception e){
                out = fs.create(new Path(outPath + "/testout001"));
            }

            HashMap<Integer, Integer> rankedCenters = meanShiftClustering.getRankedCentersHM();

            LinkedHashMap<Integer, HashSet<String>> sortedDataByLabel = meanShiftClustering.getSortedDataInSetByLabel();

            LinkedHashMap<Integer, List<String>> output = new LinkedHashMap<Integer, List<String>>();

            iter = sortedDataByLabel.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry pairs = (Map.Entry) iter.next();
                output.put((Integer) pairs.getKey(), new ArrayList<String>());
                List<String> temp = new ArrayList<String>((HashSet<String>)pairs.getValue());
                for(String s: temp){
                    List<String> list = output.get(pairs.getKey());
                    SimpleImagesInfo sii = hm.get(s);
                    list.addAll(sii.getHashcodes());
                    output.put((Integer) pairs.getKey(), list);
                }
            }

            System.out.println("--- Sorted data by label:");
            out.write(("--- Sorted data by label:").getBytes());
            //LinkedHashMap<Integer, List<String>> sortedDataByLabel = meanShiftClustering.getSortedDataByLabel();
            Iterator iter3 = output.entrySet().iterator();
            while(iter3.hasNext()){
                Map.Entry pairs = (Map.Entry) iter3.next();
                String value = ListUtil.list2String((List<String>) pairs.getValue(), "\t");
                //System.out.println(rankedCenters.get(pairs.getKey()).toString() + "\t" + value);
                out.write((rankedCenters.get(pairs.getKey()).toString() + "\t" + value).getBytes());
            }

            System.out.println("--- End sorted data by label.");
            out.write(("--- End sorted data by label.").getBytes());

            out.flush();
            out.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private void cluster(String file){

        Map<String, SimpleImagesInfo> hm;
        String splitRegex = "\\|";

        try{
            hm = getMap(file, splitRegex);

            System.out.println(hm.size());

            LinkedHashMap tempHM = MapUtil.sortByValue(hm);

            //hm = Utils.sortByValue(hm);

            //LinkedHashMap hm2 = clusterMeanShift(tempHM, 10);

            meanShift(tempHM, 10);

            //MapUtil.outputMap(hm2, 30);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, SimpleImagesInfo> getMap(String file, String slplitRegex) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        //ImagesInfo ii;
        SimpleImagesInfo sii;
        String[] items;
        URL aURL = null;

        Map<String, SimpleImagesInfo> hm = new HashMap<String, SimpleImagesInfo>();

        while(line != null){
            items = line.split(slplitRegex);
            items[0] = ImageHelper.hex2Binary(items[0]);
            sii = hm.get(items[0]);

            try{
                aURL = new URL(items[5]);
            } catch (Exception e){
                e.printStackTrace();
            }

            if (sii == null) {
                hm.put(items[0], new SimpleImagesInfo(line, aURL.getHost()));
            } else {
                boolean increase = sii.increment(aURL.getHost());
                if (increase)
                    sii.update(line, aURL.getHost());
            }

            line = br.readLine();
            aURL = null;
        }

        br.close();
        return hm;
    }

    private void meanShift(Map<String, SimpleImagesInfo> hm, int width){
        List<String> data = new ArrayList<String>();

        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            Integer num = ((SimpleImagesInfo)pairs.getValue()).getNumber();
            String hashcode = (String) pairs.getKey();
            for (int i = 0; i < num; i ++)
                data.add(hashcode);
        }

        MeanShiftClustering meanShiftClustering = new MeanShiftClustering(data, width, false);
        meanShiftClustering.meanShift();

        try{
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output010"))));

            HashMap<Integer, Integer> rankedCenters = meanShiftClustering.getRankedCentersHM();

            LinkedHashMap<Integer, HashSet<String>> sortedDataByLabel = meanShiftClustering.getSortedDataInSetByLabel();

            LinkedHashMap<Integer, List<String>> output = new LinkedHashMap<Integer, List<String>>();

            iter = sortedDataByLabel.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry pairs = (Map.Entry) iter.next();
                output.put((Integer) pairs.getKey(), new ArrayList<String>());
                List<String> temp = new ArrayList<String>((HashSet<String>)pairs.getValue());
                for(String s: temp){
                    List<String> list = output.get(pairs.getKey());
                    SimpleImagesInfo sii = hm.get(s);
                    list.addAll(sii.getHashcodes());
                    output.put((Integer) pairs.getKey(), list);
                }
            }

            System.out.println("--- Sorted data by label:");
            //LinkedHashMap<Integer, List<String>> sortedDataByLabel = meanShiftClustering.getSortedDataByLabel();
            Iterator iter3 = output.entrySet().iterator();
            while(iter3.hasNext()){
                Map.Entry pairs = (Map.Entry) iter3.next();
                String value = ListUtil.list2String((List<String>) pairs.getValue(), "\t");
                System.out.println(rankedCenters.get(pairs.getKey()).toString() + "\t" + value);
            }

            System.out.println("--- End sorted data by label.");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void clusterMeanShift(Map<String, SimpleImagesInfo> hm, int width){
        List<String> data = new ArrayList<String>();

        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            Integer num = ((SimpleImagesInfo)pairs.getValue()).getNumber();
            String hashcode = (String) pairs.getKey();
            for (int i = 0; i < num; i ++)
                data.add(hashcode);
        }

        MeanShiftClustering meanShiftClustering = new MeanShiftClustering(data, width, false);
        meanShiftClustering.meanShift();
        //meanShiftClustering.meanShiftUsingDistanceMatrix();

        try{
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output007"))));

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


        LinkedHashMap<Integer, HashSet<String>> sortedDataByLabel = meanShiftClustering.getSortedDataInSetByLabel();

        LinkedHashMap<Integer, List<String>> output = new LinkedHashMap<Integer, List<String>>();

        iter = sortedDataByLabel.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            output.put((Integer) pairs.getKey(), new ArrayList<String>());
            List<String> temp = new ArrayList<String>((HashSet<String>)pairs.getValue());
            for(String s: temp){
                List<String> list = output.get(pairs.getKey());
                SimpleImagesInfo sii = hm.get(s);
                list.addAll(sii.getHashcodes());
                output.put((Integer) pairs.getKey(), list);
            }
        }

        System.out.println("--- Sorted data by label:");
        //LinkedHashMap<Integer, List<String>> sortedDataByLabel = meanShiftClustering.getSortedDataByLabel();
        Iterator iter3 = output.entrySet().iterator();
        while(iter3.hasNext()){
            Map.Entry pairs = (Map.Entry) iter3.next();
            String value = ListUtil.list2String((List<String>) pairs.getValue(), "\t");
            System.out.println(pairs.getKey() + "  " + value);
        }

        System.out.println("--- End sorted data by label.");

        //return output;
    }

    public static void main(String[] args) {
        RunClustering rc = new RunClustering();
        rc.cluster("201202");
    }
}
