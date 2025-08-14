Conectar la app movil con el servidor

Configurar la IP en la app movil 
   La IP del servidor debe coincidir con la red en la que estás ejecutando las apps. Dependiendo de dónde se ejecuten (emulador o dispositivo físico), debes ajustar lo siguiente en ambas apps:

1. Cambia la IP en:
WebSocketClient.kt
network_security_config.xml

Si usas emulador:
Usa la IP: 10.0.2.2

Si usas dispositivo físico:
Usa la IP real local de tu computadora (ej. 192.168.0.105)
Puedes encontrarla con ipconfig (Windows) o ifconfig (Mac/Linux)

2. Ejecutar la app
   Inicia la app móvil (en emulador o dispositivo físico).