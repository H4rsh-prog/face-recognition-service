package com.faceauth.recognition.service;

import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.springframework.stereotype.Service;

@Service
public class FaceRecogService {
	private CascadeClassifier faceCascade = new CascadeClassifier("haarcascade_frontalface_default.xml");
	private VideoCapture camera;
	private int errorCount = 0;
	
	public void captureImage(String filePath) {
		this.camera = new VideoCapture(0);
		Mat frame = new Mat();
		{// BLOCK TO HANDLE EXCEPTIONS AND RUNTIME EDGE CASES
			RuntimeException potentialException = null;
			if(!this.camera.isOpened()) {
				System.err.println("COULD NOT INITIALIZE CAMERA, TRYING AGAIN IN 3 SECONDS");
				try {Thread.sleep(3000);} catch (Exception e) {e.printStackTrace();}
				this.errorCount++;
				if(errorCount>3) {
					System.err.println("TOO MANY ATTEMPTS TO INITIALIZE CAMERA");
					this.errorCount=0;
					return;
				}
				this.captureImage(filePath);
			} else {
				System.out.println("CAMERA INITIALIZED");
			}
			try {
				if(!this.camera.read(frame)) throw new RuntimeException("could not read frame");
				else {
					System.out.println("FRAME CAPTURED");
				}
				if(frame.empty()) throw new RuntimeException("frame came up empty");
			} catch (RuntimeException e) {potentialException = e;}
			finally {
				this.camera.release();
				System.out.println("CAMERA RELEASED");
				if(potentialException!=null) throw potentialException;
			}
		}
		opencv_imgcodecs.imwrite(filePath, frame);
	}
	
	public void detectFaces(String filePath) {
		Mat src = opencv_imgcodecs.imread(filePath);
		RectVector detections = new RectVector();
		this.faceCascade.detectMultiScale(src, detections);
		long detectionBufferSize = detections.size();
		for(long i=0;i<detectionBufferSize;i++) {
			Rect rect = detections.get(i);
			opencv_imgproc.rectangle(src, rect, new Scalar(0, 255, 0, 0));
		}
		opencv_imgcodecs.imwrite(filePath, src);
	}
}
