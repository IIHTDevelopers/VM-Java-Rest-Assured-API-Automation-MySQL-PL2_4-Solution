package testcases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import rest.CustomResponse;
import io.restassured.path.json.JsonPath; // Make sure to use the correct JsonPath import

public class TestCodeValidator {

	// Method to validate if specific keywords are used in the method's source code
	public static boolean validateTestMethodFromFile(String filePath, String methodName, List<String> keywords)
			throws IOException {

		String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

		// FIX: More general method signature match
		String methodRegex = "(public\\s+\\w+\\s+" + methodName + "\\s*\\(.*?\\)\\s*\\{)([\\s\\S]*?)}";
		Pattern methodPattern = Pattern.compile(methodRegex);
		Matcher methodMatcher = methodPattern.matcher(fileContent);

		if (methodMatcher.find()) {
			String methodBody = methodMatcher.group(2); // body inside the method
			System.out.println("Extracted method body:\n" + methodBody);

			boolean allKeywordsPresent = true;

			for (String keyword : keywords) {
				Pattern keywordPattern = Pattern.compile("\\b" + keyword + "\\b");
				if (!keywordPattern.matcher(methodBody).find()) {
					System.out.println("'" + keyword + "' is missing in the method.");
					allKeywordsPresent = false;
				}
			}

			return allKeywordsPresent;
		} else {
			System.out.println("Method " + methodName + " not found in the file.");
			return false;
		}
	}

