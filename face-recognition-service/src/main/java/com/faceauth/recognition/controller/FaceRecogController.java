package com.faceauth.recognition.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.faceauth.recognition.service.FaceRecogService;

@RestController
public class FaceRecogController {
	@Autowired private FaceRecogService service;
	private String filePath = "./temp_asset/captured_image.jpg";
	
	@GetMapping("/")
	public Object initBase() {
		record response(String status) {}
		return new response("Running");
	}
	@GetMapping("/face")
	public void initOpenCV() {
		this.service.captureImage(filePath);
		this.service.detectFaces(filePath);
	}
	@GetMapping("/face/realtime")
	public void faceDetectionRealTime() {
		this.service.realTimeFaceDetection();
	}
	
	@GetMapping("/face/train")
	public void faceRecognitionRealTime() throws InterruptedException {
		int choice = 0;
		while(choice != -1) {
			
		}
		this.service.trainFaceRecognition();
		Thread.sleep(2000);
		this.service.predictFace();
	}
}
