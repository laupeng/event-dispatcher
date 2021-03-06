<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <substitutionProperty name="default_pattern" value="%date %-5level %logger{30} - %msg%n" />

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoding>UTF-8</encoding>
        <encoder><!-- 必须指定，否则不会往文件输出内容 -->
            <pattern>${default_pattern}</pattern>
        </encoder>
    </appender>
    
    <!--Output to central logging -->
    <appender name="CentralLogging"
              class="com.ctrip.freeway.appender.CentralLoggingAppender">
        <appId>100000557</appId>
        <serverIp>{$ClogIp}</serverIp>
        <serverPort>{$ClogPort}</serverPort>
    </appender>

    <appender name="bizfileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoding>UTF-8</encoding>
        <file>/opt/logs/tomcat/eventdispatcher.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/logs/tomcat/eventdispatcher.log.%d{yyyyMMddHH}</fileNamePattern>
            <maxHistory>3</maxHistory>
        </rollingPolicy>
        <encoder><!-- 必须指定，否则不会往文件输出内容 -->
            <pattern>${default_pattern}</pattern>
        </encoder>
    </appender>

    <appender name="debugfileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoding>UTF-8</encoding>
        <file>/opt/logs/tomcat/debugeventdispatcher.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/logs/tomcat/debugeventdispatcher.log.%d{yyyy-MM-dd}</fileNamePattern>
            <maxHistory>3</maxHistory>
        </rollingPolicy>
        <encoder><!-- 必须指定，否则不会往文件输出内容 -->
            <pattern>${default_pattern}</pattern>
        </encoder>
    </appender>

    <logger level="info" name="biz" additivity="false">
        <appender-ref ref="bizfileAppender"/>
    </logger>
    <root level="info">
        <appender-ref ref="debugfileAppender"/>
        <appender-ref ref="CentralLogging"/>
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>