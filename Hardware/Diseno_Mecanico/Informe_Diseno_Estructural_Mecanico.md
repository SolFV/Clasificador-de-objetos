# 📐 Informe Técnico: Diseño Estructural y Mecánico de la Cinta Transportadora
**Proyecto 4 — Clasificador Automático de Cajas con Visión Artificial: Interfaz de Usuario y Estructura Mecánica de Cinta Transportadora**  
*Facultad de Ingeniería — Universidad Nacional de Asunción (FIUNA)*  
*Cátedra: Proyecto 4 (2do Ciclo 2026) | Profesores: Prof. Ing. Federico Gaona, MSc. — Prof. Ing. Esteban Fretes, MSc.*  
*Autores: José Fabián Medina Dávalos, Sol Aramí Fernández Vargas, Gabriela Belén Orrego Arzamendia*

---

## 1. 📌 Descripción General y Alcance del Diseño Mecánico

El presente informe técnico describe la arquitectura estructural, componentes de ensamble, cálculos cinemáticos y planos de fabricación del sistema mecánico de la cinta transportadora del **Proyecto 4 (Primer Parcial)**.

En esta etapa, el sistema sustituye el accionamiento previo por motor DC por un **motor paso a paso NEMA 23 (23HS5628)** acoplado mediante un par de engranajes rectos de reducción diseñados en **SolidWorks**. Esta solución garantiza un avance controlado, repetible y con paradas programadas de **1,2 s** frente a la estación de inspección óptica (cámara OV5640 y sensor infrarrojo E18-D80NK), suprimiendo cualquier desenfoque por movimiento (*motion blur*) durante la captura de imágenes.

```
+-----------------------------------------------------------------------------------------+
|                                ESTRUCTURA MECÁNICA GENERAL                              |
|                                                                                         |
|  [ Motor NEMA 23 ]                                                                      |
|        |                                                                                |
|  [ Engranaje Conductor (z=10, m=4) ]                                                    |
|        | (Reducción 3.8:1)                                                              |
|  [ Engranaje Conducido (z=38, m=4) ]                                                    |
|        |                                                                                |
|  [ Eje y Rodillo Motriz ] ======> [ Banda de Caucho (200 mm) ] ======> [ Rodillo Tensor ]
|                                    (Chasis: Madera Contrachapada + 7 Varillas DIN 975)  |
+-----------------------------------------------------------------------------------------+
```

---

## 2. ⚙️ Criterios de Diseño y Requerimientos Funcionales

1. **Ancho Útil de Transporte:** Banda de $200\text{ mm}$ de ancho para alojar con holgura paquetes y cajas de hasta $150\text{ mm}$.
2. **Control de Avance y Posicionamiento:** Capacidad de detener la cinta de manera instantánea y repetible al activarse el sensor IR E18-D80NK (latencia $< 50\text{ ms}$).
3. **Rigidez Torsional y Alineación:** Chasis autoportante de madera contrachapada fijado mediante varillas roscadas pasantes de acero métrico, garantizando paralelismo estricto entre ejes.
4. **Mantenimiento y Desmontaje:** Facilidad para cambiar o tensar la banda sin requerir el desarmado total del bastidor.
5. **Manufactura Híbrida y Accesible:**
   - Estructura portante en madera contrachapada mecanizada.
   - Transmisión cinemática fabricada mediante **impresión 3D (PLA)**.
   - Elementos de unión normalizados (acero DIN 975 grado comercial).

---

## 3. 🧩 Descripción de Componentes Mecánicos

### 3.1 Chasis y Laterales del Bastidor (`Tabla.SLDPRT` / `Soporte Lateral.pdf`)
- **Material:** Madera contrachapada de primera calidad de $12.00\text{ mm}$ de espesor.
- **Dimensiones Principales:** Longitud total $700.00\text{ mm}$, altura $150.00\text{ mm}$.
- **Puntos de Sujeción y Fijación:**
  - 6 perforaciones pasantes de $\varnothing 6.50\text{ mm}$ distribuidas a lo largo del soporte para las varillas roscadas estructurales (con cotas de espaciado $175.00\text{ mm}$, $350.00\text{ mm}$, etc.).
  - Alojamiento de ejes principales con orificios de $\varnothing 8.00\text{ mm}$ situados a $25.00\text{ mm}$ y $58.04\text{ mm}$ de referencias clave.
  - Guías y ranuras de ajuste para el tensado de la banda en el rodillo conducido.

