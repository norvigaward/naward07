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

import nl.surfsara.warcutils.WarcInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

/**
 * Tool implementation that runs a mapreduce job that extracts hrefs from warc files.
 *
 * @author mathijs.kattenberg@surfsara.nl
 */
public class ImTrends extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        Configuration conf = this.getConf();

        Job job = Job.getInstance(conf, "Image Trends");
        job.setJarByClass(ImTrends.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(ImTrendsExtracter.class);
        job.setReducerClass(Reducer.class);
        job.setInputFormatClass(WarcInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //job.setOutputFormatClass(OutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;

    }

}
