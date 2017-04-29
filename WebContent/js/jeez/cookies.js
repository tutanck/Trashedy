/**
 *Cookies manager */

function createCookie (name,value,days) {
	if (days) {
		var date = new Date ();
		date.setTime (date.getTime() + (days *24*60*60*1000) ) ;
		var expires = " ; expires ="+ date.toGMTString () ;
	}else 
		var expires = " " ;
	document.cookie = name+"="+ value+expires +" ; path = / " ;
}

function readCookie (name) {
	var nameEQ = name + "=" ;
	var nameEQ2 = name + " = " ;
	var ca = document.cookie.split(';') ;
	//alert("ca: "+ca);
	for( var i =0; i < ca.length ; i ++) {
		var c = ca[i] ;
		while (c.charAt(0)==' ') c= c.substring(1 , c.length ) ;
		//alert("c : "+c);
		if(c.indexOf(nameEQ) == 0)
			return c.substring(nameEQ.length,c.length) ;
		else if (c.indexOf(nameEQ2) == 0)
			return c.substring(nameEQ.length,c.length) ;
	}
	return null;
}

function eraseCookie (name) {createCookie (name," ",-1 ) ;}