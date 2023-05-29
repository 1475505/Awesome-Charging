# Database instruction

## 在这里对db里的数据进行说明

### users

| 数据       | 格式      | 内容   | 说明          |
|----------|---------|------|------------|
| id       | BIGINT  | 整数   | user的id(主键) |
| car_id   | VARCHAR | 字符串  | 这里保存用户汽车的车牌号 |
| password | VARCHAR | 字符串  | 用户密码 |      
| username | VARCHAR | 字符串  | 用户名 |


### admins

| 数据         | 格式      | 内容  | 说明           |
|------------|---------|-----|--------------|
| id         | BIGINT  | 整数  | admin的id(主键) |
| password   | VARCHAR | 字符串 | 管理员密码        |      
| admin_name | VARCHAR | 字符串 | 管理员用户名       |

### requests（充电请求）

| 数据                | 格式       | 内容      | 说明                                                                                                                                                                                  |
|-------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id                | BIGINT   | 整数      | request的id(主键)                                                                                                                                                                      |
| car_id            | VARCHAR  | 字符串     | 车牌号                                                                                                                                                                                 |
| car_position      | TINYINT  | 整数      | 当前汽车在队列中的位置                                                                                                                                                                         |
| done_amount       | DOUBLE   | 浮点数     | 已经冲进去了多少度                                                                                                                                                                           |
| request_amount    | DOUBLE   | 浮点数     | 充电量                                                                                                                                                                                 |
| request_mod       | TINYINT  | 整数（1或2） | 表示快慢充（大组文档中这里的变量类型是string）                                                                                                                                                          |
| succ_reps         | VARCHAR  | 字符串     | <br/>本次订单可能未完成，比如被后续调度等。<br/>这种情况可以创建个新请求，然后从这个字段获取后续的情况。<br/>比如：<br/>id = 1，要充5度电，已充2度，然后充电桩寄了<br/>后面调度可以建个补偿请求，若id=3，设置充3度电。<br/>然后id=1的succReqs增加元素3.<br/>*这个组长怎么用，我们就怎么用，我们是小粉红 |
| created_at        | DATETIME | 时间戳     | 订单创建的时间戳                                                                                                                                                                            |
| status            | TINYINT  | 整数      | 这个请求的状态：<br />public enum Status *{<br/>\*    *INIT*, //0, 表示这个请求尚未被处理<br/>    *DOING*, //1，正在处理<br/>    *DONE*,  //2，处理完了<br/>    *OTHER*  //3<br/>*}*                             |
| end_charge_time   | DATETIME | 时间戳     | 结束充电的时间戳                                                                                                                                                                            |
| start_charge_time | DATETIME | 时间戳     | 开始充电的时间戳                                                                                                                                                                            |

### queues(队列)
| 数据           | 格式      | 内容  | 说明               |
|--------------|---------|-----|------------------|
| id           | BIGINT  | 整数  | queues的id(主键)    |
| queue_id     | VARCHAR | 字符串 | queue的id(用于代码实现) |      
| waiting_cars | VARCHAR | 字符串 | 等待车辆队列           |

### piles（充电桩实体）

| 数据              | 格式    | 内容                       | 说明                                                      |
| ----------------- | ------- |--------------------------| --------------------------------------------------------- |
| id                | BIGINT  | 整数                       | piles的id(主键)                                           |
| cars              | VARCHAR | 字符串                      | 充电桩对应的等待队列                                      |
| fee_pattern       | VARCHAR | 字符串                      | 计费模式                                                  |
| peak_price        | DOUBLE  | 浮点数                      | 峰值价格                                                  |
| pile_id           | VARCHAR | 字符串                      | 反正就是充电桩的名字，比如“龙神纳西妲六号桩”                                       |
| serve_price       | DOUBLE  | 浮点数                      | 服务价格                                                  |
| status            | ENUM    | 枚举（ERROR,OFF,UNRUNNING,CHARGING） | 四个状态                                                  |
| total_capacity    | INT     | 整数                       | 等待队列的长度                                            |
| total_charge_num  | INT     | 整数                       | 完成过多少次充电请求 |
| total_charge_time | INT     | 整数                       | 总共充电时间                                              |
| usual_price       | DOUBLE  | 浮点数                      | 常时电价                                                  |
| valley_price      | DOUBLE  | 浮点数                      | 谷时电价                                                  |
