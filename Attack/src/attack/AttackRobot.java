package attack;
/*Almost finished*/ 
/*can sense the color*/
import java.util.Random;
 
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
//import lejos.utility.Delay;
 
public class AttackRobot{
	
	DifferentialPilot pilot;
	DifferentialPilot pilot2;
	Random ran;
	
	//touch sensor
	TouchSensor touch;
	
	//color sensor
	EV3ColorSensor colorSensor;
	SampleProvider colorProvider;
	float[] colorSample;
	
	EV3ColorSensor colorSensor2;
	SampleProvider colorProvider2;
	float[] colorSample2;
	
	//sonar sensor
	EV3UltrasonicSensor sonarSensor;
	Ultrasonic ultra;
	
	public static void main(String[] args){
		new AttackRobot();
	}
	
	public AttackRobot(){
		pilot = new DifferentialPilot(1.5f, 7, Motor.B, Motor.C);
		pilot2 = new DifferentialPilot(1.5f, 7, Motor.A, Motor.D);
		ran = new Random();
		
		int i = 5000;
		int j = 15;
		int time = 5000;
		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1");
		Port s2 = brick.getPort("S2");
		Port s3 = brick.getPort("S3");
		Port s4 = brick.getPort("S4");
		
		EV3TouchSensor sensor = new EV3TouchSensor(s4);
		
		//sonar
		sonarSensor = new EV3UltrasonicSensor(s2);
		ultra = new Ultrasonic(sonarSensor.getMode("Distance"));
		
		//color
		colorSensor = new EV3ColorSensor(s3);
		colorProvider = colorSensor.getRedMode();
		colorSample = new float[colorProvider.sampleSize()];
		colorProvider.fetchSample(colorSample, 0);
		
		colorSensor2 = new EV3ColorSensor(s1);
		colorProvider2 = colorSensor2.getRedMode();
		colorSample2 = new float[colorProvider2.sampleSize()];
		colorProvider2.fetchSample(colorSample2, 0);
		
		
		//touch
		touch = new TouchSensor(sensor);
		
		//graphics
		
		
		//pilot.forward();
		//pilot2.backward();
		//Motor.A.backward();
		//Motor.D.backward();
		int end_flag = -1;
		int flag = -1;
		char cs = 'B'; char cs2 = 'B'; //initialization
		int y_axis = 0;
		while(true){
			

			colorProvider.fetchSample(colorSample, 0);
			colorProvider2.fetchSample(colorSample2, 0);
			
			GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
			g.drawString(colorSample[0]+" "+colorSample[0]+" "+cs+" "+cs2, 0, y_axis, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			
			y_axis += 15;
			
			
			if( colorSample[0] < 0.1 && colorSample2[0] < 0.05){ //BB
				cs = 'B'; cs2 = 'B';
				while(ultra.distance() > 0.25 ){
					//pilot.rotate(30); //상대방을 찾을때까지 회전
					Motor.B.backward();
					Motor.A.forward();						
					Motor.C.forward();						
					Motor.D.backward();
					circle_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
				}
				Sound.buzz();
				//찾았다면 상대방 방향으로 직진
				while(true) {
					
					//pilot.travel(10);
					//pilot2.travel(-10);
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					timer();
					//Motor.A.stop();
					//Sound.twoBeeps();
				
					if(touch.pressed())  continue;
			

					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					break;
				}
				colorProvider.fetchSample(colorSample, 0);
				colorProvider2.fetchSample(colorSample2, 0);
				if(colorSample[0] < 0.1 ) cs = 'B';
				else  cs = 'W';
				if(colorSample2[0] < 0.05) cs2 = 'B';
				else cs2 = 'W';
				
			}
			else if( colorSample[0] < 0.1 && colorSample2[0] >= 0.05){ //BW
				cs = 'B'; cs2 = 'W';
				Sound.beep();
				//forward
				Motor.B.forward();
				Motor.C.forward();
				Motor.A.backward();
				Motor.D.backward();
				white_timer();
				Motor.B.stop();
				Motor.C.stop();
				Motor.A.stop();
				Motor.D.stop();

			}
			else if( colorSample[0] >= 0.1 && colorSample2[0] < 0.05){ //WB
				cs = 'W'; cs2 = 'B';
				Sound.twoBeeps();
				//backward
				Motor.B.backward();
				Motor.C.backward();
				Motor.A.forward();
				Motor.D.forward();
				white_timer();
				Motor.B.stop();
				Motor.C.stop();
				Motor.A.stop();
				Motor.D.stop();				
			}
			else if( colorSample[0] >= 0.1 && colorSample2[0] >= 0.05){ //WW
				
				
				if( cs == 'B' && cs2 == 'B'){
					cs = 'W'; cs2 = 'W';
					Sound.twoBeeps();
					Sound.beep();
					//big circle
					Motor.B.backward();
					Motor.A.forward();						
					Motor.C.forward();						
					Motor.D.backward();
					circle_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					black_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();				
				}
				else if( cs == 'B' && cs2 == 'W'){
					cs = 'W'; cs2 = 'W';
					Sound.twoBeeps();
					Sound.twoBeeps();
					//forward
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					white_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
				}
				else if( cs == 'W' && cs2 == 'B'){
					cs = 'W'; cs2 = 'W';
					Sound.twoBeeps();
					Sound.twoBeeps();
					Sound.beep();
					//backward
					Motor.B.backward();
					Motor.C.backward();
					Motor.A.forward();
					Motor.D.forward();
					white_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();	
				}
				else if( cs == 'W' && cs2 == 'W'){
					cs = 'W'; cs2 = 'W';
					Sound.twoBeeps();
					Sound.twoBeeps();
					Sound.twoBeeps();
					Motor.B.backward();
					Motor.A.forward();						
					Motor.C.forward();						
					Motor.D.backward();
					circle_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					black_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
				
					//big circle
				}
				
				//cs = 'W'; cs2 = 'W';
				
				
			}
			
			
			/*	colorProvider.fetchSample(colorSample, 0);
				if(colorSample[0] >= 0.1){ //경기장 바깥 (하얀색) 이라면 
					while(true){
						//pilot2.rotate(-360);						
						//pilot.travel(30);
						//pilot2.rotate(-360);
						//pilot.travel(30); //회전하면서 안으로 들어오려고 시도
						Motor.B.backward();
						Motor.A.forward();						
						Motor.C.forward();						
						Motor.D.backward();
						circle_timer();
						Motor.B.stop();
						Motor.C.stop();
						Motor.A.stop();
						Motor.D.stop();
						
						Motor.B.forward();
						Motor.C.forward();
						Motor.A.backward();
						Motor.D.backward();
						white_timer();
						Motor.B.stop();
						Motor.C.stop();
						Motor.A.stop();
						Motor.D.stop();
						
						
						
						
						Sound.buzz();
						
					
						colorProvider.fetchSample(colorSample, 0); //현재 위치 바닥 색 
						
						if(colorSample[0] < 0.1) break; //경기장 안으로 들어왔으면 break 후 
						time-=1;
						if(time == 0){ 
							end_flag = 1;
							Sound.twoBeeps();
							break;
						} 
						
						continue;
					}
									
					
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					black_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					break; //여기서 한번 더 break해서 다시 맨 처음 탐색으로 돌아감
				}
			}
			if(end_flag == 1) break;*/
		}//end of first while loop
	}//end of attack robot class
	
	public static void timer(){
		try{
			Thread.sleep(3000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void circle_timer(){//it is good already
		try{
			Thread.sleep(200);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void black_timer(){
		try{
			Thread.sleep(1800);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void white_timer(){
		try{
			Thread.sleep(1000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
}