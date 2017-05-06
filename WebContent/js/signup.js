hideOnClick(elt('signup-modal'))

window.addEventListener("click", function(event) {
	if (event.target == elt('signup-modal')){ 	
		show(elt('signup-goal-btn'));
		show(elt('signin-goal-btn'));
	}
})