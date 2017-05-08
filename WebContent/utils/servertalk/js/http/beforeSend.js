beforeSend =
	function (jqXHR, settings){
	 jqXHR.url = settings.url;
	 jqXHR.settings = settings;
	 openWaiter();
}