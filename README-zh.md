# Nusadua
[[English]](README.md)  [[中文]](README-zh.md)
## 介绍
Nusadua为Java提供了方法参数默认值重载的语法糖, 可以用来简化代码. 我们都知道，Java里经常要通过方法重载来实现参数的默认值，而且只能用重载来实现；而Scala是可以在方法签名里直接定义参数默认值的，这个项目就是为了做到这一点（虽然不是很完美）。  
嗯，为什么叫"Nusadua"呢？写Java的都知道`Lombok`，而`Nusadua`的角色跟`Lombok`很像。我去年在巴厘岛旅行的时候，住在Nusa Dua海滩边上，当时就有了实现这个语法糖的想法；而`Lombok`这个名字其实也是印度尼西亚的一个岛，龙目岛，天气晴朗的时候，在巴厘岛东岸可以直接看到龙目岛（其实是能看到林贾尼火山，在龙目岛的一座火山），所以……就叫"Nusadua"吧。

## 使用方法
首先增加依赖, 以Maven为例，在`pom.xml`增加:  
```xml
<dependency>
    <groupId>io.github.leibnizhu</groupId>
    <artifactId>nusadua</artifactId>
    <version>0.0.2</version>
</dependency>
```
在Scala里, 是这样定义方法参数的默认值的:
```scala
def query(userId: Int = -1, keyword:String =""): List[Xxx] = {
    //Do something
}
```
在Java里，对应代码是:
```java
public List<Xxx> query(Integer userId, String keyword) {
    //Do something
}

public List<Xxx> query(String keyword) {
    return query(-1, keyword);
}

public List<Xxx> query(Integer userId) {
    return query(userId, "");
}

public List<Xxx> query() {
    return query(-1, "");
}
```
嗯，很冗余。  
**但是**, 如果用`Nusadua`: 
```java
@MethodOverload(field = "userId", defaultInt = -1)
@MethodOverload(field = "keyword", defaultString = "")
public List<Xxx> query(Integer userId, String keyword) {
    //Do something
}
```
就可以了。
对于数组类型的默认值可以用`defaultXxxArr`:
```java
@MethodOverload(field = "keyword", defaultStringArr = {""})
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}
```
编译后等价于:
```java
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}

public List<Xxx> query(Integer userId) {
   return query(userId, new String[]{""});
}
```
但现在对数组类型支持不是很好（主要是装箱类型的数组）.  
对于默认值为null的参数，可以使用`defaultNull=true`:
```java
@MethodOverload(field = "keyword", defaultNull = true)
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}
```
编译后等价于:
```java
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}

public List<Xxx> query(Integer userId) {
   return query(userId, null);
}
```

## Q&A
### 为什么用defaultInt,defaultStringArr,defaultXxx...这么多种注解参数?
Java规定了注解的成员属性只能是以下之一:
- 基础类型
- String
- 枚举
- 其他注解
- Class
- 以上类型的数组

嗯，没有`Object`，也就是说不能用一个成员属性接收所有类型的方法参数默认值，而且，null也是不允许的。所以才对基础类型和String、以及他们的数组类型定义了这么多注解参数，以及专门为null设置了一个参数。是有点麻烦，不过终归比写一堆重载方法好。  
### 一共有哪些注解参数?
- defaultBool
- defaultBoolArr
- defaultByte
- defaultByteArr
- defaultChar
- defaultCharArr
- defaultDouble
- defaultDoubleArr
- defaultFloat
- defaultFloatArr
- defaultInt
- defaultIntArr
- defaultLong
- defaultLongArr
- defaultNull
- defaultShort
- defaultShortArr
- defaultString
- defaultStringArr 
  
### 方法签名冲突是怎么处理的?
当你在一个方法上使用两个或以上的`@MethodOverload`注解的时候，就可能在方法重载的时候会出现方法签名冲突的情况，比如：
```java
@MethodOverload(field = "userName", defaultString = "1")
@MethodOverload(field = "keyword", defaultString = "2")
public List<Xxx> query(String userName, String keyword) {
    //Do something
}
```
`Nusadua`首先会生成只有一个参数默认值的方法，如果这个阶段出现了方法签名冲突，会直接报编译异常，比如以上代码在maven编译的时候会报：  
```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:testCompile (default-testCompile) on project nusadua: Compilation failure
[ERROR] io.github.leibnizhu.nusadua.NusaduaTest.query(String userName,String keyword) method has a MethodOverload annotation's ERROR,Annotation definition: method with same signature (String userName) already existed! Can not continue!
```
单默认参数方法的生成结束后，会逐一生成两个默认参数的方法、三个默认参数的方法（直到所有默认参数都用上的方法，N个注解理想情况下一共有`C(N,1)+C(N,2)+....+C(N,N)`个重载方法）。这个阶段如果出现方法签名冲突，会直接忽略，但哪个方法会被忽略就不受控了，对于这种情况，要尽量避免。比如：
```java
@MethodOverload(field = "str1", defaultString = "true")
@MethodOverload(field = "i", defaultInt = -1)
@MethodOverload(field = "str2", defaultString = "false")
public void multiSignConflict(String str1, int i, String str2) {
    System.out.println(String.format("String1=%s, int=%s, String2=%s", str1, i, str2));
}
```
可能会编译为：
```java
public void multiSignConflict(String str1, int i, String str2) {
    System.out.println(String.format("String1=%s, int=%s, String2=%s", str1, i, str2));
}

public void multiSignConflict(int i, String str2) {
    this.multiSignConflict("true", i, str2);
}

public void multiSignConflict(String str1, int i) {
    this.multiSignConflict(str1, i, "false");
}

public void multiSignConflict(String str1, String str2) {
    this.multiSignConflict(str1, -1, str2);
}

public void multiSignConflict(int i) {
    this.multiSignConflict("true", i, "false");
}

/**
 * 这里是使用了两个默认值的方法，有两个方法签名都是只有一个String，哪个会被保留是不可预估的（实际上可以）
 */
public void multiSignConflict(String str2) {
    this.multiSignConflict("true", -1, str2);
}

public void multiSignConflict() {
    this.multiSignConflict("true", -1, "false");
}
```

### 下一步要做什么?
- 增强完善数组类型的支持。
- 写IntelliJ IDEA的插件（否则IDEA编辑时会不识别新的重载方法，虽然最后编译运行的时候不会出错）
