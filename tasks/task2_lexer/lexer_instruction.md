# 编译器总体框架与词法分析  
**编译技术实验词法分析专题报告**

## 1. 编译器整体架构

### 1.1 编译器结构组成
- **前端 (Front)**:  
  - **词法分析器 (Lexer)**: 将输入字符串分解为单词 (Token)。  
  - **语法分析器 (Parser)**: 基于单词生成语法树。  
- **中端 (Middle)**:  
  - **语义分析器 (Visitor)**: 利用语法树生成中间代码。  
  - **符号表管理 (Symbol Manager)**: 管理符号信息。  
  - **中间代码容器**: 存储与目标机器无关的中间代码。  
- **后端 (Back)**:  
  - **目标翻译器 (Translator)**: 将中间代码翻译为目标代码。  
  - **存储管理 (Activity Record)**: 管理存储信息。  
  - **优化器 (Optimizer)**: 对中间代码或目标代码进行优化（可选）。  
  - **解释执行程序 (Virtual Machine)**: 直接运行中间代码，输出执行结果（可选）。  

### 1.2 编译器工作流程
- **前端**: 输入字符串 → 词法分析 → 语法分析 → 语法树。  
- **中端**: 语法树 → 语义分析 → 中间代码生成（与目标机器无关）。  
- **后端**: 中间代码 → 优化（可选） → 目标代码生成，或直接解释执行中间代码。  

### 1.3 北航编译实验任务
- **作业1: 词法分析**  
  - 实现词法分析器，识别单词。  
- **作业2: 语法分析**  
  - 实现语法分析器，生成语法树。  
- **作业3: 语义分析**  
  - 实现符号表管理和语义分析，处理未定义、重定义等错误。  
- **作业4: 代码生成（三种可选子任务）**  
  - **4.1 MIPS代码生成**: 设计中间代码，翻译为MIPS目标代码（较难）。  
  - **4.2 PCODE代码生成+解释执行**: 生成PCODE中间代码并直接运行（较简单）。  
  - **4.3 LLVM代码生成**: 生成LLVM中间代码（中等难度）。  
- **作业5（选做）: 优化竞速**  
  - 实现代码优化，参与竞速排行榜。

---

## 2. 词法分析

### 2.1 词法分析总体概述
- **功能**:  
  - 划分单词，提取类别、值等信息。  
  - 处理注释（单行和跨行）。  
  - 统计行号。  

### 2.2 词法分析设计
- **类设计**:  
  - **Lexer**: 词法分析器类（单例模式）。  
  - **TokenType**: 单词类型枚举类。  
- **主要接口**:  
  - `next()`: 处理下一个单词。  
  - `getToken()`: 获取当前单词值。  
  - `getTokenType()`: 获取当前单词类型。  
- **主要数据成员**:  
  - `source`: 源程序字符串。  
  - `curPos`: 当前字符串位置指针。  
  - `token`: 解析的单词值。  
  - `tokenType`: 解析的单词类型。  
  - `reserveWords`: 保留字表。  
  - `lineNum`: 当前行号。  
  - `number`: 解析的数值。  

### 2.3 词法分析编码实现
- **主要方法**: `next()`（模拟有限状态自动机）。  
  - **识别标识符/关键字**:  
    ```java
    if (isNonDigit(c)) { // 标识符或保留字
        token += c;
        while (curPos < string.length() && 
               (isNonDigit(string.charAt(curPos)) || Character.isDigit(string.charAt(curPos)))) {
            c = string.charAt(curPos++);
            token += c;
        }
        reserve(); // 查关键字表
        return 0;
    }
    ```
  - **识别无符号整数**:  
    ```java
    else if (Character.isDigit(c)) { // 无符号整数
        token += c;
        while (curPos < string.length() && Character.isDigit(string.charAt(curPos))) {
            c = string.charAt(curPos++);
            token += c;
        }
        lexType = LexType.INTCON; // 设置单词类别
        number = Integer.valueOf(token); // 转化为数值
        return 0;
    }
    ```

### 2.4 词法分析难点
- **注释处理**:  
  - **单行注释**: `// this is a line of note`  
  - **跨行注释**: `/* this is the first line of note ... */`  
  - **挑战**:  
    - 区分注释符号 (`/`、`*`) 与除号、乘号。  
    - 处理单行与跨行注释的混合情况（如 `////////` 或 `/* */` 嵌套）。  
    - 实现复杂有限状态自动机。  

- **单行注释编程**:  
  ```java
  else if (c == '/') { // 第一个 /
      token += c;
      if (curPos < string.length() && string.charAt(curPos) == '/') { // 第二个 /
          c = string.charAt(curPos++);
          token += c;
          while (curPos < string.length() && string.charAt(curPos) != '\n') {
              c = string.charAt(curPos++);
              token += c;
          }
          if (curPos < string.length()) {
              c = string.charAt(curPos++);
              token += c;
              lineNum++; // 单行注释末尾的\n
          }
          lexType = LexType.NOTE;
          return next();
      }
  }
  ```

- **跨行注释编程**:  
  ```java
  else if (curPos < string.length() && string.charAt(curPos) == '*') { // /*
      c = string.charAt(curPos++);
      token += c;
      while (curPos < string.length()) {
          while (curPos < string.length() && string.charAt(curPos) != '*') { // 非*字符
              c = string.charAt(curPos++);
              token += c;
              if (c == '\n') lineNum++;
          }
          while (curPos < string.length() && string.charAt(curPos) == '*') { // *字符
              c = string.charAt(curPos++);
              token += c;
          }
          if (curPos < string.length() && string.charAt(curPos) == '/') { // /字符
              c = string.charAt(curPos++);
              token += c;
              lexType = LexType.NOTE;
              return next();
          }
      }
  }
  ```

- **有限状态自动机 (FSM)**:  
  - States for comment handling:  
    - Start: `/`  
    - Single-line comment: `//` + non-newline characters → newline or end.  
    - Multi-line comment: `/*` → non-`*` characters → `*` → `*/`.  

### 2.5 错误处理
- Focus on **a类错误** (as per assignment requirements).  

### 2.6 词法分析总结
- **编程难度**: Relatively low.  
- **关键作用**: Provides foundational information (e.g., token type, line number) for syntax analysis.  
- **重点**: Implement finite state automaton (FSM) programmatically.  

---

