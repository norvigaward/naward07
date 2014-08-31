
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="style/font.css">
<link rel="stylesheet" href="style/font2.css">
<link rel="stylesheet" href="style/main.css">
<script src="style/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
  $("button").click(function(){
	var $this = $(this);
    $this.toggleClass('SeeMore');
    if($this.hasClass('SeeMore')){
        $this.text('Less...');         
    } else {
        $this.text('More...');
    }
  $("span.hide_first").toggle(1000);
  });
});

$.fn.smartFloat = function() {
    var position = function(element) {
		var top = element.position().top;//, pos = element.css("position");
		var height = element.height();
		//var height = 2000;
		var out_height = $('#article').height();
		// alert(top);
		// alert(height);
		// alert(out_height);
		$(window).scroll(function() {
			var scrolls = $(this).scrollTop();
			height = element.height();
			//alert(scrolls);
			//alert(scrolls - top + height);
			if (scrolls > top && scrolls - top + height <= out_height) {
				if (window.XMLHttpRequest) {
					element.css({
						position: "fixed",
						top: 0
					});
				} else {
					element.css({
						top: scrolls
					});
				}
			} else if(scrolls - top + height > out_height){
				//alert(out_height - height);
				element.css({
					position: "absolute",
					top: out_height - height + top
				});
			} else {
				element.css({
					position: "absolute",
					top: top
				});
			}
		});
    };
    return $(this).each(function() {
		position($(this));
    });
};
	
$(function(){ 
	$("#main_time_frame").smartFloat(); //.caseMenu替换为你要置顶的元素，请注意，此元素恢复到原来的位置时，
	//CSS带有position:absolute,所以得考虑其有一个固定的位置，在其之后的内容不会跑上来。
})
</script>
<title>ImTrends, the trends of images in World Wide Web.</title>
</head>

<?php 
	$filename = "";
	$desc = "";
	$clusters = "";
	if (count($_GET) > 0){
		$filename = $_GET["time"];
		$desc = $_GET["desc"];
		if (count($_GET) >= 3){
			//var_dump($_GET);
			$clusters = $_GET["cluster"];
		}
	} else{
		$filename = "201401";
		$desc = "Jan. 2014";
	}
?>

<body>
<!-- ez-start-2A -->
<div id="wrapper">
  <?php include("header.html"); ?>
  
  
  <div id="body">
    <?php 
    	if ($clusters == "") {
    		include("time3.php");
    	} else {
    		include("cluster.php");
    	}
    	
    ?>
  	<?php include("aside2.php"); ?>
  	<div class=\"clear\"></div>
  </div>
  
  <?php include("footer.html"); ?>
</div>
</body>
</html>
