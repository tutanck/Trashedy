var JEEZ="JEEZ: ";
var JZAPPSERVER;
var JZAPPROUTER;
var JZAPPROUTES;
//true; TODO rem
jz_init();

function jz_init(debug){
	var fn="jz_init: ", router_url;

	clog(fn,"Jeez loading started..");

	if(!JZAPPROUTER)
		throw JEEZ+fn+"jeez config variable 'JZAPPROUTER' is undefined.";

	JZAPPROUTER=jz_unslash(JZAPPROUTER,0);
	clog(fn,"JZAPPROUTER =",JZAPPROUTER)

	if(JZAPPSERVER){
		JZAPPSERVER=jz_slash(JZAPPSERVER,JZAPPSERVER.length-1)
		clog(fn,"JZAPPSERVER =",JZAPPSERVER)
		router_url=JZAPPSERVER+JZAPPROUTER;
	}else 
		router_url=JZAPPROUTER;

	jz_send_ajax_request_(router_url,routes_,null,{},"get","text",false);

	clog(fn,"Jeez loaded and ready for use..");

	function routes_(routes){JZAPPROUTES=JSON.parse(routes);if(debug)clog(JSON.stringify(JZAPPROUTES))} 
}




send("/signup",clog)
function send(url,response_processor,error_manager,data={},async=false){	
	var fn="send_: ";
	if(!jz_configured_()) 
		throw JEEZ+fn+"JEEZ routes not loaded";
	
	var config = JZAPPROUTES[jz_slash(url,0)];
	clog(fn,"route config: ",url,"->",JSON.stringify(config)); 
	
	//TODO param verifs
	
	if(JZAPPSERVER)
		url=JZAPPSERVER+jz_unslash(url,0);
	else
		url=jz_unslash(url,0);

	jz_send_ajax_request_(url,response_processor,error_manager,data,jz_HTTPMethod_(),jz_dataType_(),async);
}



function jz_send_ajax_request_(url,response_processor,error_manager,data={},HTTPMethod="get",dataType="text",async=true){
	var fn="jz_send_ajax_request_: ";
	
	clog(fn,"sending data =",JSON.stringify(data),"to url = ",url+".","HTTPMethod is '"+HTTPMethod+"'.","DataType is '"+dataType+"'.","Async mode is '"+async+"'.");
	
	$.ajax({
		type : HTTPMethod,
		url : url,
		data : data,
		dataType : dataType,
		async:async,
		success : response_processor,
		error : function(XHR, testStatus, errorThrown) {
			clog(fn,"Unable to talk with the server at '"+url+"' " +
					"about '"+JSON.stringify(data)+"' with '"+HTTPMethod+"' method " +
					"in '"+dataType+"' dialect");
			clog("\n"+JSON.stringify(XHR) + " - " + testStatus + " - " + errorThrown); 
			if(error_manager!=undefined && error_manager!=null) error_manager(); 
		}
	});
}


function jz_HTTPMethod_(url){ return JZAPPROUTES["/signup"].httpm}

function jz_dataType_(){return "text"}

function jz_configured_(){return JZAPPROUTES!=undefined && JZAPPROUTES!=null;}

function jz_unslash(s,i){return (s.charAt(i)==="\/")? [s.slice(0,i),"",s.slice(i+1)].join(''):s;}

function jz_slash(s,i){return (s.charAt(i)!=="\/")? [s.slice(0,i+1),"\/",s.slice(i+1)].join(''):s;}

function clog(){
	var str=JEEZ;
	for (let i=0; i < arguments.length; i++) 
		str+=arguments[i]+" ";
	console.log(str);
}