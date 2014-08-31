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
				
				echo "<table>";
				
				$items = explode("\t", $line, 3);
				$img = explode("|", $items[1]);

				echo "<tr>";
				echo "<td rowspan=\"3\"><div class=\"gallery\" style=\"height:200px;width:200px;float:left;\"><img src=\"".$img[6]."\" width=\"200\" height=\"200\"></div></td>";
				echo "<td>Popularity: ".$items[0]."</td>";
				echo "</tr>";

				echo "<tr>";
				echo "<td>Size: ".$img[3]."&#215;".$img[4]."</td>";
				echo "</tr>";

				echo "<tr>";
				echo "<td><a href=\"".$img[6]."\">url</a></td>";
				echo "</tr>";
				
				echo "<tr>";
				echo "<td> </td>";
				echo "</tr>";
				
				echo "<tr>";
				echo "<td> </td>";
				echo "</tr>";
				
				echo "</table>";
				
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
				<div id="main_time"><p><?php echo $desc; ?></p></div>
				<!-- <p>201403|fffcf88080c8f8ff|00000b780b300|98|98|20140317|http://imgv2-3.scribdassets.com/img/word_user/75414463/98x98/ec1f1ba7d4/1340822774 201403|fffcf88080c8f8ff|00000b780b300|98|98|20140317|http://imgv2-3.scribdassets.com/img/word_user/75414463/98x98/ec1f1ba7d4/1340822774 12 200310|c0f4c700c38bffff|344b4480fc330|1155|850|20031006|http://www.theroyalforums.com/forums/attachment.php?attachmentid=19163&d=1065393846 1 200310|f8800330bfbf87ff|43c0c48f43fc0|590|349|20031017|http://www.theroyalforums.com/forums/attachment.php?attachmentid=21977&d=1066411068 </p> -->
				<div id="gallery_frame">
					<p>
						<?php 
							//echo $filename; 
							readTimeFile($filename);
						?>
					</p>
				</div>
      </div>