# 后端开发TODO

此分支暂不测试故障恢复部分。

**目前的代码，可能缺少时间加速**

# 代码FAQ

### websocket

后端使用方式：
`taskService.scheduleTask(carId, estimator.estimateCarChargeTime(carId), "你的电电应该充满啦~");
`
。scheduleTask方法中的第二个参数重载有两种方法，一种是物理真实时间戳（别注册模拟的假时间），一种是duration之后。第三个参数是发送给前端的内容。

前端监听`ws://{BASE_URL}/ws/{user_name}`

![](http://img.070077.xyz/202306011614301.png)



## 处理充电请求

#### 进行返回中预估等待时间的计算

相关代码： com/bupt/charger/util/Estimator.java


已经写了对车的充电剩余时间、预估等待时间的计算，但是： 
- **在等待区时间的计算**：不只有一个充电桩，不用等所有车充电，而是部分。这里的操作是，求和，除以充电桩数（TODO，加宇设定）。
- **在充电区时间的计算**：除了等待区，充电区里也有车，这些车还在充电或等待充电，一样需要加上这些预估的时间。

#### 调度

**增加对所在充电桩的库表维护，请review！**

唤醒时需要处理两种情形：

1. 如果直接有空位，websocket后直接进去。
2. 等待其他函数将队列消费出队。比如：某车结束充电，可以直接作为函数调用，传其队列中新头部为入参。


> 可以直接设等待websocket用户中的车的状态为charging，队列保持在队头即可。我想着应该没有产生负面影响。如果有bug，当时设计时有个pending状态也可以利用。
> 
> 因为startTime就是在【开始充电（插电）】请求后设置的，不会提前计费。
> 
> 当然，websocket记得响应嗷


---

## 调度充电状态

#### 开始充电

1. 正在充电的车辆保持为pile.car_queue中的第一个。
2. 

#### 结束充电

相关代码：com/bupt/charger/util/Calculator.java

计费，小活。

1. **在等候区结束充电，需要释放相关队列**
2. 唤醒调度程序，调度下一辆车。


---

## 处理修改请求


---