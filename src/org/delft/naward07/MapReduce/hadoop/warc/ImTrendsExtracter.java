package org.delft.naward07.MapReduce.hadoop.warc;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.jwat.common.HttpHeader;
import org.jwat.common.Payload;
import org.jwat.warc.WarcRecord;

/**
 * @author Feng Wang
 */
class ImTrendsExtracter extends Mapper<LongWritable, WarcRecord, Text, Text> {
    private Text k = new Text();

    private static enum Counters {
        CURRENT_RECORD, NUM_HTTP_RESPONSE_RECORDS
    }

    @Override
    public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
        context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());

        //Record2Hashcode r1 = new Record2Hashcode();

        String hc = Record2Hashcode.getHashcode(value);
        if (!"".equals(hc) && hc != null) {
            String[] items = hc.split("\\|");//.substring(0, 3);
            //Text k = new Text(items[4]);
            k.set(items[4].substring(0, 6));
            context.getCounter(Counters.NUM_HTTP_RESPONSE_RECORDS).increment(1);
            context.write(k, new Text(hc));
        }
    }

}
