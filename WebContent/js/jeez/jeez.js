var JEEZ="JEEZ: ";
var JZAPPSERVER;
var JZAPPROUTER;
var JZAPPROUTES;

(function jz_init(debug){
	var fn="jz_init: ", router_url;

	clog(fn,"Jeez loading started..");

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

	jz_send_ajax_request_(router_url,routes_,null,{},"get","text",false);

	clog(fn,"Jeez loaded and ready for use..");

	function routes_(routes){JZAPPROUTES=JSON.parse(routes);if(debug)clog(JSON.stringify(JZAPPROUTES))} 
})();//true ->debug


function check(url,field){
	var fn="check: ";

	if( isFalsy(field) || isFalsy(field.name) )
		throw JEEZ+fn+"Input to check is not defined";

	var _def=find_param_def_(config_(url),field.name);

	if(!_def)
		throw JEEZ+fn+"Input to check is undefined on server";

	try{
		valid_(_def,field.value,_def.expected);
	}catch(e){
		if(e instanceof JEEZInvalidParameterException)
			return status(1,field,e.invalidity);
	}
	return status_();
}

connect("signup",clog,["f"])
function connect(url,response_processor,fields,error_manager,async=true){
	var fn="connect: ";
	var index = new Map();
	var data={};

	for(let field of new Set(fields)){
		if( isEmpty(field) || isEmpty(field.name) || isEmpty(field.value) ){
			clog(fn,"Warning: Array 'fields' contains an empty field");
			continue;
		}
		index.set(field.name,field);
		data[field.name]=field.value;
	}

	clog(fn,"data = '"+JSON.stringify(data)+"'");

	try{
		requst(url,response_processor,error_manager,data);
	}catch(e){
		if(e instanceof JEEZInvalidParameterException)
			return status(1,index.get(e.fparamName),e.invalidity);
	}
	return status_();
}


function requst(url,response_processor,data={},error_manager,async=true){	
	var fn="requst: ";
	
	if(!jz_configured_()) 
		throw JEEZ+fn+"JEEZ routes not loaded";

	var config=config_(url);
	clog(fn,"route config: ",url,"->",JSON.stringify(config));

	_check_params(config.expin,true);
	_check_params(config.optin);

	jz_send_ajax_request_(url_(url),response_processor,error_manager,data,config.httpm,"text",async);

	
	function _check_params(params,expected=false){ 
		for(let pi in params){
			let fparam = params[pi]; //formal param def
			valid_(fparam,data[fparam.name],expected); 
		}
	}
	
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


	/**
	 * Get JavaScript Formal Type */
	function getJSFType(intType){ 
		switch (intType) { 
		case 0:return "string"; 
		case 1:return "number";
		case 2:return "number";
		case 3:return "number"; 
		case 4:return "number"; case 5:return "boolean";	
		default:throw JEEZ+fn+"JEEZError#SNO : InternalTypingError"; 
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
		error : function(XHR, testStatus, errorThrown) {
			clog(fn,"Unable to talk with the server at '"+url+"' " +
					"about '"+JSON.stringify(data)+"' with '"+HTTPMethod+"' method " +
					"in '"+dataType+"' dialect");
			clog("\n"+JSON.stringify(XHR) + " - " + testStatus + " - " + errorThrown); 
			if(!isEmpty(error_manager)) error_manager(XHR, testStatus, errorThrown); 
		}
	});
}

/*GLOBAL TOOLS*/

function jz_configured_(){return !isEmpty(JZAPPROUTES);}

function config_(url){ return JZAPPROUTES[jz_slash_bf(url,0)]; }

function find_param_def_(config,fname){ 
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
					else throw "JEEZError : unable to prior param def"
					found.name=param.name;
				found.type=param.type;
				found.rules=param.rules;
				return found;
			}
	}
}

function status_(status=0,field,cause){ return {"status":status,"field":field,"cause":cause}; }

function url_(url){ return JZAPPSERVER ? JZAPPSERVER+jz_unslash(url,0) : jz_unslash(url,0); }

function jz_unslash(s,i){return (s.charAt(i)==="\/")? [s.slice(0,i),"",s.slice(i+1)].join(''):s;}

function jz_slash_bf(s,i){return (s.charAt(i)!=="\/")? [s.slice(0,i),"\/",s.slice(i)].join(''):s;}

function jz_slash_af(s,i){return (s.charAt(i)!=="\/")? [s.slice(0,i+1),"\/",s.slice(i+1)].join(''):s;}


function clog(){
	var str=JEEZ;
	for (let i=0; i < arguments.length; i++) 
		str+=arguments[i]+" ";
	console.log(str);
}

function isFalsy(value){ 
	return value == undefined 
	|| value == null
}

function isEmpty(value){ 
	return value == undefined 
	|| value == null
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