package ru.mail.bamboo.plugins.build.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PredefinedVariables implements Serializable {
    private List<VariableSet> variableSetList;

    public List<VariableSet> getVariableSetList() {
        return variableSetList;
    }

    public void setVariableSetList(List<VariableSet> variableSetList) {
        this.variableSetList = variableSetList;
    }
}
