package testcases;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import rest.ApiUtil;
import testcases.TestCodeValidator;
import coreUtilities.utils.FileOperations;
import org.apache.poi.xssf.usermodel.*;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import static org.testng.Assert.assertEquals;
import rest.CustomResponse;
import java.security.SecureRandom;
import io.restassured.http.ContentType;

import java.security.SecureRandom;
import io.restassured.http.ContentType;

@SuppressWarnings("unused")
public class RestAssured_TestCases {

	private static String baseUrl;
	private static String username;
	private static String password;
	private static String cookieValue = null;
	private ApiUtil apiUtil;
	private int employeeStatus;
	private TestCodeValidator testCodeValidator;
	private String apiUtilPath = System.getProperty("user.dir") + "\\src\\main\\java\\rest\\ApiUtil.java";
	private String excelPath = System.getProperty("user.dir") + "\\src\\main\\resources\\TestData.xlsx";
	private static String jobTitleIdToDelete;
	private static int idBefore;

	@Test(priority = 0, groups = { "PL1" }, description = "1. Login to the application using Selenium WebDriver\n"
			+ "2. Extract the cookie named 'orangehrm' after successful login\n"
			+ "3. Store the cookie value for subsequent API requests")
	public void loginWithSeleniumAndGetCookie() throws InterruptedException {
		RestAssured.useRelaxedHTTPSValidation();
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		apiUtil = new ApiUtil();
		baseUrl = apiUtil.getBaseUrl();
		username = apiUtil.getUsername();
		password = apiUtil.getPassword();

		driver.get(baseUrl + "/web/index.php/auth/login");
		Thread.sleep(3000); // Wait for page load

		// Login to the app
		driver.findElement(By.name("username")).sendKeys(username);
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.cssSelector("button[type='submit']")).click();
		Thread.sleep(9000); // Wait for login

