package at.ait.dme.yuma4j.server.gui;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.model.tags.SemanticTag;
import at.ait.dme.yuma4j.server.URIBuilder;
import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.feeds.UserPage;
import at.ait.dme.yuma4j.server.gui.search.Search;


public abstract class BaseAnnotationListPage extends WebPage {

	private static final String SPAN = "<span class=\"grad\"></span>";
	private static final String ELLIPSIS = "...";
	private static final String SERVER_BASE_URL = ServerConfig.getInstance(WicketApplication.getConfig()).getServerBaseURL();
	
	private Logger logger = Logger.getLogger(BaseAnnotationListPage.class);
	
	public BaseAnnotationListPage() {
		add(new BookmarkablePageLink<String>("home", Search.class));
	}
	
	public void setTitle(String title) {
		add(new Label("title", title));				
	}
		
	public void setHeadline(String headline) {
		add(new Label("heading", SPAN + headline).setEscapeModelStrings(false));		
	}
	
	public void setAnnotations(List<Annotation> annotations) {
		add(new AnnotationListView("annotations", annotations));				
	}
	
	public void setFeedURL(String feedUrl) {
		if (feedUrl == null) {
			add(new ExternalLink("list-feed-url", "#").add(new SimpleAttributeModifier("style", "visibility:hidden")));
		} else {
			add(LinkHeaderContributor.forRss(feedUrl));
			add(new ExternalLink("list-feed-url", feedUrl));
		}
	}
	
	private class AnnotationListView extends ListView<Annotation> {

		private static final long serialVersionUID = 6677934776500475422L;
		
		public AnnotationListView(String id, List<Annotation> list) {
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem<Annotation> item) {
			Annotation a = (Annotation) item.getModelObject();
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(UserPage.PARAM_USERNAME, a.getCreator().getUsername());
			item.add(
				new BookmarkablePageLink<String>("author-href", UserPage.class, new PageParameters(params))
					.add(new Label("author-label", a.getCreator().getUsername())));
			
			String feedPageUri;
			try {
				feedPageUri = SERVER_BASE_URL + "object/" + URLEncoder
					.encode(a.getObjectURI(), "UTF-8")
					.replace("%", "%25");
			} catch (Throwable t) {
				// Should never happen
				logger.error(t.getMessage());
				t.printStackTrace();
				feedPageUri = "#";
			}
			String screenUri = a.getObjectURI();
			if (screenUri.length() > 55)
				screenUri = screenUri.substring(0, 55) + ELLIPSIS;
			item.add(new ExternalLink("objectPage", feedPageUri, screenUri));
}}}