## 1. Compile DSS：
   
   After getting the project code from git, use Maven to package the project installation package。  
   
   (1) You can modify the versions of linkis, Java, Scala, Maven and other software in the top-level pom.xml file to adapt to your company's environment, as follows:
   
```xml
  <properties>
          <dss.version>0.7.0</dss.version>
          <linkis.version>0.9.1</linkis.version>
          <scala.version>2.11.8</scala.version>
          <jdk.compile.version>1.8</jdk.compile.version>
          <maven.version>3.3.3</maven.version>
  </properties>
```

   (2) **If you are using pom.xml locally for the first time, you must execute the following command in the directory of the outermost project pom.xml**：
   
```bash
    mvn -N  install
```

   (3) Execute the following command in the directory of the outermost project pom.xml
    
```bash
    mvn clean install
```  

   (4) Obtain the installation package in the assembly - > target directory of the project:

```
    wedatasphere-dss-x.x.x-dist.tar.gz
```

## 2.Compile a single service
   
   After getting the project code from git, use Maven to package the project installation package.   

（1） **If you are using pom.xml locally for the first time, you must execute the following command in the directory of the outermost project pom.xml**：
   
```bash
    mvn -N  install
```
         
（2） Jump to the corresponding module through the command line in terminal, for example:
   
```bash   
    cd dss-server
```

（3） Execute the compilation command under the pom.xml directory corresponding to the module:
   
```bash      
    mvn clean install
```
         
（4） Get the installation package. There will be compiled packages in the target directory of the corresponding module:
   
```
    dss-server.zip
```