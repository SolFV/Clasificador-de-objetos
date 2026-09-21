# 🏭 Clasificador Automático de Cajas con Visión Artificial
### Proyecto 4: Interfaz de Usuario y Estructura Mecánica de Cinta Transportadora

<div align="center">

![Status](https://img.shields.io/badge/Estado-1er%20Parcial%20Completado%20%26%20Validado-success?style=for-the-badge)
![Hardware](https://img.shields.io/badge/Hardware-NEMA%2023%20%2B%20ESP32--S3%20PCB-blue?style=for-the-badge)
![CAD](https://img.shields.io/badge/CAD-SolidWorks%202024-red?style=for-the-badge)
![Vision AI](https://img.shields.io/badge/Visi%C3%B3n-n8n%20%2B%20GPT--4o--mini-brightgreen?style=for-the-badge)
![Platform](https://img.shields.io/badge/Plataforma-ESP32--S3%20%28PSRAM%208MB%29-orange?style=for-the-badge)
![License](https://img.shields.io/badge/Licencia-MIT-yellow?style=for-the-badge)

**Sistema mecatrónico embebido de inspección de calidad y clasificación automatizada de paquetes sobre cinta transportadora, integrando visión artificial en la nube, tracción controlada por motor paso a paso, almacenamiento de evidencias y dashboard de supervisión en tiempo real.**

*Facultad de Ingeniería — Universidad Nacional de Asunción (FIUNA)*  
*Cátedra: Proyecto 4 (2do Ciclo 2026) | Septiembre de 2026*  
*Profesores: Prof. Ing. Federico Gaona, MSc. (Teoría) — Prof. Ing. Esteban Fretes, MSc. (Práctica)*

</div>

---

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Estado del Proyecto — Hitos del 1er Parcial](#-estado-del-proyecto--hitos-del-1er-parcial)
- [Esquema General del Sistema](#-esquema-general-del-sistema)
- [Arquitectura de Hardware](#️-arquitectura-de-hardware)
  - [1. Subsistema Mecánico y Estructura (SolidWorks)](#1-subsistema-mecánico-y-estructura-solidworks)
  - [2. Subsistema de Tracción (NEMA 23 + L298N)](#2-subsistema-de-tracción-nema-23--l298n)
  - [3. Subsistema Electrónico de Control (PCB ESP32-S3)](#3-subsistema-electrónico-de-control-pcb-esp32-s3)
- [Flujo de Operación y Lógica de Clasificación](#-flujo-de-operación-y-lógica-de-clasificación)
- [Infraestructura IoT y Servidor de Visión (n8n)](#-infraestructura-iot-y-servidor-de-visión-n8n)
- [Interfaz de Usuario (Dashboard de Supervisión)](#-interfaz-de-usuario-dashboard-de-supervisión)
- [Resultados Experimentales del Parcial](#-resultados-experimentales-del-parcial)
- [Costos del Proyecto](#-costos-del-proyecto)
- [Estructura del Repositorio](#-estructura-del-repositorio)
- [Autores y Agradecimientos](#-autores-fiuna--2026)
- [Licencia](#-licencia)

---

## 📖 Descripción General

El proyecto resuelve el **control de calidad en líneas de empaquetado** mediante un prototipo mecatrónico accesible y modular: una cinta transportadora desplaza cajas frente a una estación de inspección óptica (sensor infrarrojo de proximidad y cámara digital), un agente de visión artificial dictamina el estado del empaque y el sistema registra la evidencia fotográfica en la nube a la vez que acciona un mecanismo deflector para separar los productos defectuosos.

El alcance del **Primer Parcial del Proyecto 4** cierra la brecha entre la electrónica fabricada previamente en el Proyecto 3 y la operación física en planta, dotando al clasificador de:
1. **Estructura mecánica formal:** Chasis, rodillos, banda de caucho y soportes diseñados en SolidWorks con planos constructivos acotados.
2. **Tracción precisa con motor paso a paso:** Sustitución del motor DC de tracción por un motor paso a paso **NEMA 23** con controlador dedicado **L298N**, logrando un avance controlado con paradas repetibles de **1,2 s** frente a la cámara para eliminar el desenfoque por movimiento (*motion blur*).
3. **Pipeline de visión en la nube:** Servidor autoalojado **n8n** que procesa la imagen con **OpenAI `gpt-4o-mini`**, almacena evidencias en **Google Drive** y registros numéricos en **Google Sheets**.
4. **Dashboard web en tiempo real:** Interfaz de supervisión que lee la hoja de resultados cada 10 segundos, mostrando métricas, miniaturas de las cajas inspeccionadas y exportación a CSV.

---

## ✅ Estado del Proyecto — Hitos del 1er Parcial

> **El Primer Parcial del Proyecto 4 ha sido completado y validado experimentalmente con éxito en laboratorio.**

### Matriz de Hitos (P4 — Primer Parcial):

| Hito / Entregable | Estado | Observación |
| :--- | :---: | :--- |
| **Diseño mecánico en SolidWorks (CAD 3D)** | ✅ Completado | Modelos de bastidor, rodillos, engranajes y ensamble completo (`Ensamble.SLDASM`). |
| **Planos técnicos acotados de fabricación** | ✅ Completado | Planos 2D en PDF de engranaje conductor, conducido, soporte lateral y ensamble general. |
| **Tracción paso a paso NEMA 23 + L298N** | ✅ Completado | Paradas programadas de 1,2 s y avance exacto sin deslizamiento. |
| **Transmisión por engranajes rectos (3.8:1)** | ✅ Completado | Impresión 3D en PLA de piñón (z=10) y corona (z=38), módulo 4 mm. |
| **Flujo de Visión n8n + GPT-4o-mini** | ✅ Completado | Inferencia visual remota con latencia de 2,5 a 4,0 s. |
| **Registro Cloud (Google Drive + Sheets)** | ✅ Completado | Almacenamiento automático de fotos y fila de metadatos por evento. |
| **Dashboard Web de Supervisión** | ✅ Completado | Actualización continua cada 10 s, KPIs por estado, visor fotográfico y exportación CSV. |
| **Ensamble y validación del prototipo físico** | ✅ Completado | Estructura de contrachapado y varillas DIN 975 operativa y verificada en banco. |

---

## 🗺️ Esquema General del Sistema

El sistema opera mediante la interacción coordinada de cinco bloques funcionales:

```
                  +-------------------------------------------------------------+
                  |                      UNIDAD DE CONTROL                      |
                  |                Placa Propia ESP32-S3 (3.3V)                 |
                  +--------------+------------------------------+---------------+
                                 |                              |
               Señal de Detención|                              |Control de Desvío
               y Pulsos de Avance|                              |Pulsos PWM (0° / 180°)
                                 v                              v
            +--------------------+---------+          +---------+--------------------+
            |      SUBSISTEMA TRACCIÓN     |          |       SUBSISTEMA DESVÍO      |
            |     Driver L298N + NEMA 23   |          |    Servomotor SER0006        |
            |  Engranajes PLA (Red. 3.8:1) |          | Brazo Deflector (20s retenc.)|
            +--------------------+---------+          +------------------------------+
                                 |
                                 v
                     [ Cinta Transportadora ] <=== [ Cajas en Tránsito ]
                                 |
                          Detección IR (E18-D80NK)
                          Captura Foto (OV5640 SVGA)
                                 |
                                 v WiFi (HTTP POST Webhook)
            +--------------------+---------------------------------------------------+
            |                     SERVIDOR DE VISIÓN (n8n Autoalojado)               |
            |             Agente IA de Visión (OpenAI gpt-4o-mini)                  |
            +--------------------+------------------------------+--------------------+
                                 |                              |
                   Guarda Foto   v                 Guarda Fila  v
                         +-------+-------+              +-------+-------+
                         | Google Drive  |              | Google Sheets |
                         +-------+-------+              +-------+-------+
                                 |                              |
                                 |            Lectura cada 10 s |
                                 +--------------+---------------+
                                                v
                                 +--------------+---------------+
                                 |     DASHBOARD WEB (GUI)      |
                                 |  Supervisión en Tiempo Real  |
                                 +------------------------------+
```

---

## 🛠️ Arquitectura de Hardware

### 1. Subsistema Mecánico y Estructura (SolidWorks)
La cinta transportadora se diseñó íntegramente en SolidWorks bajo criterios de rigidez, repetibilidad y desmontaje modular:
- **Bastidor Lateral (`Tabla.SLDPRT` / [Soporte Lateral.pdf](Hardware/Diseno_Mecanico/Soporte%20Lateral.pdf)):** 2 placas de madera contrachapada de $700 \times 150\text{ mm}$ y $12\text{ mm}$ de espesor con perforaciones para varillas y rodamientos.
- **Tirantes Transversales (`Varilla.SLDPRT`):** 7 varillas roscadas de acero al carbono galvanizado bajo norma **DIN 975** (M6), aportando alta rigidez torsional.
- **Banda de Transporte (`Cinta.SLDPRT`):** Banda cerrada de caucho continuo con ancho útil de **200 mm**, compatible con cajas de hasta 150 mm.
- **Transmisión por Engranajes Rectos en PLA (Módulo 4 mm):**
  - **Engranaje Conductor (`Engranaje_C.SLDPRT` / [Engranaje_Conductor.pdf](Hardware/Diseno_Mecanico/Engranaje_Conductor.pdf)):** $z_1 = 10\text{ dientes}$, diámetro exterior $\varnothing 48.00\text{ mm}$, acople a eje del motor NEMA 23 de $\varnothing 8.00\text{ mm}$.
  - **Engranaje Conducido (`engranaje_G.SLDPRT` / [engranaje_conducido.PDF](Hardware/Diseno_Mecanico/engranaje_conducido.PDF)):** $z_2 = 38\text{ dientes}$, diámetro exterior $\varnothing 160.00\text{ mm}$, acople a eje motriz de $\varnothing 6.00\text{ mm}$.
  - **Relación de Reducción:** $i = 3.8:1$, cuadruplicando el par sobre el rodillo motriz y permitiendo una resolución de avance lineal de $\approx 0.165\text{ mm/paso}$.
- **Planos Técnicos:** Disponibles en [Hardware/Diseno_Mecanico/](Hardware/Diseno_Mecanico/) con el ensamble general en [Cinta transportadora.pdf](Hardware/Diseno_Mecanico/Cinta%20transportadora.pdf) y el informe técnico detallado en [Informe_Diseno_Estructural_Mecanico.md](Hardware/Diseno_Mecanico/Informe_Diseno_Estructural_Mecanico.md).

### 2. Subsistema de Tracción (NEMA 23 + L298N)
- **Motor:** Paso a paso híbrido bipolar NEMA 23 (**modelo 23HS5628**), ángulo de paso $1.8^\circ$ (200 pasos/rev) y torque nominal de **12.6 kgf-cm** (1.23 N·m).
- **Controlador:** Driver tipo puente H bipolar **L298N**, alimentado desde una línea de potencia externa dedicada y comandado por señales lógicas de 3.3V desde la placa ESP32-S3.

### 3. Subsistema Electrónico de Control (PCB ESP32-S3)
Se reutiliza la placa desarrollada y validada en el Proyecto 3 (alojada en [Hardware/Diseño final p3/](Hardware/Diseño%20final%20p3/)):
- **Microcontrolador:** ESP32-S3-WROOM-1-N16R8 (Dual-core 240 MHz, 16 MB Flash, 8 MB PSRAM verificada).
- **Cámara Óptica:** Módulo digital Omnivision **OV5640** conectado mediante interfaz de video en paralelo DVP (resolución de trabajo SVGA $800 \times 600$).
- **Sensor de Presencia:** Sensor fotoeléctrico infrarrojo **E18-D80NK** enfocado a la zona de captura (tiempo de respuesta $< 50\text{ ms}$).
- **Actuador de Desvío:** Servomotor **DFRobot SER0006** montado con brazo deflector mecánico a la salida del tramo de inspección.

---

## 🔄 Flujo de Operación y Lógica de Clasificación

El lazo de control opera de acuerdo a la siguiente secuencia automática:

1. **Avance Continuo:** El motor NEMA 23 hace avanzar la cinta transportadora de manera uniforme.
2. **Detección Frontal:** El sensor infrarrojo E18-D80NK detecta la llegada del paquete e interrumpe el avance.
3. **Parada y Captura:** La cinta se detiene exactamente durante **1,2 segundos**. La cámara OV5640 toma una fotografía nítida en SVGA almacenándola en la PSRAM.
4. **Transmisión IoT:** El ESP32-S3 envía la imagen por WiFi al webhook de n8n mediante HTTP POST.
5. **Inferencia Visual:** El agente de IA (`gpt-4o-mini`) analiza la morfología y condiciones del empaque, dictaminando la categoría:
   - 🟢 **Verde (Buen Estado):** Caja intacta, correctamente sellada y lista para despacho.
   - 🟡 **Amarillo (Sin Caja o No Empaque):** Falsa alarma, objeto extraño o cinta vacía.
   - 🔴 **Rojo (Caja Dañada):** Paquete aplastado, roto, perforado o con defectos visibles.
6. **Almacenamiento Cloud:** El flujo guarda la imagen en una carpeta compartida de **Google Drive** y registra una nueva fila en **Google Sheets** con timestamp, color y descripción detallada.
7. **Acción del Servomotor:** El ESP32-S3 recibe la respuesta `[{color, descripcion}]` y ejecuta la maniobra:
   - 🟢 **Verde:** El brazo deflector permanece a **0°** (la caja continúa sin desvío).
   - 🟡 / 🔴 **Amarillo o Rojo:** El servomotor gira a **180°** y mantiene la posición de desvío durante **20 segundos** para enviar la pieza a la bandeja de rechazo.
8. **Reanudación:** El servomotor retorna a posición neutra (0°) y la cinta reanuda la marcha.

---

## 🤖 Infraestructura IoT y Servidor de Visión (n8n)

El procesamiento cognitivo se delega íntegramente a un servidor local **n8n autoalojado** (*Workflow Proyecto 4*):

```
[Webhook HTTP] ──▶ [Recibir imagen y Nombrar] ──▶ [Base64 a Binario] ──▶ [AI Agent (gpt-4o-mini)]
                                                                                │
[Responder al Webhook: {color, desc}] ◀── [Limpiar Output Agente] ◀─────────────┘
          │
          ├──▶ [Subir a Google Drive]
          └──▶ [Subir a Google Sheets]
```

- **Modelo de Inferencia:** OpenAI `gpt-4o-mini` configurado con *system prompt* especializado en control de calidad industrial.
- **Respuesta JSON:** Formato estandarizado `[ { "color": "verde" | "amarillo" | "rojo", "descripcion": "..." } ]`.
- **Ventaja de la Arquitectura:** Libera al microcontrolador de tareas pesadas de redes neuronales, acelerando la toma de decisiones ($2.5 - 4.0\text{ s}$).

---

## 🖥️ Interfaz de Usuario (Dashboard de Supervisión)

El monitoreo del sistema se realiza a través de un **Dashboard Web en Tiempo Real** de un solo archivo:
- **Consulta Automática:** Sondea la planilla de Google Sheets (*Proyecto4 - Hoja 1*) cada **10 segundos**.
- **Panel de KPIs:** Conteo total de cajas evaluadas, desglose por categoría (verde, amarillo, rojo) y cálculo porcentual.
- **Tarjeta de Última Detección:** Muestra la fotografía en miniatura cargada desde Google Drive, el estado asignado, la descripción diagnóstica de la IA y el ángulo/tiempo aplicado por el servomotor.
- **Historial y Exportación:** Tabla con las últimas 5 detecciones en vivo y botón para exportar todo el registro histórico acumulado a formato **CSV**.
- **Accesos Directos:** Enlaces directos a la planilla en línea y a la carpeta de evidencias en Drive.
- **Modo Seguro:** Opera en modo solo lectura para supervisión remota sin interferir en la máquina de estados del firmware.

---

## 📊 Resultados Experimentales del Parcial

Durante las pruebas funcionales del prototipo integrado se validaron las siguientes métricas de desempeño:

| Parámetro Operativo | Valor Medido | Valor Esperado / Criterio |
| :--- | :---: | :---: |
| **Tiempo de parada de cinta frente a cámara** | **1,2 s** | Repetible, sin desenfoque de imagen |
| **Latencia de detección del sensor IR** | **< 50 ms** | Detención instantánea del NEMA 23 |
| **Tiempo de respuesta inferencia n8n** | **2,5 a 4,0 s** | Dentro del ciclo de cinta previsto |
| **Retención del servomotor en rechazo (180°)** | **20 s** | Desvío seguro al contenedor de descarte |
| **Frecuencia de actualización del Dashboard** | **10 s** | Supervisión fluida de métricas Cloud |
| **Resolución cinemática de la cinta** | **~0,165 mm/paso** | Posicionamiento milimétrico repetible |

---

## 💰 Costos del Proyecto

La estructura de costos del prototipo se basa en una tasa oficial de referencia de **1 USD = 6.391 Gs.**:

### 1. Materiales y Herramientas Compradas (Incorporadas al Proyecto 4)

| Descripción / Componente | MPN / Modelo | Cant. | Unitario (USD) | Total (USD) | Total (Gs.) |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Motor paso a paso NEMA 23 (12.6 kgf-cm)** | 23HS5628 | 1 | 46,94 | 46,94 | 300.000 |
| **Microcontrolador ESP32-S3** | ESP32-S3-WROOM-1-N16R8 | 2 | 8,72 | 17,44 | 111.459 |
| **Módulo de cámara OV5640** | 5840 (Adafruit) | 1 | 9,95 | 9,95 | 63.590 |
| **Capacitor electrolítico 100 uF** | EEE-FK1H101P | 6 | 0,76 | 4,56 | 29.143 |
| **Servomotor 180°** | SER0006 | 1 | 3,63 | 3,63 | 23.199 |
| **Capacitor cerámico 4,7 uF 0805** | CL21B475KOFNNNE | 6 | 0,25 | 1,50 | 9.587 |
| **Diodo Schottky 40 V, 1 A** | SS14F-HF | 4 | 0,32 | 1,28 | 8.180 |
| **Conector USB tipo C hembra** | USB4510-03-1-A | 1 | 0,84 | 0,84 | 5.368 |
| **Capacitor cerámico 10 uF 0805** | CL21B106KOQNNNE | 6 | 0,14 | 0,84 | 5.368 |
| **Capacitor cerámico 22 uF 0805** | CL21A226MQQNNNE | 6 | 0,13 | 0,78 | 4.985 |
| **Resistencia 15 kOhm 0805** | RC0805FR-0715KL | 6 | 0,13 | 0,78 | 4.985 |
| **Capacitor cerámico 2,2 uF 0805** | CL21B225KOFNNNE | 6 | 0,12 | 0,72 | 4.602 |
| **Regulador LDO 3,3 V** | LD1117S33TR | 2 | 0,34 | 0,68 | 4.346 |
| **Resistencia 10 kOhm 0805** | RC0805FR-0710KL | 6 | 0,11 | 0,66 | 4.218 |
| **Capacitores y Resistencias SMD 0805 varios** | Pasivos estándar | — | — | 6,14 | 39.299 |
| **SUBTOTAL COMPONENTES COMPRADOS** | — | — | — | **$96,06** | **644.219 Gs.** |

> *Costo por integrante (3 personas): **$32.02 USD (214.740 Gs.)**.*  
> El desglose línea a línea con enlaces y números de parte se encuentra en el archivo [BOM.csv](BOM.csv).

### 2. Materiales y Herramientas Preexistentes o Prestadas (Sin costo incremental)

| Descripción | Cantidad | Observaciones / Procedencia |
| :--- | :---: | :--- |
| **Placa propia ESP32-S3 con cámara OV5640** | 1 | Fabricada y validada en Proyecto 3 |
| **Servomotor de desvío SER0006** | 1 | Reutilizado de la etapa previa |
| **Controlador L298N para motor paso a paso** | 1 | Driver provisto para tracción |
| **Notebook con servidor n8n autoalojado** | 1 | Estación de inferencia local |
| **Licencia / estación SolidWorks CAD** | 1 | Laboratorio FIUNA |
| **Multímetro y herramientas de banco/taller** | 1 | Instrumental de ensamblaje y prueba |

---

## 📁 Estructura del Repositorio

```
📦 Clasificador de Objetos/
├── 📂 Hardware/                                # Proyectos de diseño físico y electrónico
│   ├── 📂 Diseno_Mecanico/                     # Diseño mecánico CAD y planos (P4)
│   │   ├── 📄 Informe_Diseno_Estructural_Mecanico.md # Informe técnico mecánico detallado
│   │   ├── 📄 Cinta transportadora.pdf         # Plano de ensamble general, despiece y lista de partes
│   │   ├── 📄 Soporte Lateral.pdf              # Plano acotado de soporte lateral de madera contrachapada
│   │   ├── 📄 Engranaje_Conductor.pdf          # Plano acotado de engranaje conductor (z=10, m=4)
│   │   ├── 📄 engranaje_conducido.PDF          # Plano acotado de engranaje conducido (z=38, m=4)
│   │   ├── 🧊 Ensamble.SLDASM                  # Ensamblaje 3D general en SolidWorks
│   │   ├── 🧊 Cinta.SLDPRT                     # Modelo 3D de la banda transportadora
│   │   ├── 🧊 Engranaje_C.SLDPRT               # Modelo 3D de engranaje conductor (PLA)
│   │   ├── 🧊 engranaje_G.SLDPRT               # Modelo 3D de engranaje conducido (PLA)
│   │   ├── 🧊 Tabla.SLDPRT                     # Modelo 3D de soporte lateral
│   │   ├── 🧊 Varilla.SLDPRT                   # Modelo 3D de varilla roscada DIN 975
│   │   └── 🧊 nema23.SLDPRT                    # Modelo 3D de motor paso a paso NEMA 23
│   ├── 📂 Componentes/                         # Bibliotecas KiCad personalizadas
│   ├── 📂 Diseño final p3/                     # Proyecto final KiCad de la PCB (Proyecto 3)
│   │   └── 📂 Grupo15/                         # Esquemático, ruteado, producción y Gerbers
│   ├── 📂 ESP32_S3_WROOM2_N32R16_29/           # Footprint y símbolo de la MCU
│   ├── 🖼️ Diseño.jpeg                          # Esquemático general del circuito
│   ├── 🖼️ pcb_ruteado.PNG                       # Ruteado y layout de 2 capas
│   ├── 🖼️ pcb_3d.PNG                           # Render 3D superior de la PCB
│   ├── 🖼️ pcb_3d_1.png                         # Render 3D superior sin componentes
│   └── 🖼️ pcb_3d_vistinferior.png              # Render 3D inferior de la PCB
├── 📂 Software/                                # Firmware y aplicaciones de control
│   ├── 📄 P3_2P_code.txt                       # Firmware C++/Arduino para ESP32-S3
│   ├── 📂 SnapshotP4.0/                        # Aplicación Android para inspección
│   │   └── 📱 Cinta IA.apk                     # Instalador ejecutable Android
│   └── 📂 Pruebas/                             # Scripts de validación de cámara y PSRAM
├── 📄 BOM.csv                                  # Lista de materiales y costos actualizada (USD / Gs.)
├── 📄 Informe_finalP3.pdf                      # Informe oficial completo del Proyecto 3
├── 📄 Cronograma de actividades P4.pdf         # Cronograma oficial de la Etapa P4 (PDF)
├── 📊 Cronograma de actividades.xlsx           # Planilla Excel de seguimiento
├── 🖼️ DiagramadeGantt.png                     # Diagrama de Gantt oficial
├── 🖼️ diagrama-flujo.png                       # Diagrama de flujo general del sistema
├── 📄 LICENSE                                  # Licencia de código abierto MIT
└── 📄 README.md                                # Documentación principal del repositorio
```

---

## 👥 Autores (FIUNA — 2026)

* **José Fabián Medina Dávalos** — [jfmedina@fiuna.edu.py](mailto:jfmedina@fiuna.edu.py)
* **Sol Aramí Fernández Vargas** — [solfernandez@fiuna.edu.py](mailto:solfernandez@fiuna.edu.py)
* **Gabriela Belén Orrego Arzamendia** — [gorrego@fiuna.edu.py](mailto:gorrego@fiuna.edu.py)

**Facultad de Ingeniería — Universidad Nacional de Asunción (FIUNA)**  
*Cátedra de Proyecto 4 — Carrera de Ingeniería Mecatrónica*  
*Docentes:* Prof. Ing. Federico Gaona, MSc. (Teoría) & Prof. Ing. Esteban Fretes, MSc. (Práctica)

---

## 📄 Licencia

Este proyecto está distribuido bajo la **Licencia MIT**. Para más detalles, consulte el archivo [LICENSE](LICENSE).

---

<div align="center">

*Clasificador Automático de Cajas con Visión Artificial — FIUNA — 2026*

</div>
