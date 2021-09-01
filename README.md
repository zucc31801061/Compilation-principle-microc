# 2020-2021学年第2学期

## 实验报告

![](D:\计算机科学\study2021上\编程语言原理与编译\大作业\img\zucc.png)

-   课程名称: <u>编程语言原理与编译</u>
-   实验项目: <u>期末大作业</u>
-   专业班级: <u>计算机1801</u>
-   学生学号: <u>31801061</u>
-   学生姓名: <u>王灵霜</u>
-   实验指导教师:<u>郭鸣</u>

-   [Github](https://github.com/zucc31801061/microc-c)

## 项目提交说明

- 权重表：

  | 姓名   | 学号     | 班级       | 任务                   | 权重 |
  | ------ | -------- | ---------- | ---------------------- | ---- |
  | 王灵霜 | 31801061 | 计算机1801 | 设计、编码、测试、文档 | 1.0  |

- 代码提交日志

  ![1](.\img\1.png)

  ![2](.\img\2.png)

## 功能介绍

在MicroC的基础上进行编译器功能的部分扩展。

| 功能                    | 示例                                              | 对应文件       | 自评等级 |
| ----------------------- | ------------------------------------------------- | -------------- | -------- |
| 变量赋值声明            | int i = 0;                                        | test_init.c    | 4        |
| 前置与后置自增、自减    | ++i; --i; i++; i--;                               | test_spsm.c    | 4        |
| for循环                 | for(i=0; i<3; i++){   }                           | test_for.c     | 3        |
| 三目运算符              | a?b:c                                             | test_prim3.c   | 4        |
| do-while                | do{   }while(i<3);                                | test_dowhile.c | 3        |
| while                   | while(i<3){  }                                    | test_while.c   | 3        |
| for i in range(0,3)     | for i in range(0,3)}{  }                          | test_forin.c   | 4        |
| Switch-case(-default)   | switch(){case 1:...; case 2:....; default:....; } | test_switch.c  | 2        |
| += -= *= /= %=          | i += 2;                                           | test_assop.c   | 4        |
| 与 或 非 异或 & ｜ ~  ^ | a ^ b;  a \| b;  a & b;   ~a;                     | test_logic.c   | 4        |
| 移位运算  >>  <<        | a << 2; a >> 2;                                   | test_shift.c   | 4        |
| Max Min                 | max(e1, e2); min(e1, e2);                         | test_maxmin.c  | 3        |


## 执行命令

- `dotnet "C:\Users\lenovo\.nuget\packages\fslexyacc\10.2.0\build\fslex\netcoreapp3.1\fslex.dll"  -o "CLex.fs" --module CLex --unicode CLex.fsl`  
  生成`CLex.fs`词法分析器

- `dotnet "C:\Users\lenovo\.nuget\packages\fslexyacc\10.2.0\build\fsyacc\netcoreapp3.1\fsyacc.dll"  -o "CPar.fs" --module CPar CPar.fsy`  
  生成`CPar.fs`语法分析器与`CPar.fsi`  

- `javac Machine.java`  
  生成虚拟机

- `dotnet fsi -r ./bin/Debug/net5.0/FsLexYacc.Runtime.dll Absyn.fs CPar.fs CLex.fs Debug.fs Parse.fs Machine.fs Comp.fs ParseAndComp.fs`  
  运行编译器。

- 在`dotnet fsi`中载入:  
  `open ParseAndComp;;`:  载入module

  `fromString`：从字符串中进行编译

  `fromFile`：从文件中进行编译

  `compileToFile`：生成中间表示


示例：

```fsharp
compileToFile (fromFile "test/test_init.c") "test/test_init.out";;  
#q;;

fromString "void main(){int i = 1;}";;
```

生成中间表示之后，便可以使用虚拟机对中间代码进行运行得出结果：

虚拟机功能：

- `java Machine` 运行中间表示
- `java Machinetrace` 追踪堆栈变化

示例：

```bash
java Machine test_init.out
java Machinetrace test_init.out
```

## 功能实现

- 变量赋值声明

  - 简介：原本的microC只能声明变量，不能顺带赋值。改进后可以同时进行。

  - 例子：

    ```C
    int main(){
      int i = 1;
      print i;
    }
    ```

    运行结果：

    ![init-result](.\img\init-result.png)

    运行栈：

    ![init-trace](.\img\init-trace.png)

---

- 自增、自减操作

  - 简介:包含i++ ++i --i i--操作

  - 例子：

    ```C
    void main(){
        int i;
        i = 1;
        print i++;//1
        print ++i;//3
        print i--;//3
        print --i;//1
    }
    ```

    运行结果：  
    ![spsm-result](.\img\spsm-result.png)

    运行栈：

    ![spsm-trace](.\img\spsm-trace.png)

---

