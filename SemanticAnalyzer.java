package Project;

// SemanticAnalyzer.java
import java.util.*;
import java.util.regex.*;

public class SemanticAnalyzer {

    private Set<String> symbolTable = new LinkedHashSet<>();

    public List<String> analyze(List<String> lines) {
        List<String> errors = new ArrayList<>();
        symbolTable.clear();

        int lineNum = 0;

        for (String raw : lines) {
            lineNum++;
            String line = raw.trim();

            if (line.isEmpty() || line.startsWith("//")) continue;

            Matcher decl = Pattern.compile("^int\\s+([A-Za-z_][A-Za-z0-9_]*)").matcher(line);
            if (decl.find()) {
                String var = decl.group(1);
                if (symbolTable.contains(var)) {
                    errors.add("Duplicate variable '" + var + "' at line " + lineNum);
                } else {
                    symbolTable.add(var);
                }
                continue;
            }

            Matcher assign = Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\s*=").matcher(line);
            if (assign.find()) {
                String var = assign.group(1);
                if (!symbolTable.contains(var)) {
                    errors.add("Use of undeclared variable '" + var + "' at line " + lineNum);
                }
            }
        }

        return errors;
    }

    public Set<String> getSymbolTable() {
        return symbolTable;
    }
}