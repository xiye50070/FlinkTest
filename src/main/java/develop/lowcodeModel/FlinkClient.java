package develop.lowcodeModel;

 import org.apache.flink.api.common.JobID;
 import org.apache.flink.client.deployment.StandaloneClusterId;
 import org.apache.flink.client.program.PackagedProgram;
 import org.apache.flink.client.program.PackagedProgramUtils;
 import org.apache.flink.client.program.rest.RestClusterClient;
 import org.apache.flink.configuration.Configuration;
 import org.apache.flink.configuration.DeploymentOptions;
 import org.apache.flink.configuration.JobManagerOptions;
 import org.apache.flink.configuration.RestOptions;
 import org.apache.flink.runtime.jobgraph.JobGraph;
 import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;

 import java.io.File;
 import java.util.concurrent.CompletableFuture;


public class FlinkClient {

    /*
flink run \
-t yarn-per-job \
-Dyarn.application.name=TestDemo \
-Dparallelism.default=3 \
-Djobmanager.memory.process.size=2048mb \
-Dtaskmanager.memory.process.size=2048mb \
-Dtaskmanager.numberOfTaskSlots=2 \
-Denv.java.opts="-Dfile.encoding=UTF-8" \
-Drest.flamegraph.enabled=true \
-c com.wmz.apiTest.stateApi.AverageTimestampExample \
FlinkTest-1.0-SNAPSHOT.jar

    */


    public static void main(String[] args) {
        String jarFilePath = "X:\\IdeaProjects\\FlinkTest\\target\\FlinkTest-1.0-SNAPSHOT.jar";
        RestClusterClient<StandaloneClusterId> client = null;
        try {
            // 集群信息
            Configuration configuration = new Configuration();
            configuration.setString(JobManagerOptions.ADDRESS, "192.168.165.27");
            configuration.setString(DeploymentOptions.TARGET, "yarn-per-job");
            configuration.setInteger(JobManagerOptions.PORT, 6123);
            configuration.setInteger(RestOptions.PORT, 8081);
            client = new RestClusterClient<StandaloneClusterId>(configuration, StandaloneClusterId.getInstance());
            int parallelism = 1;
            File jarFile=new File(jarFilePath);
            SavepointRestoreSettings savepointRestoreSettings=SavepointRestoreSettings.none();
            PackagedProgram program = PackagedProgram.newBuilder()
                    .setConfiguration(configuration)
                    .setEntryPointClassName("com.wmz.apiTest.stateApi.AverageTimestampExample")
                    .setJarFile(jarFile)
                    .setSavepointRestoreSettings(savepointRestoreSettings).build();
            JobGraph jobGraph=PackagedProgramUtils.createJobGraph(program,configuration,parallelism,false);
            CompletableFuture<JobID> result = client.submitJob(jobGraph);
            JobID jobId=  result.get();
            System.out.println("提交完成");
            System.out.println("jobId:"+ jobId.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
