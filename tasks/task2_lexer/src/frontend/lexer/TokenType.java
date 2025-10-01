package frontend.lexer;

/**
 * TokenType 枚举类，定义所有单词的类别码
 */
public enum TokenType {
    // 标识符和常量
    IDENFR("IDENFR"),      // 标识符
    INTCON("INTCON"),      // 整型常量
    STRCON("STRCON"),      // 字符串常量
    
    // 关键字
    CONSTTK("CONSTTK"),    // const
    INTTK("INTTK"),        // int
    STATICTK("STATICTK"),  // static
    VOIDTK("VOIDTK"),      // void
    BREAKTK("BREAKTK"),    // break
    CONTINUETK("CONTINUETK"), // continue
    IFTK("IFTK"),          // if
    ELSETK("ELSETK"),      // else
    FORTK("FORTK"),        // for
    RETURNTK("RETURNTK"),  // return
    MAINTK("MAINTK"),      // main
    PRINTFTK("PRINTFTK"),  // printf
    
    // 运算符
    PLUS("PLUS"),          // +
    MINU("MINU"),          // -
    MULT("MULT"),          // *
    DIV("DIV"),            // /
    MOD("MOD"),            // %
    LSS("LSS"),            // <
    LEQ("LEQ"),            // <=
    GRE("GRE"),            // >
    GEQ("GEQ"),            // >=
    EQL("EQL"),            // ==
    NEQ("NEQ"),            // !=
    ASSIGN("ASSIGN"),      // =
    AND("AND"),            // &&
    OR("OR"),              // ||
    NOT("NOT"),            // !
    
    // 界符
    SEMICN("SEMICN"),      // ;
    COMMA("COMMA"),        // ,
    LPARENT("LPARENT"),    // (
    RPARENT("RPARENT"),    // )
    LBRACK("LBRACK"),      // [
    RBRACK("RBRACK"),      // ]
    LBRACE("LBRACE"),      // {
    RBRACE("RBRACE"),      // }
    
    // 错误标记
    ERROR("ERROR");        // 错误
    
    private final String type;    
    TokenType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}