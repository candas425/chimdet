package ro.utcn.backend.backendservices;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Employee;
import ro.utcn.backend.model.Holiday;
import ro.utcn.backend.model.factory.IEntity;
import ro.utcn.backend.repositories.EmployeeRepository;
import ro.utcn.frontend.frontendServices.helper.WorklogEmployee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for Employee
 * Created by Lucian on 5/20/2017.
 */

@Service
public class EmployeeService extends BaseService {

    @Autowired
    private HolidayService holidayService;
    @Autowired
    private EmployeeRepository employeeRepository;

    public void saveEmployee(Employee employee) throws PersistanceException {
        List<Employee> employeeList = employeeRepository.findAllByNumeOrCnp(employee.getNume(), employee.getCnp());
        if (employeeList.isEmpty()) {
            employeeRepository.save(employee);
        } else {
            throw new PersistanceException(PersistanceException.OBJECT_EXIST);
        }
    }

    public void deleteEmployee(Employee employee) throws PersistanceException {
        List<Holiday> holidayList = holidayService.getAllForEmployee(employee);
        for(Holiday holiday: holidayList){
            holidayService.deleteHoliday(holiday);
        }

        employeeRepository.delete(employee);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public void update(IEntity baseModel) throws PersistanceException {
        employeeRepository.save((Employee) baseModel);
    }

    public List<WorklogEmployee> getEmployeeListForCondica(LocalDate date) {
        List<WorklogEmployee> worklogEmployeeList = new ArrayList<>();
        List<Employee> employees = getAll();
        int numberOfDays = date.getMonth().length(date.isLeapYear());
        for (int i = 1; i <= numberOfDays; i++) {
            LocalDate tempDate = LocalDate.of(date.getYear(), date.getMonth(), i);
            for (Employee employee : employees) {
                WorklogEmployee worklogEmployee = new WorklogEmployee(employee, tempDate);
                worklogEmployeeList.add(worklogEmployee);
            }
        }
        return worklogEmployeeList;
    }
}
