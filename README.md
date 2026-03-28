# 🏭 Cinta Transportadora Inteligente - Clasificador de Objetos con IA e IoT

![Status: WIP](https://img.shields.io/badge/Status-Work_In_Progress-orange)
![License: MIT](https://img.shields.io/badge/License-MIT-blue)
![Hardware: ESP32-S3](https://img.shields.io/badge/Hardware-ESP32--S3-success)

## 📖 Descripción del Proyecto

Este proyecto consiste en el diseño, fabricación e implementación de una placa electrónica (PCB) basada en el microcontrolador **ESP32-S3**, destinada al control y supervisión de una cinta transportadora inteligente. 

El sistema tiene como propósito principal la **clasificación automatizada de objetos** integrando tecnologías de visión artificial e Internet de las Cosas (IoT). 

### ⚙️ ¿Cómo funciona?
1. **Captura:** A través de un módulo de cámara (OV5640), el hardware captura imágenes de los elementos que pasan por la cinta.
2. **Procesamiento IoT:** Las imágenes se transmiten vía WiFi (mediante un webhook) a un servidor externo.
3. **Inferencia IA:** Un algoritmo de inteligencia artificial en el servidor evalúa si el objeto es correcto o defectuoso.
4. **Acción:** La PCB recibe la decisión del servidor y acciona un servomotor para desviar los elementos anómalos, mientras gestiona la velocidad y el estado del motor DC principal de la cinta.

---

## 🚧 Estado del Proyecto: En Desarrollo (WIP)

Actualmente, el proyecto se encuentra en la **Fase de Diseño de Hardware**. Estamos utilizando **KiCad** para finalizar el ruteo de la PCB (separando cuidadosamente las etapas de potencia y señal) y preparándola para su impresión y posterior ensamblaje.

---

## ✨ Características Principales

* **Control de Motores Integrado:** Gestión de motor DC de 6V (800RPM) mediante el driver DRV8873-Q1 y control de servomotor para clasificación física.
* **Visión Artificial embebida:** Integración de cámara para captura de imágenes en tiempo real.
* **Comunicación de Baja Latencia:** Transmisión de datos mediante webhooks sobre WiFi para integrarse con modelos de Deep Learning externos.
* **Diseño Industrial:** PCB diseñada considerando prevención de fallas térmicas y aislamiento entre etapas de señal y potencia.

---

## 🛠️ Hardware y Lista de Materiales (BOM Principal)

El diseño de la PCB se basa en los siguientes componentes críticos:

| Componente | Especificación / Modelo | Función |
| :--- | :--- | :--- |
| **Microcontrolador (MCU)** | ESP32-S3-WROOM-2-N32R16V | Procesamiento, WiFi y control general |
| **Driver de Motor** | Texas Instruments DRV8873-Q1 | Control del motor DC de la cinta |
| **Regulador LDO** | LM1117MPX-3.3 | Alimentación estable a 3.3V |
| **Motor DC** | NOVAMAX 6V 5.75A 800RPM | Impulso de la cinta transportadora |
| **Cámara** | OV5640 (Color CMOS) | Captura de imágenes para clasificación |

*(Para ver la lista completa de componentes pasivos como capacitores cerámicos SMD 0805 y resistencias, consultar el archivo BOM en el repositorio).*

---

## 💻 Tecnologías / Stack

* **Hardware / EDA:** KiCad
* **Firmware MCU:** C/C++
* **Comunicaciones:** WiFi / Webhooks
* **Backend / IA:** (Servidor externo)

---

## 👥 Autores y Colaboradores

Este proyecto está siendo desarrollado por:

* **[José Fabián Medina Dávalos]** - [https://github.com/Jfmedina-cyber]
* **[Sol Arami Fernández Vargas]** - [https://github.com/SolFV]
* **[Gabriela Belén Orrego Arzamendia]** [https://github.com/gabriela-orrego]

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT**. Consulta el archivo `LICENSE` para más detalles.
