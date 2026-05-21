package com.faceauth.recognition.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] imageMatrix;
}
