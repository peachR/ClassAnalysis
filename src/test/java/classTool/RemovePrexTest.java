package classTool;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import peach.classTool.tool.classTool.ClassAnalysises;

public class RemovePrexTest {

	@Test
	public void testRemovePrex() {
		String str = "public java.lang.Object get(int)";
		String ans = ClassAnalysises.removePrex(str);
		assertThat(ans,is("public abstract Object get(int)"));
	}

}
