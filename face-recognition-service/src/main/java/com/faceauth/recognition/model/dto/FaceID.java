package com.faceauth.recognition.model.dto;

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
public class FaceID {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int ID;
	private String LABEL;
}
