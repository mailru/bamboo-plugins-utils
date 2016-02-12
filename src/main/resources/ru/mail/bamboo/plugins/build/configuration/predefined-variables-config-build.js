(function($) {
    AJS.toInit(function() {
        BAMBOO.simpleDialogForm = function(options) {
            var defaults = {
                trigger: null,
                dialogWidth: null,
                dialogHeight: null,
                success: null,
                cancel: null,
                header: null,
                help: null,
                showPredefinedVariables: false
            };
            options = AJS.$.extend(defaults, options);
            var $trigger,
                $loading = AJS.$('<span class="icon icon-loading aui-dialog-content-loading" />'),
                $formContainer = AJS.$('<div class="aui-dialog-content"/>').html($loading),
                dialog,
                predefinedVariables,
                setFocus = function() {
                    var $firstError = $formContainer.find(":input:visible:enabled.errorField:first").focus();
                    if (!$firstError.length) {
                        $formContainer.find(":input:visible:enabled:first").focus();
                    }
                },
                setupDialogContent = function(html) {
                    $formContainer.html(html);
                    setupForm($formContainer.find("form"));
                    BAMBOO.asyncForm({
                        $delegator: $formContainer,
                        target: "form",
                        success: function(data) {
                            if (AJS.$.isFunction(options.success)) {
                                options.success(data);
                            }
                            dialog.remove();
                            $formContainer.html($loading);
                        },
                        cancel: function(e) {
                            e.preventDefault();
                            if (AJS.$.isFunction(options.cancel)) {
                                options.cancel(e);
                            }
                            dialog.remove();
                            $formContainer.html($loading);
                        },
                        formReplaced: setupForm
                    });
                },
                setupForm = function($dialogForm) {
                    if (options.help) {
                        addHelp();
                    }
                    if (options.showPredefinedVariables)
                        addPredefinedVariables();
                    setFocus();
                    BAMBOO.DynamicFieldParameters.syncFieldShowHide($dialogForm, false);
                },
                showDialog = function() {
                    dialog = new AJS.Dialog({
                        width: options.dialogWidth,
                        height: options.dialogHeight,
                        keypressListener: function(e) {
                            if (e.which == jQuery.ui.keyCode.ESCAPE) {
                                dialog.remove();
                                $formContainer.html($loading);
                            }
                        }
                    });
                    var header = options.header ? options.header : $trigger.attr("title");
                    if (header) {
                        dialog.addHeader(header);
                    }
                    dialog.addPanel("", $formContainer);

                    // $trigger.attr("data-dialog-href") won't be needed once we're on jQuery 1.4.3
                    AJS.$.ajax({
                        url: $trigger.attr("href") || $trigger.data("dialog-href") || $trigger.attr("data-dialog-href"),
                        data: {'bamboo.successReturnMode': 'json', decorator: 'nothing', confirm: true},
                        success: setupDialogContent,
                        cache: false
                    });

                    dialog.show();
                },
                addHelp = function() {
                    return AJS.$('<div />', {
                        'class': 'dialog-tip',
                        html: options.help
                    }).prependTo($formContainer.find('.buttons-container'));
                },
                addPredefinedVariables = function() {
                    var planKey = $('#continueParametrisedBuild').length ? $('#continueParametrisedBuild_planKey').val() : $('#runParametrisedManualBuild_planKey').val();

                    $.ajax({
                        url: AJS.format('{0}/rest/bamboo-utils/1.0/predefinedVariables/{1}', AJS.contextPath(), planKey),
                        success: function(data) {
                            predefinedVariables = data;
                            if (predefinedVariables.variableSetList && predefinedVariables.variableSetList.length) {
                                var $overrideBtn = $('.custom-build .variables-list .variables-override');
                                $overrideBtn.closest('td').attr('colspan', 1);

                                var options = _.map(predefinedVariables.variableSetList, function(variableSet) {
                                    return {text: variableSet.name, value: variableSet.name};
                                });
                                var output = '<td>' + bamboo.feature.variables.select({
                                    id: 'predefinedVariables-variables-set-select',
                                    name: '',
                                    options: options,
                                    extraClasses: ''
                                }) + '</td>';
                                output += '<td style="text-align: left;"><a class="aui-button aui-button-link aui-style predefinedVariables-variables-set-override">' + AJS.I18n.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.overrideFromSet') + '</a></td>';
                                $overrideBtn.closest('tr').append(output);

                                $('.custom-build .predefinedVariables-variables-set-override').on('click', handleOverrideVariables);
                            }
                        },
                        cache: false
                    });
                },
                handleOverrideVariables = function(e) {
                    var $variableTableBody = $('.custom-build .variables-list tbody');

                    var variableSet = $('#predefinedVariables-variables-set-select').val();
                    _.each(predefinedVariables.variableSetList, function(varSet) {
                        if (variableSet == varSet.name)
                            $.each(varSet.variables, function(key, value) {
                                var $row = $(bamboo.feature.variables.variablesTableRow({
                                    id: '',
                                    key: '',
                                    value: '',
                                    availableVariables: [{
                                        "text": key,
                                        "value": key,
                                        "extraAttributes": {"data-current-value": value}
                                    }]
                                })).appendTo($variableTableBody);

                                var existingVariableInputs = $variableTableBody.find(AJS.format('input[name="variable_{0}"]', key));
                                existingVariableInputs.each(function(idx, input) {
                                    $(input).closest('tr').remove();
                                });
                                handleChangeVariableSelection.call($row);
                            });
                    });

                    e.preventDefault();
                },
                handleChangeVariableSelection = function() {
                    var $this = $(this),
                        $option = $this.find('option:selected'),
                        $text = $this.closest('tr').find('input:text'),
                        id = $option.val();
                    $this.prop('name', 'key_' + id);
                    $text.prop('name', 'variable_' + id).val($option.get(0).getAttribute('data-current-value') || '');
                };

            if (options.trigger) {
                var namespace = "click";
                if (options.eventNamespace && options.eventNamespace.length) {
                    namespace += "." + options.eventNamespace;
                }
                AJS.$(document).undelegate(options.trigger, namespace);
                AJS.$(document).delegate(options.trigger, namespace, function(e) {
                    $trigger = AJS.$(this);
                    e.preventDefault();
                    showDialog();
                });
            } else {
                showDialog();
            }
        };

        var parameterisedManualBuildBtn = $('#runMenuParent a[id^=parameterisedManualBuild_]');
        parameterisedManualBuildBtn.each(function(idx, btn) {
            if ($(btn).hasClass('run-custom-stage'))
                BAMBOO.simpleDialogForm({
                    trigger: '.run-custom-stage',
                    showPredefinedVariables: true,
                    dialogWidth: 800,
                    dialogHeight: 400,
                    success: redirectAfterReturningFromDialog,
                    cancel: null
                });
            else
                BAMBOO.simpleDialogForm({
                    trigger: '#' + btn.id,
                    showPredefinedVariables: true,
                    dialogWidth: 800,
                    dialogHeight: 400,
                    success: redirectAfterReturningFromDialog,
                    cancel: null,
                    header: AJS.I18n.getText('chain.editParameterisedManualBuild.form.title'),
                    help: AJS.I18n.getText('ru.mail.bamboo.plugins.build.configuration.predefinedVariables.help')
                });
        });

    });
})(AJS.$);
