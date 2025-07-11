## Overview
This simple app can be used to fix payments that aren't being imported into TG correctly

## Dependencies:

* Java 21

## Package:

* `mvn install`
* `mvn clean package`


## Running:

* `mvn install`
* `mvn "-Dexec.args=-classpath %classpath com.topcoder.fix_payments.FixPayments" -Dexec.executable=/usr/bin/java -Dexec.classpathScope=runtime process-classes org.codehaus.mojo:exec-maven-plugin:1.5.0:exec`
