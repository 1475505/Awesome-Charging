package com.bupt.charger.service;

import com.bupt.charger.entity.User;
import com.bupt.charger.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author ll （ created: 2023-06-01 14:53 )
 */
@Service
@Slf4j
public class TaskService {
    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    WsService wsService;

    @Autowired
    UserRepository userRepository;

    private Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    // 首次调用是加入，再次调用是覆盖。也可以直接cancelTask取消。
    public void scheduleTask(String carId, LocalDateTime scheduledTime, String message) {
        if (tasks.containsKey(carId)) {
            cancelTask(carId);
        }
        ScheduledFuture<?> task = taskScheduler.schedule(() -> executeTask(carId, message), ZonedDateTime.of(scheduledTime, ZoneOffset.of("+8")).toInstant());
        log.info("注册任务通知: CarId=" + carId + " message= " + message);
        tasks.put(carId, task);
    }


    // 首次调用是加入，再次调用是覆盖。也可以直接cancelTask取消。
    // Duration是一个时间段，设置的执行时间是当前.plus(duration)
    public void scheduleTask(String carId, Duration duration, String message) {
        Instant scheduledTime = Instant.now().plus(duration);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> executeTask(carId, message), scheduledTime);
        tasks.put(carId, scheduledTask);
    }

    public void cancelTask(String carId) {
        ScheduledFuture<?> scheduledTask = tasks.get(carId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            tasks.remove(carId);
        }
    }

    private void executeTask(String carId, String message) {
        log.info("启动任务：通知车辆 -- CarId=" + carId);
        User user = userRepository.findByCarId(carId);
        wsService.sendToUser(user.getUsername(), message);
    }
}
