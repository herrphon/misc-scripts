import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueImpl

def issue = ComponentAccessor.issueManager.getIssueObject("CR-79814");
