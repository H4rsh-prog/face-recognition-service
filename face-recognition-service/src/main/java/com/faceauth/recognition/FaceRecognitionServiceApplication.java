package com.faceauth.recognition;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FaceRecognitionServiceApplication {
	
	static {
		Loader.load(opencv_java.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(FaceRecognitionServiceApplication.class, args);
	}

}
