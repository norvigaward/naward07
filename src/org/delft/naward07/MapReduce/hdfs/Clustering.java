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
package org.delft.naward07.MapReduce.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Runnable class that uses an existing Kerberos tgt to read a specified warc,
 * wet or wat file from HDFS and dumps the headers for each WarcRecord.
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
public class Clustering implements Runnable {
	private String path;
    private String outPath;

	public Clustering(String path, String outPath) {
		this.path = path;
        this.outPath = outPath;
	}

	@Override
	public void run() {
        System.out.println("test3");
        // PropertyConfigurator.configure("log4jconfig.properties");
		final Configuration conf = new Configuration();
		// The core-site.xml and hdfs-site.xml are cluster specific. If you wish to use this on other clusters adapt the files as needed.
		conf.addResource(Clustering.class.getResourceAsStream("/nl/surfsara/warcexamples/hdfs/resources/core-site.xml"));
		conf.addResource(Clustering.class.getResourceAsStream("/nl/surfsara/warcexamples/hdfs/resources/hdfs-site.xml"));

        System.out.println("test4");

		conf.set("hadoop.security.authentication", "kerberos");
		conf.set("hadoop.security.authorization", "true");

		System.setProperty("java.security.krb5.realm", "CUA.SURFSARA.NL");
		System.setProperty("java.security.krb5.kdc", "kdc.hathi.surfsara.nl");

		UserGroupInformation.setConfiguration(conf);

		UserGroupInformation loginUser;

		try {
			loginUser = UserGroupInformation.getLoginUser();
			System.out.println("Logged in as: " + loginUser.getUserName());
            RunClustering runClustering = new RunClustering(conf, path, outPath);
			loginUser.doAs(runClustering);
		} catch (IOException e) {
			// Just dump the error..
			e.printStackTrace();
		}
	}

}