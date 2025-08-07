package rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

public class ApiUtil {
	private static final Set<Integer> usedNumbers = new HashSet<>();
	private static final Random random = new Random();
	private static String BASE_URL;
	Properties prop;

	/**
	 * Retrieves the base URL from the configuration properties file.
	 *
	 * <p>
	 * This method loads the properties from the file located at
	 * <code>{user.dir}/src/main/resources/config.properties</code> and extracts the
	 * value associated with the key <code>base.url</code>. The value is stored in
	 * the static variable <code>BASE_URL</code> and returned.
	 *
	 * @return the base URL string if successfully read from the properties file;
	 *         {@code null} if an I/O error occurs while reading the file.
	 */
	public String getBaseUrl() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			BASE_URL = prop.getProperty("base.url");
			return BASE_URL;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the username from the configuration properties file.
	 *
	 * <p>
	 * This method reads the properties from the file located at
	 * <code>{user.dir}/src/main/resources/config.properties</code> and returns the
	 * value associated with the key <code>username</code>.
	 *
	 * @return the username as a {@code String} if found in the properties file;
	 *         {@code null} if an I/O error occurs while reading the file.
	 */
	public String getUsername() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			return prop.getProperty("username");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the password value from the configuration properties file.
	 *
	 * <p>
	 * This method reads the <code>config.properties</code> file located in the
	 * <code>src/main/resources</code> directory relative to the project root. It
	 * loads the properties from the file and returns the value associated with the
	 * <code>password</code> key.
	 *
	 * <p>
	 * If an <code>IOException</code> occurs while accessing or reading the file,
	 * the exception stack trace is printed and the method returns
	 * <code>null</code>.
	 *
	 * @return the password value as a <code>String</code> from the properties file,
	 *         or <code>null</code> if an error occurs during file access
	 */
	public String getPassword() {
		prop = new Properties();
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties")) {
			prop.load(fis);
			return prop.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sends a GET request to the specified login endpoint with a cookie and
	 * optional request body.
	 *
	 * <p>
	 * This method constructs and sends a GET request using RestAssured. It includes
	 * a cookie named <code>orangehrm</code> with the provided value and sets the
	 * <code>Content-Type</code> header to <code>application/json</code>. If a
	 * request body map is provided, it is included in the request before the
	 * response is captured.
	 *
	 * @param endpoint    the API endpoint to send the GET request to (relative to
	 *                    the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         and status line
	 */
	public CustomResponse GetLogin(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");

		Response response = request.get(BASE_URL + endpoint); // ✅ Send the request
		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		if (body != null) {
			request.body(body);
		}
		return new CustomResponse(response, statusCode, statusLine); // ✅ Fixed instantiation
	}

	/**
	 * Sends a GET request to the specified employee action summary endpoint with a
	 * cookie and optional request body.
	 *
	 * <p>
	 * This method constructs and sends a GET request using RestAssured. It sets the
	 * <code>Content-Type</code> header to <code>application/json</code> and
	 * includes a cookie named <code>orangehrm</code> with the given value. If a
	 * request body map is provided, it is added to the request before execution.
	 *
	 * @param endpoint    the API endpoint to retrieve employee action summary data
	 *                    (relative to the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         and status line
	 */
	public CustomResponse GetEmpActionSummary(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");
		Response response = request.get(BASE_URL + endpoint);
		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		if (body != null) {
			request.body(body);
		}

		return new CustomResponse(response, statusCode, statusLine);
	}

	/**
	 * Sends a GET request to the specified dashboard shortcut endpoint with a
	 * cookie and optional request body, and extracts specific permission flags from
	 * the JSON response.
	 *
	 * <p>
	 * This method uses RestAssured to send a GET request to the given endpoint,
	 * setting the <code>Content-Type</code> header to <code>application/json</code>
	 * and including the <code>orangehrm</code> cookie. It parses the response JSON
	 * to extract permission-related boolean values such as leave and time
	 * management shortcuts.
	 *
	 * @param endpoint    the API endpoint to retrieve dashboard shortcut
	 *                    permissions (relative to the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, and extracted permission flags from the response body
	 */
	public CustomResponse GetDashboardShortcut(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");
		Response response = request.get(BASE_URL + endpoint);
		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		Boolean leaveAssignLeave = (Boolean) data.get("leave.assign_leave");
		Boolean leavLeaveList = (Boolean) data.get("leave.leave_list");
		Boolean leaveApplyLeave = (Boolean) data.get("leave.apply_leave");
		Boolean leaveMyLeave = (Boolean) data.get("leave.my_leave");
		Boolean timeEmployeeTimesheet = (Boolean) data.get("time.employee_timesheet");
		Boolean timeMyTimesheet = (Boolean) data.get("time.my_timesheet");

		return new CustomResponse(response, statusCode, statusLine, leaveAssignLeave, leavLeaveList, leaveApplyLeave,
				leaveMyLeave, timeEmployeeTimesheet, timeMyTimesheet);
	}

	/**
	 * Sends a GET request to the specified employee leave information endpoint with
	 * a cookie and optional request body.
	 *
	 * <p>
	 * This method uses RestAssured to construct and send a GET request to the given
	 * endpoint. It sets the <code>Content-Type</code> header to
	 * <code>application/json</code> and includes a cookie named
	 * <code>orangehrm</code> with the provided value. If a request body map is
	 * provided, it is attached to the request before sending.
	 *
	 * @param endpoint    the API endpoint to retrieve employee leave information
	 *                    (relative to the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         and status line
	 */
	public CustomResponse GetEmpLeaveInfo(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");
		Response response = request.get(BASE_URL + endpoint);
		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		if (body != null) {
			request.body(body);
		}
		return new CustomResponse(response, statusCode, statusLine);
	}

	/**
	 * Sends a GET request to the specified employee subunit endpoint with a cookie
	 * and optional request body, and extracts subunit details from the response.
	 *
	 * <p>
	 * This method uses RestAssured to send a GET request to the provided endpoint.
	 * It sets the <code>Content-Type</code> header to <code>application/json</code>
	 * and includes a cookie named <code>orangehrm</code> with the given value.
	 * After receiving the response, it parses the JSON to extract the first
	 * subunit's ID, name, and associated employee count.
	 *
	 * @param endpoint    the API endpoint to retrieve employee subunit information
	 *                    (relative to the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        a map containing key-value pairs to be sent as the JSON
	 *                    request body (can be null)
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, subunit ID, subunit name, and subunit employee count
	 */
	public CustomResponse GetEmpSubunit(String endpoint, String cookieValue, Map<String, String> body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue).header("Content-Type",
				"application/json");
		Response response = request.get(BASE_URL + endpoint);
		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> dataList = jsonPath.getList("data");
		Map<String, Object> firstItem = dataList.get(0);
		Map<String, Object> subunit = (Map<String, Object>) firstItem.get("subunit");

		int subUnitId = (int) subunit.get("id");
		String subUnitName = (String) subunit.get("name");
		int subUnitCount = (int) firstItem.get("count");

		if (body != null) {
			request.body(body);
		}
		return new CustomResponse(response, statusCode, statusLine, subUnitId, subUnitName, subUnitCount);
	}

	/**
	 * Sends a PUT request to update employee name information and extracts response
	 * data including ID, name, and associated currency details.
	 *
	 * <p>
	 * This method uses RestAssured to construct and send a PUT request to the
	 * specified endpoint. It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named
	 * <code>orangehrm</code>, and sends the provided request body as JSON. After
	 * receiving the response, it extracts the employee ID, name, and a list of
	 * associated currency details from the response body.
	 *
	 * @param endpoint    the API endpoint to send the PUT request to (relative to
	 *                    the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        the request body containing employee name update data
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, lists of employee IDs, names, and currency details
	 */
	public CustomResponse PutEmpName(String endpoint, String cookieValue, Object body) {
		RequestSpecification request = RestAssured.given().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(body);
		Response response = request.put(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		// Store ID and name in lists (each with a single value)
		List<Integer> idList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();

		idList.add((Integer) data.get("id"));
		nameList.add((String) data.get("name"));

		// Extract currencies
		List<Map<String, Object>> currencies = (List<Map<String, Object>>) data.get("currencies");
		List<String> currencyDetails = new ArrayList<>();

		for (Map<String, Object> currency : currencies) {
			String currencyName = (String) currency.get("name");
			String currencyId = (String) currency.get("id");
			currencyDetails.add("Currency Name: " + currencyName + ", Currency ID: " + currencyId);
		}
		return new CustomResponse(response, statusCode, statusLine, idList, nameList, currencyDetails);
	}

	/**
	 * Sends a POST request to create a new employee status and extracts the
	 * resulting ID and name from the response.
	 *
	 * <p>
	 * This method uses RestAssured to send a POST request to the specified
	 * endpoint. It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named
	 * <code>orangehrm</code>, and sends the given request body. After receiving the
	 * response, it parses the JSON to extract the employee status ID and name,
	 * storing them in separate lists.
	 *
	 * @param endpoint    the API endpoint to send the POST request to (relative to
	 *                    the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        the JSON-formatted request body containing the employee
	 *                    status details
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, and lists with the created employee status ID and name
	 */
	public CustomResponse PostEmpStatus(String endpoint, String cookieValue, String body) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(body).post(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		// Store ID and name in lists (each with a single value)
		List<Integer> idList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();

		idList.add((Integer) data.get("id"));
		nameList.add((String) data.get("name"));

		return new CustomResponse(response, statusCode, statusLine, idList, nameList);
	}

	/**
	 * Sends a PUT request to update an existing employee status and extracts the
	 * updated ID and name from the response.
	 *
	 * <p>
	 * This method uses RestAssured to send a PUT request to the specified endpoint.
	 * It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named
	 * <code>orangehrm</code>, and sends the provided request body. After receiving
	 * the response, it extracts the updated employee status ID and name from the
	 * response data and stores them in lists. The method also logs the request body
	 * and raw response to the console for debugging purposes.
	 *
	 * @param endpoint    the API endpoint to send the PUT request to (relative to
	 *                    the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param requestBody the request body containing employee status update details
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, and lists with the updated employee status ID and name
	 */
	public CustomResponse PutEmpStatus(String endpoint, String cookieValue, Object requestBody) {
		System.out.println("Requestbody in apiutil: " + requestBody);

		Response response = RestAssured.given().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).put(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		System.out.println("Raw Response:\n" + response.getBody().asString());

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		// Store ID and name in lists (each with a single value)
		List<Integer> idList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();

		idList.add((Integer) data.get("id"));
		nameList.add((String) data.get("name"));

		return new CustomResponse(response, statusCode, statusLine, idList, nameList);
	}

	/**
	 * Sends a POST request to create a new employee name entry and extracts the
	 * resulting ID and name from the response.
	 *
	 * <p>
	 * This method uses RestAssured to send a POST request to the specified
	 * endpoint. It sets the <code>Content-Type</code> header to
	 * <code>application/json</code>, includes a cookie named
	 * <code>orangehrm</code>, and sends the given request body. After receiving the
	 * response, it parses the JSON to extract the newly created employee ID and
	 * name, storing them in separate lists.
	 *
	 * @param endpoint    the API endpoint to send the POST request to (relative to
	 *                    the base URL)
	 * @param cookieValue the value of the <code>orangehrm</code> cookie to include
	 *                    in the request
	 * @param body        the JSON-formatted request body containing the employee
	 *                    name details
	 * @return a {@link CustomResponse} object containing the response, status code,
	 *         status line, and lists with the created employee ID and name
	 */
	public CustomResponse PostEmpName(String endpoint, String cookieValue, String body) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(body).post(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		Map<String, Object> data = jsonPath.getMap("data");

		// Store ID and name in lists (each with a single value)
		List<Integer> idList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();

		idList.add((Integer) data.get("id"));
		nameList.add((String) data.get("name"));

		return new CustomResponse(response, statusCode, statusLine, idList, nameList);
	}

	/**
	 * Generates a unique name by appending a random 4-digit number to the provided
	 * base string.
	 *
	 * <p>
	 * This method generates a random number between 1000 and 9999 and ensures its
	 * uniqueness by checking against the <code>usedNumbers</code> set. Once a
	 * unique number is identified, it is added to the set to avoid future
	 * duplicates.
	 *
	 * @param base the base string to which the unique 4-digit number will be
	 *             appended
	 * @return a <code>String</code> combining the base value and the generated
	 *         unique number
	 */
	public static String generateUniqueName(String base) {
		int uniqueNumber;
		do {
			uniqueNumber = 1000 + random.nextInt(9000);
		} while (usedNumbers.contains(uniqueNumber));

		usedNumbers.add(uniqueNumber);
		return base + uniqueNumber;
	}

	/*
	 * Sends a GET request to retrieve Employee Locations in OrangeHRM and parses
	 * the response.
	 * 
	 * This method performs the following steps: 1. Sends a GET request to the
	 * specified API endpoint using RestAssured. 2. Includes the authentication
	 * cookie for session authorization. 3. Parses the JSON response to retrieve: -
	 * "data": A list of employee location objects. - "meta": Additional metadata
	 * related to the employee locations. 4. Wraps the raw API response, HTTP status
	 * details, list of locations, and metadata into a CustomResponse object for use
	 * in validations and reporting.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * retrieving employee locations. - cookieValue: The authentication cookie value
	 * for the OrangeHRM session.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - List of employee location objects. - Metadata
	 * map from the API response.
	 * 
	 * Notes: - This method assumes "data" is always a list of location objects. -
	 * If "data" or "meta" are null, the returned CustomResponse will contain null
	 * values for them. - Additional parsing logic can be added if test cases
	 * require extracting specific fields from the locations.
	 */

	public CustomResponse getEmployeeLocations(String endpoint, String cookieValue) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();

		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> locationList = jsonPath.getList("data");
		Map<String, Object> metaMap = jsonPath.getMap("meta");

		return new CustomResponse(response, statusCode, statusLine, locationList, metaMap);
	}

	/*
	 * Sends a GET request to retrieve Admin Users in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Sends a GET request to the
	 * specified API endpoint using RestAssured. 2. Includes the authentication
	 * cookie for session authorization. 3. Parses the JSON response to retrieve: -
	 * "data": A list of admin user objects. - "meta": Additional metadata related
	 * to the users. 4. Iterates through each admin user object in "data" to
	 * extract: - userIdList: List of user IDs. - userNameList: List of usernames. -
	 * userRoleNameSet: Set of unique user role names. - employeeIdList: List of
	 * associated employee IDs. 5. Counts the total number of admin users. 6. Wraps
	 * the raw API response, HTTP status details, extracted data, and counts into a
	 * CustomResponse object.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * retrieving admin users. - cookieValue: The authentication cookie value for
	 * the OrangeHRM session.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - Full list of admin user objects. - Metadata
	 * map from the API response. - List of user IDs. - List of usernames. - Set of
	 * unique role names. - List of employee IDs. - Total admin user count.
	 * 
	 * Notes: - This method assumes "data" is always a list of user objects. -
	 * Null-safety checks are included for "userRole" and "employee" fields. - User
	 * roles are stored in a Set to ensure uniqueness.
	 */

	public CustomResponse getAdminUsers(String endpoint, String cookieValue) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		JsonPath jsonPath = response.jsonPath();

		List<Map<String, Object>> userList = jsonPath.getList("data");
		Map<String, Object> metaMap = jsonPath.getMap("meta");

		// Extract extra fields
		List<Integer> userIdList = new ArrayList<>();
		List<String> userNameList = new ArrayList<>();
		Set<String> userRoleNameSet = new HashSet<>();
		List<String> employeeIdList = new ArrayList<>();

		for (Map<String, Object> user : userList) {
			userIdList.add((Integer) user.get("id"));
			userNameList.add((String) user.get("userName"));

			Map<String, Object> userRole = (Map<String, Object>) user.get("userRole");
			if (userRole != null) {
				userRoleNameSet.add((String) userRole.get("name"));
			}

			Map<String, Object> employee = (Map<String, Object>) user.get("employee");
			if (employee != null) {
				employeeIdList.add((String) employee.get("employeeId"));
			}
		}

		int userCount = userList.size();

		return new CustomResponse(response, statusCode, statusLine, userList, metaMap, userIdList, userNameList,
				userRoleNameSet, employeeIdList, userCount);
	}

