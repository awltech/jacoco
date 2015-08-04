package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMyObject {

	@Test
	public void test() {
		MyObject object =new MyObject();
		assertEquals("toto",object.toto());		
	}

}
