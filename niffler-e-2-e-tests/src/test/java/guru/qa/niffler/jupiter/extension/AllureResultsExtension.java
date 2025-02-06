package guru.qa.niffler.jupiter.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class AllureResultsExtension implements SuiteExtension {

    private static final Logger LOG = LoggerFactory.getLogger(AllureResultsExtension.class);

    private static final String ALLURE_DOCKER_API = System.getenv("ALLURE_DOCKER_API");
    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");
    private static final String BUILD_URL = System.getenv("BUILD_URL");
    private static final String HEAD_COMMIT_MESSAGE = System.getenv("HEAD_COMMIT_MESSAGE");
    private static final String EXECUTION_TYPE = System.getenv("EXECUTION_TYPE");

    private static final String ALLURE_RESULTS_PATH = "allure-results";

    @Override
    public void afterSuite() {
        LOG.info("All tests completed. Uploading results to Allure Docker Service...");

        if (ALLURE_DOCKER_API == null || ALLURE_DOCKER_API.isEmpty()) {
            LOG.error("ALLURE_DOCKER_API is not set. Skipping Allure upload.");
            return;
        }

        File resultsDir = new File(ALLURE_RESULTS_PATH);
        if (!resultsDir.exists() || !resultsDir.isDirectory()) {
            LOG.error("Allure results directory not found: {}", ALLURE_RESULTS_PATH);
            return;
        }

        // 1. Отправка результатов тестов
        if (!sendResults()) {
            LOG.error("Failed to send Allure results.");
            return;
        }

        // 2. Генерация отчета
        generateReport();
    }

    private boolean sendResults() {
        try {
            URL url = new URL(ALLURE_DOCKER_API + "/send-results");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format(
                    "{ \"results\": \"%s\", \"executionType\": \"%s\", \"githubToken\": \"%s\", \"buildUrl\": \"%s\", \"commitMessage\": \"%s\" }",
                    encodeResults(), EXECUTION_TYPE, GITHUB_TOKEN, BUILD_URL, HEAD_COMMIT_MESSAGE
            );

            connection.getOutputStream().write(jsonPayload.getBytes());
            connection.getOutputStream().flush();

            int responseCode = connection.getResponseCode();
            LOG.info("Allure send-results response: {}", responseCode);
            return responseCode == 200 || responseCode == 204;
        } catch (Exception e) {
            LOG.error("Error sending results to Allure", e);
            return false;
        }
    }

    private void generateReport() {
        try {
            URL url = new URL(ALLURE_DOCKER_API + "/generate-report");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            LOG.info("Allure generate-report response: {}", responseCode);
        } catch (Exception e) {
            LOG.error("Error generating Allure report", e);
        }
    }

    private String encodeResults() {
        try {
            File resultsDir = new File(ALLURE_RESULTS_PATH);
            StringBuilder encoded = new StringBuilder();
            for (File file : resultsDir.listFiles()) {
                if (file.isFile()) {
                    byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    encoded.append(Base64.getEncoder().encodeToString(bytes)).append("\n");
                }
            }
            return encoded.toString();
        } catch (Exception e) {
            LOG.error("Error encoding Allure results", e);
            return "";
        }
    }
}