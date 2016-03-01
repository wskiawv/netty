package com.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogLevel;

public class NettyServer {
	public String TCP="TCP";
	public String HTTP="HTTP";
	public String port="8889";
	public NettyServer(int port, String type){
		// Configure the server.
				EventLoopGroup bossGroup = new NioEventLoopGroup(1);
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
		             if (TCP.equals(type)) {
		            	 tcpServer(bossGroup, workerGroup, port); 
					}else{
						httpServer(bossGroup, workerGroup, port);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					// Shut down all event loops to terminate all threads.
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
	}

	private void httpServer(EventLoopGroup bossGroup,EventLoopGroup workerGroup, int port) throws InterruptedException{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(
				new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch)throws Exception {
						// Create a default pipeline implementation.
				        ChannelPipeline pipeline = ch.pipeline();
				      
				        /**
				         * http-request解码器
				         * http服务器端对request解码
				         */
				        pipeline.addLast("decoder", new HttpRequestDecoder());
				        /**
				         * http-response解码器
				         * http服务器端对response编码
				         */
				        pipeline.addLast("encoder", new HttpResponseEncoder());
				 
				        /**
				         * 压缩
				         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
				         * while respecting the "Accept-Encoding" header.
				         * If there is no matching encoding, no compression is done.
				         */
				        //pipeline.addLast("deflater", new HttpContentCompressor());
				      /*  pipeline.addLast(new LoggingDiyHandler(InternalLogLevel.INFO));
				 
				        pipeline.addLast("handler", new HttpServerHandler());*/
					}
				});
		// Start the server.
		ChannelFuture f = b.bind(port).sync();
	
		// Wait until the server socket is closed.
		f.channel().closeFuture().sync();
	}
	
	private void tcpServer(EventLoopGroup bossGroup,EventLoopGroup workerGroup, int port) throws InterruptedException{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(
				new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch)throws Exception {
						ChannelPipeline p = ch.pipeline();
						ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0, 4,0, 4));
						ch.pipeline().addLast(new LengthFieldPrepender(4,false));
						/*ch.pipeline().addLast(new DiyDecoder());
						ch.pipeline().addLast(new DiyEncoder());*/
						/*p.addLast(new LoggingDiyHandler(InternalLogLevel.INFO));
						p.addLast(new ChannelInboundHandlerAdapter());*/
					}
				});
		// Start the server.
		ChannelFuture f = b.bind(port).sync();
	
		// Wait until the server socket is closed.
		f.channel().closeFuture().sync();
	}
	public static void main( String[] args ){
		new NettyServer(8889,"TCP");
	}
}
