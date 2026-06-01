# 🏭 Cinta Transportadora Inteligente - Clasificador de Objetos con IA e IoT

![Status](https://img.shields.io/badge/Status-Advanced__Testing-green)
![Hardware](https://img.shields.io/badge/Hardware-Assembled__&__Tested-blue)
![Platform](https://img.shields.io/badge/Platform-ESP32--S3-orange)


Este repositorio contiene el firmware, los diseños de hardware y la documentación para el sistema embebido inteligente de clasificación automatizada de objetos en una cinta transportadora. El proyecto integra visión artificial en el borde (Edge Computing), conectividad IoT y control secuencial en tiempo real.

---

## ⚡ Estado Actual del Proyecto: Pruebas de Integración y Validation
El proyecto ha superado con éxito las fases iniciales de diseño esquemático, ruteo en KiCad, fabricación y ensamblaje físico de la PCB en laboratorio. Actualmente, nos encontramos en la **fase de validación final e integración total de software/hardware**, habiendo verificado el correcto funcionamiento aislado de los siguientes subsistemas:

* **Adquisición Óptica:** Inicialización exitosa de la cámara OV5640, operando de manera continua para capturar fotogramas QVGA almacenados en la PSRAM.
* **Conectividad y Servidor de IA:** Conexión WiFi estable del ESP32-S3 y envío exitoso del payload en formato síncrono (Base64 + binario JPEG) hacia el Webhook de n8n alojado en un servidor VPS. Recepción correcta de las respuestas del modelo de IA dentro de los márgenes de latencia previstos.
* **Clasificación Mecánica:** Respuesta precisa del servomotor a las instrucciones de la IA (giros de 90° o 180° según la anomalía detectada) a través de canales PWM independientes y respetando el tiempo de retención programado de 10 segundos.

**Próximos pasos inmediatos:** Ensayos integrados en la cinta con el sensor de proximidad infrarrojo **JSumo JS40F** y el motor DC impulsado por el driver de potencia **DRV8873** para cerrar completamente el lazo de automatización.

---

## ⚙️ ¿Cómo Funciona? (Flujo Conceptual del Algoritmo)
El lazo cerrado de control y toma de decisiones distribuidas opera de la siguiente manera:

1. **Transporte y Detección:** El motor DC principal desplaza los objetos sobre la cinta transportadora mediante modulación PWM gestionada por el driver de potencia DRV8873. Al interrumpirse el haz del sensor infrarrojo de proximidad (*JSumo JS40F*), el ESP32-S3 detiene la cinta inmediatamente.
2. **Captura en Borde:** La cámara OV5640 se activa de forma síncrona, captura el fotograma del objeto en la PSRAM, realiza la compresión a formato JPEG y codifica el archivo en Base64 para optimizar la transmisión.
3. **Inferencia Remota (IoT):** El ESP32-S3 realiza una petición síncrona HTTP POST de tipo *Multipart* dirigida al webhook de **n8n** en la nube. El servidor procesa la imagen a través de un algoritmo de Inteligencia Artificial para determinar el estado o color del objeto y retorna una estructura JSON.
4. **Actuación y Clasificación:** El microcontrolador parsea la respuesta JSON recibida:
   * **Anomalía Tipo A (Rojo):** Mueve el servomotor a 90° por un lapso de 10 segundos para desviar el objeto al contenedor correspondiente.
   * **Anomalía Tipo B (Amarillo):** Mueve el servomotor a 180° por un lapso de 10 segundos.
   * **Objeto Correcto (Verde):** El servomotor permanece en reposo (0°).
5. **Reanudación:** Cumplido el tiempo de actuación, el servomotor retorna a su posición inicial, la cinta transportadora vuelve a encenderse y el sistema se reconfigura para esperar el siguiente elemento.

---

## 🛠️ Arquitectura de Hardware y BOM (Lista de Materiales Principales)
El diseño electrónico de la PCB fue optimizado mediante un plano de masa continuo y una estricta separación física entre las trazas de **potencia** (motores y actuadores) y **señal** (líneas de datos de la cámara y líneas de control del microcontrolador) para mitigar ruidos electromagnéticos parásitos.

### Componentes Clave de la Placa:
| Descripción | Cantidad | Función Principal |
| :--- | :---: | :--- |
| **ESP32-S3-WROOM-2-N32R16V** | 1 | Microcontrolador central (Núcleo del sistema, WiFi, manejo de PSRAM). |
| **Módulo de Cámara OV5640 DVP** | 2 | Captura de imágenes con interfaz digital de video en paralelo. |
| **Driver de Motor DRV8873SPWPRQ1** | 2 | Controlador de puente H de grado automotriz para el motor DC de la cinta. |
| **Conector USB Tipo C (16 pines SMD)** | 1 | Interfaz de alimentación y programación general de la placa. |
| **Regulador LDO LM1117MPX-3.3/NOPB** | 2 | Regulación de voltaje lineal para la etapa lógica digital de 3.3V. |
| **JSumo JS40F** | 1 | Sensor infrarrojo digital para la detección precisa del paso del objeto. |

### Tecnologías de Montaje y Manufactura Utilizadas:
* Aplicación homogénea de pasta de soldar mediante esténcil de precisión.
* Soldadura de componentes SMD mediante placa de calor (*Reflow*) y estación de aire caliente.
* Montaje manual de componentes de inserción (THT) con control térmico.
* Inspección visual y validación óptica/térmica con microscopio estereofónico de laboratorio.

---

## 📁 Estructura del Repositorio
* `/Hardware`: Contiene el proyecto completo desarrollado en KiCad (esquemáticos, layout de la PCB de 2 capas, archivos Gerber y planos finales de fabricación).
* `/Software`: Código fuente en C++/Arduino optimizado para el ESP32-S3, manejo de colas de tareas, captura de imágenes con `esp_camera`, y rutinas síncronas HTTP.
* `/BOM`: Archivo expandido `BOM.csv` con los costos detallados, números de parte (MPN) y enlaces a distribuidores de los componentes electrónicos implementados.

---

## 👥 Autores (MCT - 2026)
* **José Fabián Medina Dávalos** - [jfmedina@fiuna.edu.py](mailto:jfmedina@fiuna.edu.py) - +595 994 945 088
* **Sol Aramí Fernández Vargas** - [solfernandez@fiuna.edu.py](mailto:solfernandez@fiuna.edu.py) - +595 982 152 869
* **Gabriela Belén Orrego Arzamendia** - [gorrego@fiuna.edu.py](mailto:gorrego@fiuna.edu.py) - +595 983 141 917

*Facultad de Ingeniería, Universidad Nacional de Asunción (FIUNA)*
