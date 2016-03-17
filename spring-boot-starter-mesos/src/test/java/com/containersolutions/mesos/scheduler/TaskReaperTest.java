package com.containersolutions.mesos.scheduler;

import com.containersolutions.mesos.TestHelper;
import com.containersolutions.mesos.scheduler.events.InstanceCountChangeEvent;
import com.containersolutions.mesos.scheduler.state.StateRepository;
import org.apache.mesos.Protos;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskReaperTest {
    @Mock
    InstanceCount instanceCount;

    @Mock
    StateRepository stateRepository;

    @Mock
    UniversalScheduler universalScheduler;

    @Captor
    ArgumentCaptor<Protos.TaskID> taskIDArgumentCaptor;

    @InjectMocks
    TaskReaper taskReaper = new TaskReaper();

    @Test
    public void willNotKillTasksWhenCountIsFullfilled() throws Exception {
        when(stateRepository.allTaskInfos()).thenReturn(tasksInfoSet("task 1", "task 2"));
        when(instanceCount.getCount()).thenReturn(2);
        taskReaper.onApplicationEvent(new InstanceCountChangeEvent(1));

        verifyZeroInteractions(universalScheduler);
    }

    @Test
    public void willKillTaskWhenScalingDown() throws Exception {
        when(stateRepository.allTaskInfos()).thenReturn(tasksInfoSet("task 1", "task 2"));
        when(instanceCount.getCount()).thenReturn(1);
        taskReaper.onApplicationEvent(new InstanceCountChangeEvent(1));

        verify(universalScheduler).killTask(taskIDArgumentCaptor.capture());
        assertEquals("task 2 id", taskIDArgumentCaptor.getValue().getValue());
    }

    private static Set<Protos.TaskInfo> tasksInfoSet(String ... names) {
        return Arrays.stream(names)
                .map(TestHelper::createDummyTask)
                .collect(Collectors.toSet());
    }
}