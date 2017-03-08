# BKStorage
## Chức năng
Đồng bộ dữ liệu giữa các thiết bị của người dùng, đảm bảo an toàn cho dữ liệu, không bị mất khi thiết gặp phải sự cố, hoặc những tình huống đáng tiếc xảy ra.
## Yêu cầu
- Java SE Runtime Environment 8 hoặc mới hơn

## Kiến trúc
![architecture pic](https://uphinhnhanh.com/images/2017/03/08/architecture.png)
- Client (các thiết bị của người dùng) kết nối đến Load balancer quan mạng Internet. Truy vấn thời gian của hệ thống thông qua TimeServer.
- Load Balancer: Thực hiện cân bằng tải: chuyển các request từ client đến server tương ứng.
- TimeServer: Các đồng hồ trong hệ thống sẽ được đồng bộ theo đồng hồ của máy chủ này.
- StorageServer: Các máy chủ lưu trữ dữ liệu của người dùng.
- DBServer: CSDL của hệ thống.
- MessageQueue: Hàng đợi thông điệp, dùng để giao tiếp giữa các máy chủ
