package account;

import account.dtos.PayrollDTO;
import account.dtos.PayrollRequestBody;
import account.models.Payroll;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Configuration
public class ApplicationConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Payroll.class, PayrollDTO.class)
                .addMappings(mapper -> {
                    mapper.map(payroll -> payroll.getEmployee().getName(), PayrollDTO::setName);
                    mapper.map(payroll -> payroll.getEmployee().getLastname(), PayrollDTO::setLastname);
                    mapper.using(periodConverter).map(Payroll::getPeriod, PayrollDTO::setPeriod);
                    mapper.using(salaryConverter).map(Payroll::getSalary, PayrollDTO::setSalary);
                });
        return modelMapper;
    }

    private final Converter<String, String> periodConverter = ctx -> {
        String period = ctx.getSource();
        String numMonth = period.substring(0, 2);
        Month month = Month.of(Integer.parseInt(numMonth));
        String name = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return period.replaceFirst(numMonth, name);
    };

    private final Converter<Long, String> salaryConverter = ctx -> {
        long salaryInCents = ctx.getSource();
        double salary = (double) salaryInCents / 100;
        String string = String.valueOf(salary);
        String[] strings = string.split("\\.");
        String integers = strings[0];
        String decimals = strings[1];
        return "%s dollar(s) %s cent(s)".formatted(integers, decimals);
    };


}
