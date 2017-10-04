package domain.model;

import java.util.List;

import play.db.ebean.*;
import play.data.validation.Constraints;
import play.data.format.Formats;

import com.google.common.base.Objects;
import javax.persistence.*;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
public class ProjectEntity extends Model {
	private static final long serialVersionUID = 3186952000077433972L;

	// omitted getter/setters for the sake
	
	@Id @GeneratedValue
	@Constraints.Required
	@Formats.NonEmpty
	public Long id;
	@Constraints.Required
	@Formats.NonEmpty
	public String name;
	public String description;
	@Constraints.Required
	@Formats.NonEmpty
	public Integer teamId;
	@Constraints.Required
	@Formats.NonEmpty
	public String githubUrl = "";
	@Constraints.Required
	@Formats.NonEmpty
	public String gitUrl = "";
	public Integer githubWatchers = 0;
	public Integer githubForks = 0;

	@JsonIgnore @OneToMany(mappedBy = "projectEntity")
	public List<BackLogItemEntity> backLogItems;

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", id)
				.add("name", name).add("teamId", teamId)
				.add("githubUrl", githubUrl).add("gitUrl", gitUrl)
				.add("description", description)
				.add("githubWatchers", githubWatchers)
				.add("githubForks", githubForks)
				.add("backLogItems", backLogItems.toString()).toString();
	}

	public final static Finder<Long, ProjectEntity> find = new Finder<>(
			Long.class, ProjectEntity.class);

}