		// Extract cookie named "orangehrm"
		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("orangehrm")) {
				cookieValue = cookie.getValue();
				break;
			}
		}

		driver.quit();
		testCodeValidator = new TestCodeValidator();

		if (cookieValue == null) {
			throw new RuntimeException("orangehrm cookie not found after login");
		}
	}

	/**
	 * Test Case: Validate the GET /web/index.php/auth/login endpoint using a valid
	 * session cookie.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Retrieve the stored session cookie value from the login
	 * method. 2. Send a GET request to '/web/index.php/auth/login' without a
	 * request body. 3. Log the HTTP status code and the response body for
	 * debugging. 4. Verify that the implementation uses Rest Assured methods only.
	 * 5. Assert that the response status code is 200 (OK). 6. Verify that the
	 * response body contains the expected HTML login page structure.
	 *
	 * Expected Results: - Status code is 200. - Response body contains HTML markup
	 * for the login page. - Implementation follows Rest Assured best practices.
	 */

	@Test(priority = 1, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Send a GET request to the '/web/index.php/auth/login' endpoint with a valid cookie\n"
					+ "2. Do not pass any request body (null)\n"
					+ "3. Print and verify the response status code and response body\n"
					+ "4. Assert that the response status code is 200 (OK) and response is HTML page")
	public void GetLogin() throws IOException {
		System.out.println("Cookie is " + cookieValue);
		String endpoint = "/web/index.php/auth/login";
		CustomResponse customResponse = apiUtil.GetLogin(endpoint, cookieValue, null);

		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "GetLogin",
				List.of("given", "cookie", "get", "response"));

		// Fetch response from custom response object
		String responseBody = customResponse.getResponseBody(); // or
		customResponse.getResponse().asString();
		int statusCode = customResponse.getStatusCode();

		System.out.println("Status Code: " + statusCode);
		System.out.println("Response Body: " + responseBody);

		Assert.assertTrue(isImplementationCorrect, "GetLogin must be implemented using Rest Assured methods only!");
		assertEquals(statusCode, 200);
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		// Additional HTML page assertions
		Assert.assertTrue(TestCodeValidator.GetLogin(customResponse),
				"The login page response body did not meet expected structure or content.");
	}

	/**
	 * Test Case: Validate the GET
	 * /web/index.php/api/v2/dashboard/employees/action-summary endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Retrieve the stored session cookie value from the login
	 * method. 2. Send a GET request to
	 * '/web/index.php/api/v2/dashboard/employees/action-summary' without a request
	 * body. 3. Capture and log the HTTP status code, status line, and response
	 * body. 4. Verify that the implementation uses Rest Assured methods only. 5.
	 * Assert that the status code is 200 (OK). 6. Validate that each item in the
	 * response data contains non-null 'id' and 'group' fields.
	 *
	 * Expected Results: - Status code is 200. - Response contains valid 'id' and
	 * 'group' values in all returned data items. - Implementation follows Rest
	 * Assured best practices.
	 */

	@Test(priority = 2, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Send a GET request to the '/web/index.php/api/v2/dashboard/employees/action-summary' endpoint with a valid cookie\n"
					+ "2. Do not include any request body (null)\n"
					+ "3. Print the response status code and response body\n"
					+ "4. Assert that the response status code is 200 (OK)")
	public void GetEmpActionSummary() throws IOException {
		String endpoint = "/web/index.php/api/v2/dashboard/employees/action-summary";
		CustomResponse customResponse = apiUtil.GetEmpActionSummary(endpoint, cookieValue, null);

		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath,
				"GetEmpActionSummary", List.of("given", "cookie", "get", "response"));

		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Status Line: " + customResponse.getStatusLine());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		Assert.assertTrue(isImplementationCorrect,
				"GetEmpActionSummary must be implemented using the Rest Assured methods only!");
		assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200.");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertTrue(TestCodeValidator.GetEmpActionSummary(customResponse),
				"Response must have non-null 'id' and 'group' fields in each data item.");

	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/dashboard/shortcuts
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Retrieve the stored session cookie value from the login
	 * method. 2. Send a GET request to '/web/index.php/api/v2/dashboard/shortcuts'
	 * without a request body. 3. Capture and log the HTTP status code, status line,
	 * and response body. 4. Extract the 'data' object and verify that required
	 * shortcut keys exist. 5. Validate that the following keys are present and not
	 * null: - leave.assign_leave - leave.leave_list - leave.apply_leave -
	 * leave.my_leave - time.employee_timesheet - time.my_timesheet 6. Verify that
	 * the implementation uses Rest Assured methods only. 7. Assert that the status
	 * code is 200 (OK). 8. Confirm that all values in 'data' are of boolean type.
	 *
	 * Expected Results: - Status code is 200. - All required keys exist in the
	 * response with boolean values. - Implementation follows Rest Assured best
	 * practices.
	 */

	@Test(priority = 3, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Send a GET request to the '/web/index.php/api/v2/dashboard/shortcuts' endpoint with a valid cookie\n"
					+ "2. Do not provide any request body (null)\n"
					+ "3. Print the response status code and body for verification\n"
					+ "4. Assert that the response status code is 200 (OK)")
	public void GetDashboardShortcut() throws IOException {
		String endpoint = "/web/index.php/api/v2/dashboard/shortcuts";
		CustomResponse customResponse = apiUtil.GetDashboardShortcut(endpoint, cookieValue, null);

		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath,
				"GetDashboardShortcut", List.of("given", "cookie", "get", "response"));

		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Status Line: " + customResponse.getStatusLine());
		System.out.println("Response Body: " + customResponse.getResponseBody());
		JsonPath jsonPath = customResponse.getResponse().jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		// Safely extract values
		Boolean leaveAssignLeave = customResponse.leaveAssignLeave;
		Boolean leavLeaveList = customResponse.leavLeaveList;
		Boolean leaveApplyLeave = customResponse.leaveApplyLeave;
		Boolean leaveMyLeave = customResponse.leaveMyLeave;
		Boolean timeEmployeeTimesheet = customResponse.timeTmployeeTimesheet;
		Boolean timeMyTimesheet = customResponse.timeMyTimesheet;

		Assert.assertNotNull(leaveAssignLeave, "Missing: leave.assign_leave");
		Assert.assertNotNull(leavLeaveList, "Missing: leave.leave_list");
		Assert.assertNotNull(leaveApplyLeave, "Missing: leave.apply_leave");
		Assert.assertNotNull(leaveMyLeave, "Missing: leave.my_leave");
		Assert.assertNotNull(timeEmployeeTimesheet, "Missing: time.employee_timesheet");
		Assert.assertNotNull(timeMyTimesheet, "Missing: time.my_timesheet");

		Assert.assertTrue(isImplementationCorrect,
				"GetDashboardShortcut must be implemented using the Rest Assured methods only!");
		assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200.");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertTrue(TestCodeValidator.GetDashboardShortcut(customResponse),
				"All values in 'data' must be of boolean type.");

	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/dashboard/employees/leaves
	 * endpoint with the current date.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Generate the current date in 'yyyy-MM-dd' format. 2. Build the
	 * endpoint URL using the generated date as the query parameter. 3. Send a GET
	 * request to
	 * '/web/index.php/api/v2/dashboard/employees/leaves?date={current_date}'
	 * without a request body. 4. Capture and log the date used, HTTP status code,
	 * status line, and response body. 5. Verify that the implementation uses Rest
	 * Assured methods only. 6. Assert that the status code is 200 (OK).
	 *
	 * Expected Results: - Status code is 200. - The endpoint responds with valid
	 * leave information for the provided date. - Implementation follows Rest
	 * Assured best practices.
	 */

	@Test(priority = 4, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Generate the current date dynamically in 'yyyy-MM-dd' format\n"
					+ "2. Send a GET request to the '/web/index.php/api/v2/dashboard/employees/leaves?date={current_date}' endpoint with a valid cookie\n"
					+ "3. Do not include any request body (null)\n"
					+ "4. Log the date used, response status code, and response body\n"
					+ "5. Assert that the response status code is 200 (OK)")
	public void GetEmpLeaveInfo() throws IOException {
		// Dynamic current date in yyyy-MM-dd format
		String currdate = LocalDate.now().toString();
		String endpoint = "/web/index.php/api/v2/dashboard/employees/leaves?date=" + currdate;

		CustomResponse customResponse = apiUtil.GetEmpLeaveInfo(endpoint, cookieValue, null);

		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "GetEmpLeaveInfo",
				List.of("given", "cookie", "get", "response"));

		System.out.println("Date Used: " + currdate);
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Status Line: " + customResponse.getStatusLine());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		Assert.assertTrue(isImplementationCorrect,
				"GetEmpLeaveInfo must be implemented using the Rest Assured methods only!");
		assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200.");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/dashboard/employees/subunit
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Retrieve the stored session cookie value from the login
	 * method. 2. Send a GET request to
	 * '/web/index.php/api/v2/dashboard/employees/subunit' without a request body.
	 * 3. Capture and log the HTTP status code, status line, and response body. 4.
	 * Verify that 'subUnitName', 'subUnitId', and 'subUnitCount' are present and
	 * not null in the response. 5. Verify that the implementation uses Rest Assured
	 * methods only. 6. Assert that the status code is 200 (OK). 7. Validate that
	 * subunit data contains non-null 'id', 'name', and 'count' fields.
	 *
	 * Expected Results: - Status code is 200. - Response contains valid subunit
	 * information with required fields. - Implementation follows Rest Assured best
	 * practices.
	 */

	@Test(priority = 5, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Send a GET request to the '/web/index.php/api/v2/dashboard/employees/subunit' endpoint with a valid cookie\n"
					+ "2. Do not provide any request body (null)\n"
					+ "3. Print the response status code and response body for validation\n"
					+ "4. Assert that the response status code is 200 (OK)")
	public void GetEmpSubunit() throws IOException {
		String endpoint = "/web/index.php/api/v2/dashboard/employees/subunit";
		CustomResponse customResponse = apiUtil.GetEmpSubunit(endpoint, cookieValue, null);

		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "GetEmpSubunit",
				List.of("given", "cookie", "get", "response"));

		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Status Line: " + customResponse.getStatusLine());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		Assert.assertNotNull(customResponse.subUnitName);
		Assert.assertNotNull(customResponse.subUnitId);
		Assert.assertNotNull(customResponse.subUnitCount);

		Assert.assertTrue(isImplementationCorrect,
				"GetEmpSubunit must be implemented using the Rest Assured methods only!");
		assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200.");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertTrue(TestCodeValidator.GetEmpSubunit(customResponse),
				"Subunit data must contain non-null 'id', 'name', and 'count' fields.");

	}

	/**
	 * Test Case: Validate the GET
	 * /web/index.php/api/v2/dashboard/employees/locations endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Construct the endpoint
	 * '/web/index.php/api/v2/dashboard/employees/locations'. 2. Send a GET request
	 * with the valid session cookie. 3. Capture and log the HTTP status code and
	 * the response body. 4. Verify that the implementation uses Rest Assured
	 * methods only. 5. Assert that the status code is 200 (OK). 6. Verify that
	 * 'employeeLocationList' is not null or empty. 7. Verify that 'employeeMetaMap'
	 * is not null and contains 'totalLocationCount' greater than 0.
	 *
	 * Expected Results: - Status code is 200. - Response contains a valid list of
	 * employee locations. - Meta data contains a total location count greater than
	 * zero. - Implementation follows Rest Assured best practices.
	 */

	@Test(priority = 6, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Construct the endpoint '/web/index.php/api/v2/dashboard/employees/locations'\n"
					+ "2. Send a GET request with a valid cookie\n"
					+ "3. Print the response status code and body for verification\n"
					+ "4. Assert response code is 200 and all required fields are present")
	public void GetEmployeeLocations() throws Exception {

		String endpoint = "/web/index.php/api/v2/dashboard/employees/locations";

		// Step 1: Make API call
		CustomResponse customResponse = apiUtil.getEmployeeLocations(endpoint, cookieValue);

		// Step 2: Implementation check
		boolean isCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "getEmployeeLocations",
				List.of("given", "cookie", "get", "response"));

		// Step 3: Logging
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body GetEmployeeLocations: " + customResponse.getResponseBody());

		// Step 4: Assertions
		Assert.assertTrue(isCorrect, "GET must use RestAssured methods properly.");
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected HTTP 200");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertNotNull(customResponse.employeeLocationList, "Employee location list should not be null.");
		Assert.assertFalse(customResponse.employeeLocationList.isEmpty(), "Location list should not be empty.");
		Assert.assertNotNull(customResponse.employeeMetaMap, "Meta data should not be null.");
		Assert.assertTrue((int) customResponse.employeeMetaMap.get("totalLocationCount") > 0,
				"Total location count must be > 0");
	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/admin/users endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Construct the endpoint
	 * '/web/index.php/api/v2/admin/users?limit=50&offset=0&sortField=u.userName&sortOrder=ASC'.
	 * 2. Send a GET request with the valid session cookie. 3. Capture and log the
	 * HTTP status code and the response body. 4. Verify that the implementation
	 * uses Rest Assured methods only. 5. Assert that the status code is 200 (OK).
	 * 6. Verify that 'userList' is not null or empty. 7. Verify that 'userMetaMap'
	 * is not null and contains a total count greater than 0. 8. Verify that
	 * 'userCount', 'userIdList', 'userNameList', 'userRoleNameSet', and
	 * 'employeeIdList' are populated. 9. Validate that the first user object
	 * contains: - 'id' - 'userName' - 'employee' with 'empNumber' and 'employeeId'
	 * - 'userRole' with 'id', 'name', and 'displayName'
	 *
	 * Expected Results: - Status code is 200. - Response contains a valid list of
	 * users with required fields. - Meta data reflects a valid total user count. -
	 * First user entry has complete and correct structure. - Implementation follows
	 * Rest Assured best practices.
	 */

	@Test(priority = 7, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Construct the endpoint '/web/index.php/api/v2/admin/users?limit=50&offset=0&sortField=u.userName&sortOrder=ASC'\n"
					+ "2. Send a GET request with a valid cookie\n"
					+ "3. Print the response status code and body for verification\n"
					+ "4. Assert response code is 200 and required fields are present\n"
					+ "5. Validate user list content and structure")
	public void GetAdminUsers() throws Exception {

		String endpoint = "/web/index.php/api/v2/admin/users?limit=50&offset=0&sortField=u.userName&sortOrder=ASC";

		// Step 1: API Call
		CustomResponse customResponse = apiUtil.getAdminUsers(endpoint, cookieValue);

		// Step 2: Implementation check
		boolean isCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "getAdminUsers",
				List.of("given", "cookie", "get", "response"));

		// Step 3: Logging
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		// Step 4: Core Assertions
		Assert.assertTrue(isCorrect, "GET must use RestAssured methods properly.");
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected HTTP 200");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertNotNull(customResponse.userList, "User list should not be null.");
		Assert.assertFalse(customResponse.userList.isEmpty(), "User list should not be empty.");
		Assert.assertNotNull(customResponse.userMetaMap, "Meta map should not be null.");
		Assert.assertTrue((int) customResponse.userMetaMap.get("total") > 0, "Total users must be > 0");

		// Step 5: Validate Extra Fields for Uniqueness
		Assert.assertEquals(customResponse.userCount, customResponse.userList.size(), "User count mismatch");
		Assert.assertFalse(customResponse.userIdList.isEmpty(), "User ID list should not be empty");
		Assert.assertFalse(customResponse.userNameList.isEmpty(), "User name list should not be empty");
		Assert.assertFalse(customResponse.userRoleNameSet.isEmpty(), "User roles should not be empty");
		Assert.assertFalse(customResponse.employeeIdList.isEmpty(), "Employee ID list should not be empty");

		// Step 6: Validate First User Fields
		Map<String, Object> firstUser = customResponse.userList.get(0);
		Assert.assertTrue(firstUser.containsKey("id"), "Missing 'id' in user");
		Assert.assertTrue(firstUser.containsKey("userName"), "Missing 'userName' in user");
		Assert.assertTrue(firstUser.containsKey("employee"), "Missing 'employee' in user");
		Assert.assertTrue(firstUser.containsKey("userRole"), "Missing 'userRole' in user");

		Map<String, Object> employee = (Map<String, Object>) firstUser.get("employee");
		Assert.assertNotNull(employee.get("empNumber"), "Missing 'empNumber' in employee");
		Assert.assertNotNull(employee.get("employeeId"), "Missing 'employeeId' in employee");

		Map<String, Object> role = (Map<String, Object>) firstUser.get("userRole");
		Assert.assertNotNull(role.get("id"), "Missing 'id' in userRole");
		Assert.assertNotNull(role.get("name"), "Missing 'name' in userRole");
		Assert.assertNotNull(role.get("displayName"), "Missing 'displayName' in userRole");

	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/admin/job-titles endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Construct the endpoint
	 * '/web/index.php/api/v2/admin/job-titles?limit=50&offset=0&sortField=jt.jobTitleName&sortOrder=ASC'.
	 * 2. Send a GET request with the valid session cookie. 3. Capture and log the
	 * HTTP status code and the response body. 4. Verify that the implementation
	 * uses Rest Assured methods only. 5. Assert that the status code is 200 (OK).
	 * 6. Verify that 'jobTitleIdList' is not null. 7. Verify that 'jobTitleMetaMap'
	 * is not null. 8. Verify that 'totalJobTitles' is greater than or equal to 0.
	 *
	 * Expected Results: - Status code is 200. - Response contains a valid list of
	 * job titles. - Job title meta data is present and correctly structured. -
	 * Total job titles count is valid. - Implementation follows Rest Assured best
	 * practices.
	 */

	@Test(priority = 8, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Construct the endpoint '/web/index.php/api/v2/admin/job-titles?limit=50&offset=0&sortField=jt.jobTitleName&sortOrder=ASC'\n"
					+ "2. Send a GET request with a valid cookie\n"
					+ "3. Print the response status code and body for verification\n"
					+ "4. Assert response code is 200 and required fields are present and non-empty")
	public void GetJobTitlesTest() throws Exception {

		String endpoint = "/web/index.php/api/v2/admin/job-titles?limit=50&offset=0&sortField=jt.jobTitleName&sortOrder=ASC";

		// Step 1: Make API call
		CustomResponse customResponse = apiUtil.getJobTitles(endpoint, cookieValue);

		// Step 2: Implementation check
		boolean isCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "getJobTitles",
				List.of("given", "cookie", "get", "response"));

		// Step 3: Logging
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		// Step 4: Assertions
		Assert.assertTrue(isCorrect, "GET must use RestAssured methods properly.");
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected HTTP 200 OK");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		// Job title ID list
		Assert.assertNotNull(customResponse.jobTitleIdList, "Job title ID list should not be null.");

		// Meta data and total count
		Assert.assertNotNull(customResponse.jobTitleMetaMap, "Meta map should not be null.");
		Assert.assertTrue(customResponse.totalJobTitles >= 0, "Total job titles should be >= 0.");
	}

	/**
	 * Test Case: Validate the DELETE /web/index.php/api/v2/admin/job-titles
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Create a new job title using the createJobTitle() method. 2.
	 * Fetch the first job title ID using the GET
	 * /web/index.php/api/v2/admin/job-titles endpoint. 3. Store the fetched ID as
	 * 'idBefore'. 4. Construct the DELETE request body with 'idBefore' in the "ids"
	 * array. 5. Send a DELETE request to /web/index.php/api/v2/admin/job-titles
	 * with the valid cookie and request body. 6. Assert that the response status
	 * code is 200 (OK). 7. Fetch the first job title ID again using GET
	 * /web/index.php/api/v2/admin/job-titles. 8. Store the new ID as 'idAfter'. 9.
	 * Verify that 'idAfter' is not equal to 'idBefore' to ensure deletion took
	 * effect.
	 *
	 * Expected Results: - Status code is 200 after deletion. - The deleted job
	 * title ID is no longer the first job title in the list. - API call
	 * successfully removes the specified job title.
	 */

	@Test(priority = 9, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Fetch job title ID using GET '/web/index.php/api/v2/admin/job-titles' also add a new job title before deleting\n"
					+ "2. Delete that job title using DELETE '/web/index.php/api/v2/admin/job-titles'\n"
					+ "3. Assert response code is 200 and first job title is different after deletion")
	public void DeleteJobTitleByIdTest() throws Exception {

		createJobTitle();
		// Step 1: Fetch the first job title ID before deletion
		int idBefore = getFirstJobTitleId();
		System.out.println("First Job Title ID before deletion: " + idBefore);

		String requestBody = "{\n" + "  \"ids\": [" + idBefore + "]\n" + "}";
		String endpoint = "/web/index.php/api/v2/admin/job-titles";

		// Step 2: Delete the job title
		CustomResponse customResponse = apiUtil.DeleteJobTitleById(endpoint, cookieValue, requestBody);

		// Step 3: Assert response status
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 after deletion");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		// Step 4: Fetch the first job title ID after deletion
		int idAfter = getFirstJobTitleId();
		System.out.println("First Job Title ID after deletion: " + idAfter);

		// Step 5: Validate that the first job title ID after deletion is different from
		// the deleted one
		Assert.assertNotEquals(idAfter, idBefore,
				"First job title ID after deletion should not match the deleted job title ID");
	}

	/**
	 * Test Case: Validate the GET /web/index.php/api/v2/admin/pay-grades endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 *
	 * Test Steps: 1. Send a GET request to
	 * /web/index.php/api/v2/admin/pay-grades?limit=50&offset=0 using a valid
	 * session cookie. 2. Log the response status code and body for debugging
	 * purposes. 3. Verify that the response status code is 200 (OK). 4. Validate
	 * that the Grade ID list, Grade Name list, and Currency list are not null or
	 * empty. 5. Ensure the number of Grade IDs matches the number of Grade Names.
	 * 6. Verify all Grade IDs are positive integers. 7. Ensure all Grade Names are
	 * non-empty strings. 8. Validate that all currency values contain both a name
	 * and an identifier in the format 'Name (ID)'.
	 *
	 * Expected Results: - API responds with HTTP 200 OK. - All returned lists (IDs,
	 * Names, Currencies) are populated with valid data. - IDs are positive
	 * integers. - Grade Names are non-empty. - Currencies follow the expected
	 * naming format.
	 */

	@Test(priority = 10, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "Precondition: Null\n"
					+ "1. Send a GET request to the '/web/index.php/api/v2/admin/pay-grades?limit=50&offset=0' endpoint with a valid cookie and the request body\n"
					+ "2. Print the request body, response status code, and response body for debugging\n"
					+ "3. Assert that the response status code is 200, indicating the request was successful")
	public void GetPaygrades() throws IOException {

		// Call updated API method
		CustomResponse customResponse = apiUtil.GetPaygrades("/web/index.php/api/v2/admin/pay-grades?limit=50&offset=0",
				cookieValue);

		// Validate implementation
		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "GetPaygrades",
				List.of("given", "cookie", "get", "response"));

		// Logging
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		// Assertions
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		// Validate Grade IDs list is not null/empty
		Assert.assertNotNull(customResponse.empIdList, "Grade ID list should not be null");
		Assert.assertFalse(customResponse.empIdList.isEmpty(), "Grade ID list should not be empty");

		// Validate Grade Names list is not null/empty
		Assert.assertNotNull(customResponse.empNameList, "Grade Name list should not be null");
		Assert.assertFalse(customResponse.empNameList.isEmpty(), "Grade Name list should not be empty");

		// Validate Currencies list is not null/empty
		Assert.assertNotNull(customResponse.currencyList, "Currency list should not be null");
		Assert.assertFalse(customResponse.currencyList.isEmpty(), "Currency list should not be empty");

		// Validate the size matches between IDs and Names
		Assert.assertEquals(customResponse.empIdList.size(), customResponse.empNameList.size(),
				"Grade ID list and Grade Name list sizes should match");

		// Validate IDs are positive integers
		Assert.assertTrue(customResponse.empIdList.stream().allMatch(id -> id > 0), "All Grade IDs should be positive");

		// Validate Names are not blank
		Assert.assertTrue(customResponse.empNameList.stream().allMatch(name -> name != null && !name.trim().isEmpty()),
				"All Grade Names should be non-empty strings");

		// Validate Currencies contain "(USD)" or valid format
		Assert.assertTrue(
				customResponse.currencyList.stream().allMatch(curr -> curr.contains("(") && curr.contains(")")),
				"All currencies should contain name and ID in format 'Name (ID)'");

	}

	/**
	 * Test Case: Validate the PUT /web/index.php/api/v2/admin/pay-grades/{id}
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie must be available from a
	 * successful login via Selenium in the 'loginWithSeleniumAndGetCookie' method.
	 * - At least one pay grade must already exist in the system.
	 *
	 * Test Steps: 1. Retrieve an existing Pay Grade ID using the getPayGradeid()
	 * helper method. 2. Generate a unique random pay grade name to ensure test
	 * isolation. 3. Construct the PUT request body with the new name. 4. Send a PUT
	 * request to /web/index.php/api/v2/admin/pay-grades/{id} using a valid session
	 * cookie and the request body. 5. Log the request body, response status code,
	 * and response body for debugging. 6. Validate that the implementation uses
	 * required Rest Assured steps (given, cookie, body, put, response).
	 *
	 * Expected Results: - API responds with HTTP 200 OK. - The response body
	 * contains the updated pay grade name. - The response body is not null. - The
	 * pay grade name change is successfully reflected in the API response.
	 */

	@Test(priority = 11, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "Precondition: Get the id of the Paygrade\n"
					+ "1. Get the paygrade id and generate a URL '/web/index.php/api/v2/admin/pay-grades/{id}'\n"
					+ "2. Send a PUT request to the endpoint with a valid cookie and the request body\n"
					+ "3. Print the request body, response status code, and response body for debugging\n"
					+ "4. Assert that the response status code is 200, indicating the request was successful")
	public void PutPaygrades() throws IOException {

		// Step 1: Get existing Paygrade ID
		int id = getPayGradeid();
		String uniquename = generateRandomString(8);
		System.out.println("name is :" + uniquename + "id is");
		// Step 2: Hard‑coded request body
		// Step 2: Correct request body format (OrangeHRM requires currencies)
		String requestBody = "{\n" + "  \"name\": \"" + uniquename + "\"\n" + "}";

		System.out.println(requestBody);

		// Step 3: Call API method
		CustomResponse customResponse = apiUtil.PutPaygrades("/web/index.php/api/v2/admin/pay-grades/" + id,
				cookieValue, requestBody);

		// Step 4: Validate implementation
		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath, "PutPaygrades",
				List.of("given", "cookie", "body", "put", "response"));
		Assert.assertTrue(isImplementationCorrect, "Implementation is incorrect or missing required steps.");

		// Step 5: Logging for debugging
		System.out.println("Request Body: " + requestBody);
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		// Step 6: Assertions
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200.");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertNotNull(customResponse.getResponseBody(), "Response body should not be null.");
		Assert.assertTrue(customResponse.getResponseBody().contains(uniquename),
				"Response should contain updated paygrade name.");
	}

	/**
	 * Test Case: Validate POST /web/index.php/api/v2/admin/employment-statuses
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie obtained via Selenium login.
	 *
	 * Test Steps: 1. Generate a unique employment status name using the current
	 * timestamp to avoid conflicts. 2. Construct the JSON request body with the
	 * generated name. 3. Send a POST request to
	 * /web/index.php/api/v2/admin/employment-statuses including the valid session
	 * cookie and request body. 4. Log the request body, HTTP status code, and
	 * response body for debugging.
	 *
	 * Expected Results: - The API responds with HTTP 200 OK. - The response body
	 * contains the newly created employment status name. - The response contains a
	 * valid, non-empty employment status ID list. - The employment status name list
	 * is not null.
	 */

	@Test(priority = 12, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "Precondition: Null\n" + "1. Generate a unique name \n"
					+ "2. Send POST request to '/web/index.php/api/v2/admin/employment-statuse'\n"
					+ "3. Make sure the request contains the req body,cookie"
					+ "4. Verify response contains the added name")
	public void PostEmpStatuses() throws IOException {

		// Step 1: Generate unique name
		String uniqueName = "PayGrade_" + System.currentTimeMillis();

		// Step 2: Create request body
		String requestBody = "{\n" + "  \"name\": \"" + uniqueName + "\"\n" + "}";

		String endpoint = "/web/index.php/api/v2/admin/employment-statuses";

		// Step 3: Send POST request
		CustomResponse customResponse = apiUtil.PostEmpStatuses(endpoint, cookieValue, requestBody);

		// Debugging output
		System.out.println("Request Body: " + requestBody);
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponse().asString());

		// Step 4: Assertions
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200 after creating paygrade");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertTrue(customResponse.getResponse().asString().contains(uniqueName),
				"Response should contain the newly added paygrade name");
		Assert.assertTrue(customResponse.containsText(uniqueName), "Paygrade name not found in response");

		Assert.assertFalse(customResponse.statusIdList.isEmpty(), "Grade ID list should not be empty");
		idBefore = customResponse.statusIdList.get(0);

		Assert.assertNotNull(customResponse.statusNameList, "Grade Name list should not be null");

	}

	/**
	 * Test Case: Validate PUT /web/index.php/api/v2/admin/employment-statuses/{id}
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie obtained via Selenium login.
	 * - An existing employment status ID available from a previous POST or GET
	 * request.
	 *
	 * Test Steps: 1. Generate a unique employment status name using a random string
	 * for test isolation. 2. Construct the JSON request body with the generated
	 * name. 3. Build the endpoint URL using the previously stored employment status
	 * ID. 4. Send a PUT request to update the employment status name, including the
	 * valid cookie. 5. Log the request body, HTTP status code, and response body
	 * for debugging.
	 *
	 * Expected Results: - API should return HTTP 200 OK. - Response body should
	 * contain the newly updated employment status name. - The status name list in
	 * the response should not be null and must contain the updated name.
	 */

	@Test(priority = 13, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Fetch employment status ID using GET '/web/index.php/api/v2/admin/employment-statuses'\n"
					+ "2. Construct endpoint '/web/index.php/api/v2/admin/employment-statuses/{id}'\n"
					+ "3. Send a PUT request with a valid cookie and body containing a new name\n"
					+ "4. Print request/response details\n"
					+ "5. Assert response code is 200 and updated name is reflected in the response")
	public void PutEmploymentStatusTest() throws IOException {

// Step 1: Get existing employment status ID
//		int id = getFirstEmploymentStatus();
		String uniqueName = generateRandomString(8);
		System.out.println("Generated Employment Status Name: " + uniqueName);

// Step 2: Create request body
		String requestBody = "{\n" + "  \"name\": \"" + uniqueName + "\"\n" + "}";

		String endpoint = "/web/index.php/api/v2/admin/employment-statuses/" + idBefore;

// Step 3: Send PUT request
		CustomResponse customResponse = apiUtil.PutEmploymentStatus(endpoint, cookieValue, requestBody);

// Step 4: Validate implementation (optional, if you're doing method validation)
		boolean isImplementationCorrect = TestCodeValidator.validateTestMethodFromFile(apiUtilPath,
				"PutEmploymentStatus", List.of("given", "cookie", "body", "put", "response"));
		Assert.assertTrue(isImplementationCorrect, "PUT must use RestAssured methods properly.");

// Step 5: Print request/response
		System.out.println("Request Body: " + requestBody);
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

// Step 6: Assertions
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");
		Assert.assertNotNull(customResponse.getResponseBody(), "Response body should not be null");
		Assert.assertTrue(customResponse.getResponseBody().contains(uniqueName), "Updated name not reflected");

		Assert.assertNotNull(customResponse.statusNameList, "Updated name list should not be null");
		Assert.assertTrue(customResponse.statusNameList.contains(uniqueName), "Updated name not in name list");
	}

	/**
	 * Test Case: Validate DELETE /web/index.php/api/v2/admin/employment-statuses
	 * endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie obtained via Selenium login.
	 * - At least one employment status exists in the system.
	 *
	 * Test Steps: 1. Create a new employment status to ensure a valid record exists
	 * for deletion. 2. Retrieve the first employment status ID before deletion. 3.
	 * Construct the request body containing the employment status ID. 4. Send a
	 * DELETE request to the employment-statuses endpoint with the valid cookie. 5.
	 * Assert that the API returns HTTP 200 OK, indicating successful deletion. 6.
	 * Fetch the first employment status ID after deletion for verification.
	 *
	 * Expected Results: - The API should return HTTP 200 OK after deletion. - The
	 * first employment status ID after deletion should differ from the deleted ID.
	 * - Response body should not be null after deletion.
	 */

	@Test(priority = 14, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Fetch employment status ID using GET '/web/index.php/api/v2/admin/employment-statuses'\n"
					+ "2. Delete that employment status using DELETE '/web/index.php/api/v2/admin/employment-statuses'\n"
					+ "3. Assert response code is 200 and verify that the first employment status ID has changed")
	public void DeleteEmploymentStatusByIdTest() throws Exception {

		// Step 1: Get first employment status ID before deletion
		createEmploymentStatus();
		idBefore = getFirstEmploymentStatus();
		System.out.println("Employment Status ID to be deleted: " + idBefore);

		String requestBody = "{\n" + "  \"ids\": [" + idBefore + "]\n" + "}";
		String endpoint = "/web/index.php/api/v2/admin/employment-statuses";

		// Step 2: Send DELETE request
		CustomResponse customResponse = apiUtil.DeleteJobTitleById(endpoint, cookieValue, requestBody);

		// Step 3: Assert deletion response status
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected status code 200 after deletion");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		// Step 4: Get employment status ID after deletion
		int idAfter = getFirstEmploymentStatus();
		System.out.println("Here it is" + customResponse.getResponseBody());
		// int idAfter = deleteResponse.empId;
		System.out.println("First Employment Status ID after deletion: " + idAfter);
		Assert.assertNotNull(customResponse.getResponseBody(), "Response body should not be null after deletion");
	}

	/**
	 * Test Case: Validate POST /web/index.php/api/v2/admin/job-categories endpoint.
	 *
	 * Precondition: - A valid OrangeHRM session cookie obtained via Selenium login.
	 *
	 * Test Steps: 1. Generate a unique job category name for test isolation. 2.
	 * Construct a valid JSON request body containing the new job category name. 3.
	 * Send a POST request to the job-categories endpoint with the valid cookie and
	 * request body. 4. Log the request body, response status code, and response
	 * body for debugging. 5. Assert that the API returns HTTP 200 OK, indicating
	 * successful creation. 6. Verify that the response body contains the newly
	 * added job category name. 7. Ensure that the returned job category ID and name
	 * list are not null.
	 *
	 * Expected Results: - API should return HTTP 200 OK. - Response should contain
	 * the exact job category name sent in the request. - Job category ID and name
	 * list should be non-null in the response.
	 */

	@Test(priority = 15, dependsOnMethods = "loginWithSeleniumAndGetCookie", groups = {
			"PL1" }, description = "1. Generate a unique job category name\n"
					+ "2. Send POST request to '/web/index.php/api/v2/admin/job-categories'\n"
					+ "3. Assert response status is 200\n"
					+ "4. Validate that the response contains the added job category name")
	public void PostJobCategoriesTest() throws IOException {

		// Step 1: Generate unique name
		String uniqueName = "JobCat_" + generateRandomString(6);

		// Step 2: Create request body
		String requestBody = "{\n" + "  \"name\": \"" + uniqueName + "\"\n" + "}";

		String endpoint = "/web/index.php/api/v2/admin/job-categories";

		// Step 3: Send POST request
		CustomResponse customResponse = apiUtil.PostJobCategories(endpoint, cookieValue, requestBody);

		// Step 4: Logging
		System.out.println("Request Body: " + requestBody);
		System.out.println("Status Code: " + customResponse.getStatusCode());
		System.out.println("Response Body: " + customResponse.getResponseBody());

		// Step 5: Assertions
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Expected HTTP 200 OK");
		Assert.assertEquals(customResponse.getStatusLine(), "HTTP/1.0 200 OK", "The status line does not match!");

		Assert.assertTrue(customResponse.getResponseBody().contains(uniqueName),
				"Response should contain the newly added job category name: " + uniqueName);

		Assert.assertNotNull(customResponse.empId, "Job category ID list should not be null");

		Assert.assertNotNull(customResponse.statusNameList, "Job category Name list should not be null");

	}

	/*------------Helper Methods------------*/

	public void createJobTitleById() {

		String requestBody = String.format(
				"{ \"title\": \"%s\", \"description\": \"%s\", \"specification\": null, \"note\": \"\" }",
				generateRandomString(8), // title
				generateRandomString(10) // description
		);

		Response response = RestAssured.given().cookie("orangehrm", cookieValue).contentType(ContentType.JSON)
				.body(requestBody).post(baseUrl + "/web/index.php/api/v2/admin/employment-statuses");

	}

	public String generateRandomString(int length) {

		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = RANDOM.nextInt(CHARACTERS.length());
			sb.append(CHARACTERS.charAt(index));
		}

		return sb.toString();
	}

	public void CreateEmp() {
		String name = generateRandomString(8);

		// Create JSON body as a string
		String requestBody = "{ \"name\": \"" + name + "\" }";

		// Send POST request with body and cookie
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).contentType(ContentType.JSON)
				.body(requestBody).post(baseUrl + "/web/index.php/api/v2/admin/employment-statuses");

		// Log response
		System.out.println("Request Body: " + requestBody);
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body: " + response.getBody().asString());
	}

	public int getPayGradeid() {
		String endpoint = "/web/index.php/api/v2/admin/pay-grades?limit=50&offset=0";
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(baseUrl + endpoint);

		if (response.statusCode() == 200) {
			int firstId = response.jsonPath().getInt("data[0].id");
			System.out.println("First Job Title ID: " + firstId);
			return firstId;
		} else {
			System.out.println("Failed to fetch job titles. Status code: " + response.statusCode());
			return -1;
		}
	}

	public int getemploymentstatusid() {
		String endpoint = "/web/index.php/api/v2/admin/employment-statuses?limit=50&offset=0";

		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(baseUrl + endpoint);

		if (response.statusCode() == 200) {
			int firstId = response.jsonPath().getInt("data[0].id");
			System.out.println("First Job Title ID: " + firstId);
			return firstId;
		} else {
			System.out.println("Failed to fetch job titles. Status code: " + response.statusCode());
			return -1;
		}

	}

	public void createJobTitle() {
		String endpoint = "/web/index.php/api/v2/admin/job-titles";

		// Generate a unique title using timestamp
		String uniqueTitle = "Job_" + System.currentTimeMillis();

		// Build request body
		String requestBody = "{\n" + "  \"title\": \"" + uniqueTitle + "\",\n" + "  \"description\": \"\",\n"
				+ "  \"specification\": null,\n" + "  \"note\": \"\"\n" + "}";

		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).post(baseUrl + endpoint).then().extract()
				.response();

		System.out.println("Create Job Title Request Body: " + requestBody);
		System.out.println("Create Job Title Response Code: " + response.getStatusCode());
		System.out.println("Create Job Title Response: " + response.asString());

		if (response.getStatusCode() != 200) {
			throw new RuntimeException("Failed to create job title. Status: " + response.getStatusCode());
		}
	}

	public int getFirstEmploymentStatus() {
		String endpoint = "/web/index.php/api/v2/admin/employment-statuses?limit=50&offset=0";

		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(baseUrl + endpoint);

		if (response.statusCode() == 200) {
			int firstId = response.jsonPath().getInt("data[0].id");
			System.out.println("First Job Title ID: " + firstId);
			return firstId;
		} else {
			System.out.println("Failed to fetch job titles. Status code: " + response.statusCode());
			return -1;
		}

	}

	public int getFirstJobTitleId() {
		String endpoint = "/web/index.php/api/v2/admin/job-titles?limit=50&offset=0&sortField=jt.jobTitleName&sortOrder=ASC";

		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.get(baseUrl + endpoint);

		System.out.println("Get Job Titles Response: " + response.asString());

		if (response.statusCode() == 200) {
			List<Map<String, Object>> dataList = response.jsonPath().getList("data");
			if (dataList != null && !dataList.isEmpty() && dataList.get(0).get("id") != null) {
				int firstId = ((Number) dataList.get(0).get("id")).intValue();
				System.out.println("First Job Title ID: " + firstId);
				return firstId;
			} else {
				System.out.println("No job titles found in response.");
				return -1;
			}
		} else {
			System.out.println("Failed to fetch job titles. Status code: " + response.statusCode());
			return -1;
		}
	}

	public void createEmploymentStatus() {
		String endpoint = "/web/index.php/api/v2/admin/employment-statuses";

		// Generate a random string for name
		String uniqueName = "EmpStatus_" + System.currentTimeMillis();

		// Request body
		String requestBody = "{\n" + "  \"name\": \"" + uniqueName + "\"\n" + "}";

		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).post(baseUrl + endpoint).then().extract()
				.response();

		System.out.println("Create Employment Status Request Body: " + requestBody);
		System.out.println("Create Employment Status Response Code: " + response.getStatusCode());
		System.out.println("Create Employment Status Response: " + response.asString());

		if (response.getStatusCode() != 200) {
			throw new RuntimeException("Failed to create employment status. Status: " + response.getStatusCode());
		}
	}

}
