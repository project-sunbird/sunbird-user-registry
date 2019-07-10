# sunbird-user-registry
This is a copy of OpenSABER maintained [here](https://github.com/project-sunbird/open-saber) with additional schemas 
pertaining to Sunbird user-org service. This is maintained by Sunbird user-org team.

## Building this repo

### 1. Run dependency resolver 
Copies/Overwrites the user related json schema files and application configuration from the sb_registry folder. Additionally, 
takes a backup of the existing application.yml.  

> ./sb-registry-configure-dependencies.sh 

### 2. Go to java folder and build
> mvn clean install


## Ready to go
Now you can run, the application uses 8080 port  
> "java -jar registry-x.y.z.jar"