package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.repository.PilesRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class ScheduleService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

    @Autowired
    private PilesRepository pilesRepository;

    static int fSumNumber = 0;
    static int tSumNumber = 0;

    // 是否是否等候区叫号服务
    static boolean isWaitArea = false;

    // 添加到等候区队列，返回分配的号码
    public String moveToWaitingQueue(Car car) {
        // 设置车辆状态
        car.setStatus(Car.Status.waiting);
        car.setArea(Car.Area.WAITING);
        carRepository.save(car);
        // 根据类型查询等待区的队列
        ChargeRequest carRequest = chargeReqRepository.findTopByCarIdAndStatusOrderByCreatedAtDesc(car.getCarId(), ChargeRequest.Status.DOING);
        // 查看充电类型
        String queueId = "";
        String res = "";
        if (carRequest.getRequestMode() == ChargeRequest.RequestMode.SLOW) {
            // 查看该类型下等候区队列车辆有多少
            queueId = "T";
            res = queueId + (++fSumNumber);

        } else if (carRequest.getRequestMode() == ChargeRequest.RequestMode.FAST) {
            queueId = "F";
            res = queueId + (++tSumNumber);
        } else {
            log.info("充电类型错误");
            // TODO: 错误处理
        }
        ChargingQueue waitQueue = chargingQueueRepository.findByQueueId(queueId);
        // 添加到相应的位置
        waitQueue.addWaitingCar(car.getCarId());
        // 保存到数据库
        chargingQueueRepository.save(waitQueue);
        return res;
    }

    // TODO: 检查充电桩队列是否存在空位,进行移进队列，需要实时检查，可以开辟额外线程
    public void isChargingQueueHasEmpty(String queueId) {
        // 遍历所有充电桩的队列，查看是否有空位
        // TODO: 读取配置文件获取各个充电桩的个数
        int fastChargerNumber = 2;
        int slowChargerNumber = 3;

        // 是否需要移入充电区域
        boolean fIsNeedMove = false;
        boolean tIsNeedMove = false;
        // 检查快充充电桩
        for (int i = 0; i < fastChargerNumber; i++) {
            String chargingQueueId = "CF" + (char) ('A' + i);
            ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(chargingQueueId);
            if (chargingQueue.getWaitingCarCnt() < chargingQueue.getCapacity()) {
                fIsNeedMove = true;
                break;
            }
        }

        // 检查慢充充电桩
        for (int i = 0; i < slowChargerNumber; i++) {
            String chargingQueueId = "CT" + (char) ('A' + i);
            ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(chargingQueueId);
            if (chargingQueue.getWaitingCarCnt() < chargingQueue.getCapacity()) {
                tIsNeedMove = true;
                break;
            }
        }

        if (tIsNeedMove) {
            if (isWaitArea) {
                //    执行基本调度
                moveToChargingQueue("T");
            } else {
                //    执行故障调度
                moveToChargingQueue("ErrorT");
            }
        }

        if (fIsNeedMove) {
            if (isWaitArea) {
                moveToChargingQueue("F");
            } else {
                moveToChargingQueue("ErrorF");
            }
        }

        if (chargingQueueRepository.findByQueueId("ErrorT").getWaitingCarCnt() == 0 && chargingQueueRepository.findByQueueId("ErrorF").getWaitingCarCnt() == 0) {
            isWaitArea = true;
        }
    }

    // 进入充电区
    public void moveToChargingQueue(String waitQueueId) {

        ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(waitQueueId);
        String topCarId = chargingQueue.getTopCarId();
        Car car = carRepository.findByCarId(topCarId);
        // 设置车辆状态
        car.setStatus(Car.Status.waiting);
        car.setArea(Car.Area.CHARGING);
        carRepository.save(car);
        if (topCarId != null && topCarId.equals("")) {
            //    执行调度策略
            // 从等待区移走
            chargingQueue.consumeWaitingCar();
            chargingQueueRepository.save(chargingQueue);
            // 无论是故障调度还是基本调度都是从一个等候队列到一个充电队列,选择时间最短的充电队列
            String assignQueueId = basicSchedule(topCarId, waitQueueId.endsWith("F") ? ChargeRequest.RequestMode.FAST : ChargeRequest.RequestMode.SLOW);
            //    TODO: 可以通知具体的车辆,也可以不通知

        }
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
    public String basicSchedule(String carId, ChargeRequest.RequestMode mode) {
        //    被调度车辆完成充电所需时长最短进行选择空闲充电桩
        //    筛选空闲充电桩
        char midChar;
        if (mode == ChargeRequest.RequestMode.FAST) {
            midChar = 'F';
        } else if (mode == ChargeRequest.RequestMode.SLOW) {
            midChar = 'T';
        } else {
            //    出现错误BUG
            log.info("调度策略-充电传入类型错误");
            return null;
        }
        //    获取空闲充电桩
        int pileNum;
        if (midChar == 'F') {
            // TODO: 读取配置获得快充个数
            pileNum = 2;
        } else {
            // TODO: 配置获取慢充个数
            pileNum = 3;
        }
        // 空闲队列
        ArrayList<ChargingQueue> queueHaveEmpty = new ArrayList<>();
        for (int i = 0; i < pileNum; i++) {
            String queueId = "C" + midChar + (char) ('A' + i);
            ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(queueId);
            if (chargingQueue.getWaitingCarCnt() < chargingQueue.getCapacity()) {
                queueHaveEmpty.add(chargingQueue);
            }
        }

        //    获取所有空闲队列中所有车辆充完电需要的时间
        // 如果只有一个空闲队列,那么就是这个了
        if (queueHaveEmpty.size() == 1) {
            ChargingQueue chargingQueue = queueHaveEmpty.get(0);
            //    将车辆加入到该充电桩的队列中
            chargingQueue.addWaitingCar(carId);
            //    保存到数据库
            chargingQueueRepository.save(chargingQueue);
            return chargingQueue.getQueueId();
        }
        // 这里正在充电的车辆直接是在队列第一个。
        ArrayList<Double> amountList = new ArrayList<>();
        for (ChargingQueue chargingQueue : queueHaveEmpty) {
            //    获取队列中所有车辆
            List<String> carList = chargingQueue.getWaitingCarsList();
            //    计算所有车辆充完电需要的总电量
            double sumAmount = 0;
            for (String car : carList) {
                //    获取该车辆的充电请求
                ChargeRequest chargeRequest = chargeReqRepository.findTopByCarIdAndStatusOrderByCreatedAtDesc(car, ChargeRequest.Status.DOING);
                //    计算该车辆充完电需要的时间
                sumAmount += chargeRequest.getRequestAmount();
            }
            amountList.add(sumAmount);
        }

        //    获取最小电量
        double minAmount = Double.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < amountList.size(); i++) {
            if (amountList.get(i) < minAmount) {
                minAmount = amountList.get(i);
                minIndex = i;
            }
        }

        //    获取最小时间对应的充电桩
        ChargingQueue chargingQueue = queueHaveEmpty.get(minIndex);
        //    将车辆加入到该充电桩的队列中
        chargingQueue.addWaitingCar(carId);

        // 刘亮乱改的，不知道对不对：
        Car car = carRepository.findByCarId(carId);
        car.setArea(Car.Area.CHARGING);
        car.setQueue(Car.Queue.CHARGING);
        car.setStatus(Car.Status.charging);
        String pileId = chargingQueue.getQueueId().substring(2); //是这么获取吗？
        car.setPileId(pileId);
        Pile pile = pilesRepository.findByPileId(pileId);
        pile.addCar(carId);
        pilesRepository.save(pile);

        //    保存到数据库
        chargingQueueRepository.save(chargingQueue);
        //    返回充电桩的队列号
        return chargingQueue.getQueueId();

    }

/*  TODO: 获取故障上报请求,也可管理员端直接进行,暂停正在充电的车辆,同时转移队列到故障队列
    注意一个问题: 优先级调度就是将对应的原充电桩队列转移到相应模式的故障队列,但是时间顺序队列需要将同类型的所有没在充电的车辆全部汇集到故障队列里面,同时需要按照车辆排队号码进行排序,数字越大越靠后.汇聚之后将原来的队列清空,因为我们有实时检测是否有队列空的,自然就会从故障队列里面加回去
 */


}
