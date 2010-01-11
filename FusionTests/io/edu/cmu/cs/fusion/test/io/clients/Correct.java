package edu.cmu.cs.fusion.test.io.clients;

import edu.cmu.cs.fusion.test.io.api.FileWriter;


public class Correct {
	public int m1() {
		FileWriter fw = new FileWriter();
		fw.write("");
		fw.write("");
		fw.close();
		return 23;
	}

}
