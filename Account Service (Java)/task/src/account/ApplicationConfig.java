package account;

import account.dtos.PayrollDTO;
import account.models.Payroll;
import account.utils.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


        return modelMapper;
    }

    @Bean
    Converter converter() {
        return new Converter();
    }
}
