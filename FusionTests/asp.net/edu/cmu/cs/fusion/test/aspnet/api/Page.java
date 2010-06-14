package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Callback;
import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.test.aspnet.relations.*;

public class Page extends Control {
	
	@Callback(name = "PreInit")
	public void preInit() {
	}
	
	@Callback(name = "Init")
	public void init() {
	}
	
	@Callback(name = "PreLoad")
	public void preLoad() {
	}

	@Callback(name = "Load")
	public void load() {
		
	}
	
	public void render() {
	}

	public void close() {
	}

	@PageRequest({"result", "target"})
	public Request getRequest() {
		return null;
	}
}
