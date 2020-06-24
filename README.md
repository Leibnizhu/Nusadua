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
That's all.  

## Q&A
### Why defaultInt,defaultString,defaultXxx...?
In Java, annotation member types must be one of:
- primitive
- String
- an Enum
- another Annotation
- Class
- an array of any of the above

No `Object`, it means we can not define an annotation member to accept all type of default value; So I define annotation members like `defaultInt`, `defaultString` to accept String and primitive type and their Array. However, array support is not so good now.
### How to define a field with default value of null?
use `@MethodOverload(field = "Xxx", defaultNull = true)`
### What's the next step?
- Enhance default array handling.
- Write a plugin for IntelliJ IDEA (Otherwise IDEA will report an compile error when editing (Even so, maven compile will not error))
