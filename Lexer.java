package Project;
import java.util.*;
import java.util.regex.*;

public class Lexer {

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "int", "float", "double", "if", "else", "return", "for", "while"
    ));

    public List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        if (code == null || code.trim().isEmpty()) return tokens;

        Pattern pattern = Pattern.compile(
            "\\s*(?:(\\bint\\b|\\bfloat\\b|\\bdouble\\b|\\bif\\b|\\belse\\b|\\breturn\\b|\\bfor\\b|\\bwhile\\b)"
          + "|([A-Za-z_][A-Za-z0-9_]*)"
          + "|(\\d+)"
          + "|(==|!=|<=|>=|[+\\-*/=;(){}<>]))"
        );

        Matcher m = pattern.matcher(code);

        while (m.find()) {
            String kw = m.group(1);
            String id = m.group(2);
            String num = m.group(3);
            String op = m.group(4);

            if (kw != null) tokens.add(new Token("KEYWORD", kw));
            else if (id != null) {
                if (KEYWORDS.contains(id)) tokens.add(new Token("KEYWORD", id));
                else tokens.add(new Token("IDENTIFIER", id));
            } 
            else if (num != null) tokens.add(new Token("NUMBER", num));
            else if (op != null) tokens.add(new Token("OPERATOR", op));
        }

        return tokens;
    }
}

