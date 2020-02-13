package com.mashibing.netty.liang;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 2020/2/11.
 */
public class NettyServerTest {

    private int port;
    public static HashMap<String,Object> map = new HashMap<String,Object>();

    public NettyServerTest(int port){
        this.port = port;
    }

    public static void main(String[] args) {
        new NettyServerTest(8888).serverStart();
    }
    //初始化服务器端
    public void serverStart(){
        //构建boss,worker工作组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //构建ServerBootStrap,注册工作组
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //初始化channel
                        ch.pipeline().addLast(new Handler(map));
                        //将每个客户端连接存入集合
                        map.put(ch.hashCode()+"",ch);
                    }
                });

        try {
            ChannelFuture cf = b.bind(port).sync();
            //绑定端口
            //异步监听关闭事件
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}

class Handler extends ChannelInboundHandlerAdapter{
    private Map<String,Object> map;

    public Handler(Map<String,Object> map){
        super();
        this.map = map;
    }

    /**
     * 读取来自客户端消息时候调用的方法
     * @param ctx:上下文环境
     * @param msg：要读取的消息对象
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //耗时操作可放在NioEventLoop的TaskQueue里面异步执行
        /*ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                //模拟耗时操作
                try {
                    Thread.sleep(10*1000);
                    String msg = "耗时操作执行结束了....";
                    ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/
        if(msg!=null){
            ByteBuf buf = (ByteBuf) msg;
            String message = buf.toString(CharsetUtil.UTF_8);
            System.out.println("客户端说："+buf.toString(CharsetUtil.UTF_8));
            System.out.println("消息来自客户端："+ctx.channel().remoteAddress());

            //循环集合转发给每个客户端
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> entry = iterator.next();
                SocketChannel sc = (SocketChannel) entry.getValue();
                sc.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
            }
        }
    }

    /**
     * 客户端的信息读取完毕时会调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("来自客户端:"+ctx.channel().remoteAddress()+"的消息读取完毕了。。。");
       /* String msg = "hello client....";
        ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));*/

    }

    /**
     * 异常处理器
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("进入异常处理器。。。");
        cause.printStackTrace();
        //从集合中移除异常的channel
        removeKey(ctx);
        ctx.close();
    }


    public void removeKey(ChannelHandlerContext ctx){
        java.lang.String msg = "客户端："+ctx.channel().hashCode()+"离开了";
        map.remove(ctx.channel().hashCode()+"");
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        //通知其他客户端，有人离开
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            SocketChannel sc = (SocketChannel) entry.getValue();
            sc.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
        }
    }


}