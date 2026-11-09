# 🛒 E-Commerce Backend System

Hệ thống Backend RESTful API thương mại điện tử toàn diện được xây dựng trên nền tảng **Java 21**, **Spring Boot 4 / 3**, **Spring Security (JWT OAuth2)**, **Spring Data JPA**, và **MySQL 8.0**. Dự án hỗ trợ quy trình bán hàng đa vai trò (Khách hàng, Nhân viên bán hàng, Thủ kho, Marketing và Quản trị viên hệ thống), quản lý kho vận đa chi nhánh và kiểm soát hàng tồn kho theo từng vị trí kệ.

---

## 📋 Mục Lục

- [Công Nghệ Sử Dụng](#-công-nghệ-sử-dụng)
- [Kiến Trúc & Điểm Nổi Bật](#-kiến-trúc--điểm-nổi-bật)
- [Các Phân Hệ Chức Năng](#-các-phân-hệ-chức-năng)
- [Mô Hình Phân Quyền (RBAC)](#-mô-hình-phân-quyền-rbac)
- [Tài Khoản Mẫu Trải Nghiệm](#-tài-khoản-mẫu-trải-nghiệm)
- [Hướng Dẫn Khởi Chạy Dự Án](#-hướng-dẫn-khởi-chạy-dự-án)
  - [Cách 1: Khởi chạy bằng Docker & Docker Compose (Khuyên dùng)](#cách-1-khởi-chạy-bằng-docker--docker-compose-khuyên-dùng)
  - [Cách 2: Khởi chạy trực tiếp trên máy cục bộ (Local)](#cách-2-khởi-chạy-trực-tiếp-trên-máy-cục-bộ-local)
- [Cấu Hình Biến Môi Trường](#-cấu-hình-biến-môi-trường)
- [Kiểm Thử API Với Postman](#-kiểm-thử-api-với-postman)
- [Cấu Trúc Thư Mục Dự Án](#-cấu-trúc-thư-mục-dự-án)

---

## 🛠 Công Nghệ Sử Dụng

- **Ngôn ngữ**: Java 21 (LTS)
- **Framework chính**: Spring Boot 4.1.0
  - Spring Data JPA & Hibernate 7
  - Spring Security (OAuth2 Resource Server với JWT)
  - Spring Validation (Jakarta Validation)
- **Cơ sở dữ liệu**: MySQL 8.0, HikariCP Connection Pool
- **Mapping & Tiện ích**:
  - MapStruct 1.5.5.Final (Object Mapping hiệu năng cao không qua Reflection thời gian chạy)
  - Lombok 1.18.30
  - Nimbus JOSE + JWT (Mã hóa và kiểm tra chữ ký token HMAC-SHA512)
- **Container hóa & Triển khai**:
  - Docker & Docker Compose
  - Base Image: `eclipse-temurin:21-jre-alpine` tối ưu dung lượng và tài nguyên JVM
- **Kiểm thử**: JUnit 5, Mockito, Postman Collection v2.1.0

---

## 💡 Kiến Trúc & Điểm Nổi Bật

1. **Phòng chống N+1 Query & Tối ưu Database**:
   - Sử dụng `@EntityGraph` và `@BatchSize(size = 30)` trên các quan hệ One-to-Many / Many-to-Many (`Product`, `Order`, `OrderDetail`, `CartDetail`, `Warehouse`).
   - Chuyển đổi toàn bộ quan hệ `@ManyToOne` sang `FetchType.LAZY`.
   - Batch query gom danh sách ID khi tính toán giỏ hàng và tồn kho, loại bỏ hoàn toàn các truy vấn lặp trong vòng lặp (`N query -> 1 batch query`).
   - Đánh chỉ mục (`@Index`) tối ưu trên các trường tìm kiếm và khóa ngoại thường xuyên lọc (`name`, `status`, `order_date`, v.v.).

2. **Chống Race Condition & Deadlock**:
   - Trừ số lượng suất khuyến mãi (`applicableQuantity`) bằng câu lệnh Update nguyên tử (Atomic Database Decrement).
   - Khóa bi quan (`Pessimistic Lock`) và sắp xếp thứ tự danh sách ID sản phẩm khi trừ tồn kho (`LocationDetail`) để triệt tiêu nguy cơ Deadlock giữa các transaction đồng thời.

3. **Điều phối đơn hàng thông minh theo khoảng cách kho**:
   - Tự động tìm kho có đủ hàng cho toàn bộ sản phẩm trong giỏ.
   - Tính toán phí giao hàng dựa trên vị trí địa lý của Tỉnh/Thành giao hàng so với Tỉnh/Thành đặt kho (Nội tỉnh, Cùng miền, Khác miền).

4. **Bảo mật Token & Cơ chế Blacklist**:
   - Hỗ trợ Access Token và Refresh Token thời hạn dài.
   - Khi Logout hoặc đổi mật khẩu, JTI của Token được lưu vào bảng `invalidated_tokens`.
   - Tiến trình tự động dọn dẹp (`@Scheduled`) chạy ngầm lúc 02:00 sáng hàng ngày để xóa sạch các token đã hết hạn, giữ bảng dữ liệu luôn nhẹ.

---

## 📦 Các Phân Hệ Chức Năng

### 1. Phân hệ Khách hàng (Customer)
- Đăng ký tài khoản, đăng nhập JWT.
- Quản lý thông tin hồ sơ cá nhân (`/customers/me`) và đổi mật khẩu an toàn.
- Quản lý giỏ hàng (`/carts`): Thêm, sửa số lượng, kiểm tra tồn kho tức thời (`validate-stock`), xóa sản phẩm.
- Đặt hàng từ giỏ hàng (`/orders`), theo dõi trạng thái đơn hàng, hủy đơn hàng khi đơn chưa được duyệt.
- Đánh giá sản phẩm từ 1 đến 5 sao kèm bình luận (`/reviews`).

### 2. Phân hệ Nhân viên Bán hàng (Sale Staff)
- Xem danh sách đơn hàng thuộc các Tỉnh/Thành phố mình phụ trách.
- Xác nhận duyệt đơn hàng (`confirm-order`) để chuyển tiếp sang bộ phận Kho đóng gói.
- Cập nhật trạng thái xử lý đơn hàng.
- Phản hồi bình luận, đánh giá của khách hàng về sản phẩm.

### 3. Phân hệ Thủ kho & Quản lý Kho bãi (Warehouse)
- Quản lý kho hàng đa chi nhánh, phân chia vị trí lưu trữ trên từng kệ hàng (`locations`).
- Lập phiếu nhập kho (`ImportNote`) từ nhà cung cấp -> Tự động tăng số lượng tồn kho theo đúng vị trí kệ.
- Lập phiếu xuất kho (`ExportNote`) -> Kiểm tra tồn kho và trừ số lượng tại kệ.
- Điều chuyển hàng hóa giữa các kệ trong kho (`moveProduct`).
- Quản lý đơn hàng cần đóng gói: Nhận đơn, in vận đơn, gán mã vận chuyển (`shippingCode`) và chuyển trạng thái sang `Đang giao hàng`.
- Truy vết toàn bộ lịch sử hoạt động xuất/nhập kho (`WarehouseActivity`).

### 4. Phân hệ Tiếp thị & Khuyến mãi (Marketing)
- Thiết lập chương trình khuyến mãi theo khoảng thời gian (`startDate` - `endDate`).
- Giảm giá linh hoạt theo số tiền cố định và giới hạn số suất khuyến mãi Flash Sale cho từng sản phẩm (`applicableQuantity`).
- Ghi log vết lịch sử chỉnh sửa chương trình khuyến mãi (`PromotionLog`).
- Xem thống kê đơn hàng và khách hàng phục vụ chiến dịch tiếp thị.

### 5. Phân hệ Quản trị viên (Admin)
- Quản lý người dùng, tạo tài khoản và phân quyền cho nhân viên.
- Toàn quyền CRUD Sản phẩm, Danh mục, Loại sản phẩm, Phương thức thanh toán.
- Quản trị cấu hình bảo mật vai trò (`Role`) và quyền hạn chi tiết (`Permission`).
- Kiểm duyệt đánh giá (ẩn đánh giá không phù hợp).

---

## 🔐 Mô Hình Phân Quyền (RBAC)

Hệ thống áp dụng mô hình phân quyền ma trận giữa **Roles** và **Permissions**:

```
[User / Employee] ──(1:N)──> [Role] ──(N:N)──> [Permission]
```

- **ADMIN**: Nắm giữ toàn bộ quyền hạn trong hệ thống.
- **SALE**: `ORDER_VIEW_ALL`, `ORDER_APPROVE`, `ORDER_UPDATE`, `ORDER_CANCEL`, `CUSTOMER_VIEW`, `PRODUCT_VIEW`, `REVIEW_REPLY`, `REPORT_SALES_VIEW`,...
- **WAREHOUSE**: `INVENTORY_VIEW`, `INVENTORY_UPDATE`, `WAREHOUSE_VIEW`, `LOCATION_VIEW`, `LOCATION_MANAGE`, `IMPORT_NOTE_CREATE`, `EXPORT_NOTE_CREATE`, `ORDER_VIEW_ALL`, `ORDER_UPDATE`,...
- **MARKETING**: `PRODUCT_VIEW`, `PRODUCT_CREATE`, `PRODUCT_UPDATE`, `PROMOTION_VIEW`, `PROMOTION_CREATE`, `PROMOTION_UPDATE`, `PROMOTION_DELETE`, `REPORT_MARKETING_VIEW`, `REVIEW_MODERATE`,...
- **CUSTOMER**: `ORDER_VIEW_SELF`, `ORDER_CREATE`, `ORDER_CANCEL`, `PRODUCT_VIEW`, `CATEGORY_VIEW`.

---

## 👥 Tài Khoản Mẫu Trải Nghiệm

Hệ thống được tích hợp sẵn bộ dữ liệu mẫu (Sample Data Seeder) ngay khi ứng dụng khởi chạy lần đầu:

| Vai trò | Tên đăng nhập / Email | Mật khẩu | Phạm vi trách nhiệm |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Quản trị toàn hệ thống |
| **Customer** | `customer@gmail.com` | `customer123` | Khách hàng mua sắm |
| **Sale Staff** | `sale01` | `sale123` | Nhân viên tư vấn & duyệt đơn Hà Nội |
| **Sale Staff** | `sale02` | `sale123` | Nhân viên tư vấn & duyệt đơn TP.HCM |
| **Warehouse** | `warehouse01` | `warehouse123` | Thủ kho Tổng Miền Nam (TP.HCM) |
| **Warehouse** | `warehouse02` | `warehouse123` | Thủ kho Trung chuyển Miền Trung (Đà Nẵng) |
| **Marketing** | `marketing01` | `marketing123` | Chuyên viên Marketing & Khuyến mãi |

---

## 🚀 Hướng Dẫn Khởi Chạy Dự Án

### Cách 1: Khởi chạy bằng Docker & Docker Compose (Khuyên dùng)

Hệ thống đã có sẵn `Dockerfile`, `docker-compose.yml`, và `.env`:

1. **Khởi động Docker Desktop** trên máy của bạn.
2. **Build gói ứng dụng và bật cụm container**:
   ```bash
   # 1. Build file JAR
   ./mvnw clean package -DskipTests

   # 2. Khởi chạy MySQL và Spring Boot App ngầm
   docker-compose up -d --build
   ```
3. **Kiểm tra trạng thái**:
   ```bash
   docker ps
   docker-compose logs -f app
   ```
4. Ứng dụng sẽ sẵn sàng tại địa chỉ: `http://localhost:8080/ecommerce`

5. **Dừng container khi không sử dụng**:
   ```bash
   docker-compose down
   ```

---

### Cách 2: Khởi chạy trực tiếp trên máy cục bộ (Local)

#### Yêu cầu tiên quyết:
- Cài đặt **Java 21** trở lên.
- Cài đặt **MySQL 8.0** đang chạy ở cổng `3306`.
- Tạo database tên `e_commerce`:
  ```sql
  CREATE DATABASE e_commerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

#### Các bước thực hiện:
1. **Kiểm tra file cấu hình** `src/main/resources/application.yaml` (hoặc thiết lập biến môi trường):
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/e_commerce
       username: root
       password: root
   ```

2. **Chạy ứng dụng bằng Maven Wrapper**:
   - Trên Windows:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - Trên Linux / macOS:
     ```bash
     ./mvnw spring-boot:run
     ```

3. Truy cập kiểm tra API tại:
   `http://localhost:8080/ecommerce/products`

---

## ⚙ Cấu Hình Biến Môi Trường

Hệ thống hỗ trợ cấu hình linh hoạt thông qua biến môi trường hoặc file `.env`:

| Biến môi trường | Giá trị mặc định | Giải thích |
| :--- | :--- | :--- |
| `PORT` | `8080` | Cổng HTTP Server của Backend |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/e_commerce` | Đường dẫn kết nối JDBC MySQL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Tài khoản Database |
| `SPRING_DATASOURCE_PASSWORD` | `root` | Mật khẩu Database |
| `JWT_SIGNER_KEY` | *(Chuỗi 64 ký tự HMAC bí mật)* | Khóa bí mật ký JWT Token |
| `JWT_VALID_DURATION` | `3600` *(1 giờ)* | Thời hạn hiệu lực của Access Token |
| `JWT_REFRESHABLE_DURATION` | `36000` *(10 giờ)* | Thời hạn cho phép Refresh Token |

---

## 📮 Kiểm Thử API Với Postman

Dự án cung cấp sẵn file Postman Collection hoàn chỉnh:
📄 [**`e_commerce_postman_collection.json`**](file:///d:/Project_ITe/e_commerce/e_commerce_postman_collection.json)

### Điểm nổi bật của bộ Postman:
- **Hơn 98 API** được phân nhóm trực quan theo 13 thư mục.
- **Tự động lưu Token**: Khi chạy request đăng nhập (Admin, Customer, Sale, Warehouse, Marketing), script sẽ tự động trích xuất token lưu vào Collection Variables (`{{adminToken}}`, `{{customerToken}}`,...).
- **Tự động lưu ID liên kết chuỗi**: Tự động lưu `productId`, `orderId`, `warehouseId`, `locationId`,... khi vừa tạo mới để tái sử dụng ngay trong các request tiếp theo.
- **Bộ Assertions tự động**: Kiểm tra response format, HTTP status code và mã nghiệp vụ `1000`.

### Cách sử dụng:
1. Mở Postman -> Chọn **Import** -> Chọn file `e_commerce_postman_collection.json`.
2. Chạy `01. Authentication / 1.1 Đăng nhập Admin` hoặc `1.2 Đăng nhập Customer`.
3. Kiểm thử thoải mái toàn bộ các luồng nghiệp vụ còn lại mà không cần copy token thủ công.

---

## 📂 Cấu Trúc Thư Mục Dự Án

```
e_commerce/
├── src/
│   ├── main/
│   │   ├── java/com/example/e_commerce/
│   │   │   ├── configuration/     # Cấu hình Security, JWT Decoder, Data Initializers
│   │   │   ├── controller/        # 15 REST Controllers xử lý API Endpoints
│   │   │   ├── dto/               # Request & Response Data Transfer Objects
│   │   │   ├── entity/            # JPA Entities & Quan hệ Database
│   │   │   ├── enums/             # OrderStatus, UserRole, UserPermission, ErrorCode,...
│   │   │   ├── exception/         # Xử lý ngoại lệ toàn cục (GlobalExceptionHandler, AppException)
│   │   │   ├── mapper/            # MapStruct Mappers (DTO <-> Entity)
│   │   │   ├── repository/        # Spring Data JPA Repositories (Projections, Custom Queries)
│   │   │   ├── service/           # Service Interfaces & Implementations
│   │   │   ├── specification/     # JPA Criteria dynamic filter cho Sản phẩm
│   │   │   └── ECommerceApplication.java
│   │   └── resources/
│   │       └── application.yaml   # Cấu hình môi trường ứng dụng
│   └── test/
│       └── java/.../service/      # Unit Tests & Integration Tests
├── Dockerfile                     # Cấu hình Build Docker Image (Eclipse Temurin 21)
├── docker-compose.yml             # Cấu hình cụm dịch vụ App + MySQL 8.0
├── .env                           # File biến môi trường cho Docker Compose
├── .dockerignore                  # Loại trừ file rác khi build image
├── e_commerce_postman_collection.json # Bộ Postman API Test đầy đủ
├── pom.xml                        # Maven Dependencies & Plugins
└── README.md                      # Tài liệu hướng dẫn dự án
```

---

## 🛡️ License & Bản Quyền

Dự án phát triển phục vụ mục đích nghiên cứu, học tập và triển khai hệ thống thương mại điện tử thực tế. Mọi đóng góp và phản hồi xin vui lòng tạo Issue hoặc Pull Request.

