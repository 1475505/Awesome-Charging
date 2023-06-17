
# Database instruction

## FAQ

1. requests的doneAmount最终一定会和requestAmount一致吗？

需要分两种情况。
- succReqs不为空，说明产生故障，后续的请求会被创建，进行补偿。
- status=CANCELED，说明用户在等候区停止充电。这个是可以忽略的。

## 在这里对db里的数据进行说明

主要还是看代码注释。

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

| 数据                | 格式       | 内容     | 说明                                                                                                                                                                                   |
|-------------------|----------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id                | BIGINT   | 整数     | request的id(主键)                                                                                                                                                                       |
| car_id            | VARCHAR  | 字符串    | 车牌号                                                                                                                                                                                  |
| bill_id           | BIGINT   | 整数     | 对应的详单ID                                                                                                                                                                              |
| done_amount       | DOUBLE   | 浮点数    | 已经冲进去了多少度                                                                                                                                                                            |
| request_amount    | DOUBLE   | 浮点数    | 总共需要的充电量                                                                                                                                                                             |
| request_mode      | TINYINT  | 枚举充电模式 | public enum RequestMode *{<br/>\*    *UNSET*, *FAST*, *SLOW<br/>**}*                                                                                                                 |
| succ_reps         | VARCHAR  | 字符串    | <br/>本次订单可能未完成，比如故障被后续调度等。<br/>这种情况可以创建个新请求，然后从这个字段获取后续的情况。<br/>比如：<br/>id = 1，要充5度电，已充2度，然后充电桩寄了<br/>后面调度可以建个补偿请求，若id=3，设置充3度电。<br/>然后id=1的succReqs增加元素3.<br/>这个组长怎么用，我们就怎么用，我们是小粉红 |
| created_at        | DATETIME | 时间戳    | 订单创建的时间戳                                                                                                                                                                             |
| status            | TINYINT  | 整数     | 这个请求的状态：<br />public enum Status *{<br/>\*    *INIT*, //0, 表示这个请求尚未被处理<br/>    *DOING*, //1，正在处理<br/>    *DONE*,  //2，处理完了<br/>    *CANCELED*  //3<br/>*}*                           |
| end_charge_time   | DATETIME | 时间戳    | 结束充电的时间戳                                                                                                                                                                             |
| start_charge_time | DATETIME | 时间戳    | 开始充电的时间戳                                                                                                                                                                             |

### queues(队列)
| 数据           | 格式      | 内容  | 说明               |
|--------------|---------|-----|------------------|
| id           | BIGINT  | 整数  | queues的id(主键)    |
| queue_id     | VARCHAR | 字符串 | queue的id(用于代码实现) |
| waiting_cars | VARCHAR | 字符串 | 等待车辆队列           |
| capacity     | int     |     | 队列长度上限           |

### piles（充电桩实体）

| 数据                | 格式      | 内容                                                                                            | 说明                             |
|-------------------|---------|-----------------------------------------------------------------------------------------------|--------------------------------|
| id                | BIGINT  | 整数                                                                                            | piles的id(主键)                   |
| capacity          | int     |                                                                                               | 队列长度上限                         |
| carQueue          | VARCHAR | 字符串                                                                                           | 充电桩对应的等待队列。第一个是充电中的车辆，后面是等待的车辆 |
| fee_pattern       | VARCHAR | 字符串                                                                                           | 计费模式                           |
| peak_price        | DOUBLE  | 浮点数                                                                                           | 峰值价格                           |
| pile_id           | VARCHAR | 字符串                                                                                           | 反正就是充电桩的名字，比如“龙神纳西妲六号桩”        |
| serve_price       | DOUBLE  | 浮点数                                                                                           | 服务费价格                          |
| status            | ENUM    | 枚举（ERROR,OFF,FREE,CHARGING）                                                                   | 四个状态                           |
| mode              |         | private enum Mode *{<br/>\*    *F*, // 快充<br/>    *T* // 这个叫慢充，不知道是哪个单词，反正是慢充。对应老师的文档<br/>*}* | 这是个快充还是慢充桩F/T                  |
| total_capacity    | INT     | 整数                                                                                            | 已经完成过多少次充电                     |
| total_charge_num  | INT     | 整数                                                                                            | 完成过多少次充电请求                     |
| total_charge_time | INT     | 整数                                                                                            | 总共充电时间                         |
| usual_price       | DOUBLE  | 浮点数                                                                                           | 常时电价                           |
| valley_price      | DOUBLE  | 浮点数                                                                                           | 谷时电价                           |

### cars
| 数据             | 格式      | 内容  | 说明                                                                                                                                                                                      |
|----------------|---------|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id             | BIGINT  | 整数  | cars的id(主键)                                                                                                                                                                             |
| area           | TINYINT | 整数  | 车所在区域<br />public enum Area *{<br/>\*    *COMPLETED*, //0,表示没来充电<br/>    *WAITING*,  //1<br/>    *CHARGING*, //2<br/>    *OTHER*;  //3                                                  |
| car_id         | VARCHAR | 字符串 | 车辆的名称(用于代码实现)                                                                                                                                                                           |
| handing_req_id | BIGINT  | 整数  | 目前如果正在处理的充电请求，对应的ID（主键）                                                                                                                                                                 |
| pile_id        | VARCHAR | 字符串 | 目前如果在充电区，在哪个pile（名称，不是主键）                                                                                                                                                               |
| queue          | TINYINT | 整数  | 队列状态。<br />public enum Queue *{<br/>\*    *UNQUEUED*, //0,表示没来充电<br/>    *WAITING*,  //等待队列<br/>    *CHARGING*, //充电队列<br/>    *OTHER*  //3<br/>*}*                                     |
| queue_no       | VARCHAR | 字符串 | 目前如果在队列，队列号，对应queues表的id（非主键）                                                                                                                                                           |
| status         | TINYINT | 枚举  | 车状态<br />public enum Status *{<br/>\*    *COMPLETED*, //0<br/>    *waiting*,  //1<br/>    *pending*,  //2，可能是等待叫号回应。这个设计不一定用上<br/>    *charging*, //3<br/>    *OTHER*  //4<br/>*}<br/>* |