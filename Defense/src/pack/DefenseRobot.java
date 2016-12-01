package pack;
 
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
 
public class DefenseRobot{
	
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
		new DefenseRobot();
	}
	
	public DefenseRobot(){
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
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		
		// states
		int s_1,s_2;
		pilot.forward();
		pilot2.backward();
		//pilot.forward();
		//pilot2.backward();
		//Motor.A.backward();
		//Motor.D.backward();
		int end_flag = -1;
		int flag = -1;
		//int cnt = 0;
		s_1 = 0; s_2 = 0; // black is 0, white is 1
		while(true){
			i -= 1;
			//Delay.msDelay(2);
			
			colorProvider.fetchSample(colorSample, 0);
			
			if( colorSample[0] >= 0.1 ){//out of boundary
				if( colorSample2[0] >=0.05){ // WW가 된 경우
					if(( s_1 == 0 && s_2 == 0) || (s_1 == 1 && s_2 == 1)){
						j+=15;
						Sound.beep();
						//회전
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
						s_1 = 1; s_2 = 1;
					}
					else if( s_1 == 0 && s_2== 1){ // 뒤black 앞white
						Sound.twoBeeps();
						//pilot.backward();
						Motor.B.backward();
						Motor.C.backward();
						Motor.A.forward();
						Motor.D.forward();
						white_timer();
						Motor.B.stop();
						Motor.C.stop();
						Motor.A.stop();
						Motor.D.stop();
						s_1 = 1; s_2 = 1;
					}
					else if( s_1 == 1 && s_2 == 0){ 
						//pilot.forward();
						Sound.beep();
						Sound.twoBeeps();
						Motor.B.forward();
						Motor.C.forward();
						Motor.A.backward();
						Motor.D.backward();
						white_timer();
						Motor.B.stop();
						Motor.C.stop();
						Motor.A.stop();
						Motor.D.stop();
						s_1 = 1; s_2 =1;
					}
					s_1 = 1; s_2 = 1;
				}
				else if( colorSample2[0] <0.05){ //뒤 WB가 된 상황
					//pilot.backward();
					Sound.twoBeeps();
					Sound.twoBeeps();
					/*cnt++;
					if( cnt >=2){
						Motor.B.forward();
						Motor.C.forward();
						Motor.A.backward();
						Motor.D.backward();
						white_timer();
						Motor.B.stop();
						Motor.C.stop();
						Motor.A.stop();
						Motor.D.stop();
						//cnt = 0;
					}*/
					//else{

					Motor.B.backward();
					Motor.C.backward();
					Motor.A.forward();
					Motor.D.forward();
					white_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					//}
					s_1 = 0; s_2 = 1;
				}
				
				/*
				pilot.stop();
				pilot2.stop();
				//pilot.travel(-6);
				
				pilot.rotate(360);
				pilot.travel(10);
				pilot.rotate(360);
				pilot.travel(10);
				*/
				//Motor.B.forward();
				
				//pilot2.rotate(-180);
				//pilot2.rotate(-90);
				
				
			}
			else{//앞이 black
				if( colorSample2[0] < 0.05){ // 뒤도 black.. (BB)ok인 상황
					pilot.stop();
					pilot2.stop();
					//pilot.rotate(360);
					pilot.rotate(90);
					pilot.travel(10);
					//pilot.rotate(360);
					pilot.rotate(90);
					pilot.travel(10);
					if( ultra.distance() < 0.1  || touch.pressed()){
						GraphicsLCD g3 = BrickFinder.getDefault().getGraphicsLCD();
						g3.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, j, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
						i = 5000;
						pilot.rotate(30);
						pilot.travel(5);
						pilot.forward();
						pilot2.backward();
					}
					//cnt = 0;
					s_1 = 0; s_2 = 0;
				}
				else{ // 뒤는 white BW가 된 상황
					Sound.beep();
					Sound.twoBeeps();Sound.beep();
					Sound.twoBeeps();
					//pilot.forward();
					Motor.B.forward();
					Motor.C.forward();
					Motor.A.backward();
					Motor.D.backward();
					white_timer();
					Motor.B.stop();
					Motor.C.stop();
					Motor.A.stop();
					Motor.D.stop();
					s_1 = 1; s_2 = 0;
				}
				
				
			}
			/*if( i <= 0){
				pilot.stop();		
				
				
				GraphicsLCD g2 = BrickFinder.getDefault().getGraphicsLCD();
				g2.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, 55, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
				
				//post graphics
				//Sound.twoBeeps();
				timer();
				
				sensor.close();
				System.exit(0);
			}*/
			
		}
	}
	
	public static void timer(){
		try{
			Thread.sleep(4000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void circle_timer(){
		try{
			Thread.sleep(100);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void black_timer(){
		try{
			Thread.sleep(1500);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public static void white_timer(){
		try{
			Thread.sleep(2000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
}

/*package pack;

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
import lejos.utility.Delay;

public class DefenseRobot{
	
	DifferentialPilot pilot;
	DifferentialPilot pilot2;
	Random ran;
	
	//touch sensor
	TouchSensor touch;
	
	//color sensor
	EV3ColorSensor colorSensor;
	SampleProvider colorProvider;
	float[] colorSample;
	
	//sonar sensor
	EV3UltrasonicSensor sonarSensor;
	Ultrasonic ultra;
	
	public static void main(String[] args){
		new DefenseRobot();
	}
	
	public DefenseRobot(){
		pilot = new DifferentialPilot(1.5f, 7, Motor.B, Motor.C);
		pilot2 = new DifferentialPilot(1.5f, 7, Motor.A, Motor.D);
		ran = new Random();
		
		int i = 5000;
		int j = 15;
		
		Brick brick = BrickFinder.getDefault();
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
		
		//touch
		touch = new TouchSensor(sensor);
		
		//graphics
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		
		
		pilot.forward();
		pilot2.backward();
		
		//Motor.A.backward();
		//Motor.D.backward();
		
		while(true){
			i -= 1;
			//Delay.msDelay(2);
			
			colorProvider.fetchSample(colorSample, 0);
			
			if( colorSample[0] >= 0.1 ){//out of boundary
				
				GraphicsLCD g3 = BrickFinder.getDefault().getGraphicsLCD();
				g3.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, j, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
				j+=15;
				
				pilot.stop();
				pilot2.stop();
				//pilot.travel(-6);
				
				pilot.rotate(360);
				pilot.travel(10);
				pilot.rotate(360);
				pilot.travel(10);
				
				//Motor.B.forward();
				
				//pilot2.rotate(-180);
				//pilot2.rotate(-90);
				
				
			}
			else{
				if( ultra.distance() < 0.1  || touch.pressed()){
					//i = 5000;
					pilot.forward();
					pilot2.backward();
				}
			}
			if( i <= 0){
				pilot.stop();		
				
				
				GraphicsLCD g2 = BrickFinder.getDefault().getGraphicsLCD();
				g2.drawString("col: "+colorSample[0]+" dis: " +ultra.distance()+" tch: "+touch.pressed(), 0, 55, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
				
				//post graphics
				//Sound.twoBeeps();
				timer();
				
				sensor.close();
				System.exit(0);
			}
			
		}
	}
	
	public static void timer(){
		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}	

}
*/