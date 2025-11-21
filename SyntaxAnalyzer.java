package Project;

// SyntaxAnalyzer.java
import java.util.*;

public class SyntaxAnalyzer {

    public List<String> checkBalanced(String code) {
        List<String> errors = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        int pos = 0;

        for (char c : code.toCharArray()) {
            pos++;
            if (c == '(' || c == '{' || c == '[') stack.push(c);

            if (c == ')' || c == '}' || c == ']') {
                if (stack.isEmpty()) {
                    errors.add("Unmatched closing '" + c + "' at position " + pos);
                    return errors;
                }
                char t = stack.pop();
                if (!matches(t, c)) {
                    errors.add("Mismatched brackets at position " + pos);
                    return errors;
                }
            }
        }

        if (!stack.isEmpty()) errors.add("Unclosed brackets found.");
        return errors;
    }

    private boolean matches(char open, char close) {
        return (open == '(' && close == ')')
            || (open == '{' && close == '}')
            || (open == '[' && close == ']');
    }

    public List<String> checkSemicolons(List<String> lines) {
        List<String> errors = new ArrayList<>();
        int lineNum = 0;

        for (String line : lines) {
            lineNum++;
            String trimmed = line.trim();

            if (trimmed.isEmpty()) continue;
            if (trimmed.endsWith("{") || trimmed.equals("}")) continue;
            if (trimmed.startsWith("if") || trimmed.startsWith("for") || trimmed.startsWith("while") || trimmed.startsWith("else")) continue;
            if (trimmed.startsWith("//")) continue;

            if (!trimmed.endsWith(";")) {
                errors.add("Possible missing semicolon at line " + lineNum + ": " + trimmed);
            }
        }

        return errors;
    }

    public List<String> analyze(String code, List<String> lines) {
        List<String> errors = new ArrayList<>();
        errors.addAll(checkBalanced(code));
        errors.addAll(checkSemicolons(lines));
        return errors;
    }
}