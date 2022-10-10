package com.cloud.cloudclient.network;

import com.cloud.cloudclient.Main;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class ClientNetty {

    public static Bootstrap bootstrap;

    public void start(TCPConnection connection) {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 8189))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new ObjectEncoder());
                        ch.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new CommonHandler());
                    }
                });
        try {
            ChannelFuture f = bootstrap.connect().syncUninterruptibly();
            f.addListener(future -> connection.setChannel(Optional.of(f.channel())));
            f.channel().closeFuture().syncUninterruptibly();
        } finally {
            try {
                log.debug("Server disconnected");
                group.shutdownGracefully().sync();
                Main.disconnect();
                reconnect(connection);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void getNewChannel() {
        Bootstrap boot = bootstrap.clone();
        try {
            ChannelFuture f = boot.connect().sync();
            ConnectionUtil.setDownloadConnection(f);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug("Connection download interrupted");
        } finally {
            ConnectionUtil.closeDownloadConnection();
        }
    }

    private void reconnect(TCPConnection connection) throws InterruptedException {
        Thread.sleep(10000);
        log.debug("reconnect to the server");
        start(connection);
    }
}
