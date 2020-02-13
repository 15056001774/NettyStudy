package com.mashibing.netty.liang;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * Created by hp on 2020/2/12.
 */
public class NettyClientTest {

    public static void main(String[] args) {
        new NettyClientTest().clientStart();
    }

    public void clientStart(){
        //构建工作组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建BootStrap
        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        //连接服务端
        try {
            ChannelFuture future = b.connect("127.0.0.1", 8888);
            //监听关闭事件
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

}

class ClientHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel构建完成时调用。。。");
        //发送消息给服务端
        String msg = "客户端："+ctx.channel().hashCode()+" 上线了";
        ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("消息发送完成时调用。。。。");
            }
        });
    }

    /**
     * 读数据时被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("读取来自服务器端的数据："+buf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 数据读取完成时被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("来自服务器端的数据读取完毕。。。");
    }

    /**
     * 发生异常时被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("发生异常了。。。");
        cause.printStackTrace();
        ctx.close();
    }


}
