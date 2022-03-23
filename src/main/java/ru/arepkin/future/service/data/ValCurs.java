package ru.arepkin.future.service.data;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
@Root(name = "ValCurs", strict = false)
public class ValCurs {

    @ElementList(entry = "Valute", inline = true)
    private List<Valute> items;

    public List<Valute> getItems() {
        return items;
    }

    public void setItems(List<Valute> items) {
        this.items = items;
    }

    public Map<String, String> getValuts() {
        return getItems().stream().collect(Collectors.toMap(Valute::getCurCode, Valute::getValue));
    }
}
