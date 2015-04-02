# Specifying Constraints #
Constraints are written as a class-level Java 5 annotation called `Constraint`. To write multiple constraints on the same class, use the annotation `Constraints` to make an array of `Constraint` annotations.

# Examples #

```
@Constraint(
	op="ListItem.setSelected(boolean selected) : void",
	trigger = "!selected AND Child(target, ctrl) AND ctrl instanceof DropDownList",
	requires = "Selected(target)",
	effects = {"!CorrectlySelected(ctrl)"}
)
```

```
@Constraint(
	op="EOM",
	trigger = "!CorrectlySelected(ctrl) AND ctrl instanceof DropDownList",
	requires = "FALSE",
	effects = {}
)
```

```
@Constraint(
	op = "File(String s)",
	trigger = "TRUE",
	requires = "File_GetAbsolutePath(x)",
	effects = {}					
)
```

```
@Constraint(
	op = "File.getParentFile() : void",
	trigger = "TRUE",
	requires = "File_Init(target)",
	effects = {}
)
```

# Syntax #
A constraint has 4 parts:
  1. The operator being constrained (Operator)
  1. The trigger predicate (Predicate)
  1. The requires predicate (Predicate)
  1. The effects list (Effects)

The first 3 parts are written as Strings, and the last is an array of Strings. The syntax of these strings is described below. **Bold text** is used to describe required syntax, while _italic text_ is used to describe a 0-n comma separated list.

Notice that the effects list is **not** a predicate; you cannot specify logical operators. It is simply a list of effects. This is because it makes little sense to specify an effect with a disjunction or an implication. Additionally, the effects can only be relationships, not type checks or equality checks.

## Operator syntax ##
Operator ::= MethodCall | Constructor | EOM<br>
MethodCall ::= Type<b>.methodName(</b><i>Type var</i> <b>):</b> Type<br>
Constructor ::= Type<b>(</b><i>Type var</i><b>)</b><br>
EOM ::=  | <b>eom</b><br>
Type ::= elementalType | typeName<br>

<h2>Predicate syntax</h2>
Both the trigger predicate and the requires predicate have the same syntax.<br>
<br>
Predicate ::= Predicate <b>IMPLIES</b> Predicate | Predicate <b>OR</b> Predicate | Predicate <b>AND</b> Predicate | <b>(</b>Predicate<b>)</b> | Relationship | Boolean<br>
Relationship ::= <b>!</b>Relationship | TypeRelationship | EqualityRelationship | UserRelationship | TestRelationship<br>
Boolean ::= <b>TRUE</b> | <b>FALSE</b> | var | <b>!</b>var<br>
TypeRelationship ::= var <b>instanceof</b> Type<br>
EqualityRelationship ::= var <b>==</b> var<br>
UserRelationship ::= relName<b>(</b><i>var</i><b>)</b><br>
TestRelationship ::= <b>?</b>relName<b>(</b><i>var</i><b>) :</b> var<br>

<h2>Effects syntax</h2>

Effect ::= <b>!</b>Effect | UserRelationship | TestRelationship<br>
UserRelationship ::= relName<b>(</b><i>WCVar</i><b>)</b><br>
TestRelationship ::= <b>?</b>relName<b>(</b><i>WCVar</i><b>) :</b> var<br>
WCVar ::= <b><code>*</code></b> | var<br>