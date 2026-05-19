package com.faceauth.recognition.model.dto;

import java.util.ArrayList;

import org.bytedeco.opencv.opencv_core.MatVector;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceMatrix {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private int matVectorId;
	private byte[] imageMatrix;
	private long matVectorPosition;	
}
