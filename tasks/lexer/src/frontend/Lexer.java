package src.frontend;

import java.io.*;
import java.util.*;

/**
 * Lexer 类，实现词法分析器的核心功能
 */
public class Lexer {
    private StringBuilder sourceCode;  // 源代码内容
    private int curPos;                // 当前位置
    private int lineNum;               // 当前行号
    private List<Token> tokens;        // 存储所有token
    private List<String> errors;       // 存储所有错误
    private static Lexer instance;     // 单例实例
    
    // 保留字表
    private Map<String, TokenType> reserveWords;
    
    /**
     * 私有构造函数（单例模式）
     */
    private Lexer() {
        sourceCode = new StringBuilder();
        curPos = 0;
        lineNum = 1;
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        
        // 初始化保留字表
        reserveWords = new HashMap<>();
        reserveWords.put("const", TokenType.CONSTTK);
        reserveWords.put("int", TokenType.INTTK);
        reserveWords.put("static", TokenType.STATICTK);
        reserveWords.put("void", TokenType.VOIDTK);
        reserveWords.put("break", TokenType.BREAKTK);
        reserveWords.put("continue", TokenType.CONTINUETK);
        reserveWords.put("if", TokenType.IFTK);
        reserveWords.put("else", TokenType.ELSETK);
        reserveWords.put("for", TokenType.FORTK);
        reserveWords.put("return", TokenType.RETURNTK);
        reserveWords.put("main", TokenType.MAINTK);
        reserveWords.put("printf", TokenType.PRINTFTK);
        reserveWords.put("getint", TokenType.IDENFR);  // getint是函数名，不是关键字
    }
    
    /**
     * 获取单例实例
     */
    public static Lexer getInstance() {
        if (instance == null) {
            instance = new Lexer();
        }
        return instance;
    }
    
