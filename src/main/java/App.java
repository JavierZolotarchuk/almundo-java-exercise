/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class App {

    private static final int MAX_OPERATOR = 3;
    private static final int MAX_SUPERVISOR = 1;
    private static final int MAX_DIRECTOR = 1;

    private static final int MAX_CALLS = 10;

    private static Dispatcher dispatcher = Dispatcher.getInstance();

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < MAX_OPERATOR; i++) {
            dispatcher.addEmployeeAvailable(new Employee(EmployeeTypes.OPERATOR));
        }

        for (int i = 0; i < MAX_SUPERVISOR; i++) {
            dispatcher.addEmployeeAvailable(new Employee(EmployeeTypes.SUPERVISOR));
        }

        for (int i = 0; i < MAX_CALLS; i++) {
            Call call = new Call(i);
            dispatcher.addCall(call);
        }

        dispatcher.run();

    }

}

/*
public class App {

    private static final int MAX_OPERATOR = 3;
    private static final int MAX_SUPERVISOR = 1;
    private static final int MAX_DIRECTOR = 1;

    private static final int MAX_CALLS = 10;

    private static Dispatcher dispatcher = Dispatcher.getInstance();
    private static Semaphore semaphoreOperator = new Semaphore(MAX_OPERATOR);
    private static Semaphore semaphoreSupervisor = new Semaphore(MAX_SUPERVISOR);
    private static Semaphore semaphoreDirector = new Semaphore(MAX_DIRECTOR);

    public static void main(String[] args) throws InterruptedException {

        //Inicializando llamadas
        for (int i = 0; i < MAX_CALLS; i++) {
            Call call = new Call(i);
            dispatcher.dispatchCall(call);
        }

        int countOperators = semaphoreOperator.availablePermits();
        int countSupervisors = semaphoreSupervisor.availablePermits();
        int countDirectors = semaphoreDirector.availablePermits();

        int totalEmployees = countOperators + countSupervisors + countDirectors;

        ExecutorService executor = Executors.newFixedThreadPool(totalEmployees);

        //Inicializando Operadores
        for (int i = 0; i < countOperators; i++) {
            executor.execute(new Operator(semaphoreOperator));
        }

        //Inicializando Supervisores
        for (int i = 0; i < countSupervisors; i++) {
            executor.execute(new Supervisor(semaphoreSupervisor,semaphoreOperator));
        }

        //Inicializando Directores
        for (int i = 0; i < countDirectors; i++) {
            executor.execute(new Director(semaphoreDirector,semaphoreSupervisor));
        }
    }
}




 */
