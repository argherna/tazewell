# tazewell

Lightweight Tomcat components to ease development of web applications that require heavy integration.

When deployed to the `lib` directory in Tomcat, tazewell makes available utility valves that can be used in place of heavier weight integrations such as with web iso systems.

## use tazewell in your project

### maven

tazewell can be found in the [Maven Central repository](https://oss.sonatype.org/). Include the following dependency in your pom file:

```xml
<dependency>
  <groupId>com.github.argherna.tazewell</groupId>
  <artifactId>tazewell</artifactId>
  <version>0.1.0</version>
</dependency>
```

## building tazewell

tazewell uses [Maven](http://maven.apache.org) as its build tool. A number of profiles are used to control the build depending on the target deployment.

### prerequisites

* JDK 1.7
* Maven 3.2.3
* Tomcat 7+

### preparing a full build

Execute this command from the directory of the parent module: `mvn clean install`. This will compile all the files and build the jar file.

## use cases

See the [wiki](https://github.com/argherna/tazewell/wiki#uses) for configuration examples and use cases of tazewell.
