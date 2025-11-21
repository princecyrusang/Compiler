package Project;
// Token.java
public class Token {
    public final String type;
    public final String lexeme;

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return String.format("%-10s : %s", type, lexeme);
    }
}