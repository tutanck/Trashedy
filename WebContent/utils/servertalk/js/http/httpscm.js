/*HTTP StatusCode Manager*/
httpscm = function (jqXHR, testStatus, errorThrown){
	switch (jqXHR.status) {
	case 400:toastr.warning("SNO");break; //TODO rem aft bug fix
	case 401:toastr.warning("Merci de d'abord vous authentifier.");break;
	case 403:toastr.warning("Vous n'avez pas l'autorisation d'y accéder.");	break;
	case 404:toastr.warning("Ce que vous cherchez n'existe plus.");break;
	case 503:toastr.error("Ce service est temporairement indisponible.");break;
	default:toastr.error("Une erreur vient de se produire (-_-)");
	}
}