package com.bupt.charger.service;

import com.bupt.charger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ll （ created: 2023-06-01 15:09 )
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private WsService wsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);;

    //TEST:
    private String carId = "dick";
    private LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(5);


    @BeforeEach
    public void setUp() {
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }

    @Test
    public void testScheduleTask() throws InterruptedException {
        taskService.scheduleTask(carId, scheduledTime, "我的小马名字叫珍珠");
        Thread.sleep(20000);
    }

    @Test
    public void testCancelTask() {
        taskService.scheduleTask(carId, scheduledTime, "测试");
        taskService.cancelTask(carId);
    }
}