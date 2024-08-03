/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify.service;

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

import balbucio.throwable.Throwable;
import ds.desktop.notify.model.NotifyDirection;
import ds.desktop.notify.model.NotifyType;

/**
 * A {@code NotifyServer} runs a notification service for this host. Other
 * processes can use {@link NotifyClient}s in order to show notifications
 * through this service.
 *
 * @author DragShot
 * @since 0.9 (2019-06-18)
 */
public class NotifyServer extends NotifyService {
    /**
     * The executor that handles notification requests.
     */
    private ExecutorService executor;
    /**
     * The server socket the service is listening at.
     */
    private ServerSocket server;
    /**
     * A flag signaling if the service is active or not.
     */
    private boolean alive = false;

    @Override
    public void start() {
        if (alive) return;
        Throwable.printThrow(() -> {
            executor = Executors.newSingleThreadExecutor();
            server = new ServerSocket();
            server.bind(new InetSocketAddress("localhost", LISTENING_PORT), 50);
            alive = true;
//            new Thread(() -> Throwable.silently(() -> {
//                while (alive) {
//                    final Socket socket = server.accept();
//                    executor.submit(() -> socketOps(socket));
//                }
//            });
        });
    }

    @Override
    public void postNotification(String title, String message, NotifyType type, NotifyDirection align, Long timeout, String themeName) {
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
     *
     * @param socket The socket to operate on.
     */
    private void socketOps(Socket socket) {
        Throwable.silently(() -> {
            boolean lineUp = true;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String req = null, resp = null;
            String title = null, message = null, themeName = null;
            NotifyType type = null;
            NotifyDirection align = null;
            Long timeout = null;

            while (lineUp) {
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
                    type = NotifyType.valueOf(req);
                    resp = "OK";
                } else if (req.startsWith("--align")) {
                    req = readValue("--align", req);
                    align = NotifyDirection.valueOf(req);
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
                    new Thread(this::stop).start();
                    resp = "OK";
                    lineUp = false;
                }
            }
        });
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
                } catch (InterruptedException ex) {
                }
            }
            try {
                server.close();
            } catch (Exception ex) {
            }
        }
    }

}
