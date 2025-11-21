package Project;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

public class CompilerSimulator extends JFrame {

    private JTextArea codeArea, resultArea;
    private JButton openBtn, lexicalBtn, syntaxBtn, semanticBtn, clearBtn;
    private Lexer lexer = new Lexer();
    private SyntaxAnalyzer syntax = new SyntaxAnalyzer();
    private SemanticAnalyzer semantic = new SemanticAnalyzer();
    private List<String> lines = new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompilerSimulator().setVisible(true));
    }

    public CompilerSimulator() {
        setTitle("Compiler");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        resetState();
    }

    private void initUI() {
    
    
    
    JPanel leftPanel = new JPanel(new GridLayout(6, 1, 8, 8));
    openBtn = new JButton("Open File");
    lexicalBtn = new JButton("Lexical Analysis");
    syntaxBtn = new JButton("Syntax Analysis");
    semanticBtn = new JButton("Semantic Analysis");
    clearBtn = new JButton("Clear");



    leftPanel.add(openBtn);
    leftPanel.add(lexicalBtn);
    leftPanel.add(syntaxBtn);
    leftPanel.add(semanticBtn);
    leftPanel.add(clearBtn);

    // Code area (top of split) - non-editable if you only load files
    codeArea = new JTextArea();
    codeArea.setEditable(false);
    codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    JScrollPane codeScroll = new JScrollPane(codeArea);
    codeScroll.setBorder(BorderFactory.createTitledBorder("Code"));

    // Result area (large box) - non-editable scrollable text area
    resultArea = new JTextArea();
    resultArea.setEditable(false);
    resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    resultArea.setLineWrap(false);
    JScrollPane resultScroll = new JScrollPane(resultArea);
    // Add an outer titled border to match the "Result:" label above the box
    resultScroll.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    // Build a top panel that shows the "Result:" label and a bit of spacing above the big box
    JPanel resultTop = new JPanel(new BorderLayout(4,4));
    JLabel resultLabel = new JLabel("Result:");
    resultLabel.setBorder(BorderFactory.createEmptyBorder(2,4,2,4));
    resultTop.add(resultLabel, BorderLayout.NORTH);

    // Combine label + big result area in a single panel
    JPanel resultPanel = new JPanel(new BorderLayout(4,4));
    resultPanel.add(resultTop, BorderLayout.NORTH);
    resultPanel.add(resultScroll, BorderLayout.CENTER);

    // Use a JSplitPane to put the code viewer above and the result panel below
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, resultPanel);
    split.setResizeWeight(0.5);            // adjust ratio (top / bottom). Change to 0.4 or 0.6 if needed.
    split.setDividerLocation(260);         // initial pixel position; tweak to taste
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    // Right panel holds the split pane
    JPanel rightPanel = new JPanel(new BorderLayout(8,8));
    rightPanel.add(split, BorderLayout.CENTER);

    // Add to the frame
    getContentPane().setLayout(new BorderLayout(8,8));
    getContentPane().add(leftPanel, BorderLayout.WEST);
    getContentPane().add(rightPanel, BorderLayout.CENTER);

    // Wire actions (unchanged)
    openBtn.addActionListener(e -> openFile());
    lexicalBtn.addActionListener(e -> doLexical());
    syntaxBtn.addActionListener(e -> doSyntax());
    semanticBtn.addActionListener(e -> doSemantic());
    clearBtn.addActionListener(e -> clearAll());
}
    private void resetState() {
        openBtn.setEnabled(true);
        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Code Files", "txt", "java"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String code = new String(Files.readAllBytes(file.toPath()));
                codeArea.setText(code);
                lines = List.of(code.split("\\R"));
                resultArea.setText("File loaded: " + file.getName() + "\n");
                lexicalBtn.setEnabled(true);
            } catch (Exception ex) {
                resultArea.setText("Error reading file.");
            }
        }
    }

    private void doLexical() {
        tokens = lexer.tokenize(codeArea.getText());
        if (tokens.isEmpty()) {
            resultArea.setText("No tokens found.\n");
            return;
        }

        StringBuilder sb = new StringBuilder("Tokens:\n");
        for (Token t : tokens) sb.append(t).append("\n");
        resultArea.setText(sb.toString());

        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(true);
    }

    private void doSyntax() {
        List<String> errors = syntax.analyze(codeArea.getText(), lines);

        if (errors.isEmpty()) {
            resultArea.setText("Syntax: OK\n");
            syntaxBtn.setEnabled(false);
            semanticBtn.setEnabled(true);
        } else {
            resultArea.setText("Syntax Errors:\n");
            for (String err : errors) resultArea.append(err + "\n");
        }
    }

    private void doSemantic() {
        List<String> errors = semantic.analyze(lines);

        if (errors.isEmpty()) {
            resultArea.setText("Semantic: OK\nDeclared Variables: " + semantic.getSymbolTable());
            semanticBtn.setEnabled(false);
        } else {
            resultArea.setText("Semantic Errors:\n");
            for (String err : errors) resultArea.append(err + "\n");
        }
    }

    private void clearAll() {
        codeArea.setText("");
        resultArea.setText("");
        tokens = new ArrayList<>();         
        lines = new ArrayList<>();          
        semantic = new SemanticAnalyzer();
        resetState();
    }
}
