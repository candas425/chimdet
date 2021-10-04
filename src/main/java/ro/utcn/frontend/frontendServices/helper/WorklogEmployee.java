package ro.utcn.frontend.frontendServices.helper;

import ro.utcn.backend.model.Employee;

import java.time.LocalDate;

/**
 * Helper class for Condica
 *
 * @author Lucas
 */
public class WorklogEmployee {

    private Employee employee;
    private LocalDate localDate;

    public WorklogEmployee(Employee employee, LocalDate localDate) {
        this.employee = employee;
        this.localDate = localDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
