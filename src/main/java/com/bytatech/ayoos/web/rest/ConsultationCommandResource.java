package com.bytatech.ayoos.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.ConsultationRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.DefaultInfoRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.InitiateMedicalSummaryRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.ParamedicalExaminationRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.PrescriptionRequest;
import com.bytatech.ayoos.client.alfresco_rest_api.model.SiteBodyCreate;
import com.bytatech.ayoos.client.digitalsigning.model.SigningCredentials;
import com.bytatech.ayoos.domain.Prescription;
import com.bytatech.ayoos.service.ConsultationCommandService;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * REST controller for managing MedicalSummary.
 */
@RestController
@RequestMapping("/api/command")
public class ConsultationCommandResource {

	private final Logger log = LoggerFactory.getLogger(ConsultationCommandResource.class);

    private static final String ENTITY_NAME = "consultation";
    
    @Autowired
    ConsultationCommandService consultationCommandService;
    
    @PostMapping("/initiateConsultation")
    public String initiateConsultationSummary(@RequestBody InitiateMedicalSummaryRequest medicalSummaryRequest) {
    	
    	return consultationCommandService.initiate(medicalSummaryRequest);
		 
    }
    
    @PostMapping("/collectDefaultInfo/{taskId}")
    public void collectInformations(@PathVariable String taskId, @RequestBody DefaultInfoRequest defaultInfoRequest){
    	
    	consultationCommandService.collectDefaultInfo(taskId,defaultInfoRequest);
    }
    
    @PostMapping("/collectConsultationInfo/{taskId}")
    public void collectConsultationInformations(@PathVariable String taskId, @RequestBody ConsultationRequest consultationRequest){
    	
    	consultationCommandService.collectConsultationInfo(taskId,consultationRequest);
    }
    
    @PostMapping("/collectParamedicalExaminationInfo/{taskId}")
    public void collectParamedicalExaminationInformations(@PathVariable String taskId, @RequestBody ParamedicalExaminationRequest paramedicalExxminationRequest){
    	
    	consultationCommandService.collectParamedicalExaminationInfo(taskId,paramedicalExxminationRequest);
    }
    
    /*@PostMapping("/collectExaminationInfo/{taskId}")
    public void collectExaminationInformations(@PathVariable String taskId,ExaminationRequest examinationRequest){
    	consultationCommandService.collectExaminationInfo(taskId, examinationRequest);
    }*/
    
   /* @PostMapping("/collectExaminationResultInfo/{taskId}")
    public void collectExaminationResultsInformations(@PathVariable String taskId, ExaminationResult examinationResult){
    	consultationCommandService.collectExaminationResultInfo(taskId, examinationResult);
    	
    }*/
    
    
    @PostMapping("/collectPrescriptionInfo/{taskId}")
    public void collectPrescriptionInformations(@PathVariable String taskId, @RequestBody Prescription prescriptionRequest ){
    	consultationCommandService.collectPrescriptionInfo(taskId,prescriptionRequest);
    }
    
    //create report for prescription with jsaper
    
   @PostMapping("/createPrescriptionReport")
    public void createPrescriptionReports( ){
    	
    	consultationCommandService.createPrescriptionReport();
    }
    
   
   @PostMapping("/createPrescriptionReportAndSave")
   public void createPrescriptionReportAndSave( ){
   	
   	consultationCommandService.createPrescriptionReportAndSave();
   }
    
   @PostMapping("/sign")
	public String sign(@RequestBody SigningCredentials signingCredentials) {
		
	   consultationCommandService.sign(signingCredentials);
		return "+++++++successfully signed+++++++";
   }
   
   @PostMapping("/upload")
   public String upload(MultipartFile file){
   	
   	consultationCommandService.upload(file);
   	return "success";
   }
   
   @PostMapping("/createSite")
	public String createSite(@RequestBody SiteBodyCreate siteBodyCreate) {
		
		//System.out.println(sitesApi.createSite(siteBodyCreate, false, false, new ArrayList<>()).getBody());
		consultationCommandService.createSite(siteBodyCreate); 
		return "succes";
	}
   
   
}