    /**
     * 读取源代码
     */
    public void readSource(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            sourceCode.append(line).append("\n");
        }
        reader.close();
    }
    
    /**
     * 执行词法分析
     */
    public void analyze() {
        while (curPos < sourceCode.length()) {
            char c = sourceCode.charAt(curPos);
            
            // 跳过空白字符
            if (Character.isWhitespace(c)) {
                if (c == '\n') {
                    lineNum++;
                }
                curPos++;
                continue;
            }
            
            // 识别标识符或关键字
            if (Character.isLetter(c) || c == '_') {
                analyzeIdentifierOrKeyword();
            }
            // 识别数字常量
            else if (Character.isDigit(c)) {
                analyzeNumber();
            }
            // 识别字符串常量
            else if (c == '"') {
                analyzeString();
            }
            // 识别运算符和界符
            else {
                analyzeOperatorOrDelimiter();
            }
        }
        
        // 输出结果
        writeOutput();
    }
    
    /**
     * 分析标识符或关键字
     */
    private void analyzeIdentifierOrKeyword() {
        StringBuilder identifier = new StringBuilder();
        char c = sourceCode.charAt(curPos);
        
        // 收集标识符字符
        while (curPos < sourceCode.length() && (Character.isLetterOrDigit(c) || c == '_')) {
            identifier.append(c);
            curPos++;
            if (curPos < sourceCode.length()) {
                c = sourceCode.charAt(curPos);
            }
        }
        
        String idStr = identifier.toString();
        // 检查是否是关键字
        if (reserveWords.containsKey(idStr)) {
            tokens.add(new Token(reserveWords.get(idStr), idStr, lineNum));
        } else {
            // 否则是标识符
            tokens.add(new Token(TokenType.IDENFR, idStr, lineNum));
        }
    }
    
    /**
     * 分析数字常量
     */
    private void analyzeNumber() {
        StringBuilder number = new StringBuilder();
        char c = sourceCode.charAt(curPos);
        
        // 收集数字字符
        while (curPos < sourceCode.length() && Character.isDigit(c)) {
            number.append(c);
            curPos++;
            if (curPos < sourceCode.length()) {
                c = sourceCode.charAt(curPos);
            }
        }
        
        tokens.add(new Token(TokenType.INTCON, number.toString(), lineNum));
    }
    
    /**
     * 分析字符串常量
     */
    private void analyzeString() {
        StringBuilder strBuilder = new StringBuilder();
        curPos++; // 跳过开头的双引号
        char c = sourceCode.charAt(curPos);
        
        while (curPos < sourceCode.length() && c != '"') {
            if (c == '\n') {
                // 字符串跨行，错误
                errors.add(lineNum + " a");
                return;
            }
            strBuilder.append(c);
            curPos++;
            if (curPos < sourceCode.length()) {
                c = sourceCode.charAt(curPos);
            }
        }
        
        if (curPos < sourceCode.length() && c == '"') {
            curPos++; // 跳过结尾的双引号
            tokens.add(new Token(TokenType.STRCON, "\"" + strBuilder.toString() + "\"", lineNum));
        } else {
            // 字符串没有闭合，错误
            errors.add(lineNum + " a");
        }
    }
    
    /**
     * 分析运算符和界符
     */
    private void analyzeOperatorOrDelimiter() {
        char c = sourceCode.charAt(curPos);
        
        switch (c) {
            case '+':
                tokens.add(new Token(TokenType.PLUS, "+", lineNum));
                curPos++;
                break;
            case '-':
                tokens.add(new Token(TokenType.MINU, "-", lineNum));
                curPos++;
                break;
            case '*':
                tokens.add(new Token(TokenType.MULT, "*", lineNum));
                curPos++;
                break;
            case '/':
                // 检查是否是注释
                if (curPos + 1 < sourceCode.length()) {
                    char next = sourceCode.charAt(curPos + 1);
                    if (next == '/') {
                        // 单行注释
                        processSingleLineComment();
                    } else if (next == '*') {
                        // 多行注释
                        processMultiLineComment();
                    } else {
                        tokens.add(new Token(TokenType.DIV, "/", lineNum));
                        curPos++;
                    }
                } else {
                    tokens.add(new Token(TokenType.DIV, "/", lineNum));
                    curPos++;
                }
                break;
            case '%':
                tokens.add(new Token(TokenType.MOD, "%", lineNum));
                curPos++;
                break;
            case '<':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '=') {
                    tokens.add(new Token(TokenType.LEQ, "<=", lineNum));
                    curPos += 2;
                } else {
                    tokens.add(new Token(TokenType.LSS, "<", lineNum));
                    curPos++;
                }
                break;
            case '>':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '=') {
                    tokens.add(new Token(TokenType.GEQ, ">=", lineNum));
                    curPos += 2;
                } else {
                    tokens.add(new Token(TokenType.GRE, ">", lineNum));
                    curPos++;
                }
                break;
            case '=':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '=') {
                    tokens.add(new Token(TokenType.EQL, "==", lineNum));
                    curPos += 2;
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "=", lineNum));
                    curPos++;
                }
                break;
            case '!':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '=') {
                    tokens.add(new Token(TokenType.NEQ, "!=", lineNum));
                    curPos += 2;
                } else {
                    tokens.add(new Token(TokenType.NOT, "!", lineNum));
                    curPos++;
                }
                break;
            case '&':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '&') {
                    tokens.add(new Token(TokenType.AND, "&&", lineNum));
                    curPos += 2;
                } else {
                    // 错误：单个&符号
                    errors.add(lineNum + " a");
                    curPos++;
                }
                break;
            case '|':
                if (curPos + 1 < sourceCode.length() && sourceCode.charAt(curPos + 1) == '|') {
                    tokens.add(new Token(TokenType.OR, "||", lineNum));
                    curPos += 2;
                } else {
                    // 错误：单个|符号
                    errors.add(lineNum + " a");
                    curPos++;
                }
                break;
            case ';':
                tokens.add(new Token(TokenType.SEMICN, ";", lineNum));
                curPos++;
                break;
            case ',':
                tokens.add(new Token(TokenType.COMMA, ",", lineNum));
                curPos++;
                break;
            case '(': 
                tokens.add(new Token(TokenType.LPARENT, "(", lineNum));
                curPos++;
                break;
            case ')':
                tokens.add(new Token(TokenType.RPARENT, ")", lineNum));
                curPos++;
                break;
            case '[':
                tokens.add(new Token(TokenType.LBRACK, "[", lineNum));
                curPos++;
                break;
            case ']':
                tokens.add(new Token(TokenType.RBRACK, "]", lineNum));
                curPos++;
                break;
            case '{':
                tokens.add(new Token(TokenType.LBRACE, "{", lineNum));
                curPos++;
                break;
            case '}':
                tokens.add(new Token(TokenType.RBRACE, "}", lineNum));
                curPos++;
                break;
            default:
                // 未识别的字符，但继续处理
                curPos++;
                break;
        }
    }
    
    /**
     * 处理单行注释
     */
    private void processSingleLineComment() {
        curPos += 2; // 跳过 //
        while (curPos < sourceCode.length() && sourceCode.charAt(curPos) != '\n') {
            curPos++;
        }
        if (curPos < sourceCode.length()) {
            lineNum++;
            curPos++;
        }
    }
    
    /**
     * 处理多行注释
     */
    private void processMultiLineComment() {
        curPos += 2; // 跳过 /*
        boolean commentEnded = false;
        
        while (curPos < sourceCode.length() - 1) {
            if (sourceCode.charAt(curPos) == '\n') {
                lineNum++;
            }
            if (sourceCode.charAt(curPos) == '*' && sourceCode.charAt(curPos + 1) == '/') {
                commentEnded = true;
                curPos += 2;
                break;
            }
            curPos++;
        }
        
        if (!commentEnded) {
            // 多行注释未闭合，错误
            errors.add(lineNum + " a");
        }
    }
    
    /**
     * 输出结果
     */
    private void writeOutput() {
        try {
            // 输出token到lexer.txt
            if (errors.isEmpty()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter("lexer.txt"))) {
                    for (Token token : tokens) {
                        writer.println(token);
                    }
                }
            } else {
                try (PrintWriter writer = new PrintWriter(new FileWriter("error.txt"))) {
                    // 按行号排序错误
                    Collections.sort(errors);
                    // 输出第一个错误（题目要求每个错误样例只有一个错误）
                    writer.println(errors.get(0));
                }
            }
        } catch (IOException e) {
            System.err.println("写入输出文件时出错: " + e.getMessage());
        }
    }
}