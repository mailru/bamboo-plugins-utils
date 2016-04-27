(function($) {
    AJS.toInit(function() {
        var variableSetViewTemplate = _.template($('#predefinedVariables-set').html());
        var variableTemplate = _.template($('#predefinedVariables-variable-row').html());

        $('#predefinedVariables-add-variable-set').on('click', function() {
            $('#predefinedVariables-add-variable-set').before(new Backbone.View.VariableSetView({}).render().$el);
        });
        $('#updateChainMiscellaneous').submit(function() {
            var variableSetList = [];

            $('.predefinedVariables-variable-set').each(function(index, setContainer) {
                var $setContainer = $(setContainer);
                var name = $setContainer.find('.variable-set-name').val();
                var variables = {};
                $setContainer.find('tbody tr').each(function(index, row) {
                    var $row = $(row);
                    variables[$row.find('.variable-name').val()] = $row.find('.variable-value').val();
                });

                variableSetList.push({name: name, variables: variables});
            });

            $('input[name="custom.predefinedVariables"]').val(JSON.stringify({variableSetList: variableSetList}));
        });

        Backbone.View.VariableSetView = Backbone.View.extend({
            events: {
                'click .variable-define': '_defineVariable',
                'click .delete-variable': '_deleteVariable',
                'click .delete-variable-set': '_deleteVariableSet'
            },
            initialize: function(options) {
                this.name = options.name || '';
                this.variables = options.variables || [];
                this.render();
            },
            render: function() {
                this.$el.html(variableSetViewTemplate({name: this.name}));
                $.each(this.variables, $.proxy(function(name, value) {
                    this._addVariable({name: name, value: value});
                }, this));
                return this;
            },
            _defineVariable: function() {
                this._addVariable();
            },
            _addVariable: function(variable) {
                this.$('tbody').append(variableTemplate(variable || {name: '', value: ''}));
            },
            _deleteVariable: function(e) {
                $(e.target).closest('tr').remove();
            },
            _deleteVariableSet: function() {
                this.remove();
            }
        });
    });
})(AJS.$);
