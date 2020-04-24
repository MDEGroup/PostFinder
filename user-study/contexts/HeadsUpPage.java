/*
 * HeadsUp Agile
 * Copyright 2009-2012 Heads Up Development Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.headsupdev.agile.web;

import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.headsupdev.support.java.Base64;

import org.apache.wicket.*;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;




/**
 * The parent to all HeadsUp styled pages
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0
 */
public abstract class HeadsUpPage
    extends Page
    implements Serializable
{
    public static final String REMEMBER_COOKIE_NAME = "agile-login";
    public static final Pattern ID_PATTERN = Pattern.compile( "[a-zA-Z0-9-_\\.]*" );

    private static final String REMEMBER_STORE_KEY = "remembermes";
    private static Map<String, String> rememberMap = null;

    private static final String DIALOG_PANEL_ID = "agile-dialog";

    private Application application;
    private Project project;
    private List<Link> links = new LinkedList<Link>();

    private PageParameters parameters;
    private WebMarkupContainer dialog, submenu;
    private String searchQuery;
    private ArrayList<MenuLink> menuLinks = new ArrayList<MenuLink>();
    private boolean linksRendered = false;

    public HeadsUpPage()
    {
        parameters = new PageParameters();
    }

    public PageParameters getPageParameters()
    {
        return parameters;
    }

    public void setPageParameters( PageParameters parameters )
    {
        this.parameters = parameters;
    }

    protected void setHeaders( WebResponse response )
    {
        response.setHeader( "Pragma", "no-cache" );
        response.setHeader( "Cache-Control", "no-store" );
    }

    public void layout()
    {
        WebManager.getInstance().checkPermissions( this );

        Cookie remember = ( (WebRequest) getRequest() ).getCookie( REMEMBER_COOKIE_NAME );
        if ( remember != null ) {
            String value = remember.getValue();
            int pos = value.indexOf( ':' );
            if ( pos != -1 ) {
                String username = value.substring( 0, pos );
                String key = value.substring( pos + 1 );

                checkRemembers();
                if ( key.equals( rememberMap.get( username ) ) ) {
                    org.headsupdev.agile.api.User user = getSecurityManager().getUserByUsername( username );
                    if ( user != null ) {
                        getSession().setUser( user );
                    }
                }
            }
        }

        /* security check */
        if ( getRequiredPermission() != null )
        {
            requirePermission( getRequiredPermission() );
        }

        for ( MenuLink link : application.getLinks( project ) )
        {
            addLink( link );
        }

        add( CSSPackageResource.getHeaderContribution( HeadsUpPage.class, "agile.css" ) );
        add( CSSPackageResource.getHeaderContribution( HeadsUpPage.class, "mobile.css", "handheld, only screen and (max-width: 767px)" ) );

        add( new WebMarkupContainer( "headerlogo" ).add( new AttributeModifier( "src", true,
            new PropertyModel<String>( WebManager.getInstance(), "headerLogo" ) ) ) );
        add( new ListView<Application>( "mainmenu", ApplicationPageMapper.get().getApplications( getSession().getUser() ) ) {
            protected void populateItem( ListItem<Application> listItem )
            {
                final Application app = listItem.getModelObject();
                if ( "home".equals( app.getApplicationId() ) )
                {
                    listItem.setVisible( false );
                    return;
                }

                String link = "/" + project.getId() + "/" + app.getApplicationId() + "/";
                if ( ApplicationPageMapper.isHomeApp( app ) ) {
                    if ( project.equals( StoredProject.getDefault() ) )
                    {
                        link  = "/";
                    }
                    else
                    {
                        link = "/" + project.getId() + "/show/";
                    }
                }
                ExternalLink applink = new ExternalLink( "mainmenu-link", link );
                applink.add( new Label( "mainmenu-label", app.getName() ) );
                listItem.add( applink );

                listItem.add( new AttributeModifier( "class", new Model<String>()
                {
                    public String getObject() {
                        if ( app.equals( getHeadsUpApplication() ) )
                        {
                            return "mainmenu-item-selected";
                        }
                        else
                        {
                            return "mainmenu-item";
                        }
                    }
                } ) );
            }
        });

        submenu = new WebMarkupContainer( "submenu-container" );
        add( submenu );

        submenu.add( new ListView<Link>( "submenu", links ) {
            protected void populateItem( ListItem<Link> listItem )
            {
                Link link = listItem.getModelObject();
                listItem.add( link );

                listItem.add( new AttributeModifier( "class", new Model<String>()
                {
                    public String getObject() {
                        if ( getClass().equals( /* TODO find class of link */ null ) )
                        {
                            return "submenu-item-selected";
                        }
                        else
                        {
                            return "submenu-item";
                        }
                    }
                } ) );
            }
        } );

        User user = getSession().getUser();
        String pageHint = getHeadsUpApplication().getApplicationId();
        if ( ApplicationPageMapper.isHomeApp( getHeadsUpApplication() ) )
        {
            if ( ApplicationPageMapper.get().getApplication( "dashboard" ) != null )
            {
                pageHint = "show";
            }
            else
            {
                pageHint = "";
            }
        }
        WebMarkupContainer projectmenu = new WebMarkupContainer( "projectmenu" );
        projectmenu.setOutputMarkupId( true );
        projectmenu.setMarkupId( "projectmenu" );
        projectmenu.add( new ProjectListPanel( "project-tree", getStorage().getRootProjects(), getPageClass( pageHint ),
                getProject() )
                .setVisible( userHasPermission( user, new ProjectListPermission(), null ) ) );
        add( projectmenu );

        WebMarkupContainer noProjects = new WebMarkupContainer( "noprojects" );
        noProjects.setVisible( getStorage().getProjects().size() == 0 );
        projectmenu.add( noProjects );

        PageParameters params = new PageParameters();
        params.add( "project", Project.ALL_PROJECT_ID );
        BookmarkablePageLink allProjects = new BookmarkablePageLink( "allprojects-link", getPageClass( pageHint ), params );
        allProjects.add( new AttributeModifier( "class", new Model<String>() {
            public String getObject()
            {
                if ( getProject().equals( StoredProject.getDefault() ) )
                {
                    return "selected";
                }

                return "";
            }
        } ) );
        allProjects.setVisible( getStorage().getProjects().size() > 0 );
        projectmenu.add( allProjects );

        if ( !getClass().getName().endsWith( "Login" ) && !getClass().getName().endsWith( "Logout" ) )
        {
            getSession().setPreviousPageClass( getClass() );
            getSession().setPreviousPageParameters( getPageParameters() );
        }
        WebMarkupContainer userpanel = new WebMarkupContainer( "userpanel" );
        userpanel.setOutputMarkupId( true );
        userpanel.setMarkupId( "userpanel" );
        if ( !PrivateConfiguration.isInstalled() || user.equals( HeadsUpSession.ANONYMOUS_USER ) )
        {
            userpanel.add( new Label( "username", "you are not logged in" ) );
            userpanel.add( new WebMarkupContainer( "account" ).setVisible( false ) );

            Link login = new AjaxFallbackLink( "login-link" )
            {
                @Override
                public void onClick( AjaxRequestTarget target )
                {
                    if ( target == null )
                    {
                        setResponsePage( getPageClass( "login" ), getProjectPageParameters() );
                    }
                    else
                    {
                        showDialog( new LoginDialog( DIALOG_PANEL_ID, true, HeadsUpPage.this ), target );
                    }
                }
            };
            login.add( new Label( "login-label", "login" ) );
            userpanel.add( login );
        }
        else
        {
            userpanel.add( new Label( "username", "logged in as " + user.getFullnameOrUsername() ) );
            Class<? extends Page> userLink = getPageClass( "account" );
            if ( userLink != null )
            {
                userpanel.add( new BookmarkablePageLink( "account", userLink, getProjectPageParameters() ) );
            }
            else
            {
                userpanel.add( new WebMarkupContainer( "account" ).setVisible( false ) );
            }

            Link login = new AjaxFallbackLink( "login-link" )
            {
                @Override
                public void onClick( AjaxRequestTarget target )
                {
                    if ( target == null )
                    {
                        setResponsePage( getPageClass( "logout" ), getProjectPageParameters() );
                    }
                    else
                    {
                        showDialog( new LogoutDialog( DIALOG_PANEL_ID, true, HeadsUpPage.this ), target );
                    }
                }
            };
            login.add( new Label( "login-label", "logout" ) );
            userpanel.add( login );
        }

        if ( ApplicationPageMapper.get().getSearchApp() != null )
        {
            userpanel.add( new BookmarkablePageLink( "search", getPageClass( "search" ), getProjectPageParameters() ) );
        }
        else
        {
            userpanel.add( new WebMarkupContainer( "search" ).setVisible( false ) );
        }
        if ( ApplicationPageMapper.get().getSupportApp() != null )
        {
            userpanel.add( new BookmarkablePageLink( "support", ApplicationPageMapper.get().getSupportApp().getHomePage(),
                    getProjectPageParameters() ) );
        }
        else
        {
            userpanel.add( new WebMarkupContainer( "support" ).setVisible( false ) );
        }

        WebMarkupContainer userpanelButton = new WebMarkupContainer( "userpanelbutton" );
        userpanelButton.add( new Label( "label", "\u25bc" ) );
        add( userpanelButton );

        Animator animator = new Animator();
        animator.addCssStyleSubject( new MarkupIdModel( userpanel ), "up", "down" );
        animator.attachTo( userpanelButton, "onclick", Animator.Action.toggle() );

        boolean showUserTools = getSession().getUser() != null && !getSession().getUser().equals( HeadsUpSession.ANONYMOUS_USER );
        if ( showUserTools )
        {
            int userIssues = AccountSummaryPanel.getIssuesAssignedTo( getSession().getUser() ).size();

            WebMarkupContainer userDashbutton = new WebMarkupContainer( "userdashbutton" );
            userpanel.add( userDashbutton.setVisible( userIssues > 0 ) );

            Label totals = new Label( "totals", String.valueOf( userIssues ) );
            totals.add( new AttributeModifier( "class", true, new Model<String>()
            {
                @Override
                public String getObject()
                {
                    if ( AccountSummaryPanel.userHasOverdueMilestones( getSession().getUser() ) )
                    {
                        return "totals overdue";
                    }

                    if ( AccountSummaryPanel.userHasDueSoonMilestones(getSession().getUser()) )
                    {
                        return "totals duesoon";
                    }

                    return "totals";
                }
            } ) );
            userDashbutton.add( totals );

            UserDashboard dash = new UserDashboard( "userdashboard", this );
            dash.setOutputMarkupId( true );
            dash.setMarkupId( "userdashboard" );
            add( dash );
            WebMarkupContainer dashBack = new WebMarkupContainer( "userdashboardbackground" );
            dashBack.setOutputMarkupId( true );
            dashBack.setMarkupId( "userdashboardbackground" );
            add( dashBack );

            animator = new Animator();
            animator.withEaseInOutTransition();
            animator.addCssStyleSubject( new MarkupIdModel( dash ), "up", "down" );
            animator.addCssStyleSubject( new MarkupIdModel( dashBack ), "up", "down" );
            animator.addSubject( new IAnimatorSubject() {
                public String getJavaScript() {
                    return "function showBackground() {" +
                            "   var background = Wicket.$('userdashboardbackground');" +
                            "   if (userdashbuttonAnimator.state > 0) {" +
                            "       background.style.display=\"block\";" +
                            "       document.body.style.overflow=\"hidden\";" +
                            "   } else {" +
                            "       background.style.display=\"none\";" +
                            "       document.body.style.overflow=\"auto\";" +
                            "   }" +
                            "}";
                }
            } );
            animator.attachTo( userDashbutton, "onclick", Animator.Action.toggle() );
        }
        else
        {
            WebMarkupContainer userDashbutton = new WebMarkupContainer( "userdashbutton" );
            userpanel.add( userDashbutton.setVisible( false ) );

            add( new WebMarkupContainer( "userdashboard" ).setVisible( false ) );
            add( new WebMarkupContainer( "userdashboardbackground" ).setVisible( false ) );
        }
        add( userpanel );

        Form form = new Form( "quicksearch" ) {

            protected void onSubmit() {
                super.onSubmit();

                PageParameters params = getProjectPageParameters();
                params.add( "query", searchQuery );
                setResponsePage( getPageClass( "search" ), params );
            }
        };
        add( form.setVisible( ApplicationPageMapper.get().getSearchApp() != null ) );
        form.add( new TextField<String>( "query", new PropertyModel<String>( this, "searchQuery" ) ) );


        WebMarkupContainer projectselect = new WebMarkupContainer( "projectlink" );
        projectselect.add( new Label( "projectname", getProject().getAlias() + "   \u25bc" ) );
}}