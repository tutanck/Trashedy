//hide-show.js

elt = 
	function(idelt) {
	return document.getElementById(idelt);
}

show = 
	function (elt){
	elt.style.visibility="visible";
}

setDisplay = 
	function (elt,display){
	elt.style.display=val;
}

hide = 
	function (elt){
	elt.style.visibility="hidden";
}

hideOnClick = 
	function (elt){
	window.addEventListener("click", function(event) {
		if (event.target == elt) 	
			hide(elt)
	})
}

showOnClick = 
	function (elt){
	window.addEventListener("click", function(event) {
		if (event.target == elt) 	
			show(elt.id)
	})
}


toggleOnClick = 
	function (toShow,toHide){
	var fn="toggleOnClick: ";

	var saa =" is undefined ";
	if(toShow == undefined)		throw fn+"'toShow'"+saa;
	if(toHide == undefined)		throw fn+"'toHide'"+saa;

	stopIfNotArray_(toShow);
	stopIfNotArray_(toHide);

	for (let showi in toShow){
		clog(toShow[showi])
		show(toShow[showi]);
	}
	for (let hidei in toHide){
		clog(toHide[hidei])
		hide(toHide[hidei]);
	}
} 

function stopIfNotArray_(elt){
	if( Object.prototype.toString.call( elt ) !== '[object Array]' )
		throw elt.id+" should be an array";
}