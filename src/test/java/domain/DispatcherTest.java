package domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import util.QueueBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

/**
 * Created by javierzolotarchuk on 14/05/18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { DispatcherTest.class })
public class DispatcherTest {

    Dispatcher dispatcher = Dispatcher.getInstance();

    @Test
    public void addCall() throws Exception {

        QueueBlock<Call> callsQuequeMock = mock(QueueBlock.class);
        Whitebox.setInternalState(dispatcher,"callsQueue",callsQuequeMock);

        Call call = new Call(0);
        dispatcher.addCall(call);

        verify(callsQuequeMock,times(1)).add(call);
    }

    @Test
    public void addEmployee() throws Exception {

        QueueBlock<Employee> employeesQuequeMock = mock(QueueBlock.class);
        Whitebox.setInternalState(dispatcher,"employeesQueque",employeesQuequeMock);

        Employee employee = new Employee(EmployeeTypes.OPERATOR);
        dispatcher.addEmployee(employee);

        verify(employeesQuequeMock,times(1)).add(employee);
    }

    @Test
    public void getEmployeeAvilable() {

        QueueBlock<Employee> employeesQuequeMock = mock(QueueBlock.class);
        Whitebox.setInternalState(dispatcher,"employeesQueque",employeesQuequeMock);

        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee(EmployeeTypes.OPERATOR);
        employees.add(employee);

        when(employeesQuequeMock.getOriginalQueue()).thenReturn(employees);

        dispatcher.getEmployeeAvailable();

        verify(employeesQuequeMock,times(1)).downCountAndBlock();
        verify(employeesQuequeMock,times(1)).getOriginalQueue();
        verify(employeesQuequeMock,times(1)).removeElement(employee);
        verify(employeesQuequeMock,times(1)).unBlock();
    }

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