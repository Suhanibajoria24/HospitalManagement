# folder Structure : ***

```
hospital-management-system/
│
├── src/main/java/com/hospital/management/
│
│   ├── HospitalManagementApplication.java
│
│   ├── controller/
│   │   ├── PhysicianController.java
│   │   ├── DepartmentController.java
│   │   ├── PatientController.java
│   │   ├── NurseController.java
│   │   ├── AppointmentController.java
│   │   ├── MedicationController.java
│   │   ├── PrescriptionController.java
│   │   ├── ProcedureController.java
│   │   └── TrainingController.java
│
│   ├── service/
│   │   ├── PhysicianService.java
│   │   ├── DepartmentService.java
│   │   ├── PatientService.java
│   │   ├── NurseService.java
│   │   ├── AppointmentService.java
│   │   ├── MedicationService.java
│   │   ├── PrescriptionService.java
│   │   ├── ProcedureService.java
│   │   ├── TrainingService.java
│   │
│   │   └── impl/
│   │       ├── PhysicianServiceImpl.java
│   │       ├── DepartmentServiceImpl.java
│   │       ├── PatientServiceImpl.java
│   │       ├── NurseServiceImpl.java
│   │       ├── AppointmentServiceImpl.java
│   │       ├── MedicationServiceImpl.java
│   │       ├── PrescriptionServiceImpl.java
│   │       ├── ProcedureServiceImpl.java
│   │       └── TrainingServiceImpl.java
│
│   ├── repository/
│   │   ├── PhysicianRepository.java
│   │   ├── DepartmentRepository.java
│   │   ├── PatientRepository.java
│   │   ├── NurseRepository.java
│   │   ├── AppointmentRepository.java
│   │   ├── MedicationRepository.java
│   │   ├── PrescribesRepository.java
│   │   ├── ProcedureRepository.java
│   │   └── TrainedInRepository.java
│
│   ├── entity/
│   │   ├── Physician.java
│   │   ├── Department.java
│   │   ├── Patient.java
│   │   ├── Nurse.java
│   │   ├── Appointment.java
│   │   ├── Medication.java
│   │   ├── Prescribes.java
│   │   ├── Procedure.java
│   │   └── TrainedIn.java
│
│   ├── dto/
│   │   ├── PhysicianRequestDTO.java
│   │   ├── PhysicianResponseDTO.java
│   │   ├── DepartmentRequestDTO.java
│   │   ├── DepartmentResponseDTO.java
│   │   ├── PatientRequestDTO.java
│   │   ├── PatientResponseDTO.java
│   │   ├── NurseRequestDTO.java
│   │   ├── NurseResponseDTO.java
│   │   ├── AppointmentRequestDTO.java
│   │   ├── AppointmentResponseDTO.java
│   │   ├── MedicationRequestDTO.java
│   │   ├── MedicationResponseDTO.java
│   │   ├── PrescriptionRequestDTO.java
│   │   ├── PrescriptionResponseDTO.java
│   │   ├── ProcedureRequestDTO.java
│   │   ├── ProcedureResponseDTO.java
│   │   ├── TrainingRequestDTO.java
│   │   └── TrainingResponseDTO.java
│
│   ├── mapper/
│   │   ├── PhysicianMapper.java
│   │   ├── DepartmentMapper.java
│   │   ├── PatientMapper.java
│   │   ├── NurseMapper.java
│   │   ├── AppointmentMapper.java
│   │   ├── MedicationMapper.java
│   │   ├── PrescriptionMapper.java
│   │   ├── ProcedureMapper.java
│   │   └── TrainingMapper.java
│
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── BadRequestException.java
│
│   ├── config/
│   │   ├── CorsConfig.java
│   │   └── SwaggerConfig.java
│
│   └── util/
│       ├── ApiResponse.java
│       └── Constants.java
│
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   ├── data.sql
│
├── pom.xml
└── README.md


```