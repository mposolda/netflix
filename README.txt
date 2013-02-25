How to build Netflix portlet and run it on GateIn portal
-------------------------------------------

1. Running "mvn clean install" to build netflix portlet (It's recommended to use Maven3 and JDK6 or JDK7).

2. Deploy netflix portlet by copying "target/netflix-jsf.war" into GATEIN_HOME/standalone/deployments (assuming you are using GateIn on JBoss AS7)

3. There is some classloading conflict on JBoss AS7 because some GateIn libraries are in conflict with dependencies of Netflix client.
If you are on GateIn on JBoss AS7 you need this to solve it:
3.a) Download JAR file http://nexus.blueleftistconstructor.com/content/repositories/releases/com/lysergicjava/nfclient/netflix-java-client/2.1.2/netflix-java-client-2.1.2.jar
into GATEIN_HOME/modules/org/gatein/lib/main/ directory
3.b) Add additional resource into GATEIN_HOME/modules/org/gatein/lib/main/module.xml by adding this line into resources section:
<resource-root path="netflix-java-client-2.1.2.jar"/>

4. Register yourself on http://developer.netflix.com to obtain netflix consumer key and netflix consumer secret.

5. Run GateIn portal with properties -Dnetflix.consumer.key="YOUR_CONSUMER_KEY" and -Dnetflix.consumer.secret="YOUR_CONSUMER_SECRET" where
YOUR_CONSUMER_KEY and YOUR_CONSUMER_SECRET are values obtained from registration on netflix api page

5. Add your portlet to some page on GateIn portal. You can search for movies and/or actors/directors and enjoy informations about movies.

