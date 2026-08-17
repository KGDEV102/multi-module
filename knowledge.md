# Maven Multi-Module Project

## 1. Cấu trúc tổng quát

Project root khai báo và quản lý các module con trong `pom.xml`:

```text
shop (root/parent, packaging = pom)
├── shop-core (library, packaging = jar)
└── shop-app (executable application, packaging = jar)
    └── phụ thuộc vào shop-core
```

- `shop`: project cha, không chứa code dùng để chạy ứng dụng.
- `shop-core`: module thư viện chứa code dùng chung.
- `shop-app`: module thực thi, chứa class khởi động ứng dụng.

## 2. Root project

Root project phải sử dụng:

```xml
<packaging>pom</packaging>
```

Maven yêu cầu project chứa `<modules>` phải có packaging là `pom`. Root project không được compile thành file JAR mà đảm nhận hai vai trò chính: **parent** và **aggregator**.

### 2.1. Parent — cung cấp cấu hình dùng chung

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

- `groupId` và `version`.
- Phiên bản Java.
- Dependency dùng chung.
- Phiên bản dependency.
- Plugin và cấu hình compiler.
- Repository, profile và các cấu hình build khác.

Ví dụ, root project khai báo:

```xml
<properties>
    <java.version>25</java.version>
</properties>
```

Các module con không cần khai báo lại `java.version`.

### 2.2. Aggregator — gom các module để build cùng nhau

Root project khai báo các module:

```xml
<modules>
    <module>shop-core</module>
    <module>shop-app</module>
</modules>
```

Khi chạy lệnh tại thư mục root:

```bash
mvn clean install
```

Maven sẽ build toàn bộ reactor theo thứ tự phụ thuộc:

```text
shop → shop-core → shop-app
```

Tóm lại, root project có nhiệm vụ:

- Gom các module con.
- Quản lý phiên bản và cấu hình chung.
- Cung cấp dependency và plugin dùng chung.
- Điều khiển reactor build.

## 3. Tạo module con

Tạo **module** bên trong root project, không tạo một project độc lập. Trong `pom.xml` của module:

1. Thay Spring Boot parent ban đầu bằng root parent.
2. Có thể xóa `groupId` và `version` của module nếu chúng giống parent.
3. Có thể xóa các metadata không sử dụng như `url` và `scm`.
4. Không đặt `<packaging>pom</packaging>` nếu module chứa code Java cần compile.

Module Java sử dụng packaging `jar`. Vì `jar` là giá trị mặc định của Maven, có thể bỏ qua khai báo này hoặc viết rõ:

```xml
<packaging>jar</packaging>
```

## 4. Module `shop-core`

`shop-core` chứa code dùng chung cho các module khác, ví dụ:

- Entity và base entity.
- Repository.
- Service nghiệp vụ dùng chung.
- Exception và utility.

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

- `@MappedSuperclass`: các field của class cha được ánh xạ xuống entity con.
- `abstract`: không cho phép tạo trực tiếp object `BaseEntity`; chỉ các entity con mới được khởi tạo.

## 5. Module `shop-app`

`shop-app` là module dùng để chạy ứng dụng và thường chứa class có `@SpringBootApplication`.

Để sử dụng code từ `shop-core`, `shop-app` cần khai báo dependency:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>shop-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

Hướng phụ thuộc đúng là:

```text
shop-app → shop-core
```

Không nên để `shop-core` phụ thuộc ngược lại `shop-app`.


# các file cấu hình ứng dụng nằm trong entry point chạy app là shop app 
# khai báo dependency là module con trong entry point chạy app
<dependency>
    <groupId>com.example</groupId>
    <artifactId>shop-user</artifactId>
    <version>${project.version}</version>
</dependency>

