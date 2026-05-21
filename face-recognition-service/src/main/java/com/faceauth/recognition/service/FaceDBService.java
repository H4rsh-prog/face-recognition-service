package com.faceauth.recognition.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faceauth.recognition.model.FaceIDRepo;
import com.faceauth.recognition.model.FaceMatrixRepo;
import com.faceauth.recognition.model.dto.FaceID;
import com.faceauth.recognition.model.dto.FaceMatrix;
import com.faceauth.recognition.model.dto.TrainingSet;

import jakarta.transaction.Transactional;

@Service
public class FaceDBService {
	@Autowired FaceIDRepo faceIdRepo;
	@Autowired FaceMatrixRepo faceMatRepo;
	
	public ArrayList<FaceMatrix> getMatList(){
		return (ArrayList<FaceMatrix>) this.faceMatRepo.findAll();		
	}
	
	public HashMap<Integer, String> getLabels(){
		HashMap<Integer, String> map = new HashMap<>();
		List<FaceID> faceIds =  (List<FaceID>) this.faceIdRepo.findAll();
		for(FaceID id : faceIds) {
			map.put(id.getID(), id.getLABEL());
		}
		return map;
	}
	
	public TrainingSet entityListToTrainingSet(ArrayList<FaceMatrix> list) {
		MatVector faces = new MatVector();
		Mat labels = new Mat(list.size(), 1, opencv_core.CV_32SC1);
		int listSize = list.size();
		try (IntIndexer indexer = labels.createIndexer()) {
			for(int i=0;i<listSize;i++) {
				faces.push_back(MatConvertor.fromByteArr(list.get(i).getImageMatrix()));
				indexer.put(i, 0, list.get(i).getMatVectorId());
			}
		}
		return new TrainingSet(faces, labels);
	}
	
	public void persistFace(MatVector face, int vectorId) {
		ArrayList<FaceMatrix> listOfEntities = new ArrayList<>(); 
		for(Mat mat : face.get()) {
			listOfEntities.add(new FaceMatrix(null, vectorId, MatConvertor.toByteArr(mat)));
		}
		this.faceMatRepo.saveAll(listOfEntities);
	}
	
	public void persistFaceId(int vectorId, String label) {
		this.faceIdRepo.save(new FaceID(vectorId, label));
	}

	public boolean idExists(int personId) {
		return this.faceIdRepo.existsById(personId);
	}

	public int getLabelId(String personLabel) {
		Optional<FaceID> existing = this.faceIdRepo.findByLABEL(personLabel);
		if(existing.isEmpty()) {
			return this.faceIdRepo.save(new FaceID(0, personLabel)).getID();
		}
		return existing.get().getID();
	}

	@Transactional
	public void removeFace(String label) {
		Optional<FaceID> opt = this.faceIdRepo.findByLABEL(label);
		if(opt.isPresent()) {
			int id = opt.get().getID();
			this.faceMatRepo.deleteByMatVectorId(id);
			this.faceIdRepo.deleteById(id);
		}
	}
}