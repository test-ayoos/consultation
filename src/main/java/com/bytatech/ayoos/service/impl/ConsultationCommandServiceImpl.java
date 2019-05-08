package com.bytatech.ayoos.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.bytatech.ayoos.client.activiti_rest_api.api.FormsApi;
import com.bytatech.ayoos.client.activiti_rest_api.api.ProcessInstancesApi;
import com.bytatech.ayoos.client.activiti_rest_api.api.TasksApi;
import com.bytatech.ayoos.client.activiti_rest_api.model.DataResponse;
import com.bytatech.ayoos.client.activiti_rest_api.model.FormDataResponse;
import com.bytatech.ayoos.client.activiti_rest_api.model.ProcessInstanceCreateRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.ProcessInstanceResponse;
import com.bytatech.ayoos.client.activiti_rest_api.model.RestFormProperty;
import com.bytatech.ayoos.client.activiti_rest_api.model.RestVariable;
import com.bytatech.ayoos.client.activiti_rest_api.model.SubmitFormRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.ConsultationDetails;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.ConsultationRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.DefaultInfoRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.InitiateMedicalSummaryRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.ParamedicalExaminationRequest;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.PatientInfo;
import com.bytatech.ayoos.client.activiti_rest_api.model.consultation.PrescriptionRequest;
import com.bytatech.ayoos.client.alfresco_rest_api.api.NodesApi;
import com.bytatech.ayoos.client.alfresco_rest_api.api.SitesApi;
import com.bytatech.ayoos.client.alfresco_rest_api.model.NodeBodyCreate;
import com.bytatech.ayoos.client.alfresco_rest_api.model.NodeEntry;
import com.bytatech.ayoos.client.alfresco_rest_api.model.SiteBodyCreate;
import com.bytatech.ayoos.client.digitalsigning.api.DigitalSigningApi;
import com.bytatech.ayoos.client.digitalsigning.api.DigitalSigningUploadApi;
import com.bytatech.ayoos.client.digitalsigning.model.SigningCredentials;
import com.bytatech.ayoos.repository.ConsultationRepository;
import com.bytatech.ayoos.repository.search.ConsultationSearchRepository;
import com.bytatech.ayoos.security.SecurityUtils;
import com.bytatech.ayoos.service.ConsultationCommandService;
import com.bytatech.ayoos.service.UserService;
import com.bytatech.ayoos.service.dto.UserDTO;
import com.bytatech.ayoos.service.mapper.ConsultationMapper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Service Implementation for managing MedicalSummary.
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class ConsultationCommandServiceImpl implements ConsultationCommandService{

	private final Logger log = LoggerFactory.getLogger(ConsultationCommandServiceImpl.class);

	/*@Autowired
	private ConsultationCommandResourceAssembler assembler;*/
	
    private final ConsultationRepository consultationRepository;

    private final ConsultationMapper consultationMapper;

    //private final ConsultationSearchRepository consultationSearchRepository;
    
    @Autowired
	DigitalSigningApi digitalSigningApi;
    
	@Autowired
	DigitalSigningUploadApi digitalSigningUploadApi;
    
    @Autowired
	private UserService userService;
	
    @Autowired
    private ProcessInstancesApi processInstanceApi;
    
    @Autowired
    private FormsApi formsApi;

    @Autowired
    private TasksApi tasksApi;
    
    @Autowired
    private NodesApi nodesApi;
    
    @Autowired
	SitesApi sitesApi;
    
    
    
	public ConsultationCommandServiceImpl(ConsultationRepository consultationRepository,
			ConsultationMapper consultationMapper) {
		this.consultationRepository = consultationRepository;
		this.consultationMapper = consultationMapper;
		
	}

	
	
	@Override
	public void initiate(InitiateMedicalSummaryRequest medicalSummaryRequest) {
		ProcessInstanceCreateRequest processInstanceCreateRequest=new ProcessInstanceCreateRequest();
   		List<RestVariable> variables=new ArrayList<RestVariable>();
   		//processInstanceCreateRequest.setProcessDefinitionId("consultation:1:43");
   		processInstanceCreateRequest.setProcessDefinitionId("consultation:2:923");
   		RestVariable restVariable=new RestVariable();
   		
   		restVariable.setName("token");
   		restVariable.setScope("global");
   		restVariable.setType("string");
   		restVariable.setValue(medicalSummaryRequest.getToken());
   		variables.add(restVariable);
   		
   		
   		log.info("*****************************************************"+variables.size());
   		processInstanceCreateRequest.setVariables(variables);
   		
   		
   		ResponseEntity<ProcessInstanceResponse> processInstanceResponse = processInstanceApi
				.createProcessInstance(processInstanceCreateRequest);
		String processInstanceId = processInstanceResponse.getBody().getId();
		String processDefinitionId = processInstanceCreateRequest.getProcessDefinitionId();
		log.info("++++++++++++++++processDefinitionId++++++++++++++++++"+ processDefinitionId);
		log.info("++++++++++++++++ProcessInstanceId is+++++++++++++ " + processInstanceId);
		
   		processInstanceApi.createProcessInstance(processInstanceCreateRequest);
   		
		/*ResponseEntity<DataResponse> taskResponseDefaultInfo = tasksApi.getTasks("Choose DefaultInfo", null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, processInstanceId, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null);
		System.out.println(taskResponseDefaultInfo.getBody());
		String chooseDefaultInfoTaskId = ((List<LinkedHashMap<String, String>>) taskResponseDefaultInfo.getBody().getData())
				.get(0).get("id");
		log.info("Task ID of Choose DefaultInfo task ID is " + chooseDefaultInfoTaskId);
		collectDefaultInfo(chooseDefaultInfoTaskId, testDefaultInfoRequest(medicalSummaryRequest.getToken()));
		
		CommandResource commandResource = assembler.toResource(processInstanceId);
		commandResource.setStatus(taskResponseDefaultInfo.getStatusCode().name());
		*/
		
		
   		
   	}
		
	public ConsultationDetails getAppointmentDetails() {
		ConsultationDetails consultationDetails = new ConsultationDetails();
		//consultationDetails.setAppointmentDateAndTime(ZonedDateTime.now(ZoneId.systemDefault()).toString());
		//appointmentDetails.setTrackingID(generateTrackingID());
		consultationDetails.setToken(sampleToken());
		
		UserDTO currentUser = null;
		if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
			currentUser = userService.getUserFromAuthentication(
					(OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication());
		}
		//appointmentDetails.setPatientInfo(getPatientDetails(currentUser));
		consultationDetails.setPatientId(currentUser.getFirstName());
		
		log.info("Consultation Details are " + consultationDetails);
		return consultationDetails;
	}

	
	public PatientInfo getPatientDetails(UserDTO userDTO) {
		// User principal call to get patient Details here
		if (userDTO == null) {

			return null;

		} else {
			PatientInfo patientInfo = new PatientInfo();
			patientInfo.setEmail(userDTO.getEmail());
			patientInfo.setPhoneNumber(Long.parseLong(SecurityUtils.getPhoneNumberFromAuthentication().get()));
			patientInfo.setFirstName(userDTO.getFirstName());
			patientInfo.setPatientId(userDTO.getLogin());
			log.info("Patient Details " + patientInfo);
			return patientInfo;
		}
	}
	
	public String sampleToken() {
		String tokenName =null;
		for (int i=0;i<1;i++) {
		tokenName = "Token"+i;
		}
		return tokenName;
	}

	public DefaultInfoRequest testDefaultInfoRequest(String token) {
		
		DefaultInfoRequest defaultInfoRequest = new DefaultInfoRequest();
		defaultInfoRequest.setPatientId("karthi");
		//defaultInfoRequest.setToken(token);
		
		
		return defaultInfoRequest;
	}



	@Override
	public void collectDefaultInfo(String taskId, DefaultInfoRequest defaultInfoRequest) {
		log .info("into ====================collectDefaultInfo()");
   		List<RestFormProperty>formProperties=new ArrayList<RestFormProperty>();
   		SubmitFormRequest submitFormRequest = new SubmitFormRequest();
   		submitFormRequest.setAction("completed");
   		submitFormRequest.setTaskId(taskId);
   		
   		RestFormProperty patientIdFormProperty = new RestFormProperty();
   		patientIdFormProperty.setId("patientId");
   		patientIdFormProperty.setName("patientId");
   		patientIdFormProperty.setType("String");
   		patientIdFormProperty.setReadable(true);
   		patientIdFormProperty.setValue(defaultInfoRequest.getPatientId());
   		formProperties.add(patientIdFormProperty);
   		
   		RestFormProperty tokenFormProperty = new RestFormProperty();
   		/*tokenFormProperty.setId("token");
   		tokenFormProperty.setName("token");
   		tokenFormProperty.setType("String");
   		tokenFormProperty.setReadable(true);
   		tokenFormProperty.setValue(defaultInfoRequest.getToken());
   		formProperties.add(tokenFormProperty);*/
   		
   		RestFormProperty doctorIdFormProperty = new RestFormProperty();
   		doctorIdFormProperty.setId("doctorId");
   		doctorIdFormProperty.setName("doctorId");
   		doctorIdFormProperty.setType("String");
   		doctorIdFormProperty.setReadable(true);
   		doctorIdFormProperty.setValue(defaultInfoRequest.getDoctorId());
   		formProperties.add(doctorIdFormProperty);
   		
   		RestFormProperty paramedRequiredFormProperty = new RestFormProperty();
   		paramedRequiredFormProperty.setId("paramedRequired");
   		paramedRequiredFormProperty.setName("paramedRequired");
   		paramedRequiredFormProperty.setType("String");
   		paramedRequiredFormProperty.setReadable(true);
   		paramedRequiredFormProperty.setValue(defaultInfoRequest.getParamedRequired());
   		formProperties.add(paramedRequiredFormProperty);
   		
   		submitFormRequest.setProperties(formProperties);
   		//String processInstanceId=tasksApi.getTask(taskId).getBody().getProcessInstanceId();
   		//CommandResource commandResource = assembler.toResource(processInstanceId);
   		//ResponseEntity<ProcessInstanceResponse> response = formsApi.submitForm(submitFormRequest);
   		//commandResource.setStatus(response.getStatusCode().name());
   		formsApi.submitForm(submitFormRequest);
		
	}

	@Override
	public void collectConsultationInfo(String taskId, ConsultationRequest consultationRequest) {
		log .info("into ====================collectConsultationInfo()");
   		List<RestFormProperty>formProperties=new ArrayList<RestFormProperty>();
   		SubmitFormRequest submitFormRequest = new SubmitFormRequest();
   		submitFormRequest.setAction("completed");
   		submitFormRequest.setTaskId(taskId);
   		
   		RestFormProperty symptomFormProperty = new RestFormProperty();
   		symptomFormProperty.setId("symptom");
   		symptomFormProperty.setName("symptom");
   		symptomFormProperty.setType("String");
   		symptomFormProperty.setReadable(true);
   		symptomFormProperty.setValue(consultationRequest.getSymptom());
   		formProperties.add(symptomFormProperty);
   		
   		RestFormProperty dateFormProperty = new RestFormProperty();
   		dateFormProperty.setId("date");
   		dateFormProperty.setName("date");
   		dateFormProperty.setType("Date");
   		dateFormProperty.setReadable(true);
   		dateFormProperty.setWritable(true);
   		SimpleDateFormat formatter=new SimpleDateFormat("MM-dd-yyyy");
   		String date=formatter.format(consultationRequest.getDate());
   		dateFormProperty.setValue(date);
   		formProperties.add(dateFormProperty);
   		
   		RestFormProperty evaluationFormProperty = new RestFormProperty();
   		evaluationFormProperty.setId("evaluation");
   		evaluationFormProperty.setName("evaluation");
   		evaluationFormProperty.setType("String");
   		evaluationFormProperty.setReadable(true);
   		evaluationFormProperty.setValue(consultationRequest.getEvaluation());
   		formProperties.add(evaluationFormProperty);
   		
   		RestFormProperty labResultDMSURLFormProperty = new RestFormProperty();
   		labResultDMSURLFormProperty.setId("labResultDMSURL");
   		labResultDMSURLFormProperty.setName("labResultDMSURL");
   		labResultDMSURLFormProperty.setType("String");
   		labResultDMSURLFormProperty.setReadable(true);
   		labResultDMSURLFormProperty.setValue(consultationRequest.getLabResultDMSURL());
   		formProperties.add(labResultDMSURLFormProperty);
   		
   		
   		RestFormProperty examinationRequiredFormProperty = new RestFormProperty();
   		examinationRequiredFormProperty.setId("examinationRequired");
   		examinationRequiredFormProperty.setName("examinationRequired");
   		examinationRequiredFormProperty.setType("String");
   		examinationRequiredFormProperty.setReadable(true);
   		examinationRequiredFormProperty.setValue(consultationRequest.getExaminationRequired());
   		formProperties.add(examinationRequiredFormProperty);
   		
   		submitFormRequest.setProperties(formProperties);
   		formsApi.submitForm(submitFormRequest);
   		
		
	}

	@Override
	public void collectParamedicalExaminationInfo(String taskId,
			ParamedicalExaminationRequest paramedicalExaminationRequest) {
		log .info("into ====================collectConsultationInfo()");
   		List<RestFormProperty>formProperties=new ArrayList<RestFormProperty>();
   		SubmitFormRequest submitFormRequest = new SubmitFormRequest();
   		submitFormRequest.setAction("completed");
   		submitFormRequest.setTaskId(taskId);
   		
   		RestFormProperty paramedRefFormProperty = new RestFormProperty();
   		paramedRefFormProperty.setId("paramedRef");
   		paramedRefFormProperty.setName("paramedRef");
   		paramedRefFormProperty.setType("String");
   		paramedRefFormProperty.setReadable(true);
   		paramedRefFormProperty.setValue(paramedicalExaminationRequest.getParamedRef());
   		formProperties.add(paramedRefFormProperty);
   		
   		RestFormProperty bpFormProperty = new RestFormProperty();
   		bpFormProperty.setId("bp");
   		bpFormProperty.setName("bp");
   		bpFormProperty.setType("Date");
   		bpFormProperty.setReadable(true);
   		bpFormProperty.setValue(paramedicalExaminationRequest.getBp());
   		formProperties.add(bpFormProperty);
   		
   		RestFormProperty weightFormProperty = new RestFormProperty();
   		weightFormProperty.setId("weight");
   		weightFormProperty.setName("weight");
   		weightFormProperty.setType("String");
   		weightFormProperty.setReadable(true);
   		weightFormProperty.setValue(paramedicalExaminationRequest.getWeight());
   		formProperties.add(weightFormProperty);
   		
   		RestFormProperty heightFormProperty = new RestFormProperty();
   		heightFormProperty.setId("height");
   		heightFormProperty.setName("height");
   		heightFormProperty.setType("String");
   		heightFormProperty.setReadable(true);
   		heightFormProperty.setValue(paramedicalExaminationRequest.getHeight());
   		formProperties.add(heightFormProperty);
   		
   		
   		RestFormProperty temperatureFormProperty = new RestFormProperty();
   		temperatureFormProperty.setId("temperature");
   		temperatureFormProperty.setName("temperature");
   		temperatureFormProperty.setType("String");
   		temperatureFormProperty.setReadable(true);
   		temperatureFormProperty.setValue(paramedicalExaminationRequest.getTemperature());
   		formProperties.add(temperatureFormProperty);
   		
   		submitFormRequest.setProperties(formProperties);
   		formsApi.submitForm(submitFormRequest);
		
	}

	@Override
	public void collectPrescriptionInfo(String taskId, PrescriptionRequest prescriptionRequest) {
		log .info("into ====================collectPrescriptionInfo()");
   		List<RestFormProperty>formProperties=new ArrayList<RestFormProperty>();
   		SubmitFormRequest submitFormRequest = new SubmitFormRequest();
   		submitFormRequest.setAction("completed");
   		submitFormRequest.setTaskId(taskId);
		
   		RestFormProperty drugFormProperty = new RestFormProperty();
   		drugFormProperty.setId("drug");
   		drugFormProperty.setName("drug");
   		drugFormProperty.setType("String");
   		drugFormProperty.setReadable(true);
   		drugFormProperty.setWritable(true);
   		drugFormProperty.setValue(prescriptionRequest.getDrug());
   		formProperties.add(drugFormProperty);
   		System.out.println("================================"+drugFormProperty.getValue()+"====================================");
   		
   	
   		RestFormProperty doseFormProperty = new RestFormProperty();
   		doseFormProperty.setId("dose");
   		doseFormProperty.setName("dose");
   		doseFormProperty.setType("String");
   		doseFormProperty.setReadable(true);
   		drugFormProperty.setWritable(true);
   		doseFormProperty.setValue(prescriptionRequest.getDose());
   		formProperties.add(doseFormProperty);
   		
   		RestFormProperty frequencyFormProperty = new RestFormProperty();
   		frequencyFormProperty.setId("frequency");
   		frequencyFormProperty.setName("frequency");
   		frequencyFormProperty.setType("String");
   		frequencyFormProperty.setReadable(true);
   		drugFormProperty.setWritable(true);
   		frequencyFormProperty.setValue(prescriptionRequest.getFrequency());
   		formProperties.add(frequencyFormProperty);
   		
   		RestFormProperty periodFormProperty = new RestFormProperty();
   		periodFormProperty.setId("period");
   		periodFormProperty.setName("period");
   		periodFormProperty.setType("String");
   		periodFormProperty.setReadable(true);
   		drugFormProperty.setWritable(true);
   		periodFormProperty.setValue(prescriptionRequest.getPeriod());
   		formProperties.add(periodFormProperty);
   		
   		System.out.println("/////////////////////////"+periodFormProperty.getValue()+"////////////////////////////////////////");
   		//System.out.println("***********************"+prescriptionRequest.toString()+"*****************");
		
		
		try {
			createPrescriptionReport(prescriptionRequest);
		} catch (JRException e) {
			e.printStackTrace();

		}
		 
   		//saveToRepo(formProperties);
   		
   		submitFormRequest.setProperties(formProperties);
   		formsApi.submitForm(submitFormRequest);
		
	}


	@Override
	public void createPrescriptionReport() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void createPrescriptionReportAndSave() {
		// TODO Auto-generated method stub
		//upload to alfesco after creating report
	}
	
	@Override
	public String upload(@RequestParam MultipartFile file) {
		System.out.println("+++++++++++++success+++++++++++++");

		

		Resource resource = null;

		try {
			resource = new ByteArrayResource(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String name = "document"+".pdf";
		NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
		nodeBodyCreate.setName(name);
		nodeBodyCreate.setNodeType("cm:content");
		//http://127.0.0.1:8013/share/page/site/dsite/documentlibrary
		nodeBodyCreate.setRelativePath("Sites/patient");
		//nodeBodyCreate.setRelativePath("Signed Documents");
		NodeEntry nodeEntry = nodesApi.createNode("-my-", nodeBodyCreate, true, null, null).getBody();
		nodesApi.updateNodeContent(nodeEntry.getEntry().getId(), resource, true, null, null, null, null);
		System.out.println("*****************************"+nodeEntry.getEntry().getId());
		nodeBodyCreate.getRelativePath();
		System.out.println("*********************************"+nodeBodyCreate.getRelativePath());
		//digitalSigningUploadApi.signDocument(upload);
		
		String docName = "workspace://SpacesStore/"+nodeEntry.getEntry().getId();
		
		SigningCredentials s= new SigningCredentials();
		s.setDocument(docName);
		s.setPassword("qwertyui");
		
		sign(s);
	
		
		return "successfully uploaded";
	}
	
	
	
	
	public String sign(@RequestBody SigningCredentials signingCredentials) {
		
		
		System.out.println("/////////////////////"+signingCredentials.getDocument()+"/////////////////////////////");
		System.out.println("/////////////////////"+signingCredentials.getPassword()+"/////////////////////////////");
		
		digitalSigningApi.signDocument(signingCredentials);
		System.out.println(signingCredentials.getDocument());
		System.out.println(signingCredentials.getPassword());
		System.out.println("+++++++++++++success+++++++++++++");
		
		return "successfully signed";
		
	}



	@Override
	public void createSite(SiteBodyCreate siteBodyCreate) {
		System.out.println("+++++++++++++success++++++++++");
		sitesApi.createSite(siteBodyCreate, false, false, new ArrayList<>()).getBody();
		
	}
	
	

/*public void load(List<RestFormProperty> formProperties){
	sysout
}*/

	
		
/*List<PrescriptionRequest> list = repo.findAll();
		
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);

		JasperReport jasperReport = JasperCompileManager.compileReport("E:\\JASPER\\JaspersoftWorkspace\\MyReports\\Invoice.jrxml");
		System.out.println("************invoice template found********");
		Map<String, Object> parameters = new HashMap<String, Object>();

		JasperPrint jasperPrint = null;
		try {
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);

		} catch (JRException e1) {

			e1.printStackTrace();
		}

		File outDir = new File("E:\\JASPER\\jasperOutput\\test");
		outDir.mkdirs();

		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint, "E:\\JASPER\\jasperOutput\\test\\Product.pdf");
			
			//Resource resource=new ByteArrayResource(JasperExportManager.exportReportToPdf(jasperPrint));
			
			//System.out.println("************"+resource+"**********");
			
		} catch (JRException e) {

			e.printStackTrace();
		}

		System.out.println("Done!");
		*/
		
	//@Override
	
	  public void createPrescriptionReport(PrescriptionRequest prescriptionRequest)
	  throws JRException {
	  
	  log.info("REST request to create pdf**********");
	  
	  
	  List<PrescriptionRequest> list = new ArrayList<PrescriptionRequest>();
	  
	  //list=formProperties;
	  log.info("*****"+prescriptionRequest.toString()+"***********");
	  
	//  System.out.println(formProperties.get(1).getValue());
		
		JasperReport jr = JasperCompileManager.compileReport("Blank.jrxml");

		JRBeanCollectionDataSource customerJRBean = new JRBeanCollectionDataSource(list);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CustomerData", customerJRBean);

		JasperPrint jp = JasperFillManager.fillReport(jr, parameters, customerJRBean);
		JasperExportManager.exportReportToPdfFile(jp, "Blank1.pdf");
		 
	  
	  
	  }
	 
	  public void saveToRepo(List<RestFormProperty> formProperties) {
		  System.out.println("----------------into sateToRepo");
		  List<RestFormProperty> list = new ArrayList<RestFormProperty>();
		  for(RestFormProperty properties : list) {
			  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+properties.getValue());
		  }
		  
	  }


}
