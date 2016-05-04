package ru.mail.bamboo.plugins.utils.build.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class VariableSet implements Serializable {
    private String name;
    private Map<String, String> variables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getVariables() {
        if (variables == null)
            variables = new HashMap<String, String>();
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}
