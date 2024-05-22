package account.services;

import account.models.Employee;
import account.models.LoginInformation;
import account.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final EmployeeRepository repo;

    @Autowired
    public UserDetailsServiceImpl(EmployeeRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = repo
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .roles(employee.getRoles().toArray(new String[0]))
                .disabled(!employee.getLoginInformation().isEnabled())
                .accountLocked(employee.getLoginInformation().isLocked())
                .build();
    }

}
