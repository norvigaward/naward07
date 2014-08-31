/**
 * Created by Feng Wang on 14-7-14.
 */

package org.delft.naward07.MapReduce.hadoop.warc;

import java.io.*;

import org.delft.naward07.Utils.ImageUtils.ImageHelper;
import org.delft.naward07.Utils.ImageUtils.ImageHelperPHash;
import org.apache.http.client.utils.DateUtils;

import org.jwat.warc.WarcRecord;
import org.jwat.common.HttpHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Record2Hashcode {
    private static final int sizeThresholdMin = 8;
    private static final int sizeThresholdMax = 4000;
    private static final int payloadThreshold = 1000000;

    Record2Hashcode() {
    }

    private static String checkItems(String finger, String date, String lastModified, String url) {
        try {
            if ("".equals(finger) || ("".equals(date) && "".equals(lastModified)) || "".equals(url))
                return "";
        } catch (Exception e) {
            return "";
        }

        if (!"".equals(lastModified))
            return finger + "|" + lastModified + "|" + url;
        else return finger + "|" + date + "|" + url;
    }

    public static String getHashcode(WarcRecord record) {
        try {
            HttpHeader httpHeader = record.getHttpHeader();
            if (httpHeader == null) {
                return "";
            } else if (httpHeader.contentType != null && httpHeader.contentType.contains("image")) {
                InputStream inputStream1 = null;

                if (record.getPayload().getTotalLength() > payloadThreshold)
                    return "";

                inputStream1 = record.getPayload().getInputStream();

                String url = record.header.warcTargetUriStr;
                System.out.println("url: " + url);

//                byte[] b1 = IOUtils.toByteArray(inputStream1);
//                byte[] encoded = Base64.encodeBase64(b1);
//                String finger = new String(encoded, "US-ASCII");
                String finger = produceFingerPrint(inputStream1);

                String date = "";
                String lastModified = "";
                DateFormat df = new SimpleDateFormat("yyyyMMdd");

                try {
                    lastModified = httpHeader.getHeader("Last-Modified").value;
                    Date tempD1 = DateUtils.parseDate(lastModified);
                    lastModified = df.format(tempD1);

//                    System.out.println();
//                    System.out.println("last: " + lastModified);
                } catch (Exception e) {
//                    System.out.println();
//                    System.out.println("header:");
//                    List<HeaderLine> l = httpHeader.getHeaderList();
//                    for (HeaderLine temp : l){
//                        System.out.println("   " + temp.toString() + " " + temp.name + " " + temp.value);
//                    }
                }

                if (!lastModified.matches("[0-9]*")) {
                    int ind = lastModified.lastIndexOf(" ");
                    lastModified = lastModified.substring(0, ind) + " GMT";
                    Date tempD1 = DateUtils.parseDate(lastModified);
                    lastModified = df.format(tempD1);
                }

//                try{
//                    date = httpHeader.getHeader("Date").value;
//                    Date tempD2 = DateUtils.parseDate(date);
//                    date = df.format(tempD2);
//                }
//                catch (Exception e){
//
//                }

                //String url = record.header.warcTargetUriUri.getHost() + record.header.warcTargetUriStr;
                //String url = URLEncoder.encode(record.header.warcTargetUriStr);

                return checkItems(finger, date, lastModified, url);

            } else {
                return "";
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }

    public static String produceFingerPrint(InputStream input) throws Exception {
        BufferedImage source = ImageIO.read(input);
        if (source == null) {
            System.out.println("no image");
            return "";
        } else {
            System.out.println("image exist, height:" + source.getHeight());
            System.out.println("image exist, width:" + source.getWidth());

            if (source.getHeight() < sizeThresholdMin || source.getWidth() < sizeThresholdMin)
                return "";
            if (source.getHeight() > sizeThresholdMax && source.getWidth() > sizeThresholdMax)
                return "";

            ImageHelperPHash ihph = new ImageHelperPHash();
            String hc2 = ihph.getHash(source);

            int width = 8;
            int height = 8;

            BufferedImage imSmall = ImageHelper.resize(source, width, height, false);

            int[] pixels = new int[width * height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pixels[i * height + j] = ImageHelper.rgbToGray(imSmall.getRGB(i, j));
                }
            }

            int avgPixel = ImageHelper.average(pixels);

            int[] comps = new int[width * height];
            for (int i = 0; i < comps.length; i++) {
                if (pixels[i] >= avgPixel) {
                    comps[i] = 1;
                } else {
                    comps[i] = 0;
                }
            }

//            String hc = Arrays.toString(comps);
//            hc = hc.replace("[", "");
//            hc = hc.replace("]", "");
//            hc = hc.replace(",", "");
//            hc = hc.replace(" ", "");

            StringBuffer hashCode = new StringBuffer();
            for (int i = 0; i < comps.length; i += 4) {
                int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1] * (int) Math.pow(2, 2) + comps[i + 2] * (int) Math.pow(2, 1) + comps[i + 2];
                hashCode.append(ImageHelper.binaryToHex(result));
            }

            StringBuffer hashCode2 = new StringBuffer();
            int[] comps2 = new int[(hc2 + "000").length()];
            for (int i = 0; i < comps2.length; i++) {
                comps2[i] = Integer.parseInt((hc2 + "000").charAt(i) + "");
            }
            for (int i = 0; i < comps2.length; i += 4) {
                int result = comps2[i] * (int) Math.pow(2, 3) + comps2[i + 1] * (int) Math.pow(2, 2) + comps2[i + 2] * (int) Math.pow(2, 1) + comps2[i + 2];
                hashCode2.append(ImageHelper.binaryToHex(result));
            }

            return hashCode.toString() + "|" + hashCode2.toString() + "|" + source.getHeight() + "|" + source.getWidth();
        }
    }

}
