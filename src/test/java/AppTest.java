import domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by javierzolotarchuk on 14/05/18.
 */
public class AppTest {

    private static final int MAX_OPERATOR = 6;
    private static final int MAX_SUPERVISOR = 3;
    private static final int MAX_DIRECTOR = 1;

    private static final int MAX_CALLS = 30;

    private static Dispatcher dispatcher = Dispatcher.getInstance();

    @Test
    public void consecutiveCallsAndConsecutiveEmployees() throws InterruptedException {

        List<Call> calls = new ArrayList<>();

        for (int i = 0; i < MAX_CALLS; i++) {
            Call call = new Call(i);
            calls.add(call);
            dispatcher.addCall(call);
        }

        for (int i = 0; i < MAX_OPERATOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.OPERATOR));
        }

        for (int i = 0; i < MAX_SUPERVISOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.SUPERVISOR));
        }

        for (int i = 0; i < MAX_DIRECTOR; i++) {
            dispatcher.addEmployee(new Employee(EmployeeTypes.DIRECTOR));
        }

       new Thread(() ->{
            try {
                dispatcher.run();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(MAX_CALLS*11000);

        List<Call> callsFinished = calls.stream()
                .filter(call -> call.getStatus().equals(CallStatus.FINISHED))
                .collect(Collectors.toList());

        Assert.assertEquals(callsFinished,calls);
    }

    @Test
    public void parallelCallsAndParallelEmployees() throws InterruptedException {

        List<Call> calls = new ArrayList<>();

        throwParallelCalls(30,10,calls);

        throwParallelOperators(10,30,EmployeeTypes.OPERATOR);

        throwParallelOperators(5,15,EmployeeTypes.SUPERVISOR);

        throwParallelOperators(2,4,EmployeeTypes.DIRECTOR);

        Thread ejecution = new Thread(() ->{
            try {
                dispatcher.run();
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        ejecution.start();

        Thread.sleep(10000);


        List<Call> callsFinished = calls.stream()
                .filter(call -> call.getStatus().equals(CallStatus.FINISHED))
                .collect(Collectors.toList());

        Assert.assertEquals(callsFinished,calls);
    }


    private void throwParallelCalls(int countThreads, int countCallsXThreads, List<Call> calls) {

        for (int i=0; i < countThreads; i++) {
            new Thread(() ->{
                try {
                    for (int j=0; j < countCallsXThreads; j++ ) {
                        Call call = new Call(j);
                        calls.add(call);
                        dispatcher.addCall(call);
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void throwParallelOperators(int countThreads,int countEmployeesXThread,EmployeeTypes employeeType) {

        for (int i=0; i < countThreads; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < countEmployeesXThread; j++) {
                        dispatcher.addEmployee(new Employee(employeeType));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}