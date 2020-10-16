package p4analyser.broker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// NOTE For future reference, this guide was helpful: http://emehrkay.com/getting-started-with-tinkerpop-s-gremlin-server-and-gizmo-python
// NOTE The official way would have been to use gremlin-server.sh or gremlin-server.bat.

public class LocalGremlinServer {

    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private static String GREMLIN_SERVER_CONF_PATH = loader.getResource("conf/gremlin-server-min.yaml").getPath();
    private static String TINKERGRAPH_EMPTY_PROPERTIES_PATH = loader.getResource("conf/tinkergraph-empty.properties").getPath();
    private static String EMPTY_SAMPLE_GROOVY_PATH = loader.getResource("conf/empty-sample.groovy").getPath();

    // TODO this should come from the config file
    public String host = "localhost";
    public int port = 8182;
    public String remoteTraversalSourceName = "g";

    private p4analyser.blackboard.App bb = null;
    public void start(){
        updateServerConfig();
        bb = new p4analyser.blackboard.App(new String[] { "-c", GREMLIN_SERVER_CONF_PATH });
        bb.start();
    }

    public void close(){
        bb.close();
    }

    // NOTE: GremlinServer does not seem to substitute "classpath:" inside the YAML, so we have to include the path manually
    private void updateServerConfig() {
        Path path = Paths.get(GREMLIN_SERVER_CONF_PATH);
        Charset charset = StandardCharsets.UTF_8;

        String content;
        try {
            content = new String(Files.readAllBytes(path), charset);
            content = content.replaceAll("TINKERGRAPH_EMPTY_PROPERTIES", TINKERGRAPH_EMPTY_PROPERTIES_PATH);
            content = content.replaceAll("EMPTY_SAMPLE_GROOVY", EMPTY_SAMPLE_GROOVY_PATH);
            Files.write(path, content.getBytes(charset));
//            System.out.println(Files.lines(Paths.get(GREMLIN_SERVER_CONF_PATH)).collect(Collectors.toList()));
        } catch (IOException e1) {
            throw new IllegalStateException("Failed to edit config file "+GREMLIN_SERVER_CONF_PATH);
        }
    }

}
