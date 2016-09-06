import org.apache.log4j.Logger;

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.SearchService.ParseResult;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.util.ImportUtils;


String getIssuesFromJql(String jql) {
	User currentCrowdUser = ComponentAccessor.getUserUtil().getUser("admin");
	ComponentManager componentManager = ComponentManager.getInstance();
	SearchService searchService = componentManager.getSearchService();
	ParseResult parseResult = searchService.parseQuery( currentCrowdUser, jql );

	String issueString = "";

	if (parseResult.isValid()) {
		SearchResults results = searchService.search( currentCrowdUser,
									parseResult.getQuery(),
									PagerFilter.getUnlimitedFilter() );

		issueString += results.issues.size + "\n"
		results.issues.each{ issue ->
			issueString += issue.key + "\n"      
		}
	} else {
		throw new RuntimeException("Something is wrong");
	}

	return issueString
}

int removeAttachmentsFromJql(String jql) {
	User currentCrowdUser = ComponentAccessor.getUserUtil().getUser("admin");
	ComponentManager componentManager = ComponentManager.getInstance();
	SearchService searchService = componentManager.getSearchService();
	ParseResult parseResult = searchService.parseQuery( currentCrowdUser, jql );

	int count = -1;
	if (parseResult.isValid()) {
		SearchResults results = searchService.search( currentCrowdUser,
									parseResult.getQuery(),
									PagerFilter.getUnlimitedFilter() );
		count = results.issues.size;
		
		results.issues.each{ issue ->
			removeAttachmentsFromIssue(issue);
			reindexIssue(issue);
		}
	} else {
		throw new RuntimeException("Something is wrong");
	}
	return count;
}

void removeAttachmentsFromIssue(Issue issue) {
	AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
	List<Attachment> attachments = attachmentManager.getAttachments(issue);
	 for (Attachment attachment : attachments) {
		attachmentManager.deleteAttachment(attachment);
	}
}

void reindexIssue(Issue issue){
   boolean wasIndexing = ImportUtils.isIndexIssues();
   ImportUtils.setIndexIssues(true);
   try {
      ComponentAccessor.getIssueIndexManager().reIndex(issue);
   } catch (IndexException e) {
      System.out.println("Reindex error!!");
   }finally{
      ImportUtils.setIndexIssues(wasIndexing);
   }
}


String result = "";
String jql = "\"Size of Attachments\" > 100 AND resolutiondate < -360d"
result += "before:<br>" + getIssuesFromJql(jql);

removeAttachmentsFromJql(jql);

result += "<br> after:<br>" + getIssuesFromJql(jql);
