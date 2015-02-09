# tazewell

Lightweight Tomcat components to ease development of web applications that require heavy integration.

When deployed to the `lib` directory in Tomcat, tazewell makes available utility valves that can be used in place of heavier weight integrations such as with web iso systems.

## building tazewell

tazewell uses [Maven](http://maven.apache.org) as its build tool. A number of profiles are used to control the build depending on the target deployment.

### prerequisites

* JDK 1.7
* Maven 3.2.3

### preparing a full build

Execute this command from the directory of the parent module: `mvn clean install`. This will compile all the files and build the jar file.

## uses

See the [wiki](https://github.com/argherna/tazewell/wiki) for configuration examples and use cases of tazewell.