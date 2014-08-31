package org.delft.naward07.MapReduce.hadoop.warc;

import org.delft.naward07.Utils.ImageUtils.ImagesInfo;
import org.delft.naward07.Utils.MapUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Feng Wang
 */

class ImTrendsReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    public void reduce(Text pKey, Iterable<Text> pValues, Context pContext)
            throws IOException, InterruptedException {

        Map<String, ImagesInfo> hm = new HashMap<String, ImagesInfo>();
        String s;
        String[] items;
        ImagesInfo ii;

        for (Text t : pValues) {

            s = t.toString();
            items = s.split("\\|");

            //int count = hm.containsKey(items[0]) ? hm.get(items[0]).num : 0;
            ii = hm.get(items[0]);

            if (ii == null) {
                hm.put(items[0], new ImagesInfo(items[0]));
                hm.get(items[0]).updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), /*items[0],*/ items[1], items[5]);
            } else {
                //ii.increment(Integer.parseInt(items[2]), Integer.parseInt(items[3]), items[0], items[1], items[4]);
                ii.increment();
                ii.updateImagelist(Integer.parseInt(items[2]), Integer.parseInt(items[3]), /*items[0],*/ items[1], items[5]);
            }
        }

        System.out.println("hash map size: " + hm.size());

        hm = MapUtil.sortByValue(hm);

        pContext.write(pKey, new Text(MapUtil.map2String(hm, 100)));
    }




}