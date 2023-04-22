import java.util.List;

public class JavaCodeEditor {
    // Define an enum for edit action types
    public enum ActionType {
        INSERT, DELETE, MODIFY
    }

    // Define a class to represent edit actions
    public static class EditAction {
        private ActionType actionType;
        private int lineNumber;
        private String content;

        public EditAction(ActionType actionType, int lineNumber, String content) {
            this.actionType = actionType;
            this.lineNumber = lineNumber;
            this.content = content;
        }

        // Getters and setters
        // ...
    }

    public String applySuggestedEdits(String originalCode, List<EditAction> editActions) {
        // Split the original code into lines
        String[] codeLines = originalCode.split("\n");

        // Iterate through the edit actions and apply each one to the code
        for (EditAction editAction : editActions) {
            int lineNumber = editAction.lineNumber;
            switch (editAction.actionType) {
                case INSERT:
                    // Insert a new line
                    codeLines[lineNumber - 1] += "\n" + editAction.content;
                    break;
                case DELETE:
                    // Delete the specified line
                    codeLines[lineNumber - 1] = "";
                    break;
                case MODIFY:
                    // Modify the specified line
                    codeLines[lineNumber - 1] = editAction.content;
                    break;
            }
        }

        // Join the updated code lines into a single code block
        String updatedCode = String.join("\n", codeLines);

        // Return the updated code
        return updatedCode;
    }
}
