package com.faceauth.recognition.service;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

@Service
public class MatConvertor {
	
	public static byte[] toByteArr(Mat mat) {
		BytePointer buffer = new BytePointer();
		opencv_imgcodecs.imencode(".png", mat, buffer);
		byte[] result = new byte[(int) buffer.limit()];
		buffer.get(result);
		return result;
	}
	
	public static Mat fromByteArr(byte[] byteArr) {
		Mat mat = new Mat(byteArr);
		return opencv_imgcodecs.imdecode(mat, opencv_imgcodecs.IMREAD_COLOR);
	}
	
	public static boolean areEqual(Mat a, Mat b) {
	    if (a.rows() != b.rows() || a.cols() != b.cols() || a.type() != b.type())
	        return false;
	    BytePointer aPtr = a.data();
	    BytePointer bPtr = b.data();
	    long n = a.total() * a.channels();
	    return aPtr.limit(n).equals(bPtr.limit(n));
	}
}
