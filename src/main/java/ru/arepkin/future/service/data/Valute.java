package ru.arepkin.future.service.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
@Root(name = "Valute", strict = false)
public class Valute {
    @Element(name = "CharCode")
    private String curCode;

    @Element(name = "Value")
    private String value;

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
