package domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class TeamMemberEntity extends Model {

	private static final long serialVersionUID = 3688286425198415611L;

	@Id @Constraints.Required
	@Formats.NonEmpty
	@Size(max = 16)
	public String identity;
	@Formats.NonEmpty
	@Size(max = 64)
	public String name;
	@Constraints.Required
	@Formats.NonEmpty
	@Size(max = 64)
	public String email;

	@JsonIgnore @ManyToOne(optional = false)
	public TeamEntity team;

	public final static Finder<String, TeamMemberEntity> find = new Finder<>(
			String.class, TeamMemberEntity.class);

	public void setTeam(TeamEntity team) {
		this.team = team;
		
	}
	
}
