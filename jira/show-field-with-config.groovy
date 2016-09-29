import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.impl.TextAreaCFType;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;


class YamlGenerator {
    private final CustomFieldManager customFieldManager;
    private final OptionsManager optionsManager;

    public YamlGenerator(CustomFieldManager customFieldManager, OptionsManager optionsManager) {
		this.customFieldManager = customFieldManager;
        this.optionsManager = optionsManager;
	}

    public String getCustomFieldValues(customFieldNames) {       
        String result = "---\ncustomfields:\n"

		customFieldNames.each { 
    		result += getCustomFieldValue(it)
		}
        
        
		return "<pre>" + result + "</pre>"
    }
    
    private String getCustomFieldValue(String customFieldName) {
        Collection<CustomField> customFields = customFieldManager.getCustomFieldObjectsByName(customFieldName);
        CustomField customField = customFields.first();

        StringBuilder result = new StringBuilder();
        result.append("- name: " + customField.getName() + "\n")

        if (customField.getCustomFieldType() instanceof SelectCFType) {
            def customFieldConfig = getFieldConfig(customField);
            def options = optionsManager.getOptions(customFieldConfig);

            result.append("  type: SelectList \n")
            result.append("  description: \"${customField.description}\" \n")
            result.append("  values: \n")

            options.each { option ->
                result.append("  - " + option.toString() + "\n")
            }
            return result + "\n"
        }
        if (customField.getCustomFieldType() instanceof RenderableTextCFType) {
            result.append("  type: TextField \n")
            result.append("  description: \"${customField.description}\" \n\n")
            
        	return result
        }
        if (customField.getCustomFieldType() instanceof TextAreaCFType) {
            result.append("  type: TextArea \n")
            result.append("  description: \"${customField.description}\" \n\n")

        	return result
        }
        if (customField.getCustomFieldType() instanceof DateCFType) {
            result.append("  type: DatePicker \n")
        	result.append("  description: \"${customField.description}\" \n\n")
        	return result
        }
        result.append("  type: " + customField.getCustomFieldType().getClass() + "\n\n")
        return result
    }
    
    private FieldConfig getFieldConfig(CustomField customField) {
        def contexts = CustomFieldUtils.buildJiraIssueContexts(true, null, null);
        def context = contexts.get(0);
        FieldConfig other = customField.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig();
        FieldConfig global = customField.getRelevantConfig(context);
        return (global != null ? global : other);
    }

}

        
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
OptionsManager optionsManager = ComponentAccessor.getOptionsManager();

YamlGenerator yamlGenerator = new YamlGenerator(customFieldManager, optionsManager)

def fields = ['field 1', 'field 2']

yamlGenerator.getCustomFieldValues(fields)

