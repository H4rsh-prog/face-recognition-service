package com.faceauth.recognition.controller;

import org.bytedeco.opencv.global.opencv_highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
	public void faceTrainingSet(@RequestParam("label") String label, @RequestParam(defaultValue = "20", name = "sample", required = false) int sampleSize) throws InterruptedException {
		this.service.collectFaceRecognitionData(label, sampleSize);
	}
	
	@GetMapping("/face/forget/{label}")
	public void faceTrainingSetRemoval(@PathVariable("label") String label) throws InterruptedException {
		this.service.forgetTrainingSet(label);
	}
	
	@GetMapping("/face/predict")
	public void predictFace() {
		if(this.service.trainFaceRecognition()) {
			this.service.predictFace();
		}
	}
}
