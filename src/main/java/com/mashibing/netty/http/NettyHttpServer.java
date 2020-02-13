package com.mashibing.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by hp on 2020/2/12.
 * 使用netty实现简单http服务器
 */
public class NettyHttpServer {

    private int port;
    public NettyHttpServer(int port){
        this.port = port;
    }

    public void serverStart(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /**
                         * new HttpServerCodec(): netty提供的对http请求的编码与解码handler
                         *     自定义的处理器
                         */
                             ch.pipeline().addLast("myCodec",new HttpServerCodec())
                                          .addLast("myHandler",new MyHandler());
                    }
                });
        try {
            ChannelFuture cf = b.bind("127.0.0.1", port).sync();
            //异步监听关闭事件
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyHttpServer(8888).serverStart();
    }
}

    class MyHandler extends SimpleChannelInboundHandler<HttpObject>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
            //当有http请求时，触发
            if(httpObject instanceof HttpRequest){
                HttpRequest request = (HttpRequest) httpObject;
                String uri = request.uri();
                if("/favicon.ico".equals(uri)){
                    System.out.println("过滤掉网站图标请求。。。");
                    return;
                }
                System.out.println("客户端地址："+ctx.channel().remoteAddress());
                //回复消息给浏览器
                ByteBuf buf = Unpooled.copiedBuffer("hello client", CharsetUtil.UTF_8);
                //构造一个http响应,将内容填充进去
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK ,buf);
                //设置返回头信息类型为文本类型
                response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                //设置返回头信息长度
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
                //将构建好的response返回
                ctx.writeAndFlush(response);
            }
        }

    }