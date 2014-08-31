package org.delft.naward07.postProcessing;

import org.delft.naward07.Utils.StringUtil;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by wdwind on 14-8-27.
 */

public class MapOutProcessing {

    public void mapOutProcessing(String dir, String filename) throws IOException {
        System.out.println("Start transfer data to csv.");

        HashSet<String> hs = new HashSet<String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + filename)));

//        File outCSV = new File(dir + "CSV/" + filename + ".csv");
//        File parent = outCSV.getParentFile();
//        if(!parent.exists() && !parent.mkdirs()){
//            throw new IllegalStateException("Couldn't create dir: " + parent);
//        }
//        if(!outCSV.exists())
//            outCSV.createNewFile();
//        BufferedWriter bwCSV = new BufferedWriter(new FileWriter(outCSV));
//
//        File outIndex = new File(dir + "index/" + filename + ".index");
//        File parent2 = outIndex.getParentFile();
//        if(!parent2.exists() && !parent2.mkdirs()){
//            throw new IllegalStateException("Couldn't create dir: " + parent);
//        }
//        if(!outIndex.exists())
//            outIndex.createNewFile();
//        BufferedWriter bwIndex = new BufferedWriter(new FileWriter(outIndex));

        BufferedWriter bwCSV = createPathAndFile(dir + "CSV/" + filename + ".csv");
        BufferedWriter bwIndex = createPathAndFile(dir + "index/" + filename + ".index");
        BufferedWriter bwNum = createPathAndFile(dir + "num/" + filename + ".num");

        String line = br.readLine();
        URL aURL = null;

        long index = 0;

        while (line != null) {
            if(line.equals(""))
                continue;

            //System.out.println(line);
            String[] items = line.split("\\|");

            try{
                aURL = new URL(items[5]);
            } catch (Exception e){
                e.printStackTrace();
            }

            if(hs.contains(items[0] + aURL.getHost())){
                //System.out.println(line);
                line = br.readLine();
                continue;
            } else {
                hs.add(items[0] + aURL.getHost());
                //bw.write("tttt  ");
                bwCSV.write(hashcode2CSV(items[0]));
                bwCSV.write("\n");

                bwIndex.write(index + "\t" +line);
                bwIndex.write("\n");

                index++;
            }

            line = br.readLine();
            aURL = null;
        }

        bwNum.write(String.valueOf(index));
        bwNum.flush();
        bwNum.close();

        br.close();

        bwCSV.flush();
        bwCSV.close();

        bwIndex.flush();
        bwIndex.close();
    }

    private BufferedWriter createPathAndFile(String filepath) throws IOException {
        File out = new File(filepath);
        File parent = out.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        if(!out.exists())
            out.createNewFile();
        return new BufferedWriter(new FileWriter(out));
    }

    private String hashcode2CSV(String hashcode) {
        // Hashcode to binary
        hashcode = StringUtil.hex2Binary(hashcode);

        char[] chs = hashcode.toCharArray();

        StringBuilder sb = new StringBuilder();

        for (char c : chs) {
            sb.append(c);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public void processFolder(String folder) throws Exception {
        File f = new File(folder);
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

//        for(File ff: files)
//            System.out.println(ff.getParent());
//
//        System.out.println("----------------------------");
//
//        for(String n: names)
//            System.out.println(n);

        if (files.size() != names.size())
            throw new Exception("The size of files and names are not the same.");

        for (int i = 0; i < names.size(); i ++){
            File temp = files.get(i);
            System.out.println("-- Start processing file " + temp.getAbsolutePath());
            mapOutProcessing(temp.getParent() + "\\", names.get(i));
        }
    }

    public static void main(String[] args) throws Exception {
        MapOutProcessing mop = new MapOutProcessing();
        //mop.mapOutProcessing("./data/input/", "200303");

        mop.processFolder("C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008");

    }

}
