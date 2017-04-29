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