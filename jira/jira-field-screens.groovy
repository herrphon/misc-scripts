import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;

    
FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
def customFieldManager = ComponentAccessor.getCustomFieldManager();

def screens = fieldScreenManager.getFieldScreens().findAll { screen ->
    screen.name.startsWith('MSRF')
}

List<String> result = new ArrayList<>();
result.add("---")
result.add("screens:")

screens.each { screen ->
	result.add("- name: ${screen.name}")
	result.add("  tabs:")
	screen.getTabs().each { tab ->  
        if (tab.name.equals('.')) {
            result.add('  - "' + tab.name + '":')
        } else {
        	result.add("  - ${tab.name}:")
        }
		tab.getFieldScreenLayoutItems().each { item ->
            def fieldId = item.getFieldId()
            if (fieldId.startsWith("customfield_")) {
            	def cf = customFieldManager.getCustomFieldObject(fieldId)
                def name = cf.getName()
                if (name.contains('#')) {
                    result.add('    - "' + name + '"')
                } else {
                    result.add("    - ${name}")
                }
            } else {
				result.add("    - ${item.getFieldId()}")
            }
		}
    }
    result.add("")
    result.add("")
}

// def tab = screen.getTabs().first()
// result.add("Screen " + tab.position + ": " + tab.name)

// tab.addFieldScreenLayoutItem("summary", 2)
// tab.rename('test')
// tab.addFieldScreenLayoutItem('Summary')
// tab.getFieldScreenLayoutItem('Summary').remove()

// def summaryItem = tab.getFieldScreenLayoutItem('summary')
// result.add("summaryItem: " + summaryItem.remove())

// result.add("contains field: " + tab.isContainsField('summary'))


"<pre>" + result.join("\n") + "</pre>"
