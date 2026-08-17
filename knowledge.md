# Maven Multi-Module Project

## 1. Cấu trúc tổng quát

Project root khai báo và quản lý các module con trong `pom.xml`:

```text
shop (root/parent, packaging = pom)
├── shop-core (library, packaging = jar)
├── shop-user (library, packaging = jar)
└── shop-app (executable application, packaging = jar)
    └── phụ thuộc vào shop-user
```

- `shop`: project cha, không chứa code dùng để chạy ứng dụng.
- `shop-core`: module thư viện chứa code dùng chung.
- `shop-user`: module nghiệp vụ user.
- `shop-app`: module thực thi, chứa class khởi động ứng dụng.

## 2. Root project

Root project phải sử dụng:

```xml
<packaging>pom</packaging>
```

Maven yêu cầu project chứa `<modules>` phải có packaging là `pom`. Root project không compile thành file JAR mà đảm nhận hai vai trò chính: `parent` và `aggregator`.

### 2.1. Parent

Module con khai báo root project làm parent:

```xml
<parent>
    <groupId>com.example</groupId>
    <artifactId>shop</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

Các module có thể kế thừa từ parent:

- `groupId` và `version`
- phiên bản Java
- dependency dùng chung
- phiên bản dependency
- plugin và cấu hình compiler
- repository, profile và các cấu hình build khác

Ví dụ, root project khai báo:

```xml
<properties>
    <java.version>25</java.version>
</properties>
```

Các module con không cần khai báo lại `java.version`.

### 2.2. Aggregator

Root project khai báo các module:

```xml
<modules>
    <module>shop-core</module>
    <module>shop-user</module>
    <module>shop-app</module>
</modules>
```

Khi chạy lệnh tại thư mục root:

```bash
mvn clean install
```

Maven sẽ build toàn bộ reactor theo thứ tự phụ thuộc:

```text
shop -> shop-core -> shop-user -> shop-app
```

Tóm lại, root project có nhiệm vụ:

- gom các module con
- quản lý phiên bản và cấu hình chung
- cung cấp dependency và plugin dùng chung
- điều khiển reactor build

## 3. Tạo module con

Tạo module bên trong root project, không tạo một project độc lập. Trong `pom.xml` của module:

1. Thay Spring Boot parent ban đầu bằng root parent.
2. Có thể xóa `groupId` và `version` của module nếu chúng giống parent.
3. Có thể xóa các metadata không sử dụng như `url` và `scm`.
4. Không đặt `<packaging>pom</packaging>` nếu module chứa code Java cần compile.

Module Java dùng packaging `jar`. Vì `jar` là giá trị mặc định của Maven, có thể bỏ qua khai báo này hoặc viết rõ:

```xml
<packaging>jar</packaging>
```

## 4. Module `shop-core`

`shop-core` chứa code dùng chung cho các module khác, ví dụ:

- entity và base entity
- service dùng chung
- exception và utility

Ví dụ base entity dùng chung:

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
```

Ý nghĩa:

- `@MappedSuperclass`: các field của class cha được ánh xạ xuống entity con
- `abstract`: không cho phép tạo trực tiếp object `BaseEntity`; chỉ các entity con mới được khởi tạo

## 5. Module `shop-app`

`shop-app` là module dùng để chạy ứng dụng và thường chứa class có `@SpringBootApplication`.

### 5.1. Dependency giữa các module

Hướng phụ thuộc đúng là:

```text
shop-app -> shop-user -> shop-core
```

Không nên để `shop-core` phụ thuộc ngược lại `shop-app`.

Trong `shop-app`, cần khai báo dependency tới module chứa tính năng đang chạy:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>shop-user</artifactId>
    <version>${project.version}</version>
</dependency>
```

Ghi nhớ:

- cấu hình ứng dụng đặt trong entry point chạy app là `shop-app`
- dependency của `shop-app` phải khai báo các module mà app cần dùng lúc runtime
- thường không cần khai báo trực tiếp `shop-core` trong `shop-app` nếu `shop-user` đã phụ thuộc vào `shop-core`

### 5.2. Cấu hình scan cho Spring và JPA

Để code của module thực sự chạy được, ngoài dependency còn cần cấu hình scan phù hợp:

```java
@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example.shopuser.entity")
@EnableJpaRepositories(basePackages = "com.example.shopuser.repository")
```

Ý nghĩa:

- `@SpringBootApplication(scanBasePackages = "com.example")`: để Spring scan `component`, `service`, `controller` ở `shop-app`, `shop-user`, `shop-core` nếu chúng nằm dưới `com.example`
- `@EntityScan(...)`: để JPA thấy entity `User`
- `@EnableJpaRepositories(...)`: để Spring Data thấy `UserRepository`

Nếu không khai báo đúng, rất dễ gặp các lỗi sau:

- không tạo bean `service` hoặc `repository`
- không nhận entity
- JPA/Hibernate không tạo table
- `/api/users` hoặc logic database không chạy đúng

### 5.3. Kết luận về dependency trong `shop-app`

- bắt buộc phải có `shop-user` vì app đang chạy chức năng user từ module đó
- thường không cần khai báo trực tiếp `shop-core` nếu `shop-user` đã phụ thuộc vào `shop-core`
- chỉ khai báo trực tiếp `shop-core` trong `shop-app` nếu `shop-app` tự dùng class từ `shop-core`
