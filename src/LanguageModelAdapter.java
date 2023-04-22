import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class LanguageModelAdapter {
    private static final String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/engines/davinci-codex/completions";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your own OpenAI API key

    public String getLanguageModelSuggestion(ErrorInfo errorInfo) {
        // Format the input for the language model
        String prompt = createPromptFromErrorInfo(errorInfo);

        // Interact with the language model API
        String response = callLanguageModelAPI(prompt);

        // Parse the response to extract suggested edits
        String suggestedEdits = extractSuggestedEdits(response);

        // Return the suggested edits
        return suggestedEdits;
    }

    private String createPromptFromErrorInfo(ErrorInfo errorInfo) {
        // Generate a prompt based on the error information
        String prompt = String.format("Error Type: %s\nLine Number: %d\nDescription: %s\nFix the code:",
                errorInfo.getErrorType(), errorInfo.getLineNumber(), errorInfo.getDescription());
        return prompt;
    }

    private String callLanguageModelAPI(String prompt) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String responseString = "";

        // Create request JSON body
        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("prompt", prompt);
        requestBodyJson.addProperty("max_tokens", 50);

        // Convert JSON object to string
        String requestBodyString = gson.toJson(requestBodyJson);

        // Create request body
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBodyString
        );

        // Create request
        Request request = new Request.Builder()
                .url(OPENAI_API_ENDPOINT)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        // Send request and capture response
        try (Response response = client.newCall(request).execute()) {
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseString;
    }

    private String extractSuggestedEdits(String response) {
        // Parse the language model's response to extract the suggested edits
        Gson gson = new Gson();
        JsonObject responseJson = gson.fromJson(response, JsonObject.class);
        String suggestedEdits = responseJson.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        return suggestedEdits.trim();
    }
}
