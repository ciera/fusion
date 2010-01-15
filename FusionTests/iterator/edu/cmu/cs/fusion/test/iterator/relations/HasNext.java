package edu.cmu.cs.fusion.test.iterator.relations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.io.api.FileWriter;
import edu.cmu.cs.fusion.test.iterator.api.Iterator;

@Relation({Iterator.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface HasNext {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}