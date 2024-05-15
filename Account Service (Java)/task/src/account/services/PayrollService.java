package account.services;


import account.dtos.PayrollDTO;
import account.exceptions.DuplicateEmployeePeriodException;
import account.exceptions.EmployeeEmailNotValidException;
import account.exceptions.PayrollRecordNotFound;
import account.models.Employee;
import account.models.Payroll;
import account.repositories.PayrollRepository;
import account.utils.Converter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PayrollService {

    private final EmployeeService employeeService;

    private final PayrollRepository payrollRepository;

    private final Converter converter;

    @Autowired
    public PayrollService(EmployeeService employeeService, PayrollRepository payrollRepository, Converter converter) {
        this.employeeService = employeeService;
        this.payrollRepository = payrollRepository;
        this.converter = converter;
    }

    public List<PayrollDTO> getPayroll(String email) {
        Employee employee = employeeService.findByEmail(email)
                .orElseThrow(() -> new EmployeeEmailNotValidException("Cannot find employee %s".formatted(email)));

        List<Payroll> payrolls = payrollRepository.findByEmployee(email);

        return payrolls.stream()
                .map(payroll -> {
                    String period = converter.convertPeriodToString(payroll.getPeriod());
                    String salary = converter.convertSalaryToString(payroll.getSalary());
                    return PayrollDTO.builder()
                            .name(employee.getName())
                            .lastname(employee.getLastname())
                            .period(period)
                            .salary(salary)
                            .build();
                })
                .toList();
    }
    public PayrollDTO getPayroll(String email, String period) {
        Employee employee = employeeService.findByEmail(email)
                .orElseThrow(() -> new EmployeeEmailNotValidException("Cannot find employee %s".formatted(email)));

        Payroll payroll = payrollRepository.findByEmployeeAndPeriod(email, period)
                .orElseThrow(() -> new PayrollRecordNotFound("Cannot find payroll for %s at %s".formatted(email, period)));

        String displayPeriod = converter.convertPeriodToString(payroll.getPeriod());
        String displaySalary = converter.convertSalaryToString(payroll.getSalary());

        return PayrollDTO.builder()
                .name(employee.getName())
                .lastname(employee.getLastname())
                .period(displayPeriod)
                .salary(displaySalary)
                .build();
    }

    @Transactional
    public List<Payroll> savePayrolls(List<Payroll> payrolls) {
        Set<String> periodSet = new HashSet<>();
        List<String> emails = new ArrayList<>();


        payrolls.forEach(payroll -> {
            periodSet.add(payroll.getPeriod());
            emails.add(payroll.getEmployee());
        });

        if (periodSet.size() != payrolls.size()) {
            throw new DuplicateEmployeePeriodException("Duplicated period");
        }

        if (!employeeService.validateEmails(emails)) {
            throw new EmployeeEmailNotValidException("Invalid employee email");
        }

        Iterable<Payroll> saved = payrollRepository.saveAll(payrolls);

        List<Payroll> savedList = new ArrayList<>();
        saved.forEach(savedList::add);

        return savedList;
    }


    public Payroll updatePayroll(Payroll payroll) {
        employeeService.findByEmail(payroll.getEmployee())
                .orElseThrow(() -> new EmployeeEmailNotValidException("Employee not record"));

        Payroll record = payrollRepository.findByEmployeeAndPeriod(payroll.getEmployee(), payroll.getPeriod())
                .orElseThrow(() -> new PayrollRecordNotFound("Previous payroll record not record"));

        record.setSalary(payroll.getSalary());
        return payrollRepository.save(payroll);
    }
}
