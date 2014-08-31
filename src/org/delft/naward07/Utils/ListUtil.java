package org.delft.naward07.Utils;

import java.util.List;

/**
 * Created by Feng Wang on 14-8-25.
 */

public class ListUtil {
    public static String list2String(List<String> list, String delimiter){
        StringBuilder sb = new StringBuilder();

        String loopDelimiter = "";

        for(String s : list){
            sb.append(loopDelimiter);
            sb.append(s);

            loopDelimiter = delimiter;
        }

        return sb.toString();
    }
}
