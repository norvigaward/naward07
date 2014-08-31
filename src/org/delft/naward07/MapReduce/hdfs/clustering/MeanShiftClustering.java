package org.delft.naward07.MapReduce.hdfs.clustering;

import org.delft.naward07.Utils.*;

import java.util.*;

/**
 * Created by wdwind on 14-8-8.
 */

public class MeanShiftClustering {
    // Data
    private List<String> data;
    // Labels
    private List<Integer> label;

    //private List<String> centers;
    //private HashSet<String> centers;

    // Centers
    private LinkedHashSet<String> centers;

    // The closet points in the data set to the centers
    private List<Integer> centers_in_data;

    // Ranked centers by the frequency
    private HashMap<Integer, Integer> rankedCenters;

    // Window width
    private int width;

    // Max iterations
    private final int ITER = 10;

    // If using data distances matrix
    private boolean distanceMatrix = false;

    // Distance matrix
    private HashMap<Long, Integer> distances;

    // Data by label
    private LinkedHashMap<Integer, List<String>> dataByLabel;

    // Data by label without repetition
    private LinkedHashMap<Integer, HashSet<String>> dataWithoutRepetition;

    public MeanShiftClustering(List<String> data, int width){
        this.data = data;
        this.width = width;

        Init();
    }

    public MeanShiftClustering(List<String> data, int width, boolean distanceMatrix){
        this.data = data;
        this.width = width;
        this.distanceMatrix = distanceMatrix;

        Init();
    }

    private void Init(){
        label = new ArrayList<Integer>();
        centers = new LinkedHashSet<String>();
        centers_in_data = new ArrayList<Integer>();
        distances = new HashMap<Long, Integer>();
        dataByLabel = new LinkedHashMap<Integer, List<String>>();
        dataWithoutRepetition = new LinkedHashMap<Integer, HashSet<String>>();


        if (distanceMatrix){
            long startTime = System.currentTimeMillis();
            for(long i = 0; i < data.size(); i ++){
                for (long j = i; j < data.size(); j ++){
                    int dist = distance(data.get((int)i), data.get((int)j));
                    long key = (i + j)*(i + j + 1)/2 + j;
                    distances.put(key, dist);
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Calculating distances matrix time: " + (endTime - startTime)/1000);
        }
    }

    public void meanShiftUsingDistanceMatrix(){

        List<String> within = new ArrayList<String>();
        List<Integer> withinIndex = new ArrayList<Integer>();

        int n = 0;

        long startTime = System.currentTimeMillis();

        // Calculate center for each data point
        //for(String obj: data){
        for(int i = 0; i < data.size(); i ++){
            Integer obj = i;

            System.out.print(obj + "  " + n + "  ");
            n ++;
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime)/1000);

            Integer oldObj = -1;
            int iter = 0;

            while(i != oldObj && iter < ITER){
                //System.out.println(iter);
                //for(String obj2: data){
                for (int j = 0; j < data.size(); j ++){
                    String obj2 = data.get(j);
                    if (distances.get((obj + j)*(obj + j + 1)/2 + j) < width){
                        within.add(obj2);
                        withinIndex.add(j);
                    }
                }
                Integer center = meanCentroids(within, withinIndex);
                oldObj = obj;
                obj = center;
                within.clear();
                withinIndex.clear();
                iter ++;
            }

            centers.add(data.get(obj));
        }

        // Assign data points to centers
        for (String obj: data){
            int dis = -1;
            int index = -1;

            int i = 0;
            for (String c: centers){
                int tempDis = distance(obj, c);
                if(dis == -1){
                    dis = tempDis;
                    index = i;
                } else {
                    if (tempDis < dis){
                        dis = tempDis;
                        index = i;
                    }
                }
                i ++;
            }
            label.add(index);
        }

        // Get center in the data
        Object[] cString = centers.toArray();
        for (int i = 0; i < centers.size(); i++){
            int minDist = -1;
            int index = -1;

            for (int j = 0; j < label.size(); j++){
                if (label.get(j) != i){
                    continue;
                }

                int tempDist = distance(data.get(j), (String) cString[i]);
                if (minDist == -1){
                    minDist = tempDist;
                    index = j;
                } else {
                    if (tempDist < minDist){
                        minDist = tempDist;
                        index = j;
                    }
                }
            }

            centers_in_data.add(index);
        }
    }

    public void meanShift(){
        //List<String> centers = new ArrayList<String>();

        List<String> within = new ArrayList<String>();

        int n = 0;

        long startTime = System.currentTimeMillis();

        // Calculate center for each data point
        for(String obj: data){
            System.out.print(obj + "  " + n + "  ");
            n ++;
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime)/1000);

            String oldObj = "";
            int iter = 0;

