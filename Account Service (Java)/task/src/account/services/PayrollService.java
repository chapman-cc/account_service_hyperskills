package account.services;


import account.dtos.PayrollDTO;
import account.dtos.PayrollRequestBody;
import account.exceptions.DuplicateEmployeePeriodException;
import account.exceptions.EmployeeEmailNotValidException;
import account.exceptions.PayrollRecordNotFound;
import account.models.Employee;
import account.models.Payroll;
import account.repositories.PayrollRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper  modelMapper;

    @Autowired
    public PayrollService(EmployeeService employeeService, PayrollRepository payrollRepository, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.payrollRepository = payrollRepository;
        this.modelMapper = modelMapper;
    }

    public List<PayrollDTO> getPayroll(String email) {
        return payrollRepository.findByEmployeeEmail(email).stream()
                .map(payroll -> modelMapper.map(payroll, PayrollDTO.class))
                .toList();
    }

    public PayrollDTO getPayroll(String email, String period) {
        Payroll payroll = payrollRepository.findByEmployeeEmailAndPeriod(email, period)
                .orElseThrow(() -> new PayrollRecordNotFound("Cannot find payroll for %s at %s".formatted(email, period)));

        return modelMapper.map(payroll, PayrollDTO.class);
    }

    @Transactional
    public List<Payroll> savePayrolls(List<PayrollRequestBody> bodies) {
        Set<String> periodSet = new HashSet<>();
        Set<String> emailSet = new HashSet<>();
        List<Payroll> pendingPayrolls = new ArrayList<>();

        bodies.forEach(payroll -> {
            periodSet.add(payroll.getPeriod());
            emailSet.add(payroll.getEmployeeEmail());
        });
        List<String> emails = emailSet.stream().toList();

        if (periodSet.size() != bodies.size()) {
            throw new DuplicateEmployeePeriodException("Duplicated period");
        }

        if (!employeeService.validateEmails(emails)) {
            throw new EmployeeEmailNotValidException("Invalid employee email");
        }

        Employee employee = null;

        for (PayrollRequestBody body : bodies) {
            if (employee == null || employee.getEmail().equals(body.getEmployeeEmail())) {
                employee = employeeService.findByEmail(body.getEmployeeEmail())
                        .orElseThrow(() -> new EmployeeEmailNotValidException("Employee not record"));
            }

            pendingPayrolls.add(Payroll.builder()
                    .period(body.getPeriod())
                    .salary(body.getSalary())
                    .employee(employee)
                    .build());
        }

        Iterable<Payroll> saved = payrollRepository.saveAll(pendingPayrolls);

        List<Payroll> savedList = new ArrayList<>();
        saved.forEach(savedList::add);

        return savedList;
    }

    @Transactional
    public Payroll updatePayroll(PayrollRequestBody body) {
        Payroll payroll = payrollRepository.findByEmployeeEmailAndPeriod(body.getEmployeeEmail(), body.getPeriod())
                .orElseThrow(() -> new PayrollRecordNotFound("Previous payroll record not record"));

        payroll.setSalary(body.getSalary());
        return payrollRepository.save(payroll);
    }
}
