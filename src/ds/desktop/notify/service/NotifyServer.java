/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify.service;

import ds.desktop.notify.DesktopNotify;
import ds.desktop.notify.NotificationBuilder;
import ds.desktop.notify.NotifyTheme;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A {@code NotifyServer} runs a notification service for this host. Other
 * processes can use {@link NotifyClient}s in order to show notifications
 * through this service.
 * @author DragShot
 * @since 0.9 (2019-06-18)
 */
public class NotifyServer extends NotifyService {
    /** The executor that handles notification requests. */
    private ExecutorService executor;
    /** The server socket the service is listening at. */
    private ServerSocket server;
    /** A flag signaling if the service is active or not. */
    private boolean alive;

    @Override
    public void start() {
        if (alive) return;
        try {
            executor = Executors.newSingleThreadExecutor();
            server = new ServerSocket();
            server.bind(new InetSocketAddress("localhost", LISTENING_PORT), 50);
            alive = true;
        } catch (IOException ex) {
            DesktopNotify.logError("NotifyServer", "Unable to set up listen server!", ex);
            alive = false;
        }
        if (alive) {
            new Thread(new Runnable() { @Override public void run() {
                DesktopNotify.logInfo("NotifyServer", "Listen server started");
                while (alive) {
                    try {
                        final Socket socket = server.accept();
                        executor.submit(new Runnable() { @Override public void run() { socketOps(socket); }});
                    } catch (IOException ex) { }
                }
            }}, "Notification service").start();
        }
    }

    @Override
    public void postNotification(String title, String message, Integer type, Integer align, Long timeout, String themeName) {
        NotificationBuilder builder = new NotificationBuilder();
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        if (type != null) builder.setType(type);
        if (align != null) builder.setTextOrientation(align);
        if (timeout != null) builder.setTimeOut(timeout);
        if (themeName != null) {
            if (themeName.equals(NotifyService.DARK_THEME)) {
                builder.setTheme(NotifyTheme.Dark);
            } else if (themeName.equals(NotifyService.LIGHT_THEME)) {
                builder.setTheme(NotifyTheme.Light);
            }
        }
        builder.build().show();
    }
    
    /**
     * Performs the defined operations in a given socket session.
     * @param socket The socket to operate on.
     */
    private void socketOps(Socket socket) {
        boolean lineUp = true;
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String req = null, resp = null;
            String title = null, message = null, themeName = null;
            Integer type = null, align = null;
            Long timeout = null;
            while (lineUp) {
                try {
                    req = in.readLine();
                    //System.out.println("IN: " + req);
                    if (req == null) {
                        lineUp = false;
                        resp = null;
                    } else if (req.startsWith("DESCRIBE")) {
                        resp = "DSDN 090";
                    } else if (req.startsWith("BUILD")) {
                        resp = "READY";
                    } else if (req.startsWith("--title")) {
                        req = readValue("--title", req);
                        title = req;
                        resp = "OK";
                    } else if (req.startsWith("--message")) {
                        req = readValue("--message", req);
                        message = req;
                        resp = "OK";
                    } else if (req.startsWith("--type")) {
                        req = readValue("--type", req);
                        type = Integer.parseInt(req);
                        resp = "OK";
                    } else if (req.startsWith("--align")) {
                        req = readValue("--align", req);
                        align = Integer.parseInt(req);
                        resp = "OK";
                    } else if (req.startsWith("--timeout")) {
                        req = readValue("--timeout", req);
                        timeout = Long.parseLong(req);
                        resp = "OK";
                    } else if (req.startsWith("--theme")) {
                        req = readValue("--theme", req);
                        themeName = req;
                        resp = "OK";
                    } else if (req.startsWith("POST")) {
                        postNotification(title, message, type, align, timeout, themeName);
                        resp = "DONE";
                        lineUp = false;
                    } else if (req.startsWith("SHUTDOWN")) {
                        new Thread(new Runnable() { @Override public void run() {
                            stop();
                        }}).start();
                        resp = "OK";
                        lineUp = false;
                    }
                } catch (SocketException ex) {
                    if (ex.getMessage().contains("Connection reset")) {
                        lineUp = false;
                    }
                } catch (Exception ex) {
                    resp = "EXCEPTION " + ex.getClass() + " " + ex.getMessage();
                    DesktopNotify.logError("NotifyServer", "Exception during operation: ", ex);
                } catch (Error err) {
                    resp = "SYSERROR " + err.getClass() + " " + err.getMessage();
                    DesktopNotify.logError("NotifyServer", "Error during operation: ", err);
                } finally {
                    if (req != null) {
                        out.println(resp);
                        //System.out.println("OUT: " + resp);
                    }
                }
            }
        } catch (Exception ex) {
            DesktopNotify.logError("NotifyServer", "Exception during connection: ", ex);
        } catch (Error err) {
            DesktopNotify.logError("NotifyServer", "Error during connection: ", err);
        }
    }
    
    private String readValue(String head, String line) {
        return line.substring(head.length()).replace("\\r", "\r").replace("\\n", "\n").trim();
    }

    @Override
    public void stop() {
        if (alive) {
            alive = false;
            executor.shutdown();
            boolean done = false;
            while (!done) {
                try {
                    done = executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {}
            }
            try {
                server.close();
            } catch (Exception ex) {}
            DesktopNotify.logInfo("NotifyServer", "Listen server stopped");
        }
    }
    
}
