package com.faceauth.recognition.model;

import org.springframework.data.repository.CrudRepository;

import com.faceauth.recognition.model.dto.FaceMatrix;

public interface FaceMatrixRepo extends CrudRepository<FaceMatrix, String> {
	public void deleteByMatVectorId(int id);
}
