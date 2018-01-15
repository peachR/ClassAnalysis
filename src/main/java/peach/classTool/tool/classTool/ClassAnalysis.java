package peach.classTool.tool.classTool;

import static peach.classTool.tool.classTool.ClassAnalysises.changeMemberToString;
import static peach.classTool.tool.classTool.ClassAnalysises.getClassMethod;
import static peach.classTool.tool.classTool.ClassAnalysises.groupingMethods;
import static peach.classTool.tool.classTool.ClassAnalysises.removePrex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import peach.classTool.tool.otherTool.SetMath;

public class ClassAnalysis<T> {

	private final Class<T> type;

	private Map<String, List<Method>> methodMap;
	private List<Constructor<?>> constructors;
	private Map<String, List<Field>> fieldMap;

	// 构造器
	private ClassAnalysis(Class<T> type) {
		this.type = type;
		getMethodMapByModifier();
		getConstructor();
		getAllFields();
	}

	// 工厂方法
	/**
	 * 根据指定类型构造ClassAnalysis实例
	 * 
	 * @param cl
	 *            ClassAnalysis待分析的类型
	 * @return ClassAnalysis实例
	 */
	public static <T> ClassAnalysis<T> newInstance(Class<T> cl) {
		return new ClassAnalysis<>(cl);
	}

	/**
	 * 根据指定对象的类型构造ClassAnalysis实例
	 * 
	 * @param obj
	 *            ClassAnalysis待分析类型的对象
	 * @return ClassAnalysis实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> ClassAnalysis<T> newInstance(T obj) {
		return newInstance((Class<T>) obj.getClass());
	}

	// 获取域

	private List<Field> getAllField(Optional<Class<?>> type) {
		Field[] fields = type.map(Class::getDeclaredFields).orElse(new Field[] {});
		return new ArrayList<>(Arrays.asList(fields));
	}

	private Map<String, List<Field>> groupingFieldMap(List<Field> list) {
		Map<String, List<Field>> map = list.stream()
				.collect(Collectors.groupingBy(f -> Modifier.toString(f.getModifiers())));
		return map;
	}

	private void getAllFields() {
		fieldMap = groupingFieldMap(getAllField(Optional.ofNullable(type)));
	}

	private List<Field> getFieldsByModifier(String modString) {
		return fieldMap.entrySet().stream().filter(entry -> entry.getKey().indexOf(modString) > -1)
				.map(entry -> entry.getValue()).flatMap(list -> list.stream()).collect(Collectors.toList());
	}

	private List<Field> getFieldsByModifier(int mod) {
		return getFieldsByModifier(Modifier.toString(mod));
	}

	// 获取类各个部分的方法
	/**
	 * 获取类的所有方法按照修饰符分类
	 */
	private void getMethodMapByModifier() {
		methodMap = groupingMethods(type);
	}

	/**
	 * 根据modifier过滤方法map
	 * 
	 * @param map
	 *            方法的map
	 * @param modifier
	 *            修饰符
	 * @return 过滤后的方法集合
	 */
	private Set<Method> getMethodByModifier(Map<String, List<Method>> map, String modifier) {
		Set<Method> set = map.entrySet().stream().filter(entry -> entry.getKey().indexOf(modifier) > -1)
				.map(entry -> entry.getValue()).flatMap(list -> list.stream()).collect(Collectors.toSet());
		return set;
	}

	private void getConstructor() {
		Constructor<?>[] constructors = type.getDeclaredConstructors();
		Arrays.sort(constructors, (c1, c2) -> Modifier.toString(c2.getModifiers()).indexOf("private"));
		this.constructors = new ArrayList<>(Arrays.asList(constructors));
	}

	/**
	 * 获取待分析类型{@value type}所有父类
	 * 
	 * @return 待分析类型{@value type}所有父类的列表
	 */
	private List<Class<?>> getSuperClasses() {
		LinkedList<Class<?>> list = new LinkedList<>();
		Class<?> now = this.type;
		while (now != null) {
			list.push(now);
			now = now.getSuperclass();
		}
		return list;
	}

	private Set<Method> getAllPublicMethod(Optional<Class<?>> cl) {
		Set<Method> set = new HashSet<>();
		Collections.addAll(set, cl.map(method -> method.getMethods()).orElse(new Method[] {}));
		return set;
	}

	/**
	 * 获取待分析类型{@value type}所有父类的名称
	 * 
	 * @return 待分析类型{@value type}所有父类的名称
	 */
	public List<String> getSuperClassesName() {
		return getSuperClasses().stream().map(cl -> cl.getName()).collect(Collectors.toList());
	}

	/**
	 * 获取待分析类型{@value type}所有接口列表
	 * 
	 * @return 待分析类型{@value type}所有接口的列表
	 */
	public List<String> getInterfacesName() {
		List<String> list = getInterfaces().stream().map(t -> t.getName()).collect(Collectors.toList());
		return list;
	}

	public List<Class<?>> getInterfaces() {
		return Arrays.asList(type.getInterfaces());
	}

