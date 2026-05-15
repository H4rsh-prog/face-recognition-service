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
	@GetMapping("/opencv")
	public void initOpenCV() {
		this.service.captureImage(filePath);
		this.service.detectFaces(filePath);
	}
}
