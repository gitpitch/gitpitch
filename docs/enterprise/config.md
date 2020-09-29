# GitPitch Enterprise Config

- [Production Logging](#production-logging)
- [Debug Logging](#debug-logging)
- [Personal Access Tokens](#personal-access-tokens)
- [HTTPS/SSL Config](#httpsssl-config)
- [HTTPS/SSL PEM Certs](#httpsssl-pem-certs)
- [HTTPS/SSL JKS Certs](#httpsssl-jks-certs)

### Production Logging

The following production log configuration directs enterprise server log statements to a file called **gitpitch-production.log** in your Docker volume directory. Logging to standard out is disabled.

```xml
<configuration>

  <conversionRule conversionWord="coloredLevel"
                  converterClass="play.api.libs.logback.ColoredLevel" />

  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/data/gitpitch-production.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>/data/gitpitch-production.log-%d{yyyy-ww}</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
      <pattern>%date %coloredLevel %logger{15} - %message%n%xException{10}</pattern>
    </encoder>
  </appender>

  <appender name="ASYNCFILE" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE" />
  </appender>

  <logger name="com.gitpitch" level="INFO" additivity="false">
    <appender-ref ref="ASYNCFILE" />
  </logger>

  <logger name="play" level="INFO" />

  <root level="WARN">
    <appender-ref ref="ASYNCFILE" />
  </root>

</configuration>
```

To activate production logging, copy the above configuration block and use it to replace the existing content within the **gitlog.xml** file in your Docker volume directory. The small icon in the top-right of the block provides a convenient one-click
copy to your clipboard.

### Debug Logging

The following debug log configuration directs enterprise server log statements to a file called **gitpitch-debug.log** in your Docker volume directory. Logging to standard out is disabled.

Debug logging generates verbose log output. This configuration is provided to help gather the log data required to debug startup and runtime problems with your GitPitch Enterprise instance. Typically debug output is something we might request to help analyse and resolve problems during your trial evaluation.

All log statements are written to a file called **gitpitch-debug.log** in your Docker volume directory. Logging to standard out is disabled.

```xml
<configuration>

  <conversionRule conversionWord="coloredLevel"
                  converterClass="play.api.libs.logback.ColoredLevel" />

  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/data/gitpitch-debug.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>/data/gitpitch-debug.log-%d{yyyy-ww}</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
      <pattern>%date %coloredLevel %logger{15} - %message%n%xException{10}</pattern>
    </encoder>
  </appender>

  <appender name="ASYNCFILE" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE" />
  </appender>

  <logger name="com.gitpitch" level="DEBUG" additivity="false">
    <appender-ref ref="ASYNCFILE" />
  </logger>

  <logger name="play" level="INFO" />

  <root level="WARN">
    <appender-ref ref="ASYNCFILE" />
  </root>

</configuration>
```

To activate debug logging, copy the above configuration block and use it to replace the existing content within the **gitlog.xml** file in your Docker volume directory. The small icon in the top-right of the block provides a convenient one-click copy to your clipboard.

### Personal Access Tokens

The enterprise server runtime can be configured to use *personal access tokens* aka *PAT* for individual Git accounts using an optional external configuration file, called **tokens.yaml**.

> When a *PAT* is activated, slideshow presentations can be published within both *public* and *priviate* repositories on your Git server.

For example, the following sample **tokens.yaml** file contains *tokens* for three distinct Git accounts:

```yaml
david  : 0d5f871a56be7ba345d0f89b0f270950cda1be11
devops : 65c13fd1035046fedf7b531c2f820f6ff9caa336
maya   : 11030045fe6fcd5460b3e537351ba7fa3149a466
```

As a GitPitch Enterprise administrator, you can activate *personal access tokens* for an unlimited number of accounts on your Git server. The *tokens.yaml* file must be added to your Docker volume directory.

After copying the **tokens.yaml** configuration file, the contents of your Docker volume directory should look as follows:

```tree
├── gitpitch.conf
|── gitlog.xml
|── tokens.yaml
```

Note, these file names are case-sensitive. Also note, you will need to restart a running server in order to pick-up modifications to these files.


### HTTPS/SSL Config

To configure the enterprise server to handle HTTPS requests you must add additional configuration to your **gitpitch.conf** configuration file.

This *HTTPS/SSL* specific configuration specifies the following properties:

- Identifies the type of SSL certificate store - PEM or JKS
- Identifies the path to your SSL certificates file
- Identifies an optional password for your SSL certificates file

The *path* to your SSL certificates file must be specified using an absolute **/data** path as shown in the sample [pem](#httpsssl-pem-certs) and [jks](#httpsssl-jks-certs) configuratons that follow.

This **/data** path maps to the Docker volume directory on your Linux server. To activate *HTTPS/SSL* for the server your *certs.pem* or *certs.jks* certificates file must be deployed into the Docker volume directory alongside your *gitpitch.conf* configuration file.


### HTTPS/SSL PEM Certs

```
play.ws {

  #
  # Reference documentation:
  # https://www.playframework.com/documentation/latest/WsSSL
  #

  ssl {
    trustManager = {
	stores = [
	  { type = "PEM", path = "/data/certs.pem" }
	]
    }

    #
    # Optional - JSSE debug activation for troubleshooting.
    #debug.handshake = true
    #
  }
}
```

##### Note 1.
The *path* property must be specified using an absolute **/data** path as shown above. This path maps to Docker volume directory on your Linux server.

### HTTPS/SSL JKS Certs

```
play.ws {

  #
  # Reference documentation:
  # https://www.playframework.com/documentation/latest/WsSSL
  #

  ssl {
    trustManager = {
        stores = [
          { type = "JKS", path = "/data/certs.jks", password = "changeit" }
        ]
    }

    #
    # Optional - JSSE debug activation for troubleshooting.
    #debug.handshake = true
    #
  }
}
```

##### Note 1.
The *path* property must be specified using an absolute **/data** path as shown above. This path maps to Docker volume directory on your Linux server.