            while(!obj.equals(oldObj) && iter < ITER){
                //System.out.println(iter);
                for(String obj2: data){
                    if (distanceWithin(obj, obj2, width))
                        within.add(obj2);
                }
                String center = mean(within);
                oldObj = obj;
                obj = center;
                within.clear();
                iter ++;
            }
            centers.add(obj);
        }

        // Assign data points to centers
        n = 0;
        for (String obj: data){
            System.out.println(n);
            n ++;
            int dis = -1;
            int index = -1;

            int i = 0;
            for (String c: centers){
                int tempDis = distance(obj, c);
                if(dis == -1){
                    dis = tempDis;
                    index = i;
                } else {
                    if (tempDis < dis){
                        dis = tempDis;
                        index = i;
                    }
                }
                i ++;
            }
            label.add(index);
        }

        // Get center in the data
        Object[] cString = centers.toArray();
        for (int i = 0; i < centers.size(); i++){
            System.out.println(i);
            int minDist = -1;
            int index = -1;

            for (int j = 0; j < label.size(); j++){
                if (label.get(j) != i){
                    continue;
                }

                int tempDist = distance(data.get(j), (String) cString[i]);
                if (minDist == -1){
                    minDist = tempDist;
                    index = j;
                } else {
                    if (tempDist < minDist){
                        minDist = tempDist;
                        index = j;
                    }
                }
            }

            centers_in_data.add(index);
        }
    }

    private int distance(String bs1, String bs2){
        // check preconditions
        if (bs1 == null || bs2 == null || bs1.length() != bs2.length()) {
            throw new IllegalArgumentException();
        }

        // compute hamming distance
        int distance = 0;
        for (int i = 0; i < bs1.length(); i++) {
            if (bs1.charAt(i) != bs2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    private boolean distanceWithin(String bs1, String bs2, int minDist){
        // check preconditions
        if (bs1 == null || bs2 == null || bs1.length() != bs2.length()) {
            throw new IllegalArgumentException();
        }

        // compute hamming distance
        int distance = 0;
        for (int i = 0; i < bs1.length(); i++) {
            if (bs1.charAt(i) != bs2.charAt(i)) {
                distance++;
                if (distance > minDist)
                    return false;
            }
        }

        return true;
    }

    private String mean(List<String> within) {
        int size = within.get(0).length();
        double[] sum = new double[size];
        //Arrays.fill(sum, 0);

        for (String s: within){
            for(int i = 0; i < size; i++){
                sum[i] += Integer.parseInt(s.substring(i, i + 1));
            }
        }

        String out = "";

        for (int i = 0; i < sum.length; i++){
            sum[i] /= within.size();
            sum[i] = (sum[i] >= 0.5) ? 1 : 0;
            out += (int) sum[i];
        }

        return out;
    }

    private Integer meanCentroids(List<String> within, List<Integer> withinIndex) {
        int size = within.get(0).length();
        double[] sum = new double[size];
        //Arrays.fill(sum, 0);

        for (String s: within){
            for(int i = 0; i < size; i++){
                sum[i] += Integer.parseInt(s.substring(i, i + 1));
            }
        }

        String out = "";

        for (int i = 0; i < sum.length; i++){
            sum[i] /= within.size();
            sum[i] = (sum[i] >= 0.5) ? 1 : 0;
            out += (int) sum[i];
        }

        int dist = -1;
        int index = -1;

        for(int i = 0; i < within.size(); i++){
            String obj = within.get(i);
            int temp = distance(obj, out);
            if(dist == -1){
                dist = temp;
                index = i;
            } else {
                if(temp < dist){
                    dist = temp;
                    index = i;
                }
            }
        }

        return index;
    }

    private void rankCenters(){
        rankedCenters = new HashMap<Integer, Integer>();

        for(Integer c: label){
            Integer integer = rankedCenters.get(c);
            if (integer == null){
                rankedCenters.put(c, 1);
            } else {
                rankedCenters.put(c, integer + 1);
            }
        }

        rankedCenters = (HashMap) MapUtil.sortByValue(rankedCenters, false);
    }

    public LinkedHashMap<String, Integer> getRankedCenters(){
        rankCenters();
        LinkedHashMap<String, Integer> output = new LinkedHashMap<String, Integer>();

        Iterator iter = rankedCenters.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            output.put(data.get(centers_in_data.get((Integer) pairs.getKey())), (Integer) pairs.getValue());
            iter.remove();
        }

        return output;
    }

    public HashMap<Integer, Integer> getRankedCentersHM() {
        rankCenters();
        return rankedCenters;
    }

    public LinkedHashSet<String> getCenters(){
        return centers;
    }

    public List<Integer> getLabel(){
        return label;
    }

    public List<Integer> getCenters_in_data(){
        return centers_in_data;
    }

    public LinkedHashMap<Integer, List<String>> getDataByLabel(){
        //List<String> dataList;
        dataByLabel = new LinkedHashMap<Integer, List<String>>();

        for(int i = 0; i < label.size(); i++){
            List<String> dataList = dataByLabel.get(label.get(i));
            if (dataList == null) {
                dataList = new ArrayList<String>();
                dataList.add(data.get(i));
                dataByLabel.put(label.get(i), dataList);
            } else {
                dataList.add(data.get(i));
                dataByLabel.put(label.get(i), dataList);
            }
        }

        return dataByLabel;
    }

    public LinkedHashMap<Integer, HashSet<String>> getDataInSetByLabel(){
        //List<String> dataList;
        dataWithoutRepetition = new LinkedHashMap<Integer, HashSet<String>>();

        for(int i = 0; i < label.size(); i++){
            HashSet<String> dataSet = dataWithoutRepetition.get(label.get(i));
            if (dataSet == null) {
                dataSet = new HashSet<String>();
                dataSet.add(data.get(i));
                dataWithoutRepetition.put(label.get(i), dataSet);
            } else {
                dataSet.add(data.get(i));
                dataWithoutRepetition.put(label.get(i), dataSet);
            }
        }

        return dataWithoutRepetition;
    }

    public LinkedHashMap<Integer, List<String>> getSortedDataByLabel(){
        rankCenters();
        LinkedHashMap<Integer, List<String>> dataByLabel = getDataByLabel();
        LinkedHashMap<Integer, List<String>> output = new LinkedHashMap<Integer, List<String>>();

        Iterator iter = rankedCenters.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            output.put((Integer) pairs.getKey(), (List<String>) dataByLabel.get(pairs.getKey()));
            iter.remove();
        }

        return output;
    }

    public LinkedHashMap<Integer, HashSet<String>> getSortedDataInSetByLabel(){
        rankCenters();
        LinkedHashMap<Integer, HashSet<String>> dataByLabel = getDataInSetByLabel();
        LinkedHashMap<Integer, HashSet<String>> output = new LinkedHashMap<Integer, HashSet<String>>();

        Iterator iter = rankedCenters.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            output.put((Integer) pairs.getKey(), (HashSet<String>) dataByLabel.get(pairs.getKey()));
            iter.remove();
        }

        return output;
    }

    public static void main(String[] args) {
        List<String> data = new ArrayList<String>();
        data.add("0000");
        data.add("0001");
        data.add("1111");
        data.add("1100");
        data.add("1110");
        data.add("0101");

        MeanShiftClustering meanShiftClustering = new MeanShiftClustering(data, 1);
        meanShiftClustering.meanShift();

        System.out.println("--- Centers:");

        Object[] centers = meanShiftClustering.getCenters().toArray();
        for (Object s: centers)
            System.out.println((String) s);

        System.out.println("--- End centers.");
        System.out.println();

        System.out.println("--- Labels:");

        List<Integer> labels = meanShiftClustering.getLabel();
        for (Integer i : labels)
            System.out.println(i);

        System.out.println("--- End labels.");
        System.out.println();

        System.out.println("---- Centers in data:");

        List<Integer> centers_index = meanShiftClustering.getCenters_in_data();
        for (Integer i : centers_index)
            System.out.println(data.get(i));

        System.out.println("--- End centers in data.");
        System.out.println();

        System.out.println("--- Ranked centers");

        LinkedHashMap<String, Integer> rankedCenters = meanShiftClustering.getRankedCenters();
        Iterator iter = rankedCenters.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry pairs = (Map.Entry) iter.next();
            System.out.println(pairs.getKey() + "  " + pairs.getValue());
            iter.remove();
        }

        System.out.println("--- End ranked centers.");

        System.out.println();

        System.out.println("--- Data by label:");

        LinkedHashMap<Integer, List<String>> dataByLabel = meanShiftClustering.getDataByLabel();
        Iterator iter2 = dataByLabel.entrySet().iterator();
        while (iter2.hasNext()){
            Map.Entry pairs = (Map.Entry) iter2.next();
            String value = ListUtil.list2String((List<String>)pairs.getValue(), "\t");
            System.out.println(pairs.getKey() + "  " + value);
        }

        System.out.println("--- End data by label.");

        System.out.println();

        System.out.println("--- Sorted data by label:");
        LinkedHashMap<Integer, List<String>> sortedDataByLabel = meanShiftClustering.getSortedDataByLabel();
        Iterator iter3 = sortedDataByLabel.entrySet().iterator();
        while(iter3.hasNext()){
            Map.Entry pairs = (Map.Entry) iter3.next();
            String value = ListUtil.list2String((List<String>)pairs.getValue(), "\t");
            System.out.println(pairs.getKey() + "  " + value);
        }

        System.out.println("--- End sorted data by label.");

//        HashMap<Integer, Integer> rankedCentersS = meanShiftClustering.getRankedCentersHM();
//
//        Iterator iter4 = rankedCentersS.entrySet().iterator();
//        while (iter4.hasNext()){
//            Map.Entry pairs = (Map.Entry) iter4.next();
//            System.out.println(pairs.getKey()+" "+pairs.getValue());
//        }

    }
}
