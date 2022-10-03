package com.cloud.cloud_server.server;


import com.cloud.cloud_server.dao.UserDAO;
import com.cloud.cloud_server.dao.UserDaoJDBC;
import com.cloud.cloud_server.dao.UserSecureDAO;
import com.cloud.cloud_server.dao.UserSecureDaoJDBC;
import com.cloud.cloud_server.entity.User;
import com.cloud.cloud_server.entity.UserSecure;
import com.cloud.cloud_server.network.CommonHandler;
import com.cloud.cloud_server.network.TCPListener;
import com.cloud.cloud_server.util.JdbcConnection;
import com.cloud.common.util.SHAUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@Slf4j
public class ServerNetty {
    private final int SERVER_PORT = 8189;
    private final UserSecureDAO userSecureDAO;
    private TCPListener tcpConnectionListener;

    public static void main(String[] args) {
        new ServerNetty();
    }

    public ServerNetty() {
        userSecureDAO = UserSecureDaoJDBC.getINSTANCE();
        tcpConnectionListener = new TCPListener();

        initializeData();
        start();
    }

    @SneakyThrows
    private void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(SERVER_PORT))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast("encoder", new ObjectEncoder());
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new CommonHandler(tcpConnectionListener));
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }

    private void initializeData() {

        try (var connection = JdbcConnection.getConnection(); var statement = connection.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/Query.sql")));
            statement.execute(sql);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        UserSecure userOneSecure = new UserSecure("admin", SHAUtils.SHA256("1234"));
        UserSecure userTwoSecure = new UserSecure("lilyPit", SHAUtils.SHA256("1234"));
        UserSecure userThreeSecure = new UserSecure("Karmenchik", SHAUtils.SHA256("1234"));
        UserSecure mainUserSecure = new UserSecure("SteveApple", SHAUtils.SHA256("1234"));
        UserSecure userFourSecure = new UserSecure("Jonson@Lol", SHAUtils.SHA256("1234"));
        UserSecure userFiveSecure = new UserSecure("KittyClair", SHAUtils.SHA256("1234"));
        UserSecure userSixSecure = new UserSecure("KekLol", SHAUtils.SHA256("1234"));

        userSecureDAO.createUserSecure(userOneSecure);
        userSecureDAO.createUserSecure(userTwoSecure);
        userSecureDAO.createUserSecure(userThreeSecure);
        userSecureDAO.createUserSecure(userFourSecure);
        userSecureDAO.createUserSecure(userFiveSecure);
        userSecureDAO.createUserSecure(userSixSecure);
        userSecureDAO.createUserSecure(mainUserSecure);

        UserDAO userRepository = UserDaoJDBC.getINSTANCE();

        User mainUser = userRepository.saveUser(new User("admin", "/images/iconsForUsers/admin.jpg"));
        User userOne = userRepository.saveUser(new User("lilyPit"));
        User userTwo = userRepository.saveUser(new User("Karmenchik"));
        User userThree = userRepository.saveUser(new User("SteveApple"));
        User userFour = userRepository.saveUser(new User("Jonson@Lol"));
        User userFive = userRepository.saveUser(new User("KittyClair"));
        User userSix = userRepository.saveUser(new User("KekLol"));
    }
}
