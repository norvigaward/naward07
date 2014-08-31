package org.delft.naward07.postProcessing;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wdwind
 */

public class FileNamesForPHP {

    public static void getTimePeriodsName(String inputFolder, String outputName) throws IOException {
        File f = new File(inputFolder);
        //ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

//        for(File ff: files)
//            System.out.println(ff.getParent());

        System.out.println("----------------------------");

        for(String n: names)
            System.out.println(n);

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputName)));

        for(String n: names){
            try {
                Integer.parseInt(n);
//                Integer year = Integer.parseInt(n.substring(0, 4));
//                Integer month = Integer.parseInt(n.substring(4, 6));

                String year = n.substring(0, 4);
                String month = n.substring(4, 6);

                StringBuilder sb = new StringBuilder();
                sb.append("\"");
                sb.append(n);
                sb.append("\"");
                sb.append(", ");
                bw.write(sb.toString());

                System.out.println(year);
                System.out.println(month);

            } catch (Exception e){
                //e.printStackTrace();
                continue;
            }
        }

        bw.write("\n");

        for(String n: names){
            try {
                Integer.parseInt(n);
//                Integer year = Integer.parseInt(n.substring(0, 4));
                Integer month = Integer.parseInt(n.substring(4, 6));

                String year = n.substring(0, 4);
//                String month = n.substring(4, 6);

                String mon = getMon(month);

                StringBuilder sb = new StringBuilder();
                sb.append("\"");
                sb.append(mon);
                sb.append(" ");
                sb.append(year);
                sb.append("\"");
                sb.append(", ");
                bw.write(sb.toString());

                System.out.println(year);
                System.out.println(month);

            } catch (Exception e){
                //e.printStackTrace();
                continue;
            }
        }

        bw.flush();
        bw.close();
    }

    private static String getMon(int month) {
        switch (month){
            case 1:
                return "Jan.";
            case 2:
                return "Feb.";
            case 3:
                return "Mar.";
            case 4:
                return "Apr.";
            case 5:
                return "May.";
            case 6:
                return "Jun.";
            case 7:
                return "Jul.";
            case 8:
                return "Aug.";
            case 9:
                return "Sep.";
            case 10:
                return "Oct.";
            case 11:
                return "Nov.";
            case 12:
                return "Dec";
            default:
                return "";
        }

    }

    public static void totalLines(String inputFolder){
        long lines = 0;

        File f = new File(inputFolder);
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

        System.out.println("----------------------------");

        for(String n: names)
            System.out.println(n);

        int i = 0;
        for(String n: names){
            try {
                Integer.parseInt(n);

                System.out.println(files.get(i).getAbsolutePath());

                lines += countLines(files.get(i).getAbsolutePath());

            } catch (Exception e){
                //e.printStackTrace();
                //continue;
            }
            i++;
        }

        System.out.println(lines);
    }

    public static long countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            long count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    public static void getImgNum(String inputFolder){
        File f = new File(inputFolder);
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

        StringBuilder sb = new StringBuilder();
        sb.append("array(");

        int i = 0;
        for(String n: names){
            try {
                Integer.parseInt(n);

                System.out.println(files.get(i).getAbsolutePath());

                sb.append("\"");
                sb.append(n);
                sb.append("\"=>\"");
                sb.append(countLines(files.get(i).getAbsolutePath()));
                sb.append("\",");
            } catch (Exception e){
                //e.printStackTrace();
                //continue;
            }
            i++;
        }
        sb.append(");");

        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws IOException {
        //FileNamesForPHP.getTimePeriodsName("C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008", "C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008\\time");
        //FileNamesForPHP.totalLines("C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008");
        FileNamesForPHP.getImgNum("C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008");
    }

}
