package com.faceauth.recognition.model.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class FaceMatrix {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private int matVectorId;
	private byte[] imageMatrix;
}
