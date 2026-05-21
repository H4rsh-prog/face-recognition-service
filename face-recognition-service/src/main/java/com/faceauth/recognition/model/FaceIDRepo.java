package com.faceauth.recognition.model;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.faceauth.recognition.model.dto.FaceID;

public interface FaceIDRepo extends CrudRepository<FaceID, Integer> {
	public Optional<FaceID> findByLABEL(String label);
	public void deleteByLABEL(String label);
}
