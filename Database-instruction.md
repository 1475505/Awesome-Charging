# Database instruction

## 在这里对db里的数据进行说明

### users

| 数据       | 格式      | 内容 | 说明          |
|----------|---------|--|-------------|
| id       | BIGINT  | 整数 | user的id(主键) |
| car_id   | VARCHAR | 字符串 | 这里保存用户汽车的车牌号 |
| password | VARCHAR | 字符串 | 用户密码 |      
| username | VARCHAR | 字符串 | 用户名 |


### admins

| 数据         | 格式      | 内容  | 说明           |
|------------|---------|-----|--------------|
| id         | BIGINT  | 整数  | admin的id(主键) |
| password   | VARCHAR | 字符串 | 管理员密码        |      
| admin_name | VARCHAR | 字符串 | 管理员用户名       |

### requests（充电请求）

| 数据                | 格式       | 内容      | 说明                         |
|-------------------|----------|---------|----------------------------|
| id                | BIGINT   | 整数      | request的id(主键)             |
| car_id            | VARCHAR  | 字符串     | 管理员密码                      |      
| car_position      | TINYINT  | 整数      | 管理员用户名                     |
| done_amount       | DOUBLE   | 浮点数     | 已经冲进去了多少度                  |
| request_amount    | DOUBLE   | 浮点数     | 充电量                        |   
| request_mod       | TINYINT  | 整数（1或2） | 表示快慢充（大组文档中这里的变量类型是string） | 
| succ_reps         | VARCHAR  | 字符串     | <font size="10">（暂时不知道，等亮神救一下）</font><br />         |
| created_at        | DATETIME | 时间戳     | 订单创建的时间戳                   |
| status            | TINYINT  | 整数      | <font size="10">（暂时不知道，等亮神救一下）</font><br />         |      
| end_charge_time   | DATETIME | 时间戳     | 结束充电的时间戳                   |
| start_charge_time | DATETIME | 时间戳     | 开始充电的时间戳                   |

### queues(队列)
| 数据           | 格式      | 内容  | 说明               |
|--------------|---------|-----|------------------|
| id           | BIGINT  | 整数  | queues的id(主键)    |
| queue_id     | VARCHAR | 字符串 | queue的id(用于代码实现) |      
| waiting_cars | VARCHAR | 字符串 | 等待车辆队列           |

### piles（充电桩实体）

| 数据                | 格式       | 内容                              | 说明                                          |
|-------------------|----------|---------------------------------|---------------------------------------------|
| id                | BIGINT   | 整数                              | piles的id(主键)                                |
| cars              | VARCHAR  | 字符串                             | 充电桩对应的等待队列                                  |      
| fee_pattern       | VARCHAR  | 字符串                             | 计费模式                                        |
| peak_price        | DOUBLE   | 浮点数                             | 峰值价格                                        |
| pile_id           | VARCHAR  | 字符串                             | piles的id(代码实现)                              |   
| serve_price       | DOUBLE   | 浮点数                             | 服务价格                                        | 
| status            | ENUM     | 枚举（CHARGING,ERROR,ON,UNRUNNING） | 四个状态                                        |
| total_capacity    | INT      | 整数                              | 等待队列的长度                                     |
| total_charge_num  | INT      | 整数                              | <font size="10">（暂时不知道，等亮神救一下）</font><br /> |      
| total_charge_time | INT      | 整数                              | 总共充电时间                                      |
| usual_price       | DOUBLE   | 浮点数                             | 常时电价                                        |
| valley_price      | DOUBLE   | 浮点数                             | 谷时电价                                        |      
