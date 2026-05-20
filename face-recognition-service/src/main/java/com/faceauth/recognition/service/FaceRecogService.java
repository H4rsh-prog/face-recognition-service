package com.faceauth.recognition.service;

import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import com.faceauth.recognition.model.FaceIDRepo;
import com.faceauth.recognition.model.FaceMatrixRepo;
import com.faceauth.recognition.model.HaarcascadeClassifiers;
import com.faceauth.recognition.model.dto.TrainingSet;

import java.util.ArrayList;
import java.util.HashMap;

import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceRecogService {
	@Autowired private FaceDBService faceDBService;

	@Autowired private HaarcascadeClassifiers classifiers;
	private LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
	private VideoCapture camera;
	private int errorCount = 0;
	
	public boolean initCamera() {
		this.camera = new VideoCapture(0);
		if(!this.camera.isOpened()) {
			System.err.println("COULD NOT INITIALIZE CAMERA, TRYING AGAIN IN 3 SECONDS");
			try {Thread.sleep(3000);} catch (Exception e) {e.printStackTrace();}
			this.errorCount++;
			if(errorCount>3) {
				System.err.println("TOO MANY ATTEMPTS TO INITIALIZE CAMERA, STANDING DOWN");
				this.errorCount=0;
				return false;
			}
			this.initCamera();
		}
		System.out.println("CAMERA INITIALIZED");
		return true;
	}
	
	public void captureImage(String filePath) {
		this.camera = new VideoCapture(0);
		Mat frame = new Mat();
		{// BLOCK TO HANDLE EXCEPTIONS AND RUNTIME EDGE CASES
			RuntimeException potentialException = null;
			this.initCamera();
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
		System.out.println(opencv_highgui.waitKey(0));
		opencv_highgui.destroyWindow("show");
	}
	
	public void realTimeFaceDetection() {
		Mat frame = new Mat();
		Mat gray = new Mat();
		RectVector detections = new RectVector();
		CascadeClassifier[] classifiers = this.classifiers.getFaceClassifiers();
		try {
			this.initCamera();
			int frameCount = 0;
			int x = 0;
			while(frameCount<1000) {
				if(this.camera.read(frame) && (!frame.empty())) {
					opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
						System.out.println("EXTRACTING NEXT CASCADE CLASSIFIER ["+0+"] REMAINING");
						classifiers[0].detectMultiScale(gray, detections);
						long detectionBufferSize = detections.size();
						for(long i=0;i<detectionBufferSize;i++) {
							System.out.println("DETECTED FACE ["+i+"]");
							Rect rect = detections.get(i);
							opencv_imgproc.putText(frame, "FACE_DETECTED_["+i+"]", new Point(rect), 1, 1, new Scalar(0, 255, 0, 0));
							opencv_imgproc.rectangle(frame, rect, new Scalar(0, 255, 0, 0));
							System.out.println("PLOTTED RECTANGLE AT FACE ["+i+"]");
						}
					opencv_highgui.imshow("show", frame);
					x = opencv_highgui.waitKey(1);
					if(x!=-1) break;
					frameCount++;
				}
			}
			opencv_highgui.destroyWindow("show");
		} finally {
			this.camera.release();
		}
	}
	
	public void collectFaceRecognitionData(int personId, String personLabel, int sampleSize) {
//		this.labelNames.put(personId, personLabel);
		if(faceDBService.idExists(personId)) {
			System.err.println("PERSON ID ALREADY EXISTS IN DATABASE");
			return;
		}
		MatVector faces = new MatVector();
		int trainingDataSetCount = 0;
		CascadeClassifier[] classifiers = this.classifiers.getFaceClassifiers();
		try {
			if(this.initCamera()) {
				Mat frame = new Mat(), gray = new Mat();
				RectVector detections = new RectVector();
				while(sampleSize>0) {
					if(!(this.camera.read(frame) && !frame.empty())) continue;
					System.out.println("FRAME READ");
					opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
					classifiers[0].detectMultiScale(gray, detections);
					long facesDetected = detections.size();
					if(facesDetected>0) {
						System.out.println("FACE FOUND");
						Rect rect = detections.get(0);
						Mat faceCropped = new Mat(gray, rect);
						Mat faceResized = new Mat();
						opencv_imgproc.resize(faceCropped, faceResized, new Size(100, 100));
						opencv_highgui.imshow(personLabel, faceResized);
						opencv_highgui.waitKey(0);
						faces.push_back(faceResized);
						if(trainingDataSetCount>20) {
							break;
						} else {
							trainingDataSetCount++;
							sampleSize--;
						}
					}
				}
				this.faceDBService.persistFace(faces, personId, personLabel);
			}
		} finally {
			opencv_highgui.destroyWindow(personLabel);
			this.camera.release();
		}
	}
	
	public void trainFaceRecognition() {
		TrainingSet trainingSet = this.faceDBService.entityListToTrainingSet(this.faceDBService.getMatList());
		this.recognizer.train(trainingSet.faces(), trainingSet.label());
		System.out.println("MODEL TRAINED");
	}
	
	public void predictFace() {
		HashMap<Integer, String> labels = new HashMap<>();
		CascadeClassifier[] classifiers = this.classifiers.getFaceClassifiers();
		Mat frame = new Mat(), gray = new Mat();
		RectVector detections = new RectVector();
		try {
			if(this.initCamera()) {
				int x = -1;
				while(this.camera.read(frame) && !frame.empty()) {
					opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
					classifiers[0].detectMultiScale(gray, detections);
					long facesDetected = detections.size();
					for(long i=0;i<facesDetected;i++) {
						Rect rect = detections.get(i);
						Mat faceCropped = new Mat(gray, rect);
						Mat faceResized = new Mat();
						opencv_imgproc.resize(faceCropped, faceResized, new Size(100, 100));
						int personId = this.recognizer.predict_label(faceResized);
						opencv_imgproc.putText(frame, labels.getOrDefault(personId, "UNKNOWN"), new Point(rect), 1, 1, new Scalar(0, 255, 0, 0));
						opencv_imgproc.rectangle(frame, rect, new Scalar(0, 255, 0, 0));
					}
					opencv_highgui.imshow("show", frame);
					x = opencv_highgui.waitKey(1);
					if(x!=-1) break;
				}
			}
		} finally {
			this.camera.release();
		}
	}
}
