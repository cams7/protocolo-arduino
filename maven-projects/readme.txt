Windows
    http://jlog.org/rxtx-win.html 

    Copie o arquivo rxtxSerial.dll para:

    C:\Program Files\Java\jdkx.x.x\bin, onde x.x.x é a versão do JDK, por exemplo C:\Program Files\Java\jdk1.6.40\bin;
    C:\Program Files\Java\jrex\bin,  onde x é a versão do JRE, por exemplo C:\Program Files\Java\jre7\bin;
    C:\Windows\System32;
    C:\Windows\SysWOW64 (caso sistema operacional 64-bits (x64)).

    Copie o arquivo RXTXcomm.jar para:

    C:\Program Files\Java\jre\jrex\lib\ext, onde x é a versão do JRE, por exemplo C:\Program Files\Java\jre7\lib\ext. 

Linux
    http://jlog.org/rxtx-lin.html 

    Copie o arquivo librxtxSerial.so para:   

    /usr/lib/, exemplo: cp /home/Usuario/librxtxSerial.so /usr/lib/.

    Copie o arquivo RXTXcomm.jar para:

    /usr/share/java/, exemplo.: cp /home/Usuario/RXTXcomm.jar /usr/share/java/.
	

For Windows VAIO:
	cd ~/Desenv/github/sisbarc/java/jmx/acende_apaga_leds_monitor
	java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1234 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=192.168.1.7 -jar target/acende_apaga_leds_monitor.jar

	
	service:jmx:rmi:///jndi/rmi://192.168.1.7:1234/jmxrmi
	
	
