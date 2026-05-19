package com.faceauth.recognition.model;

import org.springframework.data.repository.CrudRepository;

import com.faceauth.recognition.model.dto.FaceID;

public interface FaceIDRepo extends CrudRepository<FaceID, Integer> {}
