# 🛵 Domi · Sistema de domicilios por consola

🎯 Aplicación de consola en Java para el operador de Domi. Gestiona clientes, repartidores y pedidos, y decide sola qué repartidor lleva cada pedido: elige el de **menor costo** 💰 y, si hay empate, el **más rápido** ⚡. Los datos viven en memoria mientras el programa está abierto.

Incluye todos los niveles: 🟢 Base, 🟡 Corazón, 🔴 Operación real (calificaciones, modo lluvia y cierre del día) y 🐉 Jefe final (dron). Como extra ⭐, la opción 14 muestra el historial de pedidos de un cliente.

## 🚀 Cómo ejecutar el proyecto en IntelliJ

1. 📥 Clonar el repositorio: `git clone <URL-del-repo>`
2. 📂 Abrir la carpeta en IntelliJ IDEA (File → Open).
3. ☕ Verificar el SDK de Java (File → Project Structure → Project SDK).
4. ▶️ Ejecutar la clase `Main` del paquete `app`.

💡 Si las tildes o los emojis se ven como `?`, agregar `-Dfile.encoding=UTF-8` en las VM options de la configuración de ejecución.

## 🗂️ Estructura del proyecto

| Paquete | Contenido | Responsabilidad |
|---|---|---|
| 📦 `modelo` | `Cliente`, `Repartidor` (abstracta), `Bici`, `Moto`, `Carro`, `Dron`, `FabricaRepartidores`, `Pedido`, interfaz `Calificable` | Representar el negocio. Cada clase se valida a sí misma |
| 📦 `servicio` | `DomiService`, `Cotizacion`, `ReporteCierre` | Guardar las colecciones y aplicar las reglas. No lee del teclado ni imprime menús |
| 📦 `app` | `Main` | Mostrar el menú, leer lo que escribe el operador y mostrar resultados |

💡 Principio que seguí: **Main pregunta y muestra, el servicio decide, el modelo se valida a sí mismo.**

## 🧠 Conceptos de POO aplicados

- 🔒 **Encapsulamiento:** atributos privados; los setters validan y lanzan `IllegalArgumentException` con mensajes claros.
- 🧬 **Herencia y clase abstracta:** `Repartidor` es abstracta y `Bici`, `Moto`, `Carro` y `Dron` heredan de ella.
- 🎭 **Polimorfismo:** el servicio le hace la misma pregunta a cada repartidor (`puedeLlevar`, `calcularCosto`, `calcularTiempo`) y cada uno responde a su manera. El servicio nunca pregunta de qué tipo es.
- 🔌 **Interfaz:** `Calificable` (recibir calificación de 1 a 5 y calcular el promedio), implementada por `Repartidor`, por lo que `Bici`, `Moto`, `Carro` y `Dron` la heredan.
- 🚨 **Excepciones:** el modelo y el servicio lanzan `IllegalArgumentException`; `Main` las atrapa con `try/catch` en cada opción, muestra el mensaje y vuelve al menú.

## ❓ Respuestas a las preguntas de la sección 6.3

- 🗝️ **¿Por qué un mapa y no una lista para los clientes?** Los clientes se buscan constantemente por cédula. En un `HashMap` (cédula → cliente) la búsqueda es directa, sin recorrer todos, y además la llave no se puede repetir, lo que ayuda a rechazar cédulas duplicadas.
- 🏘️ **¿Qué ventaja tiene que los barrios (`HashSet`) no se repitan?** Un barrio con cobertura es "está o no está". El `HashSet` garantiza que no haya duplicados y responde rápido si un barrio tiene cobertura.
- 📏 **¿Por qué el operador nunca escribe los km?** Porque los km dependen del barrio del cliente. Si el operador los escribiera podría equivocarse o alterar el precio. El servicio los toma del `HashMap` barrio → km.
- 📋 **¿Por qué aquí sí sirve una lista (repartidores)?** Son pocos, el orden de registro importa para mostrarlos y al cotizar se recorren todos. No hay una llave natural para buscarlos constantemente.
- 🔎 **¿Cómo encuentro un pedido por su número?** Recorro la lista de pedidos y comparo `getNumero()` con el número buscado (`buscarPedido` en `DomiService`).

## 🧩 Decisiones de diseño

- ⭐ **Calificaciones:** cada repartidor guarda solo la **suma** y la **cantidad** de calificaciones. Es suficiente para calcular el promedio, ocupa menos memoria que una lista y evita recorrerla cada vez. El promedio es 0 mientras no haya calificaciones. La calificación se valida antes de cambiar el estado del pedido, así que una calificación inválida no deja el pedido a medias.
- 🌧️ **Modo lluvia:** el **servicio** sabe si está lloviendo (es un estado de la operación del día) y cada **repartidor** responde `afectadoPorLluvia()`: `Bici` y `Dron` dicen que sí, el resto que no. Con eso el servicio excluye a los afectados y aplica el recargo del 20 % en un solo lugar. Así, un tipo nuevo se adapta sin tocar la lógica de asignación.
- 🏭 **`FabricaRepartidores`:** es el único lugar que conoce los tipos disponibles al registrar. El menú muestra la lista que le entrega, así que `Main` no cambia cuando aparece un tipo nuevo.
- 📌 **Estado del pedido:** un `Set` con `EN_CAMINO`, `ENTREGADO` y `CANCELADO` permite validar rápido cualquier valor que llegue.

## 🐉 Jefe final: el dron

**¿Cuántos archivos tuve que tocar para agregar el dron y por qué?**

**Dos** ✌️: creé `Dron.java` (nueva clase que hereda de `Repartidor`) y agregué "Dron" en `FabricaRepartidores.java`. No toqué `DomiService` ni `Main`, porque cotizar y asignar es polimórfico: el servicio solo le pregunta a cada repartidor. La restricción de lluvia del dron se resuelve con `afectadoPorLluvia()`, sin ningún `if` por tipo.

## 🎬 Video demo

🔗 Enlace: 

## 🤔 Reflexión de cierre

- 💾 **Si cierro el programa y lo abro de nuevo, ¿qué pasó con los pedidos del día?** Se perdieron. Todo vive en memoria (`ArrayList` y `HashMap`), así que al cerrar el programa los datos desaparecen y solo quedan los datos de arranque.
- 🏢 **Si Domi abre una segunda sede con otro operador, ¿cómo compartiría los clientes y los pedidos?** Cada operador tendría su propia copia en memoria, así que no se verían entre sí. Haría falta una **base de datos central** 🗄️ a la que ambos se conecten (o un servicio compartido que la use), con los clientes, repartidores y pedidos guardados de forma persistente. Así los dos verían la disponibilidad real de los repartidores y no se asignaría el mismo repartidor dos veces.

**👩‍💻 Autora:** _(Ana C. Lizarazo Páez)_ 
