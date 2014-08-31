/**
 * Created by Feng Wang on 14-6-11.
 */
package org.delft.naward07.Utils.ImageUtils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.math.BigInteger;

public class ImageHelper {

    public static final String path = System.getProperty("user.dir");

    /**
     * image resize <br/>
     * save:ImageIO.write(BufferedImage, imgType[jpg/png/...], File);
     *
     * @param source source image
     * @param width  width
     * @param height height
     * @param b      keep ratio
     */
    public static BufferedImage resize(BufferedImage source, int width,
                                       int height, boolean b) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) width / source.getWidth();
        double sy = (double) height / source.getHeight();

        if (b) {
            if (sx > sy) {
                sx = sy;
                width = (int) (sx * source.getWidth());
            } else {
                sy = sx;
                height = (int) (sy * source.getHeight());
            }
        }

        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width,
                    height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else
            target = new BufferedImage(width, height, type);
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    /**
     * Calculate gray value
     *
     * @param pixels pixels
     * @return int gray value
     */
    public static int rgbToGray(int pixels) {
        // int _alpha = (pixels >> 24) & 0xFF;
        int _red = (pixels >> 16) & 0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }

    /**
     * Calculate mean value of array
     *
     * @param pixels array
     * @return int mean value
     */
    public static int average(int[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * Binary to hex
     *
     * @param int binary
     * @return char hex
     */
    public static char binaryToHex(int binary) {
        char ch = ' ';
        switch (binary) {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }

//    public static String hex2Binary(String s) {
//        return new BigInteger(s, 16).toString(2);
//    }

    public static String hex2Binary(String Hex) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Hex.length(); i ++){
            String bin = new BigInteger(Hex.substring(i, i + 1), 16).toString(2);
            while (bin.length() < 4)
                bin = "0" + bin;
            sb.append(bin);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(ImageHelper.hex2Binary("a14aa1dbdb818f9759"));
        System.out.println(ImageHelper.hex2Binary("111111111111111111"));
    }

}
