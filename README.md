# SCRAL - Smart City Resource Adaptation Layer##
The Smart City Resource Adaptation Layer (SCRAL) provides a REST - based uniform and transparent access to physical devices, capillary networks, systems and services for monitoring and actuation in a
Smart City context.

Peculiar device functionalities are uniformed, abstracted and mapped to a well-known  set  of  functions  and  primitives  complying  with  (device)  models  handled  in  the  semantic framework of the ALMANAC platform. Moreover, due to its nature of interface between the ALMANAC platform and the real world, the SCRAL offers primitives for applying access-control, data-validation and role-based policies on field-level data sources. While typical SCRAL instances are distributed near to  physical  devices,  meaning  that more  than  one  SCRAL  instance  is  usually  adopted  in  a  single ALMANAC P
latform Instance (PI), at least one cloud instance is typically available in a PI to support connection  of  smart  devices,  i.e.,  of  devices  able  to  natively  exchange  data  conforming  to  the
ALMANAC data model. In such a case, the main SCRAL duty is to enforce access rights, perform data validation and support needed provisioning primitives.

## Installation How-To
The SCRAL is delivered as a simple web application that can be run on any Servlet Container. Additionally a set of test scripts are provided to allow checking and diagnosing the component both in the post-installation and in the runtime operation phases.

Currently the SCRAL has been extensively tested in 2 Servlet containers:

