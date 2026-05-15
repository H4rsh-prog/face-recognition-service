package com.faceauth.recognition.model;

import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class HaarcascadeClassifiers {
	private CascadeClassifier[] faceClassifiers = {
				new CascadeClassifier("./haarcascade_files/haarcascade_frontalface_default.xml"),
				new CascadeClassifier("./haarcascade_files/haarcascade_profileface.xml"),
				new CascadeClassifier("./haarcascade_files/haarcascade_frontalface_alt.xml"),
				new CascadeClassifier("./haarcascade_files/haarcascade_frontalface_alt_tree.xml"),
				new CascadeClassifier("./haarcascade_files/haarcascade_frontalface_alt2.xml")
			};
}
