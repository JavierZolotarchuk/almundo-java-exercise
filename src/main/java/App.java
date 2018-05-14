import domain.Call;
import domain.Dispatcher;
import domain.Employee;
import domain.EmployeeTypes;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class App {

    private static final int MAX_OPERATOR = 30;
    private static final int MAX_SUPERVISOR = 15;
    private static final int MAX_DIRECTOR = 3;

    private static final int MAX_CALLS = 1000;

    private static Dispatcher dispatcher = Dispatcher.getInstance();

    public static void main(String[] args) {

        for (int i = 0; i < MAX_OPERATOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.OPERATOR));
        }

        for (int i = 0; i < MAX_SUPERVISOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.SUPERVISOR));
        }

        for (int i = 0; i < MAX_DIRECTOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.DIRECTOR));
        }

        for (int i = 0; i < MAX_CALLS; i++) {
            Call call = new Call(i);
            dispatcher.addCall(call);
        }

        dispatcher.run();
    }
}
