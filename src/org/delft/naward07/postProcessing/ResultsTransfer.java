package org.delft.naward07.postProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.delft.naward07.Utils.MapUtil;

/**
 * @author Feng Wang
 */

public class ResultsTransfer {
	
	private final String INDEX = "index";
	private final String RESULT = "results_C_clustering";
	private final String OUTPATH = "ranked_clusters";
	
	public void resultsTransfer(String path){
		path = checkPathEnd(path);
		
		File folder = new File(path + RESULT);
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles == null) {
			throw new NullPointerException("Wrong path.");
		}
		
		File outputPath = new File(path + OUTPATH);
		outputPath.mkdirs();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		        
		        System.out.println(path + "\\" + INDEX + file.getName().split("\\.")[0] + "." + INDEX);
		        
		        File correspondIndex = new File(path + "\\" + INDEX + "\\" + file.getName().split("\\.")[0] + "." + INDEX);
		        
		        try {
					singleTrnasfer(path, file.getName().split("\\.")[0], file, correspondIndex);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		    } else {
				System.out.println("Not a file.");
			}
		}
	}

	private void singleTrnasfer(String path, String name, File rawOutput, File index) throws Exception {
		// TODO Auto-generated method stub
		
		File output = new File(path + "\\" + OUTPATH + "\\" + name + ".out");
		output.getParentFile().mkdirs();
		output.createNewFile();
		
		BufferedWriter bwOut = new BufferedWriter(new FileWriter(output));
		
		BufferedReader brRaw = new BufferedReader(new FileReader(rawOutput));
		BufferedReader brIndex = new BufferedReader(new FileReader(index));
		
		Map<Integer, ItemIndices> hmItem = new HashMap<Integer, ItemIndices>();
		//Map<Integer, HashcodeIndices> hmHashcodes = new HashMap<Integer, HashcodeIndices>();
		List<String> hashcodes = new ArrayList<String>();
		
		String line = brRaw.readLine();
		
		if (line == null || line.equals("")) {
			throw new Exception("Wrong line");
		}
		
		String[] items = line.split(",");
		
		ItemIndices ii;
		
		for (int i = 0; i < items.length; i++) {
			int clusterID = Integer.parseInt(items[i]);
			ii = hmItem.get(clusterID);
			
			if (ii == null) {
				ii = new ItemIndices();
				ii.update(i);
				
				hmItem.put(clusterID, ii);
			} else {
				ii.update(i);
				hmItem.put(clusterID, ii);
			}
		}
		
		brRaw.close();
		hmItem = MapUtil.sortByValue(hmItem);
		
		
		line = brIndex.readLine();
		while(line != null){
			if (line.equals("")) {
				line = brIndex.readLine();
				continue;
			}
			
			items = line.split("\\t");
			
			hashcodes.add(items[1]);
			
			line = brIndex.readLine();
		}
		
		brIndex.close();
		
		Iterator iterator = hmItem.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry pairs = (Map.Entry) iterator.next();
			ItemIndices temp = (ItemIndices) pairs.getValue();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(temp.getNum());
			sb.append("\t");
			
			for(Integer i: temp.getIndices()){
				sb.append(hashcodes.get(i));
				sb.append("\t");
			}
			
			sb.append("\n");
			bwOut.write(sb.toString());
			
			iterator.remove();
		}
		
		bwOut.flush();
		bwOut.close();
		
	}

	private String checkPathEnd(String path) {
		
		if (!path.endsWith("/") && !path.endsWith("\\")) {
			path = path.concat("\\");
		}
		
		return path;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResultsTransfer rt = new ResultsTransfer();
		rt.resultsTransfer("C:\\Users\\D062988\\Documents\\DS\\clustering008");
	}

}