	/**
	 * 获取待分析类型{@value type}的构造器列表
	 * 
	 * @return 待分析类型{@value type}的构造器列表
	 */
	public List<String> getConstructors() {
		return this.constructors.stream().map(con -> con.toString()).collect(Collectors.toList());
	}

	/**
	 * 获取待分析类型{@value type}的公有方法 这些方法包括他们从父类继承而来的方法 列表将会将static方法放在最前面
	 * 
	 * @return 待分析类型{@value type}的公有方法
	 */
	public List<Method> getPublicMethods() {
		List<Method> list = new ArrayList<>(getAllPublicMethod(Optional.ofNullable(type)));
		sortFirst(list, "static");
		return list;
	}

	/**
	 * 获取除了Object类继承而来的所有公有方法
	 * 
	 * @return除了Object类继承而来的所有公有方法
	 */
	public List<Method> getPublicMethodsExcludeObject() {
		List<Method> list = getPublicMethods();
		Set<String> objectMethods = methodRemovePre(getClassMethod(Optional.ofNullable(Object.class)));
		list.removeIf(m -> objectMethods.contains(removePrex(m.toString())));
		return list;
	}

	private Set<String> methodRemovePre(Collection<Method> set) {
		return set.stream().map(method -> method.toString()).map(str -> removePrex(str)).collect(Collectors.toSet());
	}

	public List<String> getSelfPublicMethods() {
		Set<String> selfSet = methodRemovePre(getAllPublicMethod(Optional.ofNullable(type)));
		Set<String> superSet = methodRemovePre(getAllPublicMethod(Optional.ofNullable(type.getSuperclass())));
		// List<Class<?>> list = getInterfaces();
		// for(Class<?> inter: list){
		// superSet.addAll(methodRemovePre(Arrays.asList(inter.getMethods())));
		// }
		return new ArrayList<>(SetMath.difference(selfSet, superSet));
	}

	/**
	 * 获取待分析类型{@value type}的私有方法 列表将会将static方法放在最前面
	 * 
	 * @return 待分析类型{@value type}的私有方法
	 */
	public List<Method> getPrivateMethods() {
		List<Method> list = new ArrayList<>(getMethodByModifier(methodMap, "private"));
		sortFirst(list, "static", "final");
		return list;
	}

	/**
	 * 按照给定的修饰符顺序排序，在第一个修饰符相同时按 后一个排序以此类推
	 * 
	 * @param list
	 *            待排序的列表，成员为Memer以及他的子类
	 * @param whos
	 *            排序修饰符序列
	 */
	private void sortFirst(List<? extends Member> list, String... whos) {
		if (whos.length == 0)
			whos = new String[] { "" };
		String s = whos[0];
		Comparator<Member> c = (m1, m2) -> Modifier.toString(m2.getModifiers()).indexOf(s);
		for (String who : whos)
			c = c.thenComparing((m1, m2) -> Modifier.toString(m2.getModifiers()).indexOf(who));
		list.sort(c);
	}

	/**
	 * 获取保护方法
	 * 
	 * @return 保护方法列表
	 */
	public List<Method> getProtectedMethods() {
		List<Method> list = new ArrayList<>(getMethodByModifier(methodMap, "protected"));
		sortFirst(list, "static");
		return list;
	}

	/**
	 * 获取静态方法列表
	 * 
	 * @return 静态方法列表
	 */
	public List<Method> getStaticMethods() {
		List<Method> list = new ArrayList<>(getMethodByModifier(methodMap, "static"));
		sortFirst(list, "private");
		return list;
	}

	/**
	 * 获取方法的隐射表，按访问权限分类
	 * 
	 * @return
	 */
	public Map<String, List<String>> getAllMethodMap() {
		Map<String, List<String>> map = new LinkedHashMap<>();
		map.put("Constructor", getConstructors());
		map.put("private", changeMemberToString(getPrivateMethods()));
		map.put("protected", changeMemberToString(getProtectedMethods()));
		return map;
	}

	public Map<String, Set<String>> getAllPubilcMethodMap() {
		Map<String, Set<String>> map = new LinkedHashMap<>();
		List<Class<?>> superType = getSuperClasses();
		superType.remove(Object.class);
		Optional<Set<String>> temp = Optional.ofNullable(
				(Set<String>) changeMemberToString(this.getAllPublicMethod(Optional.ofNullable(Object.class)),
						HashSet::new).stream().map(str -> removeModifier(str)).collect(Collectors.toSet()));
		for (Class<?> parent : superType) {
			Set<String> now = (Set<String>) changeMemberToString(this.getAllPublicMethod(Optional.ofNullable(parent)),
					HashSet::new);
			final Set<String> inner = temp.orElse(new HashSet<>());
			now = now.stream().filter(str -> !inner.contains(removeModifier(str))).collect(Collectors.toSet());
			map.put(parent.getName(), SetMath.difference(now, temp.orElse(new HashSet<>())));
			temp.orElse(new HashSet<>()).addAll(now.stream().map(str -> removeModifier(str)).collect(Collectors.toSet()));
		}
		return map;
	}

