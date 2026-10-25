import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Minimal RCON client so a dev server can be driven from the shell. */
public final class Rcon {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = Integer.parseInt(System.getenv().getOrDefault("RCON_PORT", "25575"));
        String password = System.getenv().getOrDefault("RCON_PASS", "test");

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(30000);
            // The server reads a whole packet with one read() and drops the client otherwise, so
            // each frame has to go out as a single write.
            socket.setTcpNoDelay(true);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            send(out, 1, 3, password);
            Packet login = read(in);
            if (login == null || login.id == -1) {
                System.out.println("AUTH_FAILED");
                return;
            }

            int id = 10;
            for (String command : args) {
                send(out, id++, 2, command);
                Packet response = read(in);
                System.out.println("> " + command);
                System.out.println(response == null ? "<no response>" : response.body);
                System.out.println("---");
            }
        }
    }

    private record Packet(int id, int type, String body) {
    }

    /** RCON speaks little-endian, unlike {@link DataOutputStream}. */
    private static void send(DataOutputStream out, int id, int type, String body) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream frame = new ByteArrayOutputStream(payload.length + 14);
        DataOutputStream frameOut = new DataOutputStream(frame);
        frameOut.writeInt(Integer.reverseBytes(4 + 4 + payload.length + 2));
        frameOut.writeInt(Integer.reverseBytes(id));
        frameOut.writeInt(Integer.reverseBytes(type));
        frameOut.write(payload);
        frameOut.writeByte(0);
        frameOut.writeByte(0);
        out.write(frame.toByteArray());
        out.flush();
    }

    private static Packet read(DataInputStream in) throws IOException {
        int length = Integer.reverseBytes(in.readInt());
        int id = Integer.reverseBytes(in.readInt());
        int type = Integer.reverseBytes(in.readInt());
        byte[] body = new byte[length - 10];
        in.readFully(body);
        in.readByte();
        in.readByte();
        return new Packet(id, type, new String(body, StandardCharsets.UTF_8));
    }
}
