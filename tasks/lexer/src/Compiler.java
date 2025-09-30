package src;

import src.frontend.Lexer;
import java.io.IOException;

/**
 * 编译器主类，负责调用词法分析器
 */
public class Compiler {
    public static void main(String[] args) {
        try {
            // 获取词法分析器单例实例
            Lexer lexer = Lexer.getInstance();
            // 读取源文件
            lexer.readSource("testfile_error.txt");
            // 执行词法分析
            lexer.analyze();
        } catch (IOException e) {
            System.err.println("文件操作错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("编译错误: " + e.getMessage());
        }
    }
}