	/*
	 * Sends a GET request to retrieve Job Titles in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Sends a GET request to the
	 * specified API endpoint using RestAssured. 2. Passes the authentication cookie
	 * for session authorization. 3. Parses the JSON response to retrieve: - "data":
	 * A list of job title objects. - "meta": Additional metadata related to the job
	 * titles. 4. Iterates through each job title object in "data" to extract: -
	 * jobIdList: List of job title IDs. - jobTitleList: List of job title names. -
	 * jobSpecList: List of associated job specification details (maps). 5. Counts
	 * the total number of job titles. 6. Wraps the raw API response, HTTP status
	 * details, extracted data, and counts in a CustomResponse object.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * retrieving job titles. - cookieValue: The authentication cookie value for the
	 * OrangeHRM session.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - Full list of job title objects. - Metadata map
	 * from the API response. - List of job title IDs. - List of job specifications.
	 * - Total job title count.
	 * 
	 * Notes: - This method assumes "data" is always a list of job title objects. -
	 * If "data" is null or empty, the method may throw a NullPointerException
	 * unless null-safety checks are added. - The job specification details are
	 * stored as raw maps for flexibility in test validations.
	 */

	public CustomResponse getJobTitles(String endpoint, String cookieValue) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		JsonPath jsonPath = response.jsonPath();