- FOR循环

  - 简介：增加了for循环

  - 例子：

    ```C
    void main(){
        int i;
        for(i = 0; i < 5; ++i){
            print i;
        }
    }
    ```

    运行结果：  
    ![for-result](.\img\for-result.png)

    运行栈：

    ![for-trace](.\img\for-trace.png)

---

- dowhile循环

  - 简介：增加了dowhile循环

  - 例子：

    ```C
    void main(){
        int i = 0;
        do{
            print i;
            i++;
        }while(i < 3);
    }
    ```
    
    运行结果：
    
    ![dowhile-result](.\img\dowhile-result.png)
    运行栈：
    
    ![dowhile-trace](.\img\dowhile-trace.png)

---

- 三目运算符

  - 简介：三目运算符 a>b?a:b

  - 用例：

    ```C
    void main(){
        int a = 1;
        print a>2?2:3;
        print a+2>2?2:3;
    }
    ```

    运行结果： 
    
    ![prim3-result](.\img\prim3-result.png)
    
    运行栈：
    
    ![prim3-trace](.\img\prim3-trace.png)

---

- for in range

  - 简介：for i in range(0,5)    范围循环

  - 例子：

    ```C
    void main(){
        int i;
        for i in range(0, 5){
            print i;
        }
    }
    ```

    运行结果：

    ![forin-result](.\img\forin-result.png)
    运行栈：

    ![forin-trace](.\img\forin-trace.png)

---

- Switch-case-default

  - 例子：

    ```C
    void main(){
        int i = 0;
        switch (i) {
            case 1: print 1;
            case 2: print 2;
            default: print 3; //3
        }
        switch (i) {
            case 0: print i++;//0
            case 1: print i++;//1
            case 2: print 3; //2
        }
    }
    ```
    
    运行结果： 

    ![switch-result](.\img\switch-result.png)
    
    运行栈：![switch-trace](.\img\switch-trace.png)

---

- += -= /= *= %=

  - 例子：
  
    ```c
    void main(){
        int i;
        i = 1;
        i+=2;
        print i;//3
        i-=1;
        print i;//2
        i*=4;
        print i;//8
        i/=2;
        print i;//4
        i%=3;
        print i;//1
    }
    ```
    
    运行结果： 
    
    ![assop-result](.\img\assop-result.png)运行栈：
    
    ![assop-trace](.\img\assop-trace.png)

---

- 与 或 非 异或 & ｜ ~  ^

  - 例子：

    ```c
    void main(){
        print 1&0;
        print 1|0;
        print 2^1;
        print ~1;
    }
    ```
    
    运行结果： 
    
    ![logic-result](.\img\logic-result.png)
    
    运行栈：
    
    ![logic-trace](.\img\logic-trace.png)

---

- 移位 << >>功能：

  - 例子：

    ```C
    void main(){
        int i = 1;
        print i << 2;
        print i >> 1;
    }
    ```

    运行结果： 
    
    ![shift-result](.\img\shift-result.png)
    
    运行栈：
    
    ![shift-trace](.\img\shift-trace.png)

---

- Max Min 功能：

  - 例子：

    ```C
    void main(){
        print max(5,2);
        print min(3*3,8);
    }
    ```

    运行结果： 
    
    ![maxmin-result](.\img\maxmin-result.png)
    
    运行栈：
    
    ![maxmin-trace](.\img\maxmin-trace.png)

---

- JVM

  BITAND = 26,BITOR = 27,BITXOR = 28,BITLEFT = 29,BITRIGHT = 30, BITNOT = 31

  - 指令集添加：
    - BITAND：
      - 简介：& 与
      - 功能：将两个表达式进行与运算
    - BITOR： 
      - 简介：｜ 或
      - 功能：将两个表达式进行或运算
    - BITXOR： 
      - 简介：^  异或
      - 功能：将两个表达式进行异或运算
    - BITLEFT： 
      - 简介：<<
      - 功能：将值左移
    - BITRIGHT： 
      - 简介：>>
      - 功能：将值右移
    - BITNOT： 
      - 简介：~
      - 功能：二进制取反 

---



## 心得体会

- 大项目开发过程心得

  这学期的课程任务比较多，以及microc开始实在是毫无头绪，所以一直拖，大概只写了四五天的样子。后面从头开始看编译器的结构以及老师给的注释，了解了一点。因为原项目中有while，所以dowhile和for实现起来比较方便，但是再写其它功能的时候就卡住了。中间想写continue和break来着，但是发现编译器的存储只支持int，重新改变存储环境又是一件比较费时间的事，于是放弃了。以及我的switch有点问题，进入一个case之后就自动break了，也不会改。感受到写一个功能的结构逻辑要求，可能最终实现的代码并不多，但是要想明白其中一步步的定义构造还是挺麻烦的。

- 本课程评价

  对于我来说，还是觉得这门课有点难，一个是和讲的课和课本知识不是很配套，还有一个就是fsharp网上的教程较少，遇到困难或报错的时候无法及时的知道解决方法，以及底层的一些汇编指令不是很懂。

