package domain;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public enum EmployeeTypes {
    OPERATOR(1), SUPERVISOR(2), DIRECTOR(3);

    private int orderNum;

    EmployeeTypes(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getOrderNum() {
        return orderNum;
    }
}
