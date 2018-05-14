import domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by javierzolotarchuk on 14/05/18.
 */
public class AppTest {

    private static Dispatcher dispatcher = Dispatcher.getInstance();

    @Test
    public void countCallsEqualsCountEmployees() throws InterruptedException {
        int countCalls = 10;
        int countCallsXGroup = 1;
        int countOperators = 6;
        int countSupervisors = 3;
        int countDirectors = 1;

        dispacthCalls(countCalls,countCallsXGroup,countOperators,countSupervisors,countDirectors);
    }

    @Test
    public void moreCallsThanEmployees() throws InterruptedException {
        int countCalls = 100;
        int countCallsXGroup = 20;
        int countOperators = 6;
        int countSupervisors = 3;
        int countDirectors = 1;

        dispacthCalls(countCalls,countCallsXGroup,countOperators,countSupervisors,countDirectors);
    }

    private void dispacthCalls(int countCalls,int countCallsXGroup,int countOperators,int countSupervisors,int countDirectors) throws InterruptedException {

        //creamos las llamadas
        List<Call> callsToSend = createCalls(countCalls);

        //las dividimos en grupos
        Map<Integer, List<Call>> groupsCalls =
                callsToSend.stream().collect(Collectors.groupingBy(s -> s.getId() / countCallsXGroup));
        List<List<Call>> subCalls = new ArrayList<List<Call>>(groupsCalls.values());

        //mandamos cada grupo al dispatcher en paralelo
        subCalls.forEach(calls -> sendCalls(calls)); // se mandan en paralelo las 4 listas de llamadas

        //creamos los empleados
        List<Employee> operators = createEmployees(EmployeeTypes.OPERATOR,countOperators);
        List<Employee> supervisors = createEmployees(EmployeeTypes.SUPERVISOR,countSupervisors);
        List<Employee> directors = createEmployees(EmployeeTypes.DIRECTOR,countDirectors);

        //mandamos cada tipo de empleado en paralelo
        sendEmployees(operators);
        sendEmployees(supervisors);
        sendEmployees(directors);

        //procesamos las llamdas
        dipatchCalls();

        List<Call> callsFinished = callsFinished(callsToSend);

        //espera activa para finalizar el test cuando todas las llamadas fueron contestadas
        while (callsFinished.size() != callsToSend.size()) {
            callsFinished = callsFinished(callsToSend);
        }

        //checkeamos que todas las llamdas hayan sido contestadas
        Assert.assertEquals(callsFinished, callsToSend);
    }

    //-----------Aux Methods-----------------------
    private List<Call> createCalls(int count) {
        List<Call> calls = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            calls.add(new Call(i));
        }

        return calls;
    }

    private void dipatchCalls() {
        new Thread(() -> {
            try {
                dispatcher.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<Call> callsFinished(List<Call> calls) {

        List<Call> callsFinished = calls.stream()
                .filter(call -> call.getStatus().equals(CallStatus.FINISHED))
                .collect(Collectors.toList());
        return callsFinished;
    }

    private List<Employee> createEmployees(EmployeeTypes type, int count) {
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            employees.add(new Employee(type));
        }

        return employees;
    }

    private void sendCalls(List<Call> calls) {
        new Thread(() ->{
            try {
                calls.forEach(call -> dispatcher.addCall(call));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendEmployees(List<Employee> employees) {
        new Thread(() ->{
            try {
                employees.forEach(employee -> dispatcher.addEmployee(employee));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}