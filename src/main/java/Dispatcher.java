import java.util.Comparator;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class Dispatcher {

    private static final Dispatcher dispatcher = getInstance(); //Singleton

    private QueueBlock<Call> callsQueue = new QueueBlock<>();
    private QueueBlock<Employee> employeesQueque = new QueueBlock<>();

    public static Dispatcher getInstance() {

        if (dispatcher == null) {
            return new Dispatcher();
        } else {
            return dispatcher;
        }
    }

    public void addEmployee(Employee employee) throws InterruptedException {
        employeesQueque.add(employee);
    }

    private Call getCall() throws InterruptedException {
       return callsQueue.poll();
    }

    public void addCall(Call call) throws InterruptedException {
        callsQueue.add(call);
    }


    public void run() {

        while (true) {
            try {
                Call call = getCall(); // si no hay llamadas se bloquea
                Employee employee = getEmployeeAvilable(); //si no hay empleados se bloquea
                delegateCall(employee,call); //abre un hilo donde el empleado atiende la llamada
            } catch (Exception e) {
                System.out.println("Ocurrio un error: " + e.getStackTrace().toString());
                e.printStackTrace();
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

    public Employee getEmployeeAvilable() throws InterruptedException {
        employeesQueque.blockingBeforeGet();
        final Employee employee = getEmployeeWithLowerHierarchy();
        removeEmployee(employee);
        employeesQueque.unLocking();
        return employee;
    }

    private Employee getEmployeeWithLowerHierarchy() {
        return employeesQueque.getOriginalQueue().stream()
                .min(Comparator.comparing(Employee::getPriority)).get();
    }

    private void removeEmployee(Employee employee) {
        employeesQueque.removeElement(employee);
    }

}
