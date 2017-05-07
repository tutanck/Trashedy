openWaiter = 
	function (dark){
	var modal ="<div id='loaderWait' class='jloader'></div>";
	var darkContainer="<div id='modalWait' class='jmodal'>"+modal+"</div>";
	var clearContainer="<div id='modalWait'>"+modal+"</div>";
	var modalContainer = dark?darkContainer:clearContainer;

	if(elt('modalWait') != undefined)
		printHTML('#modalWait',modal);
	else
		printHTMLSup('body',modalContainer);

	show(elt('modalWait'));
}

closeWaiter =
	function(){hide(elt('modalWait'));}


/**later-> http://stackoverflow.com/questions/574944/how-to-load-up-css-files-using-javascript
//https://www.google.fr/search?newwindow=1&q=dynamically+include+css&spell=1&sa=X&ved=0ahUKEwjG3MSf2djRAhWE7hoKHaETBM4QvwUIGSgA&biw=1449&bih=768*/