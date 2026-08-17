# Runtime đa module và H2

## Tổng quan

`shop` ở thư mục gốc là Maven aggregator/parent. Danh sách `<modules>` chỉ giúp Maven build các module; nó không tự đưa code của module con vào classpath lúc chạy.

`shop-app` là entry point duy nhất. Module này phải khai báo trực tiếp các module nghiệp vụ cần chạy. Chuỗi phụ thuộc hiện tại là:

```text
shop-app -> shop-user -> shop-core
```

Vì `shop-user` đã phụ thuộc `shop-core`, `shop-app` không cần khai báo lại `shop-core` trừ khi code trong `shop-app` trực tiếp sử dụng API của `shop-core` và muốn thể hiện dependency đó rõ ràng.

## Quy tắc entry point

Class `ShopAppApplication` chịu trách nhiệm nạp các thành phần của tất cả module:

- Component scan: toàn bộ package dưới `com.example`.
- Entity scan: `com.example.shopuser.entity`.
- Repository scan: `com.example.shopuser.repository`.

Khi thêm module nghiệp vụ mới, cần thực hiện cả hai việc:

1. Thêm dependency của module vào `shop-app/pom.xml`.
2. Đảm bảo component nằm dưới `com.example`; mở rộng entity/repository scan khi module mới có dữ liệu JPA.

## H2 và JPA

H2 là database trong bộ nhớ của tiến trình `shop-app`. Database chỉ tồn tại sau khi ứng dụng khởi động thành công và bị xóa khi ứng dụng dừng.

Thông tin đăng nhập H2 Console:

```text
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:shopdb
User Name: sa
Password: để trống
```

JDBC URL trên trang đăng nhập phải giống chính xác `spring.datasource.url` trong `shop-app/src/main/resources/application.yml`. Dùng một tên khác, chẳng hạn `jdbc:h2:mem:userdb`, sẽ kết nối tới database khác hoặc báo database không tồn tại.

## Kiểm tra

Từ thư mục gốc, chạy:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd -pl shop-app -am spring-boot:run
```

Khi khởi động đúng, log có thông tin Hikari/Hibernate và câu lệnh tạo hoặc cập nhật bảng. Sau đó mới mở H2 Console bằng thông tin ở trên.

## Tham chiếu

- `pom.xml`: danh sách module và cấu hình dùng chung.
- `shop-app/pom.xml`: classpath runtime của ứng dụng.
- `shop-app/src/main/java/com/example/shopapp/ShopAppApplication.java`: cấu hình scan.
- `shop-app/src/main/resources/application.yml`: DataSource, JPA và H2 Console.
