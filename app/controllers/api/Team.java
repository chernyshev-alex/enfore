package controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.google.common.collect.ImmutableMap;

import domain.model.ProjectEntity;
import domain.model.TeamEntity;
import domain.model.TeamMemberEntity;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class Team extends Controller {
	/**
	 * Creates a new Team
	 *
	 * Input:
	 *
	 * { "name": "Team Foo" }
	 *
	 * Response:
	 *
	 * 201, { "id": 1, "name": "Team Foo", "members": 0 }
	 *
	 * This is one of the required endpoints.
	 *
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createTeam() {
		final JsonNode json = request().body().asJson();
		TeamEntity team = Json.fromJson(json, TeamEntity.class);
		team.save();
		return created(Json.toJson(team));
	}

	/**
	 * Adds the Team Member to the Team if not already present
	 *
	 * Input:
	 *
	 * { "identity" : "johndoe" }
	 *
	 * Response:
	 *
	 * 201
	 *
	 * [{"identity" : "johndoe", "email" : "john@example.com", "name" :
	 * "John Doe"}]
	 *
	 * (the new list of team members)
	 *
	 * This is one of the required endpoints.
	 *
	 */
	public static Result addMember(Long teamId, String memberId) {
		TeamEntity team = TeamEntity.find.byId(teamId);
		if (team != null) {
			final JsonNode json = request().body().asJson();
			TeamMemberEntity member = Json.fromJson(json,
					TeamMemberEntity.class);
			member.setTeam(team);
			member.save();
			return created(Json.toJson(member));
		}
		return notFound();
	}

	/**
	 * Returns the Team Members of the Team with the given id
	 *
	 * Response:
	 *
	 * 200 [{"identity" : "johndoe", "email" : "john@example.com", "name" :
	 * "John Doe"}]
	 *
	 * This is one of the required endpoints.
	 *
	 */
	public static Result getMembers(Long teamId) {
		TeamEntity team = TeamEntity.find.byId(teamId);
		if (team != null) {
			// for team only
			List<TeamMemberEntity> members = TeamMemberEntity.find.all();
			return ok(Json.toJson(members));
		}
		return notFound();
	}

	/**
	 * Returns the Team with the given id
	 *
	 * Response:
	 *
	 * 200 {"id": 1, "name": "Team Foo", "members": 0}
	 *
	 */
	public static Result getTeam(Long teamId) {
		TeamEntity team = TeamEntity.find.byId(teamId);
		if (team != null) {
			return ok(Json.toJson(team));
		}
		return notFound();
	}

	/**
	 * Removes the Team Member from the given Team if they are in the team
	 *
	 * Response: 200 [] (the new list of team members, in this example an empty
	 * JSON array
	 *
	 */
	public static Result removeMember(Long teamId, String memberId) {
		TeamEntity team = TeamEntity.find.byId(teamId);
		if (team != null) {
			TeamMemberEntity.find.byId(memberId).delete("team");
			List<TeamMemberEntity> members = TeamMemberEntity.find
					.select("*").where().eq("team", teamId)
					.findList();
			return ok(Json.toJson(members));
		}
		return notFound();
	}

}