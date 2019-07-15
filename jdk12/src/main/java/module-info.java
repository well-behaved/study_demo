//设置模块名称 模块为开放模式
module com.xue.demo.jdk {
    /*
    依赖java.base模块 声明当前模块与另一个模块的依赖关系。
        语法：
        requires <module>;
        requires transitive <module>;
        requires static <module>;
        requires transitive static <module>;
       require语句中的静态修饰符表示在编译时的依赖是强制的，但在运行时是可选的。requires static N语句意味着模块M取决于模块N
       ，模块N必须在编译时出现才能编译模块M，而在运行时存在模块N是可选的。require语句中的transitive修饰符会导致依赖于当前模块的其他模块具有隐式依赖性。
       假设有三个模块P，Q和R，假设模块Q包含requires transitive R语句，如果如果模块P包含包含requires Q语句，这意味着模块P隐含地取决于模块R。

     */
    requires com.xue.demo.jdk2;
    requires transitive java.compiler;
    requires  java.base;
    requires java.scripting;
//    requires kotlin.
    /*
    导出jdk包 允许仅在编译时和运行时访问指定包的公共API
     */
    exports com.xue.demo.jdk;
    /*
    开放语句 开放语句允许对所有模块的反射访问指定的包或运行时指定的模块列表
    。 其他模块可以使用反射访问指定包中的所有类型以及这些类型的所有成员（私有和公共）。
    opens <package> to <module1>, <module2>
     */
    opens com.xue.demo.jdk to java.base;
    /*
    Java允许使用服务提供者和服务使用者分离的服务提供者机制。 JDK 9允许使用语句（uses statement）和提供语句（provides statement）实现其服务。
     */
    provides com.xue.demo.jdk2.IUserService with com.xue.demo.jdk.nine.IUserServiceImpl;
}