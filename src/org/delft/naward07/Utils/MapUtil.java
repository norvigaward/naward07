package org.delft.naward07.Utils;

/**
 * Created by Feng Wang on 14-7-22.
 */

import java.util.*;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map, boolean dir) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        if (dir)
            Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
                public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });
        else
            Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
                public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                    return -(o1.getValue()).compareTo(o2.getValue());
                }
            });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String map2String(Map map, int num) {
        if (num < 0 || num >= map.size()) {
            num = map.size() + 10;
        }

        String out = "";
        Iterator it = map.entrySet().iterator();

        int iter = 0;
        while (it.hasNext() && iter < num) {
            Map.Entry pairs = (Map.Entry) it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            //out += pairs.getKey();
            //out += "\t";
            out += pairs.getValue().toString();
            out += "\n";
            it.remove(); // avoids a ConcurrentModificationException
            iter++;
        }

        return out;
    }

    public static void outputMap(Map map, int num){
        if (num < 0 || num > map.size()) {
            num = map.size() + 10;
        }

        String out = map2String(map, num);
        System.out.println(out);
    }
}
