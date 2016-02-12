${webResourceManager.requireResource("ru.mail.bamboo.plugins.bamboo-utils:predefinedVariables-resources")}
[#if predefinedVariables??]
<script>
    AJS.toInit(function() {
        var predefinedVars = JSON.parse('${predefinedVariables?js_string}' || '{}');
        predefinedVars.variableSetList && _.each(predefinedVars.variableSetList, AJS.$.proxy(function(predefinedSet) {
            AJS.$('#predefinedVariables-add-variable-set').before(new Backbone.View.VariableSetView(predefinedSet).render().$el);
        }, this));
    });
</script>
[/#if]
[@ui.bambooSection titleKey="ru.mail.bamboo.plugins.build.configuration.predefinedVariables.title" cssClass="predefinedVariables-edit-panel"]
<div class="description">${action.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.description')}</div>
<input type="hidden" name="custom.predefinedVariables"/>
<a id="predefinedVariables-add-variable-set" class="aui-button aui-button-link">${action.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.addSet')}</a>
[/@ui.bambooSection]
<script type="text/template" id="predefinedVariables-set">
    <div class="predefinedVariables-variable-set">
        <div class="field-group">
            <label>${action.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.nameSet')}</label>
            <input class="text variable-set-name" type="text" autocomplete="off" value="<%= name %>"/>
            <a class="aui-button aui-button-subtle delete-variable-set" title="Delete" tabindex="-1">
                <span class="aui-icon aui-icon-small aui-iconfont-delete" title="${action.getText('global.buttons.delete')}">${action.getText('global.buttons.delete')}</span>
            </a>
        </div>
        <fieldset class="group">
            <legend><span>${action.getText('plan.configuration.general.variables')}</span></legend>
            <div class="field-group">
                <table class="aui variables-list">
                    <colgroup>
                        <col width="30%">
                        <col width="*">
                        <col width="10">
                    </colgroup>
                    <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th class="operations"><span class="assistive"></span></th>
                    </tr>
                    </thead>
                    <tfoot>
                    <tr>
                        <td colspan="3">
                            <a class="aui-button aui-button-link aui-style variable-define">${action.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.defineVariable')}</a>
                        </td>
                    </tr>
                    </tfoot>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </fieldset>
    </div>
</script>
<script type="text/template" id="predefinedVariables-variable-row">
    <tr>
        <td>
            <select class="select variable-name">
            [#list action.plan.effectiveVariables as variable]
                <option value="${variable.key}"
                <%= name == '${variable.key}' ? 'selected' : '' %> >${variable.key}</option>
            [/#list]
            </select>
        </td>
        <td>
            <input class="text variable-value" type="text" autocomplete="off" value="<%= value %>"/>
        </td>
        <td class="operations">
            <a class="delete-variable" title="${action.getText('global.buttons.delete')}" tabindex="-1">
                <span class="aui-icon aui-icon-small aui-iconfont-remove" title="${action.getText('global.buttons.delete')}">${action.getText('global.buttons.delete')}</span>
            </a>
        </td>
    </tr>
</script>