		List<Map<String, Object>> jobList = jsonPath.getList("data");
		Map<String, Object> metaMap = jsonPath.getMap("meta");

		// Extract fields
		List<Integer> jobIdList = new ArrayList<>();
		List<String> jobTitleList = new ArrayList<>();
		List<Map<String, Object>> jobSpecList = new ArrayList<>();

		for (Map<String, Object> job : jobList) {
			jobIdList.add((Integer) job.get("id"));
			jobTitleList.add((String) job.get("title"));

			Map<String, Object> jobSpec = (Map<String, Object>) job.get("jobSpecification");
			jobSpecList.add(jobSpec);
		}

		int jobCount = jobList.size();

		return new CustomResponse(response, statusCode, statusLine, jobList, metaMap, jobIdList, jobSpecList, jobCount);
	}

	public CustomResponse deleteJobTitleById(String endpoint, String cookieValue) {
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).when().delete(BASE_URL + endpoint);

		int statusCode = response.getStatusCode();
		String statusLine = response.getStatusLine();
		String responseBody = response.getBody().asString();

		return new CustomResponse(response, statusCode, statusLine
		// null, null, // jobList and metaMap are not applicable
		// null, null, // jobIdList and jobSpecList are not applicable
		// 0 // jobCount is not applicable
		);
	}

	/*
	 * Sends a GET request to retrieve Pay Grades in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a GET request
	 * to the given API endpoint using RestAssured. 2. Includes the authentication
	 * cookie in the request for authorization. 3. Parses the JSON response and
	 * retrieves the "data" list containing pay grade objects. 4. Iterates through
	 * each pay grade object to extract: - gradeIdList: IDs of the pay grades. -
	 * gradeNameList: Names of the pay grades. - currencyList: Formatted currency
	 * strings in the form "CurrencyName (CurrencyID)". 5. Handles cases where the
	 * "currencies" field is present by looping through and formatting details. 6.
	 * Wraps the raw API response, HTTP status code/line, and the extracted lists
	 * into a CustomResponse object.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * retrieving pay grades. - cookieValue: The authentication cookie value for the
	 * OrangeHRM session.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - List of pay grade IDs. - List of pay grade
	 * names. - List of currency strings in the format "Name (ID)".
	 * 
	 * Notes: - This method assumes "data" is always a list of objects. If the API
	 * returns an empty list or null, additional null-safety checks should be added.
	 * - The method collects all currencies across all pay grades in a single list.
	 */

	public CustomResponse GetPaygrades(String endpoint, String cookieValue) {
		// Send API request
		Response response = RestAssured.given().cookie("orangehrm", cookieValue).get(BASE_URL + endpoint);

		// Parse JSON response
		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> dataList = jsonPath.getList("data");

		// Prepare lists for constructor
		List<Integer> gradeIdList = new ArrayList<>();
		List<String> gradeNameList = new ArrayList<>();
		List<String> currencyList = new ArrayList<>();

		// Extract data
		for (Map<String, Object> grade : dataList) {
			gradeIdList.add(((Number) grade.get("id")).intValue());
			gradeNameList.add((String) grade.get("name"));

			List<Map<String, String>> currencies = (List<Map<String, String>>) grade.get("currencies");
			if (currencies != null) {
				for (Map<String, String> currency : currencies) {
					String currencyDetail = currency.get("name") + " (" + currency.get("id") + ")";
					currencyList.add(currencyDetail);
				}
			}
		}

		// Return populated CustomResponse
		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), gradeIdList,
				gradeNameList, currencyList);
	}

	/*
	 * Sends a PUT request to update Pay Grades in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a PUT request
	 * to the given API endpoint using RestAssured. 2. Includes the authentication
	 * cookie and the JSON request body containing updated pay grade details. 3.
	 * Retrieves the "data" field from the JSON response, which may be returned as:
	 * - A single object (Map) representing one updated pay grade. - A list of
	 * objects (List<Map>) representing multiple updated pay grades. 4. Calls
	 * extractGradeData() for each object to populate: - gradeIdList: IDs of the
	 * updated pay grades. - gradeNameList: Names of the updated pay grades. -
	 * currencyList: Associated currencies for each pay grade. 5. Wraps the raw API
	 * response, HTTP status code/line, and the extracted lists into a
	 * CustomResponse object.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * updating pay grades. - cookieValue: The authentication cookie value for the
	 * OrangeHRM session. - requestBody: The JSON request body containing updated
	 * pay grade information.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - List of updated pay grade IDs. - List of
	 * updated pay grade names. - List of associated currencies.
	 * 
	 * Notes: - The method dynamically handles both single-object and list responses
	 * for "data". - If the API response format changes, the parsing logic should be
	 * updated accordingly.
	 */

	public CustomResponse PutPaygrades(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().put(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		// Prepare lists for constructor
		List<Integer> gradeIdList = new ArrayList<>();
		List<String> gradeNameList = new ArrayList<>();
		List<String> currencyList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");

		if (dataObj instanceof List) {
			// Handle if API returns a list
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;
			for (Map<String, Object> grade : dataList) {
				extractGradeData(grade, gradeIdList, gradeNameList, currencyList);
			}
		} else if (dataObj instanceof Map) {
			// Handle if API returns a single object
			Map<String, Object> grade = (Map<String, Object>) dataObj;
			extractGradeData(grade, gradeIdList, gradeNameList, currencyList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), gradeIdList,
				gradeNameList, currencyList);
	}

	/*
	 * Sends a DELETE request to remove a Job Title in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a DELETE
	 * request to the given API endpoint using RestAssured. 2. Includes
	 * authentication cookie and JSON request body containing the job title ID(s) to
	 * delete. 3. Extracts the "data" field from the JSON response as a list of
	 * integer IDs representing deleted job titles. 4. Leaves the name list empty,
	 * since DELETE responses typically do not return names. 5. Wraps the raw
	 * response, status info, and extracted IDs in a CustomResponse object.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * deleting job titles. - cookieValue: The authentication cookie value for the
	 * OrangeHRM session. - requestBody: The JSON request body containing one or
	 * more job title IDs to delete.
	 * 
	 * Returns: A CustomResponse object containing the raw API response, status
	 * info, and list of deleted job title IDs.
	 * 
	 * Note: This implementation assumes the API returns "data" as a JSON array of
	 * IDs (e.g., { "data": [19] }). If the response format changes, parsing logic
	 * should be updated accordingly.
	 */

	public CustomResponse DeleteJobTitleById(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().delete(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		// Extract [id] from: { "data": [19], ... }
		List<Integer> empStatusIdList = jsonPath.getList("data", Integer.class);

		// For DELETE, name list is usually not present — leave empty
		List<String> empStatusNameList = new ArrayList<>();

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), empStatusIdList,
				empStatusNameList);
	}

	/*
	 * Sends a POST request to create a new Employment Status in OrangeHRM and
	 * parses the response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a POST request
	 * to the given API endpoint using RestAssured. 2. Includes the authentication
	 * cookie and the JSON request body containing new employment status details. 3.
	 * Extracts the "data" field from the JSON response. - This implementation
	 * assumes "data" is returned as a single JSON object (Map). - The object should
	 * contain at least "id" and "name" fields for the created employment status. 4.
	 * Adds the extracted employment status ID and name to separate lists. 5. Wraps
	 * the raw API response, HTTP status code/line, and parsed lists into a
	 * CustomResponse object for validation in tests.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * creating employment statuses. - cookieValue: The authentication cookie value
	 * for the OrangeHRM session. - requestBody: The JSON request body containing
	 * employment status information.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - A list containing the created employment
	 * status ID. - A list containing the created employment status name.
	 * 
	 * Notes: - This method currently only handles the case where "data" is a single
	 * object. - If the API returns "data" as a list or is empty, this
	 * implementation should be updated to handle that case safely.
	 */

	public CustomResponse PostEmpStatuses(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().post(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		List<Integer> empStatusIdList = new ArrayList<>();
		List<String> empStatusNameList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");
		if (dataObj instanceof Map) {
			Map<String, Object> status = (Map<String, Object>) dataObj;
			empStatusIdList.add(((Number) status.get("id")).intValue());
			empStatusNameList.add((String) status.get("name"));
//        extractEmploymentStatusData(status, empStatusIdList, empStatusNameList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), empStatusIdList,
				empStatusNameList);
	}

	/*
	 * Sends a PUT request to update an Employment Status in OrangeHRM and parses
	 * the response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a PUT request
	 * to the given API endpoint using RestAssured. 2. Includes the authentication
	 * cookie and the JSON request body containing updated employment status
	 * details. 3. Retrieves the "data" field from the JSON response, which may be
	 * returned as: - A single object (Map) representing one updated employment
	 * status. - A list of objects (List<Map>) representing multiple updated
	 * employment statuses. 4. Calls extractEmploymentStatusData() to populate lists
	 * of employment status IDs and names. 5. Wraps the raw API response, status
	 * info, and parsed lists in a CustomResponse object for further validation in
	 * tests.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * updating employment statuses. - cookieValue: The authentication cookie value
	 * for the OrangeHRM session. - requestBody: The JSON request body containing
	 * updated employment status information.
	 * 
	 * Returns: A CustomResponse object containing: - The raw API response. - HTTP
	 * status code and status line. - A list of updated employment status IDs. - A
	 * list of updated employment status names.
	 * 
	 * Notes: - The method dynamically handles both single-object and list responses
	 * for "data". - If the API response format changes, parsing logic may require
	 * updates.
	 */

	public CustomResponse PutEmploymentStatus(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().put(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		// Prepare lists for constructor
		List<Integer> empStatusIdList = new ArrayList<>();
		List<String> empStatusNameList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");

		if (dataObj instanceof List) {
			// API returned a list
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;
			for (Map<String, Object> status : dataList) {
				extractEmploymentStatusData(status, empStatusIdList, empStatusNameList);
			}
		} else if (dataObj instanceof Map) {
			// API returned a single object
			Map<String, Object> status = (Map<String, Object>) dataObj;
			extractEmploymentStatusData(status, empStatusIdList, empStatusNameList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), empStatusIdList,
				empStatusNameList);
	}

	/*
	 * Sends a POST request to create a new Job Category in OrangeHRM and parses the
	 * response.
	 * 
	 * This method performs the following steps: 1. Builds and sends a POST request
	 * to the given API endpoint using RestAssured. 2. Includes authentication
	 * cookie and JSON request body in the request. 3. Parses the JSON response to
	 * extract Job Category IDs and names. 4. Currently supports parsing when the
	 * "data" field in the response is a JSON object. 5. Wraps the raw response and
	 * extracted lists in a CustomResponse object for test validations.
	 * 
	 * Parameters: - endpoint: The API endpoint path (relative to the base URL) for
	 * job categories. - cookieValue: The authentication cookie value for the
	 * OrangeHRM session. - requestBody: The JSON request body containing job
	 * category details.
	 * 
	 * Returns: A CustomResponse object containing the raw API response, status
	 * info, and parsed job category IDs and names.
	 * 
	 * Note: This implementation only handles the case where the "data" field in the
	 * JSON response is a single object (Map). If the API returns an array for
	 * "data", additional parsing logic should be added to handle it safely.
	 */

	public CustomResponse PostJobCategories(String endpoint, String cookieValue, String requestBody) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue)
				.header("Content-Type", "application/json").body(requestBody).when().post(BASE_URL + endpoint).then()
				.extract().response();

		JsonPath jsonPath = response.jsonPath();

		List<Integer> jobCategoryIdList = new ArrayList<>();
		List<String> jobCategoryNameList = new ArrayList<>();

		Object dataObj = jsonPath.get("data");
		if (dataObj instanceof Map) {
			Map<String, Object> jobCat = (Map<String, Object>) dataObj;
			extractJobCategoryData(jobCat, jobCategoryIdList, jobCategoryNameList);
		}

		return new CustomResponse(response, response.getStatusCode(), response.getStatusLine(), jobCategoryIdList,
				jobCategoryNameList);
	}

	private List<Integer> getAllJobTitleIds(String cookieValue) {
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie("orangehrm", cookieValue).when()
				.get(BASE_URL + "/web/index.php/api/v2/admin/job-titles").then().extract().response();

		JsonPath jsonPath = response.jsonPath();
		return jsonPath.getList("data.id");
	}

