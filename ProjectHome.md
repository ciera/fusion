A specification language and static analysis to find misuses of complex framework protocols.

Overall goals for Fusion:
  * Concise specifications
  * No specifications on plugin code; only the framework is specified.
  * Incremental specifications. There should be no need to specify the entire framework. It can be specified as the need arises.
  * Human-readable error messages.
  * Cost-effective results that provide a balance between false positives and false negatives
  * Works on not just traditional OO frameworks, but also works with artifacts like XML configuration files that are ubiquitous in modern software frameworks.

The core part of the system was published at ECOOP 2009 under the title "Checking Framework Interactions with Relationships". The publication is available here: http://www.cs.cmu.edu/~cchristo/pubs.html. Until my thesis is finished, the first couple sections here is probably the best resource for understanding what a "relationship" is.

The extension on XML files it not yet published, but it did win **first place** at the SPLASH/OOPSLA ACM Student Research Competition!

The specification language itself is described under WritingFusionSpecifications.

I am currently using Fusion to specify parts of the Spring framework, in addition to more of the ASP.NET framework than is shown in the ECOOP paper. All these examples will be in my thesis, and on the web once I've finished.





