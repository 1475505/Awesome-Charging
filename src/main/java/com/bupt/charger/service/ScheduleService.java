package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.util.Estimator;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class ScheduleService {
    // note: 将所有充电区的队列放在pile的queue里面，等候区的才放在ChargingQueue里面
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

    @Autowired
    private PilesRepository pilesRepository;


    @Autowired
    private Estimator estimator;
    static int fSumNumber = 0;
    static int tSumNumber = 0;

    // 是否是否等候区叫号服务
    static boolean isStopWaitArea = false;

    // 添加到等候区队列，返回分配的号码
    public String moveToWaitingQueue(Car car) {
        // 根据类型查询等待区的队列
        ChargeRequest carRequest = chargeReqRepository.findTopByCarIdAndStatusOrderByCreatedAtDesc(car.getCarId(), ChargeRequest.Status.DOING);
        // 查看充电类型
        String queueId = "";
        String res = "";
        if (carRequest.getRequestMode() == ChargeRequest.RequestMode.SLOW) {
            queueId = "T";
            res = queueId + (++fSumNumber);

        } else if (carRequest.getRequestMode() == ChargeRequest.RequestMode.FAST) {
            queueId = "F";
            res = queueId + (++tSumNumber);
        } else {
            log.info("充电请求的充电类型错误");
            // 错误处理
            return null;
        }
        ChargingQueue waitQueue = chargingQueueRepository.findByQueueId(queueId);
        // 添加到相应的位置,
        if (waitQueue.addWaitingCar(car.getCarId())) {
            // 设置车辆状态,车辆的queueNo状态也需要更改
            car.setStatus(Car.Status.waiting);
            car.setArea(Car.Area.WAITING);
            car.setQueueNo(waitQueue.getQueueId());
            // 保存到数据库
            chargingQueueRepository.save(waitQueue);
            carRepository.save(car);
        } else {
            throw new ApiException("等候区已经爆满不可进入");
        }
        // 调用调度函数
        moveToChargingQueue();
        return res;
    }

    // 获得有空余的充电队列
    public List<Pile> getChargingNotFullQueue(Pile.Mode mode) {
        // 根据模式检测相应充电区的充电队列是否有空余，有则添加
        List<Pile> res = new ArrayList<>();
        String prefixId = "";
        int pileSum = 0;
        if (mode == Pile.Mode.F) {
            prefixId = "CF";
            // TODO: 读取配置文件获取快充个数
            pileSum = 2;
        } else if (mode == Pile.Mode.T) {
            prefixId = "CT";
            // TODO: 读取配置文件获取快充个数
            pileSum = 3;
        } else {
            log.info("充电类型错误");
            return null;
        }

        for (int i = 0; i < pileSum; i++) {
            String chargingQueueId = prefixId + (char) ('A' + i);
            Pile pile = pilesRepository.findByPileId(chargingQueueId);
            //    如果没有达到容量就是有空余
            if (pile.getQCnt() < pile.getCapacity()) {
                res.add(pile);
            }
        }
        return res;
    }

    // 提醒车辆开始充电函数
    public boolean remindCarStartCharge(String pileId) {
        // TODO: 调用这个函数的情况：1. 空队列进来了新车(即最短时间调度函数检测到这个事件) 2. 有车辆结束充电，通知下一个。

        //     如果队列里面没有车辆，那么就不需要提醒；不然提醒指定队列的第一辆车开始充电
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile.getQCnt() == 0) {
            return false;
        }
        String carId = pile.getQList().get(0);
        //    TODO:通知前端指定车辆

        return true;
    }


    // 进入充电区
    public String moveToChargingQueue() {
        // TODO: 以下情况会调用这个函数：1. 有车辆发送结束充电请求 2. 刚进入等候区（因为这个时候可能有多个充电队列空余） 3. 提交故障请求时，因为可能其他多个充电队列空余，但故障仅发生在有车充电的充电桩里面了(TODO) 4. 充电桩故障完恢复上线时(暂时没有处理,TODO)

        // 获取两个模式下的空余队列
        List<Pile> fastPiles = getChargingNotFullQueue(Pile.Mode.F);
        List<Pile> slowPiles = getChargingNotFullQueue(Pile.Mode.T);

        if (fastPiles != null && fastPiles.size() > 0) {
            return basicSchedule(fastPiles, Pile.Mode.F);
        }

        if (slowPiles != null && slowPiles.size() > 0) {
            return basicSchedule(slowPiles, Pile.Mode.T);
        }

        return null;

    }

    // 将指定车辆从等候区移除
    public void removeFromWaitingQueue(String carId, ChargeRequest.RequestMode oldMode) {
        String oldQueueId;
        if (oldMode == ChargeRequest.RequestMode.FAST) {
            oldQueueId = "F";
        } else if (oldMode == ChargeRequest.RequestMode.SLOW) {
            oldQueueId = "T";
        } else {
            log.info("移除等候区错误");
            return;
        }
        ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(oldQueueId);
        chargingQueue.removeWaitingCar(carId);
        chargingQueueRepository.save(chargingQueue);
    }

    // 基本调度策略
    public String basicSchedule(List<Pile> piles, Pile.Mode mode) {
        String suffixId;
        if (mode == Pile.Mode.T) {
            suffixId = "T";
        } else if (mode == Pile.Mode.F) {
            suffixId = "F";
        } else {
            log.info("调度策略错误");
            return null;
        }

        String waitQueueId;
        if (isStopWaitArea) {
            waitQueueId = "Error" + suffixId;
        } else {
            waitQueueId = suffixId;
        }

        // 从等候区中寻找和这个充电桩充电模式匹配的队列，然后将第一个车辆调度过来
        ChargingQueue waitQueue = chargingQueueRepository.findByQueueId(waitQueueId);

        // 从等待区移走
        String topCarId = waitQueue.consumeWaitingCar();
        if (topCarId != null && topCarId.equals("")) {
            Car car = carRepository.findByCarId(topCarId);
            // 执行调度策略
            // 无论是故障调度还是基本调度都是从一个等候队列到一个充电队列,选择时间最短的充电队列
            // 如果只有一个空闲队列，那么就直接调度到这个队列
            Pile resPile;
            if (piles.size() == 1) {
                resPile = piles.get(0);
            } else {
                // 计算每个队列的充电总时间，选择总电量最少的那个充电桩
                ArrayList<Duration> leftTimeList = new ArrayList<>();
                for (Pile pile : piles) {
                    //    获取队列中所有车辆
                    List<String> carList = pile.getQList();
                    //    计算所有车辆充完电需要的总时间
                    Duration sumDuration = Duration.ZERO;
                    for (int i = 0; i < carList.size(); i++) {
                        if (i == 0) {
                            Car chargingCar = carRepository.findByCarId(carList.get(i));
                            //    检查这个车辆是否在充电中
                            if (chargingCar.getStatus() == Car.Status.charging) {
                                sumDuration = sumDuration.plus(estimator.estimateCarLeftChargeTime(chargingCar.getCarId()));
                            } else {
                                sumDuration = sumDuration.plus(estimator.estimateCarChargeTime(chargingCar.getCarId()));
                            }
                        } else {
                            sumDuration = sumDuration.plus(estimator.estimateCarChargeTime(carList.get(i)));
                        }
                    }
                    leftTimeList.add(sumDuration);
                }
                //    获取最小时间
                int minIndex = 0;
                Duration minDuration = leftTimeList.get(0);
                for (int i = 0; i < leftTimeList.size(); i++) {
                    if (leftTimeList.get(i).compareTo(minDuration) < 0) {
                        minIndex = i;
                    }
                }
                resPile = piles.get(minIndex);
            }

            resPile.addCar(car.getCarId());

            // 设置车辆状态
            car.setStatus(Car.Status.waiting);
            car.setArea(Car.Area.CHARGING);
            car.setQueueNo(resPile.getPileId());
            car.setPileId(resPile.getPileId());

            //    保存
            pilesRepository.save(resPile);
            carRepository.save(car);
            chargingQueueRepository.save(waitQueue);

            // 如果这个队列加上新加的也只有一个，那么就通知车辆
            if (resPile.getQCnt() == 1) {
                //    通知车辆
                remindCarStartCharge(resPile.getPileId());
            }
            return resPile.getPileId();
        }
        return null;
    }

/*  TODO: 获取故障上报请求,也可管理员端直接进行,暂停正在充电的车辆,同时转移队列到故障队列
    注意一个问题: 优先级调度就是将对应的原充电桩队列转移到相应模式的故障队列,但是时间顺序队列需要将同类型的所有没在充电的车辆全部汇集到故障队列里面,同时需要按照车辆排队号码进行排序,数字越大越靠后.汇聚之后将原来的队列清空,因为我们有实时检测是否有队列空的,自然就会从故障队列里面加回去
 */

}
