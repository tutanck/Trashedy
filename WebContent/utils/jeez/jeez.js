var JEEZ="JEEZ: ";
var JZAPPSERVER;
var JZAPPROUTER;
var JZAPPROUTES;
var JZDEBUG;

(function jz_init(debug){
	var fn="jz_init: ", router_url;

	clog(fn,"Jeez loading..");

	if(!JZAPPROUTER)
		throw JEEZ+fn+"jeez config variable 'JZAPPROUTER' is undefined.";

	JZAPPROUTER=jz_unslash(JZAPPROUTER,0);
	clog(fn,"JZAPPROUTER =",JZAPPROUTER)

	if(JZAPPSERVER){
		JZAPPSERVER=jz_slash_af(JZAPPSERVER,JZAPPSERVER.length-1)
		clog(fn,"JZAPPSERVER =",JZAPPSERVER)
		router_url=JZAPPSERVER+JZAPPROUTER;
	}else 
		router_url=JZAPPROUTER;	 

	jz_send_ajax_request_(router_url,routes_,default_on_jz_load_failure,{},"get","text",false);

	 function routes_ (routes){
		clog(fn,"Jeez loaded and ready for use..");
		JZAPPROUTES=JSON.parse(routes);
		if(debug)
			clog(JSON.stringify(JZAPPROUTES))
	} 

	function default_on_jz_load_failure (){clog(fn,"Jeez failed to load");}

})(JZDEBUG);



/**Extras*/

function check (url,field,callback=educate,messages){
	var fn="check: ";

	if( isFalsy(field) || isFalsy(field.name) )
		throw JEEZ+fn+"Input to check is not defined";

	var _def=find_param_def_(config_(url),field.name);

	if(!_def)
		throw JEEZ+fn+"Incoming parameter '"+field.name+"' is undefined for url '"+url+"'";

	if(isEmpty(field.value)) return clear(field);

	var fields=[]; fields.push(field);

	try{
		valid_(_def,field.value,_def.expected);
	}catch(e){
		if(e instanceof JEEZInvalidParameterException)
			return callback(fields,state_(1,field,e.invalidity,e),messages,url);
		else throw e;
	}
	callback(fields,state_(),messages,url);
}


function warn(state,messages){//	TODO msg 
	var fn="warn: ";
	clog(fn,"state:",JSON.stringify(state));	

	if(state.status) {
		var field = state.field;

		clog(fn,"error:",state.e+".","guilty_field:",field.name);

		var invalidmsg=" field is invalid";

		if(!document.getElementById("jzTag"+field.id))
			printDivAfter(field.id,"<center><font color='red'> "+field.name +invalidmsg+"</font></center>");
		else
			printHTML("#jzTag"+field.id,"<center><font color='red'> "+field.name +invalidmsg+"</font></center>");
	}
}

function clear(field){
	var fn="clear: ";
	clog(fn,"clearing:",field.name);
	if(document.getElementById("jzTag"+field.id))
		printHTML("#jzTag"+field.id,"");
}

function clear_all(fields){
	var fn="clear_all: ";

	if( Object.prototype.toString.call( fields ) !== '[object Array]' )
		throw JEEZ+fn+"fields must be an array of inputs";

	for(let fi in fields) clear(fields[fi]);
}


function educate(fields,state,messages){ state.status ? warn(state,messages) : clear_all(fields); }

connect = 
	function (url,ajax_response_processor,ajax_error_manager,fields=[],immediate_callback=educate,async=true){
	var fn="connect: ";
	var index = new Map();
	var data={};

	if( Object.prototype.toString.call( fields ) !== '[object Array]' )
		throw JEEZ+fn+"fields must be an array of inputs";

	for(let field of new Set(fields)){
		if( isEmpty(field) || isEmpty(field.name) ){
			clog(fn,"Warning: Array 'fields' contains an empty field");
			continue;
		}

		if(isEmpty(field.id))
			throw fn+"field "+field.name+" must have an valid 'id' attribute";

		index.set(field.name,field);
		data[field.name]=field.value;
		clear(field);
	}

	clog(fn,"data = '"+JSON.stringify(data)+"'");

	try{
		requst(url,ajax_response_processor,ajax_error_manager,data);
	}catch(e){
		if(e instanceof JEEZInvalidParameterException)
			if(e.invalidity===-1 && isFalsy(index.get(e.fparamName)))
				throw e; 
			else
				return immediate_callback(fields,state_(1,index.get(e.fparamName),e.invalidity,e));
		else throw e;
	}
	immediate_callback(fields,state_());
}


function soft_connect(url,ajax_response_processor,ajax_error_manager,field,immediate_callback=educate,async=true){
	var fn ="soft_connect: ";
	
	
	if( isEmpty(field) || isEmpty(field.name) )
		throw fn+"'field' parameter must be defined and have an valid 'name' attribute";

	if(isEmpty(field.id))
		throw fn+"field '"+field.name+"' must have an valid 'id' attribute";
	
	if(isEmpty(field.value)) return clear(field);
	
	var fields =[];
	fields.push(field);
	
	connect(url,ajax_response_processor,ajax_error_manager,fields,immediate_callback,async);
}




