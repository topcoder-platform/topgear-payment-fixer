## Overview
This simple app can be used to fix payments that aren't being imported into TG correctly

## Dependencies:

* Java 21
* 
* * Assuming Informix Client SDK installation is in `/opt/IBM/Informix_Client-SDK', set the LD_LIBRARY_PATH and INFORMIXDIR:
  * `export LD_LIBRARY_PATH=/opt/IBM/Informix_Client-SDK/lib:/opt/IBM/Informix_Client-SDK/lib/esql:/opt/IBM/Informix_Client-SDK/lib/cli`
  * `export INFORMIXDIR=/opt/IBM/Informix_Client-SDK`

## Running:

* `mvn install`
* `mvn "-Dexec.args=-classpath %classpath com.topcoder.fix_payments.FixPayments" -Dexec.executable=/usr/bin/java -Dexec.classpathScope=runtime process-classes org.codehaus.mojo:exec-maven-plugin:1.5.0:exec`

select * from tcs_catalog:project_payment 
where amount>0 and pacts_payment_id is null and project_payment_type_id=1 and create_date>"2024-11-01 00:00:00"
order by create_date desc mvn insta