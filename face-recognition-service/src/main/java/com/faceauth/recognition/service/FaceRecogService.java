package com.faceauth.recognition.service;

import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import com.faceauth.recognition.model.HaarcascadeClassifiers;

import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.CvArr;
import org.bytedeco.opencv.opencv_core.CvArrArray;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceRecogService {

	@Autowired private HaarcascadeClassifiers classifiers;
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
		System.out.println("DETECTING FACES");
		Mat src = opencv_imgcodecs.imread(filePath);
		Mat gray = new Mat();
		opencv_imgproc.cvtColor(src, gray, opencv_imgproc.COLOR_BGR2GRAY);
		RectVector detections = new RectVector();
		CascadeClassifier[] classifiers = this.classifiers.getFaceClassifiers();
		boolean detected = false;
		for(int j=classifiers.length-1;j>=0;j--) {
			System.out.println("EXTRACTING NEXT CASCADE CLASSIFIER ["+j+"] REMAINING");
			classifiers[j].detectMultiScale(gray, detections);
			long detectionBufferSize = detections.size();
			for(long i=0;i<detectionBufferSize;i++) {
				detected = true;
				System.out.println("DETECTED FACE ["+i+"]");
				Rect rect = detections.get(i);
				opencv_imgproc.putText(src, "FACE_DETECTED_BY_CLASSIFIER ["+j+"]", new Point(rect), 1, 1, new Scalar(0, 255, 0, 0));
				opencv_imgproc.rectangle(src, rect, new Scalar(0, 255, 0, 0));
				System.out.println("PLOTTED RECTANGLE AT FACE ["+i+"]");
			}
		}
		if(detected) System.out.println("FACE(S) DETECTED");
		opencv_imgcodecs.imwrite(filePath, src);
		opencv_highgui.imshow("show", src);
		opencv_highgui.waitKey(5000);
		opencv_highgui.destroyWindow("show");
	}
}
