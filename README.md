# 智能充电桩管理系统

文档：

[作业文档](https://xydchcnnf8.feishu.cn/drive/folder/fldcnug1JMxcVQkgOoCPPwrz0Ch)

[接口文档](https://fsbupteducn.feishu.cn/docx/Hg3Tdv3N5oV8gxx5q8Jc6eYWn8e)

[常见问题和规定](https://xydchcnnf8.feishu.cn/docx/TulYdfNEWokTFjx9xm5cKUrLnYd)

[自定义接口](https://fsbupteducn.feishu.cn/docx/DGNidirGvocoDDxQCWRcuAAdn7b)

---

## 后端开发TODO

### TODOnow

### TODOgugu

登录接口缺少用户登录态维护：懒得做鉴权。还不好debug，直接不验证了。

### 客户端

- [x] 登录接口
- [x] 注册接口

---

- [x] 提交充电请求接口
- [x] 处理充电请求

---

- [x] 修改充电量
- [x] 修改充电模式
- [x] 处理修改请求

---

- [x] 查看（车辆处于的）队列状态
- [x] 缺少对剩余时间的计算

---

- [x] 开始充电
- [x] 查看充电状态（订单信息）
- [x] 结束充电
- [x] 接口写了，需要调度来修改状态

---

- [x] 查看账单接口
- [x] 查看详单接口（补充）
- [x] 导出详单和账单，思路：详单每次以追加方式加入文件中，文件需要指定位置，表头为("车辆编码", "详单编号", "详单生成时间", "
  充电桩编号", "充电电量", "充电时长", "启动时间", "停止时间", "充电费用(元)", "服务费(元),总费用(元)") 。
- [x] 账单导出
- [x] 没测，表没有数据

---

- [x] 登出

### 管理员端

- [x] 管理员使用一个按钮初始化数据库（新增需求）
- [x] 管理员登录
- [x] 管理员登出
- [x] 启动充电桩
- [x] 运行充电桩(阉割了，没用)
- [x] 关闭充电桩
- [x] 使充电桩宕机

---

- [x] 设置充电桩参数(计费规则,高峰时段单价,平时单价, 谷时单价,服务费用单价)

---

- [x] 查看充电桩状态
- [x] 查看指定充电桩队列状态

---

- [ ] ~~生成报表~~