* [Apache Tomcat](http://tomcat.apache.org/) (v7.0)
* [Jetty](http://eclipse.org/jetty/)

Assuming that the reference environment for the SCRAL installation is a Ubuntu-based server (but only for the sake of demonstrating installation, any platform capable of hosting a Servlet Container shall be suitable), the full installation process is as follows:

#### Apache Tomcat set-up

Download and install Apache Tomcat using the distribution package manager:

```
sudo apt-get install tomcat7
```

### SCRAL set-up

Download the latest SCRAL version from this git repository

```
git clone https://github.com/almanacproject/scral.git
```

Compile the scral with Maven
```
cd scral/
mvn install
```

Assuming that you downloaded  the SCRAL war file in the ```\home\almanac``` folder, move the war file to the right place on the Servlet Container (i.e., Tomcat)

```
sudo mv ./scral.war /var/lib/tomcat7/webapps/
```
At this point Tomcat should automatically deploy the war file into a published context named after the war file name, i.e., scral

Check if the SCRAL is running by looking at the Tomcat7 logs

```
less /var/log/tomcat7/catalina.out
```

you should see logging lines generated by the SCRAL. To further check the actual working, open-up a browser and go to the ```http://localhost:8080/scral/devices``` address. You should see a variable (possibly empty) lis of device ids.

#### SCRAL configuration
To configure the SCRAL for interfacing devices and gateways, the provided SCRAL configuration file shall be edited according to the peculiarities of the current installation. The scral location file is located in the ```WEB-INF/classes``` folder of the corresponding web application.

```
cd /var/lib/tomcat7/webapps/scral/WEB-INF/classes/applicationContext-pwal.xml
```

And the file is structured as follows:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd">
<bean id="PWAL" class="it.ismb.pertlab.pwal.PwalImpl">
<constructor-arg>
<list value-type="it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager">
<!-- <ref bean="serialManager" /> -->
<!-- <ref bean="xivelyManager"/> -->
<!-- <ref bean="m2mManager" /> -->
<ref bean="WasteBinSimulatorManager" />
<!-- <ref bean="WaterMeterSimulatorManager"/> -->
<!--<ref bean="UeiManager" />-->
<!--<ref bean="LorryManager" />-->
</list>
</constructor-arg>
<!-- Fill the value with the scral FQDN -->
<property name="fqdn" value="scral.example.example" />
</bean>

<!-- <bean id="serialManager" class="it.ismb.pertlab.pwal.serialmanager.SerialManager" -->
<!-- depends-on="mqttPusher"> -->
<!-- <constructor-arg> -->
<!-- <map> -->
<!-- <entry key="idFillLevelSensor"> -->
<!-- <bean class="it.ismb.pertlab.pwal.manager.serial.SerialDeviceDescriptor"> -->
<!-- <property name="clazz" value="it.ismb.pertlab.pwal.manager.serial.device.FillLevelSensorWSN"/> -->
<!-- <property name="clazzParameters"> -->
<!-- <props> -->
<!-- <prop key="empty_distance">100.0</prop> -->
<!-- <prop key="full_distance">20.0</prop> -->
<!-- </props> -->
<!-- </property> -->
<!-- <property name="baudrate" value="9600"/> -->
<!-- <property name="databits" value="8"/> -->
<!-- <property name="stopbits" value="1"/> -->
<!-- <property name="parity" value="0"/> -->
<!-- <property name="port" value="/dev/ttyACM0"/> -->
<!-- </bean> -->
<!-- </entry> -->
<!-- </map> -->
<!-- </constructor-arg> -->
<!-- </bean> -->
<!-- <bean id="xivelyManager" class="it.ismb.pertlab.pwal.xivelymanager.XivelyManager"
depends-on="mqttPusher"/> -->
<!-- <bean id="m2mManager" class="it.ismb.pertlab.pwal.etsi_m2m_manager.EtsiM2MManager" -->
<!-- depends-on="mqttPusher"> -->
<!-- <constructor-arg value="http://m2mserver.example" /> -->
<!-- </bean> -->
<bean id="mqttPusher" class="it.ismb.pertlab.pwal.pusher.mqtt.MqttPusher">
<constructor-arg value="tcp://<mqtt-address>:<mqtt-port>" />
<constructor-arg value="<scral-id>"></constructor-arg>
<!-- Fill the value with the scral FQDN, will be removed -->
<constructor-arg value="scral.example.example"></constructor-arg>
</bean>
<bean id="WasteBinSimulatorManager"
class="it.ismb.pertlab.pwal.wastebinsimulator.WasteBinSimulatorManager">
<constructor-arg value="turin_waste.jsonld" />
<constructor-arg value="600000" />
</bean>

<!-- <bean id="ResourceCatalogConnector" class="it.ismb.pertlab.pwal.resourcecatalog.connector.ResourceCatalogConnector">
<constructor-arg value="http://<catalogue-address>"/> <constructor-arg
value="<scral-address>"/> </bean> -->

<bean id="UeiManager" class="it.ismb.pert.pwal.managers.wastebin.uei.UeiManager"
depends-on="mqttPusher">
<constructor-arg>
<map>
<entry key="BIN-KEY">
<bean
class="it.ismb.pert.pwal.managers.wastebin.uei.device.descriptor.UeiDescriptor">
<property name="id" value="BIN 1" />
<property name="streetAddress" value="Route 66" />
<property name="garbageType" value="DryRubbish" />
<property name="location">
<bean class="it.ismb.pertlab.smartcity.api.GeoPoint">
<property name="latitude" value="5.0" />
<property name="longitude" value="5.0" />
</bean>
</property>
</bean>
</entry>
</map>
</constructor-arg>
<constructor-arg value="true"></constructor-arg>
</bean>
<bean id="LorryManager" class="it.ismb.pert.pwal.managers.wastebin.lorry.LorryManager"
depends-on="mqttPusher">
<constructor-arg>
<map>
<entry key="LORRY-KEY">
<bean
class="it.ismb.pert.pwal.managers.wastebin.lorry.device.descriptor.LorryDescriptor">
<property name="id" value="LORRY-ID1" />
<property name="location">
<bean class="it.ismb.pertlab.smartcity.api.GeoPoint">
<property name="latitude" value="5.0" />
<property name="longitude" value="5.0" />
</bean>
</property>
</bean>
</entry>
</map>
</constructor-arg>
<constructor-arg value="true"></constructor-arg>
</bean>
</beans>

```

As can easily be noticed, device and technology managers are activated by:

1. Listing the corresponding  reference in the bean entry with id equal to ```PWAL```
2. Defining a suitable ```<bean>...</bean>``` entry for providing the needed initialization parameters, e.g., the number of fake bins to generate for the ```WasteBinSimulatorManager```

Once updated the configuration, the SCRAL should be restarted. This can be done either by stopping and restarting the corresponding context on the servlet container, or, more easily, by restarting the container:

```
sudo service tomcat7 restart
```

At this point the SCRAL shall be active and serving the confugured devices / technologies.