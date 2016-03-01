package com.netty.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Channel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient {
	private JFrame frame;  
    private JTextArea textArea;  
    private JTextArea textField;  
    private JButton btn_start;  
    private JButton btn_stop; 
    private JButton btn_login; 
    private JButton btn_upload; 
    private JButton btn_operate;
    private JButton btn_again;
    private JButton btn_create_task; 
    private JButton btn_submit_task; 
    private JButton btn_list_task;
    private JButton btn_list_detail;
    private JButton btn_list_detail_DK;
    private JButton btn_check_log;
    private JTextField txt_hostIp;
    private JTextField txt_taskId;
    private JButton btn_send;  
    private JButton btn_siren;
    private JPanel northPanel;  
    private JPanel southPanel;  
    private JScrollPane rightScroll;  
    private JScrollPane leftScroll;  
    private JSplitPane centerSplit;  
  
    private DefaultListModel listModel;  
    private boolean isConnected = false;  
  
    private Socket socket;  
    private PrintWriter writer;  
    private BufferedReader reader;  
    private MessageThread messageThread;// 负责接收消息的线程  
    
    private JButton btn_running_list_task ; //查询正在运行的任务
    
    //添加netty环境

	private final static int prot = 8888;
	Bootstrap  bootstarap;
	ChannelFuture channefuture;
	EventLoopGroup group = new NioEventLoopGroup();  
	//ClientHanndler handler;
		
	
	public void login(){
		String msg = "{\"data\": {\"userName\": \"Mrbai\",\"password\":\"abc123456\"},\"msgId\": \"20150202090909001\",\"serviceName\": \"login\",\"tokenId\": \"1001\"}";
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void checkLog(){
		BufferedReader br = null;
		try {
			br = getLogInfo();
			if(br != null){
				String line;
				while((line = br.readLine()) != null){
					textArea.setText(line);
				}
			}
		} catch (IOException e) {
			textArea.setText("获取出错");
		} finally {
			try {
				if(br!=null)br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void siren(){
		String msg =  readFile("siren.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void createTask(){
		String msg =  readFile("createTask.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void submitTask(){
		String msg =  readFile("submitTask.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void listTask(){
		String msg =  readFile("queryTaskList.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void detail(){
		String msg =  readFile("queryTaskDetail.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void detailDK(){
		String msg =  readFile("queryTaskDKDetail.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void operate(){
		String msg = readFile("operateTask.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void again(){
		String msg = readFile("submitFailedTask.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void runningListTask(){
		String msg =  readFile("queryRunningTaskList.txt");
		textArea.setText(textArea.getText() + "\r\n发送消息:"+ format(msg));
		sendMessage(msg);
	}
	
	public void start(){
		 //handler =  new ClientHanndler(textArea);
		 bootstarap = new Bootstrap();  
		 bootstarap.group(group)  
	      .channel(NioSocketChannel.class)  
	      .option(ChannelOption.TCP_NODELAY, true)  
	      .handler(new ChannelInitializer<SocketChannel>() {	        
	        protected void initChannel(SocketChannel ch) throws Exception {  
	        	  ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		          ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)); 
		          ch.pipeline().addLast(new LengthFieldPrepender(4, false));
		          ch.pipeline().addLast(new StringDecoder());
		          ch.pipeline().addLast(new StringEncoder());
		         /* ch.pipeline().addLast(new DiyEncoder());
		          ch.pipeline().addLast(new DiyDecoder());
		          ch.pipeline().addLast(handler);   */
	        }
	      }
	    );
		String ip = txt_hostIp.getText().split(" ")[0];
		String port = txt_hostIp.getText().split(" ")[1];
		
        // Start the client.
		try {
			channefuture = bootstarap.connect(ip,Integer.parseInt(port));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void stop(){
		
		// Wait until the connection is closed.
		try {
			if(channefuture != null){
				channefuture.channel().closeFuture();
				// Shut down the event loop to terminate all threads.
			}
			group.shutdownGracefully();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public ChannelFuture getChannekFuture(){
		return channefuture;
	}
    // 主方法,程序入口  
    public static void main(String[] args) throws IOException {  
    	new NettyClient().start();
    }  
  
    // 执行发送  
    public void send() {  
      
        String message = textField.getText().trim();  
        if (message == null || message.equals("")) {  
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",  
                    JOptionPane.ERROR_MESSAGE);  
            return;  
        }  
        sendMessage(message);  
        textField.setText(null);  
    	textArea.setText(textArea.getText() + "\r\n 发送消息:"+message);
    }  
  
    // 构造方法  
    public NettyClient() {  
    	
    	Font f = new Font(null,Font.PLAIN,13);
    	
        textArea = new JTextArea();  
        textArea.setFont(f);
        textArea.setEditable(false);  
        textArea.setForeground(Color.blue);  
        
        textField = new JTextArea();  
        
        textField.setFont(f);
        
        btn_login = new JButton("登陆");
        btn_start = new JButton("连接");  
        btn_stop = new JButton("断开");  
        btn_send = new JButton("发送"); 
        
        btn_siren = new JButton("私人订制");
        btn_create_task = new JButton("创建任务");
        btn_upload = new JButton("上传场景");
        btn_operate = new JButton("暂停任务");
        btn_again = new JButton("失败重提");
        btn_submit_task = new JButton("提交任务");
        btn_list_task = new JButton("查询任务");
        btn_list_detail = new JButton("任务详情");
        btn_list_detail_DK = new JButton("DIKU任务详情");
        btn_check_log = new JButton("查看日志");
        txt_hostIp = new JTextField();
        txt_taskId = new JTextField();
        txt_taskId.setColumns(6);
        //txt_hostIp.setText("121.40.188.208 8888");
        txt_hostIp.setText("127.0.0.1 8889");

        //txt_hostIp.setText("115.29.53.145 8888");//生产环境
        //txt_hostIp.setText("115.28.59.182 8888");//测试环境
        
        northPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(2,7);
        northPanel.setLayout(gridLayout);
        
        northPanel.add(new JLabel("服务器IP&端口"));
        northPanel.add(txt_hostIp);
        //northPanel.add(btn_start);  
        //northPanel.add(btn_stop);  
        northPanel.add(btn_login);
        northPanel.add(btn_create_task);
        northPanel.add(btn_siren);
        northPanel.add(txt_taskId);
        northPanel.add(btn_upload);
        northPanel.add(btn_operate);
        northPanel.add(btn_again);
        northPanel.add(btn_submit_task);
        northPanel.add(btn_list_task);
        northPanel.add(btn_list_detail);
        northPanel.add(btn_list_detail_DK);
        northPanel.add(btn_check_log);
        
        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder("输出"));  
        
        southPanel = new JPanel(new BorderLayout());  
        JScrollPane jScrollPane	= new JScrollPane(textField); 
        southPanel.setPreferredSize(new Dimension(100,250));
        southPanel.add(jScrollPane,"Center");
        southPanel.add(btn_send,"East");
        southPanel.setBorder(new TitledBorder("输入(回车发送)"));
        
        btn_running_list_task = new JButton("运行的任务");
        northPanel.add(btn_running_list_task);
  
        //centerSplit.setDividerLocation(100);  
  
        frame = new JFrame("客户端请求模拟器");  
        frame.setLayout(new BorderLayout());  
        frame.add(northPanel, "North");  
        frame.add(rightScroll, "Center");  
        frame.add(southPanel, "South");  
        frame.setSize(1080, 600);  
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;  
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;  
        frame.setLocation((screen_width - frame.getWidth()) / 2,  
                (screen_height - frame.getHeight()) / 2);  
        frame.setVisible(true);  
  
  
        textField.addKeyListener(new KeyListener() {
			
			
			public void keyTyped(KeyEvent e) {
				
			}
			
			public void keyReleased(KeyEvent e) {
				
			}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					send();
				}
			}
		});

        // 单击查询日志按钮时事件  
        btn_check_log.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
            	checkLog();
            }  
        });
        //单击登陆按钮事件
        btn_login.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           login();
            }
        });
        
        //单击登陆按钮事件
        btn_upload.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           uploadFile();
            }
        });
        
        btn_siren.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           siren();
            }
        });
        
        btn_create_task.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           createTask();
            }
        });
        btn_submit_task.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           submitTask();
            }
        });
        btn_list_task.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           listTask();
            }
        });
        btn_list_detail.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           detail();
            }
        });
        
        btn_list_detail_DK.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           detailDK();
            }
        });
        btn_operate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			operate();	
			}
		});
        
        btn_again.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			again();
			}
		});
        
        
        // 单击连接按钮时事件  
        btn_start.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
           
            }
        });  
  
        // 单击断开按钮时事件  
        btn_stop.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
               stop();
            }  
        });  
        

        // 单击发送按钮时事件  
        btn_send.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
            	send();
            }  
        });  
        
        //单机查询运行任务按钮
        btn_running_list_task.addActionListener(new ActionListener() {  
        	public void actionPerformed(ActionEvent e) {  
        		runningListTask();
        	}  
        });  
        
        
  
        // 关闭窗口时事件  
        frame.addWindowListener(new WindowAdapter() {  
            public void windowClosing(WindowEvent e) {  
            	stop();
                System.exit(0);// 退出程序  
            }  
        });  
    }  
  
  
    /**  
     * 发送消息  
     *   
     * @param message  
     */  
    public void sendMessage(String message) {  
    	Channel channel = (Channel) getChannekFuture().channel();
		((io.netty.channel.Channel) channel).writeAndFlush(message);
    }  
  
 
  
    // 不断接收消息的线程  
    class MessageThread extends Thread {  
        private BufferedReader reader;  
        private JTextArea textArea;  
  
        // 接收消息线程的构造方法  
        public MessageThread(BufferedReader reader, JTextArea textArea) {  
            this.reader = reader;  
            this.textArea = textArea;  
        }  
  
        // 被动的关闭连接  
        public synchronized void closeCon() throws Exception {  
            // 清空用户列表  
            listModel.removeAllElements();  
            // 被动的关闭连接释放资源  
            if (reader != null) {  
                reader.close();  
            }  
            if (writer != null) {  
                writer.close();  
            }  
            if (socket != null) {  
                socket.close();  
            }  
            isConnected = false;// 修改状态为断开  
        }  
  
    }
    
    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for(int i=0;i<jsonStr.length();i++){
          char c = jsonStr.charAt(i);
          if(level>0&&'\n'==jsonForMatStr.charAt(jsonForMatStr.length()-1)){
            jsonForMatStr.append(getLevelStr(level));
          }
          switch (c) {
          case '{': 
          case '[':
            jsonForMatStr.append(c+"\n");
            level++;
            break;
          case ',': 
            jsonForMatStr.append(c+"\n");
            break;
          case '}':
          case ']':
            jsonForMatStr.append("\n");
            level--;
            jsonForMatStr.append(getLevelStr(level));
            jsonForMatStr.append(c);
            break;
          default:
            jsonForMatStr.append(c);
            break;
          }
        }
        
        return jsonForMatStr.toString();

      }
      
      private static String getLevelStr(int level){
        StringBuffer levelStr = new StringBuffer();
        for(int levelI = 0;levelI<level ; levelI++){
          levelStr.append("\t");
        }
        return levelStr.toString();
      }
      
      
      public  String readFile(String fileName){
    	  
    	String inPackage = "/com/rayvisioncs/example/in/";
  		BufferedReader reader;
  		String tempString = null;
  		String requestJson = "";
  		String responseJson = "";
    	try{
    		File infile = new File(new File(NettyClient.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+fileName);
			
			requestJson = "";
			reader = new BufferedReader( new InputStreamReader(new FileInputStream(infile), "UTF-8"));
			while((tempString = reader.readLine()) != null){
				if(!txt_taskId.getText().trim().equals("") && tempString.trim().startsWith("\"taskId\"")){
					tempString = "\"taskId\":\""+txt_taskId.getText().trim()+"\"" + (tempString.trim().endsWith(",") ? "," : "");
				}
				requestJson += tempString;
			}
			reader.close();
			
  		}catch(Exception e) {
  			e.printStackTrace();
  		}finally{
  			
  		}
    	return requestJson;
  	}
      
      public BufferedReader getLogInfo() throws IOException{
    	  String custId = "100001";
    	  String taskId_frame = txt_taskId.getText();
    	  if(taskId_frame.split("_").length < 2){
    		  return null;
    	  }
    	  String taskId = taskId_frame.split("_")[0];
    	  String frame = taskId_frame.split("_")[1];
    	 // BufferedReader br = OssAdapter.getOssFileInputStream("rayvisionpublic", "d/log/"+custId+"/"+taskId+"/frame"+frame+".txt","UTF-8");
    	  return null;
      }
      
      public   boolean uploadFile(){
    	String inPackage = "/com/rayvisioncs/example/in/";
      	String scene = "ball2.mb";//本地场景路径
      	String keyPath = "d/961602/limin/maya/z/ball2.mb";//上传到oss上的路径
      	String rendercfg = "render.cfg";//本地render.cfg的路径
      	String key2Path = "p/temp/961602/"+txt_taskId.getText().trim()+"/render.cfg";//上传到oss上的路径
//      	String missings="/scene/missings.cfg";//本地missings文件
//      	String key3Path="p/temp/100001/"+txt_taskId.getText().trim()+"/missings.cfg";//上传到oss上的路径
      	String plugins="plugins.cfg";//本地plugins文件
      	String key4Path="p/temp/961602/"+txt_taskId.getText().trim()+"/plugins.cfg";//上传到oss上的路径
      	String service="server.cfg";//本地server文件
      	String key5Path="p/temp/961602/"+txt_taskId.getText().trim()+"/server.cfg";//上传到oss上的路径
      	try{
      		//File infile = new File(new File(Client.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+scene);
      		//OssAdapter.uploadBigFile(Constants.INPUT_BUCKET, keyPath, infile);
      		//File infile2 = new File(new File(Client.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+rendercfg);
      		//OssAdapter.uploadBigFile(Constants.INPUT_BUCKET, key2Path, infile2);
//      		File infile3 = new File(new File(AllCaseMain.class.getResource("/").getFile()).getParent()+missings);
//      		OssAdapter.uploadBigFile(Constants.INPUT_BUCKET, key3Path, infile3);
//      		File infile4 = new File(new File(Client.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+plugins);
//      		OssAdapter.uploadBigFile(Constants.INPUT_BUCKET, key4Path, infile4);
//      		File infile5 = new File(new File(Client.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+service);
//      		OssAdapter.uploadBigFile(Constants.INPUT_BUCKET, key5Path, infile5);
      		textArea.setText(textArea.getText() + "\r\n 上传成功！");
      		return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			
		}
      	
    }
      
      public static void test2() throws IOException{
//    	  String inPackage = "/example/in/com/rayvisioncs/";
    	  String fileName = "hello.text";
    	  
    	  
    	  
    	  //File infile = new File(new File(Client.class.getResource("/").getFile()).getPath()+File.separator+inPackage+File.separator+fileName);
      }	

}
