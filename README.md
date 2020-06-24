# Nusadua
[[English]](README.md)  [[中文]](README-zh.md)
## Introduction
Nusadua is a java method overload syntactic sugar, you can use it to simplify your code. As we known, we often use method-overloading to set a default value for a parameter, yes, we can only archive it with method-overloading in Java. However, in Scala we can define parameter default value in method signature easily, so I try to bring this syntactic sugar to Java.  
So, why 'Nusadua'? As Javaer, we all known `Lombok`, `Nusadua`'s role is very similar to `Lombok`. I had this idea when I was traveling in Bali (Nusa Dua beach) last year, and Lombok(Rinjani Volcano) can be seen from Bali, so that's where the name of this project comes from.

## Usage
Add dependency, for example, in Maven `pom.xml` file:  
```xml
<dependency>
    <groupId>io.github.leibnizhu</groupId>
    <artifactId>nusadua</artifactId>
    <version>0.0.1</version>
</dependency>
```
In Scala, we define a method with default value like:
```scala
def query(userId: Int = -1, keyword:String =""): List[Xxx] = {
    //Do something
}
```
In Java, the code above can be translated as:
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
Obviously, it's very redundant.  
**But**, after using `Nusadua`: 
```java
@MethodOverload(field = "userId", defaultInt = -1)
@MethodOverload(field = "keyword", defaultString = "")
public List<Xxx> query(Integer userId, String keyword) {
    //Do something
}
```
For array type default value, we can use `defaultXxxArr`:
```java
@MethodOverload(field = "keyword", defaultStringArr = {""})
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}
```
Equals to:
```java
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}

public List<Xxx> query(Integer userId) {
   return query(userId, new String[]{""});
}
```
However, array support is not so good now.  
For null default value, we can use `defaultNull=true`:
```java
@MethodOverload(field = "keyword", defaultNull = true)
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}
```
Equals to:
```java
public List<Xxx> query(Integer userId, String[] keyword) {
    //Do something
}

public List<Xxx> query(Integer userId) {
   return query(userId, null);
}
```

## Q&A
### Why defaultInt,defaultStringArr,defaultXxx...?
In Java, annotation member types must be one of:
- primitive
- String
- an Enum
- another Annotation
- Class
- an array of any of the above

No `Object`, it means we can not define an annotation member to accept all type of default value; So I define annotation members like `defaultInt`, `defaultStringArr` to accept String and primitive type and their Array.  
### All supported annotation members?
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
  
### What about method signature conflict?
When we use more than one `@MethodOverload` on the same method, method signature conflict might happen. For example:
```java
@MethodOverload(field = "userName", defaultString = "1")
@MethodOverload(field = "keyword", defaultString = "2")
public List<Xxx> query(String userName, String keyword) {
    //Do something
}
```
`Nusadua` try to generate new methods with single default value at first; if method signature conflict happened at this part, `Nusadua` would throw a compiling ERROR and stop compiling, as the example above: 
```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:testCompile (default-testCompile) on project nusadua: Compilation failure
[ERROR] io.github.leibnizhu.nusadua.NusaduaTest.query(String userName,String keyword) method has a MethodOverload annotation's ERROR,Annotation definition: method with same signature (String userName) already existed! Can not continue!
```
After single default value method generation finish, `Nusadua` try to generate new methods with two, three, four...(up to `@MethodOverload` quantity) default values successively; if method signature conflict happened at this part, the new method would be dropped. For example: 
```java
@MethodOverload(field = "str1", defaultString = "true")
@MethodOverload(field = "i", defaultInt = -1)
@MethodOverload(field = "str2", defaultString = "false")
public void multiSignConflict(String str1, int i, String str2) {
    System.out.println(String.format("String1=%s, int=%s, String2=%s", str1, i, str2));
}
```
will be compiled to:
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
 * which String parameter will be keep is unpredictable
 */
public void multiSignConflict(String str2) {
    this.multiSignConflict("true", -1, str2);
}

public void multiSignConflict() {
    this.multiSignConflict("true", -1, "false");
}
```

### What's the next step?
- Enhance default array handling.
- Write a plugin for IntelliJ IDEA (Otherwise IDEA will report an compile error when editing (Even so, maven compile will not error))
