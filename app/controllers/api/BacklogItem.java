package controllers.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.JsonNode;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import domain.model.BackLogItemEntity;
import domain.model.BacklogItemStatus;
import domain.model.BacklogItemType;
import domain.model.GitHubComment;
import domain.model.Priority;
import domain.model.TaskEntity;
import domain.model.TaskStatus;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class BacklogItem extends Controller {
	/**
	 * Returns the Backlog Item with the given id
	 *
	 * Response:
	 *
	 * 200 { "id" : 1, "name" : "Shiny UI", "summary" :
	 * "As a user, I want to have a shiny UI", "itemType" : "FEATURE",
	 * "storyPoints" : 5, "priority" : "URGENT", "status" : "ESTIMATED", "tasks"
	 * : 3, "projectId" : 1 }
	 *
	 */
	public static Result getBacklogItem(Long backlogItemId) {
		BackLogItemEntity item = BackLogItemEntity.find.byId(backlogItemId);
		return (item != null) ? ok(Json.toJson(item)) : notFound();
	}

	/**
	 * Returns the Task with the given id, belonging to the specified Backlog
	 * Item (Tasks are not globally unique, only in the context of their Backlog
	 * Item resource)
	 *
	 * Response:
	 *
	 * 200 { "id" : 1, "name" : "Set up project", "description" :
	 * "Set up the build file with all dependencies", "backlogItemId" : 1,
	 * "githubStatus" : "OPEN", "githubUrl" :
	 * "https://github.com/octocat/Hello-World/issues/1347", "githubComments" :
	 * [ { "login" : "johndoe", "body" : "This is more difficult than expected",
	 * "url" :
	 * "https://github.com/octocat/Hello-World/issues/1347#issuecomment-1" } ] }
	 *
	 */
	public static Result getTask(Long backlogItemId, Long taskId) {
		BackLogItemEntity backLogItem = BackLogItemEntity.find
				.byId(backlogItemId);
		if (backLogItem != null) {
			List<TaskEntity> tasks = backLogItem.taskItems;
			for (TaskEntity task : tasks) {
				if (task.id == taskId) {
					return ok(Json.toJson(task));
				}
			}
		}
		return notFound();
	}

	/**
	 * Returns all Tasks belonging to this Backlog Item
	 *
	 * Response: 200 [{ "id" : 1, "name" : "Set up project", "description" :
	 * "Set up the build file with all dependencies", "backlogItemId" : 1,
	 * "githubStatus" : "OPEN", "githubUrl" :
	 * "https://github.com/octocat/Hello-World/issues/1347", "githubComments" :
	 * [ { "login" : "johndoe", "body" : "This is more difficult than expected",
	 * "url" :
	 * "https://github.com/octocat/Hello-World/issues/1347#issuecomment-1" } ]
	 * }]
	 *
	 */
	public static Result getTasks(Long backlogItemId) {
		BackLogItemEntity backLogItem = BackLogItemEntity.find
				.byId(backlogItemId);
		return (backLogItem != null) ? ok(Json.toJson(backLogItem.taskItems))
				: notFound();
	}

	/**
	 * Replaces the summary of the Backlog Item with the given id with a new one
	 *
	 * Input:
	 *
	 * { "summary" : "my new summary" }
	 *
	 * Response:
	 *
	 * 200, empty
	 */
	public static Result changeSummary(Long backlogItemId) {
		BackLogItemEntity backLogItem = BackLogItemEntity.find
				.byId(backlogItemId);
		if (backLogItem != null) {
			JsonNode json = request().body().asJson();
			backLogItem.summary = json.path("summary").getTextValue();
			// TODO git change summary
			return ok();
		}
		return notFound();
	}

	/**
	 * Prioritizes the Backlog Item with the given id
	 *
	 * Input:
	 *
	 * { "priority" : "LOW" }
	 *
	 * Response:
	 *
	 * 200, empty
	 */
	public static Result prioritize(Long backlogItemId) {
		BackLogItemEntity backLogItem = BackLogItemEntity.find
				.byId(backlogItemId);
		if (backLogItem != null) {
			JsonNode json = request().body().asJson();
			backLogItem.priority = Priority.valueOf(json.path("priority")
					.getTextValue());
			// TODO GITHUB

			return ok();
		}
		return notFound();
	}

	/**
	 * Creates a new Backlog Item
	 *
	 * Input:
	 *
	 * { "name" : "Shiny UI", "summary" :
	 * "As a user, I want to have a shiny UI", "itemType" : "FEATURE",
	 * "storyPoints" : 5, "priority" : "URGENT", "projectId" : 1 }
	 *
	 * Response:
	 *
	 * 201 { "id" : 1, "name" : "Shiny UI", "summary" :
	 * "As a user, I want to have a shiny UI", "itemType" : "FEATURE",
	 * "storyPoints" : 5, "priority" : "URGENT", "status" : "ESTIMATED", "tasks"
	 * : 0, "projectId" : 1 }
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createBacklogItem() {
		JsonNode json = request().body().asJson();
		BackLogItemEntity item = Json.fromJson(json, BackLogItemEntity.class);
		// TODO GITHUB
		item.save();
		return created(Json.toJson(item));
	}

	/**
	 * Creates a new Task for the given Backlog Item, as well as a GitHub issue
	 * linked to that task in the GitHub repository that is linked to the
	 * Project of the Backlog Item
	 *
	 * Input:
	 *
	 * { "name" : "Set up project", "description" :
	 * "Set up the build file with all dependencies", "backlogItemId" : 1 }
	 *
	 * Response:
	 *
	 * 201 { "id" : 1, "name" : "Set up project", "description" :
	 * "Set up the build file with all dependencies", "backlogItemId" : 1,
	 * "githubStatus" : "OPEN", "githubUrl" :
	 * "https://github.com/octocat/Hello-World/issues/1347", "githubComments" :
	 * [] }
	 *
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createTask(Long backlogItemId) {
		BackLogItemEntity backLog = BackLogItemEntity.find
				.byId(backlogItemId);
		if (backLog != null) {
			JsonNode json = request().body().asJson();
			TaskEntity task = Json.fromJson(json, TaskEntity.class);
			task.githubStatus = TaskStatus.OPEN;
			task.githubUrl = "";
			task.githubComments.add(new GitHubComment("login", "body", "url"));
			backLog.taskItems.add(task);
			task.save();
			backLog.save();
			return created(Json.toJson(task));
		}
		return notFound();
	}
}