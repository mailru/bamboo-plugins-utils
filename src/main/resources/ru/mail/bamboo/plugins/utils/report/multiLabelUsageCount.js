(function ($) {
    AJS.toInit(function () {
        var $label = $('fieldset.dependsOnreportKey');
        if ($('#generateReport_reportKey option:selected').val() == 'ru.mail.bamboo.plugins.bamboo-utils:multiLabelUsageCount')
            $label.show();

        $label.addClass('showOnru.mail.bamboo.plugins.bamboo-utils:multiLabelUsageCount').style("display;");
    });
})(AJS.$);
