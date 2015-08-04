package test;

public class MyObject {

	public String toto = "tata";

	public String toto() {
		// TODO Auto-generated method stub
		
		int i = 10;
		int j = 1;
		 
		// concat(titi);

		return "toto";
	}

	public String concat(String s) {
		try {
			return concat(s + "");
		} catch (Throwable e) {
			return "";// concat(s);
		}

	}

}
