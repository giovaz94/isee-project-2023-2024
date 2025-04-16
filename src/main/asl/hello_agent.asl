/* Initial beliefs and rules */

/* Initial goals */

!start. // initial achievement goal (prefixed by a '!' or '?')

/* Plans */

// a procedure for handling the start goal
+!start : true <-
	.print("hello world").
