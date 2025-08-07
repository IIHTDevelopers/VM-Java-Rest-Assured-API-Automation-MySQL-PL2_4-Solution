package rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CustomResponse {
	private final Response response;
	public int empId;
	private final int statusCode;
	private final String statusLine;
	private final String responseBody;
	public Boolean leaveAssignLeave;
	public Boolean leavLeaveList;
	public Boolean leaveApplyLeave;
	public Boolean leaveMyLeave;
	public Boolean timeTmployeeTimesheet;
	public Boolean timeMyTimesheet;
	public List<Integer> statusIdList;
	public List<String> statusNameList;
	public int subUnitId;
	public String subUnitName;
	public int subUnitCount;
	public List<Integer> empIdList;
	public List<String> empNameList;
	public List<String> currencyList;
	public List<Map<String, Object>> employeeLocationList;
	public Map<String, Object> employeeMetaMap;
	public List<Map<String, Object>> userList;
	public Map<String, Object> userMeta;
	public Map<String, Object> userMetaMap;
	public List<Integer> userIdList;
	public List<String> userNameList;
	public Set<String> userRoleNameSet;
	public List<String> employeeIdList;
	public int userCount;
	public List<Map<String, Object>> jobTitleList;
	public Map<String, Object> jobTitleMetaMap;
	public int jobTitleCount;
	public List<String> jobTitleNameList;
	public Set<Integer> jobTitleIdSet;
	public Map<String, String> jobTitleDescriptionMap;
	public List<String> jobSpecFilenameList;
	public int totalJobTitles;
	public List<Map<String, Object>> jobTitleDataList;
	public List<Integer> jobTitleIdList;
	public List<Map<String, Object>> jobSpecificationList;

	/**
	 * Constructs a {@link CustomResponse} object using the provided response,
	 * status code, and status line.
	 *
	 * <p>
	 * This constructor allows manual assignment of the HTTP status code and status
	 * line while also extracting and storing the response body as a string for
	 * easier access.
	 *
	 * @param response   the RestAssured {@link Response} object returned from the
	 *                   API call
	 * @param statusCode the HTTP status code to be assigned to this response
	 * @param statusLine the HTTP status line to be assigned to this response
	 */
	public CustomResponse(Response response, int statusCode, String statusLine) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
	}

	/**
	 * Constructs a {@link CustomResponse} object with response metadata and
	 * dashboard permission flags.
	 *
	 * <p>
	 * This constructor initializes the HTTP response details and extracts
	 * permission flags related to leave and timesheet functionalities. The response
	 * body is also stored as a string for further processing or logging.
	 *
	 * @param response              the RestAssured {@link Response} object returned
	 *                              from the API call
	 * @param statusCode            the HTTP status code of the response
	 * @param statusLine            the HTTP status line of the response
	 * @param leaveAssignLeave      flag indicating access to the 'Assign Leave'
	 *                              feature
	 * @param leavLeaveList         flag indicating access to the 'Leave List'
	 *                              feature
	 * @param leaveApplyLeave       flag indicating access to the 'Apply Leave'
	 *                              feature
	 * @param leaveMyLeave          flag indicating access to the 'My Leave' feature
	 * @param timeTmployeeTimesheet flag indicating access to the 'Employee
	 *                              Timesheet' feature
	 * @param timeMyTimesheet       flag indicating access to the 'My Timesheet'
	 *                              feature
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, Boolean leaveAssignLeave,
			Boolean leavLeaveList, Boolean leaveApplyLeave, Boolean leaveMyLeave, Boolean timeTmployeeTimesheet,
			boolean timeMyTimesheet) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.leaveAssignLeave = leaveAssignLeave;
		this.leavLeaveList = leavLeaveList;
		this.leaveApplyLeave = leaveApplyLeave;
		this.leaveMyLeave = leaveMyLeave;
		this.timeTmployeeTimesheet = timeTmployeeTimesheet;
		this.timeMyTimesheet = timeMyTimesheet;
	}

	/**
	 * Constructs a {@link CustomResponse} object with response metadata and
	 * employee subunit details.
	 *
	 * <p>
	 * This constructor initializes the HTTP response properties and stores
	 * information about the employee's subunit, including subunit ID, name, and the
	 * number of employees in that subunit. The full response body is also stored as
	 * a string.
	 *
	 * @param response     the RestAssured {@link Response} object returned from the
	 *                     API call
	 * @param statusCode   the HTTP status code of the response
	 * @param statusLine   the HTTP status line of the response
	 * @param subUnitId    the ID of the employee's subunit
	 * @param subUnitName  the name of the employee's subunit
	 * @param subUnitCount the number of employees in the subunit
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, int subUnitId, String subUnitName,
			int subUnitCount) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.subUnitCount = subUnitCount;
		this.subUnitId = subUnitId;
		this.subUnitName = subUnitName;
	}

	/**
	 * Constructs a {@link CustomResponse} object with response metadata and
	 * employee status details.
	 *
	 * <p>
	 * This constructor initializes the HTTP response properties and stores lists of
	 * employee status IDs and names extracted from the response. The full response
	 * body is also captured as a string.
	 *
	 * @param response   the RestAssured {@link Response} object returned from the
	 *                   API call
	 * @param statusCode the HTTP status code of the response
	 * @param statusLine the HTTP status line of the response
	 * @param idList     a list containing employee status IDs extracted from the
	 *                   response
	 * @param nameList   a list containing employee status names extracted from the
	 *                   response
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, List<Integer> idList,
			List<String> nameList) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.statusIdList = idList;
		this.statusNameList = nameList;
	}

	// Getter for status code
	public int getStatusCode() {
		return statusCode;
	}

	// Getter for status line
	public String getStatusLine() {
		return statusLine;
	}

	// Getter for body as raw string
	public String getResponseBody() {
		return responseBody;
	}

	// Getter for full Response object (if needed)
	public Response getResponse() {
		return response;
	}

	/**
	 * Constructs a CustomResponse for the employee locations API.
	 *
	 * @param response     the HTTP Response object
	 * @param statusCode   the status code returned
	 * @param statusLine   the status line
	 * @param locationList the list of employee locations and counts
	 * @param metaMap      metadata like totalLocationCount and
	 *                     unassignedEmployeeCount
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, List<Map<String, Object>> locationList,
			Map<String, Object> metaMap) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.employeeLocationList = locationList;
		this.employeeMetaMap = metaMap;
	}

	public CustomResponse(Response response, int statusCode, String statusLine, int id) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.empId = id;
	}

	/**
	 * Constructs a CustomResponse for the Admin Users API.
	 *
	 * @param response        Raw HTTP response
	 * @param statusCode      HTTP status code
	 * @param statusLine      HTTP status line
	 * @param userList        Full user objects list from "data"
	 * @param userMetaMap     Metadata map from "meta"
	 * @param userIdList      List of all user IDs
	 * @param userNameList    List of all usernames
	 * @param userRoleNameSet Set of all distinct user role names
	 * @param employeeIdList  List of employee IDs from embedded employee objects
	 * @param userCount       Count of users (can be from meta or list size)
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, List<Map<String, Object>> userList,
			Map<String, Object> userMetaMap, List<Integer> userIdList, List<String> userNameList,
			Set<String> userRoleNameSet, List<String> employeeIdList, int userCount) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();

		this.userList = userList;
		this.userMetaMap = userMetaMap;

		this.userIdList = userIdList;
		this.userNameList = userNameList;
		this.userRoleNameSet = userRoleNameSet;
		this.employeeIdList = employeeIdList;
		this.userCount = userCount;
	}

	/**
	 * Constructs a CustomResponse for the GET Job Titles API.
	 *
	 * @param response the HTTP Response object
	 * @param tag      a tag to indicate which API is calling (for overload clarity)
	 */
	public CustomResponse(Response response, String tag) {
		this.response = response;
		this.statusCode = response.getStatusCode();
		this.statusLine = response.getStatusLine();
		this.responseBody = response.getBody().asString();

		JsonPath jsonPath = response.jsonPath();

		if ("getJobTitles".equals(tag)) {
			this.jobTitleList = jsonPath.getList("data");
			Map<String, Object> meta = jsonPath.getMap("meta");
			this.totalJobTitles = meta != null ? (int) meta.getOrDefault("total", 0) : 0;
		}
	}

	public CustomResponse(Response response, int statusCode, String statusLine, List<Map<String, Object>> jobList,
			Map<String, Object> metaMap, List<Integer> jobIdList, List<Map<String, Object>> jobSpecList, int jobCount) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();

		this.jobTitleDataList = jobList;
		this.jobTitleMetaMap = metaMap;
		this.jobTitleIdList = jobIdList;
		this.jobSpecificationList = jobSpecList;
		this.totalJobTitles = jobCount;
	}

	/**
	 * Constructs a CustomResponse for the GET Grades API.
	 *
	 * <p>
	 * Extracts grade IDs, grade names, and associated currency information.
	 * Currency details are stored as strings in the format "CurrencyName
	 * (CurrencyId)".
	 *
	 * @param response      the RestAssured {@link Response} object returned from
	 *                      the API call
	 * @param statusCode    the HTTP status code of the response
	 * @param statusLine    the HTTP status line of the response
	 * @param gradeIdList   a list of grade IDs extracted from the response
	 * @param gradeNameList a list of grade names extracted from the response
	 * @param currencyList  a list of currency details in "Name (Id)" format
	 */
	public CustomResponse(Response response, int statusCode, String statusLine, List<Integer> gradeIdList,
			List<String> gradeNameList, List<String> currencyList) {
		this.response = response;
		this.statusCode = statusCode;
		this.statusLine = statusLine;
		this.responseBody = response.getBody().asString();
		this.empIdList = gradeIdList; // Reusing empIdList for grade IDs
		this.empNameList = gradeNameList; // Reusing empNameList for grade names
		this.currencyList = currencyList;
	}

	public boolean containsText(String text) {
		return response.asString().contains(text);
	}

}
