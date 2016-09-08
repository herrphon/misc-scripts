import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;


def customFieldManager = ComponentAccessor.getCustomFieldManager();


public FieldConfig getFieldConfig(CustomField customField) {
    def contexts = CustomFieldUtils.buildJiraIssueContexts(true, null, null);
    def context = contexts.get(0);
    FieldConfig other = customField.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig();
    FieldConfig global = customField.getRelevantConfig(context);
    return (global != null ? global : other);
}



Collection<CustomField> customFieldList = customFieldManager.getCustomFieldObjectsByName("blubb");

def customField = customFieldList.first();

StringBuilder result = new StringBuilder();
result.append("---\ncustomfields:\n")
result.append("- name: " + customField.getName() + "\n")

if (customField.getCustomFieldType() instanceof SelectCFType) {
    def customFieldConfig = getFieldConfig(customField);
    def optionsManager = ComponentAccessor.getOptionsManager();
    def options = optionsManager.getOptions(customFieldConfig);

	result.append("  type: SelectList \n" + 
                  "  values: \n")

    options.each { option ->
        result.append("  - " + option.toString() + "\n")
    }
    return "<pre>" + result + "</pre>"
}

return customField.getName() + " " + customField.getCustomFieldType().getClass()
