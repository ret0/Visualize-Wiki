package edu.mit.cci.visualize.wiki.xml;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.Revision;

@Root(strict = false)
public class Api {

	@Element
	private Query query;

	@Element(name = "query-continue", required = false)
	private QueryContinue queryContinue;

	public QueryContinue getQueryContinue() {
        return queryContinue;
    }

    public void setQueryContinue(final QueryContinue queryContinue) {
        this.queryContinue = queryContinue;
    }

    public Query getQuery() {
		return query;
	}

	public void setQuery(final Query query) {
		this.query = query;
	}

	/*
	 ***************************
	 * Direct Access to primitives...
	 ***************************
	 */

	public boolean isLastPageInRequestSeries() {
	    return queryContinue == null;
	}

	public String getQueryContinueID() {
	    return queryContinue.getRevisions().getRvstartid();
	}

	public List<Revision> getAllRevisionsForRequest() {
	    List<Revision> revisionsInRequest = Lists.newArrayList();
	    for (Rev rev : query.getPages().get(0).getRevisions()) {
	        revisionsInRequest.add(new Revision(rev.getUser(), rev.getTimestamp(), rev.getSize()));
        }
	    return revisionsInRequest;
	}

    /**
     * activity = editCount / daysSinceRegistration
     * Score = 0.3 * daysSinceRegistration + 0.3 * editCount + 0.4 * activity
     * @param userXML
     */
    public int generateScoreForUser() {
        User user = query.getUsers().get(0);
        Double editCount = Double.valueOf(user.getEditcount());
        DateTime now = new DateTime();
        DateTime userRegisteredAt = new DateTime(user.getRegistration());
        int daysSinceRegistration = Days.daysBetween(userRegisteredAt, now).getDays();
        Double activity = editCount / daysSinceRegistration;
        Double score = 0.3 * daysSinceRegistration + 0.3 * editCount + 0.4 * activity;
        return (int) Math.round(score);
    }
}

@Root(strict = false)
class QueryContinue {

    @Element
    private Revisions revisions;

    public Revisions getRevisions() {
        return revisions;
    }

    public void setRevisions(final Revisions revisions) {
        this.revisions = revisions;
    }
}

@Root(strict = false)
class Revisions {

    @Attribute
    private String rvstartid;

    public String getRvstartid() {
        return rvstartid;
    }

    public void setRvstartid(final String rvstartid) {
        this.rvstartid = rvstartid;
    }
}

@Root(strict = false)
class Query {

	@ElementList(required = false)
	private List<Page> pages;

	@ElementList(required = false)
	private List<User> users;

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(final List<Page> pages) {
		this.pages = pages;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(final List<User> users) {
		this.users = users;
	}
}

@Root(strict = false)
class Page {

	@ElementList
	private List<Rev> revisions;

	public void setRevisions(final List<Rev> revisions) {
		this.revisions = revisions;
	}

	public List<Rev> getRevisions() {
		return revisions;
	}

}

@Root(strict = false)
class User {

	@Attribute
	private String editcount;

	@Attribute
	private String registration;

	@Attribute
	private String name;

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(final String registration) {
		this.registration = registration;
	}

	public String getEditcount() {
		return editcount;
	}

	public void setEditcount(final String editcount) {
		this.editcount = editcount;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}

class Rev {

	@Attribute(required = false)
	private String user;

	@Attribute(required = false)
	private String minor;

	@Attribute
	private String timestamp;

	@Attribute
	private int size;

	@Attribute(required = false)
	private String anon;

	@Attribute(required = false)
	private String userhidden;

	public String getUserhidden() {
        return userhidden;
    }

    public void setUserhidden(final String userhidden) {
        this.userhidden = userhidden;
    }

    public String getAnon() {
        return anon;
    }

    public void setAnon(final String anon) {
        this.anon = anon;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMinor() {
		return minor;
	}

	public void setMinor(final String minor) {
		this.minor = minor;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
