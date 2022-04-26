import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;


public class Main {
	
	static JFrame frame = new JFrame();
	static int frame_width = 650;
	static int frame_height = 800;
	static int frame_x = 720-frame_width/2;
	static int frame_y = 30;
	static Color background_color = new Color(5,0,30);
	static Color sqColor = Color.gray.brighter();
	static int sqSize = 70;
	static int square_gap = ((frame_width-sqSize*8)/9);
	static JLabel zero = new JLabel("0");
	static int buttonWidth = (int)(sqSize*.65);
	static int buttonGap = 5;
	static int buttons_yOffset = 30+(sqSize*6)+(square_gap*6)+15;
	static int buttons_xOffset = (frame_width-(buttonWidth*10)-(buttonGap*10))/2;
	static List<List<JTextField>> lines = new ArrayList();
	static List<JButton> buttons = new ArrayList();
	static String button_vals = "1234567890+-*/=ED";
	static int linesCompleted = 0;
	static boolean[] buttonsGreen = new boolean[20];
	static List<String> answers = new ArrayList();
	static String answer;
	
	
	public static void main(String[] args) throws AWTException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub

		uploadAnswers("/Users/jkaruntzos/eclipse-workspace/Nerdle/src/answers.txt");
		newAnswer();
		
		frame.setBounds(frame_x,frame_y,frame_width,frame_height);
		frame.setBackground(background_color);
		frame.setDefaultCloseOperation(3);
		frame.setTitle("Nerdle");
		
		
		//Text Fields
		int currX = square_gap;
		int currY = 30;
		for(int r = 0; r < 6; r++) {
			List<JTextField> line = new ArrayList();
			for(int c = 0; c < 8; c++) {
			
				JTextField curr = new JTextField();
				curr.setBounds(currX+2, currY+2, sqSize-5, sqSize-5);
				curr.setBackground(Color.gray.brighter());
				curr.setBorder(null);
				curr.setHorizontalAlignment(JTextField.CENTER);
				curr.setFont(new Font("Arial", Font.BOLD, 35));
				if(r==0) {
					curr.setEnabled(true);
				}
				else {
					curr.setEnabled(false);
				}
				
				line.add(curr);
				currX += (square_gap+sqSize);

			}
			lines.add(line);
			currX = square_gap;
			currY += (square_gap+sqSize);
		}
		
