/**
 * 
 */
$(function(){
	$("#sure").bind("click",function(){
		$.ajax({
			type:"post",
			url:"/classTool/getFields",
			data:{"className": $("#className").val()},
			dataType: "json",
			success:function(data){
				if(data.length > 0 && data[0] == "classNotFound")
					alert("error");
				else{
					createList(data);
				}
			}
		});
	});

	$("#getAll").bind("click", function(){
		$.ajax({
			type: "POST",
			url: "/classTool/getAllfieldsMap",
			data:{className: $("#className").val()},
			dataType: "json",
			success:function(data){
				if(data.classNotFound != null && data.classNotFound != undefined){
					alert("error");
				}else
					createMap(data);
			}
		});
	});
	
	$("#getClassInfomation").bind("click",function(){
		$.ajax({
			type: "post",
			url: "getAll",
			data:{"className":$("#className").val()},
			dataType: "json",
			success:function(data){
				if(data.classNotFound != null && data.classNotFound != undefined){
					alert("error");
				}else
					createAllMap(data);
			}
		});
	});
});

function createList(data){
	var $field = $("#field");
	var fields = new Array();
	for(var i = 0;i < data.length;i++){
		fields.push("<li>"+data[i]+"</li>");
	}
	var ul = $("<ul></ul>");
	ul.append(fields.join(""));
	$field.empty().append(ul);
}

function createMap(data){
	var $field = $("#field");
	var fields = new Array();
	for(x in data){
		fields.push("<li>"+x+"<ul>");
		for(var i = 0;i < data[x].length;i++){
			fields.push("<li>"+data[x][i]+"</li>");
		}
		fields.push("</ul></li>");
	}
	$field.empty().append("<ul>" + fields.join("") + "</ul>");
}

function createAllMap(data){
	var $field = $("#field");
	var fields = new Array();
	for(x in data){
		fields.push("<li>" + x + "<ul>");
		for( i in data[x]){
			fields.push("<li>"+i+"<ul>");
			for(var j = 0;j < data[x][i].length;j++){
				fields.push("<li>" + data[x][i][j] + "</li>");
			}
			fields.push("</ul></li>")
		}
		fields.push("</ul></li>");
	}
	$field.empty().append("<ul>" + fields.join("") + "</ul>");
}