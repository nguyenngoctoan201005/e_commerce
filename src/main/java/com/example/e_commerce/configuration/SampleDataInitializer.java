package com.example.e_commerce.configuration;

import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.Category;
import com.example.e_commerce.entity.CategoryType;
import com.example.e_commerce.entity.Customer;
import com.example.e_commerce.entity.Discount;
import com.example.e_commerce.entity.District;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.Location;
import com.example.e_commerce.entity.LocationDetail;
import com.example.e_commerce.entity.Order;
import com.example.e_commerce.entity.OrderDetail;
import com.example.e_commerce.entity.PaymentMethod;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.ProductDiscount;
import com.example.e_commerce.entity.ProductImage;
import com.example.e_commerce.entity.Province;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.entity.Warehouse;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.enums.Region;
import com.example.e_commerce.enums.UserRole;
import com.example.e_commerce.enums.WarehouseStatus;
import com.example.e_commerce.repository.CategoryRepository;
import com.example.e_commerce.repository.CategoryTypeRepository;
import com.example.e_commerce.repository.CustomerRepository;
import com.example.e_commerce.repository.DiscountRepository;
import com.example.e_commerce.repository.DistrictRepository;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.LocationDetailRepository;
import com.example.e_commerce.repository.LocationRepository;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.PaymentMethodRepository;
import com.example.e_commerce.repository.ProductRepository;
import com.example.e_commerce.repository.ProvinceRepository;
import com.example.e_commerce.repository.RoleRepository;
import com.example.e_commerce.repository.WarehouseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.init-sample-data", havingValue = "true", matchIfMissing = true)
@org.springframework.core.annotation.Order(2)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SampleDataInitializer implements CommandLineRunner {

        ProvinceRepository provinceRepository;
        DistrictRepository districtRepository;
        PaymentMethodRepository paymentMethodRepository;
        CategoryRepository categoryRepository;
        CategoryTypeRepository categoryTypeRepository;
        ProductRepository productRepository;
        WarehouseRepository warehouseRepository;
        LocationRepository locationRepository;
        LocationDetailRepository locationDetailRepository;
        EmployeeRepository employeeRepository;
        CustomerRepository customerRepository;
        DiscountRepository discountRepository;
        OrderRepository orderRepository;
        RoleRepository roleRepository;
        PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) {
                log.info("=== KIỂM TRA VÀ KHỞI TẠO BỘ DỮ LIỆU MẪU MỞ RỘNG (EXPANDED SAMPLE DATA) ===");

                initProvincesAndDistricts();
                initPaymentMethods();
                initCategoriesAndTypes();
                initProducts();
                initEmployees();
                initCustomers();
                initWarehousesAndStock();
                initDiscounts();
                initOrders();

                log.info("=== HOÀN TẤT KHỞI TẠO BỘ DỮ LIỆU MẪU MỞ RỘNG ===");
        }

        private void initProvincesAndDistricts() {
                if (provinceRepository.count() > 0) {
                        log.info("-> Tỉnh thành & Quận huyện đã tồn tại, kiểm tra bổ sung quận/huyện nếu thiếu...");
                        addDistrictsIfMissing();
                        return;
                }

                log.info("-> Đang khởi tạo Tỉnh thành & Quận huyện...");
                Province hanoi = new Province();
                hanoi.setProvinceName("Hà Nội");
                hanoi.setRegion(Region.NORTH);

                Province hcm = new Province();
                hcm.setProvinceName("TP. Hồ Chí Minh");
                hcm.setRegion(Region.SOUTH);

                Province danang = new Province();
                danang.setProvinceName("Đà Nẵng");
                danang.setRegion(Region.CENTRAL);

                provinceRepository.saveAll(List.of(hanoi, hcm, danang));

                District badinh = new District();
                badinh.setDistrictName("Quận Ba Đình");
                badinh.setProvince(hanoi);

                District caugiay = new District();
                caugiay.setDistrictName("Quận Cầu Giấy");
                caugiay.setProvince(hanoi);

                District dongda = new District();
                dongda.setDistrictName("Quận Đống Đa");
                dongda.setProvince(hanoi);

                District quan1 = new District();
                quan1.setDistrictName("Quận 1");
                quan1.setProvince(hcm);

                District quan3 = new District();
                quan3.setDistrictName("Quận 3");
                quan3.setProvince(hcm);

                District binhthanh = new District();
                binhthanh.setDistrictName("Quận Bình Thạnh");
                binhthanh.setProvince(hcm);

                District haichau = new District();
                haichau.setDistrictName("Quận Hải Châu");
                haichau.setProvince(danang);

                District sontra = new District();
                sontra.setDistrictName("Quận Sơn Trà");
                sontra.setProvince(danang);

                districtRepository.saveAll(List.of(badinh, caugiay, dongda, quan1, quan3, binhthanh, haichau, sontra));
        }

        private void addDistrictsIfMissing() {
                Province hanoi = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Hà Nội"))
                                .findFirst().orElse(null);
                Province hcm = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Hồ Chí Minh"))
                                .findFirst().orElse(null);
                Province danang = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Đà Nẵng"))
                                .findFirst().orElse(null);

                if (hanoi != null
                                && districtRepository.findAll().stream()
                                                .noneMatch(d -> d.getDistrictName().equals("Quận Đống Đa"))) {
                        District d = new District();
                        d.setDistrictName("Quận Đống Đa");
                        d.setProvince(hanoi);
                        districtRepository.save(d);
                }
                if (hcm != null && districtRepository.findAll().stream()
                                .noneMatch(d -> d.getDistrictName().equals("Quận Bình Thạnh"))) {
                        District d = new District();
                        d.setDistrictName("Quận Bình Thạnh");
                        d.setProvince(hcm);
                        districtRepository.save(d);
                }
                if (danang != null
                                && districtRepository.findAll().stream()
                                                .noneMatch(d -> d.getDistrictName().equals("Quận Sơn Trà"))) {
                        District d = new District();
                        d.setDistrictName("Quận Sơn Trà");
                        d.setProvince(danang);
                        districtRepository.save(d);
                }
        }

        private void initPaymentMethods() {
                if (paymentMethodRepository.count() > 0) {
                        log.info("-> Phương thức thanh toán đã tồn tại, bỏ qua khởi tạo.");
                        return;
                }

                log.info("-> Đang khởi tạo Phương thức thanh toán...");
                PaymentMethod cod = new PaymentMethod();
                cod.setPaymentMethodName("Thanh toán khi nhận hàng (COD)");

                PaymentMethod vnpay = new PaymentMethod();
                vnpay.setPaymentMethodName("Chuyển khoản VNPAY / QR Code");

                PaymentMethod creditCard = new PaymentMethod();
                creditCard.setPaymentMethodName("Thẻ tín dụng / Thẻ ghi nợ quốc tế");

                paymentMethodRepository.saveAll(List.of(cod, vnpay, creditCard));
        }

        private void initCategoriesAndTypes() {
                if (categoryRepository.count() == 0) {
                        log.info("-> Đang khởi tạo Danh mục sản phẩm (Categories)...");
                        Category cat1 = Category.builder().categoryName("Bánh Quy Bơ").origin("Pháp").build();
                        Category cat2 = Category.builder().categoryName("Bánh Sinh Nhật & Bánh Kem").origin("Việt Nam")
                                        .build();
                        Category cat3 = Category.builder().categoryName("Trà & Đồ Uống").origin("Nhật Bản").build();
                        categoryRepository.saveAll(List.of(cat1, cat2, cat3));
                }

                if (categoryTypeRepository.count() == 0) {
                        log.info("-> Đang khởi tạo Loại sản phẩm (Category Types)...");
                        CategoryType type1 = new CategoryType();
                        type1.setTypeName("Bánh Hộp Thiếc");

                        CategoryType type2 = new CategoryType();
                        type2.setTypeName("Bánh Kem Tươi");

                        CategoryType type3 = new CategoryType();
                        type3.setTypeName("Trà Thượng Hạng");

                        categoryTypeRepository.saveAll(List.of(type1, type2, type3));
                }
        }

        private void initProducts() {
                if (productRepository.count() >= 5) {
                        log.info("-> Sản phẩm đã đầy đủ, bỏ qua khởi tạo.");
                        return;
                }

                log.info("-> Đang khởi tạo/bổ sung Sản phẩm mẫu...");
                Category catCookie = categoryRepository.findAll().stream()
                                .filter(c -> c.getCategoryName().contains("Bánh Quy"))
                                .findFirst().orElse(null);
                Category catCake = categoryRepository.findAll().stream()
                                .filter(c -> c.getCategoryName().contains("Kem"))
                                .findFirst().orElse(null);
                Category catTea = categoryRepository.findAll().stream().filter(c -> c.getCategoryName().contains("Trà"))
                                .findFirst().orElse(null);

                CategoryType typeCookie = categoryTypeRepository.findAll().stream()
                                .filter(t -> t.getTypeName().contains("Thiếc")).findFirst().orElse(null);
                CategoryType typeCake = categoryTypeRepository.findAll().stream()
                                .filter(t -> t.getTypeName().contains("Tươi"))
                                .findFirst().orElse(null);
                CategoryType typeTea = categoryTypeRepository.findAll().stream()
                                .filter(t -> t.getTypeName().contains("Trà"))
                                .findFirst().orElse(null);

                if (catCookie == null)
                        catCookie = categoryRepository.findAll().stream().findFirst().orElse(null);
                if (catCake == null)
                        catCake = catCookie;
                if (catTea == null)
                        catTea = catCookie;

                if (typeCookie == null)
                        typeCookie = categoryTypeRepository.findAll().stream().findFirst().orElse(null);
                if (typeCake == null)
                        typeCake = typeCookie;
                if (typeTea == null)
                        typeTea = typeCookie;

                if (productRepository.count() == 0) {
                        Product p1 = Product.builder()
                                        .name("Bánh Quy Bơ Danisa Hộp Thiếc Hoàng Gia")
                                        .unitPrice(new BigDecimal("185000"))
                                        .unit("Hộp")
                                        .weight(0.9)
                                        .description("Hương vị bơ truyền thống Đan Mạch thơm ngon giòn rụm trong hộp thiếc sang trọng.")
                                        .category(catCookie)
                                        .categoryType(typeCookie)
                                        .build();
                        ProductImage img1 = new ProductImage();
                        img1.setImageUrl("https://images.unsplash.com/photo-1558961363-fa8fdf82db35");
                        p1.addProductImage(img1);
                        ProductImage img2 = new ProductImage();
                        img2.setImageUrl("https://images.unsplash.com/photo-1499636136210-6f4ee915583e");
                        p1.addProductImage(img2);

                        Product p2 = Product.builder()
                                        .name("Bánh Kem Bắp Pháp Phủ Hạnh Nhân")
                                        .unitPrice(new BigDecimal("320000"))
                                        .unit("Cái")
                                        .weight(1.2)
                                        .description("Bánh bông lan mềm xốp kết hợp kem bắp thanh ngọt tự nhiên và hạnh nhân nướng.")
                                        .category(catCake)
                                        .categoryType(typeCake)
                                        .build();
                        ProductImage img3 = new ProductImage();
                        img3.setImageUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587");
                        p2.addProductImage(img3);

                        Product p3 = Product.builder()
                                        .name("Trà Xanh Matcha Uji Thượng Hạng")
                                        .unitPrice(new BigDecimal("260000"))
                                        .unit("Gói")
                                        .weight(0.2)
                                        .description(
                                                        "Bột trà xanh nguyên chất nhập khẩu từ vùng Uji Kyoto Nhật Bản giàu chất chống oxy hóa.")
                                        .category(catTea)
                                        .categoryType(typeTea)
                                        .build();
                        ProductImage img4 = new ProductImage();
                        img4.setImageUrl("https://images.unsplash.com/photo-1536256263959-770b48d82b0a");
                        p3.addProductImage(img4);

                        productRepository.saveAll(List.of(p1, p2, p3));
                }

                // Bổ sung thêm Product 4 & Product 5 nếu chưa có
                if (productRepository.findAll().stream().noneMatch(p -> p.getName().contains("Chocolate Bỉ"))) {
                        Product p4 = Product.builder()
                                        .name("Bánh Mousse Chocolate Bỉ Đậm Vị")
                                        .unitPrice(new BigDecimal("290000"))
                                        .unit("Hộp")
                                        .weight(0.8)
                                        .description("Chocolate Bỉ nguyên chất 70% cacao kết hợp lớp mousse mềm mịn tan chảy.")
                                        .category(catCake)
                                        .categoryType(typeCake)
                                        .build();
                        ProductImage img5 = new ProductImage();
                        img5.setImageUrl("https://images.unsplash.com/photo-1606313564200-e75d5e30476c");
                        p4.addProductImage(img5);
                        productRepository.save(p4);
                }

                if (productRepository.findAll().stream().noneMatch(p -> p.getName().contains("Trà Ô Long"))) {
                        Product p5 = Product.builder()
                                        .name("Trà Ô Long Alishan Thượng Hạng")
                                        .unitPrice(new BigDecimal("350000"))
                                        .unit("Hộp")
                                        .weight(0.3)
                                        .description("Búp trà ô long núi cao Alishan thơm ngát hương hoa tự nhiên.")
                                        .category(catTea)
                                        .categoryType(typeTea)
                                        .build();
                        ProductImage img6 = new ProductImage();
                        img6.setImageUrl("https://images.unsplash.com/photo-1576092768241-dec231879fc3");
                        p5.addProductImage(img6);
                        productRepository.save(p5);
                }
        }

        private void initEmployees() {
                log.info("-> Đang kiểm tra và khởi tạo Nhân viên (Sale, Warehouse, Marketing)...");

                Role saleRole = roleRepository.findByRoleName(UserRole.SALE).orElse(null);
                Role warehouseRole = roleRepository.findByRoleName(UserRole.WAREHOUSE).orElse(null);
                Role marketingRole = roleRepository.findByRoleName(UserRole.MARKETING).orElse(null);

                // 1. Sale staff 1
                if (!employeeRepository.existsByUsername("sale01") && saleRole != null) {
                        Employee sale01 = Employee.builder()
                                        .username("sale01")
                                        .password(passwordEncoder.encode("sale123"))
                                        .fullname("Trần Thị Bán Hàng")
                                        .idCardNumber("001200000001")
                                        .roles(new HashSet<>(List.of(saleRole)))
                                        .build();
                        employeeRepository.save(sale01);
                        log.info("   + Tạo nhân viên Sale: sale01 / sale123");
                }

                // 2. Sale staff 2
                if (!employeeRepository.existsByUsername("sale02") && saleRole != null) {
                        Employee sale02 = Employee.builder()
                                        .username("sale02")
                                        .password(passwordEncoder.encode("sale123"))
                                        .fullname("Nguyễn Văn Tư Vấn")
                                        .idCardNumber("001200000004")
                                        .roles(new HashSet<>(List.of(saleRole)))
                                        .build();
                        employeeRepository.save(sale02);
                        log.info("   + Tạo nhân viên Sale: sale02 / sale123");
                }

                // 3. Warehouse staff TP.HCM
                if (!employeeRepository.existsByUsername("warehouse01") && warehouseRole != null) {
                        Employee wh01 = Employee.builder()
                                        .username("warehouse01")
                                        .password(passwordEncoder.encode("warehouse123"))
                                        .fullname("Lê Văn Thủ Kho TP.HCM")
                                        .idCardNumber("001200000002")
                                        .roles(new HashSet<>(List.of(warehouseRole)))
                                        .build();
                        employeeRepository.save(wh01);
                        log.info("   + Tạo thủ kho TP.HCM: warehouse01 / warehouse123");
                }

                // 4. Warehouse staff Đà Nẵng
                if (!employeeRepository.existsByUsername("warehouse02") && warehouseRole != null) {
                        Employee wh02 = Employee.builder()
                                        .username("warehouse02")
                                        .password(passwordEncoder.encode("warehouse123"))
                                        .fullname("Phạm Minh Kho Đà Nẵng")
                                        .idCardNumber("001200000005")
                                        .roles(new HashSet<>(List.of(warehouseRole)))
                                        .build();
                        employeeRepository.save(wh02);
                        log.info("   + Tạo thủ kho Đà Nẵng: warehouse02 / warehouse123");
                }

                // 5. Marketing staff
                if (!employeeRepository.existsByUsername("marketing01") && marketingRole != null) {
                        Employee mkt01 = Employee.builder()
                                        .username("marketing01")
                                        .password(passwordEncoder.encode("marketing123"))
                                        .fullname("Đoàn Thúy Marketing")
                                        .idCardNumber("001200000003")
                                        .roles(new HashSet<>(List.of(marketingRole)))
                                        .build();
                        employeeRepository.save(mkt01);
                        log.info("   + Tạo nhân viên Marketing: marketing01 / marketing123");
                }
        }

        private void initCustomers() {
                log.info("-> Đang kiểm tra và khởi tạo Khách hàng mẫu (Customers)...");
                District badinh = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Ba Đình"))
                                .findFirst().orElse(null);
                District caugiay = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Cầu Giấy"))
                                .findFirst().orElse(null);
                District quan1 = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Quận 1"))
                                .findFirst().orElse(null);
                District haichau = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Hải Châu"))
                                .findFirst().orElse(null);

                // Khách hàng 1: customer@gmail.com
                if (!customerRepository.existsByEmail("customer@gmail.com")) {
                        createCustomer("customer@gmail.com", "customer123", "Nguyễn Văn Khách Hàng", "0987654321",
                                        "Số 123 Đường Cầu Giấy", caugiay);
                }

                // Khách hàng 2: customer2@gmail.com
                if (!customerRepository.existsByEmail("customer2@gmail.com")) {
                        createCustomer("customer2@gmail.com", "customer123", "Hoàng Thị Mai", "0912345678",
                                        "Số 45 Lê Duẩn, Quận 1",
                                        quan1);
                }

                // Khách hàng 3: customer3@gmail.com
                if (!customerRepository.existsByEmail("customer3@gmail.com")) {
                        createCustomer("customer3@gmail.com", "customer123", "Đỗ Minh Tuấn", "0934567890",
                                        "Số 78 Bạch Đằng, Hải Châu", haichau);
                }

                // Khách hàng 4: customer4@gmail.com
                if (!customerRepository.existsByEmail("customer4@gmail.com")) {
                        createCustomer("customer4@gmail.com", "customer123", "Vũ Lan Anh", "0977889900",
                                        "Số 50 Phố Kim Mã",
                                        badinh);
                }
        }

        private void createCustomer(String email, String rawPassword, String fullname, String phone, String address,
                        District district) {
                Customer customer = Customer.builder()
                                .email(email)
                                .password(passwordEncoder.encode(rawPassword))
                                .fullname(fullname)
                                .phoneNumber(phone)
                                .address(address)
                                .district(district)
                                .build();

                Cart cart = new Cart();
                cart.setCustomer(customer);
                customer.setCart(cart);

                customerRepository.save(customer);
                log.info("   + Đã tạo Khách hàng: {} / {}", email, rawPassword);
        }

        private void initWarehousesAndStock() {
                log.info("-> Đang kiểm tra và khởi tạo Kho hàng, Vị trí & Tồn kho...");

                Employee admin = employeeRepository.findByUsername("admin").orElse(null);
                Employee wh01 = employeeRepository.findByUsername("warehouse01").orElse(admin);
                Employee wh02 = employeeRepository.findByUsername("warehouse02").orElse(admin);

                Province hanoi = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Hà Nội"))
                                .findFirst().orElse(null);
                Province hcm = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Hồ Chí Minh"))
                                .findFirst().orElse(null);
                Province danang = provinceRepository.findAll().stream()
                                .filter(p -> p.getProvinceName().contains("Đà Nẵng"))
                                .findFirst().orElse(null);

                List<Product> products = productRepository.findAll();

                // 1. Kho Tổng Miền Bắc - Hà Nội
                Warehouse whHanoi = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("Hà Nội"))
                                .findFirst().orElse(null);

                if (whHanoi == null && hanoi != null && admin != null) {
                        whHanoi = Warehouse.builder()
                                        .warehouseName("Kho Tổng Miền Bắc - Hà Nội")
                                        .status(WarehouseStatus.ACTIVE)
                                        .province(hanoi)
                                        .employee(admin)
                                        .build();
                        whHanoi = warehouseRepository.save(whHanoi);
                }

                if (whHanoi != null) {
                        createLocationAndStockIfMissing(whHanoi, "Kệ A-01 (Khu bảo quản tiêu chuẩn)",
                                        "Lưu trữ bánh kẹo và trà cao cấp", products, 120L);
                        createLocationAndStockIfMissing(whHanoi, "Kệ A-02 (Khu vực bánh hộp thiếc)",
                                        "Chuyên lưu trữ bánh Danisa đóng thùng", products, 80L);
                        createLocationAndStockIfMissing(whHanoi, "Kệ Lạnh C-01 (Bảo quản mát 2-6°C)",
                                        "Dành riêng cho bánh kem tươi", products, 40L);
                }

                // 2. Kho Tổng Miền Nam - TP.HCM
                Warehouse whHcm = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("TP.HCM")
                                                || w.getWarehouseName().contains("Hồ Chí Minh"))
                                .findFirst().orElse(null);

                if (whHcm == null && hcm != null && wh01 != null) {
                        whHcm = Warehouse.builder()
                                        .warehouseName("Kho Tổng Miền Nam - TP.HCM")
                                        .status(WarehouseStatus.ACTIVE)
                                        .province(hcm)
                                        .employee(wh01)
                                        .build();
                        whHcm = warehouseRepository.save(whHcm);
                }

                if (whHcm != null) {
                        createLocationAndStockIfMissing(whHcm, "Kệ B-01 (Khu vực nhiệt độ mát)",
                                        "Vị trí lưu trữ bánh kem và tráng miệng", products, 150L);
                        createLocationAndStockIfMissing(whHcm, "Kệ B-02 (Khu vực đồ uống và trà)",
                                        "Vị trí lưu trữ trà Uji & Ô long", products, 100L);
                }

                // 3. Kho Trung Chuyển Miền Trung - Đà Nẵng
                Warehouse whDanang = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("Đà Nẵng"))
                                .findFirst().orElse(null);

                if (whDanang == null && danang != null && wh02 != null) {
                        whDanang = Warehouse.builder()
                                        .warehouseName("Kho Trung Chuyển Miền Trung - Đà Nẵng")
                                        .status(WarehouseStatus.ACTIVE)
                                        .province(danang)
                                        .employee(wh02)
                                        .build();
                        whDanang = warehouseRepository.save(whDanang);
                }

                if (whDanang != null) {
                        createLocationAndStockIfMissing(whDanang, "Kệ D-01 (Khu tập kết hàng Miền Trung)",
                                        "Vị trí phân phối nhanh cho các tỉnh lân cận", products, 90L);
                        createLocationAndStockIfMissing(whDanang, "Kệ D-02 (Khu vực xuất hàng nhanh)",
                                        "Vị trí chuẩn bị đóng gói giao hỏa tốc", products, 60L);
                }
        }

        private void createLocationAndStockIfMissing(Warehouse warehouse, String locationName, String description,
                        List<Product> products, Long stockQuantity) {
                boolean locationExists = locationRepository
                                .existsByWarehouseWarehouseIdAndLocationName(warehouse.getWarehouseId(), locationName);
                if (!locationExists) {
                        Location loc = Location.builder()
                                        .locationName(locationName)
                                        .description(description)
                                        .warehouse(warehouse)
                                        .build();
                        loc = locationRepository.save(loc);

                        for (Product p : products) {
                                LocationDetail detail = LocationDetail.builder()
                                                .location(loc)
                                                .product(p)
                                                .stockQuantity(stockQuantity)
                                                .build();
                                locationDetailRepository.save(detail);
                        }
                        log.info("   + Đã tạo vị trí {} tại {} (Mỗi SP: {} tồn kho)", locationName,
                                        warehouse.getWarehouseName(),
                                        stockQuantity);
                }
        }

        private void initDiscounts() {
                if (discountRepository.count() > 0) {
                        log.info("-> Khuyến mãi đã tồn tại, bỏ qua khởi tạo.");
                        return;
                }

                log.info("-> Đang khởi tạo Khuyến mãi & Chi tiết áp dụng sản phẩm (Discounts)...");
                List<Product> products = productRepository.findAll();
                if (products.isEmpty()) {
                        return;
                }

                // Discount 1: Giảm 20.000 VNĐ
                Discount discount1 = new Discount();
                discount1.setDescription("Khuyến mãi Mừng Khai Trương - Giảm 20.000 VNĐ cho bánh quy");
                discount1.setDiscountAmount(new BigDecimal("20000"));
                discount1.setStartDate(LocalDateTime.now().minusDays(5));
                discount1.setEndDate(LocalDateTime.now().plusDays(30));

                ProductDiscount pd1 = new ProductDiscount();
                pd1.setProduct(products.get(0));
                pd1.setApplicableQuantity(100L);
                discount1.addProductDiscount(pd1);

                if (products.size() > 1) {
                        ProductDiscount pd2 = new ProductDiscount();
                        pd2.setProduct(products.get(1));
                        pd2.setApplicableQuantity(50L);
                        discount1.addProductDiscount(pd2);
                }

                discountRepository.save(discount1);

                // Discount 2: Giảm 40.000 VNĐ
                Discount discount2 = new Discount();
                discount2.setDescription("Ưu đãi Combo Trà & Bánh Kem - Giảm 40.000 VNĐ");
                discount2.setDiscountAmount(new BigDecimal("40000"));
                discount2.setStartDate(LocalDateTime.now().minusDays(2));
                discount2.setEndDate(LocalDateTime.now().plusDays(45));

                if (products.size() > 2) {
                        ProductDiscount pd3 = new ProductDiscount();
                        pd3.setProduct(products.get(2));
                        pd3.setApplicableQuantity(80L);
                        discount2.addProductDiscount(pd3);
                }

                discountRepository.save(discount2);
                log.info("   + Đã tạo 2 chương trình khuyến mãi mẫu kèm chi tiết sản phẩm áp dụng.");
        }

        private void initOrders() {
                if (orderRepository.count() > 0) {
                        log.info("-> Đơn hàng mẫu đã tồn tại, bỏ qua khởi tạo.");
                        return;
                }

                log.info("-> Đang khởi tạo Đơn hàng mẫu & Chi tiết đơn hàng (Orders)...");

                Customer cust1 = customerRepository.findByEmail("customer@gmail.com").orElse(null);
                Customer cust2 = customerRepository.findByEmail("customer2@gmail.com").orElse(cust1);
                Customer cust3 = customerRepository.findByEmail("customer3@gmail.com").orElse(cust1);
                Customer cust4 = customerRepository.findByEmail("customer4@gmail.com").orElse(cust1);

                if (cust1 == null)
                        return;

                PaymentMethod cod = paymentMethodRepository.findAll().stream()
                                .filter(p -> p.getPaymentMethodName().contains("COD"))
                                .findFirst().orElse(null);
                PaymentMethod vnpay = paymentMethodRepository.findAll().stream()
                                .filter(p -> p.getPaymentMethodName().contains("VNPAY"))
                                .findFirst().orElse(cod);

                Warehouse whHanoi = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("Hà Nội"))
                                .findFirst().orElse(null);
                Warehouse whHcm = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("TP.HCM")
                                                || w.getWarehouseName().contains("Hồ Chí Minh"))
                                .findFirst().orElse(whHanoi);
                Warehouse whDanang = warehouseRepository.findAll().stream()
                                .filter(w -> w.getWarehouseName().contains("Đà Nẵng"))
                                .findFirst().orElse(whHanoi);

                Employee sale01 = employeeRepository.findByUsername("sale01").orElse(null);
                Employee admin = employeeRepository.findByUsername("admin").orElse(null);

                District caugiay = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Cầu Giấy"))
                                .findFirst().orElse(null);
                District quan1 = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Quận 1"))
                                .findFirst().orElse(null);
                District haichau = districtRepository.findAll().stream()
                                .filter(d -> d.getDistrictName().contains("Hải Châu"))
                                .findFirst().orElse(null);

                List<Product> products = productRepository.findAll();
                if (products.isEmpty())
                        return;

                // Đơn hàng 1: CHO_XAC_NHAN
                Order order1 = Order.builder()
                                .customer(cust1)
                                .recipientName("Nguyễn Văn Khách Hàng")
                                .recipientPhone("0987654321")
                                .email("customer@gmail.com")
                                .shippingAddress("Số 123 Đường Cầu Giấy, Quận Cầu Giấy, Hà Nội")
                                .district(caugiay)
                                .paymentMethod(cod)
                                .warehouse(whHanoi)
                                .employee(sale01 != null ? sale01 : admin)
                                .status(OrderStatus.CHO_XAC_NHAN)
                                .orderDate(LocalDateTime.now().minusHours(2))
                                .shippingFee(new BigDecimal("25000"))
                                .shippingCode("GHN_HN_1001")
                                .notes("Giao giờ hành chính, gọi trước khi đến")
                                .build();

                OrderDetail od1_1 = OrderDetail.builder()
                                .product(products.get(0))
                                .quantity(2L)
                                .priceAtPurchase(products.get(0).getUnitPrice())
                                .build();
                order1.addOrderDetail(od1_1);

                if (products.size() > 2) {
                        OrderDetail od1_2 = OrderDetail.builder()
                                        .product(products.get(2))
                                        .quantity(1L)
                                        .priceAtPurchase(products.get(2).getUnitPrice())
                                        .build();
                        order1.addOrderDetail(od1_2);
                }
                orderRepository.save(order1);

                // Đơn hàng 2: DA_XAC_NHAN
                Order order2 = Order.builder()
                                .customer(cust2)
                                .recipientName("Hoàng Thị Mai")
                                .recipientPhone("0912345678")
                                .email("customer2@gmail.com")
                                .shippingAddress("Số 45 Lê Duẩn, Quận 1, TP.HCM")
                                .district(quan1)
                                .paymentMethod(vnpay)
                                .warehouse(whHcm)
                                .employee(sale01 != null ? sale01 : admin)
                                .status(OrderStatus.DA_XAC_NHAN)
                                .orderDate(LocalDateTime.now().minusDays(1))
                                .shippingFee(new BigDecimal("30000"))
                                .shippingCode("VTP_SG_1002")
                                .notes("Đóng gói bọc xốp chống va đập giúp mình nhé")
                                .build();

                if (products.size() > 1) {
                        OrderDetail od2_1 = OrderDetail.builder()
                                        .product(products.get(1))
                                        .quantity(1L)
                                        .priceAtPurchase(products.get(1).getUnitPrice())
                                        .build();
                        order2.addOrderDetail(od2_1);
                }
                orderRepository.save(order2);

                // Đơn hàng 3: DANG_GIAO
                Order order3 = Order.builder()
                                .customer(cust3)
                                .recipientName("Đỗ Minh Tuấn")
                                .recipientPhone("0934567890")
                                .email("customer3@gmail.com")
                                .shippingAddress("Số 78 Bạch Đằng, Quận Hải Châu, Đà Nẵng")
                                .district(haichau)
                                .paymentMethod(cod)
                                .warehouse(whDanang)
                                .employee(sale01 != null ? sale01 : admin)
                                .status(OrderStatus.DANG_GIAO)
                                .orderDate(LocalDateTime.now().minusDays(2))
                                .shippingFee(new BigDecimal("20000"))
                                .shippingCode("JNT_DN_1003")
                                .notes("Giao tận tay người nhận, gọi trước 15 phút")
                                .build();

                OrderDetail od3_1 = OrderDetail.builder()
                                .product(products.get(0))
                                .quantity(1L)
                                .priceAtPurchase(products.get(0).getUnitPrice())
                                .build();
                order3.addOrderDetail(od3_1);
                orderRepository.save(order3);

                // Đơn hàng 4: THANH_CONG
                Order order4 = Order.builder()
                                .customer(cust1)
                                .recipientName("Nguyễn Văn Khách Hàng")
                                .recipientPhone("0987654321")
                                .email("customer@gmail.com")
                                .shippingAddress("Số 123 Đường Cầu Giấy, Quận Cầu Giấy, Hà Nội")
                                .district(caugiay)
                                .paymentMethod(cod)
                                .warehouse(whHanoi)
                                .employee(admin)
                                .status(OrderStatus.THANH_CONG)
                                .orderDate(LocalDateTime.now().minusDays(5))
                                .shippingFee(new BigDecimal("25000"))
                                .shippingCode("GHN_HN_1000")
                                .notes("Đã giao thành công, khách khen bánh rất ngon")
                                .build();

                OrderDetail od4_1 = OrderDetail.builder()
                                .product(products.get(0))
                                .quantity(3L)
                                .priceAtPurchase(products.get(0).getUnitPrice())
                                .build();
                order4.addOrderDetail(od4_1);
                orderRepository.save(order4);

                log.info("   + Đã tạo 4 đơn hàng mẫu với các trạng thái: CHO_XAC_NHAN, DA_XAC_NHAN, DANG_GIAO, THANH_CONG.");
        }
}
