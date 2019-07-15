/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify.service;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A {@code NotifyClient} allows to use a notification service running on
 * another process of the host in order to display notifications, just like if
 * the service were running on this process.
 * @author DragShot
 * @since 0.9 (2019-06-18)
 */
public class NotifyClient extends NotifyService {
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    @Override
    public void start() {}

    @Override
    public void postNotification(String title, String message, Integer type, Integer align, Long timeout, String themeName) {
        try {
            connect();
            String resp;
            out.println("DESCRIBE");
            resp = in.readLine();
            if (!resp.startsWith("DSDN")) return;
            if (!sendAndCheck("BUILD", "", "READY")) return;
            if (title != null && !sendAndCheck("--title", title, "OK")) return;
            if (message != null && !sendAndCheck("--message", message, "OK")) return;
            if (type != null && !sendAndCheck("--type", String.valueOf(type), "OK")) return;
            if (align != null && !sendAndCheck("--align", String.valueOf(align), "OK")) return;
            if (timeout != null && !sendAndCheck("--timeout", String.valueOf(timeout), "OK")) return;
            if (themeName != null && !sendAndCheck("--theme", themeName, "OK")) return;
            sendAndCheck("POST", "", "DONE");
        } catch (IOException ex) {
            
        } finally {
            disconnect();
        }
    }
    
    /**
     * Sends one message ({@code command} + {@code value}) through the socket
     * and verifies that the answer received afterwards matches the expected
     * value.
     * @param command The command to send in the message.
     * @param value   The value to send in the message.
     * @param resp    The expected answer after the message was sent.
     * @return {@code true} if the received answer matches with the expècted
     *         value, {@code false} otherwise.
     * @throws IOException if something happens during the operation.
     */
    private boolean sendAndCheck(String command, String value, String resp) throws IOException {
        boolean correct = true;
        if (value != null) {
            out.println((command + " " + value.replace("\r", "\\r").replace("\n", "\\n")).trim());
            correct = checkAnswer(resp);
        }
        return correct;
    }
    
    /**
     * Receives one message through the socket and verifies if it matches with
     * a given value.
     * @param val The expected message.
     * @return {@code true} if the received answer matches with the expècted
     *         value, {@code false} otherwise.
     * @throws IOException if something happens during the operation.
     */
    private boolean checkAnswer(String val) throws IOException {
        String resp = in.readLine();
        return Objects.equals(resp, val);
    }

    @Override
    public void stop() {
        try {
            connect();
            sendAndCheck("SHUTDOWN", "", "OK");
        } catch (IOException ex) {
            
        } finally {
            disconnect();
            NotifyService.reset();
        }
    }
    
    /**
     * Establishes a connection to the currently existing service. If the
     * attempt is successful, the connection will remain open for further use.
     * @throws IOException if something happens during the operation.
     * @see #disconnect()
     */
    private void connect() throws IOException {
        socket = new Socket("localhost", LISTENING_PORT);
        socket.setSoTimeout(1000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        try {
            String resp;
            out.println("DESCRIBE");
            resp = in.readLine();
            if (!resp.startsWith("DSDN")) disconnect();
        } catch (IOException ex) {
            disconnect();
        }
        if (socket == null) throw new IOException("Listen server was not found.");
    }
    
    /**
     * Closes the current connection to the currently existing service, if
     * there's any. Future operations will require a reconnection.
     * @see #connect()
     */
    private void disconnect() {
        try {
            close(in); in = null;
            close(out); out = null;
            close(socket); socket = null;
        } catch (IOException ex) {}
    }
    
    /**
     * Closes a {@link Closeable} object.
     * @param obj The object to close.
     * @throws IOException if an I/O error occurs
     */
    private void close(Closeable obj) throws IOException {
        if (obj != null) obj.close();
    }
    
    /**
     * Attempts to create a {@link NotifyClient} object and polls for any
     * {@link NotifyServer} running on this host. If the service is located, the
     * client is kept and returned to the caller.
     * @return A newly created {@link NotifyClient}, able to connect to a local
     *         {@link NotifyServer} running on this host, or {@code null} if no
     *         service could be located.
     */
    protected static NotifyClient tryAndGet() {
        NotifyClient client = new NotifyClient();
        try {
            client.connect();
            client.disconnect();
        } catch (IOException ex) {
            client = null;
        }
        return client;
    }
    
}
