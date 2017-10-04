package domain.model;

import java.util.ArrayList;
import java.util.List;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints;
import play.data.format.Formats;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
public class TaskEntity extends Model {
	private static final long serialVersionUID = 7837211051739120389L;

	@Id @NotNull
	public Long id;
	@NotNull
	public Long backlogItemId;
	@NotNull @Size(max=128)
	public String name;
	public String description;
	public TaskStatus githubStatus;
	public String githubUrl;
	
	@JsonIgnore @ManyToOne(optional = true)
	public ProjectEntity backLogEntity;
	
	@OneToMany(mappedBy="task")
	public List<GitHubComment> githubComments = new ArrayList<>();

	public static Finder<Long, TaskEntity> find = new Finder<Long, TaskEntity>(
			Long.class, TaskEntity.class);
	
	public static Optional<TaskEntity> forId(final Long id) {
		return Optional.fromNullable(find.byId(id));
	}

	public final void add() {
		if (forId(id).isPresent()) {
			throw new RuntimeException("entity " + id + " is already taken");
		} else {
			save();
		}
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", id)
				.add("backlogItemId", backlogItemId).add("name", name)
				.add("description", description).add("githubStatus", githubStatus)
				.add("githubUrl", githubUrl)
				.add("githubComments", githubComments.toString())
				.toString();
	}
	
}
