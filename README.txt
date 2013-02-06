How to build Netflix portlet and run it on GateIn portal
-------------------------------------------

1. You can change property "portal.dir" with path to server at pom.xml (section with properties). But this step is not needed because you can also change this property when you will run maven in later step by adding it to java properties with -Dportal.dir=... flag (see later steps).

2. Running "mvn clean install" will build netflix portlet and upload it to local repo with sources and javadocs. But you can use profile "deploy" if you want to automatically deploy portlet into your portal.

So recommended way to build and deploy whole project:

mvn clean install -P deploy -Dportal.dir=/path/to/gatein/home

3. Run GateIn portal with properties -Dnetflix.consumer.key="YOUR_CONSUMER_KEY" and -Dnetflix.consumer.secret="YOUR_CONSUMER_SECRET" where 
YOUR_CONSUMER_KEY and YOUR_CONSUMER_SECRET are values obtained from registration on netflix api page -- http://developer.netflix.com
