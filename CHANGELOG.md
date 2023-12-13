# Change Log

## v1.1.0 (2023-12-12)

- Support for RSS export
- Refactor classes separate packages
- Update gradle plugins:
    - io.freefair.lombok version '8.4'
    - com.google.cloud.tools.jib version '3.4.0'
    - org.openrewrite.rewrite '6.5.12'
- Update gradle dependencies:
    - org.springframework.boot:spring-boot-starter:3.2.0
    - org.springframework.boot:spring-boot-starter-web:3.2.0
    - org.springframework.boot:spring-boot-configuration-processor:3.2.0
    - org.projectlombok:lombok:1.18.30
    - org.springframework.boot:spring-boot-starter-test:3.2.0
    - org.glassfish.jaxb:jaxb-runtime:4.0.4
    - org.openrewrite.recipe:rewrite-recipe-bom:2.5.3
    - org.glassfish.jaxb:jaxb-runtime:2.3.9
    - net.sourceforge.htmlunit:htmlunit:2.65.1 (removed)
- Update rewrite recipes:
    - org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2
    - org.openrewrite.java.migrate.lombok.UpdateLombokToJava17 (removed)
    - org.openrewrite.java.cleanup.FixStringFormatExpressions (removed)
    - org.openrewrite.java.RemoveUnusedImports (added)

## v1.0.0 (2023-04-30)

- Initial release using https://api.dailytsp.com/close/, based on scholarshare.price
