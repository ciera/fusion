package edu.cmu.cs.fusion.test.aspnet.api;

import edu.cmu.cs.fusion.annot.Infer;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.aspnet.relations.*;

public class Request {
	@Authenticated(value={"this"}, effect=Effect.TEST, test="result")
	public boolean isAuthenticated() {
		return false;
	}
}