		JPanel pn = new JPanel() {
			@Override
			public void paint(Graphics g) {
				
				int currX = square_gap;
				int currY = 30;
				g.setColor(sqColor);
				
				for(int r = 0; r < 6; r++) {
					for(int c = 0; c < 8; c++) {
						
						g.fillRoundRect(currX, currY, sqSize, sqSize, 8, 8);
						currX += (square_gap+sqSize);
						
					}
					currX = square_gap;
					currY += (square_gap+sqSize);
				}
				
				
			}
		};
		
		
		//Buttons
		int i = 0;
		currX = buttons_xOffset;
		currY = buttons_yOffset;
		for(int r = 0; r < 2; r++) {
			for(int c = 0; c < 10; c++) {
				
				if(r == 1 && c > 6)
					break;
				
				String l = button_vals.substring(i,i+1);
				
				JButton b = new JButton(l);
				if(b.getText().equals("E")) {
					b.setText("Enter");
					b.setBounds(currX, currY, (buttonWidth+buttonGap)*2, sqSize);
				}
				else if(b.getText().equals("D")) {
					b.setText("Delete");
					b.setBounds(currX+buttonWidth+buttonGap*2, currY, buttonWidth*2+buttonGap, sqSize);
				}
				else {
					b.setBounds(currX, currY, buttonWidth, sqSize);
				}
				
				b.setBackground(Color.gray.brighter());
				b.setBorder(new RoundBtn(10));
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(b.getText().equals("Enter") == false && b.getText().equals("Delete") == false) {
							boolean found = false;
							for(List<JTextField> line: lines) {
								for(JTextField tf: line) {
									if(tf.isEnabled()) {
										if(tf.getText().equals("")) {
											found = true;
											tf.setText(b.getText());
											break;
										}
									}
								}
								if(found == true)
									break;
							}
						}
						else if(b.getText().equals("Enter")){
							List<JTextField> line = lines.get(linesCompleted);
							String eq = "";
							for(JTextField tf: line) {
								eq += tf.getText();
							}
							
							
							if(isValidEquation(eq)) {
								//VALID EQUATION
								//HIGHLIGHT COMPARISONS
								
								String[] colors = compareToAnswer(eq);
								int i = 0;
								for(JTextField tf: line) {
									if(colors[i].equals("darkGray"))
										tf.setBackground(Color.DARK_GRAY.darker());
									else if(colors[i].equals("yellow"))
										tf.setBackground(Color.yellow.darker());
									else if(colors[i].equals("green"))
										tf.setBackground(Color.green.darker());
									tf.setDisabledTextColor(Color.white);
									tf.setEnabled(false);
									i++;
								}
								
								
								for(int c = 0; c < eq.length(); c++) {
									String curr = eq.substring(c,c+1);
									for(int z = 0; z < buttons.size(); z++) {
										JButton b = buttons.get(z);
										if(b.getText().equals(curr)) {
											if(b.getName() == null) {
		
												if(colors[c].equals("yellow"))
													b.setBackground(Color.yellow.darker());
												else if(colors[c].equals("darkGray"))
													b.setBackground(Color.DARK_GRAY.darker());
												else if(colors[c].equals("green")) {
													b.setBackground(Color.green.darker());
													b.setName("green");
												}
												b.setForeground(Color.white);
				
											}
										}
									}
								}
								
								if(eq.equals(answer)) {
									//WINNING MESSAGE
									winLosePopup(true);
								}
								else {
								
									if(linesCompleted != 5) {
										linesCompleted++;
										for(JTextField tf: lines.get(linesCompleted)) {
											tf.setEnabled(true);
										}
									}
									else {
										winLosePopup(false);
									}
								
								}
							}
							//INVALID EQUATION
							else {
								JLabel inVLabel = new JLabel("Invalid Input");
								inVLabel.setHorizontalAlignment(SwingConstants.CENTER);
								JOptionPane.showMessageDialog(frame, inVLabel, "Invalid", JOptionPane.PLAIN_MESSAGE, null);
							}
								
						}
						//DELETE
						else if(b.getText().equals("Delete")) {
							List<JTextField> line = lines.get(linesCompleted);
							for(int i = line.size()-1; i >= 0; i--) {
								JTextField curr = line.get(i);
								if(curr.getText().equals("") == false) {
									curr.setText("");
									break;
								}
							}
						}
					}
				});
				b.setOpaque(true);
				buttons.add(b);
				frame.add(b);
				
				currX += buttonWidth+buttonGap;
				i++;
			}
			currX = buttons_xOffset+buttonWidth/2;
			currY += (buttonGap+sqSize);
		}
		
		
		
		
		for(List<JTextField> tfl: lines) {
			for(JTextField tf: tfl) {
				frame.add(tf);
			}
			
		}
		
		
		frame.add(zero);
		frame.add(pn);
		frame.setVisible(true);
	}
	
	
	public static void winLosePopup(boolean win) {

		String[] newGameOps = {"New Game","Exit"};
		int n;
		if(win) {
			JLabel wLabel = new JLabel("Winner!\nPlay Again?");
			wLabel.setHorizontalAlignment(SwingConstants.CENTER);
			n = JOptionPane.showOptionDialog(frame,
					wLabel,
					"WIN",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.CLOSED_OPTION,
					null,     //do not use a custom Icon
					newGameOps,  //the titles of buttons
					newGameOps[0]);
			
			
		}
		else {
			JLabel wLabel = new JLabel("Loser!\nAnswer was: "+answer);
			wLabel.setHorizontalAlignment(SwingConstants.CENTER);
			n = JOptionPane.showOptionDialog(frame,
					wLabel,
					"LOSE",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.CLOSED_OPTION,
					null,     //do not use a custom Icon
					newGameOps,  //the titles of buttons
					newGameOps[0]);
			
		}
		if(n==JOptionPane.YES_OPTION)
			newGame();
		else {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	
	
	public static void newGame() {
		int l = 0;
		for(List<JTextField> line: lines) {
			for(JTextField tf: line) {
				tf.setBackground(sqColor);
				tf.setText("");
				if(l != 0)
					tf.setEnabled(false);
				else
					tf.setEnabled(true);
			}
			l++;
		}
		for(JButton b: buttons) {
			b.setBackground(Color.LIGHT_GRAY);
			b.setForeground(Color.black);
			b.setName(null);
		}
		linesCompleted = 0;
		newAnswer();
	}
	
	
	//Compares s to answer and returns the colors corresponding to each letter in s
	public static String[] compareToAnswer(String s) {
		String[] out = new String[s.length()];
		char[] sa = s.toCharArray();
		
		for(int i = 0; i < sa.length; i++) {
			char c = sa[i];
			if(answer.charAt(i) == c) {
				out[i] = "green";
			}
			else if(answer.indexOf(c) == -1) {
				out[i] = "darkGray";
			}
			else {
				out[i] = "yellow";
			}
		}
		
		return out;
	}
	
	
	//Fills answers with answers.txt
	public static void uploadAnswers(String file) throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    
		    String line = br.readLine();
		    
		    while (line != null) {
		    	//System.out.println(line);
		    	answers.add(line);
		        line = br.readLine();
		    }
		}
	}
	
	//Fills answer with random answer from answers.txt
	public static void newAnswer() {
		int min = 0;
		int max = answers.size()-1;
		int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
		answer = answers.get(random_int);
	}
	
	
	
	//Returns true if the string is a valid equation
	public static boolean isValidEquation(String s) {
		
		if(s.indexOf("=") == -1) {
			return false;
		}
		
		if(s.length() != 8)
			return false;
		
		for(int i = 0; i < s.length(); i++) {
			char curr = s.charAt(i);
			if(!isNumeric(curr) && !isOp(curr)) {
				return false;
			}
		}
		
		String left = s.split("=")[0];
		String right = s.split("=")[1];
		if(convert(left) != convert(right)) {
			return false;
		}
		
		
		return true;
	}
	
	//Returns true if c is numeric
	public static boolean isNumeric(char c) {
		String ns = "0123456789";
		return (ns.indexOf(c) != -1);
	}
	
	///Returns true if c is an operation
	public static boolean isOp(char c) {
		String ops = "*/-+=";
		return (ops.indexOf(c) != -1);
	}
	
	//Converts an equation to a number following order of operations
	public static int convert(String eq) {
		
		
		List<String> nums = new ArrayList();
		List<String> ops = new ArrayList();
		String prev = "";
		int opsSize = 1;
		int priority_index = -1;
		String priority_op = "";
		
		while(opsSize > 0) {
		
			priority_index = -1;
			priority_op = "";
			prev = "";
			nums = new ArrayList();
			ops = new ArrayList();
				
			for(int i = 0; i < eq.length(); i++) {
				String curr = eq.substring(i,i+1);
				if(curr.equals("+") || curr.equals("-") || curr.equals("*") || curr.equals("/")) {
					if(priority_index == -1) {
						priority_index = ops.size();
						priority_op = curr;
					}
					else if(priority_op.equals("+") || priority_op.equals("-")) {
						if(curr.equals("*") || curr.equals("/")) {
							priority_op = curr;
							priority_index = ops.size();
						}
					}
					ops.add(curr);
					nums.add(prev);
					prev = "";
				}
				else {
					prev += curr;
				}
				
			}
			nums.add(prev);
			
			if(ops.size() == 0) {
				return Integer.valueOf(eq);
			}
			
			
			int a = Integer.valueOf(nums.get(priority_index));
			int b = Integer.valueOf(nums.get(priority_index+1));
			String n = "";
			int out = 0;
			if(priority_op.equals("+")) {
				out = a+b;
			}
			else if(priority_op.equals("-")) {
				out = a-b;
			}
			else if(priority_op.equals("*")) {
				out = a*b;
			}
			else {
				out = a/b;
			}
			// [5,2,3]
			// [+,*]
			int num_index = 0;
			int op_index = 0;
			while(num_index != nums.size()) {
				
				if(num_index == priority_index) {
					n += Integer.toString(out);
					num_index+=2;
					op_index++;
					if(op_index < ops.size()) {
						n += ops.get(op_index);
						op_index++;
					}
				}
				else {
					if(num_index < nums.size()) {
						n += nums.get(num_index);
						if(op_index < ops.size())
							n += ops.get(op_index);
						op_index++;
						num_index++;
					}
				}
				
				
			}
		
			opsSize = ops.size();
			
			eq = n;
			
		}
		
		
		return Integer.valueOf(eq);
	}
	

}

class RoundBtn implements Border 
{
    private int r;
    RoundBtn(int r) {
        this.r = r;
    }
    public Insets getBorderInsets(Component c) {
        return new Insets(this.r+1, this.r+1, this.r+2, this.r);
    }
    public boolean isBorderOpaque() {
        return true;
    }
    public void paintBorder(Component c, Graphics g, int x, int y, 
    int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, r, r);
    }
}


