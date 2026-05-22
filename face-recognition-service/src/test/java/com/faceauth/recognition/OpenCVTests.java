package com.faceauth.recognition;

import java.lang.reflect.Array;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;

import com.faceauth.recognition.service.MatConvertor;

public class OpenCVTests {
	@BeforeEach
	public void setUp() {
		System.out.println("TEST STACK STARTING [OPENCV]");
	}
	@AfterEach
	public void tearDown() {
		System.out.println("TEST STACK STARTING [OPENCV]");
	}
	@Test
	public void test() throws InterruptedException {
		
	}
}