### 3.2 Tirantes Estructurales Transversales (`Varilla.SLDPRT`)
- **Norma y Material:** Acero al carbono galvanizado según norma **DIN 975**.
- **Cantidad:** 7 varillas roscadas M6.
- **Función:** Unen rígidamente ambos laterales de madera contrachapada mediante tuercas y arandelas exteriores e interiores, proporcionando una estructura tipo celosía de alta resistencia a la flexión y torsión.

### 3.3 Banda Transportadora y Rodillos (`Cinta.SLDPRT`)
- **Banda:** Banda plana vulcanizada de caucho continuo con cara superior antideslizante de $200\text{ mm}$ de ancho útil.
- **Rodillos:** Cilindros concéntricos montados sobre rodamientos de bolas de bajo rozamiento para asegurar rotación suave bajo carga.

---

## 4. ⚙️ Sistema de Transmisión por Engranajes Cilíndricos Rectos

Para vincular el motor NEMA 23 al eje del rodillo motriz se diseñó una etapa reductora de engranajes de dientes rectos, modelados en SolidWorks e impresos en PLA de alta densidad de relleno.

### 4.1 Parámetros Geométricos del Engranaje Conductor (`Engranaje_C.SLDPRT` / `Engranaje_Conductor.pdf`)

| Parámetro Geométrico | Símbolo | Valor de Diseño |
| :--- | :---: | :---: |
| **Módulo** | $m$ | $4\text{ mm}$ |
| **Número de Dientes** | $z_1$ | $10$ |
| **Ángulo de Presión** | $\alpha$ | $20^\circ$ |
| **Diámetro Primitivo** | $d_{p1} = m \cdot z_1$ | $40.00\text{ mm}$ |
| **Diámetro Exterior** | $d_{e1} = d_{p1} + 2m$ | $48.00\text{ mm}$ |
| **Espesor del Diente (Ancho de Cara)** | $b_1$ | $12.00\text{ mm}$ |
| **Altura Total con Cubo** | $h_{\text{cubo}}$ | $22.00\text{ mm}$ |
| **Diámetro del Cubo** | $d_{\text{cubo}}$ | $16.00\text{ mm}$ |
| **Diámetro de Eje (Eje Motor NEMA 23)** | $d_{\text{eje1}}$ | $\varnothing 8.00\text{ mm}$ |
| **Tipo de Perfil** | — | Evolvente / Involuta estándar |
| **Material** | — | Polímero termoplástico (PLA) |

### 4.2 Parámetros Geométricos del Engranaje Conducido (`engranaje_G.SLDPRT` / `engranaje_conducido.PDF`)

| Parámetro Geométrico | Símbolo | Valor de Diseño |
| :--- | :---: | :---: |
| **Módulo** | $m$ | $4\text{ mm}$ |
| **Número de Dientes** | $z_2$ | $38$ |
| **Ángulo de Presión** | $\alpha$ | $20^\circ$ |
| **Diámetro Primitivo** | $d_{p2} = m \cdot z_2$ | $152.00\text{ mm}$ |
| **Diámetro Exterior** | $d_{e2} = d_{p2} + 2m$ | $160.00\text{ mm}$ |
| **Espesor del Diente** | $b_2$ | $12.00\text{ mm}$ |
| **Diámetro de Eje** | $d_{\text{eje2}}$ | $\varnothing 6.00\text{ mm}$ |
| **Tipo de Perfil** | — | Evolvente / Involuta estándar |
| **Material** | — | Polímero termoplástico (PLA) |

### 4.3 Relación de Reducción y Distancia Entre Centros

* **Relación de transmisión ($i$):**
  $$i = \frac{z_2}{z_1} = \frac{38}{10} = 3.8:1$$

* **Distancia teórica entre centros ($a$):**
  $$a = \frac{d_{p1} + d_{p2}}{2} = \frac{40\text{ mm} + 152\text{ mm}}{2} = \frac{192\text{ mm}}{2} = 96.00\text{ mm}$$

> **Efecto cinemático:** La relación de reducción $3.8:1$ multiplica el torque entregado al rodillo motriz por un factor de casi 4, permitiendo mover la cinta con suavidad y posicionar la caja de manera controlada frente a la cámara.

---

## 5. 🧮 Cinemática de Tracción y Parada

