package account.utils;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class Converter {
    public String convertPeriodToString(String period) {
        String numMonth = period.substring(0, 2);
        Month month = Month.of(Integer.parseInt(numMonth));
        String name = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return period.replaceFirst(numMonth, name);
    }

    public String convertSalaryToString(long salaryInCents) {
        double salary = (double) salaryInCents / 100;
        String string = String.valueOf(salary);
        String[] strings = string.split("\\.");
        String integers = strings[0];
        String decimals = strings[1];
        return "%s dollar(s) %s cent(s)".formatted(integers, decimals);
    }
}
