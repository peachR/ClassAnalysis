package peach.classTool.tool.classTool;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClassAnalysises {
	public static Map<String, List<Method>> groupingMethods(Class<?> cl) {
		return groupingMethods(Optional.ofNullable(cl));
	}

	public static Map<String, List<Method>> groupingMethods(Optional<Class<?>> cl) {
		return getClassMethod(cl).stream()
				.collect(Collectors.groupingBy(method -> Modifier.toString(method.getModifiers())));
	}
	
	/**
	 * 返回给定类型对象的所有方法的集合
	 * 
	 * @param cl
	 *            需要获取所有方法集合的类型对象
	 * @return cl类的所有方法的集合
	 */
	public static Set<Method> getClassMethod(Optional<Class<?>> cl) {
		return (Set<Method>) getClassMethod(HashSet::new,cl);
	}
	
	public static <T extends Collection<Method>> T getClassMethod(Supplier<T> supplier, Optional<Class<?>> cl){
		T result = supplier.get();
		Collections.addAll(result,cl.map(t -> t.getDeclaredMethods()).orElse(new Method[] {}));
		return result;
	}
	
	public static List<String> changeMemberToString(Collection<? extends Member> list){
		return list.stream().map(m -> removePrex(m.toString())).collect(Collectors.toList());
	}
	
	public static Collection<String> changeMemberToString(Collection<? extends Member> list, Supplier<? extends Collection<String>>collectionFactory){
		return list.stream().map(m -> removePrex(m.toString())).collect(Collectors.toCollection(collectionFactory));
	}
	
	public static String removePrex(String str) {
		Matcher m = Pattern.compile("(\\w+\\.)+(\\w+\\()").matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, m.group(2));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
