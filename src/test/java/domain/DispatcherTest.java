package domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import util.QueueBlock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by javierzolotarchuk on 14/05/18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { DispatcherTest.class })
public class DispatcherTest {

    Dispatcher dispatcher = Dispatcher.getInstance();

    @Test
    public void getEmployeeWithLowerHierarchyWithAlTypesOfEmployees() throws Exception {

        QueueBlock<Employee> employeesQueue = createEmployeesQueue(EmployeeTypes.OPERATOR,EmployeeTypes.SUPERVISOR,EmployeeTypes.DIRECTOR);
        compareCorrectTypeEmployee(dispatcher,EmployeeTypes.OPERATOR,employeesQueue);
    }

    @Test
    public void getEmployeeWithLowerHierarchyWithOnlySupervisorsAndDirectors() throws Exception {

        QueueBlock<Employee> employeesQueue = createEmployeesQueue(EmployeeTypes.SUPERVISOR,EmployeeTypes.DIRECTOR);
        compareCorrectTypeEmployee(dispatcher,EmployeeTypes.SUPERVISOR,employeesQueue);
    }

    @Test
    public void getEmployeeWithLowerHierarchyWithOnlyDirectors() throws Exception {

        QueueBlock<Employee> employeesQueue = createEmployeesQueue(EmployeeTypes.DIRECTOR);
        compareCorrectTypeEmployee(dispatcher,EmployeeTypes.DIRECTOR,employeesQueue);
    }

    private QueueBlock<Employee> createEmployeesQueue(EmployeeTypes... types) {

        List<Employee> employeesList = Arrays.stream(types)
                .map(type -> new Employee(type)).collect(Collectors.toList());

        QueueBlock<Employee> employeesQueue = new QueueBlock<>();
        employeesList.forEach(emp -> employeesQueue.add(emp));

        return employeesQueue;
    }

    private void compareCorrectTypeEmployee(Dispatcher dispatcher,EmployeeTypes typeExpected, QueueBlock<Employee> employeesQueue) throws Exception {

        Whitebox.setInternalState(dispatcher,"employeesQueque",employeesQueue);

        Employee employee = Whitebox.<Employee> invokeMethod(dispatcher,"getEmployeeWithLowerHierarchy");

        Assert.assertEquals(employee.getType(),typeExpected);
    }


}