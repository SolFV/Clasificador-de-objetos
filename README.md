# 🏭 Clasificador de Objetos mediante Cinta Transportadora Impulsado por IA

<div align="center">

![Status](https://img.shields.io/badge/Estado-Proyecto%20Finalizado-brightgreen?style=for-the-badge)
![Hardware](https://img.shields.io/badge/Hardware-Ensamblado%20%26%20Verificado-blue?style=for-the-badge)
![Platform](https://img.shields.io/badge/Plataforma-ESP32--S3-orange?style=for-the-badge)
![Accuracy](https://img.shields.io/badge/Precisión-93.33%25-success?style=for-the-badge)
![License](https://img.shields.io/badge/Licencia-MIT-yellow?style=for-the-badge)

**Sistema embebido de clasificación automatizada de objetos sobre cinta transportadora, integrando visión artificial, conectividad IoT y control en tiempo real.**

*Proyecto 3 — Mecatrónica (MCT) — FIUNA, 2026*

</div>

---

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Estado del Proyecto](#-estado-del-proyecto--proyecto-finalizado)
- [Cómo Funciona](#-cómo-funciona)
- [Resultados Obtenidos](#-resultados-obtenidos)
- [Arquitectura de Hardware](#️-arquitectura-de-hardware)
- [Servidor de IA](#-servidor-de-ia--infraestructura-iot)
- [Software y Firmware](#-software-y-firmware)
- [Costos del Proyecto](#-costos-del-proyecto)
- [Manufactura y Ensamblaje](#-técnicas-de-manufactura-y-ensamblaje)
- [Estructura del Repositorio](#-estructura-del-repositorio)
- [Diagrama de Flujo](#-diagrama-de-flujo)
- [Autores](#-autores-mct--2026)
- [Licencia](#-licencia)

---

## 📖 Descripción General

Este repositorio contiene el firmware, los diseños de hardware y la documentación completa del sistema embebido inteligente de clasificación automatizada de objetos en una cinta transportadora. El proyecto integra **visión artificial**, **conectividad IoT** mediante un servidor de IA auto-hospedado y **control secuencial en tiempo real** sobre un microcontrolador ESP32-S3.

El sistema captura imágenes de objetos transportados por una cinta, las envía a un modelo de Inteligencia Artificial alojado en un servidor privado **n8n**, y en función de la clasificación recibida, acciona un servomotor que desvía los objetos anómalos a contenedores diferenciados.

---

## ✅ Estado del Proyecto — Proyecto Finalizado

> **El proyecto ha sido completado exitosamente con resultados validados experimentalmente.**

Se han superado con éxito todas las fases del proyecto: diseño esquemático, ruteo en KiCad, fabricación y ensamblaje de la PCB, integración completa de software/hardware y validación final con un lote de prueba de 30 cajas.

| Hito | Estado |
| :--- | :---: |
| Diseño esquemático y PCB en KiCad | ✅ Completado |
| Fabricación y ensamblaje de la PCB | ✅ Completado |
| Validación de subsistemas individuales | ✅ Completado |
| Integración total Software / Hardware | ✅ Completado |
| Configuración del servidor de IA | ✅ Completado |
| Pruebas finales con lote de 30 cajas | ✅ Completado |
| Precisión de clasificación alcanzada | **93.33%** |

### Subsistemas Verificados:

- 🟢 **Adquisición Óptica:** Cámara OV5640 operando en modo SVGA (800×600), fotogramas YUV422 almacenados en PSRAM (8 MB verificados).
- 🟢 **Conectividad IoT:** Conexión WiFi estable, envío exitoso del payload (Base64 + binario JPEG) al webhook del servidor autoalojado. Respuestas del modelo de IA recibidas correctamente.
- 🟢 **Motor DC y Cinta:** PWM a 20 kHz con 39.2% de duty cycle, desplazamiento uniforme gestionado por driver DRV8873.
- 🟢 **Clasificación Mecánica:** Servomotor DFRobot SER0006 con respuesta precisa (0°, 90° o 180°) y tiempo de retención de 5 segundos.
- 🟢 **Sensor de Proximidad:** Sensor IR E18-D80NK con detección fiable del paso de objetos.

---

## ⚙️ ¿Cómo Funciona?

El lazo cerrado de control y toma de decisiones distribuidas opera en cinco etapas secuenciales:

### 1️⃣ Transporte y Detección
El **motor DC NOVAMAX 6V 800 RPM** desplaza los objetos sobre la cinta transportadora mediante modulación **PWM (20 kHz, 39.2% duty cycle)** gestionada por el driver de potencia **DRV8873SPWPRQ1**. Cuando el sensor infrarrojo de proximidad **E18-D80NK** detecta un objeto, el ESP32-S3 detiene la cinta inmediatamente.

### 2️⃣ Captura en Borde (Edge Computing)
La cámara **OV5640** captura un fotograma en resolución **SVGA (800×600)** en formato **YUV422**, que se almacena en la **PSRAM**. Posteriormente, se comprime a formato **JPEG** y se codifica en **Base64** para optimizar la transmisión.

### 3️⃣ Inferencia Remota (IoT)
El ESP32-S3 realiza una petición **HTTP POST Multipart** dirigida al webhook alojado en el servidor privado autoalojado. El modelo de Inteligencia Artificial evalúa la imagen y retorna una estructura **JSON** con la clasificación por color.

### 4️⃣ Actuación y Clasificación
El microcontrolador parsea la respuesta JSON recibida y acciona el **servomotor DFRobot SER0006**:

| Color | Significado | Acción del Servomotor | Duración |
| :---: | :--- | :--- | :---: |
| 🟢 **Verde** | Objeto válido / correcto | Permanece en **0°** — el objeto pasa libremente | — |
| 🟡 **Amarillo** | Anomalía Tipo A | Gira a **90°** — desvía al contenedor A | 5 segundos |
| 🔴 **Rojo** | Anomalía Tipo B | Gira a **180°** — desvía al contenedor B | 5 segundos |

### 5️⃣ Reanudación
Cumplido el tiempo de actuación, el servomotor retorna a su posición inicial (0°), la cinta transportadora se reactiva y el sistema se reconfigura para esperar el siguiente objeto.

---

## 📊 Resultados Obtenidos

Los siguientes resultados fueron obtenidos en la validación final del sistema con un lote de prueba de **30 cajas** (10 por cada categoría de color):

### Precisión de Clasificación: **93.33%**

| Categoría | Cajas Evaluadas | Aciertos | Errores | Precisión |
| :---: | :---: | :---: | :---: | :---: |
| 🟢 Verde (Válido) | 10 | 9 | 1 | 90% |
| 🟡 Amarillo (Anomalía A) | 10 | 10 | 0 | **100%** |
| 🔴 Rojo (Anomalía B) | 10 | 9 | 1 | 90% |
| **Total** | **30** | **28** | **2** | **93.33%** |

> **Nota:** Los 2 errores se atribuyeron a factores ópticos (múltiples objetos en el encuadre y problemas de enmarcado), **no a fallas de hardware**.

### Rendimiento del Sistema

| Parámetro | Valor Medido | Valor Teórico | Desviación |
| :--- | :---: | :---: | :---: |
| PWM del Motor DC | 20 kHz, 39.2% duty | — | Desplazamiento uniforme |
| Tiempo de retención del servo | 5012 ms | 5000 ms | 0.24% |
| Tensión línea 3.3V | 3.27 V | 3.3 V | Estable |
| Pico de corriente soportado | 2.1 A | — | Sin caídas |
| Tiempo de ciclo por caja | ~1 minuto | — | — |

---

## 🛠️ Arquitectura de Hardware

El diseño electrónico de la PCB de **2 capas** fue optimizado mediante un plano de masa continuo y una estricta separación física entre las trazas de **potencia** (motores y actuadores) y **señal** (líneas de datos de la cámara y líneas de control del microcontrolador) para mitigar ruidos electromagnéticos parásitos.

### Componentes Principales de la Placa

| Componente | Referencia | Cant. | Función Principal |
| :--- | :--- | :---: | :--- |
| **Microcontrolador** | ESP32-S3-WROOM-1-N16R8 | 1 | Núcleo del sistema: WiFi, control, manejo de PSRAM (8 MB) |
| **Cámara** | OV5640 DVP | 1 | Captura SVGA 800×600, interfaz digital de video en paralelo |
| **Driver de Motor** | DRV8873SPWPRQ1 | 2 | Puente H de grado automotriz para motor DC |
| **Regulador LDO** | LD1117S33TR | 2 | Regulación lineal de voltaje a 3.3V para la etapa digital |
| **Conector USB** | USB4510-03-1-A (Type-C) | 1 | Alimentación y programación, 16 pines SMD |
| **Servomotor** | DFRobot SER0006 | 1 | Actuador de clasificación (0°/90°/180°) |
| **Motor DC** | NOVAMAX 6V 800 RPM | 1 | Tracción de la cinta transportadora |
| **Sensor IR** | E18-D80NK | 1 | Detección de proximidad por infrarrojos |
| **Diodo Schottky** | SS14F-HF | — | Protección contra corrientes inversas |

### Especificaciones Eléctricas Clave

| Parámetro | Especificación |
| :--- | :--- |
| Alimentación de entrada | 5V vía USB Type-C |
| Regulación lógica | 3.3V (LD1117S33TR × 2) |
| Frecuencia PWM del motor | 20 kHz |
| Duty cycle del motor | 39.2% |
| Interfaz de cámara | DVP paralelo (YUV422) |
| Resolución de captura | SVGA (800 × 600 px) |
| Memoria PSRAM | 8 MB (verificada) |
| Conectividad | WiFi 802.11 b/g/n (2.4 GHz) |

---

## 🤖 Servidor de IA — Infraestructura IoT

La clasificación de objetos se realiza mediante un flujo de automatización alojado en un servidor privado autoalojado (*self-hosted*), que actúa como puente entre el ESP32-S3 y el modelo de Inteligencia Artificial.

### Flujo de Comunicación

```
ESP32-S3  ──HTTP POST Multipart──▶  Servidor Autoalojado (Webhook)  ──▶  Modelo de IA
                                                                      │
ESP32-S3  ◀──── JSON { "color": "..." } ◀──────────────────────────────┘
```

### Detalles Técnicos

| Aspecto | Detalle |
| :--- | :--- |
| **Plataforma** | Servidor autoalojado (self-hosted) |
| **Protocolo** | HTTP POST Multipart |
| **Payload de envío** | Base64 (texto) + Binario JPEG |
| **Formato de respuesta** | JSON (`{ "color": "green" \| "yellow" \| "red" }`) |
| **Seguridad** | WiFiClientSecure (HTTPS) |
| **Latencia** | Dentro de los márgenes operativos previstos |

El servidor autoalojado recibe la imagen capturada por la cámara OV5640, la procesa a través del modelo de IA, y devuelve la clasificación como un campo `color` en formato JSON que el ESP32-S3 interpreta para accionar el servomotor correspondiente.

---

## 💻 Software y Firmware

### Bibliotecas Utilizadas

| Biblioteca | Propósito |
| :--- | :--- |
| `esp_camera.h` | Inicialización y control de la cámara OV5640 vía DVP |
| `img_converters.h` | Conversión y compresión de fotogramas a JPEG |
| `WiFi.h` | Gestión de la conexión WiFi del ESP32-S3 |
| `HTTPClient.h` | Peticiones HTTP POST hacia el webhook del servidor autoalojado |
| `WiFiClientSecure.h` | Capa de seguridad TLS/HTTPS para las comunicaciones |
| `ESP32Servo.h` | Control PWM del servomotor DFRobot SER0006 |

### Algoritmos Implementados

| Función | Descripción |
| :--- | :--- |
| `b64_encode()` | Codificación Base64 nativa procesando el JPEG almacenado en PSRAM |
| `sendToWebhook()` | Empaqueta Base64 + binario JPEG en formato Multipart para el webhook |
| `parseColor()` | Parser JSON que extrae el campo `color` de la respuesta del servidor |
| `moveServoForColor()` / `procesarColor()` | Control del actuador según la clasificación recibida |

### Archivo Principal

- 📄 **`Software/P3_2P_code.txt`** — Firmware de producción completo (C++/Arduino para ESP32-S3)

### Scripts de Prueba (`Software/Pruebas/`)

| Archivo | Descripción |
| :--- | :--- |
| `Prueba camara.txt` | Test de inicialización y captura de la cámara OV5640 |
| `Prueba PSRAM.txt` | Diagnóstico de la PSRAM (8 MB confirmados funcionales) |
| `Resultado camara.txt` | Log de resultados de la prueba de cámara |
| `Resultados PSRAM.txt` | Log de resultados del diagnóstico de PSRAM |

---

## 💰 Costos del Proyecto

| Concepto | Monto (USD) | Monto (PYG) |
| :--- | :---: | :---: |
| **Inversión total** | $62.12 | 397,009 |
| **Costo por integrante** (×3) | $20.71 | 132,336 |

> El detalle completo de materiales, números de parte (MPN) y enlaces a distribuidores se encuentra en el archivo **`BOM.csv`**.

---

## 🔧 Técnicas de Manufactura y Ensamblaje

El proceso de fabricación y ensamblaje de la PCB siguió un flujo profesional de producción:

1. **Aplicación de pasta de soldar** mediante esténcil de precisión para deposición homogénea.
2. **Soldadura por reflujo** de componentes SMD sobre placa de calor (*hot plate reflow*).
3. **Correcciones con estación de aire caliente** y aplicación de flux para retoques.
4. **Soldadura manual THT** de componentes de inserción (USB Type-C, botones, conectores).
5. **Inspección óptica y térmica** bajo microscopio estereoscópico de laboratorio.
6. **Limpieza final** con alcohol isopropílico (IPA).

---

## 📁 Estructura del Repositorio

```
📦 Clasificador de Objetos/
├── 📂 Hardware/                          # Proyecto completo en KiCad
│   ├── 📂 Componentes/                   # Bibliotecas KiCad personalizadas (17 componentes)
│   │   ├── ESP32-S3-WROOM2-N32R16V/
│   │   ├── LDO lm1117/
│   │   ├── driver_dvr8873/
│   │   ├── diodo schottky/
│   │   └── ... (resistencias, capacitores, headers, etc.)
│   ├── 📂 Diseño final/                  # Archivos finales del proyecto KiCad
│   │   ├── Grupo15.kicad_pcb
│   │   ├── Grupo15.kicad_sch
│   │   └── Grupo15.kicad_pro
│   ├── 📂 ESP32_S3_WROOM2_N32R16_29/     # Footprint y símbolo del ESP32-S3
│   ├── 🖼️ Diseño.jpeg                    # Esquemático del diseño
│   ├── 🖼️ Placa pcb.jpeg                 # Layout de la PCB
│   ├── 🖼️ Placa 3D.jpeg                  # Render 3D (vista inferior)
│   └── 🖼️ Placa 3D parte superior.jpeg   # Render 3D (vista superior)
├── 📂 Software/                          # Firmware en C++/Arduino
│   ├── 📄 P3_2P_code.txt                 # Firmware de producción principal
│   └── 📂 Pruebas/                       # Scripts y resultados de pruebas
│       ├── Prueba camara.txt
│       ├── Prueba PSRAM.txt
│       ├── Resultado camara.txt
│       ├── Resultados PSRAM.txt
│       ├── 🖼️ Imagen_camara.jpeg
│       └── 🖼️ Evidencia_fecha_de_resultado.jpeg
├── 📄 BOM.csv                            # Lista de materiales con costos y MPN
├── 🖼️ diagrama-flujo.png                 # Diagrama de flujo del sistema
├── 📄 LICENSE                            # Licencia MIT
└── 📄 README.md                          # Este archivo
```

---

## 🔄 Diagrama de Flujo

<div align="center">

![Diagrama de Flujo del Sistema](diagrama-flujo.png)

</div>

---

## 👥 Autores (MCT — 2026)

* **José Fabián Medina Dávalos** - [jfmedina@fiuna.edu.py](mailto:jfmedina@fiuna.edu.py) 
* **Sol Aramí Fernández Vargas** - [solfernandez@fiuna.edu.py](mailto:solfernandez@fiuna.edu.py) 
* **Gabriela Belén Orrego Arzamendia** - [gorrego@fiuna.edu.py](mailto:gorrego@fiuna.edu.py) 

*Facultad de Ingeniería, Universidad Nacional de Asunción (FIUNA)*

---

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT**. Consulte el archivo [LICENSE](LICENSE) para más detalles.

---

<div align="center">

*Proyecto 3 — Mecatrónica (MCT) — FIUNA — Universidad Nacional de Asunción — 2026*

</div>