// Helper method to extract grade data
	private void extractGradeData(Map<String, Object> grade, List<Integer> gradeIdList, List<String> gradeNameList,
			List<String> currencyList) {
		if (grade.get("id") != null) {
			gradeIdList.add(((Number) grade.get("id")).intValue());
		}
		if (grade.get("name") != null) {
			gradeNameList.add((String) grade.get("name"));
		}

		List<Map<String, String>> currencies = (List<Map<String, String>>) grade.get("currencies");
		if (currencies != null) {
			for (Map<String, String> currency : currencies) {
				String currencyDetail = currency.get("name") + " (" + currency.get("id") + ")";
				currencyList.add(currencyDetail);
			}
		}
	}
// Helper method to extract EmploymentStatusData

	private void extractEmploymentStatusData(Map<String, Object> status, List<Integer> empStatusIdList,
			List<String> empStatusNameList) {
		if (status.get("id") != null) {
			empStatusIdList.add(((Number) status.get("id")).intValue());
		}
		if (status.get("name") != null) {
			empStatusNameList.add((String) status.get("name"));
		}
	}

//Helper method to extract JobCategoryData
	private void extractJobCategoryData(Map<String, Object> category, List<Integer> jobCategoryIdList,
			List<String> jobCategoryNameList) {
		if (category.get("id") != null) {
			jobCategoryIdList.add(((Number) category.get("id")).intValue());
		}
		if (category.get("name") != null) {
			jobCategoryNameList.add((String) category.get("name"));
		}
	}

}