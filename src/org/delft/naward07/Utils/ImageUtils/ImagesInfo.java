package org.delft.naward07.Utils.ImageUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by Feng Wang on 14-7-22.
 */

public class ImagesInfo implements Comparable<ImagesInfo> {
    private final int HASH = 0;
    private final int PHASH = 1;
    private final int HEIGHT = 2;
    private final int WIDTH = 3;
    private final int TIME = 4;
    private final int URL = 5;
    private final int HOST = 6;

    private class ImageInfo {
        private int width;
        private int height;

        private String hash;
        private String pHash;

        private String url;
        private String host;

        ImageInfo(int width, int height, /*String hash,*/ String pHash, String url) {
            this.width = width;
            this.height = height;
            this.pHash = pHash;
            this.url = url;
        }

        ImageInfo(int width, int height, String hash, String pHash, String url) {
            this(width, height, pHash, url);
            this.hash = hash;
        }

        ImageInfo(int width, int height, String hash, String pHash, String url, String host) {
            this(width, height, hash, pHash, url);
            this.host = host;
        }

        public String getHost(){
            return host;
        }

        public String getHash(){
            return hash;
        }

        public String getpHash(){
            return pHash;
        }

        @Override
        public String toString() {
//            StringBuilder result = new StringBuilder();
//            String NEW_LINE = System.getProperty("line.separator");
//
//            result.append(" Width: " + width + NEW_LINE);
//            result.append(" Height: " + height + NEW_LINE);
//            result.append(" Hash: " + hash + NEW_LINE );
//            result.append(" pHash: " + pHash + NEW_LINE);
//            //Note that Collections and Maps also override toString
//            result.append(" url: " + url + NEW_LINE);

            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    private int num = 1;
    private List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    //private String month;
    private String hash;
    private String pHash;

    public ImagesInfo(String hash) {
        //this.month = month;
        this.hash = hash;
    }

    public ImagesInfo(String hash, String pHash) {
        //this.month = month;
        this.hash = hash;
        this.pHash = pHash;
    }

    public void increment() {
        ++num;
    }

    public boolean incrementByHost(String host){
        for (ImageInfo ii : imageList){
            if (ii.getHost().equals(host)){
                return false;
            }
        }
        return true;
    }

    public boolean increment(int item, String content){
        String method;
        switch (item){
            case HASH:
                method = "getHash";
                break;
            case PHASH:
                method = "getpHash";
                break;
            case HEIGHT:
            case WIDTH:
            case URL:
                method = "";
                break;
            case HOST:
                method = "getHost";
                break;
            default:
                method = "";
                break;
        }

        if(method.equals("")){
            increment();
            return true;
        }

        String compare = "";

        for (ImageInfo ii : imageList){
            try{
//                Class[] cc = this.getClass().getDeclaredClasses();
//                System.out.println(cc[0].getMethod("getHost").getName());
//                //System.out.println(this.getClass().getDeclaredClasses()[1].getClass().getMethod("getHost").getName());
//                System.out.println(cc.length);
//                for(Class ccc : cc){
//                    System.out.println(ccc.getName());
//                    System.out.println(ccc.getMethod("getHost").getName());
//                }
                Method m = ii.getClass().getMethod(method);
                compare = m.invoke(ii).toString();
            } catch (NoSuchMethodException e){
                e.printStackTrace();
            } catch (InvocationTargetException e){
                e.printStackTrace();
            } catch (IllegalAccessException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            if (compare.equals(content)){
                return false;
            }
        }
        return true;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void updateImagelist(int width, int height, String pHash, String url) {
        imageList.add(new ImageInfo(width, height, pHash, url));
    }

    public void updateImagelist(int width, int height, String hash, String pHash, String url) {
        imageList.add(new ImageInfo(width, height, hash, pHash, url));
    }

    public void updateImagelist(int width, int height, String hash, String pHash, String url, String host) {
        imageList.add(new ImageInfo(width, height, hash, pHash, url, host));
    }

    @Override
    public int compareTo(ImagesInfo iThat) {
        return -Integer.compare(this.getNum(), iThat.getNum());
    }

    @Override
    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    public static void main(String[] args) {
//        ImagesInfo ii = new ImagesInfo("test1", "asd");
//
////        ii.increment(1,2,"1","2","t");
////        ii.increment(4,5, "a","b","c");
//
//        System.out.println(ii.toString());
    }
}
