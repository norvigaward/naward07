/**
 * Copyright 2014 SURFsara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Map function that from a WarcRecord extracts all links. The resulting key,
 * values: page URL, link.
 *
 * @author mathijs.kattenberg@surfsara.nl
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
