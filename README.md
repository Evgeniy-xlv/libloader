### LibLoader

A simple and useful tool allows the developer not to worry about dependencies of their mods. This tool will automatically download and install the specified libraries.

Usage:
```java
@Mod(
    modid = "mymod"
)
@DependsOn({
    "https://jitpack.io/com/github/Evgeniy-Xlv/json-configs/1.0.0/json-configs-1.0.0.jar",
    "https://jitpack.io/com/github/Evgeniy-Xlv/libloader/0.0.1/libloader-0.0.1.jar",
})
public class MyMod {
}
```