---
date: 2026-08-17
session: h2-jpa-multimodule-fix
---

# Journal: 2026-08-17 — H2/JPA multi-module fix

## Context

`shop-app` opened `/h2-console`, but H2 reported `mem:userdb` missing and Hibernate did not appear to create the schema from entities in `shop-user`.

## What happened

- `shop-app/pom.xml` had malformed nested dependency markup, so `shop-user` and its transitive JPA dependencies were not loaded correctly.
- The console used `jdbc:h2:mem:userdb`, while the application DataSource is `jdbc:h2:mem:shopdb`.
- The application package did not automatically discover components, entities, and repositories in sibling modules.
- The JPA repository, service, and controller used `Long` IDs although `BaseEntity.id` is a generated `String` UUID.
- The fix makes `shop-app` depend on `shop-user`, configures cross-module scanning, and aligns user IDs with `String`.

## Reflection

Opening H2 Console only proves that the console servlet is available; it does not prove that the application DataSource or Hibernate schema generation started. Multi-module runtime dependencies, scan boundaries, and the exact JDBC URL must be checked together.

## Decisions

| Decision | Rationale | Impact |
|----------|-----------|--------|
| Keep `shop-app` as the only executable module | Centralizes runtime assembly and configuration | Feature modules act as libraries |
| Depend directly on `shop-user`, not redundantly on `shop-core` | `shop-core` is already transitive through `shop-user` | Simpler dependency graph |
| Configure component, entity, and repository scanning explicitly | Packages are siblings rather than children of `com.example.shopapp` | Spring discovers user beans and JPA metadata |
| Use `String` consistently for user IDs | Matches the UUID identifier declared by `BaseEntity` | Repository and API ID operations are type-safe |
| Connect H2 Console to `jdbc:h2:mem:shopdb` | Must target the same in-memory database as the application | Console displays Hibernate-created tables |

## Next

- Restart `shop-app`, then verify startup logs include Hikari, Hibernate, and schema creation.
- Connect at `/h2-console` with `jdbc:h2:mem:shopdb`, user `sa`, and a blank password.
- Exercise the user create, list, get, and delete endpoints to confirm the module wiring end to end.
