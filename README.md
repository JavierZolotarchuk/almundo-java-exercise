**Almundo.com -Java Exercise-**

## Modelo del sistema gral

![alt text](https://raw.githubusercontent.com/JavierZolotarchuk/almundo-java-exercise/master/images/modelado.jpg)

Es un flujo bastante simple, en donde la clase **Dispatcher** recibe todas las llamadas y las va derivando a los distintos empleados, antes de derivar una llamada chequea los empleados que tiene disponibles y elige uno teniendo en cuenta la regla de negocio que dice que las llamadas las deben tomar los operadores, solo pueden tomarlas los supervisores cuando todos los operadores esten ocupados y los directores cuando todos los operadores y supervisores esten ocupados.

La clase **Dispatcher** recibe las llamadas a travez del metodo **addCall** lo que provoca que la misma se encole para luego derivarla.

Con lo empleados ocurre algo similar, la clase **Dispatcher** cuenta con el metodo **addEmployee** el cual mete al empleado en una cola, la cual es utilizada para buscar un empleado disponible al momento de atender la llamada.

**Y todo esto como funciona?**

Vamos por partes, si bien el ejemplo lo desarrollamos de manera secuencial, esto es solo con fin didactico ya que en la realidad es un sistema concurrente (no necesariamente se va a ejecutar todo en el orden que lo vamos a explicar)

La clase **Dispatcher** maneja 2 colas que soportan concurrencia, las mismas son usadas para almacenar a las llamadas y a los empleados.

**Ingreso de llamadas:**

![alt text](https://raw.githubusercontent.com/JavierZolotarchuk/almundo-java-exercise/master/images/ingresoLlamadas.png)

las llamadas las recibe el **dispatcher** a travez del metodo **addCall** y las enconla en una cola concurrente. A medida que las llamadas se van atendiendo se van eliminando de la cola. Es decir en la cola solo estan las llamadas pendientes por atender.

**Ingreso de empleados:**

![alt text](https://raw.githubusercontent.com/JavierZolotarchuk/almundo-java-exercise/master/images/ingresoEmpleados.png)

los empleados los recibe el **dispatcher** a travez del metodo **addEmployee** y los encola en una cola concurrente. El **dispatcher** los va a ir sacando de la lista en medida que tenga llamadas para responder, cuando vaya a buscar un empleado. Va a mirar todos los que tiene en la cola (que son lo que estan disponibles para atender) y de esos va a elegir el que tenga menor grado de jerarquia. Entendiendo por jerarquia los puestos **(Operador,Supervisor y Director)**.
Al elegir un empleado para atender una llamada, el mismo es borrado de la cola ya que solo estan los que estan disponibles para atender llamadas. Cuando el empleado finaliza la llamada, el mismo le notifica al **dispatcher** que esta listo para atender nuevas llamadas y este ultimo lo vuelve a agregar en la cola.


**Uso de las colas:**

![alt text](https://raw.githubusercontent.com/JavierZolotarchuk/almundo-java-exercise/master/images/manejoDeLasColas.png)

Tanto la cola de llamadas como la cola de empleados tienen un semaforo contador el cual hace que si la cola esta vacia, el **dispatcher** quede bloqueado hasta que el evento de insercion lo despierte.

Por ej: si no hay llamadas para atender, el **dispatcher** no se queda procesando. Cuando llega una nueva llamada esta lo despierta (desbloquea el semaforo) y permite que siga procesando (la atienda).

Lo mismo pasa con los empleados, si tenemos una llamada para atender y no hay ningun empleado libre, el **dispatcher** queda bloqueado hasta que se desocupe un empleado y lo despierte.

Otra solucion podria haber sido hacer una espera activa preguntando si por ej ya habia algun empleado libre pero consume procesamiento ya que hay que andar preguntando muchas veces. Por dicho motivo se eligio el semaforo contador.

Los bloqueos se pueden ver claramente en la implementacion de los metodos **getCall** y **getEmployeeAvailable**, si estos 2 pasan sin bloquearse, significa que tenemos llamadas por atender y empleados libres es entonces que se llama al metodo **delegateCall** el cual abre un Thread y le dice al empleado que responda la llamada **( employee.answer(call) )**. Gracias a esto podemos responder paralelamente tantas llamadas como empleados tengamos.

Debido al diseño del sistema, cuando haya mas llamadas que empleados estas simplemente se van a encolar y van a ser atendidas a medida que se vayan desocupando los empleados. Esto se puede ver claramente en [AppTest](https://github.com/JavierZolotarchuk/almundo-java-exercise/blob/master/src/test/java/AppTest.java) donde hay 2 tests, uno que recibe tantas llamadas como empleados y otro que recibe una cantidad bastante mayor de llamadas que de empleados.