	private String removeModifier(String method) {
		Pattern p = Pattern.compile("(\\w+\\.? )+(\\w+\\.)*(\\w+\\()");
		StringBuffer sb = new StringBuffer();
		Matcher m = p.matcher(method);
		while (m.find()) {
			m.appendReplacement(sb, m.group(3));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 获取私有域列表,按static fianl在前排序
	 * 
	 * @return 私有域列表
	 */
	public List<Field> getPrivateFields() {
		List<Field> list = getFieldsByModifier(Modifier.PRIVATE);
		sortFirst(list, "static", "final");
		return list;
	}

	public List<Field> getProtectedFields() {
		List<Field> list = getFieldsByModifier(Modifier.PROTECTED);
		sortFirst(list, "static", "final");
		return list;
	}

	public List<Field> getPublicFields() {
		List<Field> list = getFieldsByModifier(Modifier.PUBLIC);
		sortFirst(list, "static", "final");
		return list;
	}

	public List<Field> getStaticFields() {
		List<Field> list = getFieldsByModifier(Modifier.STATIC);
		sortFirst(list, "private", "final");
		return list;
	}

	public List<Field> getFields(int... mods) {
		int mod = IntStream.of(mods).sum();
		List<Field> list = getFieldsByModifier(mod);
		sortFirst(list, "private", "static", "final");
		return list;
	}

	/**
	 * 获取给定域列表的所有域的建议名称的列表 给定列表可以由{@value ClassAnalysis}的获取方法生成 简易名称为不带包名.类名.的名称
	 * 
	 * @param supplier
	 * @return
	 */
	public List<String> getSimpleFields(Supplier<List<Field>> supplier) {
		String reg = "(\\w+\\.)*";
		return supplier.get().stream().map(field -> field.toString()).map(str -> str.replaceAll(reg, ""))
				.collect(Collectors.toList());
	}

	/**
	 * 获取给定域列表的所有域的建议名称的列表 给定列表可以由{@value ClassAnalysis}的带修饰符 筛选的获取方法生成
	 * 简易名称为不带包名.类名.的名称
	 * 
	 * @param function
	 * @param args
	 * @return
	 */
	public <T> List<String> getSimpleFields(Function<T[], List<Field>> function, T[] args) {
		String reg = "(\\w+\\.)*";
		return function.apply(args).stream().map(field -> field.toString()).map(str -> str.replaceAll(reg, ""))
				.collect(Collectors.toList());
	}

	/**
	 * 获取所有域，按照给定的修饰符排序
	 * 
	 * @param sorter
	 *            排序参照的修饰符
	 * @return 所有域列表
	 */
	public List<Field> getAllFieldList(String... sorter) {
		List<Field> list = getAllField(Optional.ofNullable(type));
		sortFirst(list, sorter);
		return list;
	}

	/**
	 * 获取所有拥有给定修饰符的域
	 * 
	 * @param mod
	 *            给定的修饰符{@code Modifier}
	 * @return 符合要求的域的列表
	 */
	public List<Field> getAllFieldList(Integer... mod) {
		String[] strs = Stream.of(mod).map(i -> Modifier.toString(i)).toArray(len -> new String[len]);
		return getAllFieldList(strs);
	}

	/**
	 * 获取所有域包括父类的返回一个映射表 表中键为域所在类类型
	 * 
	 * @return 所有域包括父类的返回一个映射表
	 */
	public Map<String, List<String>> getAllFieldMap() {
		List<Class<?>> parents = this.getSuperClasses();
		parents.add(type);
		Map<String, List<String>> map = new LinkedHashMap<>();
		for (Class<?> cl : parents) {
			List<Field> list = Arrays.asList(cl.getDeclaredFields());
			sortFirst(list, "private", "static", "final", "protected");
			map.put(cl.getName(), this.getSimpleFields(() -> list));
		}
		return map;
	}

	/**
	 * 获取待分析类型{@value type}
	 * 
	 * @return 待分析类型{@value type}
	 */
	public String getType() {
		return type.getName();
	}

	public static void main(String[] args) {
		ClassAnalysis<? extends List> analysis = ClassAnalysis.newInstance(ArrayList.class);
		// ClassAnalysis<? extends A> analysis =
		// ClassAnalysis.newInstance(C.class);
		System.out.println(analysis.methodMap);
		System.out.println(analysis.getSuperClasses());
		System.out.println(analysis.getInterfaces());
		System.out.println(analysis.getConstructors());
		System.out.println(analysis.getPrivateMethods());
		System.out.println(analysis.getPublicMethods());
		System.out.println(analysis.getPublicMethodsExcludeObject());

		System.out.println(analysis.getSelfPublicMethods());
		System.out.println(analysis.getPublicMethodsExcludeObject().size());
		System.out.println(analysis.getSelfPublicMethods().size());
		System.out.println(
				analysis.getAllFieldList(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED));
		System.out.println(analysis.getAllMethodMap());
		System.out.println(analysis.getAllPubilcMethodMap());
	}

}