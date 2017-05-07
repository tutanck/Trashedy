ON_JZ_LOAD_FAILURE = function(XHR, testStatus, errorThrown){
	var fn ="ON_JZ_LOAD_FAILURE:";
	clog(fn,"Jeez failed to load");
	data={};
	
	data.msg =
	"XHR="+"\n"+JSON.stringify(XHR)+
	"\ntestStatus="+ testStatus+
	"\nerrorThrown=" + errorThrown+
	"\nJZAPPSERVER="+JZAPPSERVER+
	"\nJZAPPROUTER="+JZAPPROUTER+
	"\nJZDEBUG="+JZDEBUG+
	"\nJZAPPROUTES="+JZAPPROUTES;
	
	jz_send_ajax_request_("http://localhost:8080/Essais0/alertme",chill,null,data,"post","text",true);
}

chill = function (){}