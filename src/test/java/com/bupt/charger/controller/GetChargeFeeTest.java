package com.bupt.charger.controller;

import com.bupt.charger.entity.Pile;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.util.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetChargeFeeTest {
    @InjectMocks
    private Calculator c;

    @Mock
    private ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);

    @Mock
    private PilesRepository pilesRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private Pile pile;

    @BeforeEach
    public void setUp() {
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }

    @Test
    public void test() {
        // 初始化一个有效的测试用例 pile


        // 使用 Mockito.when() 和 thenReturn() 对 getPileById 方法进行模拟
//        when(pilesRepository.findByPile("test")).thenReturn(pile);

        System.out.println(c.getChargeFee(
                LocalDateTime.parse("2023-06-01T11:06:00"),
                LocalDateTime.parse("2023-06-01T12:06:00"),
                "test",
                648));
    }
}
