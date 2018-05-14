**Almundo.com -Java Exercise-**

![Alt text](relative/path/to/modelado.jpg?raw=true "Modelado")

Es un flujo bastante simple, en donde la clase **Dispatcher** recibe todas las llamadas y las va derivando a los distintos empleados, antes de derivar una llamada chequea los empleados que tiene disponibles y elige uno teniendo en cuenta la condicion de negocio que dice que las llamadas las deben tomar los operadores, solo pueden tomarlas los supervisores cuando todos los operadores esten ocupados y los directores cuando todos los operadores y supervisores esten ocupados.

La clase **Dispatcher** recibe las llamadas a travez del metodo **addCall** lo que provoca que la misma se encole para luego derivarla.

Con lo empleados ocurre algo similar, la clase **Dispatcher** cuenta con el metodo **addEmployee** el cual mete al empleado en una cola, la cual es utilizada para buscar un empleado disponible al momento de atender la llamada.

**Y todo esto como funciona?**

La clase **Dispatcher** maneja 2 colas que soportan concurrencia, las mismas son usadas para almacenar a las llamadas y a los empleados.

La sincronizacion de las colas se maneja de tal forma que si no hay llamadas, el **Dispatcher** se blockea esperando a que aparezca una nueva llamada y en ese entonces busca un empleado. Es decir si no hay llamadas no se queda loopeando hasta que aparezca alguna (no usamos espera activa). Sino que cuando aparece una nueva llamada, destraba la ejecucion y se delega la respuesta de la misma en un empleado.

Con la cola de empleados ocurre lo mismo, si al momento de querer contestar una llamada no hay ninguno libre, se queda blockeado hasta que venga uno y pueda responderla.

Al momento de elegir al empleado, se llama al metodo **getEmployeeWithLowerHierarchy** el cual nos da al empleado de menor jerarquia, lo cual cumple con el requerimiento pedido. Una vez encontrado el empleado de menor jerarquia, el mismo es eliminado de la cola ya que no va a estar disponible hasta terminar de contestar la llamada.

Lo mismo ocurre con las llamadas, a medida que se van costentando se van eliminando de la cola.

Cuando se obtuvo una llamada y un empleado, se procede a contestarla para hacer esto se abre un Thread y se le dice al empleado que responda la llamada **( employee.answer(call) )**, debido a que abrimos un hilo para esto podemos responder tantas llamadas en paralelo como empleados libres tengamos.

Cuando el empleado termina de contestar la llamada, el mismo conoce al **Dispatcher** (que es un Singleton) y se notifica como disponible lo cual le permite poder responder nuevas llamadas (es nuevamente agregado en la cola de empleados).

Como ya dijimos antes vamos a poder responder tantas llamadas en paralelo como cantidad de empleados tengamos disponibles. Si hay mas llamadas que empleados, estas simplemente van a permanecer mas tiempo en la cola y a medida que los empleados se vayan desocupando las van a ir tomando.

Si bien no hay un limite fijo de cuantas llamadas se pueden atender con x cantidad de empleados, se debe hacer a conciencia ya que no queremos quedar mal con los clientes ;).