### 5.1 Motor Paso a Paso NEMA 23 (Modelo 23HS5628)
- **Ángulo de paso nativo:** $1.8^\circ \pm 5\%$ ($200\text{ pasos/revolución}$).
- **Torque de retención nominal:** $12.6\text{ kgf}\cdot\text{cm}$ ($1.23\text{ N}\cdot\text{m}$).
- **Controlador:** L298N (puente H bipolar).

### 5.2 Desplazamiento Lineal por Paso
Para un diámetro del rodillo motriz $D_{\text{rodillo}} \approx 40\text{ mm}$:
- Circunferencia del rodillo motriz:
  $$C_{\text{rodillo}} = \pi \cdot D_{\text{rodillo}} \approx 3.1416 \times 40\text{ mm} \approx 125.66\text{ mm}$$
- Desplazamiento lineal del rodillo motriz por revolución del motor:
  $$\Delta x_{\text{rev\_motor}} = \frac{C_{\text{rodillo}}}{i} = \frac{125.66\text{ mm}}{3.8} \approx 33.07\text{ mm/rev}$$
- Resolución de avance por paso del motor ($1.8^\circ$):
  $$\Delta x_{\text{paso}} = \frac{33.07\text{ mm}}{200\text{ pasos}} \approx 0.165\text{ mm/paso}$$

Esta precisión milimétrica ($0.165\text{ mm}$ por paso completo) garantiza que cuando el sensor fotoeléctrico infrarrojo **E18-D80NK** detecta el borde de ataque de la caja, la cinta se detiene exactamente en el centro del encuadre óptico de la cámara **OV5640**.

---

## 6. 📋 Lista de Partes y Ensamble General (`Ensamble.SLDASM` / `Cinta transportadora.pdf`)

| Ítem | N° de Parte / Nombre | Descripción y Material | Cantidad | Plano Técnico / Modelo |
| :---: | :--- | :--- | :---: | :--- |
| **1** | `Soporte Lateral` | Madera Contrachapada e = 12 mm | 2 | `Soporte Lateral.pdf` / `Tabla.SLDPRT` |
| **2** | `Varilla Roscada` | Acero al carbono DIN 975 M6 | 7 | `Varilla.SLDPRT` |
| **3** | `Motor Paso a Paso` | NEMA 23 (23HS5628) | 1 | `nema23.SLDPRT` |
| **4** | `Engranaje Conducido` | Polímero (PLA) z=38, m=4 | 1 | `engranaje_conducido.PDF` / `engranaje_G.SLDPRT` |
| **5** | `Engranaje Conductor` | Polímero (PLA) z=10, m=4 | 1 | `Engranaje_Conductor.pdf` / `Engranaje_C.SLDPRT` |
| **6** | `Cinta` | Banda cerrada de Caucho (200 mm útil) | 1 | `Cinta.SLDPRT` |
| **7** | `Ensamble General` | Conjunto montado de cinta transportadora | 1 | `Cinta transportadora.pdf` / `Ensamble.SLDASM` |

---

## 7. 📸 Registro Fotográfico del Prototipo Construido

Durante la fase de validación física del prototipo se registraron las siguientes observaciones de ensamble:
- **Estructura Portante:** Soportes de madera contrachapada alineados rígidamente por las 7 varillas roscadas de acero M6 con doble tuerca de apriete.
- **Montaje del Motor:** El NEMA 23 se encuentra asegurado firmemente al lateral de la cinta, con su eje de $\varnothing 8\text{ mm}$ acoplado al engranaje conductor de 10 dientes en PLA.
- **Engrane:** El engranaje conducido de 38 dientes engrana directamente con el conductor, verificándose un juego entre dientes (*backlash*) adecuado para evitar atoramientos y desgaste prematuro.
- **Respuesta Operativa:** Paradas programadas de **1,2 s** perfectamente repetibles y estables ante la detección por el sensor IR, sin deslizamiento apreciable de la banda de caucho.

---

## 8. 🎯 Conclusiones del Diseño Mecánico (1er Parcial)

1. La sustitución del motor DC por el motor paso a paso **NEMA 23** con transmisión de engranajes $3.8:1$ solucionó de raíz el problema de la inercia y la repetibilidad del frenado.
2. El uso de engranajes de módulo $m = 4\text{ mm}$ proporciona robustez suficiente ante los esfuerzos dinámicos de arranque y frenado brusco.
3. Como mejora para las siguientes fases se contempla incorporar un tensor dinámico con resorte para la banda de caucho y una cubierta protectora para los engranajes de transmisión.
