package ru.mail.bamboo.plugins.build.configuration;

import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanHelper;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.util.comparator.SinglePropertyComparator;
import com.atlassian.bamboo.v2.build.BaseBuildConfigurationAwarePlugin;
import com.atlassian.bamboo.v2.build.configuration.MiscellaneousBuildConfigurationPlugin;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.bamboo.plugins.utils.RestExecutor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

@Path("predefinedVariables")
@Produces(MediaType.APPLICATION_JSON)
public class PredefinedVariablesConfigurationPlugin extends BaseBuildConfigurationAwarePlugin implements MiscellaneousBuildConfigurationPlugin {
    public static final String PREDEFINED_VARIABLES_JSON_OBJECT_KEY = "predefinedVariables";
    public static final String PLAN_LEVEL_PREDEFINED_VARIABLES = "custom.predefinedVariables.planLevelConfig";

    private final PlanManager planManager;

    public PredefinedVariablesConfigurationPlugin(PlanManager planManager, TemplateRenderer templateRenderer) {
        this.planManager = planManager;
        setTemplateRenderer(templateRenderer);
    }

    private String getJsonObject(PredefinedVariables predefinedVariables) {
        return new Gson().toJson(sort(predefinedVariables));
    }

    private PredefinedVariables sort(PredefinedVariables predefinedVariables) {
        if (predefinedVariables.getVariableSetList() != null)
            Collections.sort(predefinedVariables.getVariableSetList(), new SinglePropertyComparator<VariableSet, String>(VariableSet.class, "name", String.class, null));
        return predefinedVariables;
    }

    @Override
    public boolean isApplicableTo(@NotNull Plan plan) {
        return true;
    }

    @Override
    protected void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final Plan plan) {
        super.populateContextForView(context, plan);
        context.put(PREDEFINED_VARIABLES_JSON_OBJECT_KEY, getJsonObject(PlanHelper.getConfigObject(plan, PLAN_LEVEL_PREDEFINED_VARIABLES, PredefinedVariables.class)));
    }

    @Override
    protected void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull BuildConfiguration buildConfiguration, @Nullable Plan plan) {
        super.populateContextForEdit(context, buildConfiguration, plan);
        if (plan != null)
            context.put(PREDEFINED_VARIABLES_JSON_OBJECT_KEY, getJsonObject(PlanHelper.getConfigObject(plan, PLAN_LEVEL_PREDEFINED_VARIABLES, PredefinedVariables.class)));
    }

    @GET
    @Path("{planKey}")
    public Response getPredefinedVariables(final @PathParam("planKey") String planKey) {
        return new RestExecutor<PredefinedVariables>() {
            @Override
            protected PredefinedVariables doAction() throws Exception {
                Plan plan = planManager.getPlanByKey(PlanKeys.getPlanKey(planKey));
                if (plan == null)
                    return null;
                return sort(PlanHelper.getConfigObject(plan, PLAN_LEVEL_PREDEFINED_VARIABLES, PredefinedVariables.class));
            }
        }.getResponse();
    }
}
