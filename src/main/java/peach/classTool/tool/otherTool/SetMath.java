package peach.classTool.tool.otherTool;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 对集合进行数学操作，包括并，交，差，补
 * @author peach
 * @version 0.1
 */
public class SetMath {
	/**
	 * 对给定的两个集合进行并操作，返回其结果集合
	 * 结果为两个集合的所有元素组成的集合
	 * @param s1 进行并操作的集合
	 * @param s2 进行并操作的集合
	 * @return 并操作结果集合
	 */
	public static <T> Set<T> union(Set<T> s1, Set<T> s2){
		Set<T> set = new HashSet<>(s1);
		set.addAll(s2);
		return set;
	}
	/**
	 * 对两个给定的集合进行交操做
	 * 结果为两个集合个共有元素组成的集合
	 * @param s1 进行交操作的集合
	 * @param s2 进行交操作的集合
	 * @return 交操作结果集合
	 */
	public static <T> Set<T> intersection(Set<T> s1, Set<T> s2){
		Set<T> set = new HashSet<>(s1);
		set.retainAll(s2);
		return set;
	}
	/**
	 * 对两个集合进行差操作
	 * 结果为第一个集合中由且第二个集合中没有的元素组成
	 * 的集合
	 * @param s1 进行差操作的主集合
	 * @param s2 进行差操作的集合
	 * @return 差操作结果，第一个集合中由且第二个集合
	 * 中没有的元素组成的集合
	 */
	public static <T> Set<T> difference(Set<T> s1, Set<T> s2){
		Set<T> set = new HashSet<>(s1);
		set.removeAll(s2);
		return set;
	}

	/**
	 * 对两集合进行补操作
	 * 结果为第一个集合独有和第二个集合独有的元素组成
	 * 的集合
	 * @param s1 进行补操作的集合
	 * @param s2 进行补操作的集合
	 * @return 补操作结果集合
	 */
	public static <T> Set<T> complement(Set<T> s1, Set<T> s2){
		return difference(union(s1, s2), intersection(s1,s2));
	}
}
