countdown =
	function(date,idday,idhour,idmin,idsec){

	//Set the date we're counting down to
	var countDownDate = date.getTime();

	//Update the count down every 1 second
	var x = setInterval(function() {

		//Get todays date and time
		var now = new Date().getTime();

		//Find the distance between now an the count down date
		var distance = countDownDate - now;

		document.getElementById(idday).innerHTML =
			Math.floor(distance / (1000 * 60 * 60 * 24));
		document.getElementById(idhour).innerHTML =
			Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		document.getElementById(idmin).innerHTML =
			Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
		document.getElementById(idsec).innerHTML =
			Math.floor((distance % (1000 * 60)) / 1000);

		//If the count down is over clear interval 
		if (distance < 0)	clearInterval(x);

	}, 1000);
}