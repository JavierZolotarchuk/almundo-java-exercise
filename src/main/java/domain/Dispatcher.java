package domain;

import util.QueueBlock;

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

    public void addEmployee(Employee employee) {
        employeesQueque.add(employee);
    }

    public void addCall(Call call) {
        System.out.println("ID: " + call.getId()  + " Espere por favor, ya sera atendido");
        callsQueue.add(call);
    }


    public void run() {
        while (true) {
            try {
                dispatchCall();
            } catch (Exception e) {
                System.out.println("Ocurrio un error: " + e.getStackTrace());
                e.printStackTrace();
            }
        }
    }

    public void dispatchCall() {
        Call call = getCall(); // si no hay llamadas se bloquea (hasta que haya una nueva llamada)
        Employee employee = getEmployeeAvilable(); //si no hay empleados se bloquea (hasta que haya un empleado libre)
        delegateCall(employee,call); //abre un hilo donde el empleado atiende la llamada
    }

    public Employee getEmployeeAvilable() {
        employeesQueque.blockAndDownCount();
        final Employee employee = getEmployeeWithLowerHierarchy();
        removeEmployee(employee);
        employeesQueque.unBlock();
        return employee;
    }

    private Call getCall() {
        return callsQueue.poll();
    }

    private void delegateCall(Employee employee,Call call) {
        new Thread(() ->{
            try {
                employee.answer(call);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Employee getEmployeeWithLowerHierarchy() {
        return employeesQueque.getOriginalQueue().stream()
                .min(Comparator.comparing(Employee::getPriority)).get();
    }

    private void removeEmployee(Employee employee) {
        employeesQueque.removeElement(employee);
    }

}
