package com.renison.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.renison.exception.BadRequestException;
import com.renison.exception.InternalErrorException;
import com.renison.exception.NotFoundException;
import com.renison.jackson.View.Admin;
import com.renison.model.Category;
import com.renison.model.CategoryScore;
import com.renison.model.Test;
import com.renison.model.TestSession;
import com.renison.util.JsonUtil;

@RestController
@RequestMapping("/tests")
@CrossOrigin("*")
public class TestController extends BaseController<Test> {

	public TestController() {
		super();
	}

	public Test getActive() {
		Test test = (Test) this.sessionFactory.getCurrentSession().createCriteria(getResourceType())
				.add(Restrictions.eq("active", true)).uniqueResult();
		if (test == null) {
			throw new NotFoundException(33002698748l, "No active test", "");
		}
		return test;
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/copy", method = RequestMethod.POST)
	public @ResponseBody Test copyTest(@PathVariable Long testId) {
		Session session = sessionFactory.getCurrentSession();
		Test test = get(testId);
		test.setName(test.getName() + " copy");
		test.detach();
		session.clear();
		session.persist(test);
		return test;
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/categories", method = RequestMethod.GET)
	public List<Category> getCategories(@PathVariable Long testId) {
		return this.get(testId).getCategories();
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/categories", method = RequestMethod.POST)
	public @ResponseBody Category createCategory(@PathVariable Long testId, @RequestBody Category category) {
		Test test = this.get(testId);
		test.addCategory(category);
		sessionFactory.getCurrentSession().save(test);
		return category;
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/status", method = RequestMethod.POST)
	public @ResponseBody Test toggleTestStatus(@PathVariable Long testId, @RequestParam Boolean isStart) {
		if (isStart == null) {
			throw new BadRequestException(963558814l, "need isStart param", "");
		}
		Test test = this.get(testId);
		Session session = sessionFactory.getCurrentSession();
		if (isStart) {// deactivate all other test when starting a specific one
			int result = session.createSQLQuery("UPDATE test set active = false").executeUpdate();
			if (result == 0) {
				throw new InternalErrorException(55894320l, "cannot execute query", "");
			}
		}
		test.setActive(isStart);
		session.save(test);
		return test;
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/score", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.OK)
	public void scoreTest(@PathVariable Long testId) {
		Test test = get(testId);
		Session session = sessionFactory.getCurrentSession();
		Set<TestSession> testSessions = test.getTestSessions();
		for (TestSession testSession : testSessions) { // for each testsession
			testSession.scoreTest(session);
			session.save(testSession);
		}
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/{testId}/report", method = RequestMethod.GET)
	public @ResponseBody ArrayNode generateReport(@PathVariable Long testId) {
		Test test = get(testId);
		Session session = sessionFactory.getCurrentSession();
		Set<TestSession> testSessions = test.getTestSessions();
		ArrayNode result = JsonUtil.newArrayNode();
		for (TestSession t : testSessions) {
			ObjectNode row = JsonUtil.newJsonObject();
			row = JsonUtil.merge(row, t.getStudent().toReportFormat());
			// get something like
			// {"listening":50,"reading":30} keys are category name, values are
			// scores
			ObjectNode categoryScores = JsonUtil.newJsonObject();
			for (Category category : test.getCategories()) {
				Optional<CategoryScore> categoryScore = t.getCategoryScores().stream()
						.filter((cs) -> cs.getCategory().equals(category)).findFirst();
				String categoryName = category.getName() + " (total: " + category.getTotalScore() + ")";
				categoryScores.put(categoryName,
						categoryScore.isPresent() ? categoryScore.get().getScore().doubleValue() : 0.0);
			}
			row = JsonUtil.merge(row, categoryScores);
			result.add(row);
		}
		return result;
	}

	@JsonView(Admin.class)
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	// this is a hack to resolve a weird frontend issue
	// angular does not like passing testId in the url
	public @ResponseBody ArrayNode generateReport2(@RequestParam("id") Long id) {
		return generateReport(id);
	}

	protected Class<Test> getResourceType() {
		return Test.class;
	}
}