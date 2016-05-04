package ru.mail.bamboo.plugins.utils.build.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

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
