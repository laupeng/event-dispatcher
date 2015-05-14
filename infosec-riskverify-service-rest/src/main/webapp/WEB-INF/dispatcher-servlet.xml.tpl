<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    
    <context:component-scan base-package="rest"/>
    <context:annotation-config/>
    <mvc:annotation-driven/>
    
    <!-- RESTful接口访问监控 -->
    <mvc:interceptors>
        <bean class="com.ctrip.infosec.sars.monitor.springmvc.SarsMonitorSpringMvcInterceptor">
            <property name="urlList">
                <util:list>
                    <value>/**</value>
                </util:list>
            </property>
        </bean>
    </mvc:interceptors>
    
</beans>