package com.faceauth.recognition.model.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class FaceID {
	@Id
	private int ID;
	private String LABEL;
}
