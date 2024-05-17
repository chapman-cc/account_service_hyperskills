package account.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConverterTest {

    private Converter converter;

    public ConverterTest() {
        this.converter = new Converter();
    }

    @BeforeEach
    void setUp() {
        converter = new Converter();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertNumPeriodToStringPeriod() {

        assertThat(converter.convertPeriodToString("01-2024")).isEqualTo("January-2024");
        assertThat(converter.convertPeriodToString("02-2024")).isEqualTo("February-2024");
        assertThat(converter.convertPeriodToString("03-2024")).isEqualTo("March-2024");
        assertThat(converter.convertPeriodToString("04-2024")).isEqualTo("April-2024");
        assertThat(converter.convertPeriodToString("05-2024")).isEqualTo("May-2024");
        assertThat(converter.convertPeriodToString("06-2024")).isEqualTo("June-2024");
        assertThat(converter.convertPeriodToString("07-2024")).isEqualTo("July-2024");
        assertThat(converter.convertPeriodToString("08-2024")).isEqualTo("August-2024");
        assertThat(converter.convertPeriodToString("09-2024")).isEqualTo("September-2024");
        assertThat(converter.convertPeriodToString("10-2024")).isEqualTo("October-2024");
        assertThat(converter.convertPeriodToString("11-2024")).isEqualTo("November-2024");
        assertThat(converter.convertPeriodToString("12-2024")).isEqualTo("December-2024");

        assertThatThrownBy(() -> assertThat(converter.convertPeriodToString("13-2024")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void convertSalaryToString() {
        assertThat(converter.convertSalaryToString(123456)).isEqualTo("1234 dollar(s) 56 cent(s)");
    }
}