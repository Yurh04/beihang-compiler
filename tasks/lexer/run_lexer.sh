#!/bin/bash
# ./run_lexer.sh 
# 自动化运行词法分析器脚本

# 设置颜色输出
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
NC="\033[0m" # No Color

# 项目根目录
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src"
BIN_DIR="$ROOT_DIR/bin"

# 显示帮助信息
show_help() {
    echo -e "${YELLOW}用法: ./run_lexer.sh [选项] [测试文件]${NC}"
    echo -e ""
    echo -e "选项:"
    echo -e "  -h, --help           显示此帮助信息"
    echo -e "  -c, --compile        仅编译代码"
    echo -e "  -r, --run            仅运行程序（需要先编译）"
    echo -e "  -f, --file FILE      指定测试文件"
    echo -e ""
    echo -e "示例:"
    echo -e "  ./run_lexer.sh                # 编译并运行，使用默认测试文件"
    echo -e "  ./run_lexer.sh -f testfile_error.txt  # 使用错误测试文件"
    echo -e "  ./run_lexer.sh -c             # 仅编译代码"
    echo -e "  ./run_lexer.sh -r             # 仅运行程序"
}

# 编译代码
compile() {
    echo -e "${GREEN}正在编译源代码...${NC}"
    
    # 确保bin目录存在
    mkdir -p "$BIN_DIR"
    
    # 编译源代码
    cd "$ROOT_DIR"
    javac -d "$BIN_DIR" "$SRC_DIR/frontend/Lexer.java" "$SRC_DIR/frontend/Token.java" "$SRC_DIR/frontend/TokenType.java" "$SRC_DIR/Compiler.java"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}编译成功！${NC}"
        return 0
    else
        echo -e "${RED}编译失败！${NC}"
        return 1
    fi
}

# 运行程序
run() {
    local test_file="$1"
    
    # 检查编译文件是否存在
    if [ ! -f "$BIN_DIR/src/Compiler.class" ]; then
        echo -e "${RED}编译文件不存在，请先编译代码！${NC}"
        return 1
    fi
    
    echo -e "${GREEN}正在运行词法分析器，测试文件: $test_file${NC}"
    
    # 临时修改Compiler.java中的测试文件路径
    temp_file="$SRC_DIR/Compiler.java.tmp"
    sed 's/lexer.readSource("[^"]*");/lexer.readSource("'"$test_file"'");/' "$SRC_DIR/Compiler.java" > "$temp_file"
    mv "$temp_file" "$SRC_DIR/Compiler.java"
    
    # 重新编译以应用更改
    javac -d "$BIN_DIR" "$SRC_DIR/frontend/Lexer.java" "$SRC_DIR/frontend/Token.java" "$SRC_DIR/frontend/TokenType.java" "$SRC_DIR/Compiler.java"
    
    # 运行程序
    cd "$ROOT_DIR"
    java -cp "$BIN_DIR" src.Compiler
    
    # 检查是否生成了输出文件
    if [ -f "lexer.txt" ]; then
        echo -e "${GREEN}词法分析完成，结果已保存到 lexer.txt${NC}"
        # 显示部分输出内容
        echo -e "${YELLOW}输出内容预览:${NC}"
        head -n 10 lexer.txt
    elif [ -f "error.txt" ]; then
        echo -e "${YELLOW}检测到错误，错误信息已保存到 error.txt${NC}"
        cat error.txt
    fi
    
    return 0
}

# 主函数
main() {
    local compile_only=false
    local run_only=false
    local test_file="testfile.txt" # 默认测试文件
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -h|--help)
                show_help
                return 0
                ;;
            -c|--compile)
                compile_only=true
                shift
                ;;
            -r|--run)
                run_only=true
                shift
                ;;
            -f|--file)
                if [[ -n "$2" && ! "$2" == -* ]]; then
                    test_file="$2"
                    shift 2
                else
                    echo -e "${RED}错误: -f 选项需要指定测试文件名${NC}"
                    show_help
                    return 1
                fi
                ;;
            *)
                # 如果参数不是选项，假设是测试文件名
                if [[ ! "$1" == -* ]]; then
                    test_file="$1"
                else
                    echo -e "${RED}错误: 未知选项 '$1'${NC}"
                    show_help
                    return 1
                fi
                shift
                ;;
        esac
    done
    
    # 检查测试文件是否存在
    if [ "$run_only" == true ] || [ "$compile_only" == false ]; then
        if [ ! -f "$ROOT_DIR/$test_file" ]; then
            echo -e "${RED}错误: 测试文件 '$test_file' 不存在${NC}"
            return 1
        fi
    fi
    
    # 执行操作
    if [ "$compile_only" == true ]; then
        compile
    elif [ "$run_only" == true ]; then
        run "$test_file"
    else
        # 默认模式：先编译后运行
        compile && run "$test_file"
    fi
    
    return $?
}

# 执行主函数
main "$@"