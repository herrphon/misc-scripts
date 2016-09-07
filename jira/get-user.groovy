import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.user.ApplicationUser

ApplicationUser currentUser = ComponentAccessor.jiraAuthenticationContext.user;
User currentCrowdUser = ComponentAccessor.userManager.getUserObject(currentUser.name);
User someUser = ComponentAccessor.userManager.getUserObject('john.doe');
