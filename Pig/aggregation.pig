/* by Hong Huang */
dt = load '/user/naward07/main_output019' using PigStorage('\t') AS (period:int,Hash:chararray);
dump dt;
tt = foreach dt{
str = STRSPLIT($1,'\\|');
generate $0,FLATTEN(str);
};
/*dump tt;*/

d = foreach tt{
generate  $0 AS period,$1 AS Hash,$2 AS pHash,$3 AS height,$4 AS width,$5 AS time,$6 AS url;
};
DESCRIBE d;
/*load the data*/
/*
d = load '$input' using PigStorage('|') AS (period:int,Hash:chararray,pHash:chararray,height:int,width:int,time:chararray,url:chararray); 
*/
/*DUMP d;*/

/*count the occurrence of each pattern in each period*/
b = group d by (period,Hash);
/*DUMP b;*/
E = foreach b generate group,COUNT(d) AS count;
/*DUMP E;*/

/*generate the top n pattern in each period*/
flat = foreach E generate FLATTEN($0),$1;
/*DUMP flat;*/
grid = group flat by period;
/*DUMP grid;*/
DESCRIBE flat;
t = foreach grid{
	sorted = order flat by count desc;
	top = limit sorted 2;
	generate flatten(top); 
};
DUMP t;

/*join with original table*/
table = JOIN t BY (period,Hash), d BY (period,Hash);
DUMP table;

/*get the necessary fields*/
preorder = foreach table{
	generate $2,$3,$4,$5,$6,$7,$8,$9;
};
DUMP preorder;

/*order the results*/
result = ORDER preorder BY period DESC,count DESC;
DUMP result;

/*store the results*/
store result into '/user/naward07/re';