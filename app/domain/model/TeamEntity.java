package domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Objects;

import play.db.ebean.Model;

@Entity
public class TeamEntity extends Model {
	private static final long serialVersionUID = 2390581929649712819L;

	// getters/setters were omitted for sake
	
	@Id @NotNull
	public Integer id;
	@NotNull @Size(max = 50)
	public String name;
	public Integer members =0;
	
	public final static Finder<Long, TeamEntity> find = new Finder<>(
			Long.class, TeamEntity.class);
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", id)
				.add("name", name).add("members", members)
				.toString();
	}
	
}
