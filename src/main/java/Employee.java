import java.util.Date;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class Employee {

    private Dispatcher dispatcher = Dispatcher.getInstance();

    public EmployeeTypes getType() {
        return type;
    }

    public int getPriority() { return type.getOrderNum(); }

    private EmployeeTypes type;

    public Employee(EmployeeTypes type) {
        this.type = type;
    }

    public void answer(Call call) throws InterruptedException {
        System.out.println(type + " Respondiendo llamada: " + call.getId() + " son las: " + new Date().getTime());
        call.answer();
        dispatcher.addEmployeeAvailable(this);
    }
}
