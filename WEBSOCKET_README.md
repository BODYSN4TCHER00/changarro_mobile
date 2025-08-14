# Cliente WebSocket - Aplicación Móvil

Cliente WebSocket para conectar tu aplicación móvil con el servidor Automotive.

## 📁 Estructura del Proyecto

```
mobile/app/src/main/java/com/example/ing/
├── network/
│   ├── WebSocketClient.kt        # Cliente WebSocket
│   └── ConnectionViewModel.kt    # Lógica de conexión
├── screens/
│   └── ConnectionScreen.kt       # Interfaz de conexión
└── components/navigation/
    ├── AppNavigation.kt          # Navegación (actualizado)
    └── BottomNavigation.kt       # Navegación inferior (actualizado)
```

## 🎯 Características

- ✅ **Cliente WebSocket** para conectar al servidor Automotive
- ✅ **Interfaz intuitiva** con estado de conexión visual
- ✅ **Envío y recepción** de mensajes en tiempo real
- ✅ **Historial de mensajes** recibidos
- ✅ **Navegación integrada** en la aplicación

## 📱 Cómo Usar

### 1. Preparar el Servidor Automotive

1. Ejecuta la aplicación Automotive en el emulador
2. Ve a "Servidor WebSocket" desde la pantalla principal
3. Presiona **"Iniciar"** para activar el servidor
4. Anota la IP mostrada (ej: `192.168.1.100:8080`)

### 2. Conectar desde la Aplicación Móvil

1. Ejecuta la aplicación móvil en tu dispositivo físico
2. Ve a la pestaña **"Conexión"** en la navegación inferior
3. Ingresa la URL del servidor: `ws://192.168.1.100:8080`
4. Presiona **"Conectar"**
5. Espera a que el estado cambie a verde (Conectado)

### 3. Intercambiar Mensajes

#### Desde Automotive hacia Móvil:
- En la aplicación Automotive, presiona **"Enviar Mensaje de Prueba"**
- El mensaje aparecerá en la lista de "Mensajes recibidos" en el móvil

#### Desde Móvil hacia Automotive:
- En la aplicación móvil, escribe un mensaje en el campo "Escribe tu mensaje"
- Presiona **"Enviar"**
- El mensaje aparecerá en la pantalla del servidor Automotive

## 🎨 Estados de la Interfaz

- **🔴 Rojo**: No conectado
- **🟡 Amarillo**: Conectando...
- **🟢 Verde**: Conectado al servidor

## 📋 Flujo de Comunicación

1. **Automotive inicia servidor** → Muestra IP
2. **Móvil se conecta** → Estado cambia a verde
3. **Automotive envía mensaje** → Móvil lo recibe y muestra
4. **Móvil envía mensaje** → Automotive lo recibe y muestra

## 🔧 Solución de Problemas

### No se conecta
- Verifica que ambos dispositivos estén en la misma red WiFi
- Confirma la IP mostrada en Automotive
- Asegúrate de que el puerto 8080 no esté bloqueado
- Verifica que el servidor Automotive esté iniciado

### URL incorrecta
- La URL debe comenzar con `ws://` (no `http://`)
- Incluye el puerto `:8080` al final
- Ejemplo correcto: `ws://192.168.1.100:8080`

### Mensajes no llegan
- Verifica que el estado esté en verde (Conectado)
- Asegúrate de que el servidor Automotive esté activo
- Revisa los logs en Android Studio para errores

## 🚀 Próximos Pasos

1. Compila y ejecuta ambas aplicaciones
2. Inicia el servidor en Automotive
3. Conecta desde el móvil
4. ¡Prueba el intercambio de mensajes!

## 📝 Notas

- La conexión se mantiene activa hasta que se desconecte manualmente
- Los mensajes se envían como texto plano
- El historial de mensajes se mantiene durante la sesión
- Usa el botón "Limpiar" para borrar el historial de mensajes 