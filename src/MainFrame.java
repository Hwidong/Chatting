import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextArea textArea;

	private static Socket cs;
	private byte[] bt;
	private OutputStream os;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		//클라이언트 소스
		cs = new Socket();
		try {
			cs.connect(new InetSocketAddress("192.168.0.14", 5001));
			os = cs.getOutputStream();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		////////////////
		
		setResizable(false);
		setTitle("Chatting");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textField_1.getText();
				System.out.println("입력 : "+s);
				
				try {
					bt = s.getBytes("UTF-8");
					os.write(bt);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println(e1.getMessage());
				}
				textField_1.setText("");
			}
		});
		btnSend.setBounds(303, 424, 79, 37);
		contentPane.add(btnSend);
		
		textField_1 = new JTextField();
		textField_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				//익명객체가 아니면 여기서 바로 ButtonClicked 호출할수 있을것 같지만
				//익명이라 그냥 복붙함 ㅠㅠ
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String s = textField_1.getText();
					System.out.println("입력 : "+s);
					
					try {
						bt = s.getBytes("UTF-8");
						os.write(bt);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println(e1.getMessage());
					}
					textField_1.setText("");
				}
			}
		});
		textField_1.setBounds(12, 424, 279, 37);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(12, 10, (370-12*2), (404-10*2));
		textArea.setCaretPosition(textArea.getDocument().getLength());
		//textArea.setLineWrap(true);
		//contentPane.add(textArea);
		textArea.append("<SYSTEM> Bit R43 쳇팅방에 입장하셨습니다."+"\n");
		
		ReadingThread rt = new ReadingThread(cs,textArea);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(12, 10, 370, 404);
		textArea.repaint();
		contentPane.add(scrollPane);
		rt.start();
	}
}

class ReadingThread extends Thread{
	private JTextArea ta;
	private Socket cs;
	private byte[] bt;
	private int size;
	
	public ReadingThread(Socket cs, JTextArea ta) {
		this.ta = ta;
		this.cs = cs;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		while(true) {
			try {
				InputStream is = cs.getInputStream();
				bt = new byte[1024];
				size = is.read(bt);
				
				System.out.println("서버에서 받아옴");
				
				if (size == (-1)) {
					cs.close();
					break;
				}
				
				String s = new String(bt, 0, size, "UTF-8");
				System.out.println(s);
				
				ta.append(s+"\n");
				ta.setCaretPosition(ta.getDocument().getLength());

				
				//ta.setText(s);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//while
	}
}