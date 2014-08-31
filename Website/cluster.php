<?php 
	$filename = "";
	$desc = "";
	$cluster = "";
	$linecount = -1;
	if (count($_GET) > 0){
		$filename = $_GET["time"];
		$desc = $_GET["desc"];
		$cluster = $_GET["cluster"];
		if (count($_GET) == 4) {
			$line_count = $_GET["linecount"];
		}
	} else{
		$filename = "201401";
		$desc = "Jan. 2014";
		$cluster = "1";
	}
?>

<?php
	$MAX_IMG = 100;

	function showCluster($time, $cluster, $linecount) {
		$output = null;
		$file = "Data/".$time.".out";

		if ($linecount == -1) {
			$linecount = getLines($file);
		}

		if (file_exists($file) && is_readable($file)){
			$file_handle = fopen($file, "r");
			$i = 1;

			$line = "";
			
			while (!feof($file_handle)) {
				$last_line = $line;
				$line = fgets($file_handle);

				if (strlen($line) < 10)
					continue;

				if ($i == $cluster)
					break;
				
				$i ++;
			}
			
			fclose($file_handle);
		}

		if ($line == "") {
			$line = $last_line;
			$cluster = $i - 1;
		}

		showContent($line, $cluster, $linecount);
		//return $output;
		//echo "Hello world!";
	}

	function showContent($line, $cluster, $linecount){
		$items = explode("\t", $line);

		global $filename;
		global $desc;
		global $MAX_IMG;

		echo "<div class=\"cluster\"><p><span class=\"index\">Cluster ".$cluster." </span>in ".$desc.".<span>&nbsp;&nbsp;&nbsp;Popularity: ".$items[0]."</span></p> <span class=\"navigator\">";

		if ($cluster != 1)
			echo "<span id=\"last\" class=\"on\"><a href=\"index.php?time=".$filename."&desc=".$desc."&cluster=".($cluster - 1)."&linecount=".$linecount."\">&#60;&#60; Last cluster</a></span>";
		else
			echo "<span id=\"last\" class=\"off\">&#60;&#60; Last cluster</span>";
		
		echo "<span id=\"return\"><a href=\"index.php?time=".$filename."&desc=".$desc."&linecount=".$linecount."\">Return to ".$desc."</a></span>";

		if ($cluster != $linecount)
			echo "<span id=\"next\" class=\"on\"><a href=\"index.php?time=".$filename."&desc=".$desc."&cluster=".($cluster + 1)."&linecount=".$linecount."\">Next cluster &#62;&#62;</a></span>";
		else
			echo "<span id=\"next\" class=\"off\">Next cluster &#62;&#62;</span>";

		echo "</span></div>";

		echo "<p id=\"top\">Images in this cluster:</p>";

		for ($i=1; $i < count($items) ; $i++) { 
			# code...
			$img = explode("|", $items[$i]);
			if (count($img) < 5) {
				break;
			}

			echo "<div class=\"gallery\" style=\"height:320px;float:left;\"><img src=\"".$img[5]."\" width=\"400\" height=\"320\"></div>";

			echo "<div class=\"img_desc\">";
				
			//echo "<p class=\"counter\">".$i.".</p>";
			
			//echo "<p>Popularity: ".$items[0]."</p>";

			echo "<p>Size: ".$img[2]."&#215;".$img[3]."</p>";

			echo "<p>Hashcode: ".$img[1]."</p>";

			echo "<p><a href=\"".$img[5]."\" target=\"_blank\">Source link</a></p>";
			
			//echo "</p>";
			
			echo "</div>";
			
			echo "<div class=\"clear\"></div>";

			if ($i >= $MAX_IMG) {
				break;
			}
			
		}
	}

	function getLines($file)
	{
	    $f = fopen($file, 'rb');
	    $lines = 0;

	    global $MAX_IMG;

	    while (!feof($f)) {
	        $lines += substr_count(fread($f, 8192), "\n");
	        if ($lines > $MAX_IMG) {
	        	return $MAX_IMG;
	        }
	    }

	    fclose($f);

	    return $lines;
	}

?>

<div id="article">
	<!--<div id="main_time"><p><?php echo $desc; ?></p><p id="raw"><a href="Data/<?php echo $filename; ?>">[Raw result]</a></p></div>-->
	<div id="gallery_frame">
		<p>
			<?php 
				//echo $filename; 
				showCluster($filename, $cluster, $linecount);
			?>
		</p>
	</div>
</div>