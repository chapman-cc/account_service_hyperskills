package account.utils;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.regex.Pattern;

import static junit.framework.TestCase.*;


public class RegexTest {

    @Test
    public void testEmployeeEmailRegex() {
        // write test against testing regex fits acme.com
        Pattern pattern = Pattern.compile(Regex.EMPLOYEE_EMAIL);

        assertTrue(pattern.matcher("john@acme.com").matches());
        assertTrue(pattern.matcher("John@acme.com").matches());

        assertFalse(pattern.matcher("John@amc.com").matches());
        assertFalse(pattern.matcher("john@amce.com").matches());
        assertFalse(pattern.matcher("John@amce.co").matches());
    }

    @Test
    public void testPayrollPeriodRegex(){
        Pattern pattern = Pattern.compile(Regex.PAYROLL_PERIOD);

        assertTrue(pattern.matcher("01-2024").matches());
        assertTrue(pattern.matcher("06-2024").matches());
        assertTrue(pattern.matcher("12-2024").matches());
    }
}