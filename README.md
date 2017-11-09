# webscreen

allows users to share view to their screen with other users on the web applicatiion 

### server deployment
1. get jre ([server jre8](http://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html))
2. get application server (developed and tested on [Tomcat 8.5](https://tomcat.apache.org/download-80.cgi))
3. setup ssl (highly recommended - user passwords are send over the network)
4. deploy (for new tomcat server delete the content of tomcats webapps directory and copy [ROOT.war](https://github.com/webscreenapp/webscreen/raw/master/ROOT.war) file to that directory)
5. run server

### desktop application
1. [get java](https://java.com/en/download/)
2. create account through the web application
3. login to the web application and manage access (as default noone but you can see your screen views)
4. run [webscreen.jar](https://github.com/webscreenapp/webscreen/raw/master/webscreen.jar)
5. fill server address and your account info
