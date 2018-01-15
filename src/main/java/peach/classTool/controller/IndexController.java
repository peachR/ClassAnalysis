package peach.classTool.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import peach.classTool.service.ClassAnalysisService;

@Controller
@RequestMapping(value="")
public class IndexController {
	@Autowired
	private ClassAnalysisService classService;
	
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String toIndex(){
		return "index";
	}
	
	@RequestMapping(value="/getFields", method=RequestMethod.POST)
	@ResponseBody
	public List<String> handlerGetFieldsAjax(@RequestParam(value="className") String className){
		return classService.getObjField(className);
	}
	
	@RequestMapping(value="/getAllfieldsMap", method=RequestMethod.POST)
	@ResponseBody
	public Map<String,List<String>> handlerGetAllFieldsMapAjax(@RequestParam(value="className") String className){
		return classService.getObjAllFieldMap(className);
	}
	
	@RequestMapping(value="/getAll", method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Map<String,? extends Collection<String>>> handlerGetAllAjax(@RequestParam(value="className") String className){
		return classService.getClassAllInformation(className);
	}
}