	// This method takes the method name as an argument and returns its body as a
	// String.
	public static String fetchBody(String filePath, String methodName) {
		StringBuilder methodBody = new StringBuilder();
		boolean methodFound = false;
		boolean inMethodBody = false;
		int openBracesCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Check if the method is found by matching method signature
				if (line.contains("public Response " + methodName + "(")
						|| line.contains("public String " + methodName + "(")) {
					methodFound = true;
				}

				// Once the method is found, start capturing lines
				if (methodFound) {
					if (line.contains("{")) {
						inMethodBody = true;
						openBracesCount++;
					}

					// Capture the method body
					if (inMethodBody) {
						methodBody.append(line).append("\n");
					}

					// Check for closing braces to identify the end of the method
					if (line.contains("}")) {
						openBracesCount--;
						if (openBracesCount == 0) {
							break; // End of method body
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return methodBody.toString();
	}

    public static boolean GetLogin(CustomResponse customResponse) {
        try {
            String responseBody = customResponse.getResponseBody();
            int statusCode = customResponse.getStatusCode();

            // ✅ Assert status code is 200
            if (statusCode != 200) return false;

            // ✅ Basic structure check for HTML page
            if (!responseBody.contains("<html>")) return false;
            if (!responseBody.contains("<head>")) return false;
            if (!responseBody.contains("<body>")) return false;

            // ✅ Check for presence of title and branding
            if (!responseBody.contains("<title>OrangeHRM</title>")) return false;
            if (!responseBody.contains("OrangeHRM")) return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean GetEmpActionSummary(CustomResponse customResponse) {
        try {
            String responseBody = customResponse.getResponseBody();
            int statusCode = customResponse.getStatusCode();

            // ✅ Status code should be 200
            if (statusCode != 200) return false;

            JsonPath jsonPath = new JsonPath(responseBody);

            // ✅ Validate 'data' array exists and is not empty
            List<Map<String, Object>> data = jsonPath.getList("data");
            if (data == null || data.isEmpty()) return false;

            // ✅ Validate each item has non-null, non-empty 'id' and 'group'
            for (Map<String, Object> item : data) {
                if (item.get("id") == null) return false;
                if (item.get("group") == null || item.get("group").toString().trim().isEmpty()) return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	public static boolean GetDashboardShortcut(CustomResponse customResponse) {
		try {
			String responseBody = customResponse.getResponseBody();
			int statusCode = customResponse.getStatusCode();

			// ✅ Validate status code
			if (statusCode != 200)
				return false;

			JsonPath jsonPath = new JsonPath(responseBody);

			// ✅ Get 'data' map and validate it's not null or empty
			Map<String, Object> data = jsonPath.getMap("data");
			if (data == null || data.isEmpty())
				return false;

			// ✅ Check each value is a Boolean
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if (!(entry.getValue() instanceof Boolean)) {
					System.out.println("Key '" + entry.getKey() + "' does not have a boolean value.");
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean GetEmpLeaveInfo(CustomResponse customResponse) {
		try {
			String responseBody = customResponse.getResponseBody();
			int statusCode = customResponse.getStatusCode();

			// ✅ Status code check
			if (statusCode != 200)
				return false;

			JsonPath jsonPath = new JsonPath(responseBody);
			List<Map<String, Object>> dataList = jsonPath.getList("data");

			if (dataList == null || dataList.isEmpty())
				return false;

			for (Map<String, Object> item : dataList) {
				// ✅ Top-level fields
				if (item.get("id") == null)
					return false;
				if (item.get("date") == null)
					return false;
				if (item.get("lengthHours") == null)
					return false;
				if (item.get("duration") == null)
					return false;

				// ✅ Nested employee object
				Map<String, Object> employee = (Map<String, Object>) item.get("employee");
				if (employee == null)
					return false;
				if (employee.get("empNumber") == null)
					return false;
				if (employee.get("firstName") == null)
					return false;
				if (employee.get("lastName") == null)
					return false;
				if (employee.get("middleName") == null)
					return false;
				if (employee.get("employeeId") == null)
					return false;
				// `terminationId` can be null — ignore

				// ✅ Nested leaveType object
				Map<String, Object> leaveType = (Map<String, Object>) item.get("leaveType");
				if (leaveType == null)
					return false;
				if (leaveType.get("id") == null)
					return false;
				if (leaveType.get("name") == null)
					return false;
				if (leaveType.get("deleted") == null)
					return false;

				// ✅ Allow `startTime` and `endTime` to be null
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean GetEmpSubunit(CustomResponse customResponse) {
		try {
			String responseBody = customResponse.getResponseBody();
			int statusCode = customResponse.getStatusCode();

			// ✅ Check status code
			if (statusCode != 200)
				return false;

			JsonPath jsonPath = new JsonPath(responseBody);
			List<Map<String, Object>> dataList = jsonPath.getList("data");

			if (dataList == null || dataList.isEmpty())
				return false;

			for (Map<String, Object> item : dataList) {
				// ✅ Validate top-level 'count'
				if (item.get("count") == null)
					return false;

				// ✅ Validate nested 'subunit' object
				Map<String, Object> subunit = (Map<String, Object>) item.get("subunit");
				if (subunit == null)
					return false;
				if (subunit.get("id") == null)
					return false;
				if (subunit.get("name") == null || subunit.get("name").toString().trim().isEmpty())
					return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean PutEmpName(CustomResponse customResponse) {
		try {
			String responseBody = customResponse.getResponseBody();
			int statusCode = customResponse.getStatusCode();

			// ✅ Validate status code
			if (statusCode != 200)
				return false;

			JsonPath jsonPath = new JsonPath(responseBody);

			Map<String, Object> data = jsonPath.getMap("data");
			if (data == null)
				return false;

			Object id = data.get("id");
			Object name = data.get("name");

			if (id == null)
				return false;
			if (name == null || name.toString().trim().isEmpty())
				return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean PostEmpName(CustomResponse customResponse) {
		try {
			String responseBody = customResponse.getResponseBody();
			int statusCode = customResponse.getStatusCode();

			// ✅ Check status
			if (statusCode != 200)
				return false;

			JsonPath jsonPath = new JsonPath(responseBody);
			Map<String, Object> data = jsonPath.getMap("data");

			if (data == null)
				return false;

			Object id = data.get("id");
			Object name = data.get("name");

			if (id == null)
				return false;
			if (name == null || name.toString().trim().isEmpty())
				return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
