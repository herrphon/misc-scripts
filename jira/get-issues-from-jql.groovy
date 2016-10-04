import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.index.IssueIndexManager



List<Issue> getIssuesFromJqlForUser(jql, user) {
    SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
    SearchService.ParseResult parseResult =  searchService.parseQuery(user, jql);
    IssueManager issueManager = ComponentAccessor.getIssueManager()
    List<Issue> issues = null;

    if (parseResult.isValid()) {
        def searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
    
        // Transform issues from DocumentIssueImpl to the "pure" form IssueImpl (some methods don't work with DocumentIssueImps)
        issues = searchResult.issues.collect {issueManager.getIssueObject(it.id)}
    } else {
        log.error("Invalid JQL: " + jql);
    } 
    return issues
}





String jql = 'project = TEST'
ApplicationUser currentUser = ComponentAccessor.jiraAuthenticationContext.user;
User currentCrowdUser = ComponentAccessor.userManager.getUserObject(currentUser.name);

List<Issue> issues = getIssuesFromJqlForUser(jql, currentCrowdUser)
