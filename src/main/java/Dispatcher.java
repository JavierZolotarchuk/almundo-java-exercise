import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class Dispatcher {
    private static final Dispatcher dispatcher = getInstance(); //Singleton

    private static Semaphore countCalls = new Semaphore(0);
    private static Semaphore mutexCall = new Semaphore(1);
    private static Semaphore countEmployees = new Semaphore(0);
    private static Semaphore mutexEmployee = new Semaphore(1);
    private Queue<Call> callsQueue = new ConcurrentLinkedQueue<>(); //ver que significa que sea concurrente
    private Queue<Employee> employeesAvailableQueque = new ConcurrentLinkedQueue<>();

    // despues hacer bien con esto, ya que supuestamente maneja sola los locks
    //BlockingQueue<Call> callsQueue = new LinkedBlockingQueue<Call>();

    public static Dispatcher getInstance() {

        if (dispatcher == null) {
            return new Dispatcher();
        } else {
            return dispatcher;
        }
    }

    public void addEmployeeAvailable(Employee employee) throws InterruptedException {
        mutexEmployee.acquire();
        employeesAvailableQueque.add(employee);
        mutexEmployee.release();
        countEmployees.release();
    }

    public Call getCall() throws InterruptedException {
        Call call = null;
        countCalls.acquire();
        mutexCall.acquire();
        call = callsQueue.poll();
        mutexCall.release();
        return call;
    }

    public void addCall(Call call) throws InterruptedException {
        mutexCall.acquire();
        callsQueue.add(call);
        mutexCall.release();
        countCalls.release();
    }


    public void run() {

        while (true) {
            try {
                Call call = getCall(); // si no hay llamadas se bloquea
                Employee employee = getOneEmployeeAvilable(); //si no hay empleados se bloquea
                delegateCall(employee,call); //abre un hilo donde el empleado atiende la llamada
            } catch (Exception e) {
                System.out.println("Ocurrio un error: " + e.getStackTrace().toString());
            }
        }
    }

    private void delegateCall(Employee employee,Call call) {
        new Thread(() ->{
            try {
                employee.answer(call);
            } catch(InterruptedException v) {
                System.out.println(v);
            }
        }).start();
    }

    public Employee getOneEmployeeAvilable() throws InterruptedException {
        //final Employee employee = null;

        countEmployees.acquire();
        mutexEmployee.acquire();
        final Employee employee = employeesAvailableQueque.stream()
                .min(Comparator.comparing(Employee::getPriority)).get(); //ojo que esto en realidad devuelve un optional
        //TODO aca elimino el elemento de la cola

        employeesAvailableQueque = employeesAvailableQueque.stream()
                .filter(emp -> emp != employee) //al no comparar por equals, compara por la identidad que es justo lo que queremos ;)
                .collect(toCollection(ConcurrentLinkedQueue::new));

        mutexEmployee.release();

        return employee; //lo de arriba solo nos da el empleado de menor prioridad, pero tenemos que sacarlo de nuestra lista
    }

}
