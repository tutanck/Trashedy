window.onerror = function(msg, url, line, col, error) {
	var fn="snowden: ";
	// Note that col & error are new to the HTML 5 spec and may not be 
	// supported in every browser.  It worked for me in Chrome.
	var extra = !col ? '' : '\ncolumn: ' + col;
	extra += !error ? '' : '\nerror: ' + error;

	if(JZDEBUG)
		clog(fn,"Error: " + msg + "\nurl: " + url + "\nline: " + line + extra);    

	data={};
	data.msg =
		"Snowden said : "+
		"\nError="+msg+
		"\nurl="+url+
		"\nline: " + line + extra+
		"\nJZAPPSERVER="+JZAPPSERVER+
		"\nJZAPPROUTER="+JZAPPROUTER+
		"\nJZDEBUG="+JZDEBUG+
		"\nJZAPPROUTES="+JZAPPROUTES;

	jz_send_ajax_request_("http://localhost:8080/Essais0/snowden",(function(){}),null,data,"post","text",true);

	var suppressErrorAlert = false;
	// If you return true, then error alerts (like in older versions of 
	// Internet Explorer) will be suppressed.
	return suppressErrorAlert;
};
//http://stackoverflow.com/questions/951791/javascript-global-error-handling