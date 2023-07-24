package ru.netology.card;

import com.codeborne.selenide.ElementsCollection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardOrderingTest {

    @BeforeEach
    void setUp() {
       open("http://localhost:9999");
    }

    @ParameterizedTest
    @CsvFileSource (files="src/test/resources/data_positive.csv", numLinesToSkip = 1, delimiter = '|')
    void shouldCardForm(String name, String phone, String expected) {
        $("[data-test-id=name] input").sendKeys(name);
        $("[data-test-id=phone] input").sendKeys(phone);
        $("[data-test-id=agreement]").click();
        $("button").click();

        $("[data-test-id=order-success]").shouldHave(exactText(expected));
    }

    @Test
    void shouldCardFormInvalidName() {
        ElementsCollection elements = $$(".input__control");
        elements.get(0).sendKeys("Smith John"); // Имя набрано латиницей
        elements.get(1).sendKeys("+79651234567");
        $("[data-test-id=agreement]").click();
        $("button").click();

        $("[data-test-id=name].input_invalid .input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldCardFormInvalidPhone() {
        $("[data-test-id=name] input").sendKeys("Смит Джон");
        $("[data-test-id=phone] input").sendKeys("+796512345O7"); // В номере телефона вместо 0 написано O
        $("button").click();

        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldCardFormWithoutName() { // В заявке отсутствует имя
        $("[data-test-id=phone] input").sendKeys("+79651234567");
        $("[data-test-id=agreement]").click();
        $("button").click();

        $("[data-test-id=name].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldCardFormWithoutPhone() { // В заявке отсутствует номер телефона
        $("[data-test-id=name] input").sendKeys("Смит Джон");
        $("[data-test-id=agreement]").click();
        $("button").click();

        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));

    }

    @Test
    void shouldCardFormWithoutCheckbox() { // В заявке не проставлен чек-бокс
        $("[data-test-id=name] input").sendKeys("Смит Джон");
        $("[data-test-id=phone] input").sendKeys("+79651234567");
        $("button").click();

        $("[data-test-id=agreement].input_invalid .checkbox__text").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй"));
    }
}
