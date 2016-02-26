package dk.mwl.mesos.scheduler;

import dk.mwl.mesos.scheduler.config.MesosConfigProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mesos.Protos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TaskInfoFactoryCommand implements TaskInfoFactory {
    protected final Log logger = LogFactory.getLog(getClass());

    @Value("${spring.application.name}")
    protected String applicationName;

    @Autowired
    MesosConfigProperties mesosConfig;

    @Autowired
    Supplier<UUID> uuidSupplier;

    @Override
    public Protos.TaskInfo create(String taskId, Protos.Offer offer, List<Protos.Resource> resources) {
        logger.debug("Creating Mesos task for taskId=" + taskId);
        return Protos.TaskInfo.newBuilder()
                .setName(applicationName + ".task")
                .setSlaveId(offer.getSlaveId())
                .setTaskId(Protos.TaskID.newBuilder().setValue(taskId))
                .addAllResources(resources)
                .setCommand(Protos.CommandInfo.newBuilder()
                        .setShell(true)
                        .setValue(mesosConfig.getCommand()))
                .build();
    }
}
