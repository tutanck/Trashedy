function Gate(fields,form,url,dataType,response_processor){
	//alert('Gate:');
	if(KFC(fields))
		connect(fields,form,url,dataType,response_processor);	
}

/**
 * -Stringify the form's fields into an url's request
 * -call the send_ajax_request with provided parameters and home made url's request
 * @param fields
 * @param form
 * @param url
 * @param dataType
 * @param response_processor */
function connect(fields,form,url,dataType,response_processor){
	var request="";
	for(var i=0; i<fields.length;i++)
		if(i==fields.length-1)
			request+=fields[i].name+"="+fields[i].value;
		else
			request+=fields[i].name+"="+fields[i].value+"&";
	//alert("connect->"+request);
	send_ajax_request_(form.method, url, request, dataType, response_processor)
}

/**
 * KeybasedFormChecking
 * @param fields
 * @returns {Boolean}
 */
function KFC(fields){
	//alert('kfc');
	approval=true;
	for(var i in fields)
		if(fields[i].value.trim().length==0){
			approval=false;
			if(!document.getElementById("smarttag"+fields[i].id))
				printDivAfter(fields[i].id,"<center><font color='red'>Le champ "+fields[i].name +" est obligatoire.</font></center>");
			else
				printHTML("#smarttag"+fields[i].id,"<center><font color='red'>Le champ "+fields[i].name +" est obligatoire.</font></center>");
		}else 
			if(document.getElementById("smarttag"+fields[i].id))
				printHTML("#smarttag"+fields[i].id,"");
	
	/**less generic - but still generic : */
	if(fields[i].hasAttribute("twin")) 
		alert("yo");
	//TODO get twins and check they have same value
	
	
	
	return approval;
}