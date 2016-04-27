package ru.mail.bamboo.plugins.utils.build.configuration;

import com.atlassian.bamboo.build.BuildDefinition;
import com.atlassian.bamboo.plugin.module.ext.CustomBuildDefinitionTransformer;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PredefinedVariablesConfigurationTransformer implements CustomBuildDefinitionTransformer {
    private static final String PREDEFINED_VARIABLES_ENABLED = "custom.predefinedVariables";

    @Override
    public void transformBuildDefinition(@NotNull Map<String, Object> configObjects,
                                         @NotNull Map<String, String> configParameters,
                                         @NotNull BuildDefinition buildDefinition) {
        if (configParameters.containsKey(PREDEFINED_VARIABLES_ENABLED))
            try {
                configObjects.put(PredefinedVariablesConfigurationPlugin.PLAN_LEVEL_PREDEFINED_VARIABLES,
                                  new Gson().fromJson(configParameters.get(PREDEFINED_VARIABLES_ENABLED), PredefinedVariables.class));
            } catch (Exception e) {
                configObjects.put(PredefinedVariablesConfigurationPlugin.PLAN_LEVEL_PREDEFINED_VARIABLES, new PredefinedVariables());
            }
        else
            configObjects.put(PredefinedVariablesConfigurationPlugin.PLAN_LEVEL_PREDEFINED_VARIABLES, new PredefinedVariables());
    }
}
