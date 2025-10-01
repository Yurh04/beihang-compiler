package frontend.lexer;

/**
 * Token 类，表示词法分析器识别的单词
 */
public class Token {
    private TokenType type;    // 单词类型
    private String value;      // 单词值
    private int lineNum;       // 行号
    private String errorCode;  // 错误码（如果是错误）
    
    /**
     * 构造函数
     * @param type 单词类型
     * @param value 单词值
     * @param lineNum 行号
     */
    public Token(TokenType type, String value, int lineNum) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }
    
    /**
     * 构造函数（错误token）
     * @param type 单词类型
     * @param value 单词值
     * @param lineNum 行号
     * @param errorCode 错误码
     */
    public Token(TokenType type, String value, int lineNum, String errorCode) {
        this(type, value, lineNum);
        this.errorCode = errorCode;
    }
    
    // getter 方法
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLineNum() {
        return lineNum;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return type + " " + value;
    }
}