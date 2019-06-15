# sunbird-user-registry
Opensaber implementation for Sunbird

Configration changes regarding opensaber for Sunbird:

Crated sunbird-user-registry service on the base of open-saber(https://github.com/project-sunbird/open-saber) with branch release-2.0.3

Run sb-registry-configure-dependencies.sh file which copies User related json schemas and application.yml configuration and replaces the exisiting configuration files.

Go to java folder path and run "mvn clean install" in the terminal, it gives registry executable jar.

Run the registry jar with "java -jar registry-2.0.3.jar".

Start using the sunbird-user-registry with user related schemas, organisation related schemas will be added soon.



