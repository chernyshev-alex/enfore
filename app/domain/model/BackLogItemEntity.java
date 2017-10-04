package domain.model;

import java.util.List;

import play.db.ebean.*;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Objects;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity(name ="backlog")
public class BackLogItemEntity extends Model {

	private static final long serialVersionUID = 4775310743469194378L;

	@Id @GeneratedValue @NotNull
	public Long id;
	@NotNull @Size(max=64)
	public String name;
	@NotNull @Size(max=1024)
	public String summary;
	@NotNull 
	public BacklogItemType itemType;
	public Integer storyPoints =0;
	@NotNull 
	public Priority priority;
	@NotNull 
	public BacklogItemStatus status;
	public Integer tasks =0;
	@NotNull 
	public Long projectId;

	@JsonIgnore @ManyToOne
	public ProjectEntity projectEntity;

	@JsonIgnore 
	@OneToMany(mappedBy = "backLogEntity", cascade=CascadeType.ALL)
	public List<TaskEntity> taskItems;

	public final static Finder<Long, BackLogItemEntity> find = new Finder<>(
			Long.class, BackLogItemEntity.class);
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", id)
				.add("name", name).add("summary", summary)
				.add("itemType", itemType).add("storyPoints", storyPoints)
				.add("priority", priority).add("status", status)
				.add("projectId", projectId).add("tasks", tasks)
				.add("taskItems", taskItems.toString())
				.toString();
	}

}
