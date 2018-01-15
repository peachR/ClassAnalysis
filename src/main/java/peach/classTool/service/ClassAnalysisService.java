package peach.classTool.service;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import peach.classTool.tool.classTool.ClassAnalysis;

@Service
public class ClassAnalysisService {
	
	public List<String> getObjField(String obj, Integer ...mod){
		try{
			Class<?> cl = Class.forName(obj);
			ClassAnalysis<?> cler = ClassAnalysis.newInstance(cl);
			if(mod.length == 0)
				mod = new Integer[]{Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED};
			return cler.getSimpleFields(cler::getAllFieldList,mod);
		}catch(Exception e){
			return Arrays.asList("classNotFound");
		}
	}
	
	public Map<String,List<String>> getObjAllFieldMap(String obj){
		try{
			Class<?> cl = Class.forName(obj);
			ClassAnalysis<?> analysis = ClassAnalysis.newInstance(cl);
			return analysis.getAllFieldMap();
		}catch(Exception e){
			Map<String,List<String>> map = new HashMap<>();
			map.put("classNotFound", Arrays.asList("classNotFound"));
			return map;
		}
	}
	
	public Map<String,Map<String,? extends Collection<String>>> getClassAllInformation(String obj){
		try{
			Class<?> cl = Class.forName(obj);
			ClassAnalysis<?> analysis = ClassAnalysis.newInstance(cl);
			Map<String,Map<String,? extends Collection<String>>> map = new LinkedHashMap<>();
			map.put("field", analysis.getAllFieldMap());
			map.put("method", analysis.getAllMethodMap());
			map.put("public", analysis.getAllPubilcMethodMap());
			return map;
		}catch(Exception e){
			Map<String,Map<String,? extends Collection<String>>> map = new HashMap<>();
			map.put("classNotFound", new HashMap<>());
			return map;
		}
	}
}
