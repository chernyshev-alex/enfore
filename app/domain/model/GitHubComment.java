package domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.base.Objects;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class GitHubComment extends Model {

	private static final long serialVersionUID = 7229439673115305355L;

	@Id @GeneratedValue
	@Constraints.Required
	@Formats.NonEmpty
	public Long id;
	public String login;
	public String body;
	public String url;

	@JsonIgnore @ManyToOne(optional=false)
	public TaskEntity task;
	
	public GitHubComment(String login, String body, String url) {
		this.login = login;
		this.body = body;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", id)
				.add("login", login)
				.add("body", body)
				.add("url", url).toString();
	}
	
}
