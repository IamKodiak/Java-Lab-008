import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.concurrent.TimeUnit;

public class LEDClient {
    private ZContext zctx;
    private ZMQ.Socket zsocket;
    private Gson gson;
    private String connStr;
    private final String topic = "GPIO";

    private static final int[] OFF = {0, 0, 0};

    public LEDClient(String protocol, String host, int port) {
        zctx = new ZContext();
        zsocket = zctx.createSocket(SocketType.PUB);
        this.connStr = String.format("%s://%s:%d", protocol, "*", port);
        zsocket.bind(connStr);
        this.gson = new Gson();
    }

    public void send(int[] color) throws InterruptedException {
        JsonArray ja = gson.toJsonTree(color).getAsJsonArray();
        String message = topic + " " + ja.toString();
        System.out.println(message);
        zsocket.send(message);
    }

    public void blinkN(int[] color, int times, int miliseconds) throws  InterruptedException{
        for(int i=0; i<times; i++) {
            send(color);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
            send(LEDClient.OFF);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        }
    }

    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient ledClient = new LEDClient("tcp", "192.168.1.117", 5001);
        try {
            int[] color = {255, 255, 0};
            int[] color1 = {0, 21, 255};
            for (int i = 0; i < 30; i++) {
                ledClient.blinkN(color, 1, 250);
                ledClient.blinkN(color1, 1, 250);
            }
                ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
int[] color = {255, 255, 0};
int[] color1 = {0, 21, 255};

int[] color = {0, 255, 85};
int[] color1 = {255, 0, 30};
 */