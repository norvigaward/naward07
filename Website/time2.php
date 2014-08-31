<?php 
	$filename = "";
	$desc = "";
	if (count($_GET) > 0){
		$filename = $_GET["time"];
		$desc = $_GET["desc"];
	} else{
		$filename = "201408";
		$desc = "Aug. 2014";
	}
?>

<?php
	function readTimeFile($time) {
		$output = null;
		$file = "Data/".$time;
		if (file_exists($file) && is_readable($file)){
			$file_handle = fopen($file, "r");
			
			while (!feof($file_handle)) {
				$line = fgets($file_handle);

				if (strlen($line) < 10)
					continue;
				
				
				$items = explode("\t", $line, 3);
				$img = explode("|", $items[1]);

				echo "<div class=\"gallery\" style=\"height:240px;float:left;\"><img src=\"".$img[6]."\" width=\"300\" height=\"240\"></div>";
				
				echo "<div class=\"img_desc\">";
				
				echo "<p>Popularity: ".$items[0]."</p>";

				echo "<p>Size: ".$img[3]."&#215;".$img[4]."</p>";

				echo "<p><a href=\"".$img[6]."\" target=\"_blank\">Source link</a></p>";
				
				echo "</p>";
				
				echo "</div>";
				
				echo "<div class=\"clear\"></div>";
				
				//echo "<hr>";
			}
			
			fclose($file_handle);
		}
		//return $output;
		//echo "Hello world!";
	}

	//writeMsg();
?>

<div id="article">
	<div id="main_time"><p><?php echo $desc; ?></p><p id="raw"><a href="Data/<?php echo $filename; ?>">[Raw result]</a></p></div>
	<div id="gallery_frame">
		<p>
			<?php 
				//echo $filename; 
				readTimeFile($filename);
			?>
		</p>
	</div>
</div>