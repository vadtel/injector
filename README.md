"Simple Dependency injector"

Create *jar* file  
`mvn package`

Add dependency to your project(change path to *jar* file)

    <dependency>
           <groupId>org.vadtel</groupId>
           <artifactId>injector</artifactId>
           <version>1.0</version>
           <scope>system</scope>
           <systemPath>${project.basedir}/src/main/resources/injector-1.0.jar</systemPath>
    </dependency>
