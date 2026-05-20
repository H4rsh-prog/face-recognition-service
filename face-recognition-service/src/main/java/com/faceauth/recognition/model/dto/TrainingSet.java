package com.faceauth.recognition.model.dto;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

public record TrainingSet(
			MatVector faces,
			Mat label
		) {}
