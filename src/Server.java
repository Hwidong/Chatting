import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

   public static void main(String[] args) {
      // TODO Auto-generated method stub
      System.out.println("Server is started");
      
      Connect con = new Connect();
      con.start();
      
      System.out.println("Server is closed");
   }
}
class Connect extends Thread{
   @Override
   public void run() {
      int i=0;
      ArrayList<ClientThread> clientAL = new ArrayList<ClientThread>();
      // TODO Auto-generated method stub
      super.run();
      
      InetAddress address;
      try {
         // 포트번호 0~1023 까지는 예약 되어 있음
         address = InetAddress.getLocalHost();
         System.out.println(address);
         ServerSocket mss = new ServerSocket();
         // 외부에서 접속하게 하려면 ip 주소를 써주고
         // 혼자 놀려면 localhost로 써주면 됨
         
         mss.bind(new InetSocketAddress("192.168.0.14", 5001));
         
         System.out.println("소켓 연결 ㅇㅋ");
         
         
         while (true) {
            Socket ss = mss.accept();
            ClientThread c = new ClientThread(ss,clientAL);
            clientAL.add(c);
            c.start();
            //접속자수 카운트
            
            System.out.println(clientAL.size() + "명 접속 중");
            System.out.println(ss.getInetAddress()+"에서 접속함");
         }            
      } catch (Exception e) {
         // TODO Auto-generated catch block
         System.out.println(e.getMessage());
      }
      
   }
}

class ClientThread extends Thread{
   
   private byte[] bt;
   private int size;
   private int i;
   private Socket ss;
   private ArrayList<ClientThread> clientAL;
   
   public ClientThread(Socket ss, ArrayList<ClientThread> clientAL) {
      try {
         this.ss = ss;
         i=0;
         this.clientAL = clientAL;
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
   
   @Override
   public void run() {
      // TODO Auto-generated method stub
      super.run();
      
      System.out.println("ClientThread is start");
      
      while(true) {
         String s;
         try {      
            InputStream is = ss.getInputStream();
            
            bt = new byte[1024];
            size = is.read(bt);
            
            if(size == (-1)) {
               ss.close();
               break;      
            }
      
            s = new String(bt, 0, size, "UTF-8");
            System.out.println("출력" + this.ss.getInetAddress() + " : " + s);
            
            
            for(int i=0 ; i<clientAL.size() ; i++) {
               clientAL.get(i).spread(ss.getInetAddress().toString(),bt,size);
            }
            
            
            
         } catch (Exception e) {
            System.out.println(ss.getInetAddress() + "나감");
            System.out.println((clientAL.size()-1)+"명 접속 중");
            clientAL.remove(this);
            break;
         }
         
      }//while
   }
   
   public void spread(String ip,byte[] bt, int newSize) {
      OutputStream os;
      try {
         
         
         String s = new String(bt, 0, newSize, "UTF-8");
         if(ip.substring(11).equals("14"))
            s = String.format("<SYSTEM> : %s",s);
         else
            s = String.format("[User%s] : %s",ip.substring(11),s);
         //System.out.println(s);
         bt = s.getBytes("UTF-8");
         
         
         s = new String(bt, 0, bt.length, "UTF-8");
         //System.out.println("다시 변환 : "+s);
         
         os = ss.getOutputStream();
         os.write(bt);
      } catch (IOException e) {
      }
   }
}