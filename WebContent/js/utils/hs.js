//hide-show.js

elt = 
	function(idelt) {
	return document.getElementById(idelt);
}

show= 
	function (elt){
	elt.style.display='block'
}

hide = 
	function (elt){
	elt.style.display='none'
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