/**JEEZ*/
function requst(url,ajax_response_processor,ajax_error_manager,data={},async=true){	
	var fn="requst: ";

	if(!jz_configured_()) 
		throw JEEZ+fn+"JEEZ routes not loaded";

	data = isStrongEmpty(data)? {}:data;

	var config=config_(url);
	if(!config) 
		throw JEEZ+fn+"No route found on server for url '"+url+"'";

	clog(fn,"route config: ",url,"->",JSON.stringify(config));

	_check_params(config.expin,true);
	_check_params(config.optin);

	jz_send_ajax_request_(url_(url),ajax_response_processor,ajax_error_manager,data,config.httpm,"text",async);

	function _check_params (params,expected=false){ 
		for(let pi in params){
			let fparam = params[pi]; //formal param def
			valid_(fparam,data[fparam.name],expected); 
		}
	};
}


function valid_(formal,effval,expected){
	var fn="valid_: ";

	if(isFalsy(formal))
		throw JEEZ+fn+"Empty formal parameter definition.";

	clog (fn,"formal =",JSON.stringify(formal));

	let fname=formal.name;
	let frules=formal.rules;

	if(isEmpty(effval))
		if(expected) 
			throw new JEEZInvalidParameterException(fname,-1,JEEZ+fn+"Missing expected parameter '"+fname+"'");
		else return;

	if(getJSFType(formal.type) !== typeof effval)
		throw new JEEZInvalidParameterException(fname,0,JEEZ+fn+"Incompatible type for parameter '"+fname+"', expected was "+ftype);

	for(let ri in frules){
		let frule = frules[ri];
		if(!new RegExp("\\b"+frule+"\\b").test(effval))
			throw new JEEZInvalidParameterException(fname,1,JEEZ+fn+"Parameter '"+fname+"' does not match the rule '"+frule+"'");
	}

	//TODO USE parse iof
	function getJSFType(intType){// Get JavaScript Formal Type  
		switch (intType) { 
		case 0: return "string"; 
		case 1:	case 2:	case 3:	case 4: return "number";
		case 5: return "boolean";	
		default: throw JEEZ+fn+"JEEZError#SNO : InternalTypingError"; 
		} 
	}
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
		error : function(jqXHR, testStatus, errorThrown) {
			clog(fn,"Unable to talk with the server at '"+url+"' " +
					"about '"+JSON.stringify(data)+"' with '"+HTTPMethod+"' method " +
					"in '"+dataType+"' dialect");
			clog("\n"+JSON.stringify(jqXHR) + " - " + testStatus + " - " + errorThrown); 
			if(!isEmpty(error_manager)) error_manager(jqXHR, testStatus, errorThrown); 
		}
	});
}



function jz_configured_(){return !isEmpty(JZAPPROUTES);}

function config_(url){ return JZAPPROUTES[jz_slash_bf(url,0)]; }

function url_(url){ return JZAPPSERVER ? JZAPPSERVER+jz_unslash(url,0) : jz_unslash(url,0); }

function find_param_def_(config,fname){ 
	var fn="find_param_def_: ";

	clog(fn+"fname:"+fname,", config: "+JSON.stringify(config));

	var found={};
	var matrix=[];
	matrix.push(config.expin);
	matrix.push(config.optin);

	for(let i in matrix){
		let array = matrix[i];
		for(let pi in array)
			if(array[pi].name===fname){
				let param = array[pi];
				if(i==0)
					found["expected"]=true;
				else
					if(i==1)
						found["expected"]=false;
					else throw JEEZ+fn+"JEEZError#SNO : unable to prior param def";

				found.name=param.name;
				found.type=param.type;
				found.rules=param.rules;
				return found;
			}
	}
}

function state_(status=0,field,cause,e){ return {"status":status,"field":field,"cause":cause,"e":e}; }

function jz_unslash(s,i){return (s.charAt(i)==="\/")? [s.slice(0,i),"",s.slice(i+1)].join(''):s;}

function jz_slash_bf(s,i){return (s.charAt(i)!=="\/")? [s.slice(0,i),"\/",s.slice(i)].join(''):s;}

function jz_slash_af(s,i){return (s.charAt(i)!=="\/")? [s.slice(0,i+1),"\/",s.slice(i+1)].join(''):s;}


function clog(){
	var str=JEEZ;
	for (let i=0; i < arguments.length; i++) 
		str+=arguments[i]+" ";
	console.log(str);
}

function isStrongFalsy (value){ 
	return value === undefined 
	|| value === null
}

function isFalsy(value){ 
	return value == undefined 
	|| value == null
}

function isEmpty(value){ 
	return isFalsy(value)
	|| new RegExp('^\\s*$').test(value)
	|| value == "null"
}

function isStrongEmpty(value){ 
	return isStrongFalsy(value)
	|| new RegExp('^\\s*$').test(value)
	|| value == "null"
}

function JEEZInvalidParameterException(fparamName,invalidity,message) {
	this.name = "JEEZInvalidParameterException";
	this.fparamName=fparamName;
	this.invalidity = invalidity; //-1:missing , 0:typing , 1:ruling
	this.message = (message || "");
}
JEEZInvalidParameterException.prototype = new Error();

function printDivAfter(dom,val){ $("#"+dom).after("<div id=\"jzTag"+dom+"\" class=\"warning-wrapper\">"+val+"</div>\n"); }

function printHTML(dom,htm){ $(dom).html(htm); }